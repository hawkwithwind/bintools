package com.webot.bintools.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.webot.bintools.models.SymDecodeRequest;
import com.webot.bintools.models.CommonResponse;
import com.webot.bintools.models.DecodeResponse;
import com.webot.bintools.tools.DecodeTools;
import com.webot.bintools.tools.CryptTools;

@RestController
public class CryptoController {

    @RequestMapping("/hello")
    public String greeting(@RequestParam String name) {
        return "hello, " + name;
    }

    @PostMapping("/decode")
    public Object symDecode(@RequestBody SymDecodeRequest request) {
        try {
            byte[] text;
            byte[] key;
            byte[] iv;
            String paramName = "";
            
            switch(request.codec) {
            case SymDecodeRequest.HEX:
                try {
                    paramName = "密文";
                    text = DecodeTools.decodeHex(request.cryptText);
                    paramName = "密钥";
                    key  = DecodeTools.decodeHex(request.key);
                    paramName = "初始偏移";
                    iv   = DecodeTools.decodeHex(request.iv);
                } catch (Exception e) {
                    Exception ex =  new Exception("参数 " + paramName + "错误");
                    ex.initCause(e);
                    throw ex;
                }
                break;
            default:
                return new CommonResponse("不支持编码 " + request.codec, null);
            }

            try {
                switch(request.method) {
                case SymDecodeRequest.AESCBC:
                    byte[] decryptText = CryptTools.aesCbcDecryptData(key, iv, text);
                    String result = DecodeTools.encodeHex(decryptText);
                    return new CommonResponse
                        ("ok", new DecodeResponse(SymDecodeRequest.HEX, result));
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
}
