package com.bsreeinf.jobapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by bsreeinf on 03/07/15.
 */
public class SimpleContainer {
    public static final int CONTAINER_TYPE_SKILLS = 0;
    public static final int CONTAINER_TYPE_LANGUAGES = 1;
    public static final int CONTAINER_TYPE_EDUCATION = 2;
    public static final int CONTAINER_TYPE_SALARY_RANGES = 3;
    public static final int CONTAINER_TYPE_LOCATIONS = 4;
    public static final int CONTAINER_TYPE_INDUSTRIES = 5;
    public static final int CONTAINER_TYPE_COMPANIES = 6;
    public static final int CONTAINER_TYPE_SKILLS2 = 7;
    public static final int CONTAINER_TYPE_LANGUAGES2 = 8;
    public static final int CONTAINER_TYPE_EDUCATION2 = 9;

    private int tag;
    private List<SimpleBlock> elements;
    private HashMap<Integer, SimpleBlock> idMap;

    public SimpleContainer(final int tag, final JsonArray data) {
        this.tag = tag;
        elements = new ArrayList<>();
        idMap = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            elements.add(new SimpleBlock(data.get(i).getAsJsonObject()));
            idMap.put(elements.get(i).getId(), elements.get(i));
        }
    }

    public SimpleBlock getBlockByID(int id) {
        return idMap.get(id);
    }

    public List<SimpleBlock> getElementList() {
        return elements;
    }

    public int getTag() {
        return tag;
    }

    public String getTitles(List<Integer> ids) {
        String result = "";
        if (ids == null)
            result = getTitles();
        else if (ids.size() == 0)
            result = "None Selected";
        else {
            for (int i = 0; i < ids.size(); i++) {
                result += getBlockByID(ids.get(i)).description + (i == ids.size() - 1 ? "" : ", ");
            }
        }
        return result;
    }

    public String getTitles() {
        String result = "";
        if (elements.size() == 0)
            result = "None Selected";
        else {
            for (int i = 0; i < elements.size(); i++) {
                result += elements.get(i).description + (i == elements.size() - 1 ? "" : ", ");
            }
        }
        return result;
    }

    public static class SimpleBlock {
        private int id;
        private String description;

        public SimpleBlock(final JsonObject element) {
            this.id = element.get("id").getAsInt();
            if (!element.has("description")) {
                this.description = element.get("min_amount").getAsString() + " - " + element.get("max_amount").getAsString();
            } else {
                this.description = element.get("description").getAsString();
            }
        }

        public SimpleBlock(final int id, final String title) {
            this.id = id;
            this.description = title;
        }

        public int getId() {
            return id;
        }

        public String getTitle() {
            return description;
        }
    }
}
