package com.app.common.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang3.StringUtils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

@UtilityClass
@Slf4j
public class DateUtils {
    
    public final String YYYYMMDD_TIME_STR = "yyyy-MM-dd HH:mm:ss";

    public final String YYYYMMDD_STR = "yyyy-MM-dd";
    
    public final DateTimeFormatter YYYY_MM_DD_TIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
    
    public final DateTimeFormatter YYYY_MM_DD = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.KOREA);
	
    public final DateTimeFormatter YYYYMMDDTIME = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss", Locale.KOREA);
    
    public final DateTimeFormatter YYYYMMDD = DateTimeFormatter.ofPattern("yyyyMMdd", Locale.KOREA);
    
    /**
     * LocalDateTime 현재날짜
     * 
     * @param 
     * @return LocalDateTime
     * @author guney
     * @date 2024. 5. 7.
     */
    public LocalDateTime currentDateTime() {
        return LocalDateTime.now();
    }
    
    /**
     * LocalDateTime 현재날짜 포맷
     * 
     * @param String format : 포맷형식
     * @return String
     * @author guney
     * @date 2024. 5. 7.
     */
    public String currentDateTimeFormat() {
        return currentDateTimeFormat(null);
    }
    
    public String currentDateTimeFormat(String format) {
        if (StringUtils.isNotBlank(format)) {
            return LocalDateTime.now().format(DateTimeFormatter.ofPattern(format, Locale.KOREA));
        }
        
        return LocalDateTime.now().format(YYYY_MM_DD_TIME);
    }
    
    /**
     * LocalDateTime Parse
     * 
     * @param String date
     * @return LocalDateTime
     * @author guney
     * @date 2024. 5. 17.
     */
    public LocalDateTime localDateTimeParse(String date) {
        if (StringUtils.isBlank(date)) return null;
        return LocalDateTime.parse(date, YYYY_MM_DD_TIME);
    }
    
    /**
     * 현재날짜 자정시간 (00:00:00)
     * 
     * @param 
     * @return LocalDateTime
     * @author guney
     * @date 2024. 5. 17.
     */
    public LocalDateTime curLocalDateTimeStart() {
        return LocalDate.now().atStartOfDay();
    }
    
    /**
     * 현자날짜 마지막시간 (23:59:59.999999999)
     * 
     * @param 
     * @return LocalDateTime
     * @author guney
     * @date 2024. 5. 17.
     */
    public LocalDateTime curLocalDateTimeEnd() {
        return LocalDate.now().atTime(LocalTime.MAX);
    }
    
    /**
     * LocalDate 현재날짜
     * 
     * @param 
     * @return LocalDate
     * @author guney
     * @date 2024. 5. 7.
     */
    public LocalDate currentDate() {
        return LocalDate.now();
    }
    
    /**
     * LocalDate 현재날짜 포맷
     * 
     * @param String format : 포맷형식
     * @return String
     * @author guney
     * @date 2024. 5. 7.
     */
    public String currentDateFormat() {
        return currentDateFormat(null);
    }
    
    public String currentDateFormat(String format) {
        if (StringUtils.isNotBlank(format)) {
            return LocalDate.now().format(DateTimeFormatter.ofPattern(format, Locale.KOREA));
        }
        
        return LocalDate.now().format(YYYY_MM_DD_TIME);
    }
    
    /**
     * LocalDate를 Date로 변환
     * @param localDate
     * @return
     */
    public Date toDate(LocalDate localDate) {

        if (localDate == null) {
            return null;
        }

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime을 Date로 변환
     * @param localDateTime
     * @return
     */
    public Date toDate(LocalDateTime localDateTime) {

        if (localDateTime == null) {
            return null;
        }

        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date를 LocalDate로 변환
     * @param date
     * @return
     */
    public LocalDate toLocalDate(Date date) {

        if (date == null) {
            return null;
        }

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

    /**
     * Date를 LocalDateTime으로 변환
     * @param date
     * @return
     */
    public LocalDateTime toLocalDateTime(Date date) {

        if (date == null) {
            return null;
        }

        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    /**
     * time stamp 값을 LocalDateTime 형태로 변환
     * @param time
     * @return
     */
    public LocalDateTime toLocalDateTime(long time) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(time),
                TimeZone.getDefault().toZoneId());
    }
    
    /**
     * yyyy-MM-dd 형식의 String > LocalDate 변환
     * @param date
     * @return LocalDate
     * @user guney
     * @date 2025. 12. 23.
     */
    public static LocalDate localDateParse(String date) {
    	if (StringUtils.isBlank(date)) return null;
    	return LocalDate.parse(date, YYYY_MM_DD);
    }
    
    /**
     * yyyy-MM-dd HH:mm:ss 형식의 LocalDateTime > String 변환
     * @param date
     * @return LocalDateTime
     * @user guney
     * @date 2025. 12. 15.
     */
    public static String localDateTimeToString(LocalDateTime date) {
    	if (date == null) return null;
    	return date.format(YYYY_MM_DD_TIME);
    }
    
    /**
     * yyyy-MM-dd 형식의 LocalDateTime > String 변환
     * @param date
     * @return LocalDate
     * @user guney
     * @date 2025. 12. 23.
     */
    public static String localDateToString(LocalDate date) {
    	if (date == null) return null;
    	return date.format(YYYY_MM_DD);
    }
    
    /**
     * 날짜 입맛대로 포맷
     * @param date
     * @param format
     * @return String
     * @user guney
     * @date 2026. 2. 15.
     */
    public static String localDateTimeToString(LocalDateTime date, String format) {
    	if (date == null) return null;
    	return date.format(DateTimeFormatter.ofPattern(format));
    }

    /**
     * view 시간노출 함수
     * 
     * @param 
     * @return String
     * @author guney
     * @date 2024. 3. 16.
     */
    public String timediff(String regDtStr) {

        String recommentHistoryTime = "";

        try {
            LocalDateTime regDt = LocalDateTime.parse(regDtStr, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            LocalDateTime currentDt = LocalDateTime.now();

            long secondsDiff = ChronoUnit.SECONDS.between(regDt, currentDt);

            if (secondsDiff < 60) {
                recommentHistoryTime = "방금";
            } else if (secondsDiff >= 60 && secondsDiff < 3600) {
                long minutesDiff = ChronoUnit.MINUTES.between(regDt, currentDt);
                recommentHistoryTime = minutesDiff + "분전";
            } else if (secondsDiff >= 3600 && secondsDiff < 86400) {
                long hoursDiff = ChronoUnit.HOURS.between(regDt, currentDt);
                recommentHistoryTime = hoursDiff + "시간전";
            } else if (secondsDiff >= 86400 && secondsDiff < 2592000) {
                long daysDiff = ChronoUnit.DAYS.between(regDt, currentDt);
                recommentHistoryTime = daysDiff + "일전";
            } else if (secondsDiff >= 2592000 && secondsDiff < 31104000) {
                long monthsDiff = ChronoUnit.MONTHS.between(regDt, currentDt);
                recommentHistoryTime = monthsDiff + "개월전";
            } else {
                long yearsDiff = ChronoUnit.YEARS.between(regDt, currentDt);
                recommentHistoryTime = yearsDiff + "년전";
            }

        } catch (Exception e) {
            System.out.println("유효하지 않은 형식입니다.");
            log.error(">>>>>>>ERR-TIMEDIFF:{}",e.getMessage());
        }

        return recommentHistoryTime;
    }
}
