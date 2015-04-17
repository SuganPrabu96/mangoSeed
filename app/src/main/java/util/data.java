package util;

import java.util.Arrays;
import java.util.List;

import NavigationDrawer.NavDrawerItem;
import e_commerce.e_commerce.R;

public class data {
    public static NavDrawerItem[] navtitles={
            new NavDrawerItem("Products", R.drawable.ic_launcher),
            new NavDrawerItem("My account", R.drawable.ic_launcher),
            new NavDrawerItem("General Settings", R.drawable.ic_launcher),
            new NavDrawerItem("Notifications", R.drawable.ic_launcher),
            new NavDrawerItem("Order History", R.drawable.ic_launcher),
            new NavDrawerItem("About", R.drawable.ic_launcher),
            new NavDrawerItem("Help", R.drawable.ic_launcher),
            new NavDrawerItem("Logout", R.drawable.ic_launcher)
    };
    public static List<NavDrawerItem> getNavDrawerItems(){
        return Arrays.asList(navtitles);
    }
}
