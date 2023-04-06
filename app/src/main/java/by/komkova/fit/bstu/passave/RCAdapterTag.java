package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RCAdapterTag extends RecyclerView.Adapter<RCAdapterTag.RCTagViewHolder>{

    private String log_tag = getClass().getName();
    private Context applicationContext;

    Context context;
    ArrayList<RCModelTag> tagArrayList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    public RCAdapterTag(Context context, ArrayList<RCModelTag> tagArrayList) {
        this.context = context;
        this.tagArrayList = tagArrayList;
    }

    @NonNull
    @Override
    public RCAdapterTag.RCTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rc_item_tag, parent, false);
        applicationContext = MainActivity.getContextOfApplication();

        return new RCAdapterTag.RCTagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCAdapterTag.RCTagViewHolder holder, int position) {
        RCModelTag rcModelTag = tagArrayList.get(position);
        holder.rc_tag_name.setText(rcModelTag.getTagName());

        holder.itemView.setOnClickListener(view -> {
            RCModelTag rcItemTag = tagArrayList.get(position);

//            MainActivity activity = (MainActivity) view.getContext();
//            Fragment detailsTagFragment = new DetailsNoteFragment();
//            Bundle bundle = new Bundle();
//            bundle.putInt("note_id", rcItemNote.getId());
//
//            detailsNoteFragment.setArguments(bundle);
//            FragmentManager fragmentManager = activity.getSupportFragmentManager();
//            fragmentManager.beginTransaction().replace(R.id.fragment_layout,  detailsNoteFragment).commit();
        });
    }

    @Override
    public int getItemCount() { return tagArrayList.size(); }

    public static class RCTagViewHolder extends RecyclerView.ViewHolder {
        TextView rc_tag_name;

        public RCTagViewHolder(@NonNull View itemView) {
            super(itemView);

            rc_tag_name = itemView.findViewById(R.id.tagNameTextView);
        }
    }
}
