package m.nicholas.lifeline.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import m.nicholas.lifeline.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Fragment_Emergency_Contacts extends Fragment {

    public static Fragment_Emergency_Contacts newInstance(){
        return new Fragment_Emergency_Contacts();
    }

    private Fragment_Emergency_Contacts() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_emergency_contacts, container, false);
    }

}
