package mitso.v.homework_12.fragments_menu.data_base;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import java.util.ArrayList;

import mitso.v.homework_12.MainActivity;
import mitso.v.homework_12.R;
import mitso.v.homework_12.models.Person;

public class ShowUsersFragment extends ListFragment {

    private DatabaseHelper mDatabaseHelper = null;
    private Cursor mCursor = null;

//    private SQLiteDatabase mSqLiteDatabase;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        setRetainInstance(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SimpleCursorAdapter adapter=
                new SimpleCursorAdapter(getActivity(), R.layout.person_card,
                        mCursor,
                        new String[] {
                                DatabaseHelper.PERSON_LOGIN,
                                DatabaseHelper.PERSON_PASSWORD,
                                DatabaseHelper.PERSON_FIRST_NAME,
                                DatabaseHelper.PERSON_LAST_NAME,
                                DatabaseHelper.PERSON_GENDER },
                        new int[] {
                                R.id.person_login,
                                R.id.person_password,
                                R.id.person_first_name,
                                R.id.person_last_name,
                                R.id.person_gender },
                        0);

        setListAdapter(adapter);

        if (mCursor == null) {
            mDatabaseHelper = new DatabaseHelper(getActivity());
            ((CursorAdapter) getListAdapter()).changeCursor(doQuery());
        }

        checkList();
    }

    @Override
    public void onDestroy() {
        ((CursorAdapter)getListAdapter()).getCursor().close();
        mDatabaseHelper.close();
        super.onDestroy();
    }

    private Cursor doQuery() {
        return mDatabaseHelper.getReadableDatabase()
                .query(DatabaseHelper.DATABASE_TABLE,
                        new String[] { "ROWID AS _id",
                                DatabaseHelper.PERSON_LOGIN,
                                DatabaseHelper.PERSON_PASSWORD,
                                DatabaseHelper.PERSON_FIRST_NAME,
                                DatabaseHelper.PERSON_LAST_NAME,
                                DatabaseHelper.PERSON_GENDER },
                        null, null, null, null, DatabaseHelper.PERSON_LOGIN);
    }

    private void checkList() {

        ArrayList<Person> dataBasePersons = new ArrayList<>();

        Cursor cursor = mDatabaseHelper.getWritableDatabase().query(DatabaseHelper.DATABASE_TABLE, new String[]{
                        DatabaseHelper.PERSON_LOGIN,
                        DatabaseHelper.PERSON_PASSWORD,
                        DatabaseHelper.PERSON_FIRST_NAME,
                        DatabaseHelper.PERSON_LAST_NAME,
                        DatabaseHelper.PERSON_GENDER},
                null, null, null, null, DatabaseHelper.PERSON_LOGIN);

        while (cursor.moveToNext()) {
            String personLogin = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_LOGIN));
            String personPassword = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_PASSWORD));
            String personFirstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_FIRST_NAME));
            String personLastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_LAST_NAME));
            String personGender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_GENDER));

            dataBasePersons.add(new Person(personLogin, personPassword, personFirstName, personLastName, personGender));
        }
        cursor.close();

//        if (dataBasePersons != null)
//            Toast.makeText(getActivity(), String.valueOf(dataBasePersons.size()), Toast.LENGTH_LONG).show();
//        else
//            Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();

        ArrayList<Person> listPersons = ((MainActivity) getActivity()).getDataFragment().getPersons();

//        if (persons != null)
//            Toast.makeText(getActivity(), String.valueOf(persons.size()), Toast.LENGTH_LONG).show();
//        else
//            Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();


        if (listPersons != null) {
            if (listPersons.size() > dataBasePersons.size())

                for (int i = dataBasePersons.size(); i < listPersons.size(); i++) {
                    ContentValues cv = new ContentValues();

                    cv.put(DatabaseHelper.PERSON_LOGIN, listPersons.get(i).getLogin());
                    cv.put(DatabaseHelper.PERSON_PASSWORD, listPersons.get(i).getPassword());
                    cv.put(DatabaseHelper.PERSON_FIRST_NAME, listPersons.get(i).getFirstName());
                    cv.put(DatabaseHelper.PERSON_LAST_NAME, listPersons.get(i).getLogin());
                    cv.put(DatabaseHelper.PERSON_GENDER, listPersons.get(i).getGender());

                    mDatabaseHelper.getWritableDatabase().insert(DatabaseHelper.DATABASE_TABLE, DatabaseHelper.PERSON_LOGIN, cv);
                    ((CursorAdapter)getListAdapter()).changeCursor(doQuery());
                }
        }
    }
}
