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
public class Fragment_Medical_Info extends Fragment {


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

        return view;
    }

}
