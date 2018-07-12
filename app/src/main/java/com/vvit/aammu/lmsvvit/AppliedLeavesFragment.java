package com.vvit.aammu.lmsvvit;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.vvit.aammu.lmsvvit.model.Employee;
import com.vvit.aammu.lmsvvit.model.Leave;
import com.vvit.aammu.lmsvvit.utils.FirebaseUtils;
import com.vvit.aammu.lmsvvit.utils.FirebaseViewHolder;
import com.vvit.aammu.lmsvvit.utils.MyAdapter;

import java.lang.reflect.Field;
import java.util.List;


public class AppliedLeavesFragment extends Fragment
        //implements LoaderManager.LoaderCallbacks<List<Leave>>
{
    private MyAdapter adapter;
    private RecyclerView recyclerView;
    private Employee employee;
    private List<Leave> leaveList;
    FirebaseUtils firebaseUtils;

    public AppliedLeavesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =inflater.inflate(R.layout.fragment_applied_leaves, container, false);
        recyclerView = view.findViewById(R.id.id_recycler_applied_leaves);
        Bundle bundle = getArguments();
        employee = bundle.getParcelable("employee");
        firebaseUtils = new FirebaseUtils(getActivity(),FirebaseDatabase.getInstance().getReference());
        //getActivity().getLoaderManager().restartLoader(21, null, AppliedLeavesFragment.this);
        //reference = FirebaseDatabase.getInstance().getReference();
        setUpAdapter();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter.notifyDataSetChanged();
        //setUpAdapter();
    }

    private void setUpAdapter() {
/*
        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Leave,FirebaseViewHolder>(Leave.class,R.layout.row_applied_leaves,FirebaseViewHolder.class,reference) {
            @Override
            protected void populateViewHolder(FirebaseViewHolder viewHolder, Leave model, int position) {
                viewHolder.bind(model);
            }
        };
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(firebaseRecyclerAdapter);
*/
        adapter = new MyAdapter(getActivity(), firebaseUtils.getLeaves(employee), new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClickListener(Leave leave) {
                Toast.makeText(getActivity(), " "+leave.getNoOfDays(), Toast.LENGTH_SHORT).show();
            }
        });recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
    }



    public static Fragment newInstance(Employee employee) {
        AppliedLeavesFragment fragment = new AppliedLeavesFragment();
        Bundle args = new Bundle();
        args.putParcelable("employee",employee);
        fragment.setArguments(args);
        return fragment;
    }

   /* @Override
    public Loader onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader(getActivity()) {
            List<Leave> leaveList;
            @Override
            public Object loadInBackground() {
                Log.i("Leave ","Loading in background");
                FirebaseUtils firebaseUtils = new FirebaseUtils(getActivity(), FirebaseDatabase.getInstance().getReference());
                leaveList = firebaseUtils.getLeaves(employee);
                return leaveList;
            }

            @Override
            protected void onStartLoading() {
                if(leaveList==null)
                    forceLoad();
                else
                    deliverResult(leaveList);
            }

            @Override
            public void deliverResult(Object data) {
                leaveList = (List<Leave>) data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Leave>> loader, List<Leave> data) {

        Log.i("Leave ","Loading Finished");
        leaveList = data;
        //adapter.notifyDataSetChanged();
        attachAdapter();
    }



    @Override
    public void onLoaderReset(Loader loader) {

    }*/
}
