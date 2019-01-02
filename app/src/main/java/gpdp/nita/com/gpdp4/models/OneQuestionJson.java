package gpdp.nita.com.gpdp4.models;

import com.google.gson.annotations.SerializedName;

public class OneQuestionJson {

    @SerializedName("column_name")
    private String columnName;

    @SerializedName("heading")
    private String question;

    @SerializedName("category")
    private int category;

    @SerializedName("data_type")
    private int dataType;

    @SerializedName("options")
    private String options;

    @SerializedName("dependencies")
    private String dependencies;

    public String getColumnName() {
        return columnName;
    }

    public String getQuestion() {
        return question;
    }

    public int getCategory() {
        return category;
    }

    public int getDataType() {
        return dataType;
    }

    public String getOptions() {
        return options;
    }

    public String getDependencies() {
        return dependencies;
    }
}
