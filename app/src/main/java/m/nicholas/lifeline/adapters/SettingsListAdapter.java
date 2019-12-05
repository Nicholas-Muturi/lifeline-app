package m.nicholas.lifeline.adapters;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class SettingsListAdapter extends ArrayAdapter {

    public SettingsListAdapter(@NonNull Context context, int resource, @NonNull List objects) {
        super(context, resource, objects);
    }

}
