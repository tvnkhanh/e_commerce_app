package com.example.e_commerce_app.screens;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.e_commerce_app.R;
import com.example.e_commerce_app.models.Product;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.io.Serializable;
import java.util.List;

public class PaymentScreen extends Fragment {
    private ImageView backBtn;
    private BottomNavigationView bottomNavigationView;
    private List<Product> products;

    public PaymentScreen() {
    }

    public PaymentScreen(BottomNavigationView bottomNavigationView) {
        this.bottomNavigationView = bottomNavigationView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), onBackPressedPaymentCallback);
        View view = inflater.inflate(R.layout.fragment_payment_screen, container, false);

        if (getArguments() != null) {
            products = (List<Product>) getArguments().getSerializable("products");
        }

        setControl(view);
        setEvent();

        return view;
    }

    private void setControl(View view) {
        backBtn = view.findViewById(R.id.payment_screen_back_btn);
    }

    private void setEvent() {
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getParentFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                Fragment currentFragment = fragmentManager.findFragmentById(R.id.cart_screen_container);
                if (currentFragment != null) {
                    fragmentTransaction.detach(currentFragment);
                }

                Fragment cartFragment = fragmentManager.findFragmentByTag("CartFragment");
                if (cartFragment == null) {
                    cartFragment = new CartScreen(bottomNavigationView);
                    fragmentTransaction.add(R.id.cart_screen_container, cartFragment, "CartFragment");
                } else {
                    fragmentTransaction.attach(cartFragment);
                }

                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    private final OnBackPressedCallback onBackPressedPaymentCallback = new OnBackPressedCallback(true) {
        @Override
        public void handleOnBackPressed() {
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}