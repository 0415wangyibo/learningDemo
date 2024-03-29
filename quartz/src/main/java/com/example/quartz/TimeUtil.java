package com.example.quartz;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;

/**
 * Created with Intellij IDEA.
 *
 * @author wangyb
 * @Date 2018/9/15 13:33
 * Modified By:
 * Description:时间、日期工具类
 */
public class TimeUtil {
    private static final LocalDate TODAY = LocalDate.now();

    private static final String CRON_DATE_FORMAT = "ss mm HH dd MM ? yyyy";

    /**
     * 获取当前年
     *
     * @return xx年
     */
    public static Integer getCurrentYear() {
        return TODAY.getYear();
    }

    /**
     * 获取当前月
     *
     * @return xx月
     */
    public static Integer getCurrentMonth() {
        return TODAY.getMonthValue();
    }

    /**
     * 获取多少个月之前的年
     *
     * @param months 月份数
     * @return xx年
     */
    public static Integer getYearBeforeMonths(long months) {
        return TODAY.minus(months, ChronoUnit.MONTHS).getYear();
    }

    /**
     * 获取多少个月之前的月
     *
     * @param months 月份数
     * @return xx月
     */
    public static Integer getMonthBeforeMonths(long months) {
        return TODAY.minus(months, ChronoUnit.MONTHS).getMonthValue();
    }

    /**
     * {@code Date} to {@code LocalDate}
     *
     * @param date 需要转换的Date
     * @return LocalDate
     */
    public static LocalDate dateToLocalDate(Date date) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);
        return localDateTime.toLocalDate();
    }

    /**
     * {@code LocalDate} to {@code Date}
     *
     * @param localDate 需要转换的LocalDate
     * @return Date
     */
    public static Date localDateToDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * format("yyyy-MM")  -->  format("yyyy-MM-01")
     *
     * @param beginDate 起始年月
     * @return 起始年月日, 当月第一天
     */
    public static String parseBeginDateToFormat(String beginDate) {
        return beginDate + "-" + "01";
    }

    /**
     * format("yyyy-MM")  -->  format("yyyy-MM-dd")
     *
     * @param endDate 结束年月
     * @return 结束年月, 月份所在最后一天
     */
    public static String parseEndDateToFormat(String endDate) {
        String[] datePart = endDate.split("-");
        int year = Integer.parseInt(datePart[0]);
        int month = Integer.parseInt(datePart[1]);
        if (month == LocalDate.now().getMonthValue()) {
            return endDate + "-" + LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        } else {
            return endDate + "-" + LocalDate.of(year, month, 1).with(TemporalAdjusters.lastDayOfMonth()).getDayOfMonth();
        }
    }

    /**
     * 将date字符串转成Date
     *
     * @param time
     * @return
     */
    public static Date parseStringToDateTime(String time) {
        String format = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        LocalDateTime localDateTime = LocalDateTime.parse(time, df);
        ZoneId zoneId = ZoneId.systemDefault();
        ZonedDateTime zdt = localDateTime.atZone(zoneId);
        return Date.from(zdt.toInstant());
    }

    public static LocalDateTime stringToLocalDateTime(String time) {
        String format = "yyyy-MM-dd HH:mm:ss";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(time, df);
    }

    // date格式:xxxx-xx-xx
    public static LocalDateTime dateStringToLocalDateTime(String date, Integer hour, Integer minute, Integer second) {
        return LocalDateTime.of(LocalDate.parse(date), LocalTime.of(hour, minute, second));
    }

    /**
     * localDateTime --> date
     *
     * @param localDateTime
     * @return
     */
    public static Date localDateTimeToDate(LocalDateTime localDateTime) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDateTime.atZone(zone).toInstant();
        return Date.from(instant);
    }

    /**
     * 将HH:mm格式时间转化为cron表达式
     *
     * @param timeSting
     * @return
     */
    public static String timeStringToCron(String timeSting) {
        String[] times = timeSting.split(":");
        return String.format("0 %d %d ? * *", Integer.valueOf(times[1]), Integer.valueOf(times[0]));
    }

    /**
     * 将xxxx-xx-xx xx:xx:xx 格式时间转化为cron表达式
     *
     * @param localDateTime
     * @return
     */
    public static String localDateTimeToCron(LocalDateTime localDateTime) {
        return new SimpleDateFormat(CRON_DATE_FORMAT).format(localDateTimeToDate(localDateTime));
    }
}

