package com.bsreeinf.jobapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bsreeinf.jobapp.util.Commons;
import com.bsreeinf.jobapp.util.DialogMultiselect;
import com.bsreeinf.jobapp.util.SimpleContainer;
import com.bsreeinf.jobapp.util.TestNetwork;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;


public class RegisterUser extends Activity {
    private static final String TAG = "RegisterUser";
    private String strHighestEducatioIDs, strSkillsIDs, strLanguagesIDs;
    private ArrayList<String> arrHighestEducatioIDs, arrSkillsIDs, arrLanguagesIDs;
    private ViewFlipper viewFlipper;
    private Context context;
    private int viewCount;
    private EditText txtName;
    private EditText txtEmail;
    private EditText txtPhone;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private String user_id = null;
    private LinearLayout layoutHighestEducation, layoutSkills, layoutLanguages;
    private TextView txtHighestEducation, txtSkills, txtLanguages;

    private Typeface font, fontBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register_user);
        user_id = null;
        context = this;
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        txtName = (EditText) findViewById(R.id.txtName);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtPhone = (EditText) findViewById(R.id.txtPhone);
        txtPassword = (EditText) findViewById(R.id.txtPassword);
        txtConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        txtHighestEducation = (TextView) findViewById(R.id.txtHighestEducation);
        txtSkills = (TextView) findViewById(R.id.txtSkills);
        txtLanguages = (TextView) findViewById(R.id.txtLanguages);

        txtName.setTypeface(font);
        txtEmail.setTypeface(font);
        txtPhone.setTypeface(font);
        txtPassword.setTypeface(font);
        txtConfirmPassword.setTypeface(font);
        txtHighestEducation.setTypeface(font);
        txtSkills.setTypeface(font);
        txtLanguages.setTypeface(font);

        layoutHighestEducation = (LinearLayout) findViewById(R.id.layoutHighestEducation);
        layoutSkills = (LinearLayout) findViewById(R.id.layoutSkills);
        layoutLanguages = (LinearLayout) findViewById(R.id.layoutLanguages);

        viewCount = viewFlipper.getChildCount();
        findViewById(R.id.btnValidateRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRegistrationDataValid())
                    return;
                setPage(1);
            }

        });

        findViewById(R.id.btnRegister).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isRegistrationPasswordValid())
                    return;
                if (Commons.IS_NETWORK_TEST_EMULATED) {
                    callbackRegistrationRequest(TestNetwork.request(
                                    TestNetwork.getSampleRegistrationRequest(TestNetwork.TYPE_DEFAULT))
                    );
                } else {
                    //TODO Actual network request here; Callbacks: callbackInitRequest()
                    final ProgressDialog progress = Commons.getCustomProgressDialog(context);
                    JsonObject requestJson = new JsonObject();
                    requestJson.addProperty("name", txtName.getText().toString().trim());
                    requestJson.addProperty("email", txtEmail.getText().toString().trim());
                    requestJson.addProperty("phone", txtPhone.getText().toString().trim());
                    requestJson.addProperty("password", txtPassword.getText().toString().trim());
                    Log.d("Ion Request", "Request Json is : " + requestJson.toString());
                    Ion.with(getApplicationContext())
                            .load(Commons.HTTP_POST, Commons.URL_USERS)
                            .setLogging("Ion Request", Log.DEBUG)
                            .followRedirect(true)
                            .setJsonObjectBody(requestJson)
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

                                    callbackRegistrationRequest(result);
                                }
                            });
                }
            }

        });
        findViewById(R.id.btnUpdateProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isListsDataValid())
                    return;
                if (Commons.IS_NETWORK_TEST_EMULATED) {
//                    callbackUserDataUpdateRequest(TestNetwork.request(
//                                    TestNetwork.getSampleUserDataUpdateRequest(TestNetwork.TYPE_DEFAULT))
//                    );
                } else {
                    //TODO Actual network request here; Callbacks: callbackInitRequest()
                    final ProgressDialog progress = Commons.getCustomProgressDialog(context);
                    JsonObject requestJson = new JsonObject();
                    requestJson.addProperty("highest_education", strHighestEducatioIDs);
                    requestJson.addProperty("skills", strSkillsIDs);
                    requestJson.addProperty("language", strLanguagesIDs);

                    Log.d("Ion Request", "Request Json is : " + requestJson.toString());
                    Ion.with(getApplicationContext())
                            .load(Commons.HTTP_PUT, Commons.URL_USERS + "/" + user_id)
                            .setLogging("Ion Request", Log.DEBUG)
                            .followRedirect(true)
                            .setJsonObjectBody(requestJson)
                            .asString()
                            .setCallback(new FutureCallback<String>() {
                                @Override
                                public void onCompleted(Exception e, String result) {
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

                                    callbackUserDataUpdateRequest(result);
                                }
                            });
                }
            }

        });

