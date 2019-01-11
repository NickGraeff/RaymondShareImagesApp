package com.nickgraeff.raymondshareimagesapp;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.net.Uri;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    // Constants
    private final int PICK_IMAGE = 1;
    private final int GET_IMAGE = 2;

    // UI Variables
    private Button mShareButton;
    private Button mGetButton;
    private ImageView mSharedImageView;
    private EditText mSharePatientText;
    private EditText mGetPatientText;
    private final Activity thisActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // No explanation needed; request the permission
        ActivityCompat.requestPermissions(thisActivity,
                new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                1);
        // Get reference to UI elements
        mShareButton = findViewById(R.id.share_image_button);
        mSharedImageView = findViewById(R.id.shared_image_view);
        mGetButton = findViewById(R.id.get_image_button);
        mSharePatientText = findViewById(R.id.self_patient_textview);
        mGetPatientText = findViewById(R.id.search_patient_textview);

        // Action for share image button
        mShareButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Do something in response to button click
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });

        mGetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Do something in response to button click
                // TODO: Make sure the variables aren't null...
                DatabaseReference fbdb = FirebaseDatabase.getInstance().getReference().child("images/" + mGetPatientText.getText().toString());
                fbdb.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        // TODO: Explain what's happening here...
                        for (DataSnapshot data1 : dataSnapshot.getChildren()) {
                            final Object imageURI = data1.getValue();
                            if (imageURI != null) {
                                StorageReference storage = FirebaseStorage.getInstance().getReference().child(imageURI.toString());
                                final File localFile = getTempFile();
                                storage.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {

                                        Bitmap myBitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());

                                        mSharedImageView.setImageBitmap(myBitmap);
                                    }
                                });

                            }

                            break; // Doing just to get the first item, additional code needed...
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == PICK_IMAGE) {
            if (resultCode != Activity.RESULT_OK) {
                return;
            }
            Uri uri = data == null ? null : data.getData();
            if (uri != null) {

                // Create a storage reference from our app
                // TODO: make sure msharepatienttext is valid (filled out)
                StorageReference storage = FirebaseStorage.getInstance().getReference();
                storage.child("images/" + mSharePatientText.getText().toString() + "/" + uri.getPathSegments().get(uri.getPathSegments().size()-1)).putFile(uri);
                DatabaseReference fbdb = FirebaseDatabase.getInstance().getReference().child("images").child(mSharePatientText.getText().toString()).push();
                fbdb.setValue("images/" + mSharePatientText.getText().toString() + "/" + uri.getPathSegments().get(uri.getPathSegments().size()-1));
            }
        }
    }

    private File getTempFile() {
        try {
            return File.createTempFile("images", "jpg");
        } catch (Exception e) {
            return null;
        }
    }

}
