package com.example.mazerunner.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Score implements Parcelable {
    private String name;
    private int score;

    // Constructor principal
    public Score(String name, int score) {
        this.name = name;
        this.score = score;
    }

    // Constructor vacío opcional
    public Score() {
        this.name = "";
        this.score = 0;
    }

    // Parcelable constructor
    protected Score(Parcel in) {
        name = in.readString();
        score = in.readInt();
    }

    // Parcelable Creator
    public static final Creator<Score> CREATOR = new Creator<Score>() {
        @Override
        public Score createFromParcel(Parcel in) {
            return new Score(in);
        }

        @Override
        public Score[] newArray(int size) {
            return new Score[size];
        }
    };

    // Getters
    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    // Parcelable methods
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(score);
    }
}
