package com.CL.slcscanner.Utils;

/**
 * Created by vrajesh on 3/9/2018.
 */

public class AppConstants {

    //live
    public static final String BASE_URL = "http://apps.cimconlighting.com/";

    //5.2.6 / 5.3.0 - api-v4
    public static final String SUB_URL = "SLCScannerV2/api-v4/";//api-v3/";//api-v2/

    //5.3.1 api-beta
    //public static final String SUB_URL = "SLCScannerV2/api-beta/";

    //public static final String SUB_URL = "SLCScannerV2/api-v1/";//api/
    //public static final String BASE_URL = "http://184.72.226.73/";

    //local
    //http://199.199.50.48/SLCScannerV2_bk/api/
    //public static final String BASE_URL = "http://199.199.50.48/";
    //public static final String SUB_URL = "SLCScannerV2/api-v2/";//"SLCScannerV2/api/";

    public static final boolean isLogDisplay = true;

    //API
    public static final String API_CHECK_UNIQUE_CODE = SUB_URL + "checkuniquecode";
    //public static final String API_CHECK_UNIQUE_CODE = "LightingGale/api-v2/checkuniquecode";

    public static final String API_CHECK_LOGIN = SUB_URL + "userLogin";
    public static final String API_SLC_LIST = SUB_URL + "slclist";

    public static final String API_SLC_DETAIL = SUB_URL + "slc";
    public static final String API_SAVE_NEW_SLC_DATA = SUB_URL + "saveslcdata";
    public static final String API_EDIT_UPDATE_SLC_DATA = SUB_URL + "editslcdata";
    public static final String API_CHECK_MAC_ID = SUB_URL + "checkmacaddress";
    public static final String API_CHECK_SLC_ID = SUB_URL + "checkslcid";
    public static final String API_GET_ADD_OTHER_DATA = SUB_URL + "addotherdata";
    public static final String API_GET_ASSESTS = SUB_URL + "assets";
    public static final String API_GET_ADDRESS = SUB_URL + "address";
    public static final String API_BACKGROUND = SUB_URL + "sync";
    public static final String API_POLE_ID_CHECK = SUB_URL + "checkpoleid";
    public static final String API_CHANGE_LANGUAGE = SUB_URL + "change-language";
    public static final String API_GET_LAT_LONG = SUB_URL + "getSlcListUsingLatLong";
    public static final String API_GET_NOTE = SUB_URL + "getpoleoptions";
    public static final String API_CHECK_MAC_ADDRESS_BEFORE_SLCID = SUB_URL + "checkMacAddressSlcIdbeforeSave";
    public static final String API_CHECK_INTERNAL_UNIQUE_MACA_DDRESS_API = SUB_URL + "checkInternalUniqueMacAddressAPI";
    public static final String API_CHECK_INTERNAL_UNIQUE_MACA_DDRESS_EDIT_API = SUB_URL + "checkInternalUniqueMacAddressEditAPI";
    public static final String API_GET_CLIENT_TYPE="LightingGale/GetClientType";
    public static final String API_CHECK_INTERNAL_UNIQUE_SLCID_EDIT_API = "checkInternalUniqueSLCIDEditAPI";
    //no use logner
    public static final String API_SETTINGS = SUB_URL + "setting";
    public static final String API_GET_FIXTURE_DATA = SUB_URL + "addfixturemanufacturer";
    public static final String API_GET_FIXTURE_WATTAGE_DATA = SUB_URL + "addwattage";

    //SPF
    public static final String SPF = "SLCScannerSPF";
    public static final String CLIENT_ID = "client_id";
    public static final String USER_ID = "user_id";
    public static final String TAG = "cimcon";
    public static final String TAG2 = "--*";
    public static final String USERNAME = "username";
    public static final String UNIQUECODE = "uniqueCode";
    public static final String LATTITUDE = "lat";
    public static final String LONGITUDE = "long";
    public static final String DEVICE_ID = "device_id";
    public static final String CLIENT_NAME = "ClientName";

    //Shared Preference
    public static final String SPF_LIST_ARM_LEGTH = "arm_lenght";
    public static final String SPF_LIST_MOUNTING_METHOD = "mounting_method";
    public static final String SPF_LIST_POLE_TYPE = "pole_type";
    public static final String SPF_LIST_POLE_OWNER = "pole_owner";
    public static final String SPF_LIST_POLE_FEED = "pole_power_feed";
    public static final String SPF_LIST_POLE_DIAMETER = "pole_diameter";
    public static final String SPF_LIST_POLE_CONFIG = "pole_config";
    public static final String SPF_LIST_POLE_CONDITION = "pole_condition";
    public static final String SPF_LIST_FIXTURES_PER_POLE = "fixtures_per_pole";
    public static final String SPF_LIST_FIXTURE_MANUFACTURER = "fixture_manufacturer";
    public static final String SPF_LIST_FIXTURE_WATTAGE = "fixture_wattage";
    public static final String SPF_LIST_HEIGHT = "pole_height";

