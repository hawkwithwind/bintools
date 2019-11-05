//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import java.text.DecimalFormat;
import java.util.Date;

public class TimeMeter {
    private long timeStamp;

    public TimeMeter() {
        this.timeStamp = System.currentTimeMillis();
    }

    public TimeMeter(long ts) {
        this.timeStamp = ts;
    }

    public TimeMeter(Date d) {
        this.timeStamp = d.getTime();
    }

    public long get() {
        return this.timeStamp;
    }

    private String getPassStr(long ts) {
        String unit = "ms";
        double dts = (double) ts;
        DecimalFormat format = new DecimalFormat("0.0");
        if (ts < 1000L) {
            return ts + unit;
        } else {
            dts /= 1000.0D;
            unit = "s";
            if (dts < 60.0D) {
                return format.format(dts) + unit;
            } else {
                dts /= 60.0D;
                unit = "min(s)";
                if (dts < 60.0D) {
                    return format.format(dts) + unit;
                } else {
                    dts /= 60.0D;
                    unit = "hour(s)";
                    if (dts < 24.0D) {
                        return format.format(dts) + unit;
                    } else {
                        dts /= 24.0D;
                        unit = "day(s)";
                        return format.format(dts) + unit;
                    }
                }
            }
        }
    }

    public String passStr() {
        return this.getPassStr(this.pass());
    }

    public String passStr(long ts) {
        return this.getPassStr(this.pass(ts));
    }

    public String passStr(Date d) {
        return this.getPassStr(this.pass(d));
    }

    public void mark() {
        this.timeStamp = System.currentTimeMillis();
    }

    public void mark(long ts) {
        this.timeStamp = ts;
    }

    public void mark(Date d) {
        this.timeStamp = d.getTime();
    }

    public long pass() {
        return System.currentTimeMillis() - this.timeStamp;
    }

    public long pass(long ts) {
        return ts - this.timeStamp;
    }

    public long pass(Date d) {
        return d.getTime() - this.timeStamp;
    }

    public boolean isAfter() {
        return this.timeStamp > System.currentTimeMillis();
    }

    public boolean isAfter(long ts) {
        return this.timeStamp > ts;
    }

    public boolean isAfter(Date d) {
        return this.timeStamp > d.getTime();
    }

    public boolean isBefore() {
        return this.timeStamp < System.currentTimeMillis();
    }

    public boolean isBefore(long ts) {
        return this.timeStamp < ts;
    }

    public boolean isBefore(Date d) {
        return this.timeStamp < d.getTime();
    }
}
