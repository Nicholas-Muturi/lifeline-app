package m.nicholas.lifeline.ui;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.nicholas.lifeline.Constants;
import m.nicholas.lifeline.R;

import static android.app.Activity.RESULT_OK;
import static m.nicholas.lifeline.Constants.FIREBASE_STORAGE_PROFILE_IMAGE_PATH;
import static m.nicholas.lifeline.Constants.PICK_IMAGE_REQUEST;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_User_Profile extends Fragment implements View.OnClickListener {

    private static final String TAG = "frag";
    @BindView(R.id.profName) EditText etName;
    @BindView(R.id.profImageView) ImageView profImageView;
    @BindView(R.id.profImageSelector) Button btnImageSelector;
    @BindView(R.id.btnProfSaveDetails) Button btnSaveDetails;
    private StorageReference mStorageRef;
    private String currentDisplayName;
    private FirebaseAuth mAuth;
    private Uri mImageUri;

    public static Fragment_User_Profile newInstance(){
        return new Fragment_User_Profile();
    }

    private Fragment_User_Profile() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        ButterKnife.bind(this,view);

        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference(FIREBASE_STORAGE_PROFILE_IMAGE_PATH).child(mAuth.getUid());
        currentDisplayName = Objects.requireNonNull(mAuth.getCurrentUser()).getDisplayName();
        etName.setText(currentDisplayName);

        btnImageSelector.setOnClickListener(this);
        btnSaveDetails.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View view) {
        if(view == btnImageSelector){
            openFileChooser();
        }
        if(view == btnSaveDetails){
            updateUserBio();
        }
    }

    private void updateUserBio() {
        String name = etName.getText().toString().trim();

        if(!name.isEmpty() || !name.equals(currentDisplayName)){
            UserProfileChangeRequest displayNameRequest = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
            Objects.requireNonNull(mAuth.getCurrentUser()).updateProfile(displayNameRequest).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                    updateUsernameInDatabase();
                else
                    Toast.makeText(getContext(),"Name update failed",Toast.LENGTH_SHORT).show();
            });
        }

        if(mImageUri != null){
            StorageReference fileRef = mStorageRef.child("profile_photo.jpg");
            UploadTask uploadTask = fileRef.putFile(mImageUri);
            uploadTask.continueWithTask(task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                // Continue with the task to get the download URL
                return fileRef.getDownloadUrl();
            }).addOnCompleteListener(taskComplete -> {
                if (taskComplete.isSuccessful()) {
                    Uri downloadUri = taskComplete.getResult();

                    UserProfileChangeRequest displayImageRequest = new UserProfileChangeRequest.Builder().setPhotoUri(downloadUri).build();
                    Objects.requireNonNull(mAuth.getCurrentUser()).updateProfile(displayImageRequest).addOnCompleteListener(task -> {
                        if(!task.isSuccessful()) Toast.makeText(getContext(),"Profile photo update failed",Toast.LENGTH_SHORT).show();
                    });
                }
            });
        }

        Intent intent = new Intent(getContext(),SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        Toast.makeText(getContext(),"Update Successful",Toast.LENGTH_SHORT).show();
        startActivity(intent);
        getActivity().finish();

    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(profImageView);
        }
    }

    private void updateUsernameInDatabase(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference(Constants.FIREBASE_CHILD_USER_INFO).child(user.getUid());
        dbRef.child("name").setValue(user.getDisplayName());
    }

}
