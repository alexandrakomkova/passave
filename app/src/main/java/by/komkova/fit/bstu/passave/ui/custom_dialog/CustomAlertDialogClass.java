package by.komkova.fit.bstu.passave.ui.custom_dialog;

import android.app.AlertDialog;
// import android.support.v7.app.AlertDialog.Builder;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import by.komkova.fit.bstu.passave.R;
import by.komkova.fit.bstu.passave.helpers.AppLogs;

public class CustomAlertDialogClass {

    public static void showWarningOkDialog(View view, Context applicationContext, int errorDesc) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.errorLayout);
        View v = LayoutInflater.from(applicationContext).inflate(R.layout.error_ok_dialog, constraintLayout);
        Button errorOkay = v.findViewById(R.id.errorOkayButton);

        TextView errorDescription = v.findViewById(R.id.errorDescription);
        errorDescription.setText(errorDesc);

        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        errorOkay.findViewById(R.id.errorOkayButton).setOnClickListener(new View.OnClickListener() {

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

    public static void showWarningOkCancelDialog(View view, Context applicationContext, int errorDesc, Runnable f1, int Id) {
        ConstraintLayout constraintLayout = view.findViewById(R.id.errorLayout);
        View v = LayoutInflater.from(applicationContext).inflate(R.layout.error_ok_cancel_dialog, constraintLayout);
        Button errorClose = v.findViewById(R.id.errorCloseButton);
        Button errorOkay = v.findViewById(R.id.errorOkayButton);

        TextView errorDescription = v.findViewById(R.id.errorDescription);
        errorDescription.setText(R.string.folder_delete);

        AlertDialog.Builder builder = new AlertDialog.Builder(applicationContext);
        builder.setView(v);
        final AlertDialog alertDialog = builder.create();

        errorClose.findViewById(R.id.errorCloseButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        errorOkay.findViewById(R.id.errorOkayButton).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                f1.run();
            }
        });

        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        }
        alertDialog.show();
    }



}
