package by.komkova.fit.bstu.passave;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import androidx.constraintlayout.widget.ConstraintLayout;

public class CustomAlertDialogClass {

    public static void showWarningDialog(View view, Context applicationContext) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.errorLayout);
        View v = LayoutInflater.from(applicationContext).inflate(R.layout.error_dialog, constraintLayout);
        Button errorClose = v.findViewById(R.id.errorCloseButton);

        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        errorClose.findViewById(R.id.errorCloseButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }
}
