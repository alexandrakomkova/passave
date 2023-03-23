package by.komkova.fit.bstu.passave;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<RCModel> modelArrayList;
    RCAdapter rcAdapter;

    String title[] = new String[]{
            "apple", "banana"
    };

    int image[] = new int[]{
            R.drawable.home_icon, R.drawable.add_password_icon
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));

        return view;
    }
}