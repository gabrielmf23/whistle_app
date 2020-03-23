package com.example.whistle.whistlewithtabs;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.whistle.whistlewithtabs.Objects.Championships;
import com.example.whistle.whistlewithtabs.Objects.Confederations;
import com.example.whistle.whistlewithtabs.Objects.Countries;
import com.example.whistle.whistlewithtabs.Objects.Team;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class TabTeams extends Fragment {
    public static final String TAG = "TabTeamsFragment";

    private TableRow trFiltersTeams;
    private TableRow trHiddenFiltersTeams;

    private String mainURL = "https://whistleprojectapi2018.azurewebsites.net/api/";
    private JSONObject jsonObject;
    private JSONArray jsonarray;
    private JSONArray jsonArrayAsync;
    private ArrayList<Confederations> confederationsArrayList;
    private ArrayList<Countries> countriesArrayList;
    private ArrayList<Championships> championshipsArrayList;
    private Spinner confederationSpinner;
    private Spinner countriesSpinner;
    private Spinner championshipsSpinner;
    private Team selectedTeam;
    private View viewTeams;

    private SharedPreferences userDetails = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (viewTeams == null){
            viewTeams = inflater.inflate(R.layout.tab_teams, container, false);

            //region Banners
            AdView mAdViewBannerTopMain = viewTeams.findViewById(R.id.adViewTopTeams);
            AdRequest adRequestBannerTop = new AdRequest.Builder().build();
            mAdViewBannerTopMain.loadAd(adRequestBannerTop);

//            AdView mAdViewBannerBottomMain = viewTeams.findViewById(R.id.adViewBottomTeams);
//            AdRequest adRequestBannerBottom = new AdRequest.Builder().build();
//            mAdViewBannerBottomMain.loadAd(adRequestBannerBottom);
            //endregion

            TextView tvTeams = viewTeams.findViewById(R.id.tvTeams);
            tvTeams.setVisibility(View.INVISIBLE);

            //region Hide-Show Filters
            ImageView imageViewCollapseFiltersTeams = viewTeams.findViewById(
                    R.id.imageViewCollapseFiltersTeams);
            ImageView imageViewShowFiltersTeams = viewTeams.findViewById(
                    R.id.imageViewShowFiltersTeams);

            trFiltersTeams = viewTeams.findViewById(R.id.trFiltersTeams);
            trHiddenFiltersTeams = viewTeams.findViewById(R.id.trHiddenFiltersTeams);
            trHiddenFiltersTeams.setVisibility(View.INVISIBLE);



            imageViewCollapseFiltersTeams.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    trFiltersTeams.setVisibility(View.GONE);
                    trHiddenFiltersTeams.setVisibility(View.VISIBLE);
                }
            });

            imageViewShowFiltersTeams.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    trFiltersTeams.setVisibility(View.VISIBLE);
                    trHiddenFiltersTeams.setVisibility(View.GONE);
                }
            });
            //endregion
        }
        return viewTeams;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewTeams = (View) getView();
    }

    //region Filter Teams

    public void getLists(List<ArrayList> list) {
        if (list != null) {
            if (list.size() > 0){
                confederationsArrayList = (ArrayList<Confederations>) list.get(0);
                countriesArrayList = (ArrayList<Countries>) list.get(1);
                championshipsArrayList = (ArrayList<Championships>) list.get(2);
                populateSpinners();
            }
        }
    }

    private void populateSpinners(){

        //region Spinners
        //region Spinner Confederations
        confederationSpinner = viewTeams.findViewById(R.id.spinner_confTeams);

        //region Adapter
        ArrayAdapter<Confederations> confAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                confederationsArrayList
        );
        confederationSpinner.setAdapter(confAdapter);
        //endregion

        //region Listener
        confederationSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {
                        try
                        {
                            Confederations confederations =
                                    (Confederations) confederationSpinner.getSelectedItem();
                            ArrayList<Countries> list;

                            //region Update Countries Spinner

                            if (confederations.getId() == 0){
                                list = countriesArrayList;
                            }
                            else{
                                list = mountCountriesLists(confederations.getId());
                            }

                            countriesSpinner = viewTeams.findViewById(R.id.spinner_countryTeams);
                            ArrayAdapter<Countries> countriesAdapter = new ArrayAdapter<>(
                                    getActivity(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    list
                            );
                            countriesSpinner.setAdapter(countriesAdapter);

                            //endregion

                            if (confederations.getId() != 1){
                                countriesSpinner.setEnabled(true);
                                prepareChampionshipSpinner(false, 0,
                                        0);
                            }
                            else{
                                countriesSpinner.setEnabled(false);
                            }
                            clearLayout();
                        }
                        catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
        //endregion

        //endregion

        //region Spinner Countries
        countriesSpinner = viewTeams.findViewById(R.id.spinner_countryTeams);

        //region Adapter
        final ArrayAdapter<Countries> countriesAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                countriesArrayList
        );
        countriesSpinner.setAdapter(countriesAdapter);
        //endregion

        //region Listener
        countriesSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {

                        try {
                            boolean championshipSpinnerEnable = false;
                            Countries countries =
                                    (Countries) countriesSpinner.getSelectedItem();
                            if (countries.getId() != 0){
                                try {
                                    Countries select = (Countries) countriesSpinner.getAdapter().getItem(0);
                                }catch (Exception e) {
                                    e.printStackTrace();
                                }
                                championshipSpinnerEnable = true;
                            }

                            prepareChampionshipSpinner(championshipSpinnerEnable,
                                    countries.getId(), countries.getIdConfederation());
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
        //endregion
        //endregion

        //region Spinner Championships
        championshipsSpinner = viewTeams.findViewById(R.id.spinner_champTeams);
        //Enable only if a country is selected
        championshipsSpinner.setEnabled(false);

        //region Listener
        championshipsSpinner
                .setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                    @Override
                    public void onItemSelected(AdapterView<?> arg0,
                                               View arg1, int position, long arg3) {
                        int id = ((Championships) championshipsSpinner.getSelectedItem()).getId();

                        if (id > 0){
                            populateTeamsTable(getTeamsByChampionship(id));
                        }
                        else{
                            clearLayout();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> arg0) {
                        // TODO Auto-generated method stub
                    }
                });
        //endregion
        //endregion
        //endregion
    }

    private class Requests extends AsyncTask<String, Void, JSONArray>{
        @Override
        protected JSONArray doInBackground(String... params) {
            jsonArrayAsync = JSONFunctions.getJSONfromURL(params[0]);

            return jsonArrayAsync;
        }
    }

    //region Old Method
    /*private ArrayList getSpinnerItem(Class classOf, String jsonAttribute, String controller) {
        try {
            jsonarray = getJSONArray(false, controller, "", 0);
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
                if (i == 0){
                    BaseEntity object1 = (BaseEntity) classOf.newInstance();

                    object1.setId(0);
                    if (object1 instanceof Confederations){
                        object1.setName("All Countries");
                    }
                    else{
                        if (object1 instanceof Countries){
                            object1.setName("Select a country");
                        }
                        else{
                            object1.setName("Select a championship");
                        }
                    }
                    try {
                        tempArrayList.add(object1);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
                //endregion

                BaseEntity object = (BaseEntity) classOf.newInstance();

                object.setId(Integer.parseInt(jsonObject.optString("ID")));
                object.setName(jsonObject.optString(jsonAttribute));

                if (object instanceof Countries){
                    ((Countries) object).setIdConfederation(Integer.parseInt(
                            jsonObject.optString("Confederation")));
                }
                if (object instanceof Championships){
                    ((Championships) object).setIdCountry(Integer.parseInt(
                            jsonObject.optString("Country")));
                }
                try {
                    tempArrayList.add(object);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            return tempArrayList;
        } catch (Exception e) {
            // Log.e("Error", e.getMessage());
            e.printStackTrace();
            return new ArrayList();
        }
    }*/
    //endregion

    private ArrayList<Team> getTeamsByChampionship(int id){
        try {
            jsonarray = getJSONArray(true, "Teams",
                    "Championship", id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        ArrayList<Team> tempArrayList = new ArrayList<>();
        try {
            // Locate the NodeList name
            for (int i = 0; i < jsonarray.length(); i++) {
                jsonObject = jsonarray.getJSONObject(i);

                Team team = new Team();

                team.setChampionship(id);
                team.setTeam(Integer.parseInt(jsonObject.optString("ID")));
                team.setTeamName(jsonObject.optString("TeamName"));
                try {
                    tempArrayList.add(team);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
            //need to clean the var, to avoid show "in memory" values
            jsonArrayAsync = null;
            return tempArrayList;
        } catch (Exception e) {
            // Log.e("Error", e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private JSONArray getJSONArray(boolean async, String controller, String action, int id){
        String url = String.format("%s%s/", mainURL, controller);

        if (!action.isEmpty()) {
            url = String.format("%s%s/", url, action);
        }

        if (id > 0) {
            url = String.format("%s%s/", url, Integer.toString(id));
        }

        try {
            if (async) {
                new Requests().execute(url);
                while (jsonArrayAsync == null){
                    Thread.sleep(1000);
                }
                return jsonArrayAsync;
            }
            return JSONFunctions.getJSONfromURL(url);
        } catch(Exception e){
            e.printStackTrace();
            return new JSONArray();
        }
    }

    private void populateTeamsTable(ArrayList<Team> teams){
        clearLayout();
        ScrollView scrollView = viewTeams.findViewById(R.id.svTeams);

        boolean hasRows = false;

        TableLayout tableLayout = new TableLayout(getActivity());
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT));
        tableLayout.setWeightSum(1);
        tableLayout.setStretchAllColumns(true);

        for (final Team team : teams){
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setGravity(Gravity.CENTER);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT, 1.0f));
            tableRow.setMinimumHeight(80);
            //region Team
            TextView tvTeam = new TextView(getActivity());
            tvTeam.setText(team.getTeamName());
            tvTeam.setTextSize(15);
            tableRow.addView(tvTeam);
            //endregion
            //region Listener
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    viewTeams.findViewById(R.id.constraintLayoutTeams).setVisibility(View.INVISIBLE);
                    viewTeams.findViewById(R.id.constraintLayoutTeamData).setVisibility(View.VISIBLE);

                    selectedTeam = team;
                    TextView tvReturn = viewTeams.findViewById(R.id.tvReturnTeams);
                    tvReturn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            viewTeams.findViewById(R.id.constraintLayoutTeams).setVisibility(View.VISIBLE);
                            viewTeams.findViewById(R.id.constraintLayoutTeamData).setVisibility(View.INVISIBLE);
                        }
                    });
                    populateTeamData();
                }
            });
            //endregion
            tableLayout.addView(tableRow);
            hasRows = true;
        }

        scrollView.addView(tableLayout);
        if (hasRows){
            TextView tvTeams = viewTeams.findViewById(R.id.tvTeams);
            tvTeams.setVisibility(View.VISIBLE);
        }
    }

    private void clearLayout(){
        TextView tvNextMatches = viewTeams.findViewById(R.id.tvTeams);
        tvNextMatches.setVisibility(View.INVISIBLE);
        ScrollView scrollView = viewTeams.findViewById(R.id.svTeams);
        scrollView.removeAllViews();
    }

    private ArrayList<Countries> mountCountriesLists(int id){
        try {
            ArrayList<Countries> auxCountriesList = new ArrayList<>();
            if (id > 1) {
                Countries select = new Countries();
                select.setId(0);
                select.setName("Select a country");
                auxCountriesList.add(select);
                for (Countries country : countriesArrayList) {
                    if (country.getIdConfederation() == id && country.getId() != id) {
                        auxCountriesList.add(country);
                    }
                }
            }
            else {
                Countries fifa = new Countries();
                fifa.setId(1);
                fifa.setName("FIFA");
                fifa.setIdConfederation(1);
                auxCountriesList.add(fifa);
            }
            return auxCountriesList;
        } catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private ArrayList<Championships> mountChampionshipsLists(int idCountry, int idConfederation){
        try {
            ArrayList<Championships> auxChampionshipList = new ArrayList<>();
            for (Championships championships : championshipsArrayList) {
                if (championships.getIdCountry() == idCountry || championships.getId() == 0 ||
                        championships.getIdCountry() == idConfederation) {
                    auxChampionshipList.add(championships);
                }
            }
            return auxChampionshipList;
        } catch (Exception e){
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private void prepareChampionshipSpinner(boolean enable, int idCountry, int idConfederation){
        championshipsSpinner = viewTeams.findViewById(R.id.spinner_champTeams);

        ArrayList<Championships> auxChampsList = new ArrayList<>();
        if (enable){
            auxChampsList = mountChampionshipsLists(idCountry, idConfederation);
        }

        ArrayAdapter<Championships> champAdapter = new ArrayAdapter<>(
                getActivity(),
                android.R.layout.simple_spinner_dropdown_item,
                auxChampsList
        );
        championshipsSpinner.setAdapter(champAdapter);
        championshipsSpinner.setEnabled(enable);
    }

    //endregion

    //region Team Data
    private void populateTeamData(){
        //region Set text
        TextView tvCompleteName = viewTeams.findViewById(R.id.tvCompleteName);
        tvCompleteName.setText(selectedTeam.getTeamName());
        tvCompleteName.setTextSize(24);

//        TextView referee = viewTeams.findViewById(R.id.tvReferee);
//        referee.setText(selectedNextMatch.getRefereeName());
//        TextView date = viewTeams.findViewById(R.id.tvDate);
//        date.setText(selectedNextMatch.getMatchDate());
        //endregion

        userDetails = this.getActivity().getSharedPreferences("details", MODE_PRIVATE);

        //region Favorite Team
        int userFavTeam = userDetails.getInt("favTeam", 0);

        final ImageView favIconOff = viewTeams.findViewById(R.id.imageViewFavoriteOff);
        final ImageView favIconOn = viewTeams.findViewById(R.id.imageViewFavoriteOn);

        if (userFavTeam == selectedTeam.getTeam()){
            favIconOn.setVisibility(View.VISIBLE);
            favIconOff.setVisibility(View.INVISIBLE);
        }
        else{
            favIconOn.setVisibility(View.INVISIBLE);
            favIconOff.setVisibility(View.VISIBLE);
        }

        favIconOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favIconOn.setVisibility(View.VISIBLE);
                favIconOff.setVisibility(View.INVISIBLE);
                userDetails.edit().putInt("favTeam", selectedTeam.getTeam()).apply();
            }
        });

        favIconOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favIconOff.setVisibility(View.VISIBLE);
                favIconOn.setVisibility(View.INVISIBLE);
                userDetails.edit().putInt("favTeam", 0).apply();
            }
        });
        //endregion

        //populateChartsSpinner();
    }
    //endregion
}