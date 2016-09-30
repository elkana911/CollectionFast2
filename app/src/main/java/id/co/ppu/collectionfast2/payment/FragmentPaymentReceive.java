package id.co.ppu.collectionfast2.payment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import id.co.ppu.collectionfast2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPaymentReceive extends Fragment {


    public FragmentPaymentReceive() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment_receive, container, false);
    }

}
