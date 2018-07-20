package com.vvit.aammu.lmsvvit.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vvit.aammu.lmsvvit.R;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;

public class ApplicantAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity activity;
    private List<Employee> employee;
    private Leave leave;
    private OnItemClickListener listener;


    public ApplicantAdapter(Activity activity, List<Employee> employee, OnItemClickListener listener) {
        this.activity = activity;
        this.employee = employee;
        this.listener = listener;
    }


    public interface OnItemClickListener{
        void onItemClickListener(Employee employee,Leave leave);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_applicants,parent,false);
        ButterKnife.bind(this,view);
        return new ViewHolder(view) ;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ApplicantAdapter.ViewHolder viewHolder = (ApplicantAdapter.ViewHolder) holder;
        Employee currentEmployee = employee.get(position);
        viewHolder.bind(currentEmployee,listener);
    }

    @Override
    public int getItemCount() {
        return employee.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.id_row_applicant_name)
        TextView applicantName;
        @BindView(R.id.id_row_applicant_applied_date)
        TextView appliedDate;
        @BindView(R.id.id_row_status)
        TextView leaveStatus;
        @BindView(R.id.id_row_applicant_leaves_days_applied)
        TextView noOfDaysApplied;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void bind(final Employee employee, final OnItemClickListener listener){
            applicantName.setText(employee.getName());
            if(employee.getDesignation().equalsIgnoreCase("HOD")){
                appliedDate.setVisibility(GONE);
                leaveStatus.setVisibility(GONE);
                noOfDaysApplied.setVisibility(GONE);
            }
            else{
                List<Leave> leaves = employee.getLeaves().getLeave();
                appliedDate.setVisibility(View.VISIBLE);
                leaveStatus.setVisibility(View.VISIBLE);
                noOfDaysApplied.setVisibility(View.VISIBLE);
                if(leaves!=null) {
                    for (int i = 1; i < leaves.size(); i++) {
                        if (leaves.get(i).getStatus().equals(Leave.Status.APPLIED)) {
                            String text = appliedDate.getText().toString();
                            appliedDate.setText(String.format(activity.getString(R.string.strings_append), text, leaves.get(i).getAppliedDate()));
                            text = noOfDaysApplied.getText().toString();
                            noOfDaysApplied.setText(String.format(activity.getString(R.string.strings_append), text, String.valueOf(leaves.get(i).getNoOfDays())));
                            leave = leaves.get(i);
                            text = leaveStatus.getText().toString();
                            leaveStatus.setText(String.format(activity.getString(R.string.strings_append), text, leave.getStatus()));
                        }
                    }
                }
            }
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(employee,leave);
                }
            });
        }
    }
}
