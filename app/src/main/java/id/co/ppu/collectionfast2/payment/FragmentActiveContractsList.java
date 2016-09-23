package id.co.ppu.collectionfast2.payment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.DividerItemDecoration;
import id.co.ppu.collectionfast2.pojo.TrnContractBuckets;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Masalahnya tidak dpt menggunakan RealmRecyclerView karena Realm tidak mendukung pencarian via child property
 */

/**
 * Please apply http://stackoverflow.com/questions/30398247/how-to-filter-a-recyclerview-with-a-searchview
 * later
 */

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentActiveContractsList.OnActiveContractSelectedListener} interface
 * to handle interaction events.
 */
public class FragmentActiveContractsList extends DialogFragment {

    public static final String ARG_PARAM1 = "collector.code";

    private OnActiveContractSelectedListener mListener;

    private String collectorCode;

    private Realm realm;
    private ActiveContractsListAdapter mAdapter;

    @BindView(R.id.recycler_view)
    RecyclerView recycler_view;

    public FragmentActiveContractsList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.collectorCode = getArguments().getString(ARG_PARAM1);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setTitle("Pick Contract");
        return dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();
        loadList(this.collectorCode);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }
    }

    private void loadList(String collectorCode) {

        // since realm cant sort
        RealmResults<TrnContractBuckets> buckets = this.realm.where(TrnContractBuckets.class).equalTo("collectorId", collectorCode).findAll();

        List<ActiveContract> list = new ArrayList<>();
        for (TrnContractBuckets b : buckets) {
            ActiveContract obj = new ActiveContract();
            obj.setContractNo(b.getPk().getContractNo());
            obj.setCustName(b.getCustName());
            list.add(obj);
        }

        // sort
        Collections.sort(list, new Comparator<ActiveContract>() {
            @Override
            public int compare(ActiveContract s1, ActiveContract s2) {
                return s1.getContractNo().compareToIgnoreCase(s2.getContractNo());
            }
        });


        if (mAdapter == null) {
            mAdapter = new ActiveContractsListAdapter(
                    getContext(),
                    list
            );
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());

        recycler_view.setLayoutManager(mLayoutManager);
        recycler_view.setAdapter(mAdapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_active_contracts_list, container, false);

        ButterKnife.bind(this, view);

        recycler_view.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnActiveContractSelectedListener) {
            mListener = (OnActiveContractSelectedListener) context;
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
    public interface OnActiveContractSelectedListener {
        void onContractSelected(String contractNo);
    }


    //    http://www.coderzheaven.com/2016/05/13/filtering-a-recyclerview-with-custom-objects/
    public class ActiveContractsListAdapter extends RecyclerView.Adapter<ActiveContractsListAdapter.DataViewHolder> {
        private List<ActiveContract> listItems, filterList;
        private Context mContext;

        public ActiveContractsListAdapter(Context context, List<ActiveContract> listItems) {
            this.listItems = listItems;
            this.mContext = context;
            this.filterList = new ArrayList<ActiveContract>();
            // we copy the original list to the filter list and use it for setting row values
            this.filterList.addAll(this.listItems);
        }
        @Override
        public DataViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.row_active_contract_list, null);
            DataViewHolder viewHolder = new DataViewHolder((FrameLayout)view);
            return viewHolder;

        }

        @Override
        public void onBindViewHolder(DataViewHolder customViewHolder, int position) {

            final ActiveContract listItem = filterList.get(position);
            customViewHolder.tvCustName.setText(listItem.getCustName());
            customViewHolder.tvNoContract.setText(listItem.getContractNo());

            customViewHolder.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnActiveContractSelectedListener) {
                        ((OnActiveContractSelectedListener) getContext()).onContractSelected(listItem.getContractNo());

                        getDialog().dismiss();
                    }
                }
            });
            /*
            customViewHolder.tvNoContract.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnActiveContractSelectedListener) {
                        ((OnActiveContractSelectedListener) getContext()).onContractSelected(listItem.getContractNo());

                        getDialog().dismiss();
                    }

                }
            });
             */
        }

        @Override
        public int getItemCount() {
            return (null != filterList ? filterList.size() : 0);
        }

        /*
        @Override
        public void onBindRealmViewHolder(DataViewHolder dataViewHolder, int position) {
            final TrnContractBuckets detail = realmResults.get(position);

            FrameLayout container = dataViewHolder.container;
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    if (getContext() instanceof OnLKPListListener) {
//                        ((OnLKPListListener) getContext()).onLKPSelected(detail);
//                    }
                }
            });

            TextView v = dataViewHolder.tvNoContract;
            v.setText(detail.getPk().getContractNo());

        }*/

        public class DataViewHolder extends RecyclerView.ViewHolder {

            public FrameLayout container;

            @BindView(R.id.tvNoContract)
            TextView tvNoContract;

            @BindView(R.id.tvCustName)
            TextView tvCustName;

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
