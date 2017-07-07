package id.co.ppu.collectionfast2.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import id.co.ppu.collectionfast2.pojo.LKPData;
import id.co.ppu.collectionfast2.pojo.LoginInfo;
import id.co.ppu.collectionfast2.pojo.chat.TrnChatContact;
import id.co.ppu.collectionfast2.pojo.master.MasterData;
import id.co.ppu.collectionfast2.pojo.master.MstDelqReasons;
import id.co.ppu.collectionfast2.pojo.master.MstLDVClassifications;
import id.co.ppu.collectionfast2.pojo.master.MstLDVParameters;
import id.co.ppu.collectionfast2.pojo.master.MstLDVStatus;
import id.co.ppu.collectionfast2.pojo.master.MstParam;
import id.co.ppu.collectionfast2.pojo.master.MstPotensi;
import id.co.ppu.collectionfast2.pojo.trn.HistInstallments;
import id.co.ppu.collectionfast2.pojo.trn.TrnBastbj;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollectAddr;
import id.co.ppu.collectionfast2.pojo.trn.TrnCollectAddrPK;
import id.co.ppu.collectionfast2.pojo.trn.TrnContractBuckets;
import id.co.ppu.collectionfast2.pojo.trn.TrnContractBucketsPK;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVComments;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetails;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVDetailsPK;
import id.co.ppu.collectionfast2.pojo.trn.TrnLDVHeader;
import id.co.ppu.collectionfast2.pojo.trn.TrnPhoto;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVB;
import id.co.ppu.collectionfast2.pojo.trn.TrnRVColl;
import id.co.ppu.collectionfast2.pojo.trn.TrnRepo;
import id.co.ppu.collectionfast2.pojo.trn.TrnVehicleInfo;
import io.realm.Realm;

/**
 * Created by Eric on 06-Feb-17.
 */

public class DemoUtil {

//    public static boolean isDemo(LoginInfo userData) {
    public static boolean isDemo() {
//        return userData != null
//                && userData.getCollectorType() != null
//                && userData.getCollectorType().equals(Utility.ROLE_DEMO);
        Realm realm = Realm.getDefaultInstance();
        try {
//            return realm.where(LoginInfo.class)
//                    .equalTo(Storage.KEY_USER_COLL_TYPE, Utility.ROLE_DEMO)
//                    .count() > 0;
            LoginInfo loginInfo = realm.where(LoginInfo.class)
                    .equalTo("key", Storage.KEY_USER_COLL_TYPE)
                    .findFirst();

            if (loginInfo == null)
                return false;

            return loginInfo.getValue().equals(Utility.ROLE_DEMO);

        }finally {
            if (realm != null) {
                realm.close();
            }
        }

    }

    /*
    public static boolean isDemo(Context ctx) {
        LoginInfo currentUser = (LoginInfo) Storage.getPreference(Storage.KEY_USER, null);

        return isDemo(currentUser);
    }

    @Deprecated
    public static UserData buildDemoUser() {
        UserData demo = new UserData();

        demo.setUserId("demo");
        demo.setBranchId("71000");
        demo.setBranchName("Gading Serpong");
        demo.setEmailAddr("elkana911@radanafinance.co.id");
        demo.setJabatan("Demo Coordinator");
        demo.setNik("F.01.99.1234");
        demo.setAlamat("Faraday Utara 39");
        demo.setPhoneNo("555-1234");
        demo.setCollectorType(Utility.ROLE_DEMO);
        demo.setUserPwd("demo");
        demo.setBirthPlace("Banten");
        demo.setBirthDate(Utility.convertStringToDate("09/08/2016", "dd/MM/yyyy"));
        demo.setMobilePhone("555-123");
        demo.setFullName("John Doe");
        demo.setBussUnit("NMC");

        UserConfig config = new UserConfig();
        config.setUid(java.util.UUID.randomUUID().toString());
//        config.setDeviceId(Utility.getDeviceId(MainActivity.this));
//        config.setImeiDevice();

        demo.setConfig(config);

        return demo;
    }
    */

