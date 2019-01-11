package com.nickgraeff.raymondshareimagesapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MainActivity extends AppCompatActivity {

    private Button mShareButton;
    private ImageView mSharedImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get reference to UI elements
        mShareButton = findViewById(R.id.share_image_button);
        mSharedImageView = findViewById(R.id.shared_image_view);

        // Action for share image button
        mShareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Do something in response to button click
            }
        });

        // Create a storage reference from our app
        FirebaseStorage storage = FirebaseStorage.getInstance().child("images");
        StorageReference storageRef = storage.getReference();
    }


}
