package id.co.ppu.collectionfast2.lkp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.HistInstallments;
import id.co.ppu.collectionfast2.pojo.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.RealmResults;

public class ActivityPaymentHistory extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";

    private String contractNo = null;

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
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();
        TrnContractBuckets contractBuckets = this.realm.where(TrnContractBuckets.class).equalTo("pk.contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_payment_history);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(dtl.getContractNo());
        long angsuran = contractBuckets.getPrncAmt() + contractBuckets.getIntrAmt();
        etAngsuran.setText(Utility.convertLongToRupiah(angsuran));

        long tenor = contractBuckets.getTop();
        etTenor.setText(String.valueOf(tenor));

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
//        RealmResults<HistInstallments> list = this.realm.where(HistInstallments.class).equalTo("pk.contractNo", this.contractNo).findAllSorted(new String[]{"pk.contractNo", "pk.instNo"}, new Sort[]{Sort.ASCENDING, Sort.ASCENDING});
        RealmResults<HistInstallments> list = this.realm.where(HistInstallments.class).equalTo("pk.contractNo", this.contractNo).findAll();

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
    }
}
