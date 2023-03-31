package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RCAdapterFolder extends RecyclerView.Adapter<RCAdapterFolder.RCFolderViewHolder> {
    private String log_tag = getClass().getName();
    private Context applicationContext;

    Context context;
    ArrayList<RCModelFolder> folderArrayList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;


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
            Fragment addFolderFragment = new AddFolderFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("folder_id", rcItemFolder.getId());
            bundle.putBoolean("isEdit", true);

            addFolderFragment.setArguments(bundle);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout,  addFolderFragment).commit();

            return true;
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

//    private void sortPasswordsByFolderTitle(String folderTitle) {
//        String query = "select * from " + databaseHelper.PASSWORD_NOTE_TABLE + " where "+databaseHelper.PN + " = "+ String.valueOf(Id);
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
