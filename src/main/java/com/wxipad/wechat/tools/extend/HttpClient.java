//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.extend;

import com.wxipad.wechat.tools.constant.ConstFramework;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class HttpClient {
    protected static final METHOD DEFAULT_METHOD;
    protected static final int DEFAULT_TIMEOUT = 30000;
    protected static final int BUFF_SIZE = 4096;
    protected static final String COOKIE = "Cookie";
    protected static final String SET_COOKIE = "Set-Cookie";
    protected static final String SECURE = "secure";
    protected static final String VERSION = "version";
    protected static final String COMMENT = "comment";
    protected static final String DOMAIN = "domain";
    protected static final String PATH = "path";
    protected static final String MAX_AGE = "max-age";
    protected static final String EXPIRES = "expires";
    protected static final String[] DATE_FORMATS;
    protected static int HttpClientRequestCounter;

    static {
        DEFAULT_METHOD = METHOD.GET;
        HttpClientRequestCounter = 0;
        DATE_FORMATS = new String[]{"EEE, dd MMM yyyy HH:mm:ss zzz", "EEEE, dd-MMM-yy HH:mm:ss zzz", "EEE MMM d HH:mm:ss yyyy", "EEE, dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MMM-yyyy HH-mm-ss z", "EEE, dd MMM yy HH:mm:ss z", "EEE dd-MMM-yyyy HH:mm:ss z", "EEE dd MMM yyyy HH:mm:ss z", "EEE dd-MMM-yyyy HH-mm-ss z", "EEE dd-MMM-yy HH:mm:ss z", "EEE dd MMM yy HH:mm:ss z", "EEE,dd-MMM-yy HH:mm:ss z", "EEE,dd-MMM-yyyy HH:mm:ss z", "EEE, dd-MM-yyyy HH:mm:ss z"};
    }

    public final ArrayList<Cookie> cookies = new ArrayList();
    public String accept;
    public String acceptCharset;
    public String acceptEncoding;
    public String acceptLanguage;
    public Charset charset;
    public String host;
    public String referer;
    public String userAgent;
    public SSLSocketFactory ssf;
    public METHOD defaultMethod;
    public int timeout;

    public HttpClient() {
        this.defaultMethod = DEFAULT_METHOD;
        this.timeout = 30000;
    }

    protected static String buildParamData(HashMap<String, String> paramMap, Charset charset) throws UnsupportedEncodingException {
        StringBuilder dataBuilder = new StringBuilder();
        if (paramMap != null) {
            int paramCnt = 0;

            for (Iterator var4 = paramMap.keySet().iterator(); var4.hasNext(); ++paramCnt) {
                String key = (String) var4.next();
                if (paramCnt > 0) {
                    dataBuilder.append("&");
                }

                String value = (String) paramMap.get(key);
                dataBuilder.append(URLEncode(key, charset)).append("=").append(URLEncode(value, charset));
            }
        }

        return dataBuilder.toString();
    }

    public static String URLEncode(String str, Charset charset) throws UnsupportedEncodingException {
        return str == null ? "" : URLEncoder.encode(str, charset.name());
    }

    public static String URLDecode(String str, Charset charset) throws UnsupportedEncodingException {
        return str == null ? "" : URLDecoder.decode(str, charset.name());
    }

    public static String getRefUrlStr(String httpUrl, String url) {
        httpUrl = httpUrl.trim().toLowerCase();
        url = url.trim().toLowerCase();
        if (url.startsWith("http://")) {
            return url;
        } else if (httpUrl.startsWith("http://")) {
            int endIndex;
            String prefix;
            if (url.startsWith("/")) {
                endIndex = httpUrl.indexOf(47, "http://".length());
                prefix = httpUrl.substring(0, endIndex);
                return prefix + url;
            } else {
                endIndex = httpUrl.lastIndexOf(47);
                prefix = httpUrl.substring(0, endIndex + 1);
                return prefix + url;
            }
        } else {
            return url;
        }
    }

    public void asBroswer(String userAgent) {
        this.accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
        this.acceptEncoding = "gzip, deflate, sdch";
        this.acceptLanguage = "zh-CN,zh;q=0.8,en-US;q=0.6,en;q=0.4";
        this.userAgent = userAgent;
    }

    public void gzipAllowed(boolean allowed) {
        if (allowed) {
            this.acceptEncoding = "gzip, deflate";
        } else {
            this.acceptEncoding = null;
        }

    }

    public HttpClientRequest getNewRequest(String urlStr, HashMap<String, Object> paramMap) {
        return new HttpClientRequest(urlStr, paramMap);
    }

    public HttpClientRequest getNewRequest(String urlStr, HashMap<String, Object> paramMap, HttpClientListener listener) {
        return new HttpClientRequest(urlStr, paramMap, listener);
    }

    public HttpClientRequest getNewRequest(String urlStr, METHOD method, HashMap<String, String> paramMap) {
        return new HttpClientRequest(urlStr, method, paramMap);
    }

    public HttpClientRequest getNewRequest(String urlStr, METHOD method, HashMap<String, String> paramMap, HttpClientListener listener) {
        return new HttpClientRequest(urlStr, method, paramMap, listener);
    }

    public HttpClientRequest getNewRequest(String urlStr, byte[] dataBytes) {
        return new HttpClientRequest(urlStr, dataBytes);
    }

    public HttpClientRequest getNewRequest(String urlStr, byte[] dataBytes, HttpClientListener listener) {
        return new HttpClientRequest(urlStr, dataBytes, listener);
    }

    public HttpClientRequest getNewRequest(String urlStr, String dataStr) {
        Charset useCharset = this.charset == null ? ConstFramework.getCharset() : this.charset;
        dataStr = dataStr == null ? "" : dataStr;
        return new HttpClientRequest(urlStr, dataStr.getBytes(useCharset));
    }

    public HttpClientRequest getNewRequest(String urlStr, String dataStr, HttpClientListener listener) {
        Charset useCharset = this.charset == null ? ConstFramework.getCharset() : this.charset;
        dataStr = dataStr == null ? "" : dataStr;
        return new HttpClientRequest(urlStr, dataStr.getBytes(useCharset), listener);
    }

    public HttpClientResponse get(String urlStr) {
        return this.send(urlStr, METHOD.GET, (HashMap) null);
    }

    public HttpClientResponse get(String urlStr, HashMap<String, String> paramMap) {
        return this.send(urlStr, METHOD.GET, paramMap);
    }

    public HttpClientResponseData getData(String urlStr) {
        return this.sendData(urlStr, METHOD.GET, (HashMap) null);
    }

    public HttpClientResponseData getData(String urlStr, HashMap<String, String> paramMap) {
        return this.sendData(urlStr, METHOD.GET, paramMap);
    }

    public HttpClientResponse post(String urlStr) {
        return this.send(urlStr, METHOD.POST, (HashMap) null);
    }

    public HttpClientResponse post(String urlStr, HashMap<String, String> paramMap) {
        return this.send(urlStr, METHOD.POST, paramMap);
    }

    public HttpClientResponseData postData(String urlStr) {
        return this.sendData(urlStr, METHOD.POST, (HashMap) null);
    }

    public HttpClientResponseData postData(String urlStr, HashMap<String, String> paramMap) {
        return this.sendData(urlStr, METHOD.POST, paramMap);
    }

    public HttpClientResponse write(String urlStr, String dataStr) {
        Charset useCharset = this.charset == null ? ConstFramework.getCharset() : this.charset;
        dataStr = dataStr == null ? "" : dataStr;
        HttpClientRequest request = new HttpClientRequest(urlStr, dataStr.getBytes(useCharset));

        try {
            return request.initRequest().getResponse();
        } catch (Exception var6) {
            return new HttpClientResponse(var6);
        }
    }

    public HttpClientResponseData writeData(String urlStr, String dataStr) {
        Charset useCharset = this.charset == null ? ConstFramework.getCharset() : this.charset;
        dataStr = dataStr == null ? "" : dataStr;
        HttpClientRequest request = new HttpClientRequest(urlStr, dataStr.getBytes(useCharset));

        try {
            return request.initRequest().getDataResponse();
        } catch (Exception var6) {
            return new HttpClientResponseData(var6);
        }
    }

    public HttpClientResponse write(String urlStr, byte[] dataBytes) {
        HttpClientRequest request = new HttpClientRequest(urlStr, dataBytes);

        try {
            return request.initRequest().getResponse();
        } catch (Exception var5) {
            return new HttpClientResponse(var5);
        }
    }

    public HttpClientResponseData writeData(String urlStr, byte[] dataBytes) {
        HttpClientRequest request = new HttpClientRequest(urlStr, dataBytes);

        try {
            return request.initRequest().getDataResponse();
        } catch (Exception var5) {
            return new HttpClientResponseData(var5);
        }
    }

    public HttpClientResponse send(String urlStr, METHOD method, HashMap<String, String> paramMap) {
        HttpClientRequest request = new HttpClientRequest(urlStr, method, paramMap);

        try {
            return request.initRequest().getResponse();
        } catch (Exception var6) {
            return new HttpClientResponse(var6);
        }
    }

    public HttpClientResponseData sendData(String urlStr, METHOD method, HashMap<String, String> paramMap) {
        HttpClientRequest request = new HttpClientRequest(urlStr, method, paramMap);

        try {
            return request.initRequest().getDataResponse();
        } catch (Exception var6) {
            return new HttpClientResponseData(var6);
        }
    }

    public HttpClientResponse multipartPost(String urlStr, HashMap<String, Object> paramMap) {
        HttpClientRequest request = new HttpClientRequest(urlStr, paramMap);

        try {
            return request.initRequest().getResponse();
        } catch (Exception var5) {
            return new HttpClientResponse(var5);
        }
    }

    public HttpClientResponseData multipartPostData(String urlStr, HashMap<String, Object> paramMap) {
        HttpClientRequest request = new HttpClientRequest(urlStr, paramMap);

        try {
            return request.initRequest().getDataResponse();
        } catch (Exception var5) {
            return new HttpClientResponseData(var5);
        }
    }

    public File download(String urlStr, String path) {
        return this.download(urlStr, path, METHOD.GET, (HashMap) null);
    }

    public File download(String urlStr, String path, METHOD method, HashMap<String, String> paramMap) {
        if (urlStr != null && path != null) {
            path = path.replace('\\', File.separatorChar).replace('/', File.separatorChar);
            int index = path.lastIndexOf(File.separatorChar);
            String fileName = path.substring(index + 1);
            if ("".equals(fileName)) {
                int ui = urlStr.lastIndexOf(File.separatorChar);
                fileName = urlStr.substring(ui >= 0 ? ui + 1 : 0);
                if (fileName.equals("")) {
                    fileName = "index.html";
                }
            }

            String dirPath = path.substring(0, index + 1);
            if (dirPath == null || dirPath.isEmpty()) {
                dirPath = "." + File.separator;
            }

            File dir = new File(dirPath);
            if (!dir.exists() && !dir.mkdirs()) {
                return null;
            } else {
                File file = new File(dirPath + fileName);
                HttpClientRequest request = new HttpClientRequest(urlStr, method, paramMap);

                try {
                    InputStream is = request.initRequest().conn.getInputStream();
                    String contentEncoding = request.conn.getHeaderField("Content-Encoding");
                    boolean gzip = contentEncoding != null && contentEncoding.toLowerCase().equals("gzip");
                    boolean deflate = contentEncoding != null && contentEncoding.toLowerCase().equals("deflate");
                    if (gzip) {
                        is = new GZIPInputStream((InputStream) is);
                    }

                    if (deflate) {
                        is = new InflaterInputStream((InputStream) is, new Inflater(true));
                    }

                    try {
                        FileOutputStream os = new FileOutputStream(file);

                        try {
                            byte[] buff = new byte[4096];

                            int readCnt;
                            while ((readCnt = ((InputStream) is).read(buff)) != -1) {
                                os.write(buff, 0, readCnt);
                            }
                        } finally {
                            os.close();
                        }
                    } finally {
                        ((InputStream) is).close();
                    }

                    request.initRequest().conn.disconnect();
                    return file;
                } catch (Exception var28) {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public HttpClientRequest doGet(HttpClientListener listener, String urlStr) {
        return this.doSend(listener, urlStr, METHOD.GET, (HashMap) null);
    }

    public HttpClientRequest doGet(HttpClientListener listener, String urlStr, HashMap<String, String> paramMap) {
        return this.doSend(listener, urlStr, METHOD.GET, paramMap);
    }

    public HttpClientRequest doPost(HttpClientListener listener, String urlStr) {
        return this.doSend(listener, urlStr, METHOD.POST, (HashMap) null);
    }

    public HttpClientRequest doPost(HttpClientListener listener, String urlStr, HashMap<String, String> paramMap) {
        return this.doSend(listener, urlStr, METHOD.POST, paramMap);
    }

    public HttpClientRequest doWrite(HttpClientListener listener, String urlStr, String dataStr) {
        Charset useCharset = this.charset == null ? ConstFramework.getCharset() : this.charset;
        dataStr = dataStr == null ? "" : dataStr;
        return (new HttpClientRequest(urlStr, dataStr.getBytes(useCharset), listener)).send();
    }

    public HttpClientRequest doWrite(HttpClientListener listener, String urlStr, byte[] dataBytes) {
        return (new HttpClientRequest(urlStr, dataBytes, listener)).send();
    }

    public HttpClientRequest doSend(HttpClientListener listener, String urlStr, METHOD method, HashMap<String, String> paramMap) {
        return (new HttpClientRequest(urlStr, method, paramMap, listener)).send();
    }

    public HttpClientRequest doMultipartPost(HttpClientListener listener, String urlStr, HashMap<String, Object> paramMap) {
        return (new HttpClientRequest(urlStr, paramMap, listener)).send();
    }

    protected void solveSetCookie(Map<String, List<String>> headerFields) {
        if (headerFields != null && this.cookies != null) {
            synchronized (this.cookies) {
                List<String> values = (List) headerFields.get("Set-Cookie");
                if (values != null) {
                    Iterator var4 = values.iterator();

                    while (true) {
                        Cookie cookie;
                        do {
                            if (!var4.hasNext()) {
                                return;
                            }

                            String v = (String) var4.next();
                            String[] fields = v.split(";");
                            String[] cookieStrs = fields[0].split("=");
                            String name = cookieStrs[0];
                            if (cookieStrs.length > 1) {
                                String val = cookieStrs[1];
                                if (val != null && val.startsWith("\"") && val.endsWith("\"")) {
                                    val = val.substring(1, val.length() - 1);
                                }

                                cookie = new Cookie(name, val);
                            } else {
                                cookie = new Cookie(name, (String) null);
                            }

                            int i;
                            for (i = 1; i < fields.length; ++i) {
                                String[] header = fields[i].split("=");
                                String headerName = header[0].trim().toLowerCase();
                                String headerValue = header.length > 1 ? header[1].trim() : "";
                                if (headerValue != null && headerValue.startsWith("\"") && headerValue.endsWith("\"")) {
                                    headerValue = headerValue.substring(1, headerValue.length() - 1);
                                }

                                if ("secure".equals(headerName)) {
                                    cookie.secure = true;
                                } else if ("version".equals(headerName)) {
                                    try {
                                        cookie.version = Integer.parseInt(headerValue);
                                    } catch (Exception var22) {
                                        cookie.version = 0;
                                    }
                                } else if ("comment".equals(headerName)) {
                                    cookie.comment = headerValue;
                                } else if ("domain".equals(headerName)) {
                                    cookie.domain = headerValue;
                                } else if ("path".equals(headerName)) {
                                    cookie.path = headerValue;
                                } else if ("max-age".equals(headerName)) {
                                    int maxAge;
                                    try {
                                        maxAge = Integer.parseInt(headerValue);
                                    } catch (Exception var21) {
                                        maxAge = -1;
                                    }

                                    cookie.timeStamp = System.currentTimeMillis() + (long) (maxAge * 1000);
                                } else if ("expires".equals(headerName)) {
                                    String[] var14 = DATE_FORMATS;
                                    int var15 = var14.length;
                                    int var16 = 0;

                                    while (var16 < var15) {
                                        String dateFormat = var14[var16];

                                        try {
                                            SimpleDateFormat format = new SimpleDateFormat(dateFormat, Locale.US);
                                            Date date = format.parse(headerValue);
                                            cookie.timeStamp = date.getTime();
                                            break;
                                        } catch (Exception var23) {
                                            ++var16;
                                        }
                                    }
                                }
                            }

                            for (i = 0; i < this.cookies.size(); ++i) {
                                if (((Cookie) this.cookies.get(i)).name.equals(cookie.name)) {
                                    this.cookies.remove(i);
                                    break;
                                }
                            }
                        } while (cookie.timeStamp != 0L && cookie.timeStamp < System.currentTimeMillis());

                        this.cookies.add(cookie);
                    }
                }
            }
        }
    }

    protected void solveCookie(HttpURLConnection url) {
        if (url != null && this.cookies != null) {
            synchronized (this.cookies) {
                String domain = url.getURL().getHost();
                String path = url.getURL().getPath();
                StringBuilder sb = null;
                Iterator iterator = this.cookies.iterator();

                while (true) {
                    while (iterator.hasNext()) {
                        Cookie c = (Cookie) iterator.next();
                        if (c.timeStamp != 0L && c.timeStamp < System.currentTimeMillis()) {
                            iterator.remove();
                        } else if (c.matchDomain(domain) && c.matchPath(path)) {
                            if (sb == null) {
                                sb = new StringBuilder(c.name + "=" + c.value);
                            } else {
                                sb.append(";").append(c.name).append("=").append(c.value);
                            }
                        }
                    }

                    if (sb != null) {
                        url.setRequestProperty("Cookie", sb.toString());
                    }

                    return;
                }
            }
        }
    }

    public static enum METHOD {
        GET,
        POST,
        HEAD,
        OPTIONS,
        PUT,
        DELETE,
        TRACE;

        private METHOD() {
        }
    }

    public interface HttpClientListener {
        void onData(HttpClientRequest var1, HttpClientResponse var2, String var3);

        void onError(HttpClientRequest var1, HttpClientResponse var2, int var3);

        void onException(HttpClientRequest var1, HttpClientResponse var2, Exception var3);
    }

    public static class Cookie {
        public static final int DEFUALT_VERSION = 0;
        public static final int DEFUALT_MAX_AGE = -1;
        protected static final long TS_EXPIRE = 0L;
        public String name;
        public String value;
        public boolean secure = false;
        public int version = 0;
        public String comment = null;
        public String domain = null;
        public String path = null;
        public long timeStamp = 0L;

        public Cookie(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public boolean matchDomain(String domain) {
            if (this.domain == null) {
                return true;
            } else {
                return domain.indexOf(this.domain) >= 0;
            }
        }

        public boolean matchPath(String path) {
            return this.path == null ? true : path.startsWith(this.path);
        }
    }

    public static class UserAgent {
        public static final String MAC_SAFARI = "Mozilla/5.0 (Macintosh; U; Intel Mac OS X 10_6_8; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50";
        public static final String MAC_CHROME = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_9_5) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/41.0.2272.101 Safari/537.36";
        public static final String MAC_FIREFOX = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.6; rv:2.0.1) Gecko/20100101 Firefox/4.0.1";
        public static final String MAC_OPERA = "Opera/9.80 (Macintosh; Intel Mac OS X 10.6.8; U; en) Presto/2.8.131 Version/11.11";
        public static final String WIN_IE6 = "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)";
        public static final String WIN_IE7 = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 6.0)";
        public static final String WIN_IE8 = "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.0; Trident/4.0)";
        public static final String WIN_IE9 = "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0;";
        public static final String WIN_SAFARI = "Mozilla/5.0 (Windows; U; Windows NT 6.1; en-us) AppleWebKit/534.50 (KHTML, like Gecko) Version/5.1 Safari/534.50";
        public static final String WIN_CHROME = "Mozilla/5.0 (Windows NT 5.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.66 Safari/537.36";
        public static final String WIN_FIREFOX = "Mozilla/5.0 (Windows NT 6.1; rv:2.0.1) Gecko/20100101 Firefox/4.0.1";
        public static final String WIN_OPERA = "Opera/9.80 (Windows NT 6.1; U; en) Presto/2.8.131 Version/11.11";
        public static final String APPLE_IPAD = "Mozilla/5.0 (iPad; CPU OS 7_0 like Mac OS X) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53";
        public static final String APPLE_IPHONE4 = "Mozilla/5.0 (iPhone; U; CPU iPhone OS 4_2_1 like Mac OS X; en-us) AppleWebKit/533.17.9 (KHTML, like Gecko) Version/5.0.2 Mobile/8C148 Safari/6533.18.5";
        public static final String APPLE_IPHONE5 = "Mozilla/5.0 (iPhone; CPU iPhone OS 7_0 like Mac OS X; en-us) AppleWebKit/537.51.1 (KHTML, like Gecko) Version/7.0 Mobile/11A465 Safari/9537.53";
        public static final String APPLE_IPHONE6 = "Mozilla/5.0 (iPhone; CPU iPhone OS 8_0 like Mac OS X) AppleWebKit/600.1.3 (KHTML, like Gecko) Version/8.0 Mobile/12A4345d Safari/600.1.4";
        public static final String GOOGLE_NEXUS4 = "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 4 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
        public static final String GOOGLE_NEXUS5 = "Mozilla/5.0 (Linux; Android 4.2.1; en-us; Nexus 5 Build/JOP40D) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";
        public static final String GOOGLE_NEXUS7 = "Mozilla/5.0 (Linux; Android 4.3; Nexus 7 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.72 Safari/537.36";
        public static final String GOOGLE_NEXUS10 = "Mozilla/5.0 (Linux; Android 4.3; Nexus 10 Build/JSS15Q) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/29.0.1547.72 Safari/537.36";
        public static final String SAMSUNG_NOTE = "Mozilla/5.0 (Linux; U; Android 2.3; en-us; SAMSUNG-SGH-I717 Build/GINGERBREAD) AppleWebKit/533.1 (KHTML, like Gecko) Version/4.0 Mobile Safari/533.1";
        public static final String SAMSUNG_NOTE2 = "Mozilla/5.0 (Linux; U; Android 4.1; en-us; GT-N7100 Build/JRO03C) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";
        public static final String SAMSUNG_NOTE3 = "Mozilla/5.0 (Linux; U; Android 4.3; en-us; SM-N900T Build/JSS15J) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30";

        public UserAgent() {
        }
    }

    public class HttpClientResponseData extends HttpClientResponseBase {
        public byte[] data;

        public HttpClientResponseData(int code, Map<String, List<String>> headerFields, byte[] data) {
            super(code, headerFields);
            this.data = data;
        }

        public HttpClientResponseData(Exception ex) {
            super(ex);
            this.data = null;
        }
    }

    public class HttpClientResponse extends HttpClientResponseBase {
        public String text;

        public HttpClientResponse(int code, Map<String, List<String>> headerFields, String text) {
            super(code, headerFields);
            this.text = text;
        }

        public HttpClientResponse(Exception ex) {
            super(ex);
            this.text = null;
        }
    }

    public class HttpClientResponseBase {
        public static final int CODE_EXPCETION = -1;
        public static final int CODE_OK = 200;
        public int code;
        public Map<String, List<String>> headerFields;
        public Exception ex;

        public HttpClientResponseBase(int code, Map<String, List<String>> headerFields) {
            this.code = code;
            this.headerFields = headerFields;
            this.ex = null;
        }

        public HttpClientResponseBase(Exception ex) {
            this.code = -1;
            this.ex = ex;
        }

        public String getStatus() {
            switch (this.code) {
                case -1:
                    return "Exception";
                case 100:
                    return "Continue";
                case 101:
                    return "Switching Protocols";
                case 200:
                    return "OK";
                case 201:
                    return "Created";
                case 202:
                    return "Accepted";
                case 203:
                    return "Non-Authoritative Information";
                case 204:
                    return "No Content";
                case 205:
                    return "Reset Content";
                case 206:
                    return "Partial Content";
                case 300:
                    return "Multiple Choices";
                case 301:
                    return "Moved Permanently";
                case 302:
                    return "Found";
                case 303:
                    return "See Other";
                case 304:
                    return "Not Modified";
                case 305:
                    return "Use Proxy";
                case 307:
                    return "Temporary Redirect";
                case 400:
                    return "Bad Request";
                case 401:
                    return "Unauthorized";
                case 402:
                    return "Payment Required";
                case 403:
                    return "Forbidden";
                case 404:
                    return "Not Found";
                case 405:
                    return "Method Not Allowed";
                case 406:
                    return "Not Acceptable";
                case 407:
                    return "Proxy Authentication Required";
                case 408:
                    return "Request Time-out";
                case 409:
                    return "Conflict";
                case 410:
                    return "Gone";
                case 411:
                    return "Length Required";
                case 412:
                    return "Precondition Failed";
                case 413:
                    return "Request Entity Too Large";
                case 414:
                    return "Request-URI Too Large";
                case 415:
                    return "Unsupported Media Type";
                case 416:
                    return "Requested range not satisfiable";
                case 417:
                    return "Expectation Failed";
                case 500:
                    return "Internal Server Error";
                case 501:
                    return "Not Implemented";
                case 502:
                    return "Bad Gateway";
                case 503:
                    return "Service Unavailable";
                case 504:
                    return "Gateway Time-out";
                case 505:
                    return "HTTP Version not supported";
                default:
                    return "Unknow";
            }
        }
    }

    public class HttpClientRequest extends Thread {
        public long id;
        public METHOD method;
        public String urlStr;
        public HashMap paramMap;
        public byte[] dataBytes;
        public URL url;
        public HttpURLConnection conn;
        protected HttpClientListener listener;
        protected boolean multipart;

        public HttpClientRequest(String urlStr, METHOD method, HashMap<String, String> paramMap) {
            this.id = (long) (HttpClient.HttpClientRequestCounter++);
            this.urlStr = urlStr;
            this.method = method;
            this.paramMap = paramMap;
            this.listener = null;
            this.multipart = false;
        }

        public HttpClientRequest(String urlStr, METHOD method, HashMap<String, String> paramMap, HttpClientListener listener) {
            this.id = (long) (HttpClient.HttpClientRequestCounter++);
            this.urlStr = urlStr;
            this.method = method;
            this.paramMap = paramMap;
            this.listener = listener;
            this.multipart = false;
        }

        public HttpClientRequest(String urlStr, HashMap<String, Object> paramMap) {
            this.id = (long) (HttpClient.HttpClientRequestCounter++);
            this.urlStr = urlStr;
            this.method = METHOD.POST;
            this.paramMap = paramMap;
            this.listener = null;
            this.multipart = true;
        }

        public HttpClientRequest(String urlStr, HashMap<String, Object> paramMap, HttpClientListener listener) {
            this.id = (long) (HttpClient.HttpClientRequestCounter++);
            this.urlStr = urlStr;
            this.method = METHOD.POST;
            this.paramMap = paramMap;
            this.listener = listener;
            this.multipart = true;
        }

        public HttpClientRequest(String urlStr, byte[] dataBytes) {
            this.id = (long) (HttpClient.HttpClientRequestCounter++);
            this.urlStr = urlStr;
            this.method = METHOD.POST;
            this.paramMap = null;
            this.dataBytes = dataBytes;
            this.listener = null;
            this.multipart = false;
        }

        public HttpClientRequest(String urlStr, byte[] dataBytes, HttpClientListener listener) {
            this.id = (long) (HttpClient.HttpClientRequestCounter++);
            this.urlStr = urlStr;
            this.method = METHOD.POST;
            this.paramMap = null;
            this.dataBytes = dataBytes;
            this.listener = listener;
            this.multipart = false;
        }

        public void run() {
            HttpClientResponse response;
            try {
                this.initRequest();
                response = this.getResponse();
            } catch (Exception var3) {
                response = HttpClient.this.new HttpClientResponse(var3);
            }

            if (this.listener != null) {
                if (response.ex == null) {
                    if (response.code == 200) {
                        this.listener.onData(this, response, response.text);
                    } else {
                        this.listener.onError(this, response, response.code);
                    }
                } else {
                    this.listener.onException(this, response, response.ex);
                }
            }

        }

        public HttpClientRequest send() {
            this.start();
            return this;
        }

        public HttpClientRequest initRequest() throws Exception {
            return this.initRequest((HashMap) null);
        }

        public HttpClientRequest initRequest(HashMap<String, String> propertys) throws Exception {
            Charset useCharset = HttpClient.this.charset == null ? ConstFramework.getCharset() : HttpClient.this.charset;
            this.method = this.method == null ? HttpClient.this.defaultMethod : this.method;
            this.method = this.method == null ? HttpClient.DEFAULT_METHOD : this.method;
            if (this.method == METHOD.GET) {
                String data = HttpClient.buildParamData(this.paramMap, useCharset);
                if ("".equals(data)) {
                    this.url = new URL(this.urlStr);
                } else if (this.urlStr.contains("?")) {
                    this.url = new URL(this.urlStr + "&" + data);
                } else {
                    this.url = new URL(this.urlStr + "?" + data);
                }
            } else {
                this.url = new URL(this.urlStr);
            }

            this.conn = (HttpURLConnection) this.url.openConnection();
            if (this.url.getProtocol().equalsIgnoreCase("https")) {
                HttpsURLConnection conn2 = (HttpsURLConnection) this.conn;
                conn2.setHostnameVerifier(new HostnameVerifier() {
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                });
                if (HttpClient.this.ssf != null) {
                    conn2.setSSLSocketFactory(HttpClient.this.ssf);
                }
            }

            if (HttpClient.this.timeout > 0) {
                this.conn.setConnectTimeout(HttpClient.this.timeout);
                this.conn.setReadTimeout(HttpClient.this.timeout);
            }

            this.conn.setDoOutput(METHOD.POST == this.method);
            this.conn.setUseCaches(false);
            this.conn.setRequestMethod(this.method.name());
            this.conn.setRequestProperty("Connection", "Keep-Alive");
            if (HttpClient.this.accept != null) {
                this.conn.setRequestProperty("Accept", HttpClient.this.accept);
            }

            if (HttpClient.this.acceptCharset != null) {
                this.conn.setRequestProperty("Accept-Charset", HttpClient.this.acceptCharset);
            }

            if (HttpClient.this.acceptEncoding != null) {
                this.conn.setRequestProperty("Accept-Encoding", HttpClient.this.acceptEncoding);
            }

            if (HttpClient.this.acceptLanguage != null) {
                this.conn.setRequestProperty("Accept-Language", HttpClient.this.acceptLanguage);
            }

            if (HttpClient.this.charset != null) {
                this.conn.setRequestProperty("Charset", HttpClient.this.charset.name());
            }

            if (HttpClient.this.host != null) {
                this.conn.setRequestProperty("Host", HttpClient.this.host);
            }

            if (HttpClient.this.referer != null) {
                this.conn.setRequestProperty("Referer", HttpClient.this.referer);
            }

            if (HttpClient.this.userAgent != null) {
                this.conn.setRequestProperty("User-Agent", HttpClient.this.userAgent);
            }

            String datax;
            if (propertys != null) {
                Iterator var21 = propertys.keySet().iterator();

                while (var21.hasNext()) {
                    datax = (String) var21.next();
                    this.conn.setRequestProperty(datax, (String) propertys.get(datax));
                }
            }

            HttpClient.this.solveCookie(this.conn);
            if (METHOD.POST == this.method) {
                if (this.multipart) {
                    StringBuilder sbBoundary = new StringBuilder("----NutHttpClientFormBoundary");
                    sbBoundary.append(Long.toHexString(System.currentTimeMillis()));
                    this.conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + sbBoundary.toString());
                    byte[] startBoundary = sbBoundary.insert(0, "--").toString().getBytes();
                    byte[] endBoundary = sbBoundary.append("--").toString().getBytes();
                    byte[] fileContentType = "Content-Type: application/octet-stream".getBytes();
                    byte[] lineBreak = "\r\n".getBytes();
                    int disposotionLen1 = "Content-Disposition: form-data; name=\"\"".getBytes().length;
                    int disposotionLen2 = "Content-Disposition: form-data; name=\"\"; filename=\"\"".getBytes().length;
                    int contentLength = 0;
                    Iterator var11 = this.paramMap.keySet().iterator();

                    Object namex;
                    while (var11.hasNext()) {
                        Object name = var11.next();
                        namex = this.paramMap.get(name);
                        if (namex instanceof String) {
                            String strx = (String) namex;
                            contentLength += startBoundary.length + lineBreak.length + disposotionLen1 + name.toString().getBytes(useCharset).length + lineBreak.length + lineBreak.length + strx.getBytes(useCharset).length + lineBreak.length;
                        } else if (namex instanceof File) {
                            File file = (File) namex;
                            contentLength = (int) ((long) contentLength + (long) (startBoundary.length + lineBreak.length + disposotionLen2 + name.toString().getBytes(useCharset).length + file.getName().getBytes(useCharset).length + lineBreak.length + fileContentType.length + lineBreak.length + lineBreak.length) + file.length() + (long) lineBreak.length);
                        } else if (namex instanceof byte[]) {
                            byte[] bytes = (byte[]) ((byte[]) namex);
                            contentLength += startBoundary.length + lineBreak.length + disposotionLen1 + name.toString().getBytes(useCharset).length + lineBreak.length + lineBreak.length + bytes.length + lineBreak.length;
                        }
                    }

                    contentLength += endBoundary.length + lineBreak.length;
                    this.conn.setRequestProperty("Content-Length", Integer.toString(contentLength));
                    OutputStream osx = this.conn.getOutputStream();
                    Iterator var27 = this.paramMap.keySet().iterator();

                    while (true) {
                        while (var27.hasNext()) {
                            namex = var27.next();
                            Object dataxx = this.paramMap.get(namex);
                            String disposition;
                            if (dataxx instanceof String) {
                                String str = (String) dataxx;
                                osx.write(startBoundary);
                                osx.write(lineBreak);
                                disposition = "Content-Disposition: form-data; name=\"" + namex.toString() + "\"";
                                osx.write(disposition.getBytes(useCharset));
                                osx.write(lineBreak);
                                osx.write(lineBreak);
                                osx.write(str.getBytes(useCharset));
                                osx.write(lineBreak);
                            } else if (!(dataxx instanceof File)) {
                                if (dataxx instanceof byte[]) {
                                    byte[] bytesx = (byte[]) ((byte[]) dataxx);
                                    osx.write(startBoundary);
                                    osx.write(lineBreak);
                                    disposition = "Content-Disposition: form-data; name=\"" + namex.toString() + "\"";
                                    osx.write(disposition.getBytes(useCharset));
                                    osx.write(lineBreak);
                                    osx.write(lineBreak);
                                    osx.write(bytesx);
                                    osx.write(lineBreak);
                                }
                            } else {
                                File filex = (File) dataxx;
                                osx.write(startBoundary);
                                osx.write(lineBreak);
                                disposition = "Content-Disposition: form-data; name=\"" + namex.toString() + "\"; filename=\"" + filex.getName() + "\"";
                                osx.write(disposition.getBytes(useCharset));
                                osx.write(lineBreak);
                                osx.write(fileContentType);
                                osx.write(lineBreak);
                                osx.write(lineBreak);
                                FileInputStream is = new FileInputStream(filex);
                                byte[] fileBuff = new byte[4096];

                                int readCnt;
                                while ((readCnt = is.read(fileBuff)) != -1) {
                                    osx.write(fileBuff, 0, readCnt);
                                }

                                is.close();
                                osx.write(lineBreak);
                            }
                        }

                        osx.write(endBoundary);
                        osx.write(lineBreak);
                        osx.close();
                        break;
                    }
                } else {
                    byte[] requestData = this.dataBytes;
                    if (requestData == null) {
                        datax = HttpClient.buildParamData(this.paramMap, useCharset);
                        requestData = datax.getBytes(useCharset);
                    }

                    this.conn.setRequestProperty("Content-Length", Integer.toString(requestData.length));
                    OutputStream os = this.conn.getOutputStream();
                    os.write(requestData);
                    os.close();
                }
            }

            return this;
        }

        public HttpClientResponse getResponse() throws Exception {
            int rtnCode = this.conn.getResponseCode();
            Map<String, List<String>> headerFields = this.conn.getHeaderFields();
            HttpClient.this.solveSetCookie(headerFields);
            HttpClientResponse response;
            if (rtnCode == 200) {
                String contentType = this.conn.getHeaderField("Content-Type");
                String responseCharset = null;
                if (contentType != null) {
                    String[] fields = contentType.split(";");
                    String[] var8 = fields;
                    int var9 = fields.length;

                    for (int var10 = 0; var10 < var9; ++var10) {
                        String field = var8[var10];
                        String[] pair = field.trim().split("=");
                        if (pair.length >= 2 && "charset".equals(pair[0].trim().toLowerCase())) {
                            responseCharset = pair[1].trim();
                            break;
                        }
                    }
                }

                String contentEncoding = this.conn.getHeaderField("Content-Encoding");
                boolean gzip = contentEncoding != null && contentEncoding.toLowerCase().equals("gzip");
                boolean deflate = contentEncoding != null && contentEncoding.toLowerCase().equals("deflate");
                InputStream is = this.conn.getInputStream();
                if (gzip) {
                    is = new GZIPInputStream((InputStream) is);
                }

                if (deflate) {
                    is = new InflaterInputStream((InputStream) is, new Inflater(true));
                }

                if (responseCharset == null) {
                    responseCharset = HttpClient.this.acceptCharset;
                }

                BufferedReader in;
                if (responseCharset != null) {
                    in = new BufferedReader(new InputStreamReader((InputStream) is, responseCharset));
                } else {
                    Charset useCharset = HttpClient.this.charset == null ? ConstFramework.getCharset() : HttpClient.this.charset;
                    in = new BufferedReader(new InputStreamReader((InputStream) is, useCharset));
                }

                char[] buff = new char[4096];
                StringBuilder responseSb = new StringBuilder();

                int length;
                while ((length = in.read(buff)) != -1) {
                    responseSb.append(new String(buff, 0, length));
                }

                in.close();
                ((InputStream) is).close();
                response = HttpClient.this.new HttpClientResponse(rtnCode, headerFields, responseSb.toString());
            } else {
                response = HttpClient.this.new HttpClientResponse(rtnCode, headerFields, (String) null);
            }

            this.conn.disconnect();
            return response;
        }

        public HttpClientResponseData getDataResponse() throws Exception {
            int rtnCode = this.conn.getResponseCode();
            Map<String, List<String>> headerFields = this.conn.getHeaderFields();
            HttpClient.this.solveSetCookie(headerFields);
            HttpClientResponseData response;
            if (rtnCode == 200) {
                String contentEncoding = this.conn.getHeaderField("Content-Encoding");
                boolean gzip = contentEncoding != null && contentEncoding.toLowerCase().equals("gzip");
                boolean deflate = contentEncoding != null && contentEncoding.toLowerCase().equals("deflate");
                InputStream is = this.conn.getInputStream();
                if (gzip) {
                    is = new GZIPInputStream((InputStream) is);
                }

                if (deflate) {
                    is = new InflaterInputStream((InputStream) is, new Inflater(true));
                }

                ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
                byte[] buff = new byte[4096];

                int length;
                while ((length = ((InputStream) is).read(buff)) != -1) {
                    out.write(buff, 0, length);
                }

                byte[] data = out.toByteArray();
                out.close();
                ((InputStream) is).close();
                response = HttpClient.this.new HttpClientResponseData(rtnCode, headerFields, data);
            } else {
                response = HttpClient.this.new HttpClientResponseData(rtnCode, headerFields, (byte[]) null);
            }

            this.conn.disconnect();
            return response;
        }
    }
}
