package com.bsreeinf.jobapp.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bsreeinf on 06/07/15.
 */
public class CompanyContainer {

    List<Company> listCoimpanies;

    public CompanyContainer(JsonArray companies) {
        listCoimpanies = new ArrayList<>();
        for (int i = 0; i < companies.size(); i++) {
            listCoimpanies.add(new Company(companies.get(i).getAsJsonObject()));
        }
    }

    public Company getCompanyByID(int id) {
        for (int i = 0; i < listCoimpanies.size(); i++) {
            if (id == listCoimpanies.get(i).getId())
                return listCoimpanies.get(i);
        }
        return null;
    }

    public List<SimpleContainer.SimpleBlock> getElementList() {
        List<SimpleContainer.SimpleBlock> elementList = new ArrayList<>();
        for (int i = 0; i < listCoimpanies.size(); i++) {
            int id = listCoimpanies.get(i).getId();
            String title = listCoimpanies.get(i).getName();
            elementList.add(new SimpleContainer.SimpleBlock(id, title));
        }
        return elementList;
    }

    public class Company {
        private int id;
        private String name;
        private String phone;
        private String email;
        private String address;

        public Company(JsonObject company) {
            this.id = company.get("id").getAsInt();
            this.name = company.get("name").getAsString();
            this.phone = company.get("phone").getAsString();
            this.email = company.get("email").getAsString();
            this.address = ""; //company.get("address") == null ? "" : company.get("address").getAsString();
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getPhone() {
            return phone;
        }

        public String getEmail() {
            return email;
        }

        public String getAddress() {
            return address;
        }
    }
}
