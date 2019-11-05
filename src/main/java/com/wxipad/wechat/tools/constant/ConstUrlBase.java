//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.wxipad.wechat.tools.constant;

public abstract class ConstUrlBase {
    public static final String NONE_URL = "javascript:;";
    public static final String JS_URL = "javascript:";
    public final String contextUrl;
    public final String baseUrl;
    public final String jsBaseUrl;
    public final String cssBaseUrl;
    public final String imgBaseUrl;

    public ConstUrlBase(String contextPath) {
        this.contextUrl = contextPath;
        this.baseUrl = contextPath + "/";
        this.jsBaseUrl = this.baseUrl + "js/";
        this.cssBaseUrl = this.baseUrl + "css/";
        this.imgBaseUrl = this.baseUrl + "img/";
    }

    public String getStaticBaseUrl() {
        return this.baseUrl;
    }

    public String getStaticJsBaseUrl() {
        return this.getStaticBaseUrl() + "js/";
    }

    public String getStaticCssBaseUrl() {
        return this.getStaticBaseUrl() + "css/";
    }

    public String getStaticImgBaseUrl() {
        return this.getStaticBaseUrl() + "img/";
    }

    public String jointBase(String staticSrc) {
        return this.baseUrl + staticSrc;
    }

    public String jointBaseJs(String jsSrc) {
        return this.jsBaseUrl + jsSrc;
    }

    public String jointBaseCss(String cssSrc) {
        return this.cssBaseUrl + cssSrc;
    }

    public String jointBaseImg(String imgSrc) {
        return this.imgBaseUrl + imgSrc;
    }

    public String jointStatic(String staticSrc) {
        return this.getStaticBaseUrl() + staticSrc;
    }

    public String jointStaticJs(String jsSrc) {
        return this.getStaticJsBaseUrl() + jsSrc;
    }

    public String jointStaticCss(String cssSrc) {
        return this.getStaticCssBaseUrl() + cssSrc;
    }

    public String jointStaticImg(String imgSrc) {
        return this.getStaticImgBaseUrl() + imgSrc;
    }
}
