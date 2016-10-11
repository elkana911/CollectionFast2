package id.co.ppu.collectionfast2.lkp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserConfig;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.TrnBastbj;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityRepoEntry extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_LDV_NO = "ldvNo";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;

    private Date serverDate;

    @BindView(R.id.activity_repo_entri)
    View activityRepoEntri;

    @BindView(R.id.etContractNo)
    EditText etContractNo;

    @BindView(R.id.etPAL)
    EditText etPAL;

    @BindView(R.id.etTglTransaksi)
    EditText etTglTransaksi;

    @BindView(R.id.etKodeTarik)
    EditText etKodeTarik;

    @BindView(R.id.etKomentar)
    EditText etKomentar;

    @BindView(R.id.radioSTNK)
    RadioButton radioSTNK;

    @BindView(R.id.radioNoSTNK)
    RadioButton radioNoSTNK;

    @BindView(R.id.spBASTK)
    MaterialBetterSpinner spBASTK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repo_entry);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            contractNo = extras.getString(PARAM_CONTRACT_NO);
            collectorId = extras.getString(PARAM_COLLECTOR_ID);
            this.ldvNo = extras.getString(PARAM_LDV_NO);
        }

        if (this.collectorId == null || this.contractNo == null || this.ldvNo == null) {
            throw new RuntimeException("collectorId / ldvNo / contractNo cannot null");
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_repo_entry);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(dtl.getContractNo());
        etPAL.setText(dtl.getPalNo());

        serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();

        if (serverDate == null) {
            Utility.showDialog(this, "Error Date", "Unable to open Repo Entry Form.\nTry to Reset Data or contact the Administrator.");
            return;
        }

        etTglTransaksi.setText(Utility.convertDateToString(serverDate, "dd-MMM-yyyy"));

        TrnLDVHeader header = this.realm.where(TrnLDVHeader.class).findFirst();
        UserConfig userConfig = this.realm.where(UserConfig.class).findFirst();

        RealmResults<TrnBastbj> bastbjs = this.realm.where(TrnBastbj.class).equalTo("bastbjStatus", "OP").findAll();
        BastkAdapter bastkAdapter = new BastkAdapter(this, android.R.layout.simple_spinner_item, bastbjs);
        spBASTK.setAdapter(bastkAdapter);

        TrnRepo trnRepo = this.realm.where(TrnRepo.class)
                .equalTo("contractNo", contractNo)
                .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                .findFirst();

        if (trnRepo != null) {
            etKomentar.setText(trnRepo.getRepoComments());
            etKodeTarik.setText(trnRepo.getRepoNo());
            spBASTK.setText(trnRepo.getBastbjNo());

            radioSTNK.setChecked(trnRepo.getStnkStatus() != null && trnRepo.getStnkStatus().equals("Y"));
            radioNoSTNK.setChecked(trnRepo.getStnkStatus() == null || !trnRepo.getStnkStatus().equals("Y"));

        } else {

            // generate running number
            if (userConfig.getKodeTarikRunningNumber() == null)
                userConfig.setKodeTarikRunningNumber(0L);
            long runningNumber = userConfig.getKodeTarikRunningNumber() + 1;

            /* ga usah di save dulu
            userConfig.setKodeTarikRunningNumber(runningNumber);
            realm.copyToRealmOrUpdate(userConfig);
            */

            etKodeTarik.setText(generateKodeTarik(header.getOfficeCode(), runningNumber));

        }

    }

    private String generateKodeTarik(String officeCode, long runningNumber) {
        StringBuffer sb = new StringBuffer();

        sb.append(officeCode).append(Utility.convertDateToString(serverDate, "MM"))
                .append(Utility.convertDateToString(serverDate, "dd"))
                .append(Utility.leftPad(runningNumber, 3));

        return sb.toString();
    }

    @OnClick(R.id.btnSave)
    public void onClickSave() {
        // reset errors
        etContractNo.setError(null);

        // attempt save
        boolean cancel = false;
        View focusView = null;
        final String contractNo = etContractNo.getText().toString();
        final String bastbjNo = spBASTK.getText().toString();
        final String repoNo = etKodeTarik.getText().toString();
        final String komentar = etKomentar.getText().toString();

        final boolean useStnk = radioSTNK.isChecked();

        if (TextUtils.isEmpty(contractNo)) {
            etContractNo.setError(getString(R.string.error_field_required));
            focusView = etContractNo;
            cancel = true;
        }

        if (TextUtils.isEmpty(repoNo)) {
            etKodeTarik.setError(getString(R.string.error_field_required));
            focusView = etKodeTarik;
            cancel = true;
        }

        if (TextUtils.isEmpty(komentar)) {
            etKomentar.setError(getString(R.string.error_field_required));
            focusView = etKomentar;
            cancel = true;
        }

        if (TextUtils.isEmpty(bastbjNo)) {
            spBASTK.setError(getString(R.string.error_field_required));
            focusView = spBASTK;
            cancel = true;
        }

        if (!TextUtils.isEmpty(komentar)&& etKomentar.getText().length() > 200) {
            etKomentar.setError(getString(R.string.error_value_too_long));
            focusView = etKomentar;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                UserConfig userConfig = realm.where(UserConfig.class).findFirst();
                Date serverDate = realm.where(ServerInfo.class).findFirst().getServerDate();

                String createdBy = "JOB" + Utility.convertDateToString(serverDate, Utility.DATE_DATA_PATTERN);

                SyncTrnRepo trnSync = realm.where(SyncTrnRepo.class)
                        .equalTo("repoNo", repoNo)
                        .equalTo("contractNo", contractNo)
                        .equalTo("createdBy", createdBy)
                        .isNotNull("syncedDate")
                        .findFirst();

                if (trnSync != null) {
                    Snackbar.make(activityRepoEntri, "Data already synced", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                TrnBastbj trnBastbj = realm.where(TrnBastbj.class).equalTo("bastbjNo", bastbjNo).findFirst();
                if (trnBastbj != null) {
                    trnBastbj.setBastbjStatus("CL");
                    trnBastbj.setLastupdateBy(Utility.LAST_UPDATE_BY);
                    trnBastbj.setLastupdateTimestamp(new Date());
                    realm.copyToRealmOrUpdate(trnBastbj);
                }

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

                trnLDVDetails.setLdvFlag("PCU");
                trnLDVDetails.setWorkStatus("V");
                trnLDVDetails.setFlagToEmrafin("N");
                trnLDVDetails.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnLDVDetails.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnLDVDetails);

                // just in case re-entry need to query
                TrnRepo trnRepo = realm.where(TrnRepo.class)
                        .equalTo("lastupdateBy", Utility.LAST_UPDATE_BY)
                        .equalTo("contractNo", contractNo).findFirst();

                if (trnRepo == null) {
                    trnRepo = new TrnRepo();

                    if (userConfig.getKodeTarikRunningNumber() == null)
                        userConfig.setKodeTarikRunningNumber(0L);
                    long runningNumber = userConfig.getKodeTarikRunningNumber() + 1;
                    userConfig.setKodeTarikRunningNumber(runningNumber);
                    realm.copyToRealmOrUpdate(userConfig);

                    trnRepo.setCreatedBy(Utility.LAST_UPDATE_BY);
                    trnRepo.setCreatedTimestamp(new Date());

                }

                trnRepo.setContractNo(contractNo);
                trnRepo.setCustNo(trnLDVDetails.getCustNo());
                trnRepo.setRepoNo(repoNo);
                trnRepo.setBastbjNo(trnBastbj.getBastbjNo());
                trnRepo.setFlagToEmrafin("N");
                trnRepo.setStnkStatus(useStnk ? "Y" : "N");
                trnRepo.setRepoComments(etKomentar.getText().toString());
                trnRepo.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnRepo.setLastupdateTimestamp(new Date());
                realm.copyToRealmOrUpdate(trnRepo);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(ActivityRepoEntry.this, "Repo saved !", Toast.LENGTH_SHORT).show();

            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(ActivityRepoEntry.this, "Database Error !", Toast.LENGTH_LONG).show();

            }
        });
    }

    @OnClick(R.id.btnUploadPicture)
    public void onClickUploadPicture() {
        Intent i = new Intent(this, ActivityUploadPicture.class);
        i.putExtra(ActivityUploadPicture.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityUploadPicture.PARAM_COLLECTOR_ID, this.collectorId);
        i.putExtra(ActivityUploadPicture.PARAM_LDV_NO, this.ldvNo);

        startActivity(i);

    }

    @OnClick(R.id.btnPaymentHist)
    public void onClickPaymentHist() {
        Intent i = new Intent(this, ActivityPaymentHistory.class);
        i.putExtra(ActivityPaymentHistory.PARAM_CONTRACT_NO, etContractNo.getText().toString());

        startActivity(i);
    }

    public class BastkAdapter extends ArrayAdapter<TrnBastbj> {
        private Context ctx;
        private List<TrnBastbj> list;


        public BastkAdapter(Context context, int resource, List<TrnBastbj> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public TrnBastbj getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
            tv.setPadding(10, 10, 10, 10);
            tv.setTextColor(Color.BLACK);
            tv.setText(list.get(position).getBastbjNo());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(this.ctx);
            label.setPadding(10, 10, 10, 10);
            label.setText(list.get(position).getBastbjNo());
            label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }
}
