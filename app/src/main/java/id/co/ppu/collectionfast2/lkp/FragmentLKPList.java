package id.co.ppu.collectionfast2.lkp;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import co.moonmonkeylabs.realmsearchview.RealmSearchAdapter;
import co.moonmonkeylabs.realmsearchview.RealmSearchViewHolder;
import id.co.ppu.collectionfast2.MainActivity;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.DividerItemDecoration;
import id.co.ppu.collectionfast2.component.RealmSearchView;
import id.co.ppu.collectionfast2.listener.OnPostRetrieveServerInfo;
import id.co.ppu.collectionfast2.pojo.DisplayTrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * https://developer.android.com/training/basics/fragments/communicating.html
 * <p/>
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFragmentLKPListInteractionListener} interface
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

//    private LKPListAdapter mAdapter;

    private OnFragmentLKPListInteractionListener mListener;

    private DatePickerDialog.OnDateSetListener listenerDateTglLKP = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
            Calendar cal = Calendar.getInstance();
            cal.set(year, month, day);

            long deltaDays = Utility.getDateDiff(Utility.getDateWithoutTime(new Date()), Utility.getDateWithoutTime(cal.getTime()), TimeUnit.DAYS);
            if (deltaDays < -1) {
                Utility.showDialog(getContext(), "Invalid Date", "Please pick yesterday date.");
                return;
            }

            etTglLKP.setText(Utility.convertDateToString(cal.getTime(), Utility.DATE_DISPLAY_PATTERN));

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
    private LKPListAdapter mAdapter;


    public FragmentLKPList() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();

        loadCurrentLKP();

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

        /*
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
        */

        /*
        etTglLKP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!cbLKPInquiry.isChecked()) {
                    return;
                }

                final Calendar c = Calendar.getInstance();
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                new DatePickerDialog(getContext(), listenerDateTglLKP, year, month, day).show();
            }
        });*/

        search_view.getSearchBar().setVisibility(View.GONE);
        search_view.getRealmRecyclerView().addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));

        return view;
    }

    public void performClickSync() {
        Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString())
                ? new Date()
                : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);

        // fix cbLKPInquiry
        if (Utility.isSameDay(dateLKP, new Date())) {
            cbLKPInquiry.setChecked(false);
        }

        retrieveLKP((OnFragmentLKPListInteractionListener) getActivity(), dateLKP);

    }

    public void retrieveLKP(final OnFragmentLKPListInteractionListener implementor, final Date lkpDate) {

        if (implementor == null) {
            Toast.makeText(getContext(), "No implementor for FragmentLKPList", Toast.LENGTH_SHORT).show();
            return;
        }

        TrnLDVHeader invalidHeader = DataUtil.isLDVHeaderValid(realm, collectorCode);
        if (invalidHeader != null) {
            // tampilkan lkp di lokal
            etTglLKP.setText(Utility.convertDateToString(invalidHeader.getCreatedTimestamp(), Utility.DATE_DISPLAY_PATTERN));
            loadLKP(collectorCode, invalidHeader.getCreatedTimestamp());
            ((MainActivity)getActivity()).showSnackBar(getString(R.string.warning_close_batch));
            return;
        }

        // be careful, kalo retrieve lkpinquiry ya header bisa > 1
        // tembok utama, cant retrieve lkp if header > 1
        /*
        long headerCount = realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collectorCode).count();

        if (headerCount > 1) {
            Utility.createAndShowProgressDialog(getContext(), "Invalid header", "Cant retrieve LKP.\nMultiple LKP found !");
            return;
        }
        */

        final ProgressDialog mProgressDialog = Utility.createAndShowProgressDialog(getContext(), getString(R.string.message_please_wait));

        try {
            DataUtil.retrieveServerInfo(collectorCode, this.realm, getContext(), new OnPostRetrieveServerInfo() {
                @Override
                public void onSuccess(ServerInfo serverInfo) {

                    Utility.dismissDialog(mProgressDialog);

                    // tembok pertama, jika tgl server berubah, must sync !
                    if (!Utility.isSameDay(new Date(), serverInfo.getServerDate())) {
                        if (implementor.isAnyDataToSync())
                            ((MainActivity)getActivity()).syncTransaction(true, null);
                    }

                    if (Utility.isSameDay(lkpDate, serverInfo.getServerDate()) && implementor.isAnyDataToSync()) {
                        ((MainActivity)getActivity()).syncTransaction(true, null);
                    } else {
                        implementor.onLKPInquiry(collectorCode, lkpDate);
                    }
                }

                @Override
                public void onFailure(Throwable throwable) {
                    Utility.dismissDialog(mProgressDialog);

                    Toast.makeText(getContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Deprecated
    public void retrieveLKPOld(OnFragmentLKPListInteractionListener listener, Date serverDate) {
        UserConfig userConfig = this.realm.where(UserConfig.class).findFirst();

        if (userConfig == null) {
            Utility.showDialog(getContext(), "Cannot Get LKP", "Unable to get initial data.\nPlease logout and login again.");
            return;
        }

        if (!Utility.isSameDay(userConfig.getLastLogin(), serverDate)) {
            Utility.showDialog(getContext(), "Close Batch", "Please do Close Batch first");
            return;
        }

        Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? serverDate : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);
        final String createdBy = "JOB" + Utility.convertDateToString(dateLKP, "yyyyMMdd");

        TrnLDVHeader header = this.realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collectorCode)
                .equalTo("createdBy", createdBy)
                .findFirst();

        if (header != null) {
            ((MainActivity)getActivity()).syncTransaction(true, null);
//            ((MainActivity)getActivity()).syncTransaction(false, true, null);
            return;
        }

        if (listener != null)
            listener.onLKPInquiry(this.collectorCode, dateLKP);

    }

    @OnClick(R.id.cbLKPInquiry)
    public void onClickLkpInquiry() {

        if (cbLKPInquiry.isChecked()) {


//            if (TextUtils.isEmpty(etTglLKP.getText().toString()))
//                etTglLKP.performClick();
//            else
                // pilih tanggal kemarin
                etTglLKP.setText(Utility.convertDateToString(Utility.getYesterday(new Date()), Utility.DATE_DISPLAY_PATTERN));

        } else {
            // reset ke hari ini
            etTglLKP.setText(Utility.convertDateToString(new Date(), Utility.DATE_DISPLAY_PATTERN));
        }

        loadCurrentLKP();
    }

    @OnClick(R.id.etNoLKP)
    public void NoLKP() {
        Intent i = new Intent(getActivity(), ActivityDetailsLKPSummary.class);
        i.putExtra(ActivityDetailsLKPSummary.PARAM_COLLECTOR_ID, this.collectorCode);

        startActivity(i);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentLKPListInteractionListener) {
            mListener = (OnFragmentLKPListInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentLKPListInteractionListener");
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

    public String loadCurrentLKP() {
        try {
            if (serverDate == null)
                serverDate = this.realm.where(ServerInfo.class)
                        .findFirst()
                        .getServerDate();

            Date dateLKP = TextUtils.isEmpty(etTglLKP.getText().toString()) ? serverDate : Utility.convertStringToDate(etTglLKP.getText().toString(), Utility.DATE_DISPLAY_PATTERN);

            return loadLKP(collectorCode, dateLKP);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     *
     * @param collectorCode sementara ignore, karena gadget hanya dipakai 1 orang/collector saja
     *                      @return ldvNo
     */
    public String loadLKP(final String collectorCode, final Date dateLKP) {

        search_view.setVisibility(View.INVISIBLE);

        search_view.setAdapter(null);

        tvSeparator.setText(R.string.msg_no_contracts );

        final String createdBy = "JOB" + Utility.convertDateToString(dateLKP, "yyyyMMdd");

        final TrnLDVHeader header = this.realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collectorCode)
                .equalTo("createdBy", createdBy)
                .findFirst();

        if (header == null) {
            return null;
        }

        final String ldvNo = header.getLdvNo();

        ((MainActivity)getActivity()).currentLDVNo = ldvNo;

        etNoLKP.setText(header.getLdvNo());
        etTglLKP.setText(Utility.convertDateToString(header.getLdvDate(), Utility.DATE_DISPLAY_PATTERN));

        this.realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                realm.delete(DisplayTrnLDVDetails.class);

                //populate
                final RealmResults<TrnLDVDetails> _buffer = realm.where(TrnLDVDetails.class)
                        .equalTo("pk.ldvNo", ldvNo)
                        .equalTo("createdBy", createdBy)
                        .findAll();

                //ga perlu sort karena dr server udah sort by seqNo

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
                    displayTrnLDVDetails.setFlagDone(obj.getFlagDone());
                    displayTrnLDVDetails.setAddress(obj.getAddress());

                    if (DataUtil.isLKPSynced(realm, obj) > 0) {
                        displayTrnLDVDetails.setWorkStatus("SYNC");
                    }else{
                        displayTrnLDVDetails.setWorkStatus(obj.getWorkStatus());
                    }


                    realm.copyToRealm(displayTrnLDVDetails);
                }
            }
        });

        long count = realm.where(DisplayTrnLDVDetails.class).count();

        try {
            if (!Utility.isSameDay(dateLKP, serverDate)) {
                tvSeparator.setText(getString(R.string.label_prev_contract) + count);
            } else {
                tvSeparator.setText(getString(R.string.label_today_contract) + count);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        /* entah knp ga mau
        etNoLKP.setHint("No LKP");
        if (DataUtil.isLDVHeaderClosed(realm, collectorCode, dateLKP)) {
            etNoLKP.setHint("No LKP (CLOSED)");
        }
        */

        if (count < 1) {
            search_view.setVisibility(View.INVISIBLE);
        } else {
            search_view.setVisibility(View.VISIBLE);
        }

        // due to the limitations of realmsearchview, searchable column has been disabled
        // because when sort by seqNo, i cant search by custname
        mAdapter = new LKPListAdapter(
                getContext(),
                this.realm,
                "seqNo"
        );

        search_view.setAdapter(mAdapter);
        search_view.getRealmRecyclerView().invalidate();

        return ldvNo;
    }

    public void refresh() {
        mAdapter.notifyDataSetChanged();
//        search_view.getRealmRecyclerView().invalidate();
//        search_view.invalidate();
    }

//    public String getCurrentLKPValue() {
//        return etNoLKP.getText().toString().trim();
//    }

    public class LKPListAdapter extends RealmSearchAdapter<DisplayTrnLDVDetails, LKPListAdapter.DataViewHolder> {

        private int lastPosition = -1;
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
                    if (getContext() instanceof OnFragmentLKPListInteractionListener) {
                        ((OnFragmentLKPListInteractionListener)getContext()).onLKPSelected(detail);
                    }

                }
            });

            dataViewHolder.ivCancelSync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getContext() instanceof OnFragmentLKPListInteractionListener) {
                        ((OnFragmentLKPListInteractionListener)getContext()).onLKPCancelSync(detail);
                    }

                }
            });


            TextView v = dataViewHolder.tvNoLKP;
            v.setText("Contract No: " + detail.getContractNo());

            dataViewHolder.llRowLKP.setBackgroundColor(Color.WHITE);    // must
            dataViewHolder.ivCancelSync.setVisibility(View.GONE);
            dataViewHolder.tvContactShortName.setVisibility(View.GONE);

            if (detail.getWorkStatus().equalsIgnoreCase("V")) {
                dataViewHolder.llRowLKP.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorLKPYellow));
                dataViewHolder.ivCancelSync.setVisibility(View.VISIBLE);

            } else if (detail.getWorkStatus().equalsIgnoreCase("W")) {
                dataViewHolder.llRowLKP.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorLKPBlue));
            } else if (detail.getWorkStatus().equalsIgnoreCase("SYNC")) {
                dataViewHolder.llRowLKP.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.colorLKPGreen));
                dataViewHolder.ivCancelSync.setVisibility(View.GONE);
            }

            dataViewHolder.tvContactShortName.setVisibility(dataViewHolder.ivCancelSync.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);

            TextView custName = dataViewHolder.tvCustName;
            /* ga cocok
            Typeface font = Typeface.createFromAsset(getContext().getAssets(), Utility.FONT_SAMSUNG);
            custName.setTypeface(font);
            */
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

            TextView tvContactShortName = dataViewHolder.tvContactShortName;
            tvContactShortName.setText(Utility.getFirstTwoChars(detail.getCustName()).toUpperCase());
            Typeface font = Typeface.createFromAsset(getActivity().getAssets(), Utility.FONT_SAMSUNG);
            tvContactShortName.setTypeface(font);

            // http://stackoverflow.com/questions/17823451/set-android-shape-color-programmatically
            Drawable background = tvContactShortName.getBackground();
            if (background instanceof GradientDrawable) {
                // cast to 'ShapeDrawable'
                GradientDrawable shapeDrawable = (GradientDrawable) background;

                position %= 15;

                int resColor = R.color.chatContact1;

                if (position == 1)
                    resColor = R.color.chatContact2;
                else if (position == 2)
                    resColor = R.color.chatContact3;
                else if (position == 3)
                    resColor = R.color.chatContact4;
                else if (position == 4)
                    resColor = R.color.chatContact5;
                else if (position == 5)
                    resColor = R.color.chatContact6;
                else if (position == 6)
                    resColor = R.color.chatContact7;
                else if (position == 7)
                    resColor = R.color.chatContact8;
                else if (position == 8)
                    resColor = R.color.chatContact9;
                else if (position == 9)
                    resColor = R.color.chatContact10;
                else if (position == 10)
                    resColor = R.color.chatContact11;
                else if (position == 11)
                    resColor = R.color.chatContact12;
                else if (position == 12)
                    resColor = R.color.chatContact13;
                else if (position == 13)
                    resColor = R.color.chatContact14;
                else if (position == 14)
                    resColor = R.color.chatContact15;

                shapeDrawable.setColor(ContextCompat.getColor(getContext(), resColor));

                custName.setTextColor(ContextCompat.getColor(getContext(), resColor));
            }


            Animation animation = AnimationUtils.loadAnimation(getContext(),
                    (position > lastPosition) ? R.anim.up_from_bottom
                            : R.anim.down_from_top);
            dataViewHolder.itemView.startAnimation(animation);
            lastPosition = position;
            /*
            if (position > lastPosition) {
                Animation anim = AnimationUtils.loadAnimation(getContext(), R.anim.up_from_bottom);
                dataViewHolder.itemView.startAnimation(anim);
                lastPosition = position;
            }
            */
        }

        public class DataViewHolder extends RealmSearchViewHolder {

            public FrameLayout container;

            @BindView(R.id.llRowLKP)
            LinearLayout llRowLKP;

            @BindView(R.id.ivCancelSync)
            ImageView ivCancelSync;

            @BindView(R.id.tvCustName)
            TextView tvCustName;

            @BindView(R.id.tvNoLKP)
            TextView tvNoLKP;

            @BindView(R.id.tvKelKec)
            TextView tvKelKec;

            @BindView(R.id.tvContactShortName)
            TextView tvContactShortName;

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

    public interface OnFragmentLKPListInteractionListener {
        void onLKPSelected(DisplayTrnLDVDetails detail);
        void onLKPCancelSync(DisplayTrnLDVDetails detail);

        void onLKPInquiry(String collectorCode, Date lkpDate);

        boolean isAnyDataToSync();
    }

}
