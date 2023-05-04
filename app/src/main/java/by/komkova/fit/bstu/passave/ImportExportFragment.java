package by.komkova.fit.bstu.passave;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class ImportExportFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_import_export, container, false);

        Button import_file_btn = view.findViewById(R.id.import_file_btn);
        import_file_btn.setOnClickListener(this::importDatabase);

        Button export_file_btn = view.findViewById(R.id.export_file_btn);
        export_file_btn.setOnClickListener(this::exportDatabase);

        return view;
    }

    private void importDatabase(View v) {}

    private void exportDatabase(View v) {}
}