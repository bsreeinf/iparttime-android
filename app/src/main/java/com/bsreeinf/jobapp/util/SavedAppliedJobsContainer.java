package com.bsreeinf.jobapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsreeinf on 06/07/15.
 */
public class SavedAppliedJobsContainer {
    public static final String JOB_STATUS_APPLIED = "0";
    public static final String JOB_STATUS_SAVED = "1";
    private List<SavedAppliedStub> listSavedAppliedJobs;

    public SavedAppliedJobsContainer(JsonArray arrSavedAppliedJobs) {
        listSavedAppliedJobs = new ArrayList<>();
        for (int i = 0; i < arrSavedAppliedJobs.size(); i++) {
            listSavedAppliedJobs.add(new SavedAppliedStub(arrSavedAppliedJobs.get(i).getAsJsonObject()));
        }
    }

    public List<SavedAppliedStub> getSavedAppliedListJobs() {
        return listSavedAppliedJobs;
    }

    public SavedAppliedStub getSavedAppliedStub(int index) {
        return listSavedAppliedJobs.get(index);
    }

    public SavedAppliedStub getMatchingSavedAppliedStub(String job_id, String user_id) {
        for (int i = 0; i < listSavedAppliedJobs.size(); i++) {
            SavedAppliedStub stub = listSavedAppliedJobs.get(i);
            if (stub.getJob_id().equals(job_id) && stub.getUser_id().equals(user_id)) {
                return stub;
            }
        }
        return null;
    }

    public class SavedAppliedStub {
        private String id;
        private String job_id;
        private String user_id;
        private String status;

        public SavedAppliedStub(JsonObject objJob) {
            this.id = objJob.get("id").getAsString();
            this.user_id = objJob.get("user_id").getAsString();
            this.job_id = objJob.get("job_id").getAsString();
            this.status = objJob.get("status").getAsString();
            System.out.println("Row: " + user_id + " " + job_id + " " + status);
        }

        public String getJob_id() {
            return job_id;
        }

        public String getId() {
            return id;
        }

        public String getUser_id() {
            return user_id;
        }

        public String getStatus() {
            return status;
        }
    }
}