    public static void buildDemoLoginInfo() {
        Realm realm = Realm.getDefaultInstance();
        try{
            realm.beginTransaction();

            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USERID, "demo"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BRANCH_ID, "71000"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BRANCH_NAME, "Gading Serpong"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_EMAIL, "elkana911@radanafinance.co.id"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_JABATAN, "Demo Coordinator"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_NIK, "F.01.99.1234"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_ADDRESS, "Faraday Utara 5/39"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_PHONE, "555-1234"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_COLL_TYPE, Utility.ROLE_DEMO));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_PASSWORD, "demo"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BIRTH_PLACE, "Banten"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BIRTH_DATE, "20160809"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_FULLNAME, "John Doe"));
            realm.copyToRealmOrUpdate(new LoginInfo(Storage.KEY_USER_BUSS_UNIT, "NMC"));

            realm.commitTransaction();
        }finally {
            if (realm != null) {
                realm.close();
            }
        }
    }

    private static TrnCollectAddr generateTrnCollAddress(String contractNo, long seqNo, String collCode, String collAddress, String collRT, String collRW, String collKelKode, String collKel, String collKecCode, String collKec, String collCityCode, String collCity, String collZip, String collSubZip, String collProvCode, String collProv, String collFixPhArea, String collFixPhone, String collFaxArea, String collFax, String collMobPhone, String collName, String collEmail, String collMobPhone2, String collNickName, String createdBy, Date createdTimestamp, String officeCode) {
        TrnCollectAddr obj = new TrnCollectAddr();

        TrnCollectAddrPK pk = new TrnCollectAddrPK();
        pk.setSeqNo(seqNo);
        pk.setContractNo(contractNo);
        obj.setPk(pk);

        obj.setCollAddr(collAddress);
        obj.setCollRt(collRT);
        obj.setCollRw(collRW);
        obj.setCollKecCode(collKecCode);
        obj.setCollKec(collKec);
        obj.setCollKelCode(collKelKode);
        obj.setCollKel(collKel);
        obj.setCollCityCode(collCityCode);
        obj.setCollCity(collCity);
        obj.setCollZip(collZip);
        obj.setCollSubZip(collSubZip);
        obj.setCollProvCode(collProvCode);
        obj.setCollProv(collProv);
        obj.setCollFixPhArea(collFixPhArea);
        obj.setCollFixPhone(collFixPhone);
        obj.setCollFaxArea(collFaxArea);
        obj.setCollFaximile(collFax);
        obj.setCollMobPhone(collMobPhone);
        obj.setCollEmail(collEmail);
        obj.setCollName(collName);
        obj.setCollMobPhone2(collMobPhone2);
        obj.setCollNickName(collNickName);
        obj.setCreatedBy(createdBy);
        obj.setCreatedTimestamp(createdTimestamp);
//        obj.setOfficeCode

        return obj;
    }

    private static TrnLDVDetails generateTrnLDVDetail(String ldvNo, long seqNo, String period
            , String contractNo, String custNo, String custName
            , Long ovdInstNo, Date ovdDueDate, Date dueDate
            , Long instNo, Long prncAmbc, Long intrAmbc, Long penaltyAmbc
            , Long prncAC, Long intrAC, Long penaltyAC, String ldvFlag
            , String workStatus, Long prncOTS, Date startedTimestamp
            , String createdBy, Date createdTimestamp, String occupation
            , String subOccupation, Long monthInst, Long daysIntrAmbc
            , Long collectionFee, Date lastPaidDate, Long dpd) {
        TrnLDVDetails obj = new TrnLDVDetails();

        TrnLDVDetailsPK pk = new TrnLDVDetailsPK();
        pk.setLdvNo(ldvNo);
        pk.setSeqNo(seqNo);
        obj.setPk(pk);

        obj.setPeriod(period);
        obj.setContractNo(contractNo);
        obj.setCustNo(custNo);
        obj.setCustName(custName);
        obj.setOvdInstNo(ovdInstNo);
        obj.setOvdDueDate(ovdDueDate);
        obj.setDueDate(dueDate);
        obj.setInstNo(instNo);
        obj.setPrincipalAmount(null);
        obj.setInterestAmount(null);
        obj.setPrincipalAMBC(prncAmbc);
        obj.setInterestAMBC(intrAmbc);
        obj.setPenaltyAMBC(penaltyAmbc);
        obj.setPrincipalAmountCollected(prncAC);
        obj.setInterestAmountCollected(intrAC);
        obj.setPenaltyAmountCollected(penaltyAC);
        obj.setLdvFlag(ldvFlag);
        obj.setWorkStatus(workStatus);
        obj.setPrincipalOutstanding(prncOTS);
        obj.setStartedTimestamp(startedTimestamp);
        obj.setCreatedBy(createdBy);
        obj.setCreatedTimestamp(createdTimestamp);
        obj.setOccupation(occupation);
        obj.setSubOccupation(subOccupation);
        obj.setPalNo(null);
        obj.setFlagToEmrafin(null);
        obj.setDateToEmrafin(null);
        obj.setFlagDone(null);
        obj.setDateDone(null);
        obj.setMonthInst(monthInst);
        obj.setDaysIntrAmbc(daysIntrAmbc);
        obj.setCollectionFee(collectionFee);
        obj.setLastPaidDate(lastPaidDate);
        obj.setDpd(dpd);

        return obj;
    }


    private static MstLDVStatus generateMstLDVStatus(String lkpStatus, String statusDesc, Long statusLevel) {
        MstLDVStatus mst = new MstLDVStatus();
        mst.setLkpStatus(lkpStatus);
        mst.setStatusDesc(statusDesc);
        mst.setStatusLevel(statusLevel);
        mst.setStartedTimestamp(new Date());
        mst.setCreatedBy("1120017");
        mst.setCreatedTimestamp(new Date());

        return mst;
    }

    private static MstParam generateMstParam(Long moduleId, String key, String value, String notes) {
        MstParam mst = new MstParam();

        mst.setModuleId(moduleId);
        mst.setKey(key);
        mst.setValue(value);
        mst.setNotes(notes);
        mst.setCreatedTimestamp(new Date());

        return mst;
    }

    public static MasterData buildMasterData() {

        MasterData md = new MasterData();

        List<MstParam> params = new ArrayList<MstParam>();
        params.add(generateMstParam(5L, "DANA SOSIAL SYARIAH", "5000", "DANA SOSIAL CONTRACT SYARIAH"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "1", "Flow Up"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "2", "Stay"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "3", "Pick Up"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "4", "Pelunasan Normal"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "5", "Pelunasan Pretermination"));
        params.add(generateMstParam(10L, "ACTION_PLAN", "6", "Flow Down"));
        params.add(generateMstParam(6L, "MIN_PENALTY_RV", "30000", "Minimal penerimaan denda angsuran outdoor / RV Collector"));
        md.setParams(params);

        List<MstLDVStatus> ldpStatuses = new ArrayList<MstLDVStatus>();
        ldpStatuses.add(generateMstLDVStatus("U", "UNASSIGN", 0L));
        ldpStatuses.add(generateMstLDVStatus("A", "ASSIGN", 1L));
        ldpStatuses.add(generateMstLDVStatus("V", "VISITED", 2L));
        ldpStatuses.add(generateMstLDVStatus("W", "WORK", 3L));
        ldpStatuses.add(generateMstLDVStatus("S", "SELF CURE", 5L));
        ldpStatuses.add(generateMstLDVStatus("P", "APPROVED", 4L));
        md.setLdpStatus(ldpStatuses);


        List<MstLDVParameters> ldpParameters = new ArrayList<MstLDVParameters>();
        ldpParameters.add(generateMstLDVParams("PTP", "Promised To Pay", "Y", "Y", "W", "N", "Y", 7L));
        ldpParameters.add(generateMstLDVParams("PTC", "Promised To Be Collected", "Y", "Y", "W", "Y", "N", 7L));
        ldpParameters.add(generateMstLDVParams("BCO", "Not Meet Customer", "Y", "N", "V", "N", "Y", 7L));
        ldpParameters.add(generateMstLDVParams("UNV", "Unvisited", "N", "N", "A", "N", "Y", 7L));
        ldpParameters.add(generateMstLDVParams("NEW", "New Assignment", "N", "N", "A", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("PTD", "Pay Today Directly", "N", "N", "V", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("COL", "Collected", "N", "N", "W", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("PCU", "Tarik Barang", "Y", "Y", "W", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("PRE", "Pretermination", "Y", "Y", "W", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("BPH", "Bad Phone", "Y", "N", "V", "Y", "N", 7L));
        ldpParameters.add(generateMstLDVParams("UTC", "Unable To Contact", "N", "N", "A", "Y", "N", 7L));
        ldpParameters.add(generateMstLDVParams("MSG", "Message", "Y", "N", "A", "Y", "N", 7L));
        ldpParameters.add(generateMstLDVParams("PTD-D", "Pay To Day-Deskcall", "N", "N", "W", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("FRZ", "Freeze", "N", "Y", "V", "N", "N", 7L));
        ldpParameters.add(generateMstLDVParams("COL2", "Collected not input amount", "Y", "N", "W", "N", "Y", 7L));
        ldpParameters.add(generateMstLDVParams("SKIP", "Skip", "Y", "Y", "W", "N", "Y", 7L));
        md.setLdpParameters(ldpParameters);

        List<MstLDVClassifications> ldpClassifications = new ArrayList<MstLDVClassifications>();
        ldpClassifications.add(generateMstLDVClassification("1", "Nasabah ada, unit ada", "N", "Y"));
        ldpClassifications.add(generateMstLDVClassification("2", "Nasabah ada, unit tidak ada", "N", "Y"));
        ldpClassifications.add(generateMstLDVClassification("3", "Nasabah tidak ada, unit ada", "N", "Y"));
        ldpClassifications.add(generateMstLDVClassification("4", "Nasabah tidak ada, unit tidak ada", "Y", "Y"));
        md.setLdpClassifications(ldpClassifications);

        List<MstDelqReasons> delqReasons = new ArrayList<MstDelqReasons>();
        delqReasons.add(generateMstDelqReason("101", "Konsumen belum terima gaji/Pendapatan", "Y"));
        delqReasons.add(generateMstDelqReason("104", "Konsumen menunggu kiriman uang", "Y"));
        delqReasons.add(generateMstDelqReason("107", "Penurunan usaha konsumen", "Y"));
        delqReasons.add(generateMstDelqReason("110", "Konsumen ke luar kota ", "Y"));
        delqReasons.add(generateMstDelqReason("113", "Customer/keluarga terkena musibah (sakit, kecelakaan dll)", "Y"));
        delqReasons.add(generateMstDelqReason("116", "Uang terpakai kebutuhan penting lainnya", "Y"));
        delqReasons.add(generateMstDelqReason("119", "Customer pindah alamat dan di ketahui", "Y"));
        delqReasons.add(generateMstDelqReason("122", "Customer pindah alamat dan tidak di ketahui", "Y"));
        delqReasons.add(generateMstDelqReason("125", "Susah bayar karena Tempat pembayaran jauh", "Y"));
        delqReasons.add(generateMstDelqReason("128", "Selalu minta di tagih", "Y"));
        delqReasons.add(generateMstDelqReason("131", "Konsumen di PHK", "Y"));
        delqReasons.add(generateMstDelqReason("134", "Usaha konsumen bangkrut", "Y"));
        delqReasons.add(generateMstDelqReason("201", "Tanggal Jatuh Tempo salah", "Y"));
        delqReasons.add(generateMstDelqReason("204", "Angsuran salah (TOP atau Amount)", "Y"));
        delqReasons.add(generateMstDelqReason("207", "STNK / BPKB belum jadi", "Y"));
        delqReasons.add(generateMstDelqReason("210", "Salah nama STNK/BPKB", "Y"));
        delqReasons.add(generateMstDelqReason("213", "PPK belum di terima customer", "Y"));
        delqReasons.add(generateMstDelqReason("216", "Telp tidak update", "Y"));
        delqReasons.add(generateMstDelqReason("219", "No Telp. Salah", "Y"));
        delqReasons.add(generateMstDelqReason("222", "Konsumen tidak layak kredit", "Y"));
        delqReasons.add(generateMstDelqReason("225", "Alamat tidak jelas", "Y"));
        delqReasons.add(generateMstDelqReason("228", "Alamat tidak pernah ada / Alamat Fiktif", "Y"));
        delqReasons.add(generateMstDelqReason("231", "Kontrak fiktif", "Y"));
        delqReasons.add(generateMstDelqReason("301", "pembayaran Payment point belum update (dispute)", "Y"));
        delqReasons.add(generateMstDelqReason("304", "Sudah bayar masuk titipan angsuran", "Y"));
        md.setDelqReasons(delqReasons);

        List<MstPotensi> potensis = new ArrayList<MstPotensi>();
        potensis.add(generateMstPotensi("G", "1", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("G", "1", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("G", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("G", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("G", "2", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));

        potensis.add(generateMstPotensi("H", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("H", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("H", "2", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));

        potensis.add(generateMstPotensi("I", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("I", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("I", "2", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("J", "1", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("J", "1", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("J", "1", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("J", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("J", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("J", "2", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("J", "3", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("J", "3", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("J", "3", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));

        potensis.add(generateMstPotensi("K", "1", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("K", "1", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        potensis.add(generateMstPotensi("K", "1", 3L, 100L, "Tingkat Keberhasilan Penagihan 100 %", "Y"));
        potensis.add(generateMstPotensi("K", "2", 1L, 0L, "Tingkat Keberhasilan Penagihan 0 %", "Y"));
        potensis.add(generateMstPotensi("K", "2", 2L, 50L, "Tingkat Keberhasilan Penagihan 50 %", "Y"));
        md.setPotensi(potensis);
        return md;
    }

    private static MstPotensi generateMstPotensi(String delqId, String classCode, Long seqNo, Long potensi, String potensiDesc, String isActive) {
        MstPotensi mst = new MstPotensi();
        mst.setDelqId(delqId);
        mst.setClassCode(classCode);
        mst.setSeqNo(seqNo);
        mst.setPotensi(potensi);
        mst.setPotensiDesc(potensiDesc);
        mst.setIsActive(isActive);
        return mst;
    }

    private static MstDelqReasons generateMstDelqReason(String delqCode, String description, String visible) {
        MstDelqReasons mst = new MstDelqReasons();

        mst.setDelqCode(delqCode);
        mst.setDescription(description);
        mst.setVisible(visible);
        mst.setCreatedTimestamp(new Date());
        mst.setCreatedBy("ERIC");

        return mst;
    }

    private static MstLDVParameters generateMstLDVParams(String lkpFlag, String description, String needComment, String needDate, String workFlag, String needCollect, String isActive, Long maxPromiseDays) {
        MstLDVParameters mst = new MstLDVParameters();
        mst.setLkpFlag(lkpFlag);
        mst.setDescription(description);
        mst.setNeedComment(needComment);
        mst.setNeedDate(needDate);
        mst.setWorkFlag(workFlag);
        mst.setNeedCollect(needCollect);
        mst.setIsActive(isActive);
        mst.setMaxPromiseDays(maxPromiseDays);
        mst.setCreatedBy("ERIC");
        mst.setCreatedTimestamp(new Date());

        return mst;
    }

    private static MstLDVClassifications generateMstLDVClassification(String classCode, String description, String needSpecialCollect, String visible) {
        MstLDVClassifications mst = new MstLDVClassifications();

        mst.setClassCode(classCode);
        mst.setDescription(description);
        mst.setNeedSpecialCollect(needSpecialCollect);
        mst.setVisible(visible);
        mst.setStartedTimetamp(new Date());
        mst.setCreatedBy("HD1");
        mst.setCreatedTimestamp(new Date());
        return mst;
    }

    private static TrnRVB generateTrnRVB(String rvbNo, String officeCode, String collCode, String createdBy, Date createdTimestamp) {
        TrnRVB trn = new TrnRVB();
        trn.setRvbNo(rvbNo);
        trn.setOfficeCode(officeCode);
        trn.setRvbDate(new Date());
        trn.setRvbOnHand(collCode);
        trn.setRvbStatus("OP");
        trn.setCreatedBy(createdBy);
        trn.setCreatedTimestamp(createdTimestamp);

        return trn;
    }

    public static LKPData buildLKP(final Date lkpDate, final String collCode, final String officeCode, final String createdBy) {

        LKPData data = new LKPData();
        TrnLDVHeader header = new TrnLDVHeader();

        header.setLdvNo("71000" + Utility.convertDateToString(lkpDate, "yyyyMMdd") + "026160172");
//        header.setLdvNo("7100020160829026160172");
        header.setLdvDate(lkpDate);
        header.setOfficeCode(officeCode);
        header.setCollCode(collCode);
        header.setUnitTotal(5L);   // brarti ada 5 lkp
        header.setPrncAC(23395481L);
        header.setPrncAC(0L);
        header.setIntrAMBC(14286519L);
        header.setIntrAC(0L);
        header.setAmbcTotal(37682000L);
        header.setAcTotal(0L);
        header.setWorkFlag("A");
        header.setStartedTimestamp(lkpDate);
        header.setCreatedBy(createdBy);
        header.setCreatedTimestamp(lkpDate);

        data.setHeader(header);

        List<TrnLDVDetails> details = new ArrayList<TrnLDVDetails>();

        String period = Utility.convertDateToString(lkpDate, "yyyyMM");

        Date ovdDueDate = new Date();
        Date dueDate = new Date();

        Date createdTimestamp = new Date();
        Date startedTimestamp = new Date();
        //1
        details.add(generateTrnLDVDetail(header.getLdvNo(), 86L, period, "71000000008115", "71000150000027", "MALAN"
                , 17L, ovdDueDate, dueDate, 16L, 1193882L, 1446118L, 350300L, 0L, 0L, 0L, "NEW", "A", 281350L, startedTimestamp, createdBy, createdTimestamp
                , "Pekerja Lepas/Freelance (>=1)", "FREELANCE"
                , 660000L, 336600L, null, null, 102L));
        //2
        details.add(generateTrnLDVDetail(header.getLdvNo(), 87L, period, "71000000069115", "71000150000555", "RUDI EFENDI"
                , 14L, ovdDueDate, dueDate, 13L, 951098L, 1488902L, 678500L, 0L, 0L, 0L, "NEW", "A", 223658L, startedTimestamp, createdBy, createdTimestamp
                , "Kry Swasta Tetap", "MEKANIK"
                , 610000L, 353800L, 10000L, null, 116L));
        //3
        details.add(generateTrnLDVDetail(header.getLdvNo(), 88L, period, "71000000034615", "71000150000254", "RASIDIN"
                , 16L, ovdDueDate, dueDate, 15L, 934575L, 1465425L, 417000L, 0L, 0L, 0L, "NEW", "A", 219758L, startedTimestamp, createdBy, createdTimestamp
                , "Pekerja Lepas/Freelance (>=1)", "PEMBANTU RUMAH TANGGA"
                , 600000L, 309000L, 10000L, null, 103L));
        //4
        details.add(generateTrnLDVDetail(header.getLdvNo(), 89L, period, "71000900029814", "71000140002255", "SUNIK"
                , 22L, ovdDueDate, dueDate, 21L, 1966547L, 929453L, 1161760L, 0L, 0L, 0L, "NEW", "A", 461931L, startedTimestamp, createdBy, createdTimestamp
                , "Kry Swasta Kontrak (>=1)", "Perusahaan Swasta"
                , 724000L, 427160L, null, null, 118L));
        //5
        details.add(generateTrnLDVDetail(header.getLdvNo(), 90L, period, "71000000349714", "71000140004033", "BUSRON"
                , 19L, ovdDueDate, dueDate, 18L, 1901013L, 1498987L, 1097750L, 0L, 0L, 0L, "NEW", "A", 464567L, startedTimestamp, createdBy, createdTimestamp
                , "Kry Swasta Tetap", "Perusahaan Swasta"
                , 858000L, 323000L, null, null, 76L));
        /*
        details.add(generateTrnLDVDetail(header.getLdvNo(), 91L, period, "71000000192715", "71000150001452", "GUSTIANA"
                , 13L, ovdDueDate, dueDate, 12L, 1883295L, 1548705L, 1900650L, 0L, 0L, 0L, "NEW", "A", 480831L, startedTimestamp, createdBy, createdTimestamp
                , "Wiraswasta (Tempat usaha sendiri)", "PEDAGANG MAKANAN"
                , 858000L, 193050L, 10000L, null, 45L));
        details.add(generateTrnLDVDetail(header.getLdvNo(), 92L, period, "71000000279114", "71000140003373", "DJARDAWATI"
                , 21L, ovdDueDate, dueDate, 20L, 1286020L, 1381980L, 2890715L, 0L, 0L, 0L, "NEW", "A", 314910L, startedTimestamp, createdBy, createdTimestamp
                , "Kry Pemerintah Tetap", "Badan-badan dan lembaga-lembaga pemerintah"
                , 667000L, 216775L, null, null, 65L));
        details.add(generateTrnLDVDetail(header.getLdvNo(), 93L, period, "71000900009014", "71000140001900", "ACHMAD ISHAK"
                , 24L, ovdDueDate, dueDate, 23L, 1580526L, 903474L, 2177005L, 0L, 0L, 0L, "NEW", "A", 388774L, startedTimestamp, createdBy, createdTimestamp
                , "Pekerja Lepas/Freelance (>=1)", "FREELANCE"
                , 621000L, 267030L, null, null, 86L));
        details.add(generateTrnLDVDetail(header.getLdvNo(), 94L, period, "71000900013114", "71000140002002", "CHANDRAR"
                , 23L, ovdDueDate, dueDate, 22L, 1398533L, 1085467L, 1820515L, 0L, 0L, 0L, "NEW", "A", 329104L, startedTimestamp, createdBy, createdTimestamp
                , "Pekerja Lepas/Freelance (>=1)", "FREELANCE"
                , 621000L, 338445L, null, null, 109L));
        details.add(generateTrnLDVDetail(header.getLdvNo(), 95L, period, "71000000267113", "71000130002520", "ANIS"
                , 31L, ovdDueDate, dueDate, 30L, 1708811L, 651189L, 3035950L, 0L, 0L, 0L, "NEW", "A", 418764L, startedTimestamp, createdBy, createdTimestamp
                , "Pekerja Lepas/Freelance (>=1)", "FREELANCE"
                , 590000L, 209450L, null, null, 71L));
        details.add(generateTrnLDVDetail(header.getLdvNo(), 96L, period, "71000000124514", "71000140001175", "AAN ANSORI"
                , 26L, ovdDueDate, dueDate, 25L, 2452654L, 579346L, 3160110L, 0L, 0L, 0L, "NEW", "A", 600842L, startedTimestamp, createdBy, createdTimestamp
                , "Wiraswasta (Tempat usaha sendiri)", "Perseorangan (Sektor Swasta)"
                , 758000L, 272880L, null, null, 72L));
        details.add(generateTrnLDVDetail(header.getLdvNo(), 97L, period, "71000000183914", "71000140001485", "HERI"
                , 25L, ovdDueDate, dueDate, 24L, 1278061L, 621939L, 738500L, 0L, 0L, 0L, "NEW", "A", 313245L, startedTimestamp, createdBy, createdTimestamp
                , "Pekerja Lepas/Freelance (>=1)", "FREELANCE"
                , 475000L, 159125L, null, null, 67L));
        details.add(generateTrnLDVDetail(header.getLdvNo(), 98L, period, "71000000264414", "71000140003173", "THEO FREDYTUS REVANO"
                , 20L, ovdDueDate, dueDate, 19L, 2994886L, 365114L, 108300L, 0L, 0L, 0L, "NEW", "A", 697276L, startedTimestamp, createdBy, createdTimestamp
                , "Kry Swasta Tetap", "Perusahaan Swasta"
                , 840000L, 449400L, 10000L, null, 107L));
        details.add(generateTrnLDVDetail(header.getLdvNo(), 99L, period, "71000000121915", "71000140004127", "ZAINAH"
                , 13L, ovdDueDate, dueDate, 12L, 984032L, 271968L, 366850L, 0L, 0L, 0L, "NEW", "A", 226226L, startedTimestamp, createdBy, createdTimestamp
                , "Kry Swasta Tetap", "CLEANING SERVICE"
                , 314000L, 160140L, 10000L, null, 102L));
        details.add(generateTrnLDVDetail(header.getLdvNo(), 100L, period, "71000000306114", "71000140003500", "ADY KURNIAWAN"
                , 19L, ovdDueDate, dueDate, 18L, 881548L, 48452L, 3062100L, 0L, 0L, 0L, "NEW", "A", 881548L, startedTimestamp, createdBy, createdTimestamp
                , "Wiraswasta (Tempat usaha sendiri)", "Perseorangan (Sektor Swasta)"
                , 930000L, 502200L, null, null, 108L));
                */
        data.setDetails(details);

        List<TrnCollectAddr> addresses = new ArrayList<TrnCollectAddr>();
        addresses.add(generateTrnCollAddress("71000000008115", 1L, collCode, "JALAN IMPRES", "016", "003", "30771015", "Perajen", "3201020", "BanyuasinI", "3201", "Kab Banyu Asin", "30771", "015", "32", "Prop Sumatera Selatan", "0711", "0", null, null, "081271516996", null, "MALAN", "081271516996", "MALAN", createdBy, createdTimestamp, officeCode));
        addresses.add(generateTrnCollAddress("71000000069115", 1L, collCode, "JALAN KANCIL PUTIH II", "010", "037", "30135002", "Demang Lebar Daun", "3220030", "IlirBaratI", "3220", "Kota Palembang Plaju", "30135", "002", "32", "Prop Sumatera Selatan", "0711", "0", null, null, "08977777046", null, "RUDI EFENDI", null, "RUDI EFENDI", createdBy, createdTimestamp, officeCode));
        addresses.add(generateTrnCollAddress("71000000034615", 1L, collCode, "DESA TIRTO RAHARJO", "004", "003", "30975016", "Tirto Raharjo", "3201070", "Muara Padang", "3201", "Kab Banyu Asin", "30975", "016", "32", "Prop Sumatera Selatan", "0711", "0", null, null, "082375838457", null, "RASIDIN", "082375838547", "RASIDIN", createdBy, createdTimestamp, officeCode));
        addresses.add(generateTrnCollAddress("71000900029814", 1L, collCode, "LORONG SENTOSA JAYA NOMOR 1123", "029", "007", "30266001", "Tangga Takat", "3220140", "SeberangUluIi", "3220", "Kota Palembang Plaju", "30266", "001", "32", "Prop Sumatera Selatan", "0711", "0", null, null, "082183047537", null, "SUNIK", "082183047537", "SUNIK", createdBy, createdTimestamp, officeCode));
        addresses.add(generateTrnCollAddress("71000000349714", 1L, collCode, "JLN JOKO ATAS NO 927", "027", "011", "30143001", "29Ilir", "3220040", "IlirBaratIi", "3220", "Kota Palembang Plaju", "30143", "001", "32", "Prop Sumatera Selatan", "0711", "0", null, null, "08127303577", null, "BUSRON", null, "BUSRON", createdBy, createdTimestamp, officeCode));
        data.setAddress(addresses);

        List<TrnBastbj> bastbjs = new ArrayList<TrnBastbj>();
        data.setBastbj(bastbjs);

        String supervisorId = "21080093";
        List<TrnContractBuckets> buckets = new ArrayList<TrnContractBuckets>();
        // hanya berlaku non lkp inquiry
        if (Utility.isSameDay(lkpDate, new Date())) {

            buckets.add(generateTrnContractBucket(period, "11000000327715", officeCode, supervisorId, collCode, "0000021706", "MAPURO", 33L, 18L, Utility.convertStringToDate("20170220", "yyyyMMdd"), Utility.convertStringToDate("20170218", "yyyyMMdd"), Utility.convertStringToDate("20170309", "yyyyMMdd"), 19L, -20L, 441279L, 368721L, 423768L, null, 4000L, 9347017L, "S", startedTimestamp, createdBy, createdTimestamp, "1100017J007513", 423768L, 386232L, 10000L, "K"));

            buckets.add(generateTrnContractBucket(period, "11000000470415", officeCode, supervisorId, collCode, "0000022491", "ZULKIFLI", 26L, 14L, Utility.convertStringToDate("20170206", "yyyyMMdd"), Utility.convertStringToDate("20170206", "yyyyMMdd"), Utility.convertStringToDate("20170304", "yyyyMMdd"), 15L, -4L, 516967L, 112087L, 495983L, null, 8500L, 8367494L, "A", startedTimestamp, createdBy, createdTimestamp, "1201017R002195", 495983L, 354017L, 10000L, "K"));

            buckets.add(generateTrnContractBucket(period, "11000000523316", officeCode, supervisorId, collCode, "0000028314", "RASIMAN", 33L, 14L, Utility.convertStringToDate("20170209", "yyyyMMdd"), Utility.convertStringToDate("20170206", "yyyyMMdd"), Utility.convertStringToDate("20170306", "yyyyMMdd"), 2L, -6L, 977696L, 1116304L, 941163L, null, 0L, 29700000L, "W", startedTimestamp, createdBy, createdTimestamp, "1100017R003548", 941163L, 1152837L, 10000L, "K"));
        }
        data.setBuckets(buckets);

        List<HistInstallments> histInstallments = new ArrayList<HistInstallments>();
        data.setHistoryInstallments(histInstallments);

        List<TrnLDVComments> ldvComments = new ArrayList<TrnLDVComments>();
        data.setLdvComments(ldvComments);

        List<TrnRepo> repos = new ArrayList<TrnRepo>();
        data.setRepo(repos);

        List<TrnRVB> rvbs = new ArrayList<TrnRVB>();
        // hanya berlaku non lkp inquiry
        if (Utility.isSameDay(lkpDate, new Date())) {
            rvbs.add(generateTrnRVB("7100016K1183025", officeCode, collCode, createdBy, createdTimestamp));
            rvbs.add(generateTrnRVB("7100016K1183026", officeCode, collCode, createdBy, createdTimestamp));
            rvbs.add(generateTrnRVB("7100016K1183027", officeCode, collCode, createdBy, createdTimestamp));
            rvbs.add(generateTrnRVB("7100016K1183028", officeCode, collCode, createdBy, createdTimestamp));
            rvbs.add(generateTrnRVB("7100016K1183722", officeCode, collCode, createdBy, createdTimestamp));
            rvbs.add(generateTrnRVB("7100016K1183723", officeCode, collCode, createdBy, createdTimestamp));
        }
        data.setRvb(rvbs);

        List<TrnRVColl> rvColls = new ArrayList<TrnRVColl>();
        data.setRvColl(rvColls);

        List<TrnVehicleInfo> vehicleInfos = new ArrayList<TrnVehicleInfo>();
        data.setVehicleInfo(vehicleInfos);

        List<TrnPhoto> photos = new ArrayList<TrnPhoto>();
        data.setPhoto(photos);

        return data;

    }

    private static TrnContractBuckets generateTrnContractBucket(String period, String contractNo, String officeCode, String supervisorId, String collCode, String custNo, String custName
            , Long top, Long instNo, Date dueDate, Date paidDate, Date ovdDueDate, Long ovdInstNo, Long dpd, Long prncAmt, Long intrAmt, Long prncAC, Long intrAC, Long penaltyAC, Long prncOTS, String lkpStatus, Date startedTimestamp, String createdBy, Date createdTimestamp, String rvNo, Long prncAmbc, Long intrAmbc, Long collectionFee, String platform) {
        TrnContractBuckets trn = new TrnContractBuckets();

        TrnContractBucketsPK pk = new TrnContractBucketsPK();
        pk.setContractNo(contractNo);
        pk.setOfficeCode(officeCode);
        pk.setPeriod(period);
        trn.setPk(pk);

        trn.setCollectionFee(collectionFee);
        trn.setCollectorId(collCode);
        trn.setContractStatus("AC");
        trn.setCustName(custName);
        trn.setCustNo(custNo);
        trn.setDpd(dpd);
        trn.setDueDate(dueDate);
        trn.setInstNo(instNo);
        trn.setIntrAc(intrAC);
        trn.setIntrAmt(intrAmt);
        trn.setIntrAMBC(intrAmbc);

        trn.setDanaSosial(0L);
        trn.setLkpStatus(lkpStatus);
        trn.setOvdDueDate(ovdDueDate);
        trn.setOvdInstNo(ovdInstNo);
        trn.setPlatform(platform);

        trn.setDpd(dpd);
        trn.setPenaltyAC(penaltyAC);
        trn.setPaidDate(paidDate);

        trn.setPrncAc(prncAC);
        trn.setPrncAMBC(prncAmbc);
        trn.setPrncAmt(prncAmt);
        trn.setPrncOTS(prncOTS);

        trn.setRvNo(rvNo);
        trn.setSupervisorId(supervisorId);
        trn.setTop(top);

        trn.setStartedTimestamp(startedTimestamp);
        trn.setCreatedBy(createdBy);
        trn.setCreatedTimestamp(createdTimestamp);

        return trn;
    }

    private static TrnChatContact generateTrnChatContact(String collCode, String nickName, String room, String statusMsg) {
        TrnChatContact trn = new TrnChatContact();
        trn.setCollCode(collCode);
        trn.setAndroidId(UUID.randomUUID().toString());
        trn.setContactType("BOT");
        trn.setNickName(nickName);
        trn.setRoom(room);
        trn.setStatusMsg(statusMsg);

        return trn;
    }

    public static List<TrnChatContact> buildDummyChatContacts() {

        List<TrnChatContact> dummyData = new ArrayList<>();

        dummyData.add(generateTrnChatContact("10462478", "Leo Nardo Hutabarat", "123", "Available"));
        dummyData.add(generateTrnChatContact("29846289", "Michelle Duo", "123", "Semangatttt"));
        dummyData.add(generateTrnChatContact("38501527", "Fransisco Torrent", "123", "Ha ha ha"));

        return dummyData;
    }

}
