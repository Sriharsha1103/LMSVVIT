package com.vvit.aammu.lmsvvit.utils;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EmployeeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Employee> employeeList;
    private Activity activity;
    private OnItemClickListener listener;

    public EmployeeListAdapter(List<Employee> employeeList, Activity activity, OnItemClickListener listener) {
        this.employeeList = employeeList;
        this.activity = activity;
        this.listener = listener;

    }

    public interface OnItemClickListener{
        void onItemClickListener(Leave leave);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.row_employee_leaves,parent,false);
        ButterKnife.bind(this,view);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Employee employee = employeeList.get(position);
        viewHolder.bind(employee,listener);
    }

    @Override
    public int getItemCount() {

        return employeeList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{

        private static final String KEY_LEAVE_LIST = "leaves/leave";
        @BindView(R.id.id_row_employee_name)TextView emp_name;
        @BindView(R.id.id_recycler_employee_list)RecyclerView emp_list;
        @BindView(R.id.id_row_employee_leaves)TextView emp_leaves;
        private MyAdapter adapter;
        private List<Leave> leavesList = new ArrayList<>();
        private FirebaseUtils firebaseUtils;
        private DatabaseReference mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        public void bind(Employee employee, final OnItemClickListener listener) {
            emp_name.setText(employee.getName());
            String text = emp_leaves.getText().toString();
            emp_leaves.setText(String.format(activity.getString(R.string.strings_append), text,String.valueOf(employee.getLeaves().getcls())));
            firebaseUtils = new FirebaseUtils(activity,adapter);
            setUpAdapter(employee);

            }
        private void setUpAdapter(final Employee employee) {
            if (firebaseUtils.checkNetwork()) {
                mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                    Employee employee1;
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        leavesList.clear();
                        int i = 0;
                        for (final DataSnapshot data : dataSnapshot.getChildren()) {
                            employee1 = data.getValue(Employee.class);
                            assert employee1 != null;
                            if (employee1.getName().equalsIgnoreCase(employee.getName())) {
                                if (data.hasChild(KEY_LEAVE_LIST)) {
                                    data.getRef().child(KEY_LEAVE_LIST).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            int childCount = 1;
                                            for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                                Leave leave = data.child(KEY_LEAVE_LIST).child(String.valueOf(childCount)).getValue(Leave.class);
                                                if (leave.getStatus().equals(Leave.Status.ACCEPTED)) {
                                                    leavesList.add(leave);
                                                    adapter.notifyDataSetChanged();
                                                }

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
            } else {
                Toast.makeText(activity, activity.getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
            }
            adapter = new MyAdapter(activity, leavesList, new MyAdapter.OnItemClickListener() {
                @Override
                public void onItemClickListener(Leave leave) {
                    listener.onItemClickListener(leave);
                }
            });
            emp_list.setLayoutManager(new LinearLayoutManager(activity));
            emp_list.setAdapter(adapter);
        }
    }
}
