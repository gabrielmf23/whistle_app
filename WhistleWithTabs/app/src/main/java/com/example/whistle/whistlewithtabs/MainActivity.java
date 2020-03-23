package com.example.whistle.whistlewithtabs;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;

import com.example.whistle.whistlewithtabs.Objects.BaseEntity;
import com.example.whistle.whistlewithtabs.Objects.Championships;
import com.example.whistle.whistlewithtabs.Objects.Confederations;
import com.example.whistle.whistlewithtabs.Objects.Countries;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class  MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    String mainURL = "https://whistleprojectapi2018.azurewebsites.net/api/";
    ArrayList<Confederations> confederationsArrayList;
    ArrayList<Countries> countriesArrayList;
    ArrayList<Championships> championshipsArrayList;
    List<ArrayList> lists = new ArrayList<>();
    JSONObject jsonObject;
    JSONArray jsonarray;
    JSONArray jsonArrayAsync;
    SectionsPageAdapter adapter;
    ViewPager mViewPager;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        mViewPager = findViewById(R.id.container);
        setupViewPager(mViewPager);

        //Keep the
        mViewPager.setOffscreenPageLimit(7);

        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        if (tabLayout.getTabAt(3) != null)
            tabLayout.getTabAt(3).setIcon(R.drawable.ic_menu_settings);

        new DownloadJSON().execute();
    }

    private void setupViewPager(ViewPager viewPager){
        adapter = new SectionsPageAdapter(getSupportFragmentManager());
        adapter.addFragment(new TabMatches(), "Matches");
        adapter.addFragment(new TabTeams(), "Teams");
        adapter.addFragment(new TabReferees(), "Referees");
        adapter.addFragment(new TabSettings(), "");

        viewPager.setAdapter(adapter);
    }

    private class DownloadJSON extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            //region Populate lists
            confederationsArrayList = getSpinnerItem(Confederations.class,
                    "ConfederationName", "Confederations");

            lists.add(confederationsArrayList);

            countriesArrayList = getSpinnerItem(Countries.class,
                    "CountryName", "Countries");

            lists.add(countriesArrayList);

            championshipsArrayList = getSpinnerItem(Championships.class,
                    "ChampionshipName", "Championships");

            lists.add(championshipsArrayList);
            //endregion
            return null;
        }

        @Override
        protected void onPostExecute(Void args) {
            TabMatches matches = (TabMatches) adapter.getItem(0);
            matches.getLists(lists);
            matches.getHeaders(getIntent().getStringExtra("token"),
                    getIntent().getStringExtra("account"));

            TabTeams teams = (TabTeams) adapter.getItem(1);
            teams.getLists(lists);

//            TabReferees referees = (TabReferees) adapter.getItem(2);
//            referees.getLists(lists);
        }

    }

    private ArrayList getSpinnerItem(Class classOf, String jsonAttribute, String controller) {
        try {
            jsonarray = getJSONArray(controller, "", 0);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        ArrayList tempArrayList = new ArrayList<>();
        try {
            // Locate the NodeList name
            for (int i = 0; i < jsonarray.length(); i++) {
                jsonObject = jsonarray.getJSONObject(i);

                //region Add First item to a spinner to avoid fire other events
                if (i == 0) {
                    BaseEntity object1 = (BaseEntity) classOf.newInstance();

                    object1.setId(0);
                    if (object1 instanceof Confederations) {
                        object1.setName("All Countries");
                    } else {
                        if (object1 instanceof Countries) {
                            object1.setName("Select a country");
                        } else {
                            object1.setName("Select a championship");
                        }
                    }
                    try {
                        tempArrayList.add(object1);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                //endregion

                BaseEntity object = (BaseEntity) classOf.newInstance();

                object.setId(Integer.parseInt(jsonObject.optString("ID")));
                object.setName(jsonObject.optString(jsonAttribute));

                if (object instanceof Countries) {
                    ((Countries) object).setIdConfederation(Integer.parseInt(
                            jsonObject.optString("Confederation")));
                }
                if (object instanceof Championships) {
                    ((Championships) object).setIdCountry(Integer.parseInt(
                            jsonObject.optString("Country")));
                }
                try {
                    tempArrayList.add(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return tempArrayList;
        } catch (Exception e) {
            // Log.e("Error", e.getMessage());
            e.printStackTrace();
            return new ArrayList();
        }
    }

    private JSONArray getJSONArray(String controller, String action, int id){
        String url = String.format("%s%s/", mainURL, controller);

        if (!action.isEmpty()) {
            url = String.format("%s%s/", url, action);
        }

        if (id > 0) {
            url = String.format("%s%s/", url, Integer.toString(id));
        }

        try {
//            if (async) {
//                new Requests().execute(url);
//                while (jsonArrayAsync == null){
//                    Thread.sleep(1000);
//                }
//                return jsonArrayAsync;
//            }


            List<String> headers = new ArrayList<>();
            headers.add(getIntent().getStringExtra("token"));
            headers.add(getIntent().getStringExtra("account"));
            return JSONFunctions.getJSONFromURLAndHeaders(url, headers);
        } catch(Exception e){
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private class Requests extends AsyncTask<String, Void, JSONArray>{
        @Override
        protected JSONArray doInBackground(String... params) {
            List<String> headers = new ArrayList<>();
            headers.add(getIntent().getStringExtra("token"));
            headers.add(getIntent().getStringExtra("account"));
            jsonArrayAsync = JSONFunctions.getJSONFromURLAndHeaders(params[0], headers);

            return jsonArrayAsync;
        }
    }
}
