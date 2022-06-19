package com.android.guobao.liao.apptweak;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ReflectUtil {
    static public Class<?> classForName(String name, boolean initialize, ClassLoader loader) {
        try {
            return Class.forName(name, initialize, loader);
        } catch (Exception e) {
            return null;
        }
    }

    static public Constructor<?> findClassConstructor(Class<?> clazz, String constructor) {
        String decl = null;
        constructor = clazz.getName() + constructor;
        Constructor<?>[] cs = clazz.getDeclaredConstructors();

        for (int i = 0; i < cs.length; i++) {
            decl = cs[i].toString();
            decl = decl.substring(0, decl.lastIndexOf(')') + 1);
            decl = decl.substring(decl.lastIndexOf(' ') + 1);
            if (decl.equals(constructor)) {
                return cs[i];
            }
        }
        return null;
    }

    static public Method findClassMethod(Class<?> clazz, String method) {
        boolean isname = (method.indexOf('(') == -1);
        String method_ = (!isname ? clazz.getName() + "." + method : method);

        String decl = null;
        Method[] ms = clazz.getDeclaredMethods();

        for (int i = 0; i < ms.length; i++) {
            if (!isname) {
                decl = ms[i].toString();
                decl = decl.substring(0, decl.lastIndexOf(')') + 1);
                decl = decl.substring(decl.lastIndexOf(' ') + 1);
            } else {
                decl = ms[i].getName();
            }
            if (decl.equals(method_)) {
                return ms[i];
            }
        }
        int index = clazz.getName().lastIndexOf('$');
        if (index != -1) {
            clazz = classForName(clazz.getName().substring(0, index), false, clazz.getClassLoader());
            Method m = findClassMethod(clazz, method);
            return m;
        }
        clazz = clazz.getSuperclass();
        if (clazz == null) {
            return null;
        }
        Method m = findClassMethod(clazz, method);
        return m;
    }

    static public Field findClassField(Class<?> clazz, String fieldName) {
        try {
            Field f = clazz.getDeclaredField(fieldName);
            return f;
        } catch (Exception e) {
        }
        int index = clazz.getName().lastIndexOf('$');
        if (index != -1) {
            clazz = classForName(clazz.getName().substring(0, index), false, clazz.getClassLoader());
            Field f = findClassField(clazz, fieldName);
            return f;
        }
        clazz = clazz.getSuperclass();
        if (clazz == null) {
            return null;
        }
        Field f = findClassField(clazz, fieldName);
        return f;
    }

    static public Object newClassInstance(Class<?> clazz, String constructor, Object... args) {
        try {
            Constructor<?> c = findClassConstructor(clazz, constructor);
            c.setAccessible(true);
            Object o = c.newInstance(args);
            return o;
        } catch (Exception e) {
            return null;
        }
    }

    static public Object newClassInstance(Class<?> clazz) {
        return newClassInstance(clazz, "()");
    }

    static public Object[] newClassArrayInstance(Class<?> clazz, int length, Object... values) {
        try {
            Object[] o = (Object[]) Array.newInstance(clazz, length);
            int len = (values.length > length ? length : values.length);

            for (int i = 0; i < len; i++) {
                o[i] = values[i];
            }
            return o;
        } catch (Exception e) {
            return null;
        }
    }

    static public Object getObjectField(Object o, String name) {
        try {
            Field f = findClassField(o.getClass(), name);
            f.setAccessible(true);
            Object v = f.get(o);
            return v;
        } catch (Exception e) {
            return null;
        }
    }

    static public boolean setObjectField(Object o, String name, Object v) {
        try {
            Field f = findClassField(o.getClass(), name);
            f.setAccessible(true);
            f.set(o, v);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static public Object getClassField(Class<?> clazz, String name) {
        try {
            Field f = findClassField(clazz, name);
            f.setAccessible(true);
            Object v = f.get(null);
            return v;
        } catch (Exception e) {
            return null;
        }
    }

    static public boolean setClassField(Class<?> clazz, String name, Object v) {
        try {
            Field f = findClassField(clazz, name);
            f.setAccessible(true);
            f.set(null, v);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static public Object callObjectMethod(Object o, String method, Object... args) {
        try {
            Method m = findClassMethod(o.getClass(), method);
            m.setAccessible(true);
            Object hr = m.invoke(o, args);
            return hr;
        } catch (Exception e) {
            return null;
        }
    }

    static public Object callClassMethod(Class<?> clazz, String method, Object... args) {
        try {
            Method m = findClassMethod(clazz, method);
            m.setAccessible(true);
            Object hr = m.invoke(null, args);
            return hr;
        } catch (Exception e) {
            return null;
        }
    }

    static public String getMethodNameBySign(Class<?> clazz, String sign, int index) {
        int pos = 0;
        Method[] ms = clazz.getDeclaredMethods();

        String decl, retn;
        for (int i = 0; i < ms.length; i++) {
            decl = ms[i].toGenericString();
            decl = decl.substring(0, decl.lastIndexOf(')') + 1);
            retn = decl.substring(0, decl.lastIndexOf(' '));
            retn = retn.substring(retn.lastIndexOf(' ') + 1);
            decl = decl.substring(decl.lastIndexOf('('));
            decl += retn;
            if (decl.equals(sign) && pos++ == index) {
                return ms[i].getName();
            }
        }
        return null;
    }

    static public String getMethodNameBySign(Class<?> clazz, String sign) {
        return getMethodNameBySign(clazz, sign, 0);
    }

    static public String getFieldNameByType(Class<?> clazz, String type, int index) {
        int pos = 0;
        Field[] fs = clazz.getDeclaredFields();

        String gent;
        for (int i = 0, spec; i < fs.length; i++) {
            gent = fs[i].getGenericType().toString();
            spec = gent.lastIndexOf(' ');
            gent = (spec == -1 ? gent : gent.substring(spec + 1));
            if (gent.equals(type) && pos++ == index) {
                return fs[i].getName();
            }
        }
        return null;
    }

    static public String getFieldNameByType(Class<?> clazz, String type) {
        return getFieldNameByType(clazz, type, 0);
    }

    static public String objectToString(Object o) {
        String str = o.getClass().getName() + "->objectToString{\r\n";

        Field[] fs = o.getClass().getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            str += String.format("\to%02d: %s = %s\r\n", i, fs[i].getName(), getObjectField(o, fs[i].getName()));
        }
        str += "}\r\n";
        return str;
    }

    static public String classToString(Class<?> clazz) {
        String str = clazz.getName() + "->classToString{\r\n";

        Field[] fs = clazz.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            str += String.format("\tf%02d: %s\r\n", i, fs[i].toGenericString());
        }
        Constructor<?>[] cs = clazz.getDeclaredConstructors();
        for (int i = 0; i < cs.length; i++) {
            str += String.format("\tc%02d: %s\r\n", i, cs[i].toGenericString());
        }
        Method[] ms = clazz.getDeclaredMethods();
        for (int i = 0; i < ms.length; i++) {
            str += String.format("\tm%02d: %s\r\n", i, ms[i].toGenericString());
        }
        str += "}\r\n";
        return str;
    }
}
