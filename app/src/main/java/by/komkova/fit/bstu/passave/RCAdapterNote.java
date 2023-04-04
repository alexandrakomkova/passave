package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RCAdapterNote extends RecyclerView.Adapter<RCAdapterNote.RCNoteViewHolder>{
    private String log_tag = getClass().getName();
    private Context applicationContext;

    Context context;
    ArrayList<RCModelNote> noteArrayList;
    DatabaseHelper databaseHelper;
    SQLiteDatabase db;

    public RCAdapterNote(Context context, ArrayList<RCModelNote> noteArrayList) {
        this.context = context;
        this.noteArrayList = noteArrayList;
    }

    @NonNull
    @Override
    public RCAdapterNote.RCNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rc_item_note, parent, false);
        applicationContext = MainActivity.getContextOfApplication();

        return new RCAdapterNote.RCNoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCAdapterNote.RCNoteViewHolder holder, int position) {
        RCModelNote rcModel = noteArrayList.get(position);
        holder.rc_note_text.setText(rcModel.getNoteText());
    }

    @Override
    public int getItemCount() {
        return noteArrayList.size();
    }

    public static class RCNoteViewHolder extends RecyclerView.ViewHolder {
        TextView rc_note_text;

        public RCNoteViewHolder(@NonNull View itemView) {
            super(itemView);

            rc_note_text = itemView.findViewById(R.id.noteTextView);
        }
    }
}
