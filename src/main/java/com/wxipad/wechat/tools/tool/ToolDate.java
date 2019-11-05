//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.tool;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class ToolDate {
    public static final long UNKNOW_DATETIME = 0L;
    public static final long SECOND_MS = 1000L;
    public static final long MINUTE_MS = 60000L;
    public static final long HOUR_MS = 3600000L;
    public static final long DAY_MS = 86400000L;
    public static final int[] WEEKS = new int[]{1, 2, 3, 4, 5, 6, 7};
    public static final int[] WEEKS2 = new int[]{2, 3, 4, 5, 6, 7, 1};
    public static final int[] MONTHS = new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11};
    private static final int[] DATE_FIELDS = new int[]{1, 2, 5, 11, 12, 13, 14};

    public ToolDate() {
    }

    public static long toTs(Date date) {
        return date == null ? 0L : date.getTime();
    }

    public static long toTs(java.sql.Date date) {
        return date == null ? 0L : date.getTime();
    }

    public static long toTs(Time time) {
        return time == null ? 0L : time.getTime();
    }

    public static long toTs(Timestamp timeStamp) {
        return timeStamp == null ? 0L : timeStamp.getTime();
    }

    public static Date toDate(long timeStamp) {
        Calendar cal = Calendar.getInstance(TimeZone.getDefault());
        cal.setTimeInMillis(timeStamp);
        return cal.getTime();
    }

    public static Date toDate(long timeStamp, String timeZone) {
        Calendar cal = Calendar.getInstance(getTimeZone(timeZone));
        cal.setTimeInMillis(timeStamp);
        return cal.getTime();
    }

    public static Date toDate(long timeStamp, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(timeStamp);
        return cal.getTime();
    }

    public static String format(long timeStamp, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(toDate(timeStamp));
    }

    public static String format(long timeStamp, String formatStr, String timeZone) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(toDate(timeStamp, timeZone));
    }

    public static String format(long timeStamp, String formatStr, TimeZone timeZone) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(toDate(timeStamp, timeZone));
    }

    public static String formatDate(Date date, String formatStr) {
        SimpleDateFormat format = new SimpleDateFormat(formatStr);
        return format.format(date);
    }

    public static TimeZone getTimeZone(String id) {
        return id == null ? TimeZone.getDefault() : TimeZone.getTimeZone(id);
    }

    public static long now() {
        return System.currentTimeMillis();
    }

    public static Date nowDate() {
        return new Date();
    }

    public static int getCurFiled(int field) {
        Calendar instance = Calendar.getInstance();
        instance.setTimeInMillis(System.currentTimeMillis());
        return instance.get(field);
    }

    public static long parseTimeUUID(String uuid) {
        char[] chArr = (uuid == null ? "" : uuid).toCharArray();
        int count = 0;
        long ts = 0L;

        for (int i = 0; i < chArr.length && count < 16; ++i) {
            char ch = chArr[i];
            if (ch >= '0' && ch <= '9') {
                ts = ts * 16L + (long) (ch - 48);
            } else if (ch >= 'a' && ch <= 'z') {
                ts = ts * 16L + (long) (ch - 97 + 10);
            } else if (ch >= 'A' && ch <= 'Z') {
                ts = ts * 16L + (long) (ch - 65 + 10);
            } else {
                --count;
            }

            ++count;
        }

        return ts;
    }

    public static long parseParam(String param, boolean asStart) {
        return parseParam(param, asStart, TimeZone.getDefault());
    }

    public static long parseParam(String param, boolean asStart, String timeZone) {
        return parseParam(param, asStart, getTimeZone(timeZone));
    }

    public static long parseParam(String param, boolean asStart, TimeZone timeZone) {
        Date d = parseParamDate(param, asStart, timeZone);
        return d == null ? 0L : d.getTime();
    }

    public static Date parseParamDate(String param, boolean asStart) {
        return parseParamDate(param, asStart, TimeZone.getDefault());
    }

    public static Date parseParamDate(String param, boolean asStart, String timeZone) {
        return parseParamDate(param, asStart, getTimeZone(timeZone));
    }

    public static Date parseParamDate(String param, boolean asStart, TimeZone timeZone) {
        if (param == null) {
            return null;
        } else {
            int[] nums = new int[7];

            int index;
            for (index = 0; index < nums.length; ++index) {
                nums[index] = -1;
            }

            index = 0;
            boolean lastNum = false;
            boolean getNum = false;
            param = param.trim();

            int i;
            for (i = 0; i < param.length(); ++i) {
                char ch = param.charAt(i);
                if (ch >= '0' && ch <= '9') {
                    i = ch - 48;
                    if (lastNum) {
                        nums[index] = nums[index] * 10 + i;
                    } else {
                        nums[index] = i;
                    }

                    getNum = true;
                    lastNum = true;
                } else {
                    if (lastNum) {
                        ++index;
                        if (index >= nums.length) {
                            break;
                        }
                    }

                    lastNum = false;
                }
            }

            if (!getNum) {
                return null;
            } else {
                Calendar cal = Calendar.getInstance(timeZone);
                boolean boundNum = false;

                int num;
                for (i = 0; i < nums.length; ++i) {
                    num = cal.getActualMinimum(DATE_FIELDS[i]);
                    cal.set(DATE_FIELDS[i], num);
                }

                for (i = 0; i < nums.length; ++i) {
                    num = nums[i];
                    int min = cal.getActualMinimum(DATE_FIELDS[i]);
                    if (num < min) {
                        if (!boundNum && !asStart) {
                            cal.add(DATE_FIELDS[i - 1], 1);
                        }

                        cal.set(DATE_FIELDS[i], min);
                        boundNum = true;
                    } else if (DATE_FIELDS[i] == 1) {
                        cal.set(DATE_FIELDS[i], num < 100 ? num + 2000 : num);
                    } else if (DATE_FIELDS[i] == 2) {
                        cal.set(DATE_FIELDS[i], num - 1);
                    } else {
                        cal.set(DATE_FIELDS[i], num);
                    }
                }

                return cal.getTime();
            }
        }
    }

    public static int getWeekDayIndex(int[] weeks, int dayOfWeek) {
        for (int i = 0; i < weeks.length; ++i) {
            if (weeks[i] == dayOfWeek) {
                return i;
            }
        }

        return -1;
    }

    public static long getWeekStart(long timeStamp) {
        return getWeekTs(timeStamp, WEEKS, true, TimeZone.getDefault());
    }

    public static long getWeekStart(long timeStamp, TimeZone timeZone) {
        return getWeekTs(timeStamp, WEEKS, true, timeZone);
    }

    public static long getWeekEnd(long timeStamp) {
        return getWeekTs(timeStamp, WEEKS, false, TimeZone.getDefault());
    }

    public static long getWeekEnd(long timeStamp, TimeZone timeZone) {
        return getWeekTs(timeStamp, WEEKS, false, timeZone);
    }

    public static long getWeek2Start(long timeStamp) {
        return getWeekTs(timeStamp, WEEKS2, true, TimeZone.getDefault());
    }

    public static long getWeek2Start(long timeStamp, TimeZone timeZone) {
        return getWeekTs(timeStamp, WEEKS2, true, timeZone);
    }

    public static long getWeek2End(long timeStamp) {
        return getWeekTs(timeStamp, WEEKS2, false, TimeZone.getDefault());
    }

    public static long getWeek2End(long timeStamp, TimeZone timeZone) {
        return getWeekTs(timeStamp, WEEKS2, false, timeZone);
    }

    private static long getWeekTs(long timeStamp, int[] weeks, boolean asStart, TimeZone timeZone) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(timeStamp);
        int addDays = getWeekDayIndex(weeks, cal.get(7));
        cal.add(5, asStart ? -addDays : 7 - addDays);

        for (int i = 3; i < DATE_FIELDS.length; ++i) {
            cal.set(DATE_FIELDS[i], 0);
        }

        return cal.getTimeInMillis();
    }

    public static long getYearStart(long timeStamp) {
        return getFieldsStart(timeStamp, TimeZone.getDefault(), 1);
    }

    public static long getYearStart(long timeStamp, TimeZone timeZone) {
        return getFieldsStart(timeStamp, timeZone, 1);
    }

    public static long getMonthStart(long timeStamp) {
        return getFieldsStart(timeStamp, TimeZone.getDefault(), 2);
    }

    public static long getMonthStart(long timeStamp, TimeZone timeZone) {
        return getFieldsStart(timeStamp, timeZone, 2);
    }

    public static long getDayStart(long timeStamp) {
        return getFieldsStart(timeStamp, TimeZone.getDefault(), 5);
    }

    public static long getDayStart(long timeStamp, TimeZone timeZone) {
        return getFieldsStart(timeStamp, timeZone, 5);
    }

    public static long getHourStart(long timeStamp) {
        return getFieldsStart(timeStamp, TimeZone.getDefault(), 11);
    }

    public static long getHourStart(long timeStamp, TimeZone timeZone) {
        return getFieldsStart(timeStamp, timeZone, 11);
    }

    public static long getMinuteStart(long timeStamp) {
        return getFieldsStart(timeStamp, TimeZone.getDefault(), 12);
    }

    public static long getMinuteStart(long timeStamp, TimeZone timeZone) {
        return getFieldsStart(timeStamp, timeZone, 12);
    }

    public static long getYearEnd(long timeStamp) {
        return getFieldsEnd(timeStamp, TimeZone.getDefault(), 1);
    }

    public static long getYearEnd(long timeStamp, TimeZone timeZone) {
        return getFieldsEnd(timeStamp, timeZone, 1);
    }

    public static long getMonthEnd(long timeStamp) {
        return getFieldsEnd(timeStamp, TimeZone.getDefault(), 2);
    }

    public static long getMonthEnd(long timeStamp, TimeZone timeZone) {
        return getFieldsEnd(timeStamp, timeZone, 2);
    }

    public static long getDayEnd(long timeStamp) {
        return getFieldsEnd(timeStamp, TimeZone.getDefault(), 5);
    }

    public static long getDayEnd(long timeStamp, TimeZone timeZone) {
        return getFieldsEnd(timeStamp, timeZone, 5);
    }

    public static long getHourEnd(long timeStamp) {
        return getFieldsEnd(timeStamp, TimeZone.getDefault(), 11);
    }

    public static long getHourEnd(long timeStamp, TimeZone timeZone) {
        return getFieldsEnd(timeStamp, timeZone, 11);
    }

    public static long getMinuteEnd(long timeStamp) {
        return getFieldsEnd(timeStamp, TimeZone.getDefault(), 12);
    }

    public static long getMinuteEnd(long timeStamp, TimeZone timeZone) {
        return getFieldsEnd(timeStamp, timeZone, 12);
    }

    private static long getFieldsStart(long timeStamp, TimeZone timeZone, int field) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(timeStamp);
        boolean get = false;

        for (int i = 0; i < DATE_FIELDS.length; ++i) {
            if (!get) {
                if (DATE_FIELDS[i] == field) {
                    get = true;
                }
            } else if (DATE_FIELDS[i] == 5) {
                cal.set(DATE_FIELDS[i], 1);
            } else {
                cal.set(DATE_FIELDS[i], 0);
            }
        }

        return cal.getTimeInMillis();
    }

    private static long getFieldsEnd(long timeStamp, TimeZone timeZone, int field) {
        Calendar cal = Calendar.getInstance(timeZone);
        cal.setTimeInMillis(timeStamp);
        boolean get = false;

        for (int i = 0; i < DATE_FIELDS.length; ++i) {
            if (!get) {
                if (DATE_FIELDS[i] == field) {
                    cal.add(field, 1);
                    get = true;
                }
            } else if (DATE_FIELDS[i] == 5) {
                cal.set(DATE_FIELDS[i], 1);
            } else {
                cal.set(DATE_FIELDS[i], 0);
            }
        }

        return cal.getTimeInMillis();
    }
}
