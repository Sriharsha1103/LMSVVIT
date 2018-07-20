package com.vvit.aammu.lmsvvit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;
import com.vvit.aammu.lmsvvit.utils.EmployeeListAdapter;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;


public class HomeFragment extends Fragment {
    TextView casualLeaves,casualBalance;
    TextView sickLeaves,sickBalance,name;

    private Fragment fragment;
    RecyclerView recyclerView;
    LinearLayout layout;
    AdView adView;
    FirebaseUtils firebaseUtils;
    EmployeeListAdapter adapter;
    private DatabaseReference mFirebaseDatabase;
    private Employee employee;
    private List<Employee> employeeList;

    public HomeFragment() {
    }


   public static HomeFragment newInstance(Employee employee) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putParcelable("employee",employee);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view= inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle=getArguments();
        name = view.findViewById(R.id.id_home_name);
        firebaseUtils = new FirebaseUtils(getActivity(), (DatabaseReference) null);
        layout = view.findViewById(R.id.id_content_layout);
        mFirebaseDatabase = FirebaseDatabase.getInstance().getReference();
        casualLeaves = view.findViewById(R.id.id_casual_available);
        sickLeaves = view.findViewById(R.id.id_sick_available);
        casualBalance = view.findViewById(R.id.id_casual_balance);
        sickBalance = view.findViewById(R.id.id_sick_balance);
        employee = bundle.getParcelable("employee");
        final Fragment fragment = ApplyLeaveFragment.newInstance(employee);
        casualLeaves.setText(String.valueOf(employee.getLeaves().getcls()));
        sickLeaves.setText(String.valueOf(employee.getLeaves().getsls()));
        int sickBal = 10 - Integer.parseInt(sickLeaves.getText().toString());
        int casualBal = 15 - Integer.parseInt(casualLeaves.getText().toString());
        casualBalance.setText(String.valueOf(casualBal));
        sickBalance.setText(String.valueOf(sickBal));
        recyclerView = view.findViewById(R.id.id_employee_list);
        adView = view.findViewById(R.id.adView);
        String text = name.getText().toString();
        name.setText(String.format(getString(R.string.strings_append), text, employee.getName()));
        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.id_frame_layout,fragment);
                fragmentTransaction.addToBackStack("Home Fragment");
                fragmentTransaction.commit();
            }
        });
        MobileAds.initialize(getActivity(),getString(R.string.ad_id));
        AdRequest request = new AdRequest.Builder()
                /*.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .addTestDevice("1421FAAF4869FCA6B9180D98097BB756")  // An example device ID
                */
                .build();
        adView.loadAd(request);
        if(employee.getDesignation().equalsIgnoreCase(getString(R.string.hod))){
            layout.setVisibility(GONE);
            recyclerView.setVisibility(View.VISIBLE);
            fab.setVisibility(GONE);
            setAdapter();
        }
        else{
            layout.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(GONE);
        }
        return view;
    }

    private void setAdapter() {
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
                                if (leaves.get(i).getStatus().equals(Leave.Status.ACCEPTED)){
                                    employeeList.add(employee);
                                    adapter.notifyDataSetChanged();
                                    break;
                                }

                                if (employeeList.size() <= 0) {
                                    Toast.makeText(getActivity(), R.string.no_faculty_applied, Toast.LENGTH_SHORT).show();
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
       adapter = new EmployeeListAdapter(employeeList,getActivity(), new EmployeeListAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(Leave leave) {

            }
        });
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(fragment==null && savedInstanceState!=null)
            fragment=this.getChildFragmentManager().getFragment(savedInstanceState,getString(R.string.home_frag));

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(fragment!=null)
            getChildFragmentManager().putFragment(outState,getString(R.string.home_frag),fragment);

    }


}