    public static final String SPF_OTHER_DATA_VISIBILITY = "client_id_visibility";
    public static final String SPF_POLE_ID_VISIBILITY = "pole_id_visibility";

    public static final String SPF_UNITS = "units";
    public static final String SPF_UNITES_ENGLISH = "English";
    public static final String SPF_UNITES_MATRIC = "Metric";

    public static final String SPF_DRAG_LATTITUDE = "drag_lattitude";
    public static final String SPF_DRAG_LONGITUDE = "drag_longitude";

    public static final String SPF_TEMP_MACID = "tempMacID";
    public static final String SPF_TEMP_SLCID = "tempSLCID";
    public static final String SPF_TEMP_POLE_ID = "pole_id";

    public static final String SPF_POLE_DATA = "pole_data";
    public static final String SPF_ISFROMMAP = "isFromMap";
    public static final String SPF_ID = "SPF_ID";

    public static final String PASSWORD = "password";
    public static final String IS_NONE_CHECKED = "ischecked";
    public static final String ISLOGGEDIN = "isLoggedIn";

    public static final String SPF_CLIENT_ASSETS = "client_assets";
    public static final String SPF_CLIENT_ASSETS_COPY_FEATURE = "client_assets";
    public static final String isAppKilled = "isAppKilled";

    //Fragment Backstack 2 pref for scanner and pole
    public static final String SPF_SCANNER_CURRENT_FRAG = "scanner_current_frag";
    public static final String SPF_POLE_CURRENT_FRAG = "pole_current_frag";

    public static final String SPF_SCANNER_FRAG = "spf_scanner";
    public static final String SPF_SLC_ID_SCANNER_FRAG = "spf_slc_id_scanner_frag";

    public static final String SPF_POLEID_FRAG = "spf_poleid";
    public static final String SPF_SLCID_FRAG = "spf_slcid";
    public static final String SPF_SELECT_POLE_LOCATION_FRAG = "spf_select_pole_location";
    public static final String SPF_ADDRESS_FRAG = "spf_address_frag";
    public static final String SPF_CAMERA_FRAG = "spf_camera_frag";
    public static final String SPF_SCANNER_EDIT_FRAG = "spf_scanner_edit";
    public static final String SPF_SETTING = "spf_setting";

    //pole
    public static final String SPF_POLE_EDIT_FRAG = "spf_pole_edit";
    public static final String SPF_POLE_DISPLAY_FRAG = "spf_pole_detail_display";
    public static final String SPF_POLE_LIST_FRAG = "spf_pole_list";
    public static final String SPF_POLE_ALL_MAP = "spf_all_map";
    public static final String SPF_POLE_DISPLAY_LAT = "spf_pole_display_lat";
    public static final String SPF_POLE_DISPLAY_Long = "spf_pole_display_long";

    //measuremnt Text
    public static final String UNIT_MATRIC_METER = "Meters";
    public static final String UNIT_ENGLISH_FEET = "Feet";
    public static final String UNIT_MATRIC_CM = "cm";
    public static final String UNIT_ENGLISH_INCHES = "inch";

    public static final String ADDRESS = "pole_address";
    public static final String MACID_LABLE = "MacId_lable";
    public static final String MACID_PH = "MacId_ph";

    public static final String SPF_ID_FOR_COPY_FUNCTIONALITY = "spf_id";

    //Bundle
    public static final String BUNDLE_LATTITUDE = "lattidude";
    public static final String BUNDLE_LONGITUDE = "longitude";
    public static final String BUNDLE_SLCID = "slc_id";
    public static final String BUNDLE_MACID = "mac_id";
    public static final String BUNDLE_POLE_ID = "pole_id";
    public static final String BUNDLE_DOI = "date_of_installation";
    public static final String BUNDLE_ADDRESS = "address";
    public static final String BUNDLE_ASSETS = "assets";
    public static final String BUNDLE_IS_FROM_MAP = "is_from_map";
    public static final String BUNDLE_ISNEWDATA = "isNewData";
    public static final String BUNDLE_ID = "ID";
    public static final String BUNDLE_SKIPP = "skipp";
    public static final String BUNDLE_BACK_NAV_TITTLE = "title_fragment";

    //for dynamic field
    public static final String attribDOI = "Date of Installation: ";
    public static final String attribAddress = "Address: ";
    public static final String attribSLCID = "SLC ID: ";
    public static final String attribPoleId = "Pole ID: ";
    public static final String attribMacId = "Mac ID: ";

    public static final String SPF_LOGOUT_SLCID = "spf_logout_slcid";
    public static final String SPF_DEACTIVE_TIME = "spf_active_time";
    public static final long SESSION_INTERVAL = 1000 * 60 * 60 * 24; // 24 hour

