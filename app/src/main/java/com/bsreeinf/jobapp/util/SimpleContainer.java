package com.bsreeinf.jobapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsreeinf on 03/07/15.
 */
public class SimpleContainer {
    public static final int CONTAINER_TYPE_SKILLS = 0;
    public static final int CONTAINER_TYPE_LANGUAGES = 1;
    public static final int CONTAINER_TYPE_EDUCATION = 2;
    public static final int CONTAINER_TYPE_SALARY_RANGES = 3;
    public static final int CONTAINER_TYPE_JOB_FUNCTIONS = 4;
    public static final int CONTAINER_TYPE_LOCATIONS = 5;
    public static final int CONTAINER_TYPE_INDUSTRIES = 6;
    public static final int CONTAINER_TYPE_COMPANIES = 7;
    public static final int CONTAINER_TYPE_SKILLS2 = 8;
    public static final int CONTAINER_TYPE_LANGUAGES2 = 9;
    public static final int CONTAINER_TYPE_EDUCATION2 = 10;

    private int tag;
    private List<SimpleBlock> elements;

    public SimpleContainer(final int tag, final JsonArray data) {
        this.tag = tag;
        elements = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            elements.add(new SimpleBlock(data.get(i).getAsJsonObject()));
        }
    }

    public SimpleBlock getBlockByID(String id) {
        for (int i = 0; i < elements.size(); i++) {
            if (id.equals(elements.get(i).getId()))
                return elements.get(i);
        }
        return null;
    }

    public List<SimpleBlock> getElementList() {
        return elements;
    }

    public int getTag() {
        return tag;
    }

    public static class SimpleBlock {
        private String id, title;

        public SimpleBlock(final JsonObject element) {
            this.id = element.get("id").getAsString();
            this.title = element.get("title").getAsString();
        }

        public SimpleBlock(final String id, final String title) {
            this.id = id;
            this.title = title;
        }

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }
    }
}
