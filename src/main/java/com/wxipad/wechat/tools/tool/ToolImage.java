//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.tool;

import com.wxipad.wechat.tools.constant.ConstFramework;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.plugins.bmp.BMPImageWriteParam;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.*;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ToolImage {
    public static final String FORMAT_PNG = "png";
    public static final String FORMAT_JPEG = "jpeg";
    public static final String FORMAT_JPG = "jpg";
    public static final String FORMAT_BMP = "bmp";
    public static final String FORMAT_GIF = "gif";
    private static final int BUFF_SIZE = 1024;
    private static final float IMG_QUALITY = 0.95F;

    public ToolImage() {
    }

    public static String getExt(InputStream is) {
        byte[] head = new byte[10];

        try {
            is.read(head, 0, head.length);
            if (head[0] == 71 && head[1] == 73 && head[2] == 70 && head[3] == 56 && (head[4] == 55 || head[4] == 57) && head[5] == 97) {
                return "gif";
            } else if (head[6] == 74 && head[7] == 70 && head[8] == 73 && head[9] == 70) {
                return "jpeg";
            } else if (head[6] == 69 && head[7] == 120 && head[8] == 105 && head[9] == 102) {
                return "jpg";
            } else if (head[0] == 66 && head[1] == 77) {
                return "bmp";
            } else {
                return head[1] == 80 && head[2] == 78 && head[3] == 71 ? "png" : null;
            }
        } catch (IOException var3) {
            Logger.getLogger(ToolImage.class.getName()).log(Level.SEVERE, (String) null, var3);
            return null;
        }
    }

    public static BufferedImage getFromFile(String path) {
        try {
            return ImageIO.read(new FileInputStream(path));
        } catch (Exception var2) {
            Logger.getLogger(ToolImage.class.getName()).log(Level.SEVERE, (String) null, var2);
            return null;
        }
    }

    public static BufferedImage resize(BufferedImage img, float zoom) {
        return resize(img, zoom, 0, 0, 0, 0, 0, 0);
    }

    public static BufferedImage resize(BufferedImage img, float zoom, int cutLeft, int cutTop, int cutWidth, int cutHeight) {
        return resize(img, zoom, cutLeft, cutTop, cutWidth, cutHeight, 0, 0);
    }

    public static BufferedImage resize(BufferedImage img, float zoom, int cutLeft, int cutTop, int cutWidth, int cutHeight, int maxWidth, int maxHeight) {
        int newWidth = Math.round((float) img.getWidth() * zoom);
        int newHeight = Math.round((float) img.getHeight() * zoom);
        if ((maxWidth <= 0 || newWidth <= maxWidth) && (maxHeight <= 0 || newHeight <= maxHeight)) {
            Image scale = img.getScaledInstance(newWidth, newHeight, 4);
            BufferedImage newImg;
            Graphics graphics;
            if (cutWidth != 0 && cutHeight != 0) {
                newImg = getNewRGB(cutWidth, cutHeight);
                graphics = newImg.getGraphics();
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, cutWidth, cutHeight);
                graphics.drawImage(scale, -cutLeft, -cutTop, (ImageObserver) null);
                graphics.dispose();
                return newImg;
            } else {
                newImg = getNewRGB(newWidth, newHeight);
                graphics = newImg.getGraphics();
                graphics.setColor(Color.WHITE);
                graphics.fillRect(0, 0, newWidth, newHeight);
                graphics.drawImage(scale, 0, 0, (ImageObserver) null);
                graphics.dispose();
                return newImg;
            }
        } else {
            return null;
        }
    }

    public static BufferedImage thumbnails(BufferedImage img, int width, int height) {
        int curWidth = img.getWidth();
        int curHeight = img.getHeight();
        float zoomWidth = (float) curWidth / (float) width;
        float zoomHeight = (float) curHeight / (float) height;
        float zoom = Math.min(zoomHeight, zoomWidth);
        int newWidth = Math.round((float) curWidth / zoom);
        int newHeight = Math.round((float) curHeight / zoom);
        Image scale = img.getScaledInstance(newWidth, newHeight, 4);
        BufferedImage newImg = getNewRGB(width, height);
        Graphics graphics = newImg.getGraphics();
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        graphics.drawImage(scale, -(newWidth - width) / 2, -(newHeight - height) / 2, (ImageObserver) null);
        graphics.dispose();
        return newImg;
    }

    public static BufferedImage limit(BufferedImage img, int maxWidth, int maxHeight) {
        int curWidth = img.getWidth();
        int curHeight = img.getHeight();
        if (curWidth <= maxWidth && curHeight <= maxHeight) {
            return img;
        } else {
            float zoomWidth = (float) curWidth / (float) maxWidth;
            float zoomHeight = (float) curHeight / (float) maxHeight;
            float zoom = Math.max(zoomHeight, zoomWidth);
            int newWidth = Math.round((float) curWidth / zoom);
            int newHeight = Math.round((float) curHeight / zoom);
            Image scale = img.getScaledInstance(newWidth, newHeight, 4);
            BufferedImage newImg = getNewRGB(newWidth, newHeight);
            Graphics graphics = newImg.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, newWidth, newHeight);
            graphics.drawImage(scale, 0, 0, (ImageObserver) null);
            graphics.dispose();
            return newImg;
        }
    }

    public static boolean limitSizeSaveJpg(BufferedImage img, String path, int maxWidth, int maxHeight) {
        return limitSizeSave(img, path, maxWidth, maxHeight, "jpeg");
    }

    public static boolean limitSizeSavePng(BufferedImage img, String path, int maxWidth, int maxHeight) {
        return limitSizeSave(img, path, maxWidth, maxHeight, "png");
    }

    public static boolean limitSizeSaveGif(BufferedImage img, String path, int maxWidth, int maxHeight) {
        return limitSizeSave(img, path, maxWidth, maxHeight, "gif");
    }

    public static boolean limitSizeSaveBmp(BufferedImage img, String path, int maxWidth, int maxHeight) {
        return limitSizeSave(img, path, maxWidth, maxHeight, "bmp");
    }

    public static boolean limitSizeSave(BufferedImage img, String path, int maxWidth, int maxHeight, String format) {
        int curWidth = img.getWidth();
        int curHeight = img.getHeight();
        float zoomWidth = (float) curWidth / (float) maxWidth;
        float zoomHeight = (float) curHeight / (float) maxHeight;
        float zoom = 1.0F;
        if (zoomWidth > 1.0F || zoomHeight > 1.0F) {
            zoom = zoomWidth > zoomHeight ? zoomWidth : zoomHeight;
        }

        int newWidth = Math.round((float) curWidth / zoom);
        int newHeight = Math.round((float) curHeight / zoom);
        if (zoom != 1.0F) {
            Image scale = img.getScaledInstance(newWidth, newHeight, 4);
            BufferedImage newImg = getNewRGB(newWidth, newHeight);
            Graphics graphics = newImg.getGraphics();
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, newWidth, newHeight);
            graphics.drawImage(scale, 0, 0, (ImageObserver) null);
            graphics.dispose();
            img = newImg;
        }

        return save(img, path, format);
    }

    public static boolean saveJpg(BufferedImage img, String path) {
        return save(img, path, "jpeg");
    }

    public static boolean savePng(BufferedImage img, String path) {
        return save(img, path, "png");
    }

    public static boolean saveGif(BufferedImage img, String path) {
        return save(img, path, "gif");
    }

    public static boolean saveBmp(BufferedImage img, String path) {
        return save(img, path, "bmp");
    }

    public static boolean save(BufferedImage img, String path, String format) {
        return save(img, new File(path), format, 0.95F);
    }

    public static boolean save(BufferedImage img, String path, String format, float quality) {
        return save(img, new File(path), format, quality);
    }

    public static boolean save(BufferedImage img, File file, String format) {
        return save(img, file, format, 0.95F);
    }

    public static boolean save(BufferedImage img, File file, String format, float quality) {
        try {
            ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByFormatName(format).next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(file);

            try {
                writer.setOutput(ios);
                ImageWriteParam iwparam = getImageWriteParam(format, quality);
                writer.write((IIOMetadata) null, new IIOImage(img, (List) null, (IIOMetadata) null), iwparam);
                ios.flush();
                writer.dispose();
            } finally {
                ios.close();
            }

            return true;
        } catch (IOException var11) {
            Logger.getLogger(ToolImage.class.getName()).log(Level.SEVERE, (String) null, var11);
            return false;
        }
    }

    public static byte[] toDataJpg(BufferedImage img) {
        return toData(img, "jpeg", 0.95F);
    }

    public static byte[] toDataPng(BufferedImage img) {
        return toData(img, "png", 0.95F);
    }

    public static byte[] toDataGif(BufferedImage img) {
        return toData(img, "gif", 0.95F);
    }

    public static byte[] toDataBmp(BufferedImage img) {
        return toData(img, "bmp", 0.95F);
    }

    public static byte[] toData(BufferedImage img, String format, float quality) {
        try {
            ByteArrayOutputStream byteOutput = new ByteArrayOutputStream(1024);
            ImageWriter writer = (ImageWriter) ImageIO.getImageWritersByFormatName(format).next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(byteOutput);

            try {
                writer.setOutput(ios);
                ImageWriteParam iwparam = getImageWriteParam(format, quality);
                writer.write((IIOMetadata) null, new IIOImage(img, (List) null, (IIOMetadata) null), iwparam);
                ios.flush();
                writer.dispose();
            } finally {
                ios.close();
            }

            return byteOutput.toByteArray();
        } catch (IOException var11) {
            Logger.getLogger(ToolImage.class.getName()).log(Level.SEVERE, (String) null, var11);
            return null;
        }
    }

    public static BufferedImage fromData(byte[] data) {
        try {
            ByteArrayInputStream byteInput = new ByteArrayInputStream(data);
            ImageInputStream stream = ImageIO.createImageInputStream(byteInput);

            BufferedImage var6;
            try {
                Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
                if (!iter.hasNext()) {
                    return null;
                }

                ImageReader reader = (ImageReader) iter.next();
                if ("jpeg".equals(reader.getFormatName().toLowerCase()) || "jpg".equals(reader.getFormatName().toLowerCase())) {
                    ImageIcon icon = new ImageIcon(data);
                    Image image = icon.getImage();
                    BufferedImage bimage;
                    if (image instanceof BufferedImage) {
                        bimage = (BufferedImage) image;
                        return bimage;
                    }

                    bimage = getNewRGB(image.getWidth((ImageObserver) null), image.getHeight((ImageObserver) null));
                    Graphics2D bGr = bimage.createGraphics();
                    bGr.drawImage(image, 0, 0, (ImageObserver) null);
                    bGr.dispose();
                    BufferedImage var9 = bimage;
                    return var9;
                }

                ByteArrayInputStream in = new ByteArrayInputStream(data);

                try {
                    var6 = ImageIO.read(in);
                } finally {
                    in.close();
                }
            } finally {
                stream.close();
            }

            return var6;
        } catch (Exception var20) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(ToolImage.class.getName()).log(Level.SEVERE, (String) null, var20);
            }
        }

        return null;
    }

    public static BufferedImage openImage(String path) {
        return openImage(new File(path));
    }

    public static BufferedImage openImage(File file) {
        try {
            ImageInputStream stream = ImageIO.createImageInputStream(file);

            BufferedImage var4;
            try {
                Iterator<ImageReader> iter = ImageIO.getImageReaders(stream);
                if (!iter.hasNext()) {
                    return null;
                }

                ImageReader reader = (ImageReader) iter.next();
                if ("jpeg".equals(reader.getFormatName().toLowerCase()) || "jpg".equals(reader.getFormatName().toLowerCase())) {
                    byte[] data = new byte[(int) file.length()];
                    int read = stream.read(data);
                    ImageIcon icon;
                    if ((long) read != file.length()) {
                        return null;
                    }
                    icon = new ImageIcon(data);
                    Image image = icon.getImage();
                    BufferedImage bimage;
                    if (image instanceof BufferedImage) {
                        bimage = (BufferedImage) image;
                        return bimage;
                    }

                    bimage = getNewRGB(image.getWidth((ImageObserver) null), image.getHeight((ImageObserver) null));
                    Graphics2D bGr = bimage.createGraphics();
                    bGr.drawImage(image, 0, 0, (ImageObserver) null);
                    bGr.dispose();
                    BufferedImage var10 = bimage;
                    return var10;
                }

                var4 = ImageIO.read(file);
            } finally {
                stream.close();
            }

            return var4;
        } catch (Exception var15) {
            if (ConstFramework.getDebug()) {
                Logger.getLogger(ToolImage.class.getName()).log(Level.SEVERE, (String) null, var15);
            }
        }

        return null;
    }

    public static BufferedImage getNewRGB(int width, int height) {
        return new BufferedImage(width, height, 1);
    }

    public static BufferedImage toNewRGB(BufferedImage oldImg) {
        if (oldImg == null) {
            return null;
        } else {
            BufferedImage newImg = getNewRGB(oldImg.getWidth(), oldImg.getHeight());
            Graphics g = newImg.getGraphics();
            g.drawImage(oldImg, 0, 0, (ImageObserver) null);
            g.dispose();
            return newImg;
        }
    }

    public static BufferedImage getNewARGB(int width, int height) {
        return new BufferedImage(width, height, 2);
    }

    public static BufferedImage toNewARGB(BufferedImage oldImg) {
        if (oldImg == null) {
            return null;
        } else {
            BufferedImage newImg = getNewARGB(oldImg.getWidth(), oldImg.getHeight());
            Graphics g = newImg.getGraphics();
            g.drawImage(oldImg, 0, 0, (ImageObserver) null);
            g.dispose();
            return newImg;
        }
    }

    private static ImageWriteParam getImageWriteParam(String format, float quality) {
        Object iwparam;
        if ("png".equals(format)) {
            iwparam = new JPEGImageWriteParam((Locale) null);
            ((ImageWriteParam) iwparam).setCompressionMode(2);
            ((ImageWriteParam) iwparam).setCompressionQuality(quality);
        } else if (!"jpeg".equals(format) && !"jpg".equals(format)) {
            if ("bmp".equals(format)) {
                iwparam = new BMPImageWriteParam((Locale) null);
            } else if ("gif".equals(format)) {
                iwparam = new JPEGImageWriteParam((Locale) null);
                ((ImageWriteParam) iwparam).setCompressionMode(2);
                ((ImageWriteParam) iwparam).setCompressionQuality(quality);
            } else {
                iwparam = new ImageWriteParam((Locale) null);
            }
        } else {
            iwparam = new JPEGImageWriteParam((Locale) null);
            ((ImageWriteParam) iwparam).setCompressionMode(2);
            ((ImageWriteParam) iwparam).setCompressionQuality(quality);
        }

        return (ImageWriteParam) iwparam;
    }
}
