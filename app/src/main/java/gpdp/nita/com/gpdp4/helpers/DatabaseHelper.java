package gpdp.nita.com.gpdp4.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import gpdp.nita.com.gpdp4.repositories.Constants;

public class DatabaseHelper extends SQLiteOpenHelper {


    public static String ben_code;
    public static String unique_identifier_name;
    public static String unique_identifier_val_prefix;
    public static boolean hasUniqueIdentifier = false;
    public static String unique_identifier_val;
    public static String tableName;
    public static ArrayList<String> columnNames;
    public static ArrayList<Integer> dataTypes;
    private static DatabaseHelper instance = null;
    private MyJson myJson;

    private DatabaseHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, 1);
        myJson = new MyJson(context).initLists();
    }

    public static synchronized DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
        return instance;
    }

    public static int getForm2Count() {
        int sum = 0;
        Cursor c = instance.getReadableDatabase().rawQuery("select male_num,female_num,transgender_num from gpdp_basic_info_1 where ben_code=?", new String[]{
                ben_code
        });
        if (c.moveToFirst()) {
            for (int i = 0; i < 3; i++) {
                if (c.getType(i) == Cursor.FIELD_TYPE_INTEGER) sum += c.getInt(i);
            }
        }
        c.close();
        return sum;
    }

    private String columnNamesConcat(ArrayList<String> columnNames, String loop, ArrayList<Integer> dataType, String tableName) {

        StringBuilder s = new StringBuilder(" (ben_code TEXT,");

        uniqueIdentifierResolver(s, loop);

        if (tableName.equals("gpdp_basic_info_1"))
            s.append("survey_date TEXT,");

        for (int i = 0; i < columnNames.size(); i++) {
            s.append(columnNames.get(i));
            switch (dataType.get(i)) {
                case 0:
                    s.append(" TEXT DEFAULT '0' NOT NULL,");
                    break;
                case 1:
                    s.append(" INTEGER DEFAULT 0 NOT NULL,");
                    break;
                case 2:
                    s.append(" DOUBLE DEFAULT 0 NOT NULL,");
                    break;
                case 3:
                    s.append(" DATE DEFAULT '0000-00-00' NOT NULL,");
                    break;
            }
        }
        s.deleteCharAt(s.length() - 1);
        s.append(")");
        return s.toString();
    }

    private void uniqueIdentifierResolver(StringBuilder s, String loop) {
        if (loop != null) {
            String[] tokens = loop.split(" ");
            s.append(tokens[1]).append(" TEXT,");
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        myJson.setTableAndColumnList();
        ArrayList<String> tableNames = myJson.getTableList();
        ArrayList<ArrayList<String>> columnList = myJson.getColumnsListList();
        ArrayList<String> loopList = myJson.getLoopList();
        ArrayList<ArrayList<Integer>> dataTypes = myJson.getDataTypes();

        for (int i = 0; i < tableNames.size(); i++) {

            db.execSQL(
                    "create table if not exists " + tableNames.get(i) + columnNamesConcat(
                            columnList.get(i),
                            loopList.get(i),
                            dataTypes.get(i),
                            tableNames.get(i)
                    )
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(
//                "DROP TABLE IF EXISTS "+ tableName
//        );
        onCreate(db);
    }

    private void populateContentValue(Object currentEntry, String columnName, ContentValues contentValues) {

        if (currentEntry instanceof String) {
            contentValues.put(columnName, (String) currentEntry);
        } else if (currentEntry instanceof Integer) {
            contentValues.put(columnName, (Integer) currentEntry);
        } else if (currentEntry instanceof Double) {
            contentValues.put(columnName, (Double) currentEntry);
        }
    }

    private String getDateAndTime() {

        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd H:m:s", Locale.getDefault());
        return fmt.format(Calendar.getInstance().getTime());
    }

    private void update(Object[] answers, String code) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query;
        String[] selectArgs;
        if (hasUniqueIdentifier) {
            query = "ben_code = ? and " + unique_identifier_name + " = ?";
            selectArgs = new String[]{ben_code, code};
        } else {
            query = "ben_code = ?";
            selectArgs = new String[]{ben_code};
        }
        ContentValues contentValues = new ContentValues();
        for (int i = 0; i < answers.length; i++) {
            populateContentValue(answers[i], columnNames.get(i), contentValues);
        }
        db.update(tableName, contentValues,
                query, selectArgs);
    }

    public void insert(Object[] answers) {

        if (rowExist()) {
            update(answers, hasUniqueIdentifier ? unique_identifier_val : ben_code);
        } else {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("ben_code", ben_code);
            if (hasUniqueIdentifier)
                contentValues.put(unique_identifier_name, unique_identifier_val);
            for (int i = 0; i < answers.length; i++) {
                populateContentValue(answers[i], columnNames.get(i), contentValues);
            }
            if (tableName.equals("gpdp_basic_info_1"))
                contentValues.put("survey_date", getDateAndTime());
            db.insert(tableName, null, contentValues);
        }
    }

    private boolean rowExist() {
        SQLiteDatabase db = this.getReadableDatabase();


        String query;
        String[] selectArgs;
        if (hasUniqueIdentifier) {
            query = "ben_code = ? and " + unique_identifier_name + " = ?";
            selectArgs = new String[]{ben_code, unique_identifier_val};
        } else {
            query = "ben_code = ?";
            selectArgs = new String[]{ben_code};
        }

        Cursor c = db.rawQuery("select ben_code from " + tableName + " where " + query, selectArgs);

        if (c == null) return false;
        if (!c.moveToFirst()) return false;
        boolean f = !c.isNull(0);
        c.close();
        return f;
    }

    private String getConcatCols() {
        StringBuilder s = new StringBuilder();
        for (String s1 : columnNames) {
            s.append(s1);
            s.append(",");
        }
        s.deleteCharAt(s.length() - 1);
        return s.toString();
    }

    private Cursor getCursor(String code) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query;
        String[] selectArgs;
        if (hasUniqueIdentifier) {
            query = "ben_code = ? and " + unique_identifier_name + " = ?";
            selectArgs = new String[]{ben_code, code};
        } else {
            query = "ben_code = ?";
            selectArgs = new String[]{code};
        }

        if (rowExist())
            return db.rawQuery(
                    "select " + getConcatCols() + " from " + tableName + " where " + query, selectArgs
            );
        else return null;
    }

    public ArrayList<Object> getOneRow(String code) {

        ArrayList<Object> list = new ArrayList<>();


        Cursor cursor = getCursor(code);
        if (cursor == null) {
            return list;
        }
        if (cursor.moveToFirst()) {
            int columnCount = cursor.getColumnCount();
            if (columnCount > 0) {
                for (int i = 0; i < columnCount; i++) {
                    int type = cursor.getType(i);
                    if (type == Cursor.FIELD_TYPE_STRING)
                        list.add(cursor.getString(i));
                    else if (type == Cursor.FIELD_TYPE_INTEGER)
                        list.add(cursor.getInt(i));
                    else if (type == Cursor.FIELD_TYPE_FLOAT)
                        list.add(cursor.getDouble(i));
                    else if (type == Cursor.FIELD_TYPE_NULL) {
                        if (dataTypes.get(i) == 0)
                            list.add("x");
                        else if (dataTypes.get(i) == 1)
                            list.add(0);
                    }
                }

                return list;
            } else {
                return list;
            }
        } else {
            return list;
        }
    }

    public boolean tableExists(String tableName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name= ?", new String[]{tableName}
        );
        int count = cursor.getCount();
        cursor.close();
        return count != 0;
    }

    public JSONArray createJsonFromLocalDatabase() throws JSONException {

        JSONObject innermost = new JSONObject();
        JSONArray secondlayer = new JSONArray();
        JSONObject thirdlayer = new JSONObject();
        JSONArray payloadArray = new JSONArray();


        SQLiteDatabase dat = instance.getReadableDatabase();

        Cursor cursor = dat
                .rawQuery(
                        "select name from sqlite_master where type= ? and name like ?",
                        new String[]{"table", "gpdp_%"});
        if (cursor.moveToFirst()) {
            do {
                String tablename = cursor.getString(0);

                Cursor inner = dat.rawQuery("select * from " + tablename + " where ben_code = ?", new String[]{ben_code});
                if (inner.moveToFirst()) {
                    do {
                        for (int i = 0; i < inner.getColumnCount(); i++) {
                            int type = inner.getType(i);
                            Object val = "";
                            if (type == Cursor.FIELD_TYPE_STRING) {
                                val = inner.getString(i);
                            } else if (type == Cursor.FIELD_TYPE_INTEGER) {
                                val = inner.getInt(i);
                            } else if (type == Cursor.FIELD_TYPE_FLOAT) {
                                val = inner.getDouble(i);
                            }
                            innermost.put(inner.getColumnName(i), val);
                        }
                        secondlayer.put(innermost);
                        innermost = new JSONObject();
                    } while (inner.moveToNext());

                    thirdlayer.put(tablename, secondlayer);
                    secondlayer = new JSONArray();
                    payloadArray.put(thirdlayer);
                    thirdlayer = new JSONObject();
                }

                inner.close();
            } while (cursor.moveToNext());

        }
        cursor.close();

        return payloadArray;
    }
}
