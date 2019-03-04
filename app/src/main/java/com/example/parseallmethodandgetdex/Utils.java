package com.example.parseallmethodandgetdex;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import dalvik.system.DexFile;

import static com.example.parseallmethodandgetdex.ConstantValue.DUMP_PATH;
import static com.example.parseallmethodandgetdex.ConstantValue.TAG;

class Utils {

    public static void threadGetDex(final ClassLoader classLoader) {
        Log.d(TAG, "threadGetDex getDexList.");
        ArrayList<DexFile> dexFileArrayList = getDexList(classLoader);
        if (dexFileArrayList == null) {
            return;
        }

        Log.d(TAG, "threadGetDex enumerate dex file.");
        for (DexFile dexFile : dexFileArrayList) {
            final ArrayList<String> classNameList = new ArrayList<>();
            Enumeration<String> enumeration = dexFile.entries();
            Log.d(TAG, "threadGetDex enumerate class name.");
            while (enumeration.hasMoreElements()) {
                String className = enumeration.nextElement();
                filterIllegalString(classNameList, className);
            }
            Log.d(TAG, "threadGetDex class name list size->" + classNameList.size());
            //loadmethod
            parseDex(classLoader, classNameList);
        }

    }

    public static void filterIllegalString(ArrayList<String> classNameList, String className) {
        String[] filterArray = {"android", "java", "com.google", "org", "com.android.dex", "dalvik", "javax", "libcore", "c.h.b.a.a.a", "com.alipay", "com.amap.api", "com.alibaba.fastjson", "com.example", "com.facebook"};
        boolean addFlag = true;
        try {
            JSONObject jsonObject = ConfigUtil.readFromSDcard();
            if (jsonObject != null) {
                String more = jsonObject.getString("filter");
                if (more != null && !TextUtils.isEmpty(more)) {
                    String[] strArr = more.split(",");
                    int filterArrayLength = filterArray.length;
                    int strArrLength = strArr.length;
                    filterArray = Arrays.copyOf(filterArray, filterArrayLength + strArrLength);
                    System.arraycopy(strArr, 0, filterArray, filterArrayLength, strArrLength);
                }
            }

        } catch (JSONException e) {
            Log.e(TAG, "filterIllegalString: 数组拷贝异常");
        }


        for (String filter : filterArray) {
            if (className.startsWith(filter)) {
                addFlag = false;
            }
        }
        if (addFlag) {
            classNameList.add(className);
        }
    }

    private static void parseDex(final ClassLoader classLoader, ArrayList<String> classNameList) {
        Set<Object> dexSet = new HashSet<>();
        for (final String className : classNameList) {
            try {
                Class clazz = classLoader.loadClass(className);
                try {
                    Constructor<?> constructor = clazz.getConstructor();
                    if (constructor != null) {
                        Log.d(TAG, "parseDex: constructor info->" + constructor);
                        constructor.newInstance();
                    }

                } catch (Exception e) {

                }
                Object dexObj = getDex(clazz);
                dexSet.add(dexObj);

            } catch (Exception e) {
                Log.e(TAG, "parseDex error->class name:" + className + ", error:" + e.getMessage());
            }
        }

        for (Object dexObj : dexSet) {
            byte[] bytes = getDexBytes(dexObj);
            String filename = DUMP_PATH + "dump_" + new Date().getTime() + "_" + bytes.length + ".dex";
            saveDex(bytes, filename);
        }
    }


    private static ArrayList<DexFile> getDexList(ClassLoader classLoader) {
        Object pathListObj = getObjectField(classLoader, "pathList");
        if (pathListObj == null) {
            return null;
        }

        Object dexElementsObj = getObjectField(pathListObj, "dexElements");
        if (pathListObj == null) {
            return null;
        }

        int dexElementsLength = Array.getLength(dexElementsObj);

        ArrayList<DexFile> dexFileArrayList = new ArrayList<>();
        for (int i = 0; i < dexElementsLength; i++) {
            DexFile dexFile = (DexFile) getObjectField(Array.get(dexElementsObj, i), "dexFile");

            if (dexFile != null && !dexFile.getName().startsWith("/system")) {
                dexFileArrayList.add(dexFile);
            }

        }
        Log.d(TAG, "threadGetDex: dex elements length->" + dexFileArrayList.size());
        return dexFileArrayList;
    }

    public static Object getObjectField(Object obj, String fieldName) {
        Class clazz = obj.getClass();
        Log.d(TAG, "getObjectSuperClassField obj->" + clazz.getName() + ", field name->" + fieldName);
        Object returnObj = null;
        if (!clazz.getName().equals(Object.class.getName())) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                returnObj = field.get(obj);
            } catch (Exception e) {
                Log.d(TAG, "getObjectField error->" + e.getMessage());
                returnObj = getObjectSuperClassField(obj, fieldName);
            }
        }
        return returnObj;
    }

    private static Object getObjectSuperClassField(Object obj, String fieldName) {
        Class clazz = obj.getClass().getSuperclass();
        Log.d(TAG, "getObjectSuperClassField obj->" + clazz.getName() + ", field name->" + fieldName);
        Object returnObj = null;
        if (!clazz.getName().equals(Object.class.getName())) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                returnObj = field.get(obj);
            } catch (Exception e) {
                Log.d(TAG, "getObjectSuperClassField error->" + e.getMessage());
            }
        }
        return returnObj;
    }

    public static void saveDex(byte[] bArr, String path) {
        Log.d(TAG, "saveDex bytes size->" + bArr.length + ", path name->" + path);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(path);
            fileOutputStream.write(bArr);
            fileOutputStream.close();
        } catch (Exception e) {
            Log.d(TAG, "writeByte error->" + e.getMessage());
        }
    }

    public static Object getDex(Class cls) {
        Object dexObj = null;
        try {
            Method getDexMethod = Class.forName("java.lang.Class").getDeclaredMethod("getDex", null);
            dexObj = getDexMethod.invoke(cls, null);
        } catch (Exception e) {
            Log.d(TAG, "getDex error->" + e.getMessage());
        }
        return dexObj;
    }


    public static byte[] getDexBytes(Object obj) {
        byte[] bArr = null;
        try {
            Method getBytesMethod = Class.forName("com.android.dex.Dex").getDeclaredMethod("getBytes", null);
            bArr = (byte[]) getBytesMethod.invoke(obj, null);
        } catch (Exception e) {
            Log.d(TAG, "getDexBytes error->" + e.getMessage());
        }
        return bArr;
    }

}
