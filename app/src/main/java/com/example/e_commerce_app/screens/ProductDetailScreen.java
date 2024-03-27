package com.example.e_commerce_app.screens;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.e_commerce_app.R;
import com.example.e_commerce_app.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProductDetailScreen extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Product product = (Product) getArguments().getSerializable("product");

        System.out.println(product.getProductName());

        return inflater.inflate(R.layout.fragment_product_detail_screen, container, false);
    }
}