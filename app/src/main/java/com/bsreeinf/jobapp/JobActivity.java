package com.bsreeinf.jobapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsreeinf.jobapp.util.Commons;
import com.bsreeinf.jobapp.util.JobsContainer;
import com.bsreeinf.jobapp.util.SavedAppliedJobsContainer;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;


public class JobActivity extends Activity {

    private static final String TAG = "JobActivity";
    private JobsContainer.Job job;
    private Context context;
    private Typeface font, fontBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_job);
        job = MainActivity.jobs.getJobByID(getIntent().getIntExtra("jobID", 0));
        context = this;
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");


        final TextView txtCompanyName = (TextView) findViewById(R.id.txtCompanyName);
        final TextView txtJobTitle = (TextView) findViewById(R.id.txtJobTitle);
        final TextView txtPostedOn = (TextView) findViewById(R.id.txtPostedOn);
        final TextView txtJobDescription = (TextView) findViewById(R.id.txtJobDescription);
        final TextView txtJobLocation = (TextView) findViewById(R.id.txtJobLocation);
        final TextView txtSalaryOffered = (TextView) findViewById(R.id.txtSalaryOffered);
        final TextView txtContactName = (TextView) findViewById(R.id.txtContactName);
//        final TextView txtContactPhone = (TextView) findViewById(R.id.txtContactPhone);
//        final TextView txtContactEmail = (TextView) findViewById(R.id.txtContactEmail);
        final LinearLayout btnContactPhone = (LinearLayout) findViewById(R.id.btnContactPhone);
        final LinearLayout btnContactEmail = (LinearLayout) findViewById(R.id.btnContactEmail);

        txtCompanyName.setTypeface(fontBold);
        txtJobTitle.setTypeface(font);
        txtPostedOn.setTypeface(font);
        txtJobDescription.setTypeface(font);
        txtJobLocation.setTypeface(font);
        txtSalaryOffered.setTypeface(font);
        txtContactName.setTypeface(fontBold);
//        txtContactPhone.setTypeface(font);
//        txtContactEmail.setTypeface(font);

//        txtContactPhone.setPaintFlags(txtContactPhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
//        txtContactEmail.setPaintFlags(txtContactEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnApply = (Button) findViewById(R.id.btnApply);

        txtCompanyName.setText(Commons.companyList.getCompanyByID(job.getCompany_id()).getName().toUpperCase());
        txtJobTitle.setText(job.getTitle());
        txtPostedOn.setText("Posted on " + job.getPosted_date());
        txtJobDescription.setText(job.getJob_description());
        txtJobLocation.setText(Commons.locationList.getBlockByID(job.getLocation_id()).getTitle());
        txtSalaryOffered.setText("\u20B9 " + job.getSalary_offered());
        txtContactName.setText(job.getContact_person_name());
//        txtContactPhone.setText(jobs.getJob(position).getContact_person_phone());
//        txtContactEmail.setText(jobs.getJob(position).getContact_person_email());

        btnContactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + job.getContact_person_phone()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to dial txtContactPhone.getText().toString().trim()", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", job.getContact_person_email(), null));
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email"));
            }
        });

//        findViewById(R.id.btnDismiss).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = Commons.getCustomProgressDialog(context);
                JsonObject requestJson = new JsonObject();
                requestJson.addProperty("user_id", Commons.currentUser.getId());
                requestJson.addProperty("job_id", job.get_id());
                requestJson.addProperty("job_status_id", SavedAppliedJobsContainer.JOB_STATUS_SAVED);
                JsonObject finalRequest = new JsonObject();
                finalRequest.add("saved_applied_job", requestJson);
                Log.d("Ion Request", "Request Json is : " + requestJson.toString());
                Ion.with(getApplicationContext())
                        .load(Commons.HTTP_POST, Commons.URL_SAVE_APPLY_JOB)
                        .setLogging("Ion Request", Log.DEBUG)
                        .followRedirect(true)
                        .setJsonObjectBody(finalRequest)
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
                                Toast.makeText(context, "Job saved", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (job.getQuestionnaire().getQuestionCount() == 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                    final TextView txtURL = new TextView(context);
//                    builder.setView(txtURL);
                    builder.setMessage("Are you sure you want to apply for this job?");

                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final ProgressDialog progress = Commons.getCustomProgressDialog(context);
                            JsonObject requestJson = new JsonObject();
                            requestJson.addProperty("user_id", Commons.currentUser.getId());
                            requestJson.addProperty("job_id", job.get_id());
                            requestJson.addProperty("job_status_id", SavedAppliedJobsContainer.JOB_STATUS_APPLIED);
                            JsonObject finalRequest = new JsonObject();
                            finalRequest.add("saved_applied_job", requestJson);

                            Log.d("Ion Request", "Request Json is : " + requestJson.toString());
                            Ion.with(getApplicationContext())
                                    .load(Commons.HTTP_POST, Commons.URL_SAVE_APPLY_JOB)
                                    .setLogging("Ion Request", Log.DEBUG)
                                    .followRedirect(true)
                                    .setJsonObjectBody(finalRequest)
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
                                            Toast.makeText(context, "Applied for job - " + job.getTitle(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    });
                    builder.create().show();
                } else {
                    Intent intent = new Intent(context, QuestionnaireActivity.class);
                    intent.putExtra("jobID", job.get_id());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_job, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
