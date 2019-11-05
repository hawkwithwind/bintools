package com.wxipad.wechat.tools.tool;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToolExec {
    private static final int READ_SIZE = 4096;

    public static ExecResult exec(String cmd) {
        try {
            Process pid = Runtime.getRuntime().exec(cmd);
            return solveProcess(pid);
        } catch (InterruptedException ex) {
            Logger.getLogger(ToolExec.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(ToolExec.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static ExecResult exec(String[] cmd) {
        try {
            Process pid = Runtime.getRuntime().exec(cmd);
            return solveProcess(pid);
        } catch (InterruptedException ex) {
            Logger.getLogger(ToolExec.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (IOException ex) {
            Logger.getLogger(ToolExec.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    private static ExecResult solveProcess(Process process)
            throws IOException, InterruptedException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()), 4096);
        try {
            String line = br.readLine();
            while (line != null) {
                sb.append(line).append('\n');
                line = br.readLine();
            }
            int exit = process.waitFor();
            return new ExecResult(exit, sb.toString());
        } finally {
            br.close();
        }
    }

    public static class ExecResult {
        public int exit;
        public String output;

        public ExecResult(int exit, String output) {
            this.exit = exit;
            this.output = output;
        }
    }
}
