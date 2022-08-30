package com.android.guobao.liao.apptweak.plugin;

import java.nio.ByteBuffer;
import java.security.AlgorithmParameters;
import java.security.Key;
import java.security.MessageDigest;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Arrays;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import android.util.Log;

import com.android.guobao.liao.apptweak.JavaTweakBridge;
import com.android.guobao.liao.apptweak.util.*;

@SuppressWarnings("unused")
public class JavaTweak_algo {
    static public void loadDexFile(String dex) {
        JavaTweakBridge.hookJavaMethod("java.security.MessageDigest", "update(byte[])");
        JavaTweakBridge.hookJavaMethod("java.security.MessageDigest", "update(java.nio.ByteBuffer)", "update1");
        JavaTweakBridge.hookJavaMethod("java.security.MessageDigest", "update(byte[],int,int)", "update3");
        JavaTweakBridge.hookJavaMethod("java.security.MessageDigest", "digest()");
        JavaTweakBridge.hookJavaMethod("javax.crypto.Cipher", "chooseProvider");
    }

    static private void update(Object thiz, byte[] input) {
        JavaTweakBridge.callOriginalMethod(thiz, input);
    }

    static private void update1(Object thiz, ByteBuffer input) {
        update(thiz, StringUtil.bufferToByte(input));
    }

    static private void update3(Object thiz, byte[] input, int offset, int len) {
        update(thiz, Arrays.copyOfRange(input, offset, offset + len));
    }

    static private byte[] digest(MessageDigest thiz) {
        JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
        byte[] hr = JavaTweakBridge.callOriginalMethod(thiz);
        JavaTweakBridge.writeToLogcat(Log.INFO, "MessageDigest::digest->{algo=%s, sign=%s}", thiz.getAlgorithm(), StringUtil.hexToString(hr, false));
        return hr;
    }

    static private void chooseProvider(Cipher thiz, Object initType, int opmode, Key key, AlgorithmParameterSpec paramSpec, AlgorithmParameters params, Object random) {
        byte[] iv = null;
        if (initType.toString() == "ALGORITHM_PARAM_SPEC") {
            if (paramSpec instanceof IvParameterSpec) {
                iv = ((IvParameterSpec) paramSpec).getIV();
            } else if (paramSpec instanceof GCMParameterSpec) {
                iv = ((GCMParameterSpec) paramSpec).getIV();
            }
        }
        String mode = null;
        if (opmode == Cipher.ENCRYPT_MODE) {
            mode = "ENCRYPT";
        } else if (opmode == Cipher.DECRYPT_MODE) {
            mode = "DECRYPT";
        } else {
            mode = "UNKNOWN";
        }
        JavaTweakBridge.writeToLogcat(Log.INFO, Log.getStackTraceString(new Throwable()));
        JavaTweakBridge.callOriginalMethod(thiz, initType, opmode, key, paramSpec, params, random);
        JavaTweakBridge.writeToLogcat(Log.INFO, "Cipher::chooseProvider->{algo=%s, mode=%s, key=%s, iv=%s}", thiz.getAlgorithm(), mode, StringUtil.hexToVisible(key.getEncoded()), StringUtil.hexToVisible(iv));
    }
}
