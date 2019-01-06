package gpdp.nita.com.gpdp4.models;

import java.util.ArrayList;

public class SpinnerModel extends FormsModel {

    private int selection;
    private ArrayList<String> menu;
    private ArrayList<Object> keys;
    private String[] tokens;

    public SpinnerModel(String tile, int selection, ArrayList<String> menu, ArrayList<Object> keys, int priority) {
        super(tile, priority);
        this.selection = selection;
        this.menu = menu;
        this.keys = keys;
    }

    public int getSelection() {
        return selection;
    }

    public void setSelection(int selection) {
        this.selection = selection;
    }

    public ArrayList<String> getMenu() {
        return menu;
    }

    public void setMenu(ArrayList<String> menu) {
        this.menu = menu;
    }

    public ArrayList<Object> getKeys() {
        return keys;
    }

    public void setKeys(ArrayList<Object> keys) {
        this.keys = keys;
    }

    public String[] getTokens() {
        return tokens;
    }

    public void setTokens(String[] tokens) {
        this.tokens = tokens;
    }
}
