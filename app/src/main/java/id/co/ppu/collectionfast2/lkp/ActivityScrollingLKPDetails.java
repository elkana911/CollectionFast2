package id.co.ppu.collectionfast2.lkp;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.payment.entry.ActivityPaymentEntri;
import id.co.ppu.collectionfast2.payment.receive.ActivityPaymentReceive;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollectAddr;
import id.co.ppu.collectionfast2.pojo.trn.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.TrnVehicleInfo;
import id.co.ppu.collectionfast2.util.DataUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.RealmResults;

public class ActivityScrollingLKPDetails extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_LDV_NO = "ldvNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_IS_LKP_INQUIRY = "lkpinquiry";
    public static final String PARAM_LKP_DATE = "lkpDate";
    public static final String PARAM_WORKSTATUS = "customer.workStatus";

    private static final int REQUESTCODE_PAYMENT = 10000;
    private static final int REQUESTCODE_VISITRESULT = 10001;
    private static final int REQUESTCODE_REPO = 10002;

    private String contractNo = null;
    private String ldvNo = null;
    private String collectorId = null;
    private String workStatus = null;
    private boolean isLKPInquiry = false;
    private Date lkpDate = null;

    @BindView(R.id.activity_scrolling_lkpdtl)
    View activityScrollingLkpdtl;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbar_layout;

    @BindView(R.id.etContractNo)
    AutoCompleteTextView etContractNo;

    @BindView(R.id.etPlatform)
    AutoCompleteTextView etPlatform;

    @BindView(R.id.etPhone)
    AutoCompleteTextView etPhone;

    @BindView(R.id.etAddress)
    EditText etAddress;

    @BindView(R.id.etKelurahan)
    AutoCompleteTextView etKelurahan;

    @BindView(R.id.etKecamatan)
    AutoCompleteTextView etKecamatan;

    @BindView(R.id.etTglAkhirByr)
    EditText etTglAkhirByr;

    @BindView(R.id.etNoAngsuranBayar)
    EditText etNoAngsuranBayar;

    @BindView(R.id.etSisaPokok)
    EditText etSisaPokok;

    @BindView(R.id.etLamaHari)
    EditText etLamaHari;

    @BindView(R.id.etAngsuran)
    EditText etAngsuran;

    @BindView(R.id.etTargetTagih)
    EditText etTargetTagih;

    @BindView(R.id.etDenda)
    EditText etDenda;

    @BindView(R.id.etDendaBerjalan)
    EditText etDendaBerjalan;

    @BindView(R.id.etBiayaTagih)
    EditText etBiayaTagih;

    @BindView(R.id.etDanaSosial)
    EditText etDanaSosial;

    @BindView(R.id.radioHome)
    RadioButton radioHome;

    @BindView(R.id.radioOffice)
    RadioButton radioOffice;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_lkpdetails);

        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar_layout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        Typeface font = Typeface.createFromAsset(getAssets(), Utility.FONT_SAMSUNG);
        toolbar_layout.setExpandedTitleTypeface(font);
        toolbar_layout.setCollapsedTitleTypeface(font);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("Who are you ?");
            return;
        }

        etPhone.setMovementMethod(LinkMovementMethod.getInstance());

        this.contractNo = extras.getString(PARAM_CONTRACT_NO);
        this.ldvNo = extras.getString(PARAM_LDV_NO);
        this.collectorId = extras.getString(PARAM_COLLECTOR_ID);
        this.workStatus = extras.getString(PARAM_WORKSTATUS);
        this.isLKPInquiry = extras.getBoolean(PARAM_IS_LKP_INQUIRY);
        this.lkpDate = new Date(extras.getLong(PARAM_LKP_DATE));

        if (this.collectorId == null || this.contractNo == null) {
            throw new RuntimeException("collectorId / contractNo cannot null");
        }

        try {
            TrnLDVDetails dtl = loadDetail(contractNo);
            boolean b = DataUtil.isMasterDataDownloaded(this, this.realm);
            updateButtonsVisibility(dtl);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }


    }

    @OnClick(R.id.radioHome)
    public void onClickRadioHome() {

        String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");
        TrnCollectAddr trnCollectAddr = this.realm.where(TrnCollectAddr.class)
                .equalTo("pk.contractNo", this.contractNo)
                .equalTo("pk.seqNo", 1)
//                .equalTo("createdBy", createdBy)
                .findFirst();

        etAddress.setText(trnCollectAddr == null ? "" : trnCollectAddr.getCollAddr());
    }

    @OnClick(R.id.radioOffice)
    public void onClickRadioOffice() {
        String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");
        TrnCollectAddr trnCollectAddr = this.realm.where(TrnCollectAddr.class)
                .equalTo("pk.contractNo", this.contractNo)
                .equalTo("pk.seqNo", 2)
//                .equalTo("createdBy", createdBy)
                .findFirst();

        etAddress.setText(trnCollectAddr == null ? "-" : trnCollectAddr.getCollAddr());
    }

    private void updateButtonsVisibility(TrnLDVDetails dtl) {

        Button btnPaymentReceive = ButterKnife.findById(this, R.id.btnPaymentReceive);
        Button btnVisitResultEntry = ButterKnife.findById(this, R.id.btnVisitResultEntry);
        Button btnRepoEntry = ButterKnife.findById(this, R.id.btnRepoEntry);
        Button btnChangeAddr = ButterKnife.findById(this, R.id.btnChangeAddr);

        Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
//        UserConfig userConfig = this.realm.where(UserConfig.class).findFirst();

        if (!Utility.isSameDay(new Date(), serverDate)) {
            btnPaymentReceive.setEnabled(false);
            btnVisitResultEntry.setEnabled(false);
            btnRepoEntry.setEnabled(false);
            btnChangeAddr.setEnabled(false);

            showSnackBar("Date changed, Please do Close Batch first");
            return;
        }

        if (isLKPInquiry) {
            btnPaymentReceive.setText("PAYMENT ENTRY");

            btnVisitResultEntry.setVisibility(View.GONE);

            btnRepoEntry.setVisibility(View.GONE);

            btnChangeAddr.setVisibility(View.GONE);
        }

        if (workStatus != null) {
            if (workStatus.equals("W")) {
                // udah bayar
                btnPaymentReceive.setVisibility(View.GONE);

                btnVisitResultEntry.setVisibility(View.GONE);

                btnRepoEntry.setVisibility(View.GONE);

                btnChangeAddr.setVisibility(View.GONE);

            } else if (workStatus.equals("V")) {
                // udah synced
                btnPaymentReceive.setVisibility(View.GONE);

                btnVisitResultEntry.setVisibility(View.GONE);

                btnRepoEntry.setVisibility(View.GONE);
            }
        }

        RealmResults<TrnLDVComments> trnLDVCommentses = this.getLDVComments(realm, ldvNo, contractNo).findAll();

        if (trnLDVCommentses.size() > 0) {
            btnPaymentReceive.setVisibility(View.GONE);
            btnRepoEntry.setVisibility(View.GONE);
            btnVisitResultEntry.setVisibility(View.VISIBLE);
        }

        RealmResults<TrnRVColl> trnRVColls = this.getRVColl(realm, contractNo).findAll();

        if (trnRVColls.size() > 0) {
            btnRepoEntry.setVisibility(View.GONE);
            btnVisitResultEntry.setVisibility(View.GONE);
            btnPaymentReceive.setVisibility(View.VISIBLE);
        }

        RealmResults<TrnRepo> trnRepos = this.getRepo(realm, contractNo).findAll();

        if (trnRepos.size() > 0) {
            btnPaymentReceive.setVisibility(View.GONE);
            btnVisitResultEntry.setVisibility(View.GONE);
            btnRepoEntry.setVisibility(View.VISIBLE);
        }


        int syncAsWhat = DataUtil.isLKPSynced(this.realm, dtl);
        if (syncAsWhat > 0) {
            btnChangeAddr.setVisibility(View.GONE);

            if (syncAsWhat == DataUtil.SYNC_AS_PAYMENT)
                btnPaymentReceive.setEnabled(false);
            else
                btnPaymentReceive.setVisibility(View.GONE);

            if (syncAsWhat == DataUtil.SYNC_AS_REPO)
                btnRepoEntry.setEnabled(false);
            else
                btnRepoEntry.setVisibility(View.GONE);

            if (syncAsWhat == DataUtil.SYNC_AS_VISIT)
                btnVisitResultEntry.setEnabled(false);
            else
                btnVisitResultEntry.setVisibility(View.GONE);
        }
        // cek lagi apa headernya ternyata closed
        if (DataUtil.isLDVHeaderClosed(this.realm, this.collectorId, this.lkpDate)) {
            btnPaymentReceive.setVisibility(View.GONE);
            btnVisitResultEntry.setVisibility(View.GONE);
            btnRepoEntry.setVisibility(View.GONE);
            btnChangeAddr.setVisibility(View.GONE);

            Snackbar.make(activityScrollingLkpdtl, "Sorry, this LKP was Closed.", Snackbar.LENGTH_LONG).show();
        }

    }

    public TrnLDVDetails loadDetail(String contractNo) {

        String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", createdBy)
                .findFirst();

        toolbar_layout.setTitle(dtl.getCustName());

        etContractNo.setText(dtl.getContractNo());
        etPlatform.setText(dtl.getPlatform());

        if (dtl.getAddress() != null) {
            etAddress.setText(dtl.getAddress().getCollAddr());
            etPhone.setText(dtl.getAddress().getCollMobPhone());
            etKelurahan.setText(dtl.getAddress().getCollKel());
            etKecamatan.setText(dtl.getAddress().getCollKec());
        }
        etSisaPokok.setText(Utility.convertLongToRupiah(dtl.getPrincipalOutstanding()));

        if (dtl.getLastPaidDate() != null) {
            etTglAkhirByr.setText(Utility.convertDateToString(dtl.getLastPaidDate(), Utility.DATE_DISPLAY_PATTERN));
        } else
            etTglAkhirByr.setText("Belum Bayar");

        long noAngsuranBayar = (dtl.getInstNo() == null ? 0 : dtl.getInstNo());
        etNoAngsuranBayar.setText(String.valueOf(noAngsuranBayar));

        etLamaHari.setText(String.valueOf(dtl.getDpd()));

        long angsuran = dtl.getMonthInst() == null ? 0 : dtl.getMonthInst();
        etAngsuran.setText(Utility.convertLongToRupiah(angsuran));

        long targetTagih = (dtl.getPrincipalAMBC() == null ? 0 : dtl.getPrincipalAMBC())
                + (dtl.getInterestAMBC() == null ? 0 : dtl.getInterestAMBC());
        etTargetTagih.setText(Utility.convertLongToRupiah(targetTagih));

        long denda = dtl.getPenaltyAMBC() == null ? 0 : dtl.getPenaltyAMBC();
        etDenda.setText(Utility.convertLongToRupiah(denda));

        long dendaBerjalan = dtl.getDaysIntrAmbc() == null ? 0 : dtl.getDaysIntrAmbc();
        etDendaBerjalan.setText(Utility.convertLongToRupiah(dendaBerjalan));

        long biayaTagih = (dtl.getCollectionFee() == null ? 0 : dtl.getCollectionFee());
        etBiayaTagih.setText(Utility.convertLongToRupiah(biayaTagih));

        long danaSosial = (dtl.getDanaSosial() == null ? 0 : dtl.getDanaSosial());
        etDanaSosial.setText(Utility.convertLongToRupiah(danaSosial));

        return dtl;
    }

    @OnClick(R.id.btnPaymentReceive)
    public void onClickPaymentReceive() {

        // should disable when user already do visit result/repo entri
        RealmResults<TrnRepo> trnRepos = this.getRepo(realm, contractNo).findAll();

        if (trnRepos.size() > 0) {
            showSnackBar("This contract available via Repo Entri only");
            return;
        }

        RealmResults<TrnLDVComments> trnLDVCommentses = this.getLDVComments(realm, ldvNo, contractNo).findAll();

        if (trnLDVCommentses.size() > 0) {
            showSnackBar("This contract available via Visit Result only");
            return;
        }

        if (isGPSMandatory(this.realm)) {
            if (!id.co.ppu.collectionfast2.location.Location.isLocationDetected(this)) {
                showSnackBar(getString(R.string.message_no_gps));
                id.co.ppu.collectionfast2.location.Location.pleaseTurnOnGPS(this);
            }
        }

        Intent i = null;
        if (!isLKPInquiry) {
            i = new Intent(this, ActivityPaymentReceive.class);
            i.putExtra(ActivityPaymentReceive.PARAM_CONTRACT_NO, etContractNo.getText().toString());
            i.putExtra(ActivityPaymentReceive.PARAM_LDV_NO, this.ldvNo);
            i.putExtra(ActivityPaymentReceive.PARAM_LKP_DATE, this.lkpDate.getTime());
            i.putExtra(ActivityPaymentReceive.PARAM_COLLECTOR_ID, this.collectorId);
        } else {

            // TODO: harusnya ga bisa cek detail krn asumsinya lkp kmrn udah close batch jd cuma bisa liat
            // kalo mo entry konsumen yg kmrn mau janji bayar hari ini, harusnya masuk ke screen payment entri wkt get lkp hari ini
            RealmResults<TrnContractBuckets> trnContractBucketses = this.realm.where(TrnContractBuckets.class)
                    .equalTo("pk.contractNo", contractNo)
//                .equalTo("createdBy", createdBy)
                    .findAll();

            if (trnContractBucketses.size() < 1) {
                Snackbar.make(activityScrollingLkpdtl, "Sorry, this contract is not available for Payment Entri.", Snackbar.LENGTH_LONG).show();
                return;
            }
            i = new Intent(this, ActivityPaymentEntri.class);
            i.putExtra(ActivityPaymentEntri.PARAM_CONTRACT_NO, etContractNo.getText().toString());
            i.putExtra(ActivityPaymentEntri.PARAM_LDV_NO, this.ldvNo);
            i.putExtra(ActivityPaymentEntri.PARAM_LKP_DATE, this.lkpDate.getTime());
            i.putExtra(ActivityPaymentEntri.PARAM_COLLECTOR_ID, this.collectorId);
        }

        startActivityForResult(i, REQUESTCODE_PAYMENT);

    }

    @OnClick(R.id.btnVisitResultEntry)
    public void onClickVisitResultEntry() {

        // should disable when user already do payment/repo entri
        RealmResults<TrnRepo> trnRepos = this.getRepo(realm, contractNo).findAll();

        if (trnRepos.size() > 0) {
            showSnackBar("This contract available via Repo Entri only");
            return;
        }

        RealmResults<TrnRVColl> trnRVColls = this.getRVColl(realm, contractNo).findAll();

        if (trnRVColls.size() > 0) {
            showSnackBar("This contract available via Payment only");
            return;
        }

        if (isGPSMandatory(this.realm)) {
            if (!id.co.ppu.collectionfast2.location.Location.isLocationDetected(this)) {
                showSnackBar(getString(R.string.message_no_gps));
                id.co.ppu.collectionfast2.location.Location.pleaseTurnOnGPS(this);
            }
        }

        // dibedain based on RPC job (Professional Kolektor)
//        UserData currentUser = (UserData) Storage.getPreference(Storage.KEY_USER, UserData.class);

//        MstUser user = currentUser.getUser().get(0);
        final String userCollType = Storage.getPref(Storage.KEY_USER_COLL_TYPE, null);

        Intent i;
        if (userCollType != null && userCollType.equalsIgnoreCase("RPC")) {
//        if (user.getCollectorType().equalsIgnoreCase("RPC")) {
            i = new Intent(this, ActivityVisitResultRPC.class);
        } else {
            i = new Intent(this, ActivityVisitResult.class);
        }

        i.putExtra(ActivityVisitResult.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityVisitResult.PARAM_COLLECTOR_ID, this.collectorId);
        i.putExtra(ActivityVisitResult.PARAM_LDV_NO, this.ldvNo);

        startActivityForResult(i, REQUESTCODE_VISITRESULT);
    }

    @OnClick(R.id.btnRepoEntry)
    public void onClickRepoEntry() {

        // should disable when user already do payment/visit result
        // cek by createdby MOBCOL
        RealmResults<TrnLDVComments> trnLDVCommentses = this.getLDVComments(realm, ldvNo, contractNo).findAll();

        if (trnLDVCommentses.size() > 0) {
            showSnackBar("This contract available via Visit Result only");
            return;
        }

        RealmResults<TrnRVColl> trnRVColls = this.getRVColl(realm, contractNo).findAll();

        if (trnRVColls.size() > 0) {
            showSnackBar("This contract available via Payment only");
            return;
        }

        if (isGPSMandatory(this.realm)) {
            if (!id.co.ppu.collectionfast2.location.Location.isLocationDetected(this)) {
                showSnackBar(getString(R.string.message_no_gps));
                id.co.ppu.collectionfast2.location.Location.pleaseTurnOnGPS(this);
            }
        }

        Intent i = new Intent(this, ActivityRepoEntry.class);
        i.putExtra(ActivityRepoEntry.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityRepoEntry.PARAM_COLLECTOR_ID, this.collectorId);
        i.putExtra(ActivityRepoEntry.PARAM_LDV_NO, this.ldvNo);

        startActivityForResult(i, REQUESTCODE_REPO);
    }

    @OnClick(R.id.btnVehicleInfo)
    public void onClickVehicleInfo() {

        TrnVehicleInfo trnVehicleInfo = this.realm.where(TrnVehicleInfo.class)
                .equalTo("contractNo", etContractNo.getText().toString())
                .findFirst();

        if (trnVehicleInfo == null) {
            showSnackBar("Sorry, No Vehicle Data found.");
            return;
        }

        Intent i = new Intent(this, ActivityVehicleInfo.class);
        i.putExtra(ActivityVehicleInfo.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        startActivity(i);
    }

    @OnClick(R.id.btnPaymentHist)
    public void onClickPaymentHistory() {
        Intent i = new Intent(this, ActivityPaymentHistory.class);
        i.putExtra(ActivityPaymentHistory.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityPaymentHistory.PARAM_IS_LKP_INQUIRY, isLKPInquiry);
        i.putExtra(ActivityPaymentHistory.PARAM_LKP_DATE, this.lkpDate.getTime());
        startActivity(i);
    }

    @OnClick(R.id.btnChangeAddr)
    public void onClickChangeAddr() {
        Intent i = new Intent(this, ActivityChangeAddress.class);
        i.putExtra(ActivityChangeAddress.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityChangeAddress.PARAM_COLLECTOR_ID, this.collectorId);
        startActivity(i);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUESTCODE_PAYMENT:
                break;
            case REQUESTCODE_VISITRESULT:
                break;
            case REQUESTCODE_REPO:
                break;
        }

        String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class)
                .equalTo("contractNo", contractNo)
                .equalTo("createdBy", createdBy)
                .findFirst();

        updateButtonsVisibility(dtl);
    }

    public void showSnackBar(String message) {
        Snackbar.make(activityScrollingLkpdtl, message, Snackbar.LENGTH_SHORT).show();
    }
}
