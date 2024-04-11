package com.example.e_commerce_app.screens;

import android.nfc.Tag;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce_app.R;
import com.example.e_commerce_app.adapters.PaymentAdapter;
import com.example.e_commerce_app.models.Cart;
import com.example.e_commerce_app.models.CartDetail;
import com.example.e_commerce_app.models.Product;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class PaymentScreen extends Fragment {
    private FirebaseFirestore db;
    private ImageView backBtn;
    private BottomNavigationView bottomNavigationView;
    private List<Product> products;
    private List<CartDetail> cartDetails;
    private String totalPaymentPrice;
    private RecyclerView productPaymentContainer;
    private ProgressBar progressBar;
    private Button paymentButton;
    private TextView totalPrice, totalPayment, totalPaymentFinal;

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
            cartDetails = (List<CartDetail>) getArguments().getSerializable("cartDetails");
            totalPaymentPrice = (String) getArguments().getSerializable("totalPayment");

            setControl(view);
            setEvent(products, cartDetails);
        }

        return view;
    }

    private void setControl(View view) {
        backBtn = view.findViewById(R.id.payment_screen_back_btn);
        productPaymentContainer = view.findViewById(R.id.payment_screen_list_item);
        progressBar = view.findViewById(R.id.payment_screen_progress_circle);
        paymentButton = view.findViewById(R.id.payment_screen_buy_btn);
        totalPrice = view.findViewById(R.id.payment_screen_total_price);
        totalPayment = view.findViewById(R.id.payment_screen_total_payment);
        totalPaymentFinal = view.findViewById(R.id.payment_screen_total_payment_final);
        totalPrice.setText("đ" + totalPaymentPrice);
        totalPayment.setText("đ" + totalPaymentPrice);
        totalPaymentFinal.setText("đ" + totalPaymentPrice);
    }

    private void setEvent(List<Product> products, List<CartDetail> cartDetails) {
        setOrderItemView(products, cartDetails);
        makePayment();
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void setOrderItemView(List<Product> products, List<CartDetail> cartDetails) {
        productPaymentContainer.setHasFixedSize(true);
        productPaymentContainer.setLayoutManager(new LinearLayoutManager(this.getContext()));

        PaymentAdapter paymentAdapter = new PaymentAdapter(this.getContext(), products, cartDetails);
        productPaymentContainer.setAdapter(paymentAdapter);
        progressBar.setVisibility(View.GONE);
    }

    private void makePayment() {
        paymentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;
                boolean isValid = false;
                for (Product product : products) {
                    for (CartDetail cartDetail : cartDetails) {
                        if (Objects.equals(product.getProductId(), cartDetail.getProductId())) {
                            quantity = cartDetail.getQuantity();
                        }
                    }
                    if (product.getQuantity() - quantity >= 0) {
                        isValid = true;
                    } else {
                        isValid = false;
                        db = FirebaseFirestore.getInstance();
                        removeProductInCart(product);

                        Toast.makeText(PaymentScreen.this.getContext(), product.getProductName() + " out of stock", Toast.LENGTH_SHORT).show();

                        bottomNavigationView.setVisibility(View.VISIBLE);
                        requireActivity().getSupportFragmentManager().popBackStack("home_screen", FragmentManager.POP_BACK_STACK_INCLUSIVE);

                        break;
                    }
                }
                if (isValid) {

                    db = FirebaseFirestore.getInstance();
                    DocumentReference newOrderRef = db.collection("orders").document();

                    Map<String, Object> data = new HashMap<>();
                    data.put("orderId", newOrderRef.getId());
                    data.put("createDate", new Date());
                    data.put("address", "address");
                    data.put("uid", "current_user");

                    newOrderRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(PaymentScreen.this.getContext(), "Your payment was successful", Toast.LENGTH_SHORT).show();
                            for (Product product : products) {
                                int paymentPrice = 0;
                                int quantity = 0;
                                String paymentPriceString = "";
                                for (CartDetail cartDetail : cartDetails) {
                                    NumberFormat formatter = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
                                    if (Objects.equals(product.getProductId(), cartDetail.getProductId())) {
                                        quantity = cartDetail.getQuantity();
                                    }
                                    try {
                                        Number number = formatter.parse(product.getPrice());
                                        int num = number.intValue();
                                        paymentPrice = num * quantity;
                                        paymentPriceString = formatter.format(paymentPrice);
                                    } catch (ParseException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                                removeProductInCart(product);

                                db.collection("product").document(product.getProductId()).update("quantity", product.getQuantity() - quantity);

                                Map<String, Object> newOrderDetail = new HashMap<>();
                                newOrderDetail.put("orderId", newOrderRef.getId());
                                newOrderDetail.put("productId", product.getProductId());
                                newOrderDetail.put("price", paymentPriceString);
                                newOrderDetail.put("quantity", quantity);
                                db.collection("order_detail").add(newOrderDetail).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(PaymentScreen.this.getContext(), "Error create order detail", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                bottomNavigationView.setVisibility(View.VISIBLE);
                                requireActivity().getSupportFragmentManager().popBackStack("home_screen", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                            }
                            db.collection("delivery_status").whereEqualTo("statusName", "Chờ xác nhận").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    String statusId = document.getId();

                                                    Map<String, Object> statusDetailData = new HashMap<>();
                                                    statusDetailData.put("statusId", statusId);
                                                    statusDetailData.put("orderId", newOrderRef.getId());
                                                    statusDetailData.put("dateOfStatus", new Date());

                                                    db.collection("ds_detail").add(statusDetailData).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                        @Override
                                                        public void onSuccess(DocumentReference documentReference) {
                                                            Log.d("ds_detail", "Added successfully");
                                                        }
                                                    }).addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("ds_detail", "Error adding document");
                                                        }
                                                    });
                                                }
                                            } else {
                                                Toast.makeText(PaymentScreen.this.getContext(), "Error getting status document", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    });
                }


            }
        });
    }

    private void removeProductInCart(Product product) {
        db.collection("cart").whereEqualTo("uid", "current_user").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot carts) {
                        if (!carts.isEmpty()) {
                            for (DocumentSnapshot cart : carts.getDocuments()) {
                                Cart cartObject = cart.toObject(Cart.class);
                                db.collection("cart_detail").whereEqualTo("cartId", cartObject.getCartId())
                                        .whereEqualTo("productId", product.getProductId()).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().delete();
                                                    }
                                                } else {
                                                    Toast.makeText(PaymentScreen.this.getContext(),
                                                            "Something went wrong", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });

                            }

                        }
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