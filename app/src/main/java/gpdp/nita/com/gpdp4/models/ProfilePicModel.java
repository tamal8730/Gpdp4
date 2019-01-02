package gpdp.nita.com.gpdp4.models;

public class ProfilePicModel extends FormsModel {

    private String imgUrl;
    private String benCode;

    public ProfilePicModel(String imgUrl, String benCode) {
        super("");
        this.imgUrl = imgUrl;
        this.benCode = benCode;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getBenCode() {
        return benCode;
    }

    public void setBenCode(String benCode) {
        this.benCode = benCode;
    }
}
