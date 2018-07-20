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
import com.vvit.aammu.lmsvvit.utils.ApplicantAdapter;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;


public class LeaveApplicantsFragment extends Fragment {
    private FirebaseUtils firebaseUtils;
    private ApplicantAdapter adapter;
    private RecyclerView recyclerView;
    private List<Employee> employeeList;
    private DatabaseReference mFirebaseDatabase;
    private Employee employee;
    private Fragment fragment;

    public LeaveApplicantsFragment() {
        // Required empty public constructor
    }

    public static LeaveApplicantsFragment newInstance() {
        LeaveApplicantsFragment fragment = new LeaveApplicantsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_leave_applicants, container, false);
        recyclerView = view.findViewById(R.id.id_recycler_applicants);
        firebaseUtils = new FirebaseUtils(getActivity());
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        setupAdapter();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if(fragment==null && savedInstanceState!=null)
            fragment=this.getChildFragmentManager().getFragment(savedInstanceState,"ApplicantsFragment");
        adapter.notifyDataSetChanged();
    }

    private void setupAdapter() {
        employeeList = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
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
                                if (leaves.get(i).getStatus().equals(Leave.Status.APPLIED)
                                    ||leaves.get(i).getStatus().equals(Leave.Status.PENDING)) {
                                    employeeList.add(employee);
                                    adapter.notifyDataSetChanged();

                                }
                                /*if (employeeList.size() <= 0) {
                                    Toast.makeText(getActivity(), R.string.no_faculty_applied, Toast.LENGTH_SHORT).show();
                                    getActivity().getSupportFragmentManager().popBackStack();
                                }*/
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
      adapter = new ApplicantAdapter(getActivity(), employeeList, new ApplicantAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(Employee employee,Leave leave ) {
                displayLeave(leave);
                LeaveApplicantsFragment.this.employee = employee;
            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }
    private void displayLeave(Leave leave) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.display_applied_leave,null);
        builder.setView(view);
        builder.setTitle("Leave Applied on "+leave.getAppliedDate());
        TextView appliedDate = view.findViewById(R.id.id_display_days_applied);
        TextView date = view.findViewById(R.id.id_display_applied_dates);
        EditText reason = view.findViewById(R.id.id_display_reason);
        TextView status = view.findViewById(R.id.id_display_status);
        String text = appliedDate.getText().toString();
        appliedDate.setText(String.format(getString(R.string.strings_append), text, String.valueOf(leave.getNoOfDays())));
        text = date.getText().toString();
        date.setText(String.format(getString(R.string.string_append_hypen), text, getDates(leave)));
        text = reason.getText().toString();
        reason.setEnabled(false);
        reason.setText(String.format(getString(R.string.strings_append), text, leave.getReason()));
        text = status.getText().toString();
        status.setText(String.format(getString(R.string.strings_append), text, leave.getStatus().toString()));
        builder.setPositiveButton(R.string.approve, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeStatus(Leave.Status.ACCEPTED);
            }
        });
        builder.setNegativeButton(R.string.reject, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                changeStatus(Leave.Status.REJECTED);
            }
        });
        builder.show();
    }

    private void changeStatus(Leave.Status status) {
        firebaseUtils.updateStatus(employee,status);
        getActivity().getSupportFragmentManager().popBackStack();
    }

    private String getDates(Leave leave) {
        StringBuilder builder = new StringBuilder();
        List<String> dates = leave.getDate();
        builder.append(dates.get(0));
        builder.append(dates.get(dates.size()-1));
        return builder.toString();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(fragment==null && savedInstanceState!=null)
            fragment=this.getChildFragmentManager().getFragment(savedInstanceState,getString(R.string.applicants_frag));

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(fragment!=null)
            getChildFragmentManager().putFragment(outState,getString(R.string.applicants_frag),fragment);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        getActivity().getSupportFragmentManager().popBackStack();
    }
}
