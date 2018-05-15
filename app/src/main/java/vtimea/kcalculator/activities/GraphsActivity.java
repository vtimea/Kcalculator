package vtimea.kcalculator.activities;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.greenrobot.greendao.query.QueryBuilder;

import java.nio.channels.CancelledKeyException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import vtimea.kcalculator.R;
import vtimea.kcalculator.data.DaoSession;
import vtimea.kcalculator.data.DataManager;
import vtimea.kcalculator.data.FoodItem;
import vtimea.kcalculator.data.FoodItemDao;

public class GraphsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private final int NUM_OF_DAYS_WEEK = 7;
    private final int NUM_OF_DAYS_MONTH = 30;

    private DrawerLayout mDrawerLayout;
    private LineChart chart;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graphs);
        mDrawerLayout = findViewById(R.id.home_activity_drawer_layout);
        chart = (LineChart) findViewById(R.id.chart);
        spinner = (Spinner) findViewById(R.id.spGraph);
        spinner.setOnItemSelectedListener(this);

        initNavDrawer();
        initSpinner();
        drawGraph(NUM_OF_DAYS_MONTH);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(mDrawerLayout.isDrawerOpen(GravityCompat.START))
                    mDrawerLayout.closeDrawers();
                else
                    mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initNavDrawer(){
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(1).setChecked(true);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        item.setChecked(true);
                        mDrawerLayout.closeDrawers();
                        Intent intent;
                        switch (item.getOrder()){
                            //home
                            case 0:
                                intent = new Intent(getBaseContext(), HomeActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                return true;
                            //graphs
                            case 1:
                                //stay on graphs activity
                                return true;
                            //settings
                            case 2:
                                intent = new Intent(getBaseContext(), SettingsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                return true;
                            default:
                                //stay on the activity
                                return true;
                        }
                    }
                }
        );
    }

    private void initSpinner(){
        List<String> categories = new ArrayList<String>();
        categories.add(getString(R.string.spLast30));
        categories.add(getString(R.string.spLast7));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    private void drawGraph(int days){
        List<Integer> items = getLastDaysData(days);

        //Convert the items into entries
        List<Entry> entries = new ArrayList<Entry>();
        for(int i = 0; i < items.size(); ++i){
            entries.add(new Entry(i*250, items.get(i)));
        }
        LineDataSet dataSet = new LineDataSet(entries, "Label"); // add entries to dataset
        dataSet.setValueTextSize(10);

        //Styling the chart
        dataSet.setColors(Color.BLUE);
        dataSet.setValueTextColor(Color.BLACK);
        chart.getXAxis().setDrawLabels(false);
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getDescription().setEnabled(true);
        Description description = new Description();
        description.setText(getString(R.string.graph_description) + Integer.toString(average(items)));
        description.setTextSize(14);
        chart.setDescription(description);
        chart.getLegend().setEnabled(false);

        //Set data
        LineData lineData = new LineData(dataSet);
        chart.setData(lineData);
        chart.getData().setHighlightEnabled(false);
        chart.invalidate(); // refresh
    }

    @Nullable
    private ArrayList<Integer> getLastDaysData(int numberOfDays){
        if(numberOfDays <= 0)
            return null;

        //calculate the two dates
        Calendar cal = Calendar.getInstance();

        Date temp = cal.getTime();
        Date currentDate = new Date(temp.getYear(), temp.getMonth(),  temp.getDate(), 23, 59, 0);

        cal.add(Calendar.DATE, numberOfDays*-1);
        temp = cal.getTime();
        Date lastDate = new Date(temp.getYear(), temp.getMonth(),  temp.getDate(), 0, 0, 0);

        //get data from database and add up the calories for each day
        ArrayList<Integer> list = new ArrayList<>();
        for(int i = 0; i < numberOfDays; ++i){
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -1*i);
            Date date = calendar.getTime();
            Date curr = new Date(date.getYear(), date.getMonth(),  date.getDate(), 0, 0, 0);

            DaoSession daoSession = DataManager.getInstance().getDaoSession();
            FoodItemDao foodItemDao = daoSession.getFoodItemDao();
            QueryBuilder queryBuilder = foodItemDao.queryBuilder()
                   .where(FoodItemDao.Properties.Date.eq(curr));
            List<FoodItem> items = queryBuilder.list();

            int sum = 0;
            for(FoodItem f : items){
                sum += f.getCals();
            }
            list.add(sum);
        }

        Collections.reverse(list);  //so its in ascending order (days)
        return list;    //returns the sum of the items for each day
    }

    private int average(List<Integer> list){
        int sum = 0;
        for(Integer i : list){
            sum += i;
        }
        return sum/list.size();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i == 0) {
            drawGraph(NUM_OF_DAYS_MONTH);
        }
        else {
            drawGraph(NUM_OF_DAYS_WEEK);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        //do nothing
    }
}
