package com.example.ebookcommerceapp.ui.basket;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebookcommerceapp.R;

public class BasketViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivBookImage;
    public TextView tvPersonEmail, tvBookName, tvBookAuthor, tvBookPages, tvBookPrice, tvBookDescription;
    public Button btnSeeMore, btnRemove;

    public BasketViewHolder(@NonNull View itemView) {
        super(itemView);

        ivBookImage = itemView.findViewById(R.id.ivBookImage);
        tvPersonEmail = itemView.findViewById(R.id.tvPersonEmail);
        tvBookName = itemView.findViewById(R.id.tvBookName);
        tvBookAuthor = itemView.findViewById(R.id.tvBookAuthor);
        tvBookPages = itemView.findViewById(R.id.tvBookPages);
        tvBookPrice = itemView.findViewById(R.id.tvBookPrice);
        tvBookDescription = itemView.findViewById(R.id.tvBookDescription);
        btnSeeMore = itemView.findViewById(R.id.btnSeeMore);
        btnRemove = itemView.findViewById(R.id.btnRemove);
    }
}
