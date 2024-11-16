package com.example.ebookcommerceapp.ui.home;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebookcommerceapp.models.Book;
import com.example.ebookcommerceapp.R;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeViewHolder> {
    private HomeViewModel homeViewModel;
    private List<Book> books;
    private Context context;

    public HomeAdapter(HomeViewModel homeViewModel, List<Book> books, Context context) {
        this.homeViewModel = homeViewModel;
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public HomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(context).inflate(R.layout.home_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull HomeViewHolder holder, int position) {
        Picasso.get().load(books.get(position).getBookImage()).into(holder.ivBookImage);
        holder.tvBookName.setText(books.get(position).getBookName());
        holder.tvBookAuthor.setText(books.get(position).getBookAuthor());
        holder.tvBookPages.setText(Integer.toString(books.get(position).getBookPages()));
        holder.tvBookPrice.setText(Double.toString(books.get(position).getBookPrice()));
        holder.tvBookDescription.setText(books.get(position).getBookDescription());

        holder.btnSeeMore.setOnClickListener(new View.OnClickListener() {
            private boolean isDescriptionVisible = false;

            @Override
            public void onClick(View view) {
                if (!isDescriptionVisible) {
                    holder.ivBookImage.setVisibility(View.GONE);
                    holder.tvBookName.setVisibility(View.GONE);
                    holder.tvBookAuthor.setVisibility(View.GONE);
                    holder.tvBookPages.setVisibility(View.GONE);
                    holder.tvBookPrice.setVisibility(View.GONE);

                    holder.tvBookDescription.setVisibility(View.VISIBLE);

                    holder.btnSeeMore.setText("See Less");
                } else {
                    holder.ivBookImage.setVisibility(View.VISIBLE);
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

        holder.btnAddToBasket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int bookId = books.get(holder.getBindingAdapterPosition()).getBookId();
                homeViewModel.AddToBasket(bookId);
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
}
