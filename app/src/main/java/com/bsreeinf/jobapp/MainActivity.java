package com.bsreeinf.jobapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bsreeinf.jobapp.util.AnimatedLinearLayout;
import com.bsreeinf.jobapp.util.Commons;
import com.bsreeinf.jobapp.util.CompanyContainer;
import com.bsreeinf.jobapp.util.DialogMultiselect;
import com.bsreeinf.jobapp.util.JobsAdapter;
import com.bsreeinf.jobapp.util.JobsContainer;
import com.bsreeinf.jobapp.util.SimpleContainer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.builder.Builders;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    public static JobsContainer jobs;
    private TextView txtTabHome, txtTabSaved, txtTabProfile;
    private ImageView imgTabHome, imgTabFilter, imgTabSaved, imgTabProfile;
    private ViewFlipper viewFlipper;
    private Context context;
    private ListView layoutJobsContainer;
    private LinearLayout layoutSavedJobsContainer, layoutAppliedJobsContainer;
    private AnimatedLinearLayout layoutFilter;
    private List<Integer> arrFilterLocationIDs, arrFilterCompanyIDs, arrFilterIndustryIDs, arrFilterSalaryIDs;
    private TextView txtFilterLocation, txtFilterCompany, txtFilterIndustry, txtFilterSalary;
    private List<Integer> arrSkillsIDs, arrLanguagesIDs;
    private Integer idHighestEducation;
    private EditText txtName;
    private TextView txtEmail;
    private EditText txtPhone;
    private EditText txtPassword;
    private EditText txtConfirmPassword;
    private TextView txtHighestEducation, txtSkills, txtLanguages;

    private Typeface font, fontBold;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_jobs);
        context = this;
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");
        findViewById(R.id.imgLogout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Logout?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Commons.clearCache(context);
                        finish();
                        startActivity(new Intent(context, SplashActivity.class));
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                builder.create().show();
            }
        });
        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        layoutJobsContainer = (ListView) findViewById(R.id.layoutJobsContainer);
        layoutSavedJobsContainer = (LinearLayout) findViewById(R.id.layoutSavedJobsContainer);
        layoutAppliedJobsContainer = (LinearLayout) findViewById(R.id.layoutAppliedJobsContainer);

        txtTabHome = (TextView) findViewById(R.id.txtTabHome);
        txtTabSaved = (TextView) findViewById(R.id.txtTabSaved);
        txtTabProfile = (TextView) findViewById(R.id.txtTabProfile);

        txtTabHome.setTypeface(font);
        txtTabSaved.setTypeface(font);
        txtTabProfile.setTypeface(font);

        imgTabFilter = (ImageView) findViewById(R.id.imgTabFilter);
        imgTabHome = (ImageView) findViewById(R.id.imgTabHome);
        imgTabSaved = (ImageView) findViewById(R.id.imgTabSaved);
        imgTabProfile = (ImageView) findViewById(R.id.imgTabProfile);

        layoutFilter = (AnimatedLinearLayout) findViewById(R.id.layoutFilter);
        Animation inAnimation = AnimationUtils.loadAnimation(context, R.anim.abc_fade_in);
//        Animation outAnimation = AnimationUtils.loadAnimation(context, R.anim.abc_fade_out);
        layoutFilter.setInAnimation(inAnimation);
