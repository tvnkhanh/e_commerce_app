package com.example.e_commerce_app.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.e_commerce_app.R;
import com.example.e_commerce_app.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class CartScreen extends Fragment {
    private BottomNavigationView bottomNavigationView;
    private ImageView cartScreenBackBtn;
    private Product product;

    public CartScreen(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    public CartScreen() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedCartCallback);
        View view = inflater.inflate(R.layout.fragment_cart_screen, container, false);

        if (bottomNavigationView != null) {
            bottomNavigationView.setVisibility(View.INVISIBLE);
        }

        setControl(view);
        setEvent();

        return view;
    }

    public void setControl(View view) {
        cartScreenBackBtn = view.findViewById(R.id.cart_screen_back_btn);
    }

    public void setEvent() {
        cartScreenBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                if (bottomNavigationView != null) {
                    bottomNavigationView.setVisibility(View.VISIBLE);
                }

                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment currentFragment = fragmentManager.findFragmentById(R.id.product_detail_container);
                if (currentFragment != null) {
                    fragmentTransaction.detach(currentFragment);
                }

                Fragment productDetailFragment = fragmentManager.findFragmentByTag("ProductDetailFragment");
                if (productDetailFragment == null) {
                    if (getArguments() != null) {
                        productDetailFragment = new ProductDetailScreen(bottomNavigationView);
                        product = (Product) getArguments().getSerializable("product");
                        bundle.putSerializable("product", product);
                        productDetailFragment.setArguments(bundle);
                        fragmentTransaction.add(R.id.product_detail_container, productDetailFragment, "ProductDetailFragment");
                    } else {
                        requireActivity().getSupportFragmentManager().popBackStack();
                    }
                } else {
                    fragmentTransaction.attach(productDetailFragment);
                }

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
    private final OnBackPressedCallback onBackPressedCartCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            if (bottomNavigationView != null) {
                bottomNavigationView.setVisibility(View.VISIBLE);
            }
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        onBackPressedCartCallback.remove();
    }
}