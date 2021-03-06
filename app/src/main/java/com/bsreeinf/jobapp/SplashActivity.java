package com.bsreeinf.jobapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bsreeinf.jobapp.util.Commons;
import com.bsreeinf.jobapp.util.CompanyContainer;
import com.bsreeinf.jobapp.util.SimpleContainer;
import com.bsreeinf.jobapp.util.TestNetwork;
import com.bsreeinf.jobapp.util.User;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class SplashActivity extends Activity {
    private static final String TAG = "SplashActivity";

    private ViewFlipper viewFlipper;
    private float lastX;
    private Context context;
    private boolean showCoachMarks;
    private int viewCount;
    private LinearLayout layoutPagination;
    private EditText txtEmail;
    private EditText txtPassword;
    private String strEmail, strPassword;
    private boolean respondToSwipe = true;

    private TextView txtSkipTutorial;
    private Button btnLogin, btnSignUp;

    private Typeface font, fontBold;

    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        context = this;
        prefs = Commons.getSharedPreferences(context);
        prefEdit = Commons.getEditor(context);
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-L.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "fonts/Ubuntu-B.ttf");

        txtSkipTutorial = (TextView) findViewById(R.id.lblSkipTutorial);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        txtSkipTutorial.setTypeface(font);
        btnLogin.setTypeface(fontBold);
        btnSignUp.setTypeface(fontBold);
        Commons.initURLs(null, null);

        final ProgressDialog progress = Commons.getCustomProgressDialog(context);
        Log.d("Ion Request", "Request URI is : " + Commons.URL_INIT);
        Ion.with(getApplicationContext())
                .load(Commons.HTTP_GET, Commons.URL_INIT)
                .setLogging("Ion Request", Log.DEBUG)
                .followRedirect(true)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("Ion Request", "Completed");
                        progress.dismiss();
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        if (Commons.SHOW_DEBUG_MSGS)
                            Log.d(TAG, "Ion Request " + result.toString());
                        if (Commons.SHOW_TOAST_MSGS)
                            Toast.makeText(context, "Ion Request " + result.toString(), Toast.LENGTH_LONG).show();

                        callbackInitRequest(result);
                    }
                });
    }

    private void init() {
        showCoachMarks = prefs.getBoolean(Commons.IS_FIRST_APP_USE, true);
        layoutPagination = (LinearLayout) findViewById(R.id.layoutPagination);
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPassword = (EditText) findViewById(R.id.txtPassword);

        txtEmail.setTypeface(font);
        txtPassword.setTypeface(font);

        viewCount = viewFlipper.getChildCount();
        if (showCoachMarks) {
            addPagination();
            setSelectedPage(0);
            prefEdit.putBoolean(Commons.IS_FIRST_APP_USE, false);
            prefEdit.commit();
        } else {
            for (int i = 0; i < viewCount - 1; i++)
                viewFlipper.showNext();
//            setSelectedPage(viewCount - 1);
            layoutPagination.setVisibility(View.GONE);
            respondToSwipe = false;
        }
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isLoginDataValid())
                    return;
                execLogin(txtEmail.getText().toString().trim(), txtPassword.getText().toString().trim());
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(context, RegisterUser.class));
            }
        });
        txtSkipTutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < viewCount - 1; i++)
                    viewFlipper.showNext();
                setSelectedPage(viewCount - 1);
            }
        });

        if (prefs.getBoolean("is_logged_in", false)) {
            execLogin(prefs.getString("email", ""), prefs.getString("password", ""));
        } else {
//            txtSkipTutorial.setVisibility(View.VISIBLE);
        }
    }

    private void execLogin(String email, String password) {
        final ProgressDialog progress = Commons.getCustomProgressDialog(context);
        strEmail = email;
        strPassword = password;
        Ion.with(getApplicationContext())
                .load(Commons.HTTP_GET, Commons.URL_LOGIN)
                .setLogging("Ion Request", Log.DEBUG)
                .followRedirect(true)
                .setBodyParameter("email", email)
                .setBodyParameter("password", password)
                .asJsonObject()
                .setCallback(new FutureCallback<JsonObject>() {
                    @Override
                    public void onCompleted(Exception e, JsonObject result) {
                        Log.d("Ion Request", "Completed");
                        progress.dismiss();
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        if (Commons.SHOW_DEBUG_MSGS)
                            Log.d(TAG, "Ion Request " + result.toString());
                        if (Commons.SHOW_TOAST_MSGS)
                            Toast.makeText(context, "Ion Request " + result.toString(), Toast.LENGTH_LONG).show();

                        callbackLoginRequest(result);
                    }
                });
    }

    private void finishLogin() {
        SharedPreferences.Editor editor = Commons.getEditor(context);
        editor.putString("email", strEmail);
        editor.putString("password", strPassword);
        editor.putBoolean("is_logged_in", true);
        editor.commit();
        startActivity(new Intent(context, MainActivity.class));
        finish();
    }

    private void callbackInitRequest(JsonObject responseObject) {
        if (responseObject.get(TestNetwork.P_STATUS).getAsString().equals(TestNetwork.P_SUCCESS)) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Init request succeeded");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Init request succeeded", Toast.LENGTH_LONG).show();

            Commons.educationList = new SimpleContainer(
                    SimpleContainer.CONTAINER_TYPE_EDUCATION,
                    responseObject.get("education_list").getAsJsonArray()
            );
            Commons.skillsList = new SimpleContainer(
                    SimpleContainer.CONTAINER_TYPE_SKILLS,
                    responseObject.get("skills_list").getAsJsonArray()
            );
            Commons.languageList = new SimpleContainer(
                    SimpleContainer.CONTAINER_TYPE_LANGUAGES,
                    responseObject.get("language_list").getAsJsonArray()
            );
            Commons.locationList = new SimpleContainer(
                    SimpleContainer.CONTAINER_TYPE_LOCATIONS,
                    responseObject.get("location_list").getAsJsonArray()
            );
            Commons.salaryRangeList = new SimpleContainer(
                    SimpleContainer.CONTAINER_TYPE_SALARY_RANGES,
                    responseObject.get("salary_range_list").getAsJsonArray()
            );
            Commons.companyList = new CompanyContainer(
                    responseObject.get("company_list").getAsJsonArray()
            );
            Commons.industryList = new SimpleContainer(
                    SimpleContainer.CONTAINER_TYPE_INDUSTRIES,
                    responseObject.get("industry_list").getAsJsonArray()
            );

            init();
        } else {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Init request failed");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Init request failed", Toast.LENGTH_LONG).show();
        }
    }

    private void callbackLoginRequest(JsonObject responseObject) {
        if (responseObject.get(TestNetwork.P_STATUS).getAsString().equals(TestNetwork.P_SUCCESS)) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Login request succeeded");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Login request succeeded", Toast.LENGTH_LONG).show();

            Commons.currentUser = new User(responseObject.get("user_details").getAsJsonObject());
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("email", strEmail);
            editor.putString("password", strPassword);
            editor.apply();

            finishLogin();
        } else {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Init request failed");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Login request failed", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Wrong email / password", Toast.LENGTH_LONG).show();
            txtEmail.requestFocus();
        }
    }

    private boolean isLoginDataValid() {

        if (!Commons.isValidEmail(txtEmail.getText().toString())) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "VALIDATION: Email invalid");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Invalid email address", Toast.LENGTH_LONG).show();
            txtEmail.setError("Invalid email address");
            txtEmail.requestFocus();
            return false;
        }
        if (!Commons.isValidPassword(txtPassword.getText().toString())) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "VALIDATION: Password invalid");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Password should be at least six characters long", Toast.LENGTH_LONG).show();
            txtPassword.setError("Password should be at least six characters long");
            txtPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void setSelectedPage(int index) {
        for (int i = 0; i < layoutPagination.getChildCount(); i++) {
            View view = layoutPagination.getChildAt(i);
            if (i == index) {
                view.setBackgroundResource(R.drawable.circle_red);
            } else {
                view.setBackgroundResource(R.drawable.circle_white);
            }
        }
    }

    private void addPagination() {
        if (viewCount > 0) {
            layoutPagination.setVisibility(View.VISIBLE);
            layoutPagination.removeAllViews();
        }
        for (int i = 0; i < viewCount; i++) {
            View view = new View(context);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    (int) getResources().getDimension(R.dimen.pagination_radius),
                    (int) getResources().getDimension(R.dimen.pagination_radius)
            );
            layoutParams.setMargins(
                    0, 0,
                    (int) getResources().getDimension(R.dimen.pagination_padding),
                    0
            );
            view.setLayoutParams(layoutParams);
            view.setBackgroundResource(R.drawable.circle_white);
            layoutPagination.addView(view);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!showCoachMarks || !respondToSwipe)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                float currentX = event.getX();
                // Handling left to right screen swap.
                if (lastX < currentX) {
                    // If there aren't any other children, just break.
                    if (viewFlipper.getDisplayedChild() == 0)
                        break;
                    viewFlipper.setInAnimation(context, R.anim.slide_right_in);
                    viewFlipper.setOutAnimation(context, R.anim.slide_right_out);
                    viewFlipper.showPrevious();
                    setSelectedPage(viewFlipper.getDisplayedChild());
                }
                if (lastX > currentX) {
                    if (viewFlipper.getDisplayedChild() == viewCount - 1)
                        break;
                    viewFlipper.setInAnimation(context, R.anim.slide_left_in);
                    viewFlipper.setOutAnimation(context, R.anim.slide_left_out);
                    viewFlipper.showNext();
                    setSelectedPage(viewFlipper.getDisplayedChild());
                }
                break;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
