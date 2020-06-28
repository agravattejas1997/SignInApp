package com.learn.signinapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.learn.signinapp.chat.LoginActivity;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 101;
    private SignInButton mSignInButton;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mFirebaseAuth;
    String personFamilyName,personName,personGivenName,personId,personEmail;
    Uri personPhoto;

    public static final String SHAREDPREF ="login";
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;




    private Button mSignOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("Main Activity","On Create");



        mFirebaseAuth = FirebaseAuth.getInstance();
        mSignInButton = findViewById(R.id.btn_signin);
        mSignOutButton = findViewById(R.id.btn_signout);

        sharedPreferences = getSharedPreferences(SHAREDPREF,MODE_PRIVATE);
        editor = sharedPreferences.edit();

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOptions);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mSignOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();


        Log.d("Main Activity","On Start");

    }

    @Override
    protected void onResume() {
        super.onResume();

//        if (sharedPreferences.getBoolean("log",false)) {
//            mSignInButton.setVisibility(View.INVISIBLE);
//
//        }
//        if (sharedPreferences.getBoolean("log",false))
//        {
//            mSignInButton.setVisibility(View.INVISIBLE);
//            startActivity(new Intent(MainActivity.this,ProfileActivity.class));
//        }

        Log.d("Main Activity","On Resume");

    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("Main Activity","On Pause");

    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Main Activity","On Stop");

//        editor.putBoolean("log",false);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("Main Activity","On Restart");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        editor.putBoolean("log",false);

        Log.d("Main Activity","On Destroy");

    }

    private void signOut() {


        mGoogleSignInClient.signOut();

        Toast.makeText(MainActivity.this,"You Are Logged Out",Toast.LENGTH_LONG).show();


            mSignInButton.setVisibility(View.VISIBLE);
            mSignOutButton.setVisibility(View.INVISIBLE);
            editor.putBoolean("log",false);




    }

    private void signIn() {

        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent,RC_SIGN_IN);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==RC_SIGN_IN)
        {
            editor.putBoolean("log",true);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInAccounts(task);

        }

    }

    private void handleSignInAccounts(Task<GoogleSignInAccount> completedTask) {

        try {
            GoogleSignInAccount acc = completedTask.getResult(ApiException.class);
            Toast.makeText(MainActivity.this,"Sign In Success",Toast.LENGTH_LONG).show();

            FirebaseGoogleAuth(acc);

        } catch (ApiException e) {
            e.printStackTrace();

            Toast.makeText(MainActivity.this,"Sign In Fail"+e,Toast.LENGTH_LONG).show();
            FirebaseGoogleAuth(null);
        }
    }

    private void FirebaseGoogleAuth(GoogleSignInAccount acct) {

        AuthCredential authCredential = GoogleAuthProvider.getCredential(acct.getIdToken(),null);

        mFirebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {

                    Toast.makeText(MainActivity.this,"Success",Toast.LENGTH_LONG).show();
                    FirebaseUser user = mFirebaseAuth.getCurrentUser();
                    updateUI(user);
                }
                else {

                    Toast.makeText(MainActivity.this,"Failed",Toast.LENGTH_LONG).show();
                    updateUI(null);

                }

            }
        });
    }

    private void updateUI(FirebaseUser firebaseUser) {

        mSignOutButton.setVisibility(View.VISIBLE);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if (account!=null)
        {
            personName=account.getDisplayName();
            personGivenName = account.getGivenName();
            personId= account.getId();
            personEmail = account.getEmail();
            personFamilyName= account.getFamilyName();
            personPhoto = account.getPhotoUrl();


            Toast.makeText(MainActivity.this,""+personName+" "+personEmail,Toast.LENGTH_LONG).show();

            editor.putString("nm",personName);
            editor.putString("email",personEmail);

            editor.commit();


            Intent intent= new Intent(MainActivity.this,ProfileActivity.class);
            startActivity(intent);
////            intent.putExtra("nm",personName);
//            intent.putExtra("email",personEmail);
//            intent.putExtra("photo",personPhoto);



        }
    }

    public void mapClick(View view) {
        startActivity(new Intent(MainActivity.this,AutoCompleteActivity.class));
    }

    public void loginClick(View view) {
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
    }
}
