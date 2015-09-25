package com.example.kanjuice;

import android.os.Parcel;
import android.os.Parcelable;

public class Juice implements Parcelable {
    public String juiceName;
    public boolean isMultiSelected;
    public int selectedQuantity;
    public String juiceNameInKan;

    public Juice(String juiceName, String juiceNameInKan, boolean isMultiSelected) {
        this.juiceName = juiceName;
        this.juiceNameInKan = juiceNameInKan;
        this.isMultiSelected = isMultiSelected;
        selectedQuantity = 1;

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(juiceName);
        dest.writeInt(selectedQuantity);
    }

    public static final Creator<Juice> CREATOR
            = new Creator<Juice>() {
        public Juice createFromParcel(Parcel in) {
            return new Juice(in);
        }

        public Juice[] newArray(int size) {
            return new Juice[size];
        }
    };

    private Juice(Parcel in) {
        juiceName = in.readString();
        selectedQuantity = in.readInt();
    }
}
