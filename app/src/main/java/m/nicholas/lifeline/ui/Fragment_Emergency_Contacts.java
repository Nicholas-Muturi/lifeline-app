package m.nicholas.lifeline.ui;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import m.nicholas.lifeline.adapters.firebaseContactViewHolder;
import m.nicholas.lifeline.models.Contact;
import m.nicholas.lifeline.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
@SuppressWarnings("ALL")
public class Fragment_Emergency_Contacts extends Fragment implements View.OnClickListener {
    @BindView(R.id.contactName) EditText etContactName;
    @BindView(R.id.contactNumber) EditText etContactNumber;
    @BindView(R.id.btnSaveContact) Button btnSaveContact;
    @BindView(R.id.contactsRecyclerView) RecyclerView contactRecyclerView;
    private FirebaseRecyclerAdapter<Contact, firebaseContactViewHolder> mFirebaseRecyclerAdapter;
    private DatabaseReference contactRef;
    private ValueEventListener contactListener;
    private User mUser;


    private Fragment_Emergency_Contacts() {
        // Required empty public constructor
    }

    public static Fragment_Emergency_Contacts newInstance(){
        return new Fragment_Emergency_Contacts();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_emergency_contacts, container, false);
        ButterKnife.bind(this,view);

        String uid = FirebaseAuth.getInstance().getUid();
        contactRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_EMERGENCY_CONTACTS).child(uid);
        checkMaxLimitValidation();
        setUpFirebaseAdapter();
        btnSaveContact.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        validate_and_upload_contact();
    }

    private void validate_and_upload_contact() {
        String name = etContactName.getText().toString().trim();
        String number = etContactNumber.getText().toString().trim();

        if(name.isEmpty() || name.equalsIgnoreCase("null") || name.equals(null)){
            etContactName.setError("Contact should a name");
            return;
        }
        if(number.isEmpty() || number.equalsIgnoreCase("null") || number.equals(null)){
            etContactNumber.setError("Contact should have a number");
            return;
        }

        Contact newContact = new Contact(name,number);
        contactRef.push().setValue(newContact).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                clearText();
                Toast.makeText(getContext(),"Contact saved",Toast.LENGTH_SHORT).show();
                checkMaxLimitValidation();
            }
            else {
                Toast.makeText(getContext(),"Failed to save contact",Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setUpFirebaseAdapter() {
        FirebaseRecyclerOptions<Contact> options = new FirebaseRecyclerOptions.Builder<Contact>()
                .setQuery(contactRef,Contact.class).build();

        mFirebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Contact, firebaseContactViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull firebaseContactViewHolder firebaseContactViewHolder, int i, @NonNull Contact contact) {
                firebaseContactViewHolder.bindItems(contact);
            }

            @NonNull
            @Override
            public firebaseContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_contact_item,parent,false);
                return new firebaseContactViewHolder(view);
            }
        };
        DividerItemDecoration itemDecor = new DividerItemDecoration(contactRecyclerView.getContext(),DividerItemDecoration.VERTICAL);
        contactRecyclerView.setAdapter(mFirebaseRecyclerAdapter);
        contactRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        contactRecyclerView.addItemDecoration(itemDecor);
    }

    public void checkMaxLimitValidation(){
        contactListener = new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getChildrenCount() == 3) {
                    btnSaveContact.setEnabled(false);
                    Toast.makeText(getContext(),"Max contact limit reached",Toast.LENGTH_SHORT).show();
                }
                else
                    btnSaveContact.setEnabled(true);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseRecyclerAdapter.startListening();
        contactRef.addValueEventListener(contactListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseRecyclerAdapter.stopListening();
        contactRef.removeEventListener(contactListener);
    }

    private void clearText() {
        etContactName.setText("");
        etContactNumber.setText("");
    }
}
