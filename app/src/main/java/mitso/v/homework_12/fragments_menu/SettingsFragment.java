package mitso.v.homework_12.fragments_menu;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.Toast;

import mitso.v.homework_12.R;
import mitso.v.homework_12.fragments_menu.database.DatabaseHelper;

public class SettingsFragment extends Fragment {

    private String SORT_DATABASE_BY = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.settings_fragment, container, false);

        RadioGroup mRadioGroup_Gender = (RadioGroup) view.findViewById(R.id.rg_SortBy_RF);
        mRadioGroup_Gender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.sortByLogin:
                        SORT_DATABASE_BY = DatabaseHelper.PERSON_LOGIN;
                        Toast.makeText(getContext(), SORT_DATABASE_BY, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sortByPassword:
                        SORT_DATABASE_BY = DatabaseHelper.PERSON_PASSWORD;
                        Toast.makeText(getContext(), SORT_DATABASE_BY, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sortByFirstName:
                        SORT_DATABASE_BY = DatabaseHelper.PERSON_FIRST_NAME;
                        Toast.makeText(getContext(), SORT_DATABASE_BY, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sortByLastName:
                        SORT_DATABASE_BY = DatabaseHelper.PERSON_LAST_NAME;
                        Toast.makeText(getContext(), SORT_DATABASE_BY, Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.sortByGender:
                        SORT_DATABASE_BY = DatabaseHelper.PERSON_GENDER;
                        Toast.makeText(getContext(), SORT_DATABASE_BY, Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });

        return view;
    }

    private void savePreference() {
        SharedPreferences sPref = getActivity().getPreferences(0x0000);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString("sort by", SORT_DATABASE_BY);
        ed.apply();
        Toast.makeText(getContext(), SORT_DATABASE_BY, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStop() {
        super.onStop();
        savePreference();
    }
}
