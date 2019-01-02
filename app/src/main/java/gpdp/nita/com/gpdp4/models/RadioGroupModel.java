package gpdp.nita.com.gpdp4.models;

import gpdp.nita.com.gpdp4.R;

public class RadioGroupModel extends FormsModel {

    private int id;
    private int skips = 0;
    private int skipButtonId = -1;

    public RadioGroupModel(String tile, int id) {
        super(tile);
        this.id = id;
    }

    public int getSkips() {
        return skips;
    }

    public void setSkips(int skips) {
        this.skips = skips;
    }

    public int getSkipButtonId() {
        return skipButtonId;
    }

    public void setSkipButtonId(int skipButtonId) {
        this.skipButtonId = skipButtonId;
    }

    public int getId() {
        if (id == 0) return R.id.rb0_rbvh;
        else if (id == 1) return R.id.rb1_rbvh;
        else return -1;
    }

    public void setId(int id) {
        this.id = id;
    }
}
