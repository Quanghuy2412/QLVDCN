package com.huce.qlvdcn.activity;


import static com.huce.qlvdcn.adapter.CatagoryAdapter.checkClick;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.huce.qlvdcn.R;
import com.huce.qlvdcn.adapter.ItemAdapter;
import com.huce.qlvdcn.model.Catagory;
import com.huce.qlvdcn.model.Item;

import java.util.ArrayList;
import java.util.List;

public class CatagoryActivity extends AppCompatActivity {

    private ImageView imgBack;
    private TextView tvTitle;
    private RecyclerView recyclerViewItems;
    private ItemAdapter itemAdapter;
    private MainActivity mainActivity;
    private List<Item> itemList = new ArrayList<Item>();
    private FirebaseFirestore firebaseFirestore;
    private FirebaseUser firebaseUser;
    private FirebaseStorage firebaseStorage;
    private ProgressDialog progressDialog;
    private Catagory object_catagory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catagory);

        init();
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CatagoryActivity.this, MainActivity.class));
                checkClick = false;
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        object_catagory = (Catagory) bundle.get("object_catagory");
        tvTitle.setText(object_catagory.getName());
        recyclerView();
    }

    private void init() {
        recyclerViewItems = findViewById(R.id.recyclerView_Items);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(CatagoryActivity.this));
        imgBack = findViewById(R.id.imgBack);
        tvTitle = findViewById(R.id.tvTitle);
        progressDialog = new ProgressDialog(this);
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        firebaseStorage = FirebaseStorage.getInstance();
    }

    private void recyclerView() {
        firebaseFirestore.collection("items")
                .document(firebaseUser.getUid())
                .collection("MyItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        itemList.clear();
                        //show data
                        for (DocumentSnapshot doc : task.getResult()) {
                            Item item = new Item(doc.getString("id"),
                                    doc.getString("title"),
                                    doc.getString("position"),
                                    doc.getString("time"),
                                    doc.getString("description"),
                                    doc.getString("image"),
                                    doc.getString("catagory"),
                                    doc.getString("quantity"));
                            if (item.getCatagory().equals(object_catagory.getName())) {
                                itemList.add(item);
                            }
                            itemAdapter = new ItemAdapter(mainActivity, CatagoryActivity.this, itemList);
                            recyclerViewItems.setAdapter(itemAdapter);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CatagoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void deleteItem(Item item) {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_delete_item);

        Window window = dialog.getWindow();
        if (window == null) {
            return;
        }

        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = Gravity.CENTER;
        window.setAttributes(windowAttributes);

        dialog.setCancelable(Gravity.CENTER == windowAttributes.gravity);

        TextView tvDeleteItem = dialog.findViewById(R.id.tvDeleteItem);
        TextView tvCancel = dialog.findViewById(R.id.tvCancel);

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tvDeleteItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressDialog.setMessage("Deleting Item...");
                progressDialog.show();

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
                                            .delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(CatagoryActivity.this, "Xoá vật dụng thành công!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    progressDialog.dismiss();
                                                    if (item.getImage() != null) {
                                                        firebaseStorage.getReferenceFromUrl(item.getImage())
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(CatagoryActivity.this, "Image Item is deleted!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(CatagoryActivity.this, "Failed Image to delete!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                    recyclerView();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(CatagoryActivity.this, "Xoá vật dụng thất bại!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    progressDialog.dismiss();
                                                }
                                            });
                                }
                            }
                        });
            }
        });

        dialog.show();
    }
}