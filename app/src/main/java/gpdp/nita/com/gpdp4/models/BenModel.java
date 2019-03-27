package gpdp.nita.com.gpdp4.models;

public class BenModel {

    private String benCode;
    private String imageUrl;
    private final String name;
    private final int count;
    private final int status;

    public BenModel(String benCode, String imageUrl, String name, int count, int status) {
        this.benCode = benCode;
        this.imageUrl = imageUrl;
        this.name = name;
        this.count = count;
        this.status = status;
    }

    public String getBenCode() {
        return benCode;
    }

    public void setBenCode(String benCode) {
        this.benCode = benCode;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStatus() {
        return status;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}
