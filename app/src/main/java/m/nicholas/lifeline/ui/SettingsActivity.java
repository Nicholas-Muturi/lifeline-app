package m.nicholas.lifeline.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import m.nicholas.lifeline.R;
import m.nicholas.lifeline.adapters.SettingsListAdapter;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    @BindView(R.id.settingsListView) ListView settingsList;
    private List<String> menu = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);

        setMenu();
        SettingsListAdapter adapter = new SettingsListAdapter(this,android.R.layout.simple_list_item_1,menu);
        settingsList.setAdapter(adapter);
        settingsList.setOnItemClickListener(this);
    }

    private void setMenu(){
        menu.add("Edit medical info");
        menu.add("Set emergency contacts");
        menu.add("Logout");
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String menuItem = ((TextView)view).getText().toString();

        if(menuItem.equals("Edit medical info")){
            Intent intent = new Intent(SettingsActivity.this, SettingsFragmentHolderActivity.class);
            intent.putExtra("fragment","medical");
            startActivity(intent);
        }
        if(menuItem.equals("Set emergency contacts")){
            Intent intent = new Intent(SettingsActivity.this, SettingsFragmentHolderActivity.class);
            intent.putExtra("fragment","contacts");
            startActivity(intent);
        }
        if(menuItem.equals("Logout")){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }
}
