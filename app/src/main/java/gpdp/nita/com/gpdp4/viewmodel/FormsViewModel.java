package gpdp.nita.com.gpdp4.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import gpdp.nita.com.gpdp4.R;
import gpdp.nita.com.gpdp4.helpers.DatabaseHelper;
import gpdp.nita.com.gpdp4.helpers.MyJson;
import gpdp.nita.com.gpdp4.helpers.Upload;
import gpdp.nita.com.gpdp4.interfaces.OnDependentSpinnerItemSelected;
import gpdp.nita.com.gpdp4.interfaces.OnFormsEndListener;
import gpdp.nita.com.gpdp4.interfaces.OnViewModifiedListener;
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
    private MutableLiveData<List<FormsModel>> mutableLiveData;
    private String benCode;
    private Repo mRepo;
    private MutableLiveData<ArrayList<Object>> oneRowLiveData;
    private OnFormsEndListener onFormsEndListener;

    private OnDependentSpinnerItemSelected onDependentSpinnerItemSelected;

    private OnViewModifiedListener onViewModifiedListener;

    FormsViewModel(@NonNull Application application, String benCode) {
        super(application);
        if (mutableLiveData != null) {
            return;
        }

        this.benCode = benCode;
        initDatabase(0, 0);
        loadForm(application, formNumber);
    }

    public void setOnViewModifiedListener(OnViewModifiedListener onViewModifiedListener) {
        this.onViewModifiedListener = onViewModifiedListener;
    }

    public void setOnFormsEndListener(OnFormsEndListener onFormsEndListener) {
        this.onFormsEndListener = onFormsEndListener;
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


    public void setBenCode(String benCode) {
        this.benCode = benCode;
    }

    public void insert(Object[] answers) {
        mRepo.insert(answers);
        //ArrayList<Object> oneRow=mRepo.getOneRow();
        //oneRowLiveData.postValue(oneRow);
    }

    public Object[] onViewRemoved(int position, int category) {
        if (category == 0) return mRepo.onViewRemoved(Constants.YES_NO, position);
        else if (category == 1) {
            return mRepo.onEditTextRemoved(position);
        } else if (category == 2) return mRepo.onViewRemoved(Constants.NUMBER_DEFAULT, position);
        else if (category == 3) return mRepo.onViewRemoved(Constants.DATE_DEFAULT, position);
        else return mRepo.getAnswersList();
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


    public int loadNext() {
        if (formNumber >= Constants.formSequence.size() - 1) {
            onFormsEnd();
            return -1;
        } else {
            formNumber++;
            if (formNumber == 1) {
                Constants.initFormList();
            }
            loadForm(Constants.formSequence.get(formNumber), formNumber);
            return Constants.formSequence.get(formNumber);
        }
    }

    public String getSubTitle() {
        return Constants.subTitles.get(formNumber);
    }

    public boolean loadFormNumber(int form) {
        if (!Constants.formSequence.contains(form))
            return false;
        formNumber = Constants.formSequence.indexOf(form);
        if (formNumber == 0)
            Constants.initFormList();
        loadForm(form, formNumber);
        return true;
    }

    public void onFormsEnd() {
        SharedPreferences mAutoValues = getApplication().getSharedPreferences(Constants.AUTO_VALUES, Context.MODE_PRIVATE);
        JSONArray payload = mRepo.onFormsEnd();
        Upload upload = new Upload(getApplication());
        upload.setOnFormsEndListener(new OnFormsEndListener() {
            @Override
            public void onSyncStarted() {
                onFormsEndListener.onSyncStarted();
            }

            @Override
            public void onFormsEnd(boolean isSuccessful) {
                onFormsEndListener.onFormsEnd(isSuccessful);
            }
        });

        upload.sendJSONArray(
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

        String suffix = Constants.repeatedIndices.get(index);

        OneFormJson form = new MyJson(getApplication())
                .getFormJson(formNumber);

        DatabaseHelper.ben_code = benCode;
        DatabaseHelper.tableName = form.getTableName();
        DatabaseHelper.hasUniqueIdentifier = uniqueIdentifierResolver(form);
        DatabaseHelper.unique_identifier_val = DatabaseHelper.hasUniqueIdentifier ? suffix
                : null;
        DatabaseHelper.columnNames = form.getColumnNames();
        DatabaseHelper.dataTypes = form.getDataTypes();

    }

    private boolean uniqueIdentifierResolver(OneFormJson form) {
        String loop = form.getLoop();
        if (loop == null) return false;
        else {
            String[] tokens = loop.split(" ");
            DatabaseHelper.unique_identifier_name = tokens[1];
            return true;
        }
    }

    public int loadPrev() {
        if (formNumber != 0) {
            formNumber--;
            loadForm(Constants.formSequence.get(formNumber), formNumber);
            return Constants.formSequence.get(formNumber);
        } else return -1;
    }


    public void setTableName(String tableName) {

    }

    public void modify(List<Object> oneRow) {

        List<FormsModel> formsModels = mutableLiveData.getValue();
        //List<Object> oneRow=oneRowLiveData.getValue();

        for (int i = 0; i < (formsModels != null ? formsModels.size() : 0); i++) {

            FormsModel formsModel = formsModels.get(i);
            Object run;
            run = oneRow.get(i);

            if (formsModel instanceof EditTextModel) {
                if (run == null) ((EditTextModel) formsModel).setTextInEditText("");
                else if (run.toString().equals(Constants.STRING_DEFAULT) || run.toString().equals(String.valueOf(Constants.YES_NO))) {
                    ((EditTextModel) formsModel).setTextInEditText("");
                } else ((EditTextModel) formsModel).setTextInEditText(run.toString().trim());
            } else if (formsModel instanceof DateModel) {
                if (run == null) ((DateModel) formsModel).setDate("Tap to pick a date");
                else if (run.toString().equals(Constants.DATE_DEFAULT))
                    ((DateModel) formsModel).setDate("Tap to pick a date");
                else ((DateModel) formsModel).setDate(run.toString().trim());

            } else if (formsModel instanceof RadioGroupModel) {

                RadioGroupModel radioGroupModel = (RadioGroupModel) formsModel;

                int id = 0;

                if (run != null && !run.toString().trim().equals("")) {
                    id = Integer.parseInt(run.toString().trim());
                } else if (run == null) {
                    id = -1;
                } else if (run.toString().equals(String.valueOf(Constants.YES_NO))) {
                    id = -1;
                }
                if (id == -1) {
                    radioGroupModel.setId(-1);
                } else {
                    radioGroupModel.setId(id == 0 ? R.id.rb1_rbvh : R.id.rb0_rbvh);
                    onViewModifiedListener.onViewModified(i, id == 0 ? R.id.rb1_rbvh : R.id.rb0_rbvh, radioGroupModel.getTokens());
                }


            } else if (formsModel instanceof SpinnerModel) {

                SpinnerModel spinnerModel = (SpinnerModel) formsModel;

                int pos = 0;
                if (run != null) {
                    ArrayList<String> list = spinnerModel.getMenu();
                    ArrayList<Object> keys = spinnerModel.getKeys();
                    for (int j = 0; j < list.size(); j++) {
                        if (run.toString().equals(keys.get(j).toString())) {
                            pos = j;
                            break;
                        }
                    }

                    onDependentSpinnerItemSelected.onDependentSpinnerItemSelected(i, pos, spinnerModel.getTokens());
                }
                ((SpinnerModel) formsModel).setSelection(pos);
            } else if (formsModel instanceof ProfilePicModel) {
                if (run != null) ((ProfilePicModel) formsModel).setImgUrl(run.toString());
                else ((ProfilePicModel) formsModel).setImgUrl("none");
            }
        }

        mutableLiveData.postValue(formsModels);
    }

    public void setOnDependentSpinnerItemSelected(OnDependentSpinnerItemSelected onDependentSpinnerItemSelected) {
        this.onDependentSpinnerItemSelected = onDependentSpinnerItemSelected;
    }

}
