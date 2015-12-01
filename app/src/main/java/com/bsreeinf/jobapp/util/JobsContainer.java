package com.bsreeinf.jobapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bsreeinf on 06/07/15.
 */
public class JobsContainer {
    private List<Job> listJobs;
    private HashMap<Integer, Job> idMap;

    public JobsContainer(JsonArray jobs) {
        listJobs = new ArrayList<>();
        idMap = new HashMap<>();

        for (int i = 0; i < jobs.size(); i++) {
            listJobs.add(new Job(jobs.get(i).getAsJsonObject()));
            idMap.put(listJobs.get(i).get_id(), listJobs.get(i));
        }
    }

    public List<Job> getListJobs() {
        return listJobs;
    }

    public Job getJobByIndex(int index) {
        return listJobs.get(index);
    }

    public Job getJobByID(int jobID) {
        return idMap.get(jobID);
    }

    public class Job {


        private int id;
        private int company_id;
        private int location_id;
        private int industry_id;
        private String title;
        private String postal_code;
        private String salary_type;
        private String salary_offered;
        private String job_description;
        private String contact_person_name;
        private String contact_person_phone;
        private String contact_person_email;
        private String posted_date;

        private Questionnaire questionnaire;
//        private boolean is_online;

        public Job(JsonObject objJob) {
            this.id = objJob.get("id").getAsInt();
            this.company_id = objJob.get("company_id").getAsInt();
            this.industry_id = objJob.get("industry_id").getAsInt();
            this.location_id = objJob.get("location_id").getAsInt();

            this.title = objJob.get("title").getAsString();
            this.postal_code = objJob.get("postal_code").getAsString();
            this.salary_type = objJob.get("salary_type").getAsString();
            this.salary_offered = objJob.get("salary_offered").getAsString();
            this.job_description = objJob.get("job_description").getAsString();
            this.contact_person_name = objJob.get("contact_person_name").getAsString();
            this.contact_person_phone = objJob.get("contact_person_phone").getAsString();
            this.contact_person_email = objJob.get("contact_person_email").getAsString();
            this.posted_date = Commons.getFormattedTimestamp(
                    Commons.parseUTCToLocal(
                            objJob.get("created_at")
                                    .getAsString()
                                    .replace('T', ' ')
                                    .substring(0, 18)
                    )
            );
            this.questionnaire = new Questionnaire(objJob.get("questionnaires").getAsJsonArray());
//            this.is_online = objJob.get("is_online").getAsBoolean();
        }


        public int get_id() {
            return id;
        }

        public int getCompany_id() {
            return company_id;
        }

        public int getIndustry_id() {
            return industry_id;
        }

        public int getLocation_id() {
            return location_id;
        }

        public String getTitle() {
            return title;
        }

        public String getLocation_postal_code() {
            return postal_code;
        }

        public String getSalary_type() {
            return salary_type;
        }

        public String getSalary_offered() {
            return salary_offered;
        }

        public String getJob_description() {
            return job_description;
        }

        public String getContact_person_name() {
            return contact_person_name;
        }

        public String getContact_person_phone() {
            return contact_person_phone;
        }

        public String getContact_person_email() {
            return contact_person_email;
        }

        public String getPosted_date() {
            return posted_date;
        }

//        public boolean is_online() {
//            return is_online;
//        }



        public Questionnaire getQuestionnaire() {
            return questionnaire;
        }
    }
}
