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
public class Help_Page3 extends Fragment {

    public static Fragment newInstance(Context context) {
        Help_Page3 f = new Help_Page3();

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_help_page3, null);
        return root;
    }

}
