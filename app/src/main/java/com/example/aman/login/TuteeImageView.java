package com.example.aman.login;

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
        setContentView(R.layout.activity_tutee_image_view);

        photoViewIntentReceiver=getIntent().getExtras().get("visit_user_image").toString();

        mToolbar=(Toolbar)findViewById(R.id.image_view_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        photoView=(ImageView)findViewById(R.id.display_photo);
        Picasso.get().load(photoViewIntentReceiver).placeholder(R.drawable.profile).into(photoView);

    }
}
