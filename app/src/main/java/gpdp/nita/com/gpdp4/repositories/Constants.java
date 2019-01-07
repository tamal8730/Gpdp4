package gpdp.nita.com.gpdp4.repositories;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import gpdp.nita.com.gpdp4.helpers.DatabaseHelper;

public class Constants {

    public static final String BEN_NAMES_SHARED_PREFS = "gpdp.nita.com.gpdp.ben_names";
    public static final String DATE_DEFAULT = "0000-00-00";
    //nakshakantha.com

    public static final String DATABASE_NAME = "gpdp.db";
    public static final String[] TABLES_TO_BE_DOWNLOADED_AFTER_LOGIN = {
            "form_0_ward panchayat_name gp_vc_name", //request form_0_ward, send key panchayat_id with values from gp_vc_name row
    };
    public static final String STRING_DEFAULT = "x";
    public static final int YES_NO = -1;
    public static final String REMEMBER_LOGIN = "gpdp.nita.com.gpdp.remember_login";
    public static final String AUTO_VALUES = "gpdp.nita.com.gpdp.auto";


    private static final String BASE_URL = "nakshakantha.com";
    //"164.100.127.82";


    public static final String LOGIN_VALIDATOR = "http://" + BASE_URL + "/apis/serveyor_validator.php";
    public static final String UPDATE_TABLES = "http://" + BASE_URL + "/apis/updates.php";
    public static final String HTTP_URL = "http://" + BASE_URL + "/apis/sync.php";
    public static final String UPLOAD_URL = "http://" + BASE_URL + "/apis/beneficiary_image_uploader.php";
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

            "form_15_institution_type",
            "form_15_institution_standard",
            "form_15_institution_medium",
            "form_15_answer",
            "form_15_mode_of_transportation",
            "form_15_condition_of_institution",
            "form_15_reason_not_studying_in_nearest_institution",
            "form_15_reason_for_dropout",
            "form_15_type_of_assistance",

            "form_16_question_set",

            "form_17_question_set",
            "form_17_reason",

            "form_18_19_source_of_water",
            "form_18_reason_for_poor_quality_of_water",
            "form_18_reason_of_water_scarcity",

            "form_20_unavailability_reason",
            "form_20_reason_for_irregular_use_of_toilet",
            "form_20_frequency",
            "form_20_toilet_place",
            "form_20_solid_liquid_discharge",

            "form_21_no_electricity_reason",
            "form_21_reason",

            "form_22_hazard_list",

            "form_23_knowledge_source",
            "form_23_media",
            "form_23_shelter_condition",

            "form_24_pond_improvement",

            "form_25_reason",
            "form_25_incident"

    };
    static final String IMAGE_UPLOAD_PATH = "http://" + BASE_URL + "/beneficiary_images/";
    public static int NUMBER_DEFAULT = 0;
    public static ArrayList<String> repeatedIndices = new ArrayList<>();
    public static ArrayList<String> subTitles = new ArrayList<>();


    public static void initFormList() {

        Iterator<String> iter = repeatedIndices.iterator();
        int lastIndexOfForm14 = 0;

        while (iter.hasNext()) {
            String str = iter.next();
            if (str.split("_")[0].equals(DatabaseHelper.ben_code)) iter.remove();
        }
        iter = subTitles.iterator();
        while (iter.hasNext()) {
            String str = iter.next();
            if (str.split(" ")[0].equals("Person")) iter.remove();
        }

        formSequence.removeAll(Collections.singleton(1));
        formSequence.removeAll(Collections.singleton(15));

        lastIndexOfForm14 = formSequence.lastIndexOf(14);

        for (int i = 1; i <= DatabaseHelper.getNumberOfStudents(); i++) {
            String s;
            int j = i + lastIndexOfForm14;
            if (i <= 9) s = "0" + i;
            else s = i + "";
            repeatedIndices.add(j, DatabaseHelper.ben_code + "_" + s);
            subTitles.add(j, "Student " + s);
            formSequence.add(j, 15);
        }

        for (int i = 1; i <= DatabaseHelper.getForm2Count(); i++) {
            String s;
            if (i <= 9) s = "0" + i;
            else s = i + "";
            repeatedIndices.add(i, DatabaseHelper.ben_code + "_" + s);
            subTitles.add(i, "Person " + s);
            formSequence.add(i, 1);
        }

        for (int i = 0; i < formSequence.size(); i++) {
            Log.d("posxxx", formSequence.get(i) + " " + repeatedIndices.get(i) + " " + subTitles.get(i));
        }
    }
}