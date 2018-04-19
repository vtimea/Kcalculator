package vtimea.kcalculator.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.TextView;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import vtimea.kcalculator.R;
import vtimea.kcalculator.data.DaoSession;
import vtimea.kcalculator.data.DataManager;
import vtimea.kcalculator.data.FoodItem;
import vtimea.kcalculator.data.FoodItemDao;
import vtimea.kcalculator.fragments.SlideListFragment;
import vtimea.kcalculator.fragments.SlidePhotosFragment;

public class HomeActivity extends AppCompatActivity {
    private static final int NUM_OF_PAGES = 2;

    private DrawerLayout mDrawerLayout;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    private static Date currentDate;    //current day's start that the activity is showing 00:00:00
    private static Calendar calendar;

    private TextView tvDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvDate = (TextView) findViewById(R.id.tvDate);

        DataManager.initInstance(getApplicationContext());  //init database
        initTvDate();
        initNavDrawer();
        initFab();
        initViewPager();
        setTvCalories();
    }

    @Override
    public void onResume() {
        super.onResume();
        setTvCalories();
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

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return new SlideListFragment();
            else
                return new SlidePhotosFragment();
        }

        @Override
        public int getCount() {
            return NUM_OF_PAGES;
        }
    }

    private void initNavDrawer(){
        mDrawerLayout = findViewById(R.id.home_activity_drawer_layout);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(0).setChecked(true);
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
                                //stay on home activity
                                return true;
                            //graphs
                            case 1:
                                intent = new Intent(getBaseContext(), GraphsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
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

    private void initFab(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AddItemActivity.class);
                intent.putExtra("Date", currentDate.getTime());
                startActivity(intent);
            }
        });
    }

    private void initViewPager(){
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private void initTvDate(){
        //set date to today
        Date temp = Calendar.getInstance().getTime();
        currentDate = new Date(temp.getYear(), temp.getMonth(),  temp.getDate(), 0, 0, 0);

        //datepicker
        calendar = Calendar.getInstance();
        tvDate = (TextView) findViewById(R.id.tvDate);
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDates();
            }
        };

        //
        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(HomeActivity.this, date, calendar
                        .get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void setTvCalories(){
        int calorieLimit = 1800;    //TODO get calorie limit from settings

        List<FoodItem> list = getCurrentItems();

        int sumOfCalories = 0;
        for(int i = 0; i < list.size(); ++i){
            sumOfCalories += list.get(i).getCals();
        }

        TextView tvCalories = (TextView) findViewById(R.id.tvCalories);
        tvCalories.setText(sumOfCalories + "/" + calorieLimit);
    }

    private void setViewPager(){
        //TODO
    }

    private List<FoodItem> getCurrentItems(){
        DaoSession daoSession = DataManager.getInstance().getDaoSession();
        FoodItemDao foodItemDao = daoSession.getFoodItemDao();
        QueryBuilder queryBuilder = foodItemDao.queryBuilder()
                .where(FoodItemDao.Properties.Date.eq(currentDate));
        List<FoodItem> list = queryBuilder.list();

        return list;
    }

    //updates: -the tvDate
    //         -and the tvCalories
    //when a date has been picker with the date picker
    private void updateDates() {
        //update tvDate
        String myFormat;
        if(Calendar.getInstance().getTime().getYear() == calendar.getTime().getYear()) {
            myFormat = "MM/dd";
        }
        else {
            myFormat = "yyyy/MM/dd";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
        tvDate.setText(sdf.format(calendar.getTime()));

        //update sumOfCalories
        Date temp = calendar.getTime();
        currentDate = new Date(temp.getYear(), temp.getMonth(), temp.getDate(), 0, 0, 0);
        setTvCalories();
    }

}
