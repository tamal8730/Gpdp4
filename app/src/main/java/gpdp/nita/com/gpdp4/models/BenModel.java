package gpdp.nita.com.gpdp4.models;

public class BenModel {

    private String benCode;
    private String imageUrl;

    public BenModel(String benCode, String imageUrl) {
        this.benCode = benCode;
        this.imageUrl = imageUrl;
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
}
