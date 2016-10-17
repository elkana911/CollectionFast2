package id.co.ppu.collectionfast2.lkp;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TableLayout;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.ServerInfo;

public class ActivityDetailsLKPSummary extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_LKP_DATE = "lkpDate";

    private String contractNo = null;
    private Date lkpDate = null;

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
            contractNo = extras.getString(PARAM_CONTRACT_NO);
            long lkpdate = extras.getLong(PARAM_LKP_DATE);
            this.lkpDate = new Date(lkpdate);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_details_lkp);
//            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        if (this.lkpDate == null) {
            this.lkpDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
        }

        // clear table
        tableLayout.removeAllViews();

    }
}
