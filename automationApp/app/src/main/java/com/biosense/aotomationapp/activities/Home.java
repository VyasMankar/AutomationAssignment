package com.biosense.aotomationapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.biosense.aotomationapp.R;
import com.biosense.aotomationapp.adapter.UrlListAdapter;
import com.biosense.aotomationapp.db.DatabaseHelper;
import com.biosense.aotomationapp.helper.SimpleDividerItemDecoration;
import com.biosense.aotomationapp.model.url_Info_model;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    DatabaseHelper mDatabaseHelper;
    List<url_Info_model> arrayList ;
    RecyclerView mUrlRecyclerView;
    UrlListAdapter urlListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mUrlRecyclerView = (RecyclerView) findViewById(R.id.urlRecyclerView);

        //mUrlRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getApplicationContext()));
        mUrlRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        mUrlRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        mUrlRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mDatabaseHelper=new DatabaseHelper(Home.this);
        arrayList = new ArrayList<>();
        arrayList =mDatabaseHelper.getUrlData();
        urlListAdapter =new UrlListAdapter(arrayList);

        mUrlRecyclerView.setAdapter(urlListAdapter);

    }

    public void onAddUrl(View view) {
        startActivity(new Intent(this,Add_Url.class));
        finish();
    }
}
