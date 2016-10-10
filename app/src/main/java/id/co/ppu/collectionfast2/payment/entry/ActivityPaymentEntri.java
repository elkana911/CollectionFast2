package id.co.ppu.collectionfast2.payment.entry;

import android.graphics.Color;
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
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVCollPK;
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

            Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
            String createdBy = "JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd");

            if (this.ldvNo == null) {
                TrnLDVHeader trnLDVHeader = realm.where(TrnLDVHeader.class)
                        .equalTo("collCode", this.collectorId)
                        .equalTo("createdBy", createdBy)
                        .findFirst();

                this.ldvNo = trnLDVHeader.getLdvNo();
            }

            RealmResults<TrnLDVDetails> _buffer = this.realm.where(TrnLDVDetails.class)
                    .equalTo("pk.ldvNo", ldvNo)
                    .equalTo("createdBy", createdBy)
                    .findAll();

            List<String> list = new ArrayList<>();
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

            } else {
                Button btnSave = ButterKnife.findById(this, R.id.btnSave);
                btnSave.setVisibility(View.INVISIBLE);

                Snackbar.make(activityPaymentEntri, "No contracts found !", Snackbar.LENGTH_LONG).show();
            }

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
        etDanaSosial.setText(Utility.convertLongToRupiah(contractBuckets.getDanaSosial()));

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
        etDanaSosial.setError(null
        );

        // attempt save
        boolean cancel = false;
        View focusView = null;

        final String penerimaan = etPenerimaan.getText().toString().trim();
        final String denda = etDenda.getText().toString().trim();
        final String dendaBerjalan = etDendaBerjalan.getText().toString().trim();
        final String biayaTagih = etBiayaTagih.getText().toString().trim();

        final String rvbNo = spNoRVB.getSelectedItem().toString();
        TrnRVB selectedRVB = realm.where(TrnRVB.class)
                .equalTo("rvbNo", rvbNo)
                .findFirst();

        if (!Utility.isValidMoney(denda)) {
            etDenda.setError(getString(R.string.error_amount_invalid));
            focusView = etDenda;
            cancel = true;
        }

        if (!Utility.isValidMoney(dendaBerjalan)) {
            etDendaBerjalan.setError(getString(R.string.error_amount_invalid));
            focusView = etDendaBerjalan;
            cancel = true;
        }

        if (!Utility.isValidMoney(biayaTagih)) {
            etBiayaTagih.setError(getString(R.string.error_amount_invalid));
            focusView = etBiayaTagih;
            cancel = true;
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

        if (!Utility.isValidMoney(penerimaan)) {
            etPenerimaan.setError(getString(R.string.error_amount_invalid));
            focusView = etPenerimaan;
            cancel = true;
        } else {
            long penerimaanValue = Long.parseLong(penerimaan);

            if (penerimaanValue > 99999999) {
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
                /* kata pak yoce ga perlu lagi ke ldv details
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
                trnLDVDetails.setFlagToEmrafin("N");
                trnLDVDetails.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnLDVDetails.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnLDVDetails);
*/
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

                    // kata pak yoce, running number rvbno sehari cukup satu saja ga perlu generate terus, besoknya di reset.

                    // generate runningnumber
                    UserConfig userConfig = realm.where(UserConfig.class).findFirst();

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

                    }

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
                trnRVColl.setFlagToEmrafin("N");

                trnRVColl.setPaymentFlag(2L);

                trnRVColl.setCollId(userData.getUser().get(0).getUserId());
                trnRVColl.setOfficeCode(userData.getUser().get(0).getBranchId());
                trnRVColl.setInstNo(Long.parseLong(etAngsuranKe.getText().toString()));
                trnRVColl.setFlagDone("Y");
                trnRVColl.setTransDate(serverDate);
                trnRVColl.setProcessDate(serverDate);

                trnRVColl.setLdvNo(null);

                trnRVColl.setPenaltyAc(Long.valueOf(denda));
                trnRVColl.setDaysIntrAc(Long.valueOf(dendaBerjalan));
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
                Toast.makeText(ActivityPaymentEntri.this, "Database Error", Toast.LENGTH_LONG).show();

            }
        });
    }

}

