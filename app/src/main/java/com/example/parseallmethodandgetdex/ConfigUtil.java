package com.example.parseallmethodandgetdex;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static com.example.parseallmethodandgetdex.ConstantValue.TAG;

class ConfigUtil {
    public static void saveToSDcard(final Context context, String pkgname, String filters) {
        File file = new File("/sdcard/config.cfg");
        if (file.exists()) {
            boolean flag = file.delete();
            Toast.makeText(context, flag ? "删除成功" : "删除失败", Toast.LENGTH_SHORT).show();
        }

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(pkgname);
            bufferedWriter.newLine();
            bufferedWriter.write(filters);
            bufferedWriter.flush();
            bufferedWriter.close();
            Toast.makeText(context, "保存成功，请重启目标应用", Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static JSONObject readFromSDcard() {
        File file = new File("/sdcard/config.cfg");
        JSONObject object = new JSONObject();
        if (!file.exists()) {
            return null;
        }

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            StringBuffer buffer = new StringBuffer();
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line.trim()).append(",");
            }
            line = buffer.toString();
            object.put("pkg", line.substring(0, line.indexOf(",")));
            object.put("filter", line.split(",").length > 1 ? line.substring(line.indexOf(",") + 1) : "");

        } catch (Exception e) {
            Log.e(TAG, "readFromSDcard: 文件打开失败->" +e.getMessage());
        }
        return object;
    }
}
