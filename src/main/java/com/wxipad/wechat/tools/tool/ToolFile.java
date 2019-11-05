//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.tool;

import com.wxipad.wechat.tools.constant.ConstFramework;
import com.wxipad.wechat.tools.crypto.Digest;
import com.wxipad.wechat.tools.crypto.Digest.ALGORITHM;

import java.io.*;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.core.codec.ResourceRegionEncoder.DEFAULT_BUFFER_SIZE;

public class ToolFile {
    public static final FileFilter NONE_FILTER = null;
    public static final FileFilter DIR_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return pathname.isDirectory();
        }
    };
    public static final FileFilter NOT_DIR_FILTER = new FileFilter() {
        public boolean accept(File pathname) {
            return !pathname.isDirectory();
        }
    };
    private static final long UNIT_RATE = 1024L;
    private static final String[] UNIT_NAME = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};
    private static final int NEW_FILENAME_LEN = 8;
    private static final int NEW_FILENAME_RETRY = 16;
    private static final int READ_BYTES_MAX = 10485760;
    private static final String[] SPECIAL_EXT = new String[]{".tar.bz2", ".tar.gz", ".tar.tar", ".tar.zip", ".tar.z"};
    private static int DEFAULT_BUFF_SIZE = 1024;

    public ToolFile() {
    }

    public static String getFileNoExtName(String fileName) {
        int i;
        for (i = 0; i < SPECIAL_EXT.length; ++i) {
            if (fileName.endsWith(SPECIAL_EXT[i])) {
                return fileName.substring(0, fileName.length() - SPECIAL_EXT[i].length());
            }
        }

        if (i == SPECIAL_EXT.length) {
            int extDotIndex = fileName.lastIndexOf(46);
            if (extDotIndex >= 0) {
                return fileName.substring(0, extDotIndex);
            }
        }

        return fileName;
    }

    public static String getFileExt(String fileName) {
        int i;
        for (i = 0; i < SPECIAL_EXT.length; ++i) {
            if (fileName.endsWith(SPECIAL_EXT[i])) {
                return SPECIAL_EXT[i].substring(1);
            }
        }

        if (i == SPECIAL_EXT.length) {
            int extDotIndex = fileName.lastIndexOf(46);
            if (extDotIndex >= 0) {
                return fileName.substring(extDotIndex + 1);
            }
        }

        return "";
    }

    public static boolean moveFile(String source, String target) {
        return moveFile(new File(source), new File(target));
    }

    public static boolean moveFile(File source, File target) {
        if (source != null && target != null) {
            if (!source.exists()) {
                return false;
            } else {
                if (target.exists()) {
                    deleteFile(target);
                }

                if (source.renameTo(target)) {
                    return true;
                } else {
                    return copyFile(source, target) && deleteFile(source);
                }
            }
        } else {
            return false;
        }
    }

    public static boolean copyFile(String source, String target) {
        return copyFile(new File(source), new File(target));
    }

    public static boolean copyFile(File source, File target) {
        if (source != null && target != null) {
            if (source.exists()) {
                if (source.isDirectory()) {
                    if (!target.exists()) {
                        target.mkdirs();
                    } else if (!target.isDirectory()) {
                        deleteFile(target);
                        target.mkdirs();
                    }

                    File[] var11 = source.listFiles();
                    int var3 = var11.length;

                    for (int var4 = 0; var4 < var3; ++var4) {
                        File f = var11[var4];
                        if (!copyFile(f, new File(target.getAbsolutePath() + File.separator + f.getName()))) {
                            return false;
                        }
                    }

                    return true;
                } else {
                    if (target.exists()) {
                        deleteFile(target);
                    }

                    try {
                        FileInputStream in = new FileInputStream(source);

                        try {
                            writeInputStreamToFile(in, (File) target);
                        } finally {
                            in.close();
                        }

                        return true;
                    } catch (IOException var10) {
                        Logger.getLogger(ToolFile.class.getName()).log(Level.SEVERE, (String) null, var10);
                        return false;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean deleteFile(String filePath) {
        return filePath == null ? true : deleteFile(new File(filePath));
    }

    public static boolean deleteFile(File file) {
        if (!file.exists()) {
            return true;
        } else {
            if (file.isDirectory()) {
                File[] var1 = file.listFiles();
                int var2 = var1.length;

                for (int var3 = 0; var3 < var2; ++var3) {
                    File child = var1[var3];
                    deleteFile(child);
                }
            }

            return file.delete();
        }
    }

    public static boolean existFile(String filePath) {
        if (filePath == null) {
            return false;
        } else {
            File file = new File(filePath);
            return file.exists();
        }
    }

    public static boolean checkFileName(String fileName) {
        return checkFileName(fileName, (String) null);
    }

    public static boolean checkFileName(String fileName, String ext) {
        if (fileName == null) {
            return false;
        } else if (fileName.indexOf(47) >= 0) {
            return false;
        } else if (fileName.indexOf(92) >= 0) {
            return false;
        } else {
            if (ext != null) {
                String fileExt = getFileExt(fileName);
                if (!ToolStr.equals(fileExt, ext, false)) {
                    return false;
                }
            }

            return true;
        }
    }

    public static boolean checkPath(String dirPath) {
        if (dirPath == null) {
            return false;
        } else {
            File dir = new File(dirPath);
            return dir.exists() ? dir.isDirectory() : dir.mkdirs();
        }
    }

    public static long size(String filePath) {
        if (filePath == null) {
            return 0L;
        } else {
            File file = new File(filePath);
            return size(file);
        }
    }

    public static long size(File file) {
        if (file != null && file.exists()) {
            if (!file.isDirectory()) {
                return file.length();
            } else {
                File[] listFiles = file.listFiles();
                long total = 0L;
                File[] var4 = listFiles;
                int var5 = listFiles.length;

                for (int var6 = 0; var6 < var5; ++var6) {
                    File f = var4[var6];
                    total += size(f);
                }

                return total;
            }
        } else {
            return 0L;
        }
    }

    public static String read(String path) {
        return read(new File(path), ConstFramework.getCharset());
    }

    public static String read(File file) {
        return read(file, ConstFramework.getCharset());
    }

    public static String read(String path, String charset) {
        return read(new File(path), charset);
    }

    public static String read(String path, Charset charset) {
        return read(new File(path), charset);
    }

    public static String read(File file, String charset) {
        try {
            charset = ToolStr.nullToDefault(charset, ConstFramework.getCharsetName());
            byte[] bytes = readByte(file);
            return new String(bytes, charset);
        } catch (Exception var3) {
            Logger.getLogger(ToolFile.class.getName()).log(Level.SEVERE, (String) null, var3);
            return null;
        }
    }

    public static String read(File file, Charset charset) {
        byte[] bytes = readByte(file);
        charset = charset != null ? charset : ConstFramework.getCharset();
        return new String(bytes, charset);
    }

    public static byte[] readByte(String path) {
        return readByte(new File(path));
    }

    public static byte[] readByte(File file) {
        try {
            if (!file.exists()) {
                return null;
            } else {
                long length = file.length();
                if (length > 10485760L) {
                    return null;
                } else {
                    FileInputStream in = new FileInputStream(file);

                    byte[] var6;
                    try {
                        byte[] data = new byte[(int) length];
                        int read = in.read(data);
                        if ((long) read != length) {
                            Object var12 = null;
                            return (byte[]) var12;
                        }

                        var6 = data;
                    } finally {
                        in.close();
                    }

                    return var6;
                }
            }
        } catch (Exception var11) {
            Logger.getLogger(ToolFile.class.getName()).log(Level.SEVERE, (String) null, var11);
            return null;
        }
    }

    public static boolean write(String path, String content) {
        return write(new File(path), content, ConstFramework.getCharsetName());
    }

    public static boolean write(File file, String content) {
        return write(file, content, ConstFramework.getCharsetName());
    }

    public static boolean write(String path, String content, String charset) {
        return write(new File(path), content, charset);
    }

    public static boolean write(String path, String content, Charset charset) {
        return writeBytes(path, content.getBytes(charset));
    }

    public static boolean write(File file, String content, String charset) {
        try {
            charset = ToolStr.nullToDefault(charset, ConstFramework.getCharsetName());
            return writeBytes(file, content.getBytes(charset));
        } catch (Exception var4) {
            Logger.getLogger(ToolFile.class.getName()).log(Level.SEVERE, (String) null, var4);
            return false;
        }
    }

    public static boolean write(File file, String content, Charset charset) {
        charset = charset != null ? charset : ConstFramework.getCharset();
        return writeBytes(file, content.getBytes(charset));
    }

    public static boolean writeBytes(String path, byte[] data) {
        return writeBytes(new File(path), data);
    }

    public static boolean writeBytes(File file, byte[] data) {
        try {
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream out = new FileOutputStream(file);
            if (data != null) {
                out.write(data);
            }

            out.close();
            return true;
        } catch (Exception var3) {
            Logger.getLogger(ToolFile.class.getName()).log(Level.SEVERE, (String) null, var3);
            return false;
        }
    }

    public static ArrayList<String> readLines(String filePath, String charset) {
        return filePath == null ? null : readLines(new File(filePath), charset);
    }

    public static ArrayList<String> readLines(File file, String charset) {
        if (file != null && file.exists() && !file.isDirectory()) {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), charset));

                try {
                    ArrayList lines = new ArrayList();

                    String line;
                    while ((line = reader.readLine()) != null) {
                        lines.add(line);
                    }

                    ArrayList var5 = lines;
                    return var5;
                } finally {
                    reader.close();
                }
            } catch (Exception var10) {
                Logger.getLogger(ToolFile.class.getName()).log(Level.SEVERE, (String) null, var10);
                return null;
            }
        } else {
            return null;
        }
    }

    public static boolean writeLines(String filePath, String charset, ArrayList<String> lines, boolean append) {
        return filePath == null ? false : writeLines(new File(filePath), charset, lines, append);
    }

    public static boolean writeLines(File file, String charset, ArrayList<String> lines, boolean append) {
        if (file != null && !file.isDirectory()) {
            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), charset));
                boolean echoBr = append;

                String line;
                for (Iterator var6 = lines.iterator(); var6.hasNext(); writer.write(line)) {
                    line = (String) var6.next();
                    if (!echoBr) {
                        echoBr = true;
                    } else {
                        writer.newLine();
                    }
                }

                writer.flush();
                writer.close();
                return true;
            } catch (Exception var8) {
                Logger.getLogger(ToolFile.class.getName()).log(Level.SEVERE, (String) null, var8);
                return false;
            }
        } else {
            return false;
        }
    }

    public static ArrayList<File> find(File dir) {
        ArrayList<File> list = new ArrayList();
        findAndAdd(dir, list, true, NONE_FILTER);
        return list;
    }

    public static ArrayList<File> find(File dir, FilePatternFilter filter) {
        ArrayList<File> list = new ArrayList();
        findAndAdd(dir, list, true, (FilenameFilter) filter);
        return list;
    }

    public static ArrayList<File> findFile(File dir) {
        ArrayList<File> list = new ArrayList();
        findAndAdd(dir, list, false, NONE_FILTER);
        return list;
    }

    public static ArrayList<File> findFile(File dir, FilePatternFilter filter) {
        ArrayList<File> list = new ArrayList();
        findAndAdd(dir, list, false, (FilenameFilter) filter);
        return list;
    }

    public static ArrayList<File> find(File dir, FileFilter filter) {
        ArrayList<File> list = new ArrayList();
        findAndAdd(dir, list, true, filter);
        return list;
    }

    public static ArrayList<File> find(File dir, FilenameFilter filter) {
        ArrayList<File> list = new ArrayList();
        findAndAdd(dir, list, true, filter);
        return list;
    }

    private static void findAndAdd(File file, ArrayList<File> list, boolean hasDir, FileFilter filter) {
        if (file.isDirectory()) {
            File[] childs = file.listFiles(filter);
            File[] dirs = childs;
            int var6 = childs.length;

            int var7;
            for (var7 = 0; var7 < var6; ++var7) {
                File cf = dirs[var7];
                if (!cf.isDirectory() || hasDir) {
                    list.add(cf);
                }
            }

            dirs = file.listFiles(DIR_FILTER);
            File[] var10 = dirs;
            var7 = dirs.length;

            for (int var11 = 0; var11 < var7; ++var11) {
                File df = var10[var11];
                findAndAdd(df, list, hasDir, filter);
            }

        }
    }

    private static void findAndAdd(File file, ArrayList<File> list, boolean hasDir, FilenameFilter filter) {
        if (file.isDirectory()) {
            File[] childs = file.listFiles(filter);
            File[] dirs = childs;
            int var6 = childs.length;

            int var7;
            for (var7 = 0; var7 < var6; ++var7) {
                File cf = dirs[var7];
                if (!cf.isDirectory() || hasDir) {
                    list.add(cf);
                }
            }

            dirs = file.listFiles(DIR_FILTER);
            File[] var10 = dirs;
            var7 = dirs.length;

            for (int var11 = 0; var11 < var7; ++var11) {
                File df = var10[var11];
                findAndAdd(df, list, hasDir, filter);
            }

        }
    }

    public static String getSizeStr(long size) {
        float num = (float) size;
        int index = 0;
        if (size < 1024L) {
            return size + UNIT_NAME[0];
        } else {
            while (num >= 1024.0F && index < UNIT_NAME.length) {
                num /= 1024.0F;
                ++index;
            }

            return fixSizeStr(num) + UNIT_NAME[index];
        }
    }

    private static String fixSizeStr(float num) {
        if (num < 10.0F) {
            DecimalFormat format = new DecimalFormat("0.0");
            return format.format((double) num);
        } else {
            return Integer.toString((int) num);
        }
    }

    public static String getDatePath(boolean hasDay) {
        return hasDay ? (new SimpleDateFormat("yyyy/MM/dd")).format(new Date()) : (new SimpleDateFormat("yyyy/MM")).format(new Date());
    }

    public static String getRealPath(String path) {
        if (path == null) {
            return null;
        } else {
            path = path.replace('\\', '/');
            boolean hasPrefix = path.startsWith("/");
            if (hasPrefix) {
                path = path.substring(1);
            }

            boolean hasSuffix = path.endsWith("/");
            if (hasSuffix) {
                path = path.substring(0, path.length() - 1);
            }

            ArrayList<String> names = new ArrayList();
            String[] var4 = path.split("/");
            int var5 = var4.length;

            for (int var6 = 0; var6 < var5; ++var6) {
                String name = var4[var6];
                if (name.equals("..")) {
                    if (names.size() > 0 && !((String) names.get(names.size() - 1)).equals("..")) {
                        names.remove(names.size() - 1);
                    } else {
                        names.add(name);
                    }
                } else if (!name.equals(".")) {
                    names.add(name);
                }
            }

            StringBuilder newPath = null;
            Iterator var9 = names.iterator();

            while (var9.hasNext()) {
                String name = (String) var9.next();
                if (newPath == null) {
                    newPath = new StringBuilder(name);
                } else {
                    newPath.append("/").append(name);
                }
            }

            if (newPath == null) {
                newPath = new StringBuilder();
            }

            return (hasPrefix ? "/" : "") + newPath.toString() + (hasSuffix ? "/" : "");
        }
    }

    public static String convertPath(String path, String[] oldPrefix, String[] newPrefix) {
        return convertPath(path, oldPrefix, newPrefix, (String) null);
    }

    public static String convertPath(String path, String[] oldPrefix, String[] newPrefix, String defaultPath) {
        if (path != null && oldPrefix != null && newPrefix != null && oldPrefix.length == newPrefix.length) {
            for (int i = 0; i < oldPrefix.length; ++i) {
                if (path.startsWith(oldPrefix[i])) {
                    return newPrefix[i] + path.substring(oldPrefix[i].length());
                }
            }

            return path;
        } else {
            return defaultPath;
        }
    }

    public static String getNewFileName(String path, String ext) {
        return getNewFileName(path, ext, 8, 16);
    }

    public static String getNewFileName(String path, String ext, int len) {
        return getNewFileName(path, ext, len, 16);
    }

    public static String getNewFileName(String path, String ext, int len, int cnt) {
        if (!ext.startsWith(".")) {
            ext = "." + ext;
        }

        for (int i = 0; i < cnt; ++i) {
            String fileName = ToolStr.randomMD5().substring(0, len) + ext;
            if (!(new File(path + fileName)).exists()) {
                return fileName;
            }
        }

        return null;
    }

    public static String getFileMd5(String path) {
        return getFileMd5(new File(path));
    }

    public static String getFileMd5(File file) {
        if (file.exists()) {
            try {
                FileInputStream in = new FileInputStream(file);

                String var2;
                try {
                    var2 = ToolStr.bytes2Hex(Digest.encode(in, ALGORITHM.MD5));
                } finally {
                    in.close();
                }

                return var2;
            } catch (IOException var7) {
                return null;
            }
        } else {
            return null;
        }
    }

    public static void writeInputStreamToFile(InputStream in, String filepath) throws IOException {
        writeInputStreamToFile(in, new File(filepath));
    }

    public static void writeInputStreamToFile(InputStream in, File file) throws IOException {
        byte[] data = new byte[DEFAULT_BUFF_SIZE];
        FileOutputStream out = new FileOutputStream(file);

        int read;
        try {
            while ((read = in.read(data)) != -1) {
                out.write(data, 0, read);
            }
        } finally {
            out.close();
        }

    }

    public static long lineCount(String path) {
        return lineCount(new File(path));
    }

    public static long lineCount(File file) {
        if (file.exists() && file.length() != 0L) {
            try {
                long count = 0L;
                boolean content = false;
                FileInputStream in = new FileInputStream(file);

                int read;
                try {
                    while ((read = in.read()) != -1) {
                        if (read != 13 && read != 10) {
                            if (!content) {
                                content = true;
                            }
                        } else {
                            if (content) {
                                ++count;
                            }

                            content = false;
                        }
                    }
                } finally {
                    in.close();
                }

                if (content) {
                    ++count;
                }

                return count;
            } catch (IOException var10) {
                return -1L;
            }
        } else {
            return 0L;
        }
    }

    public static class FilePatternFilter implements FilenameFilter {
        private final Pattern pettern;
        private final boolean useMatches;

        public FilePatternFilter(String regex) {
            this.pettern = Pattern.compile(regex);
            this.useMatches = false;
        }

        public FilePatternFilter(String regex, boolean matches) {
            this.pettern = Pattern.compile(regex);
            this.useMatches = matches;
        }

        public boolean accept(File dir, String name) {
            Matcher matcher = this.pettern.matcher(name);
            return this.useMatches ? matcher.matches() : matcher.find();
        }
    }

    /**
     * 将字符串写入指定文件(当指定的父路径中文件夹不存在时，会最大限度去创建，以保证保存成功！)
     *
     * @param res      原字符串
     * @param filePath 文件路径
     * @return 成功标记
     */
    public static boolean string2File(String res, String filePath) {
        boolean flag = true;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;
        try {
            File distFile = new File(filePath);
            if (!distFile.getParentFile().exists()) distFile.getParentFile().mkdirs();
            bufferedReader = new BufferedReader(new StringReader(res));
            bufferedWriter = new BufferedWriter(new FileWriter(distFile));
            char buf[] = new char[1024];         //字符缓冲区
            int len;
            while ((len = bufferedReader.read(buf)) != -1) {
                bufferedWriter.write(buf, 0, len);
            }
            bufferedWriter.flush();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            flag = false;
            return flag;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return flag;
    }

    /**
     * 文本文件转换为指定编码的字符串
     *
     * @param file     文本文件
     * @param encoding 编码类型
     * @return 转换后的字符串
     * @throws IOException
     */
    public static String file2String(File file, String encoding) {
        InputStreamReader reader = null;
        StringWriter writer = new StringWriter();
        try {
            if (encoding == null || "".equals(encoding.trim())) {
                reader = new InputStreamReader(new FileInputStream(file), encoding);
            } else {
                reader = new InputStreamReader(new FileInputStream(file));
            }
            //将输入流写入输出流
            char[] buffer = new char[DEFAULT_BUFFER_SIZE];
            int n = 0;
            while (-1 != (n = reader.read(buffer))) {
                writer.write(buffer, 0, n);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        //返回转换结果
        if (writer != null)
            return writer.toString();
        else return null;
    }
}
