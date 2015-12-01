package com.bsreeinf.jobapp.util;

import android.util.Log;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bsreeinf on 06/07/15.
 */
public class SavedAppliedJobsContainer {
    public static final int JOB_STATUS_APPLIED = 1;
    public static final int JOB_STATUS_SAVED = 2;
    public static final int JOB_STATUS_ACCEPTED = 3;
    public static final int JOB_STATUS_REJECTED = 4;
    private List<SavedAppliedStub> listSavedAppliedJobs;
    private HashMap<Integer, SavedAppliedStub> map;
    private List<Integer> savedJobIDs;
    private List<Integer> appliedJobIDs;


    public SavedAppliedJobsContainer(JsonArray arrSavedAppliedJobs) {
        listSavedAppliedJobs = new ArrayList<>();
        savedJobIDs = new ArrayList<>();
        appliedJobIDs = new ArrayList<>();

        map = new HashMap<>();
        for (int i = 0; i < arrSavedAppliedJobs.size(); i++) {
            listSavedAppliedJobs.add(new SavedAppliedStub(arrSavedAppliedJobs.get(i).getAsJsonObject()));
            map.put(listSavedAppliedJobs.get(i).getId(), listSavedAppliedJobs.get(i));

            if (listSavedAppliedJobs.get(i).getStatus() == SavedAppliedJobsContainer.JOB_STATUS_SAVED) {
                savedJobIDs.add(listSavedAppliedJobs.get(i).getId());
                Log.d("SAJ","Added Saved : " + listSavedAppliedJobs.get(i).getJob_id());
            } else if (listSavedAppliedJobs.get(i).getStatus() == SavedAppliedJobsContainer.JOB_STATUS_APPLIED) {
                appliedJobIDs.add(listSavedAppliedJobs.get(i).getJob_id());
                Log.d("SAJ", "Added Applied : " + listSavedAppliedJobs.get(i).getId());
            }
        }
    }

    public List<SavedAppliedStub> getSavedAppliedListJobs() {
        return listSavedAppliedJobs;
    }

    public List<Integer> getSavedJobIDs() {
        return savedJobIDs;
    }

    public List<Integer> getAppliedJobIDs() {
        return appliedJobIDs;
    }

    public SavedAppliedStub getSavedAppliedStub(int index) {
        return listSavedAppliedJobs.get(index);
    }

    public SavedAppliedStub getMatchingSavedAppliedStub(int job_id) {
        return map.get(job_id);
    }

    public class SavedAppliedStub {
        private int id;
        private int job_id;
        private int status;

        public SavedAppliedStub(JsonObject objJob) {
            this.id = objJob.get("id").getAsInt();
            this.job_id = objJob.get("job_id").getAsInt();
            this.status = objJob.get("job_status_id").getAsInt();
            System.out.println("Row: " + job_id + " " + status);
        }

        public int getJob_id() {
            return job_id;
        }

        public int getId() {
            return id;
        }

        public int getStatus() {
            return status;
        }
    }
}
