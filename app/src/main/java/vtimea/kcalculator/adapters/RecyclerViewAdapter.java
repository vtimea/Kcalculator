package vtimea.kcalculator.adapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.greenrobot.greendao.query.QueryBuilder;

import java.util.Date;
import java.util.List;

import vtimea.kcalculator.R;
import vtimea.kcalculator.data.DaoSession;
import vtimea.kcalculator.data.DataManager;
import vtimea.kcalculator.data.FoodItem;
import vtimea.kcalculator.data.FoodItemDao;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{
    private Date currentDate;

    public class ViewHolder extends RecyclerView.ViewHolder{
        CardView cv;
        TextView tvItemName;
        TextView tvItemCals;
        View view;
        public ViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.card_view);
            tvItemName = (TextView) itemView.findViewById(R.id.tvItemName);
            tvItemCals = (TextView) itemView.findViewById(R.id.tvItemCals);
            view = itemView;

            Log.i("REC", "View holder konstruktor!");
        }
    }

    public RecyclerViewAdapter(long date){
        currentDate = new Date(date);
        Log.i("REC", "RecyclerViewAdapter konstruktor - Date: " + currentDate.toString());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvItemName.setText(getCurrentItems().get(position).getDescription());
        holder.tvItemCals.setText(new Integer(getCurrentItems().get(position).getCals()).toString());
        Log.i("REC", "onBindViewHolder - Name: " + holder.tvItemName.getText().toString());
        Log.i("REC", "onBindViewHolder - Cals: " + holder.tvItemCals.getText().toString());
    }

    @Override
    public int getItemCount() {
        Log.i("REC", "ItemCount: " + getCurrentItems().size());
        return getCurrentItems().size();
    }

    private List<FoodItem> getCurrentItems(){
        DaoSession daoSession = DataManager.getInstance().getDaoSession();
        FoodItemDao foodItemDao = daoSession.getFoodItemDao();
        QueryBuilder queryBuilder = foodItemDao.queryBuilder()
                .where(FoodItemDao.Properties.Date.eq(currentDate));
        List<FoodItem> list = queryBuilder.list();

        return list;
    }

}
