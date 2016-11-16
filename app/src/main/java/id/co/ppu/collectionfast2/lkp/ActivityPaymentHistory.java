package id.co.ppu.collectionfast2.lkp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.trn.HistInstallments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

public class ActivityPaymentHistory extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_IS_LKP_INQUIRY = "lkpinquiry";
    public static final String PARAM_LKP_DATE = "lkpDate";

    private String contractNo = null;
    private boolean isLKPInquiry = false;
    private Date lkpDate = null;

    @BindView(R.id.etContractNo)
    EditText etContractNo;

    @BindView(R.id.etAngsuran)
    EditText etAngsuran;

    @BindView(R.id.etTenor)
    EditText etTenor;

    @BindView(R.id.table)
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_history);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            contractNo = extras.getString(PARAM_CONTRACT_NO);
            isLKPInquiry = extras.getBoolean(PARAM_IS_LKP_INQUIRY);
            long lkpdate = extras.getLong(PARAM_LKP_DATE);
            this.lkpDate = new Date(lkpdate);
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();
//        TrnContractBuckets contractBuckets = this.realm.where(TrnContractBuckets.class).equalTo("pk.contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_payment_history);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(dtl.getContractNo());

        if (this.lkpDate == null) {
            this.lkpDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
        }

//        String createdBy = "JOB" + Utility.convertDateToString(this.lkpDate, "yyyyMMdd");
        long total = this.realm.where(HistInstallments.class).count();

        RealmResults<HistInstallments> listInstallments = this.realm.where(HistInstallments.class)
                .equalTo("pk.contractNo", this.contractNo)
//                .equalTo("createdBy", createdBy)  ga perlu kata felipe
                .findAll();

        if (listInstallments.size() < 1) {
            return;
        }
/*
        List<HistInstallments> listInstallments = this.realm.copyFromRealm(list);

        // sort by no installments
        Collections.sort(listInstallments, new Comparator<HistInstallments>() {
            @Override
            public int compare(HistInstallments t1, HistInstallments t2) {
                if (t1.getPk().getInstNo() > t2.getPk().getInstNo())
                    return 1;
                else if (t1.getPk().getInstNo() < t2.getPk().getInstNo()) {
                    return -1;
                }else
                    return 0;
            }
        });

        */
        // after sort you can get max
        etTenor.setText(String.valueOf(listInstallments.get(listInstallments.size() -1).getPk().getInstNo()));

        /*
        boolean isLunas = true;
        long angsuranTertagih = 0;
        for (HistInstallments obj : listInstallments) {
            if (obj.getPaidDate() == null) {
                isLunas = false;

                angsuranTertagih = (obj.getPrncAmtDtlCust() == null ? 0 : obj.getPrncAmtDtlCust())
                        + (obj.getIntrAmtDtlAr() == null ? 0 : obj.getIntrAmtDtlAr());

                break;
            }
        }
        */

//        HistInstallments histInstallments = this.realm.where(HistInstallments.class).equalTo("pk.contractNo", contractNo).findFirst();
        /*if (isLunas) {
            etAngsuran.setText("LUNAS");
        } else {
            etAngsuran.setText(Utility.convertLongToRupiah(angsuranTertagih));
        }*/

        etAngsuran.setText(Utility.convertLongToRupiah(dtl.getMonthInst()));

        /*
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                loadTable();
            }
        });
        // Start the thread
        runOnUiThread(t);
        */

        loadTable();
