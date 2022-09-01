package com.android.guobao.liao.apptweak.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;

public class FileUtil {
    static public byte[] readFile(String file) {
        FileInputStream fis = null;
        ByteArrayOutputStream baos = null;
        try {
            byte[] buf = new byte[4096];
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();

            int n = 0;
            while ((n = fis.read(buf)) > 0) {
                baos.write(buf, 0, n);
            }
            byte[] hr = baos.toByteArray();
            return hr;
        } catch (Exception e) {
        } finally {
            try {
                if (baos != null)
                    baos.close();
                if (fis != null)
                    fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    static public boolean writeFile(String file, byte[] buf) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(buf);
            return true;
        } catch (Exception e) {
        } finally {
            try {
                if (fos != null)
                    fos.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    static public boolean copyFile(String from, String to) {
        FileInputStream fis_from = null;
        FileOutputStream fos_to = null;
        FileChannel fc_from = null;
        FileChannel fc_to = null;
        try {
            fis_from = new FileInputStream(from);
            fos_to = new FileOutputStream(to);

            fc_from = fis_from.getChannel();
            fc_to = fos_to.getChannel();

            //fc_from.transferTo(0, fc_from.size(), fc_to);
            fc_to.transferFrom(fc_from, 0, fc_from.size());
            return true;
        } catch (Exception e) {
        } finally {
            try {
                if (fc_to != null)
                    fc_to.close();
                if (fc_from != null)
                    fc_from.close();
                if (fos_to != null)
                    fos_to.close();
                if (fis_from != null)
                    fis_from.close();
            } catch (Exception e) {
            }
        }
        return false;
    }

    static public boolean moveFile(String from, String to) {
        boolean hr = false;
        if (copyFile(from, to)) {
            hr = new File(from).delete();
        }
        return hr;
    }
}
