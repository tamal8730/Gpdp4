package gpdp.nita.com.gpdp4.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import gpdp.nita.com.gpdp4.helpers.DatabaseHelper;

public class Constants {
    public static final String DATABASE_NAME = "gpdp.db";
    public static final String[] TABLES_TO_BE_DOWNLOADED_AFTER_LOGIN = {
            "form_0_ward panchayat_name gp_vc_name", //request form_0_ward, send key panchayat_id with values from gp_vc_name row
    };
    static final String DATE_DEFAULT = "0";
    public static final String LOGIN_VALIDATOR = "http://nakshakantha.com/apis/serveyor_validator.php";
    public static final String REMEMBER_LOGIN = "gpdp.nita.com.gpdp.remember_login";
    public static final String AUTO_VALUES = "gpdp.nita.com.gpdp.auto";
    public static final String UPDATE_TABLES = "http://nakshakantha.com/apis/updates.php";
    public static final String HTTP_URL = "http://nakshakantha.com/apis/sync.php";
    public static final String UPLOAD_URL = "http://nakshakantha.com/apis/beneficiary_image_uploader.php";
    static final String STRING_DEFAULT = "0";
    public static final String KEY_LOGGED_IN = "logged_in";
    public static final String KEY_SERVER_RESPONSE = "server_response";
    public static ArrayList<String> looplist;
    public static ArrayList<Integer> formSequence = new ArrayList<>();
    public static final String[] master_tables = {
            "form_0_caste",
            "form_0_religion",
            "form_0_ownership_of_house",
            "form_0_house_type",
            "form_0_type_of_secc",
            "form_0_1_gender",

            "form_1_qualification",
            "form_1_2_occupation",
            "form_1_type_of_disability",

            "form_2_type_of_pension",
            "form_2_not_getting_pension_reason",

            "form_3_activity",
            "form_3_assistance",
            "form_3_engaged_person",

            "form_4_shggradation",

            "form_5_source",

            "form_6_difficulty",

            "form_7_type_of_training",
            "form_7_business_problem",

            "form_8_rationcard_type",

            "form_9_reason_not_meal",
            "form_9_growth_monitored_no",

            "form_10_food_items",

            "form_11_source_irrigation_water",
            "form_11_12_14_18_source",
            "form_12_question_set",

            "form_13_delivery_place",
            "form_13_reason_fr_delivery_in_home",

            "form_14_diseases",
            "form_14_type_of_treatment",

            "form_16_question_set",

            "form_17_question_set",
            "form_17_reason",

            "form_18_source_of_water",
            "form_18_reason_for_poor_quality_of_water",
            "form_18_reason_of_water_scarcity"

    };
    public static final String IMAGE_UPLOAD_PATH = "http://nakshakantha.com/beneficiary_images/";
    static int NUMBER_DEFAULT = 0;
    public static ArrayList<String> repeatedIndices = new ArrayList<>();
    public static ArrayList<String> formNames = new ArrayList<>();


    public static void initFormList() {

        Iterator<String> iter = repeatedIndices.iterator();
        while (iter.hasNext()) {
            String str = iter.next();
            if (str.split("_")[0].equals(DatabaseHelper.ben_code)) iter.remove();
        }
        formSequence.removeAll(Collections.singleton(1));
        for (int i = 1; i <= DatabaseHelper.getForm2Count(); i++) {
            String s;
            if (i <= 9) s = "0" + i;
            else s = i + "";
            repeatedIndices.add(i, DatabaseHelper.ben_code + "_" + s);
            formSequence.add(i, 1);
        }

        for (int i = 0; i < formSequence.size(); i++) {
            Log.d("posxxx", formSequence.get(i) + " " + repeatedIndices);
        }

    }
}