//        SimpleContainerAdapter highestEducationAdapter = new SimpleContainerAdapter(context, Commons.educationList.getElementList());
//        highestEducationAdapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
//        highestEducationAdapter.setNotifyOnChange(true);
//        if (spnHighestEducation != null)
//            spnHighestEducation.setAdapter(highestEducationAdapter);
//
//        SimpleContainerAdapter spnSkillsAdapter = new SimpleContainerAdapter(context, Commons.skillsList.getElementList());
//        spnSkillsAdapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
//        spnSkillsAdapter.setNotifyOnChange(true);
//        if (spnSkills != null)
//            spnSkills.setAdapter(spnSkillsAdapter);
//
//        SimpleContainerAdapter spnLanguagesAdapter = new SimpleContainerAdapter(context, Commons.languageList.getElementList());
//        spnLanguagesAdapter.setDropDownViewResource(android.R.layout.select_dialog_multichoice);
//        spnLanguagesAdapter.setNotifyOnChange(true);
//        if (spnLanguages != null)
//            spnLanguages.setAdapter(spnLanguagesAdapter);

//        layoutHighestEducation, layoutSkills, layoutLanguages
        layoutHighestEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_EDUCATION,
                        Commons.educationList.getElementList(),
                        arrHighestEducatioIDs
                )
                        .show(getFragmentManager(), "");
            }
        });

        layoutSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_SKILLS,
                        Commons.skillsList.getElementList(),
                        arrSkillsIDs
                ).show(getFragmentManager(), "");
            }
        });

        layoutLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_LANGUAGES,
                        Commons.languageList.getElementList(),
                        arrLanguagesIDs
                ).show(getFragmentManager(), "");
            }
        });
    }

    private void callbackRegistrationRequest(JsonObject responseObject) {
        if (responseObject.has("id")) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Registration request succeeded");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Registration request succeeded", Toast.LENGTH_LONG).show();
            user_id = responseObject.get("id").getAsString();
            setPage(2);
        } else {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Registration request failed");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Registration request failed", Toast.LENGTH_LONG).show();
        }
    }

    private void callbackUserDataUpdateRequest(String response) {
//        if (responseObject.get(TestNetwork.P_STATUS).getAsString().equals(TestNetwork.P_SUCCESS)) {
        if (Commons.SHOW_DEBUG_MSGS)
            Log.d(TAG, "User Data Update request succeeded");
        if (Commons.SHOW_TOAST_MSGS)
            Toast.makeText(context, "User Data Update request succeeded", Toast.LENGTH_LONG).show();

        final ProgressDialog progress = Commons.getCustomProgressDialog(context);
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("email", txtEmail.getText().toString().trim());
        requestJson.addProperty("password", txtPassword.getText().toString().trim());
        Log.d("Ion Request", "Request Json is : " + requestJson.toString());
        Ion.with(getApplicationContext())
                .load(Commons.HTTP_GET, Commons.URL_LOGIN)
                .setLogging("Ion Request", Log.DEBUG)
                .followRedirect(true)
                .setJsonObjectBody(requestJson)
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

//        } else {
//            if (Commons.SHOW_DEBUG_MSGS)
//                Log.d(TAG, "User Data Update request failed");
//            if (Commons.SHOW_TOAST_MSGS)
//                Toast.makeText(context, "User Data Update request failed", Toast.LENGTH_LONG).show();
//        }
    }

    private void callbackLoginRequest(JsonObject responseObject) {
        if (responseObject.get(TestNetwork.P_STATUS).getAsString().equals(TestNetwork.P_SUCCESS)) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Login request succeeded");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Login request succeeded", Toast.LENGTH_LONG).show();

            SharedPreferences.Editor editor = Commons.getEditor(context);
            JsonObject userDetails = responseObject.get("user_details").getAsJsonObject();
            editor.putString("user_id", userDetails.get("id").getAsString());
            editor.putString("name", userDetails.get("name").getAsString());
            editor.putString("phone", userDetails.get("phone").getAsString());
            editor.putString("highest_education", userDetails.get("highest_education").getAsString());
            editor.putString("skills", userDetails.get("skills").getAsString());
            editor.putString("language", userDetails.get("language").getAsString());
            editor.commit();

            finishLogin();
        } else {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Init request failed");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Login request failed", Toast.LENGTH_LONG).show();
        }
    }

    private void finishLogin() {
        SharedPreferences.Editor editor = Commons.getEditor(context);
        editor.putString("email", txtEmail.getText().toString().toLowerCase().trim());
        editor.putString("password", txtPassword.getText().toString().trim());
        editor.putBoolean("is_logged_in", true);
        startActivity(new Intent(context, JobsActivity.class));
        finish();
    }

    private boolean isRegistrationDataValid() {
        if (!Commons.isNameValid(txtName.getText().toString())) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "VALIDATION: Name invalid");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "VALIDATION: Name invalid", Toast.LENGTH_LONG).show();
            txtName.setError("Invalid name");
            txtName.requestFocus();
            return false;
        }
        if (!Commons.isValidEmail(txtEmail.getText().toString())) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "VALIDATION: Email invalid");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Invalid email address", Toast.LENGTH_LONG).show();
            txtEmail.setError("Invalid email address");
            txtEmail.requestFocus();
            return false;
        }
        if (!Commons.isPhoneValid(txtPhone.getText().toString())) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "VALIDATION: Phone invalid");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "VALIDATION: Phone invalid", Toast.LENGTH_LONG).show();
            txtPhone.setError("Invalid phone number");
            txtPhone.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isRegistrationPasswordValid() {
        if (!Commons.isValidPassword(txtPassword.getText().toString())) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "VALIDATION: Password invalid");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Password should be at least six characters long", Toast.LENGTH_LONG).show();
            txtPassword.setError("Password should be at least six characters long");
            txtPassword.requestFocus();
            return false;
        }
        if (!txtPassword.getText().toString().equals(txtConfirmPassword.getText().toString())) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "VALIDATION: Passwords don't match");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "VALIDATION: Passwords don't match", Toast.LENGTH_LONG).show();
            txtConfirmPassword.setError("Passwords don't match");
            txtConfirmPassword.requestFocus();
            return false;
        }
        return true;
    }

    private boolean isListsDataValid() {
        return true;
    }

    private void setPage(int i) {
        if (i < 0) {
            // If there aren't any other children, just break.
            if (viewFlipper.getDisplayedChild() == 0)
                return;
            viewFlipper.setInAnimation(context, R.anim.slide_right_in);
            viewFlipper.setOutAnimation(context, R.anim.slide_right_out);
            viewFlipper.showPrevious();
        } else if (i > 0) {
            if (viewFlipper.getDisplayedChild() == viewCount - 1)
                return;
            viewFlipper.setInAnimation(context, R.anim.slide_left_in);
            viewFlipper.setOutAnimation(context, R.anim.slide_left_out);
            viewFlipper.showNext();

        }
    }


    public void onMultiselectCompleted(int tag, ArrayList<String> ids) {
        String strIDs = "";
        for (int i = 0; i < ids.size(); i++) {
            strIDs += ids.get(i) + ((i != ids.size() - 1) ? "," : "");
        }
        switch (tag) {
            case SimpleContainer.CONTAINER_TYPE_EDUCATION:
                arrHighestEducatioIDs = ids;
                strHighestEducatioIDs = strIDs;
                break;
            case SimpleContainer.CONTAINER_TYPE_SKILLS:
                arrSkillsIDs = ids;
                strSkillsIDs = strIDs;
                break;
            case SimpleContainer.CONTAINER_TYPE_LANGUAGES:
                arrLanguagesIDs = ids;
                strLanguagesIDs = strIDs;
                break;
            default:
                return;
        }
        refreshStoresText(tag, ids);
    }

    private void refreshStoresText(int tag, ArrayList<String> ids) {
        TextView textViewToUpdate;
        SimpleContainer listElements;
        switch (tag) {
            case SimpleContainer.CONTAINER_TYPE_EDUCATION:
                textViewToUpdate = txtHighestEducation;
                listElements = Commons.educationList;
                break;
            case SimpleContainer.CONTAINER_TYPE_SKILLS:
                textViewToUpdate = txtSkills;
                listElements = Commons.skillsList;
                break;
            case SimpleContainer.CONTAINER_TYPE_LANGUAGES:
                listElements = Commons.languageList;
                textViewToUpdate = txtLanguages;
                break;
            default:
                return;
        }
        if (ids == null) {
            textViewToUpdate.setText("None Selected");
            return;
        }
        textViewToUpdate.setText("");
        if (ids.size() == 0)
            textViewToUpdate.setText("None Selected");
        for (int i = 0; i < ids.size(); i++) {
            textViewToUpdate
                    .append(listElements.getBlockByID(ids.get(i)).getTitle());
            if (i != ids.size() - 1)
                textViewToUpdate.append(", ");
        }
    }
}
