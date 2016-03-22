package mitso.v.homework_12.fragments_menu;

import android.content.Context;
import android.support.v4.app.Fragment;

import mitso.v.homework_12.MainActivity;

public class BaseFragment extends Fragment {

    protected MainActivity mMainActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMainActivity = (MainActivity) context;
    }
}
