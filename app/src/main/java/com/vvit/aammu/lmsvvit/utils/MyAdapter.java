package com.vvit.aammu.lmsvvit.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vvit.aammu.lmsvvit.R;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<Leave> leave;
    private Employee employee;
    private OnItemClickListener listener;

    public MyAdapter(Activity activity, List<Leave> leave, OnItemClickListener listener) {
        this.activity = activity;
        this.leave = leave;
        this.listener = listener;

    }

    public MyAdapter(Activity activity, List<Leave> leave, Employee employee, OnItemClickListener listener) {
        this.activity = activity;
        this.leave = leave;
        this.employee = employee;
        this.listener = listener;
    }

    public interface OnItemClickListener{
        void onItemClickListener(Leave leave);
    }



    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_applied_leaves,parent,false);
        ButterKnife.bind(this,view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (MyAdapter.ViewHolder) holder;
        Leave currentLeave = leave.get(position);
        viewHolder.bind(currentLeave,listener);
    }

    @Override
    public int getItemCount() {
        return leave.size();

    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.id_row_leaves_dates_applied)
        TextView appliedDates;
        @BindView(R.id.id_row_leaves_days_applied)
        TextView noOfDaysApplied;
        @BindView(R.id.id_row_image)
        ImageView imageView;
        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(final Leave currentLeave, final OnItemClickListener listener) {
            switch(currentLeave.getStatus()){
                case APPLIED:
                    imageView.setImageResource(R.drawable.ic_visibility_off);
                    break;
                case ACCEPTED:
                    imageView.setImageResource(R.drawable.ic_accept);
                    break;
                case REJECTED:
                    imageView.setImageResource(R.drawable.ic_reject);
                    break;
                case PENDING:
                    imageView.setImageResource(R.drawable.ic_visibility_black_24dp);
                    break;
            }
            String text = appliedDates.getText().toString();
            appliedDates.setText(String.format(activity.getString(R.string.strings_append), text, currentLeave.getAppliedDate()));
            text = noOfDaysApplied.getText().toString();
            noOfDaysApplied.setText(String.format(activity.getString(R.string.strings_append), text, String.valueOf(currentLeave.getNoOfDays())));
            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(currentLeave);
                }
            });
        }
    }
}
