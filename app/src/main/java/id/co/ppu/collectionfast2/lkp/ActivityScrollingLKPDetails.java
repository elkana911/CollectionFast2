package id.co.ppu.collectionfast2.lkp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.payment.ActivityPaymentEntry;
import id.co.ppu.collectionfast2.pojo.MstUser;
import id.co.ppu.collectionfast2.pojo.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;

public class ActivityScrollingLKPDetails extends AppCompatActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_LDV_NO = "ldvNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_IS_LKP_INQUIRY = "lkpinquiry";

    private Realm realm;

    private String contractNo = null;
    private String ldvNo = null;
    private String collectorId = null;
    private boolean isLKPInquiry = false;

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbar_layout;

    @BindView(R.id.etContractNo)
    AutoCompleteTextView etContractNo;

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

    @BindView(R.id.etBiayaTagih)
    EditText etBiayaTagih;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling_lkpdetails);

        ButterKnife.bind(this);

        this.realm = Realm.getDefaultInstance();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
//            getSupportActionBar().setTitle(R.string.title_activity_lkp_form);
//            getSupportActionBar().setSubtitle("Mobile Collection");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("Who are you ?");
            return;
        }

        contractNo = extras.getString(PARAM_CONTRACT_NO);
        ldvNo = extras.getString(PARAM_LDV_NO);
        collectorId = extras.getString(PARAM_COLLECTOR_ID);
        isLKPInquiry = extras.getBoolean(PARAM_IS_LKP_INQUIRY);

        try {
            loadDetail(contractNo);
        } catch (Exception e) {
            e.printStackTrace();
            finish();
        }

        if (isLKPInquiry) {
            Button btnPaymentReceive = ButterKnife.findById(this, R.id.btnPaymentReceive);
            btnPaymentReceive.setText("PAYMENT ENTRY");

            Button btnVisitResultEntry = ButterKnife.findById(this, R.id.btnVisitResultEntry);
            btnVisitResultEntry.setVisibility(View.GONE);

            Button btnRepoEntry = ButterKnife.findById(this, R.id.btnRepoEntry);
            btnRepoEntry.setVisibility(View.GONE);

            Button btnChangeAddr = ButterKnife.findById(this, R.id.btnChangeAddr);
            btnChangeAddr.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void loadDetail(String contractNo) {

        if (this.realm == null) {
            return;
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

//        setTitle(dtl.getCustName());
        toolbar_layout.setTitle(dtl.getCustName());

        etContractNo.setText(dtl.getContractNo());

        if (dtl.getAddress() != null) {
            etAddress.setText(dtl.getAddress().getCollAddr());
            etPhone.setText(dtl.getAddress().getCollMobPhone());
            etKelurahan.setText(dtl.getAddress().getCollKel());
            etKecamatan.setText(dtl.getAddress().getCollKec());
        }
        etSisaPokok.setText(Utility.convertLongToRupiah(dtl.getPrincipalOutstanding()));

//        long count = this.realm.where(TrnContractBuckets.class).count();

        TrnContractBuckets contractBuckets = this.realm.where(TrnContractBuckets.class).equalTo("pk.contractNo", contractNo).findFirst();
        if (contractBuckets == null) {
            Toast.makeText(ActivityScrollingLKPDetails.this, "contractBuckets null for " + contractNo, Toast.LENGTH_SHORT).show();
            /*
            etTglAkhirByr.setText("<Error>");
            etNoAngsuranBayar.setText("<Error>");
            etLamaHari.setText("<Error>");
            etAngsuran.setText("<Error>");
            etTargetTagih.setText("<Error>");
            */
            return;
        }

        if (contractBuckets.getPaidDate() != null) {
            etTglAkhirByr.setText(Utility.convertDateToString(contractBuckets.getPaidDate(), "dd-MMM-yyyy"));
        }else
            etTglAkhirByr.setText("Belum Bayar");

        long noAngsuranBayar = (contractBuckets.getOvdInstNo() == null ? 0 : contractBuckets.getOvdInstNo()) -1;
        etNoAngsuranBayar.setText(String.valueOf(noAngsuranBayar));
        etLamaHari.setText(String.valueOf(contractBuckets.getDpd()));

        long angsuran = (contractBuckets.getPrncAmt() == null ? 0 : contractBuckets.getPrncAmt())
                + (contractBuckets.getIntrAmt() == null ? 0 : contractBuckets.getIntrAmt());
        etAngsuran.setText(Utility.convertLongToRupiah(angsuran));

        long targetTagih = (contractBuckets.getPrncAMBC() == null ? 0 : contractBuckets.getPrncAMBC())
                + (contractBuckets.getIntrAMBC() == null ? 0 : contractBuckets.getIntrAMBC());
        etTargetTagih.setText(Utility.convertLongToRupiah(targetTagih));

        etDenda.setText(Utility.convertLongToRupiah(dtl.getPenaltyAMBC()));
        etBiayaTagih.setText(Utility.convertLongToRupiah(10000L));
    }

    @OnClick(R.id.btnPaymentReceive)
    public void onClickPaymentReceive() {

        Intent i = new Intent(this, ActivityPaymentEntry.class);

        i.putExtra(ActivityPaymentEntry.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityPaymentEntry.PARAM_LDV_NO, this.ldvNo);
        if (!isLKPInquiry) {
            i.putExtra(ActivityPaymentEntry.PARAM_TITLE, "Payment Receive");
        }else{

        }

        startActivity(i);
    }

    @OnClick(R.id.btnVisitResultEntry)
    public void onClickVisitResultEntry() {

        // dibedain based on RPC job (Professional Kolektor)
        UserData currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        MstUser user = currentUser.getUser().get(0);

        Intent i;
        if (user.getCollectorType().equalsIgnoreCase("RPC")) {
            i = new Intent(this, ActivityVisitResultRPC.class);
        } else {
            i = new Intent(this, ActivityVisitResult.class);
        }

        i.putExtra(ActivityVisitResult.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityVisitResult.PARAM_LDV_NO, this.ldvNo);

        startActivity(i);
    }
    @OnClick(R.id.btnRepoEntry)
    public void onClickRepoEntry() {
        Intent i = new Intent(this, ActivityRepoEntry.class);
        i.putExtra(ActivityVisitResult.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        startActivity(i);
    }

    @OnClick(R.id.btnVehicleInfo)
    public void onClickVehicleInfo() {
        Intent i = new Intent(this, ActivityVehicleInfo.class);
        i.putExtra(ActivityVehicleInfo.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        startActivity(i);
    }

    @OnClick(R.id.btnPaymentHist)
    public void onClickPaymentHistory() {
        Intent i = new Intent(this, ActivityPaymentHistory.class);
        i.putExtra(ActivityPaymentHistory.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        startActivity(i);
    }
    @OnClick(R.id.btnChangeAddr)
    public void onClickChangeAddr() {
        Intent i = new Intent(this, ActivityChangeAddress.class);
        i.putExtra(ActivityChangeAddress.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityChangeAddress.PARAM_COLLECTOR_ID, this.collectorId);
        startActivity(i);
    }

}
