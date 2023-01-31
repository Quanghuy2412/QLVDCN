package com.huce.qlvdcn.activity;

import static com.huce.qlvdcn.activity.MainActivity.catagory;
import static com.huce.qlvdcn.activity.MainActivity.items;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.huce.qlvdcn.R;
import com.huce.qlvdcn.adapter.CatagorySpinnerAdapter;
import com.huce.qlvdcn.model.Item;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class UpdateActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 4;
    private static final int PERMISSION_CODE = 5;

    private boolean checkState = false;

    private Spinner spnCatagory;
    private ImageView imgBack, imgSave, imgAddImage, imageItem, imgRemoveImage;
    private CatagorySpinnerAdapter catagorySpinnerAdapter;
    private EditText edTitle, edPosition, edDescription, edQuantity;
    public Uri mImageUri;
    private String mImageUrl;
    private ProgressDialog progressDialog;
    private TextView tvDateTime;

    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private Item item;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        init();
        processEvents();
    }

    private void init() {
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        imageItem = findViewById(R.id.imageItem);
        imgAddImage = findViewById(R.id.imgAddImage);
        imgRemoveImage = findViewById(R.id.imageRemoveImage);

        imgBack = findViewById(R.id.imgBack);
        imgSave = findViewById(R.id.imgSave);
        edTitle = findViewById(R.id.edTitle);
        edPosition = findViewById(R.id.edPosition);
        edQuantity = findViewById(R.id.edQuantity);
        spnCatagory = findViewById(R.id.spnUpdateCatagory);
        edDescription = findViewById(R.id.edDescription);
        //btnSave = findViewById(R.id.btnSave);
        //btnSave.setColorFilter(Color.WHITE);
        tvDateTime = findViewById(R.id.tvDateTime);
        catagorySpinnerAdapter = new CatagorySpinnerAdapter(this, R.layout.item_selected, catagory);
        spnCatagory.setAdapter(catagorySpinnerAdapter);
        progressDialog = new ProgressDialog(this);
    }

    private void processEvents() {

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(UpdateActivity.this, MainActivity.class));
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        item = (Item) bundle.get("object_item");
        String itemImageUrl = item.getImage();
        edTitle.setText(item.getTitle());
        edPosition.setText(item.getPosition());
        edDescription.setText(item.getDescription());
        tvDateTime.setText(item.getTime());
        edQuantity.setText(item.getQuantity());
        if (itemImageUrl != null) {
            Glide.with(UpdateActivity.this).load(itemImageUrl).into(imageItem);
            imageItem.setVisibility(View.VISIBLE);
            imgRemoveImage.setVisibility(View.VISIBLE);
        } else {
            imageItem.setVisibility(View.GONE);
            imgRemoveImage.setVisibility(View.GONE);
        }
        for (int i = 0; i < catagory.size(); i++) {
            if (catagory.get(i).getName().equals(item.getCatagory())) {
                spnCatagory.setSelection(i);
            }
        }

        imgSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String id = item.getId();
                String newTitle = edTitle.getText().toString().trim();
                String newPosition = edPosition.getText().toString().trim();
                String newDescription = edDescription.getText().toString().trim();
                String newQuantity = edQuantity.getText().toString().trim();

                String newCatag = catagory.get(spnCatagory.getSelectedItemPosition()).getName();
//                if (newTitle.equals(item.getTitle()) && newPosition.equals(item.getPosition())
//                        && newDescription.equals(item.getDescription()) && newCatag.equals(item.getCatagory())) {
//                    Toast.makeText(UpdateActivity.this, "Hãy cập nhật các ô trường dữ liệu!", Toast.LENGTH_SHORT).show();
//                } else
                if (newTitle.isEmpty()) {
                    progressDialog.dismiss();
                    edTitle.setError("Title is required!");
                    //Toast.makeText(getApplicationContext(), "Tiêu đề không được để trống!", Toast.LENGTH_SHORT).show();
                    edTitle.requestFocus();
                } else if (newPosition.isEmpty()) {
                    progressDialog.dismiss();
                    edPosition.setError("Position is required!");
                    //Toast.makeText(getApplicationContext(), "Vị trí không được để trống!", Toast.LENGTH_SHORT).show();
                    edPosition.requestFocus();
                } else if (newDescription.isEmpty()) {
                    progressDialog.dismiss();
                    edDescription.setError("Description is required!");
                    //Toast.makeText(getApplicationContext(), "Mô tả không được để trống!", Toast.LENGTH_SHORT).show();
                    edDescription.requestFocus();
                }else if (newQuantity.isEmpty()) {
                    progressDialog.dismiss();
                    edQuantity.setError("Quantity is required!");
                    //Toast.makeText(getApplicationContext(), "Vị trí không được để trống!", Toast.LENGTH_SHORT).show();
                    edQuantity.requestFocus();
                }  else {

                    String newTime = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm a", Locale.getDefault()).format(new Date());
                    tvDateTime.setText(newTime);
                    uploadImage(newTitle, newPosition, newCatag, newTime, newDescription, newQuantity);
                }
            }
        });

        imgAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(UpdateActivity.this);
                String[] options = {"Chọn ảnh", "Chụp ảnh"};
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (i == 0) {
                            chooseImage();
                        }
                        if (i == 1) {
                            takePhoto();
                        }
                    }
                }).create().show();
            }
        });

        imgRemoveImage.setOnClickListener(view -> {
            imageItem.setImageBitmap(null);
            imageItem.setVisibility(View.GONE);
            imgRemoveImage.setVisibility(View.GONE);
            checkState = true;
        });

    }

    private void uploadImage(String newTitle, String newPosition, String newCatag, String newTime, String newDescription, String newQuantity) {
        if (mImageUri != null) {
            checkState = false;
            progressDialog.show();
            StorageReference ref = storageReference.child("images/" + System.currentTimeMillis());

            ref.putFile(mImageUri)
                    .addOnSuccessListener(taskSnapshot -> taskSnapshot.getStorage().getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            mImageUrl = task.getResult().toString().trim();
                            Toast.makeText(UpdateActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                            doUpdate(newTitle, newPosition, newCatag, newTime, newDescription, newQuantity);
                        }
                    }))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(UpdateActivity.this, "Upload Failed", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    })
                    .addOnProgressListener(taskSnapshot -> {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        progressDialog.setMessage("Uploaded" + (int) progress + "%");
                    });
        } else {
            mImageUrl = item.getImage();
            if (checkState){
                mImageUrl = null;
            }
            doUpdate(newTitle, newPosition, newCatag, newTime, newDescription, newQuantity);
        }
    }

private void doUpdate(String newTitle, String newPosition, String newCatag, String newTime, String newDescription, String newQuantity) {
        firebaseFirestore.collection("items")
                .document(firebaseUser.getUid())
                .collection("MyItems").whereEqualTo("id", item.getId())
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                            String documentID = documentSnapshot.getId();
                            firebaseFirestore.collection("items")
                                    .document(firebaseUser.getUid())
                                    .collection("MyItems")
                                    .document(documentID)
                                    .update("title", newTitle, "position", newPosition, "catagory", newCatag, "description", newDescription, "time", newTime, "image", mImageUrl, "quantity", newQuantity)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(UpdateActivity.this, "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(UpdateActivity.this, MainActivity.class));
                                            finish();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(UpdateActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private void chooseImage() {
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

    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
                    this.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, PERMISSION_CODE);
            } else {
                openCamera();
            }
        } else {
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
        activityResultLauncherCamera.launch(Intent.createChooser(cameraIntent, "Take Photo"));
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
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            }
        }
    }
}