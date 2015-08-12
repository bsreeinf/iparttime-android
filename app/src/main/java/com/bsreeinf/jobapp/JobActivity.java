package com.bsreeinf.jobapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
    private JobsContainer jobs;
    private String user_id;
    private Context context;
    private int position = 0;
    private Typeface font, fontBold;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job);
        jobs = JobsActivity.jobs;
        user_id = JobsActivity.user_id;
        context = this;
        position = getIntent().getIntExtra("position", 0);
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");


        final TextView txtCompanyName = (TextView) findViewById(R.id.txtCompanyName);
        final TextView txtJobTitle = (TextView) findViewById(R.id.txtJobTitle);
        final TextView txtPostedOn = (TextView) findViewById(R.id.txtPostedOn);
        final TextView txtJobDescription = (TextView) findViewById(R.id.txtJobDescription);
        final TextView txtJobLocation = (TextView) findViewById(R.id.txtJobLocation);
        final TextView txtSalaryOffered = (TextView) findViewById(R.id.txtSalaryOffered);
        final TextView txtContactName = (TextView) findViewById(R.id.txtContactName);
        final TextView txtContactPhone = (TextView) findViewById(R.id.txtContactPhone);
        final TextView txtContactEmail = (TextView) findViewById(R.id.txtContactEmail);

        txtCompanyName.setTypeface(fontBold);
        txtJobTitle.setTypeface(font);
        txtPostedOn.setTypeface(font);
        txtJobDescription.setTypeface(font);
        txtJobLocation.setTypeface(font);
        txtSalaryOffered.setTypeface(font);
        txtContactName.setTypeface(fontBold);
        txtContactPhone.setTypeface(font);
        txtContactEmail.setTypeface(font);

        txtContactPhone.setPaintFlags(txtContactPhone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        txtContactEmail.setPaintFlags(txtContactEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Button btnSave = (Button) findViewById(R.id.btnSave);
        Button btnApply = (Button) findViewById(R.id.btnApply);

        txtCompanyName.setText(Commons.companyList.getCompanyByID(jobs.getJob(position).getCompany_id()).getName().toUpperCase());
        txtJobTitle.setText(jobs.getJob(position).getTitle());
        txtPostedOn.setText(jobs.getJob(position).getPosted_date());
        txtJobDescription.setText(jobs.getJob(position).getJob_description());
        txtJobLocation.setText(Commons.locationList.getBlockByID(jobs.getJob(position).getLocation_city()).getTitle());
        txtSalaryOffered.setText("\u20B9 " + jobs.getJob(position).getSalary_offered());
        txtContactName.setText(jobs.getJob(position).getContact_person_name());
        txtContactPhone.setText(jobs.getJob(position).getContact_person_phone());
        txtContactEmail.setText(jobs.getJob(position).getContact_person_email());

        txtContactPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + txtContactPhone.getText().toString().trim()));
                    startActivity(intent);
                } catch (Exception e) {
                    Toast.makeText(context, "Unable to dial txtContactPhone.getText().toString().trim()", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtContactEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", txtContactEmail.getText().toString(), null));
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "Send email"));
            }
        });

        findViewById(R.id.btnDismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = Commons.getCustomProgressDialog(context);
                JsonObject requestJson = new JsonObject();
                requestJson.addProperty("user_id", user_id);
                requestJson.addProperty("job_id", jobs.getJob(position).getJob_id());
                requestJson.addProperty("status", SavedAppliedJobsContainer.JOB_STATUS_SAVED);
                Log.d("Ion Request", "Request Json is : " + requestJson.toString());
                Ion.with(getApplicationContext())
                        .load(Commons.HTTP_POST, Commons.URL_SAVED_APPLIED_JOBS)
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
                                Toast.makeText(context, "Job saved", Toast.LENGTH_LONG).show();
                            }
                        });
            }
        });
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progress = Commons.getCustomProgressDialog(context);
                JsonObject requestJson = new JsonObject();
                requestJson.addProperty("user_id", user_id);
                requestJson.addProperty("job_id", jobs.getJob(position).getJob_id());
                requestJson.addProperty("status", SavedAppliedJobsContainer.JOB_STATUS_APPLIED);
                Log.d("Ion Request", "Request Json is : " + requestJson.toString());
                Ion.with(getApplicationContext())
                        .load(Commons.HTTP_POST, Commons.URL_SAVED_APPLIED_JOBS)
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
                                Toast.makeText(context, "Applied for job", Toast.LENGTH_LONG).show();
                            }
                        });
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
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
