package com.example.ebookcommerceapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class PaymentActivity extends AppCompatActivity {
    private static final String domain = "http://192.168.1.202:44347/";
    private WebView paymentWebView;
    private ProgressDialog progressDialog;
    private String orderId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        paymentWebView = findViewById(R.id.paymentWebView);
        WebSettings webSettings = paymentWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processing Payment...");
        progressDialog.setCancelable(false);

        SharedPreferences sharedPreferences = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        String email = sharedPreferences.getString("SavedEmail", "");

        String paymentInitUrl = domain + "Api/PaymentInit?email=" + email;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, paymentInitUrl,
                response -> {
                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        String message = object.getString("message");
                        String redirectUrl = object.getString("redirectUrl");

                        if (status.equals("OK")) {
                            loadRedirectUrl(redirectUrl);
                        } else {
                            Toasty.error(PaymentActivity.this, message, Toast.LENGTH_LONG, true).show();
                        }

                        Log.e("api", "onResponse: " + message);
                    }
                    catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("api", "onResponse: " + e.getMessage());
                    }
                },
                error -> Log.e("api", "onErrorResponse: " + error.getLocalizedMessage())
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(stringRequest);
    }

    private void loadRedirectUrl(String url) {
        paymentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                if (url.contains("PaymentComplete")) {
                    Uri uri = Uri.parse(url);
                    orderId = uri.getQueryParameter("orderId");
                    if (orderId != null) {
                        initiatePaymentCompleteRequest(orderId);
                    }
                    return true;
                }
                return false;
            }
        });
        paymentWebView.loadUrl(url);
    }

    private void initiatePaymentCompleteRequest(String orderId) {
        progressDialog.show();

        String paymentCompleteUrl = domain + "Api/PaymentComplete?orderId=" + orderId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, paymentCompleteUrl,
                response -> {
                    progressDialog.dismiss();

                    try {
                        JSONObject object = new JSONObject(response);
                        String status = object.getString("status");
                        String message = object.getString("message");

                        if (status.equals("OK")) {
                            Toasty.success(PaymentActivity.this, message, Toast.LENGTH_LONG, true).show();

                            Intent intent = new Intent(PaymentActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Toasty.error(PaymentActivity.this, message, Toast.LENGTH_LONG, true).show();
                        }

                        Log.e("api", "onResponse: " + message);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("api", "onResponse: " + e.getMessage());
                    }
                },
                error -> {
                    progressDialog.dismiss();
                    Log.e("api", "onErrorResponse: " + error.getLocalizedMessage());
                }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        ));

        requestQueue.add(stringRequest);
    }
}
