package com.vvit.aammu.lmsvvit.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;
import com.vvit.aammu.lmsvvit.R;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<Leave> leave;
    private OnItemClickListener listener;

    public MyAdapter(Activity activity, List<Leave> leave, OnItemClickListener listener) {
        this.activity = activity;
        this.leave = leave;
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
            //FirebaseUtils firebaseUtils = new FirebaseUtils(activity,FirebaseDatabase.getInstance().getReference());
            Log.i("Leave-RecyclerView",""+currentLeave.toString());
            currentLeave.display();
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
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClickListener(currentLeave);
                }
            });
        }
    }
}
