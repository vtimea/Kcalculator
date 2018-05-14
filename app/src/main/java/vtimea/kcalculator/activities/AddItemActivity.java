package vtimea.kcalculator.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import vtimea.kcalculator.R;
import vtimea.kcalculator.data.DaoSession;
import vtimea.kcalculator.data.DataManager;
import vtimea.kcalculator.data.FoodItem;
import vtimea.kcalculator.data.FoodItemDao;

public class AddItemActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;

    private Date currentDate;
    private String mCurrentPhotoPath;
    private String currentFileName = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //set date
        Date temp = Calendar.getInstance().getTime();
        temp = new Date(1997, temp.getMonth(),  temp.getDate(), 0, 0, 0);
        currentDate = new Date(getIntent().getLongExtra("Date", temp.getTime()));
        initTvDate(currentDate);

        initFabOk();
        initFabCancel();
        initAddPhotoButton();
    }

    private void initFabOk(){
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fabOk);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText etCals = findViewById(R.id.etCals);

                String description = ((EditText) findViewById(R.id.etDesc)).getText().toString();
                String sCals = etCals.getText().toString();
                int cals = 0;
                try{
                    cals = Integer.parseInt(sCals);
                } catch (NumberFormatException e){
                    etCals.setError("Please enter a valid number!");
                }
                if(cals == 0) {
                    etCals.setError("Please enter a valid number!");
                    return;
                }
                String photoId = mCurrentPhotoPath;

                addNewItem(description, cals, currentDate, photoId);
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

    private void initTvDate(Date date){
        TextView tvDate = (TextView) findViewById(R.id.tvAddDate);
        String myFormat;
        //if the date is the current date
        if(Calendar.getInstance().getTime().getYear() == currentDate.getYear() &&
                Calendar.getInstance().getTime().getMonth() == currentDate.getMonth() &&
                Calendar.getInstance().getTime().getDate() == currentDate.getDate()) {
            tvDate.setText("Today");
        }
        else {
            myFormat = "yyyy/MM/dd";
            SimpleDateFormat sdf = new SimpleDateFormat(myFormat);
            tvDate.setText(sdf.format(currentDate));
        }
    }

    private void initAddPhotoButton(){
        Button btnAddPhoto = (Button) findViewById(R.id.btnAddPhoto);
        btnAddPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //take photo and save
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    File photoFile = null;
                    try {
                        photoFile = createImageFile();
                    } catch (IOException ex) {
                        Log.e("AddItemActivityLOG", "IOException");
                        Toast.makeText(getBaseContext(), "There was an error while opening the camera app!", Toast.LENGTH_LONG);
                    }
                    if (photoFile != null) {
                        Uri photoURI = FileProvider.getUriForFile(getApplicationContext(),
                                "vtimea.kcalculator.fileprovider",
                                photoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                        startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
                    }
                }
            }
        });
    }

    private Long addNewItem(String description, int cals, Date date, String photoId){
        DaoSession daoSession = DataManager.getInstance().getDaoSession();
        FoodItemDao foodItemDao = daoSession.getFoodItemDao();

        FoodItem item = new FoodItem();
        item.setDescription(description);
        item.setCals(cals);
        item.setDate(date);
        item.setPhotoId(photoId);

        foodItemDao.insert(item);
        return item.getId();
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentFileName = image.getName();
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.i("AddItemActivityLOG", "Filename: " + currentFileName);
        Log.i("AddItemActivityLOG", "Photo path: " + mCurrentPhotoPath);
        return image;
    }
}
