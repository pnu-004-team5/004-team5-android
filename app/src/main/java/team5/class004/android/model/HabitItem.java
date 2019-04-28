package team5.class004.android.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class HabitItem implements Serializable {
    public String id;
    public String name;
    public String fromDate;
    public String toDate;
    public String completeDate;
    public String memo;
    public String color;
}
