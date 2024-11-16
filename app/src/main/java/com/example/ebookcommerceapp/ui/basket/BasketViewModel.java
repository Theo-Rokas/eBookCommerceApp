package com.example.ebookcommerceapp.ui.basket;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebookcommerceapp.R;
import com.example.ebookcommerceapp.models.BasketItem;
import com.example.ebookcommerceapp.models.Book;
import com.example.ebookcommerceapp.models.Genre;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class BasketViewModel extends AndroidViewModel {
    private static String domain = "http://192.168.1.202:44347/";

    private SharedPreferences sharedPreferences;
    private MutableLiveData<List<BasketItem>> basketItems = new MutableLiveData<>();

    public BasketViewModel(@NonNull Application application) {
        super(application);

        sharedPreferences = getApplication().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
    }

    public LiveData<List<BasketItem>> getBasketItems() {
        return basketItems;
    }

    public void GetBasketItemsMobile() {
        String email = sharedPreferences.getString("SavedEmail", "");
        String api = domain + "Api/GetBasketItemsMobile?email=" + email;

        List<BasketItem> basketItems = new ArrayList<BasketItem>();

        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject singleObject = array.getJSONObject(i);
                            basketItems.add(new BasketItem(
                                    singleObject.getInt("basketItemId"),
                                    singleObject.getString("personEmail"),
                                    new Book(
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
                                    )
                            ));
                        }

                        this.basketItems.setValue(basketItems);

                        Log.e("api", "onResponse: " + basketItems.size());
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

    public void RemoveItemFromBasket (int basketItemId) {
        String api = domain + "Api/RemoveItemFromBasket?itemId=" + basketItemId;

        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        String message = object.getString("message");

                        if(status.equals("OK")) {
                            Toasty.success(getApplication(), message, Toast.LENGTH_LONG, true).show();
                            GetBasketItemsMobile();
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

    public void RemoveAllItemsFromBasket () {
        String email = sharedPreferences.getString("SavedEmail", "");
        String api = domain + "Api/RemoveAllItemsFromBasket?email=" + email;

        RequestQueue queue = Volley.newRequestQueue(getApplication());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, api,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        String message = object.getString("message");

                        if(status.equals("OK")) {
                            Toasty.success(getApplication(), message, Toast.LENGTH_LONG, true).show();
                            GetBasketItemsMobile();
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