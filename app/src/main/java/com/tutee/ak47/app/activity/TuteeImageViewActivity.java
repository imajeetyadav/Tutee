package com.tutee.ak47.app.activity;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class TuteeImageViewActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private String photoViewIntentReceiver;
    private ImageView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.tutee.ak47.app.R.layout.activity_tutee_image_view);

        photoViewIntentReceiver = Objects.requireNonNull(Objects.requireNonNull(getIntent().getExtras()).get("visit_user_image")).toString();

        mToolbar = findViewById(com.tutee.ak47.app.R.id.image_view_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        photoView = findViewById(com.tutee.ak47.app.R.id.display_photo);
        Picasso.get().load(photoViewIntentReceiver).placeholder(com.tutee.ak47.app.R.drawable.profile).into(photoView);

    }
}
