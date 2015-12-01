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
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bsreeinf.jobapp.util.Commons;
import com.bsreeinf.jobapp.util.JobsContainer;
import com.bsreeinf.jobapp.util.Questionnaire;
import com.bsreeinf.jobapp.util.SavedAppliedJobsContainer;
import com.daimajia.swipe.SwipeLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class QuestionnaireActivity extends Activity {

    private static final String TAG = "JobActivity";
    private final boolean isLeftRight = true;
    private final boolean autoMoveToNextQuestion = true;
    private JobsContainer.Job job;
    private Context context;
    private Typeface font, fontBold;
    private Map<Integer, Integer> answers;
    private Map<Integer, List<SwipeLayout>> refOptions;
    private int questionCount;
    private float lastTouch;
    private ViewFlipper viewFlipper;
    private LinearLayout layoutSummary;
    private int viewCount;
    private TextView layoutPagination;
    private int entry_a;
    private int exit_a;
    private int entry_b;
    private int exit_b;


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

        if (isLeftRight) {
            entry_a = R.anim.slide_left_in;
            exit_a = R.anim.slide_left_out;
            entry_b = R.anim.slide_right_in;
            exit_b = R.anim.slide_right_out;
        } else {
            entry_a = R.anim.slide_up_in;
            exit_a = R.anim.slide_up_out;
            entry_b = R.anim.slide_down_in;
            exit_b = R.anim.slide_down_out;
        }

        context = this;
        job = JobsActivity.jobs.getJobByID(getIntent().getIntExtra("jobID", 0));
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");
        answers = new HashMap<>();
        refOptions = new HashMap<>();
//        requestQuestionnaire();


        viewFlipper = (ViewFlipper) findViewById(R.id.layoutQuestionnaire);
        layoutSummary = (LinearLayout) findViewById(R.id.layoutSummary);
        layoutPagination = (TextView) findViewById(R.id.layoutPagination);

        renderQuestionnaire(job.getQuestionnaire());

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

            final LinearLayout layoutOptions = (LinearLayout) layoutQuestion.findViewById(R.id.layoutOptions);
            refOptions.put(i, new ArrayList<SwipeLayout>());
            for (int j = 0; j < question.getOptionCount(); j++) {
                Questionnaire.Option option = question.getOption(j);

                // Inflate a layout for option and set data
                SwipeLayout layoutOption = (SwipeLayout) LayoutInflater
                        .from(context)
                        .inflate(R.layout.layout_questionnaire_options_row, null);
                final TextView txtBottomOption = (TextView) layoutOption.findViewById(R.id.txtBottomOption);
                final TextView txtSurfaceOption = (TextView) layoutOption.findViewById(R.id.txtSurfaceOption);
//                LinearLayout layoutBottomOption = (LinearLayout) layoutOptions.findViewById(R.id.layoutBottomOption);

                txtSurfaceOption.setText(option.getOption());
                txtSurfaceOption.setTypeface(font);
                txtBottomOption.setText(option.getOption());
                txtBottomOption.setTypeface(font);

                layoutOption.setShowMode(SwipeLayout.ShowMode.LayDown);
                //add drag edge.(If the BottomView has 'layout_gravity' attribute, this line is unnecessary)
                layoutOption.addDrag(SwipeLayout.DragEdge.Left, txtBottomOption);
                refOptions.get(i).add(layoutOption);
                layoutOption.setRightSwipeEnabled(false);
                final int finalJ = j;
                layoutOption.addSwipeListener(new SwipeLayout.SwipeListener() {
                    @Override
                    public void onStartOpen(SwipeLayout layout) {
                        List<SwipeLayout> tempRefList = refOptions.get(finalI);
                        for (int k = 0; k < tempRefList.size(); k++) {
                            if (tempRefList.get(k).getOpenStatus() == SwipeLayout.Status.Open
                                    && k != finalJ)
                                tempRefList.get(k).close();

                        }
                    }

                    @Override
                    public void onOpen(SwipeLayout layout) {
                        // close other open options

//                        txtBottomOption.setVisibility(View.VISIBLE);

                        Log.d(TAG, "clicked " + finalI + " : " + finalJ);
                        answers.put(finalI, question.getOption(finalJ).getId());
                        ((TextView) layoutSummary.getChildAt(finalI).findViewById(R.id.lblQuestion))
                                .setText((finalI + 1) + ". " + question.getOption(finalJ).getOption());

                        // Auto-move to next question
                        if (autoMoveToNextQuestion) {
                            viewFlipper.setInAnimation(context, entry_a);
                            viewFlipper.setOutAnimation(context, exit_a);
                            viewFlipper.showNext();
                        }
                        setSelectedPage(viewFlipper.getDisplayedChild());
                    }

                    @Override
                    public void onStartClose(SwipeLayout layout) {

                    }

                    @Override
                    public void onClose(SwipeLayout layout) {
                        answers.remove(finalI);
                    }

                    @Override
                    public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {

                    }

                    @Override
                    public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {

                    }
                });

                // Add option to the view
                layoutOptions.addView(layoutOption);
            }
            viewFlipper.addView(layoutQuestion, viewFlipper.getChildCount() - 1);

        }
        // Add summary page here

        viewFlipper.showNext();
        viewFlipper.showPrevious();
        viewCount = viewFlipper.getChildCount();
        setSelectedPage(0);
        findViewById(R.id.lblSummary).setVisibility(View.VISIBLE);

        Button btnPrevQuestion = (Button) findViewById(R.id.btnPrevQuestion);
        Button btnNextQuestion = (Button) findViewById(R.id.btnNextQuestion);
        btnPrevQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPrevQuestion();
            }
        });
        btnNextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewNextQuestion();
            }
        });
    }

    private void requestQuestionnaire() {
        final ProgressDialog progress = Commons.getCustomProgressDialog(context);
        Ion.with(getApplicationContext())
                .load(Commons.HTTP_GET, Commons.URL_QUESTIONNAIRE)
                .setLogging("Ion Request", Log.DEBUG)
                .followRedirect(true)
                .setBodyParameter("job_id", job.get_id() + "")
                .setBodyParameter("user_id", Commons.currentUser.getId() + "")
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
                    requestJson.addProperty("user_id", Commons.currentUser.getId());
                    requestJson.addProperty("job_id", job.get_id());
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

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                lastTouch = event.getY();
//                break;
//            case MotionEvent.ACTION_UP:
//                float currentTouch = event.getY();
//                // Handling left to right screen swap.
//                if (lastTouch < currentTouch) {
//                    // If there aren't any other children, just break.
//                    if (viewFlipper.getDisplayedChild() == 0)
//                        break;
//                    viewFlipper.setInAnimation(context, entry_b);
//                    viewFlipper.setOutAnimation(context, exit_b);
//                    viewFlipper.showPrevious();
//                    setSelectedPage(viewFlipper.getDisplayedChild());
//                }
//                if (lastTouch > currentTouch) {
//                    if (viewFlipper.getDisplayedChild() == viewCount - 1)
//                        break;
//                    viewFlipper.setInAnimation(context, entry_a);
//                    viewFlipper.setOutAnimation(context, exit_a);
//                    viewFlipper.showNext();
//                    setSelectedPage(viewFlipper.getDisplayedChild());
//                }
//                break;
//        }
//        return false;
//    }

    private void viewPrevQuestion() {
        if (viewFlipper.getDisplayedChild() == 0)
            return;
        viewFlipper.setInAnimation(context, entry_b);
        viewFlipper.setOutAnimation(context, exit_b);
        viewFlipper.showPrevious();
        setSelectedPage(viewFlipper.getDisplayedChild());
    }

    private void viewNextQuestion() {
        if (viewFlipper.getDisplayedChild() == viewCount - 1)
            return;
        viewFlipper.setInAnimation(context, entry_a);
        viewFlipper.setOutAnimation(context, exit_a);
        viewFlipper.showNext();
        setSelectedPage(viewFlipper.getDisplayedChild());
    }

    private void setSelectedPage(int index) {
        if (index == viewCount - 1) {
            layoutPagination.setText("");
//            layoutPagination.setVisibility(View.GONE);
        } else {
//            layoutPagination.setVisibility(View.VISIBLE);
            layoutPagination.setText("Question " + (index + 1) + " of " + (viewCount - 1));
        }
    }
}