package gpdp.nita.com.gpdp4.models;

public class EditTextModel extends FormsModel {

    private String textInEditText;
    private boolean enabled;
    private int inputType;

    public EditTextModel(String title, String textInEditText, boolean enabled, int inputType, int priority) {
        super(title, priority);
        this.textInEditText = textInEditText;
        this.enabled = enabled;
        this.inputType = inputType;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getTextInEditText() {
        return textInEditText;
    }

    public void setTextInEditText(String textInEditText) {
        this.textInEditText = textInEditText;
    }

    public int getInputType() {
        return inputType;
    }

    public void setInputType(int inputType) {
        this.inputType = inputType;
    }
}
