package by.komkova.fit.bstu.passave.ui.adapters;

import static by.komkova.fit.bstu.passave.db.providers.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;
import by.komkova.fit.bstu.passave.ui.activities.MainActivity;
import by.komkova.fit.bstu.passave.ui.fragments.DetailsPasswordFragment;
import by.komkova.fit.bstu.passave.ui.models.RCModelPassword;

public class RCAdapterPassword extends RecyclerView.Adapter<RCAdapterPassword.RCViewHolder> implements Filterable {

    private String log_tag = getClass().getName();
    private Context applicationContext;

    Context context;
    ArrayList<RCModelPassword> modelArrayList;
    ArrayList<RCModelPassword> filteredModelArrayList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    public RCAdapterPassword(Context context, ArrayList<RCModelPassword> modelArrayList) {
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
        RCModelPassword rcModel = filteredModelArrayList.get(position);
        holder.rc_title.setText(rcModel.getTitle());
        holder.rc_lastDate.setText(rcModel.getLastUpdateDate());
        holder.rc_login.setText(rcModel.getLogin());
        // AppLogs.log(applicationContext, log_tag, rcModel.getLogin());

        Random mRandom = new Random();
        int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.rc_firstLetter.getBackground()).setColor(color);
        holder.rc_firstLetter.setText(rcModel.getTitle().substring(0, 1));

        int pos = holder.getAdapterPosition();

        holder.rc_favourite.setChecked(rcModel.getFavourite() == 1); // tut cheta pomenyala

        holder.rc_favourite.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){

            final RCModelPassword rcItem = filteredModelArrayList.get(pos);
            final int Id = rcItem.getId();
            final int favourite = rcItem.getFavourite();

            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    AppLogs.log(applicationContext, log_tag, context.getResources().getString(R.string.set_fav));
                    updateFavouriteStatus(Id, 1); // 'favourite' id
                } else {
                    AppLogs.log(applicationContext, log_tag, context.getResources().getString(R.string.set_not_fav));
                    updateFavouriteStatus(Id, 0); // 'not favourite' id
                }
            }
        });

        holder.itemView.setOnClickListener(view -> {
            final RCModelPassword rcItem = filteredModelArrayList.get(pos);

            MainActivity activity = (MainActivity) view.getContext();
            Fragment detailsFragment = new DetailsPasswordFragment();
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

    public void updateFavouriteStatus(Integer Id, Integer favourite) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(DatabaseHelper.PN_COLUMN_FAVOURITE, favourite);

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

                    ArrayList<RCModelPassword> tempFilteredList = new ArrayList<RCModelPassword>();

                    for (RCModelPassword rcItem : modelArrayList) {

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
                filteredModelArrayList = (ArrayList<RCModelPassword>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
