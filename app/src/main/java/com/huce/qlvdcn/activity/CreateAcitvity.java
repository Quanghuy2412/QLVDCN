package com.huce.qlvdcn.activity;

import static com.huce.qlvdcn.activity.MainActivity.catagory;
import static com.huce.qlvdcn.activity.MainActivity.items;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.huce.qlvdcn.R;
import com.huce.qlvdcn.adapter.CatagorySpinnerAdapter;
import com.huce.qlvdcn.model.Item;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateAcitvity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ACTIVITY_HOME = 0;
    private static final int ACTIVITY_CREATE = 1;
    private static final int ACTIVITY_STATISTIC = 2;
    private static final int ACTIVITY_PROFILE = 3;

    public int currentActivity = ACTIVITY_CREATE;
    private static final int REQUEST_CODE = 4;
    private static final int PERMISSION_CODE = 5;

    private ImageView imgMenu;
    private CircleImageView imgAvatar;
    private TextView tvName, tvEmail;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private Spinner spnCatagory;
    private ImageView imgBack, imgAddImage, imageItem, imgSave;
    private CatagorySpinnerAdapter catagorySpinnerAdapter;
    private EditText edTitle, edPosition, edDescription, edQuantity;

    private ProgressDialog progressDialog;
    private TextView tvDateTime;
    private String time;
    public Uri mImageUri;
    private String mImageUrl;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_acitvity);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
        showUserInformation();
        proccessEvents();
    }

    private void init() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing...");

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build());
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        imageItem = findViewById(R.id.imageItem);
        imgAddImage = findViewById(R.id.imgAddImage);

        edDescription = findViewById(R.id.edDescription);
        edQuantity = findViewById(R.id.edQuantity);
        spnCatagory = findViewById(R.id.spnCatagory);
        edTitle = findViewById(R.id.edTitle);
        imgBack = findViewById(R.id.imgBack);
        imgSave = findViewById(R.id.imgSave);
        edPosition = findViewById(R.id.edPosition);
