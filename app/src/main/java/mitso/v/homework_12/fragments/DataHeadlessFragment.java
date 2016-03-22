package mitso.v.homework_12.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import mitso.v.homework_12.constants.Constants;
import mitso.v.homework_12.models.Person;

public class DataHeadlessFragment extends Fragment {

    private ArrayList<Person> persons;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (savedInstanceState != null)
            restoreState(savedInstanceState);
        setRetainInstance(true);
        return null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(Constants.KEY_PERSON_LIST, persons);
    }

    private void restoreState(Bundle savedInstanceState) {
        persons = savedInstanceState.getParcelableArrayList(Constants.KEY_PERSON_LIST);
    }


    public ArrayList<Person> getPersons() {
        return persons;
    }

    public void setPersons(ArrayList<Person> _persons) {
        persons = _persons;
    }
}