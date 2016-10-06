package id.co.ppu.collectionfast2.lkp;

import android.os.Bundle;
import android.widget.EditText;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnVehicleInfo;

public class ActivityVehicleInfo extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    private String contractNo = null;
    private Date serverDate;

    @BindView(R.id.etContractNo)
    EditText etContractNo;

    @BindView(R.id.etCategory)
    EditText etCategory;

    @BindView(R.id.etMerk)
    EditText etMerk;

    @BindView(R.id.etType)
    EditText etType;

    @BindView(R.id.etWarna)
    EditText etWarna;

    @BindView(R.id.etNoPolisi)
    EditText etNoPolisi;

    @BindView(R.id.etNoMesin)
    EditText etNoMesin;

    @BindView(R.id.etNoRangka)
    EditText etNoRangka;

    @BindView(R.id.etThnProduksi)
    EditText etThnProduksi;

    @BindView(R.id.etNamaBPKB)
    EditText etNamaBPKB;

    @BindView(R.id.etNoBPKB)
    EditText etNoBPKB;

    @BindView(R.id.etAlamatBPKB)
    EditText etAlamatBPKB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_info);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", this.contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_vehicle_info);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etContractNo.setText(this.contractNo);

        TrnVehicleInfo trnVehicleInfo = this.realm.where(TrnVehicleInfo.class).equalTo("contractNo", this.contractNo).findFirst();
        etCategory.setText("MOTOR");
        etMerk.setText(trnVehicleInfo.getObjBrand());
        etType.setText(trnVehicleInfo.getObjType());
        etWarna.setText(trnVehicleInfo.getWarna());

        etNoPolisi.setText(trnVehicleInfo.getNoPolisi());
        etNoMesin.setText(trnVehicleInfo.getNosin());
        etNoRangka.setText(trnVehicleInfo.getNoka());
        etThnProduksi.setText(String.valueOf(trnVehicleInfo.getObjTahun()));

        etNoBPKB.setText(trnVehicleInfo.getBpkbIdNo());
        etNamaBPKB.setText(trnVehicleInfo.getBpkbName());
        etAlamatBPKB.setText(trnVehicleInfo.getBpkbAddress());

    }
}
