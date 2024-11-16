package com.example.ebookcommerceapp.ui.basket;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebookcommerceapp.activities.CreateOrEditActivity;
import com.example.ebookcommerceapp.models.BasketItem;
import com.example.ebookcommerceapp.models.Book;
import com.example.ebookcommerceapp.activities.PaymentActivity;
import com.example.ebookcommerceapp.R;
import com.example.ebookcommerceapp.databinding.FragmentBasketBinding;
import com.example.ebookcommerceapp.ui.home.HomeAdapter;
import com.example.ebookcommerceapp.ui.home.HomeViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BasketFragment extends Fragment {
    private FragmentBasketBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBasketBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        BasketViewModel basketViewModel = new ViewModelProvider(this).get(BasketViewModel.class);

        basketViewModel.GetBasketItemsMobile();

        basketViewModel.getBasketItems().observe(getViewLifecycleOwner(), basketItems -> {
            binding.recyclerView.setAdapter(new BasketAdapter(basketViewModel, basketItems, getContext()));
        });

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        binding.payFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), PaymentActivity.class);
                startActivity(intent);
            }
        });

        binding.createFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CreateOrEditActivity.class);
                startActivity(intent);
            }
        });

        binding.deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                basketViewModel.RemoveAllItemsFromBasket();
            }
        });

        return root;
    }

@Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}