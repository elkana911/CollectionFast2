package id.co.ppu.collectionfast2.payment;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.TrnRVB;
import id.co.ppu.collectionfast2.pojo.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.TrnRVCollPK;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPayment extends Fragment{

    public static final String PARAM_COLL_CODE = "collector.code";
    public static final String PARAM_CONTRACT_NO = "contractNo";
    public static final String PARAM_LDV_NO = "ldvNo";

    private Realm realm;

    private String collectorCode = null;
    private String ldvNo = null;

    @BindView(R.id.etContractNo)
    AutoCompleteTextView etContractNo;

    @BindView(R.id.ivDropDown)
    ImageView ivDropDown;

//    @BindView(R.id.etCustName)
//    AutoCompleteTextView etCustName;

    @BindView(R.id.etAngsuranKe)
    EditText etAngsuranKe;

    @BindView(R.id.etAngsuran)
    EditText etAngsuran;

    @BindView(R.id.etDenda)
    EditText etDenda;

    @BindView(R.id.etBiayaTagih)
    EditText etBiayaTagih;

    @BindView(R.id.etPenerimaan)
    EditText etPenerimaan;

    @BindView(R.id.etCatatan)
    EditText etCatatan;

    @BindView(R.id.spNoRV)
    MaterialBetterSpinner spNoRV;

    @BindViews({R.id.etContractNo, R.id.etCustName, R.id.etAngsuranKe,
            R.id.etAngsuran, R.id.etDenda,R.id.etBiayaTagih,R.id.etPenerimaan
            ,R.id.spNoRV,R.id.etCatatan})
    List<EditText> entries;

    static final ButterKnife.Action<View> CLEAR_TEXT = new ButterKnife.Action<View>() {
        @Override
        public void apply(@NonNull View view, int index) {
            ((TextView)view).setText(null);
        }
    };

    public FragmentPayment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();

        RealmResults<TrnRVB> all = this.realm.where(TrnRVB.class).equalTo("rvbStatus", "OP").findAll();

        SpinnerAdapter adapter = new SpinnerAdapter(getContext(), android.R.layout.simple_spinner_item, all);
        spNoRV.setAdapter(adapter);

        String contractNo = null;
        if (getArguments() != null) {
            contractNo = getArguments().getString(PARAM_CONTRACT_NO);
            ldvNo = getArguments().getString(PARAM_LDV_NO);
        }

        if (contractNo == null) {

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    RealmResults<TrnContractBuckets> buckets = FragmentPayment.this.realm.where(TrnContractBuckets.class).equalTo("collectorId", collectorCode).findAll();

                    List<String> list = new ArrayList<>();
                    for (TrnContractBuckets b : buckets) {
                        list.add(b.getPk().getContractNo());
                    }
                    // sort
                    Collections.sort(list);

                    // override color of text item
                    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_list_item_1, list) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            TextView text = (TextView) view.findViewById(android.R.id.text1);
                            text.setTextColor(Color.BLACK);
                            return view;
                        }
                    };

                    etContractNo.setAdapter(dataAdapter);
                }
            });
            // Start the thread
            getActivity().runOnUiThread(t);

        } else {
            ivDropDown.setVisibility(View.GONE);
            etContractNo.setFocusable(false);
            etContractNo.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
            etContractNo.setClickable(false);

            loadContract(contractNo);
        }

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
//            String serverDate = getArguments().getString(PARAM_SERVER_DATE);
//            ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(serverDate);

            this.collectorCode = getArguments().getString(PARAM_COLL_CODE);
        }