    public static final String IsFromScannerEdit = "isfromeditui";

    public static final String TAG_SCANER_EDIT = "tag_scaner_edit";

    public static final String SPF_TEMP_POLE_ID_COPY_FEATURE = "pole_id_copy_feature";
    public static final String SPF_UNITS_FOR_COPY_FEATURE = "spf_units_for_copy_feature";
    public static final String ISFROMBACK = "isfromback";
    public static final String SATELLITE_COUNTS = "satellite_counts";
    public static final String LOCATION_ACCURACY = "location_accuracy";
    public static final String SELCTED_MAP_TYPE = "selcted_map_type";
    public static final String SPF_SLECTED_TAB = "spf_slected_tab";
    public static final String SCAN_TAB_SELECTED = "scanner_tab_selected";
    public static final String LIST_TAB_SELECTED = "scan_tab_selected";
    public static final String SETTING_TAB_SELECTED = "setting_tab_selected";
    public static final String SELECTED_LANG_TYPE = "selected_lang_type";
    public static final String LANGUAGE_LOCALE = "language_locale";
    public static final String LANGUAGE_SELECTED = "language_selected";
    public static final String SELECTED_MAP_TYPE_KEY = "selected_map_type_key";
    public static final String SELECTED_LANG_TYPE_KEY = "selected_lang_type_key";
    public static final String SELECTED_MAP_INDEX = "selected_map_index";
    public static final String SELECTED_LANG_INDEX = "selected_lang_index";
    public static final String LGVERSION = "LGVERSION";
    public static final String POLE_OPTION = "POLE_OPTION";
    public static final String NOTES = "NOTES";
    public static final String TYPE = "type";
    public static final String ISVIEWONLY = "new_data";
    public static final String FROM = "from";
    public static final String NOTES_POLE_OPTION = "notes_pole_option";
    public static final String NOTES_POLE_OPTION_EDIT = "notes_pole_option_edit";
    public static final String NOTES_EDIT = "notes_edit";
    public static final String SPF_NOTE_FRAGMENT = "SPF_NOTE_FRAGMENT";
    public static final String ASSET_ARRAY = "asset_array";
    public static final String IMAGE_URL = "image_url";
    public static final String SPF_TEMP_SLC_STATUS = "spf_temp_slc_status";
    public static final String SPF_LOGIN = "spf_login";

    public static final String POLE_IMAGE ="pole-k-pole image";
    public static final String POLE_IMAGE_PT="pólo-k-imagem do pólo";
    public static final String POLE_IMAGE_SPANISH="pólo-k-imagen de poste";

    //pole id
    public static final String POLE_ID ="pole-k-pole id";
    public static final String POLE_ID_SPANISH="pólo-k-pole id";
    public static final String POLE_ID_PT="pólo-k-pólo id";

    //notes
    public static final String POLE_NOTES ="pole-k-notes";
    public static final String POLE_NOTES_SPANISH="pólo-k-notas";
    public static final String POLE_NOTES_PT="pólo-k-notas";


    //attribute name
    public static final String POLE_IMAGE_ANAME ="pole image";
    public static final String POLE_IMAGE_ANAME_PT="imagem do pólo";
    public static final String POLE_IMAGE_ANAME_SPANISH="imagen de poste";

    //pole id
    public static final String POLE_ID_ANAME ="pole id";
    public static final String POLE_ID_ANAME_SPANISH="pole id";
    public static final String POLE_ID_ANAME_PT="pólo id";

    //notes
    public static final String POLE_NOTES_ANAME ="notes";
    public static final String POLE_NOTES_ANAME_SPANISH="notas";
    public static final String POLE_NOTES_ANAME_PT="notas";




    public static final String TOKEN = "token";
    public static final String NODE_TYPE_DISPLAY_UI = "node_type_display_ui";
    public static final String VERSION = "VERSION";



   /*public static final String POLE_IMAGE ="Pole Image";
    public static final String POLE_IMAGE_PT="Imagem do pólo";
    public static final String POLE_IMAGE_SPANISH="Imagen de poste";*/

    public static String CopyFromPrevious = "copyfromprevious";

    public static String isNoneChecked = "isNoneChecked";

    //spf for UI enable - disable

    public static final String SPF_POLE_ID_COMPULSORY = "spf_pole_id_compulsory";

    public static final String SPF_CLIENT_SLC_LIST_VIEW = "spf_client_slc_list_view";
    public static final String SPF_CLIENT_SLC_EDIT_VIEW = "spf_client_slc_edit_view";
    public static final String SPF_CLIENT_SLC_POLE_ASSETS_VIEW = "spf_client_slc_pole_assets_view";
    public static final String SPF_CLIENT_SLC_POLE_IMAGE_VIEW = "spf_pole_image_capture_ui_visibility";
    public static final String SPF_CLIENT_SLC_POLE_ID = "client_slc_pole_id";

