package vtimea.kcalculator.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import vtimea.kcalculator.R;

public class SettingsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private Switch aSwitch;
    private TextView etCals;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initNavDrawer();
        initSwitch();
        etCals = findViewById(R.id.etSettingsCalories);
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

    private void initSwitch(){
        aSwitch = findViewById(R.id.swCalcAuto);

        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                //if it's checked
                if(b){
                    LayoutInflater inflater = getLayoutInflater();
                    View alertLayout = inflater.inflate(R.layout.dialog_calc_calories, null);
                    final Spinner spGender = alertLayout.findViewById(R.id.spGender);
                    final EditText etHeight = alertLayout.findViewById(R.id.etHeight);
                    final EditText etWeight = alertLayout.findViewById(R.id.etWeight);
                    final EditText etAge = alertLayout.findViewById(R.id.etAge);
                    final Spinner spActivity = alertLayout.findViewById(R.id.spActivityLevel);

                    AlertDialog.Builder alert = new AlertDialog.Builder(SettingsActivity.this);
                    alert.setTitle("Calculate your calories");
                    alert.setView(alertLayout);
                    alert.setCancelable(false);
                    alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which){
                            //cancel
                        }
                    });

                    alert.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int height = Integer.parseInt(etHeight.getText().toString());
                            int weight = Integer.parseInt(etWeight.getText().toString());
                            int age = Integer.parseInt(etAge.getText().toString());
                            int calories = calculateCalories(false, height, weight, age, ActivityLevel.MODERATELY_ACTIVE);
                            etCals.setText(Integer.toString(calories));
                            etCals.setEnabled(false);
                            Toast.makeText(getBaseContext(), "Done", Toast.LENGTH_SHORT).show();
                        }
                    });
                    AlertDialog dialog = alert.create();
                    dialog.show();
                }
                else{
                    etCals.setEnabled(true);
                }
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

}
