package by.komkova.fit.bstu.passave.ui.adapters;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.db.DatabaseHelper;
import by.komkova.fit.bstu.passave.helpers.AppLogs;
import by.komkova.fit.bstu.passave.ui.activities.MainActivity;
import by.komkova.fit.bstu.passave.ui.fragments.DetailsFolderFragment;
import by.komkova.fit.bstu.passave.ui.fragments.SortedPasswordsFragment;
import by.komkova.fit.bstu.passave.ui.models.RCModelFolder;
import by.komkova.fit.bstu.passave.ui.models.RCModelPassword;

public class RCAdapterFolder extends RecyclerView.Adapter<RCAdapterFolder.RCFolderViewHolder> {
    private String log_tag = getClass().getName();
    private Context applicationContext;

    Context context;
    ArrayList<RCModelFolder> folderArrayList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    // RCAdapter rcAdapter;
    ArrayList<RCModelPassword> modelArrayList;
    // private RecyclerView.LayoutManager layoutManager;


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
        // layoutManager = new LinearLayoutManager(applicationContext);

        return new RCAdapterFolder.RCFolderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCFolderViewHolder holder, int position) {
        RCModelFolder rcItem = folderArrayList.get(position);
        holder.rc_folder_title.setText(rcItem.getFolderTitle());

        Bundle bundleSortBy = new Bundle();
        if (bundleSortBy.getBoolean("isSortByFolder") || bundleSortBy.getBoolean("isSortByFavourite") ) {
            holder.rc_cardView.setBackgroundResource(R.drawable.round_back_white_folder_10_40_border);
        }
        // Log.d(log_tag, rcItem.getFolderTitle());

        int pos = holder.getAdapterPosition();

        holder.itemView.setOnLongClickListener(view -> {
            RCModelFolder rcItemFolder = folderArrayList.get(position);
            if(rcItemFolder.getFolderTitle().toLowerCase().equals("favourite")) {
                AppLogs.log(context, log_tag, context.getResources().getString(R.string.favourite_can_not_delete));
            } else {
                MainActivity activity = (MainActivity) view.getContext();
                Fragment detailsFolderFragment = new DetailsFolderFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("folder_id", rcItemFolder.getId());

                detailsFolderFragment.setArguments(bundle);
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout,  detailsFolderFragment).commit();
            }

            return true;
        });

        holder.itemView.setOnClickListener(view -> {
            RCModelFolder rcItemFolder = folderArrayList.get(position);

//            modelArrayList = new ArrayList<RCModelPassword>();
//            databaseHelper = new DatabaseHelper(applicationContext);
//            db = databaseHelper.getReadableDatabase();

            MainActivity activity = (MainActivity) view.getContext();
            Fragment sortedPasswordsFragment = new SortedPasswordsFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("folder_id", rcItemFolder.getId());
            bundle.putString("folder_name", rcItemFolder.getFolderTitle());

            sortedPasswordsFragment.setArguments(bundle);
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.fragment_layout,  sortedPasswordsFragment).commit();




            // old variant of sorting
//            MainActivity activity = (MainActivity) view.getContext();
//            Fragment passwordNotesFragment = new PasswordNotesFragment();
//            // Bundle bundle = new Bundle();
//
//
//            if (rcItemFolder.getFolderTitle().equals("Favourite")) {
//                bundleSortBy.putBoolean("isSortByFavourite", true);
//            } else  {
//                bundleSortBy.putBoolean("isSortByFolder", true);
//                bundleSortBy.putInt("folder_id", rcItemFolder.getId());
//                bundleSortBy.putString("folder_name", rcItemFolder.getFolderTitle());
//            }
//
//            passwordNotesFragment.setArguments(bundleSortBy);
//            FragmentManager fragmentManager = activity.getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.fragment_layout,  passwordNotesFragment).commit();


        });

//        if (bundleSortBy.getBoolean("isSortByFolder") || bundleSortBy.getBoolean("isSortByFavourite") ) {
//            holder.rc_cardView.setBackgroundResource(R.drawable.round_back_white_folder_10_40_border);
//        }
//        else {
//            holder.rc_cardView.setBackgroundResource(R.drawable.round_back_white_folder_10_40);
//        }
    }

    @Override
    public int getItemCount() { return folderArrayList.size(); }

    public static class RCFolderViewHolder extends RecyclerView.ViewHolder {
        TextView rc_folder_title;
        ConstraintLayout rc_cardView;

        public RCFolderViewHolder(@NonNull View itemView) {
            super(itemView);

            rc_folder_title = itemView.findViewById(R.id.folderTitleTextview);
            rc_cardView =  itemView.findViewById(R.id.cardView);
        }
    }


}
