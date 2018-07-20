package com.vvit.aammu.lmsvvit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.firebase.database.FirebaseDatabase;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;

import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PersonalFragment extends Fragment {
    Unbinder unbinder;
    EditText name;
    EditText department;
    EditText designation;
    EditText email;
    RadioGroup gender;
    RadioButton male;
    RadioButton female;
    Button save,cancel;
    FirebaseUtils firebaseUtils;
    private Fragment fragment;

    public PersonalFragment() {
    }

    // TODO: Rename and change types and number of parameters
    public static PersonalFragment newInstance(Employee employee) {
        PersonalFragment fragment = new PersonalFragment();
        Bundle args = new Bundle();
        args.putParcelable("employee",employee);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(fragment==null && savedInstanceState!=null)
            fragment=this.getChildFragmentManager().getFragment(savedInstanceState,"PersonalFragment");

    }
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if(fragment!=null)
            getChildFragmentManager().putFragment(outState,"PersonalFragment",fragment);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_personal, container, false);
        unbinder=ButterKnife.bind(this,view);
        firebaseUtils = new FirebaseUtils(getActivity(), FirebaseDatabase.getInstance().getReference("id"));
        Bundle bundle=getArguments();
        name  = view.findViewById(R.id.id_personal_name);
        department = view.findViewById(R.id.id_personal_department);
        designation = view.findViewById(R.id.id_personal_designation);
        email = view.findViewById(R.id.id_personal_email);
        male = view.findViewById(R.id.id_radio_male);
        female = view.findViewById(R.id.id_radio_female);
        final Employee employee = bundle.getParcelable("employee");
        name.setText(employee.getName());
        department.setText(employee.getDepartment());
        email.setText(employee.getEmailId());
        designation.setText(employee.getDesignation());
        switch(employee.getGender()){
            case "Male":    male.setChecked(true);
                            female.setChecked(false);
                            break;
            case "Female":  female.setChecked(true);
                            male.setChecked(false);
                            break;
        }
        save = view.findViewById(R.id.id_save);
        cancel = view.findViewById(R.id.id_cancel);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Employee employee1 = employee;
                employee1.setName(name.getText().toString());
                employee1.setDesignation(designation.getText().toString());
                firebaseUtils.updatePersonalInfo(employee1);
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        return view;
    }

}
