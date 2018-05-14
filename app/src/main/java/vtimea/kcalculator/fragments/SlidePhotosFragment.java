package vtimea.kcalculator.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import vtimea.kcalculator.R;
import vtimea.kcalculator.adapters.ImageAdapter;

public class SlidePhotosFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_slide_photos, container, false);

        //get date from bundle
        long date = getArguments().getLong("Date");

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);
        gridview.setAdapter(new ImageAdapter(getContext(), date));

        return rootView;
    }
}
