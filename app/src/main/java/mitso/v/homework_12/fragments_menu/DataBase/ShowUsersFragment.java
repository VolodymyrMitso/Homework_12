package mitso.v.homework_12.fragments_menu.DataBase;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.SimpleCursorAdapter;

import mitso.v.homework_12.R;

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
    }

    @Override
    public void onDestroy() {
        ((CursorAdapter)getListAdapter()).getCursor().close();
        mDatabaseHelper.close();
        super.onDestroy();
    }

    private Cursor doQuery() {
        Cursor result= mDatabaseHelper.getReadableDatabase()
                .query(DatabaseHelper.DATABASE_TABLE,
                        new String[] { "ROWID AS _id",
                                DatabaseHelper.PERSON_LOGIN,
                                DatabaseHelper.PERSON_PASSWORD,
                                DatabaseHelper.PERSON_FIRST_NAME,
                                DatabaseHelper.PERSON_LAST_NAME,
                                DatabaseHelper.PERSON_GENDER },
                        null, null, null, null, DatabaseHelper.PERSON_LOGIN);
        return(result);
    }
}
