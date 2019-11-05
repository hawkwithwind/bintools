package com.wxipad.wechat.tools.crypto;

/**
 * 功能描述
 *
 * @author: aweie
 * @date: 2019/6/17 00178:50
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import java.security.Permission;
import java.util.HashSet;
import java.util.Set;

public class CryptoServicesPermission extends Permission {
    public static final String GLOBAL_CONFIG = "globalConfig";
    public static final String THREAD_LOCAL_CONFIG = "threadLocalConfig";
    public static final String DEFAULT_RANDOM = "defaultRandomConfig";
    private final Set<String> actions = new HashSet();

    public CryptoServicesPermission(String var1) {
        super(var1);
        this.actions.add(var1);
    }

    public boolean implies(Permission var1) {
        if (var1 instanceof CryptoServicesPermission) {
            CryptoServicesPermission var2 = (CryptoServicesPermission) var1;
            if (this.getName().equals(var2.getName())) {
                return true;
            }

            return this.actions.containsAll(var2.actions);
        }

        return false;
    }

    public boolean equals(Object var1) {
        if (var1 instanceof CryptoServicesPermission) {
            CryptoServicesPermission var2 = (CryptoServicesPermission) var1;
            return this.actions.equals(var2.actions);
        }

        return false;
    }

    public int hashCode() {
        return this.actions.hashCode();
    }

    public String getActions() {
        return this.actions.toString();
    }
}
