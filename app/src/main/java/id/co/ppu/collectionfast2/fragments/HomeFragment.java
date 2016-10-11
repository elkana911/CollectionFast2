package id.co.ppu.collectionfast2.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.pojo.UserData;
import id.co.ppu.collectionfast2.pojo.master.MstSecUser;
import id.co.ppu.collectionfast2.pojo.master.MstUser;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;

public class HomeFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    @BindView(R.id.etCollectorName)
    EditText etCollectorName;

    @BindView(R.id.etJabatan)
    EditText etJabatan;

    @BindView(R.id.etNIKNo)
    EditText etNIKNo;

    @BindView(R.id.etCabang)
    EditText etCabang;

    @BindView(R.id.etAddress)
    EditText etAddress;

    @BindView(R.id.etEmail)
    EditText etEmail;

    @BindView(R.id.etPhone)
    EditText etPhone;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        ButterKnife.bind(this, view);

        Utility.setViewGroupFocusable((ViewGroup) view.findViewById(R.id.llFormCollector), false);

        UserData userData = (UserData) Storage.getObjPreference(getContext(), Storage.KEY_USER, UserData.class);

        if (userData.getUser() != null) {
            MstUser user = userData.getUser().get(0);

            etJabatan.setText(user.getJabatan());
            etNIKNo.setText(user.getNik());
            etCabang.setText(user.getBranchName());
            etAddress.setText(user.getAlamat());
        }

        if (userData.getSecUser() != null) {
            MstSecUser secUser = userData.getSecUser().get(0);

            etPhone.setText(secUser.getMobilePhone());
            etCollectorName.setText(secUser.getFullName());
            etEmail.setText(secUser.getEmailAddr());
        }

        etPhone.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

}
