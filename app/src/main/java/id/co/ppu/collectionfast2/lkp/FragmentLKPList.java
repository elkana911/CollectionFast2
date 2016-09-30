package id.co.ppu.collectionfast2.lkp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import id.co.ppu.collectionfast2.MainActivity;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.DividerItemDecoration;
import id.co.ppu.collectionfast2.component.RealmSearchView;
import id.co.ppu.collectionfast2.listener.OnLKPListListener;
import id.co.ppu.collectionfast2.pojo.DisplayTrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.TrnLDVHeader;
import id.co.ppu.collectionfast2.sync.pojo.SyncTrnRVColl;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * https://developer.android.com/training/basics/fragments/communicating.html
 * <p/>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnLKPListListener} interface
 * to handle interaction events.
 */
public class FragmentLKPList extends Fragment {

    public static final String ARG_PARAM1 = "collector.code";


    private Realm realm;
    private String collectorCode;
    private Date serverDate;

    @BindView(R.id.tilTglLKP)
    View tilTglLKP;

    @BindView(R.id.search_view)
    RealmSearchView search_view;

    @BindView(R.id.cbLKPInquiry)
    CheckBox cbLKPInquiry;

    @BindView(R.id.etNoLKP)
    EditText etNoLKP;

    @BindView(R.id.tvSeparator)
    TextView tvSeparator;

    @BindView(R.id.etTglLKP)
    EditText etTglLKP;

    @BindView(R.id.fab)
    FloatingActionButton fab;

//    private LKPListAdapter mAdapter;

    private OnLKPListListener mListener;

    private DatePickerDialog.OnDateSetListener listenerDateTglLKP = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);
            etTglLKP.setText(Utility.convertDateToString(cal.getTime(), Utility.DATE_DISPLAY_PATTERN));

//            fab.show();
//            fab.setVisibility(View.VISIBLE);
            search_view.setVisibility(View.INVISIBLE);
            tvSeparator.setText("No Contracts found" );

            // load cache
            final String createdBy = "JOB" + Utility.convertDateToString(cal.getTime(), "yyyyMMdd");
            long count = realm.where(TrnLDVHeader.class).equalTo("collCode", collectorCode)
                    .equalTo("createdBy", createdBy)
                    .count();
            if (count > 0) {
                loadLKP(collectorCode, cal.getTime());
            }
        }
    };


    public FragmentLKPList() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();

        if (serverDate == null)
            serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();

        Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? serverDate : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);

        loadLKP(collectorCode, dateLKP);

    }

    @Override
    public void onStop() {
        super.onStop();

        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }
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

        cbLKPInquiry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
