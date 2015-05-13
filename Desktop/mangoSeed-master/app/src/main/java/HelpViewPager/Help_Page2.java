package HelpViewPager;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import e_commerce.e_commerce.R;

/**
 * Created by Suganprabu on 18-03-2015.
 */
public class Help_Page2 extends Fragment {

    public static Fragment newInstance(Context context) {
        Help_Page2 f = new Help_Page2();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_help_page2, null);
        return root;
    }

}
