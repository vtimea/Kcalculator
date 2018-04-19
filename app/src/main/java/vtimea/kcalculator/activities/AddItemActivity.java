package vtimea.kcalculator.activities;

import android.app.Application;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;

import vtimea.kcalculator.R;
import vtimea.kcalculator.data.DaoMaster;
import vtimea.kcalculator.data.DaoSession;
import vtimea.kcalculator.data.DataManager;
import vtimea.kcalculator.data.FoodItem;
import vtimea.kcalculator.data.FoodItemDao;

public class AddItemActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        initFabOk();
        initFabCancel();
    }

    private void initFabOk(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabOk);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String description = ((EditText) findViewById(R.id.etDesc)).getText().toString();

                String etCals = ((EditText) findViewById(R.id.etCals)).getText().toString();
                int cals = Integer.parseInt(etCals);

                Date date = Calendar.getInstance().getTime();

                String photoId = "?";

                Log.i("TIMI", "Decription: " + description);
                Log.i("TIMI", "Calories: " + cals);
                Log.i("TIMI", "Date: " + date);
                Log.i("TIMI", "PhotoID: " + photoId);
                addNewItem(description, cals, date, photoId);
                finish();
            }
        });
    }

    private void initFabCancel(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabCancel);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void addNewItem(String description, int cals, Date date, String photoId){
        DaoSession daoSession = DataManager.getInstance().getDaoSession();
        FoodItemDao foodItemDao = daoSession.getFoodItemDao();

        FoodItem item = new FoodItem();
        item.setDescription(description);
        item.setCals(cals);
        item.setDate(date);
        item.setPhotoId(photoId);

        foodItemDao.insert(item);
        Log.i("TIMI", "Item inserted! ID: " + item.getId());
    }
}
