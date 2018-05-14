package vtimea.kcalculator.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;

import vtimea.kcalculator.R;
import vtimea.kcalculator.adapters.ImageAdapter;

public class SlidePhotosFragment extends Fragment {
    /**/
    long date;
    GridView gridView;
    Context context;
    ImageAdapter adapter;
    TextView loadingText;

    private class AsyncLoad extends AsyncTask<Void, Void, Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            //loading the photos in the bg
            adapter = new ImageAdapter(getActivity(), date, SlidePhotosFragment.this.gridView);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... voids) {}

        @Override
        protected void onPostExecute(Void v) {
            super.onPostExecute(v);
            SlidePhotosFragment.this.gridView.setAdapter(SlidePhotosFragment.this.adapter); //setting adapter
            if(adapter.isEmpty()) loadingText.setText("Nothing to show");   //if there were no photos
            else loadingText.setVisibility(View.INVISIBLE); //hide text if photos are loaded
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_slide_photos, container, false);

        //get date from bundle
        long date = getArguments().getLong("Date");

        GridView gridview = (GridView) rootView.findViewById(R.id.gridview);

        //
        loadingText = (TextView) rootView.findViewById(R.id.tvLoading);
        loadingText.setText("Loading photos...");
        loadingText.setVisibility(View.VISIBLE);
        this.gridView = gridview;
        this.date = date;
        this.context = getActivity();
        new AsyncLoad().execute();
        //

        return rootView;
    }
}
