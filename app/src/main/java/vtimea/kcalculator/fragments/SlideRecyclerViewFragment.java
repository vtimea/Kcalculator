package vtimea.kcalculator.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import vtimea.kcalculator.R;
import vtimea.kcalculator.adapters.RecyclerViewAdapter;

public class SlideRecyclerViewFragment extends Fragment {
    private RecyclerView mRecyclerView;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_slide_list, container, false);

        //get date from bundle
        long date = getArguments().getLong("Date");

        //init recycler view
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);
        RecyclerViewAdapter viewAdapter = new RecyclerViewAdapter(date);
        mRecyclerView.setAdapter(viewAdapter);

        Log.i("REC", "Recycler view created.");

        return rootView;
    }
}
