package id.co.ppu.collectionfast2.lkp;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.location.Location;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.master.MstDelqReasons;
import id.co.ppu.collectionfast2.pojo.master.MstLDVClassifications;
import id.co.ppu.collectionfast2.pojo.master.MstLDVParameters;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVCommentsPK;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityVisitResult extends BasicActivity {

    private DatePickerDialog.OnDateSetListener listenerDateTglJanjiBayar;

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_LDV_NO = "ldvNo";

    private String contractNo = null;
    private String collectorId = null;
    private String ldvNo = null;

    @BindView(R.id.activity_visit_result)
    View activityVisitResult;

    @BindView(R.id.etContractNo)
    EditText etContractNo;

    @BindView(R.id.etJob)
    AutoCompleteTextView etJob;

    @BindView(R.id.etSubJob)
    AutoCompleteTextView etSubJob;

    @BindView(R.id.etTglJanjiBayar)
    EditText etTglJanjiBayar;

    @BindView(R.id.etPotensi)
    EditText etPotensi;

    @BindView(R.id.etBertemuDengan)
    EditText etBertemuDengan;

    @BindView(R.id.etTindakSelanjutnya)
    EditText etTindakSelanjutnya;

    @BindView(R.id.etJanjiBayar)
    EditText etJanjiBayar;

    @BindView(R.id.etKomentar)
    EditText etKomentar;

    @BindView(R.id.spLKPFlags)
    Spinner spLKPFlags;

    @BindView(R.id.spKlasifikasi)
    Spinner spKlasifikasi;

    @BindView(R.id.spAlasan)
    Spinner spAlasan;

    private int getIndex(Spinner spinner, String myString){

        int index = 0;

        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_result);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);
            this.ldvNo = extras.getString(PARAM_LDV_NO);
        }

        if (this.collectorId == null || this.contractNo == null || this.ldvNo == null) {
            throw new RuntimeException("collectorId / ldvNo / contractNo cannot null");
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_visit_result);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(dtl.getContractNo());
        etJob.setText(dtl.getOccupation());
        etSubJob.setText(dtl.getSubOccupation());

        RealmResults<MstLDVParameters> rrLkpParameters = this.realm.where(MstLDVParameters.class).findAll();
        List<MstLDVParameters> ss = this.realm.copyFromRealm(rrLkpParameters);
        LKPParameterAdapter adapterLKPFlag = new LKPParameterAdapter(this, android.R.layout.simple_spinner_item, ss);
        MstLDVParameters hint = new MstLDVParameters();
        hint.setDescription(getString(R.string.spinner_please_select));
        adapterLKPFlag.insert(hint, 0);
        spLKPFlags.setAdapter(adapterLKPFlag);
        spLKPFlags.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MstLDVParameters obj = (MstLDVParameters) adapterView.getItemAtPosition(i);
                etTglJanjiBayar.setVisibility(obj.getLkpFlag() != null && obj.getLkpFlag().equals("PTP") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        RealmResults<MstLDVClassifications> rrLkpKlasifikasi = this.realm.where(MstLDVClassifications.class).findAll();
        List<MstLDVClassifications> lkpKlasifikasi = this.realm.copyFromRealm(rrLkpKlasifikasi);
        KlasifikasiAdapter adapterKlasifikasi = new KlasifikasiAdapter(this, android.R.layout.simple_spinner_item, lkpKlasifikasi);
        MstLDVClassifications hintKlasifikasi = new MstLDVClassifications();
        hintKlasifikasi.setDescription(getString(R.string.spinner_please_select));
        adapterKlasifikasi.insert(hintKlasifikasi, 0);
        spKlasifikasi.setAdapter(adapterKlasifikasi);

        RealmResults<MstDelqReasons> rrAlasan = this.realm.where(MstDelqReasons.class).findAll();
        List<MstDelqReasons> lkpAlasan = this.realm.copyFromRealm(rrAlasan);
        AlasanAdapter adapterAlasan = new AlasanAdapter(this, android.R.layout.simple_spinner_item, lkpAlasan);
        MstDelqReasons hintAlasan = new MstDelqReasons();
        hintAlasan.setDescription(getString(R.string.spinner_please_select));
        adapterAlasan.insert(hintAlasan, 0);
        spAlasan.setAdapter(adapterAlasan);

        listenerDateTglJanjiBayar = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                etTglJanjiBayar.setText(day + " / " + (month+1) + " / " + year);
            }
        };

        // load last save
        TrnLDVComments trnLDVComments = this.getLDVComments(realm, ldvNo, contractNo).findFirst();

        if (trnLDVComments != null) {
            String lkpFlag = dtl.getLdvFlag();
            for(int i = 0; i < adapterLKPFlag.getCount(); i++) {
                if(lkpFlag.equals(adapterLKPFlag.getItem(i).getLkpFlag())){
                    spLKPFlags.setSelection(i);
                    break;
                }
            }


            String classCode = trnLDVComments.getClassCode();

            for(int i = 0; i < adapterKlasifikasi.getCount(); i++) {
                if(classCode.equals(adapterKlasifikasi.getItem(i).getClassCode())){
                    spKlasifikasi.setSelection(i);
                    break;
                }
            }

            String delqCode = trnLDVComments.getDelqCode();
            for(int i = 0; i < adapterAlasan.getCount(); i++) {
                if(delqCode.equals(adapterAlasan.getItem(i).getDelqCode())){
                    spAlasan.setSelection(i);
                    break;
                }
            }/*
            spAlasan.post(new Runnable() {
                @Override
                public void run() {
                    spAlasan.setSelection(2);
                }
            });
              */
            if (trnLDVComments.getPotensi() != null)
                etPotensi.setText(String.valueOf(trnLDVComments.getPotensi()));

            if (trnLDVComments.getPromiseDate() != null)
                etTglJanjiBayar.setText(Utility.convertDateToString(trnLDVComments.getPromiseDate(), "d / M / yyyy"));

            if (trnLDVComments.getPlanPayAmount() != null)
                etJanjiBayar.setText(String.valueOf(trnLDVComments.getPlanPayAmount()));

            etBertemuDengan.setText(trnLDVComments.getWhoMet());
            etTindakSelanjutnya.setText(trnLDVComments.getActionPlan());

            etKomentar.setText(trnLDVComments.getLkpComments());
        }

    }

    @OnClick(R.id.etTglJanjiBayar)
    public void onClickTglJanjiBayar(){
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(this, listenerDateTglJanjiBayar, year, month, day).show();
    }

    @OnClick(R.id.btnSave)
    public void onClickSave() {

        // reset errors
        etPotensi.setError(null);
        etTindakSelanjutnya.setError(null);
        etContractNo.setError(null);
        etBertemuDengan.setError(null);
        etKomentar.setError(null);
        etTglJanjiBayar.setError(null);

        // attempt save
        boolean cancel = false;
        View focusView = null;
        final String contractNo = etContractNo.getText().toString();
        final String potensi = etPotensi.getText().toString();
        final String komentar = etKomentar.getText().toString();

        String sLKPFlags = spLKPFlags.getSelectedItem().toString();
        final MstLDVParameters ldvParameters = realm.where(MstLDVParameters.class).equalTo("description", sLKPFlags).findFirst();
        String sAlasan = spAlasan.getSelectedItem().toString();
        MstDelqReasons delqReasons = realm.where(MstDelqReasons.class).equalTo("description", sAlasan).findFirst();
        String sKlasifikasi = spKlasifikasi.getSelectedItem().toString();
        MstLDVClassifications ldvClassifications = realm.where(MstLDVClassifications.class).equalTo("description", sKlasifikasi).findFirst();

        final String lkpFlag = ldvParameters == null ? "" : ldvParameters.getLkpFlag();
        final String delqCode = delqReasons == null ? "" : delqReasons.getDelqCode();
        final String classCode = ldvClassifications == null ? "" : ldvClassifications.getClassCode();

        if (TextUtils.isEmpty(lkpFlag)) {
            Toast.makeText(this, "Please select LKP Flag", Toast.LENGTH_SHORT).show();
//            spLKPFlags.setError(getString(R.string.error_field_required));
            focusView = spLKPFlags;
            cancel = true;
        } else {
            if (ldvParameters.getLkpFlag().equals("PTP"))
                if (TextUtils.isEmpty(etTglJanjiBayar.getText().toString())) {
                    etTglJanjiBayar.setError(getString(R.string.error_field_required));
                    focusView = etTglJanjiBayar;
                    cancel = true;
                } else {
                    // ga boleh backdate
                    Date datePTP = Utility.convertStringToDate(etTglJanjiBayar.getText().toString(), "d / M / yyyy");

                    if (datePTP == null) {
                        etTglJanjiBayar.setError(getString(R.string.error_invalid_date));
                        focusView = etTglJanjiBayar;
                        cancel = true;
                    } else {
                        long dateDiff = Utility.getDateDiff(new Date(), datePTP, TimeUnit.DAYS);
                        if (dateDiff < 0) {
                            Toast.makeText(this, getString(R.string.error_ptp_date), Toast.LENGTH_SHORT).show();
                            etTglJanjiBayar.setError(getString(R.string.error_ptp_date));
                            focusView = etTglJanjiBayar;
                            cancel = true;
                        }
                    }
                }
        }

        if (TextUtils.isEmpty(delqCode)) {
            Toast.makeText(this, "Please select Alasan", Toast.LENGTH_SHORT).show();
            focusView = spAlasan;
            cancel = true;
        }

        if (TextUtils.isEmpty(classCode)) {
            Toast.makeText(this, "Please select Klasifikasi", Toast.LENGTH_SHORT).show();
            focusView = spKlasifikasi;
            cancel = true;
        }

        if (!TextUtils.isEmpty(etBertemuDengan.getText()) && etBertemuDengan.getText().length() > 100) {
            etBertemuDengan.setError(getString(R.string.error_value_too_long));
            focusView = etBertemuDengan;
            cancel = true;
        }

        if (!TextUtils.isEmpty(etTindakSelanjutnya.getText()) && etTindakSelanjutnya.getText().length() > 500) {
            etTindakSelanjutnya.setError(getString(R.string.error_value_too_long));
            focusView = etTindakSelanjutnya;
            cancel = true;
        }

        if (!TextUtils.isEmpty(etKomentar.getText()) && etKomentar.getText().length() > 2000) {
            etKomentar.setError(getString(R.string.error_value_too_long));
            focusView = etKomentar;
            cancel = true;
        }

        if (!TextUtils.isEmpty(potensi)) {
            int potensiValue = Integer.parseInt(potensi);
            if (potensiValue < 0 || potensiValue > 100) {
                etPotensi.setError(getString(R.string.error_amount_invalid));
                focusView = etPotensi;
                cancel = true;
            }
        }else{
            etPotensi.setError(getString(R.string.error_field_required));
            focusView = etPotensi;
            cancel = true;
        }

        if (TextUtils.isEmpty(komentar)) {
            etKomentar.setError(getString(R.string.error_field_required));
            focusView = etKomentar;
            cancel = true;
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

        double[] gps = Location.getGPS(this);
        final String latitude = String.valueOf(gps[0]);
        final String longitude = String.valueOf(gps[1]);

        if (latitude.equals("0.0") && longitude.equals("0.0")) {
            Snackbar.make(activityVisitResult, "Unable to get location. Please turn on GPS.", Snackbar.LENGTH_LONG).show();
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
                    Snackbar.make(activityVisitResult, "Data already synced", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                TrnLDVDetails trnLDVDetails = realm.where(TrnLDVDetails.class)
                        .equalTo("contractNo", contractNo)
                        .findFirst();
                trnLDVDetails.setLdvFlag(lkpFlag);
                trnLDVDetails.setWorkStatus("V");
                trnLDVDetails.setFlagToEmrafin("N");
                trnLDVDetails.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnLDVDetails.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnLDVDetails);


                // just in case re-entry need to query
                TrnLDVComments trnLDVComments = getLDVComments(realm, ldvNo, contractNo).findFirst();

                // TODO: krn ga ada primary key maka harus di delete dulu ?
                if (trnLDVComments != null) {
                }

                if (trnLDVComments == null) {
                    TrnLDVCommentsPK pk = new TrnLDVCommentsPK();
                    pk.setSeqNo(1L);
                    pk.setLdvNo(ldvNo);
                    pk.setContractNo(contractNo);

                    trnLDVComments = new TrnLDVComments();
                    trnLDVComments.setPk(pk);
                }

                trnLDVComments.setDelqCode(delqCode);
                trnLDVComments.setLkpComments(etKomentar.getText().toString());
                trnLDVComments.setActionPlan(etTindakSelanjutnya.getText().toString());
                trnLDVComments.setClassCode(classCode);
                trnLDVComments.setFlagToEmrafin("N");

                if (Utility.isNumeric(etJanjiBayar.getText().toString()))
                    trnLDVComments.setPlanPayAmount(Long.parseLong(etJanjiBayar.getText().toString()));

                if (Utility.isNumeric(potensi))
                    trnLDVComments.setPotensi(Long.parseLong(etPotensi.getText().toString()));

                trnLDVComments.setWhoMet(etBertemuDengan.getText().toString());

                if (lkpFlag.equals("PTP")) {
                    if (!TextUtils.isEmpty(etTglJanjiBayar.getText().toString()))
                        trnLDVComments.setPromiseDate(Utility.convertStringToDate(etTglJanjiBayar.getText().toString(), "d / M / yyyy") );
                }

                trnLDVComments.setLatitude(latitude);
                trnLDVComments.setLongitude(longitude);
                /*
                trnLDVComments.setOcptCode();
                trnLDVComments.setOcptCodeSub();
                */
                trnLDVComments.setCreatedBy(Utility.LAST_UPDATE_BY);
                trnLDVComments.setCreatedTimestamp(new Date());
                trnLDVComments.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnLDVComments.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnLDVComments);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(ActivityVisitResult.this, "Data saved !", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                StringBuffer message2 = new StringBuffer();
                message2.append("contractNo=").append(contractNo)
                        .append(",potensi=").append(potensi)
                        .append(",lkpFlag=").append(lkpFlag)
                        .append(",delqCode=").append(delqCode)
                        .append(",classCode=").append(classCode)
                        .append(",classCode=").append(classCode)
                        .append(",serverDate=").append(serverDate)
                ;

                NetUtil.syncLogError(getBaseContext(), realm, collectorId, "VisitResult", error.getMessage(), message2.toString());
                Toast.makeText(ActivityVisitResult.this, "Database Error !", Toast.LENGTH_LONG).show();
                Snackbar.make(activityVisitResult, error.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @OnClick(R.id.btnUploadPicture)
    public void onClickUploadPic() {
        /*
        Intent i = new Intent(this, ActivityUploadPicture.class);
        i.putExtra(ActivityUploadPicture.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityUploadPicture.PARAM_COLLECTOR_ID, this.collectorId);
        i.putExtra(ActivityUploadPicture.PARAM_LDV_NO, this.ldvNo);
        */
        Intent i = new Intent(this, ActivityUploadPictureGeo.class);
        i.putExtra(ActivityUploadPictureGeo.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        i.putExtra(ActivityUploadPictureGeo.PARAM_COLLECTOR_ID, this.collectorId);
        i.putExtra(ActivityUploadPictureGeo.PARAM_LDV_NO, this.ldvNo);

        startActivity(i);

    }

    @OnClick(R.id.btnChangeAddr)
    public void onClickChangeAddr() {
        Intent i = new Intent(this, ActivityChangeAddress.class);
        i.putExtra(ActivityChangeAddress.PARAM_CONTRACT_NO, etContractNo.getText().toString());
        startActivity(i);
    }

    public class KlasifikasiAdapter extends ArrayAdapter<MstLDVClassifications> {
        private Context ctx;
        private List<MstLDVClassifications> list;


        public KlasifikasiAdapter(Context context, int resource, List<MstLDVClassifications> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MstLDVClassifications getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
            tv.setPadding(10, 10, 10, 10);
            tv.setTextColor(Color.BLACK);
            tv.setText(list.get(position).getDescription());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(this.ctx);
            label.setPadding(10,10,10,10);
//            label.setTextColor(Color.BLACK);
            label.setText(list.get(position).getDescription());
            label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public class LKPParameterAdapter extends ArrayAdapter<MstLDVParameters> {
        private Context ctx;
        private List<MstLDVParameters> list;


        public LKPParameterAdapter(Context context, int resource, List<MstLDVParameters> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MstLDVParameters getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
            tv.setPadding(10,10,10,10);
            tv.setTextColor(Color.BLACK);
            tv.setText(list.get(position).getDescription());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
            tv.setPadding(10,10,10,10);
//            tv.setTextColor(Color.BLACK);
            tv.setText(list.get(position).getDescription());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public class AlasanAdapter extends ArrayAdapter<MstDelqReasons> {
        private Context ctx;
        private List<MstDelqReasons> list;


        public AlasanAdapter(Context context, int resource, List<MstDelqReasons> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MstDelqReasons getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
            tv.setPadding(10,10,10,10);
            tv.setTextColor(Color.BLACK);
            tv.setSingleLine(false);
            tv.setText(list.get(position).getDescription());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            label.setTextColor(Color.BLACK);
            tv.setPadding(10,10,10,10);
            tv.setSingleLine(false);
            tv.setText(list.get(position).getDescription());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return tv;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

}
