package id.co.ppu.collectionfast2.payment.entry;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
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
import id.co.ppu.collectionfast2.pojo.DisplayTrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.master.MstParam;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVCollPK;
import id.co.ppu.collectionfast2.util.NetUtil;
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
            this.ldvNo = extras.getString(PARAM_LDV_NO);
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);

            long lkpdate = extras.getLong(PARAM_LKP_DATE);
            this.lkpDate = new Date(lkpdate);

            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);

            ivDropDown.setVisibility(View.GONE);

            onContractSelected(this.contractNo);

        } else {
            UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

            this.collectorId = userData.getUserId();

            Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();

            final String createdBy = "JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd");

            if (this.ldvNo == null) {
                TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                        .equalTo("collCode", this.collectorId)
                        .equalTo("createdBy", createdBy)
                        .findFirst();

                this.ldvNo = trnLDVHeader.getLdvNo();
            }

            /*
            RealmResults<DisplayTrnContractBuckets> displayTrnContractBucketses = realm.where(DisplayTrnContractBuckets.class)
                    .findAllSorted("contractNo");

            List<DisplayTrnContractBuckets> list = realm.copyFromRealm(displayTrnContractBucketses);

            ContractBucketsAdapter dataAdapter = new ContractBucketsAdapter(this, android.R.layout.simple_spinner_item, list);
            etContractNo.setAdapter(dataAdapter);
            */

            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

            new AsyncTask<Void, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(Void... voids) {
                    Realm r = null;
                    List<String> list = new ArrayList<>();

                    try {
                        r = Realm.getDefaultInstance();
                        final RealmResults<DisplayTrnContractBuckets> buckets = r.where(DisplayTrnContractBuckets.class)
                                .findAllSorted("contractNo");

                        for (DisplayTrnContractBuckets b : buckets) {
                            list.add(b.getContractNo());
                        }

                    } finally {
                        if (r != null) {
                            r.close();
                        }
                    }

                    return list;

                }

                @Override
                protected void onPostExecute( List<String> data) {
                    super.onPostExecute(data);

                    if (data.size() > 0) {

                        // override color of text item
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ActivityPaymentEntri.this,
                                android.R.layout.simple_list_item_1, data) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = (TextView) view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };

                        etContractNo.setAdapter(dataAdapter);

                    } else {
                        Button btnSave = ButterKnife.findById(ActivityPaymentEntri.this, R.id.btnSave);
                        btnSave.setVisibility(View.INVISIBLE);

                        Snackbar.make(activityPaymentEntri, "No contracts found !", Snackbar.LENGTH_LONG).show();
                    }

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                }
            }.execute();

            /*
            final ProgressDialog mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Please wait...");
            mProgressDialog.show();

            new AsyncTask<Void, Void, List<String>>() {
                @Override
                protected List<String> doInBackground(Void... voids) {
                    Realm r = null;
                    List<String> list = new ArrayList<>();

                    try {
                        r = Realm.getDefaultInstance();
                        final RealmResults<TrnContractBuckets> buckets = r.where(TrnContractBuckets.class)
                                .equalTo("collectorId", collectorId).findAll();

                        RealmResults<TrnLDVDetails> _buffer = r.where(TrnLDVDetails.class)
                                .equalTo("pk.ldvNo", ldvNo)
                                .equalTo("createdBy", createdBy)
                                .findAll();

                        for (TrnContractBuckets b : buckets) {
                            boolean exist = false;
                            for (TrnLDVDetails _dtl : _buffer) {
                                if (_dtl.getContractNo().equalsIgnoreCase(b.getPk().getContractNo())) {
                                    exist = true;
                                    break;
                                }
                            }

                            if (!exist)
                                list.add(b.getPk().getContractNo());
                        }

                        if (list.size() > 0) {
                            // sort
                            Collections.sort(list);
                        }
                    } finally {
                        if (r != null) {
                            r.close();
                        }
                    }

                    return list;

                }

                @Override
                protected void onPostExecute( List<String> data) {
                    super.onPostExecute(data);

                    if (data.size() > 0) {

                        // override color of text item
                        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ActivityPaymentEntri.this,
                                android.R.layout.simple_list_item_1, data) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = (TextView) view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };

                        etContractNo.setAdapter(dataAdapter);

                    } else {
                        Button btnSave = ButterKnife.findById(ActivityPaymentEntri.this, R.id.btnSave);
                        btnSave.setVisibility(View.INVISIBLE);

                        Snackbar.make(activityPaymentEntri, "No contracts found !", Snackbar.LENGTH_LONG).show();
                    }

                    if (mProgressDialog.isShowing())
                        mProgressDialog.dismiss();

                }
            }.execute();
*/
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
                .findAllSorted("rvbNo");

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
        bundle.putString(FragmentActiveContractsList.PARAM_LDV_NO, this.ldvNo);
        d.setArguments(bundle);

        d.show(getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onContractSelected(String contractNo) {

        // load last save
        TrnRVColl trnRVColl = isExists(this.realm);

        if (trnRVColl != null) {
            spNoRVB.setAdapter(null);

            etPenerimaan.setText(String.valueOf(trnRVColl.getReceivedAmount()));
            etCatatan.setText(trnRVColl.getNotes());
            etDenda.setText(String.valueOf(trnRVColl.getPenaltyAc()));
            etDendaBerjalan.setText(String.valueOf(trnRVColl.getDaysIntrAc()));
            etBiayaTagih.setText(String.valueOf(trnRVColl.getCollFeeAc()));

            TrnRVB rvbNo = this.realm.where(TrnRVB.class)
                    .equalTo("rvbNo", trnRVColl.getPk().getRbvNo())
                    .findFirst();

            List<TrnRVB> list = new ArrayList<>();
            list.add(realm.copyFromRealm(rvbNo));

            RVBAdapter adapterRVB = new RVBAdapter(this, android.R.layout.simple_spinner_item, list);
            spNoRVB.setAdapter(adapterRVB);
        } else {
            etPenerimaan.setText(null);
            etDenda.setText(String.valueOf(0));
            etDendaBerjalan.setText(String.valueOf(0));
            etBiayaTagih.setText("10000");

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

        } else if (trnContractBucketses.size() > 1) {
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

        etPlatform.setText(contractBuckets.getPlatform());
        long danaSosial = contractBuckets.getDanaSosial() == null ? 0 : contractBuckets.getDanaSosial().longValue();
        etDanaSosial.setText(Utility.convertLongToRupiah(danaSosial));

        etAngsuranKe.setText(String.valueOf(contractBuckets.getOvdInstNo()));

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
        etDendaBerjalan.setError(null);
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
                long dendaBerjalanValue = Long.parseLong(dendaBerjalan);
                long dendaTotal = dendaValue + dendaBerjalanValue;

                long minDendaValue = 0;
                MstParam keyMinPenalty = this.realm.where(MstParam.class)
                        .equalTo("key", "MIN_PENALTY_RV")
                        .findFirst();
                if (keyMinPenalty != null) {
                    minDendaValue = Long.parseLong(keyMinPenalty.getValue());
                }
            }
        }

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

        final String createdBy = "JOB" + Utility.convertDateToString(lkpDate, Utility.DATE_DATA_PATTERN);

        // TODO: check lagi kalo ada di daftar LKP dan workstatus nya belum bayar ("V") jgn entri disini
        RealmResults<TrnLDVDetails> lkpDtls = realm.where(TrnLDVDetails.class)
                .equalTo("pk.ldvNo", ldvNo)
                .equalTo("contractNo", etContractNo.getText().toString())
                .equalTo("createdBy", createdBy)
                .notEqualTo("workStatus", "W")
                .findAll();

        if (lkpDtls.size() > 0) {
            focusView = etContractNo;
            Toast.makeText(this, "Please use LKP List screen to entri payment", Toast.LENGTH_SHORT).show();
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        final Date serverDate = realm.where(ServerInfo.class).findFirst().getServerDate();
        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

                SyncTrnRVColl trnSync = realm.where(SyncTrnRVColl.class)
                        .equalTo("ldvNo", ldvNo)
                        .equalTo("contractNo", contractNo)
                        .equalTo("createdBy", createdBy)
                        .isNotNull("syncedDate")
                        .findFirst();

                if (trnSync != null) {
                    Snackbar.make(activityPaymentEntri, "Cannot save, Data already synced", Snackbar.LENGTH_SHORT).show();
                    return;
                }

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

                    // kata pak yoce, running number rvbno sehari cukup satu saja ga perlu generate terus, besoknya di reset.

                    // generate runningnumber
                    UserConfig userConfig = realm.where(UserConfig.class).findFirst();

                    if (userConfig.getKodeRVCollRunningNumber() == null)
                        userConfig.setKodeRVCollRunningNumber(0L);


                    /*
                    long runningNumber = userConfig.getKodeRVCollRunningNumber();
                    if (userConfig.getKodeRVCollLastGenerated() == null
                            || !Utility.isSameDay(userConfig.getKodeRVCollLastGenerated(), new Date())
                            ) {

                        runningNumber = userConfig.getKodeRVCollRunningNumber() + 1;
                        userConfig.setKodeRVCollRunningNumber(runningNumber);
                        userConfig.setKodeRVCollLastGenerated(new Date());
                        realm.copyToRealmOrUpdate(userConfig);

                    }
*/
                    // generate runningnumber: 1 koletor 1 nomor per hari. maka triknya yyyymmdd<collectorId>
                    StringBuilder sb = new StringBuilder();
                    sb.append(Utility.convertDateToString(serverDate, "dd"))
                            .append(Utility.convertDateToString(serverDate, "MM"))
                            .append(Utility.convertDateToString(serverDate, "yyyy"))
                            .append(collectorId);
//                            .append(Utility.leftPad(runningNumber, 3));

                    TrnRVCollPK trnRVCollPK = realm.createObject(TrnRVCollPK.class);
                    trnRVCollPK.setRvCollNo(sb.toString());
                    trnRVCollPK.setRbvNo(rvbNo);

                    trnRVColl = realm.createObject(TrnRVColl.class);
                    trnRVColl.setPk(trnRVCollPK);
                    trnRVColl.setCreatedBy(Utility.LAST_UPDATE_BY);
                    trnRVColl.setCreatedTimestamp(new Date());
                }
                trnRVColl.setStatusFlag("NW");
                trnRVColl.setFlagToEmrafin("N");

                trnRVColl.setPaymentFlag(2L);

                trnRVColl.setCollId(userData.getUserId());
                trnRVColl.setOfficeCode(userData.getBranchId());
//                trnRVColl.setCollId(userData.getUser().get(0).getUserId());
//                trnRVColl.setOfficeCode(userData.getUser().get(0).getBranchId());
                trnRVColl.setInstNo(Long.parseLong(etAngsuranKe.getText().toString()));
                trnRVColl.setFlagDone("Y");
                trnRVColl.setTransDate(serverDate);
                trnRVColl.setProcessDate(serverDate);

                trnRVColl.setLdvNo(null);

                trnRVColl.setPenaltyAc(Long.valueOf(denda));
                trnRVColl.setDaysIntrAc(0L);
//                trnRVColl.setDaysIntrAc(Long.valueOf(dendaBerjalan));
                trnRVColl.setCollFeeAc(Long.valueOf(biayaTagih));

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
                StringBuffer message2 = new StringBuffer();
                message2.append("contractNo=").append(contractNo)
                        .append(",penerimaan=").append(penerimaan)
                        .append(",denda=").append(denda)
                        .append(",dendaBerjalan=").append(dendaBerjalan)
                        .append(",biayaTagih=").append(penerimaan)
                        .append(",rvbNo=").append(rvbNo)
                        .append(",serverDate=").append(serverDate)
                ;

                NetUtil.syncLogError(getBaseContext(), realm, collectorId, "PaymentEntri", error.getMessage(), message2.toString());

                Toast.makeText(ActivityPaymentEntri.this, "Database Error", Toast.LENGTH_LONG).show();
                Snackbar.make(activityPaymentEntri, error.getMessage(), Snackbar.LENGTH_LONG).show();

            }
        });
    }

}

