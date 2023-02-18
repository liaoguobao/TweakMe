package com.android.guobao.liao.apptweak.util;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Arrays;

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
        return findClassConstructor(clazz, constructor, null);
    }

    static public Constructor<?> findClassConstructor(Class<?> clazz, String constructor, Object[] args) {
        Constructor<?>[] cs = clazz.getDeclaredConstructors();
        for (int i = 0; i < cs.length; i++) {
            if (!constructor.equals("")) { //优先declare匹配
                if (getMemberDeclare(cs[i], true).equals(constructor)) {
                    return cs[i]; //匹配成功就可以直接返回，因为这种匹配是精确匹配。   
                }
            } else if (args != null) {
                if (matchMemberArgs(cs[i], args)) {
                    return cs[i]; //其次args匹配
                }
            }
        }
        return null;
    }

    static public Method findClassMethod(Class<?> clazz, String method) {
        return findClassMethod(clazz, method, null);
    }

    static public Method findClassMethod(Class<?> clazz, String method, Object[] args) {
        int pos = method.indexOf('(');
        boolean isname = (pos == -1);
        boolean issign = (pos == 0);

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
            if (!decl.equals(method)) {
                continue;
            }
            if (isname && args != null && !matchMemberArgs(ms[i], args)) {
                continue; //以方法名称查找方法是不精确的，特别是方法有重载的情况下，需要进行一次参数匹配
            }
            return ms[i];
        }
        clazz = clazz.getSuperclass();
        if (clazz == null) {
            return null;
        }
        Method m = findClassMethod(clazz, method, args);
        return m;
    }

    static public Field findClassField(Class<?> clazz, String field) {
        int pos = field.indexOf('.');
        boolean isname = (pos == -1);

        String decl = null;
        Field[] fs = clazz.getDeclaredFields();

        for (int i = 0; i < fs.length; i++) {
            if (isname) {
                decl = fs[i].getName();
            } else {
                decl = getMemberSignature(fs[i]);
            }
            if (decl.equals(field)) {
                return fs[i];
            }
        }
        clazz = clazz.getSuperclass();
        if (clazz == null) {
            return null;
        }
        Field f = findClassField(clazz, field);
        return f;
    }

    static public Object newClassInstance(Class<?> clazz, String constructor, Object... args) {
        try {
            Constructor<?> c = findClassConstructor(clazz, constructor, args);
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
            Method m = findClassMethod(o.getClass(), method, args);
            m.setAccessible(true);
            Object hr = m.invoke(o, args);
            return hr;
        } catch (Exception e) {
            return null;
        }
    }

    static public Object callClassMethod(Class<?> clazz, String method, Object... args) {
        try {
            Method m = findClassMethod(clazz, method, args);
            m.setAccessible(true);
            Object hr = m.invoke(null, args);
            return hr;
        } catch (Exception e) {
            return null;
        }
    }

    static public String peekObject(Object o, Class<?> clazz) {
        if (clazz == null) {
            clazz = o.getClass();
        } else if (!clazz.isInstance(o)) {
            return "";
        }
        String str = clazz.getName() + "->peekObject{\r\n";

        Field[] fs = clazz.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            str += String.format("\to%02d: %s = %s\r\n", i, fs[i].getName(), peekValue(getObjectField(o, fs[i].getName())));
        }
        str += "}\r\n";
        return str;
    }

    static public String peekObject(Object o) {
        return peekObject(o, null);
    }

    static public String peekValue(Object o) {
        String byteArr = (byte[].class.isInstance(o) ? StringUtil.hexToVisible(((byte[]) o).length > 4096 ? Arrays.copyOf((byte[]) o, 4096) : (byte[]) o) : null);
        String objArr = (Object[].class.isInstance(o) ? Arrays.asList((Object[]) o).toString() : null);
        String value = String.format("%s", byteArr != null ? byteArr : (objArr != null ? objArr : o));
        return value;
    }

    static public String peekClass(Class<?> clazz) {
        String str = clazz.getName() + "->peekClass{\r\n";

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

    static public boolean matchMemberArgs(Member member, Object... args) { //Method\Constructor
        Class<?>[] ts = !Method.class.isInstance(member) ? ((Constructor<?>) member).getParameterTypes() : ((Method) member).getParameterTypes();
        if (args.length != ts.length) {
            return false;
        }
        for (int j = 0; j < ts.length; j++) {
            if (args[j] == null) {
                continue;
            }
            if (!ts[j].isPrimitive()) {
                if (!ts[j].isInstance(args[j])) {
                    return false;
                }
            } else if (ts[j] == boolean.class) {
                if (!Boolean.class.isInstance(args[j])) {
                    return false;
                }
            } else if (ts[j] == byte.class) {
                if (!Byte.class.isInstance(args[j])) {
                    return false;
                }
            } else if (ts[j] == char.class) {
                if (!Character.class.isInstance(args[j])) {
                    return false;
                }
            } else if (ts[j] == short.class) {
                if (!Short.class.isInstance(args[j])) {
                    return false;
                }
            } else if (ts[j] == int.class) {
                if (!Integer.class.isInstance(args[j])) {
                    return false;
                }
            } else if (ts[j] == long.class) {
                if (!Long.class.isInstance(args[j])) {
                    return false;
                }
            } else if (ts[j] == float.class) {
                if (!Float.class.isInstance(args[j])) {
                    return false;
                }
            } else if (ts[j] == double.class) {
                if (!Double.class.isInstance(args[j])) {
                    return false;
                }
            }
        }
        return true;
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

    static public Member findClassMember(Class<?> clazz, String member, boolean isfield) {
        if (isfield) {
            return findClassField(clazz, member);
        } else if (member.length() >= 2 && member.charAt(0) == '(' && member.charAt(member.length() - 1) == ')') {
            return findClassConstructor(clazz, member);
        } else {
            return findClassMethod(clazz, member);
        }
    }

    static public String getParamTypesDeclare(Class<?>[] types) { //Method\Constructor
        if (types == null) {
            return "()";
        }
        String decl = "";
        for (int i = 0; i < types.length; i++) {
            decl += "," + types[i].getCanonicalName();
        }
        if (types.length > 0) {
            decl = decl.substring(1);
        }
        decl = "(" + decl + ")";
        return decl;
    }
}
