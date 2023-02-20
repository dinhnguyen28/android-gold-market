package com.example.goldpricemarket;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.squareup.picasso.Picasso;

import static com.google.android.gms.auth.api.signin.GoogleSignIn.getLastSignedInAccount;

public class UserInfoActivity extends AppCompatActivity {
    ImageView userProfileImage;
    TextView userProfileName, userProfileEmail;
    private GoogleApiClient googleApiClient;
    private GoogleSignInOptions gso;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

        userProfileImage = (ImageView) findViewById(R.id.userProfileImage);
        userProfileName = (TextView) findViewById(R.id.userProfileName);
        userProfileEmail = (TextView) findViewById(R.id.userProfileEmail);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        GoogleSignInAccount account = getLastSignedInAccount(UserInfoActivity.this);

        if(account != null) {

            userProfileName.setText(account.getDisplayName());
            userProfileEmail.setText(account.getEmail());

            Uri photo = account.getPhotoUrl();
            Picasso.get().load(photo).placeholder(R.drawable.ic_user_profile).into(userProfileImage);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}