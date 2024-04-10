package com.example.e_commerce_app.screens;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce_app.adapters.CategoryAdapter;
import com.example.e_commerce_app.R;
import com.example.e_commerce_app.adapters.ProductAdapter;
import com.example.e_commerce_app.models.Category;
import com.example.e_commerce_app.models.Product;
import com.example.e_commerce_app.models.Rating;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {
    private FirebaseFirestore db;
    private ProgressBar categoryProgressBar, productProgressBar;
    private BottomNavigationView bottomNavigationView;
    private ImageButton homeScreenCartBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.home_screen);

        setControl();
        setEvent();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    public void setControl() {
        categoryProgressBar = findViewById(R.id.categoryProgressBar);
        productProgressBar = findViewById(R.id.productProgressBar);
        homeScreenCartBtn = findViewById(R.id.home_screen_cart_button);
        bottomNavigationView = findViewById(R.id.bottomNavigationView);
    }

    public void setEvent() {
        RecyclerView categoryRecyclerView = findViewById(R.id.categories);
        categoryRecyclerView.setHasFixedSize(true);

        FlexboxLayoutManager categoryLayoutManager =
                new FlexboxLayoutManager(this, FlexDirection.ROW, FlexWrap.WRAP);
        categoryLayoutManager.setAlignItems(AlignItems.CENTER);
        categoryLayoutManager.setJustifyContent(JustifyContent.CENTER);
        categoryRecyclerView.setLayoutManager(categoryLayoutManager);

        RecyclerView productRecyclerView = findViewById(R.id.products);
        productRecyclerView.setHasFixedSize(true);

        GridLayoutManager productLayoutManager = new GridLayoutManager(this, 2);
        productRecyclerView.setLayoutManager(productLayoutManager);

        db = FirebaseFirestore.getInstance();

        List<Category> categories = new ArrayList<>();
        List<Product> products = new ArrayList<>();

        CategoryAdapter categoryAdapter = new CategoryAdapter(categories, HomeScreen.this);
        categoryRecyclerView.setAdapter(categoryAdapter);

        ProductAdapter productAdapter = new ProductAdapter(products, HomeScreen.this,
                new ProductAdapter.OnProductClickListener() {
                    @Override
                    public void onProductClick(Product product) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("product", product);

                        ProductDetailScreen fragment = new ProductDetailScreen(bottomNavigationView);
                        fragment.setArguments(bundle);

                        getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                                .replace(R.id.home_fragment_container, fragment)
                                .addToBackStack("home_screen")
                                .commit();
                    }
                });
        productRecyclerView.setAdapter(productAdapter);

        homeScreenCartBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CartScreen cartScreen = new CartScreen(bottomNavigationView);
                getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                        .replace(R.id.home_fragment_container, cartScreen)
                        .addToBackStack("cart")
                        .commit();
            }
        });

        db.collection("categories").get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            categoryProgressBar.setVisibility(View.GONE);
                            List<DocumentSnapshot> categoryList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot category : categoryList) {
                                Category item = category.toObject(Category.class);
                                categories.add(item);
                            }
                            categoryAdapter.notifyDataSetChanged();
                        }
                        else {
                            Toast.makeText(HomeScreen.this, "No category data found in database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeScreen.this, "Fail to get the category data", Toast.LENGTH_SHORT).show();
                    }
                });

        db.collection("product").get().
                addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            productProgressBar.setVisibility(View.GONE);
                            List<DocumentSnapshot> productList = queryDocumentSnapshots.getDocuments();
                            for (DocumentSnapshot product : productList) {
                                String productId = product.getId();
                                List<Rating> ratings = new ArrayList<>();
                                Product item = product.toObject(Product.class);

                                db.collection("product").document(productId).collection("ratings").get().
                                        addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                            @Override
                                            public void onSuccess(QuerySnapshot task) {
                                                if (!task.isEmpty()) {
                                                    List<DocumentSnapshot> ratingList = task.getDocuments();
                                                    for (DocumentSnapshot rating : ratingList) {
                                                        Rating itemRating = rating.toObject(Rating.class);
                                                        ratings.add(itemRating);
                                                    }
                                                    assert item != null;
                                                    item.setRatings(ratings);

                                                    products.add(item);

                                                    productAdapter.notifyDataSetChanged();
                                                } else {
                                                    products.add(item);

                                                    productAdapter.notifyDataSetChanged();

                                                    Log.d("RatingData", "Error getting documents: ");
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(HomeScreen.this, "No ratings data found in database", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        } else {
                            Toast.makeText(HomeScreen.this, "No product data found in database", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(HomeScreen.this, "Fail to get the product data", Toast.LENGTH_SHORT).show();
                    }
                });

    }
}