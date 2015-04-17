package e_commerce.e_commerce;

/**
 * Created by Suganprabu on 17-04-2015.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import HelpViewPager.ViewPagerAdapter;
import ItemDisplay.CardAdapter;
import ItemDisplay.CategoryCardAdapter;
import ItemDisplay.CategoryCardClass;
import ItemDisplay.ItemDetailsClass;
import ItemDisplay.SubcategoryCardAdapter;
import ItemDisplay.SubcategoryCardClass;
import NavigationDrawer.NavDrawerItem;
import NavigationDrawer.NavDrawerListAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import util.ServiceHandler;
import util.data;

public class Master extends ActionBarActivity {

    public static FragmentManager SupportFragmentManager;
    public static boolean downloadImagesOverWifi, Notifications;
    public static ProfilePictureView facebookProfileIcon;
    public static TextView profileIconText;
    public static ImageView profileIcon;
    public static CircleImageView googleProfileIcon;
    public static String modeOfLogin;
    public static int numCategories, numSubCategories[], categoryID[];
    public static ArrayList<String> categoryName;
    public static ArrayList<int[]> subcategoryID;
    public static ArrayList<String[]> subcategoryName;
    private final String categoriesURL = "http://grokart.ueuo.com/listCategories.php";
    public FragmentTransaction fragmentTransaction;
    public Dialog locationDialog;
    public String[] location = {"Chennai", "Adyar"}; // location[0] is city and location[1] is area
    ActionBar actionBar;
    private CharSequence mTitle = "E - Commerce"; //TODO change this to the name of the startup
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String productsJSON;

    // TODO change the initial value of location based on Shared prefs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modeOfLogin = getIntent().getExtras().getString("loginMethod").toString();

        setContentView(R.layout.nav_bar);

        categoryName = new ArrayList();

        facebookProfileIcon = (ProfilePictureView) findViewById(R.id.profilepic_facebook);
        profileIconText = (TextView) findViewById(R.id.profilepic_name);
        facebookProfileIcon.setCropped(true);
        profileIcon = (ImageView) findViewById(R.id.profilepic);
        googleProfileIcon = (CircleImageView) findViewById(R.id.profilepic_google);

        if(modeOfLogin.equals("Facebook")) {
            profileIcon.setVisibility(View.INVISIBLE);
            googleProfileIcon.setVisibility((View.INVISIBLE));
            facebookProfileIcon.setVisibility(View.VISIBLE);
        }

        else if(modeOfLogin.equals("Google")) {
            googleProfileIcon.setImageBitmap(LoginActivity.bmImage);
            profileIconText.setText(LoginActivity.profileText);
            profileIcon.setVisibility(View.INVISIBLE);
            facebookProfileIcon.setVisibility(View.INVISIBLE);
            googleProfileIcon.setVisibility(View.VISIBLE);

        }

        else {
            facebookProfileIcon.setVisibility(View.INVISIBLE);
            googleProfileIcon.setVisibility(View.INVISIBLE);
            profileIcon.setVisibility(View.VISIBLE);
            profileIconText.setText(LoginActivity.prefs.getString("Email",""));
        }

        actionBar = getSupportActionBar();

        Log.i("areaname", LoginActivity.prefs.getString("areaname", ""));
        if(!LoginActivity.prefs.getString("areaname","").equals(""))
            actionBar.setTitle(LoginActivity.prefs.getString("areaname",""));
        else
            actionBar.setTitle(location[1]);

        Window window = Master.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Master.this.getResources().getColor(R.color.myStatusBarColor));
        }

        SupportFragmentManager = getSupportFragmentManager();

        new LoadProductsList().execute();

        List<NavDrawerItem> datalist = data.getNavDrawerItems();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frame_container, new MyAccountFragment()); //TODO Edit this
        fragmentTransaction.commit();

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerList = (ListView) findViewById(R.id.list_slidermenu);

        drawerList.setAdapter(new NavDrawerListAdapter(getApplicationContext(), datalist));
        // Set the list's click listener
        drawerList.setOnItemClickListener(new DrawerItemClickListener());
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                drawerLayout,       /* DrawerLayout object */
                R.string.app_name,  /* "open drawer" description */
                R.string.app_name /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                //       setTitle(mTitle);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                //      setTitle(mTitle);
            }
        };
        drawerLayout.setDrawerListener(mDrawerToggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_drawer);
        mDrawerToggle.syncState();

    }

    private void selectItem(int position) {
        // Create a new fragment and specify the planet to show based on position
        // Insert the fragment by replacing any existing fragment
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        final Fragment f = fragmentManager.findFragmentById(R.id.frame_container);
        //if(fragmentTransaction.r))
        if (position != 0) {
            FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(0, 0, 0);
            getSupportFragmentManager().findFragmentById(R.id.frame_container)
                    .getView()
                    .setLayoutParams(lp);
        }

        if (position == 0) {
            fragmentTransaction.replace(R.id.frame_container, new ProductsFragment());
        } else if (position == 1) {
            fragmentTransaction.replace(R.id.frame_container, new MyAccountFragment());
        } else if (position == 2) {
            fragmentTransaction.replace(R.id.frame_container, new GeneralSettingsFragment());
        } else if (position == 3) {
            fragmentTransaction.replace(R.id.frame_container, new NotificationsFragment());
        } else if (position == 4) {
            fragmentTransaction.replace(R.id.frame_container, new OrderHistoryFragment());
        } else if (position == 5) {
            fragmentTransaction.replace(R.id.frame_container, new AboutFragment());
        } else if (position == 6) {
            fragmentTransaction.replace(R.id.frame_container, new HelpFragment());
        } else if (position == 7) {
            final AlertDialog.Builder logoutAlert = new AlertDialog.Builder(Master.this);

            logoutAlert.setCancelable(false);

            logoutAlert.setMessage("Are you sure");
            logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {

                    LoginActivity.prefs.edit().putString("Email", "").apply();
                    LoginActivity.prefs.edit().putString("Password", "").apply();
                    LoginActivity.prefs.edit().putString("Login", "").apply();
                    LoginActivity.prefs.edit().putString("Address", "").apply();
                    LoginActivity.prefs.edit().putString("Phone", "").apply();
                    LoginActivity.prefs.edit().putString("ProfilePic", "").apply();
                    LoginActivity.prefs.edit().putString("city", "").apply();
                    LoginActivity.prefs.edit().putString("area", "").apply();
                    LoginActivity.prefs.edit().putString("cityname", "").apply();
                    LoginActivity.prefs.edit().putString("areaname", "").apply();
                    LoginActivity.prefs.edit().putString("LoginMode", "").apply();
                    LoginActivity.prefs.edit().putString("LoginStatus", "Logged out").apply();
                    LoginActivity.prefs.edit().putString("Latitude", "").apply();
                    LoginActivity.prefs.edit().putString("Latitude", "").apply();
                    LoginActivity.prefs.edit().putString("Name", "").apply();

                    LoginActivity.prefs.edit().putString("Name", "").commit();
                    LoginActivity.prefs.edit().putString("Email", "").commit();
                    LoginActivity.prefs.edit().putString("Password", "").commit();
                    LoginActivity.prefs.edit().putString("Login", "").commit();
                    LoginActivity.prefs.edit().putString("Address", "").commit();
                    LoginActivity.prefs.edit().putString("Phone", "").commit();
                    LoginActivity.prefs.edit().putString("ProfilePic", "").commit();
                    LoginActivity.prefs.edit().putString("city", "").commit();
                    LoginActivity.prefs.edit().putString("area", "").commit();
                    LoginActivity.prefs.edit().putString("cityname", "").commit();
                    LoginActivity.prefs.edit().putString("areaname", "").commit();
                    LoginActivity.prefs.edit().putString("LoginMode", "").commit();
                    LoginActivity.prefs.edit().putString("LoginStatus", "Logged out").commit();
                    LoginActivity.prefs.edit().putString("Latitude", "").commit();
                    LoginActivity.prefs.edit().putString("Latitude", "").commit();

                    if (modeOfLogin.equals("App")) {
                        //TODO edit Shared prefs
                        startActivity(new Intent(Master.this, LoginActivity.class));
                        finish();
                        dialog.dismiss();
                    } else if (modeOfLogin.equals("Facebook")) {
                        LoginActivity.facebookLoginFragment.callFacebookLogout(Master.this);
                        startActivity(new Intent(Master.this, LoginActivity.class));
                        finish();
                        dialog.dismiss();
                    } else if (modeOfLogin.equals("Google")) {
                        LoginActivity.callGoogleLogout();
                        startActivity(new Intent(Master.this, LoginActivity.class));
                        finish();
                        dialog.dismiss();
                    }


                }
            });
            logoutAlert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            logoutAlert.create().show();

            if (f instanceof ProductsFragment) {
                fragmentTransaction.replace(R.id.frame_container, new ProductsFragment());
            } else if (f instanceof MyAccountFragment) {
                fragmentTransaction.replace(R.id.frame_container, new MyAccountFragment());
            } else if (f instanceof GeneralSettingsFragment) {
                fragmentTransaction.replace(R.id.frame_container, new GeneralSettingsFragment());

            } else if (f instanceof HelpFragment) {
                fragmentTransaction.replace(R.id.frame_container, new HelpFragment());

            } else if (f instanceof AboutFragment) {

                fragmentTransaction.replace(R.id.frame_container, new AboutFragment());

            } else if (f instanceof NotificationsFragment) {

                fragmentTransaction.replace(R.id.frame_container, new NotificationsFragment());

            }

        }

        fragmentTransaction.commit();
        // Highlight the selected item, update the title, and close the drawer
        drawerList.setItemChecked(position, true);
        if (position != 7) {
            for (int i = 0; i < drawerList.getChildCount(); i++)
                drawerList.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.NavigationBarUnselectedItem));
            drawerList.getChildAt(position).setBackgroundColor(getResources().getColor(R.color.NavigationBarSelectedItem));
        } else
            for (int i = 0; i < drawerList.getChildCount(); i++)
                drawerList.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.NavigationBarUnselectedItem));
        drawerLayout.closeDrawer(Gravity.START);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
