package id.co.ppu.collectionfast2.lkp;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.EditText;
import android.widget.TableLayout;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;

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

        UserData currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_details_lkp);
            getSupportActionBar().setSubtitle(currentUser.getFullName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }


        Date serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
        String createdBy = "JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd");

        TrnLDVHeader header = this.realm.where(TrnLDVHeader.class)
                .equalTo("collCode", this.collectorId)
                .equalTo("createdBy", createdBy)
                .findFirst();

        if (header == null) {
            return;
        }
        etNoLKP.setText(header.getLdvNo());

        etTglLKP.setText(DateUtils.formatDateTime(this, serverDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));

        // clear table
        tableLayout.removeAllViews();

    }
}
