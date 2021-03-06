package com.aiton.pestscontrolandroid;

import com.jeremyliao.liveeventbus.core.LiveEvent;

public class AppConstance {
    public static final String NEWS_URL = "";
    public static final String APP_HTTP = "";
    public static final String APP_MAOHAO = "";
    public static final String APP_URL = "";

    public static final int CAMERAX_REQUEST_CODE_PIC1 = 0x00000004;
    public static final int CAMERAX_REQUEST_CODE_PIC2 = 0x00000005;
    public static final int CAMERAX_REQUEST_CODE_FELL = 0x00000001;
    public static final int CAMERAX_REQUEST_CODE_STUMP = 0x00000002;
    public static final int CAMERAX_REQUEST_CODE_FINISH = 0x00000003;

    public static final String APP_SP = "APP_SP";
    public static final String USER_MODEL = "USER_MODEL";
    public static final String SETTING_MODEL = "SETTING_MODEL";
    public final static  String TAG = "PESTS";
    public final static  String TAG_ME = "TAG_ME";

    public static final String SHP_FILE = "SHP_FILE";

    public static final String AMAP_MAP = "AMAP";
    public static final String GOOGLE_MAP = "GOOGLE_MAP";
    public static final String OSM_MAP = "OSM_MAP";
    public static final String ARCGIS_MAP = "ARCGIS_MAP";
    public static final String TIANDI_MAP = "TIANDI_MAP";
    public static final String GEO_MAP = "GEO_MAP";
    public static final int AMAP_MAP_LAYER_INDEX = 1;
    public static final int GOOGLE_MAP_LAYER_INDEX = 2;
    public static final int OSM_MAP_LAYER_INDEX = 3;
    public static final int ARCGIS_MAP_LAYER_INDEX = 4;
    public static final int TIANDI_MAP_LAYER_INDEX = 5;
    public static final int GEO_MAP_LAYER_INDEX = 6;
    public static final String FEATURE_ATTRIBUTE = "FEATURE_ATTRIBUTE";
    public static final String PESTSMODEL = "PestsModel";
    public static final String TRAPMODEL = "TrapModel";
    public static final String FEATURE_ATTRIBUTE_MAP = "FEATURE_ATTRIBUTE_MAP";
    // Aliyun OSS
    public static final String ACCESS_KEY_ID = "LTAI4GBswqfEBwAotuWDWt66";
    public static final String ACCESS_KEY_SECRET = "SUAMYOVAWZmyovSZPxut98OPrxz70p";
    public static final String ENDPOINT = "oss-cn-hangzhou.aliyuncs.com";
    public static final String BUCKETNAME = "pestscontrol";

    public static final boolean isTest = false;
    // 1 ?????????????????????????????? ??? 0 ??????????????????
    public static final String ISTEST = "ISTEST";

    public static final String SELECTED_FEATURE = "SELECTED_FEATURE";
    // ???
    public static final String JYXZCNAME = "JYXZCNAME";
    // ???
    public static final String CGQNAME = "CGQNAME";
    public static final String DBH = "DBH";
    public static final String XBH = "XBH";
    public static final String LONGITUDE = "LONGITUDE";
    public static final String LATIDUTE = "LATIDUTE";

    // https://8222.qiter.com.cn/
    //https://peststest.cn.utools.club/
    public static final String URL_APP = "https://8222.qiter.com.cn/";
    public static final String URL_PESTS_SAVE = "educenter/pests/addPestsControl";
    public static final String URL_PESTS_FINDALL_CALL = "educenter/pests/findAll/call";
    public static final String URL_PESTS_FINDALL = "educenter/pests/findAll";
    public static final String URL_PESTS_CHECKUPLOAD = "educenter/pests/getByQrcodeAndLatLonAndUserIdAndStime/{qrcode}/{lat}/{lon}/{userId}/{stime}";
    public static final String URL_ALIVE = "educenter/alive";

    public static final String URL_DICT_NAME = "educms/dictionary/getByName/{name}";

    public static final String URL_QRCODE_INT = "educenter/qrcode/getOneByQrcode/{codeNumber}";

    public static final String URL_TRAP_SAVE = "educenter/trap/add";
    public static final String URL_TRAP_FINDALL_CALL = "educenter/trap/findAll/call";
    public static final String URL_TRAP_FINDALL = "educenter/trap/findAll";
    public static final String URL_TRAP_CHECKUPLOAD = "educenter/trap/getByQrcodeAndLatLonAndUserIdAndStime/{qrcode}/{lat}/{lon}/{userId}/{stime}";

    public static final String UCENTER_MEMBER_MODEL = "UCENTER_MEMBER_MODEL";
    public static final String URL_USER_LOGIN = "educenter/member/login/{mobile}/{password}";
    public static final String URL_USER_GET_MEMBER_INFO = "educenter/member/getMemberInfo";
    public static final String API_KEY = "runtimelite,1000,rud6848143592,none,PM0RJAY3FLB5JPJA4066";
    public static final String OPERATOR = "OPERATOR";
    public static final String OPERATOR_DEFAULT = "OPERATOR_DEFAULT";
    public static final String PESTS_TYPE_DEFAULT = "PESTS_TYPE_DEFAULT";
    public static final String PESTS_OPERATOR_DEFAULT = "PESTS_OPERATOR_DEFAULT";
    public static final String TIANDIMAP = "TIANDIMAP";
    public static final String DICT_NAME_PESTS_TYPE = "pests.pests.type";
    public static final String DICT_NAME_PESTS_GEODATABASE = "pests.geodatabase.file";
    public static final String TRAP_REMARK = "TRAP_REMARK";
    public static final String URL_TRAP_GET_QRCODE = "educenter/trap/getByQrcode/{qrcode}";
    public static final String AUTO_UPLOAD = "AUTO_UPLOAD";             //????????????????????????
    public static final String AUTO_UPLOAD_TRAP = "AUTO_UPLOAD_TRAP";
    public static final String WORKMANAGER_KEY = "WORKMANAGER_KEY";
    public static final String PESTS_ALL_ONCE_WORK_NOTIFICATION = "PESTS_ALL_ONCE_WORK_NOTIFICATION";
    public static final String WORK_NOTIFICATION_AUTO_UPLOAD = "WORK_NOTIFICATION_AUTO_UPLOAD";
    public static final String OK = "OK";
    public static final String NO = "NO";
    public static final String TRAP_ALL_ONCE_WORK_NOTIFICATION = "TRAP_ALL_ONCE_WORK_NOTIFICATION";

    public static final int STARTACTIVITY_MAINACTIVITY_TRAPACTIVITY = 111;
    public static final int STARTACTIVITY_MAINACTIVITY_PESTSACTIVITY = 112;
    public static final String LOAD_SHP = "LOAD_SHP";
    public static final String IS_SCAN = "IS_SCAN";
}
