package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Random;

public class RCAdapterTag extends RecyclerView.Adapter<RCAdapterTag.RCTagViewHolder> {

    private String log_tag = getClass().getName();
    private Context applicationContext;

    private final Context context;
    ArrayList<RCModelTag> tagArrayList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    private Context contextIntent;

    public RCAdapterTag(Context context, ArrayList<RCModelTag> tagArrayList) {
        this.context = context;
        this.tagArrayList = tagArrayList;
    }

    @NonNull
    @Override
    public RCAdapterTag.RCTagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rc_item_tag, parent, false);
        applicationContext = TagActivity.getContextOfApplication();

        return new RCAdapterTag.RCTagViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCAdapterTag.RCTagViewHolder holder, int position) {
        int pos = holder.getAdapterPosition();
        RCModelTag rcModelTag = tagArrayList.get(pos);
        holder.rc_tag_name.setText(rcModelTag.getTagName());

        Random mRandom = new Random();
        int color = Color.argb(255, mRandom.nextInt(256), mRandom.nextInt(256), mRandom.nextInt(256));
        ((GradientDrawable) holder.rc_tag_name.getBackground()).setColor(color);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                RCModelTag rcItemTag = tagArrayList.get(pos);

                TagActivity activity = (TagActivity) view.getContext();
                Fragment detailsTagFragment = new DetailsTagFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("tag_id", rcItemTag.getId());
                bundle.putString("tag_name", rcItemTag.getTagName());

                detailsTagFragment.setArguments(bundle);
                FragmentManager fragmentManager = activity.getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.fragment_layout,  detailsTagFragment).commit();
                return false;
            }
        });

        holder.itemView.setOnClickListener(view -> {
            RCModelTag rcItemTag = tagArrayList.get(pos);

            Intent intent = new Intent(applicationContext, MainActivity.class);
            intent.putExtra("tag_id", rcItemTag.getId());
            contextIntent.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() { return tagArrayList.size(); }

    public class RCTagViewHolder extends RecyclerView.ViewHolder {
        TextView rc_tag_name;

        public RCTagViewHolder(@NonNull View itemView) {
            super(itemView);
            contextIntent = itemView.getContext();

            rc_tag_name = itemView.findViewById(R.id.tagNameTextView);
        }
    }
}
