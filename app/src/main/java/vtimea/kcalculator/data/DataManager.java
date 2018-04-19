package vtimea.kcalculator.data;

import android.content.Context;
import android.util.Log;

import org.greenrobot.greendao.database.Database;
import java.util.List;


public class DataManager {
    private static DataManager dataManager;
    private static Database database;
    private static DaoSession daoSession;
    private static List<FoodItem> foodItems;

    public static void initInstance(Context context){
        if (dataManager == null){
            dataManager = new DataManager();

            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "items-db", null);
            database = helper.getWritableDb();
            daoSession = new DaoMaster(database).newSession();
            //daoSession.deleteAll(FoodItem.class);
            foodItems = daoSession.queryBuilder(FoodItem.class).build().list();
        }
    }

    public static DataManager getInstance(){ return dataManager; }

    public Database getDatabase(){ return database; }

    public DaoSession getDaoSession(){ return daoSession; }

    public List<FoodItem> getTaskList(){ return foodItems; }

    public void updateTaskList(){
        foodItems = daoSession.queryBuilder(FoodItem.class).build().list();
    }
}
