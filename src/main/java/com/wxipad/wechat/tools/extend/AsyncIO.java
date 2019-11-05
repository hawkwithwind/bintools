//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import java.io.*;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AsyncIO {
    private static AsyncIO i = null;
    private final BufferedReader reader;
    private final BufferedWriter writer;
    private final AtomicBoolean running;
    private final LinkedList<String> inputs;
    private final LinkedList<String> outputs;
    private final Thread r;
    private final Thread w;

    public AsyncIO() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
        this.writer = new BufferedWriter(new OutputStreamWriter(System.out));
        this.running = new AtomicBoolean(false);
        this.inputs = new LinkedList();
        this.outputs = new LinkedList();
        this.r = new Thread(new Read(), "AsyncIO Read");
        this.w = new Thread(new Write(), "AsyncIO Write");
    }

    public static void init() {
        dispose();
        i = new AsyncIO();
        i.start();
    }

    public static void dispose() {
        if (i != null) {
            i.shutdown();
        }

    }

    public static void dispose(String msg) {
        if (i != null) {
            i.shutdown(msg);
        }

    }

    public static String read(long wait) {
        String line = null;
        if (i != null) {
            line = i.asyncRead(wait);
        }

        return line;
    }

    public static String read(long wait, boolean trim) {
        String str = read(wait);
        if (str != null) {
            if (trim) {
                str = str.trim();
            }

            if (str.isEmpty()) {
                str = null;
            }
        }

        return str;
    }

    public static String read(long wait, String tip) {
        echo(tip, true);
        return read(wait);
    }

    public static String read(long wait, boolean trim, String tip) {
        echo(tip, true);
        return read(wait, trim);
    }

    public static String read() {
        return read(0L);
    }

    public static String read(boolean trim) {
        return read(0L, trim);
    }

    public static String read(String tip) {
        echo(tip, true);
        return read();
    }

    public static String read(boolean trim, String tip) {
        echo(tip, true);
        return read(trim);
    }

    public static void echo(String... msg) {
        if (i != null) {
            i.syncWrite(msg);
        }

    }

    public static void echo(String msg, boolean noBreak) {
        if (i != null) {
            i.syncWrite(msg, noBreak);
        }

    }

    public static void echox(String... msg) {
        if (i != null) {
            i.asyncWrite(msg);
        }

    }

    public static void echox(String msg, boolean noBreak) {
        if (i != null) {
            i.asyncWrite(msg, noBreak);
        }

    }

    public void start() {
        if (this.running.compareAndSet(false, true)) {
            this.r.start();
            this.w.start();
        }

    }

    public void shutdown() {
        if (this.running.compareAndSet(true, false)) {
            this.r.interrupt();
            this.w.interrupt();
            synchronized (this.inputs) {
                this.inputs.notifyAll();
            }

            synchronized (this.outputs) {
                this.outputs.notifyAll();
            }
        }

    }

    public void shutdown(String msg) {
        if (msg != null) {
            this.syncWrite(msg);
        }

        if (this.running.compareAndSet(true, false)) {
            this.r.interrupt();
            this.w.interrupt();
            synchronized (this.inputs) {
                this.inputs.notifyAll();
            }

            synchronized (this.outputs) {
                this.outputs.notifyAll();
            }
        }

    }

    public String asyncRead(long timeout) {
        if (this.running.get()) {
            String line;
            synchronized (this.inputs) {
                line = (String) this.inputs.poll();
            }

            if (line == null) {
                synchronized (this.inputs) {
                    try {
                        if (timeout > 0L) {
                            this.inputs.wait(timeout);
                        } else {
                            this.inputs.wait();
                        }
                    } catch (InterruptedException var7) {
                        Logger.getLogger(AsyncIO.class.getName()).log(Level.SEVERE, (String) null, var7);
                    }

                    line = (String) this.inputs.poll();
                }
            }

            return line;
        } else {
            return null;
        }
    }

    public void asyncWrite(String... msg) {
        String[] var2 = msg;
        int var3 = msg.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String line = var2[var4];
            this.asyncWrite(line, false);
        }

        if (msg.length == 0) {
            this.asyncWrite("", false);
        }

    }

    public void asyncWrite(String msg, boolean noBreak) {
        if (this.running.get()) {
            String str = msg;
            if (!noBreak) {
                str = msg + "\n";
            }

            synchronized (this.outputs) {
                this.outputs.add(str);
                this.outputs.notifyAll();
            }
        }

    }

    public void syncWrite(String... msg) {
        String[] var2 = msg;
        int var3 = msg.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            String line = var2[var4];
            this.syncWrite(line, false);
        }

    }

    public void syncWrite(String msg, boolean noBreak) {
        if (this.running.get()) {
            String str = msg;
            if (!noBreak) {
                str = msg + "\n";
            }

            try {
                this.writer.write(str, 0, str.length());
                this.writer.flush();
            } catch (IOException var5) {
                Logger.getLogger(AsyncIO.class.getName()).log(Level.SEVERE, (String) null, var5);
            }
        }

    }

    private class Write implements Runnable {
        private Write() {
        }

        public void run() {
            while (true) {
                try {
                    if (AsyncIO.this.running.get()) {
                        String str;
                        synchronized (AsyncIO.this.outputs) {
                            str = (String) AsyncIO.this.outputs.poll();
                        }

                        if (str != null) {
                            AsyncIO.this.writer.write(str, 0, str.length());
                            AsyncIO.this.writer.flush();
                            continue;
                        }

                        try {
                            synchronized (AsyncIO.this.outputs) {
                                AsyncIO.this.outputs.wait(100L);
                            }
                        } catch (InterruptedException var6) {
                        }
                        continue;
                    }

                    AsyncIO.this.writer.close();
                } catch (IOException var8) {
                    Logger.getLogger(AsyncIO.class.getName()).log(Level.SEVERE, (String) null, var8);
                }

                return;
            }
        }
    }

    private class Read implements Runnable {
        private Read() {
        }

        public void run() {
            while (true) {
                try {
                    if (AsyncIO.this.running.get()) {
                        String line = AsyncIO.this.reader.readLine();
                        if (line != null) {
                            synchronized (AsyncIO.this.inputs) {
                                AsyncIO.this.inputs.add(line);
                                AsyncIO.this.inputs.notifyAll();
                                continue;
                            }
                        }
                    }

                    AsyncIO.this.reader.close();
                } catch (IOException var5) {
                    Logger.getLogger(AsyncIO.class.getName()).log(Level.SEVERE, (String) null, var5);
                }

                return;
            }
        }
    }
}
