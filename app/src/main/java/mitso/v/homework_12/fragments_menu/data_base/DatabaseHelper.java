package mitso.v.homework_12.fragments_menu.data_base;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    Context context;

    private static final String DATABASE_NAME = "persons.db";
    private static final int DATABASE_VERSION = 1;

    public static final String DATABASE_TABLE = "persons";

    public static final String PERSON_LOGIN = "PERSON_LOGIN";
    public static final String PERSON_PASSWORD = "PERSON_PASSWORD";
    public static final String PERSON_FIRST_NAME = "PERSON_FIRST_NAME";
    public static final String PERSON_LAST_NAME = "PERSON_LAST_NAME";
    public static final String PERSON_GENDER = "PERSON_GENDER";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE persons (" +
                PERSON_LOGIN + " TEXT, " +
                PERSON_PASSWORD + " TEXT, " +
                PERSON_FIRST_NAME + " TEXT, " +
                PERSON_LAST_NAME + " TEXT, " +
                PERSON_GENDER + " TEXT);");
//
//        ContentValues cv = new ContentValues();
//
//        cv.put(PERSON_LOGIN, "genius789");
//        cv.put(PERSON_PASSWORD, "z0951308575");
//        cv.put(PERSON_FIRST_NAME, "VOLODYMYR");
//        cv.put(PERSON_LAST_NAME, "MITSO");
//        cv.put(PERSON_GENDER, "MALE");
//
//        db.insert(DATABASE_TABLE, PERSON_LOGIN, cv);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
