package id.co.ppu.collectionfast2.lkp;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import id.co.ppu.collectionfast2.R;
import id.co.ppu.collectionfast2.component.BasicActivity;
import id.co.ppu.collectionfast2.pojo.Area;
import id.co.ppu.collectionfast2.pojo.MstCities;
import id.co.ppu.collectionfast2.pojo.MstKecamatan;
import id.co.ppu.collectionfast2.pojo.MstKelurahan;
import id.co.ppu.collectionfast2.pojo.MstProvinsi;
import id.co.ppu.collectionfast2.pojo.MstZip;
import id.co.ppu.collectionfast2.pojo.TrnChangeAddr;
import id.co.ppu.collectionfast2.pojo.TrnChangeAddrPK;
import id.co.ppu.collectionfast2.pojo.TrnLDVDetails;
import id.co.ppu.collectionfast2.rest.ApiInterface;
import id.co.ppu.collectionfast2.rest.ServiceGenerator;
import id.co.ppu.collectionfast2.rest.request.RequestArea;
import id.co.ppu.collectionfast2.rest.response.ResponseArea;
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

        etKelurahan.setThreshold(1);

        if (!NetUtil.isConnected(this)) {
            Utility.showDialog(this, "Error", getString(R.string.error_online_required));
        }
    }

    private synchronized void retrieveAreaFromServer(String kelurahanOrZipCode) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

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

                }else{
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
    private synchronized void retrieveKelurahanFromServer(String kelurahanOrZipCode) {

        final ProgressDialog mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
        mProgressDialog.setMessage("Please wait...");
        mProgressDialog.show();

        ApiInterface fastService =
                ServiceGenerator.createService(ApiInterface.class, Utility.buildUrl(Storage.getPreferenceAsInt(getApplicationContext(), Storage.KEY_SERVER_ID, 0)));

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

                }else{
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

    private Area getArea(MstKelurahan kelurahan) {

        Area area = new Area();

        MstKecamatan kec = realm.where(MstKecamatan.class).equalTo("kecCode", kelurahan.getKecCode()).findFirst();
        area.setKecamatan(realm.copyFromRealm(kec));

        MstCities city = realm.where(MstCities.class).equalTo("cityCode", kec.getCityCode()).findFirst();
        area.setCity(realm.copyFromRealm(city));

        MstProvinsi prov = realm.where(MstProvinsi.class).equalTo("provCode", city.getProvCode()).findFirst();
        area.setProvinsi(realm.copyFromRealm(prov));

        RealmResults<MstZip> zipList = realm.where(MstZip.class)
                .equalTo("provCode", prov.getProvCode())
                .equalTo("cityCode", city.getCityCode())
                .equalTo("kecCode", kec.getKecCode())
                .equalTo("kelCode", kelurahan.getKelCode())
                .findAll();
        List<MstZip> zips = realm.copyFromRealm(zipList);
        area.setZip(zips);

        return area;
    }

    private void loadArea(MstKelurahan kelurahan) {

        Area area = getArea(kelurahan);

        etKecamatan.setText(area.getKecamatan().getKecamatan());
        etKabupaten.setText(area.getCity().getCity());
        etPropinsi.setText(area.getProvinsi().getProvinsi());

        if (area.getZip() != null && area.getZip().size() > 0) {
            etZipCode.setText(area.getZip().get(0).getPk().getZipCode());
        }else
            etZipCode.setText(null);

    }


    @OnClick(R.id.btnCari)
    public void onClickCariKelurahan() {

        if (etKelurahan.getText().toString().length() < 2) return;


        RealmResults<MstKelurahan> all = realm.where(MstKelurahan.class).findAll();
        for (MstKelurahan kel : all) {
            Log.e("eric", kel.getKelurahan());
        }

        RealmResults<MstKelurahan> kelurahanList = realm.where(MstKelurahan.class).contains("kelurahan", etKelurahan.getText().toString(), Case.INSENSITIVE).findAll();

        etKelurahan.setAdapter(null);

        if (kelurahanList.size() < 1) {
//            retrieveKelurahanFromServer(etKelurahan.getText().toString());
            retrieveAreaFromServer(etKelurahan.getText().toString());
            return;
        }

        if (kelurahanList.size() == 1) {
            loadArea(kelurahanList.get(0));
        }

        List<String> list = new ArrayList<>();
        for (MstKelurahan b : kelurahanList) {
            list.add(b.getKelurahan());
        }
        // sort
//                Collections.sort(list);

        // override color of text item
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

    @OnClick(R.id.btnSave)
    public void onClickSave() {
        // reset errors
        etEmail.setError(null);

        boolean cancel = false;
        View focusView = null;

        // Store values at the time of save attempt
        final String email = etEmail.getText().toString();
        final String address = etAddress.getText().toString();

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

                MstKelurahan kelurahan = realm.where(MstKelurahan.class).equalTo("kelurahan", etKelurahan.getText().toString()).findFirst();

                if (kelurahan == null) {
                    Toast.makeText(ActivityChangeAddress.this, "Invalid Kelurahan", Toast.LENGTH_LONG).show();
                    return;
                }

                Area area = getArea(kelurahan);

                TrnChangeAddr trnChangeAddr = new TrnChangeAddr();
                // TODO: rest of the fields
                TrnChangeAddrPK pk = new TrnChangeAddrPK();
                pk.setContractNo(contractNo);
                pk.setSeqNo(1L);
                trnChangeAddr.setPk(pk);

                trnChangeAddr.setCollId(collectorId);

                trnChangeAddr.setCollAddr(etAddress.getText().toString());
                trnChangeAddr.setCollCity(area.getCity().getCity());
                trnChangeAddr.setCollCityCode(area.getCity().getCityCode());
                trnChangeAddr.setCollKec(area.getKecamatan().getKecamatan());
                trnChangeAddr.setCollKecCode(area.getKecamatan().getKecCode());
                trnChangeAddr.setCollKel(area.getKelurahan().getKelurahan());
                trnChangeAddr.setCollKelCode(area.getKelurahan().getKelCode());
                trnChangeAddr.setCollProv(area.getProvinsi().getProvinsi());
                trnChangeAddr.setCollProvCode(area.getProvinsi().getProvCode());
                trnChangeAddr.setCollZip(null);
                trnChangeAddr.setCollSubZip(null);

                trnChangeAddr.setCollEmail(etEmail.getText().toString());
                trnChangeAddr.setCollFaxArea(null);
                trnChangeAddr.setCollFixPhArea(etFixPhone.getText().toString());
                trnChangeAddr.setCollMobPhone(etHandphone1.getText().toString());
                trnChangeAddr.setCollName(etNamaPemilik.getText().toString());
                trnChangeAddr.setCollRt(etRT.getText().toString());
                trnChangeAddr.setCollRw(etRW.getText().toString());

                trnChangeAddr.setCreatedBy(Utility.LAST_UPDATE_BY);
                trnChangeAddr.setLastupdateBy(Utility.LAST_UPDATE_BY);
//                realm.copyToRealm(trnChangeAddr);
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
