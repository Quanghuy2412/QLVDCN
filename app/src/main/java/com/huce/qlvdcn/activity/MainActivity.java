package com.huce.qlvdcn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.huce.qlvdcn.R;
import com.huce.qlvdcn.adapter.CatagoryAdapter;
import com.huce.qlvdcn.adapter.ItemAdapter;
import com.huce.qlvdcn.model.Catagory;
import com.huce.qlvdcn.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int ACTIVITY_HOME = 0;
    private static final int ACTIVITY_CREATE = 1;
    private static final int ACTIVITY_STATISTIC = 2;
    private static final int ACTIVITY_PROFILE = 3;

    public int currentActivity = ACTIVITY_HOME;

    private FirebaseFirestore firebaseFirestore;
    private FirebaseStorage firebaseStorage;

    private FirebaseUser firebaseUser;

    private DrawerLayout drawerLayout;

    private NavigationView navigationView;

    private CircleImageView imgAvatar;
    private ImageView imgMenu;
    private TextView tvName, tvEmail, tvNewItem;
    private TextView NoFilter, HightoLow, LowtoHigh;
    private SearchView searchView;
    private FloatingActionButton btnAddItem;
    private RecyclerView recyclerViewItems;
    private List<Item> itemList = new ArrayList<Item>();
    public static List<Item> items = new ArrayList<Item>();
    private ItemAdapter itemAdapter;
    private ProgressDialog progressDialog;

    public static List<Catagory> catagory = new ArrayList<>();

    private CatagoryActivity catagoryActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);


        init();
        showUserInformation();
        recyclerViewCatagory();
        recyclerViewItems();
        SearchViewItem();
        FilterItems();
    }

    public void FilterItems() {
        NoFilter.setBackgroundResource(R.drawable.filter_selected_shape);
        NoFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HightoLow.setBackgroundResource(R.drawable.filter_shape);
                LowtoHigh.setBackgroundResource(R.drawable.filter_shape);
                NoFilter.setBackgroundResource(R.drawable.filter_selected_shape);
                firebaseFirestore.collection("items")
                        .document(firebaseUser.getUid())
                        .collection("MyItems")
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressDialog.dismiss();
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
                                    itemList.add(item);
                                }
                                //adapter
                                itemAdapter = new ItemAdapter(MainActivity.this, catagoryActivity, itemList);
                                itemAdapter.notifyDataSetChanged();
                                recyclerViewItems.setAdapter(itemAdapter);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        HightoLow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HightoLow.setBackgroundResource(R.drawable.filter_selected_shape);
                LowtoHigh.setBackgroundResource(R.drawable.filter_shape);
                NoFilter.setBackgroundResource(R.drawable.filter_shape);
                firebaseFirestore.collection("items")
                        .document(firebaseUser.getUid())
                        .collection("MyItems")
                        .orderBy("time", Query.Direction.DESCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressDialog.dismiss();
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
                                    itemList.add(item);
                                }
                                //adapter
                                itemAdapter = new ItemAdapter(MainActivity.this, catagoryActivity, itemList);
                                recyclerViewItems.setAdapter(itemAdapter);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        LowtoHigh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HightoLow.setBackgroundResource(R.drawable.filter_shape);
                LowtoHigh.setBackgroundResource(R.drawable.filter_selected_shape);
                NoFilter.setBackgroundResource(R.drawable.filter_shape);
                firebaseFirestore.collection("items")
                        .document(firebaseUser.getUid())
                        .collection("MyItems")
                        .orderBy("time", Query.Direction.ASCENDING)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressDialog.dismiss();
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
                                    itemList.add(item);
                                }
                                //adapter
                                itemAdapter = new ItemAdapter(MainActivity.this, catagoryActivity, itemList);
                                recyclerViewItems.setAdapter(itemAdapter);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
