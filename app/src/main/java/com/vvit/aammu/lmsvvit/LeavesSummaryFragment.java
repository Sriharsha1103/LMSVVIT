package com.vvit.aammu.lmsvvit;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;
import com.vvit.aammu.lmsvvit.utils.EmployeeListAdapter;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;
import com.vvit.aammu.lmsvvit.utils.MyAdapter;

import java.util.ArrayList;
import java.util.List;


public class LeavesSummaryFragment extends Fragment {


    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private Employee employee;
    FirebaseUtils firebaseUtils;
    DatabaseReference mFirebaseDatabase;
    List<Leave> leavesList = new ArrayList<>();
    private Fragment fragment;
    private List<Employee> employeeList;
    private EmployeeListAdapter employeeListAdapter;

    public LeavesSummaryFragment() {
        // Required empty public constructor
    }

    public static LeavesSummaryFragment newInstance(Employee employee) {
        LeavesSummaryFragment fragment = new LeavesSummaryFragment();
        Bundle args = new Bundle();
        args.putParcelable("employee", employee);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (fragment == null && savedInstanceState != null)
            fragment = this.getChildFragmentManager().getFragment(savedInstanceState, getString(R.string.summary_frag));

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (fragment != null)
            getChildFragmentManager().putFragment(outState, getString(R.string.summary_frag), fragment);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_leaves_summary, container, false);
        recyclerView = view.findViewById(R.id.id_recycler_summary_leaves);
        Bundle bundle = getArguments();
        employee = bundle.getParcelable("employee");
        firebaseUtils = new FirebaseUtils(getActivity(), adapter);
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        if(employee.getDesignation().equalsIgnoreCase(getString(R.string.hod)))
            setAdapter();
        else
            setUpAdapter();
        return view;
    }

    private void setAdapter() {
        employeeList = new ArrayList<>();
        if(firebaseUtils.checkNetwork()){
            mFirebaseDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int j=1;
                    employeeList.clear();
                    for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                        Employee employee = dataSnapshot1.getValue(Employee.class);
                        List<Leave> leaves = employee.getLeaves().getLeave();
                        if(leaves!=null) {
                            for (int i = 1; i < leaves.size(); i++) {
                                if (leaves.get(i).getStatus().equals(Leave.Status.ACCEPTED)){
                                    employeeList.add(employee);
                                    employeeListAdapter.notifyDataSetChanged();
                                    break; }
                                if (employeeList.size() <= 0) {
                                    Toast.makeText(getActivity(), R.string.faculty_applied, Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }
                            }
                        }
                        j++;
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        else
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        employeeListAdapter = new EmployeeListAdapter(employeeList,getActivity(), new EmployeeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(Leave leave) {
                displayLeave(leave);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(employeeListAdapter);
    }

    private void setUpAdapter() {
        recyclerView.setHasFixedSize(true);
        if (firebaseUtils.checkNetwork()) {
            mFirebaseDatabase.addValueEventListener(new ValueEventListener() {
                Employee employee1;

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int i = 0;
                    for (final DataSnapshot data : dataSnapshot.getChildren()) {
                        employee1 = data.getValue(Employee.class);
                        assert employee1 != null;
                        if (employee1.getEmailId().equals(employee.getEmailId())) {
                            if (data.hasChild("leaves/leave")) {
                                leavesList.clear();
                                data.getRef().child("leaves/leave").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        int childCount = 1;
                                        for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                                            Leave leave = data.child("leaves/leave").child(String.valueOf(childCount)).getValue(Leave.class);
                                           leavesList.add(leave);
                                            childCount++;
                                            adapter.notifyDataSetChanged();
                                        }
                                        if (leavesList.size() <= 0) {
                                            Toast.makeText(getActivity(), R.string.no_leave_approved, Toast.LENGTH_SHORT).show();
                                            getActivity().getSupportFragmentManager().popBackStack();
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
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }adapter = new MyAdapter(getActivity(), leavesList, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(Leave leave) {
                displayLeave(leave);
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }

    private void displayLeave(Leave leave) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.display_applied_leave, null);
        builder.setView(view);
        builder.setTitle("Leave Applied on " + leave.getAppliedDate());
        TextView appliedDate = view.findViewById(R.id.id_display_days_applied);
        TextView date = view.findViewById(R.id.id_display_applied_dates);
        EditText reason = view.findViewById(R.id.id_display_reason);
        TextView status = view.findViewById(R.id.id_display_status);
        String text = appliedDate.getText().toString();
        appliedDate.setText(String.format(getString(R.string.strings_append), text, String.valueOf(leave.getNoOfDays())));
        text = date.getText().toString();
        date.setText(String.format(getString(R.string.strings_append), text, getDates(leave)));
        text = reason.getText().toString();
        reason.setEnabled(false);
        reason.setText(String.format(getString(R.string.strings_append), text, leave.getReason()));
        text = status.getText().toString();
        status.setText(String.format(getString(R.string.strings_append), text, leave.getStatus().toString()));
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }

    private String getDates(Leave leave) {
        StringBuilder builder = new StringBuilder();
        List<String> dates = leave.getDate();
        builder.append(dates.get(0));
        builder.append(" - ");
        builder.append(dates.get(dates.size() - 1));
        return builder.toString();
    }

}
