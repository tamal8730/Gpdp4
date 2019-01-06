package gpdp.nita.com.gpdp4.helpers;

import java.util.Comparator;

import gpdp.nita.com.gpdp4.models.FormsModel;

public class FormViewsComparator implements Comparator<FormsModel> {
    @Override
    public int compare(FormsModel o1, FormsModel o2) {
        return o1.getPriority() - o2.getPriority();
    }
}