//        showDialogPicker();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        ButterKnife.bind(this, view);

        LinearLayout llFormDependsLOV = (LinearLayout) view.findViewById(R.id.llFormDependsLOV);

        etAngsuranKe.setFocusable(false);
        etAngsuranKe.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        etAngsuranKe.setClickable(false);

        etAngsuran.setFocusable(false);
        etAngsuran.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
        etAngsuran.setClickable(false);

        return view;
    }

    public void showDialogPicker() {
        DialogFragment d = new FragmentActiveContractsList();
        Bundle bundle = new Bundle();
        bundle.putString(FragmentActiveContractsList.ARG_PARAM1, this.collectorCode);
        d.setArguments(bundle);
        d.show(getFragmentManager(), "dialog");

    }

    public void loadContract(String contractNo) {
        TrnContractBuckets contractBuckets = this.realm.where(TrnContractBuckets.class).equalTo("pk.contractNo", contractNo).findFirst();

        if (contractBuckets == null) {
            Utility.showDialog(getContext(), "Error", "Contract " + contractNo + " not found !");
            return;
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(contractBuckets.getCustName());

        etContractNo.setText(contractNo);
        etAngsuranKe.setText(String.valueOf(contractBuckets.getOvdInstNo()));


        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        etDenda.setText(String.valueOf(dtl.getPenaltyAMBC()));
        etBiayaTagih.setText("10000");

        long angsuran = (dtl.getPrincipalAmount() == null ? 0 : dtl.getPrincipalAmount())
                + (dtl.getInterestAmount() == null ? 0 : dtl.getInterestAmount());
        etAngsuran.setText(Utility.convertLongToRupiah(angsuran));

        // load last save
//        this.realm.where(RV)
        TrnRVColl trnRVColl = this.realm.where(TrnRVColl.class)
                .equalTo("contractNo", contractNo)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                .findFirst();

        if (trnRVColl != null) {
            etPenerimaan.setText(String.valueOf(trnRVColl.getReceivedAmount()));
            etCatatan.setText(trnRVColl.getNotes());
            spNoRV.setText(trnRVColl.getPk().getRbvNo());
        }

        etPenerimaan.requestFocus();
    }

    @OnClick(R.id.ivDropDown)
    public void onTapDropDownIcon() {
        showDialogPicker();
    }

    @OnClick(R.id.btnSave)
    public void onClickSave() {
        // reset errors
        etContractNo.setError(null);
        spNoRV.setError(null);
        etPenerimaan.setError(null);

        // attempt save
        boolean cancel = false;
        View focusView = null;
        final String contractNo = etContractNo.getText().toString();
        final String rvbNo = spNoRV.getText().toString();
        final String penerimaan = etPenerimaan.getText().toString();
        final String denda = etDenda.getText().toString();
        final String biayaTagih = etBiayaTagih.getText().toString();

        if (!TextUtils.isEmpty(denda)) {
            if (Long.parseLong(denda) < 0) {
                etDenda.setError(getString(R.string.error_amount_invalid));
                focusView = etDenda;
                cancel = true;
            }
        }

        if (!TextUtils.isEmpty(biayaTagih)) {
            if (Long.parseLong(biayaTagih) < 0) {
                etDenda.setError(getString(R.string.error_amount_invalid));
                focusView = etBiayaTagih;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(rvbNo)) {
            spNoRV.setError(getString(R.string.error_select_rvb));
            focusView = spNoRV;
            cancel = true;
        }

        if (TextUtils.isEmpty(penerimaan)) {
            etPenerimaan.setError(getString(R.string.error_field_required));
            focusView = etPenerimaan;
            cancel = true;
        }else{
            if (penerimaan.length() < 4) {
                etPenerimaan.setError(getString(R.string.error_amount_too_small));
                focusView = etPenerimaan;
                cancel = true;
            }
            long penerimaanValue = Long.parseLong(penerimaan);
            if (penerimaanValue > 99999999) {
                etPenerimaan.setError(getString(R.string.error_amount_too_small));
                focusView = etPenerimaan;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(contractNo)) {
            etContractNo.setError(getString(R.string.error_field_required));
            focusView = etContractNo;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                boolean isLKP = getActivity().getTitle().toString().trim().equalsIgnoreCase("payment receive");

                TrnLDVDetails dtl = realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();
                final String createdBy = dtl.getCreatedBy();

                TrnContractBuckets trnContractBuckets = realm.where(TrnContractBuckets.class).equalTo("pk.contractNo", contractNo)
                        .equalTo("createdBy", createdBy)
                        .findFirst();

                Date serverDate = realm.where(ServerInfo.class).findFirst().getServerDate();

                trnContractBuckets.setPaidDate(serverDate);
                trnContractBuckets.setRvNo(rvbNo);
                trnContractBuckets.setLkpStatus("W");
                trnContractBuckets.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnContractBuckets.setLastupdateTimestamp(new Date());
//                realm.copyToRealmOrUpdate(trnContractBuckets); // ga bisa update krn ga ada primary key
                realm.copyToRealm(trnContractBuckets); // ga bisa update krn ga ada primary key

                TrnRVB trnRVB = realm.where(TrnRVB.class).equalTo("rvbNo", rvbNo).findFirst();
                trnRVB.setRvbStatus("CL");
                trnRVB.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnRVB.setLastupdateTimestamp(new Date());
                realm.copyToRealmOrUpdate(trnRVB);

                // just in case re-entry need to query
                TrnRVColl trnRVColl = realm.where(TrnRVColl.class).equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                        .equalTo("contractNo", contractNo).findFirst();
                if (trnRVColl == null) {
                    trnRVColl = new TrnRVColl();

                    TrnRVCollPK trnRVCollPK = new TrnRVCollPK();

                    long runningNumber = 1;
                    UserConfig userConfig = realm.where(UserConfig.class).findFirst();
                    if (userConfig == null || userConfig.getKodeRVCollRunningNumber() == null) {

                    }else
                        runningNumber = userConfig.getKodeRVCollRunningNumber() +1;

                    //yyyyMMdd-runnningnumber2digit
                    StringBuffer sb = new StringBuffer();
                    sb.append(Utility.convertDateToString(serverDate, "yyyy"))
                            .append(Utility.convertDateToString(serverDate, "MM"))
                            .append(Utility.convertDateToString(serverDate, "dd"))
                            .append(Utility.leftPad(runningNumber, 3));

                    trnRVCollPK.setRvCollNo(sb.toString());
                    trnRVCollPK.setRbvNo(rvbNo);

                    trnRVColl.setPk(trnRVCollPK);
                }
                trnRVColl.setStatusFlag("NW");

                if (isLKP) {
                    trnRVColl.setPaymentFlag(1L);
                } else {
                    trnRVColl.setPaymentFlag(2L);
                }
// TODO: rest of the fields

                UserData userData = (UserData) Storage.getObjPreference(getContext(), Storage.KEY_USER, UserData.class);

                trnRVColl.setCollId(collectorCode);
                trnRVColl.setOfficeCode(userData.getUser().get(0).getBranchId());
                trnRVColl.setInstNo(Long.parseLong(etAngsuranKe.getText().toString()));
                trnRVColl.setFlagDone("Y");
                trnRVColl.setTransDate(serverDate);

                if (isLKP)
                    trnRVColl.setLdvNo(ldvNo);
                /*
                trnRVColl.setDateDone();
                trnRVColl.setDateToEmrafin();
                trnRVColl.setFlagToEmrafin();
                */
//                trnRVColl.setProcessDate(serverDate);
                trnRVColl.setContractNo(contractNo);
                trnRVColl.setNotes(etCatatan.getText().toString());
                trnRVColl.setReceivedAmount(Long.valueOf(penerimaan));
                trnRVColl.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnRVColl.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnRVColl);

                if (isLKP) {
                    TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class).equalTo("ldvNo", ldvNo).findFirst();
                    trnLDVHeader.setWorkFlag("C");
                    trnLDVHeader.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnLDVHeader.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnLDVHeader);

                    TrnLDVDetails trnLDVDetails = realm.where(TrnLDVDetails.class)
                            .equalTo("contractNo", contractNo)
                            .findFirst();
                    trnLDVDetails.setLdvFlag("COL");
                    trnLDVDetails.setWorkStatus("V");
                    trnLDVDetails.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnLDVDetails.setLastupdateTimestamp(new Date());

                    realm.copyToRealmOrUpdate(trnLDVDetails);
                }
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(getContext(), "Payment Saved", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(getContext(), "Database Error", Toast.LENGTH_LONG).show();
            }
        });

    }

    public class SpinnerAdapter extends ArrayAdapter<TrnRVB> {
        private Context ctx;
        private List<TrnRVB> list;


        public SpinnerAdapter(Context context, int resource, List<TrnRVB> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public TrnRVB getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
            tv.setPadding(10, 20, 10, 20);
            tv.setTextColor(Color.BLACK);
            tv.setText(list.get(position).getRvbNo());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(this.ctx);
            label.setTextColor(Color.BLACK);
            label.setText(list.get(position).getRvbNo());

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


}
