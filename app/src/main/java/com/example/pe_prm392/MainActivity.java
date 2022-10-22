package com.example.pe_prm392;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class MainActivity extends AppCompatActivity {
    SQLiteDatabase db;
    DbHelper helper;
    EditText jobId;
    EditText jobName;
    EditText jobStatus;
    EditText jobDes;
    TextView messText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        jobId = findViewById(R.id.jobId);
        jobName = findViewById(R.id.jobName);
        jobStatus = findViewById(R.id.jobStatus);
        jobDes = findViewById(R.id.jobDes);
        messText = findViewById(R.id.messText);
    }

    public boolean searchJob(String id) {
        helper = new DbHelper(this, null);
        db = helper.getWritableDatabase();
        String sql = "SELECT * FROM Job WHERE Id LIKE " + "\"" + jobId.getText().toString() + "\"";
        Cursor c = db.rawQuery(sql, null);
        if (c.moveToFirst()) {
            c.close();
            return true;
        } else {
            db.close();
            return false;
        }
    }

    public void addJob(View v) {
        String id = jobId.getText().toString();
        if (!id.equals("")) {
            if (!searchJob(jobId.getText().toString())) {
                helper = new DbHelper(this, null);
                db = helper.getWritableDatabase();

                String sql = "INSERT INTO Job (Id, name, status, description) VALUES (?,?,?,?) ";
                db.execSQL(sql, new String[]{
                        jobId.getText().toString(),
                        jobName.getText().toString(),
                        jobStatus.getText().toString(),
                        jobDes.getText().toString(),
                });
                db.close();
                messText.setText("\tJob has been added.");
                jobId.setText("");
                jobName.setText("");
                jobStatus.setText("");
                jobDes.setText("");
            } else {
                messText.setText("\tJob id duplicate!!!");
                jobId.setText("");
                jobName.setText("");
                jobStatus.setText("");
                jobDes.setText("");
            }
        } else {
            messText.setText("\tPlease enter all the data..");
        }
    }

    public void editJob(View v) {
        String id = jobId.getText().toString();
        if (!id.equals("")) {
            helper = new DbHelper(this, null);
            db = helper.getWritableDatabase();
            String sql = "UPDATE Job SET name = ?, status = ?, description = ? WHERE Id = ?";
            db.execSQL(sql, new String[]{
                    jobName.getText().toString(),
                    jobStatus.getText().toString(),
                    jobDes.getText().toString(),
                    jobId.getText().toString(),
            });
            db.close();
            messText.setText("\tUpdate Successfully!!!");
        } else {
            messText.setText("\tPlease enter all the data..");
        }
    }

    public void listJob(View v) {
        helper = new DbHelper(this, null);
        db = helper.getWritableDatabase();
        String sql = "SELECT * FROM Job";
        Cursor c = db.rawQuery(sql, null);

        List<Job> jobList = new ArrayList<>();
        while (c.moveToNext()) {
            @SuppressLint("Range") String id = c.getString(c.getColumnIndex("Id"));
            @SuppressLint("Range") String name = c.getString(c.getColumnIndex("name"));
            @SuppressLint("Range") String status = c.getString(c.getColumnIndex("status"));
            @SuppressLint("Range") String description = c.getString(c.getColumnIndex("description"));
            Job job = new Job(id, name, status, description);
            jobList.add(job);
        }
        db.close();

        messText.setText("\tList = " + jobList.size());

        JobAdapter adapter = new JobAdapter(jobList);
        RecyclerView rec = findViewById(R.id.rc_view);
        RecyclerView.LayoutManager layout = new LinearLayoutManager(this);
        rec.setLayoutManager(layout);
        rec.setAdapter(adapter);
    }

    public void deleteJob(View v) {
        String id = jobId.getText().toString();
        if (!id.equals("")) {
            helper = new DbHelper(this, null);
            db = helper.getWritableDatabase();
            db.delete("Job","Id =" + "\"" + id + "\"", null);
            db.close();
            messText.setText("\tDelete Success!!!");
            jobId.setText("");
        } else {
            messText.setText("\tPlease enter job id..");
        }
    }

}