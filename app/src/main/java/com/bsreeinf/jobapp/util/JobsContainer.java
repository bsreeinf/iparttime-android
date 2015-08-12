package com.bsreeinf.jobapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsreeinf on 06/07/15.
 */
public class JobsContainer {
    private List<Job> listJobs;

    public JobsContainer(JsonArray jobs) {
        listJobs = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            listJobs.add(new Job(jobs.get(i).getAsJsonObject()));
        }
    }

    public List<Job> getListJobs() {
        return listJobs;
    }

    public Job getJob(int index) {
        return listJobs.get(index);
    }

    public int getPosition(String jobID) {
        for (int i = 0; i < listJobs.size(); i++) {
            if (listJobs.get(i).getJob_id().equals(jobID))
                return i;
        }
        return 0;
    }

    public class Job {
        private String job_id;
        private String company_id;
        private String title;
        private String location_city;
        private String location_postal_code;
        private String industry;
        private String job_function;
        private String salary_type;
        private String salary_offered;
        private String job_description;
        private String contact_person_name;
        private String contact_person_phone;
        private String contact_person_email;
        private String posted_date;
        private boolean is_online;

        public Job(JsonObject objJob) {
            this.job_id = objJob.get("id").getAsString();
            this.company_id = objJob.get("company_id").getAsString();
            this.title = objJob.get("title").getAsString();
            this.location_city = objJob.get("location_city").getAsString();
            this.location_postal_code = objJob.get("location_postal_code").getAsString();
            this.industry = objJob.get("industry").getAsString();
            this.job_function = objJob.get("job_function").getAsString();
            this.salary_type = objJob.get("salary_type").getAsString();
            this.salary_offered = objJob.get("salary_offered").getAsString();
            this.job_description = objJob.get("job_description").getAsString();
            this.contact_person_name = objJob.get("contact_person_name").getAsString();
            this.contact_person_phone = objJob.get("contact_person_phone").getAsString();
            this.contact_person_email = objJob.get("contact_person_email").getAsString();
            String UTCDate = Commons.getFormattedTimestamp(
                    Commons.parseUTCToLocal(
                            objJob.get("created_at")
                                    .getAsString()
                                    .replace('T', ' ')
                                    .substring(0, 18)
                    )
            );
            this.posted_date = UTCDate;
            this.is_online = objJob.get("is_online").getAsBoolean();
        }

        public String getJob_id() {
            return job_id;
        }

        public String getCompany_id() {
            return company_id;
        }

        public String getTitle() {
            return title;
        }

        public String getLocation_city() {
            return location_city;
        }

        public String getLocation_postal_code() {
            return location_postal_code;
        }

        public String getIndustry() {
            return industry;
        }

        public String getJob_function() {
            return job_function;
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

        public boolean is_online() {
            return is_online;
        }
    }
}
