package app.snapmate.facebook;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MethodPageAdapter extends FragmentPagerAdapter {
    private int numOfTabs;

    public MethodPageAdapter(FragmentManager fm, int numOfTabs){
        super(fm);
        this.numOfTabs=numOfTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
            switch (position) {
                case 0:
                    return new FirstMethod();
                default:
                    return null;
            }
        }
        else {
            switch (position){

                case 0:
                    return new FirstMethod();
            case 1:
                return new SecondMethod();
                default:
                    return null;
        }

        }

    }

    @Override
    public int getCount() {
        return numOfTabs;
    }
}
