package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RCAdapterFolder extends RecyclerView.Adapter<RCAdapterFolder.RCFolderViewHolder> {
    private String log_tag = getClass().getName();
    private Context applicationContext;

    Context context;
    ArrayList<RCModelFolder> folderArrayList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    RCAdapter rcAdapter;
    ArrayList<RCModel> modelArrayList;
    private RecyclerView.LayoutManager layoutManager;


    public RCAdapterFolder(Context context, ArrayList<RCModelFolder> folderArrayList) {
        this.context = context;
        this.folderArrayList = folderArrayList;
    }

    @NonNull
    @Override
    public RCFolderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rc_item_folder, parent, false);
        applicationContext = MainActivity.getContextOfApplication();
        layoutManager = new LinearLayoutManager(applicationContext);

        return new RCAdapterFolder.RCFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCFolderViewHolder holder, int position) {
        RCModelFolder rcItem = folderArrayList.get(position);
        holder.rc_folder_title.setText(rcItem.getFolderTitle());
        // Log.d(log_tag, rcItem.getFolderTitle());

        int pos = holder.getAdapterPosition();

        holder.itemView.setOnLongClickListener(view -> {
            RCModelFolder rcItemFolder = folderArrayList.get(position);

            MainActivity activity = (MainActivity) view.getContext();
            Fragment detailsFolderFragment = new DetailsFolderFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("folder_id", rcItemFolder.getId());

            detailsFolderFragment.setArguments(bundle);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout,  detailsFolderFragment).commit();

            return true;
        });

        holder.itemView.setOnClickListener(view -> {
            RCModelFolder rcItemFolder = folderArrayList.get(position);

            modelArrayList = new ArrayList<RCModel>();
            databaseHelper = new DatabaseHelper(applicationContext);
            db = databaseHelper.getReadableDatabase();

            if (rcItemFolder.getFolderTitle().equals("Favourite")) {
                sortFavouritePasswordNotes();
                // AppLogs.log(applicationContext, log_tag, "tap fav");
            }

        });
    }

    @Override
    public int getItemCount() { return folderArrayList.size(); }

    public static class RCFolderViewHolder extends RecyclerView.ViewHolder {
        TextView rc_folder_title;

        public RCFolderViewHolder(@NonNull View itemView) {
            super(itemView);

            rc_folder_title = itemView.findViewById(R.id.folderTitleTextview);
        }
    }

    private void sortFavouritePasswordNotes() {
        String query = "select * from " + databaseHelper.PASSWORD_NOTE_TABLE
                + " where " + databaseHelper.PN_COLUMN_FAVOURITE+ " = 1";

        Cursor cursor= null;
        if(db !=null)
        {
            cursor = db.rawQuery(query, null);
        }
        cursor.moveToFirst();

        if (cursor != null && cursor.getCount() != 0) {
            modelArrayList.clear();
            while (cursor.moveToNext()) {
                RCModel rcItem = new RCModel();
                rcItem.setId(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_ID)));
                rcItem.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_SERVICE_NAME)));
                rcItem.setLogin(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_LOGIN)));
                rcItem.setLastUpdateDate(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_UPDATED)));
                rcItem.setFavourite(cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.PN_COLUMN_FAVOURITE)));
                modelArrayList.add(rcItem);
            }
        }
        cursor.close();

        Fragment homeFragment = new PasswordNotesFragment();
        rcAdapter = new RCAdapter(applicationContext, modelArrayList);

        ((PasswordNotesFragment) homeFragment).recyclerView.setLayoutManager(layoutManager);
        ((PasswordNotesFragment) homeFragment).recyclerView.setAdapter(rcAdapter);
    }


}
