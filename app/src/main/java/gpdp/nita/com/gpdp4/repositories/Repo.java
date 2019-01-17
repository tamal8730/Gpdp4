package gpdp.nita.com.gpdp4.repositories;

import android.app.Application;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.util.SparseBooleanArray;

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
//    private Object[] answersList;
    private OneFormJson oneFormJson;
    private DatabaseHelper databaseHelper;
    private SparseBooleanArray isNumeric;
    private Object[] answers;



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
        MutableLiveData<ArrayList<Object>> oneRowLiveData = new MutableLiveData<>();
        boolean hasUniqueId = DatabaseHelper.hasUniqueIdentifier;
        oneRowLiveData.setValue(databaseHelper.getOneRow(hasUniqueId ?
                DatabaseHelper.unique_identifier_val :
                DatabaseHelper.ben_code));

        return oneRowLiveData;
    }

    public ArrayList<Object> getOneRow() {
        boolean hasMemId = DatabaseHelper.hasUniqueIdentifier;
        return databaseHelper.getOneRow(hasMemId ? DatabaseHelper.unique_identifier_val : DatabaseHelper.ben_code);
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

    private Object getDatabaseValueOrThrow(int category,
                                           ArrayList<Object> oneRow,
                                           int position,
                                           int dataType) {
        if (oneRow.size() == 0) {

            if (category == 0) {
                answers[position] = Constants.RADIO_GROUP_DEFAULT;
            } else {
                if (dataType == 0) {
                    answers[position] = Constants.STRING_DEFAULT;
                } else if (dataType == 1 || dataType == 2) {
                    answers[position] = Constants.NUMBER_DEFAULT;
                } else if (dataType == 3) {
                    answers[position] = Constants.DATE_DEFAULT;
                } else {
                    answers[position] = null;
                }
            }

        } else {
            answers[position] = oneRow.get(position);
        }

        return answers[position];
    }

    private void setFormsModel() {

        List<OneQuestionJson> oneQuestionJsons = oneFormJson.getWidgets();
        ArrayList<Object> oneRow=databaseHelper.getOneRow(DatabaseHelper.hasUniqueIdentifier?
                DatabaseHelper.unique_identifier_val:DatabaseHelper.ben_code);

        answers=new Object[oneQuestionJsons.size()];

        isNumeric = new SparseBooleanArray();

        dataSet.clear();
//        answersList = new Object[oneQuestionJsons.size()];

        for (int i = 0; i < oneQuestionJsons.size(); i++) {

//            answersList[i] = null;

            int category = oneQuestionJsons.get(i).getCategory();
            String title = oneQuestionJsons.get(i).getQuestion();
            int dataType = oneQuestionJsons.get(i).getDataType();

            Object ans = getDatabaseValueOrThrow(category, oneRow, i, dataType);


            if (category == 0) {

                //answersList[i] = Constants.YES_NO;

                int def = -1;
                RadioGroupModel radioGroupModel = new RadioGroupModel(title,
                        Integer.parseInt(ans.toString()),
                        i);

                String depen = oneQuestionJsons.get(i).getDependencies();
                if (depen.contains(":")) {
                    String[] tokens = depen.split(":");
                    radioGroupModel.setTokens(tokens);
                } else {
                    radioGroupModel.setTokens(null);
                }

                dataSet.add(radioGroupModel);

            } else if (category == 1) {

                boolean enabled = true;
                String def = ans.toString();

                int datatype = oneQuestionJsons.get(i).getDataType();

                if (datatype == 0) {
                    //answersList[i] = Constants.STRING_DEFAULT;

                }
                else if (datatype == 1 || datatype == 2) {
                    //answersList[i] = Constants.NUMBER_DEFAULT;
                    isNumeric.put(i, true);
                }

                if (mAutoValues.contains(oneQuestionJsons.get(i).getColumnName())) {
                    def = mAutoValues.getString(oneQuestionJsons.get(i).getColumnName(), "");
                    enabled = false;
                }

                dataSet.add(new EditTextModel(title, def, enabled, datatype, i));

            } else if (category == 2) {

                //answersList[i] = Constants.STRING_DEFAULT;

                ArrayList<Object> spinnerKeys= MyJson.getSpinnerKeys(oneQuestionJsons.get(i).getOptions(),
                        oneQuestionJsons.get(i).getDataType());

                int selectionPos;

                selectionPos=spinnerKeys.indexOf(ans);

                if(selectionPos==-1)
                    selectionPos=0;

                SpinnerModel spinnerModel = new SpinnerModel(
                        title,
                        selectionPos,
                        MyJson.getSpinnerList(oneQuestionJsons.get(i).getOptions()),
                        spinnerKeys,
                        i);

                String depen = oneQuestionJsons.get(i).getDependencies();

                String[] tokens;
                if (depen.equals("none")) tokens = null;
                else {
                    if (depen.contains(":")) {
                        tokens = depen.split(":");
                    } else {
                        tokens = null;
                    }
                }

                spinnerModel.setTokens(tokens);

                dataSet.add(spinnerModel);

            } else if (category == 3) {

                //answersList[i] = Constants.DATE_DEFAULT;

                dataSet.add(new DateModel(title, ans.toString(), i));

            } else if (category == 4) {

                //answersList[i] = Constants.STRING_DEFAULT;
                dataSet.add(new ProfilePicModel(Constants.IMAGE_UPLOAD_PATH + DatabaseHelper.ben_code + ".jpeg",
                        DatabaseHelper.ben_code, i));
            }
        }
    }


    public void insert(Object[] answers) {
        databaseHelper.insert(answers);
    }

    public void insert(){
        databaseHelper.insert(answers);
    }

    public Object[] onTyping(String text, int position) {
        answers[position] = text;
        return answers;
    }

    public Object[] onDateSet(String date, int position) {
        if(date.equals("Tap to pick a date"))
            answers[position] = Constants.DATE_DEFAULT;
        else
            answers[position]=date;
        return answers;
    }

    public Object[] onSpinnerItemSelected(Object key, int position) {
        answers[position] = key;
        return answers;
    }

    public Object[] onRadioButtonSelected(int id, int position) {
        answers[position] = id;
        return answers;
    }


    private Integer getIdFromButtonId(int id) {
        if (id == -1) return -1;
        else if (id == R.id.rb0_rbvh) return 1;
        else return 0;
    }






























    public Object[] onProfilePicTapped(int position) {
        answers[position] = Constants.IMAGE_UPLOAD_PATH + DatabaseHelper.ben_code + ".jpeg";
        return answers;
    }

    public Object[] getAnswersList() {
        return answers;
    }

    public JSONArray onFormsEnd() {
        try {
            return databaseHelper.createJsonFromLocalDatabase();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Object[] onEditTextRemoved(int position) {
        if (isNumeric.get(position, false))
            answers[position] = Constants.NUMBER_DEFAULT;
        else answers[position] = Constants.STRING_DEFAULT;
        return answers;
    }

    public Object[] onViewRemoved(Object def, int position) {
        answers[position] = def;
        return answers;
    }

    public void closeDatabase() {
        databaseHelper.close();
        Log.d("datxxx","closed");
    }
}
