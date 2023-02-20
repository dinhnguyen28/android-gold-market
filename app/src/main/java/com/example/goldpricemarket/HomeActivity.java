package com.example.goldpricemarket;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.scrounger.countrycurrencypicker.library.Buttons.CountryCurrencyButton;
import com.scrounger.countrycurrencypicker.library.Country;
import com.scrounger.countrycurrencypicker.library.Currency;
import com.scrounger.countrycurrencypicker.library.Listener.CountryCurrencyPickerListener;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.text.BreakIterator;
import java.text.DecimalFormat;

import okhttp3.Headers;

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "TAG";

    private ActionBarDrawerToggle drawerToggle;
    private DrawerLayout drawerLayout;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    CountryCurrencyButton selectCountryBtn;
    //Button getValue;



    private final String urlMetal = "https://metals-api.com/api/latest";
    private final String keyApiMetal = "319ff06g18090z16o390r9ez09tlz0de6rpi9tgmwtlj375kjd720gg1kqw9";
    String currency = "";


    ImageView userImage;
    TextView userFullName, userEmailName,
            perOunce, perGram, perKg,
            priceOfGoldIn, dateApi;

    LinearLayout userMenuProfile, userMenuLogout;
    String tempUrl = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        drawerLayout = findViewById(R.id.activity_main_drawer);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(drawerToggle);

        userImage = (ImageView) findViewById(R.id.userImage);
        userFullName = (TextView) findViewById(R.id.userFullName);
        userEmailName = (TextView) findViewById(R.id.userEmailName);



        priceOfGoldIn = (TextView) findViewById(R.id.priceOfGoldIn);

        perOunce = (TextView) findViewById(R.id.perOunce);
        perGram = (TextView) findViewById(R.id.perGram);
        perKg = (TextView) findViewById(R.id.perKg);


        dateApi = (TextView) findViewById(R.id.dateTime);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

        googleApiClient = new GoogleApiClient.Builder(this).enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();


        getRequestData1("USD");

        selectCountryBtn = (CountryCurrencyButton) findViewById(R.id.selectCountry);

        selectCountryBtn.setOnClickListener(new CountryCurrencyPickerListener() {
            @Override
            public void onSelectCountry(Country country) {
                    
                currency = String.format("%s", country.getCurrency().getCode());
                priceOfGoldIn.setText("Price Of Gold In "+ country.getName());
                getRequestData(currency);
            }

            @Override
            public void onSelectCurrency(Currency currency) {

            }
        });



        userMenuProfile = (LinearLayout) findViewById(R.id.userProfileClick);

        userMenuProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this, UserInfoActivity.class));
            }
        });

        userMenuLogout = (LinearLayout) findViewById(R.id.userLogoutClick);

        userMenuLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Auth.GoogleSignInApi.signOut(googleApiClient).setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            onBackPressed();
                            Toast.makeText(HomeActivity.this, "Log Out Success", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "Log Out Failed !", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }


    private void getRequestData(String currencyy) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        tempUrl = urlMetal + "?access_key=" + keyApiMetal + "&base=USD" + "&symbol=XAU";

        //Log.d(TAG, "currency: " + currencyy);

        JsonObjectRequest jsoRequest = new JsonObjectRequest(Request.Method.GET,
                tempUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String XAU = "", currencyChoose = "";
                String date = "";
                //Log.d(TAG, "Newresponse: "+response);


                try {
                    date = response.getString("date");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                dateApi.setText(date);

                JSONObject result = null;
                try {
                    result = response.getJSONObject("rates");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    XAU = result.getString("XAU");
                    currencyChoose = result.getString(currencyy);
                    //Log.d(TAG, "onResponse: " + XAU + "_" + USD);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                double XAUf = Double.parseDouble(XAU);
                double USDf = Double.parseDouble(currencyChoose);

                double Price = USDf / XAUf;

                double kgPrice = Price/0.031;
                double gramPrice = Price/31;



                String pattern = "###,###.######";
                DecimalFormat decimalFormat = new DecimalFormat(pattern);

                String priceOz = decimalFormat.format(new BigDecimal(Price));
                String priceGr = decimalFormat.format(new BigDecimal(gramPrice));
                String priceKg = decimalFormat.format(new BigDecimal(kgPrice));


                perOunce.setText(": " + priceOz  + " " + currencyy);
                perGram.setText(": " + priceGr + " " + currencyy);
                perKg.setText(": " + priceKg + " " + currencyy);
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsoRequest);
    }


    private void getRequestData1(String currencyy) {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        tempUrl = urlMetal + "?access_key=" + keyApiMetal + "&base=USD" + "&symbol=XAU";

        //Log.d(TAG, "currency: " + currencyy);

        JsonObjectRequest jsoRequest = new JsonObjectRequest(Request.Method.GET,
                tempUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                String XAU = "", currencyChoose = "";
                String date = "";
                //Log.d(TAG, "Newresponse: "+response);


                try {
                    date = response.getString("date");
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                dateApi.setText(date);

                JSONObject result = null;
                try {
                    result = response.getJSONObject("rates");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    XAU = result.getString("XAU");
                    currencyChoose = result.getString(currencyy);
                    //Log.d(TAG, "onResponse: " + XAU + "_" + USD);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                double XAUf = Double.parseDouble(XAU);
                double USDf = Double.parseDouble(currencyChoose);

                double Price = USDf / XAUf;

                double kgPrice = Price/0.031;
                double gramPrice = Price/31;



                String pattern = "###,###.######";
                DecimalFormat decimalFormat = new DecimalFormat(pattern);

                String priceOz = decimalFormat.format(new BigDecimal(Price));
                String priceGr = decimalFormat.format(new BigDecimal(gramPrice));
                String priceKg = decimalFormat.format(new BigDecimal(kgPrice));

                perOunce.setText(": " + priceOz  + " " + currencyy);
                perGram.setText(": " + priceGr + " " + currencyy);
                perKg.setText(": " + priceKg + " " + currencyy);

                priceOfGoldIn.setText("Price Of Gold In United States");

            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        queue.add(jsoRequest);
    }


//    private void backToLoginActivity() {
//        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
//        finish();
//    }

    private void googleInfoSignInResult(GoogleSignInResult googleResultInfo) {
        if(googleResultInfo.isSuccess()) {

            GoogleSignInAccount account = googleResultInfo.getSignInAccount();

            userFullName.setText(account.getDisplayName());
            userEmailName.setText(account.getEmail());

            Uri photo = account.getPhotoUrl();
            Picasso.get().load(photo).placeholder(R.drawable.ic_profile).into(userImage);
        }
        else {
            onBackPressed();
        }
    }


    @Override
    protected void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opt = Auth.GoogleSignInApi.silentSignIn(googleApiClient);

        if(opt.isDone()) {
            GoogleSignInResult googleSignInResult = opt.get();
            googleInfoSignInResult(googleSignInResult);
        }
        else {
            opt.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                    googleInfoSignInResult(googleSignInResult);
                }
            });
        }
    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        drawerToggle.onConfigurationChanged(newConfig);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main_actions, menu);
//        return super.onCreateOptionsMenu(menu);
//    }
//
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