//
//    public void setDefaultFilter() {
//        HightoLow.setBackgroundResource(R.drawable.filter_shape);
//        LowtoHigh.setBackgroundResource(R.drawable.filter_shape);
//        NoFilter.setBackgroundResource(R.drawable.filter_selected_shape);
//    }

    private void SearchViewItem() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    private void filterList(String newText) {
        List<Item> filterList = new ArrayList<>();
        String string = "";
        for (Item item : itemList) {
            string = item.getTitle().toLowerCase() + " " + item.getPosition().toLowerCase();
            if (string.contains(newText.toLowerCase())) {
                filterList.add(item);
            }
            string = "";
        }

        if (filterList.isEmpty()) {
            Toast.makeText(this, "No data!", Toast.LENGTH_SHORT).show();
            setAdapterFilter(filterList);
        } else {
            setAdapterFilter(filterList);
        }
    }

    private void setAdapterFilter(List<Item> list) {
        itemAdapter = new ItemAdapter(MainActivity.this, catagoryActivity, list);
        recyclerViewItems.setAdapter(itemAdapter);
        itemAdapter.notifyDataSetChanged();
    }

    private void recyclerViewItems() {
        progressDialog.show();
        firebaseFirestore.collection("items")
                .document(firebaseUser.getUid())
                .collection("MyItems")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressDialog.dismiss();
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
                            itemList.add(item);
                        }

                        items.clear();
                        for (Item item : itemList) {
                            items.add(item);
                        }
                        //adapter
                        itemAdapter = new ItemAdapter(MainActivity.this, catagoryActivity, itemList);
                        recyclerViewItems.setAdapter(itemAdapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void init() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.setFirestoreSettings(new FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED).build());
        firebaseStorage = FirebaseStorage.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        imgAvatar = navigationView.getHeaderView(0).findViewById(R.id.imgAvatar);
        tvName = navigationView.getHeaderView(0).findViewById(R.id.tvName);
        tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tvEmail);

        tvNewItem = findViewById(R.id.tvNewItem);
        tvNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateAcitvity.class));
//                finish();
            }
        });

        searchView = findViewById(R.id.search_bar);
        searchView.clearFocus();
        NoFilter = findViewById(R.id.NoFilter);
        HightoLow = findViewById(R.id.HightoLow);
        LowtoHigh = findViewById(R.id.LowtoHigh);
        recyclerViewItems = findViewById(R.id.recyclerView_Items);
        recyclerViewItems.setLayoutManager(new LinearLayoutManager(this));
        drawerLayout = findViewById(R.id.drawerLayout);
        btnAddItem = findViewById(R.id.btnAddItem);
        btnAddItem.setColorFilter(Color.WHITE);
        btnAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, CreateAcitvity.class));
                finish();
            }
        });

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading data...");

        imgMenu = findViewById(R.id.imgMenu);
        imgMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.getMenu().findItem(R.id.nav_home).setChecked(true);
    }

    private void recyclerViewCatagory() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        RecyclerView recyclerViewCatagory = findViewById(R.id.recyclerView_Catagory);
        recyclerViewCatagory.setLayoutManager(linearLayoutManager);

        catagory.clear();
        //catagory.add(new Catagory("Tất cả", "cat_all"));
        catagory.add(new Catagory("Vệ sinh cá nhân", "cat_1"));
        catagory.add(new Catagory("Giấy tờ", "cat_2"));
        catagory.add(new Catagory("Mỹ phẩm", "cat_3"));
        catagory.add(new Catagory("Quần áo", "cat_4"));
        catagory.add(new Catagory("Sách vở", "cat_5"));
        RecyclerView.Adapter<CatagoryAdapter.ViewHolder> catagoryAdapter = new CatagoryAdapter(catagory, MainActivity.this);
        recyclerViewCatagory.setAdapter(catagoryAdapter);
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

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                finish();
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
                                                    Toast.makeText(MainActivity.this, "Xoá vật dụng thành công!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                    if (item.getImage() != null) {
                                                        firebaseStorage.getReferenceFromUrl(item.getImage())
                                                                .delete()
                                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        Toast.makeText(MainActivity.this, "Image Item is deleted!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                })
                                                                .addOnFailureListener(new OnFailureListener() {
                                                                    @Override
                                                                    public void onFailure(@NonNull Exception e) {
                                                                        Toast.makeText(MainActivity.this, "Failed Image to delete!", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                });
                                                    }
                                                    recyclerViewItems();
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(MainActivity.this, "Xoá vật dụng thất bại!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
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