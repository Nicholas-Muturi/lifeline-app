package m.nicholas.lifeline.ui;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.nicholas.lifeline.Constants;
import m.nicholas.lifeline.R;
import m.nicholas.lifeline.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ALL")
public class Fragment_Medical_Info extends Fragment implements View.OnClickListener {
    @BindView(R.id.editHeight) EditText etHeight;
    @BindView(R.id.editWeight) EditText etWeight;
    @BindView(R.id.editBloodType) EditText etBloodType;
    @BindView(R.id.editAllergies) EditText etAllergies;
    @BindView(R.id.editMedications) EditText etMedication;
    @BindView(R.id.editNotes) EditText etNotes;
    @BindView(R.id.btnSaveDetails) Button btnSaveDetails;
    private DatabaseReference infoRef;
    private User mUser;

    private Fragment_Medical_Info() {
        // Required empty public constructor
    }

    public static Fragment_Medical_Info newInstance(){
        return new Fragment_Medical_Info();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_edit_medical_info, container, false);
        ButterKnife.bind(this,view);

        String userId = FirebaseAuth.getInstance().getUid();
        infoRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_USER_INFO).child(userId);
        infoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUser = dataSnapshot.getValue(User.class);
                setEditTextValues(mUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnSaveDetails.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == btnSaveDetails){
            validateInput_and_updateInfo();
        }
    }

    public void validateInput_and_updateInfo(){
        String heightStr = etHeight.getText().toString().trim();
        String weightStr = etWeight.getText().toString().trim();
        String bloodType = etBloodType.getText().toString().trim().toUpperCase();
        String medicationStr = etMedication.getText().toString().trim();
        String allergiesStr = etAllergies.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        List<String> medicationList = Arrays.asList(medicationStr.split(","));
        List<String> allergyList = Arrays.asList(allergiesStr.split(","));

        if(heightStr.isEmpty() || heightStr.equals(String.valueOf(mUser.getHeight())))
            heightStr = String.valueOf(mUser.getHeight());

        if(weightStr.isEmpty() || weightStr.equals(String.valueOf(mUser.getWeight())))
            weightStr = String.valueOf(mUser.getWeight());

        if(medicationStr.isEmpty() || medicationStr.equalsIgnoreCase("None") || medicationStr.equalsIgnoreCase("Nothing"))
            medicationList = null;

        if(allergiesStr.isEmpty() || allergiesStr.equalsIgnoreCase("None") || allergiesStr.equalsIgnoreCase("Nothing"))
            allergyList = null;

        if(notes.isEmpty() || notes.equalsIgnoreCase("Nothing"))
            notes = "None";

        if(bloodType.isEmpty() || bloodType.equalsIgnoreCase(Constants.NOT_SPECIFIED))
            etBloodType.setError("Please specify a bloodtype");
        else {
            mUser.setHeight(Integer.parseInt(heightStr));
            mUser.setWeight(Integer.parseInt(weightStr));
            mUser.setMedications(medicationList);
            mUser.setAllergies(allergyList);
            mUser.setBloodType(bloodType);

            String userId = FirebaseAuth.getInstance().getUid();
            infoRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_USER_INFO).child(userId);
            infoRef.setValue(mUser).addOnCompleteListener(task -> {
                if (task.isSuccessful()){
                    clearText();
                    Intent intent = new Intent(getContext(),SettingsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    Toast.makeText(getContext(),"Update Successful",Toast.LENGTH_SHORT).show();
                    startActivity(intent);
                    getActivity().finish();
                }
                else {
                    Toast.makeText(getContext(),"Update Failed",Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public void setEditTextValues(User user){
        List<String> medications = user.getMedications();
        List<String> allergies = user.getAllergies();
        String none = "None";

        etHeight.setText(String.valueOf(user.getHeight()));
        etWeight.setText(String.valueOf(user.getWeight()));
        etBloodType.setText(user.getBloodType());
        etNotes.setText(user.getNotes());

        if(medications == null || medications.isEmpty()){
            etMedication.setText(none);
        } else {
            StringBuilder sbMedication = new StringBuilder();
            for(int i = 0; i < medications.size()-1; i++){
                sbMedication.append(medications.get(i)+ ", ");
            }
            sbMedication.append(medications.get(medications.size()-1));
            etMedication.setText(sbMedication.toString());
        }

        if(allergies == null || allergies.isEmpty()){
            etAllergies.setText(none);
        } else {
            StringBuilder sbAllergies = new StringBuilder();
            for(int i = 0; i < allergies.size()-1; i++){
                sbAllergies.append(allergies.get(i)+ ", ");
            }
            sbAllergies.append(allergies.get(allergies.size()-1));
            etAllergies.setText(sbAllergies.toString());
        }
    }

    private void clearText(){
        etHeight.setText("");
        etWeight.setText("");
        etBloodType.setText("");
        etAllergies.setText("");
        etMedication.setText("");
        etNotes.setText("");
    }

}
