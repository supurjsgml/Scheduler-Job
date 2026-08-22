FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

COPY build/libs/Scheduler-Job.jar Scheduler-Job.jar

EXPOSE 8081

ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=prod", "-jar", "Scheduler-Job.jar"]
