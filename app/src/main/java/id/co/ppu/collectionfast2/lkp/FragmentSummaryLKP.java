package id.co.ppu.collectionfast2.lkp;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.pojo.MstLDVStatus;
import id.co.ppu.collectionfast2.pojo.MstSecUser;
import id.co.ppu.collectionfast2.pojo.MstUser;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentSummaryLKP extends Fragment {

    public static final String ARG_PARAM1 = "collector.code";

    private Realm realm;
    private String collectorCode;

    @BindView(R.id.etNoLKP)
    EditText etNoLKP;

    @BindView(R.id.etTglLKP)
    EditText etTglLKP;

    @BindView(R.id.etCabang)
    EditText etCabang;

    @BindView(R.id.etARStaff)
    EditText etARStaff;

    @BindView(R.id.spWorkFlag)
    MaterialBetterSpinner spWorkFlag;

    // table
    @BindView(R.id.etTargetPenerimaan)
    EditText etTargetPenerimaan;

    @BindView(R.id.etTertagihPenerimaan)
    EditText etTertagihPenerimaan;

    @BindView(R.id.etTargetUnit)
    EditText etTargetUnit;

    @BindView(R.id.etTertagihUnit)
    EditText etTertagihUnit;

    // non lkp
    @BindView(R.id.etNonLKPPenerimaan)
    EditText etNonLKPPenerimaan;

    @BindView(R.id.etNonLKPUnit)
    EditText etNonLKPUnit;

    @BindView(R.id.etTotalTertagih)
    EditText etTotalTertagih;

    public FragmentSummaryLKP() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        this.realm = Realm.getDefaultInstance();
        TrnLDVHeader header = this.realm.where(TrnLDVHeader.class).findFirst();

        if (header == null) {
            return;
        }
        UserData userData = (UserData) Storage.getObjPreference(getContext(), Storage.KEY_USER, UserData.class);

        long unitTarget = this.realm.where(TrnLDVDetails.class).count();

        Number receivedAmount = this.realm.where(TrnRVColl.class).isNull("ldvNo").sum("receivedAmount");

        long unitNonLKP = this.realm.where(TrnRVColl.class).count();

        long totalTertagih = header.getPrncAC() + header.getIntrAC() + receivedAmount.longValue();

        loadSummary(header, userData.getUser().get(0), userData.getSecUser().get(0), unitTarget, receivedAmount.longValue(), unitNonLKP, totalTertagih);

        RealmResults<MstLDVStatus> list = this.realm.where(MstLDVStatus.class).findAll();
        WorkFlagAdapter workFlagAdapter = new WorkFlagAdapter(getContext(), android.R.layout.simple_spinner_item, list);
        spWorkFlag.setAdapter(workFlagAdapter);

    }

    private void loadSummary(TrnLDVHeader header, MstUser mstUser, MstSecUser mstSecUser, long unitTarget, long receivedAmount, long unitNonLKP, long totalTertagih) {
        etNoLKP.setText(header.getLdvNo());
        etTglLKP.setText(DateUtils.formatDateTime(getContext(), header.getLdvDate().getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));

        if (mstUser != null) {
            etCabang.setText(mstUser.getBranchName());
        }

        if (mstSecUser != null) {
            etARStaff.setText(mstSecUser.getFullName());
        }

        long targetPenerimaan = header.getPrncAMBC() + header.getIntrAMBC();
        etTargetPenerimaan.setText(Utility.convertLongToRupiah(targetPenerimaan));

        long tertagihPenerimaan = header.getPrncAC() + header.getIntrAC();
        etTertagihPenerimaan.setText(Utility.convertLongToRupiah(tertagihPenerimaan));

        etTargetUnit.setText(String.valueOf(unitTarget));

        long tertagihUnit = header.getUnitTotal();
        etTertagihUnit.setText(String.valueOf(tertagihUnit));

        etNonLKPPenerimaan.setText(Utility.convertLongToRupiah(receivedAmount));
        etNonLKPUnit.setText(String.valueOf(unitNonLKP));

        etTotalTertagih.setText(Utility.convertLongToRupiah(totalTertagih));
    }

    @Override
    public void onStop() {
        super.onStop();

        if (this.realm != null) {
            this.realm.close();
            this.realm = null;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            collectorCode = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_summary_lkp, container, false);

        ButterKnife.bind(this, view);

        return view;
    }

    public class WorkFlagAdapter extends ArrayAdapter<MstLDVStatus> {
        private Context ctx;
        private List<MstLDVStatus> list;


        public WorkFlagAdapter(Context context, int resource, List<MstLDVStatus> objects) {
            super(context, resource, objects);
            this.ctx = context;
            this.list = objects;

        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public MstLDVStatus getItem(int position) {
            return list.get(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(this.ctx);
//            TextView tv = (TextView) convertView.findViewById(R.id.nama);
            tv.setPadding(10,20,10,20);
            tv.setTextColor(Color.BLACK);
            tv.setText(list.get(position).getStatusDesc());
            tv.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

            return tv;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView label = new TextView(this.ctx);
            label.setTextColor(Color.BLACK);
            label.setText(list.get(position).getStatusDesc());

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


}
