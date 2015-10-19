package com.bsreeinf.jobapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsreeinf on 13/10/15.
 */
public class Questionnaire {
    private List<Question> questions;

    public Questionnaire(JsonArray questionnaire) {
        questions = new ArrayList<>();
        JsonArray questionnaireArray = questionnaire;
        for (int i = 0; i < questionnaireArray.size(); i++) {
            questions.add(new Question(questionnaireArray.get(i).getAsJsonObject()));
        }
    }

    public int getQuestionCount() {
        return questions.size();
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public Question getQuestion(int position){
        return questions.get(position);
    }

    /* Internal classes */

    public class Question {
        private int id;
        private String question;
        private List<Option> options;

        public Question(JsonObject questionStub) {
            this.id = questionStub.get("id").getAsInt();
            this.question = questionStub.get("question").getAsString();
            JsonArray optionsArray = questionStub.get("options").getAsJsonArray();
            options = new ArrayList<>();
            for (int i = 0; i < optionsArray.size(); i++) {
                options.add(new Option(optionsArray.get(i).getAsJsonObject()));
            }
        }

        public int getId() {
            return id;
        }

        public String getQuestion() {
            return question;
        }

        public int getOptionCount() {
            return options.size();
        }

        public List<Option> getOptions() {
            return options;
        }

        public Option getOption(int position){
            return  options.get(position);
        }

    }

    public class Option {
        private int id;
        private String option;

        public Option(JsonObject option) {
            this.id = option.get("id").getAsInt();
            this.option = option.get("option").getAsString();
        }

        public int getId() {
            return id;
        }

        public String getOption() {
            return option;
        }
    }
}
