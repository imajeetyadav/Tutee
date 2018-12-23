package com.tutee.ak47.app;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class TuteeImageView extends AppCompatActivity {
    private Toolbar mToolbar;
    private String photoViewIntentReceiver;
    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_tutee_image_view);

        photoViewIntentReceiver=getIntent().getExtras().get("visit_user_image").toString();

        mToolbar=(Toolbar)findViewById(com.tutee.ak47.app.R.id.image_view_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        photoView=(ImageView)findViewById(com.tutee.ak47.app.R.id.display_photo);
        Picasso.get().load(photoViewIntentReceiver).placeholder(com.tutee.ak47.app.R.drawable.profile).into(photoView);

    }
}