    public static final String SPF_CLIENT_MEASURMENT_UNIT = "spf_client_measurment_unit"; // Metric , English , or Both

    //MapTypePojo Overlays:
    public static final String OPEN_STREET_MAP_STANDARD_URL = "http://tile.openstreetmap.org/%d/%d/%d.png";
    public static final String OPENS_STREET_CYCLE_MAP_URL = "http://tile.thunderforest.com/cycle/${z}/${x}/${y}.png";
    public static final String OPEN_STREE_HUMANITARIAN_URL = "http://b.tile.openstreetmap.fr/hot/${z}/${x}/${y}.png";

    //stamen
    public static final String STAMEN_TERRIEAN_URL = "http://tile.stamen.com/terrain/%d/%d/%d.jpg";
    public static final String STAMEN_TONNER_URL = "http://tile.stamen.com/toner/%d/%d/%d.png";
    public static final String STAMEN_WATERCOLOR_URL = "http://tile.stamen.com/watercolor/%d/%d/%d.jpg";

    //Carto DB
    public static final String CARTO_DB_DARK_URL = "https://cartodb-basemaps-{s}.global.ssl.fastly.net/light_all/%d/%d/%d.png";
    public static final String CARTO_DB_LIGHT_POSITRON_URL = "https://cartodb-basemaps-{s}.global.ssl.fastly.net/dark_all/%d/%d/%d.png";

    public static final String KEY_MAP_TYPE_UI_TRANSFER = "key_map_type_ui_transfer";
    public static final String VALUE_FOR_ALL_MAP = "value_for_all_map";
    public static final String VALUE_FOR_SELECT_LOCATION = "value_for_select_location";

    public static final String LANGUAGE_CODE_ENGLISH = "en";
    public static final String LANGUAGE_CODE_SPANISH = "es";
    public static final String LANGUAGE_CODE_PORTUGUES = "pt";

    public static final String isfromNote = "isfromNote";
    public static String Internal = "internal";
    public static final String EXTERNAL = "external";
    public static final String IS_FINGURE_TOUCH_PRESSED = "is_fingure_touch_pressed";
    public static final String TEMP_PASS = "temp_pass";
    public static final String IS_REMMEBER = "is_remmeber";
    public static final String SECURITY_CODE ="SCODE" ;

    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;
    public static String pole_image_key="pole_image_key";
    public static String pole_id_key="pole_id_key";
    public static String pole_notes_key="pole_notes_key";


    public static final String IS_NODE_TYPE_SELECTED = "is_node_type_selected";
    public static final String SELECTED_NODE_TYPE_ID ="selected_node_type_id";
    public static final String IS_NODE_TYPE_SELECTED_LIST = "is_node_type_selected_list";
    public static final String SELECTED_NODE_TYPE_ID_LIST ="selected_node_type_id_list";





    public static final String isSingleNodeType="issinglenodetype";
    public static final String IS_SINGLE_ZIGBEE = "IS_SINGLE_ZIGBEE";
    public static final String isZigbeeContains="isZigbeeContains";
    public static final String SPF_CLIENT_TYPE_DATA ="spf_client_type_data";
    public static final String SPF_CLIENT_TYPE_DATA_ADDED = "spf_parameters_data_added";


    ///NODE TYPE:
    public static final String EDIT_SLC_UI="edit_slc_ui";
    public static final String SCAN_MAC_UI="scan_mac_ui";
    public static final String LIST_SLC_UI="list_slc_ui";
    public static final String SCAN_EDIT_MAC_UI="scan_edit_mac_ui";


    public static final String SELECTED_NODE_TYPE_SAVE = "node_type";
    public static final String SELECTED_NODE_TYPE_INDEX_SAVE = "selected_node_type_index";

    public static final String SELECTED_NODE_TYPE_LIST = "node_type_list";
    public static final String SELECTED_NODE_TYPE_INDEX_LIST = "selected_node_type_index_list";

    public static final String SELECTED_NODE_TYPE_INDEX_EDIT_MAC = "selected_node_type_index_edit_mac";
    public static final String SELECTED_NODE_TYPE_EDIT_MAC = "selected_node_type_edit_mac";

    public static final String SELECTED_NODE_TYPE_INDEX_EDIT_SLC = "selected_node_type_index_edit_slc";
    public static final String SELECTED_NODE_TYPE_EDIT_SLC = "selected_node_type_edit_slc";


    public static final String isFromScannerForEdit="isfromscannerforedit";
    public static final String NODE_TYPE_SCANNER_EDIT_MAC = "node_type_scanner_edit_mac";
    public static String isPermission="isPermission";
}