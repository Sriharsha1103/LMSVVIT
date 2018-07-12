package com.vvit.aammu.lmsvvit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vvit.aammu.lmsvvit.model.Employee;


public class HomeFragment extends Fragment {
    //Unbinder unbinder;
    //@BindView(R.id.id_casual_available)
    TextView casualLeaves,casualBalance;
   // @BindView(R.id.id_sick_available)
    TextView sickLeaves,sickBalance;
   // @BindView(R.id.id_permission)
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);
        Bundle bundle=getArguments();
        casualLeaves = view.findViewById(R.id.id_casual_available);
        sickLeaves = view.findViewById(R.id.id_sick_available);
        casualBalance = view.findViewById(R.id.id_casual_balance);
        sickBalance = view.findViewById(R.id.id_sick_balance);
        final Employee employee = bundle.getParcelable("employee");
        final Fragment fragment = ApplyLeaveFragment.newInstance(employee);
        casualLeaves.setText(String.valueOf(employee.getLeaves().getcls()));
        sickLeaves.setText(String.valueOf(employee.getLeaves().getsls()));
        int sickBal = 10 - Integer.parseInt(sickLeaves.getText().toString());
        int casualBal = 15 - Integer.parseInt(casualLeaves.getText().toString());
        casualBalance.setText(String.valueOf(casualBal));
        sickBalance.setText(String.valueOf(sickBal));
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
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

}
