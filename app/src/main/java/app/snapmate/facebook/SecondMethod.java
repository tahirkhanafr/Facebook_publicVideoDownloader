package app.snapmate.facebook;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import app.snapmate.facebook.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class SecondMethod extends Fragment {

    public SecondMethod() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second_method, container, false);
    }
}
