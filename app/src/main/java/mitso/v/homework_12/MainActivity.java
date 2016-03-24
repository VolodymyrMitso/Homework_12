package mitso.v.homework_12;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mitso.v.homework_12.constants.Constants;
import mitso.v.homework_12.fragments.RegistrationDialogFragment;
import mitso.v.homework_12.fragments.RegistrationFragment;
import mitso.v.homework_12.fragments.SignInFragment;
import mitso.v.homework_12.fragments_menu.AboutFragment;
import mitso.v.homework_12.fragments_menu.SettingsFragment;
import mitso.v.homework_12.fragments_menu.database.ShowUsersFragment;
import mitso.v.homework_12.interfaces.EventHandler;
import mitso.v.homework_12.models.Person;

public class MainActivity extends AppCompatActivity implements EventHandler {

    private ArrayList<Person> persons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        persons = loadList();
        if (savedInstanceState == null)
            commitSignInFragment();
    }

    private void commitSignInFragment() {
            SignInFragment signInFragment = new SignInFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fl_FragmentContainer_AM, signInFragment, Constants.SIGN_IN_FRAGMENT_TAG)
                    .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                    .addToBackStack(Constants.SIGN_IN_FRAGMENT_TAG)
                    .commit();
            signInFragment.setEventHandler(this);
    }

    @Override
    public void signIn(String _login, String _password, AlertDialog _alertDialog) {
        persons = loadList();
        MainSupport.signInSupport(this, persons, _login, _password, _alertDialog);
    }

    @Override
    public void openRegistrationFragment() {
        commitRegistrationFragment();
    }

    private void commitRegistrationFragment() {
        RegistrationFragment registrationFragment = new RegistrationFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_FragmentContainer_AM, registrationFragment, Constants.REGISTRATION_FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .addToBackStack(Constants.REGISTRATION_FRAGMENT_TAG)
                .commit();
        registrationFragment.setEventHandler(this);
    }

    @Override
    public void registerPerson(String _login, String _password, String _firstName, String _lastName, String _gender) {
        RegistrationDialogFragment dialog = new RegistrationDialogFragment();
        Bundle args = new Bundle();

        if (MainSupport.personDataCheck(this, _login, _password, _firstName, _lastName, _gender)) {
            Person person = new Person();
            person.setLogin(_login);
            person.setPassword(_password);
            person.setFirstName(_firstName);
            person.setLastName(_lastName);
            person.setGender(_gender);

            persons = loadList();
            persons.add(person);
            saveList(persons);

            args.putString(Constants.KEY_DIALOG_MESSAGE,
                    getResources().getString(R.string.s_dm_user_n) +
                            person.getFirstName() + " " + person.getLastName() +
                            getResources().getString(R.string.s_dm_n_registered));

        } else
            args.putString(Constants.KEY_DIALOG_MESSAGE,
                    getResources().getString(R.string.s_dm_emptyFields));

        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), Constants.DIALOG_FRAGMENT_TAG);
    }

    @Override
    protected void onResume() {
        super.onResume();

        persons = loadList();

        if (getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM) instanceof SignInFragment) {
            SignInFragment signInFragment =
                    (SignInFragment) getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM);
            signInFragment.setEventHandler(this);
        } else if (getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM) instanceof RegistrationFragment) {
            RegistrationFragment registrationFragment =
                    (RegistrationFragment) getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM);
            registrationFragment.setEventHandler(this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        persons = loadList();
        saveList(persons);

        if (getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM) instanceof SignInFragment) {
            SignInFragment signInFragment =
                    (SignInFragment) getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM);
            signInFragment.releaseEventHandler();
        } else if (getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM) instanceof RegistrationFragment) {
            RegistrationFragment registrationFragment =
                    (RegistrationFragment) getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM);
            registrationFragment.releaseEventHandler();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() >= 1) {
            if (getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM) instanceof SignInFragment)
                finish();
            else
                commitSignInFragment();
        } else {
            super.onBackPressed();
        }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mi_Settings:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_FragmentContainer_AM, new SettingsFragment(), Constants.SETTINGS_FRAGMENT_TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(Constants.SETTINGS_FRAGMENT_TAG)
                        .commit();
                return true;
            case R.id.mi_ShowUsers:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_FragmentContainer_AM, new ShowUsersFragment(), Constants.SHOW_USERS_FRAGMENT_TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(Constants.SHOW_USERS_FRAGMENT_TAG)
                        .commit();
                return true;
            case R.id.mi_About:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_FragmentContainer_AM, new AboutFragment(), Constants.ABOUT_FRAGMENT_TAG)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .addToBackStack(Constants.ABOUT_FRAGMENT_TAG)
                        .commit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void saveList(ArrayList<Person> persons) {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        Gson gson = new Gson();
        String jsonPersons = gson.toJson(persons);
        ed.putString(Constants.SAVED_LIST_KEY, jsonPersons);
        ed.apply();
    }

    public ArrayList<Person> loadList() {
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        List<Person> persons;
        if (sharedPreferences.contains(Constants.SAVED_LIST_KEY)) {
            String jsonFavorites = sharedPreferences.getString(Constants.SAVED_LIST_KEY, null);
            Gson gson = new Gson();
            Person[] personsArray = gson.fromJson(jsonFavorites,
                    Person[].class);
            persons = Arrays.asList(personsArray);
            persons = new ArrayList<>(persons);
        } else
            return new ArrayList<>();
        return (ArrayList<Person>) persons;
    }
}