package com.remote.control.allsmarttv.utils.ir_utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import com.remote.control.allsmarttv.activitiesUi.StartingScreen;
import com.remote.control.allsmarttv.R;

import java.util.Locale;

public class SupportedClass {

    public static String FEEDBACK_EMAIL = "admin@serpskills.com";

    public static boolean checkConnection(Context mContext) {
        ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        if (netInfo == null) {
            return false;
        } else {
            return true;
        }
    }

    public static void rateApp(Activity activity) {
        final String appPackageName = activity.getPackageName(); // getPackageName() from Context or Activity object
        try {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void shareApp(Activity activity) {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        String url = "https://play.google.com/store/apps/details?id=" + activity.getPackageName();
        String message = "Hey! Check out this new Universal Remote App from the below link for latest Remote functions." +
                "\n\n" +
                "Android - " + url;
        share.putExtra(Intent.EXTRA_TEXT, message);
        share.putExtra(Intent.EXTRA_SUBJECT, "UniversalRemote App");
        activity.startActivity(Intent.createChooser(share, "Share via"));
    }

    public static void feedback(Activity activity) {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", FEEDBACK_EMAIL, null));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "UniversalRemote App - Feedback");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "");
        activity.startActivity(Intent.createChooser(emailIntent, "Write Feedback"));
    }

    public static void setLangLocale(Context context, String lang, int position) {
        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration configuration = new Configuration();
        configuration.locale = locale;
        context.getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        SharedPreferences.Editor editor = context.getSharedPreferences("Settings", Context.MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.putInt("pos", position);
        editor.apply();
    }

    public static void loadLangLocale(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        String language = preferences.getString("language", "");
        int position = preferences.getInt("pos", 8);
        setLangLocale(context, language, position);
    }

    public static void langDialog(Context context, Activity activity) {

        final String[] items = {context.getResources().getString(R.string.arabic), context.getResources().getString(R.string.azer),
                context.getResources().getString(R.string.ben), context.getResources().getString(R.string.Bulg),
                context.getResources().getString(R.string.chinese), context.getResources().getString(R.string.czech),
                context.getResources().getString(R.string.danish), context.getResources().getString(R.string.dutch),
                context.getResources().getString(R.string.english), context.getResources().getString(R.string.french),
                context.getResources().getString(R.string.german), context.getResources().getString(R.string.geor),
                context.getResources().getString(R.string.hindi), context.getResources().getString(R.string.hebr),
                context.getResources().getString(R.string.hung), context.getResources().getString(R.string.indonesian),
                context.getResources().getString(R.string.italian), context.getResources().getString(R.string.japanese),
                context.getResources().getString(R.string.korean), context.getResources().getString(R.string.malay),
                context.getResources().getString(R.string.pers), context.getResources().getString(R.string.polish),
                context.getResources().getString(R.string.portuguese), context.getResources().getString(R.string.rus),
                context.getResources().getString(R.string.Español), context.getResources().getString(R.string.thai),
                context.getResources().getString(R.string.ukr), context.getResources().getString(R.string.urdu),
                context.getResources().getString(R.string.vietnamese)};


        AlertDialog.Builder dialog = new AlertDialog.Builder(context);

        TextView textView = new TextView(context);
        textView.setText(context.getResources().getString(R.string.choose));
        textView.setPadding(30, 30, 30, 30);
        textView.setTextSize(20F);
        textView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        textView.setTextColor(Color.WHITE);

        dialog.setCustomTitle(textView);

        SharedPreferences preferences = activity.getSharedPreferences("Settings", Context.MODE_PRIVATE);
        int position = preferences.getInt("pos", 8);

        dialog.setSingleChoiceItems(items, position, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (i == 0) {
                    setLangLocale(context, "ar", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();

                } else if (i == 1) {
                    setLangLocale(context, "az", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 2) {
                    setLangLocale(context, "bn", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 3) {
                    setLangLocale(context, "bg", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 4) {
                    setLangLocale(context, "zh", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 5) {
                    setLangLocale(context, "cs", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 6) {
                    setLangLocale(context, "da", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 7) {
                    setLangLocale(context, "nl", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 8) {
                    setLangLocale(context, "en", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 9) {
                    setLangLocale(context, "fr", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 10) {
                    setLangLocale(context, "de", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 11) {
                    setLangLocale(context, "ka", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 12) {
                    setLangLocale(context, "hi", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 13) {
                    setLangLocale(context, "iw", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 14) {
                    setLangLocale(context, "hu", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 15) {
                    setLangLocale(context, "in", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 16) {
                    setLangLocale(context, "it", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 17) {
                    setLangLocale(context, "ja", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 18) {
                    setLangLocale(context, "ko", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 19) {
                    setLangLocale(context, "ms", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 20) {
                    setLangLocale(context, "fa", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 21) {
                    setLangLocale(context, "pl", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 22) {
                    setLangLocale(context, "pt", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 23) {
                    setLangLocale(context, "ru", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 24) {
                    setLangLocale(context, "es", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 25) {
                    setLangLocale(context, "th", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 26) {
                    setLangLocale(context, "uk", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 27) {
                    setLangLocale(context, "ur", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                } else if (i == 28) {
                    setLangLocale(context, "vi", i);
                    Intent intent = new Intent(context, StartingScreen.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                    context.startActivity(intent);
                    activity.finish();
                }
                dialog.dismiss();
            }
        });

        AlertDialog builder = dialog.create();
        builder.show();
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int displayWidth = metrics.widthPixels;
        int displayHeight = metrics.heightPixels;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(builder.getWindow().getAttributes());
        int dialogWindowWidth = (int) (displayWidth * 0.8f);
        int dialogWindowHeight = (int) (displayHeight * 0.7f);
        lp.width = dialogWindowWidth;
        lp.height = dialogWindowHeight;
        builder.getWindow().setAttributes(lp);

    }
}
