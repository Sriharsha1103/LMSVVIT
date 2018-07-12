package com.vvit.aammu.lmsvvit.utils;

import android.app.Activity;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvit.aammu.lmsvvit.R;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;
import com.vvit.aammu.lmsvvit.model.Leaves;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FirebaseViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    View mView;
    Activity activity;
    Employee employee;
    @BindView(R.id.id_row_leaves_dates_applied)
    TextView appliedDates;
    @BindView(R.id.id_row_leaves_days_applied)
    TextView noOfDaysApplied;
    @BindView(R.id.id_row_image)
    ImageView imageView;
    public FirebaseViewHolder(View itemView,Employee employee) {
        super(itemView);
        ButterKnife.bind(this,itemView);
        itemView.setOnClickListener(this);
        this.employee = employee;
    }

    public void bind(final Leave currentLeave) {
        FirebaseUtils firebaseUtils = new FirebaseUtils(activity, FirebaseDatabase.getInstance().getReference());

        switch(currentLeave.getStatus()){
            case APPLIED:
                int resID = activity.getResources().getIdentifier("ic_applied", "mipmap",  activity.getPackageName());
                imageView.setImageResource(resID);
                break;
            case ACCEPTED:
                resID = activity.getResources().getIdentifier("ic_accpeted", "mipmap",  activity.getPackageName());
                imageView.setImageResource(resID);
                break;
            case REJECTED:
                resID = activity.getResources().getIdentifier("ic_rejected", "mipmap",  activity.getPackageName());
                imageView.setImageResource(resID);
                break;
            case PENDING:
                resID = activity.getResources().getIdentifier("ic_pending", "mipmap",  activity.getPackageName());
                imageView.setImageResource(resID);
                break;
        }
        appliedDates.setText(currentLeave.getAppliedDate());
        noOfDaysApplied.setText(String.valueOf(currentLeave.getNoOfDays()));
    }
    @Override
    public void onClick(View v) {
        final List<Leave> leavesList = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Log.i("Leave Data","");
        if(new FirebaseUtils(activity,FirebaseDatabase.getInstance().getReference()).checkNetwork()) {
            reference.addValueEventListener(new ValueEventListener() {
                Leaves leaves;
                Employee employee1;
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i=0;
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        employee1 = data.getValue(Employee.class);
                        assert employee1 != null;
                        if (employee1.getEmailId().equals(employee.getEmailId())) {
                            if(data.hasChild("leaves/leave")){
                                data.getRef().child("leaves/leave").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int childCount=1;
                                        Log.i("LeaveCount",""+data.getChildrenCount());
                                        Log.i("LeaveCount",""+dataSnapshot.getChildrenCount());
                                        for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                                            Leave leave = data.child("leaves/leave").child(String.valueOf(childCount)).getValue(Leave.class);
                                            leave.display();
                                            leavesList.add(leave);
                                            childCount++;
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            break;
                        }
                        i++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }
        else{
            Toast.makeText(activity, "No Internet, Check Network Connection", Toast.LENGTH_SHORT).show();
        }
    }
}