//        layoutFilter.setOutAnimation(outAnimation);

        refreshJobsList();

        findViewById(R.id.tabHome).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPage(0);
                imgTabFilter.setVisibility(View.VISIBLE);
                refreshJobsList();
                txtTabHome.setTextColor(getResources().getColor(R.color.pallet_coral_red_light));
                txtTabSaved.setTextColor(getResources().getColor(R.color.white));
                txtTabProfile.setTextColor(getResources().getColor(R.color.white));

                // hide filter first
                if (layoutFilter.isShown()) {
                    layoutFilter.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white));
                    } else {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white, null));
                    }
                    return;
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imgTabHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_home));
                    imgTabSaved.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_saved_white));
                    imgTabProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_profile_white));
                } else {
                    imgTabHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_home, null));
                    imgTabSaved.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_saved_white, null));
                    imgTabProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_profile_white, null));
                }
            }

        });

        findViewById(R.id.tabFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewFlipper.getDisplayedChild() != 0) {
                    if (Commons.SHOW_DEBUG_MSGS)
                        Log.d(TAG, "View jobs to filter them");
                    return;
                }
                if (layoutFilter.isShown()) {
                    layoutFilter.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white));
                    } else {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white, null));
                    }

                } else {
                    layoutFilter.setVisibility(View.VISIBLE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter));
                    } else {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter, null));
                    }
                }
            }

        });

        findViewById(R.id.btnCloseFilter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutFilter.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white));
                } else {
                    imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white, null));
                }
            }
        });

        findViewById(R.id.tabSaved).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPage(1);
                imgTabFilter.setVisibility(View.INVISIBLE);
                loadSavedAndAppliedJobs();
                txtTabSaved.setTextColor(getResources().getColor(R.color.pallet_coral_red_light));
                txtTabHome.setTextColor(getResources().getColor(R.color.white));
                txtTabProfile.setTextColor(getResources().getColor(R.color.white));

                // hide filter first
                if (layoutFilter.isShown()) {
                    layoutFilter.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white));
                    } else {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white, null));
                    }
                    return;
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imgTabSaved.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_saved));
                    imgTabHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_home_white));
                    imgTabProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_profile_white));
                } else {
                    imgTabSaved.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_saved, null));
                    imgTabHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_home_white, null));
                    imgTabProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_profile_white, null));
                }
                return;
            }

        });

        findViewById(R.id.tabProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setPage(2);
                imgTabFilter.setVisibility(View.GONE);

                txtTabProfile.setTextColor(getResources().getColor(R.color.pallet_coral_red_light));
                txtTabSaved.setTextColor(getResources().getColor(R.color.white));
                txtTabHome.setTextColor(getResources().getColor(R.color.white));

                // hide filter first
                if (layoutFilter.isShown()) {
                    layoutFilter.setVisibility(View.GONE);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white));
                    } else {
                        imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white, null));
                    }
                    return;
                }

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imgTabProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_profile));
                    imgTabSaved.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_saved_white));
                    imgTabHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_home_white));
                } else {
                    imgTabProfile.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_profile, null));
                    imgTabSaved.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_saved_white, null));
                    imgTabHome.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_home_white, null));
                }

                loadProfileData();
                return;
            }

        });

        LinearLayout layoutFilterLocation = (LinearLayout) findViewById(R.id.layoutFilterLocation);
        LinearLayout layoutFilterCompany = (LinearLayout) findViewById(R.id.layoutFilterCompany);
        LinearLayout layoutFilterIndustry = (LinearLayout) findViewById(R.id.layoutFilterIndustry);
        LinearLayout layoutFilterSalary = (LinearLayout) findViewById(R.id.layoutFilterSalary);

        txtFilterLocation = (TextView) findViewById(R.id.txtFilterLocation);
        txtFilterCompany = (TextView) findViewById(R.id.txtFilterCompany);
        txtFilterIndustry = (TextView) findViewById(R.id.txtFilterIndustry);
        txtFilterSalary = (TextView) findViewById(R.id.txtFilterSalary);

        txtFilterLocation.setTypeface(font);
        txtFilterCompany.setTypeface(font);
        txtFilterIndustry.setTypeface(font);
        txtFilterSalary.setTypeface(font);

        Button btnExecFilter = (Button) findViewById(R.id.btnExecFilter);
        btnExecFilter.setTypeface(fontBold);

        layoutFilterLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_LOCATIONS,
                        Commons.locationList.getElementList(),
                        arrFilterLocationIDs
                )
                        .show(getFragmentManager(), "");
            }
        });

        layoutFilterCompany.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_COMPANIES,
                        Commons.companyList.getElementList(),
                        arrFilterCompanyIDs
                ).show(getFragmentManager(), "");
            }
        });

        layoutFilterIndustry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_INDUSTRIES,
                        Commons.industryList.getElementList(),
                        arrFilterIndustryIDs
                ).show(getFragmentManager(), "");
            }
        });

        layoutFilterSalary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_SALARY_RANGES,
                        Commons.salaryRangeList.getElementList(),
                        arrFilterSalaryIDs
                ).show(getFragmentManager(), "");
            }
        });

        btnExecFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = Commons.getCustomProgressDialog(context);

                Builders.Any.B a = Ion.with(getApplicationContext())
                        .load(Commons.HTTP_GET, Commons.URL_JOBS)
                        .setLogging("Ion Request", Log.DEBUG)

                                                        // location, company, industry, salary

                        .followRedirect(true);

                Builders.Any.U b = a.setBodyParameter("mobile", "");
                    if (arrFilterLocationIDs != null) if (arrFilterLocationIDs.size() > 0)
                        b = (b == null ? a : b).setBodyParameter("filter_location", arrFilterCompanyIDs.toString().replace("[", "").replace("]",""));
                    if (arrFilterCompanyIDs != null) if (arrFilterCompanyIDs.size() > 0)
                        b = (b == null ? a : b).setBodyParameter("filter_company", arrFilterCompanyIDs.toString().replace("[","").replace("]", ""));
                    if (arrFilterIndustryIDs != null) if (arrFilterIndustryIDs.size() > 0)
                        b = (b == null ? a : b).setBodyParameter("filter_industry", arrFilterIndustryIDs.toString().replace("[","").replace("]", ""));
                    if (arrFilterSalaryIDs != null) if (arrFilterSalaryIDs.size() > 0)
                        b = (b == null ? a : b).setBodyParameter("filter_salary", arrFilterSalaryIDs.toString().replace("[","").replace("]",""));
                (b == null ? a : b).asJsonArray()
                        .setCallback(new FutureCallback<JsonArray>() {
                            @Override
                            public void onCompleted(Exception e, JsonArray result) {
                                progress.dismiss();
                                callbackGetJobsRequest(result);
                            }
                        });

                layoutFilter.setVisibility(View.GONE);
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white));
                } else {
                    imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white, null));
                }
            }
        });

        txtName = (EditText) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmailShow);
        txtPhone = (EditText) findViewById(R.id.txtPhone);

        txtHighestEducation = (TextView) findViewById(R.id.txtHighestEducation);
        txtSkills = (TextView) findViewById(R.id.txtSkills);
        txtLanguages = (TextView) findViewById(R.id.txtLanguages);

        txtName.setTypeface(font);
        txtEmail.setTypeface(font);
        txtPhone.setTypeface(font);
        txtHighestEducation.setTypeface(font);
        txtSkills.setTypeface(font);
        txtLanguages.setTypeface(font);

        LinearLayout layoutHighestEducation = (LinearLayout) findViewById(R.id.layoutHighestEducation);
        LinearLayout layoutSkills = (LinearLayout) findViewById(R.id.layoutSkills);
        LinearLayout layoutLanguages = (LinearLayout) findViewById(R.id.layoutLanguages);

        idHighestEducation = Commons.currentUser.getQualification_id();
        layoutHighestEducation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> arrHighestEducation = idHighestEducation == null || idHighestEducation < 1 ?
                        new ArrayList<Integer>() :
                        new ArrayList<Integer>();
                if (!(idHighestEducation == null || idHighestEducation < 1))
                    arrHighestEducation.add(idHighestEducation);
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_EDUCATION2,
                        Commons.educationList.getElementList(),
                        arrHighestEducation
                ).show(getFragmentManager(), "");
            }
        });

        arrSkillsIDs = Commons.currentUser.getSkills();
        layoutSkills.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_SKILLS2,
                        Commons.skillsList.getElementList(),
                        arrSkillsIDs
                ).show(getFragmentManager(), "");
            }
        });

        arrLanguagesIDs = Commons.currentUser.getLanguages();
        layoutLanguages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DialogMultiselect(
                        context,
                        SimpleContainer.CONTAINER_TYPE_LANGUAGES2,
                        Commons.languageList.getElementList(),
                        arrLanguagesIDs
                ).show(getFragmentManager(), "");
            }
        });

        Button btnUpdateProfile = (Button) findViewById(R.id.btnUpdateProfile);
        btnUpdateProfile.setTypeface(fontBold);
        btnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = Commons.getCustomProgressDialog(context);
                JsonObject requestJson = new JsonObject();
                requestJson.addProperty("id", Commons.currentUser.getId());
                requestJson.addProperty("name", txtName.getText().toString().trim());
                requestJson.addProperty("phone", txtPhone.getText().toString().trim());
                requestJson.addProperty("qualification_id", idHighestEducation);
                requestJson.addProperty("skills", arrSkillsIDs.toString());
                requestJson.addProperty("languages", arrLanguagesIDs.toString());
                JsonObject tmp = new JsonObject();
                tmp.add("user",requestJson);
                Log.d("Ion Request", "Request Json is : " + tmp.toString());
                Ion.with(getApplicationContext())
                        .load(Commons.HTTP_PUT, Commons.URL_USER + "/" + Commons.currentUser.getId() + ".json")
                        .setLogging("Ion Request", Log.DEBUG)
                        .followRedirect(true)
                        .setJsonObjectBody(tmp)
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
                                    Toast.makeText(context, "Profile updated successfully", Toast.LENGTH_LONG).show();

                                callbackProfileUpdateRequest(result);
                            }
                        });
            }
        });

    }

    private void callbackProfileUpdateRequest(JsonObject responseObject) {
        if (responseObject.has("id")) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Update succeeded");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Update succeeded", Toast.LENGTH_LONG).show();
            Toast.makeText(context, "Update succeeded", Toast.LENGTH_LONG).show();
        } else {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Update failed");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Update failed", Toast.LENGTH_LONG).show();
        }
    }

    private void loadSavedAndAppliedJobs() {
        final ProgressDialog progress = Commons.getCustomProgressDialog(context);
        Ion.with(getApplicationContext())
                .load(Commons.HTTP_GET, Commons.URL_SAVED_APPLIED_JOBS)
                .setLogging("Ion Request", Log.DEBUG)
                .followRedirect(true)
                .setBodyParameter("user_id", Commons.currentUser.getId() + "")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        progress.dismiss();
                        callbackGetSavedAppliedJobsRequest(result);
                    }
                });

    }

    private void refreshJobsList() {
        final ProgressDialog progress = Commons.getCustomProgressDialog(context);
        Ion.with(getApplicationContext())
                .load(Commons.HTTP_GET, Commons.URL_JOBS)
                .setLogging("Ion Request", Log.DEBUG)
                .followRedirect(true)
                .setBodyParameter("mobile", "")
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        progress.dismiss();
                        callbackGetJobsRequest(result);
                    }
                });
    }


    private void loadProfileData() {
        txtName.setText(Commons.currentUser.getName());
        txtEmail.setText(Commons.currentUser.getEmail());
        txtPhone.setText(Commons.currentUser.getPhone());
        Log.d("dd", idHighestEducation.toString());
        txtHighestEducation.setText(idHighestEducation == null || idHighestEducation < 1 ?
                "None Selected" :
                Commons.educationList.getBlockByID(idHighestEducation).getTitle());
        txtSkills.setText(Commons.skillsList.getTitles(arrSkillsIDs));
        txtLanguages.setText(Commons.languageList.getTitles(arrLanguagesIDs));

    }

    private void callbackGetJobsRequest(JsonArray responseObject) {
        System.out.println(responseObject.toString());
        jobs = new JobsContainer(responseObject);
        if (jobs.getListJobs().size() != 0) {
            JobsAdapter jobsAdapter = new JobsAdapter(context, jobs.getListJobs());
            layoutJobsContainer.setAdapter(jobsAdapter);
            layoutJobsContainer.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    Intent intent = new Intent(context, JobActivity.class);
                    intent.putExtra("jobID", jobs.getJobByIndex(position).get_id());
                    startActivity(intent);
                }
            });
        } else {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "No Jobs to show");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "No Jobs to show", Toast.LENGTH_LONG).show();
        }

    }

    private void callbackGetSavedAppliedJobsRequest(JsonArray responseObject) {
        if (responseObject == null)
            return;
        Commons.currentUser.setSavedAppliedJobs(responseObject);

        if (jobs.getListJobs().size() != 0) {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Rendering jobs");
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Get Saved/Applied Jobs request succeeded");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Get Saved/Applied Jobs request succeeded", Toast.LENGTH_LONG).show();

            layoutSavedJobsContainer.removeAllViews();
            layoutAppliedJobsContainer.removeAllViews();
            TextView lblJobTitle, lblJobLocation, lblPostDate;
            final List<Integer> savedJobs = Commons.currentUser.getSavedAppliedJobs().getSavedJobIDs();
            for (int i = 0; i < savedJobs.size(); i++) {
                if (Commons.SHOW_DEBUG_MSGS)
                    Log.d(TAG, "Rendered saved job");
                View rowView = LayoutInflater.from(context).inflate(R.layout.layout_job_concrete, null);

                lblJobTitle = (TextView) rowView.findViewById(R.id.lblJobTitle);
                lblJobLocation = (TextView) rowView.findViewById(R.id.lblJobLocation);
                lblPostDate = (TextView) rowView.findViewById(R.id.lblPostDate);

                lblJobTitle.setTypeface(fontBold);
                lblJobLocation.setTypeface(fontBold);
                lblPostDate.setTypeface(fontBold);

                final JobsContainer.Job jobA = jobs.getJobByID(savedJobs.get(i));
                lblJobTitle.setText(jobA.getTitle());
                lblJobLocation.setText(Commons.locationList.getBlockByID(jobA.getLocation_id()).getTitle());
                lblPostDate.setText(jobA.getPosted_date());
                final int pos = i;
                layoutSavedJobsContainer.addView(rowView);
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, JobActivity.class);
                        intent.putExtra("jobID", jobA.get_id());
                        startActivity(intent);
                    }
                });
            }

            final List<Integer> appliedJobs = Commons.currentUser.getSavedAppliedJobs().getAppliedJobIDs();
            for (int i = 0; i < appliedJobs.size(); i++) {
                if (Commons.SHOW_DEBUG_MSGS)
                    Log.d(TAG, "Rendered applied job");
                View rowView = LayoutInflater.from(context).inflate(R.layout.layout_job_concrete, null);

                lblJobTitle = (TextView) rowView.findViewById(R.id.lblJobTitle);
                lblJobLocation = (TextView) rowView.findViewById(R.id.lblJobLocation);
                lblPostDate = (TextView) rowView.findViewById(R.id.lblPostDate);

                lblJobTitle.setTypeface(fontBold);
                lblJobLocation.setTypeface(fontBold);
                lblPostDate.setTypeface(fontBold);

                final JobsContainer.Job jobA = jobs.getJobByID(appliedJobs.get(i));
                lblJobTitle.setText(jobA.getTitle());
                lblJobLocation.setText(Commons.locationList.getBlockByID(jobA.getLocation_id()).getTitle());
                lblPostDate.setText(jobA.getPosted_date());
                final int pos = i;
                layoutAppliedJobsContainer.addView(rowView);
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, JobActivity.class);
                        intent.putExtra("jobID", jobA.get_id());
                        startActivity(intent);
                    }
                });
            }
        } else {
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "No jobs to render");
            if (Commons.SHOW_DEBUG_MSGS)
                Log.d(TAG, "Get Saved/Applied Jobs request succeeded");
            if (Commons.SHOW_TOAST_MSGS)
                Toast.makeText(context, "Get Saved/Applied Jobs request succeeded", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_jobs, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_clear_list) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setPage(int i) {
        System.out.println(i + " " + viewFlipper.getDisplayedChild());
        if (i > viewFlipper.getDisplayedChild()) {
            viewFlipper.setInAnimation(context, R.anim.slide_left_in);
            viewFlipper.setOutAnimation(context, R.anim.slide_left_out);
            while (i > viewFlipper.getDisplayedChild()) {
                viewFlipper.showNext();
            }
        } else if (i < viewFlipper.getDisplayedChild()) {
            viewFlipper.setInAnimation(context, R.anim.slide_right_in);
            viewFlipper.setOutAnimation(context, R.anim.slide_right_out);
            while (i < viewFlipper.getDisplayedChild()) {
                viewFlipper.showPrevious();
            }
        }
    }

    public void onMultiselectCompleted(int tag, List<Integer> ids) {
        String strIDs = "";
        for (int i = 0; i < ids.size(); i++) {
            strIDs += ids.get(i) + ((i != ids.size() - 1) ? "," : "");
        }
        System.out.println("Returned IDs" + strIDs);
        switch (tag) {
            case SimpleContainer.CONTAINER_TYPE_LOCATIONS:
                arrFilterLocationIDs = ids;
                break;
            case SimpleContainer.CONTAINER_TYPE_COMPANIES:
                arrFilterCompanyIDs = ids;
                break;
            case SimpleContainer.CONTAINER_TYPE_INDUSTRIES:
                arrFilterIndustryIDs = ids;
                break;
            case SimpleContainer.CONTAINER_TYPE_SALARY_RANGES:
                arrFilterSalaryIDs = ids;
                break;
            case SimpleContainer.CONTAINER_TYPE_EDUCATION2:
                idHighestEducation = ids.get(0);
                break;
            case SimpleContainer.CONTAINER_TYPE_SKILLS2:
                arrSkillsIDs = ids;
                break;
            case SimpleContainer.CONTAINER_TYPE_LANGUAGES2:
                arrLanguagesIDs = ids;
                break;
            default:
                return;
        }
        refreshStoresText(tag, ids);
    }

    private void refreshStoresText(int tag, List<Integer> ids) {
        TextView textViewToUpdate;
        SimpleContainer listElements = null;
        CompanyContainer companyList = null;
        switch (tag) {
            case SimpleContainer.CONTAINER_TYPE_LOCATIONS:
                textViewToUpdate = txtFilterLocation;
                listElements = Commons.locationList;
                break;
            case SimpleContainer.CONTAINER_TYPE_COMPANIES:
                textViewToUpdate = txtFilterCompany;
                companyList = Commons.companyList;
                break;
            case SimpleContainer.CONTAINER_TYPE_INDUSTRIES:
                listElements = Commons.industryList;
                textViewToUpdate = txtFilterIndustry;
                break;
            case SimpleContainer.CONTAINER_TYPE_SALARY_RANGES:
                listElements = Commons.salaryRangeList;
                textViewToUpdate = txtFilterSalary;
                break;
            case SimpleContainer.CONTAINER_TYPE_EDUCATION2:
                textViewToUpdate = txtHighestEducation;
                listElements = Commons.educationList;
                break;
            case SimpleContainer.CONTAINER_TYPE_SKILLS2:
                textViewToUpdate = txtSkills;
                listElements = Commons.skillsList;
                break;
            case SimpleContainer.CONTAINER_TYPE_LANGUAGES2:
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
            if (listElements != null)
                textViewToUpdate
                        .append(listElements.getBlockByID(ids.get(i)).getTitle());
            else if (companyList != null)
                textViewToUpdate
                        .append(companyList.getCompanyByID(ids.get(i)).getName());
            if (i != ids.size() - 1)
                textViewToUpdate.append(", ");
        }
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        // hide filter first
        if (layoutFilter.isShown()) {
            layoutFilter.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white));
            } else {
                imgTabFilter.setImageDrawable(getResources().getDrawable(R.drawable.ic_tab_filter_white, null));
            }
            return;
        }

        doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT)
                .show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2500);
    }

}
