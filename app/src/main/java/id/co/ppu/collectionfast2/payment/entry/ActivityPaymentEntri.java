package id.co.ppu.collectionfast2.payment.entry;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
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
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.component.RVBAdapter;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.TrnRVB;
import id.co.ppu.collectionfast2.pojo.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.TrnRVCollPK;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.sync.pojo.SyncTrnRVColl;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Dipakai juga untuk LKP Inquiry.
 * Dibedakan dengan ActivityPaymentReceive karena pengaturan flag dan akses contractbuckets
 */
public class ActivityPaymentEntri extends BasicActivity implements FragmentActiveContractsList.OnActiveContractSelectedListener {
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_CONTRACT_NO = "contractNo";
    public static final String PARAM_LKP_DATE = "lkpDate";
    public static final String PARAM_LDV_NO = "ldvNo";

    private String contractNo = null;
    private String collectorId = null;
    private Date lkpDate = null;
    private String ldvNo = null;

    @BindView(R.id.activity_payment_entri)
    View activityPaymentEntri;

    @BindView(R.id.etContractNo)
    AutoCompleteTextView etContractNo;

    @BindView(R.id.ivDropDown)
    ImageView ivDropDown;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_entri);

        ButterKnife.bind(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.ldvNo= extras.getString(PARAM_LDV_NO);
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);

            long lkpdate = extras.getLong(PARAM_LKP_DATE);
            this.lkpDate = new Date(lkpdate);

            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);

            /*
            String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");
            TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class)
                    .equalTo("contractNo", this.contractNo)
                    .equalTo("createdBy", createdBy)
                    .findFirst();
                    */
            ivDropDown.setVisibility(View.GONE);

            onContractSelected(this.contractNo);
