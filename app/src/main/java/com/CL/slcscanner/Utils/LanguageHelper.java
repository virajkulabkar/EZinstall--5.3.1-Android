package com.CL.slcscanner.Utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;
import android.preference.PreferenceManager;

import java.util.Locale;

/**
 * Created by Vinay on 29/5/17.
 */

public class LanguageHelper {

    private static final String SELECTED_LANGUAGE = "Language.Helper.Selected.Language";

    // returns Context having application default locale for all activities
    public static Context onAttach(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        return setLanguage(context, lang);
    }

    // sets application locale with default locale persisted in preference manager on each new launch of application and
    // returns Context having application default locale
    public static Context onAttach(Context context, String defaultLanguage) {
        String lang = getPersistedData(context, defaultLanguage);
        //return  wrap(context, lang);
        return setLanguage(context, lang);
    }

    // returns language code persisted in preference manager
    public static String getLanguage(Context context) {
        return getPersistedData(context, Locale.getDefault().getLanguage());
    }

    // persists new language code change in preference manager and updates application default locale
    // returns Context having application default locale
    public static Context setLanguage(Context context, String language) {
        persist(context, language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        return updateResourcesLegacy(context, language);
    }

    // returns language code persisted in preference manager
    public static String getPersistedData(Context context, String defaultLanguage) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage);
    }

    // persists new language code in preference manager
    private static void persist(Context context, String language) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(SELECTED_LANGUAGE, language);
        editor.apply();
    }

    // For android device versions above Nougat (7.0)
    // updates application default locale configurations and
    // returns new Context object for the current Context but whose resources are adjusted to match the given Configuration
    @TargetApi(Build.VERSION_CODES.N)
    private static Context updateResources(Context context, String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        configuration.setLayoutDirection(locale);

        return context.createConfigurationContext(configuration);
    }


    public static Context setApplicationLanguage(Context context) {
        String lang = getPersistedData(context, Locale.getDefault().getLanguage());
        persist(context, lang);
        Locale newLocale = new Locale(lang);
        Locale.setDefault(newLocale);
        Resources activityRes = context.getResources();
        Configuration configuration = activityRes.getConfiguration();

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);
            activityRes.updateConfiguration(configuration, activityRes.getDisplayMetrics());
        }
        else{
            configuration.locale = newLocale;
            activityRes.updateConfiguration(configuration, activityRes.getDisplayMetrics());
        }

        return context;
    }

    // For android device versions below Nougat (7.0)
    // updates application default locale configurations and
    // returns new Context object for the current Context but whose resources are adjusted to match the given Configuration
    @SuppressWarnings("deprecation")
    private static Context updateResourcesLegacy(Context context, String language) {

        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public static ContextWrapper wrap(Context context, String language) {
        persist(context, language);
        Resources res = context.getResources();
        Configuration configuration = res.getConfiguration();
        Locale newLocale = new Locale(language);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
            context = context.createConfigurationContext(configuration);

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            configuration.setLocale(newLocale);
            context = context.createConfigurationContext(configuration);

        } else {
            configuration.locale = newLocale;
            res.updateConfiguration(configuration, res.getDisplayMetrics());
        }

        return new ContextWrapper(context);
    }
}


    /*Locale locale = new Locale(language);

    Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
                LocaleList localeList = new LocaleList(locale);
                localeList.setDefault(localeList);
                configuration.setLocales(localeList);

                Resources activityRes = context.getResources();
                activityRes.updateConfiguration(configuration, activityRes.getDisplayMetrics());

                Resources applicationRes = context.getApplicationContext().getResources();
                Configuration applicationConf = applicationRes.getConfiguration();
                applicationConf.setLocale(locale);
                applicationRes.updateConfiguration(applicationConf,applicationRes.getDisplayMetrics());*/

      /* Resources applicationRes = context.getApplicationContext().getResources();
        Configuration applicationConf = applicationRes.getConfiguration();
        applicationConf.setLocale(newLocale);
        applicationRes.updateConfiguration(applicationConf,
                applicationRes.getDisplayMetrics());
*/

