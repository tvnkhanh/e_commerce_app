package com.example.e_commerce_app.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.e_commerce_app.R;
import com.example.e_commerce_app.models.Product;
import com.example.e_commerce_app.screens.HomeScreen;
import com.example.e_commerce_app.screens.ProductDetailScreen;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    List<Product> products;
    Context context;
    private OnProductClickListener listener;

    public ProductAdapter(List<Product> products, HomeScreen homeScreen, OnProductClickListener listener) {
        this.products = products;
        this.context = homeScreen;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.product_vertical_box, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Product product = this.products.get(position);
        Picasso.get().load(product.getListImageUrl().get(0)).into(holder.productImage);
        holder.productName.setText(product.getProductName());
        holder.productPrice.setText("đ" + product.getPrice());
        if (product.getRatings() != null) {
            holder.numOfUserRating.setText("Đánh giá: " + product.getRatings().size());
        } else {
            holder.numOfUserRating.setText("Đánh giá: 0");
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onProductClick(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView productImage;
        TextView productName;
        TextView productPrice;
        TextView numOfUserRating;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.productImage);
            productName = itemView.findViewById(R.id.productName);
            productPrice = itemView.findViewById(R.id.productPrice);
            numOfUserRating = itemView.findViewById(R.id.numOfUserRating);
        }
    }

    public interface OnProductClickListener {
        void onProductClick(Product product);
    }
}
