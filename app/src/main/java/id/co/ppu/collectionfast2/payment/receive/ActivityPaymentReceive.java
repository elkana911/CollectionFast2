package id.co.ppu.collectionfast2.payment.receive;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.component.RVBAdapter;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
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

public class ActivityPaymentReceive extends BasicActivity {

    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_CONTRACT_NO = "contractNo";
    public static final String PARAM_LDV_NO = "ldvNo";
    public static final String PARAM_LKP_DATE = "lkpDate";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;
    private Date lkpDate = null;

    private RVBAdapter adapterRVB;

    @BindView(R.id.activity_payment_receive)
    View activityPaymentReceive;

    @BindView(R.id.etContractNo)
    AutoCompleteTextView etContractNo;

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

    @BindView(R.id.spNoRVB)
    Spinner spNoRVB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_receive);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
            this.ldvNo = extras.getString(PARAM_LDV_NO);

            long lkpdate = extras.getLong(PARAM_LKP_DATE);
            this.lkpDate = new Date(lkpdate);

            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);

        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class)
                .equalTo("contractNo", contractNo)
                .findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_payment_receive);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(contractNo);
        etAngsuran.setText(Utility.convertLongToRupiah(dtl.getMonthInst()));
        etAngsuranKe.setText(String.valueOf(dtl.getInstNo() + 1));
        etDenda.setText(String.valueOf(dtl.getPenaltyAMBC()));

        // TODO: dtl.getCollectionFee
        etBiayaTagih.setText("10000");

        // load last save
        TrnRVColl trnRVColl = this.realm.where(TrnRVColl.class)
                .equalTo("contractNo", this.contractNo)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                .findFirst();

        if (trnRVColl != null) {

            TrnRVB rvbNo = this.realm.where(TrnRVB.class)
                    .equalTo("rvbNo", trnRVColl.getPk().getRbvNo())
                    .findFirst();

            List<TrnRVB> list = new ArrayList<>();
            list.add(realm.copyFromRealm(rvbNo));

            adapterRVB = new RVBAdapter(this, android.R.layout.simple_spinner_item, list);

            etPenerimaan.setText(String.valueOf(trnRVColl.getReceivedAmount()));
            etCatatan.setText(trnRVColl.getNotes());

        } else {
            RealmResults<TrnRVB> trnRVBs = this.realm.where(TrnRVB.class)
                    .equalTo("rvbStatus", "OP")
                    .equalTo("rvbOnHand", collectorId)
                    .findAll();

            adapterRVB = new RVBAdapter(this, android.R.layout.simple_spinner_item, this.realm.copyFromRealm(trnRVBs));

            TrnRVB hint = new TrnRVB();
            hint.setRvbNo(getString(R.string.spinner_please_select));
            adapterRVB.insert(hint, 0);

        }
        spNoRVB.setAdapter(adapterRVB);

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
            // cek dulu apakah TrnRVBnya udah CL atau masih OP ?
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
            }
            long penerimaanValue = Long.parseLong(penerimaan);
            if (penerimaanValue > 99999999) {
                etPenerimaan.setError(getString(R.string.error_amount_too_small));
                focusView = etPenerimaan;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        // better use async so you can throw and automatic canceltransaction
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
                    Snackbar.make(activityPaymentReceive, "Data already synced", Snackbar.LENGTH_SHORT).show();
                    return;
                }

/*
                RealmResults<TrnContractBuckets> trnContractBucketses = realm.where(TrnContractBuckets.class)
                        .equalTo("pk.contractNo", contractNo)
                        .equalTo("createdBy", createdBy)
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
                        .equalTo("createdBy", createdBy)
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

                /*
                TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                        .equalTo("ldvNo", ldvNo)
                        .equalTo("createdBy", createdBy)
                        .findFirst();
                trnLDVHeader.setWorkFlag("C");
                trnLDVHeader.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnLDVHeader.setLastupdateTimestamp(new Date());
                realm.copyToRealmOrUpdate(trnLDVHeader);
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

                    TrnRVCollPK trnRVCollPK = new TrnRVCollPK();
                    trnRVCollPK.setRvCollNo(sb.toString());
                    trnRVCollPK.setRbvNo(rvbNo);

                    trnRVColl = new TrnRVColl();
                    trnRVColl.setPk(trnRVCollPK);
                    trnRVColl.setCreatedBy(Utility.LAST_UPDATE_BY);
                    trnRVColl.setCreatedTimestamp(new Date());
                }
                trnRVColl.setStatusFlag("NW");

                trnRVColl.setPaymentFlag(1L);

                trnRVColl.setCollId(collectorId);
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

                RVBAdapter adapterRVB = new RVBAdapter(ActivityPaymentReceive.this, android.R.layout.simple_spinner_item, list);
                spNoRVB.setAdapter(adapterRVB);

                Toast.makeText(ActivityPaymentReceive.this, "Payment Saved", Toast.LENGTH_SHORT).show();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(ActivityPaymentReceive.this, "Database Error", Toast.LENGTH_LONG).show();

            }
        });

    }

}
