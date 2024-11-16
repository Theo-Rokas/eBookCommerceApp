package com.example.ebookcommerceapp.activities;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebookcommerceapp.R;
import com.example.ebookcommerceapp.models.Genre;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;

public class CreateOrEditActivity extends AppCompatActivity {
    private EditText etBookName, etBookAuthor, etBookPages, etBookPrice, etBookDescription;
    private Spinner spGenre;
    private ImageView ivBookImage;
    private Button btnChooseImage, btnSubmitBook;
    private static final String domain = "http://192.168.1.202:44347/";
    private final List<Genre> genres = new ArrayList<>();
    private final List<String> genreNames = new ArrayList<>();
    private Bitmap bitmap;
    private String mobileBookImageFile;
    private String bookId;

    private final ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    if (selectedImageUri != null) {
                        try {
                            InputStream inputStream = getContentResolver().openInputStream(selectedImageUri);
                            bitmap = BitmapFactory.decodeStream(inputStream);
                            ivBookImage.setImageBitmap(bitmap);
                            encodeBitmapImage(bitmap);
                        } catch (Exception e) {
                            Log.e("ImagePicker", "Error loading image", e);
                        }
                    } else {
                        Log.e("ImagePicker", "No image selected");
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_edit);

        etBookName = findViewById(R.id.etBookName);
        etBookAuthor = findViewById(R.id.etBookAuthor);
        etBookPages = findViewById(R.id.etBookPages);
        etBookPrice = findViewById(R.id.etBookPrice);
        etBookDescription = findViewById(R.id.etBookDescription);
        spGenre = findViewById(R.id.spGenre);
        ivBookImage = findViewById(R.id.ivBookImage);
        btnChooseImage = findViewById(R.id.btnChooseImage);
        btnSubmitBook = findViewById(R.id.btnSubmitBook);

        Intent intent = getIntent();

        bookId = "0";
        if (intent.hasExtra("bookId")) {
            bookId = String.valueOf(intent.getIntExtra("bookId", 0)); // Use 0 as default
        }

        if (intent.hasExtra("bookName")) {
            String bookName = intent.getStringExtra("bookName");
            if (bookName != null && !bookName.isEmpty()) {
                etBookName.setText(bookName);
            }
        }

        if (intent.hasExtra("bookAuthor")) {
            String bookAuthor = intent.getStringExtra("bookAuthor");
            if (bookAuthor != null && !bookAuthor.isEmpty()) {
                etBookAuthor.setText(bookAuthor);
            }
        }

        if (intent.hasExtra("bookPages")) {
            int bookPages = intent.getIntExtra("bookPages", -1);
            if (bookPages != -1) {
                etBookPages.setText(String.valueOf(bookPages));
            }
        }

        if (intent.hasExtra("bookPrice")) {
            double bookPrice = intent.getDoubleExtra("bookPrice", -1.0);
            if (bookPrice != -1.0) {
                etBookPrice.setText(String.valueOf(bookPrice));
            }
        }

        if (intent.hasExtra("bookDescription")) {
            String bookDescription = intent.getStringExtra("bookDescription");
            if (bookDescription != null && !bookDescription.isEmpty()) {
                etBookDescription.setText(bookDescription);
            }
        }

        btnChooseImage.setOnClickListener(v -> openImagePicker());
        btnSubmitBook.setOnClickListener(v -> CreateOrEditBook());

        GetGenresMobile();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        imagePickerLauncher.launch(intent);
    }

    private void GetGenresMobile() {
        String api = domain + "Api/GetGenresMobile";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, api,
                response -> {
                    try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject singleObject = array.getJSONObject(i);
                            Genre genre = new Genre(
                                    singleObject.getInt("genreId"),
                                    singleObject.getString("genreName")
                            );
                            genres.add(genre);
                            genreNames.add(genre.getGenreName());
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, genreNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spGenre.setAdapter(adapter);

                        Log.d("api", "Fetched genres: " + genres.size());
                    } catch (JSONException e) {
                        Log.e("api", "Parsing error: " + e.getMessage());
                    }
                }, error -> Log.e("api", "Request error: " + error.getLocalizedMessage())
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(stringRequest);
    }

    private void encodeBitmapImage(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] bytesOfImage = byteArrayOutputStream.toByteArray();
        mobileBookImageFile = Base64.encodeToString(bytesOfImage, Base64.DEFAULT);
    }

    private void CreateOrEditBook() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String email = sharedPreferences.getString("SavedEmail", "");

        String bookName = etBookName.getText().toString().trim();
        String bookAuthor = etBookAuthor.getText().toString().trim();
        String bookPages = etBookPages.getText().toString().trim();

        String bookPrice = etBookPrice.getText().toString().trim();
        if (!bookPrice.isEmpty()) {
            try {
                double price = Double.parseDouble(bookPrice);
                bookPrice = String.format(Locale.GERMANY, "%.2f", price);
            } catch (NumberFormatException e) {
                Toasty.error(CreateOrEditActivity.this, "Invalid price format", Toast.LENGTH_SHORT, true).show();
                return;
            }
        } else {
            bookPrice = "0,00";
        }

        String finalBookPrice = bookPrice;
        String bookDescription = etBookDescription.getText().toString().trim();
        String genreId = String.valueOf(genres.get(spGenre.getSelectedItemPosition()).getGenreId());

        String apiUrl = domain + "Api/CreateOrEditBook";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                ivBookImage.setImageResource(R.drawable.ic_launcher_foreground);

                try {
                    JSONObject object = new JSONObject(response);
                    String status = object.getString("status");
                    String message = object.getString("message");

                    if(status.equals("OK")) {
                        Toasty.success(CreateOrEditActivity.this, message, Toast.LENGTH_LONG, true).show();

                        Intent intent = new Intent(CreateOrEditActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toasty.error(CreateOrEditActivity.this, message, Toast.LENGTH_LONG, true).show();
                    }

                    Log.e("api", "onResponse: " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("api", "onResponse: " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                ivBookImage.setImageResource(R.drawable.ic_launcher_foreground);
                Log.e("api", "onErrorResponse: " + error.getLocalizedMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", email);
                params.put("bookId", bookId);
                params.put("book.bookName", bookName);
                params.put("book.bookAuthor", bookAuthor);
                params.put("book.bookPages", bookPages);
                params.put("book.bookPrice", finalBookPrice);
                params.put("book.bookDescription", bookDescription);
                params.put("book.genreId", genreId);
                params.put("mobileBookImageFile", mobileBookImageFile != null ? mobileBookImageFile : "");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        queue.add(stringRequest);
    }
}
