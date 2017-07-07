package id.co.ppu.collectionfast2.lkp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.trn.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityDetailsLKPSummary extends BasicActivity {

    public static final String PARAM_COLLECTOR_ID = "collector.id";

    private String collectorId = null;

    @BindView(R.id.etNoLKP)
    EditText etNoLKP;

    @BindView(R.id.etTglLKP)
    EditText etTglLKP;

    @BindView(R.id.table)
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details_lkpsummary);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);
        }

//        UserData currentUser = (UserData) Storage.getPreference(Storage.KEY_USER, UserData.class);
        final String userFullName = Storage.getPref(Storage.KEY_USER_FULLNAME, null);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_details_lkp);
            getSupportActionBar().setSubtitle(userFullName);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
        final String createdBy = "JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd");

        TrnLDVHeader header = this.realm.where(TrnLDVHeader.class)
                .equalTo("collCode", this.collectorId)
                .equalTo("createdBy", createdBy)
                .findFirst();

        if (header == null) {
            return;
        }

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        etNoLKP.setText(header.getLdvNo());

        etTglLKP.setText(DateUtils.formatDateTime(this, serverDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));

        // clear table
        tableLayout.removeAllViews();
        // create header
        TableRow row_header = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_details_lkp_summary, null);

        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_no), "No.");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_no_contract), "No Contract");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_is_lkp), "isLKP");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_lkp_flag), "Flag");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_penerimaan), "Penerimaan");

        tableLayout.addView(row_header);

        // add rows
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);

                Realm r = null;

                try {
                    r = Realm.getDefaultInstance();
                    //populate
                    RealmResults<TrnLDVDetails> _buffer = r.where(TrnLDVDetails.class)
                            .equalTo("pk.ldvNo", etNoLKP.getText().toString())
                            .equalTo("workStatus", "V")
                            .equalTo("createdBy", createdBy)
                            .findAll();

                    int rowCounter = 0;
                    for (int i = 0; i < _buffer.size(); i++) {

                        TrnLDVDetails ldvDetails = _buffer.get(i);

                        TableRow row = (TableRow) LayoutInflater.from(ActivityDetailsLKPSummary.this).inflate(R.layout.row_details_lkp_summary, null);

                        // received amount dr rvcoll
                        TrnRVColl rvColl = r.where(TrnRVColl.class)
                                .equalTo("contractNo", ldvDetails.getContractNo())
                                .equalTo("collId", collectorId)
                                .equalTo("paymentFlag", 1L)
                                .findFirst();

                        if (rvColl != null)
                            ((TextView) row.findViewById(R.id.attrib_penerimaan)).setText(String.valueOf(rvColl.getReceivedAmount()));
                        else
                            ((TextView) row.findViewById(R.id.attrib_penerimaan)).setText(null);

                        // visit result

                        // repo

                        ((TextView) row.findViewById(R.id.attrib_no)).setText(String.valueOf(rowCounter + 1));

                        ((TextView) row.findViewById(R.id.attrib_no_contract)).setText(ldvDetails.getContractNo());

                        ((TextView) row.findViewById(R.id.attrib_is_lkp)).setText("Y");

                        ((TextView) row.findViewById(R.id.attrib_lkp_flag)).setText(ldvDetails.getLdvFlag());

                        rowCounter += 1;

                        tableLayout.addView(row);
                    }

                    // tambah row dr contract buckets
                    RealmResults<TrnContractBuckets> buckets = r.where(TrnContractBuckets.class)
                            .equalTo("collectorId", collectorId)
                            .findAll();
                    for (int i = 0; i < buckets.size(); i++) {
                        TrnContractBuckets contractBuckets = buckets.get(i);

                        if (contractBuckets == null)
                            continue;

                        TrnRVColl rvColl = r.where(TrnRVColl.class)
                                .equalTo("contractNo", contractBuckets.getPk().getContractNo())
                                .equalTo("collId", collectorId)
                                .equalTo("paymentFlag", 2L)
                                .findFirst();

                        if (rvColl == null)
                            continue;

                        if (!contractBuckets.getPk().getContractNo().equals(rvColl.getContractNo()))
                            continue;

                        rowCounter += 1;

                        TableRow row = (TableRow) LayoutInflater.from(ActivityDetailsLKPSummary.this).inflate(R.layout.row_details_lkp_summary, null);

                        ((TextView) row.findViewById(R.id.attrib_no)).setText(String.valueOf(rowCounter));

                        ((TextView) row.findViewById(R.id.attrib_no_contract)).setText(contractBuckets.getPk().getContractNo());

                        ((TextView) row.findViewById(R.id.attrib_is_lkp)).setText(null);

                        ((TextView) row.findViewById(R.id.attrib_lkp_flag)).setText("COL");

                        if (rvColl != null)
                            ((TextView) row.findViewById(R.id.attrib_penerimaan)).setText(String.valueOf(rvColl.getReceivedAmount()));
                        else
                            ((TextView) row.findViewById(R.id.attrib_penerimaan)).setText(null);

                        tableLayout.addView(row);
                    }

                } finally {

                    if (r != null) {
                        r.close();
                    }

                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }

                }

            }
        }.execute();

    }

    private void setTableHeader(TextView textView, String text) {
        textView.setText(text);
        if (Build.VERSION.SDK_INT < 23) {
            textView.setTextAppearance(this, android.R.style.TextAppearance_Small);
        } else {
            textView.setTextAppearance(android.R.style.TextAppearance_Small);
        }
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        textView.setBackgroundColor(Color.LTGRAY);

    }
}
