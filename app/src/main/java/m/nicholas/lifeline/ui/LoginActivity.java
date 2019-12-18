package m.nicholas.lifeline.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.nicholas.lifeline.R;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.loginEmail) EditText txtLoginEmail;
    @BindView(R.id.loginPassword) EditText txtLoginPassword;
    @BindView(R.id.forgotPasswordLoginView) TextView txtForgotPassword;
    @BindView(R.id.imageLoginBtn) ImageView btnLogin;
    @BindView(R.id.signUpLoginView) TextView txtSignUp;
    @BindView(R.id.loginProgressBar) ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
        createAuthListener();
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin){
            checkCredentials_then_SignIn();
        }
        if(view == txtSignUp){
            Intent signUp = new Intent(this, RegisterActivity.class);
            signUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(signUp);
        }
        if(view == txtForgotPassword){
            Toast.makeText(this,"Feature will be added later",Toast.LENGTH_SHORT).show();
        }
    }

    private void checkCredentials_then_SignIn(){
        String email = txtLoginEmail.getText().toString().trim();
        String password = txtLoginPassword.getText().toString().trim();

        if(email.isEmpty())
            txtLoginEmail.setError("Please fill in this field");
        else if(password.isEmpty())
            txtLoginPassword.setError("Please fill in this field");
        else {
            showProgressBar_hideButton();
            signInUser(email,password);
        }
    }

    private void signInUser(String email,String password){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this,task->{
           if(task.isSuccessful()){
               hideProgressBar_showButton();
               clearFields();
               Intent signUp = new Intent(this, MainActivity.class);
               signUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(signUp);
               finish();
            }
            else {
               Toast.makeText(this,"Login Failed. Check username or password",Toast.LENGTH_SHORT).show();
               hideProgressBar_showButton();
            }
        });
    }

    private void createAuthListener(){
        authListener = firebaseAuth ->{
            final FirebaseUser user = firebaseAuth.getCurrentUser();
            if(user != null){
                Intent intent = new Intent(this,MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authListener != null)
            mAuth.removeAuthStateListener(authListener);
    }



    private void showProgressBar_hideButton(){
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setVisibility(View.GONE);
    }

    private void hideProgressBar_showButton(){
        progressBar.setVisibility(View.GONE);
        btnLogin.setVisibility(View.VISIBLE);
    }

    private void clearFields(){
        txtLoginEmail.setText("");
        txtLoginPassword.setText("");
    }


}
