package com.example.parseallmethodandgetdex;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import static com.example.parseallmethodandgetdex.ConstantValue.TAG;

public class AppDetalActivity extends AppCompatActivity {

    private ImageView appInco;
    private TextView appPkg;
    private TextView appName;
    private Button submit;
    private Button exit;
    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_detal);
        // 获取意图对象
        Intent intent = getIntent();
        //获取传递的值
        Appinfo appinfo = (Appinfo) intent.getParcelableExtra("data");
        Log.d(TAG, "onCreate: appinfo->" + appinfo.getAppName());
        addView();
        addListener();
        showInfo(appinfo);
    }

    private void addListener() {
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfigUtil.saveToSDcard(AppDetalActivity.this, appPkg.getText().toString(), editText.getText().toString().trim());
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void showInfo(Appinfo appinfo) {
        appInco.setImageDrawable(appinfo.getIcon());
        appPkg.setText(appinfo.getAppPackage());
        appName.setText(appinfo.getAppName());
        try {
            JSONObject object = ConfigUtil.readFromSDcard();
            if(object!=null){
                String pkg = object.getString("pkg");
                if(pkg.equals(appinfo.getAppPackage())){
                    String filter = ConfigUtil.readFromSDcard().getString("filter");
                    String temp = filter.replace(",", "\r\n");
                    editText.setText(temp);
                }
            }


        } catch (JSONException e) {
            Log.e(TAG, "showInfo: 读取ConfigUtil异常");
        }
    }

    private void addView() {
        appInco = findViewById(R.id.app_icon);
        appPkg = findViewById(R.id.app_pkg);
        appName = findViewById(R.id.app_name);
        submit = findViewById(R.id.submit);
        exit = findViewById(R.id.exit);
        editText = findViewById(R.id.filters);
    }
}
