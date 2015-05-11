package e_commerce.e_commerce;

import android.app.SearchManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import util.SearchSuggestionProvider;
import util.ServiceHandler;


public class ItemsSearchActivity extends ActionBarActivity {

    private String searchQuery, SearchReturnedJSON;
    private final String searchURL="http://grokart.ueuo.com/search.php";
    public static SearchRecentSuggestions recentSuggestions;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_items_search);

        Intent intent = getIntent();
        if(Intent.ACTION_SEARCH.equals(intent.getAction())){
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
            recentSuggestions = new SearchRecentSuggestions(this, SearchSuggestionProvider.AUTHORITY, SearchSuggestionProvider.MODE);
            recentSuggestions.saveRecentQuery(searchQuery,null);
        }

        new Search().execute(searchQuery);
        Toast.makeText(getApplicationContext(),searchQuery,Toast.LENGTH_SHORT).show();

        Toast.makeText(getApplicationContext(),LoginActivity.prefs.getString("session",""),Toast.LENGTH_SHORT).show();

    }

    private class Search extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... params) {

            List<NameValuePair> paramsSearch = new ArrayList<>();

            paramsSearch.add(new BasicNameValuePair("session",LoginActivity.prefs.getString("session","")));
            paramsSearch.add(new BasicNameValuePair("q",params[0]));

            ServiceHandler jsonParser = new ServiceHandler();

            SearchReturnedJSON = jsonParser.makeServiceCall(searchURL, ServiceHandler.POST, paramsSearch);

            if(SearchReturnedJSON!=null){

                try {
                    JSONObject searchJSON = new JSONObject(SearchReturnedJSON);

                    Log.i("searchJSON",searchJSON.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_items_search, menu);
        return true;
    }*/

    /*@Override
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
    }*/
}
