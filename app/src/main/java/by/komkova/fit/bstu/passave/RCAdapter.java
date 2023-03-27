package by.komkova.fit.bstu.passave;

import static by.komkova.fit.bstu.passave.PasswordNoteProvider.PASSWORD_NOTE_URI;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RCAdapter extends RecyclerView.Adapter<RCAdapter.RCViewHolder> {

    private String log_tag = getClass().getName();
    private Context applicationContext;

    Context context;
    ArrayList<RCModel> modelArrayList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    public RCAdapter(Context context, ArrayList<RCModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
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
        RCModel rcModel = modelArrayList.get(position);
        holder.rc_title.setText(rcModel.getTitle());
        holder.rc_lastDate.setText(rcModel.getLastUpdateDate());

        if (rcModel.getFavourite() > 0) {
            holder.rc_favourite.setImageResource(R.drawable.star_icon);
        }

        int pos = holder.getAdapterPosition();
        holder.rc_favourite.setOnClickListener(view -> {
            final RCModel rcItem = modelArrayList.get(pos);
            final int Id = rcItem.getId();
            final int favourite = rcItem.getFavourite();
            if (favourite == 0) {
                updateFavouriteStatus(Id, 1);
            } else { updateFavouriteStatus(Id, 0); }

            notifyDataSetChanged();
        });

//        holder.rc_more.setOnClickListener(view -> {
//            final RCModel rcItem = modelArrayList.get(pos);
//            final int Id = rcItem.getId();
//            databaseHelper = new DatabaseHelper(context);
//            db = databaseHelper.getWritableDatabase();
//            PopupMenu menu = new PopupMenu(context, holder.rc_more);
//
//            menu.inflate(R.menu.card_menu);
//            menu.setOnMenuItemClickListener(item -> {
//                switch (item.getItemId()) {
//                    case R.id.delete:
//                        db.delete(DatabaseHelper.PASSWORD_NOTE_TABLE,DatabaseHelper.PN_COLUMN_ID + " = " + Id,null);
//                        notifyItemRangeChanged(pos,modelArrayList.size());
//                        modelArrayList.remove(pos);
//                        notifyItemRemoved(pos);
//                        db.close();
//                        break;
//                    case R.id.update:
//                        FragmentTransaction fragmentTransaction = getActivity()
//                                .getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.fragment_layout, new SecurityFragment());
//                        fragmentTransaction.commit();
//                        break;
//                }
//                return false;
//            });
//            menu.show();
       // });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public static class RCViewHolder extends RecyclerView.ViewHolder {
        TextView rc_title;
        TextView rc_lastDate;
        // ImageView rc_more;
        ImageView rc_favourite;

        public RCViewHolder(@NonNull View itemView) {
            super(itemView);

            rc_title = itemView.findViewById(R.id.titleTextView);
            rc_lastDate = itemView.findViewById(R.id.lastDateUpdateTextView);
            // rc_more = itemView.findViewById(R.id.more);
            rc_favourite = itemView.findViewById(R.id.likedImageView);
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
}
