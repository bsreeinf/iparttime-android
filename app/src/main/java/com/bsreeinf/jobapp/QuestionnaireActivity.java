package com.bsreeinf.jobapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bsreeinf.jobapp.util.Commons;
import com.bsreeinf.jobapp.util.JobsContainer;
import com.bsreeinf.jobapp.util.Questionnaire;
import com.bsreeinf.jobapp.util.SavedAppliedJobsContainer;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class QuestionnaireActivity extends Activity {

    private static final String TAG = "JobActivity";
    private JobsContainer jobs;
    private String user_id;
    private Context context;
    private int position = 0;
    private Typeface font, fontBold;
    private Map<Integer, Integer> answers;
    private int questionCount;

    private float lastX;
    private ViewFlipper viewFlipper;
    private LinearLayout layoutSummary;
    private int viewCount;
    private TextView layoutPagination;

    public QuestionnaireActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        try {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_questionnaire);
        jobs = JobsActivity.jobs;
        user_id = JobsActivity.user_id;
        context = this;
        position = getIntent().getIntExtra("position", 0);
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");
        answers = new HashMap<>();
        requestQuestionnaire();

        viewFlipper = (ViewFlipper) findViewById(R.id.layoutQuestionnaire);
        layoutSummary = (LinearLayout) findViewById(R.id.layoutSummary);
        layoutPagination = (TextView) findViewById(R.id.layoutPagination);

        Button btnApply = (Button) findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitJobApplication();
            }
        });
    }

    private void renderQuestionnaire(Questionnaire questionnaire) {
        questionCount = questionnaire.getQuestionCount();
        for (int i = 0; i < questionnaire.getQuestionCount(); i++) {
            final Questionnaire.Question question = questionnaire.getQuestion(i);

            final int finalI = i;
            //
            LinearLayout layoutAnswer = (LinearLayout) LayoutInflater
                    .from(context)
                    .inflate(R.layout.layout_questionnaire_row, null);
            TextView txtAnswer = (TextView) layoutAnswer.findViewById(R.id.lblQuestion);
            txtAnswer.setText((i + 1) + ". Not selected");
            txtAnswer.setTypeface(font);
            txtAnswer.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
            txtAnswer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (int k = viewCount - 1; k > finalI; k--) {
                        viewFlipper.setInAnimation(null);
                        viewFlipper.setOutAnimation(null);
                        viewFlipper.showPrevious();
                    }
                    setSelectedPage(finalI);
                }
            });
            layoutSummary.addView(layoutAnswer);
            //

            LinearLayout layoutQuestion = (LinearLayout) LayoutInflater
                    .from(context)
                    .inflate(R.layout.layout_questionnaire_row, null);
            TextView lblQuestion = (TextView) layoutQuestion.findViewById(R.id.lblQuestion);
            lblQuestion.setText(question.getQuestion());
            lblQuestion.setTypeface(font);

            final RadioGroup layoutOptions = (RadioGroup) layoutQuestion.findViewById(R.id.layoutOptions);
            for (int j = 0; j < question.getOptionCount(); j++) {
                Questionnaire.Option option = question.getOption(j);

                RadioButton layoutOption = (RadioButton) LayoutInflater
                        .from(context)
                        .inflate(R.layout.layout_questionnaire_options_row, null);
                layoutOption.setText(option.getOption());
                layoutOption.setTypeface(font);
                layoutOptions.addView(layoutOption);
            }
            layoutOptions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, int checkedId) {
                    View radioButton = layoutOptions.findViewById(checkedId);
                    int pos = layoutOptions.indexOfChild(radioButton);
                    Log.d(TAG, "clicked " + finalI + " : " + pos);
//                    if (answers.containsKey(finalI))
                    answers.put(finalI, question.getOption(pos).getId());
                    ((TextView) layoutSummary.getChildAt(finalI).findViewById(R.id.lblQuestion))
                            .setText((finalI + 1) + ". " + question.getOption(pos).getOption());

                    // Auto-move to next question
                    viewFlipper.setInAnimation(context, R.anim.slide_left_in);
                    viewFlipper.setOutAnimation(context, R.anim.slide_left_out);
                    viewFlipper.showNext();
                    setSelectedPage(viewFlipper.getDisplayedChild());
                }
            });
            viewFlipper.addView(layoutQuestion, viewFlipper.getChildCount() - 1);


        }
        // Add summary page here

        viewFlipper.showNext();
        viewFlipper.showPrevious();
        viewCount = viewFlipper.getChildCount();
        setSelectedPage(0);
        findViewById(R.id.lblSummary).setVisibility(View.VISIBLE);
    }

    private void requestQuestionnaire() {
        final ProgressDialog progress = Commons.getCustomProgressDialog(context);
        JsonObject requestJson = new JsonObject();
        requestJson.addProperty("job_id", jobs.getJob(position).getJob_id());
        Log.d("Ion Request", "Request Json is : " + requestJson.toString());
        Ion.with(getApplicationContext())
                .load(Commons.HTTP_GET, Commons.URL_QUESTIONNAIRE)
                .setLogging("Ion Request", Log.DEBUG)
                .followRedirect(true)
                .setBodyParameter("job_id", jobs.getJob(position).getJob_id())
                .setBodyParameter("user_id", user_id)
//                .setJsonObjectBody(requestJson)
                .asJsonArray()
                .setCallback(new FutureCallback<JsonArray>() {
                    @Override
                    public void onCompleted(Exception e, JsonArray result) {
                        Log.d("Ion Request", "Completed");
                        progress.dismiss();
                        if (e != null) {
                            e.printStackTrace();
                            return;
                        }
                        if (Commons.SHOW_DEBUG_MSGS)
                            Log.d(TAG, "Ion Request " + result.toString());
                        if (Commons.SHOW_TOAST_MSGS)
                            Toast.makeText(context, "Ion Request " + result.toString(),
                                    Toast.LENGTH_LONG).show();
                        if (result.size() == 1) {
                            if (result.get(0).getAsJsonObject().has("response_status")) {
                                if (result.get(0).getAsJsonObject().get("response_status").getAsString().equals("attempt_failed")) {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("You have attempted this questionnaire before and failed." +
                                            "\nYou may not apply for this job again");
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            finish();
                                        }
                                    });
                                    builder.create().show();
                                }
                            }
                        }
                        renderQuestionnaire(new Questionnaire(result));
                    }
                });
    }

    private void submitJobApplication() {
        if (questionCount != answers.size()) {
            Toast.makeText(context, "Answer all questions", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Q Count : " + questionCount + "\n Answer given : " + answers.size());
            return;
        } else {
            String strAnswer = null;
            for (Iterator i = answers.keySet().iterator(); i.hasNext(); ) {
                strAnswer = (strAnswer == null ? "" : strAnswer + ",") + "" + answers.get(i.next());
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final TextView txtURL = new TextView(context);
            builder.setView(txtURL);
            builder.setMessage("Are you sure you want to apply for this job?");

            final String finalStrAnswer = strAnswer;
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
                    requestJson.addProperty("user_id", user_id);
                    requestJson.addProperty("job_id", jobs.getJob(position).getJob_id());
                    requestJson.addProperty("questionnaire_answers", finalStrAnswer);
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
                                        Toast.makeText(context, "Ion Request " + result.toString(),
                                                Toast.LENGTH_LONG).show();
                                    if (result.has("response_status")) {
                                        String status = result.get("response_status").getAsString();
                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                finish();
                                            }
                                        });
                                        if (status.equals("failed")) {
                                            builder.setMessage("You have failed the questionnaire." +
                                                    "\nYou may not apply for this job again");
                                        } else if (status.equals("exists")) {
                                            builder.setMessage("You have already applied for this job");
                                        }
                                        builder.create().show();
                                    } else {
                                        Toast.makeText(context, "Applied for job", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            });
            builder.create().show();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
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

    private void setSelectedPage(int index) {
        if (index == viewCount - 1)
            layoutPagination.setText("");
        else
            layoutPagination.setText("Question " + (index + 1) + " of " + (viewCount - 1));
    }
}