package com.vvit.aammu.lmsvvit;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.timessquare.CalendarPickerView;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ApplyLeaveFragment extends Fragment {
    Spinner typesOfLeaves;
    Employee employee;
    AlertDialog.Builder dialog;
    EditText leavesRequired,reason;
    Date date;
    String leaveType;
    TextView leavesBalance,appliedDates,selectDates;
    Button applyLeave,cancelLeave;
    FirebaseUtils firebaseUtils;
    int leaveWanted;
    List<Date> selectedDates=null;
    public ApplyLeaveFragment() {
        // Required empty public constructor
    }

    public static ApplyLeaveFragment newInstance(Employee employee) {
        ApplyLeaveFragment fragment = new ApplyLeaveFragment();
        Bundle args = new Bundle();
        args.putParcelable("employee",employee);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apply_leave, container, false);
        typesOfLeaves = view.findViewById(R.id.id_leave_type_spinner);
        leavesBalance = view.findViewById(R.id.id_balance_leaves);
        appliedDates = view.findViewById(R.id.id_applied_dates);
        selectDates = view.findViewById(R.id.id_select_dates);
        applyLeave = view.findViewById(R.id.id_apply_leave);
        cancelLeave = view.findViewById(R.id.id_cancel_leave);
        leavesRequired = view.findViewById(R.id.id_leaves_days_required);
        reason = view.findViewById(R.id.id_reason);
        firebaseUtils = new FirebaseUtils(getActivity(), FirebaseDatabase.getInstance().getReference("id"));
        Bundle bundle = getArguments();
        employee = bundle.getParcelable("employee");
        addtoSpinner();
        selectDates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(leavesRequired.getText())){
                    Toast.makeText(getActivity(),"Please Enter number of leaves required",Toast.LENGTH_SHORT).show();
                }
                else {
                    leaveWanted = Integer.parseInt(leavesRequired.getText().toString());
                    setCalender();
                }
            }
        });
        applyLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Leave leave = new Leave();
                leave.setStatus(Leave.Status.APPLIED);
                Date date = new Date();
                int year = date.getYear()+1900;
                String dateString = date.getDate()+"/"+date.getMonth()+"/"+year;
                leave.setAppliedDate(dateString);
                leave.setNoOfDays(Integer.parseInt(leavesRequired.getText().toString()));
                leave.setReason(reason.getText().toString());
                List<String> datesApplied = new ArrayList<>();
                for(int i=0;i<leave.getNoOfDays();i++){
                    year = selectedDates.get(i).getYear()+1900;
                    dateString = selectedDates.get(i).getDate() +"/"+selectedDates.get(i).getMonth()+"/"+year;
                    datesApplied.add(dateString);
                }
                leave.setDate(datesApplied);
                Log.i("Fragment","-->");
                leave.toString();
                Log.i("Fragment","Connecting Firebase...");
                firebaseUtils.addLeave(employee,leave);
                getActivity().getFragmentManager().popBackStack();
            }
        });
        cancelLeave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
        return view;
    }

    private void setCalender() {
        dialog = new AlertDialog.Builder(getActivity());
        dialog.setTitle("Select Dates");
        final CalendarPickerView calendarView = new CalendarPickerView(getActivity(),null);
        Calendar nextYear = Calendar.getInstance();
        nextYear.add(Calendar.YEAR, 1);
        Date today = new Date();
        calendarView.init(today, nextYear.getTime())
                .inMode(CalendarPickerView.SelectionMode.RANGE);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(90, 40, 80, 40);
        calendarView.setLayoutParams(layoutParams);
        dialog.setView(calendarView);
        dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            StringBuilder stringBuilder=new StringBuilder();
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selectedDates = calendarView.getSelectedDates();
                int dayCount = selectedDates.size();
                if(dayCount> Integer.parseInt(leavesRequired.getText().toString()))
                    Toast.makeText(getActivity(),"Error: Selected must not exceed Required leaves",Toast.LENGTH_SHORT).show();
                else {
                    if (selectedDates.size() == 1) {
                        stringBuilder.append(selectedDates.get(0).getDate());
                        stringBuilder.append(" / ");
                        stringBuilder.append(selectedDates.get(0).getMonth());
                        stringBuilder.append(" / ");
                        stringBuilder.append(selectedDates.get(0).getYear()+1900);
                    } else {
                        stringBuilder.append(selectedDates.get(0).getDate());
                        stringBuilder.append(" / ");
                        stringBuilder.append(selectedDates.get(0).getMonth());
                        stringBuilder.append(" / ");
                        stringBuilder.append(selectedDates.get(0).getYear()+1900);
                        stringBuilder.append(" - ");
                        stringBuilder.append(selectedDates.get(selectedDates.size() - 1).getDate());
                        stringBuilder.append(" / ");
                        stringBuilder.append(selectedDates.get(selectedDates.size() - 1).getMonth());
                        stringBuilder.append(" / ");
                        stringBuilder.append(selectedDates.get(selectedDates.size() - 1).getYear()+1900);
                    }
                    appliedDates.setText(stringBuilder);
                }
            }
        });
        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });
        dialog.show();
    }

    private void addtoSpinner() {
        final List<String> leaveTypes = new ArrayList<>();
        leaveTypes.add("Casual Leaves");
        leaveTypes.add("Sick Leaves");
        if(employee.getGender().equals("Female"))
            leaveTypes.add("Maternity Leaves");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.support_simple_spinner_dropdown_item,leaveTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typesOfLeaves.setAdapter(adapter);
        typesOfLeaves.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(), "Selected "+item, Toast.LENGTH_SHORT).show();
                leaveType = item;
                switch(leaveType){
                    case "Casual Leaves": leavesBalance.setText(String.valueOf(employee.getLeaves().getcls()));
                        break;
                    case "Sick Leaves": leavesBalance.setText(String.valueOf(employee.getLeaves().getsls()));
                        break;
                    case "Maternity Leaves": leavesBalance.setText(String.valueOf(employee.getLeaves().getmls()));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getSupportFragmentManager().popBackStack();
    }

}
