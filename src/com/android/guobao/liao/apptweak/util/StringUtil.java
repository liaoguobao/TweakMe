package com.android.guobao.liao.apptweak.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class StringUtil {
    static public String hexToString(byte[] hex, boolean upper) {
        if (hex == null || hex.length <= 0) {
            return "";
        }
        byte[] digit = upper ? "0123456789ABCDEF".getBytes() : "0123456789abcdef".getBytes();
        byte[] str = new byte[hex.length << 1];

        for (int i = 0; i < hex.length; i++) {
            int cb = hex[i] & 0xFF; //byte范围为[-128, 127]，如果要表示为[0, 255]，必须要用int来表示，且要和0xff进行与操作后再赋值，否则在负数情况下后续查表会出现数组越界
            str[(i << 1) + 0] = digit[cb >> 4];
            str[(i << 1) + 1] = digit[cb & 0x0F];
        }
        String str_ = new String(str);
        return str_;
    }

    static public byte[] stringToHex(String str) {
        if (str == null || str.length() <= 0 || (str.length() & 1) != 0) {
            return null;
        }
        byte[] str_ = str.getBytes();
        byte[] hex = new byte[str_.length >> 1];

        for (int i = 0; i < str_.length; i += 2) {
            byte ch = str_[i + 0];
            byte cl = str_[i + 1];

            byte cb = 0;
            if (ch >= '0' && ch <= '9')
                cb |= ch - '0';
            else if (ch >= 'a' && ch <= 'f')
                cb |= ch - 'a' + 10;
            else if (ch >= 'A' && ch <= 'F')
                cb |= ch - 'A' + 10;
            else
                return null;

            cb <<= 4;
            if (cl >= '0' && cl <= '9')
                cb |= cl - '0';
            else if (cl >= 'a' && cl <= 'f')
                cb |= cl - 'a' + 10;
            else if (cl >= 'A' && cl <= 'F')
                cb |= cl - 'A' + 10;
            else
                return null;

            hex[i >> 1] = cb;
        }
        return hex;
    }

    static public String hexToVisible(byte[] hex) {
        return hexToVisible(hex, true);
    }

    static public String hexToVisible(byte[] hex, boolean upper) {
        if (hex == null || hex.length <= 0) {
            return "";
        }
        int len_ = 0;
        byte[] digit = upper ? "0123456789ABCDEF".getBytes() : "0123456789abcdef".getBytes();
        byte[] str = new byte[hex.length * 3];

        for (int i = 0; i < hex.length; i++) {
            int cb = hex[i] & 0xFF; //byte范围为[-128, 127]，如果要表示为[0, 255]，必须要用int来表示，且要和0xff进行与操作后再赋值，否则在负数情况下后续查表会出现数组越界
            if (cb > 0x20 && cb < 0x7F) {
                str[len_++] = (byte) cb;
            } else {
                str[len_++] = ' ';
                str[len_++] = digit[cb >> 4];
                str[len_++] = digit[cb & 0x0F];
            }
        }
        String str_ = new String(str, 0, len_);
        return str_;
    }

    static public byte[] visibleToHex(String str) {
        if (str == null || str.length() <= 0) {
            return null;
        }
        int len_ = 0;
        byte[] str_ = str.getBytes();
        byte[] hex = new byte[str_.length];

        for (int i = 0; i < str_.length; i++) {
            if (str_[i] != ' ') {
                hex[len_++] = str_[i];
            } else if (i + 2 >= str_.length) {
                return null;
            } else {
                byte ch = str_[i + 1];
                byte cl = str_[i + 2];
                i += 2;

                byte cb = 0;
                if (ch >= '0' && ch <= '9')
                    cb |= ch - '0';
                else if (ch >= 'a' && ch <= 'f')
                    cb |= ch - 'a' + 10;
                else if (ch >= 'A' && ch <= 'F')
                    cb |= ch - 'A' + 10;
                else
                    return null;

                cb <<= 4;
                if (cl >= '0' && cl <= '9')
                    cb |= cl - '0';
                else if (cl >= 'a' && cl <= 'f')
                    cb |= cl - 'a' + 10;
                else if (cl >= 'A' && cl <= 'F')
                    cb |= cl - 'A' + 10;
                else
                    return null;

                hex[len_++] = cb;
            }
        }
        hex = Arrays.copyOf(hex, len_);
        return hex;
    }

    static public byte[] bufferToByte(ByteBuffer buf) {
        if (!buf.hasRemaining()) {
            return null;
        }
        if (buf.hasArray()) {
            int ofs = buf.arrayOffset();
            byte[] ba = Arrays.copyOfRange(buf.array(), ofs + buf.position(), ofs + buf.limit());
            return ba;
        } else {
            byte[] ba = new byte[buf.remaining()];
            buf.get(ba, 0, ba.length);
            return ba;
        }
    }
}
