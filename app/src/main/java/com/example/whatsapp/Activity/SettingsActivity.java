package com.example.whatsapp.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.whatsapp.Model.User;
import com.example.whatsapp.R;
import com.example.whatsapp.Utils.Constants;
import com.github.ybq.android.spinkit.style.Circle;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private CircleImageView user_Profile;
    private TextInputLayout userName, userStatus;
    private Button btn_update;
    ProgressBar progressBar;

    private String name, status, imageUrl;
    private String uid = Constants.getUID();
    private User user;
    Uri uri;

    // Firebase
    FirebaseAuth mAuth;
    DatabaseReference rootRef;
    StorageReference userProfileImageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mAuth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference(Constants.USERS);
        userProfileImageRef = FirebaseStorage.getInstance().getReference("Profile Image");

        initializeViews();

        returnUserInfo();

        btn_update.setOnClickListener(this);

        user_Profile.setOnClickListener(this);

    }

    private void updateSettings() {

        if (!validationName()) {
            userName.requestFocus();
        } else if (!validationStatus()) {
            userStatus.requestFocus();
        } else {

            user = new User(name, status, uid, imageUrl);
            rootRef.child(Constants.getUID()).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        sendUserToMainActivity();
                    } else {
                        String message = task.getException().getMessage();
                        Toast.makeText(SettingsActivity.this, "Error Occurred " + message, Toast.LENGTH_LONG).show();
                    }
                }
            });

        }

    }

    private void returnUserInfo() {
        progressBar.setVisibility(View.VISIBLE);
        rootRef.child(Constants.getUID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                user = dataSnapshot.getValue(User.class);
                if (dataSnapshot.exists()) {
                    userName.getEditText().setText(user.getName());
                    userName.getEditText().setSelection(userName.getEditText().getText().length());
                    userStatus.getEditText().setText(user.getStatus());
                    userStatus.getEditText().setSelection(userStatus.getEditText().getText().length());
                    imageUrl = user.getImageUrl();
                    Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.default_pic_user)
                            .error(R.drawable.default_pic_user)
                            .into(user_Profile);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(SettingsActivity.this, "Please set and update your profile information... ", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                String message = databaseError.getMessage();
                Toast.makeText(SettingsActivity.this, "Error Occurred " + message, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if (resultCode == Activity.RESULT_OK) {

                if (result != null) {

                    uri = result.getUri();
                    uploadImageAndDownloadUrlInDB();
                }

            }

        }

    }

    private void uploadImageAndDownloadUrlInDB() {
        final StorageReference fillPathRef = userProfileImageRef.child(Constants.getUID() + ".jpg");
        progressBar.setVisibility(View.VISIBLE);
        fillPathRef.putFile(uri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                fillPathRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {
                            String downloadUrl = task.getResult().toString();
                            rootRef.child(Constants.getUID()).child("imageUrl")
                                    .setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        progressBar.setVisibility(View.INVISIBLE);
                                    }else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        String message = task.getException().toString();
                                        Toast.makeText(SettingsActivity.this, "Error Occurred : " + message, Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        } else {
                            progressBar.setVisibility(View.INVISIBLE);
                            String message = task.getException().toString();
                            Toast.makeText(SettingsActivity.this, "Error Occurred : " + message, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private void saveInfoInDB(String name, String status, String uid, String imageUrl) {
        if (imageUrl == null) {
            user = new User(name, status, uid);
        } else {
            user = new User(name, status, uid, imageUrl);
        }
        rootRef.child(Constants.getUID()).setValue(user);
    }

    private void initializeViews() {

        user_Profile = findViewById(R.id.img_user_profile);
        userName = findViewById(R.id.username);
        userStatus = findViewById(R.id.status);
        btn_update = findViewById(R.id.btn_update);

        progressBar = findViewById(R.id.progress);
        Circle circle = new Circle();
        progressBar.setIndeterminateDrawable(circle);
        progressBar.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_update:
                updateSettings();
                break;
            case R.id.img_user_profile:
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON_TOUCH)
                        .setAspectRatio(1, 1)
                        .start(SettingsActivity.this);
                break;
        }
    }

    private final static Pattern PATTERN_NAME = Pattern.compile("[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z ]+[\\u0600-\\u065F\\u066A-\\u06EF\\u06FA-\\u06FFa-zA-Z-_ ]");

    public boolean validationName() {
        name = userName.getEditText().getText().toString();
        if (name.isEmpty()) {
            userName.setError("Please enter your name");
            return false;
        } else if (!PATTERN_NAME.matcher(name).matches()) {
            userName.setError("Please enter alphabet letters only");
            return false;
        } else {
            userName.setError(null);
            return true;
        }
    }

    public boolean validationStatus() {
        status = userStatus.getEditText().getText().toString();
        if (status.isEmpty()) {
            userStatus.setError("Please enter your status");
            return false;
        } else if (!PATTERN_NAME.matcher(status).matches()) {
            userStatus.setError("Please enter alphabet letters only");
            return false;
        } else {
            userStatus.setError(null);
            return true;

        }
    }

    private void sendUserToMainActivity() {
        Intent main = new Intent(this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
        finish();
    }

}
