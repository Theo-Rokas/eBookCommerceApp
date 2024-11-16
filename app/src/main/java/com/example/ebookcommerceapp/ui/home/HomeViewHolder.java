package com.example.ebookcommerceapp.ui.home;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebookcommerceapp.R;

public class HomeViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivBookImage;
    public TextView tvBookName, tvBookAuthor, tvBookPages, tvBookPrice, tvBookDescription;
    public Button btnSeeMore, btnAddToBasket;

    public HomeViewHolder(@NonNull View itemView) {
        super(itemView);

        ivBookImage = itemView.findViewById(R.id.ivBookImage);
        tvBookName = itemView.findViewById(R.id.tvBookName);
        tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
        tvBookPages = itemView.findViewById(R.id.tvBookPages);
        tvBookPrice = itemView.findViewById(R.id.tvBookPrice);
        tvBookDescription = itemView.findViewById(R.id.tvBookDescription);
        btnSeeMore = itemView.findViewById(R.id.btnSeeMore);
        btnAddToBasket = itemView.findViewById(R.id.btnAddToBasket);
    }
}
