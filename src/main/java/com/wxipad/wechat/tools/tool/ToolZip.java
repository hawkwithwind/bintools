//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.tool;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.*;

public class ToolZip {
    private static final int BUFF_SIZE = 4096;

    public ToolZip() {
    }

    public static byte[] compress(byte[] data) {
        byte[] output = null;
        Deflater compresser = new Deflater();
        compresser.reset();
        compresser.setInput(data);
        compresser.finish();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[4096];

            while (true) {
                if (compresser.finished()) {
                    output = bos.toByteArray();
                    break;
                }

                int i = compresser.deflate(buf);
                bos.write(buf, 0, i);
            }
        } finally {
            try {
                bos.close();
            } catch (IOException var11) {
                Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var11);
            }

        }

        compresser.end();
        return output;
    }

    public static void compress(byte[] data, OutputStream os) {
        DeflaterOutputStream dos = new DeflaterOutputStream(os);

        try {
            dos.write(data, 0, data.length);
            dos.finish();
            dos.flush();
        } catch (IOException var4) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var4);
        }

    }

    public static byte[] decompress(byte[] data) {
        byte[] output = null;
        Inflater decompresser = new Inflater();
        decompresser.reset();
        decompresser.setInput(data);
        ByteArrayOutputStream o = new ByteArrayOutputStream(data.length);
        try {
            byte[] buf = new byte[4096];

            while (!decompresser.finished()) {
                int i = decompresser.inflate(buf);
                o.write(buf, 0, i);
            }

            output = o.toByteArray();
        } catch (Exception var14) {
            output = data;
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var14);
        } finally {
            try {
                o.close();
            } catch (IOException var13) {
                Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var13);
            }

        }

        decompresser.end();
        return output;
    }

    public static byte[] decompress(InputStream is) {
        InflaterInputStream iis = new InflaterInputStream(is);
        ByteArrayOutputStream o = new ByteArrayOutputStream(4096);

        try {
            byte[] buf = new byte[4096];

            int i;
            while ((i = iis.read(buf)) > 0) {
                o.write(buf, 0, i);
            }
        } catch (IOException var5) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var5);
        }

        return o.toByteArray();
    }

    public static boolean gzip(File source, File dist) {
        try {
            BufferedInputStream in = new BufferedInputStream(new FileInputStream(source));
            BufferedOutputStream out = new BufferedOutputStream(new GZIPOutputStream(new FileOutputStream(dist)));
            byte[] buff = new byte[4096];

            int read;
            while ((read = in.read(buff)) != -1) {
                out.write(buff, 0, read);
            }

            in.close();
            out.close();
            return true;
        } catch (Exception var6) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var6);
            return false;
        }
    }

    public static boolean ungzip(File source, File dist) {
        try {
            BufferedInputStream in = new BufferedInputStream(new GZIPInputStream(new FileInputStream(source)));
            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dist));
            byte[] buff = new byte[4096];

            int read;
            while ((read = in.read(buff)) != -1) {
                out.write(buff, 0, read);
            }

            in.close();
            out.close();
            return true;
        } catch (Exception var6) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var6);
            return false;
        }
    }

    public static boolean zip(File[] files, File zipFile) {
        return zip(files, zipFile, false, -1);
    }

    public static boolean zip(File[] files, File zipFile, boolean stored) {
        return zip(files, zipFile, stored, -1);
    }

    public static boolean zip(File[] files, File zipFile, int level) {
        return zip(files, zipFile, false, level);
    }

    private static boolean zip(File[] files, File zipFile, boolean stored, int level) {
        try {
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(zipFile)));

            try {
                out.setMethod(stored ? 0 : 8);
                out.setLevel(level);
                File[] var5 = files;
                int var6 = files.length;

                for (int var7 = 0; var7 < var6; ++var7) {
                    File f = var5[var7];
                    zip(out, f, stored, "");
                }
            } finally {
                out.close();
            }

            return true;
        } catch (Exception var13) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var13);
            return false;
        }
    }

    private static void zip(ZipOutputStream out, File file, boolean stored, String basePath) throws IOException {
        String name;
        ZipEntry entry;
        if (file.isDirectory()) {
            name = basePath + file.getName() + "/";
            entry = new ZipEntry(name);
            entry.setMethod(0);
            entry.setCompressedSize(0L);
            entry.setSize(0L);
            entry.setTime(file.lastModified());
            entry.setCrc(0L);
            out.putNextEntry(entry);
            out.closeEntry();
            File[] listFiles = file.listFiles();
            File[] var7 = listFiles;
            int var8 = listFiles.length;

            for (int var9 = 0; var9 < var8; ++var9) {
                File f = var7[var9];
                zip(out, f, stored, name);
            }
        } else {
            name = basePath + file.getName();
            entry = new ZipEntry(name);
            if (stored) {
                entry.setMethod(0);
                entry.setCompressedSize(file.length());
                entry.setSize(file.length());
                entry.setTime(file.lastModified());
                entry.setCrc(getCRC32(file));
            } else {
                entry.setMethod(8);
            }

            out.putNextEntry(entry);

            try {
                BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));

                try {
                    byte[] buff = new byte[4096];

                    int read;
                    while ((read = in.read(buff)) != -1) {
                        out.write(buff, 0, read);
                    }
                } finally {
                    in.close();
                }
            } finally {
                out.closeEntry();
            }
        }

    }

    public static boolean unzip(File zipFile, String basePath) {
        try {
            if (!ToolFile.checkPath(basePath)) {
                return false;
            } else {
                if (!basePath.endsWith(File.separator)) {
                    basePath = basePath + File.separator;
                }

                ZipInputStream in = new ZipInputStream(new FileInputStream(zipFile));

                ZipEntry entry;
                try {
                    while ((entry = in.getNextEntry()) != null) {
                        String entryName = entry.getName().replace('/', File.separatorChar);
                        if (!entry.isDirectory() && !entryName.endsWith(File.separator)) {
                            BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(basePath + entryName));

                            try {
                                byte[] buff = new byte[4096];

                                int read;
                                while ((read = in.read(buff)) != -1) {
                                    out.write(buff, 0, read);
                                }
                            } finally {
                                out.close();
                            }
                        } else {
                            File dir = new File(basePath + entryName);
                            dir.mkdir();
                        }
                    }
                } finally {
                    in.close();
                }

                return true;
            }
        } catch (Exception var18) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var18);
            return false;
        }
    }

    public static long getCRC32(byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            return getChecksum(in, new CRC32());
        } catch (Exception var2) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var2);
            return 0L;
        }
    }

    public static long getAdler32(byte[] data) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(data);
            return getChecksum(in, new Adler32());
        } catch (Exception var2) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var2);
            return 0L;
        }
    }

    public static long getCRC32(File file) {
        try {
            FileInputStream in = new FileInputStream(file);

            long var2;
            try {
                var2 = getChecksum(in, new CRC32());
            } finally {
                in.close();
            }

            return var2;
        } catch (Exception var8) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var8);
            return 0L;
        }
    }

    public static long getAdler32(File file) {
        try {
            FileInputStream in = new FileInputStream(file);

            long var2;
            try {
                var2 = getChecksum(in, new Adler32());
            } finally {
                in.close();
            }

            return var2;
        } catch (Exception var8) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var8);
            return 0L;
        }
    }

    public static long getChecksum(InputStream in, Checksum cs) {
        try {
            long checksum = 0L;
            CheckedInputStream cin = new CheckedInputStream(in, cs);
            byte[] buff = new byte[4096];

            try {
                while (cin.read(buff) != -1) {
                }

                checksum = cin.getChecksum().getValue();
                return checksum;
            } finally {
                cin.close();
            }
        } catch (Exception var10) {
            Logger.getLogger(ToolZip.class.getName()).log(Level.SEVERE, (String) null, var10);
            return 0L;
        }
    }
}
