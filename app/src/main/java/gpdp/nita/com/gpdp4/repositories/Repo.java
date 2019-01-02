package gpdp.nita.com.gpdp4.repositories;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.helpers.DatabaseHelper;
import gpdp.nita.com.gpdp4.helpers.MyJson;
import gpdp.nita.com.gpdp4.models.DateModel;
import gpdp.nita.com.gpdp4.models.EditTextModel;
import gpdp.nita.com.gpdp4.models.FormsModel;
import gpdp.nita.com.gpdp4.models.OneFormJson;
import gpdp.nita.com.gpdp4.models.OneQuestionJson;
import gpdp.nita.com.gpdp4.models.ProfilePicModel;
import gpdp.nita.com.gpdp4.models.RadioGroupModel;
import gpdp.nita.com.gpdp4.models.SpinnerModel;

public class Repo {
    SharedPreferences mAutoValues;
    private ArrayList<FormsModel> dataSet = new ArrayList<>();
    private Object[] answersList;
    private OneFormJson oneFormJson;
    private DatabaseHelper databaseHelper;
    private MutableLiveData<ArrayList<Object>> oneRowLiveData;


    public Repo(Application application, int formNumber) {

        mAutoValues = application.getSharedPreferences(Constants.AUTO_VALUES, Context.MODE_PRIVATE);

        loadForm(application, formNumber);
//        switch (formNumber){
//
//            case 0:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//            case 1:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//            case 2:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//            case 3:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//            case 4:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//            case 5:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//            case 6:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//            case 7:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//            case 8:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//            case 9:baseDao =FormsDatabase.getInstance(application).form0Dao();
//                break;
//
//        }

        databaseHelper = DatabaseHelper.getInstance(application);

    }

    public MutableLiveData<ArrayList<Object>> getOneRowLive() {
        oneRowLiveData = new MutableLiveData<>();
        boolean hasMemId = DatabaseHelper.hasMemberId;
        oneRowLiveData.setValue(databaseHelper.getOneRow(hasMemId ? DatabaseHelper.member_id : DatabaseHelper.ben_code));
        return oneRowLiveData;
    }

    public ArrayList<Object> getOneRow() {
        boolean hasMemId = DatabaseHelper.hasMemberId;
        return databaseHelper.getOneRow(hasMemId ? DatabaseHelper.member_id : DatabaseHelper.ben_code);
    }


    public void loadForm(Application application, int formNumber) {

        String fileName = "form_" + formNumber + ".json";
        if (formNumber <= 9) fileName = "form_0" + formNumber + ".json";

        MyJson myJson = new MyJson(application);
        oneFormJson = myJson.getFormJson(fileName);
    }


//    public LiveData<Form0> getLiveAnswers(String benCode) {
//        setAnswers(benCode);
//        form0 = formMutableLiveData.getValue();
//        return formMutableLiveData;
//    }

    public MutableLiveData<List<FormsModel>> getFormsModel() {
        setFormsModel();
        MutableLiveData<List<FormsModel>> data = new MutableLiveData<>();
        data.setValue(dataSet);
        return data;
    }

    private void setAnswers(String benCode) {
        //  formMutableLiveData = ((Form0Dao)baseDao).getBeneficiaryDetails(benCode);
    }

    private void setFormsModel() {

        List<OneQuestionJson> oneQuestionJsons = oneFormJson.getWidgets();
        dataSet.clear();
        answersList = new Object[oneQuestionJsons.size()];

        for (int i = 0; i < oneQuestionJsons.size(); i++) {

            answersList[i] = null;

            int category = oneQuestionJsons.get(i).getCategory();
            String title = oneQuestionJsons.get(i).getQuestion();


            if (category == 0) {

                int def = 0;
                RadioGroupModel radioGroupModel = new RadioGroupModel(title, def);

                String depen = oneQuestionJsons.get(i).getDependencies();
                int skips, skipButtonId;
                if (depen.contains(" ")) {

                    String[] tokens = depen.split(" ");
                    skips = Integer.parseInt(tokens[0]);
                    skipButtonId = Integer.parseInt(tokens[1]);
                    if (skipButtonId == 0) radioGroupModel.setSkipButtonId(R.id.rb0_rbvh);
                    else if (skipButtonId == 1) radioGroupModel.setSkipButtonId(R.id.rb1_rbvh);

                    radioGroupModel.setSkips(skips);
                }

                dataSet.add(radioGroupModel);

            } else if (category == 1) {

                boolean enabled = true;
                String def = null;
                int datatype = oneQuestionJsons.get(i).getDataType();

                if (datatype == 0)
                    def = Constants.STRING_DEFAULT;
                else if (datatype == 1 || datatype == 2)
                    def = String.valueOf(Constants.NUMBER_DEFAULT);

                if (mAutoValues.contains(oneQuestionJsons.get(i).getColumnName())) {
                    def = mAutoValues.getString(oneQuestionJsons.get(i).getColumnName(), "");
                    enabled = false;
                }

                dataSet.add(new EditTextModel(title, def, enabled, datatype));

            } else if (category == 2) {
                dataSet.add(new SpinnerModel(
                        title,
                        Constants.NUMBER_DEFAULT,
                        MyJson.getSpinnerList(oneQuestionJsons.get(i).getOptions()),
                        MyJson.getSpinnerKeys(oneQuestionJsons.get(i).getOptions(), oneQuestionJsons.get(i).getDataType())
                ));

            } else if (category == 3) {
//                answersList[i] = Constants.DATE_DEFAULT;
                dataSet.add(new DateModel(title, Constants.DATE_DEFAULT));
            } else if (category == 4) {
                dataSet.add(new ProfilePicModel(Constants.IMAGE_UPLOAD_PATH + DatabaseHelper.ben_code + ".jpeg",
                        DatabaseHelper.ben_code));
            }
        }
    }

    private String toCamelCase(String columnName) {
        StringBuilder s = new StringBuilder();
        String[] parts = columnName.split("_");
        for (String ss : parts) {
            StringBuilder x = new StringBuilder(ss);
            x.setCharAt(0, (char) (((int) x.charAt(0)) - 32));
            s.append(x);
        }
        return s.toString().trim();
    }

//    public LiveData<ArrayList<Object>> getOneRowLive() {
//        return myOneRow;
//    }


    public void insert(Object[] answers) {
        databaseHelper.insert(answers);
    }

    public Object[] onTyping(String text, int position) {
        answersList[position] = text;
        return answersList;
    }

    public Object[] onDateSet(String date, int position) {
        answersList[position] = date;
        return answersList;
    }

    public Object[] onSpinnerItemSelected(Object key, int position) {
        answersList[position] = key;
        return answersList;
    }

    public Object[] onRadioButtonSelected(int id, int position) {
        answersList[position] = id;
        return answersList;
    }

    public Object[] onProfilePicTapped(int position) {
        answersList[position] = Constants.IMAGE_UPLOAD_PATH + DatabaseHelper.ben_code + ".jpeg";
        return answersList;
    }

    public JSONArray onFormsEnd() {
        try {
            return databaseHelper.createJsonFromLocalDatabase();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
