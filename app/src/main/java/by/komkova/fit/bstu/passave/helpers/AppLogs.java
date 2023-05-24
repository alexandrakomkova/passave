package by.komkova.fit.bstu.passave.helpers;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class AppLogs {

    public static void log(Context context, String log_tag, String error_log) {
        Toast.makeText(context, error_log, Toast.LENGTH_SHORT).show();
        Log.d(log_tag, error_log);
    }
}
