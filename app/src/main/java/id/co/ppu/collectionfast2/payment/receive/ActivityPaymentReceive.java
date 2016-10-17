package id.co.ppu.collectionfast2.payment.receive;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
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
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.master.MstParam;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVCollPK;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityPaymentReceive extends BasicActivity {

    private final CharSequence[] collFeeList = {
            "0", "10000"
    };

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

    @BindView(R.id.etPlatform)
    EditText etPlatform;

    @BindView(R.id.etAngsuranKe)
    EditText etAngsuranKe;

    @BindView(R.id.etAngsuran)
    EditText etAngsuran;

    @BindView(R.id.etDenda)
    EditText etDenda;

    @BindView(R.id.etDendaBerjalan)
    EditText etDendaBerjalan;

    @BindView(R.id.etBiayaTagih)
    EditText etBiayaTagih;

    @BindView(R.id.etDanaSosial)
    EditText etDanaSosial;

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

        if (this.lkpDate == null) {
            this.lkpDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
        }

        String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", createdBy)
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
        etPlatform.setText(dtl.getPlatform());

        // load last save
        TrnRVColl trnRVColl = isExists(this.realm);

        if (trnRVColl != null) {

            TrnRVB rvbNo = this.realm.where(TrnRVB.class)
                    .equalTo("rvbNo", trnRVColl.getPk().getRbvNo())
                    .findFirst();

            List<TrnRVB> list = new ArrayList<>();
            list.add(realm.copyFromRealm(rvbNo));

            adapterRVB = new RVBAdapter(this, android.R.layout.simple_spinner_item, list);

            etPenerimaan.setText(String.valueOf(trnRVColl.getReceivedAmount()));
            etCatatan.setText(trnRVColl.getNotes());

            etDenda.setText(String.valueOf(trnRVColl.getPenaltyAc()));
//            etDendaBerjalan.setText(String.valueOf(trnRVColl.getDaysIntrAc()));
            etBiayaTagih.setText(String.valueOf(trnRVColl.getCollFeeAc()));
            etDanaSosial.setText("0");

        } else {
            RealmResults<TrnRVB> trnRVBs = this.realm.where(TrnRVB.class)
                    .equalTo("rvbStatus", "OP")
                    .equalTo("rvbOnHand", collectorId)
                    .findAllSorted("rvbNo");

            adapterRVB = new RVBAdapter(this, android.R.layout.simple_spinner_item, this.realm.copyFromRealm(trnRVBs));

            TrnRVB hint = new TrnRVB();
            hint.setRvbNo(getString(R.string.spinner_please_select));
            adapterRVB.insert(hint, 0);

            etDenda.setText(String.valueOf(Utility.longValue(dtl.getPenaltyAMBC()) + Utility.longValue(dtl.getDaysIntrAmbc())));
//            etDenda.setText(String.valueOf(dtl.getPenaltyAMBC()));
//            etDendaBerjalan.setText(String.valueOf(dtl.getDaysIntrAmbc()));
            etBiayaTagih.setText(dtl.getCollectionFee() == null ? "0" : String.valueOf(dtl.getCollectionFee()));
            long danaSosial = dtl.getDanaSosial() == null ? 0 : dtl.getDanaSosial().longValue();
            etDanaSosial.setText(Utility.convertLongToRupiah(danaSosial));

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
//        etDendaBerjalan.setError(null);
        etBiayaTagih.setError(null);
        etDanaSosial.setError(null);
        etPlatform.setError(null);

        // attempt save
        boolean cancel = false;
        View focusView = null;

        final String penerimaan = etPenerimaan.getText().toString().trim();
        final String denda = etDenda.getText().toString().trim();
        final String dendaBerjalan = etDendaBerjalan.getText().toString().trim();
        final String biayaTagih = etBiayaTagih.getText().toString().trim();

        if (etCatatan.getText().toString().length() > 300) {
            etCatatan.setError("Should not over " + 300);
            focusView = etCatatan;
            cancel = true;
        }

        final String rvbNo = spNoRVB.getSelectedItem().toString();
        TrnRVB selectedRVB = realm.where(TrnRVB.class)
                .equalTo("rvbNo", rvbNo)
                .findFirst();

        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, Utility.DATE_DATA_PATTERN);

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", createdBy)
                .findFirst();


        if (!Utility.isValidMoney(denda)) {
            etDenda.setError(getString(R.string.error_amount_invalid));
            focusView = etDenda;
            cancel = true;
        } else {

            if (denda.length() > Utility.MAX_MONEY_DIGITS) {
                etDenda.setError(getString(R.string.error_amount_invalid));
                focusView = etDenda;
                cancel = true;
            } else {

                long dendaValue = Long.parseLong(denda);
//                long dendaBerjalanValue = Long.parseLong(dendaBerjalan);
//                long dendaTotal = dendaValue + dendaBerjalanValue;

                long minDendaValue = 0;
                MstParam keyMinPenalty = this.realm.where(MstParam.class)
                        .equalTo("key", "MIN_PENALTY_RV")
                        .findFirst();
                if (keyMinPenalty != null) {
                    minDendaValue = Long.parseLong(keyMinPenalty.getValue());

                    long batas = Utility.longValue(dtl.getPenaltyAMBC()) + Utility.longValue(dtl.getDaysIntrAmbc());

                    if (batas < minDendaValue) {
                        if (dendaValue != batas) {
                            etDenda.setError("Denda should be " + batas);
                            focusView = etDenda;
                            cancel = true;

                        }
                    } else {
                        if (dendaValue < minDendaValue) {
                            etDenda.setError("Should not under " + minDendaValue);
                            focusView = etDenda;
                            cancel = true;
                        }
                        if (dendaValue > batas) {
                            etDenda.setError("Should not above " + batas);
                            focusView = etDenda;
                            cancel = true;
                        }
                    }
                    /*
                    if (dendaTotal > minDendaValue) {
                        if (dendaValue < minDendaValue) {
                            etDenda.setError("Should not under " + minDendaValue);
                            focusView = etDenda;
                            cancel = true;
                        }
                    } else {
                        if (dendaValue < dendaTotal) {
                            etDenda.setError("Should not under " + dendaValue);
                            focusView = etDenda;
                            cancel = true;
                        }
                    }
                    */
                }
            }


            /*
            if (dendaValue < dtl.getPenaltyAMBC().longValue()) {
                etDenda.setError("Should not under " + String.valueOf(dtl.getPenaltyAMBC()));
                focusView = etDenda;
                cancel = true;
            } else {
                long minDendaValue = 0;
                MstParam keyMinPenalty = this.realm.where(MstParam.class)
                        .equalTo("key", "MIN_PENALTY_RV")
                        .findFirst();
                if (keyMinPenalty != null) {
                    minDendaValue = Long.parseLong(keyMinPenalty.getValue());
                }

                if (dendaValue < minDendaValue) {
                    etDenda.setError("Should not under " + minDendaValue);
                    focusView = etDenda;
                    cancel = true;
                }
            }
            */

        }
/*
        if (TextUtils.isEmpty(etDendaBerjalan.getText())
                || !Utility.isNumeric(etDendaBerjalan.getText().toString())
                ) {
            etDendaBerjalan.setError(getString(R.string.error_amount_invalid));
            focusView = etDendaBerjalan;
            cancel = true;
        } else {

            if (dendaBerjalan.length() > Utility.MAX_MONEY_DIGITS) {
                etDendaBerjalan.setError(getString(R.string.error_amount_invalid));
                focusView = etDendaBerjalan;
                cancel = true;

            } else {

                long dendaBerjalanValue = Long.parseLong(dendaBerjalan);
                if (dendaBerjalanValue < dtl.getDaysIntrAmbc().longValue()) {
                    etDendaBerjalan.setError("Should not under " + String.valueOf(dtl.getDaysIntrAmbc()));
                    focusView = etDendaBerjalan;
                    cancel = true;
                }
            }
        }
*/
        if (!Utility.isValidMoney(biayaTagih)) {
            etBiayaTagih.setError(getString(R.string.error_amount_invalid));
            focusView = etBiayaTagih;
            cancel = true;
        } else {

            if (biayaTagih.length() > Utility.MAX_MONEY_DIGITS) {
                etBiayaTagih.setError(getString(R.string.error_amount_invalid));
                focusView = etBiayaTagih;
                cancel = true;
            } else {

                long biayaTagihValue = Long.parseLong(biayaTagih);

                if (biayaTagihValue == 0 || biayaTagihValue == 10000) {

                } else {
                    etBiayaTagih.setError(getString(R.string.error_coll_fee_range));
                    focusView = etBiayaTagih;
                    cancel = true;
                }
/*
                long collFee = dtl.getCollectionFee() == null ? 0 : dtl.getCollectionFee().longValue();
                if (biayaTagihValue < collFee) {
                    etBiayaTagih.setText("Should not under " + String.valueOf(collFee));
                    focusView = etBiayaTagih;
                    cancel = true;
                }
                */
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

        if (!Utility.isValidMoney(penerimaan)) {
            etPenerimaan.setError(getString(R.string.error_amount_invalid));
            focusView = etPenerimaan;
            cancel = true;
        } else {
            long penerimaanValue = Long.parseLong(penerimaan);

            if (penerimaanValue > Utility.MAX_MONEY_LIMIT) {
                etPenerimaan.setError(getString(R.string.error_amount_too_large));
                focusView = etPenerimaan;
                cancel = true;
            } else if (penerimaanValue < 10000) {
                etPenerimaan.setError(getString(R.string.error_amount_too_small));
                focusView = etPenerimaan;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        final Date serverDate = realm.where(ServerInfo.class).findFirst().getServerDate();
        final UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        if (userData == null) {
            Snackbar.make(activityPaymentReceive, "Invalid User Data. Please relogin.", Snackbar.LENGTH_SHORT).show();
            return;
        }
        // better use async so you can throw and automatic canceltransaction
        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {

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
                trnLDVDetails.setFlagToEmrafin("N");
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

                    // generate runningnumber: 1 koletor 1 nomor per hari. maka triknya yyyymmdd<collectorId>
                    UserConfig userConfig = realm.where(UserConfig.class).findFirst();

                    /*
                    if (userConfig.getKodeRVCollRunningNumber() == null)
                        userConfig.setKodeRVCollRunningNumber(0L);

                    // TODO: coba cek dulu jgn sampe generated lagi di hari yg sama
                    long runningNumber = userConfig.getKodeRVCollRunningNumber();
                    if (userConfig.getKodeRVCollLastGenerated() == null
                            || !Utility.isSameDay(userConfig.getKodeRVCollLastGenerated(), new Date())
                            ) {

                        runningNumber = userConfig.getKodeRVCollRunningNumber() + 1;
                        userConfig.setKodeRVCollRunningNumber(runningNumber);
                        userConfig.setKodeRVCollLastGenerated(new Date());
                        realm.copyToRealmOrUpdate(userConfig);

                    }*/

                    //yyyyMMdd-runnningnumber2digit
                    StringBuilder sb = new StringBuilder();
                    sb.append(Utility.convertDateToString(serverDate, "dd"))
                            .append(Utility.convertDateToString(serverDate, "MM"))
                            .append(Utility.convertDateToString(serverDate, "yyyy"))
//                            .append(Utility.leftPad(runningNumber, 3));
                            .append(collectorId);

                    TrnRVCollPK trnRVCollPK = new TrnRVCollPK();
                    trnRVCollPK.setRvCollNo(sb.toString());
                    trnRVCollPK.setRbvNo(rvbNo);

                    trnRVColl = new TrnRVColl();
                    trnRVColl.setPk(trnRVCollPK);
                    trnRVColl.setCreatedBy(Utility.LAST_UPDATE_BY);
                    trnRVColl.setCreatedTimestamp(new Date());
                }
                trnRVColl.setStatusFlag("NW");
                trnRVColl.setFlagToEmrafin("N");

                trnRVColl.setPaymentFlag(1L);

                // payment receive udah pasti denda, maka ambil dana sosial apa adanya dari detil
                long danaSosial = trnLDVDetails.getDanaSosial() == null ? 0 : trnLDVDetails.getDanaSosial().longValue();
                trnRVColl.setDanaSosial(danaSosial);
                trnRVColl.setPlatform(trnLDVDetails.getPlatform());

                trnRVColl.setCollId(collectorId);
                trnRVColl.setOfficeCode(userData.getBranchId());
                trnRVColl.setInstNo(Long.parseLong(etAngsuranKe.getText().toString()));
                trnRVColl.setFlagDone("Y");
                trnRVColl.setTransDate(serverDate);
                trnRVColl.setProcessDate(serverDate);

                trnRVColl.setPenaltyAc(Long.parseLong(denda));
//                trnRVColl.setDaysIntrAc(Long.parseLong(dendaBerjalan));
                trnRVColl.setDaysIntrAc(0L);
                trnRVColl.setCollFeeAc(Long.parseLong(biayaTagih));

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
                StringBuffer message2 = new StringBuffer();
                message2.append("contractNo=").append(contractNo)
                        .append(",penerimaan=").append(penerimaan)
                        .append(",denda=").append(denda)
                        .append(",dendaBerjalan=").append(dendaBerjalan)
                        .append(",biayaTagih=").append(penerimaan)
                        .append(",rvbNo=").append(rvbNo)
                        .append(",serverDate=").append(serverDate)
                ;

                NetUtil.syncLogError(getBaseContext(), realm, collectorId, "PaymentReceive", error.getMessage(), message2.toString());

                Toast.makeText(ActivityPaymentReceive.this, "Database Error", Toast.LENGTH_LONG).show();
                Snackbar.make(activityPaymentReceive, error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });

    }

}
