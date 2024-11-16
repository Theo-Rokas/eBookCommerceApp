package com.example.ebookcommerceapp.ui.books;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ebookcommerceapp.activities.CreateOrEditActivity;
import com.example.ebookcommerceapp.activities.MainActivity;
import com.example.ebookcommerceapp.models.Book;
import com.example.ebookcommerceapp.R;
import com.example.ebookcommerceapp.ui.home.HomeViewModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BooksAdapter extends RecyclerView.Adapter<BooksViewHolder> {
    private BooksViewModel booksViewModel;
    private List<Book> books;
    private Context context;

    public BooksAdapter(BooksViewModel booksViewModel, List<Book> books, Context context) {
        this.booksViewModel = booksViewModel;
        this.books = books;
        this.context = context;
    }

    @NonNull
    @Override
    public BooksViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new BooksViewHolder(LayoutInflater.from(context).inflate(R.layout.books_item_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BooksViewHolder holder, int position) {
        Book book = books.get(position);

        Picasso.get().load(book.getBookImage()).into(holder.ivBookImage);
        holder.tvBookName.setText(book.getBookName());
        holder.tvBookAuthor.setText(book.getBookAuthor());
        holder.tvBookPages.setText(Integer.toString(book.getBookPages()));
        holder.tvBookPrice.setText(Double.toString(book.getBookPrice()));

        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, CreateOrEditActivity.class);

                intent.putExtra("bookId", book.getBookId());
                intent.putExtra("bookName", book.getBookName());
                intent.putExtra("bookAuthor", book.getBookAuthor());
                intent.putExtra("bookPages", book.getBookPages());
                intent.putExtra("bookPrice", book.getBookPrice());
                intent.putExtra("bookDescription", book.getBookDescription());

                context.startActivity(intent);
            }
        });

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int bookId = books.get(holder.getBindingAdapterPosition()).getBookId();
                booksViewModel.RemoveBook(bookId);
            }
        });
    }

    @Override
    public int getItemCount() { return books.size(); }
}
