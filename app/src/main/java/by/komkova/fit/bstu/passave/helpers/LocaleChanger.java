package by.komkova.fit.bstu.passave.helpers;

import android.content.Context;
import android.content.res.Configuration;

import java.util.Locale;

public class LocaleChanger {

    public static void changeLocale(String languageCode, Context context)
    {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration, null);

    }
}