//        getSupportActionBar().setTitle(title);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(Gravity.LEFT))
            drawerLayout.closeDrawer(Gravity.LEFT);

        else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_master, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        /*if (id == R.id.menu_master_location) {

            String[] loc_city = {"Chennai"};
            String[] loc_area = {"Adyar","Ambattur","Anna Nagar","Ashok Nagar","Avadi","Chrompet","Guindy","K.K Nagar","Kilpauk","Kodambakkam","Koyambedu","Mylapore","Nungambakkam","Pallavaram","Saidapet","Tambaram","T Nagar","Vadapalani","Velachery"};

            locationDialog = new Dialog(Master.this);
            locationDialog.setContentView(R.layout.choose_location);
            locationDialog.setCancelable(true);
            locationDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            locationDialog.setTitle("Location");
            locationDialog.show();

            final Spinner city = (Spinner) locationDialog.findViewById(R.id.spinnerLocationCity);
            final Spinner area = (Spinner) locationDialog.findViewById(R.id.spinnerLocationArea);

            ArrayAdapter<String> adapter_area = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loc_area);
            area.setAdapter(adapter_area);

            ArrayAdapter<String> adapter_city = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loc_city);
            city.setAdapter(adapter_city);

            for(int i=0;i<city.getCount();i++){
                if(city.getItemAtPosition(i).equals(location[0])){
                    city.setSelection(i);
                    break;
                }
            }

            for(int i=0;i<area.getCount();i++){
                if(area.getItemAtPosition(i).equals(location[1])){
                    area.setSelection(i);
                    Log.i("selected",String.valueOf(i));
                    break;
                }
            }

            if(!LoginActivity.prefs.getString("city","").equals("")) {
                city.setSelection(Integer.parseInt(LoginActivity.prefs.getString("city", "")));
                Log.i("city",LoginActivity.prefs.getString("city",""));
            }

            if(!LoginActivity.prefs.getString("area","").equals("")) {
                area.setSelection(Integer.parseInt(LoginActivity.prefs.getString("area", "")));
                Log.i("area",LoginActivity.prefs.getString("area",""));
            }

            location[0] = city.getSelectedItem().toString();
            location[1] = area.getSelectedItem().toString();

            actionBar.setTitle(location[1]);

            //TODO set the default values as the existing values which the user had selected earlier

            city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    location[0] = city.getSelectedItem().toString();
                    LoginActivity.prefs.edit().putString("city",String.valueOf(id)).apply();
                    LoginActivity.prefs.edit().putString("city",String.valueOf(id)).commit();

                    LoginActivity.prefs.edit().putString("cityname",location[0]).apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    location[1] = area.getSelectedItem().toString();
                    actionBar.setTitle(location[1]);
                    LoginActivity.prefs.edit().putString("area",String.valueOf(id)).apply();
                    LoginActivity.prefs.edit().putString("area",String.valueOf(id)).commit();

                    Log.i("location[1]",location[1]);
                    LoginActivity.prefs.edit().putString("areaname",location[1]).apply();
                    Log.i("areaname",LoginActivity.prefs.getString("areaname",""));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            return true;
        }*/

        else if (id == R.id.menu_master_location) {

            String[] loc_city = {"Chennai"};
            String[] loc_area = {"Adyar", "Ambattur", "Anna Nagar", "Ashok Nagar", "Avadi", "Chrompet", "Guindy", "K.K Nagar", "Kilpauk", "Kodambakkam", "Koyambedu", "Mylapore", "Nungambakkam", "Pallavaram", "Saidapet", "Tambaram", "T Nagar", "Vadapalani", "Velachery"};

            locationDialog = new Dialog(Master.this);
            locationDialog.setContentView(R.layout.choose_location);
            locationDialog.setCancelable(true);
            locationDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            locationDialog.setTitle("Location");
            locationDialog.show();

            final Spinner city = (Spinner) locationDialog.findViewById(R.id.spinnerLocationCity);
            final Spinner area = (Spinner) locationDialog.findViewById(R.id.spinnerLocationArea);

            final RadioButton selectFromMap = (RadioButton) locationDialog.findViewById(R.id.radio_select_from_map);

            ArrayAdapter<String> adapter_area = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loc_area);
            area.setAdapter(adapter_area);

            ArrayAdapter<String> adapter_city = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, loc_city);
            city.setAdapter(adapter_city);

            for (int i = 0; i < city.getCount(); i++) {
                if (city.getItemAtPosition(i).equals(location[0])) {
                    city.setSelection(i);
                    break;
                }
            }

            for (int i = 0; i < area.getCount(); i++) {
                if (area.getItemAtPosition(i).equals(location[1])) {
                    area.setSelection(i);
                    Log.i("selected", String.valueOf(i));
                    break;
                }
            }

            if (!LoginActivity.prefs.getString("city", "").equals("")) {
                city.setSelection(Integer.parseInt(LoginActivity.prefs.getString("city", "")));
                Log.i("city", LoginActivity.prefs.getString("city", ""));
            }

            if (!LoginActivity.prefs.getString("area", "").equals("")) {
                area.setSelection(Integer.parseInt(LoginActivity.prefs.getString("area", "")));
                Log.i("area", LoginActivity.prefs.getString("area", ""));
            }

            location[0] = city.getSelectedItem().toString();
            location[1] = area.getSelectedItem().toString();

            actionBar.setTitle(location[1]);

            //TODO set the default values as the existing values which the user had selected earlier

            city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    location[0] = city.getSelectedItem().toString();
                    LoginActivity.prefs.edit().putString("city", String.valueOf(id)).apply();
                    LoginActivity.prefs.edit().putString("city", String.valueOf(id)).commit();

                    LoginActivity.prefs.edit().putString("cityname", location[0]).apply();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    location[1] = area.getSelectedItem().toString();
                    actionBar.setTitle(location[1]);
                    LoginActivity.prefs.edit().putString("area", String.valueOf(id)).apply();
                    LoginActivity.prefs.edit().putString("area", String.valueOf(id)).commit();

                    Log.i("location[1]", location[1]);
                    LoginActivity.prefs.edit().putString("areaname", location[1]).apply();
                    Log.i("areaname", LoginActivity.prefs.getString("areaname", ""));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            selectFromMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (selectFromMap.isChecked())
                        locationDialog.dismiss();
                    getLocationFromMap();
                }
            });

            return true;
        } else if (id == R.id.menu_master_home) {
            selectItem(0);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getLocationFromMap() {

        /*LocationFromMap.locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        LocationFromMap.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LocationFromMap.location[0] = location.getLatitude();
                LocationFromMap.location[1] = location.getLongitude();
                Log.i("Lat",String.valueOf(LocationFromMap.location[0]));
                Log.i("Long",String.valueOf(LocationFromMap.location[1]));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
*/
       /* LocationFromMap.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,new LocationListener() {
            @Override
            public void onLocationChanged(Location loc) {
                LocationFromMap.location[0] = loc.getLatitude();
                LocationFromMap.location[1] = loc.getLongitude();
                Log.i("Lat",String.valueOf(location[0]));
                Log.i("Long",String.valueOf(location[1]));
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });

*/
        Intent locationIntent = new Intent(Master.this, LocationFromMap.class);
        startActivity(locationIntent);
    }

    public static class GeneralSettingsFragment extends Fragment {

        CheckBox Notifications, downloadImagesOverWifi;

        public GeneralSettingsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_general_settings, container, false);

            Notifications = (CheckBox) rootView.findViewById(R.id.checkboxNotifications);
            downloadImagesOverWifi = (CheckBox) rootView.findViewById(R.id.checkboxDownloadImagesOverWifi);

            if (LoginActivity.prefs.getString("Notification", "").equals("On")) {
                Notifications.setChecked(true);
            } else
                Notifications.setChecked(false);

            if (Notifications.isChecked())
                Master.Notifications = true;
            else
                Master.Notifications = false;

            if (downloadImagesOverWifi.isChecked())
                Master.downloadImagesOverWifi = true;
            else
                Master.downloadImagesOverWifi = false;

            downloadImagesOverWifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        Master.downloadImagesOverWifi = true;
                    else
                        Master.downloadImagesOverWifi = false;
                }
            });

            Notifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Master.Notifications = true;
                        LoginActivity.prefs.edit().putString("Notification", "On").apply();
                        LoginActivity.prefs.edit().putString("Notification", "On").commit();
                    } else {
                        Master.Notifications = false;
                        LoginActivity.prefs.edit().putString("Notification", "Off").apply();
                        LoginActivity.prefs.edit().putString("Notification", "Off").commit();
                    }
                }
            });

            return rootView;
        }
    }

    public static class HelpFragment extends Fragment {

        ViewPager mViewPager;
        ViewPagerAdapter customAdapter;
        private Button _btn1, _btn2, _btn3;

        public HelpFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_help, container, false);

            mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
            customAdapter = new ViewPagerAdapter(getActivity().getApplicationContext(), getActivity().getSupportFragmentManager());

            mViewPager.setAdapter(customAdapter);
            mViewPager.setCurrentItem(0);

            _btn1 = (Button) rootView.findViewById(R.id.btn1);
            _btn2 = (Button) rootView.findViewById(R.id.btn2);
            _btn3 = (Button) rootView.findViewById(R.id.btn3);

            _btn1.setBackgroundResource(R.drawable.rounded_cell_selected);
            mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    mViewPager.setCurrentItem(position);
                    if (position == 0) {
                        _btn1.setBackgroundResource(R.drawable.rounded_cell_selected);
                        _btn2.setBackgroundResource(R.drawable.rounded_cell);
                        _btn3.setBackgroundResource(R.drawable.rounded_cell);
                    } else if (position == 1) {
                        _btn1.setBackgroundResource(R.drawable.rounded_cell);
                        _btn2.setBackgroundResource(R.drawable.rounded_cell_selected);
                        _btn3.setBackgroundResource(R.drawable.rounded_cell);
                    } else if (position == 2) {
                        _btn1.setBackgroundResource(R.drawable.rounded_cell);
                        _btn2.setBackgroundResource(R.drawable.rounded_cell);
                        _btn3.setBackgroundResource(R.drawable.rounded_cell_selected);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });
            return rootView;
        }

    }

    public static class AboutFragment extends Fragment {

        public AboutFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_about, container, false);

            return rootView;
        }
    }

    public static class NotificationsFragment extends Fragment {

        public NotificationsFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_notifications, container, false);

            Switch toggle_notify = (Switch) rootView.findViewById(R.id.toggle_notify);

            if (LoginActivity.prefs.getString("Notification", "").equals("On"))
                toggle_notify.setChecked(true);

            else
                toggle_notify.setChecked(false);

            Master.Notifications = toggle_notify.isChecked();
            toggle_notify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked == true) {
                        Master.Notifications = true;
                        LoginActivity.prefs.edit().putString("Notification", "On").apply();
                        LoginActivity.prefs.edit().putString("Notification", "On").commit();
                    } else if (isChecked == false) {
                        Master.Notifications = false;
                        LoginActivity.prefs.edit().putString("Notification", "Off").apply();
                        LoginActivity.prefs.edit().putString("Notification", "Off").commit();
                    }
                }
            });
            return rootView;
        }
    }

    public static class OrderHistoryFragment extends Fragment {

        public OrderHistoryFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_order_history, container, false);


            return rootView;
        }
    }

    public static class MyAccountFragment extends Fragment {

        public MyAccountFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);

            return rootView;
        }
    }

    public static class ProductsFragment extends Fragment {

        public static Handler categoryMsgHandler;
        TextView categoryCat, subCategoryCat, productsCat, subCategorySubCat, productsSubCat, productsProduct;
        ArrayList<ItemDetailsClass> listOfItems = new ArrayList<>();
        ArrayList<CategoryCardClass> listOfCateg = new ArrayList<>();
        ArrayList<SubcategoryCardClass> listOfSubCateg = new ArrayList<>();
        private CardAdapter mAdapter1;
        private RecyclerView categoryRecycleView, subcategoryRecycleView, productsRecyclerView;
        private CategoryCardAdapter mAdapter2;
        private SubcategoryCardAdapter mAdapter3;
        private SwipeRefreshLayout swipeRefreshLayoutProducts;

        public ProductsFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView1 = inflater.inflate(R.layout.fragment_category, container, false);

            for (int i = 0; i < numCategories; i++) {
                listOfCateg.add(i, new CategoryCardClass(categoryName.get(i), 0));  //TODO change this to image URL received from db
            }

            swipeRefreshLayoutProducts = (SwipeRefreshLayout) rootView1.findViewById(R.id.swipeToRefresh_Products);

            swipeRefreshLayoutProducts.setColorSchemeColors(R.color.primary,R.color.darkGreen);

            swipeRefreshLayoutProducts.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    refreshItems();
                }
            });

            categoryRecycleView = (RecyclerView) rootView1.findViewById(R.id.my_recycler_view2);
            categoryRecycleView.setHasFixedSize(true);
            categoryRecycleView.setLayoutManager(new GridLayoutManager(rootView1.getContext(), 3));
            categoryRecycleView.setItemAnimator(new DefaultItemAnimator());

            mAdapter2 = new CategoryCardAdapter(listOfCateg, rootView1.getContext());
            categoryRecycleView.setAdapter(mAdapter2);

            subcategoryRecycleView = (RecyclerView) rootView1.findViewById(R.id.my_recycler_view3);
            subcategoryRecycleView.setHasFixedSize(true);
            subcategoryRecycleView.setLayoutManager(new GridLayoutManager(rootView1.getContext(), 3));
            subcategoryRecycleView.setItemAnimator(new DefaultItemAnimator());

            productsRecyclerView = (RecyclerView) rootView1.findViewById(R.id.my_recycler_view);
            productsRecyclerView.setHasFixedSize(true);
            productsRecyclerView.setLayoutManager(new GridLayoutManager(rootView1.getContext(), 2));
            productsRecyclerView.setItemAnimator(new DefaultItemAnimator());

            mAdapter1 = new CardAdapter(listOfItems, rootView1.getContext());
            productsRecyclerView.setAdapter(mAdapter1);

            categoryMsgHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.arg1 == 1) {

                        Log.i("Inside handler", "True");

                        if(listOfSubCateg.isEmpty()) {
                            for (int i = 0; i < numSubCategories[msg.arg2]; i++) {
                                listOfSubCateg.add(i, new SubcategoryCardClass(subcategoryName.get(msg.arg2)[i], 0));
                                //TODO change this to image URL received from db
                            }
                        }

                        mAdapter3 = new SubcategoryCardAdapter(listOfSubCateg, rootView1.getContext());
                        subcategoryRecycleView.setAdapter(mAdapter3);

                        rootView1.findViewById(R.id.category).setVisibility(View.INVISIBLE);
                        rootView1.findViewById(R.id.subcategory).setVisibility(View.VISIBLE);
                        rootView1.findViewById(R.id.products).setVisibility(View.INVISIBLE);
                    }
                }
            };

            rootView1.findViewById(R.id.category).setVisibility(View.VISIBLE);
            rootView1.findViewById(R.id.subcategory).setVisibility(View.INVISIBLE);
            rootView1.findViewById(R.id.products).setVisibility(View.INVISIBLE);


            categoryCat = (TextView) rootView1.findViewById(R.id.category_cat);
            subCategoryCat = (TextView) rootView1.findViewById(R.id.subcategory_cat);
            productsCat = (TextView) rootView1.findViewById(R.id.products_cat);

            subCategorySubCat = (TextView) rootView1.findViewById(R.id.subcategory_subcat);
            productsSubCat = (TextView) rootView1.findViewById(R.id.products_subcat);

            productsProduct = (TextView) rootView1.findViewById(R.id.products_product);


            subCategoryCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView1.findViewById(R.id.category).setVisibility(View.VISIBLE);
                    rootView1.findViewById(R.id.subcategory).setVisibility(View.INVISIBLE);
                    rootView1.findViewById(R.id.products).setVisibility(View.INVISIBLE);
                }
            });

            productsCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView1.findViewById(R.id.category).setVisibility(View.VISIBLE);
                    rootView1.findViewById(R.id.subcategory).setVisibility(View.INVISIBLE);
                    rootView1.findViewById(R.id.products).setVisibility(View.INVISIBLE);
                }
            });

            productsSubCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rootView1.findViewById(R.id.category).setVisibility(View.INVISIBLE);
                    rootView1.findViewById(R.id.subcategory).setVisibility(View.VISIBLE);
                    rootView1.findViewById(R.id.products).setVisibility(View.INVISIBLE);
                }
            });

            return rootView1;
        }

        private void refreshItems(){

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity().getApplicationContext(),"Refreshing",Toast.LENGTH_SHORT).show();
                    swipeRefreshLayoutProducts.setRefreshing(false);
                }
            });
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            System.out.println(position);
            selectItem(position);
        }
    }

    public class LoadProductsList extends AsyncTask<Void,Void,Void> {

        ProgressDialog p1 = new ProgressDialog(Master.this);

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

            p1.setTitle("Loading Products List...");
            p1.setCancelable(false);
            p1.show();

        }

        @Override
        protected Void doInBackground(Void... arg){

            ServiceHandler jsonParser = new ServiceHandler();
            productsJSON=jsonParser.makeServiceCall(categoriesURL,ServiceHandler.POST,null);
            if(productsJSON!=null){
                try{
                    JSONObject productsListJSON = new JSONObject(productsJSON);
                    if(productsListJSON.getString("success").equals("true")) {
                        numCategories = Integer.parseInt(productsListJSON.getString("numCategories"));
                        JSONArray list = new JSONArray(String.valueOf(productsListJSON.getJSONArray("list")));

                        categoryID = new int[numCategories];
                        numSubCategories = new int[numCategories];
                        subcategoryID = new ArrayList<>();
                        subcategoryName = new ArrayList<>();

                        Log.i("list",String.valueOf(list));

                        for(int i=0;i<numCategories;i++){
                            JSONObject catObj = list.getJSONObject(i);
                            JSONArray sub = catObj.getJSONArray("subcategories");
                            categoryID[i] = catObj.getInt("ID");
                            categoryName.add(i, catObj.getString("name"));
                            numSubCategories[i] = catObj.getInt("numSubcategories");
                            int[] subIds = new int[numSubCategories[i]];
                            String[] subNames = new String[numSubCategories[i]];
                            for(int j=0;j<numSubCategories[i];j++) {
                                subIds[j] = sub.getJSONObject(j).getInt("subID");
                                subNames[j] = sub.getJSONObject(j).getString("name");
                            }
                            subcategoryID.add(i,subIds);
                            subcategoryName.add(i,subNames);
                            Log.i("subId", String.valueOf(subcategoryID.size()));
                            Log.i("subCat", String.valueOf(subcategoryName.size()));

                        }
                        Log.i("Loaded","Fully done");
                    }


                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            if(p1!=null && p1.isShowing())
            {
                p1.hide();
                p1.dismiss();
            }
        }

    }



}

