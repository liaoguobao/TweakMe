package com.android.guobao.liao.apptweak.plugin;

import java.nio.ByteBuffer;
import java.security.Key;
import java.security.MessageDigest;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import android.util.Log;
import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.JavaTweakHook;
import com.android.guobao.liao.apptweak.JavaTweakPlugin;
import com.android.guobao.liao.apptweak.JavaTweakReplace;
import com.android.guobao.liao.apptweak.util.*;

public class JavaTweak_algo extends JavaTweakPlugin {
    protected void loadDexFile(String dex) {
        JavaTweakBridge.hookJavaMethod(MessageDigest.class, "update(byte[])");
        JavaTweakBridge.hookJavaMethod(MessageDigest.class, "update(java.nio.ByteBuffer)", new JavaTweakReplace(true) {
            protected Object replaceHookedMethod(Object thiz, Object[] args) {
                return ((MessageDigest) thiz).digest(StringUtil.bufferToByte((ByteBuffer) args[0]));
            }
        });
        JavaTweakBridge.hookJavaMethod(MessageDigest.class, "update(byte[],int,int)", new JavaTweakReplace(true) {
            protected Object replaceHookedMethod(Object thiz, Object[] args) {
                return ((MessageDigest) thiz).digest(Arrays.copyOfRange((byte[]) args[0], (int) args[1], (int) args[1] + (int) args[2]));
            }
        });
        JavaTweakBridge.hookJavaMethod(MessageDigest.class, "digest()", new JavaTweakHook() {
            protected void afterHookedMethod(Object thiz, Object[] args) {
                //JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
                JavaTweakBridge.writeToLogcat(Log.INFO, "MessageDigest::digest->{algo=%s, sign=%s}", ((MessageDigest) thiz).getAlgorithm(), StringUtil.hexToString((byte[]) getResult(), false));
            }
        });
        JavaTweakBridge.hookJavaMethod(Cipher.class, "chooseProvider", new JavaTweakHook() {
            protected void afterHookedMethod(Object thiz, Object[] args) {
                byte[] iv = null;
                if (args[0].toString() == "ALGORITHM_PARAM_SPEC") {
                    if (args[3] instanceof IvParameterSpec) {
                        iv = ((IvParameterSpec) args[3]).getIV();
                    } else if (args[3] instanceof GCMParameterSpec) {
                        iv = ((GCMParameterSpec) args[3]).getIV();
                    }
                }
                String mode = null;
                if ((int) args[1] == Cipher.ENCRYPT_MODE) {
                    mode = "ENCRYPT";
                } else if ((int) args[1] == Cipher.DECRYPT_MODE) {
                    mode = "DECRYPT";
                } else {
                    mode = "UNKNOWN";
                }
                //JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
                JavaTweakBridge.writeToLogcat(Log.INFO, "Cipher::chooseProvider->{algo=%s, mode=%s, key=%s, iv=%s}", ((Cipher) thiz).getAlgorithm(), mode, StringUtil.hexToVisible(((Key) args[2]).getEncoded()), StringUtil.hexToVisible(iv));
            }
        });
    }
}
