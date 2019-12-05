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
@SuppressWarnings("ALL")
public class Fragment_Emergency_Contacts extends Fragment {


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

        return view;
    }

}
