package mitso.v.homework_12;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import mitso.v.homework_12.constants.Constants;
import mitso.v.homework_12.fragments.DataHeadlessFragment;
import mitso.v.homework_12.fragments.RegistrationDialogFragment;
import mitso.v.homework_12.fragments.RegistrationFragment;
import mitso.v.homework_12.fragments.SignInFragment;
import mitso.v.homework_12.fragments_menu.AboutFragment;
import mitso.v.homework_12.fragments_menu.BaseFragment;
import mitso.v.homework_12.fragments_menu.DataBase.ShowUsersFragment;
import mitso.v.homework_12.fragments_menu.SettingsFragment;
import mitso.v.homework_12.interfaces.EventHandler;
import mitso.v.homework_12.models.Person;

public class MainActivity extends AppCompatActivity implements EventHandler {

    private ArrayList<Person> persons = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (savedInstanceState == null) {
            commitSignInFragment();
            commitHeadlessFragment();
        }
    }

    private void commitSignInFragment() {
        SignInFragment signInFragment = new SignInFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_FragmentContainer_AM, signInFragment, Constants.SIGN_IN_FRAGMENT_TAG)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
        signInFragment.setEventHandler(this);
    }

    private void commitHeadlessFragment() {
        DataHeadlessFragment dataHeadlessFragment = new DataHeadlessFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(dataHeadlessFragment, Constants.HEADLESS_FRAGMENT_TAG)
                .commit();
    }

    public DataHeadlessFragment getDataFragment () {
        return (DataHeadlessFragment) getSupportFragmentManager().findFragmentByTag(Constants.HEADLESS_FRAGMENT_TAG);
    }

    @Override
    public void signIn(String _login, String _password, AlertDialog _alertDialog) {
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
                .addToBackStack(null)
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

            persons.add(person);
            getDataFragment().setPersons(persons);

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
        if (getDataFragment().getPersons() != null)
            persons = getDataFragment().getPersons();

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
        getDataFragment().setPersons(persons);

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
        super.onBackPressed();
        if (getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM) instanceof SignInFragment) {
            SignInFragment signInFragment =
                    (SignInFragment) getSupportFragmentManager().findFragmentById(R.id.fl_FragmentContainer_AM);
            signInFragment.setEventHandler(this);
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
                updateFragment(new SettingsFragment());
                return true;
            case R.id.mi_ShowUsers:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fl_FragmentContainer_AM, new ShowUsersFragment())
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                return true;
            case R.id.mi_About:
                updateFragment(new AboutFragment());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateFragment(BaseFragment baseFragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fl_FragmentContainer_AM, baseFragment)
                .addToBackStack(null)
                .commitAllowingStateLoss();
    }
}