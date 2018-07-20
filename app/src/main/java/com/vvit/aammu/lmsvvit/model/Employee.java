package com.vvit.aammu.lmsvvit.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Employee implements Parcelable{
    private String name;
    private String department;
    private String designation;
    private String gender;
    private String emailId;
    private String password;
    private Leaves leaves;

    public Employee(String name, String department, String designation, String gender, String emailId, String password, Leaves leaves) {
       this.name = name;
        this.department = department;
        this.designation = designation;
        this.gender = gender;
        this.emailId = emailId;
        this.password = password;
        this.leaves = leaves;
    }

    public Employee() {
    }

    protected Employee(Parcel in) {
        name = in.readString();
        department = in.readString();
        designation = in.readString();
        gender = in.readString();
        emailId = in.readString();
        password = in.readString();
        leaves = in.readParcelable(Leaves.class.getClassLoader());
    }

    public static final Creator<Employee> CREATOR = new Creator<Employee>() {
        @Override
        public Employee createFromParcel(Parcel in) {
            return new Employee(in);
        }

        @Override
        public Employee[] newArray(int size) {
            return new Employee[size];
        }
    };

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Leaves getLeaves() {
        return leaves;
    }

    public void setLeaves(Leaves leaves) {
        this.leaves = leaves;
    }

    public void displayDetails() {
        Log.i("Printing Details","....");
        Log.i("Name:",getName());
        Log.i("EmailID:",getEmailId());
        Log.i("Password:",getPassword());

    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(department);
        dest.writeString(designation);
        dest.writeString(gender);
        dest.writeString(emailId);
        dest.writeString(password);
        dest.writeParcelable(leaves, flags);
    }
}
