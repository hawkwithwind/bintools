//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

public class SpeedMeter {
    public static final int DEFAULT_SIZE = 60;
    public static final long DEFAULT_PERIOD = 1000L;
    private final int[] values;
    private final int size;
    private final long period;
    private final long start;
    private long last;

    public SpeedMeter() {
        this.values = new int[60];
        this.size = 60;
        this.period = 1000L;
        this.start = this.now();
        this.last = this.now();

        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = 0;
        }

    }

    public SpeedMeter(int size, long period) {
        this.values = new int[size];
        this.size = size;
        this.period = period;
        this.start = this.now();
        this.last = this.now();

        for (int i = 0; i < this.values.length; ++i) {
            this.values[i] = 0;
        }

    }

    public void access() {
        this.access(1);
    }

    public void access(int value) {
        synchronized (this) {
            long now = this.now();
            long index1 = (this.last - this.start) / this.period;
            long index2 = (now - this.start) / this.period;
            if (index1 != index2) {
                for (long i = index1 + 1L; i <= index2; ++i) {
                    int index = (int) i % this.size;
                    this.values[index] = 0;
                }
            }

            if (value > 0) {
                int index = (int) index2 % this.size;
                int[] var10000 = this.values;
                var10000[index] += value;
            }

            this.last = now;
        }
    }

    public int average() {
        synchronized (this) {
            int sum = 0;
            this.access(0);

            for (int i = 0; i < this.values.length; ++i) {
                sum += this.values[i];
            }

            return sum / this.values.length;
        }
    }

    private long now() {
        return System.currentTimeMillis();
    }
}
