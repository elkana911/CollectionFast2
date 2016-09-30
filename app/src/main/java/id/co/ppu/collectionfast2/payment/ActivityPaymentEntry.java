package id.co.ppu.collectionfast2.payment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import java.util.Date;

import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.payment.entry.FragmentActiveContractsList;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.util.Storage;

public class ActivityPaymentEntry extends BasicActivity implements FragmentActiveContractsList.OnActiveContractSelectedListener {

    public static final String PARAM_CONTRACT_NO = "contractNo";
    public static final String PARAM_TITLE = "title";
    public static final String PARAM_LDV_NO = "ldvNo";
    public static final String PARAM_LKP_DATE = "lkpDate";

    String ldvNo = null;
    String contractNo = null;
    Date lkpDate = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        ButterKnife.bind(this);


        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            setTitle("Payment Entry");
        } else {
            setTitle(extras.getString(PARAM_TITLE));

            this.ldvNo = extras.getString(PARAM_LDV_NO);
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
            this.lkpDate = new Date(extras.getLong(PARAM_LKP_DATE));
        }


        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setHomeButtonEnabled(true);
        }

        Fragment fragmentPayment = new FragmentPayment();
        Bundle bundle = new Bundle();

        UserData currentUser = (UserData) Storage.getObjPreference(getApplicationContext(), Storage.KEY_USER, UserData.class);
        bundle.putString(FragmentPayment.PARAM_COLL_CODE, currentUser.getUser().get(0).getUserId());

        bundle.putString(FragmentPayment.PARAM_LDV_NO, this.ldvNo);
        bundle.putString(FragmentPayment.PARAM_CONTRACT_NO, this.contractNo);
        bundle.putLong(FragmentPayment.PARAM_LKP_DATE, this.lkpDate.getTime());
        fragmentPayment.setArguments(bundle);

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, fragmentPayment);
        ft.commit();
    }

    @Override
    public void onBackPressed() {

        FragmentPayment frag = (FragmentPayment)
                getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag != null) {
            /*if (frag.anyChanges()) {
                new AlertDialog.Builder(this)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Quit Form")
                        .setMessage("Any changes will be canceled. Are you sure " +
                                "you want to continue?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();

            }else
            */
                super.onBackPressed();
        }
    }


    @Override
    public void onContractSelected(String contractNo) {
        FragmentPayment frag = (FragmentPayment)
                getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (frag == null)
            return;

        frag.etContractNo.setText(contractNo);
        frag.loadContract(contractNo);
    }
}
