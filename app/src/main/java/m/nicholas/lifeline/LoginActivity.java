package m.nicholas.lifeline;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.loginEmail) EditText txtLoginEmail;
    @BindView(R.id.loginPassword) EditText txtLoginPassword;
    @BindView(R.id.forgotPasswordLoginView) TextView txtForgotPassword;
    @BindView(R.id.imageLoginBtn) ImageView btnLogin;
    @BindView(R.id.signUpLoginView) TextView txtSignUp;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        firebaseAuth = FirebaseAuth.getInstance();

        btnLogin.setOnClickListener(this);
        txtForgotPassword.setOnClickListener(this);
        txtSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin){
            checkCredentials_then_SignIn();
        }
        if(view == txtSignUp){
            Intent signUp = new Intent(this,RegisterActivity.class);
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
            signInUser(email,password);
        }
    }

    private void signInUser(String email,String password){
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this,task->{
           if(task.isSuccessful()){
               Intent signUp = new Intent(this,MainActivity.class);
               signUp.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
               startActivity(signUp);
               finish();
            }
            else {
               Toast.makeText(this,"Login Failed. Check username or password",Toast.LENGTH_SHORT).show();
            }
        });
    }

}
