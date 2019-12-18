package m.nicholas.lifeline.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.nicholas.lifeline.Constants;
import m.nicholas.lifeline.R;
import m.nicholas.lifeline.models.User;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = RegisterActivity.class.getName() ;
    @BindView(R.id.signUpName) EditText txtRegisterName;
    @BindView(R.id.signUpEmail) EditText txtRegisterEmail;
    @BindView(R.id.signUpDOB) EditText txtRegisterDOB;
    @BindView(R.id.signUpPass) EditText txtRegisterPass;
    @BindView(R.id.signUpConfirmPass) EditText txtRegisterConfirmPass;
    @BindView(R.id.imageSignUpBtn) ImageView btnSignUp;
    @BindView(R.id.returnToLoginView) TextView tvBackToLogin;
    @BindView(R.id.signUpProgressBar) ProgressBar progressBar;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        btnSignUp.setOnClickListener(this);
        tvBackToLogin.setOnClickListener(this);
        createAuthListener();
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

    @Override
    public void onClick(View view) {
        if(view == btnSignUp){
            validateInput_then_SignUp();
        }
        if(view == tvBackToLogin){
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
    }

    private void validateInput_then_SignUp() {
        // TODO: 05-Dec-19 Move D.O.B string to user information page
        String name = txtRegisterName.getText().toString().trim();
        String email = txtRegisterEmail.getText().toString().trim();
        String dob = txtRegisterDOB.getText().toString().trim();
        String password = txtRegisterPass.getText().toString().trim();
        String confirmPass = txtRegisterConfirmPass.getText().toString().trim();
        Calendar today = Calendar.getInstance();
        List<String> dobList = new ArrayList<>();

        if(!dob.isEmpty()) dobList = Arrays.asList(dob.split("-"));

        if(name.isEmpty()){
            txtRegisterName.setError("Please fill in this field");
        }
        else if(email.isEmpty()){
            txtRegisterEmail.setError("Please fill in this field");
        }
        else if(dob.isEmpty()){
            txtRegisterDOB.setError("Please fill in this field");
        }
        else if(dobList.size() < 3){
            txtRegisterDOB.setError("Please use the format DD-MM-YYY");
        }
        else if(Integer.parseInt(dobList.get(2)) > today.get(Calendar.YEAR)){
            txtRegisterDOB.setError("Year is invalid");
        }
        else if(Integer.parseInt(dobList.get(1)) > 12){
            txtRegisterDOB.setError("Month is invalid");
        }
        else if(Integer.parseInt(dobList.get(0)) > 31){
            txtRegisterDOB.setError("Day is invalid");
        }
        else if(password.isEmpty()){
            txtRegisterPass.setError("Please fill in this field");
        }
        else if(confirmPass.isEmpty()){
            txtRegisterConfirmPass.setError("Please fill in this field");
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            txtRegisterEmail.setError("Invalid password format");
        }
        else if(password.length() < 6){
            txtRegisterPass.setError("Too short! Minimum length is 6 characters");
        }
        else if(!password.equals(confirmPass)){
            txtRegisterConfirmPass.setError("Password do not match");
        }
        else{
            /* -- Everything is valid, call sign up function-- */
            showProgressBar_hideButton();
            User mUser = new User(name, dob);

            signUpUser(mUser, email, password);
        }
    }

    private void signUpUser(User user, String email, String password){
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,task -> {
           if(task.isSuccessful()){
               clearFields();
               hideProgressBar_showButton();

               /* -- SET DEFAULT EMERGENCY MESSAGE -- */
               DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_EMERGENCY_MESSAGE).child(FirebaseAuth.getInstance().getUid());
               messageRef.setValue(Constants.DEFAULT_MESSAGE);

               /* -- UPDATE DISPLAY NAME && UPLOAD INFO -- */
               setFirebaseDisplayName(user.getName());
               uploadUserInfo(user);

               Intent intent = new Intent(this,LoginActivity.class);
               intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
               Toast.makeText(this,"Sign up successful",Toast.LENGTH_SHORT).show();
               startActivity(intent);
               finish();
           }
           else {
               hideProgressBar_showButton();
               Toast.makeText(this,"Sign up failed. Try again later",Toast.LENGTH_SHORT).show();
           }
        });
    }

    private void setFirebaseDisplayName(String name){
        UserProfileChangeRequest displayNameRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
        Objects.requireNonNull(mAuth.getCurrentUser()).updateProfile(displayNameRequest).addOnCompleteListener(this, task -> {
            if(task.isSuccessful()){
                Log.i(TAG, "setFirebaseDisplayName: Username set");
            }
        });
    }

    private void uploadUserInfo(User user){
        String childPath = FirebaseAuth.getInstance().getUid();
        assert childPath != null;
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_USER_INFO).child(childPath);
        ref.setValue(user);
    }

    private void showProgressBar_hideButton(){
        progressBar.setVisibility(View.VISIBLE);
        btnSignUp.setVisibility(View.GONE);
    }

    private void hideProgressBar_showButton(){
        progressBar.setVisibility(View.GONE);
        btnSignUp.setVisibility(View.VISIBLE);
    }

    private void clearFields(){
        txtRegisterName.setText("");
        txtRegisterEmail.setText("");
        txtRegisterDOB.setText("");
        txtRegisterPass.setText("");
        txtRegisterConfirmPass.setText("");
    }

}
