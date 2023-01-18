package com.android.guobao.liao.apptweak.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class ReflectUtil {
    static public Class<?> classForName(ClassLoader loader, String name, boolean initialize) {
        try {
            return Class.forName(name, initialize, loader);
        } catch (Throwable e) {
            return null;
        }
    }

    static public Class<?> classForName(ClassLoader loader, String name) {
        return classForName(loader, name, true);
    }

    static public Class<?> classForName(String name) {
        return classForName(null, name);
    }

    static public Constructor<?> findClassConstructor(Class<?> clazz, String constructor) {
        String decl = null;
        Constructor<?>[] cs = clazz.getDeclaredConstructors();

        for (int i = 0; i < cs.length; i++) {
            decl = getMemberDeclare(cs[i], true);
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
        int pos = method.indexOf('(');
        boolean isname = (pos == -1);
        boolean issign = (pos == 0);

        int index_ = 0;
        String decl = null;
        Method[] ms = clazz.getDeclaredMethods();

        for (int i = 0; i < ms.length; i++) {
            if (isname) {
                decl = ms[i].getName();
            } else if (!issign) {
                decl = getMemberDeclare(ms[i], true);
            } else {
                decl = getMemberSignature(ms[i]);
            }
            if (decl.equals(method) && index_++ == index) {
                return ms[i];
            }
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

        int index_ = 0;
        String decl = null;
        Field[] fs = clazz.getDeclaredFields();

        for (int i = 0; i < fs.length; i++) {
            if (isname) {
                decl = fs[i].getName();
            } else {
                decl = getMemberSignature(fs[i]);
            }
            if (decl.equals(field) && index_++ == index) {
                return fs[i];
            }
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

    static public String getMemberDeclare(Member member, boolean nopath) { //Method\Constructor
        String decl = member.toString(); //private static java.lang.Object com.android.guobao.liao.apptweak.JavaTweakCallback.handleHookedMethod(java.lang.Object,java.lang.Object[],java.lang.Object) throws java.lang.Throwable
        if (true) {
            decl = decl.substring(0, decl.lastIndexOf(')') + 1);
            decl = decl.substring(decl.lastIndexOf(' ') + 1); //com.android.guobao.liao.apptweak.JavaTweakCallback.handleHookedMethod(java.lang.Object,java.lang.Object[],java.lang.Object)
        }
        if (nopath) {
            decl = decl.substring(member.getDeclaringClass().getName().length());
            decl = decl.substring(decl.charAt(0) == '.' ? 1 : 0); //handleHookedMethod(java.lang.Object,java.lang.Object[],java.lang.Object)
        }
        return decl;
    }

    static public String getMemberSignature(Member member) { //Method\Field
        String decl = null;
        String retn = null;
        if (Method.class.isInstance(member)) {
            decl = ((Method) member).toGenericString(); //private static java.lang.Object com.android.guobao.liao.apptweak.JavaTweakCallback.handleHookedMethod(java.lang.Object,java.lang.Object[],java.lang.Object) throws java.lang.Throwable
            decl = decl.substring(0, decl.lastIndexOf(')') + 1);
            retn = decl.substring(0, decl.lastIndexOf(' '));
            retn = retn.substring(retn.lastIndexOf(' ') + 1);
            decl = decl.substring(decl.lastIndexOf('('));
            decl = decl + retn; //(java.lang.Object,java.lang.Object[],java.lang.Object)java.lang.Object
        } else {
            decl = ((Field) member).getGenericType().toString(); //private static java.lang.Object
            decl = decl.substring(decl.lastIndexOf(' ') + 1); //java.lang.Object
            decl = ((Field) member).getType().isPrimitive() ? "." + decl : decl; //.int
        }
        return decl;
    }

    static public Member findClassMember(Class<?> clazz, String member, int index, boolean isfield) {
        if (isfield) {
            return findClassField(clazz, member, index);
        } else if (member.length() >= 2 && member.charAt(0) == '(' && member.charAt(member.length() - 1) == ')') {
            return findClassConstructor(clazz, member);
        } else {
            return findClassMethod(clazz, member, index);
        }
    }

    static public Member findClassMember(Class<?> clazz, String member, boolean isfield) {
        return findClassMember(clazz, member, 0, isfield);
    }
}
