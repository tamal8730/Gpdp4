package gpdp.nita.com.gpdp4.models;

public class FormsModel {
    private String tile;
    private int priority;

    public FormsModel(String tile, int priority) {
        this.tile = tile;
        this.priority = priority;
    }

    public String getTile() {
        return tile;
    }

    public void setTile(String tile) {
        this.tile = tile;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
