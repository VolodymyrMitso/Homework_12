package mitso.v.homework_12.fragments_menu;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import mitso.v.homework_12.R;

public class ShowUsersFragment extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.show_users_fragments, container, false);

        return view;
    }

}
