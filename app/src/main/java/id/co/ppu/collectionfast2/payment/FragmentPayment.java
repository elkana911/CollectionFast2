package id.co.ppu.collectionfast2.payment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.RVBAdapter;
import id.co.ppu.collectionfast2.payment.entry.FragmentActiveContractsList;
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
    public static final String PARAM_LKP_DATE = "lkpDate";

    private Realm realm;

    private String contractNo = null;
    private String collectorCode = null;
    private String ldvNo = null;
    private Date lkpDate = null;

    private RVBAdapter adapterRVB;

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

    @BindView(R.id.tblNoRVB)
    TableLayout tblNoRVB;

    @BindView(R.id.spNoRVB)
    Spinner spNoRVB;

    @BindView(R.id.etNoRVB)
    EditText etNoRVB;

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

        if (getArguments() != null) {
            this.contractNo = getArguments().getString(PARAM_CONTRACT_NO);
            this.ldvNo = getArguments().getString(PARAM_LDV_NO);

            long lkpdate = getArguments().getLong(PARAM_LKP_DATE);
            this.lkpDate = new Date(lkpdate);

            this.collectorCode = getArguments().getString(PARAM_COLL_CODE);
        }

        RealmResults<TrnRVB> openRVBList = realm.where(TrnRVB.class).equalTo("rvbStatus", "OP").findAll();

        if (adapterRVB == null) {
            List<TrnRVB> listRVB = realm.copyFromRealm(openRVBList);
            adapterRVB = new RVBAdapter(getContext(), android.R.layout.simple_spinner_item, listRVB);
        }

        TrnRVB hint = new TrnRVB();
        hint.setRvbNo(getString(R.string.spinner_please_select));
        adapterRVB.insert(hint, 0);
        spNoRVB.setAdapter(adapterRVB);

        if (contractNo == null) {
            // as payment entry
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
//            this.contractNo = getArguments().getString(PARAM_CONTRACT_NO);
//            this.ldvNo = getArguments().getString(PARAM_LDV_NO);
//
//            long lkpdate = getArguments().getLong(PARAM_LKP_DATE);
//            this.lkpDate = new Date(lkpdate);
//
//            this.collectorCode = getArguments().getString(PARAM_COLL_CODE);
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

        String createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");

        TrnContractBuckets contractBuckets = this.realm.where(TrnContractBuckets.class)
                                                .equalTo("pk.contractNo", contractNo)
                                                .equalTo("createdBy", createdBy)
                                                .findFirst();

        if (contractBuckets == null) {
            Utility.showDialog(getContext(), "Error", "Contract " + contractNo + " not found !");
            return;
        }

        ((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle(contractBuckets.getCustName());

        etContractNo.setText(contractNo);
        etAngsuranKe.setText(String.valueOf(contractBuckets.getOvdInstNo()));


        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        etDenda.setText(String.valueOf(dtl.getPenaltyAMBC()));
        etBiayaTagih.setText("10000");

        long angsuran = (dtl.getPrincipalAmount() == null ? 0 : dtl.getPrincipalAmount())
                + (dtl.getInterestAmount() == null ? 0 : dtl.getInterestAmount());
        etAngsuran.setText(Utility.convertLongToRupiah(angsuran));

        // load last save
        TrnRVColl trnRVColl = this.realm.where(TrnRVColl.class)
                .equalTo("contractNo", contractNo)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                .findFirst();

        if (trnRVColl != null) {
            etPenerimaan.setText(String.valueOf(trnRVColl.getReceivedAmount()));
            etCatatan.setText(trnRVColl.getNotes());

            String sRVBNo = trnRVColl.getPk().getRbvNo();
            /*
            for (int i = 0; i < adapterRVB.getCount(); i++) {
                if (sRVBNo.equals(adapterRVB.getItem(i).getRvbNo())) {
                    spNoRVB.setSelection(i);
                    break;
                }
            }
            */
            etNoRVB.setText(sRVBNo);
            tblNoRVB.setVisibility(View.GONE);
            etNoRVB.setVisibility(View.VISIBLE);

            // avoid user change no rvb
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
        etPenerimaan.setError(null);

        // attempt save
        boolean cancel = false;
        View focusView = null;


        final String contractNo = etContractNo.getText().toString();

        // kalo nongol brarti edit mode
        String rvbNo = null;
        if (etNoRVB.isShown()) {
            rvbNo = etNoRVB.getText().toString();
        } else {

            String sRVBNo = spNoRVB.getSelectedItem().toString();
            TrnRVB trnRVB = realm.where(TrnRVB.class).equalTo("rvbNo", sRVBNo).findFirst();

            rvbNo = trnRVB == null ? "" : trnRVB.getRvbNo();
        }


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
            Toast.makeText(getContext(), "Please select No RV !", Toast.LENGTH_SHORT).show();
            focusView = spNoRVB;
            cancel = true;
        } else {
            // cek dulu apakah TrnRVBnya udah CL atau masih OP ?

            TrnRVB rvbNo1 = realm.where(TrnRVB.class).equalTo("rvbNo", rvbNo).findFirst();
            if (rvbNo1.getRvbStatus().equals("CL")) {
                Toast.makeText(getContext(), "The selected No RV is already used.\nPlease select No RV !", Toast.LENGTH_SHORT).show();
                focusView = spNoRVB;
                cancel = true;
            }

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


        final String finalRvbNo = rvbNo;
        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                boolean isLKP = getActivity().getTitle().toString().trim().equalsIgnoreCase("payment receive");

                Date serverDate = realm.where(ServerInfo.class).findFirst().getServerDate();

                String createdBy;
                if (isLKP) {
                    createdBy = "JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd");
                } else {
                    createdBy = "JOB" + Utility.convertDateToString(lkpDate, "yyyyMMdd");
                }

                TrnLDVDetails dtl = realm.where(TrnLDVDetails.class)
                                        .equalTo("contractNo", contractNo)
                                        .equalTo("createdBy", createdBy)
                        .findFirst();

                TrnContractBuckets trnContractBuckets = realm.where(TrnContractBuckets.class).equalTo("pk.contractNo", contractNo)
                        .equalTo("createdBy", createdBy)
                        .findFirst();


                trnContractBuckets.setPaidDate(serverDate);
                trnContractBuckets.setRvNo(finalRvbNo);
                trnContractBuckets.setLkpStatus("W");
                trnContractBuckets.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnContractBuckets.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnContractBuckets); // ga bisa update krn ga ada primary key

                TrnRVB trnRVB = realm.where(TrnRVB.class).equalTo("rvbNo", finalRvbNo).findFirst();
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
                    trnRVCollPK.setRbvNo(finalRvbNo);

                    trnRVColl.setPk(trnRVCollPK);
                    trnRVColl.setCreatedBy(Utility.LAST_UPDATE_BY);
                    trnRVColl.setCreatedTimestamp(new Date());
                }
                trnRVColl.setStatusFlag("NW");

                if (isLKP) {
                    trnRVColl.setPaymentFlag(1L);
                } else {
                    trnRVColl.setPaymentFlag(2L);
                }

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


}