//                    tilTglLKP.setVisibility(View.VISIBLE);
                } else {
//                    tilTglLKP.setVisibility(View.GONE);
                    loadLKP(collectorCode, serverDate);
                }
            }
        });

        etTglLKP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!cbLKPInquiry.isChecked())
                    return;

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getContext(), listenerDateTglLKP, year, month, day).show();
            }
        });

        search_view.getSearchBar().setVisibility(View.GONE);
        search_view.getRealmRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    @OnClick(R.id.fab)
    public void onClickFab() {

        if (getContext() instanceof OnLKPListListener) {

            Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? serverDate : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);
            ((OnLKPListListener)getContext()).onLKPInquiry(this.collectorCode, dateLKP);
        }

    }

    @OnClick(R.id.cbLKPInquiry)
    public void onClickLkpInquiry() {

        if (cbLKPInquiry.isChecked()) {
            if (TextUtils.isEmpty(etTglLKP.getText().toString()))
                etTglLKP.performClick();

        } else {

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLKPListListener) {
            mListener = (OnLKPListListener) context;
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

    public void search(String query) {
//        Toast.makeText(getActivity(), "You search " + query, Toast.LENGTH_SHORT).show();

        search_view.getSearchBar().setText(query);
    }

    /**
     *
     * @param collectorCode sementara ignore, karena gadget hanya dipakai 1 orang/collector saja
     *                      @return ldvNo
     */
    public String loadLKP(final String collectorCode, Date dateLKP) {

        final String createdBy = "JOB" + Utility.convertDateToString(dateLKP, "yyyyMMdd");

        TrnLDVHeader header = this.realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collectorCode)
                .equalTo("createdBy", createdBy)
                .findFirst();

        if (header == null) {
            return null;
        }

        String ldvNo = header.getLdvNo();

        ((MainActivity)getActivity()).currentLDVNo = ldvNo;

        etNoLKP.setText(header.getLdvNo());
        etTglLKP.setText(Utility.convertDateToString(header.getLdvDate(), Utility.DATE_DISPLAY_PATTERN));
//        etTglLKP.setText(DateUtils.formatDateTime(getContext(), header.getLdvDate().getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));

        //populate
        final RealmResults<TrnLDVDetails> _buffer = this.realm.where(TrnLDVDetails.class)
                .equalTo("pk.ldvNo", header.getLdvNo())
                .equalTo("createdBy", createdBy)
                .findAll();

        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(DisplayTrnLDVDetails.class);
//                boolean d = realm.where(DisplayTrnLDVDetails.class).equalTo(Utility.COLUMN_CREATED_BY, createdBy).findAll().deleteAllFromRealm();

                for (TrnLDVDetails obj : _buffer) {

                    DisplayTrnLDVDetails displayTrnLDVDetails = realm.createObject(DisplayTrnLDVDetails.class);

                    displayTrnLDVDetails.setLkpDate(Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN));

                    displayTrnLDVDetails.setSeqNo(obj.getPk().getSeqNo());
                    displayTrnLDVDetails.setLdvNo(obj.getPk().getLdvNo());
                    displayTrnLDVDetails.setCollId(collectorCode);
                    displayTrnLDVDetails.setContractNo(obj.getContractNo());
                    displayTrnLDVDetails.setCustName(obj.getCustName());
                    displayTrnLDVDetails.setCustNo(obj.getCustNo());
                    displayTrnLDVDetails.setCreatedBy(obj.getCreatedBy());
                    displayTrnLDVDetails.setWorkStatus(obj.getWorkStatus());

                    displayTrnLDVDetails.setAddress(obj.getAddress());

                    realm.copyToRealm(displayTrnLDVDetails);
                }
            }
        });

        String dateLabel = "Today";

        if (!Utility.isSameDay(dateLKP, serverDate)) {
            dateLabel = "Previous";
        }

        long count = this.realm.where(DisplayTrnLDVDetails.class).count();

        tvSeparator.setText(dateLabel + " Contracts: " + count);

        if (count < 1) {
//            fab.show();
            search_view.setVisibility(View.INVISIBLE);
        } else {
            search_view.setVisibility(View.VISIBLE);
//            fab.hide();
        }

        LKPListAdapter mAdapter = new LKPListAdapter(
                getContext(),
                this.realm,
                "custName"
        );

        search_view.setAdapter(mAdapter);
        search_view.getRealmRecyclerView().invalidate();

        return ldvNo;
    }

    public void clearTodayList() {

//        if (!tvSeparator.getText().toString().startsWith("Today"))
//            return;

        search_view.setVisibility(View.INVISIBLE);
    }

    public String getCurrentLKP() {
        return etNoLKP.getText().toString().trim();
    }

    public class LKPListAdapter extends RealmSearchAdapter<DisplayTrnLDVDetails, LKPListAdapter.DataViewHolder> {

        public LKPListAdapter(@NonNull Context context, @NonNull Realm realm, @NonNull String filterKey) {
            super(context, realm, filterKey);
        }

        @Override
        public DataViewHolder onCreateRealmViewHolder(ViewGroup viewGroup, int i) {
            View v = inflater.inflate(R.layout.row_lkp_list, viewGroup, false);
            return new DataViewHolder((FrameLayout) v);
        }

        @Override
        public void onBindRealmViewHolder(DataViewHolder dataViewHolder, int position) {
            final DisplayTrnLDVDetails detail = realmResults.get(position);
/*
            FrameLayout container = dataViewHolder.container;
            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnLKPListListener) {
                        ((OnLKPListListener)getContext()).onLKPSelected(detail);
                    }
                }
            });
*/
            dataViewHolder.llRowLKP.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnLKPListListener) {
                        ((OnLKPListListener)getContext()).onLKPSelected(detail);
                    }

                }
            });

            TextView v = dataViewHolder.tvNoLKP;
            v.setText("Contract No: " + detail.getContractNo());

            dataViewHolder.llRowLKP.setBackgroundColor(Color.WHITE);    // must

            if (detail.getWorkStatus().equalsIgnoreCase("V")) {
                dataViewHolder.llRowLKP.setBackgroundColor(Color.YELLOW);
            } else if (detail.getWorkStatus().equalsIgnoreCase("W")) {
                dataViewHolder.llRowLKP.setBackgroundColor(Color.BLUE);
            }

            /*
            RealmResults<TrnSync> trnSync = realm.where(TrnSync.class)
                                            .equalTo("tblName", TrnLDVDetails.class.getSimpleName())
                                            .equalTo("key1", detail.getLdvNo())
                                            .equalTo("key2", detail.getContractNo())
                                            .equalTo("createdBy", detail.getCreatedBy())
                                            .isNotNull("syncedDate")
                                            .findAll();
                                            */
            RealmResults<SyncTrnRVColl> trnSync = realm.where(SyncTrnRVColl.class)
                    .equalTo("ldvNo", detail.getLdvNo())
                    .equalTo("contractNo", detail.getContractNo())
                    .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                    .isNotNull("syncedDate")
                    .findAll();

            if (trnSync.size() > 0) {
                dataViewHolder.llRowLKP.setBackgroundColor(Color.GREEN);
            }

            TextView custName = dataViewHolder.tvCustName;
            if (Build.VERSION.SDK_INT >= 24) {
                custName.setText(Html.fromHtml("<strong>" + detail.getCustName() + "</strong>", Html.FROM_HTML_MODE_LEGACY));
            } else {
                custName.setText(Html.fromHtml("<strong>" + detail.getCustName() + "</strong>"));
                }
            //  "Cust: " + detail.getCustName()

            TextView tvKelKec = dataViewHolder.tvKelKec;
            if (detail.getAddress() != null) {
                tvKelKec.setText("Kel/Kec: " +detail.getAddress().getCollKel() + "/" + detail.getAddress().getCollKec());
            }
        }

        public class DataViewHolder extends RealmSearchViewHolder {

            public FrameLayout container;

            @BindView(R.id.llRowLKP)
            LinearLayout llRowLKP;

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
