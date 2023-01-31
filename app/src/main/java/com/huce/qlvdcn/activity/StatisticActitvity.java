package com.huce.qlvdcn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.QuerySnapshot;
import com.huce.qlvdcn.R;
import com.huce.qlvdcn.model.Item;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class StatisticActitvity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ACTIVITY_HOME = 0;
    private static final int ACTIVITY_CREATE = 1;
    private static final int ACTIVITY_STATISTIC = 2;
    private static final int ACTIVITY_PROFILE = 3;

    public int currentActivity = ACTIVITY_STATISTIC;

    private TextView tvstatisticVSCN, tvstatisticGT, tvstatisticMP, tvstatisticQA, tvstatisticSV;
    private AppCompatButton btnCreate, btnHome;
    private ImageView imgBack;
    private ImageView imgMenu;
    private CircleImageView imgAvatar;
    private TextView tvName, tvEmail;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private FirebaseFirestore firebaseFirestore;

    private FirebaseUser firebaseUser;

    private List<Item> itemList = new ArrayList<Item>();

    int VSCN = 0, GT = 0, MP = 0, QA = 0, SV = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic_actitvity);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        init();
        showUserInformation();
        Events();
    }


    private void Events() {
        firebaseFirestore.collection("items")
                .document(firebaseUser.getUid())
                .collection("MyItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
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
                            itemList.add(item);
                        }

                        for (int i = 0; i < itemList.size(); i++) {
                            switch (itemList.get(i).getCatagory()) {
                                case "Vệ sinh cá nhân":
                                    VSCN++;
                                    break;
                                case "Giấy tờ":
                                    GT++;
                                    break;
                                case "Mỹ phẩm":
                                    MP++;
                                    break;
                                case "Quần áo":
                                    QA++;
                                    break;
                                case "Sách vở":
                                    SV++;
                                    break;
                            }
                        }
                        tvstatisticVSCN.setText(String.valueOf(VSCN));
                        tvstatisticGT.setText(String.valueOf(GT));
                        tvstatisticMP.setText(String.valueOf(MP));
                        tvstatisticQA.setText(String.valueOf(QA));
                        tvstatisticSV.setText(String.valueOf(SV));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(StatisticActitvity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatisticActitvity.this, CreateAcitvity.class));
                finish();
            }
        });

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatisticActitvity.this, MainActivity.class));
                finish();
            }
        });

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StatisticActitvity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void init() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();



        tvstatisticVSCN = findViewById(R.id.tvstatisticVSCN);
        tvstatisticGT = findViewById(R.id.tvstatisticGT);
        tvstatisticMP = findViewById(R.id.tvstatisticMP);
        tvstatisticQA = findViewById(R.id.tvstatisticQA);
        tvstatisticSV = findViewById(R.id.tvstatisticSV);
        imgBack = findViewById(R.id.imgBack);
        btnHome = findViewById(R.id.btnHome);
        btnCreate = findViewById(R.id.btnCreate);

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

        navigationView.getMenu().findItem(R.id.nav_statistic).setChecked(true);
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
                startActivity(new Intent(StatisticActitvity.this, ProfileActivity.class));
                finish();
            }
        });
    }
}