//        btnAdd = findViewById(R.id.btnAdd);
//        btnAdd.setColorFilter(Color.WHITE);
        tvDateTime = findViewById(R.id.tvDateTime);
        catagorySpinnerAdapter = new CatagorySpinnerAdapter(this, R.layout.item_selected, catagory);
        spnCatagory.setAdapter(catagorySpinnerAdapter);

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

        navigationView.getMenu().findItem(R.id.nav_create).setChecked(true);
    }

    private void proccessEvents() {
        spnCatagory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        time = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date());
        tvDateTime.setText(time);

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateAcitvity.this, MainActivity.class));
                finish();
            }
        });

        findViewById(R.id.imageRemoveImage).setOnClickListener(view -> {
            imageItem.setImageBitmap(null);
            imageItem.setVisibility(View.GONE);
            findViewById(R.id.imageRemoveImage).setVisibility(View.GONE);
        });

        imgAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CreateAcitvity.this);
                String[] options = {"Chọn ảnh", "Chụp ảnh"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0){
                            selectImage();
                        }
                        if (i == 1){
                            takePhoto();
                        }
                    }
                }).create().show();
                //selectImage();
            }
        });

        edQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String quantity = edQuantity.getText().toString().trim();
                int q = Integer.parseInt(quantity);
                if (q < 0){
                    edQuantity.setError("Số lượng phải lớn hơn 0");
                    edQuantity.requestFocus();
                }
            }
        });

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edTitle.getText().toString().trim();
                String position = edPosition.getText().toString().trim();
                String catag = catagory.get(spnCatagory.getSelectedItemPosition()).getName();
                String des = edDescription.getText().toString().trim();
                String quan = edQuantity.getText().toString().trim();

                if (title.isEmpty()) {
                    progressDialog.dismiss();
                    edTitle.setError("Title is required!");
                    //Toast.makeText(getApplicationContext(), "Tiêu đề không được để trống!", Toast.LENGTH_SHORT).show();
                    edTitle.requestFocus();
                } else if (position.isEmpty()) {
                    progressDialog.dismiss();
                    edPosition.setError("Position is required!");
                    //Toast.makeText(getApplicationContext(), "Vị trí không được để trống!", Toast.LENGTH_SHORT).show();
                    edPosition.requestFocus();
                } else if (des.isEmpty()) {
                    progressDialog.dismiss();
                    edDescription.setError("Description is required!");
                    //Toast.makeText(getApplicationContext(), "Mô tả không được để trống!", Toast.LENGTH_SHORT).show();
                    edDescription.requestFocus();
                }else if (quan.isEmpty()) {
                    progressDialog.dismiss();
                    edQuantity.setError("Quantity is required!");
                    //Toast.makeText(getApplicationContext(), "Mô tả không được để trống!", Toast.LENGTH_SHORT).show();
                    edQuantity.requestFocus();
                }else {
                    for (Item item : items) {
                        if (title.toLowerCase().equals(item.getTitle().toLowerCase())) {
                            edTitle.setError("Vật dụng đã tồn tại trong hệ thống!");
                            return;
                        }
                    }

                    if (mImageUri != null) {
                        progressDialog.show();
                        StorageReference ref = storageReference.child("images/" + System.currentTimeMillis());
                        ref.putFile(mImageUri)
                                .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Uri> task) {
                                        mImageUrl = task.getResult().toString();
                                        Toast.makeText(CreateAcitvity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        addItem(title, position, catag, des, quan);
                                    }
                                }))
                                .addOnFailureListener(e -> {
                                    Toast.makeText(CreateAcitvity.this, "Upload Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                })
                                .addOnProgressListener(taskSnapshot -> {
                                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
                                            .getTotalByteCount());
                                    progressDialog.setMessage("Uploaded " + (int) progress + "%");
                                });
                    } else addItem(title, position, catag, des, quan);

                }
            }
        });
    }

    private void addItem(String title, String position, String catag, String des, String quan) {
        String id = UUID.randomUUID().toString();
        DocumentReference documentReference = firebaseFirestore.collection("items").document(firebaseUser.getUid()).collection("MyItems").document();
        Map<String, Object> item = new HashMap<>();
        item.put("id", id);
        item.put("title", title);
        item.put("position", position);
        item.put("quantity", quan);
        item.put("catagory", catag);
        item.put("description", des);
        item.put("image", mImageUrl);
        item.put("time", time);
        documentReference.set(item).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Thêm vật dụng thành công!", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(CreateAcitvity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Thêm vật dụng thất bại!", Toast.LENGTH_SHORT).show();
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

    private void takePhoto(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(this.checkSelfPermission(Manifest.permission.CAMERA)==PackageManager.PERMISSION_DENIED ||
                this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)==PackageManager.PERMISSION_DENIED){
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            }else {
                openCamera();
            }
        }else {
            openCamera();
        }
    }

    private void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        mImageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        activityResultLauncherCamera.launch(Intent.createChooser(cameraIntent,"Take Photo"));
    }


    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }

    private final ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Intent intent = result.getData();
                if (intent == null) {
                    return;
                }
                mImageUri = intent.getData();
                imageItem.setImageURI(mImageUri);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(mImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageItem.setImageBitmap(bitmap);
                    imageItem.setVisibility(View.VISIBLE);
                    findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    });

    private final ActivityResultLauncher<Intent> activityResultLauncherCamera = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                imageItem.setImageURI(mImageUri);
                try {
                    InputStream inputStream = getContentResolver().openInputStream(mImageUri);
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    imageItem.setImageBitmap(bitmap);
                    imageItem.setVisibility(View.VISIBLE);
                    findViewById(R.id.imageRemoveImage).setVisibility(View.VISIBLE);
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
        if (requestCode == PERMISSION_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
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
        Glide.with(this).load(photoUrl).error(R.drawable.avatar).into(imgAvatar);
        imgAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CreateAcitvity.this, ProfileActivity.class));
                finish();
            }
        });
    }
}