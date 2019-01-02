package gpdp.nita.com.gpdp4.repositories;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import gpdp.nita.com.gpdp4.models.FormsModel;

public class RemovedViews {
    private static final RemovedViews instance = new RemovedViews();

    //private static final HashMap<View,FormsModel> allViews=new HashMap<>();
    private static final HashMap<FormsModel, ArrayList<FormsModel>> dumpedViews = new HashMap<>();
    private static final ArrayList<FormsModel> value = new ArrayList<>();

    private RemovedViews() {

    }

    public static RemovedViews getInstance() {
        return instance;
    }

    public void removeViews(List<FormsModel> models, int start, int batchSize) {

        FormsModel key = models.get(start - 1);
        for (int i = 0; i < batchSize; i++) {
            value.add(models.remove(i + start));
        }
        dumpedViews.put(key, value);
    }

    public HashMap<FormsModel, ArrayList<FormsModel>> getDumpedViews() {
        return dumpedViews;
    }
}