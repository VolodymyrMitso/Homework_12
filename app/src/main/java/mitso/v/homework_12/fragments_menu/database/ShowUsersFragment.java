package mitso.v.homework_12.fragments_menu.database;

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

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mitso.v.homework_12.R;
import mitso.v.homework_12.models.Person;

public class ShowUsersFragment extends ListFragment {

    private DatabaseHelper mDatabaseHelper = null;
    private Cursor mCursor = null;

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
                                DatabaseHelper.KEY_ID,
                                DatabaseHelper.PERSON_LOGIN,
                                DatabaseHelper.PERSON_PASSWORD,
                                DatabaseHelper.PERSON_FIRST_NAME,
                                DatabaseHelper.PERSON_LAST_NAME,
                                DatabaseHelper.PERSON_GENDER },
                        new int[] {
                                R.id.person_id,
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
                        new String[] {
                                DatabaseHelper.KEY_ID,
                                DatabaseHelper.PERSON_LOGIN,
                                DatabaseHelper.PERSON_PASSWORD,
                                DatabaseHelper.PERSON_FIRST_NAME,
                                DatabaseHelper.PERSON_LAST_NAME,
                                DatabaseHelper.PERSON_GENDER },
                        null, null, null, null, sortBy());
    }

    private void checkList() {
        ArrayList<Person> databasePersons = getDatabasePersons();
        ArrayList<Person> listPersons = loadList();

        if (listPersons != null) {
            if (listPersons.size() > databasePersons.size()) {

                for (int i = databasePersons.size(); i < listPersons.size(); i++) {
                    ContentValues cv = new ContentValues();

                    cv.put(DatabaseHelper.PERSON_LOGIN, listPersons.get(i).getLogin());
                    cv.put(DatabaseHelper.PERSON_PASSWORD, listPersons.get(i).getPassword());
                    cv.put(DatabaseHelper.PERSON_FIRST_NAME, listPersons.get(i).getFirstName());
                    cv.put(DatabaseHelper.PERSON_LAST_NAME, listPersons.get(i).getLogin());
                    cv.put(DatabaseHelper.PERSON_GENDER, listPersons.get(i).getGender());

                    mDatabaseHelper.getWritableDatabase().insert(DatabaseHelper.DATABASE_TABLE, DatabaseHelper.PERSON_LOGIN, cv);
                    ((CursorAdapter) getListAdapter()).changeCursor(doQuery());
                }
            }
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        Cursor cursor = (Cursor) l.getItemAtPosition(position);

        final String personLogin = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_LOGIN));
        final String personPassword = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_PASSWORD));
        final String personFirstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_FIRST_NAME));
        final String personLastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_LAST_NAME));
        final String personGender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_GENDER));

        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Edit / Delete");
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
        personFirstNameEdit.setText(personFirstName);
        personLastNameEdit.setText(personLastName);
        personGenderEdit.setText(personGender);

        final long finalID = id;

        alertDialog.setNegativeButton("Edit", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {

                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.PERSON_LOGIN, personLoginEdit.getText().toString());
                values.put(DatabaseHelper.PERSON_PASSWORD, personPasswordEdit.getText().toString());
                values.put(DatabaseHelper.PERSON_FIRST_NAME, personFirstNameEdit.getText().toString());
                values.put(DatabaseHelper.PERSON_LAST_NAME, personLastNameEdit.getText().toString());
                values.put(DatabaseHelper.PERSON_GENDER, personGenderEdit.getText().toString());

                mDatabaseHelper.getWritableDatabase().update(DatabaseHelper.DATABASE_TABLE,
                        values, DatabaseHelper.KEY_ID + "=" + finalID, null);

                ((CursorAdapter) getListAdapter()).changeCursor(doQuery());
            }
        });

        alertDialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface arg0, int arg1) {
                mDatabaseHelper.getWritableDatabase().delete(DatabaseHelper.DATABASE_TABLE,
                        DatabaseHelper.KEY_ID + "=" + finalID, null);

                ((CursorAdapter) getListAdapter()).changeCursor(doQuery());
            }
        });

        alertDialog.show();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveList(getDatabasePersons());
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
            persons = new ArrayList<>(persons);
        } else
            return new ArrayList<>();
        return (ArrayList<Person>) persons;
    }

    public void saveList(ArrayList<Person> persons) {
        SharedPreferences sPref = getActivity().getPreferences(0x0000);
        SharedPreferences.Editor ed = sPref.edit();
        Gson gson = new Gson();
        String jsonPersons = gson.toJson(persons);
        ed.putString("list", jsonPersons);
        ed.apply();
    }

    private String sortBy() {
        SharedPreferences sPref = getActivity().getPreferences(0x0000);
        return sPref.getString("sort by", DatabaseHelper.KEY_ID);
    }

    private ArrayList<Person> getDatabasePersons() {
        ArrayList<Person> databasePersons = new ArrayList<>();

        Cursor cursor = mDatabaseHelper.getWritableDatabase().query(DatabaseHelper.DATABASE_TABLE, new String[]{
                        DatabaseHelper.KEY_ID,
                        DatabaseHelper.PERSON_LOGIN,
                        DatabaseHelper.PERSON_PASSWORD,
                        DatabaseHelper.PERSON_FIRST_NAME,
                        DatabaseHelper.PERSON_LAST_NAME,
                        DatabaseHelper.PERSON_GENDER},
                null, null, null, null, null);

        while (cursor.moveToNext()) {
            String personLogin = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_LOGIN));
            String personPassword = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_PASSWORD));
            String personFirstName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_FIRST_NAME));
            String personLastName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_LAST_NAME));
            String personGender = cursor.getString(cursor.getColumnIndex(DatabaseHelper.PERSON_GENDER));

            databasePersons.add(new Person(personLogin, personPassword, personFirstName, personLastName, personGender));
        }
        cursor.close();

        return databasePersons;
    }
}
