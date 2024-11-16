package com.example.ebookcommerceapp.ui.basket;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebookcommerceapp.models.BasketItem;
import com.example.ebookcommerceapp.R;
import com.example.ebookcommerceapp.ui.home.HomeViewModel;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class BasketAdapter extends RecyclerView.Adapter<BasketViewHolder> {
    private BasketViewModel basketViewModel;
    private Context context;
    private List<BasketItem> basketItems;

    public BasketAdapter(BasketViewModel basketViewModel, List<BasketItem> basketItems, Context context) {
        this.basketViewModel = basketViewModel;
        this.basketItems = basketItems;
        this.context = context;
    }

    @NonNull
    @Override
    public BasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BasketViewHolder(LayoutInflater.from(context).inflate(R.layout.basket_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BasketViewHolder holder, int position) {
        Picasso.get().load(basketItems.get(position).getBook().getBookImage()).into(holder.ivBookImage);
        holder.tvPersonEmail.setText(basketItems.get(position).getPersonEmail());
        holder.tvBookName.setText(basketItems.get(position).getBook().getBookName());
        holder.tvBookAuthor.setText(basketItems.get(position).getBook().getBookAuthor());
        holder.tvBookPages.setText(Integer.toString(basketItems.get(position).getBook().getBookPages()));
        holder.tvBookPrice.setText(Double.toString(basketItems.get(position).getBook().getBookPrice()));
        holder.tvBookDescription.setText(basketItems.get(position).getBook().getBookDescription());

        holder.btnSeeMore.setOnClickListener(new View.OnClickListener() {
            private boolean isDescriptionVisible = false;

            @Override
            public void onClick(View view) {
                if (!isDescriptionVisible) {
                    holder.ivBookImage.setVisibility(View.GONE);
                    holder.tvPersonEmail.setVisibility(View.GONE);
                    holder.tvBookName.setVisibility(View.GONE);
                    holder.tvBookAuthor.setVisibility(View.GONE);
                    holder.tvBookPages.setVisibility(View.GONE);
                    holder.tvBookPrice.setVisibility(View.GONE);

                    holder.tvBookDescription.setVisibility(View.VISIBLE);

                    holder.btnSeeMore.setText("See Less");
                } else {
                    holder.ivBookImage.setVisibility(View.VISIBLE);
                    holder.tvPersonEmail.setVisibility(View.VISIBLE);
                    holder.tvBookName.setVisibility(View.VISIBLE);
                    holder.tvBookAuthor.setVisibility(View.VISIBLE);
                    holder.tvBookPages.setVisibility(View.VISIBLE);
                    holder.tvBookPrice.setVisibility(View.VISIBLE);

                    holder.tvBookDescription.setVisibility(View.GONE);

                    holder.btnSeeMore.setText("See More");
                }

                isDescriptionVisible = !isDescriptionVisible;
            }
        });

        holder.btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int basketItemId = basketItems.get(holder.getBindingAdapterPosition()).getBasketItemId();
                basketViewModel.RemoveItemFromBasket(basketItemId);
            }
        });

    }

    @Override
    public int getItemCount() { return basketItems.size(); }
}
