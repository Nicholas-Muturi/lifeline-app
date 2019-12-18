package m.nicholas.lifeline.ui;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.util.Log;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import m.nicholas.lifeline.Constants;
import m.nicholas.lifeline.R;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ALL")
public class Fragment_Message extends Fragment implements View.OnClickListener {

    @BindView(R.id.editMessage) EditText etMessage;
    @BindView(R.id.btnSaveMessage) Button btnMessage;
    private DatabaseReference messageRef;
    private ValueEventListener messageListener;

    private Fragment_Message() {
        // Required empty public constructor
    }

    public static Fragment_Message newInstance(){
        return new Fragment_Message();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this,view);

        String userId = FirebaseAuth.getInstance().getUid();
        messageRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_EMERGENCY_MESSAGE).child(userId);
        setCurrentMessage();
        btnMessage.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == btnMessage){
            saveCustomMessage();
        }
    }

    private void saveCustomMessage() {
        String customMessage = etMessage.getText().toString().trim();

        if(customMessage.isEmpty()){
            customMessage = Constants.DEFAULT_MESSAGE;
        }
        messageRef.setValue(customMessage);

        /*-- CLEAR FIELDS & RETURN TO SETTINGS --*/
        etMessage.setText("");
        Intent intent = new Intent(getContext(),SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(getContext(),"Custom message saved",Toast.LENGTH_SHORT).show();
        startActivity(intent);
        getActivity().finish();
    }

    private void setCurrentMessage(){
        messageRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String message = (String) dataSnapshot.getValue();
                etMessage.setText(message);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



}
