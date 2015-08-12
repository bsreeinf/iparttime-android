package com.bsreeinf.jobapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

/**
 * Created by bsreeinf on 28/06/15.
 */
public class TestNetwork {
    public static final int TYPE_DEFAULT = 0;

    // request/response constants
    public static final String P_MODE = "mode";
    public static final String P_INIT = "init";
    public static final String P_LOGIN = "login";
    public static final String P_REGISTRATION = "registration";
    public static final String P_USER_DATA_UPDATE = "user_data_update";
    public static final String P_GET_JOBS = "get_jobs";
    public static final String P_GET_SAVED_APPLIED_JOBS = "get_saved_applied_jobs";

    public static final String P_STATUS = "status";
    public static final String P_SUCCESS = "success";
    public static final String P_FAILED = "failed";
    public static final String P_EMAIL = "email";
    public static final String P_PASSWORD = "password";
    public static final String P_JOBS = "jobs";
    public static final String P_JOB_TITLE = "job_title";
    public static final String P_JOB_LOCATION = "job_location";
    public static final String P_JOB_POST_DATE = "job_post_date";
    public static final String P_SAVED_JOBS = "saved_jobs";
    public static final String P_APPLIED_JOBS = "applied_jobs";

    private static JsonObject job = null;


    private static final String TAG = "TestNetwork";


    // sample request generators
    public static JsonObject getSampleInitRequest(int type) {
        JsonObject request = new JsonObject();
        request.addProperty(P_MODE, P_INIT);
        return request;
    }

    public static JsonObject getSampleLoginRequest(int type, String m_email, String m_password) {
        JsonObject request = new JsonObject();
        request.addProperty(P_MODE, P_LOGIN);
        if (type == TYPE_DEFAULT) {
            request.addProperty(P_EMAIL, m_email);
            request.addProperty(P_PASSWORD, m_password);
        }

        return request;
    }

    public static JsonObject getSampleRegistrationRequest(int type) {
        JsonObject request = new JsonObject();
        request.addProperty(P_MODE, P_REGISTRATION);
        if (type == TYPE_DEFAULT) {
        }
        return request;
    }

    public static JsonObject getSampleUserDataUpdateRequest(int type) {
        JsonObject request = new JsonObject();
        request.addProperty(P_MODE, P_USER_DATA_UPDATE);
        if (type == TYPE_DEFAULT) {
        }
        return request;
    }

    public static JsonObject getSampleGetJobsRequest(int type) {
        JsonObject request = new JsonObject();
        request.addProperty(P_MODE, P_GET_JOBS);
        if (type == TYPE_DEFAULT) {
        }
        return request;
    }

    public static JsonObject getSampleGetJSavedAppliedobsRequest(int type) {
        JsonObject request = new JsonObject();
        request.addProperty(P_MODE, P_GET_SAVED_APPLIED_JOBS);
        if (type == TYPE_DEFAULT) {
        }
        return request;
    }

    // Emulated request handler
    public static JsonObject request(JsonObject requestObject) {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty(P_STATUS, P_FAILED);

        switch (requestObject.get(P_MODE).getAsString()) {
            case P_INIT:
                responseObject.addProperty(P_STATUS, P_SUCCESS);
                break;
            case P_LOGIN:
                responseObject.addProperty(P_STATUS, P_SUCCESS);
                break;
            case P_REGISTRATION:
                responseObject.addProperty(P_STATUS, P_SUCCESS);
                break;
            case P_USER_DATA_UPDATE:
                responseObject.addProperty(P_STATUS, P_SUCCESS);
                break;
            case P_GET_JOBS:
                responseObject.addProperty(P_STATUS, P_SUCCESS);
                JsonArray jobs = new JsonArray();

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Software Dev");
                job.addProperty(P_JOB_LOCATION, "Bangalore");
                job.addProperty(P_JOB_POST_DATE, "19/07/2014");
                jobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Designer");
                job.addProperty(P_JOB_LOCATION, "Dubai");
                job.addProperty(P_JOB_POST_DATE, "01/06/2015");
                jobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Architect");
                job.addProperty(P_JOB_LOCATION, "Chennai");
                job.addProperty(P_JOB_POST_DATE, "09/05/2015");
                jobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Assistant");
                job.addProperty(P_JOB_LOCATION, "Gelf");
                job.addProperty(P_JOB_POST_DATE, "19/05/2015");
                jobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Temp");
                job.addProperty(P_JOB_LOCATION, "Pune");
                job.addProperty(P_JOB_POST_DATE, "14/06/2015");
                jobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Team Lead");
                job.addProperty(P_JOB_LOCATION, "Mumbai");
                job.addProperty(P_JOB_POST_DATE, "30/03/2015");
                jobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "HR");
                job.addProperty(P_JOB_LOCATION, "Gwalior");
                job.addProperty(P_JOB_POST_DATE, "22/03/2015");
                jobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Tester");
                job.addProperty(P_JOB_LOCATION, "Hyderabad");
                job.addProperty(P_JOB_POST_DATE, "10/11/2014");
                jobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Developer");
                job.addProperty(P_JOB_LOCATION, "Kerala");
                job.addProperty(P_JOB_POST_DATE, "19/06/2015");
                jobs.add(job);

                responseObject.add(P_JOBS, jobs);
                break;
            case P_GET_SAVED_APPLIED_JOBS:
                responseObject.addProperty(P_STATUS, P_SUCCESS);
                JsonArray savedJobs = new JsonArray();
                JsonArray appliedJobs = new JsonArray();

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Software Dev");
                job.addProperty(P_JOB_LOCATION, "Bangalore");
                job.addProperty(P_JOB_POST_DATE, "19/07/2014");
                savedJobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Designer");
                job.addProperty(P_JOB_LOCATION, "Dubai");
                job.addProperty(P_JOB_POST_DATE, "01/06/2015");
                savedJobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Architect");
                job.addProperty(P_JOB_LOCATION, "Chennai");
                job.addProperty(P_JOB_POST_DATE, "09/05/2015");
                savedJobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Assistant");
                job.addProperty(P_JOB_LOCATION, "Gelf");
                job.addProperty(P_JOB_POST_DATE, "19/05/2015");
                appliedJobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Temp");
                job.addProperty(P_JOB_LOCATION, "Pune");
                job.addProperty(P_JOB_POST_DATE, "14/06/2015");
                appliedJobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Team Lead");
                job.addProperty(P_JOB_LOCATION, "Mumbai");
                job.addProperty(P_JOB_POST_DATE, "30/03/2015");
                appliedJobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "HR");
                job.addProperty(P_JOB_LOCATION, "Gwalior");
                job.addProperty(P_JOB_POST_DATE, "22/03/2015");
                appliedJobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Tester");
                job.addProperty(P_JOB_LOCATION, "Hyderabad");
                job.addProperty(P_JOB_POST_DATE, "10/11/2014");
                appliedJobs.add(job);

                job = new JsonObject();
                job.addProperty(P_JOB_TITLE, "Developer");
                job.addProperty(P_JOB_LOCATION, "Kerala");
                job.addProperty(P_JOB_POST_DATE, "19/06/2015");
                appliedJobs.add(job);

                responseObject.add(P_SAVED_JOBS, savedJobs);
                responseObject.add(P_APPLIED_JOBS,appliedJobs);

        }
        return responseObject;
    }


}
