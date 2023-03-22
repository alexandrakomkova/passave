package by.komkova.fit.bstu.passave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RCAdapter extends RecyclerView.Adapter<RCAdapter.RCViewHolder> {

    Context context;
    ArrayList<RCModel> modelArrayList;

    public RCAdapter(Context context, ArrayList<RCModel> modelArrayList) {
        this.context = context;
        this.modelArrayList = modelArrayList;
    }

    @NonNull
    @Override
    public RCViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rc_item, parent, false);

        return new RCViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RCViewHolder holder, int position) {
        RCModel rcModel = modelArrayList.get(position);
        holder.rc_title.setText(rcModel.title);
        holder.rc_image.setImageResource(rcModel.image);
    }

    @Override
    public int getItemCount() {
        return modelArrayList.size();
    }

    public class RCViewHolder extends RecyclerView.ViewHolder {
        ImageView rc_image;
        TextView rc_title;

        public RCViewHolder(@NonNull View itemView) {
            super(itemView);

            rc_image = itemView.findViewById(R.id.rc_image);
            rc_title = itemView.findViewById(R.id.rc_title);
        }
    }
}
