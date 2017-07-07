package id.co.ppu.collectionfast2.lkp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.Area;
import id.co.ppu.collectionfast2.pojo.AreaList;
import id.co.ppu.collectionfast2.pojo.master.MstCities;
import id.co.ppu.collectionfast2.pojo.master.MstKecamatan;
import id.co.ppu.collectionfast2.pojo.master.MstKelurahan;
import id.co.ppu.collectionfast2.pojo.master.MstProvinsi;
import id.co.ppu.collectionfast2.pojo.master.MstZip;
import id.co.ppu.collectionfast2.pojo.sync.SyncTrnChangeAddr;
import id.co.ppu.collectionfast2.pojo.trn.TrnChangeAddr;
import id.co.ppu.collectionfast2.pojo.trn.TrnChangeAddrPK;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.rest.APIClientBuilder;
import id.co.ppu.collectionfast2.rest.APInterface;
import id.co.ppu.collectionfast2.rest.request.RequestArea;
import id.co.ppu.collectionfast2.rest.response.ResponseArea;
import id.co.ppu.collectionfast2.rest.response.ResponseAreaList;
import id.co.ppu.collectionfast2.util.NetUtil;
import id.co.ppu.collectionfast2.util.Storage;
import id.co.ppu.collectionfast2.util.Utility;
import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivityChangeAddress extends BasicActivity {

    public static final String PARAM_CONTRACT_NO = "customer.contractNo";
    public static final String PARAM_COLLECTOR_ID = "collector.id";

    private String contractNo = null;
    private String collectorId = null;

    @BindView(R.id.activity_change_addr)
    View activityChangeAddr;

    @BindView(R.id.btnCari)
    Button btnCari;

    @BindView(R.id.etAddress)
    EditText etAddress;

    @BindView(R.id.etRT)
    EditText etRT;

    @BindView(R.id.etRW)
    EditText etRW;

    @BindView(R.id.etSubZip)
    EditText etSubZip;

    @BindView(R.id.etPropinsi)
    EditText etPropinsi;

    @BindView(R.id.etKabupaten)
    EditText etKabupaten;

    @BindView(R.id.etKecamatan)
    EditText etKecamatan;

    @BindView(R.id.etKelurahan)
    AutoCompleteTextView etKelurahan;

    @BindView(R.id.etFixPhone)
    EditText etFixPhone;

    @BindView(R.id.etZip)
    EditText etZipCode;

    @BindView(R.id.etHandphone1)
    EditText etHandphone1;

    @BindView(R.id.etHandphone2)
    EditText etHandphone2;

    @BindView(R.id.etEmail)
    AutoCompleteTextView etEmail;

    @BindView(R.id.etNamaPemilik)
    AutoCompleteTextView etNamaPemilik;

    @BindView(R.id.etAlamatPemilik)
    AutoCompleteTextView etAlamatPemilik;

    @BindView(R.id.etPhonePemilik)
    AutoCompleteTextView etPhonePemilik;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_address);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            this.contractNo = extras.getString(PARAM_CONTRACT_NO);
            this.collectorId = extras.getString(PARAM_COLLECTOR_ID);
        }

        TrnLDVDetails dtl = this.realm.where(TrnLDVDetails.class).equalTo("contractNo", contractNo).findFirst();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.title_activity_change_address);
            getSupportActionBar().setSubtitle(dtl.getCustName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
        }

        etNamaPemilik.setText(dtl.getCustName());
        etAlamatPemilik.setText(dtl.getAddress().getCollAddr());
        etPhonePemilik.setText(dtl.getAddress().getCollMobPhone());

        etPhonePemilik.setMovementMethod(LinkMovementMethod.getInstance());

        etKelurahan.setThreshold(1);
        etKelurahan.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String s = adapterView.getItemAtPosition(i).toString();
                String[] ss = s.split("-", -1);

                btnCari.performClick();

                etKelurahan.setText(ss[0].trim());
