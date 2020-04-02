package com.tutee.ak47.app.application;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

public class TuteeOffline extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //Firebase offline
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);


        //load image offline - picasso library
        Picasso.Builder builder=new Picasso.Builder(this);
        builder.downloader(new OkHttp3Downloader(this,Integer.MAX_VALUE));
        Picasso built=builder.build();
        built.setIndicatorsEnabled(true);
        built.setLoggingEnabled(true);
        Picasso.setSingletonInstance(built);
    }
}
