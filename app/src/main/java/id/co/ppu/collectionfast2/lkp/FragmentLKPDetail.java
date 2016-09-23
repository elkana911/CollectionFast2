package id.co.ppu.collectionfast2.lkp;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentLKPDetail extends Fragment {

    private OnLKPDetailListener mListener;

    public FragmentLKPDetail() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lkpdetail, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLKPDetailListener) {
            mListener = (OnLKPDetailListener) context;
        }

        else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    // Container Activity must implement this interface
    public interface OnLKPDetailListener {
        void onClickPaymentReceive();
        void onClickVisitResult();
    }

    @Nullable
    @OnClick(R.id.btnPaymentReceive)
    public void onClickPaymentReceive() {
        if (mListener == null) {
            return;
        }
        mListener.onClickPaymentReceive();
    }

    @Nullable
    @OnClick(R.id.btnVisitResultEntry)
    public void onClickVisitResultEntry() {
        if (mListener == null) {
            return;
        }
        mListener.onClickVisitResult();
    }
}
