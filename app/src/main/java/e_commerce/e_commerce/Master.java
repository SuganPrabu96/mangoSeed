package e_commerce.e_commerce;

/**
 * Created by Suganprabu on 17-04-2015.
 */


//TODO in cart_card.xml I have changed the marginLeft of the removeButton for testing purposes
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.widget.ProfilePictureView;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import Cart.CartItemsClass;
import Cart.CartRecyclerViewAdapter;
import HelpViewPager.ViewPagerAdapter;
import ItemDisplay.CategoryCardAdapter;
import ItemDisplay.CategoryCardClass;
import ItemDisplay.ItemDetailsClass;
import ItemDisplay.ItemsCardAdapter;
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
    public static int numCategories, numSubCategories[], categoryID[], productsID[], numProducts,
                  newProductsID[], newProductsCatId[], newProductsSubCatId[];
    public static ArrayList<String> categoryName, productsName, productDesc;
    public static ArrayList<int[]> subcategoryID;
    public static ArrayList<String[]> subcategoryName;
    private final String categoriesURL = "http://grokart.ueuo.com/listCategories.php";
    private static final String updateDetailsURL = "http://grokart.ueuo.com/editInfo.php";
    private final String locationURL = "http://grokart.ueuo.com/latlong.php";
    private static final String itemsURL = "http://grokart.ueuo.com/catProds.php";
    private static final String itemsImagesURL = "http://grokart.ueuo.com/prodImage.php";
    private static final String logoutURL = "http://grokart.ueuo.com/logout.php";
    private static final String newItemsURL = "http://grokart.ueuo.com/latest.php";
    public FragmentTransaction fragmentTransaction;
    public static Dialog locationDialog;
    public String[] location = {"Chennai", "Adyar"}; // location[0] is city and location[1] is area
    ActionBar actionBar;
    private CharSequence mTitle = "E - Commerce"; //TODO change this to the name of the startup
    private DrawerLayout drawerLayout;
    private ListView drawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String productsJSON;
    private String locationReturnedJSON;
    private static String itemsReturnedJSON, itemsURLReturnedJSON, logoutReturnedJSON, newItemsReturnedJSON;
    private static String updateDetailsReturnedJSON;
    public static ProgressDialog updateProgress, locationProgress, loadItemsProgress, logoutProgress, loadCatSubCatProgress,
            loadNewItemsProgress;
    public static Handler locationHandler, logoutHandler, loadItems, loadItemImages, newItemsHandler;
    public static boolean logoutSuccess = false;
    public static InputMethodManager inputMethodManager;
    public static RecyclerView cartItemRecyclerView;
    public static CartRecyclerViewAdapter cAdapter;
    public static ArrayList<CartItemsClass> cartitems = new ArrayList<>();
    String[] loc_city = {"Chennai"};
    String[] loc_area = {"Adyar", "Ambattur", "Anna Nagar"};
    String[] loc_lat = {"13.0063","13.0983","13.0846"};
    String[] loc_long = {"80.2574","80.1622","80.2179"};
    private static int count = 0;

    // TODO change the initial value of location based on Shared prefs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        modeOfLogin = getIntent().getExtras().getString("loginMethod").toString();

        setContentView(R.layout.nav_bar);

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        categoryName = new ArrayList();
        productsName = new ArrayList();

        cAdapter = new CartRecyclerViewAdapter(cartitems, getApplicationContext());

        updateProgress = new ProgressDialog(Master.this);
        locationProgress = new ProgressDialog(Master.this);
        loadItemsProgress = new ProgressDialog(Master.this);
        logoutProgress = new ProgressDialog(Master.this);
        loadCatSubCatProgress = new ProgressDialog(Master.this);
        loadNewItemsProgress = new ProgressDialog(Master.this);

        facebookProfileIcon = (ProfilePictureView) findViewById(R.id.profilepic_facebook);
        profileIconText = (TextView) findViewById(R.id.profilepic_name);
        facebookProfileIcon.setCropped(true);
        profileIcon = (ImageView) findViewById(R.id.profilepic);
        googleProfileIcon = (CircleImageView) findViewById(R.id.profilepic_google);

        if (modeOfLogin.equals("Facebook")) {
            profileIcon.setVisibility(View.INVISIBLE);
            googleProfileIcon.setVisibility((View.INVISIBLE));
            facebookProfileIcon.setVisibility(View.VISIBLE);
        } else if (modeOfLogin.equals("Google")) {
            googleProfileIcon.setImageBitmap(LoginActivity.bmImage);
            profileIconText.setText(LoginActivity.profileText);
            profileIcon.setVisibility(View.INVISIBLE);
            facebookProfileIcon.setVisibility(View.INVISIBLE);
            googleProfileIcon.setVisibility(View.VISIBLE);

        } else {
            facebookProfileIcon.setVisibility(View.INVISIBLE);
            googleProfileIcon.setVisibility(View.INVISIBLE);
            profileIcon.setVisibility(View.VISIBLE);
            profileIconText.setText(LoginActivity.prefs.getString("Email", ""));
        }

        actionBar = getSupportActionBar();
        actionBar.setTitle(LoginActivity.prefs.getString("areaname",""));

        if(!LoginActivity.prefs.getString("LoginStatus","").equals("Already Logged in"))
            getLocationForItems();

        Window window = Master.this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.setStatusBarColor(Master.this.getResources().getColor(R.color.myStatusBarColor));
        }

        SupportFragmentManager = getSupportFragmentManager();

        List<NavDrawerItem> datalist = data.getNavDrawerItems();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.add(R.id.frame_container, new ProductsFragment());
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


        Master.locationHandler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.arg1 == 1) {
                    if (msg.arg2 == 1) {
                        new LocationDetails().execute(String.valueOf(LocationFromMap.location[0]), String.valueOf(LocationFromMap.location[1]), LoginActivity.session);
                    }
                }
            }
        };

    }

    private void getLocationForItems(){
        {  locationDialog = new Dialog(Master.this);
            locationDialog.setContentView(R.layout.choose_location);
            locationDialog.setCancelable(false);
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

            city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean check = false;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(check) {
                        location[0] = city.getSelectedItem().toString();
                        LoginActivity.prefs.edit().putString("city", String.valueOf(id)).apply();
                        LoginActivity.prefs.edit().putString("city", String.valueOf(id)).commit();

                        LoginActivity.prefs.edit().putString("cityname", location[0]).apply();
                    }
                    check=true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean check = false;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(check) {
                        location[1] = area.getSelectedItem().toString();
                        actionBar.setTitle(location[1]);
                        LoginActivity.prefs.edit().putString("area", String.valueOf(id)).apply();
                        LoginActivity.prefs.edit().putString("area", String.valueOf(id)).commit();

                        Log.i("location[1]", location[1]);
                        LoginActivity.prefs.edit().putString("areaname", location[1]).apply();
                        Log.i("areaname", LoginActivity.prefs.getString("areaname", ""));

                        locationDialog.hide();
                        locationDialog.dismiss();

                        new LocationDetails().execute(loc_lat[position], loc_long[position], LoginActivity.session);
                    }
                    check=true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            selectFromMap.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (selectFromMap.isChecked()) {
                        locationDialog.dismiss();
                        locationDialog.hide();
                    }
                    getLocationFromMap();
                }
            });

            Log.i("areaname", LoginActivity.prefs.getString("areaname", ""));
            if (!LoginActivity.prefs.getString("areaname", "").equals(""))
                actionBar.setTitle(LoginActivity.prefs.getString("areaname", ""));
            else
                actionBar.setTitle(location[1]);


        }

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
        } else if(position == 5){
            fragmentTransaction.replace(R.id.frame_container,new OffersFragment());
        } else if(position == 6){
            fragmentTransaction.replace(R.id.frame_container, new MyWalletFragment());
        } else if (position == 7) {
            fragmentTransaction.replace(R.id.frame_container, new AboutFragment());
        } else if (position == 8) {
            fragmentTransaction.replace(R.id.frame_container, new HelpFragment());
        } else if (position == 9) {
            final AlertDialog.Builder logoutAlert = new AlertDialog.Builder(Master.this);

            logoutAlert.setCancelable(false);

            logoutAlert.setMessage("Are you sure");
            logoutAlert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(final DialogInterface dialog, int which) {

                    new Logout().execute(LoginActivity.session);

                    logoutHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.arg1 == 1) {
                                if(msg.arg2 ==1)

                                    if (Master.logoutSuccess) {
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
                            }
                        }
                    };
                };

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

            } else if(f instanceof MyWalletFragment) {
                fragmentTransaction.replace(R.id.frame_container, new MyWalletFragment());

            } else if(f instanceof OffersFragment){
                fragmentTransaction.replace(R.id.frame_container, new OffersFragment());

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
        if (position != 9) {
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
            String[] loc_area = {"Adyar", "Ambattur", "Anna Nagar"};

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

            city.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean check = false;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(check) {
                        location[0] = city.getSelectedItem().toString();
                        LoginActivity.prefs.edit().putString("city", String.valueOf(id)).apply();
                        LoginActivity.prefs.edit().putString("city", String.valueOf(id)).commit();

                        LoginActivity.prefs.edit().putString("cityname", location[0]).apply();
                    }
                    check=true;
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                boolean check = false;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(check) {
                        location[1] = area.getSelectedItem().toString();
                        actionBar.setTitle(location[1]);
                        LoginActivity.prefs.edit().putString("area", String.valueOf(id)).apply();
                        LoginActivity.prefs.edit().putString("area", String.valueOf(id)).commit();

                        Log.i("location[1]", location[1]);
                        LoginActivity.prefs.edit().putString("areaname", location[1]).apply();
                        Log.i("areaname", LoginActivity.prefs.getString("areaname", ""));
                    }
                    check=true;
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

            cartItemRecyclerView = (RecyclerView) rootView.findViewById(R.id.cart_items_recyclerview);
            cartItemRecyclerView.setHasFixedSize(false);
            cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            cartItemRecyclerView.setItemAnimator(new DefaultItemAnimator());

            cartItemRecyclerView.setAdapter(cAdapter);
            return rootView;
        }
    }

    public static class OffersFragment extends Fragment{

        public OffersFragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_offers, container, false);

            return rootView;

        }
    }

    public static class MyWalletFragment extends Fragment{

        public MyWalletFragment(){

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {


            View rootView = inflater.inflate(R.layout.fragment_my_wallet, container, false);

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

            cartItemRecyclerView = (RecyclerView) rootView.findViewById(R.id.cart_items_recyclerview);
            cartItemRecyclerView.setHasFixedSize(false);
            cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            cartItemRecyclerView.setItemAnimator(new DefaultItemAnimator());

            cartItemRecyclerView.setAdapter(cAdapter);

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

            cartItemRecyclerView = (RecyclerView) rootView.findViewById(R.id.cart_items_recyclerview);
            cartItemRecyclerView.setHasFixedSize(false);
            cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            cartItemRecyclerView.setItemAnimator(new DefaultItemAnimator());

            cartItemRecyclerView.setAdapter(cAdapter);
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


            cartItemRecyclerView = (RecyclerView) rootView.findViewById(R.id.cart_items_recyclerview);
            cartItemRecyclerView.setHasFixedSize(false);
            cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            cartItemRecyclerView.setItemAnimator(new DefaultItemAnimator());

            cartItemRecyclerView.setAdapter(cAdapter);

            return rootView;
        }
    }

    public static class MyAccountFragment extends Fragment {

        public MyAccountFragment() {
        }

        private EditText editNewName, editNewEmail, editNewAddress, editNewPhone, editNewPassword;
        private TextView name, email, address, phone, password;
        Button submit;
        public static Handler msgHandler;
        private Dialog confirmChangesDialog;
        private boolean confirmChangesAuth;
        private Handler confirmChangesMsgHandler;
        private LinearLayout myAccountLayout;


        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView = inflater.inflate(R.layout.fragment_my_account, container, false);

            myAccountLayout = (LinearLayout) rootView.findViewById(R.id.my_account_layout);

            submit = (Button) rootView.findViewById(R.id.accountButtonSubmit);

            editNewName = (EditText) rootView.findViewById(R.id.accountEditTextName);
            name = (TextView) rootView.findViewById(R.id.accountTextViewName);

            editNewEmail = (EditText) rootView.findViewById(R.id.accountEditEmail);
            email = (TextView) rootView.findViewById(R.id.accountTextViewEmail);

            editNewPhone = (EditText) rootView.findViewById(R.id.accountEditTextPhone);
            phone = (TextView) rootView.findViewById(R.id.accountTextViewPhone);

            editNewAddress = (EditText) rootView.findViewById(R.id.accountEditTextAddress);
            address = (TextView) rootView.findViewById(R.id.accountTextViewAddress);

            editNewPassword = (EditText) rootView.findViewById(R.id.accountEditTextPassword);
            password = (TextView) rootView.findViewById(R.id.accountTextViewPassword);

            editNewName.setVisibility(View.INVISIBLE);
            name.setVisibility(View.VISIBLE);

            editNewEmail.setVisibility(View.INVISIBLE);
            email.setVisibility(View.VISIBLE);

            editNewPhone.setVisibility(View.INVISIBLE);
            phone.setVisibility(View.VISIBLE);

            editNewAddress.setVisibility(View.INVISIBLE);
            address.setVisibility(View.VISIBLE);

            editNewPassword.setVisibility(View.INVISIBLE);
            password.setVisibility(View.VISIBLE);

            submit.setVisibility(View.INVISIBLE);

            name.setText(LoginActivity.prefs.getString("Name", ""));
            email.setText(LoginActivity.prefs.getString("Email", ""));
            phone.setText(LoginActivity.prefs.getString("Phone", ""));
            address.setText(LoginActivity.prefs.getString("Address", ""));
            password.setText(LoginActivity.prefs.getString("Password", ""));

            editNewName.setHint(name.getText());
            editNewEmail.setHint(email.getText());
            editNewPhone.setHint(phone.getText());
            editNewAddress.setHint(address.getText());
            editNewPassword.setHint(password.getText());

            myAccountLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(inputMethodManager.isAcceptingText()){
                        if(editNewName.isEnabled()) {
                            inputMethodManager.hideSoftInputFromWindow(editNewName.getWindowToken(), 0);
                            editNewName.setVisibility(View.INVISIBLE);
                            name.setVisibility(View.VISIBLE);
                        }
                        if(editNewEmail.isEnabled()) {
                            inputMethodManager.hideSoftInputFromWindow(editNewEmail.getWindowToken(), 0);
                            editNewEmail.setVisibility(View.INVISIBLE);
                            email.setVisibility(View.VISIBLE);
                        }
                        if(editNewPassword.isEnabled()){
                            editNewPassword.setVisibility(View.INVISIBLE);
                            password.setVisibility(View.VISIBLE);
                            inputMethodManager.hideSoftInputFromWindow(editNewPassword.getWindowToken(),0);}

                        if(editNewPhone.isEnabled()) {
                            editNewPhone.setVisibility(View.INVISIBLE);
                            phone.setVisibility(View.VISIBLE);
                            inputMethodManager.hideSoftInputFromWindow(editNewPhone.getWindowToken(), 0);
                        }
                        if(editNewAddress.isEnabled()) {
                            editNewAddress.setVisibility(View.INVISIBLE);
                            address.setVisibility(View.VISIBLE);
                            inputMethodManager.hideSoftInputFromWindow(editNewAddress.getWindowToken(), 0);
                        }
                    }

                }
            });

            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    address.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    name.setVisibility(View.INVISIBLE);
                    editNewAddress.setVisibility(View.INVISIBLE);
                    editNewPassword.setVisibility(View.INVISIBLE);
                    editNewPhone.setVisibility(View.INVISIBLE);
                    editNewEmail.setVisibility(View.INVISIBLE);
                    editNewName.setVisibility(View.VISIBLE);

                    inputMethodManager.showSoftInput(editNewName,InputMethodManager.SHOW_FORCED);

                }

            });

            email.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    address.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    email.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.VISIBLE);
                    editNewAddress.setVisibility(View.INVISIBLE);
                    editNewPassword.setVisibility(View.INVISIBLE);
                    editNewPhone.setVisibility(View.INVISIBLE);
                    editNewEmail.setVisibility(View.VISIBLE);
                    editNewName.setVisibility(View.INVISIBLE);

                    inputMethodManager.showSoftInput(editNewEmail,InputMethodManager.SHOW_FORCED);
                }
            });

            phone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    address.setVisibility(View.VISIBLE);
                    password.setVisibility(View.VISIBLE);
                    phone.setVisibility(View.INVISIBLE);
                    email.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    editNewAddress.setVisibility(View.INVISIBLE);
                    editNewPassword.setVisibility(View.INVISIBLE);
                    editNewPhone.setVisibility(View.VISIBLE);
                    editNewEmail.setVisibility(View.INVISIBLE);
                    editNewName.setVisibility(View.INVISIBLE);

                    inputMethodManager.showSoftInput(editNewPhone,InputMethodManager.SHOW_FORCED);
                }
            });

            address.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    address.setVisibility(View.VISIBLE);
                    password.setVisibility(View.INVISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    editNewAddress.setVisibility(View.VISIBLE);
                    editNewPassword.setVisibility(View.INVISIBLE);
                    editNewPhone.setVisibility(View.INVISIBLE);
                    editNewEmail.setVisibility(View.INVISIBLE);
                    editNewName.setVisibility(View.INVISIBLE);

                    inputMethodManager.showSoftInput(editNewAddress,InputMethodManager.SHOW_FORCED);
                }
            });

            password.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    address.setVisibility(View.VISIBLE);
                    password.setVisibility(View.INVISIBLE);
                    phone.setVisibility(View.VISIBLE);
                    email.setVisibility(View.VISIBLE);
                    name.setVisibility(View.VISIBLE);
                    editNewAddress.setVisibility(View.INVISIBLE);
                    editNewPassword.setVisibility(View.VISIBLE);
                    editNewPhone.setVisibility(View.INVISIBLE);
                    editNewEmail.setVisibility(View.INVISIBLE);
                    editNewName.setVisibility(View.INVISIBLE);

                    inputMethodManager.showSoftInput(editNewPassword,InputMethodManager.SHOW_FORCED);
                }

            });

            editNewName.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    editNewName.setOnKeyListener(new View.OnKeyListener() {
                            @Override
                            public boolean onKey(View v, int keyCode, KeyEvent event) {
                                if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)
                                    inputMethodManager.hideSoftInputFromWindow(editNewName.getWindowToken(), 0);
                                return false;
                            }
                     });
                    editNewName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            Log.i("Inside onEditorAction","True");
                            Log.i("actionId", String.valueOf(actionId));

                            int result = actionId & EditorInfo.IME_MASK_ACTION;
                            if(result==EditorInfo.IME_ACTION_DONE)
                                inputMethodManager.hideSoftInputFromWindow(editNewName.getWindowToken(),0);
                            return false;
                        }
                    });
                    if (name.getText() != s.toString()) {
                        name.setText(s.toString());
                        editNewName.setHint(s.toString());
                        submit.setVisibility(View.VISIBLE);
                    } else if (LoginActivity.prefs.getString("Name", "").equals(name.getText()) &&
                            LoginActivity.prefs.getString("Email", "").equals(email.getText()) &&
                            LoginActivity.prefs.getString("Address", "").equals(address.getText()) &&
                            LoginActivity.prefs.getString("Phone", "").equals(phone.getText()) &&
                            LoginActivity.prefs.getString("Password", "").equals(password.getText()))
                        submit.setVisibility(View.INVISIBLE);

                }
            });

            editNewEmail.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    editNewEmail.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)
                                inputMethodManager.hideSoftInputFromWindow(editNewEmail.getWindowToken(), 0);
                            return false;
                        }
                    });
                    editNewEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            Log.i("Inside onEditorAction","True");
                            Log.i("actionId", String.valueOf(actionId));

                            int result = actionId & EditorInfo.IME_MASK_ACTION;
                            if(result==EditorInfo.IME_ACTION_DONE)
                                inputMethodManager.hideSoftInputFromWindow(editNewEmail.getWindowToken(),0);
                            return false;
                        }
                    });
                    if (email.getText() != s.toString()) {
                        email.setText(s.toString());
                        editNewEmail.setHint(s.toString());
                        submit.setVisibility(View.VISIBLE);
                    } else if (LoginActivity.prefs.getString("Name", "").equals(name.getText()) &&
                            LoginActivity.prefs.getString("Email", "").equals(email.getText()) &&
                            LoginActivity.prefs.getString("Address", "").equals(address.getText()) &&
                            LoginActivity.prefs.getString("Phone", "").equals(phone.getText()) &&
                            LoginActivity.prefs.getString("Password", "").equals(password.getText()))
                        submit.setVisibility(View.INVISIBLE);
                }
            });

            editNewPhone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    editNewPhone.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)
                                inputMethodManager.hideSoftInputFromWindow(editNewPhone.getWindowToken(), 0);
                            return false;
                        }
                    });
                    editNewPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            Log.i("Inside onEditorAction","True");
                            Log.i("actionId", String.valueOf(actionId));

                            int result = actionId & EditorInfo.IME_MASK_ACTION;
                            if(result==EditorInfo.IME_ACTION_DONE)
                                inputMethodManager.hideSoftInputFromWindow(editNewPhone.getWindowToken(),0);
                            return false;
                        }
                    });
                    if (phone.getText() != s.toString()) {
                        phone.setText(s.toString());
                        editNewPhone.setHint(s.toString());
                        submit.setVisibility(View.VISIBLE);
                    } else if (LoginActivity.prefs.getString("Name", "").equals(name.getText()) &&
                            LoginActivity.prefs.getString("Email", "").equals(email.getText()) &&
                            LoginActivity.prefs.getString("Address", "").equals(address.getText()) &&
                            LoginActivity.prefs.getString("Phone", "").equals(phone.getText()) &&
                            LoginActivity.prefs.getString("Password", "").equals(password.getText()))
                        submit.setVisibility(View.INVISIBLE);
                }
            });

            editNewAddress.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    editNewAddress.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)
                                inputMethodManager.hideSoftInputFromWindow(editNewAddress.getWindowToken(), 0);
                            return false;
                        }
                    });
                    editNewAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            Log.i("Inside onEditorAction","True");
                            Log.i("actionId", String.valueOf(actionId));

                            int result = actionId & EditorInfo.IME_MASK_ACTION;
                            if(result==EditorInfo.IME_ACTION_DONE)
                                inputMethodManager.hideSoftInputFromWindow(editNewAddress.getWindowToken(),0);
                            return false;
                        }
                    });
                    if (address.getText() != s.toString()) {
                        address.setText(s.toString());
                        editNewAddress.setText(s.toString());
                        submit.setVisibility(View.VISIBLE);
                    } else if (LoginActivity.prefs.getString("Name", "").equals(name.getText()) &&
                            LoginActivity.prefs.getString("Email", "").equals(email.getText()) &&
                            LoginActivity.prefs.getString("Address", "").equals(address.getText()) &&
                            LoginActivity.prefs.getString("Phone", "").equals(phone.getText()) &&
                            LoginActivity.prefs.getString("Password", "").equals(password.getText()))
                        submit.setVisibility(View.INVISIBLE);
                }
            });

            editNewPassword.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    editNewPassword.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            if(event.getKeyCode()==KeyEvent.KEYCODE_ENTER)
                                inputMethodManager.hideSoftInputFromWindow(editNewPassword.getWindowToken(), 0);
                            return false;
                        }
                    });
                    editNewPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                        @Override
                        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                            Log.i("Inside onEditorAction","True");
                            Log.i("actionId", String.valueOf(actionId));

                            int result = actionId & EditorInfo.IME_MASK_ACTION;
                            if(result==EditorInfo.IME_ACTION_DONE)
                                inputMethodManager.hideSoftInputFromWindow(editNewPassword.getWindowToken(),0);
                            return false;
                        }
                    });
                    if (password.getText() != s.toString()) {
                        password.setText(s.toString());
                        editNewPassword.setHint(s.toString());
                        submit.setVisibility(View.VISIBLE);
                    } else if (LoginActivity.prefs.getString("Name", "").equals(name.getText()) &&
                            LoginActivity.prefs.getString("Email", "").equals(email.getText()) &&
                            LoginActivity.prefs.getString("Address", "").equals(address.getText()) &&
                            LoginActivity.prefs.getString("Phone", "").equals(phone.getText()) &&
                            LoginActivity.prefs.getString("Password", "").equals(password.getText()))
                        submit.setVisibility(View.INVISIBLE);
                }
            });

            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    confirmChangesDialog = new Dialog(rootView.getContext());
                    confirmChangesDialog();

                    confirmChangesMsgHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.arg1 == 1) {
                                if (msg.arg2 == 0)
                                    confirmChangesAuth = false;
                                else if (msg.arg2 == 1)
                                    confirmChangesAuth = true;
                            }
                        }
                    };

                    if (confirmChangesAuth == true) {

                        if (LoginActivity.prefs.getString("Password", "").equals(editNewPassword.getText().toString()))
                            new ChangeDetailsTask().execute(name.getText().toString(), email.getText().toString(),
                                    password.getText().toString(), address.getText().toString(), phone.getText().toString());
                        else
                            new ChangeDetailsTask().execute(name.getText().toString(), email.getText().toString(), ""
                                    , address.getText().toString(), phone.getText().toString());
                    } else
                        Toast.makeText(rootView.getContext(), "Wrong password", Toast.LENGTH_SHORT).show();
                }
            });

            editNewName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editNewName.setVisibility(View.INVISIBLE);
                    name.setVisibility(View.VISIBLE);
                }
            });

            editNewEmail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editNewEmail.setVisibility(View.INVISIBLE);
                    email.setVisibility(View.VISIBLE);
                }
            });

            editNewAddress.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editNewAddress.setVisibility(View.INVISIBLE);
                    address.setVisibility(View.VISIBLE);
                }
            });

            editNewPhone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editNewPhone.setVisibility(View.INVISIBLE);
                    phone.setVisibility(View.VISIBLE);
                }
            });

            editNewPassword.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editNewPassword.setVisibility(View.INVISIBLE);
                    password.setVisibility(View.VISIBLE);
                }
            });

            msgHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.arg1 == 0)
                        Toast.makeText(rootView.getContext(), (CharSequence) msg.obj, Toast.LENGTH_SHORT).show();
                    if (msg.obj.equals("Updated details successfully")) {
                        editNewName.setVisibility(View.INVISIBLE);
                        editNewEmail.setVisibility(View.INVISIBLE);
                        editNewAddress.setVisibility(View.INVISIBLE);
                        editNewPhone.setVisibility(View.INVISIBLE);
                        editNewPassword.setVisibility(View.INVISIBLE);

                        name.setVisibility(View.VISIBLE);
                        email.setVisibility(View.VISIBLE);
                        address.setVisibility(View.VISIBLE);
                        phone.setVisibility(View.VISIBLE);
                        password.setVisibility(View.VISIBLE);
                    }
                }
            };

            cartItemRecyclerView = (RecyclerView) rootView.findViewById(R.id.cart_items_recyclerview);
            cartItemRecyclerView.setHasFixedSize(false);
            cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));
            cartItemRecyclerView.setItemAnimator(new DefaultItemAnimator());

            cartItemRecyclerView.setAdapter(cAdapter);

            return rootView;
        }

        private void confirmChangesDialog() {
            final EditText confirmPassword;
            final Button confirmChangesSubmit;

            confirmChangesDialog.setContentView(R.layout.confirm_changes);
            confirmChangesDialog.setTitle("Enter password to confirm changes");
            confirmChangesDialog.setCancelable(true);
            confirmChangesDialog.show();

            confirmPassword = (EditText) confirmChangesDialog.findViewById(R.id.confirmChangesPassword);
            confirmChangesSubmit = (Button) confirmChangesDialog.findViewById(R.id.confirmChangesSubmit);

            confirmChangesSubmit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg = new Message();
                    msg.arg1 = 1;
                    if (confirmPassword.getText().toString().equals(LoginActivity.prefs.getString("Password", ""))) {
                        msg.arg2 = 1;
                    } else
                        msg.arg2 = 0;
                    confirmChangesMsgHandler.sendMessage(msg);
                }
            });

        }
    }

    public static class ProductsFragment extends Fragment {

        public static Handler categoryMsgHandler, subcategoryMsgHandler, productsMsgHandler;
        TextView categoryCat, subCategoryCat, productsCat, subCategorySubCat, productsSubCat, productsProduct;
        ArrayList<ItemDetailsClass> listOfItems;
        ArrayList<CategoryCardClass> listOfCateg = new ArrayList<>();
        ArrayList<SubcategoryCardClass> listOfSubCateg;
        private ItemsCardAdapter mAdapter1;
        private RecyclerView categoryRecycleView, subcategoryRecycleView, productsRecyclerView;
        private CategoryCardAdapter mAdapter2;
        private SubcategoryCardAdapter mAdapter3;
        private static SwipeRefreshLayout swipeRefreshLayoutProducts;
        private int catID,subcatID;
        private int[] categoryImageURL = {R.drawable.personalcare, R.drawable.brandedfoods, R.drawable.groceries};
        private static FragmentManager fragManag;

        public ProductsFragment() {
        }

        @Override
        public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            final View rootView1 = inflater.inflate(R.layout.fragment_category, container, false);

            fragManag = getFragmentManager();

            /*if (savedInstanceState == null) {
                fragManag
                        .beginTransaction()
                        .add(R.id.item_container, new CardFrontFragment())
                        .commit();
            }*/

            if(MainActivity.internetConnection.isConnectingToInternet())
                if (numCategories > 0)
                    for (int i = 0; i < numCategories; i++) {
                        listOfCateg.add(i, new CategoryCardClass(categoryName.get(i), categoryImageURL[i]));
                    }

                else{
                    listOfCateg.clear();
                }

            swipeRefreshLayoutProducts = (SwipeRefreshLayout) rootView1.findViewById(R.id.swipeToRefresh_Products);

            swipeRefreshLayoutProducts.setColorSchemeColors(R.color.primary, R.color.darkGreen);

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
            productsRecyclerView.setLayoutManager(new GridLayoutManager(rootView1.getContext(), 3));
            productsRecyclerView.setItemAnimator(new DefaultItemAnimator());

            categoryMsgHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.arg1 == 1) {

                        Log.i("Arg2", String.valueOf(msg.arg2));
                        catID = msg.arg2;

                        listOfSubCateg = new ArrayList<>();

                        for (int i = 0; i < numSubCategories[msg.arg2-1]; i++) {
                            listOfSubCateg.add(i, new SubcategoryCardClass(subcategoryName.get(msg.arg2-1)[i], 0));
                            //TODO change this to image URL received from db
                        }

                        mAdapter3 = new SubcategoryCardAdapter(listOfSubCateg, rootView1.getContext());
                        subcategoryRecycleView.setAdapter(mAdapter3);

                        rootView1.findViewById(R.id.category).setVisibility(View.INVISIBLE);
                        rootView1.findViewById(R.id.subcategory).setVisibility(View.VISIBLE);
                        rootView1.findViewById(R.id.products).setVisibility(View.INVISIBLE);
                    }
                }
            };

            subcategoryMsgHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.arg1 == 2) {

                        Log.i("Arg2", String.valueOf(msg.arg2));
                        subcatID = msg.arg2;

                        mAdapter3 = new SubcategoryCardAdapter(listOfSubCateg, rootView1.getContext());
                        subcategoryRecycleView.setAdapter(mAdapter3);

                        if(MainActivity.internetConnection.isConnectingToInternet())
                            new LoadItems().execute(String.valueOf(catID), String.valueOf(subcatID));

                        rootView1.findViewById(R.id.category).setVisibility(View.INVISIBLE);
                        rootView1.findViewById(R.id.subcategory).setVisibility(View.VISIBLE);
                        rootView1.findViewById(R.id.products).setVisibility(View.INVISIBLE);
                    }
                }
            };

            productsMsgHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.arg1 == 3) {


                        listOfItems = new ArrayList<>();

                        Log.i("numProducts", String.valueOf(numProducts));
                        Log.i("productsNameLength", String.valueOf(productsName.size()));
                        if(numProducts!=0)
                            for(int i=0;i<numProducts;i++)
                                listOfItems.add(i, new ItemDetailsClass(productsName.get(i),"1")); //TODO change this to URL from db

                        else
                            listOfItems = null;

                        mAdapter1 = new ItemsCardAdapter(listOfItems, rootView1.getContext());
                        productsRecyclerView.setAdapter(mAdapter1);

                        rootView1.findViewById(R.id.category).setVisibility(View.INVISIBLE);
                        rootView1.findViewById(R.id.subcategory).setVisibility(View.INVISIBLE);
                        rootView1.findViewById(R.id.products).setVisibility(View.VISIBLE);
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

            cartItemRecyclerView = (RecyclerView) rootView1.findViewById(R.id.cart_items_recyclerview);
            cartItemRecyclerView.setHasFixedSize(false);
            cartItemRecyclerView.setLayoutManager(new LinearLayoutManager(rootView1.getContext()));
            cartItemRecyclerView.setItemAnimator(new DefaultItemAnimator());

            cartItemRecyclerView.setAdapter(cAdapter);

            return rootView1;

        }

        private void refreshItems() {

            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    new LoadItems().execute(String.valueOf(catID),String.valueOf(subcatID));
                    newItemsHandler = new Handler() {
                        public void handleMessage(Message msg) {
                            if (msg.arg1 == 1) {
                                if(swipeRefreshLayoutProducts.isRefreshing())
                                    swipeRefreshLayoutProducts.setRefreshing(false);
                            }
                        }
                    };
                }
            });
        }

        private static void flipCards(String s){
            if(s.equals("cur_front"))
                fragManag.beginTransaction().setCustomAnimations(R.animator.card_flip_right_in,R.animator.card_flip_right_out,
                        R.animator.card_flip_left_in,R.animator.card_flip_left_out)
                        .replace(R.id.item_container, new CardBackFragment())
                        .commit();

            else if(s.equals("cur_back"))
                fragManag.beginTransaction().setCustomAnimations(R.animator.card_flip_left_in,R.animator.card_flip_left_out,
                        R.animator.card_flip_right_in,R.animator.card_flip_right_out)
                        .replace(R.id.item_container, new CardFrontFragment())
                        .commit();
        }

        public static class CardFrontFragment extends Fragment {
            private Button btnAdd;
            private FrameLayout itemCardFrame;

            public CardFrontFragment(){

            }
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.items_card_front,container,false);

                btnAdd = (Button) rootView.findViewById(R.id.buttonAdd);
                itemCardFrame = (FrameLayout) rootView.findViewById(R.id.item_card_frame);

                itemCardFrame.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent itemDescriptionIntent = new Intent(getActivity(),ParallaxToolbarScrollViewActivity.class);
                        itemDescriptionIntent.putExtra("Description","");
                        startActivity(itemDescriptionIntent);
                    }
                });
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getActivity().getApplicationContext(),"Clicked",Toast.LENGTH_SHORT).show();
                        flipCards("cur_front");
                    }
                });
                return rootView;
            }
        }

        public static class CardBackFragment extends Fragment {
            public CardBackFragment(){

            }
            @Override
            public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                     Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.items_card_back,container,false);


                return rootView;
            }
        }

    }

    public static void addtocart_fn(ItemDetailsClass item){

        Master.cAdapter.add(new CartItemsClass(item.getItemtitle()));

        Log.i("CartItems Length", String.valueOf(cartitems.size()));
        Log.i("Cart Recycler View Size", String.valueOf(cAdapter.getItemCount()));
    }

    public static void removefrom_cart(int position){

        Master.cAdapter.remove(position);
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            System.out.println(position);
            selectItem(position);
        }
    }

    public class NewItems extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String...params){

            Log.i("Inside Background", "True");

            List<NameValuePair> paramsItems = new ArrayList<NameValuePair>();

            paramsItems.add(new BasicNameValuePair("session", params[0]));
            paramsItems.add(new BasicNameValuePair("time", params[1]));

            ServiceHandler jsonParser = new ServiceHandler();
            newItemsReturnedJSON = jsonParser.makeServiceCall(newItemsURL, ServiceHandler.POST, paramsItems);
            if (newItemsReturnedJSON != null) {
                try {
                    Log.i("newItemsReturnedJSON", newItemsReturnedJSON);
                    JSONObject newItemsJSON = new JSONObject(newItemsReturnedJSON);
                    if (newItemsJSON.getString("success").equals("true")) {
                        //TODO store the catID, subcatID, prodID here
                    } else {

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            Message msg = new Message();
            msg.arg1 = 1;
            newItemsHandler.sendMessage(msg);

        }
    }

    public class LoadCatSubCat extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            loadCatSubCatProgress.setTitle("Loading Products List...");
            loadCatSubCatProgress.setCancelable(false);
            loadCatSubCatProgress.show();

        }

        @Override
        protected Void doInBackground(Void... arg) {

            ServiceHandler jsonParser = new ServiceHandler();
            productsJSON = jsonParser.makeServiceCall(categoriesURL, ServiceHandler.POST, null);
            if (productsJSON != null) {
                try {
                    JSONObject productsListJSON = new JSONObject(productsJSON);
                    if (productsListJSON.getString("success").equals("true")) {
                        numCategories = productsListJSON.getInt("numCategories");
                        JSONArray list = new JSONArray(String.valueOf(productsListJSON.getJSONArray("list")));

                        categoryID = new int[numCategories];
                        numSubCategories = new int[numCategories];
                        subcategoryID = new ArrayList<>();
                        subcategoryName = new ArrayList<>();

                        Log.i("list", String.valueOf(list));

                        for (int i = 0; i < numCategories; i++) {
                            JSONObject catObj = list.getJSONObject(i);
                            JSONArray sub = catObj.getJSONArray("subcategories");
                            categoryID[i] = catObj.getInt("ID");
                            categoryName.add(i, catObj.getString("name"));
                            numSubCategories[i] = catObj.getInt("numSubcategories");
                            int[] subIds = new int[numSubCategories[i]];
                            String[] subNames = new String[numSubCategories[i]];
                            for (int j = 0; j < numSubCategories[i]; j++) {
                                subIds[j] = sub.getJSONObject(j).getInt("subID");
                                subNames[j] = sub.getJSONObject(j).getString("name");
                            }
                            subcategoryID.add(i, subIds);
                            subcategoryName.add(i, subNames);
                            Log.i("subId", String.valueOf(subcategoryID.size()));
                            Log.i("subCat", String.valueOf(subcategoryName.size()));

                        }
                        Log.i("Loaded", "Fully done");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            newItemsHandler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.arg1 == 1) {
                        if (loadCatSubCatProgress != null && loadCatSubCatProgress.isShowing()) {
                            loadCatSubCatProgress.hide();
                            loadCatSubCatProgress.dismiss();
                        }
                    }
                }
            };

        }

    }

    public static class ChangeDetailsTask extends AsyncTask<String, Void, String> {

        String name, email, password, address, phone;
        boolean updateSuccess = false;

        @Override
        protected void onPreExecute() {
            Log.i("Inside PreExecute", "True");
            updateProgress.setTitle("Updating");
            updateProgress.setCancelable(false);
            updateProgress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Inside Background", "True");

            name = params[0];
            email = params[1];
            password = params[2];
            address = params[3];
            phone = params[4];

            List<NameValuePair> paramsUpdate = new ArrayList<NameValuePair>();
            paramsUpdate.add(new BasicNameValuePair("session", LoginActivity.session));
            paramsUpdate.add(new BasicNameValuePair("email", email));
            paramsUpdate.add(new BasicNameValuePair("name", name));
            paramsUpdate.add(new BasicNameValuePair("password", password));
            paramsUpdate.add(new BasicNameValuePair("address", address));
            paramsUpdate.add(new BasicNameValuePair("telephone", phone));
            ServiceHandler jsonParser = new ServiceHandler();
            updateDetailsReturnedJSON = jsonParser.makeServiceCall(updateDetailsURL, ServiceHandler.POST, paramsUpdate);
            if (updateDetailsReturnedJSON != null) {
                try {
                    JSONObject updateJSON = new JSONObject(updateDetailsReturnedJSON);
                    if (updateJSON.getString("success").equals("true")) {

                        LoginActivity.customerEmail = email;
                        LoginActivity.customerPassword = password;
                        LoginActivity.customerPhone = phone;
                        LoginActivity.customerAddress = address;
                        LoginActivity.customerName = name;

                        LoginActivity.prefs.edit().putString("Name", name).apply();
                        LoginActivity.prefs.edit().putString("Name", name).commit();
                        LoginActivity.prefs.edit().putString("Email", LoginActivity.user).apply();
                        LoginActivity.prefs.edit().putString("Password", LoginActivity.pass).apply();
                        LoginActivity.prefs.edit().putString("Email", LoginActivity.user).commit();
                        LoginActivity.prefs.edit().putString("Password", LoginActivity.pass).commit();
                        LoginActivity.prefs.edit().putString("Phone", phone).apply();
                        LoginActivity.prefs.edit().putString("Address", address).apply();
                        LoginActivity.prefs.edit().putString("Phone", phone).commit();
                        LoginActivity.prefs.edit().putString("Address", address).commit();

                        updateSuccess = true;
                    } else
                        updateSuccess = false;


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Inside PostExecute", "True");
            super.onPostExecute(result);

            if (updateProgress != null && updateProgress.isShowing() == true) {
                updateProgress.hide();
                updateProgress.cancel();
            }

            Message msg = new Message();
            msg.arg1 = 0;
            if (updateSuccess)
                msg.obj = "Updated details successfully";
            else
                msg.obj = "Error in updating details";
            MyAccountFragment.msgHandler.sendMessage(msg);
        }

    }
    public class LocationDetails extends AsyncTask<String, Void, String> {

        private boolean locationDetailsSuccess = false;
        @Override
        protected void onPreExecute() {
            Log.i("Inside PreExecute", "True");
            locationProgress.setTitle("Updating");
            locationProgress.setCancelable(false);
            locationProgress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Inside Background", "True");

            List<NameValuePair> paramsLocation = new ArrayList<NameValuePair>();

            paramsLocation.add(new BasicNameValuePair("latitude",params[0]));
            paramsLocation.add(new BasicNameValuePair("longitude",params[1]));
            paramsLocation.add(new BasicNameValuePair("session", LoginActivity.session));
            ServiceHandler jsonParser = new ServiceHandler();
            locationReturnedJSON = jsonParser.makeServiceCall(locationURL, ServiceHandler.POST, paramsLocation);
            if (locationReturnedJSON != null) {
                try{
                    Log.i("locationReturnedJSON",locationReturnedJSON);
                    JSONObject locationJSON = new JSONObject(locationReturnedJSON);
                    if(locationJSON.getString("success").equals("true")){
                        locationDetailsSuccess = true;
                        //TODO load items from this place
                    }
                    else
                        locationDetailsSuccess = false;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Inside PostExecute", "True");
            super.onPostExecute(result);
            if(locationProgress!=null && locationProgress.isShowing())
            {
                locationProgress.hide();
                locationProgress.dismiss();
            }

            if(!locationDetailsSuccess)
                Toast.makeText(getApplicationContext(),"Unable to load products",Toast.LENGTH_SHORT).show();
            else {
                new LoadCatSubCat().execute();
                new NewItems().execute(LoginActivity.session, String.valueOf(System.currentTimeMillis()));
            }
        }

    }

    public static class LoadItems extends AsyncTask<String, Void, String> {

        private boolean loadItemsSuccess = false;
        private int[] newItemsIDs, oldItemsIDs;
        private String[] newItemsName, oldItemsNames;
        private int tempProductsId;
        private String tempProductsName;
        private boolean newItemCatSuccess = false, newItemSubCatSuccess = false, newItemProductSuccess= false;
        private int newID = 0, oldID = 0;

        @Override
        protected void onPreExecute() {
            Log.i("Inside PreExecute", "True");
            loadItemsProgress.setTitle("Loading items list...");
            loadItemsProgress.setCancelable(false);
            loadItemsProgress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Inside Background", "True");

            List<NameValuePair> paramsItems = new ArrayList<NameValuePair>();

            Log.i("catID",params[0]);
            Log.i("subCatID",params[1]);

            paramsItems.add(new BasicNameValuePair("session", LoginActivity.session));
            paramsItems.add(new BasicNameValuePair("ID",params[0]));
            paramsItems.add(new BasicNameValuePair("subID",params[1]));
            ServiceHandler jsonParser = new ServiceHandler();
            itemsReturnedJSON = jsonParser.makeServiceCall(itemsURL, ServiceHandler.POST, paramsItems);
            if (itemsReturnedJSON != null) {
                try{

                    Log.i("itemsReturnedJSON",itemsReturnedJSON);
                    JSONObject itemsJSON = new JSONObject(itemsReturnedJSON);
                    if(itemsJSON.getString("success").equals("true")){

                        newID = 0;
                        oldID =0;
                        loadItemsSuccess = true;
                        JSONArray itemsList = new JSONArray(String.valueOf(itemsJSON.getJSONArray("items"))); //TODO change the key name
                        JSONObject tempItemJSON;
                        numProducts = itemsJSON.getInt("itemCount");
                        productsID = new int[numProducts];
                        oldItemsIDs = new int[numProducts];
                        newItemsIDs = new int[numProducts];
                        oldItemsNames = new String[numProducts];
                        newItemsName = new String[numProducts];

                        productDesc = new ArrayList<>();
                        productsName = new ArrayList<>();

                        for(int i=0;i<numProducts;i++) {
                            tempItemJSON = new JSONObject(String.valueOf(itemsList.getJSONObject(i)));
                            Log.i("tempItems", String.valueOf(tempItemJSON));

                            tempProductsId = tempItemJSON.getInt("PID");
                            tempProductsName = tempItemJSON.getString("name");

                        if(newProductsCatId!=null)
                            for(int j=0;j<newProductsCatId.length;j++){
                                if(Integer.parseInt(params[0])==newProductsCatId[j]){
                                    newItemCatSuccess = true;
                                    break;
                                }
                                else
                                    newItemCatSuccess = false;
                            }

                        if(newProductsSubCatId!=null)
                            if(newItemCatSuccess){
                                for(int j=0;j<newProductsSubCatId.length;j++){
                                    if(Integer.parseInt(params[1])==newProductsSubCatId[j]){
                                        newItemSubCatSuccess = true;
                                        break;
                                    }
                                    else
                                        newItemSubCatSuccess = false;
                                }
                            }

                            if(newItemSubCatSuccess && newItemCatSuccess){
                                for(int j=0;j<newProductsID.length;j++){
                                    if(tempProductsId==newProductsID[j]){
                                        newItemProductSuccess = true;
                                        break;
                                    }
                                    else
                                        newItemProductSuccess = false;
                                }
                            }

                            if(newItemProductSuccess && newItemSubCatSuccess && newItemCatSuccess){
                                newItemsIDs[newID] = tempProductsId;
                                newItemsName[newID] = tempProductsName;
                                newID++;
                            }

                            else{
                                oldItemsIDs[oldID] = tempProductsId;
                                oldItemsNames[oldID] = tempProductsName;
                                oldID++;
                            }

                        }

                        Log.i("oldItemsNames",oldItemsNames[0]);
                        Log.i("productsIdLength", String.valueOf(productsID.length));
                        Log.i("oldIDLength", String.valueOf(oldItemsIDs.length));

                        for(int i=0;i<newID;i++){
                            productsID[i] = newItemsIDs[i];
                            productsName.add(newItemsName[i]);
                        }

                        Log.i("productsNameStatus", String.valueOf(productsName.isEmpty()));
                        if(!productsName.isEmpty())
                        for(int j=productsID.length;j<(productsID.length+oldID);j++){
                            productsID[j] = oldItemsIDs[j-productsID.length];
                            productsName.add(oldItemsNames[j-productsID.length]);
                        }

                        else
                        for(int j=0;j<oldID;j++){
                            productsID[j] = oldItemsIDs[j];
                            productsName.add(oldItemsNames[j]);
                        }

                        Log.i("productsLength", String.valueOf(productsName.size()));
                        //productsID[i] = tempItemJSON.getInt("PID");
                        //productsName.add(i, tempItemJSON.getString("name"));
                        // productDesc.add(i, tempItemJSON.getString("description"));
                    }
                    else
                        ;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Inside PostExecute", "True");
            super.onPostExecute(result);

            if(loadItemsProgress!=null && loadItemsProgress.isShowing()) {
                loadItemsProgress.hide();
                loadItemsProgress.dismiss();
            }

            if(ProductsFragment.swipeRefreshLayoutProducts.isRefreshing())
                ProductsFragment.swipeRefreshLayoutProducts.setRefreshing(false);

            if(loadItemsSuccess) {
                Message itemsMsg = new Message();
                itemsMsg.arg1=3;
                ProductsFragment.productsMsgHandler.sendMessage(itemsMsg);

            }

            for(int i=0;i<numProducts;i++)
                new LoadProductImages().execute(String.valueOf(productsID[i]));

        }

    }

    private static class LoadProductImages extends AsyncTask<String, Void, String>{

        @Override
        protected void onPreExecute() {
            Log.i("Inside PreExecute", "True");
            loadItemsProgress.setTitle("Loading items list...");
            loadItemsProgress.setCancelable(false);
            loadItemsProgress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Inside Background", "True");

            List<NameValuePair> paramsItems = new ArrayList<NameValuePair>();

            paramsItems.add(new BasicNameValuePair("PID",params[0]));
            paramsItems.add(new BasicNameValuePair("width", "100"));
            ServiceHandler jsonParser = new ServiceHandler();
            itemsURLReturnedJSON = jsonParser.makeServiceCall(itemsImagesURL, ServiceHandler.GET, paramsItems);
            if (itemsURLReturnedJSON != null) {
                try{
                    Log.i("itemsURLReturnedJSON",itemsURLReturnedJSON);
                    JSONObject itemsURLJSON = new JSONObject(itemsURLReturnedJSON);
                    if(itemsURLJSON.getString("success").equals("true")){
                        //TODO load item images from this place
                    }
                    else
                        ;
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Inside PostExecute", "True");
            super.onPostExecute(result);

            if(loadItemsProgress!=null && loadItemsProgress.isShowing()) {
                loadItemsProgress.hide();
                loadItemsProgress.dismiss();
            }

        }

    }

    private class Logout extends AsyncTask<String, Void, String> {

        private boolean logoutSuccess = false;
        @Override
        protected void onPreExecute() {
            Log.i("Inside PreExecute", "True");
            logoutProgress.setTitle("Logging out...");
            logoutProgress.setCancelable(false);
            logoutProgress.show();
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Inside Background", "True");

            List<NameValuePair> paramsItems = new ArrayList<NameValuePair>();

            paramsItems.add(new BasicNameValuePair("session", params[0]));
            ServiceHandler jsonParser = new ServiceHandler();
            logoutReturnedJSON = jsonParser.makeServiceCall(logoutURL, ServiceHandler.POST, paramsItems);
            if (logoutReturnedJSON != null) {
                try {
                    Log.i("logoutReturnedJSON", logoutReturnedJSON);
                    JSONObject logoutJSON = new JSONObject(logoutReturnedJSON);
                    if (logoutJSON.getString("success").equals("true")) {
                        logoutSuccess = true;
                        Master.logoutSuccess = true;
                    } else {
                        logoutSuccess = false;
                        Master.logoutSuccess = false;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.i("Inside PostExecute", "True");
            super.onPostExecute(result);

            if ( logoutProgress != null && logoutProgress.isShowing()) {
                logoutProgress.hide();
                logoutProgress.dismiss();
            }

            if(logoutSuccess) {
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

                Message msg = new Message();
                msg.arg1 = 1;
                msg.arg2 = 1;
                logoutHandler.sendMessage(msg);

            }

            else if(MainActivity.internetConnection.isConnectingToInternet())
                Toast.makeText(getApplicationContext(),"No network connection available",Toast.LENGTH_SHORT).show();

            else
                Toast.makeText(getApplicationContext(),"Unable to connect",Toast.LENGTH_SHORT).show();

        }

    }
}