package com.bsreeinf.jobapp.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.telephony.PhoneNumberUtils;
import android.view.Window;
import android.widget.ProgressBar;

import com.bsreeinf.jobapp.R;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Commons {

    public static final boolean IS_NETWORK_TEST_EMULATED = false;
    public static final String USER_LOGGED_IN = "isUserLoggerIn";
    public static final int progressColor = 0xFF00ADEF;
    public static final boolean SHOW_DEBUG_MSGS = true;
    public static final boolean SHOW_TOAST_MSGS = false;
    protected static final String TAG = "Commons";
    public static final String IS_FIRST_APP_USE = "isFirstAppUse";

    public static String server_ip;
    public static String serverAddress;
    public static String URL_INIT, URL_USERS, URL_USER, URL_LOGIN, URL_JOBS, URL_JOB, URL_SAVED_APPLIED_JOBS, URL_QUESTIONNAIRE;

    public static String HTTP_GET = "GET";
    public static String HTTP_POST = "POST";
    public static String HTTP_PUT = "PUT";
    public static String HTTP_DELETE = "DELETE";

    public static SimpleContainer educationList;
    public static SimpleContainer skillsList;
    public static SimpleContainer languageList;
    public static SimpleContainer locationList;
    public static SimpleContainer jobFunctionList;
    public static SimpleContainer salaryRangeList;
    public static SimpleContainer industryList;
    public static CompanyContainer companyList;

    // Shared Preferences Constants
    private static SharedPreferences pref = null;
    private static Editor editor = null;
    private static SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
    private static SimpleDateFormat dayDateShortFormat = new SimpleDateFormat("EEE dd MMM, HH:mm", Locale.US);

//    education_list
//    skills_list
//    language_list
//    location_list
//    job_function_list
//    salary_range_list

    public static void initURLs(String ip, String port) {
//        if (!ip.trim().isEmpty()) {
//            server_ip = ip;
//        }
        serverAddress = "http://192.168.1.8:3000";
//        serverAddress = "https://safe-ocean-9547.herokuapp.com";
        URL_INIT = serverAddress + "/init";
        URL_USERS = serverAddress + "/users";
        URL_USER = serverAddress + "/user";
        URL_LOGIN = serverAddress + "/login";
        URL_JOB = serverAddress + "/job";
        URL_JOBS = serverAddress + "/jobs";
        URL_SAVED_APPLIED_JOBS = serverAddress + "/saved_applied_jobs";
        URL_QUESTIONNAIRE = serverAddress + "/questionnaires";
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        if (pref != null)
            return pref;
        if (context != null) {
            pref = PreferenceManager.getDefaultSharedPreferences(context);
            return pref;
        }
        return pref;
    }

    public static Editor getEditor(Context context) {
        if (pref != null)
            return pref.edit();
        if (context != null) {
            editor = getSharedPreferences(context).edit();
            return editor;
        }
        return editor;
    }

    public static void clearCache(Context context) {
        editor = getEditor(context);
        editor.clear();
        editor.commit();
    }

    public static boolean isLoggedIn(final Context context) {
        return getSharedPreferences(context).getBoolean(USER_LOGGED_IN, false);
    }

    public static Typeface getTypefaceShelf(Context context) {
        return Typeface.createFromAsset(context.getAssets(),
                "fonts/see_you_at_the_movies.ttf");
    }

    public static boolean checkFileExists(Context context, String filename) {
        File file = new File(context.getExternalFilesDir(null), filename);
        return file.exists();
    }

    public static ProgressDialog getCustomProgressDialog(Context context) {
        ProgressDialog dialog = new ProgressDialog(context, R.style.CustomProgressDialog);
        dialog.setCancelable(false);
        dialog.getWindow().requestFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        dialog.show();
        dialog.setContentView(R.layout.dialog_custom_progressdialog);
        ((ProgressBar) dialog.findViewById(R.id.progress))
                .getIndeterminateDrawable()
                .setColorFilter(
                        context.getResources().getColor(R.color.spinner_color),
                        PorterDuff.Mode.SRC_IN
                );
        return dialog;
    }

    public static String parseUTCToLocal(String UTCtime) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date parsed = null;
        try {
            parsed = format.parse(UTCtime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        TimeZone tz = TimeZone.getDefault();
        format.setTimeZone(tz);
        return format.format(parsed);
    }

    public static String getFormattedTimestamp(String date) {
        date = parseUTCToLocal(date);
        Date dt = null;
        try {
            dt = dateTimeFormat.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dayDateShortFormat.format(dt);
    }

    // validating email id
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // validating password with retype password
    public static boolean isValidPassword(String pass) {
        if (pass != null && pass.length() > 6) {
            return true;
        }
        return false;
    }

    public static boolean isNameValid(String name) {
        return name.length() > 0;
    }

    public static boolean isPhoneValid(String phone) {
        return PhoneNumberUtils.isGlobalPhoneNumber(phone);
//        return true;
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }
}
