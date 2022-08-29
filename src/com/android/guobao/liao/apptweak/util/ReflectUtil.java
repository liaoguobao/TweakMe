package com.android.guobao.liao.apptweak.util;

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
        //(java.lang.String,int,byte[])
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
        return findClassMethod(clazz, method, 0);
    }

    static public Method findClassMethod(Class<?> clazz, String method, int index) {
        //getDeclaredMethods
        //getDeclaredMethods()
        //()java.lang.reflect.Method[]
        int pos = method.indexOf('(');
        boolean isname = (pos == -1);
        boolean issign = (pos == 0);
        String method_ = ((!isname && !issign) ? clazz.getName() + "." + method : method);

        int index_ = 0;
        String decl = null;
        String retn = null;
        Method[] ms = clazz.getDeclaredMethods();

        for (int i = 0; i < ms.length; i++) {
            if (isname) { //getDeclaredMethods
                decl = ms[i].getName();
            } else if (!issign) { //getDeclaredMethods()
                decl = ms[i].toString();
                decl = decl.substring(0, decl.lastIndexOf(')') + 1);
                decl = decl.substring(decl.lastIndexOf(' ') + 1);
            } else { //()java.lang.reflect.Method[]
                decl = ms[i].toGenericString();
                decl = decl.substring(0, decl.lastIndexOf(')') + 1);
                retn = decl.substring(0, decl.lastIndexOf(' '));
                retn = retn.substring(retn.lastIndexOf(' ') + 1);
                decl = decl.substring(decl.lastIndexOf('('));
                decl += retn;
            }
            if (decl.equals(method_) && index_++ == index) {
                return ms[i];
            }
        }
        int pos_ = clazz.getName().lastIndexOf('$');
        if (pos_ != -1) {
            clazz = classForName(clazz.getName().substring(0, pos_), false, clazz.getClassLoader());
            Method m = findClassMethod(clazz, method, index);
            return m;
        }
        clazz = clazz.getSuperclass();
        if (clazz == null) {
            return null;
        }
        Method m = findClassMethod(clazz, method, index);
        return m;
    }

    static public Field findClassField(Class<?> clazz, String field) {
        return findClassField(clazz, field, 0);
    }

    static public Field findClassField(Class<?> clazz, String field, int index) {
        int pos = field.indexOf('.');
        boolean isname = (pos == -1);
        String field_ = (pos == 0 ? field.substring(1) : field); //.int .byte[],如果是内置类型，前面一定要带一个点号，以做区分

        if (!isname) { //如果传入的是字段类型，通过遍历方式模糊匹配
            int index_ = 0;
            int spec = 0;
            String decl = null;
            Field[] fs = clazz.getDeclaredFields();

            for (int i = 0; i < fs.length; i++) {
                decl = fs[i].getGenericType().toString();
                spec = decl.lastIndexOf(' ');
                decl = (spec == -1 ? decl : decl.substring(spec + 1));
                if (decl.equals(field_) && index_++ == index) {
                    return fs[i];
                }
            }
        } else { //如果传入的是字段名称，直接调用方法
            try {
                Field f = clazz.getDeclaredField(field);
                return f;
            } catch (Exception e) {
            }
        }
        int pos_ = clazz.getName().lastIndexOf('$');
        if (pos_ != -1) {
            clazz = classForName(clazz.getName().substring(0, pos_), false, clazz.getClassLoader());
            Field f = findClassField(clazz, field, index);
            return f;
        }
        clazz = clazz.getSuperclass();
        if (clazz == null) {
            return null;
        }
        Field f = findClassField(clazz, field, index);
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

    static public Object getObjectField(Object o, String field) {
        try {
            Field f = findClassField(o.getClass(), field);
            f.setAccessible(true);
            Object v = f.get(o);
            return v;
        } catch (Exception e) {
            return null;
        }
    }

    static public boolean setObjectField(Object o, String field, Object v) {
        try {
            Field f = findClassField(o.getClass(), field);
            f.setAccessible(true);
            f.set(o, v);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    static public Object getClassField(Class<?> clazz, String field) {
        try {
            Field f = findClassField(clazz, field);
            f.setAccessible(true);
            Object v = f.get(null);
            return v;
        } catch (Exception e) {
            return null;
        }
    }

    static public boolean setClassField(Class<?> clazz, String field, Object v) {
        try {
            Field f = findClassField(clazz, field);
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
