package vtimea.kcalculator.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import vtimea.kcalculator.R;

public class SettingsActivity extends AppCompatActivity {
    static private final String PREF_CALS = "Calories";
    static private final int DEFAULT_CALORIES = 2000;

    private DrawerLayout mDrawerLayout;
    private TextView etCals;
    private Button btnCalculate;

    Spinner spGender;
    EditText etHeight ;
    EditText etWeight;
    EditText etAge;
    Spinner spActivity;

    private enum ActivityLevel{
        SEDENTARY(1.2), MODERATELY_ACTIVE(1.3), ACTIVE(1.4);
        private final double value;

        private ActivityLevel(double value) {
            this.value = value;
        }

        public double getValue() {
            return value;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initNavDrawer();
        initButton();
        initCalories();
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
        mDrawerLayout = findViewById(R.id.home_activity_drawer_layout);

        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu_white_24dp);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().getItem(2).setChecked(true);
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
                                intent = new Intent(getBaseContext(), GraphsActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                                startActivity(intent);
                                return true;
                            //settings
                            case 2:
                                //stay on settings activity
                                return true;
                            default:
                                //stay on the activity
                                return true;
                        }
                    }
                }
        );
    }

    private void initButton(){
        btnCalculate = findViewById(R.id.btnCalcAuto);

        btnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = getLayoutInflater();
                View alertLayout = inflater.inflate(R.layout.dialog_calc_calories, null);

                //init components on dialog
                spGender = alertLayout.findViewById(R.id.spGender);
                etHeight = alertLayout.findViewById(R.id.etHeight);
                etWeight = alertLayout.findViewById(R.id.etWeight);
                etAge = alertLayout.findViewById(R.id.etAge);
                spActivity = alertLayout.findViewById(R.id.spActivityLevel);


                //init alert dialog
                AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                alert.setTitle(R.string.alert_title);
                alert.setView(alertLayout);
                alert.setCancelable(false);

                //CANCEL
                alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which){}
                });

                //OK
                alert.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });

                //show dialog
                AlertDialog dialog = alert.create();
                dialog.show();
                Button button = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                button.setOnClickListener(new CustomListener(dialog));
            }
        });
    }

    private void initCalories(){
        etCals = findViewById(R.id.etSettingsCalories);

        //if the user previously saved a calorie intake then show that, else show 2000
        int calories = getCaloriesFromPrefs(this);
        if(calories < 0) {
            etCals.setText(DEFAULT_CALORIES);
        }
        else{
            etCals.setText(Integer.toString(calories));
        }

        etCals.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //do nothing
            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(etCals.getText().toString().equals(""))
                    return;
                int calories = Integer.parseInt(etCals.getText().toString());
                saveCaloriesToPrefs(calories);
            }
        });
    }

    private int calculateCalories(boolean isMale, int height, int weight, int age, ActivityLevel activityLevel){
        final int mWeight = 10;
        final double mHeight = 6.25;
        final int mAge = -5;
        final int male = 5;
        final int female = -161;
        if(isMale){
            double result = (mWeight * weight + mHeight * height + mAge * age + male)*activityLevel.getValue();
            return (int)result;
        }
        else{
            double result = (mWeight * weight + mHeight * height + mAge * age + female)*activityLevel.getValue();
            return (int)result;
        }
    }

    private void saveCaloriesToPrefs(int calories){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(PREF_CALS);
        editor.putInt(PREF_CALS,calories);
        editor.apply();
    }

    static int getCaloriesFromPrefs(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int calories = preferences.getInt(PREF_CALS, DEFAULT_CALORIES);
        return calories;
    }

    class CustomListener implements View.OnClickListener {
        private final Dialog dialog;
        public CustomListener(Dialog dialog) {
            this.dialog = dialog;
        }

        @Override
        public void onClick(View v) {
            String sHeight = etHeight.getText().toString();
            String sWeight = etWeight.getText().toString();
            String sAge = etAge.getText().toString();

            if(!sHeight.equals("") && !sWeight.equals("") && !sAge.equals("")){
                int height = Integer.parseInt(etHeight.getText().toString());
                int weight = Integer.parseInt(etWeight.getText().toString());
                int age = Integer.parseInt(etAge.getText().toString());
                boolean isMale = spGender.getSelectedItemPosition() == 0;
                ActivityLevel activityLevel;
                switch (spActivity.getSelectedItemPosition()){
                    case 0:
                        activityLevel = ActivityLevel.SEDENTARY;
                        break;
                    case 1:
                        activityLevel = ActivityLevel.MODERATELY_ACTIVE;
                        break;
                    case 2:
                        activityLevel = ActivityLevel.ACTIVE;
                        break;
                    default:
                        activityLevel = ActivityLevel.SEDENTARY;
                        break;
                }

                //calculate and save calories
                int calories = calculateCalories(isMale, height, weight, age, activityLevel);
                saveCaloriesToPrefs(calories);
                etCals.setText(Integer.toString(calories));
                etCals.clearFocus();

                dialog.dismiss();
            }

            if(sHeight.equals(""))
                etHeight.setError(getString(R.string.error_invalid_number));

            if(sWeight.equals(""))
                etWeight.setError(getString(R.string.error_invalid_number));

            if(sAge.equals(""))
                etAge.setError(getString(R.string.error_invalid_number));


        }
    }

}
