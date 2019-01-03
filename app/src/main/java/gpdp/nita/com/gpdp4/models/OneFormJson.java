package gpdp.nita.com.gpdp4.models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class OneFormJson {

    @SerializedName("name")
    private String formName;

    @SerializedName("table_name")
    private String tableName;

    @SerializedName("form_number")
    private int formNumber;

    @SerializedName("loop")
    private String loop;

    @SerializedName("widgets")
    private List<OneQuestionJson> widgets;


    public String getFormName() {
        return formName;
    }

    public String getTableName() {
        return tableName;
    }

    public int getFormNumber() {
        return formNumber;
    }

    public String getLoop() {
        return loop;
    }

    public List<OneQuestionJson> getWidgets() {
        return widgets;
    }

    public ArrayList<String> getColumnNames() {
        ArrayList<String> columnNames = new ArrayList<>();
        for (int i = 0; i < widgets.size(); i++) {
            columnNames.add(widgets.get(i).getColumnName());
        }

        return columnNames;
    }

    public ArrayList<Integer> getDataTypes() {
        ArrayList<Integer> dataTypes = new ArrayList<>();
        for (int i = 0; i < widgets.size(); i++) {
            dataTypes.add(widgets.get(i).getDataType());
        }

        return dataTypes;
    }
}
