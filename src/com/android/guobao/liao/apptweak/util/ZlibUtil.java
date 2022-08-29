package com.android.guobao.liao.apptweak.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

public class ZlibUtil {
    static private byte[] transform(byte[] indata, boolean deflate, boolean gzip) {
        byte[] outdata = null;
        try {
            if (deflate) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                DeflaterOutputStream dos = !gzip ? new DeflaterOutputStream(baos) : new GZIPOutputStream(baos);
                dos.write(indata);
                dos.close();
                baos.close();
                outdata = baos.toByteArray();
            } else {
                ByteArrayInputStream bais = new ByteArrayInputStream(indata);
                InflaterInputStream iis = !gzip ? new InflaterInputStream(bais) : new GZIPInputStream(bais);

                int n = 0;
                byte[] buf = new byte[4096];
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                while ((n = iis.read(buf)) > 0) {
                    baos.write(buf, 0, n);
                }
                baos.close();
                iis.close();
                bais.close();
                outdata = baos.toByteArray();
            }
        } catch (Exception e) {
        }
        return outdata;
    }

    static public byte[] zlibDeflate(byte[] indata) {
        return transform(indata, true, false);
    }

    static public byte[] zlibInflate(byte[] indata) {
        return transform(indata, false, false);
    }

    static public byte[] gzipDeflate(byte[] indata) {
        return transform(indata, true, true);
    }

    static public byte[] gzipInflate(byte[] indata) {
        return transform(indata, false, true);
    }

    static public boolean isZlib(byte[] indata) {
        if (indata == null || indata.length < 2) {
            return false;
        }
        if ((int) (indata[0] & 0xFF) != 0x78 || (int) (indata[1] & 0xFF) != 0x9C) {
            return false;
        }
        return true;
    }

    static public boolean isGzip(byte[] indata) {
        if (indata == null || indata.length < 2) {
            return false;
        }
        if ((int) (indata[0] & 0xFF) != 0x1F || (int) (indata[1] & 0xFF) != 0x8B) {
            return false;
        }
        return true;
    }

    static public boolean isZip(byte[] indata) {
        if (indata == null || indata.length < 4) {
            return false;
        }
        if ((int) (indata[0] & 0xFF) != 0x50 || (int) (indata[1] & 0xFF) != 0x4B || (int) (indata[2] & 0xFF) != 0x03 || (int) (indata[3] & 0xFF) != 0x04) {
            return false;
        }
        return true;
    }

    static public byte[] inflate(byte[] indata) {
        if (isGzip(indata)) {
            return gzipInflate(indata);
        } else if (isZlib(indata)) {
            return zlibInflate(indata);
        } else {
            return null;
        }
    }
}
