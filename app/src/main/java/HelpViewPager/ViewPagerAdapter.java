package HelpViewPager;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Suganprabu on 18-03-2015.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {
    private Context _context;
    public static int totalPage=3;
    public ViewPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        _context=context;

    }
    @Override
    public Fragment getItem(int position) {
        Fragment f = new Fragment();
        switch(position){
            case 0:
                f=Help_Page1.newInstance(_context);
                break;
            case 1:
                f= Help_Page2.newInstance(_context);
                break;
            case 2:
                f= Help_Page3.newInstance(_context);
                break;
        }
        return f;
    }
    @Override
    public int getCount() {
        return totalPage;
    }

}