//        loadTable(listInstallments);
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


    private void loadTable() {
        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        // clear table
        tableLayout.removeAllViews();
        // create header
        TableRow row_header = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_payment_history, null);

        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_no), "No.");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_due_date), "Due date");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_paid_date), "Paid date");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_ovd_days), "OVD days");

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
                try{
                    r = Realm.getDefaultInstance();

                    RealmResults<HistInstallments> list = r.where(HistInstallments.class)
                            .equalTo("pk.contractNo", contractNo)
                            .findAll();

                    for (int i = 0; i < list.size(); i++) {

                        HistInstallments obj = r.copyFromRealm(list.get(i));

                        TableRow row = (TableRow) LayoutInflater.from(ActivityPaymentHistory.this).inflate(R.layout.row_payment_history, null);
                        ((TextView) row.findViewById(R.id.attrib_no)).setText(String.valueOf(i + 1));
//            ((TextView) row.findViewById(R.id.attrib_due_date)).setText(Utility.convertDateToString(new Date(), "dd/MM/yyyy"));
                        ((TextView) row.findViewById(R.id.attrib_due_date)).setText( Utility.convertDateToString(obj.getDueDate(), "dd/MM/yyyy"));

                        TextView tvPaidDate = ButterKnife.findById(row, R.id.attrib_paid_date);
                        if (obj.getPaidDate() != null) {
                            tvPaidDate.setText(Utility.convertDateToString(obj.getPaidDate(), "dd/MM/yyyy"));
                        }else
                            tvPaidDate.setText(null);

//            if (i > 9) {
//                paidDate.setTextColor(Color.RED);
//            }

                        TextView tvOvdDays = ButterKnife.findById(row, R.id.attrib_ovd_days);

                        if (obj.getPaidDate() != null) {
                            long ovdDays = Utility.getDateDiff(obj.getDueDate(), obj.getPaidDate(), TimeUnit.DAYS);
                            tvOvdDays.setText(ovdDays < 1 ? "0" : String.valueOf(ovdDays));

                            if (ovdDays > 0) {
                                tvOvdDays.setTextColor(Color.RED);
                                tvPaidDate.setTextColor(Color.RED);
                            }

                        } else {

                        }
                        tableLayout.addView(row);
                    }

                }finally {
                    if (r != null)
                        r.close();

                    if (mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                }

            }
        }.execute();

        /*
        RealmResults<HistInstallments> list = this.realm.where(HistInstallments.class)
                .equalTo("pk.contractNo", this.contractNo)
                .findAll();

        for (int i = 0; i < list.size(); i++) {

            HistInstallments obj = this.realm.copyFromRealm(list.get(i));

            TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_payment_history, null);
            ((TextView) row.findViewById(R.id.attrib_no)).setText(String.valueOf(i + 1));
//            ((TextView) row.findViewById(R.id.attrib_due_date)).setText(Utility.convertDateToString(new Date(), "dd/MM/yyyy"));
            ((TextView) row.findViewById(R.id.attrib_due_date)).setText( Utility.convertDateToString(obj.getDueDate(), "dd/MM/yyyy"));

            TextView tvPaidDate = ButterKnife.findById(row, R.id.attrib_paid_date);
            if (obj.getPaidDate() != null) {
                tvPaidDate.setText(Utility.convertDateToString(obj.getPaidDate(), "dd/MM/yyyy"));
            }else
                tvPaidDate.setText(null);

//            if (i > 9) {
//                paidDate.setTextColor(Color.RED);
//            }

            TextView tvOvdDays = ButterKnife.findById(row, R.id.attrib_ovd_days);

            if (obj.getPaidDate() != null) {
                long ovdDays = Utility.getDateDiff(obj.getDueDate(), obj.getPaidDate(), TimeUnit.DAYS);
                tvOvdDays.setText(ovdDays < 1 ? "0" : String.valueOf(ovdDays));

                if (ovdDays > 0) {
                    tvOvdDays.setTextColor(Color.RED);
                    tvPaidDate.setTextColor(Color.RED);
                }

            } else {

            }



            tableLayout.addView(row);
        }
        */
    }

    private void loadTable(List<HistInstallments> list) {

        // clear table
        tableLayout.removeAllViews();
        // create header
        TableRow row_header = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_payment_history, null);

        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_no), "No.");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_due_date), "Due date");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_paid_date), "Paid date");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_ovd_days), "OVD days");

        tableLayout.addView(row_header);
        // add rows

        for (int i = 0; i < list.size(); i++) {

            HistInstallments obj = list.get(i);

            TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_payment_history, null);
            ((TextView) row.findViewById(R.id.attrib_no)).setText(String.valueOf(i + 1));
//            ((TextView) row.findViewById(R.id.attrib_due_date)).setText(Utility.convertDateToString(new Date(), "dd/MM/yyyy"));
            ((TextView) row.findViewById(R.id.attrib_due_date)).setText( Utility.convertDateToString(obj.getDueDate(), "dd/MM/yyyy"));

            TextView tvPaidDate = ButterKnife.findById(row, R.id.attrib_paid_date);
            if (obj.getPaidDate() != null) {
                tvPaidDate.setText(Utility.convertDateToString(obj.getPaidDate(), "dd/MM/yyyy"));
            }else
                tvPaidDate.setText(null);

//            if (i > 9) {
//                paidDate.setTextColor(Color.RED);
//            }

            TextView tvOvdDays = ButterKnife.findById(row, R.id.attrib_ovd_days);

            if (obj.getPaidDate() != null) {
                long ovdDays = Utility.getDateDiff(obj.getDueDate(), obj.getPaidDate(), TimeUnit.DAYS);
                tvOvdDays.setText(ovdDays < 1 ? "0" : String.valueOf(ovdDays));

                if (ovdDays > 0) {
                    tvOvdDays.setTextColor(Color.RED);
                    tvPaidDate.setTextColor(Color.RED);
                }

            } else {

            }

            tableLayout.addView(row);
        }
    }

    private void loadTable(RealmResults<HistInstallments> list) {

        // clear table
        tableLayout.removeAllViews();
        // create header
        TableRow row_header = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_payment_history, null);

        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_no), "No.");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_due_date), "Due date");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_paid_date), "Paid date");
        setTableHeader((TextView) ButterKnife.findById(row_header, R.id.attrib_ovd_days), "OVD days");

        tableLayout.addView(row_header);
        // add rows

        for (int i = 0; i < list.size(); i++) {

            HistInstallments obj = list.get(i);
//            HistInstallments obj = this.realm.copyFromRealm(list.get(i));

            TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.row_payment_history, null);
            ((TextView) row.findViewById(R.id.attrib_no)).setText(String.valueOf(i + 1));
//            ((TextView) row.findViewById(R.id.attrib_due_date)).setText(Utility.convertDateToString(new Date(), "dd/MM/yyyy"));
            ((TextView) row.findViewById(R.id.attrib_due_date)).setText( Utility.convertDateToString(obj.getDueDate(), "dd/MM/yyyy"));

            TextView tvPaidDate = ButterKnife.findById(row, R.id.attrib_paid_date);
            if (obj.getPaidDate() != null) {
                tvPaidDate.setText(Utility.convertDateToString(obj.getPaidDate(), "dd/MM/yyyy"));
            }else
                tvPaidDate.setText(null);

//            if (i > 9) {
//                paidDate.setTextColor(Color.RED);
//            }

            TextView tvOvdDays = ButterKnife.findById(row, R.id.attrib_ovd_days);

            if (obj.getPaidDate() != null) {
                long ovdDays = Utility.getDateDiff(obj.getDueDate(), obj.getPaidDate(), TimeUnit.DAYS);
                tvOvdDays.setText(ovdDays < 1 ? "0" : String.valueOf(ovdDays));

                if (ovdDays > 0) {
                    tvOvdDays.setTextColor(Color.RED);
                    tvPaidDate.setTextColor(Color.RED);
                }

            } else {

            }

            tableLayout.addView(row);
        }
    }
}
