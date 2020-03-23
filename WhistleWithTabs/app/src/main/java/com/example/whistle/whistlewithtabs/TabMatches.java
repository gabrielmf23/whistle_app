package com.example.whistle.whistlewithtabs;

import android.graphics.Color;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.whistle.whistlewithtabs.Objects.Championships;
import com.example.whistle.whistlewithtabs.Objects.Confederations;
import com.example.whistle.whistlewithtabs.Objects.Countries;
import com.example.whistle.whistlewithtabs.Objects.NextMatch;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TabMatches extends Fragment {
    private static final String TAG = "TabMatchesFragment";

    private TableRow trFilters;
    private TableRow trHiddenFilters;
    private JSONArray jsonArrayAsync;
    private ArrayList<Confederations> confederationsArrayList;
    private ArrayList<Countries> countriesArrayList;
    private ArrayList<Championships> championshipsArrayList;
    private Spinner confederationSpinner;
    private Spinner countriesSpinner;
    private Spinner championshipsSpinner;
    private View viewMatches;
    private JSONObject jsonObject;
    private NextMatch selectedNextMatch;
    private Spinner spCharts;
    private PieChart pieChart;
    private List<String> headers;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (viewMatches == null){
            viewMatches = inflater.inflate(R.layout.tab_matches, container, false);
            viewMatches.findViewById(R.id.constraintLayoutResults).setVisibility(View.INVISIBLE);

            //region Banners
            AdView mAdViewBannerTopMain = viewMatches.findViewById(R.id.adViewTopMatches);
            AdRequest adRequestBannerTop = new AdRequest.Builder().build();
            mAdViewBannerTopMain.loadAd(adRequestBannerTop);

            AdView mAdViewBannerBottomMain = viewMatches.findViewById(R.id.adViewBottomMatches);
            AdRequest adRequestBannerBottom = new AdRequest.Builder().build();
            mAdViewBannerBottomMain.loadAd(adRequestBannerBottom);
            //endregion

            TextView tvNextMatches = viewMatches.findViewById(R.id.tvNextMatches);
            tvNextMatches.setVisibility(View.INVISIBLE);

            //region Hide-Show Filters
            TextView tvHideFilters = viewMatches.findViewById(R.id.tvHideFiltersMatch);
            TextView tvShowFilters = viewMatches.findViewById(R.id.tvShowFiltersMatch);

            trFilters = viewMatches.findViewById(R.id.trFilters);
            trHiddenFilters = viewMatches.findViewById(R.id.trHiddenFilters);
            trHiddenFilters.setVisibility(View.INVISIBLE);

            tvHideFilters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    trFilters.setVisibility(View.GONE);
                    trHiddenFilters.setVisibility(View.VISIBLE);
                }
            });

            tvShowFilters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    trFilters.setVisibility(View.VISIBLE);
                    trHiddenFilters.setVisibility(View.GONE);
                }
            });
            //endregion
        }
        return viewMatches;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewMatches = (View) getView();
    }

    //region Matches

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

    public void getHeaders(String token, String account){
        headers = new ArrayList<>();
        headers.add(token);
        headers.add(account);
    }

    private void populateSpinners(){
        //region Spinners
        //region Spinner Confederations
        confederationSpinner = viewMatches.findViewById(R.id.spinner_conf);

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

                            countriesSpinner = viewMatches.findViewById(R.id.spinner_country);
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
        countriesSpinner = viewMatches.findViewById(R.id.spinner_country);

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
        championshipsSpinner = viewMatches.findViewById(R.id.spinner_champ);
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
                            populateNextMatchesTable(getNextMatchesByChampionship(id));
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

    private class RequestsJSONObject extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... params) {
            jsonObject = JSONFunctions.getJSONObjectFromURLAndHeaders(params[0], headers);
            return jsonObject;
        }
    }

    private ArrayList<NextMatch> getNextMatchesByChampionship(int id){
        JSONArray jsonarray;
        try {
            jsonarray = getJSONArray(true, "NextMatches",
                    "Championship", id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
        ArrayList<NextMatch> tempArrayList = new ArrayList<>();
        try {
            // Locate the NodeList name
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject jsonObject = jsonarray.getJSONObject(i);

                NextMatch nextMatch = new NextMatch();

                nextMatch.setReferee(Integer.parseInt(jsonObject.optString("Referee")));
                nextMatch.setRefereeName(jsonObject.optString("RefereeName"));
                nextMatch.setSelectedTeam(Integer.parseInt(jsonObject.optString(
                        "SelectedTeam")));
                nextMatch.setSelectedTeamName(jsonObject.optString("SelectedTeamName"));
                nextMatch.setAgainstTeam(Integer.parseInt(jsonObject.optString(
                        "AgainstTeam")));
                nextMatch.setAgainstTeamName(jsonObject.optString("AgainstTeamName"));
                nextMatch.setFieldControl(jsonObject.optString("FieldControl"));
                nextMatch.setMatchDate(jsonObject.optString("MatchDate"));
                nextMatch.setChampionship(Integer.parseInt(jsonObject.optString(
                        "Championship")));

                try {
                    tempArrayList.add(nextMatch);
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
        String mainURL = "https://whistleprojectapi2018.azurewebsites.net/api/";
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

    private void populateNextMatchesTable(ArrayList<NextMatch> nextMatches){
        clearLayout();
        ScrollView scrollView = viewMatches.findViewById(R.id.svNextMatches);

        boolean hasRows = false;

        TableLayout tableLayout = new TableLayout(getActivity());
        tableLayout.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT, TableLayout.LayoutParams.FILL_PARENT));
        tableLayout.setWeightSum(1);
        tableLayout.setStretchAllColumns(true);

        for (final NextMatch nextMatch : nextMatches){
            TableRow tableRow = new TableRow(getActivity());
            tableRow.setGravity(Gravity.CENTER);
            tableRow.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT,
                    TableRow.LayoutParams.FILL_PARENT, 1.0f));
            tableRow.setMinimumHeight(80);
            //region Home Team
            TextView tvHome = new TextView(getActivity());
            tvHome.setText(nextMatch.getSelectedTeamName());
            tvHome.setTextSize(15);
            tableRow.addView(tvHome);
            //endregion
            //region x
            TextView textView = new TextView(getActivity());
            textView.setText("x");
            textView.setTextSize(15);
            tableRow.addView(textView);
            //endregion
            //region Away Team
            TextView tvAway = new TextView(getActivity());
            tvAway.setText(nextMatch.getAgainstTeamName());
            tvAway.setTextSize(15);
            tableRow.addView(tvAway);
            //endregion
            //region Date
            TextView tvDate = new TextView(getActivity());
            tvDate.setText(nextMatch.getMatchDate());
            tvDate.setTextSize(15);
            tableRow.addView(tvDate);
            //endregion
            //region Listener
            tableRow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view)
                {
                    viewMatches.findViewById(R.id.constraintLayout).setVisibility(View.INVISIBLE);
                    viewMatches.findViewById(R.id.constraintLayoutResults).setVisibility(View.VISIBLE);
                    selectedNextMatch = nextMatch;
                    TextView tvReturn = viewMatches.findViewById(R.id.tvReturnMatches);
                    tvReturn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view)
                        {
                            viewMatches.findViewById(R.id.constraintLayout).setVisibility(View.VISIBLE);
                            viewMatches.findViewById(R.id.constraintLayoutResults).setVisibility(View.INVISIBLE);
                        }
                    });
                    populateMatchData();
                    try {
                        showCharts(0);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            });
            //endregion
            tableLayout.addView(tableRow);
            hasRows = true;
        }

        scrollView.addView(tableLayout);
        if (hasRows){
            TextView tvNextMatches = viewMatches.findViewById(R.id.tvNextMatches);
            tvNextMatches.setVisibility(View.VISIBLE);
        }
    }

    private void clearLayout(){
        TextView tvNextMatches = viewMatches.findViewById(R.id.tvNextMatches);
        tvNextMatches.setVisibility(View.INVISIBLE);
        ScrollView scrollView = viewMatches.findViewById(R.id.svNextMatches);
        scrollView.removeAllViews();
        //LinearLayout linearLayout = findViewById(R.id.llNextMatches);
        //linearLayout.removeAllViews();
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
        championshipsSpinner = viewMatches.findViewById(R.id.spinner_champ);

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

    //region Results

    private void populateMatchData(){
        //region Set text
        TextView tvHome = viewMatches.findViewById(R.id.tvHomeTeam);
        tvHome.setText(selectedNextMatch.getSelectedTeamName());
        TextView tvAway = viewMatches.findViewById(R.id.tvAwayTeam);
        tvAway.setText(selectedNextMatch.getAgainstTeamName());

        TextView referee = viewMatches.findViewById(R.id.tvReferee);
        referee.setText(selectedNextMatch.getRefereeName());
        TextView date = viewMatches.findViewById(R.id.tvDate);
        date.setText(selectedNextMatch.getMatchDate());

        //TODO put "only for " on label to inform that the statistics will be only based on the selected championship
        ((TextView)(viewMatches.findViewById(R.id.cbOnlyThisChampionship))).setText(
                championshipsSpinner.getSelectedItem().toString());
        //endregion

        populateChartsSpinner();

        CheckBox cb = viewMatches.findViewById(R.id.cbOnlyThisChampionship);
        cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                showCharts(spCharts.getSelectedItemPosition());
            }
        });
    }

    private void populateChartsSpinner(){
        spCharts = viewMatches.findViewById(R.id.spChartList);

        //region Adapter
        //List types of chart
        ArrayList<String> chartsArrayList = new ArrayList<>();
        chartsArrayList.add("All matches");
        chartsArrayList.add("All matches with " + selectedNextMatch.getSelectedTeamName() + " as " +
                "Home Team");
        chartsArrayList.add("All matches with " + selectedNextMatch.getRefereeName());
        chartsArrayList.add("All matches with " + selectedNextMatch.getRefereeName() + " and " +
                selectedNextMatch.getSelectedTeamName() + " as " + "Home Team");

        ArrayAdapter<String> confAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                chartsArrayList
        );
        spCharts.setAdapter(confAdapter);
        //endregion

        //region Listener
        spCharts.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> arg0,
                                       View arg1, int position, long arg3) {
                try
                {
                    //TODO show ad

                    //Add +1 to be aligned with the API switch
                    showCharts(spCharts.getSelectedItemPosition());
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
    }

    private void showCharts(int chartType){
        chartType++;
        if (((CheckBox)(viewMatches.findViewById(R.id.cbOnlyThisChampionship))).isChecked())
            chartType += 4;

        String url = "https://whistleprojectapi2018.azurewebsites.net/api/Statistics/";

        url = url + "id=" + Integer.toString(chartType) +
                "/home=" + Integer.toString(selectedNextMatch.getSelectedTeam()) +
                "/away=" + Integer.toString(selectedNextMatch.getAgainstTeam()) +
                "/referee=" + Integer.toString(selectedNextMatch.getReferee()) +
                "/champ=" + Integer.toString(selectedNextMatch.getChampionship());

        //Request info
        jsonObject = null;
        getData(url);

        if (jsonObject != null){
            getGraphicalView((Integer.parseInt(jsonObject.optString("VictoriesTeamA"))),
                             (Integer.parseInt(jsonObject.optString("VictoriesTeamB"))),
                             (Integer.parseInt(jsonObject.optString("Draws"))));
        }
    }

    private JSONObject getData(String url){
        new RequestsJSONObject().execute(url);
        while (jsonObject == null){
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return jsonObject;
    }

    private void getGraphicalView(int homeVictories, int awayVictories, int draws){
        //region old version
//        CategorySeries series = new CategorySeries("Matches");
//
//        int[] portions = {homeVictories, awayVictories, draws};
//        String[] seriesNames = new String[]{nextMatch.getSelectedTeamName() + " victories ",
//                                            nextMatch.getAgainstTeamName() + " victories ",
//                                            "Draws"};
//
//        for (int i = 0; i < 3; i++){
//            series.add(seriesNames[i], portions[i]);
//        }
//
//        DefaultRenderer defaultRenderer = new DefaultRenderer();
//        SimpleSeriesRenderer simpleSeriesRenderer = null;
//
//        int[] colors = {Color.GREEN, Color.BLUE, Color.GRAY};
//
//        for (int i = 0; i < 3; i++){
//            simpleSeriesRenderer = new SimpleSeriesRenderer();
//            simpleSeriesRenderer.setColor(colors[i]);
//            defaultRenderer.addSeriesRenderer(simpleSeriesRenderer);
//        }
//
//        defaultRenderer.setZoomEnabled(false); //Disable the zoom
//        defaultRenderer.setShowLabels(false); //Remove the labels in the chart
//        defaultRenderer.setPanEnabled(false); //Disable moving/dragging
//        defaultRenderer.setLegendTextSize(30);
//        //defaultRenderer.setScale(1.5f);
//        defaultRenderer.setLegendHeight(500);
//        defaultRenderer.setChartTitleTextSize(0);
//
//
//        return ChartFactory.getPieChartView(DisplayMessageActivity.this, series,
//                                            defaultRenderer);
        //endregion

        List<Integer> _colors = new ArrayList<>();
        List<Integer> portions = new ArrayList<>();
        List<String> names = new ArrayList<>();
        if (homeVictories > 0 ){
            portions.add(homeVictories);
            names.add(selectedNextMatch.getSelectedTeamName() + " victories ");
            _colors.add(Color.rgb(9,9,236));
        }
        if (awayVictories > 0){
            portions.add(awayVictories);
            names.add(selectedNextMatch.getAgainstTeamName() + " victories ");
            _colors.add(Color.rgb(108,236,9));
        }
        if (draws > 0){
            portions.add(draws);
            names.add("Draws");
            _colors.add(Color.rgb(150,150,150));
        }

        //Is null @TODO check why it's null
        pieChart = ((View)viewMatches.getParent()).findViewById(R.id.idPieChartGeneral);
        if (pieChart != null) {
            pieChart.setRotationEnabled(false);
            pieChart.setTransparentCircleAlpha(0);
            pieChart.setDrawCenterText(false);
            pieChart.setHoleRadius(1.0f);

            addDataSet(portions, names, _colors);
        }
    }

    private void addDataSet(List<Integer> portions, List<String> names, List<Integer> _colors){
        ArrayList<PieEntry> yEntries = new ArrayList<>();
        for (int i = 0; i < portions.size(); i++){
            if ((portions.get(i)) > 0) {
                yEntries.add(new PieEntry(portions.get(i), i));
            }
        }

        ArrayList<LegendEntry> legends = new ArrayList<>();
        for (int i = 0; i < _colors.size(); i++) {
            LegendEntry legendEntry = new LegendEntry();
            legendEntry.label = names.get(i);
            legendEntry.formColor = _colors.get(i);
            legends.add(legendEntry);
        }

        PieDataSet pieDataSet = new PieDataSet(yEntries, "");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        ArrayList<Integer> colors = new ArrayList<>();
        for(int c : _colors)
            colors.add(c);

        pieDataSet.setColors(colors);

        pieChart.getLegend().setEnabled(true);
        pieChart.getLegend().setCustom(legends);
        pieChart.getDescription().setEnabled(false);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.invalidate();
    }

    //endregion
}