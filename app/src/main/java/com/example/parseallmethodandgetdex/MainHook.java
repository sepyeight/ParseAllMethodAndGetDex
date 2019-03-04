package com.example.parseallmethodandgetdex;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static com.example.parseallmethodandgetdex.ConstantValue.TAG;

public class MainHook implements IXposedHookLoadPackage {

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        JSONObject jsonObject = ConfigUtil.readFromSDcard();
        if (jsonObject == null) {
            Log.d(TAG, "handleLoadPackage: 配置文件为空");
            return;
        }

        String pkgname = jsonObject.getString("pkg");
        //报名过滤
        if (pkgname.equals("") || !pkgname.equals(lpparam.packageName)) {
            return;
        }

        Log.d(TAG, "try to hook package->" + pkgname);
        XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);

                Context context = (Context) param.thisObject;
                addButton(context);
            }
        });

//        XposedHelpers.findAndHookMethod("android.app.ActivityThread", lpparam.classLoader, "currentApplication", new XC_MethodHook() {
//            @Override
//            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                super.afterHookedMethod(param);
//                Application currentApplication = (Application) param.getResult();
//                Context context = currentApplication.getApplicationContext();
//                final ClassLoader classLoader = context.getClassLoader();
//            }
//        });

        /**
         * 有些加固会在静态方法中写system.exit(0)，这个方法正常不会调用，所以要hook掉
         */
        XposedHelpers.findAndHookMethod(System.class, "exit", int.class, new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                Log.d(TAG, " System.exit called!");
                return null;
            }
        });
    }

    private void addButton(final Context context) {
        Activity activity = (Activity) context;
        View view = activity.getWindow().getDecorView();
        if (view != null && view instanceof ViewGroup) {
            Button button = new Button(context);
            LinearLayout linearLayout = new LinearLayout(context);
            FrameLayout.LayoutParams containerLp = new FrameLayout.LayoutParams(-2, -2);
            containerLp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
            linearLayout.setLayoutParams(containerLp);
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            button.setText("go");
            linearLayout.addView(button, layoutParams);
            ((ViewGroup) view).addView(linearLayout);

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final ClassLoader classLoader = context.getClassLoader();
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            Utils.threadGetDex(classLoader);
                        }
                    }.start();
                }
            });

        }
    }
}
