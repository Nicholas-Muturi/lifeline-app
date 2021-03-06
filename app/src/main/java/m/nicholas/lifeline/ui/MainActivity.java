package m.nicholas.lifeline.ui;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.nicholas.lifeline.Constants;
import m.nicholas.lifeline.R;
import m.nicholas.lifeline.models.Contact;
import m.nicholas.lifeline.models.User;

@SuppressWarnings("ConstantConditions")
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.userProfilePhoto) ImageView profilePhoto;
    @BindView(R.id.userName) TextView tvUsername;
    @BindView(R.id.userAge) TextView tvUserAge;
    @BindView(R.id.userHeight) TextView tvUserHeight;
    @BindView(R.id.userWeight) TextView tvUserWeight;
    @BindView(R.id.userBloodType) TextView tvUserBloodType;
    @BindView(R.id.userAllergies) TextView tvUserAllergies;
    @BindView(R.id.userMedications) TextView tvUserMedications;
    @BindView(R.id.userNotes) TextView tvUserNotes;
    @BindView(R.id.btnSendEmergency) Button btnEmergency;
    @BindView(R.id.mainProgressBar) ProgressBar progressBar;
    @BindView(R.id.scrollViewMain) ScrollView scrollView;
    private FusedLocationProviderClient fusedLocationClient;
    private ValueEventListener infoEventListener;
    private DatabaseReference infoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        showProgressBar();
        hideDataFields();

        checkPermission();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        displayInformation();
        btnEmergency.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == btnEmergency)
            Get_and_Send_Message();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        infoRef.addValueEventListener(infoEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        infoRef.removeEventListener(infoEventListener);
    }

    private void displayInformation(){
        String userIdPath = FirebaseAuth.getInstance().getUid();
        assert userIdPath != null;
        infoRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_USER_INFO).child(userIdPath);
        infoEventListener = infoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                assert user != null;
                updateViews(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                hideProgressBar();
                Toast.makeText(MainActivity.this,"An error occurred",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateViews(User user){
        String none = "None";
        List<String> medications = user.getMedications();
        List<String> allergies = user.getAllergies();

        tvUsername.setText(user.getName());
        tvUserAge.setText(String.valueOf(user.getAge()));
        tvUserBloodType.setText(user.getBloodType());
        tvUserNotes.setText(user.getNotes());

        if(FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null){
            Uri imageUri = FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl();
            Picasso.get().load(imageUri).into(profilePhoto);
        }

        if(user.getHeight() == 0) tvUserHeight.setText(Constants.NOT_SPECIFIED);
        else tvUserHeight.setText(String.valueOf(user.getHeight()));

        if(user.getWeight() == 0) tvUserWeight.setText(Constants.NOT_SPECIFIED);
        else tvUserWeight.setText(String.valueOf(user.getWeight()));

        if(medications == null || medications.isEmpty()){
            tvUserMedications.setText(none);
        } else {
            StringBuilder sbMedication = new StringBuilder();
            String concatStr;
            for(int i = 0; i < medications.size()-1; i++){
                concatStr = "\u2022 "+medications.get(i)+ "\n";
                sbMedication.append(concatStr);
            }
            concatStr = "\u2022 "+medications.get(medications.size()-1);
            sbMedication.append(concatStr);
            tvUserMedications.setText(sbMedication.toString());
        }

        if(allergies == null || allergies.isEmpty()){
            tvUserAllergies.setText(none);
        } else {
            StringBuilder sbAllergies = new StringBuilder();
            String concatStr;
            for(int i = 0; i < allergies.size()-1; i++){
                concatStr = "\u2022 "+allergies.get(i)+ "\n";
                sbAllergies.append(concatStr);
            }
            concatStr = "\u2022 "+allergies.get(allergies.size()-1);
            sbAllergies.append(concatStr);
            tvUserAllergies.setText(sbAllergies.toString());
        }

        hideProgressBar();
        showDataFields();
    }

    private void Get_and_Send_Message() {
        /* -- get custom message from firebase first then send -- */
        DatabaseReference messageRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_EMERGENCY_MESSAGE).child(FirebaseAuth.getInstance().getUid());
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String emergencyMessage = (String) dataSnapshot.getValue();
                combineMessageWithUserLocation(emergencyMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void combineMessageWithUserLocation(String customMessage){

        // GET USER LOCATION IF PERMISSION IS GRANTED
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            fusedLocationClient.getLastLocation().addOnSuccessListener(this,location -> {
                   // Handle special cases where location is turned off hence null result
                   if(location != null){
                       String messageWithLocation = customMessage +
                               ".My location is: http://maps.google.com/maps?q=" +
                               location.getLatitude() +
                               "," +
                               location.getLongitude();
                       sendFinalMessageToContacts(messageWithLocation);
                   }
                   else {
                       sendFinalMessageToContacts(customMessage);
                   }
            });
        }
        else {
            sendFinalMessageToContacts(customMessage);
        }
    }

    private void sendFinalMessageToContacts(String message){
        SmsManager smsManager = SmsManager.getDefault();

        DatabaseReference contactRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_EMERGENCY_CONTACTS).child(FirebaseAuth.getInstance().getUid());
        contactRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 0){
                    Toast.makeText(MainActivity.this,"Emergency contacts not set. Go to settings to add a contact", Toast.LENGTH_SHORT).show();
                    return;
                }

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String mobileNumber = snapshot.getValue(Contact.class).getNumber();
                    smsManager.sendTextMessage(mobileNumber,null,message,null,null);
                }
                Log.i("mmmm", "onDataChange: "+message);
                Toast.makeText(MainActivity.this,"Emergency Sms Sent", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS}, Constants.SMS_PERMISSION_CODE);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},Constants.FINE_LOCATION_PERMISSION_CODE);
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},Constants.COARSE_LOCATION_PERMISSION_CODE);
        }
    }

    private void showProgressBar(){
        progressBar.setVisibility(View.VISIBLE);
    }
    private void hideProgressBar(){
        progressBar.setVisibility(View.GONE);
    }
    private void hideDataFields(){
        scrollView.setVisibility(View.GONE);
        btnEmergency.setVisibility(View.GONE);
    }
    private void showDataFields(){
        scrollView.setVisibility(View.VISIBLE);
        btnEmergency.setVisibility(View.VISIBLE);
    }
}
