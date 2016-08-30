package id.co.ppu.collectionfast2.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.moonmonkeylabs.realmrecyclerview.RealmRecyclerView;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.pojo.TrxLDVDetails;
import id.co.ppu.collectionfast2.pojo.TrxLDVHeader;
import io.realm.Realm;
import io.realm.RealmBasedRecyclerViewAdapter;
import io.realm.RealmResults;
import io.realm.RealmViewHolder;
import io.realm.Sort;

/**
 * https://developer.android.com/training/basics/fragments/communicating.html
 * <p/>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LKPListFragment.OnLKPSelectedListener} interface
 * to handle interaction events.
 */
public class LKPListFragment extends Fragment {

    private static final String ARG_PARAM1 = "collectorCode";


    private Realm realm;
    private String collectorCode;

    @BindView(R.id.realm_recycler_view)
    RealmRecyclerView realmRecyclerView;

    @BindView(R.id.tvNoLKP)
    TextView tvNoLKP;

    @BindView(R.id.tvTglLKP)
    TextView tvTglLKP;

    private LKPListAdapter mAdapter;

    private OnLKPSelectedListener mListener;

    public LKPListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();
        loadLKP(collectorCode);

    }

    @Override
    public void onStop() {
        super.onStop();
        this.realm.close();
        this.realm = null;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            collectorCode = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lkplist, container, false);

        ButterKnife.bind(this, view);

        realmRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    /*
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onLKPSelected();FragmentInteraction(uri);
        }
    }
    */

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLKPSelectedListener) {
            mListener = (OnLKPSelectedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void loadLKP(String collectorCode) {

        TrxLDVHeader header = this.realm.where(TrxLDVHeader.class).findFirst();

        tvNoLKP.setText(header.getLdvNo());
        tvTglLKP.setText(DateUtils.formatDateTime(getContext(), header.getLdvDate().getTime(), DateUtils.FORMAT_SHOW_DATE));

        RealmResults<TrxLDVDetails> all = this.realm.where(TrxLDVDetails.class).findAllSorted("ldvNo", Sort.ASCENDING);

        boolean automaticUpdate = true; // dont set to false
        boolean animateIdType = true;
        String animateExtraColumnName = "ldvNo";

        if (mAdapter == null) {
            mAdapter = new LKPListAdapter(
                    getContext(),
                    all,
                    automaticUpdate,
                    animateIdType,
                    animateExtraColumnName
            );
        }

        realmRecyclerView.setAdapter(mAdapter);
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
    public interface OnLKPSelectedListener {
        void onLKPSelected(int position);
    }

    public static class LKPListAdapter extends RealmBasedRecyclerViewAdapter<TrxLDVDetails, LKPListAdapter.DataViewHolder> {

        public LKPListAdapter(Context context,
                           RealmResults<TrxLDVDetails> realmResults,
                           boolean automaticUpdate,
                           boolean animateIdType,
                           String animateExtraColumnName) {
            super(context, realmResults, automaticUpdate, animateIdType, animateExtraColumnName);
        }

        @Override
        public DataViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.row_lkp, viewGroup, false);
            return new DataViewHolder((FrameLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(DataViewHolder dataViewHolder, int position) {
            final TrxLDVDetails user = realmResults.get(position);

            FrameLayout container = dataViewHolder.container;
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "You click " + user.getCustName(), Toast.LENGTH_SHORT).show();
                }
            });

            TextView v = dataViewHolder.tvNoLKP;
            v.setText("Contract No: " + user.getContractNo());

            TextView custName = dataViewHolder.tvCustName;
            custName.setText("Cust: " + user.getCustName());

            TextView tvKelKec = dataViewHolder.tvKelKec;
            tvKelKec.setText("Kel/Kec: " +user.getCustNo());
        }

        @Override
        public void onItemSwipedDismiss(int position) {
            /*
            final User user = realmResults.get(position);
            Toast.makeText(getContext(), "You swipe " + user.getFullName(), Toast.LENGTH_SHORT).show();
            super.onItemSwipedDismiss(position);    //harus dipanggil belakangan spy geser layoutnya
            */
        }

        public class DataViewHolder extends RealmViewHolder {

            public FrameLayout container;

            @BindView(R.id.tvCustName)
            TextView tvCustName;
            @BindView(R.id.tvNoLKP)
            TextView tvNoLKP;
            @BindView(R.id.tvKelKec)
            TextView tvKelKec;

            public DataViewHolder(FrameLayout container) {
                super(container);

                this.container = container;
                ButterKnife.bind(this, container);
            }

            //            @OnClick(R.)
            public void onClickItem() {
//                _listener.onClickedItem(position);
            }
        }
    }
}
