package gpdp.nita.com.gpdp4.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import gpdp.nita.com.gpdp4.helpers.DatabaseHelper;
import gpdp.nita.com.gpdp4.helpers.MyJson;
import gpdp.nita.com.gpdp4.helpers.Upload;
import gpdp.nita.com.gpdp4.models.DateModel;
import gpdp.nita.com.gpdp4.models.EditTextModel;
import gpdp.nita.com.gpdp4.models.FormsModel;
import gpdp.nita.com.gpdp4.models.OneFormJson;
import gpdp.nita.com.gpdp4.models.ProfilePicModel;
import gpdp.nita.com.gpdp4.models.RadioGroupModel;
import gpdp.nita.com.gpdp4.models.SpinnerModel;
import gpdp.nita.com.gpdp4.repositories.Constants;
import gpdp.nita.com.gpdp4.repositories.Repo;

public class FormsViewModel extends AndroidViewModel {

    public static int formNumber = 0;
    private static int numberOfTimesForm2Completed = 0;
    private MutableLiveData<List<FormsModel>> mutableLiveData;
    private String benCode;
    private Repo mRepo;
    private MutableLiveData<ArrayList<Object>> oneRowLiveData;
    private SharedPreferences mAutoValues;


    FormsViewModel(@NonNull Application application, String benCode) {
        super(application);
        if (mutableLiveData != null) {
            return;
        }
        this.benCode = benCode;
        initDatabase(0, 0);
        loadForm(application, formNumber);
    }

    private void loadForm(Application application, int formNumber) {

        mRepo = new Repo(application, formNumber);
        //form0MutableLiveData = mRepo.getLiveAnswers(benCode);
        mutableLiveData = mRepo.getFormsModel();
    }

    public MutableLiveData<ArrayList<Object>> getOneRow() {
        oneRowLiveData = mRepo.getOneRowLive();
        return oneRowLiveData;
    }


    public void deleteViewAtRange(int start, int batchSize) {

        List<FormsModel> formsModels = mutableLiveData.getValue();
        for (int i = 0; i < batchSize; i++) {
            if (formsModels != null) {
                formsModels.remove(start + i);
            }
        }
        mutableLiveData.postValue(formsModels);
    }


    public void setBenCode(String benCode) {
        this.benCode = benCode;
    }

    public void insert(Object[] answers) {
        mRepo.insert(answers);
        //ArrayList<Object> oneRow=mRepo.getOneRow();
        //oneRowLiveData.postValue(oneRow);
    }

    public Object[] onTyping(String text, int position) {
        return mRepo.onTyping(text, position);
    }

    public Object[] onDateSet(String date, int position) {
        return mRepo.onDateSet(date, position);
    }

    public Object[] onSpinnerItemSelected(Object key, int position) {
        return mRepo.onSpinnerItemSelected(key, position);
    }

    public Object[] onRadioButtonSelected(int id, int position) {
        return mRepo.onRadioButtonSelected(id, position);
    }

    public Object[] onProfilePictureTapped(int position) {
        return mRepo.onProfilePicTapped(position);
    }


    public LiveData<List<FormsModel>> getFormsModel() {
        return mutableLiveData;
    }


    public void loadNext() {
        Log.d("formxx", formNumber + " " + Constants.formNumbers.size());
        if (formNumber >= Constants.formNumbers.size() - 1) {
            onFormsEnd();
        } else {
            formNumber++;
            if (formNumber == 1) {
                Constants.initFormList();
            }
            loadForm(Constants.formNumbers.get(formNumber), formNumber);
        }
    }

    private void onFormsEnd() {
        mAutoValues = getApplication().getSharedPreferences(Constants.AUTO_VALUES, Context.MODE_PRIVATE);
        JSONArray payload = mRepo.onFormsEnd();
        new Upload(getApplication()).sendJSONArray(
                payload,
                DatabaseHelper.ben_code,
                mAutoValues.getString("surveyor_id", "unknown"));
    }

    private void loadForm(int formNumber, int index) {

        initDatabase(formNumber, index);

        mRepo.loadForm(getApplication(), formNumber);
        mutableLiveData.postValue(mRepo.getFormsModel().getValue());

        ArrayList<Object> oneRow = mRepo.getOneRow();
        oneRowLiveData.postValue(oneRow);
    }

    private void initDatabase(int formNumber, int index) {

        String suffix = "_" + index;
        if (formNumber <= 9) {
            suffix = "_0" + index;
        }

        OneFormJson prevForm = new MyJson(getApplication())
                .getFormJson(formNumber);

        DatabaseHelper.ben_code = benCode;
        DatabaseHelper.tableName = prevForm.getTableName();
        DatabaseHelper.hasMemberId = prevForm.getLoop() == 1;
        DatabaseHelper.member_id = prevForm.getLoop() == 1 ? benCode + suffix : null;
        DatabaseHelper.columnNames = prevForm.getColumnNames();
        DatabaseHelper.dataTypes = prevForm.getDataTypes();

    }

    public void loadPrev() {
        if (formNumber != 0) {
            formNumber--;
            loadForm(Constants.formNumbers.get(formNumber), formNumber);
        }
    }


    public void setTableName(String tableName) {

    }

    public void modify(List<Object> oneRow) {

        List<FormsModel> formsModels = mutableLiveData.getValue();
        //List<Object> oneRow=oneRowLiveData.getValue();

        for (int i = 0; i < (formsModels != null ? formsModels.size() : 0); i++) {

            FormsModel formsModel = formsModels.get(i);
            Object run;
//            if (form0 != null)
//                run = form0.run(i);

            run = oneRow.get(i);

            if (formsModel instanceof EditTextModel) {
                if (run == null) ((EditTextModel) formsModel).setTextInEditText("");
                else ((EditTextModel) formsModel).setTextInEditText(run.toString().trim());
            } else if (formsModel instanceof DateModel) {
                if (run == null) ((DateModel) formsModel).setDate("Date");
                else ((DateModel) formsModel).setDate(run.toString().trim());
            } else if (formsModel instanceof RadioGroupModel) {
                int id;
                if (run == null) id = 0;
                else id = Integer.parseInt(run.toString().trim());
                ((RadioGroupModel) formsModel).setId(id == 0 ? 1 : 0);
            } else if (formsModel instanceof SpinnerModel) {

                int pos = 0;
                if (run != null) {
                    ArrayList<String> list = ((SpinnerModel) formsModel).getMenu();
                    ArrayList<Object> keys = ((SpinnerModel) formsModel).getKeys();
                    for (int j = 0; j < list.size(); j++) {
                        if (run.toString().equals(keys.get(j).toString())) {
                            pos = j;
                            break;
                        }
                    }
                }

                ((SpinnerModel) formsModel).setSelection(pos);
            } else if (formsModel instanceof ProfilePicModel) {
                if (run != null) ((ProfilePicModel) formsModel).setImgUrl(run.toString());
                else ((ProfilePicModel) formsModel).setImgUrl("none");
            }
        }

        mutableLiveData.postValue(formsModels);
    }
}
