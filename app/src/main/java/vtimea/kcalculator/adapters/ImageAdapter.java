package vtimea.kcalculator.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
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
    private GridView gridView;
    private static List<FoodItem> items = new ArrayList<>();
    private static List<Bitmap> photos = new ArrayList<>();

    public ImageAdapter(Context c, long date, GridView gridView) {
        this.gridView = gridView;
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
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else {
            imageView = (ImageView) view;
        }

        if(photos.size()-1 >= i)
            imageView.setImageBitmap(photos.get(i));

        return imageView;
    }

    //loading photos
    private void initPhotos(){
        photos = new ArrayList<>();
        for(FoodItem f : items){
            FoodItem foodItem = f;
            Uri uri = Uri.fromFile(new File(foodItem.getPhotoLocation()));
            Bitmap bitmap = BitmapFactory.decodeFile(uri.getPath());
            if(bitmap == null)
                continue;

            Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bitmap, 450, 450);
            photos.add(ThumbImage);

        }
    }

    //returns the items that have a photo
    private List<FoodItem> getCurrentItems(){
        DaoSession daoSession = DataManager.getInstance().getDaoSession();
        FoodItemDao foodItemDao = daoSession.getFoodItemDao();
        QueryBuilder queryBuilder = foodItemDao.queryBuilder()
                .where(FoodItemDao.Properties.Date.eq(currentDate), FoodItemDao.Properties.PhotoLocation.notEq(""));
        List<FoodItem> list = queryBuilder.list();
        return list;
    }

    public boolean isEmpty(){
        return photos.size() == 0 ? true : false;
    }
}
