package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.Random;

public class RCAdapter extends RecyclerView.Adapter<RCAdapter.RCViewHolder> implements Filterable {

    private String log_tag = getClass().getName();
    private Context applicationContext;

    Context context;
    ArrayList<RCModel> modelArrayList;
    ArrayList<RCModel> filteredModelArrayList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    public RCAdapter(Context context, ArrayList<RCModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
        this.filteredModelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public RCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rc_item_password_note, parent, false);
        applicationContext = MainActivity.getContextOfApplication();

        return new RCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCViewHolder holder, int position) {
        RCModel rcModel = filteredModelArrayList.get(position);
        holder.rc_title.setText(rcModel.getTitle());
        holder.rc_lastDate.setText(rcModel.getLastUpdateDate());
        holder.rc_login.setText(rcModel.getLogin());

        Random mRandom = new Random();
        int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.rc_firstLetter.getBackground()).setColor(color);
        holder.rc_firstLetter.setText(rcModel.getTitle().substring(0, 1));

        int pos = holder.getAdapterPosition();
        if(rcModel.getFavourite() == 1) {
            holder.rc_favourite.setChecked(true);
        } else {
            holder.rc_favourite.setChecked(false);
        }

//        if (rcModel.getFavourite() == 2) {
//            holder.rc_favourite.setImageResource(R.drawable.star_icon);
//        } else {
//            holder.rc_favourite.setImageResource(R.drawable.star_border_icon);
//        }

//        holder.rc_favourite.setOnCheckedChangeListener(new View.OnClickListener() {
//            final RCModel rcItem = filteredModelArrayList.get(pos);
//            final int Id = rcItem.getId();
//            final int favourite = rcItem.getFavourite();
//            CheckBox likedCheckBox = view.findViewById(R.id.likedCheckBox);
//
////            if (favourite == 2) {
////                updateFavouriteStatus(Id, 0, 1);
////                likedImageView.setImageResource(R.drawable.star_border_icon);
////                AppLogs.log(applicationContext, log_tag, "set as not favourite");
////            } else {
////                updateFavouriteStatus(Id, 1, 2); // 'favourite' folder id
////                likedImageView.setImageResource(R.drawable.star_icon);
////                AppLogs.log(applicationContext, log_tag, "set as favourite");
////            }  // 'no folder' folder id
//
//            notifyItemChanged(Id);
//        });


//        holder.rc_favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//
//            }
//        });

        holder.rc_favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            final RCModel rcItem = filteredModelArrayList.get(pos);
            final int Id = rcItem.getId();
            final int favourite = rcItem.getFavourite();

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    AppLogs.log(applicationContext, log_tag, "set as favourite");
                    updateFavouriteStatus(Id, 0, 1);
                } else {
                    AppLogs.log(applicationContext, log_tag, "set as not favourite");
                    updateFavouriteStatus(Id, 1, 2); // 'favourite' folder id
                }
            }
        });



        holder.itemView.setOnClickListener(view -> {
            final RCModel rcItem = filteredModelArrayList.get(pos);

            MainActivity activity = (MainActivity) view.getContext();
            Fragment detailsFragment = new DetailsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("password_note_id", rcItem.getId());

            detailsFragment.setArguments(bundle);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout,  detailsFragment).commit();

            // activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, detailsFragment).addToBackStack(null).commit();
        });
    }

    @Override
    public int getItemCount() {
        return filteredModelArrayList.size();
    }

    public static class RCViewHolder extends RecyclerView.ViewHolder {
        TextView rc_firstLetter;
        TextView rc_title;
        TextView rc_login;
        TextView rc_lastDate;
        CheckBox rc_favourite;
        // ImageView rc_more;

        public RCViewHolder(@NonNull View itemView) {
            super(itemView);

            rc_firstLetter = itemView.findViewById(R.id.firstLetter);
            rc_title = itemView.findViewById(R.id.titleTextView);
            rc_login = itemView.findViewById(R.id.loginTextView);
            rc_lastDate = itemView.findViewById(R.id.lastDateUpdateTextView);
            rc_favourite = (CheckBox) itemView.findViewById(R.id.likedCheckBox);
            // rc_more = itemView.findViewById(R.id.more);
        }
    }

    public void updateFavouriteStatus(Integer Id, Integer favourite, Integer folderId) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(DatabaseHelper.PN_COLUMN_FAVOURITE, favourite);
            cv.put(DatabaseHelper.PN_COLUMN_FOLDER_ID, folderId);

            Uri uri = ContentUris.withAppendedId(PASSWORD_NOTE_URI, Id);
            int rowCount = applicationContext.getContentResolver().update(uri, cv, null, null);
            Log.d(log_tag, "updated");


        } catch (Exception e){
            Log.d(log_tag, "error: " + e.getMessage());
        }
    }

    @Override
    public Filter getFilter() {

        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                String searchString = charSequence.toString();

                if (searchString.isEmpty()) {

                    filteredModelArrayList = modelArrayList;

                } else {

                    ArrayList<RCModel> tempFilteredList = new ArrayList<RCModel>();

                    for (RCModel rcItem : modelArrayList) {

                        // search for user title
                        if (rcItem.getTitle().toLowerCase().contains(searchString)) {

                            tempFilteredList.add(rcItem);
                        }
                    }

                    filteredModelArrayList = tempFilteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = filteredModelArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                filteredModelArrayList = (ArrayList<RCModel>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

//    public void sortPasswordsByFolderTitle(String folderTitle) {
//        String query = "select * from " + databaseHelper.PASSWORD_NOTE_TABLE + " where "+databaseHelper.PN_COLUMN_FOLDER_ID + " = "+ String.valueOf(Id);
//        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//
//        Cursor cursor= null;
//        if(db !=null)
//        {
//            cursor = db.rawQuery(query, null);
//        }
//        cursor.moveToFirst();
//
//        if(cursor!=null && cursor.getCount()!=0) {
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//
//                enter_service_title_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_SERVICE_NAME)));
//                enter_login_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)));
//                enter_details_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_DESCRIPTION)));
//                enter_password_tiet.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_PASSWORD)));
//
//                cursor.moveToNext();
//            }
//
//            cursor.close();
//        }
//    }
}
