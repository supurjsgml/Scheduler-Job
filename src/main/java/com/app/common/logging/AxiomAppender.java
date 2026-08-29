package com.app.common.logging;

import java.net.InetAddress;
import java.net.URI;
import java.net.UnknownHostException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.fasterxml.jackson.databind.ObjectMapper;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxyUtil;
import ch.qos.logback.core.UnsynchronizedAppenderBase;

public class AxiomAppender extends UnsynchronizedAppenderBase<ILoggingEvent> {

    private String apiUrl;
    private String dataset;
    private String token;
    private String app;
    private int batchSize;
    private long flushIntervalMs;
    private int maxQueueSize;

    private String replica;
    private BlockingQueue<Map<String, Object>> queue;
    private ScheduledExecutorService scheduler;
    private HttpClient httpClient;
    private ObjectMapper objectMapper;
    private final AtomicBoolean isFlushing = new AtomicBoolean(false);

    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    public void setDataset(String dataset) {
        this.dataset = dataset;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setFlushIntervalMs(long flushIntervalMs) {
        this.flushIntervalMs = flushIntervalMs;
    }

    public void setMaxQueueSize(int maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    @Override
    public void start() {
        if (token == null || token.isBlank()) {
            addWarn("Axiom 토큰이 설정되지 않아 Axiom 로그 전송을 비활성화합니다.");
            return;
        }

        this.replica = resolvePodName();
        this.queue = new LinkedBlockingQueue<>(maxQueueSize);
        this.objectMapper = new ObjectMapper();
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(3))
                .build();

        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "axiom-log-flusher");
            t.setDaemon(true);
            return t;
        });

        this.scheduler.scheduleWithFixedDelay(this::flushQueue, flushIntervalMs, flushIntervalMs, TimeUnit.MILLISECONDS);
        super.start();
    }

    @Override
    protected void append(ILoggingEvent event) {
        if (!isStarted() || queue == null) {
            return;
        }

        Map<String, Object> logEntry = new HashMap<>();
        logEntry.put("_time", Instant.ofEpochMilli(event.getTimeStamp()).toString());
        logEntry.put("app", app);
        logEntry.put("replica", replica);
        logEntry.put("level", event.getLevel().toString());
        logEntry.put("logger", event.getLoggerName());
        logEntry.put("thread", event.getThreadName());
        logEntry.put("message", event.getFormattedMessage());

        if (event.getThrowableProxy() != null) {
            logEntry.put("exception", ThrowableProxyUtil.asString(event.getThrowableProxy()));
        }

        if (!queue.offer(logEntry)) {
            queue.poll();
            queue.offer(logEntry);
        }

        if (queue.size() >= batchSize) {
            scheduler.execute(this::flushQueue);
        }
    }

    private void flushQueue() {
        if (queue == null || queue.isEmpty() || !isFlushing.compareAndSet(false, true)) {
            return;
        }

        try {
            List<Map<String, Object>> batch = new ArrayList<>(batchSize);
            queue.drainTo(batch, batchSize);

            if (!batch.isEmpty()) {
                sendBatch(batch);
            }
        } finally {
            isFlushing.set(false);
        }
    }

    private void sendBatch(List<Map<String, Object>> batch) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(batch);
            String fullUrl = apiUrl.endsWith("/") ? apiUrl + dataset + "/ingest" : apiUrl + "/" + dataset + "/ingest";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .timeout(Duration.ofSeconds(5))
                    .header("Authorization", "Bearer " + token)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.discarding())
                    .exceptionally(ex -> {
                        addWarn("Axiom 로그 전송 중 오류 발생: " + ex.getMessage());
                        return null;
                    });
        } catch (Exception e) {
            addWarn("Axiom 페이로드 직렬화 또는 요청 생성 실패: " + e.getMessage());
        }
    }

    private String resolvePodName() {
        String envReplica = System.getenv("CONTAINER_APP_REPLICA_NAME");
        if (envReplica != null && !envReplica.isBlank()) {
            return envReplica;
        }
        String envPod = System.getenv("POD_NAME");
        if (envPod != null && !envPod.isBlank()) {
            return envPod;
        }
        String envHost = System.getenv("HOSTNAME");
        if (envHost != null && !envHost.isBlank()) {
            return envHost;
        }
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "unknown";
        }
    }

    @Override
    public void stop() {
        if (scheduler != null && !scheduler.isShutdown()) {
            flushQueue();
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(2, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        super.stop();
    }
}
