package vtimea.kcalculator.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import vtimea.kcalculator.data.DaoSession;
import vtimea.kcalculator.data.DataManager;
import vtimea.kcalculator.data.FoodItem;
import vtimea.kcalculator.data.FoodItemDao;

public class ImageAdapter extends BaseAdapter{
    private Date currentDate;
    private Context mContext;
    private int photo_size;
    private List<FoodItem> items;
    private List<Bitmap> photos;

    public ImageAdapter(Context c, long date) {
        mContext = c;
        currentDate = new Date(date);
        items = getCurrentItems();
        initPhotos();
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Log.i("PERFORMLOG", "---> getView <---");
        ImageView imageView;
        if (!(view instanceof ImageView)) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else {
            imageView = (ImageView) view;
        }

        imageView.setImageBitmap(photos.get(i));

        return imageView;
    }

    //loading photos
    //TODO needs to be faster
    private void initPhotos(){
        photos = new ArrayList<>();
        for(FoodItem f : items){
            FoodItem foodItem = f;
            Uri uri = Uri.fromFile(new File(foodItem.getPhotoId()));
            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(uri.getPath()), 450, 450);
            photos.add(ThumbImage);
        }
    }

    //returns a list of items that has photos
    private List<FoodItem> getCurrentItems(){
        DaoSession daoSession = DataManager.getInstance().getDaoSession();
        FoodItemDao foodItemDao = daoSession.getFoodItemDao();
        QueryBuilder queryBuilder = foodItemDao.queryBuilder()
                .where(FoodItemDao.Properties.Date.eq(currentDate));
        List<FoodItem> list = queryBuilder.list();
        for(FoodItem f : list){
            if(f.getPhotoId() == "") {
                list.remove(f);
            }
        }

        return list;
    }
}
