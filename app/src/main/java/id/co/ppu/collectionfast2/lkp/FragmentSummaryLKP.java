package id.co.ppu.collectionfast2.lkp;


import android.content.Context;
import android.content.Intent;
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
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.pojo.ServerInfo;
import id.co.ppu.collectionfast2.pojo.master.MstLDVStatus;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Sysdate only. penerimaan lkpinquiry tidak dihitung disini
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

    @BindView(R.id.spWorkFlags)
    Spinner spWorkFlags;

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

        Date serverDate = null;
        try {
            serverDate = this.realm.where(ServerInfo.class).findFirst().getServerDate();
        } catch (Exception e) {
            e.printStackTrace();

            serverDate = new Date();
        }
        String createdBy = "JOB" + Utility.convertDateToString(serverDate, "yyyyMMdd");

        // TODO: ask pak yoce, jika kasusnya hr ini udah closebatch, inquiry hr kmrn akan donlot header kmrn ?
        TrnLDVHeader header = this.realm.where(TrnLDVHeader.class)
                .equalTo("collCode", collectorCode)
                .equalTo("createdBy", createdBy)
                .findFirst();

        if (header == null) {
            return;
        }

//        UserData userData = (UserData) Storage.getPreference(Storage.KEY_USER, UserData.class);

        // based on collectorcode
        long unitTarget = 0;
        RealmResults<TrnLDVDetails> ldvDetailses = this.realm.where(TrnLDVDetails.class)
                .equalTo("createdBy", createdBy)
                .findAll();
        for (TrnLDVDetails ldvDetails : ldvDetailses) {
            if (ldvDetails.getPk().getLdvNo().equals(header.getLdvNo())) {
                unitTarget += 1;
            }
        }
/*
        long unitTarget = this.realm.where(TrnLDVDetails.class)
//                .equalTo("")
                .count();
*/

        StringBuilder rvColl = new StringBuilder();
        rvColl.append(Utility.convertDateToString(serverDate, "dd"))
                .append(Utility.convertDateToString(serverDate, "MM"))
                .append(Utility.convertDateToString(serverDate, "yyyy"))
                .append(collectorCode);

        RealmResults<TrnRVColl> trnRVColls = this.realm.where(TrnRVColl.class).findAll();

        long receivedAmount = this.realm.where(TrnRVColl.class)
                .equalTo("pk.rvCollNo", rvColl.toString())
                .isNull("ldvNo")
                .sum("receivedAmount")
                .longValue();

        // TODO: ask pak yoce maksudnya count(mc_trn_rvcoll.ldv_no) yg ldv_no nya null ??
        long unitNonLKP = this.realm.where(TrnRVColl.class)
                            .equalTo("pk.rvCollNo", rvColl.toString())
//                            .equalTo("collId", collectorCode)
                            .isNull("ldvNo")
                            .count();

//        long _totalRVColl = this.realm.where(TrnRVColl.class).count();

        long totalTertagih = this.realm.where(TrnRVColl.class)
                .equalTo("pk.rvCollNo", rvColl.toString())
                .sum("receivedAmount")
                .longValue();

//        long totalTertagih = header.getPrncAC() + header.getIntrAC() + receivedAmount.longValue();

        etNoLKP.setText(header.getLdvNo());

        etTglLKP.setText(DateUtils.formatDateTime(getContext(), serverDate.getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));
//        etTglLKP.setText(DateUtils.formatDateTime(getContext(), header.getLdvDate().getTime(), DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR | DateUtils.FORMAT_SHOW_WEEKDAY));

        final String userFullName = Storage.getPref(Storage.KEY_USER_FULLNAME, null);
        final String userBranchName = Storage.getPref(Storage.KEY_USER_BRANCH_NAME, null);
        etCabang.setText(userBranchName);
        etARStaff.setText(userFullName);

        long targetPenerimaan = (header.getPrncAMBC() == null ? 0L : header.getPrncAMBC())
                    + (header.getIntrAMBC() == null ? 0L : header.getIntrAMBC());

        etTargetPenerimaan.setText(Utility.convertLongToRupiah(targetPenerimaan));

        String ldvNo = header.getLdvNo();

        Number sumReceivedAmount = this.realm.where(TrnRVColl.class)
                .equalTo("ldvNo", ldvNo)
                .equalTo("pk.rvCollNo", rvColl.toString())
//                .equalTo("collId", collectorCode)
                .sum("receivedAmount");

        long tertagihPenerimaan = sumReceivedAmount.longValue(); //header.getPrncAC() + header.getIntrAC();
        etTertagihPenerimaan.setText(Utility.convertLongToRupiah(tertagihPenerimaan));

        etTargetUnit.setText(String.valueOf(unitTarget));

        long tertagihUnit = this.realm.where(TrnRVColl.class)
                .equalTo("ldvNo", ldvNo)
                .equalTo("pk.rvCollNo", rvColl.toString())
//                .equalTo("collId", collectorCode)
                .count();

//        long tertagihUnit = header.getUnitTotal();
        etTertagihUnit.setText(String.valueOf(tertagihUnit));

        etNonLKPPenerimaan.setText(Utility.convertLongToRupiah(receivedAmount));
        etNonLKPUnit.setText(String.valueOf(unitNonLKP));

        etTotalTertagih.setText(Utility.convertLongToRupiah(totalTertagih));

        buildWorkFlag();

    }

    private void buildWorkFlag() {
        RealmResults<MstLDVStatus> list = this.realm.where(MstLDVStatus.class).findAll();
        WorkFlagAdapter workFlagAdapter = new WorkFlagAdapter(getContext(), android.R.layout.simple_spinner_item, list);

        spWorkFlags.setAdapter(workFlagAdapter);

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

    @OnClick(R.id.btnDetailsLKP)
    public void onClickDetailsLKP() {
//        Utility.createAndShowProgressDialog(getContext(), "Details LKP Summary", "Sorry, Under Constructions");
        Intent i = new Intent(getActivity(), ActivityDetailsLKPSummary.class);

//        i.putExtra(ActivityDetailsLKPSummary.PARAM_IS_LKP_INQUIRY, isLKPInquiry);
//        i.putExtra(ActivityDetailsLKPSummary.PARAM_LKP_DATE, this.lkpDate.getTime());
        i.putExtra(ActivityDetailsLKPSummary.PARAM_COLLECTOR_ID, this.collectorCode);

        startActivity(i);
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
//            label.setTextColor(Color.BLACK);
            label.setText(list.get(position).getStatusDesc());
            label.setPadding(10, 20, 10, 20);
            label.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);

            return label;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }


}
