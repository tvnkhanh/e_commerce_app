package com.example.e_commerce_app.adapters;

import com.example.e_commerce_app.models.CartDetail;
import com.example.e_commerce_app.models.Product;

import java.util.List;

public interface QuantityListener {
    void onQuantityChange(List<Product> selectedProduct, List<CartDetail> cartDetailList);
}
