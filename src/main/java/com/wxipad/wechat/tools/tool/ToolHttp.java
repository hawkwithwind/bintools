//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.tool;

import com.wxipad.wechat.tools.constant.ConstFramework;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ToolHttp {
    public static final int NONE_TIMEOUT = 0;
    public static final int DEFAULT_TIMEOUT = 0;
    private static final int BUFF_SIZE = 4096;

    public ToolHttp() {
    }

    public static HttpResponse get(String urlStr, HashMap<String, String> paramMap) {
        return get(urlStr, paramMap, ConstFramework.getCharset(), 0);
    }

    public static HttpResponse get(String urlStr, HashMap<String, String> paramMap, Charset charset) {
        return get(urlStr, paramMap, charset, 0);
    }

    public static HttpResponse get(String urlStr, HashMap<String, String> paramMap, int timeout) {
        return get(urlStr, paramMap, ConstFramework.getCharset(), timeout);
    }

    public static HttpResponse get(String urlStr, HashMap<String, String> paramMap, Charset charset, int timeout) {
        try {
            if (charset == null) {
                charset = ConstFramework.getCharset();
            }

            String data = buildParamData(paramMap, charset);
            URL url;
            if ("".equals(data)) {
                url = new URL(urlStr);
            } else if (urlStr.indexOf("?") >= 0) {
                url = new URL(urlStr + "&" + data);
            } else {
                url = new URL(urlStr + "?" + data);
            }

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(false);
            conn.setUseCaches(false);
            conn.setRequestMethod("GET");
            if (timeout > 0) {
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
            }

            conn.setRequestProperty("Charset", charset.name());
            conn.setRequestProperty("Connection", "Keep-Alive");
            HttpResponse response = getResponse(conn, charset);
            conn.disconnect();
            return response;
        } catch (Exception var8) {
            return new HttpResponse(var8);
        }
    }

    public static HttpResponse post(String urlStr, HashMap<String, String> paramMap) {
        return post(urlStr, paramMap, ConstFramework.getCharset(), 0);
    }

    public static HttpResponse post(String urlStr, HashMap<String, String> paramMap, Charset charset) {
        return post(urlStr, paramMap, charset, 0);
    }

    public static HttpResponse post(String urlStr, HashMap<String, String> paramMap, int timeout) {
        return post(urlStr, paramMap, ConstFramework.getCharset(), timeout);
    }

    public static HttpResponse post(String urlStr, HashMap<String, String> paramMap, Charset charset, int timeout) {
        String data = buildParamData(paramMap, charset);
        return write(urlStr, data, charset, timeout);
    }

    public static HttpResponse write(String urlStr, String data) {
        return write(urlStr, data, ConstFramework.getCharset(), 0);
    }

    public static HttpResponse write(String urlStr, String data, Charset charset) {
        return write(urlStr, data, charset, 0);
    }

    public static HttpResponse write(String urlStr, String data, int timeout) {
        return write(urlStr, data, ConstFramework.getCharset(), timeout);
    }

    public static HttpResponse write(String urlStr, String data, Charset charset, int timeout) {
        try {
            if (charset == null) {
                charset = ConstFramework.getCharset();
            }

            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            if (timeout > 0) {
                conn.setConnectTimeout(timeout);
                conn.setReadTimeout(timeout);
            }

            byte[] requestData = data.getBytes(charset);
            conn.setRequestProperty("Charset", charset.name());
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("Content-Length", Integer.toString(requestData.length));
            OutputStream os = conn.getOutputStream();
            os.write(requestData);
            os.close();
            HttpResponse response = getResponse(conn, charset);
            conn.disconnect();
            return response;
        } catch (Exception var9) {
            return new HttpResponse(var9);
        }
    }

    public static HttpRequest send(HttpListener listener, String urlStr, HashMap<String, String> paramMap, METHOD method) {
        return send(listener, urlStr, paramMap, method, ConstFramework.getCharset(), 0);
    }

    public static HttpRequest send(HttpListener listener, String urlStr, HashMap<String, String> paramMap, METHOD method, Charset charset) {
        return send(listener, urlStr, paramMap, method, charset, 0);
    }

    public static HttpRequest send(HttpListener listener, String urlStr, HashMap<String, String> paramMap, METHOD method, int timeout) {
        return send(listener, urlStr, paramMap, method, ConstFramework.getCharset(), timeout);
    }

    public static HttpRequest send(HttpListener listener, String urlStr, HashMap<String, String> paramMap, METHOD method, Charset charset, int timeout) {
        HttpRequest request = HttpRequest.createRequest(listener, urlStr, paramMap);
        request.setMethod(method).setCharset(charset).setTimeout(timeout).send();
        return request;
    }

    private static String buildParamData(HashMap<String, String> paramMap, Charset charset) {
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

    private static String URLEncode(String str, Charset charset) {
        try {
            return str == null ? "" : URLEncoder.encode(str, charset.name());
        } catch (UnsupportedEncodingException var3) {
            return "";
        }
    }

    private static HttpResponse getResponse(HttpURLConnection conn, Charset useCharset) {
        try {
            if (useCharset == null) {
                useCharset = ConstFramework.getCharset();
            }

            int rtnCode = conn.getResponseCode();
            Map<String, List<String>> headerFields = conn.getHeaderFields();
            if (rtnCode != 200) {
                return new HttpResponse(rtnCode, headerFields, (String) null);
            } else {
                String contentType = conn.getHeaderField("Content-Type");
                String charset = getCharsetByContentType(contentType);
                BufferedReader in;
                if (charset != null) {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream(), charset));
                } else {
                    in = new BufferedReader(new InputStreamReader(conn.getInputStream(), useCharset));
                }

                char[] buff = new char[4096];
                StringBuilder responseSb = new StringBuilder();

                int length;
                while ((length = in.read(buff)) != -1) {
                    responseSb.append(new String(buff, 0, length));
                }

                in.close();
                return new HttpResponse(rtnCode, headerFields, responseSb.toString());
            }
        } catch (Exception var10) {
            return new HttpResponse(var10);
        }
    }

    private static String getCharsetByContentType(String contentType) {
        String rtn = null;
        if (contentType != null) {
            String[] fields = contentType.split(";");
            String[] var3 = fields;
            int var4 = fields.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                String field = var3[var5];
                String[] pair = field.trim().split("=");
                if (pair.length >= 2 && "charset".equals(pair[0].trim().toLowerCase())) {
                    rtn = pair[1].trim();
                    break;
                }
            }
        }

        return rtn;
    }

    public static enum METHOD {
        GET,
        POST;

        private METHOD() {
        }
    }

    public interface HttpListener {
        void onData(HttpRequest var1, String var2);

        void onError(HttpRequest var1, int var2);

        void onException(Exception var1);
    }

    public static class HttpResponse {
        public static final int CODE_EXPCETION = -1;
        public static final int CODE_OK = 200;
        public int code;
        public Map<String, List<String>> headerFields;
        public String text;
        public Exception ex;

        public HttpResponse(int code, Map<String, List<String>> headerFields, String text) {
            this.code = code;
            this.headerFields = headerFields;
            this.text = text;
            this.ex = null;
        }

        public HttpResponse(Exception ex) {
            this.code = -1;
            this.text = null;
            this.ex = ex;
        }
    }

    public static class HttpRequest extends Thread {
        private static long cntID = 0L;
        private long id;
        private HttpListener listener;
        private METHOD method;
        private Charset charset;
        private int timeout;
        private String urlStr;
        private HashMap<String, String> paramMap;

        private HttpRequest() {
            this.method = METHOD.GET;
            this.charset = ConstFramework.getCharset();
            this.timeout = 0;
            this.id = (long) (cntID++);
        }

        public static HttpRequest createRequest(String urlStr, HashMap<String, String> paramMap) {
            return createRequest((HttpListener) null, urlStr, paramMap);
        }

        public static HttpRequest createRequest(HttpListener listener, String urlStr, HashMap<String, String> paramMap) {
            HttpRequest request = new HttpRequest();
            request.listener = listener;
            request.urlStr = urlStr;
            request.paramMap = paramMap;
            return request;
        }

        public long getID() {
            return this.id;
        }

        public HttpRequest setListener(HttpListener newListener) {
            this.listener = newListener;
            return this;
        }

        public HttpRequest setMethod(METHOD method) {
            if (method != null) {
                this.method = method;
            }

            return this;
        }

        public HttpRequest setCharset(Charset charset) {
            if (charset != null) {
                this.charset = charset;
            }

            return this;
        }

        public HttpRequest setTimeout(int timeout) {
            if (timeout >= 0) {
                this.timeout = timeout;
            }

            return this;
        }

        public HttpRequest setCharset(String charsetName) {
            if (charsetName != null) {
                this.charset = Charset.forName(charsetName);
            }

            return this;
        }

        public void send() {
            this.start();
        }

        public void run() {
            HttpResponse response;
            if (METHOD.POST == this.method) {
                response = ToolHttp.post(this.urlStr, this.paramMap, this.charset, this.timeout);
            } else {
                response = ToolHttp.get(this.urlStr, this.paramMap, this.charset, this.timeout);
            }

            if (this.listener != null) {
                if (response.ex == null) {
                    if (response.code == 200) {
                        this.listener.onData(this, response.text);
                    } else {
                        this.listener.onError(this, response.code);
                    }
                } else {
                    this.listener.onException(response.ex);
                }
            }

        }
    }
}
