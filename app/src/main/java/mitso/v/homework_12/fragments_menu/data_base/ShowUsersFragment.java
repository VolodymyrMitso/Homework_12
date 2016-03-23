package mitso.v.homework_12.fragments_menu.data_base;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

//        ArrayList<Person> listPersons = ((MainActivity) getActivity()).getDataFragment().getPersons();
        ArrayList<Person> listPersons = loadList();

//        if (persons != null)
//            Toast.makeText(getActivity(), String.valueOf(persons.size()), Toast.LENGTH_LONG).show();
//        else
//            Toast.makeText(getActivity(), "null", Toast.LENGTH_LONG).show();


        if (listPersons != null)
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

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor = (Cursor) l.getItemAtPosition(position);

        final String personLogin = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_LOGIN));
        final String personPassword = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_PASSWORD));
        String personFirstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_FIRST_NAME));
        String personLastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_LAST_NAME));
        String personGender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_GENDER));


        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Edit person");
        LayoutInflater inflater = getActivity().getLayoutInflater();
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.person_card_edit, null);
        alertDialog.setView(relativeLayout);

        final EditText personLoginEdit = (EditText) relativeLayout.findViewById(R.id.person_login_edit);
        final EditText personPasswordEdit = (EditText) relativeLayout.findViewById(R.id.person_password_edit);
        final EditText personFirstNameEdit = (EditText) relativeLayout.findViewById(R.id.person_first_name_edit);
        final EditText personLastNameEdit = (EditText) relativeLayout.findViewById(R.id.person_last_name_edit);
        final EditText personGenderEdit = (EditText) relativeLayout.findViewById(R.id.person_gender_edit);

        personLoginEdit.setText(personLogin);
        personPasswordEdit.setText(personPassword);

        alertDialog.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.PERSON_LOGIN, personLoginEdit.getText().toString());
                values.put(DatabaseHelper.PERSON_PASSWORD, personPasswordEdit.getText().toString());

                mDatabaseHelper.getWritableDatabase().update(mDatabaseHelper.DATABASE_TABLE, values, mDatabaseHelper.PERSON_LOGIN + "= ?", new String[]{personLogin});
                mDatabaseHelper.getWritableDatabase().update(mDatabaseHelper.DATABASE_TABLE, values, mDatabaseHelper.PERSON_PASSWORD + "= ?", new String[]{personPassword});

                ((CursorAdapter)getListAdapter()).changeCursor(doQuery());

            }
        });

//        myDialog.setNeutralButton("Update", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface arg0, int arg1) {
//
//            }
//        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        alertDialog.show();

//        Toast.makeText(getActivity(), String.valueOf(id), Toast.LENGTH_SHORT).show();
    }

    public ArrayList<Person> loadList() {
        SharedPreferences sPref = getActivity().getPreferences(0x0000);
        List<Person> persons;
        if (sPref.contains("list")) {
            String jsonFavorites = sPref.getString("list", null);
            Gson gson = new Gson();
            Person[] personsArray = gson.fromJson(jsonFavorites,
                    Person[].class);
            persons = Arrays.asList(personsArray);
            persons = new ArrayList<Person>(persons);
        } else
            return new ArrayList<Person>();

        Toast.makeText(getContext(), "list loaded", Toast.LENGTH_LONG).show();

        return (ArrayList<Person>) persons;
    }
}
