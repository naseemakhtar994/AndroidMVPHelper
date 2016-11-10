package com.ufkoku.demo_app.entity;

import android.os.Parcel;
import android.os.Parcelable;



public class AwesomeEntity implements Parcelable {

    private int importantDataField;

    public AwesomeEntity(int importantDataField) {
        this.importantDataField = importantDataField;
    }

    public int getImportantDataField() {
        return importantDataField;
    }

    public void setImportantDataField(int importantDataField) {
        this.importantDataField = importantDataField;
    }

    protected AwesomeEntity(Parcel in) {
        importantDataField = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(importantDataField);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<AwesomeEntity> CREATOR = new Creator<AwesomeEntity>() {
        @Override
        public AwesomeEntity createFromParcel(Parcel in) {
            return new AwesomeEntity(in);
        }

        @Override
        public AwesomeEntity[] newArray(int size) {
            return new AwesomeEntity[size];
        }
    };

}
