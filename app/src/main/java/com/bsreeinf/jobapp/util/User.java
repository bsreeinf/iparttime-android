package com.bsreeinf.jobapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsreeinf on 25/11/15.
 */
public class User {
    private int id;
    private Integer qualification_id;
    private String name;
    private String email;
    private String phone;
    private List<Integer> skills;
    private List<Integer> languages;
    private SavedAppliedJobsContainer savedAppliedJobs;

    public User(JsonObject data) {
        this.skills = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.savedAppliedJobs = new SavedAppliedJobsContainer(data.get("saved_applied_jobs").getAsJsonArray());

        this.id = data.get("id").getAsInt();
        this.qualification_id = data.get("qualification_id").toString().equals("null") ? -1 : data.get("qualification_id").getAsInt();
        this.name = data.get("name").getAsString();
        this.email = data.get("email").getAsString();
        this.phone = data.get("phone").getAsString();

        JsonArray skillsJson = data.get("skills").getAsJsonArray();
        for (int i = 0; i < skillsJson.size(); i++) {
            skills.add(skillsJson.get(i).getAsJsonObject().get("skill_id").getAsInt());
        }
        JsonArray languagesJson = data.get("languages").getAsJsonArray();
        for (int i = 0; i < languagesJson.size(); i++) {
            languages.add(languagesJson.get(i).getAsJsonObject().get("language_id").getAsInt());
        }
    }

    public int getId() {
        return id;
    }

    public int getQualification_id() {
        return qualification_id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public List<Integer> getSkills() {
        return skills;
    }

    public List<Integer> getLanguages() {
        return languages;
    }

    public SavedAppliedJobsContainer getSavedAppliedJobs() {
        return savedAppliedJobs;
    }

    public void setSavedAppliedJobs(JsonArray savedAppliedJobs) {
        this.savedAppliedJobs = new SavedAppliedJobsContainer(savedAppliedJobs);
    }
}