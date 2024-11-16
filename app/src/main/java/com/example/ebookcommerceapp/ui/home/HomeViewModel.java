package com.example.ebookcommerceapp.ui.home;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebookcommerceapp.models.Book;
import com.example.ebookcommerceapp.models.Genre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class HomeViewModel extends AndroidViewModel {
    private static String domain = "http://192.168.1.202:44347/";
    private SharedPreferences sharedPreferences;
    private MutableLiveData<List<Book>> books = new MutableLiveData<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);

        sharedPreferences = getApplication().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
    }

    public LiveData<List<Book>> getBooks() {
        return books;
    }

    public void GetOthersBooksMobile() {
        String email = sharedPreferences.getString("SavedEmail", "");
        String api = domain + "Api/GetOthersBooksMobile?email=" + email;

        List<Book> books = new ArrayList<Book>();

        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject singleObject = array.getJSONObject(i);
                            books.add(new Book(
                                    singleObject.getInt("bookId"),
                                    singleObject.getString("bookImageUrl"),
                                    singleObject.getString("bookName"),
                                    singleObject.getString("bookAuthor"),
                                    singleObject.getInt("bookPages"),
                                    singleObject.getDouble("bookPrice"),
                                    singleObject.getString("bookDescription"),
                                    new Genre(
                                        singleObject.getInt("genreId"),
                                        singleObject.getString("genreName")
                                    )
                            ));
                        }

                        this.books.setValue(books);

                        Log.e("api", "onResponse: " + books.size());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("api", "onResponse: " + e.getMessage());
                    }
                }, error -> Log.e("api", "onErrorResponse: " + error.getLocalizedMessage())
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(stringRequest);
    }

    public void AddToBasket (int bookId) {
        String email = sharedPreferences.getString("SavedEmail", "");
        String api = domain + "Api/AddToBasket?email=" + email + "&bookId=" + bookId;

        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        String message = object.getString("message");

                        if(status.equals("OK")) {
                            Toasty.success(getApplication(), message, Toast.LENGTH_LONG, true).show();
                            GetOthersBooksMobile();
                        }
                        else {
                            Toasty.error(getApplication(), message, Toast.LENGTH_LONG, true).show();
                        }

                        Log.e("api", "onResponse: " + message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("api", "onResponse: " + e.getMessage());
                    }
                }, error -> Log.e("api", "onErrorResponse: " + error.getLocalizedMessage())
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(stringRequest);
    }
}