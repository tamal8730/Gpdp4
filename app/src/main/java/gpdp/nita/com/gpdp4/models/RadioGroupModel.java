package gpdp.nita.com.gpdp4.models;

public class RadioGroupModel extends FormsModel {

    private int id;
    private String[] tokens;


    public RadioGroupModel(String tile, int id, int priority) {
        super(tile, priority);
        this.id = id;
    }

    public String[] getTokens() {
        return tokens;
    }

    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