/*
            if (getSupportActionBar() != null) {
                getSupportActionBar().setSubtitle(dtl.getCustName());
            }

            etContractNo.setText(contractNo);
            etAngsuran.setText(Utility.convertLongToRupiah(dtl.getMonthInst()));
            etAngsuranKe.setText(String.valueOf(dtl.getInstNo() + 1));
            etDenda.setText(String.valueOf(dtl.getPenaltyAMBC()));


            buildRVB();
*/
        } else {
            UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

            this.collectorId = userData.getUser().get(0).getUserId();

            RealmResults<TrnContractBuckets> buckets = this.realm.where(TrnContractBuckets.class)
                    .equalTo("collectorId", this.collectorId).findAll();

            List<String> list = new ArrayList<>();
            for (TrnContractBuckets b : buckets) {
                list.add(b.getPk().getContractNo());
            }
            // sort
            Collections.sort(list);

            // override color of text item
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_payment_entry);
        }

        etBiayaTagih.setText("10000");

    }

    private void buildRVB() {
        spNoRVB.setAdapter(null);

        RealmResults<TrnRVB> openRVBList = realm.where(TrnRVB.class)
                .equalTo("rvbStatus", "OP")
                .findAll();

        List<TrnRVB> listRVB = realm.copyFromRealm(openRVBList);
        RVBAdapter adapterRVB = new RVBAdapter(this, android.R.layout.simple_spinner_item, listRVB);

        TrnRVB hint = new TrnRVB();
        hint.setRvbNo(getString(R.string.spinner_please_select));
        adapterRVB.insert(hint, 0);
        spNoRVB.setAdapter(adapterRVB);
    }

    @OnClick(R.id.ivDropDown)
    public void onTapDropDownIcon() {
        showDialogPicker();
    }

    public void showDialogPicker() {
        DialogFragment d = new FragmentActiveContractsList();
        Bundle bundle = new Bundle();
        bundle.putString(FragmentActiveContractsList.ARG_PARAM1, this.collectorId);
        d.setArguments(bundle);

        d.show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onContractSelected(String contractNo) {

        // load last save
        TrnRVColl trnRVColl = this.realm.where(TrnRVColl.class)
                .equalTo("contractNo", contractNo)
//                .equalTo("collId", collectorId)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                .findFirst();

        if (trnRVColl != null) {
            spNoRVB.setAdapter(null);

            etPenerimaan.setText(String.valueOf(trnRVColl.getReceivedAmount()));
            etCatatan.setText(trnRVColl.getNotes());

            TrnRVB rvbNo = this.realm.where(TrnRVB.class)
                    .equalTo("rvbNo", trnRVColl.getPk().getRbvNo())
                    .findFirst();

            List<TrnRVB> list = new ArrayList<>();
            list.add(realm.copyFromRealm(rvbNo));

            RVBAdapter adapterRVB = new RVBAdapter(this, android.R.layout.simple_spinner_item, list);
            spNoRVB.setAdapter(adapterRVB);
        } else {
            etPenerimaan.setText(null);

            buildRVB();

        }

        if (this.lkpDate == null) {
            this.lkpDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
        }

        String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");

        this.contractNo = contractNo;
        etContractNo.setText(contractNo);

        TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                .equalTo("collCode", this.collectorId)
                .equalTo("createdBy", createdBy)
                .findFirst();

        this.ldvNo = trnLDVHeader.getLdvNo();

        RealmResults<TrnContractBuckets> trnContractBucketses = this.realm.where(TrnContractBuckets.class)
                .equalTo("pk.contractNo", contractNo)
//                .equalTo("createdBy", createdBy)
                .findAll();

        if (trnContractBucketses.size() < 1) {

        }else
        if (trnContractBucketses.size() > 1) {
            Utility.showDialog(this, "Warning", "Duplicate contract " + contractNo + " found !");
        }

        TrnContractBuckets contractBuckets = trnContractBucketses.get(0);

        if (contractBuckets == null) {
            Utility.showDialog(this, "Error", "Contract " + contractNo + " not found !");
            return;
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setSubtitle(contractBuckets.getCustName());
        }

        etAngsuranKe.setText(String.valueOf(contractBuckets.getOvdInstNo()));

        // TODO: ask pak yoce drmana ambilnya di rvcol ?
        etDenda.setText(String.valueOf(0));
        etBiayaTagih.setText("10000");

        long angsuran = (contractBuckets.getPrncAmt() == null ? 0 : contractBuckets.getPrncAmt())
                + (contractBuckets.getIntrAmt() == null ? 0 : contractBuckets.getIntrAmt());
        etAngsuran.setText(Utility.convertLongToRupiah(angsuran));

        etPenerimaan.requestFocus();
    }

    private TrnRVColl isExists(Realm realm) {
        return realm.where(TrnRVColl.class)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .equalTo("contractNo", contractNo)
                .findFirst();

    }

    @OnClick(R.id.btnSave)
    public void onClickSave() {

        boolean editMode = isExists(this.realm) != null;

        // reset errors
        etPenerimaan.setError(null);
        etDenda.setError(null);
        etBiayaTagih.setError(null);

        // attempt save
        boolean cancel = false;
        View focusView = null;

        final String penerimaan = etPenerimaan.getText().toString().trim();
        final String denda = etDenda.getText().toString().trim();
        final String biayaTagih = etBiayaTagih.getText().toString().trim();

        final String rvbNo = spNoRVB.getSelectedItem().toString();
        TrnRVB selectedRVB = realm.where(TrnRVB.class)
                .equalTo("rvbNo", rvbNo)
                .findFirst();

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

        if (selectedRVB == null) {
            Toast.makeText(this, "Please select No RV !", Toast.LENGTH_SHORT).show();
            focusView = spNoRVB;
            cancel = true;
        } else {
            // cek lagi dulu apakah TrnRVBnya udah CL atau masih OP ?
            if (!editMode && selectedRVB.getRvbStatus().equals("CL")) {
                Toast.makeText(this, "The selected No RV is already used.\nPlease select another !", Toast.LENGTH_SHORT).show();
                focusView = spNoRVB;
                cancel = true;
            }

        }

        if (TextUtils.isEmpty(penerimaan)) {
            etPenerimaan.setError(getString(R.string.error_field_required));
            focusView = etPenerimaan;
            cancel = true;
        } else {
            if (penerimaan.length() < 4) {
                etPenerimaan.setError(getString(R.string.error_amount_too_small));
                focusView = etPenerimaan;
                cancel = true;
            } else {

                if (penerimaan.startsWith("0")) {
                    etPenerimaan.setError(getString(R.string.error_amount_invalid));
                    focusView = etPenerimaan;
                    cancel = true;
                } else {
                    long penerimaanValue = Long.parseLong(penerimaan);
                    if (penerimaanValue > 99999999) {
                        etPenerimaan.setError(getString(R.string.error_amount_too_large));
                        focusView = etPenerimaan;
                        cancel = true;
                    }else if (penerimaanValue < 10000) {
                        etPenerimaan.setError(getString(R.string.error_amount_too_small));
                        focusView = etPenerimaan;
                        cancel = true;
                    }

                }
            }

        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Date serverDate = realm.where(ServerInfo.class).findFirst().getServerDate();
                UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

                String createdBy = "JOB" + Utility.convertDateToString(lkpDate, Utility.DATE_DATA_PATTERN);

                SyncTrnRVColl trnSync = realm.where(SyncTrnRVColl.class)
                        .equalTo("ldvNo", ldvNo)
                        .equalTo("contractNo", contractNo)
                        .equalTo("createdBy", createdBy)
                        .isNotNull("syncedDate")
                        .findFirst();

                if (trnSync != null) {
                    Snackbar.make(activityPaymentEntri, "Data already synced", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                /*
                RealmResults<TrnContractBuckets> trnContractBucketses = realm.where(TrnContractBuckets.class)
                        .equalTo("pk.contractNo", ActivityPaymentEntri.this.contractNo)
//                        .equalTo("createdBy", createdBy)
                        .findAll();

                if (trnContractBucketses.size() > 1) {
                    throw new RuntimeException("Duplicate data ContractBucket found");
                }

                TrnContractBuckets trnContractBuckets = realm.copyFromRealm(trnContractBucketses.get(0));
                boolean b = trnContractBucketses.deleteAllFromRealm();

                trnContractBuckets.setPaidDate(serverDate);
                trnContractBuckets.setRvNo(rvbNo);
                trnContractBuckets.setLkpStatus("W");
                trnContractBuckets.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnContractBuckets.setLastupdateTimestamp(new Date());

                realm.copyToRealm(trnContractBuckets);
*/
                RealmResults<TrnLDVDetails> trnLDVDetailses = realm.where(TrnLDVDetails.class)
                        .equalTo("pk.ldvNo", ldvNo)
                        .equalTo("contractNo", contractNo)
//                        .equalTo("createdBy", createdBy)
                        .findAll();
                if (trnLDVDetailses.size() > 1) {
                    throw new RuntimeException("Duplicate data LDVDetail found");
                }
                TrnLDVDetails trnLDVDetails = realm.copyFromRealm(trnLDVDetailses.get(0));
                boolean b = trnLDVDetailses.deleteAllFromRealm();

                trnLDVDetails.setLdvFlag("COL");
                trnLDVDetails.setWorkStatus("V");
                trnLDVDetails.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnLDVDetails.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnLDVDetails);

                // dari detil dapat header utk diambil ldvNo
                /*
                TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                        .equalTo("ldvNo", trnLDVDetails.getPk().getLdvNo())
                        .equalTo("createdBy", createdBy)
                        .findFirst();

                trnLDVHeader.setWorkFlag("C");
                trnLDVHeader.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnLDVHeader.setLastupdateTimestamp(new Date());
                realm.copyToRealmOrUpdate(trnLDVHeader); // hanya di update waktu close batch
                */

                TrnRVB trnRVB = realm.where(TrnRVB.class)
                        .equalTo("rvbNo", rvbNo)
                        .findFirst();
                trnRVB.setRvbStatus("CL");
                trnRVB.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnRVB.setLastupdateTimestamp(new Date());
                realm.copyToRealmOrUpdate(trnRVB);

                // just in case re-entry need to query
                TrnRVColl trnRVColl = isExists(realm);

                if (trnRVColl == null) {

                    // generate runningnumber
                    UserConfig userConfig = realm.where(UserConfig.class).findFirst();
                    if (userConfig.getKodeRVCollRunningNumber() == null)
                        userConfig.setKodeRVCollRunningNumber(0L);
                    long runningNumber = userConfig.getKodeRVCollRunningNumber() + 1;
                    userConfig.setKodeRVCollRunningNumber(runningNumber);
                    realm.copyToRealmOrUpdate(userConfig);

                    //yyyyMMdd-runnningnumber2digit

                    StringBuilder sb = new StringBuilder();
                    sb.append(Utility.convertDateToString(serverDate, "yyyy"))
                            .append(Utility.convertDateToString(serverDate, "MM"))
                            .append(Utility.convertDateToString(serverDate, "dd"))
                            .append(Utility.leftPad(runningNumber, 3));

                    TrnRVCollPK trnRVCollPK = realm.createObject(TrnRVCollPK.class);
                    trnRVCollPK.setRvCollNo(sb.toString());
                    trnRVCollPK.setRbvNo(rvbNo);

                    trnRVColl = realm.createObject(TrnRVColl.class);
                    trnRVColl.setPk(trnRVCollPK);
                    trnRVColl.setCreatedBy(Utility.LAST_UPDATE_BY);
                    trnRVColl.setCreatedTimestamp(new Date());
                }
                trnRVColl.setStatusFlag("NW");

                trnRVColl.setPaymentFlag(2L);

                trnRVColl.setCollId(userData.getUser().get(0).getUserId());
                trnRVColl.setOfficeCode(userData.getUser().get(0).getBranchId());
                trnRVColl.setInstNo(Long.parseLong(etAngsuranKe.getText().toString()));
                trnRVColl.setFlagDone("Y");
                trnRVColl.setTransDate(serverDate);
                trnRVColl.setProcessDate(serverDate);

                trnRVColl.setLdvNo(trnLDVDetails.getPk().getLdvNo());

                trnRVColl.setContractNo(contractNo);
                trnRVColl.setNotes(etCatatan.getText().toString());
                trnRVColl.setReceivedAmount(Long.valueOf(penerimaan));
                trnRVColl.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnRVColl.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnRVColl);

            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                spNoRVB.setAdapter(null);

                List<TrnRVB> list = new ArrayList<>();
                list.add(realm.copyFromRealm(realm.where(TrnRVB.class)
                        .equalTo("rvbNo", rvbNo)
                        .findFirst()));

                RVBAdapter adapterRVB = new RVBAdapter(ActivityPaymentEntri.this, android.R.layout.simple_spinner_item, list);
                spNoRVB.setAdapter(adapterRVB);

                Toast.makeText(ActivityPaymentEntri.this, "Payment Saved", Toast.LENGTH_SHORT).show();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(ActivityPaymentEntri.this, "Database Error", Toast.LENGTH_LONG).show();

            }
        });
    }

}

