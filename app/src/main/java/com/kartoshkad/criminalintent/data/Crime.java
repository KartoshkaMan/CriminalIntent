package com.kartoshkad.criminalintent.data;

import java.util.Date;
import java.util.UUID;

/**
 * Created by user on 1/28/16.
 */
public class Crime {

    private boolean mSolved;
    private Date mDate;
    private String mSuspect;
    private String mTitle;
    private UUID mId;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
    }

    public boolean isSolved() {
        return mSolved;
    }
    public Date getDate() {
        return mDate;
    }
    public String getPhotoFilename() {
        return "IMG_" + mId + ".jpg";
    }
    public String getSuspect() {
        return mSuspect;
    }
    public String getTitle() {
        return mTitle;
    }
    public UUID getId() {
        return mId;
    }

    public void setDate(Date date) {
        mDate = date;
    }
    public void setSolved(boolean solved) {
        mSolved = solved;
    }
    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }
    public void setTitle(String title) {
        mTitle = title;
    }
}
