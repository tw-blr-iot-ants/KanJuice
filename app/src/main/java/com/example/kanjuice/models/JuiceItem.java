package com.example.kanjuice.models;

import android.os.Parcel;
import android.os.Parcelable;

public class JuiceItem implements Parcelable {
    public String juiceName;
    public boolean isMultiSelected;
    public int selectedQuantity;
    public boolean isSugarless;
    public boolean animate;
    public int imageResId;
    public int kanResId;
    public boolean isFruit;
    public String type;


    public JuiceItem(String juiceName, int imageId, int kanResId, boolean isSugarless, boolean isFruit, String type) {
        this.juiceName = juiceName;
        this.imageResId = imageId;
        this.kanResId = kanResId;
        this.isSugarless = isSugarless;
        this.type = type;
        this.isMultiSelected = false;
        this.selectedQuantity = 1;
        this.isFruit = isFruit;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(juiceName);
        dest.writeByte((byte) (isSugarless ? 1 : 0));
        dest.writeByte((byte) (isFruit ? 1 : 0));
        dest.writeInt(selectedQuantity);
    }

    public static final Creator<JuiceItem> CREATOR = new Creator<JuiceItem>() {
        public JuiceItem createFromParcel(Parcel in) {
            return new JuiceItem(in);
        }

        public JuiceItem[] newArray(int size) {
            return new JuiceItem[size];
        }
    };

    private JuiceItem(Parcel in) {
        juiceName = in.readString();
        isSugarless = in.readByte() != 0;
        isFruit = in.readByte() != 0;
        selectedQuantity = in.readInt();
        type = isFruit ? "fruit" : "juice";
    }
}
