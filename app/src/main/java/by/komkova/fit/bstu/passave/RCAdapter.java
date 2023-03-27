package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RCAdapter extends RecyclerView.Adapter<RCAdapter.RCViewHolder> {

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

        return new RCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCViewHolder holder, int position) {
        RCModel rcModel = modelArrayList.get(position);
        holder.rc_title.setText(rcModel.getTitle());
        holder.rc_lastDate.setText(rcModel.getLastUpdateDate());

        int pos = holder.getAdapterPosition();

        holder.rc_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final RCModel rcItem = modelArrayList.get(pos);
                final int Id = rcItem.getId();
                databaseHelper = new DatabaseHelper(context);
                db = databaseHelper.getWritableDatabase();
                PopupMenu menu = new PopupMenu(context, holder.rc_more);

                menu.inflate(R.menu.card_menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.delete:
                                db.delete(DatabaseHelper.PASSWORD_NOTE_TABLE,DatabaseHelper.PN_COLUMN_ID + " = " + Id,null);
                                notifyItemRangeChanged(pos,modelArrayList.size());
                                modelArrayList.remove(pos);
                                notifyItemRemoved(pos);
                                db.close();
                                break;
                            case R.id.update:
//                                Intent intent = new Intent(context, UpdateActivity.class);
//                                intent.putExtra("USERID", userId);
//                                context.startActivity(intent);
                                break;
                        }
                        return false;
                    }
                });
                menu.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class RCViewHolder extends RecyclerView.ViewHolder {
        TextView rc_title;
        TextView rc_lastDate;
        ImageView rc_more;
        ImageView rc_like;

        public RCViewHolder(@NonNull View itemView) {
            super(itemView);

            rc_title = itemView.findViewById(R.id.titleTextView);
            rc_lastDate = itemView.findViewById(R.id.lastDateUpdateTextView);
            rc_more = itemView.findViewById(R.id.more);
            rc_like = itemView.findViewById(R.id.likedImageView);
        }
    }
}
