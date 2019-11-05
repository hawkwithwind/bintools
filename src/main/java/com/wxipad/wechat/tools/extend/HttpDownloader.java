//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class HttpDownloader {
    private final HttpClient client = new HttpClient();

    public HttpDownloader() {
    }

    public HttpClient getClient() {
        return this.client;
    }

    public DownloadObj doDownload(Listener listener, String urlStr, String path) {
        return this.doDownload(new DownloadInfo(listener, urlStr, path, HttpClient.METHOD.GET, (HashMap) null));
    }

    public DownloadObj doDownload(Listener listener, String urlStr, String path, HttpClient.METHOD method, HashMap<String, String> paramMap) {
        return this.doDownload(new DownloadInfo(listener, urlStr, path, method, paramMap));
    }

    public DownloadObj doDownload(DownloadInfo info) {
        return new DownloadObj(info);
    }

    public interface Listener {
        void progress(long var1, long var3);

        void done(File var1, boolean var2);

        void exception(Exception var1);
    }

    public static class DownloadInfo {
        public static final int MIN_BUFF_SIZE = 1024;
        public static final int DEFAULT_BUFF_SIZE = 4096;
        public final Listener listener;
        public final String urlStr;
        public final String path;
        public final HttpClient.METHOD method;
        public final HashMap<String, String> paramMap;
        public final int buffSize;
        private String fileDir = null;
        private String fileName = null;
        private File fileObj = null;
        private String downName = null;
        private File downObj = null;

        public DownloadInfo(Listener listener, String urlStr, String path) {
            this.listener = listener;
            this.urlStr = urlStr;
            this.path = path;
            this.method = HttpClient.METHOD.GET;
            this.paramMap = null;
            this.buffSize = 4096;
            this.init();
        }

        public DownloadInfo(Listener listener, String urlStr, String path, HttpClient.METHOD method, HashMap<String, String> paramMap) {
            this.listener = listener;
            this.urlStr = urlStr;
            this.path = path;
            this.method = method;
            this.paramMap = paramMap;
            this.buffSize = 4096;
            this.init();
        }

        public DownloadInfo(Listener listener, String urlStr, String path, HttpClient.METHOD method, HashMap<String, String> paramMap, int buffSize) {
            this.listener = listener;
            this.urlStr = urlStr;
            this.path = path;
            this.method = method;
            this.paramMap = paramMap;
            this.buffSize = Math.max(buffSize, 1024);
            this.init();
        }

        private void init() {
            if (this.urlStr != null && this.path != null) {
                String downloadPath = this.path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
                int index = downloadPath.lastIndexOf(File.separatorChar);
                this.fileName = downloadPath.substring(index + 1);
                if ("".equals(this.fileName)) {
                    int ui = this.urlStr.lastIndexOf(File.separatorChar);
                    this.fileName = this.urlStr.substring(ui >= 0 ? ui + 1 : 0);
                    if (this.fileName.equals("")) {
                        this.fileName = "index.html";
                    }
                }

                this.fileDir = downloadPath.substring(0, index + 1);
                if (this.fileDir == null || this.fileDir.isEmpty()) {
                    this.fileDir = "." + File.separator;
                }

                File dir = new File(this.fileDir);
                if (dir.exists() || dir.mkdirs()) {
                    this.fileObj = new File(this.fileDir + this.fileName);
                    this.downName = this.fileName + ".mndownload";
                    this.downObj = new File(this.fileDir + this.downName);
                }
            }
        }

        public String getFileDir() {
            return this.fileDir;
        }

        public String getFileName() {
            return this.fileName;
        }

        public File getFileObj() {
            return this.fileObj;
        }

        public String getDownName() {
            return this.downName;
        }

        public File getDownObj() {
            return this.downObj;
        }
    }

    public class DownloadObj implements Runnable {
        public static final long DEFAULT_RANGE_SIZE = 65536L;
        public final DownloadInfo info;
        public long rangeSize = 65536L;
        private boolean stopped = true;
        private long contentLength;
        private long lastModified;
        private boolean gzip;
        private boolean deflate;

        public DownloadObj(DownloadInfo info) {
            this.info = info;
        }

        public void start() {
            (new Thread(this)).start();
        }

        public void stop() {
            this.stopped = true;
        }

        public void run() {
            this.stopped = false;

            try {
                boolean useRangeDownload = false;
                HttpClient.HttpClientRequest request = this.getNewRequest().initRequest();

                try {
                    this.contentLength = (long) request.conn.getContentLength();
                    this.lastModified = request.conn.getLastModified();
                    String acceptRanges = request.conn.getHeaderField("Accept-Ranges");
                    useRangeDownload = acceptRanges != null && acceptRanges.toLowerCase().equals("bytes");
                    String contentEncoding = request.conn.getHeaderField("Content-Encoding");
                    this.gzip = contentEncoding != null && contentEncoding.toLowerCase().equals("gzip");
                    this.deflate = contentEncoding != null && contentEncoding.toLowerCase().equals("deflate");
                    if (this.contentLength < this.rangeSize || this.contentLength < 0L) {
                        useRangeDownload = false;
                    }

                    if (!useRangeDownload) {
                        this.allDownload(request);
                    }
                } finally {
                    request.conn.disconnect();
                }

                if (useRangeDownload) {
                    this.rangeDownload();
                }
            } catch (Exception var9) {
                if (this.info.listener != null) {
                    this.info.listener.exception(var9);
                }
            }

            this.stopped = true;
        }

        private HttpClient.HttpClientRequest getNewRequest() {
            return HttpDownloader.this.client.getNewRequest(this.info.urlStr, this.info.method, this.info.paramMap);
        }

        private void allDownload(HttpClient.HttpClientRequest request) throws Exception {
            boolean success = false;
            InputStream is = request.conn.getInputStream();
            if (this.gzip) {
                is = new GZIPInputStream((InputStream) is);
            }

            if (this.deflate) {
                is = new InflaterInputStream((InputStream) is, new Inflater(true));
            }

            try {
                if (this.info.getDownObj().exists()) {
                    this.info.getDownObj().delete();
                }

                FileOutputStream os = new FileOutputStream(this.info.getDownObj());

                try {
                    byte[] buff = new byte[this.info.buffSize];
                    long downloadSize = 0L;

                    while (true) {
                        int readCntx;
                        if (!this.stopped && (readCntx = ((InputStream) is).read(buff)) != -1) {
                            os.write(buff, 0, readCntx);
                            downloadSize += (long) readCntx;
                            if (this.info.listener != null) {
                                this.info.listener.progress(downloadSize, this.contentLength);
                            }

                            if (!this.stopped) {
                                continue;
                            }
                        }

                        success = downloadSize == this.contentLength;
                        break;
                    }
                } finally {
                    os.close();
                }
            } finally {
                ((InputStream) is).close();
            }

            if (success) {
                if (this.info.getFileObj().exists()) {
                    this.info.getFileObj().delete();
                }

                this.info.getDownObj().renameTo(this.info.getFileObj());
            }

            if (this.info.listener != null) {
                this.info.listener.done(this.info.getFileObj(), success);
            }

        }

        private void rangeDownload() throws Exception {
            boolean success = false;
            if (!this.info.getDownObj().exists()) {
                this.info.getDownObj().createNewFile();
            } else if (this.info.getDownObj().lastModified() < this.lastModified) {
                this.info.getDownObj().delete();
                this.info.getDownObj().createNewFile();
            }

            RandomAccessFile downObj = new RandomAccessFile(this.info.getDownObj(), "rwd");

            try {
                long downloadSize = downObj.length();
                if (downloadSize > this.contentLength) {
                    downloadSize = 0L;
                    downObj.setLength(0L);
                }

                downObj.seek(downloadSize);
                if (!this.stopped) {
                    HashMap<String, String> propertys = new HashMap();
                    propertys.put("Range", "bytes=" + downloadSize + "-");
                    HttpClient.HttpClientRequest request = this.getNewRequest().initRequest(propertys);

                    try {
                        InputStream is = request.conn.getInputStream();
                        if (this.gzip) {
                            is = new GZIPInputStream((InputStream) is);
                        }

                        if (this.deflate) {
                            is = new InflaterInputStream((InputStream) is, new Inflater(true));
                        }

                        try {
                            byte[] buff = new byte[this.info.buffSize];
                            boolean var9 = false;

                            int readCnt;
                            while (!this.stopped && downloadSize < this.contentLength && (readCnt = ((InputStream) is).read(buff)) != -1) {
                                downObj.write(buff, 0, readCnt);
                                downloadSize += (long) readCnt;
                                if (this.info.listener != null) {
                                    this.info.listener.progress(downloadSize, this.contentLength);
                                }
                            }

                            success = downloadSize == this.contentLength;
                        } finally {
                            ((InputStream) is).close();
                        }
                    } finally {
                        request.conn.disconnect();
                    }
                }
            } finally {
                downObj.close();
            }

            if (success) {
                if (this.info.getFileObj().exists()) {
                    this.info.getFileObj().delete();
                }

                this.info.getDownObj().renameTo(this.info.getFileObj());
            }

            if (this.info.listener != null) {
                this.info.listener.done(this.info.getFileObj(), success);
            }

        }
    }
}
