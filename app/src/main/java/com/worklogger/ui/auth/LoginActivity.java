package com.worklogger.ui.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.worklogger.R;
import com.worklogger.ui.dashboard.MainActivity;
import com.worklogger.utils.UI;

public class LoginActivity extends AppCompatActivity {

    private ProgressDialog mProgressDialog;
    private static final int RC_SIGN_IN_GOOGLE = 7;
    private FirebaseAuth mFireBaseAuth=FirebaseAuth.getInstance();
    private GoogleSignInClient mGoogleSignInClient;
    private UI ui;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if(mFireBaseAuth.getCurrentUser()!=null) startMainActivity();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }

    private void init() {
        ui = new UI(this);
        mProgressDialog = new ProgressDialog(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    public void startGoogleSignIn(View view) {
        ui.showProgressDialog();
        Intent googleSignInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(googleSignInIntent, RC_SIGN_IN_GOOGLE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == RC_SIGN_IN_GOOGLE) {
                Task<GoogleSignInAccount> googleSignInAccountTask =
                        GoogleSignIn.getSignedInAccountFromIntent(data);
                try {
                    GoogleSignInAccount googleSignInAccount = googleSignInAccountTask
                            .getResult(ApiException.class);
                    authWithGoogle(googleSignInAccount);
                } catch (ApiException e) {
                    ui.showToast(e.getMessage());
                    ui.hideProgressDialog();
                }
            }
        }
    }

    private void authWithGoogle(GoogleSignInAccount googleSignInAccount) {
        final AuthCredential authCredential = GoogleAuthProvider
                .getCredential(googleSignInAccount.getIdToken(), null);

        mFireBaseAuth.signInWithCredential(authCredential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        ui.hideProgressDialog();
                        if (task.isSuccessful()) {
                            ui.showToast(
                                    "Sign In Success");
                            startMainActivity();
                        } else {
                            ui.showToast(task.getException().getMessage());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        ui.hideProgressDialog();
                        ui.showToast(e.getMessage());
                    }
                });
    }
    private void startMainActivity(){
        Intent intent=new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
