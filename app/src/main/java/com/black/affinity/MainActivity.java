package com.black.affinity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "Auth";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;

    /* Interface Elements */
    private LinearLayout messagebox;
    private ImageView loginButton;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Permission Check
        int PERMISSIONS_ALL = 1;
        String[] permissions = {
                Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
        };
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, PERMISSIONS_ALL);
        }
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
        mAuth = FirebaseAuth.getInstance();

        messagebox = findViewById(R.id.messagebox);
        loginButton = findViewById(R.id.signin);

        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            loginButton.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }else{
            FirebaseUser currentUser = mAuth.getCurrentUser();
            assert currentUser != null;
            launchNuOS(currentUser);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        updateUI("signin",null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                assert account != null;
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            boolean user = task.getResult().getAdditionalUserInfo().isNewUser();
                            if(!user){ /* old user */
                                FirebaseUser currentUser = mAuth.getCurrentUser();
                                launchNuOS(currentUser);
                            }else{ /* new user */
                                updateUI("success_new",null);
                            }
                        } else {
                            updateUI("failed",null);
                        }
                    }
                });
    }

    public void launchNuOS(FirebaseUser user){
        startActivity(new Intent(MainActivity.this,AffinityActivity.class));
        finish();
        updateUI("success_old",user);
    }

    public void updateUI(String msg,FirebaseUser user){
        TextView message = findViewById(R.id.signinmessage);
        TextView submessage = findViewById(R.id.signinsubmessage);
        ProgressBar progressBar = findViewById(R.id.signinprogress);

        switch (msg){
            case "signin":
                loginButton.setVisibility(View.GONE);
                messagebox.setVisibility(View.VISIBLE);
                message.setText("Please Wait...");
                submessage.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                break;
            case "success_new":
                loginButton.setVisibility(View.GONE);
                messagebox.setVisibility(View.VISIBLE);
                message.setText("REGISTERING NEW USER PLEASE WAIT");
                submessage.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        updateUI("done",null);
                    }
                }, 5000);
                break;
            case "success_old":
                loginButton.setVisibility(View.GONE);
                messagebox.setVisibility(View.VISIBLE);
                message.setText(user.getDisplayName());
                submessage.setVisibility(View.VISIBLE);
                submessage.setText("WELCOME BACK");
                progressBar.setVisibility(View.GONE);
                break;
            case "failed":
                loginButton.setVisibility(View.GONE);
                messagebox.setVisibility(View.VISIBLE);
                message.setText("AUTHENTICATION FAILED");
                submessage.setVisibility(View.VISIBLE);
                submessage.setText("TRY AGAIN!");
                progressBar.setVisibility(View.GONE);
                break;
            default:
                loginButton.setVisibility(View.VISIBLE);
                messagebox.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"USER REGISTERED",Toast.LENGTH_LONG).show();
                break;
        }
    }

    /*Check Permission*/
    public static boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }


    @Override
    protected void onStart() {
        super.onStart();
    }
}