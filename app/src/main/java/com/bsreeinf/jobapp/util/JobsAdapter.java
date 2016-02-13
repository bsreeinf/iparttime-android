package com.bsreeinf.jobapp.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bsreeinf.jobapp.R;

import java.util.List;

public class JobsAdapter extends ArrayAdapter<SimpleContainer> {
    private final List<JobsContainer.Job> listItems;
    private LayoutInflater inflater;
    private JobsContainer.Job currBlock;
    private Typeface font, fontBold;

    public JobsAdapter(Context context, List<JobsContainer.Job> listItems) {
        super(context, R.layout.simple_row_layout);
        this.listItems = listItems;
        this.inflater = LayoutInflater.from(context);
//        private Typeface font, fontBold;
        font = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Light.ttf");
        fontBold = Typeface.createFromAsset(context.getAssets(), "fonts/Raleway-Bold.ttf");
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null || convertView.getTag() == null) {
            convertView = inflater.inflate(R.layout.layout_jobs_row_single,
                    parent, false);
            holder = new ViewHolder();
            holder.txtTitle = (TextView) convertView.findViewById(R.id.lblJobTitle);
            holder.txtJobLocation = (TextView) convertView.findViewById(R.id.lblJobLocation);
            holder.txtPostDate = (TextView) convertView.findViewById(R.id.lblPostDate);
            holder.txtJobDescription = (TextView) convertView.findViewById(R.id.lblJobDescription);
            holder.txtCompany = (TextView) convertView.findViewById(R.id.lblCompany);
            holder.txtSalary = (TextView) convertView.findViewById(R.id.lblSalary);
            holder.txtSalaryType = (TextView) convertView.findViewById(R.id.lblSalaryType);
            holder.imgCompanyLogo = (ImageView) convertView.findViewById(R.id.imgCompanyLogo);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        currBlock = listItems.get(position);
        holder.txtTitle.setText(currBlock.getTitle());
        holder.txtJobLocation.setText(Commons.locationList.getBlockByID(currBlock.getLocation_id()).getTitle());
        holder.txtPostDate.setText(currBlock.getPosted_date());
        holder.txtCompany.setText(Commons.companyList.getCompanyByID(currBlock.getCompany_id()).getName().toUpperCase());

        String jobDescription = currBlock.getJob_description();
        jobDescription = jobDescription.length() > 40 ? jobDescription.substring(0, 30) + " ..." : jobDescription;
        holder.txtJobDescription.setText(jobDescription);
        holder.txtSalary.setText("\u20B9 " + currBlock.getSalary_offered());
        if (!Commons.companyList.getCompanyByID(currBlock.getCompany_id()).getLogo_url().trim().equals("")) {
            try {
                new Commons.DownloadImageTask(holder.imgCompanyLogo)
                        .execute(Commons.companyList.getCompanyByID(currBlock.getCompany_id()).getLogo_url().trim());
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        String salaryType;
        switch (currBlock.getSalary_type()) {
            case "d":
                salaryType = "PER DAY";
                break;
            case "w":
                salaryType = "PER WEEK";
                break;
            case "m":
                salaryType = "PER MONTH";
                break;
            default:
                salaryType = "";

        }
        holder.txtSalaryType.setText(salaryType);
//        holder.txtJobDescription.setVisibility(View.INVISIBLE);

        holder.txtTitle.setTypeface(font);
        holder.txtJobLocation.setTypeface(font);
        holder.txtPostDate.setTypeface(font);
        holder.txtJobDescription.setTypeface(font);
        holder.txtCompany.setTypeface(font);
        holder.txtSalary.setTypeface(font);
        holder.txtSalaryType.setTypeface(fontBold);

        int pos = -1;
//        pos = DialogModifyRoster.getmSelectedItem();

        if (pos == position) {
            convertView.setBackgroundColor(Color.LTGRAY);
            holder.txtTitle.setTextColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            holder.txtTitle.setTextColor(Color.DKGRAY);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    private class ViewHolder {
        public TextView txtTitle, txtJobLocation, txtPostDate, txtJobDescription, txtCompany, txtSalary, txtSalaryType;
        public ImageView imgCompanyLogo;
    }
}
