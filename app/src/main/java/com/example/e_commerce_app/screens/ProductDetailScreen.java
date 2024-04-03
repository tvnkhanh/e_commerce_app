package com.example.e_commerce_app.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.e_commerce_app.R;
import com.example.e_commerce_app.models.Product;
import com.example.e_commerce_app.models.Rating;
import com.example.e_commerce_app.models.UserInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductDetailScreen extends Fragment {
    private BottomNavigationView bottomNavigationView;
    private ImageSlider imageSlider;
    private ImageView backButton, cartButton;
    private ShapeableImageView productDetailShopImage;
    private TextView productPrice, productName, productRatingDisplay, productDetailDescText,
                productDetailRatingBarText, productDetailTotalRatings, productDetailShopName;
    private RatingBar productRatingBar;

    public ProductDetailScreen(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    public ProductDetailScreen() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedProductDetailCallback);
        View view = inflater.inflate(R.layout.fragment_product_detail_screen, container, false);

        Product product = (Product) getArguments().getSerializable("product");
        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.INVISIBLE);
        }

        setControl(view, product);
        setEvent(view, product);

        return view;
    }

    public void setControl(View view, Product product) {
        float avgRating = 0;
        int ratingNum = 0;

        imageSlider = view.findViewById(R.id.product_detail_image_slider);
        backButton = view.findViewById(R.id.product_detail_back_button);
        cartButton = view.findViewById(R.id.product_detail_cart_button);
        productName = view.findViewById(R.id.product_detail_name);
        productPrice = view.findViewById(R.id.product_detail_price);
        productRatingDisplay = view.findViewById(R.id.product_detail_rating_display);
        productDetailDescText = view.findViewById(R.id.product_detail_desc_text);
        productRatingBar = view.findViewById(R.id.product_detail_rating_bar);
        productDetailRatingBarText = view.findViewById(R.id.product_detail_rating_bar_text);
        productDetailTotalRatings = view.findViewById(R.id.product_detail_total_ratings);
        productDetailShopImage = view.findViewById(R.id.product_detail_shop_image);
        productDetailShopName = view.findViewById(R.id.product_detail_shop_name);

        productName.setText(product.getProductName());
        productPrice.setText("đ" + product.getPrice());
        if (product.getRatings() != null) {
            ratingNum = product.getRatings().size();
            for (Rating rating : product.getRatings()) {
                avgRating += rating.getRating();
            }
            avgRating = avgRating / ratingNum;
        }
        productRatingDisplay.setText(avgRating + " / 5");
        productDetailDescText.setText(product.getDesc());
        productRatingBar.setRating(avgRating);
        productDetailRatingBarText.setText(avgRating + "/5");
        productDetailTotalRatings.setText("(" + ratingNum + " đánh giá)");
    }

    public void setEvent(View view, Product product) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        List<String> productImageUrls = product.getListImageUrl();
        List<SlideModel> slideModels = new ArrayList<>();

        for (String url : productImageUrls) {
            slideModels.add(new SlideModel(url, ScaleTypes.FIT));
        }

        imageSlider.setImageList(slideModels, ScaleTypes.FIT);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomNavigationView.setVisibility(View.VISIBLE);
                requireActivity().getSupportFragmentManager().popBackStack("product_detail", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        cartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("product", product);

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment currentFragment = fragmentManager.findFragmentById(R.id.product_detail_container);
                if (currentFragment != null) {
                    fragmentTransaction.detach(currentFragment);
                }

                Fragment cartFragment = fragmentManager.findFragmentByTag("CartFragment");
                if (cartFragment == null) {
                    cartFragment = new CartScreen(bottomNavigationView);
                    cartFragment.setArguments(bundle);

                    fragmentTransaction.add(R.id.product_detail_container, cartFragment, "CartFragment");
                } else {
                    fragmentTransaction.attach(cartFragment);
                }

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        db.collection("user_info").document(product.getUid()).get().
                addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        UserInfo userInfo = documentSnapshot.toObject(UserInfo.class);
                        Picasso.get().load(userInfo.getAvatarLink()).into(productDetailShopImage);
                        productDetailShopName.setText(userInfo.getDisplayName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(view.getContext(), "No shop data found in database", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private final OnBackPressedCallback onBackPressedProductDetailCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            bottomNavigationView.setVisibility(View.VISIBLE);
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onBackPressedProductDetailCallback.remove();
    }
}