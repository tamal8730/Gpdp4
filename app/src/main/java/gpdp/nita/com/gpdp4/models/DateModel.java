package gpdp.nita.com.gpdp4.models;

public class DateModel extends FormsModel {

    private String date;

    public DateModel(String tile, String date) {
        super(tile);
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
