package com.webot.bintools.controllers;

import com.webot.bintools.models.*;
import com.webot.bintools.tools.PackTool;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.webot.bintools.bo.Decoder;
import com.webot.bintools.tools.CryptTools;
import com.webot.bintools.tools.PbTools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.Map;

@RestController
public class CryptoController {

    private static final Logger logger = LoggerFactory.getLogger(CryptoController.class);

    @RequestMapping("/hello")
    public String greeting(@RequestParam String name) {
        return "hello, " + name;
    }
    
    @PostMapping("/encode")
    public Object encode(@RequestBody EncodeRequest request) {
        try {
            byte[] buffer;
            String paramName = "";
            try {
                paramName = "原数据";
                buffer = Decoder.decode(request.fromCodec, request.text);
            } catch(Exception e) {
                Exception ex =  new Exception("参数 [" + paramName + "] 错误");
                ex.initCause(e);
                throw ex;
            }
            return new CommonResponse("ok", Decoder.encode(request.toCodec, buffer));
        } catch(Exception e) {
            return new CommonResponse(e);
        }
    }

    @PostMapping("/pbunpack")
    public Object pbUnpack(@RequestBody UnpackRequest request) {
        try {
            byte[] buffer;
            String paramName = "";
            try {
                paramName = "原数据";
                buffer = Decoder.decode(request.codec, request.text);
            } catch(Exception e) {
                Exception ex =  new Exception("参数 [" + paramName + "] 错误");
                ex.initCause(e);
                throw ex;
            }
            ByteArrayInputStream bs = new ByteArrayInputStream(buffer);
            return new CommonResponse("ok", PbTools.parse(bs, ""));
        } catch(Exception e) {
            return new CommonResponse(e);
        }
    }

    @PostMapping("/decrypt")
    public Object symDecode(@RequestBody SymDecodeRequest request) {
        try {
            byte[] text;
            byte[] key;
            byte[] iv;
            String paramName = "";
            
            try {
                paramName = "密文";
                text = Decoder.decode(request.codec, request.cryptText);
                paramName = "密钥";
                key  = Decoder.decode(request.codec, request.key);
                paramName = "初始偏移";
                iv   = Decoder.decode(request.codec, request.iv);

                logger.info(String.format("text[%d], key[%d], iv[%d]",
                                          text.length, key.length, iv.length));
                    
            } catch (Exception e) {
                Exception ex =  new Exception("参数 [" + paramName + "] 错误");
                ex.initCause(e);
                throw ex;
            }

            try {
                switch(request.method) {
                case SymDecodeRequest.AESCBC:
                    byte[] decryptText = CryptTools.aesCbcDecryptData(request.padding, key, iv, text);
                    String result = Decoder.encode(request.codec, decryptText);
                    return new CommonResponse
                        ("ok", new DecodeResponse(request.codec, result));
                default:
                    return new CommonResponse("不支持加密方法 " + request.method, null);
                }
            } catch(Exception e) {
                Exception ex =  new Exception("解密错误");
                ex.initCause(e);
                throw ex;
            }
        }catch(Exception e) {
            return new CommonResponse(e);
        }
    }

    @PostMapping("/headerUnpack")
    public Object headerUnpack(@RequestBody UnpackHeaderRequest request) throws Exception {
        try {
            try {
                Map parsedPayload = PackTool.parseHeader(request.text);
                return new CommonResponse("ok", parsedPayload);
            } catch(Exception e) {
                Exception ex =  new Exception("pack header 解码失败");
                ex.initCause(e);
                throw ex;
            }
        } catch(Exception e) {
            return new CommonResponse(e);
        }
    }

}
