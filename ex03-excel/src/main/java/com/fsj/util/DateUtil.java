package com.fsj.util;

import org.apache.commons.lang3.time.FastDateFormat;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class DateUtil {
    public final static String DAY_PATTERN = "yyyy-MM-dd";
    public final static String TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static FastDateFormat dateFormat = FastDateFormat.getInstance(DAY_PATTERN);
    @SuppressWarnings("unused")
    private static FastDateFormat timeFormat = FastDateFormat.getInstance(TIME_PATTERN);
    private static Calendar calendar = Calendar.getInstance();
    private static final FastDateFormat[] ACCEPT_DATE_FORMATS = {
            timeFormat,
            FastDateFormat.getInstance("yyyy-MM-dd HH:mm"),
            dateFormat,
            FastDateFormat.getInstance("yyyy/MM/dd"),
            FastDateFormat.getInstance("yyyy.MM.dd"),
            FastDateFormat.getInstance("yyyyMMdd")};

    /**
     * 默认转换，当不知道日期格式时用此方法转。
     * @param dateStr
     * @return
     */
    public static Date str2DateByDefault(String dateStr){
        for (FastDateFormat fdf : ACCEPT_DATE_FORMATS){
            try {
                return fdf.parse(dateStr);
            } catch (ParseException e) {

            }
        }
        return null;
    }

    public static String date2StrByDefault(Date date){
        return timeFormat.format(date);
    }
    /**
     * 获取上一天
     * 
     * @param date
     * @return
     */
    public static Date getLastDate(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 1);
        return cal.getTime();
    }

    /**
     * 转换为XML
     * 
     * @param date
     * @return
     */
    public static XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) {

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(date);
        XMLGregorianCalendar gc = null;
        try {
            gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (Exception e) {

            e.printStackTrace();
        }
        return gc;
    }

    /**
     * 获取上一周
     * 
     * @param date
     * @return
     */
    public static Date getLastWeek(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.WEEK_OF_MONTH, cal.get(Calendar.WEEK_OF_MONTH) - 1);
        return cal.getTime();
    }

    /**
     * 获取上一月
     * 
     * @param date
     * @return
     */
    public static Date getLastMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) - 1);
        return cal.getTime();
    }

    /**
     * 获取下一月
     * 
     * @param date
     * @return
     */
    public static Date getNextMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        return cal.getTime();
    }

    /**
     * 日期按秒相加差
     * 
     * @param date
     * @param i
     * @return
     */
    public static Date addSecond(Date date, int i) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.SECOND, cal.get(Calendar.SECOND) + i);
        return cal.getTime();
    }

    /**
     * 拿到当天是周几
     * 
     * @param date
     * @return
     */
    public static String getWeek(Date date) {
        String[] weekDays = { "周日", "周一", "周二", "周三", "周四", "周五", "周六" };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    /**
     * 按自已给的类型转换日期
     * 
     * @param dateFormat
     * @param dateStr
     * @return
     */
    public static Date getDate(String dateStr, String dateFormat) {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
        try {
            return sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将日期转换成时间
     * 
     * @param date
     * @param timeStr
     *            "23:59:59"
     * @return
     */
    public static Date getTime(Date date, String timeStr) {
        if (date == null) {
            return null;
        }
        String dateStr = dateFormat.format(date);
        String CombinationDateAndTimeStr = dateStr + " " + timeStr;
        try {
            return timeFormat.parse(CombinationDateAndTimeStr);
        } catch (ParseException e) {
            return null;
        }
    }

    /**
     * 将日期自定义转换成字符串
     *
     * @param date
     * @param formatStr
     * @return
     */
    public static String getDateStr(Date date, String formatStr) {
        return FastDateFormat.getInstance(formatStr).format(date);
    }

    /**
     * 拿到下一天
     * 
     * @param date
     * @return
     */
    public static Date getNextDate(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + 1);
        return calendar.getTime();
    }

    /*
     * public static List<Date> getDuringDate(Date startTime, Date endTime) {
     * List dateList = new ArrayList(); dateList.add(startTime); Date date =
     * getNextDate(startTime); while (date.before(endTime)) {
     * dateList.add(date); date = getNextDate(date); } dateList.add(endTime);
     * return dateList; }
     */
    /**
     * 拿到两天之间的日期list
     * 
     * @param startTime
     * @param endTime
     * @param formatStr
     * @return
     */
    public static List<Date> getDuringDate(Date startTime, Date endTime, String formatStr) {
        List<Date> dateList = new ArrayList<Date>();
        FastDateFormat sdf = FastDateFormat.getInstance(formatStr);
        try {

            Long days = getDayQuot(endTime, startTime);
            Date beginTemp = sdf.parse(sdf.format(startTime));
            for (Long i = 0L; i <= days; i++) {
                dateList.add(sdf.parse(sdf.format(DateUtil.addDay(beginTemp, i.intValue()))));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateList;
    }

    /**
     * 拿到两月之间的list
     * 
     * @param startTime
     * @param endTime
     * @param formatStr
     * @return
     */
    public static List<Date> getDuringMonth(Date startTime, Date endTime, String formatStr) {
        List<Date> dateList = new ArrayList<Date>();
        FastDateFormat sdf = FastDateFormat.getInstance(formatStr);
        try {
            Long months = getMonthDiff(endTime, startTime);
            Date beginTemp = sdf.parse(sdf.format(startTime));
            for (Long i = 0L; i <= months; i++) {
                dateList.add(sdf.parse(sdf.format(DateUtil.addMonth(beginTemp, i.intValue()))));
            }
            /*
             * Date beginTemp = sdf.parse(sdf.format(startTime));
             * dateList.add(beginTemp); Date date = getNextMonth(startTime);
             * while (date.before(endTime)) {
             * dateList.add(sdf.parse(sdf.format(date))); date =
             * getNextMonth(date); } Date endTemp =
             * sdf.parse(sdf.format(endTime));
             * if(DateUtil.compareDate2(beginTemp, endTemp)!=0){
             * dateList.add(endTemp); }
             */

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateList;
    }

    /**
     * 拿到两月之间的月string
     * 
     * @param startTime
     * @param endTime
     * @param formatStr
     * @return
     */
    public static List<String> getDuringMonthStr(Date startTime, Date endTime, String formatStr) {
        List<String> dateList = new ArrayList<String>();
        FastDateFormat sdf = FastDateFormat.getInstance(formatStr);
        Long months = getMonthDiff(endTime, startTime);
        for (Long i = 0L; i <= months; i++) {
            dateList.add(sdf.format(DateUtil.addMonth(startTime, i.intValue())));
        }
        /*
         * dateList.add(sdf.format(startTime)); Date date =
         * getNextMonth(startTime); while (date.before(endTime)) {
         * dateList.add(sdf.format(date)); date = getNextMonth(date); }
         * dateList.add(sdf.format(endTime));
         */
        return dateList;
    }

    /**
     * 拿到两日之间的日期string
     * 
     * @param startTime
     * @param endTime
     * @param formatStr
     * @return
     */
    public static List<String> getDuringDateStr(Date startTime, Date endTime, String formatStr) {
        List<String> dateList = new ArrayList<String>();
        FastDateFormat sdf = FastDateFormat.getInstance(formatStr);
        Long days = getDayQuot(endTime, startTime);
        for (Long i = 0L; i <= days; i++) {
            dateList.add(sdf.format(DateUtil.addDay(startTime, i.intValue())));
        }
        /*
         * dateList.add(sdf.format(startTime)); Date date =
         * getNextDate(startTime); while (date.before(endTime)) {
         * dateList.add(sdf.format(date)); date = getNextDate(date); }
         * dateList.add(sdf.format(endTime));
         */
        return dateList;
    }

    /**
     * 加、减天数
     * 
     * @param date
     * @param dayNum
     * @return
     */
    public static Date addDay(Date date, Integer dayNum) {
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) + dayNum);
        return calendar.getTime();
    }

    /**
     * 加、减小时数
     *
     * @param date
     * @param hourNum
     * @return
     */
    public static Date addHour(Date date, Integer hourNum) {
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY) + hourNum);
        return calendar.getTime();
    }

    /**
     * 加、减月数
     * 
     * @param date
     * @param monthNum
     * @return
     */
    public static Date addMonth(Date date, Integer monthNum) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + monthNum);
        return cal.getTime();
    }

    /**
     * 拿到这个月的第一天
     * 
     * @param date
     * @return
     */
    public static Date getFirstDayByMonth(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 拿到这年的第一天
     * 
     * @param date
     * @return
     */
    public static Date getFirstDayByYear(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTime();
    }

    /**
     * 拿到上个月的第一天
     * 
     * @param date
     * @return
     */
    public static Date getFirstDayOfPreviousMonth(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        return calendar.getTime();
    }

    /**
     * 拿到这个月的第一天
     * 
     * @param monthStr
     * @return
     */
    public static Date getFirstDayByMonth(String monthStr) {
        if (monthStr != null && !monthStr.equals("")) {
            SimpleDateFormat dateFormatSDF = new SimpleDateFormat("yyyy-MM-dd");
            try {
                Date firstDayByMonth = dateFormatSDF.parse(monthStr + "-1");
                return firstDayByMonth;
            } catch (ParseException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * 拿到这个月的最后一天
     * 
     * @param date
     * @return
     */
    public static Date getLastDayByMonth(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        return calendar.getTime();
    }

    /**
     * 拿到上个月的最后一天
     * 
     * @param date
     * @return
     */
    public static Date getLastDayOfPreviousMonth(Date date) {
        calendar.setTime(date);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        return calendar.getTime();
    }

    /**
     * 拿到这个月的最后一天
     * 
     * @param monthStr
     * @return
     */
    public static Date getLastDayByMonth(String monthStr) {
        if (monthStr != null && !monthStr.equals("")) {
            Date firstDayByMonth = getFirstDayByMonth(monthStr);
            if (firstDayByMonth == null) {
                return null;
            }
            calendar.setTime(firstDayByMonth);
            calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) + 1);
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
            return calendar.getTime();
        }
        return null;
    }

    /**
     * 拿到月份
     * 
     * @param date
     * @return
     */
    public static String getMonthStr(Date date) {
        String monthStr = "";
        calendar.setTime(date);
        int i = calendar.get(Calendar.MONTH) + 1;
        if (i > 9) {
            monthStr = "" + i;
        } else {
            monthStr = monthStr + "0" + i;
        }
        return monthStr;
    }

    /**
     * 拿到日
     * 
     * @param date
     * @return
     */
    public static String getDayStr(Date date) {
        String dayStr = "";
        calendar.setTime(date);
        int i = calendar.get(Calendar.DAY_OF_MONTH);
        if (i > 9) {
            dayStr = dayStr + i;
        } else {
            dayStr = dayStr + "0" + i;
        }
        return dayStr;
    }

    /**
     * 取得指定日期所在周的第一天
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);// 本周的第一天是周一
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
        return c.getTime();
    }

    /**
     * 取得指定日期所在周的最后一天
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);// 本周的第一天是周一
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6);// 周日
        return c.getTime();
    }

    /**
     * 获取第几周
     * 
     * @return
     */
    public static int getWeekNumber(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.setTime(date);
        // calendar.setMinimalDaysInFirstWeek(7);
        return calendar.get(Calendar.WEEK_OF_YEAR);
    }

    /**
     * 获取日
     * 
     * @param date
     * @return
     */
    public static int getDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取月
     * 
     * @param date
     * @return
     */
    public static int getMonth(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取年
     * 
     * @param date
     * @return
     */
    public static int getYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    /**
     * 获取这年这月有多少天
     * 
     * @param year
     * @param month
     * @return
     */
    public static int getDayCountInMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        return c.getActualMaximum(Calendar.DATE);
    }

    /**
     * 两个日期相差的天数
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static Long getDayQuot(Date date1, Date date2) {
        Long quot = null;
        if (date1 == null || date2 == null) {
            return quot;
        }
        quot = date1.getTime() - date2.getTime();
        quot = quot / 1000 / 60 / 60 / 24;
        return quot;

    }

    /**
     * 根据两个日期的年数、月数算出相差的月数
     * 
     * @param date1
     * @param date2
     * @return
     */
    public static Integer getMonthSpace(Date date1, Date date2) {
        Integer month = null;
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        if (date1 == null || date2 == null) {
            return month;
        }
        c2.setTime(date2);
        c1.setTime(date1);
        month = (c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR)) * 12
                + (c2.get(Calendar.MONTH) - c1.get(Calendar.MONTH)) + 1;
        return month;
    }

    /**
     * 
     * @param date1
     * @param date2
     * @return 如果date1==date2 返回 0 date1<date2 返回 -1 date1>date2 返回 1
     */
    public static int compareDate(Date date1, Date date2) {
        if (date1.getTime() > date2.getTime()) {
            return 1;
        } else if (date1.getTime() < date2.getTime()) {
            return -1;
        } else {
            return 0;
        }
    }

    /**
     * 获取相差的月份
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public static long getMonthDiff(Date startTime, Date endTime) {
        int syear = DateUtil.getYear(startTime);
        int smonth = DateUtil.getMonth(startTime);
        int sday = DateUtil.getDay(startTime);

        int eyear = DateUtil.getYear(endTime);
        int emonth = DateUtil.getMonth(endTime);
        int eday = DateUtil.getDay(endTime);

        long monthday = ((syear - eyear) * 12 + (smonth - emonth));

        if (eday > sday) {
            monthday = monthday + 1;
        }
        return monthday;
    }

    /**
     * 加一年 月 日
     */
    public static String dateAddYearMonthDate(String format, String StrDate, int year, int month, int day) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sf = new SimpleDateFormat(format);
        try {
            cal.setTime(sf.parse(StrDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (day != 0) {
            cal.add(Calendar.DATE, day);
        }
        if (month != 0) {
            cal.add(Calendar.MONTH, month);
        }
        if (year != 0) {
            cal.add(Calendar.YEAR, year);

        }
        return sf.format(cal.getTime());
    }

    public static Date getCurrYearFirst(String year) {
        // Calendar currCal=Calendar.getInstance();
        // int currentYear = currCal.get(Calendar.YEAR);
        int currentYear = Integer.parseInt(year);
        // System.out.println(getYearFirst(currentYear));
        return getYearFirst(currentYear);
    }

    public static Date getYearFirst(int year) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.YEAR, year);
        Date currYearFirst = calendar.getTime();
        return currYearFirst;
    }

    public static void main(String[] args) {
        // System.out.println("getLastWeek: " + getLastWeek(new Date()));
        // System.out.println("getFirstDayByMonth: " + getFirstDayByMonth(new
        // Date()));
        // System.out.println("getLastDayByMonth: " + getLastDayByMonth(new
        // Date()));
        // System.out.println("getTimeStr: " + getTimeStr(new Date()));
        // System.out.println("getDate: " + getDate(new Date()));
        // System.out.println("getFirstDayByMonth: " +
        // getFirstDayByMonth("2011-12"));
        // System.out.println("getLastDayByMonth: " +
        // getLastDayByMonth("2011-12"));
        // System.out.println(getLastWeek(new Date()));
        // System.out.println("getMonthStr: " + getMonthStr(new Date()));
        // System.out.println("getDayStr: " + getDayStr(new Date()));
        // System.out.println("getDateStr: " + getDateStr(new Date(), new
        // Date()));
        // System.out.println("getFirstDayOfPreviousMonth: " +
        // getFirstDayOfPreviousMonth(new Date()));
        // System.out.println("getLastDayOfPreviousMonth: " +
        // getLastDayOfPreviousMonth(new Date()));
        // System.out.println(DateUtil.getDateStr(getFirstDayByYear(new
        // Date())));
        // System.out.println(getDayCountInMonth(2014, 11));

        /*
         * List<Date> list = getDuringDate(DateUtil.getDate("yyyy-MM-dd",
         * "2014-11-01"), DateUtil.getDate("yyyy-MM-dd", "2014-11-05"),
         * "yyy-MM-dd HH:mm:ss"); for (Date date : list) {
         * System.out.println(date); }
         */
        try {

            System.out.println(getDate("2015-11-10 04:50:00", TIME_PATTERN));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
