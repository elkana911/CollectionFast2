package id.co.ppu.collectionfast2.lkp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;

public class ActivityVisitResultRPC extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_LDV_NO = "ldvNo";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;

    @BindView(R.id.activity_visit_result_rpc)
    View activityVisitResultRPC;

    @BindView(R.id.etContractNo)
    EditText etContractNo;

    @BindView(R.id.etTglTransaksi)
    EditText etTglTransaksi;

    @BindView(R.id.etKomentar)
    EditText etKomentar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_result_rpc);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);
            this.ldvNo = extras.getString(PARAM_LDV_NO);
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_visit_result);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(dtl.getContractNo());
//        etTglTransaksi.setText(dtl.getContractNo());
//        etKomentar.setText(dtl.getContractNo());

    }

    @OnClick(R.id.btnSave)
    public void onClickSave(){
        // reset errors
        etContractNo.setError(null);

        // attempt save
        boolean cancel = false;
        View focusView = null;
        final String contractNo = etContractNo.getText().toString();

        if (TextUtils.isEmpty(contractNo)) {
            etContractNo.setError(getString(R.string.error_field_required));
            focusView = etContractNo;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
            return;
        }

        final Date serverDate = realm.where(ServerInfo.class).findFirst().getServerDate();
        final UserData userData = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);
        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {


                String createdBy = "JOB" + Utility.convertDateToString(serverDate, Utility.DATE_DATA_PATTERN);

                SyncTrnLDVComments trnSync = realm.where(SyncTrnLDVComments.class)
                        .equalTo("ldvNo", ldvNo)
                        .equalTo("seqNo", 1L)
                        .equalTo("contractNo", contractNo)
                        .equalTo("createdBy", createdBy)
                        .isNotNull("syncedDate")
                        .findFirst();

                if (trnSync != null) {
                    Snackbar.make(activityVisitResultRPC, "Data already synced", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                TrnLDVDetails trnLDVDetails = realm.where(TrnLDVDetails.class)
                                                    .equalTo("pk.ldvNo", contractNo)
                                                    .findFirst();
                // TODO: ga jelas krn tidak ada tampilan LDVStatus
                /*
                trnLDVDetails.setLdvFlag();
                */
                trnLDVDetails.setWorkStatus("V");
                trnLDVDetails.setLastupdateBy(Utility.LAST_UPDATE_BY);
//                realm.copyToRealm(trnLDVDetails);

                TrnLDVComments trnLDVComments = new TrnLDVComments();
//                trnLDVComments.setDelqCode(delqCode);
                trnLDVComments.setLkpComments(etKomentar.getText().toString());
// TODO: rest of the fields
                /*
                trnLDVComments.setContractNo();
                trnLDVComments.setActionPlan();
                trnLDVComments.setClassCode();
                trnLDVComments.setLatitude();
                trnLDVComments.setLongitude();
                trnLDVComments.setOcptCode();
                trnLDVComments.setOcptCodeSub();
                trnLDVComments.setPk();
                trnLDVComments.setPlanPayAmount();
                trnLDVComments.setPotensi();
                trnLDVComments.setPromiseDate();
                trnLDVComments.setWhoMet();
                */
                trnLDVComments.setCreatedBy(Utility.LAST_UPDATE_BY);
                trnLDVComments.setLastupdateBy(Utility.LAST_UPDATE_BY);
//                realm.copyToRealm(trnLDVComments);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(ActivityVisitResultRPC.this, "Data saved !", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(ActivityVisitResultRPC.this, "Database Error !", Toast.LENGTH_LONG).show();
            }
        });

    }

    @OnClick(R.id.btnUploadPicture)
    public void onClickUploadPicture(){
        Intent i = new Intent(this, ActivityUploadPictureGeo.class);
        i.putExtra(ActivityUploadPictureGeo.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityUploadPictureGeo.PARAM_COLLECTOR_ID, this.collectorId);
        i.putExtra(ActivityUploadPictureGeo.PARAM_LDV_NO, this.ldvNo);
        startActivity(i);

    }
}