//                Toast.makeText(ActivityChangeAddress.this, s, Toast.LENGTH_SHORT).show();
            }
        });

        if (!NetUtil.isConnected(this)) {
            Utility.showDialog(this, "Error", getString(R.string.error_online_required));
        }

        TrnChangeAddr trnChangeAddr = realm.where(TrnChangeAddr.class)
                .equalTo("pk.contractNo", contractNo)
                .equalTo("pk.seqNo", 2)
                .equalTo("collId", collectorId)
                .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                .findFirst();
        if (trnChangeAddr != null) {
            etAddress.setText(trnChangeAddr.getCollAddr());
            etRT.setText(trnChangeAddr.getCollRt());
            etRW.setText(trnChangeAddr.getCollRw());
            etFixPhone.setText(trnChangeAddr.getCollFixPhone());
            etHandphone1.setText(trnChangeAddr.getCollMobPhone());
            etHandphone2.setText(trnChangeAddr.getCollMobPhone2());
            etEmail.setText(trnChangeAddr.getCollEmail());

            etZipCode.setText(trnChangeAddr.getCollZip());
            etSubZip.setText(trnChangeAddr.getCollSubZip());
            etPropinsi.setText(trnChangeAddr.getCollProv());
            etKabupaten.setText(trnChangeAddr.getCollCity());
            etKecamatan.setText(trnChangeAddr.getCollKec());
            etKelurahan.setText(trnChangeAddr.getCollKel());
        }
    }

    private synchronized void retrieveAreaFromServer(String kelurahanOrZipCode) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        APInterface fastService =
                APIClientBuilder.create(APInterface.class, Utility.buildUrl(Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0)));

        RequestArea req = new RequestArea();

        if (TextUtils.isDigitsOnly(kelurahanOrZipCode)) {
            req.setZipCode(kelurahanOrZipCode);
        } else {
            req.setKelurahan(kelurahanOrZipCode);
        }

        Call<ResponseArea> callArea = fastService.getArea(req);

        callArea.enqueue(new Callback<ResponseArea>() {
            @Override
            public void onResponse(Call<ResponseArea> call, Response<ResponseArea> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (response.isSuccessful()) {
                    final ResponseArea body = response.body();

                    if (body.getError() == null) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                Area area = body.getData();

                                // khusus kelurahan supaya mudah dicari ubah ke lowercase
                                MstKelurahan kel = area.getKelurahan();
                                kel.setKelurahan(kel.getKelurahan().toLowerCase());
                                realm.copyToRealmOrUpdate(kel);

                                realm.copyToRealmOrUpdate(area.getCity());
                                realm.copyToRealmOrUpdate(area.getKecamatan());
                                realm.copyToRealmOrUpdate(area.getProvinsi());
                                realm.copyToRealmOrUpdate(area.getZip());
                            }
                        });

                        RealmResults<MstKelurahan> kelurahanList = realm.where(MstKelurahan.class).contains("kelurahan", etKelurahan.getText().toString(), Case.INSENSITIVE).findAll();

                        if (kelurahanList.size() > 0) {

                            if (kelurahanList.size() == 1)
                                loadArea(kelurahanList.get(0));

                            List<String> list = new ArrayList<>();
                            for (MstKelurahan b : kelurahanList) {
                                list.add(b.getKelurahan());
                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ActivityChangeAddress.this,
                                    android.R.layout.simple_list_item_1, list) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                                    text.setTextColor(Color.BLACK);
                                    return view;
                                }
                            };

                            etKelurahan.setAdapter(dataAdapter);

                            etKelurahan.showDropDown();
                        }

                    }

                } else {
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    try {
                        Log.e("eric.unSuccessful", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseArea> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.e("eric.onFailure", t.getMessage(), t);
            }
        });
    }

    // TODO: do later mengambil daftar kelurahan
    private synchronized void retrieveKelurahanListFromServer(String kelurahanOrZipCode) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        APInterface api = APIClientBuilder.create(Utility.buildUrl(Storage.getPrefAsInt(Storage.KEY_SERVER_ID, 0)));

        RequestArea req = new RequestArea();

        if (TextUtils.isDigitsOnly(kelurahanOrZipCode)) {
            req.setZipCode(kelurahanOrZipCode);
        } else {
            req.setKelurahan(kelurahanOrZipCode);
        }

        Call<ResponseAreaList> callArea = api.getAreaList(req);

        callArea.enqueue(new Callback<ResponseAreaList>() {
            @Override
            public void onResponse(Call<ResponseAreaList> call, Response<ResponseAreaList> response) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                if (response.isSuccessful()) {
                    final ResponseAreaList body = response.body();

                    if (body.getError() == null) {
                        realm.executeTransaction(new Realm.Transaction() {
                            @Override
                            public void execute(Realm realm) {
                                AreaList area = body.getData();

                                // khusus kelurahan supaya mudah dicari ubah ke lowercase
                                for (int i = 0; i < area.getKelurahan().size(); i++) {
                                    MstKelurahan kel = area.getKelurahan().get(i);
                                    kel.setKelurahan(kel.getKelurahan().toLowerCase());
                                    realm.copyToRealmOrUpdate(kel);

                                }

                                realm.copyToRealmOrUpdate(area.getCity());
                                realm.copyToRealmOrUpdate(area.getKecamatan());
                                realm.copyToRealmOrUpdate(area.getProvinsi());
                                realm.copyToRealmOrUpdate(area.getZip());
                            }
                        });

                        RealmResults<MstKelurahan> kelurahanList = realm.where(MstKelurahan.class)
                                .contains("kelurahan", etKelurahan.getText().toString(), Case.INSENSITIVE)
                                .findAll();

                        if (kelurahanList.size() > 0) {

                            if (kelurahanList.size() == 1)
                                loadArea(kelurahanList.get(0));

                            List<String> list = new ArrayList<>();
                            for (MstKelurahan b : kelurahanList) {

                                for (MstKecamatan c : realm.where(MstKecamatan.class).equalTo("kecCode", b.getKecCode()).findAll()) {

                                    for (MstCities d : realm.where(MstCities.class).equalTo("cityCode", c.getCityCode()).findAll()) {

                                        for (MstProvinsi e : realm.where(MstProvinsi.class).equalTo("provCode", d.getProvCode()).findAll()) {

                                            list.add(b.getKelurahan() + " - " + c.getKecamatan() + " - " + d.getCity() + " - " + e.getProvinsi());

                                        }
                                    }

                                }

                            }
                            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ActivityChangeAddress.this,
                                    android.R.layout.simple_list_item_1, list) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView text = (TextView) view.findViewById(android.R.id.text1);
                                    text.setTextColor(Color.BLACK);
                                    text.setSingleLine(false);
                                    text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                                    return view;
                                }
                            };

                            etKelurahan.setAdapter(dataAdapter);

                            etKelurahan.showDropDown();
                        }

                    }

                } else {
                    // handle request errors yourself
                    ResponseBody errorBody = response.errorBody();
                    try {
                        Log.e("eric.unSuccessful", errorBody.string());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseAreaList> call, Throwable t) {
                if (mProgressDialog.isShowing())
                    mProgressDialog.dismiss();

                Log.e("eric.onFailure", t.getMessage(), t);
            }
        });

    }

    private Area getArea(MstKelurahan kelurahan) {

        Area area = new Area();

        MstKecamatan kec = realm.where(MstKecamatan.class).equalTo("kecCode", kelurahan.getKecCode()).findFirst();
        if (kec != null) {
            area.setKecamatan(realm.copyFromRealm(kec));

            long count = realm.where(MstCities.class).count();
            MstCities city = realm.where(MstCities.class).equalTo("cityCode", kec.getCityCode()).findFirst();
            if (city != null) {
                area.setCity(realm.copyFromRealm(city));

                MstProvinsi prov = realm.where(MstProvinsi.class).equalTo("provCode", city.getProvCode()).findFirst();
                if (prov != null) {
                    area.setProvinsi(realm.copyFromRealm(prov));

                    RealmResults<MstZip> zipList = realm.where(MstZip.class)
                            .equalTo("provCode", prov.getProvCode())
                            .equalTo("cityCode", city.getCityCode())
                            .equalTo("kecCode", kec.getKecCode())
                            .equalTo("kelCode", kelurahan.getKelCode())
                            .findAll();
                    List<MstZip> zips = realm.copyFromRealm(zipList);
                    area.setZip(zips);
                }
            }
        }

        return area;
    }

    private void loadArea(MstKelurahan kelurahan) {

        Area area = getArea(kelurahan);

        etKecamatan.setText(area.getKecamatan().getKecamatan());

        etKabupaten.setText(area.getCity() == null ? null : area.getCity().getCity());
        etPropinsi.setText(area.getProvinsi() == null ? null : area.getProvinsi().getProvinsi());

        if (area.getZip() != null && area.getZip().size() > 0) {
            etZipCode.setText(area.getZip().get(0).getPk().getZipCode());
        } else
            etZipCode.setText(null);

    }

    @OnClick(R.id.btnCari)
    public void onClickCariKelurahan() {

        String kelurahanValue = etKelurahan.getText().toString().trim();
        if (kelurahanValue.length() < 2) return;

        RealmResults<MstKelurahan> kelurahanList;

        if (kelurahanValue.indexOf('-') > -1) {

            String[] ss = kelurahanValue.split("-", -1);

            MstKecamatan mstKecamatan = realm.where(MstKecamatan.class).equalTo("kecamatan", ss[1].trim()).findFirst();

            kelurahanList = realm.where(MstKelurahan.class)
                    .contains("kelurahan", ss[0].trim(), Case.INSENSITIVE)
                    .equalTo("kecCode", mstKecamatan.getKecCode())
                    .findAll();

        } else {
            kelurahanList = realm.where(MstKelurahan.class)
                    .contains("kelurahan", kelurahanValue, Case.INSENSITIVE)
                    .findAll();
        }

        etKelurahan.setAdapter(null);

        if (kelurahanList.size() < 1) {
            retrieveKelurahanListFromServer(kelurahanValue);
//            retrieveAreaFromServer(etKelurahan.getText().toString());
            return;
        }

        if (kelurahanList.size() == 1) {
            loadArea(kelurahanList.get(0));
        }

        List<String> list = new ArrayList<>();
        for (MstKelurahan b : kelurahanList) {
            for (MstKecamatan c : realm.where(MstKecamatan.class)
                    .equalTo("kecCode", b.getKecCode())
                    .findAll()) {
                for (MstCities d : realm.where(MstCities.class).equalTo("cityCode", c.getCityCode()).findAll()) {

                    for (MstProvinsi e : realm.where(MstProvinsi.class).equalTo("provCode", d.getProvCode()).findAll()) {

                        list.add(b.getKelurahan() + " - " + c.getKecamatan() + " - " + d.getCity() + " - " + e.getProvinsi());
                    }
                }
            }
        }
        // sort
        Collections.sort(list);

        // override color of text item
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ActivityChangeAddress.this,
                android.R.layout.simple_list_item_1, list) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 14);
                text.setTextColor(Color.BLACK);
                return view;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);
                TextView text = (TextView) view.findViewById(android.R.id.text1);
                text.setSingleLine(false);
                text.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 9);
                return view;
            }
        };

        etKelurahan.setAdapter(dataAdapter);

        etKelurahan.showDropDown();

    }

    @OnClick(R.id.btnSave)
    public void onClickSave() {
        // reset errors
        etEmail.setError(null);
        etAddress.setError(null);
        etRW.setError(null);
        etRT.setError(null);
        etHandphone1.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Store values at the time of save attempt
        final String email = etEmail.getText().toString();
        final String address = etAddress.getText().toString();
        final String handphone1 = etHandphone1.getText().toString();
        final String handphone2 = etHandphone2.getText().toString();

        // Check for a valid email, if the user entered one.
        if (!TextUtils.isEmpty(email) && !Utility.isEmailValid(email)) {
            etEmail.setError(getString(R.string.error_invalid_email));
            focusView = etEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(address) || address.length() < 5) {
            etAddress.setError(getString(R.string.error_field_required));
            focusView = etAddress;
            cancel = true;
        } else {
            if (address.length() > 135) {
                etAddress.setError(getString(R.string.error_value_too_long));
                focusView = etAddress;
                cancel = true;
            }
        }

        if (TextUtils.isEmpty(handphone1) || handphone1.length() < 5) {
            etHandphone1.setError(getString(R.string.error_field_required));
            focusView = etHandphone1;
            cancel = true;
        } else {
            if (handphone1.length() > 15) {
                etHandphone1.setError(getString(R.string.error_value_too_long));
                focusView = etHandphone1;
                cancel = true;
            }
        }

        if (!TextUtils.isEmpty(handphone2) && handphone2.length() < 5) {
            etHandphone2.setError(getString(R.string.error_invalid_phone));
            focusView = etHandphone2;
            cancel = true;
        } else {
            if (handphone2.length() > 15) {
                etHandphone2.setError(getString(R.string.error_value_too_long));
                focusView = etHandphone2;
                cancel = true;
            }
        }

        if (cancel) {
            // There was an error; don't attempt save and focus the first
            // form field with an error
            focusView.requestFocus();
            return;
        }

        this.realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                SyncTrnChangeAddr trnSync = realm.where(SyncTrnChangeAddr.class)
                        .equalTo("contractNo", contractNo)
                        .equalTo("seqNo", 2)
                        .isNotNull("syncedDate")
                        .findFirst();

                if (trnSync != null) {
                    Snackbar.make(activityChangeAddr, "Cannot save, Data already synced", Snackbar.LENGTH_SHORT).show();
                    return;
                }

                // drpada repot2 parsing, cek aja mulai dari provinsi
                MstProvinsi mstProvinsi = realm.where(MstProvinsi.class).equalTo("provinsi", etPropinsi.getText().toString()).findFirst();

                if (mstProvinsi == null) {
                    Toast.makeText(ActivityChangeAddress.this, "Invalid Propinsi", Toast.LENGTH_LONG).show();
                    return;
                }

                MstCities mstCities = realm.where(MstCities.class)
                        .equalTo("provCode", mstProvinsi.getProvCode())
                        .equalTo("city", etKabupaten.getText().toString())
                        .findFirst();

                if (mstCities == null) {
                    Toast.makeText(ActivityChangeAddress.this, "Invalid Kabupaten", Toast.LENGTH_LONG).show();
                    return;
                }

                MstKecamatan mstKecamatan = realm.where(MstKecamatan.class)
                        .equalTo("cityCode", mstCities.getCityCode())
                        .equalTo("kecamatan", etKecamatan.getText().toString())
                        .findFirst();

                if (mstKecamatan == null) {
                    Toast.makeText(ActivityChangeAddress.this, "Invalid Kecamatan", Toast.LENGTH_LONG).show();
                    return;
                }


                MstKelurahan mstKelurahan;
                String kelurahanValue = etKelurahan.getText().toString().trim();
                if (kelurahanValue.indexOf('-') > -1) {
                    String[] ss = kelurahanValue.split("-", -1);

                    kelurahanValue = ss[0].trim();
                }

                RealmResults<MstKelurahan> mstKelurahenList = realm.where(MstKelurahan.class)
                        .equalTo("kecCode", mstKecamatan.getKecCode())
                        .equalTo("kelurahan", kelurahanValue)
                        .findAll();

                if (mstKelurahenList.size() != 1) {
                    Toast.makeText(ActivityChangeAddress.this, "Invalid Kelurahan", Toast.LENGTH_LONG).show();
                    return;
                }

                mstKelurahan = realm.copyFromRealm(mstKelurahenList.get(0));

//                Area area = getArea(mstKelurahan);

                TrnChangeAddr trnChangeAddr = realm.where(TrnChangeAddr.class)
                        .equalTo("pk.contractNo", contractNo)
                        .equalTo("pk.seqNo", 2)
                        .equalTo("collId", collectorId)
                        .equalTo("createdBy", Utility.LAST_UPDATE_BY)
//                        .findAllSorted("pk.seqNo")    harusnya
                        .findFirst();

                if (trnChangeAddr == null) {
                    trnChangeAddr = new TrnChangeAddr();

                    TrnChangeAddrPK pk = new TrnChangeAddrPK();
                    pk.setContractNo(contractNo);
                    pk.setSeqNo(2L);
                    trnChangeAddr.setPk(pk);
                } else {
                    // delete because no primary key
                    boolean d = realm.where(TrnChangeAddr.class)
                            .equalTo("pk.contractNo", contractNo)
                            .equalTo("pk.seqNo", 2)
                            .equalTo("collId", collectorId)
                            .equalTo("createdBy", Utility.LAST_UPDATE_BY)
                            .findAll().deleteAllFromRealm();
                    if (!d) {
                        Utility.showDialog(ActivityChangeAddress.this, "Fatal Error", "Cannot delete existing address");
                        return;
                    }
                }

                // TODO: rest of the fields

                trnChangeAddr.setCollId(collectorId);

                trnChangeAddr.setCollCity(mstCities.getCity());
                trnChangeAddr.setCollCityCode(mstCities.getCityCode());
                trnChangeAddr.setCollKec(mstKecamatan.getKecamatan());
                trnChangeAddr.setCollKecCode(mstKecamatan.getKecCode());
                trnChangeAddr.setCollKel(mstKelurahan.getKelurahan());
                trnChangeAddr.setCollKelCode(mstKelurahan.getKelCode());
                trnChangeAddr.setCollProv(mstProvinsi.getProvinsi());
                trnChangeAddr.setCollProvCode(mstProvinsi.getProvCode());
                trnChangeAddr.setCollZip(etZipCode.getText().toString());
                trnChangeAddr.setCollSubZip(etSubZip.getText().toString());

                trnChangeAddr.setCollEmail(etEmail.getText().toString());
                trnChangeAddr.setCollFaxArea(null);
                trnChangeAddr.setCollFixPhone(etFixPhone.getText().toString());
                trnChangeAddr.setCollMobPhone(etHandphone1.getText().toString());
                trnChangeAddr.setCollMobPhone2(etHandphone2.getText().toString());
                trnChangeAddr.setCollName(etNamaPemilik.getText().toString());
                trnChangeAddr.setCollAddr(etAddress.getText().toString());
                trnChangeAddr.setCollRt(etRT.getText().toString());
                trnChangeAddr.setCollRw(etRW.getText().toString());

                trnChangeAddr.setFlagToEmrafin("N");

                trnChangeAddr.setCreatedBy(Utility.LAST_UPDATE_BY);
                trnChangeAddr.setCreatedTimestamp(new Date());
                trnChangeAddr.setLastupdateBy(Utility.LAST_UPDATE_BY);
                trnChangeAddr.setLastupdateTimestamp(new Date());
                realm.copyToRealm(trnChangeAddr);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Toast.makeText(ActivityChangeAddress.this, "Address Changed !", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                Toast.makeText(ActivityChangeAddress.this, "Database Error !", Toast.LENGTH_LONG).show();
            }
        });
    }


}
