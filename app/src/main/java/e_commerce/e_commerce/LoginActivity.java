package e_commerce.e_commerce;

/**
 * Created by Suganprabu on 17-04-2015.
 */

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import util.ServiceHandler;

/*
    TODO Shared preferences will store user Name, Email, Password, Address, Phone, ProfilePicture, Notification, LoginMode
    TODO city, area, (cityname, areaname,) LoginStatus, Latitude, Longitude
 */



public class LoginActivity extends FragmentActivity implements
        ConnectionCallbacks, OnConnectionFailedListener,
        View.OnClickListener{
    private static final int PROFILE_PIC_SIZE = 150;
    public static String profileText;
    public static Dialog myDialog;
    public static Bitmap bmImage;
    public static boolean registerSuccess = false, loginSuccess=false;
    public static SharedPreferences prefs;
    public static String customerName, customerEmail, customerPassword, customerPhone, customerAddress, email, userPassword, personName,personPhotoUrl,personGooglePlusProfile;
    public EditText passwordEdit, emailEdit;
    public CheckBox passwordChecked;
    public Button loginSubmit;
    public TextView newUser;
    public static String pass;
    public static String user;
    public String cpass;
    public static String tempAddress;
    public static String tempPhone;
    public String name;
    public LoginButton facebookAuthButton;
    public Fragment mainFragment;
    private static int fbLogin=-1;
    private static final String TAG = "android-plus-quickstart";
    private static final int STATE_DEFAULT = 0;
    private static final int STATE_SIGN_IN = 1;
    private static final int STATE_IN_PROGRESS = 2;
    private static final int RC_SIGN_IN = 0;
    private static final int DIALOG_PLAY_SERVICES_ERROR = 0;
    private static final String SAVED_PROGRESS = "sign_in_progress";
    private static GoogleApiClient mGoogleApiClient;
    private static int mSignInProgress;
    private PendingIntent mSignInIntent;
    private int mSignInError;
    private SignInButton mSignInButton;
    public static ProgressDialog registerProgressDialog, loginProgressDialog;
    private String loginReturnedJSON,loginUserDetailsReturnedJSON;
    private final String LoginURL = "http://grokart.ueuo.com/login.php";
    private final String RegisterURL = "http://grokart.ueuo.com/newUser.php";
    private final String UserInfoURL = "http://grokart.ueuo.com/userInfo.php";
    public static String sessionId,session;
    public static String fbName,fbEmail,fbPhone,fbAddress;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        prefs = getSharedPreferences("E-Commerce", MODE_PRIVATE);

        registerProgressDialog = new ProgressDialog(LoginActivity.this);
        loginProgressDialog = new ProgressDialog(LoginActivity.this);


        if(!prefs.getString("LoginStatus","").equals("")&&prefs.getString("LoginStatus","").equals("Logged in")
                ||prefs.getString("LoginStatus","").equals("Already Logged in")){
            prefs.edit().putString("LoginStatus","Already Logged in").apply();
            prefs.edit().putString("LoginStatus","Already Logged in").commit();
            Intent loginIntent = new Intent(LoginActivity.this,Master.class);
            loginIntent.putExtra("loginMethod",prefs.getString("LoginMode",""));
            startActivity(loginIntent);
            finish();
        }


        mSignInButton = (SignInButton) findViewById(R.id.googleAuthButton);

        if (savedInstanceState == null) {
            // Add the fragment on initial activity setup
            mainFragment = new facebookLoginFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(android.R.id.content,mainFragment).commit();

        } else {
            // Or set the fragment from restored state info
            mSignInProgress = savedInstanceState
                    .getInt(SAVED_PROGRESS, STATE_DEFAULT);
            mainFragment = (facebookLoginFragment) getSupportFragmentManager()
                    .findFragmentById(android.R.id.content);
        }

        mGoogleApiClient = buildGoogleApiClient();

        passwordEdit = (EditText) findViewById(R.id.passWord);
        emailEdit = (EditText) findViewById(R.id.emailId);
        loginSubmit = (Button) findViewById(R.id.loginSubmit);
        newUser = (TextView) findViewById(R.id.newUser);
        passwordChecked = (CheckBox) findViewById(R.id.checkboxShowPassword);
        facebookAuthButton = (LoginButton) findViewById(R.id.facebookAuthButton);

        facebookAuthButton.setReadPermissions(Arrays.asList("public_profile"));
        facebookAuthButton.setFragment(mainFragment);

        newUser.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);

        customerEmail = prefs.getString("Email", "");
        customerPassword = prefs.getString("Password", "");

     /*   if(!email.equals("")&&!userPassword.equals(""))
        {
            startActivity(new Intent(LoginActivity.this,Master.class));
            // finish();
        }
*/
        passwordEdit.setHint("Password");

        emailEdit.setHint("Email Id");

        passwordChecked.setChecked(false);

        passwordChecked.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked)
                    passwordEdit.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                else if (!isChecked)
                    passwordEdit.setInputType(129);
            }
        });

        newUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                myDialog = new Dialog(LoginActivity.this);
                registerDialog();
            }
        });

        loginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userPassword = passwordEdit.getText().toString();
                email = emailEdit.getText().toString();

                //TODO check with the database for email and password
                if (!userPassword.isEmpty() && !email.isEmpty()) {
                    if(MainActivity.internetConnection.isConnectingToInternet())
                        new LoginValidation().execute();
                    else
                        Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                } else if (userPassword.isEmpty() || email.isEmpty())
                    Toast.makeText(getApplicationContext(), "The email id or password you entered is incorrect", Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void registerDialog() {
        final EditText email, password, confirmPassword, Address, Phone, Name;
        final Button registerSubmit;

        myDialog.setContentView(R.layout.register);
        myDialog.setTitle("Create a new account");
        myDialog.setCancelable(true);
        myDialog.show();

        email = (EditText) myDialog.findViewById(R.id.registerEmail);
        password = (EditText) myDialog.findViewById(R.id.registerPassword);
        confirmPassword = (EditText) myDialog.findViewById(R.id.registerConfirmPassword);
        registerSubmit = (Button) myDialog.findViewById(R.id.registerSubmit);
        Address = (EditText) myDialog.findViewById(R.id.registerAddress);
        Phone = (EditText) myDialog.findViewById(R.id.registerPhone);
        Name = (EditText) myDialog.findViewById(R.id.registerName);


        registerSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pass = password.getText().toString();
                user = email.getText().toString();
                cpass = confirmPassword.getText().toString();
                tempAddress = Address.getText().toString();
                tempPhone = Phone.getText().toString();
                name = Name.getText().toString();

                if (!user.isEmpty() && !pass.isEmpty() && !cpass.isEmpty()) {
                    if ((!name.equals("")) && user.contains("@") && pass.equals(cpass) && pass.length() >= 8 && cpass.length() >= 8) {

                        new RegisterTask().execute(name, user, pass, tempAddress, tempPhone);
                        if(LoginActivity.registerSuccess==true) {
                            LoginActivity.customerEmail = user;
                            LoginActivity.customerName = name;
                            LoginActivity.customerPassword = pass;
                            LoginActivity.customerPhone = tempPhone;
                            LoginActivity.customerAddress = tempAddress;
                            prefs.edit().putString("Name", name).apply();
                            prefs.edit().putString("Name", name).commit();
                            prefs.edit().putString("Email", user).apply();
                            prefs.edit().putString("Password", pass).apply();
                            prefs.edit().putString("Email", user).commit();
                            prefs.edit().putString("Password", pass).commit();
                            prefs.edit().putString("Phone", customerPhone).apply();
                            prefs.edit().putString("Address", customerAddress).apply();
                            prefs.edit().putString("Phone", customerPhone).commit();
                            prefs.edit().putString("Address", customerAddress).commit();
                            prefs.edit().putString("LoginMode", "App").apply();
                            prefs.edit().putString("LoginStatus", "App").commit();
                            prefs.edit().putString("LoginStatus", "Logged in").apply();
                            prefs.edit().putString("LoginStatus", "Logged in").commit();

                            AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                            b.setTitle("Success");
                            b.setMessage("You have successfully created an account");
                            final AlertDialog a = b.create();
                            a.show();

                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {

                                @Override
                                public void run() {
                                    a.dismiss();
                                    a.cancel();
                                    Intent loginIntent = new Intent(LoginActivity.this, Master.class);
                                    loginIntent.putExtra("loginMethod", "App");
                                    startActivity(loginIntent);
                                    finish();
                                    myDialog.dismiss();
                                    myDialog.cancel();
                                }
                            }, 2000);

                        }

                        else if(!MainActivity.internetConnection.isConnectingToInternet()){
                            Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
                        }


                        else if(registerSuccess==false){
                            Toast.makeText(getApplicationContext(),"The Email ID you have entered has already been registered",Toast.LENGTH_SHORT).show();
                        }

                    } else if(name.equals("")) {
                        Toast.makeText(getApplicationContext(),"Please enter your name",Toast.LENGTH_SHORT).show();
                    } else if (!pass.equals(cpass)) {
                        Toast.makeText(getApplicationContext(), "Passwords don't match", Toast.LENGTH_SHORT).show();
                    } else if (pass.length() < 8) {
                        Toast.makeText(getApplicationContext(), "Password is too short", Toast.LENGTH_SHORT).show();
                    } else if (user.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter email id", Toast.LENGTH_SHORT).show();
                    } else if (pass.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please enter password", Toast.LENGTH_SHORT).show();
                    } else if (cpass.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Please confirm your password", Toast.LENGTH_SHORT).show();
                    } else if (!user.contains("@")) {
                        Toast.makeText(getApplicationContext(), "Please enter a valid email id", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
*/
    public static class facebookLoginFragment extends Fragment {

        public facebookLoginFragment() {
        }

        private Session.StatusCallback statusCallback =
                new SessionStatusCallback();

        Session session = Session.getActiveSession();

        public UiLifecycleHelper uiHelper;

        public static final String TAG = "FacebookLoginFragment";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.com_facebook_login_activity_layout, container, false);

            uiHelper = new UiLifecycleHelper(getActivity() , callback);
            uiHelper.onCreate(savedInstanceState);

            if (session != null &&
                    (session.isOpened() || session.isClosed()) ) {
                onSessionStateChange(session, session.getState(), null);
            }
            uiHelper.onResume();


            return rootView;
        }


        @Override
        public void onResume() {
            super.onResume();
            uiHelper.onResume();
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch (requestCode) {
                case RC_SIGN_IN:
                    if (resultCode == RESULT_OK) {

                        mSignInProgress = STATE_SIGN_IN;
                    } else {

                        mSignInProgress = STATE_DEFAULT;
                    }

                    if (!mGoogleApiClient.isConnecting()) {

                        mGoogleApiClient.connect();
                    }
                    break;
            }
            uiHelper.onActivityResult(requestCode, resultCode, data);

        }
        @Override
        public void onPause() {
            super.onPause();
            uiHelper.onPause();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            uiHelper.onDestroy();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            uiHelper.onSaveInstanceState(outState);

        }

        private class SessionStatusCallback implements Session.StatusCallback {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                // Respond to session state changes, ex: updating the view

            }
        }

        private void onClickLogin() {
            Session session = Session.getActiveSession();
            if (!session.isOpened() && !session.isClosed()) {
                session.openForRead(new Session.OpenRequest(this)
                        .setPermissions(Arrays.asList("public_profile"))
                        .setCallback(statusCallback));


            } else {
                if(session!=null && session.isOpened()){
                    makeMeRequest(session);
                }
                Session.openActiveSession(getActivity(), this, true, statusCallback);
            }
        }

        private void onSessionStateChange(Session session,SessionState state,Exception exception){
            if(state.isOpened()) {
                Log.i(TAG, "Logged in");
                fbLogin=1;
                //new LoginValidation().execute();

                LoginActivity.prefs.edit().putString("LoginMode","Facebook").apply();
                LoginActivity.prefs.edit().putString("LoginStatus","Logged in").apply();
                LoginActivity.prefs.edit().putString("LoginMode","Facebook").commit();
                LoginActivity.prefs.edit().putString("LoginStatus","Logged in").commit();
                Intent loginIntent = new Intent(getActivity(),Master.class);
                loginIntent.putExtra("loginMethod","Facebook");
                startActivity(loginIntent);
                makeMeRequest(session);
            }
            else if(state.isClosed()) {
                Log.i(TAG, "Logged out");
            }
        }

        public static void callFacebookLogout(Context context) {
            Session session = Session.getActiveSession();
            if (session != null) {

                if (!session.isClosed()) {
                    session.closeAndClearTokenInformation();
                    //clear your preferences if saved
                }
            } else {

                session = new Session(context);
                Session.setActiveSession(session);

                session.closeAndClearTokenInformation();
                //clear your preferences if saved

            }

            Log.i("FB Logout","Successfully logged out");

        }

        private void makeMeRequest(final Session session) {
            // Make an API call to get user data and define a
            // new callback to handle the response.
            Request request = Request.newMeRequest(session,
                    new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user, Response response) {
                            // If the response is successful
                            if (session == Session.getActiveSession()) {
                                if (user != null) {
                                    //profilePictureView.setProfileId(user.getId());
                                    //userNameView.setText(user.getName());
                                    Log.i("username", user.getName());
                                    Log.i("pro_pic", user.getId());

                                    Master.facebookProfileIcon.setProfileId(user.getId());
                                    Master.profileIconText.setText(user.getName());

                                }
                            }
                            if (response.getError() != null) {
                                // Handle errors, will do so later.
                            }
                        }


                    });
            request.executeAsync();
        }

        private Session.StatusCallback callback = new Session.StatusCallback() {
            @Override
            public void call(Session session, SessionState state, Exception exception) {
                onSessionStateChange(session, state, exception);
                makeMeRequest(session);
            }
        };


    }

    private GoogleApiClient buildGoogleApiClient() {

        return new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API, Plus.PlusOptions.builder().build())
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVED_PROGRESS, mSignInProgress);
    }

    @Override
    public void onClick(View v) {
        if (!mGoogleApiClient.isConnecting()) {

            switch (v.getId()) {
                case R.id.googleAuthButton:
                    mSignInProgress = STATE_SIGN_IN;
                    resolveSignInError();
                    break;
            }
        }
    }


    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i(TAG, "onConnected");

        Person currentUser = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);

        final String personName = currentUser.getDisplayName();
        String personPhotoUrl = currentUser.getImage().getUrl();
        String personGooglePlusProfile = currentUser.getUrl();
        String email = Plus.AccountApi.getAccountName(mGoogleApiClient);

        Log.i("Name",personName);
        Log.i("PhotoUrl",personPhotoUrl);
        Log.i("UserUrl",personGooglePlusProfile);
        Log.i("Email",email);
        personPhotoUrl = personPhotoUrl.substring(0,
                personPhotoUrl.length() - 2)
                + PROFILE_PIC_SIZE;

        new LoadProfileImage(LoginActivity.bmImage).execute(personPhotoUrl);

        LoginActivity.prefs.edit().putString("ProfilePicture",encodeTobase64(bmImage));

        mSignInProgress = STATE_DEFAULT;

        if(mGoogleApiClient.isConnected()) {
            final Intent loginIntent = new Intent(LoginActivity.this, Master.class);
            loginIntent.putExtra("loginMethod", "Google");

            if(mSignInButton.isEnabled())
                mSignInButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LoginActivity.prefs.edit().putString("LoginMode","Google").apply();
                        LoginActivity.prefs.edit().putString("LoginStatus","Logged in").apply();
                        LoginActivity.prefs.edit().putString("LoginMode","Google").commit();
                        LoginActivity.prefs.edit().putString("LoginStatus","Logged in").commit();
                        startActivity(loginIntent);
                        profileText = personName;
                    }
                });

        }
    }


    @Override
    public void onConnectionFailed(ConnectionResult result) {

        Log.i(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + result.getErrorCode());

        if (result.getErrorCode() == ConnectionResult.API_UNAVAILABLE) {

        } else if (mSignInProgress != STATE_IN_PROGRESS) {

            mSignInIntent = result.getResolution();
            mSignInError = result.getErrorCode();

            if (mSignInProgress == STATE_SIGN_IN) {

                resolveSignInError();
            }
        }


        onSignedOut();
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {

        public LoadProfileImage(Bitmap bmImage) {
            LoginActivity.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            LoginActivity.bmImage = result;
        }
    }
    private void resolveSignInError() {
        if (mSignInIntent != null) {


            try {

                mSignInProgress = STATE_IN_PROGRESS;
                startIntentSenderForResult(mSignInIntent.getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                Log.i(TAG, "Sign in intent could not be sent: "
                        + e.getLocalizedMessage());

                mSignInProgress = STATE_SIGN_IN;
                mGoogleApiClient.connect();
            }
        } else {

            showDialog(DIALOG_PLAY_SERVICES_ERROR);
        }
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    mSignInProgress = STATE_SIGN_IN;
                } else {
                    mSignInProgress = STATE_DEFAULT;
                }
                if (!mGoogleApiClient.isConnecting()) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }*/



    private void onSignedOut() {

        mSignInButton.setEnabled(true);

    }

    @Override
    public void onConnectionSuspended(int cause) {

        mGoogleApiClient.connect();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch(id) {
            case DIALOG_PLAY_SERVICES_ERROR:
                if (GooglePlayServicesUtil.isUserRecoverableError(mSignInError)) {
                    return GooglePlayServicesUtil.getErrorDialog(
                            mSignInError,
                            this,
                            RC_SIGN_IN,
                            new DialogInterface.OnCancelListener() {
                                @Override
                                public void onCancel(DialogInterface dialog) {
                                    Log.e(TAG, "Google Play services resolution cancelled");
                                    mSignInProgress = STATE_DEFAULT;
                                }
                            });
                } else {
                    return new AlertDialog.Builder(this)
                            .setMessage(R.string.play_services_error)
                            .setPositiveButton(R.string.close,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Log.e(TAG, "Google Play services error could not be "
                                                    + "resolved: " + mSignInError);
                                            mSignInProgress = STATE_DEFAULT;
                                        }
                                    }).create();
                }
            default:
                return super.onCreateDialog(id);
        }
    }

    public static void callGoogleLogout(){

        if(mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
            //mGoogleApiClient.connect();
        }
    }

    public static String encodeTobase64(Bitmap image) {
        try {
            Bitmap immage = image;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            immage.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
            Log.d("Image Log:", imageEncoded);
            return imageEncoded;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap decodeBase64(String input) {
        try {
            byte[] encodeByte = Base64.decode(input, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch (Exception e) {
            e.getMessage();
            return null;
        }
    }

    public class RegisterTask extends AsyncTask<String,Void,String> {

        String name,email,password,address,phone;

        @Override
        protected void onPreExecute() {
            Log.i("Inside PreExecute", "True");
            LoginActivity.registerProgressDialog.setTitle("Registering...");
            LoginActivity.registerProgressDialog.show();
            LoginActivity.registerProgressDialog.setCancelable(false);
        }

        @Override
        protected String doInBackground(String... params) {
            Log.i("Inside Background","True");

            name=params[0];
            email=params[1];
            password=params[2];
            address=params[3];
            phone=params[4];

            List<NameValuePair> paramsRegister = new ArrayList<NameValuePair>();
            paramsRegister.add(new BasicNameValuePair("email",email));
            paramsRegister.add(new BasicNameValuePair("password",password));
            paramsRegister.add(new BasicNameValuePair("name",name));
            paramsRegister.add(new BasicNameValuePair("address",address));
            paramsRegister.add(new BasicNameValuePair("telephone",phone));
            ServiceHandler jsonParser = new ServiceHandler();
            loginReturnedJSON=jsonParser.makeServiceCall(RegisterURL,ServiceHandler.POST,paramsRegister);
            if(!MainActivity.internetConnection.isConnectingToInternet())
                return null;
            if(loginReturnedJSON!=null){
                try{
                    JSONObject registerJSON = new JSONObject(loginReturnedJSON);
                    Log.i("registerValidation",registerJSON.getString("success"));
                    if(registerJSON.getString("success").equals("true")) {

                        session=registerJSON.getString("session");
                        sessionId=registerJSON.getString("ID");

                        Log.i("session",session);
                        Log.i("session Id",sessionId);

                        LoginActivity.customerEmail = LoginActivity.user;
                        LoginActivity.customerPassword = LoginActivity.pass;
                        LoginActivity.customerPhone = LoginActivity.tempPhone;
                        LoginActivity.customerAddress = LoginActivity.tempAddress;
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
                        LoginActivity.prefs.edit().putString("LoginMode", "App").apply();
                        LoginActivity.prefs.edit().putString("LoginStatus", "App").commit();
                        LoginActivity.prefs.edit().putString("LoginStatus", "Logged in").apply();
                        LoginActivity.prefs.edit().putString("LoginStatus", "Logged in").commit();

                        registerSuccess = true;
                    }

                    else
                        registerSuccess = false;


                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result){
            Log.i("Inside PostExecute","True");
            super.onPostExecute(result);

            //LoginActivity.registerSuccess=true;
            if(LoginActivity.registerProgressDialog!=null && LoginActivity.registerProgressDialog.isShowing()==true){
                LoginActivity.registerProgressDialog.hide();
                LoginActivity.registerProgressDialog.cancel();
            }
            if(LoginActivity.registerSuccess==true) {

                AlertDialog.Builder b = new AlertDialog.Builder(LoginActivity.this);
                b.setTitle("Success");
                b.setMessage("You have successfully created an account");
                final AlertDialog a = b.create();
                a.show();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        a.dismiss();
                        a.cancel();
                        Intent loginIntent = new Intent(LoginActivity.this, Master.class);
                        loginIntent.putExtra("loginMethod", "App");
                        startActivity(loginIntent);
                        finish();
                        myDialog.dismiss();
                        myDialog.cancel();
                    }
                }, 2000);

            }
        }

    }


    private class LoginValidation extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            // Showing progress dialog
            loginProgressDialog.setTitle("Verifying...");
            loginProgressDialog.setCancelable(false);
            loginProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0){

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("email", email));
            params.add(new BasicNameValuePair("password", userPassword));
            ServiceHandler jsonParser = new ServiceHandler();
            loginReturnedJSON=jsonParser.makeServiceCall(LoginURL,ServiceHandler.POST,params);
            if(loginReturnedJSON!=null){
                try{
                    JSONObject loginJSON = new JSONObject(loginReturnedJSON);
                    Log.i("loginValidation",loginJSON.getString("success"));
                    if(loginJSON.getString("success").equals("true")) {

                        session=loginJSON.getString("session");
                        sessionId=loginJSON.getString("ID");

                        Log.i("Session",session);
                        Log.i("Session Id",sessionId);

                        prefs.edit().putString("Email", email).apply();
                        prefs.edit().putString("Password", userPassword).apply();
                        prefs.edit().putString("Email", email).commit();
                        prefs.edit().putString("Password", userPassword).commit();

                        prefs.edit().putString("LoginStatus", "Logged in").apply();
                        prefs.edit().putString("LoginStatus", "Logged in").commit();

                        prefs.edit().putString("LoginMode", "App").apply();
                        prefs.edit().putString("LoginMode", "App").commit();

                        loginSuccess = true;
                    }

                    else
                        loginSuccess = false;


                }catch(JSONException e){
                    e.printStackTrace();
                }
            }

            //  else Toast.makeText(getApplicationContext(),"The email id or password you entered is incorrect",Toast.LENGTH_SHORT).show();


            return null;
        }

        @Override
        protected void onPostExecute(Void result){
            super.onPostExecute(result);

            if(loginSuccess==true)
                new GetUserDetails().execute(session);
            else if(!MainActivity.internetConnection.isConnectingToInternet())
                Toast.makeText(getApplicationContext(),"Please check your internet connection",Toast.LENGTH_SHORT).show();
            else{
                loginProgressDialog.hide();
                loginProgressDialog.dismiss();
                Toast.makeText(getApplicationContext(), "The email id or password you entered is incorrect", Toast.LENGTH_SHORT).show();
            }

            /*if(loginProgressDialog!=null && loginProgressDialog.isShowing()){
                loginProgressDialog.hide();
                loginProgressDialog.dismiss();
            }

            if(loginSuccess==true) {
                new GetUserDetails().execute(session);
                Intent loginIntent = new Intent(LoginActivity.this, Master.class);
                loginIntent.putExtra("loginMethod", "App");
                startActivity(loginIntent);
                finish();
            }

            else{
                    Toast.makeText(getApplicationContext(),"The email id or password you entered is incorrect",Toast.LENGTH_SHORT).show();
                   // Log.i("loginError",loginJSON.getString("error"));
            }
            */
        }
    }

    public class GetUserDetails extends AsyncTask<String,Void,Void>{

        @Override
        protected void onPreExecute(){
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... arg){

            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("session", arg[0]));
            ServiceHandler jsonParser = new ServiceHandler();
            loginUserDetailsReturnedJSON=jsonParser.makeServiceCall(UserInfoURL,ServiceHandler.POST,params);
            if(loginUserDetailsReturnedJSON!=null){
                try{
                    Log.i("loginUserDetails",loginUserDetailsReturnedJSON);
                    JSONObject getUserDetailsJSON = new JSONObject(loginUserDetailsReturnedJSON);
                    Log.i("loginValidation",getUserDetailsJSON.getString("success"));
                    if(getUserDetailsJSON.getString("success").equals("true")) {
                        customerName=getUserDetailsJSON.getString("name");
                        customerEmail=getUserDetailsJSON.getString("email");
                        customerPhone=getUserDetailsJSON.getString("telephone");
                        customerAddress=getUserDetailsJSON.getString("address");
                        loginSuccess = true;
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

            if(loginProgressDialog!=null && loginProgressDialog.isShowing()){
                loginProgressDialog.hide();
                loginProgressDialog.dismiss();
            }

            if(loginSuccess==true) {
                prefs.edit().putString("Name",customerName).apply();
                prefs.edit().putString("Name",customerName).commit();
                prefs.edit().putString("Phone",customerPhone).apply();
                prefs.edit().putString("Phone",customerPhone).commit();
                prefs.edit().putString("Address",customerAddress).apply();
                prefs.edit().putString("Address",customerAddress).commit();

                Intent loginIntent = new Intent(LoginActivity.this, Master.class);
                loginIntent.putExtra("loginMethod", "App");
                startActivity(loginIntent);
                finish();
            }

            else{
                Toast.makeText(getApplicationContext(),"The email id or password you entered is incorrect",Toast.LENGTH_SHORT).show();
                // Log.i("loginError",loginJSON.getString("error"));
            }
        }

    }

}



