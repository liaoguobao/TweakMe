package com.android.guobao.liao.apptweak;

import java.io.IOException;
import java.util.Arrays;
import android.util.Base64;
import android.util.Log;

@SuppressWarnings("unused")
public class JavaTweak_sign {
    static private final String[] SIGN_BASE64 = new String[] {};
    static private final String ANDROIDD_SIGN = "MIIDDzCCAfegAwIBAgIES/9EuzANBgkqhkiG9w0BAQsFADA3MQswCQYDVQQGEwJVUzEQMA4GA1UEChMHQW5kcm9pZDEWMBQGA1UEAxMNQW5kcm9pZCBEZWJ1ZzAgFw0yMDA2MTkwMjMwNTNaGA8yMDUwMDYxMjAyMzA1M1owNzELMAkGA1UEBhMCVVMxEDAOBgNVBAoTB0FuZHJvaWQxFjAUBgNVBAMTDUFuZHJvaWQgRGVidWcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCP1ZHyYu8OdSAZXH0Ithad6CNphuPN7L5w2KCvFpPCHiEbBlKhHn+sQtv5r3Z9irqpMNAn10PxfNlCqmcBVDLBREVyQ8uScH+3RhFAGQpq0tD0TqgG9sHzklDdIgNxmnYX8YFbh1ZXk/NM0hvlLjRnntxQo6BXqaLrR2+bB6LjKKWgvVxUrtZCgFDHjxmh3b8FA26CA4nzNvMlMK1u+1NgcmdYIsTiwXv5DbYeA/T6u1tm3ebB6dZHL7X7CRh9GqxDK9bqCoqA+PdKgCXrv5KCdVsVZlERpIv0uhIeha2cf1JAEltdlskh4Xl/M5wm5lHF2/2W7ndQBqkkcJ5i6kofAgMBAAGjITAfMB0GA1UdDgQWBBSYGFDsFUU9Qi2lIwIcHKyNYLEGEjANBgkqhkiG9w0BAQsFAAOCAQEAWwBp+kpxbh/CqD8VWDsOMSSCShJOCaY8NXgDByhdjWqle4a30vXjZeFWdUbmgENnK09KGK75XKtzctFZpsKWGC0Pbqx0lAlhPHQicgtWX3jVVybesSMlhjJOIaN1ISa8tQ/OeWtgnxIhqiDlek6JXkX2vE68FXmtCKinhpAXdoeFnGUl8AgEGJHJ2xC75hAegCPOdfl8QhgvaMoW88Ym6e2B3m/zdaThxIE8rwnfiNenT54rJdIVXRztBpavFcdyU8VeF3ZRLVMjpoXaK3Rh6QLpkC8mKcc6JDgGsP67qPoj406mt575/3oH44Yr8ZsagXNpRFWL5Xj+hPMAN2+uhw==";
    static private final byte[] ANDROIDD_BYTES = Base64.decode(ANDROIDD_SIGN, Base64.NO_WRAP);
    static private final char[] ANDROIDD_CHARS = StringUtil.hexToString(ANDROIDD_BYTES, false).toCharArray();
    static private final int ANDROIDD_HASH = Arrays.hashCode(ANDROIDD_BYTES);
    static private String mAppCert = "";

    static public void setAppCert(String packageName, String certEncoded) {
        mAppCert = packageName + "@" + certEncoded;
    }

    static protected void loadDexFile(ClassLoader loader, String dex) {
        JavaTweakBridge.hookJavaMethod("android.content.pm.Signature", "toByteArray()");
        JavaTweakBridge.hookJavaMethod("android.content.pm.Signature", "toChars()");
        JavaTweakBridge.hookJavaMethod("android.content.pm.Signature", "hashCode()");
    }

    static private char[] toChars(Object thiz) {
        char[] hr = JavaTweakBridge.nologOriginalMethod(thiz);

        byte[] sign = getSignature(hr);
        hr = (sign != null ? StringUtil.hexToString(sign, false).toCharArray() : hr);
        return hr;
    }

    static private int hashCode(Object thiz) {
        int hr = JavaTweakBridge.nologOriginalMethod(thiz);

        byte[] sign = getSignature(hr);
        hr = (sign != null ? Arrays.hashCode(sign) : hr);
        return hr;
    }

    static private byte[] toByteArray(Object thiz) {
        byte[] hr = JavaTweakBridge.nologOriginalMethod(thiz);

        byte[] sign = getSignature(hr);
        hr = (sign != null ? sign : hr);
        return hr;
    }

    static private byte[] getSignature(Object o) {
        int type = 0;
        if (((o instanceof byte[]) && Arrays.equals((byte[]) o, ANDROIDD_BYTES))) {
            type = 1;
        } else if (((o instanceof char[]) && Arrays.equals((char[]) o, ANDROIDD_CHARS))) {
            type = 2;
        } else if (((o instanceof Integer) && (((Integer) o).intValue() == ANDROIDD_HASH))) {
            type = 3;
        } else {
            return null;
        }
        String packageName = TweakUtil.currentPackageName();
        byte[] hr = null;
        for (int i = 0; i < SIGN_BASE64.length; i++) {
            String[] sp = SIGN_BASE64[i].split("@");
            if (packageName.equals(sp[0])) {
                hr = Base64.decode(sp[1], Base64.NO_WRAP);
                break;
            }
        }
        if (hr == null && !mAppCert.isEmpty()) {
            String[] sp = mAppCert.split("@");
            if (packageName.equals(sp[0])) {
                hr = Base64.decode(sp[1], Base64.NO_WRAP);
            }
        }
        String str = null;
        if (hr != null && type == 1) {
            str = Base64.encodeToString(hr, Base64.NO_WRAP);
        } else if (hr != null && type == 2) {
            str = StringUtil.hexToString(hr, false);
        } else if (hr != null && type == 3) {
            str = String.valueOf(Arrays.hashCode(hr));
        }
        JavaTweakBridge.writeToLogcat(Log.INFO, hr != null ? String.format("SIGN_replace: %s: %s--->%s", packageName, o, str) : String.format("SIGN_null: %s: %s", packageName, o));
        return hr;
    }
}
