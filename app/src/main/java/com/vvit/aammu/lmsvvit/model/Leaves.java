package com.vvit.aammu.lmsvvit.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.List;

@IgnoreExtraProperties
public class Leaves implements Parcelable{
    private int cls;
    private int mls;
    private int sls;
    private List<Leave> leave;

    public Leaves(int cls, int mls, int sls) {
        this.cls = cls;
        this.mls = mls;
        this.sls = sls;
    }

    public Leaves() {
    }

    public Leaves(int cls, int mls, int sls, List<Leave> leaveList) {
        this.cls = cls;
        this.mls = mls;
        this.sls = sls;
        this.leave = leaveList;
    }

    protected Leaves(Parcel in) {
        cls = in.readInt();
        mls = in.readInt();

        sls = in.readInt();
    }

    public static final Creator<Leaves> CREATOR = new Creator<Leaves>() {
        @Override
        public Leaves createFromParcel(Parcel in) {
            return new Leaves(in);
        }

        @Override
        public Leaves[] newArray(int size) {
            return new Leaves[size];
        }
    };

    public int getcls() {
        return cls;
    }

    public void setcls(int cls) {
        this.cls = cls;
    }

    public int getmls() {
        return mls;
    }

    public void setmls(int mls) {
        this.mls = mls;
    }


    public int getsls() {
        return sls;
    }

    public void setsls(int sls) {
        this.sls = sls;
    }

    public List<Leave> getLeave() {
        return leave;
    }

    public void setLeave(List<Leave> leaveList) {
        this.leave = leaveList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(cls);
        dest.writeInt(mls);
        dest.writeInt(sls);
    }


}
