//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import com.wxipad.wechat.tools.tool.ToolStr;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HardwareInfo {
    private static final String SPECIAL_MAC = "00000000000000E0";

    public HardwareInfo() {
    }

    public static String getWinCpuID() {
        String serial = null;

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"wmic", "cpu", "get", "ProcessorId"});
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream());

            try {
                sc.next();
                serial = sc.next();
            } finally {
                sc.close();
            }
        } catch (Exception var7) {
            serial = null;
        }

        if (serial != null) {
            serial = serial.trim().toUpperCase();
            if (serial.isEmpty()) {
                serial = null;
            } else if (serial.indexOf("WMIC") >= 0) {
                serial = null;
            }
        }

        return serial;
    }

    public static String getLinuxCpuID() {
        String serial = null;

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"dmidecode", "-t", "processor"});
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream());

            try {
                while (sc.hasNext()) {
                    String line = sc.nextLine();
                    if (line != null) {
                        line = line.trim();
                        if (line.startsWith("ID:") && line.length() > 3) {
                            serial = line.substring(3).replace(" ", "");
                        }
                    }
                }
            } finally {
                sc.close();
            }
        } catch (Exception var8) {
            serial = null;
        }

        if (serial != null) {
            serial = serial.trim().toUpperCase();
            if (serial.isEmpty()) {
                serial = null;
            }
        }

        return serial;
    }

    public static String getMacUUID() {
        String uuid = null;

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"ioreg", "-d2", "-c", "IOPlatformExpertDevice"});
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream());

            try {
                while (sc.hasNext()) {
                    String line = sc.nextLine();
                    if (line != null) {
                        line = line.trim();
                        if (line.startsWith("\"IOPlatformUUID\"")) {
                            int index = line.indexOf("=");
                            if (index >= 0 && index < line.length() - 1) {
                                String uuidStr = line.substring(index + 1).trim();
                                if (uuidStr.startsWith("\"") && uuidStr.endsWith("\"") && uuidStr.length() > 2) {
                                    uuidStr = uuidStr.substring(1, uuidStr.length() - 1);
                                }

                                uuid = uuidStr;
                            }
                        }
                    }
                }
            } finally {
                sc.close();
            }
        } catch (Exception var10) {
            uuid = null;
        }

        if (uuid != null) {
            uuid = uuid.trim().toUpperCase();
            if (uuid.isEmpty()) {
                uuid = null;
            }
        }

        return uuid;
    }

    public static String getSerial() {
        String osname = System.getProperty("os.name");
        osname = osname == null ? "" : osname.toLowerCase();
        if (osname.indexOf("windows") >= 0) {
            return getWinCpuID();
        } else if (osname.indexOf("linux") >= 0) {
            return getLinuxCpuID();
        } else {
            return osname.indexOf("mac") >= 0 ? getMacUUID() : null;
        }
    }

    public static ArrayList<String> getMacAddress() {
        ArrayList macList = new ArrayList();

        try {
            Enumeration networkInterfaces = NetworkInterface.getNetworkInterfaces();

            while (networkInterfaces.hasMoreElements()) {
                NetworkInterface ni = (NetworkInterface) networkInterfaces.nextElement();
                byte[] hardwareAddress = ni.getHardwareAddress();
                if (hardwareAddress != null && hardwareAddress.length > 0) {
                    String mac = ToolStr.bytes2Hex(hardwareAddress).toUpperCase();
                    if (!"00000000000000E0".equals(mac)) {
                        macList.add(mac);
                    }
                }
            }
        } catch (SocketException var5) {
            Logger.getLogger(HardwareInfo.class.getName()).log(Level.SEVERE, (String) null, var5);
        }

        return macList;
    }
}
