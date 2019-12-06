package m.nicholas.lifeline.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.nicholas.lifeline.Constants;
import m.nicholas.lifeline.R;
import m.nicholas.lifeline.models.User;

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
    private DatabaseReference infoRef;
    private ValueEventListener infoEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        displayInformation();
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
                clearViews();
                updateViews(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateViews(User user){
        tvUsername.setText(user.getName());
        tvUserAge.setText(String.valueOf(user.getAge()));
        tvUserBloodType.setText(user.getBloodType());
        tvUserNotes.setText(user.getNotes());

        if(user.getHeight() == 0)
            tvUserHeight.setText(Constants.NOT_SPECIFIED);
        else
            tvUserHeight.setText(String.valueOf(user.getHeight()));

        if(user.getWeight() == 0)
            tvUserWeight.setText(Constants.NOT_SPECIFIED);
        else
            tvUserWeight.setText(String.valueOf(user.getWeight()));


        for(String str : user.getMedications()){
            tvUserMedications.append("\u2022 " +str+ " \n");
        }
        for(String str : user.getAllergies()){
            tvUserAllergies.append("\u2022 "+str+ " \n");
        }
    }

    public void clearViews(){
        tvUsername.setText("");
        tvUserAge.setText("");
        tvUserHeight.setText("");
        tvUserWeight.setText("");
        tvUserBloodType.setText("");
        tvUserNotes.setText("");
        tvUserMedications.setText("");
        tvUserAllergies.setText("");
    }

    @Override
    public void onClick(View view) {

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
}
