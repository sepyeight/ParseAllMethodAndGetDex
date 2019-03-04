package com.example.parseallmethodandgetdex;

import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class Appinfo implements Parcelable {
    private String appName;
    private String appPackage;
    private Drawable icon;

    public Appinfo(String appName, String appPackage) {
        this.appName = appName;
        this.appPackage = appPackage;
    }

    public Appinfo() {
    }

    protected Appinfo(Parcel in) {
        appName = in.readString();
        appPackage = in.readString();
    }

    public static final Creator<Appinfo> CREATOR = new Creator<Appinfo>() {
        @Override
        public Appinfo createFromParcel(Parcel in) {
            return new Appinfo(in);
        }

        @Override
        public Appinfo[] newArray(int size) {
            return new Appinfo[size];
        }
    };

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAppPackage() {
        return appPackage;
    }

    public void setAppPackage(String appPackage) {
        this.appPackage = appPackage;
    }


    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(appName);
        dest.writeString(appPackage);
    }
}
