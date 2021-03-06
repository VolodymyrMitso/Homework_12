package mitso.v.homework_12.fragments_menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import mitso.v.homework_12.R;
import mitso.v.homework_12.constants.Constants;
import mitso.v.homework_12.fragments_menu.database.DatabaseHelper;

public class SettingsFragment extends Fragment {

    private String sortDatabaseBy = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        RadioGroup mRadioGroup_Gender = (RadioGroup) view.findViewById(R.id.rg_SortBy_RF);
        mRadioGroup_Gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rb_sortByLogin_SF:
                        sortDatabaseBy = DatabaseHelper.PERSON_LOGIN;
                        break;
                    case R.id.rb_sortByPassword_SF:
                        sortDatabaseBy = DatabaseHelper.PERSON_PASSWORD;
                        break;
                    case R.id.rb_sortByFirstName_SF:
                        sortDatabaseBy = DatabaseHelper.PERSON_FIRST_NAME;
                        break;
                    case R.id.rb_sortByLastName_SF:
                        sortDatabaseBy = DatabaseHelper.PERSON_LAST_NAME;
                        break;
                    case R.id.rb_sortByGender_SF:
                        sortDatabaseBy = DatabaseHelper.PERSON_GENDER;
                        break;
                }
            }
        });

        return view;
    }

    private void savePreference() {
        SharedPreferences sPref = getActivity().getPreferences(getContext().MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(Constants.SAVED_SORT_BY_KEY, sortDatabaseBy);
        ed.apply();
    }

    @Override
    public void onStop() {
        super.onStop();
        savePreference();
    }
}
