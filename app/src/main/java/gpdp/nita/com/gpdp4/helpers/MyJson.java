package gpdp.nita.com.gpdp4.helpers;

import android.content.Context;
import android.os.Environment;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

import gpdp.nita.com.gpdp4.models.OneFormJson;
import gpdp.nita.com.gpdp4.models.OneQuestionJson;

public class MyJson {

    private Gson gson = new Gson();
    private ArrayList<String> tableList;
    private ArrayList<ArrayList<String>> columnsListList;
    private ArrayList<Integer> loopList;
    private ArrayList<ArrayList<Integer>> dataTypes;

    private Context context;

    public MyJson(Context context) {
        this.context = context;
    }

    public static ArrayList<String> getSpinnerList(String filename) {

        ArrayList<String> list = new ArrayList<>();

        String json;
        try {

            String fileName = "gpdp/data/" + filename + ".json";
            String path = Environment.getExternalStorageDirectory() + "/" + fileName;
            File file = new File(path);

            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                list.add(jsonObject.getString(key));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static ArrayList<Object> getSpinnerKeys(String filename, int dType) {

        ArrayList<Object> list = new ArrayList<>();

        String json;
        try {

            String fileName = "gpdp/data/" + filename + ".json";
            String path = Environment.getExternalStorageDirectory() + "/" + fileName;
            File file = new File(path);

            InputStream is = new FileInputStream(file);
            int size = is.available();
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            is.close();

            json = new String(buffer, "UTF-8");

            JSONObject jsonObject = new JSONObject(json);
            Iterator<String> iterator = jsonObject.keys();
            while (iterator.hasNext()) {
                String key = iterator.next();
                if (dType == 0)
                    list.add(key.trim());
                else if (dType == 1)
                    list.add(Integer.parseInt(key.trim()));
                else if (dType == 2)
                    list.add(Double.parseDouble(key.trim()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    MyJson initLists() {
        tableList = new ArrayList<>();
        columnsListList = new ArrayList<>();
        loopList = new ArrayList<>();
        dataTypes = new ArrayList<>();
        return this;
    }

    private String getJsonAsString(String filename) {
        try {
            InputStream is = context.getAssets().open("forms/" + filename);
            int size = is.available();
            byte[] buffer = new byte[size];
            int read = is.read(buffer);
            is.close();

            return new String(buffer, "UTF-8");

        } catch (IOException e) {
            return null;
        }
    }

    public OneFormJson getFormJson(String filename) {
        return gson.fromJson(getJsonAsString(filename), OneFormJson.class);
    }

    public OneFormJson getFormJson(int formNumber) {
        String s = "form_" + formNumber + ".json";
        if (formNumber <= 9)
            s = "form_0" + formNumber + ".json";
        return getFormJson(s);
    }

    void setTableAndColumnList() {
        try {
            String[] s = context.getAssets().list("forms");
            assert s != null;
            for (String value : s) {
                OneFormJson oneFormJson = getFormJson(value);

                tableList.add(oneFormJson.getTableName());
                loopList.add(oneFormJson.getLoop());

                ArrayList<String> cols = new ArrayList<>();
                ArrayList<Integer> dt = new ArrayList<>();

                for (int j = 0; j < oneFormJson.getWidgets().size(); j++) {

                    OneQuestionJson oneQuestionJson = oneFormJson.getWidgets().get(j);

                    cols.add(oneQuestionJson.getColumnName());
                    dt.add(oneQuestionJson.getDataType());
                }
                columnsListList.add(cols);
                dataTypes.add(dt);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ArrayList<Integer>> getDataTypes() {
        return dataTypes;
    }

    public ArrayList<Integer> getLoopList() {
        return loopList;
    }

    public ArrayList<String> getTableList() {
        return tableList;
    }

    public ArrayList<ArrayList<String>> getColumnsListList() {
        return columnsListList;
    }

    public ArrayList<String> getColumns(int formNumber) {
        return columnsListList.get(formNumber);
    }


}