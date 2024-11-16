package com.example.ebookcommerceapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.ebookcommerceapp.R;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {
    private EditText etEmail, etPassword, etConfirm;
    private Button btnRegister, btnLogin;
    private static String domain = "http://192.168.1.202:44347/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirm = findViewById(R.id.etConfirm);

        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                String confirm = etConfirm.getText().toString();

                String api = domain + "Api/Register?Email=" + email + "&Password=" + password+ "&ConfirmPassword=" + confirm;

                RequestQueue queue = Volley.newRequestQueue(RegisterActivity.this);
                StringRequest stringRequest = new StringRequest(Request.Method.POST, api,
                        response -> {
                            try {
                                JSONObject object = new JSONObject(response);
                                String status = object.getString("status");
                                String message = object.getString("message");

                                if(status.equals("OK")) {
                                    Toasty.success(RegisterActivity.this, message, Toast.LENGTH_LONG, true).show();

                                    editor.putString("SavedEmail", email);
                                    editor.putString("SavedPassword", password);
                                    editor.commit();

                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                else {
                                    Toasty.error(RegisterActivity.this, message, Toast.LENGTH_LONG, true).show();
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
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}