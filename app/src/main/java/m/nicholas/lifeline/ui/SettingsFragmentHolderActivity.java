package m.nicholas.lifeline.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.widget.FrameLayout;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import m.nicholas.lifeline.R;

public class SettingsFragmentHolderActivity extends AppCompatActivity {
    @BindView(R.id.settingsFrameLayout) FrameLayout fragment_container;
    private Fragment fragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_fragment_holder);
        ButterKnife.bind(this);

        String fragmentType = Objects.requireNonNull(getIntent().getExtras()).getString("fragment");

        assert fragmentType != null;
        if(fragmentType.equals("profile")){
            getProfileFragment();
        }
        if(fragmentType.equals("medical")){
            getMedicalFragment();
        }
        if(fragmentType.equals("contacts")){
            getEmergencyContactsFragment();
        }

    }

    private void getMedicalFragment(){
        fragment = Fragment_Medical_Info.newInstance();
        implementSelectedFragment(fragment);
    }

    private void getEmergencyContactsFragment(){
        fragment = Fragment_Emergency_Contacts.newInstance();
        implementSelectedFragment(fragment);
    }

    private void getProfileFragment(){
        fragment = Fragment_User_Profile.newInstance();
        implementSelectedFragment(fragment);
    }

    private void implementSelectedFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction().replace(fragment_container.getId(),fragment).commit();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SettingsFragmentHolderActivity.this, SettingsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        super.onBackPressed();
    }
}
