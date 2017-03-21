package id.co.ppu.collectionfast2.poa;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.util.PoAUtil;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;

/**
 * Picture-On-Arrival
 */
public class ActivityPoA extends BasicActivity {

    public static final String PARAM_LKP_DETAIL = "lkp.detail";
    public static final String PARAM_COLLECTOR_ID = "collector.id";
    public static final String PARAM_CUSTOMER_NAME = "customer.name";
    public static final String PARAM_CUSTOMER_ADDR = "customer.address";
    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_LDV_NO = "ldvNo";

    private String lkpDetail = null;
    private String collCode = null;
    private String contractNo = null;
    private String custAddr = null;
    private String custName = null;

    private String ldvNo = null;
    @BindView(R.id.ivCamera)
    ImageView ivCamera;
    @BindView(R.id.pulsator)
    PulsatorLayout pulsator;

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Animation animZoomIn = AnimationUtils.loadAnimation(this, R.anim.zoom_in);
        ivCamera.startAnimation(animZoomIn);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poa);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.lkpDetail = extras.getString(PARAM_LKP_DETAIL);        // bisa kosong di paymententri
            this.collCode = extras.getString(PARAM_COLLECTOR_ID);
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);  // bisa kosong di paymententry
            this.custName = extras.getString(PARAM_CUSTOMER_NAME);
            this.custAddr = extras.getString(PARAM_CUSTOMER_ADDR);
            this.ldvNo = extras.getString(PARAM_LDV_NO);

            setTitle(custName);
        }

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setSubtitle(this.custAddr);
        }


        pulsator.start();
    }

    @OnClick(R.id.llTakePhoto)
    public void onTakePoA() {

        PoAUtil.callCameraIntent(this, collCode, ldvNo, contractNo);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        File file = PoAUtil.postCameraIntoCache(this, collCode, contractNo);// /storage/emulated/0/RadanaCache/cache/poaDefault_demo_71000000008115.jpg

        if (file != null) {
            pulsator.stop();

            Intent dataReturn = new Intent();
            dataReturn.putExtra(PARAM_LKP_DETAIL, lkpDetail);
            setResult(RESULT_OK, dataReturn);

            finish();
        }
    }

}
