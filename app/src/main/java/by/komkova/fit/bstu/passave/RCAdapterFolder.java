package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
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

}
