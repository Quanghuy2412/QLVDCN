package com.huce.qlvdcn.activity;


import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.huce.qlvdcn.R;

import java.io.IOException;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ACTIVITY_HOME = 0;
    private static final int ACTIVITY_CREATE = 1;
    private static final int ACTIVITY_STATISTIC = 2;
    private static final int ACTIVITY_PROFILE = 3;

    public int currentActivity = ACTIVITY_PROFILE;
    private static final int REQUEST_CODE = 4;

    private boolean checkRequest = false;

    private ImageView imgMenu, img_avatar;
    private CircleImageView imgAvatar;
    private TextView tvName, tvEmail;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private ImageView imgBack;
    private EditText edFullName, edEmail;
    private AppCompatButton btnUpdate;
    private Uri mUri;
    private ProgressDialog progressDialog;
//    private Context context;
//    private MainActivity mainActivity;

    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
        progressDialog = new ProgressDialog(ProfileActivity.this);
        progressDialog.setMessage("Updating Profile...");
        initUI();
        setUserInformation();
        showUserInformation();
        initListener();
    }

    private void initListener() {
        img_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
                checkRequest = true;
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
                finish();
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickUpdateProfile();
            }
        });
    }

    private void onClickUpdateProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return;
        }
        progressDialog.show();
        String fullName = edFullName.getText().toString().trim();
        UserProfileChangeRequest profileUpdates= null;
        if (checkRequest) {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .setPhotoUri(mUri)
                    .build();
//            checkRequest = false;
        } else {
            profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .setPhotoUri(user.getPhotoUrl())
                    .build();
        }

        checkRequest = false;

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Update profile thành công!", Toast.LENGTH_SHORT).show();
//                            startActivity(new Intent(ProfileActivity.this, MainActivity.class));
//                            finish();
                            showUserInformation();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ProfileActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void selectImage() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            openGallery();
            return;
        }

        if (this.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            openGallery();
        } else {
            String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
            this.requestPermissions(permissions, REQUEST_CODE);
        }
    }

    private void setUserInformation() {
        edFullName.setText(firebaseUser.getDisplayName());
        edEmail.setText(firebaseUser.getEmail());
        Glide.with(ProfileActivity.this).load(firebaseUser.getPhotoUrl()).error(R.drawable.avatar).into(img_avatar);
    }

    private void initUI() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        img_avatar = findViewById(R.id.img_avatar);
        imgBack = findViewById(R.id.imgBack);
        edFullName = findViewById(R.id.edFullName);
        edEmail = findViewById(R.id.edEmail);
        btnUpdate = findViewById(R.id.btnUpdate);
        drawerLayout = findViewById(R.id.drawerLayout);
        imgAvatar = navigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tvEmail);

        imgMenu = findViewById(R.id.imgMenu);
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.getMenu().findItem(R.id.nav_profile).setChecked(true);
//        mainActivity = (MainActivity) context;
    }

    private void openGallery() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));;
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("*/*");
            activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));;
        }
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent == null) {
                    return;
                }
                mUri = intent.getData();
                img_avatar.setImageURI(mUri);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(mUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    img_avatar.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            }
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            if (currentActivity != ACTIVITY_HOME) {
                currentActivity = ACTIVITY_HOME;
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        } else if (id == R.id.nav_create) {
            if (currentActivity != ACTIVITY_CREATE) {
                currentActivity = ACTIVITY_CREATE;
                startActivity(new Intent(this, CreateAcitvity.class));
                finish();
            }
        } else if (id == R.id.nav_statistic) {
            if (currentActivity != ACTIVITY_STATISTIC) {
                currentActivity = ACTIVITY_STATISTIC;
                startActivity(new Intent(this, StatisticActitvity.class));
                finish();
            }
        } else if (id == R.id.nav_profile) {
            if (currentActivity != ACTIVITY_PROFILE) {
                currentActivity = ACTIVITY_PROFILE;
                startActivity(new Intent(this, ProfileActivity.class));
                finish();
            }
        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    public void showUserInformation() {
        //FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser == null) {
            return;
        }

        String name = firebaseUser.getDisplayName();
        String email = firebaseUser.getEmail();
        Uri photoUrl = firebaseUser.getPhotoUrl();

        if (name == null) {
            tvName.setVisibility(View.GONE);
        } else {
            tvName.setVisibility(View.VISIBLE);
            tvName.setText(name);
        }

        tvEmail.setText(email);
        Glide.with(ProfileActivity.this).load(photoUrl).error(R.drawable.avatar).into(imgAvatar);
        Glide.with(ProfileActivity.this).load(photoUrl).error(R.drawable.avatar).into(img_avatar);

    }
}