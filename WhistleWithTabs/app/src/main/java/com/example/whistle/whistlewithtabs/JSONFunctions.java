package com.example.whistle.whistlewithtabs;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class JSONFunctions {

    //region Old methods
    public static JSONArray getJSONfromURL(String pagina)
    {
        String result = mountJSON(pagina);

        JSONArray jArray = new JSONArray();
        try
        {
            jArray = new JSONArray(result);
        }
        catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return jArray;
    }

    public static JSONObject getJSONObjectFromURL(String pagina)
    {
        String result = mountJSON(pagina);
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = new JSONObject(result);
        }
        catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return jsonObject;
    }

    private static String mountJSON(String pagina){
        String result = "";
        int status;
        InputStream is = null;

        try {
            URL url = new URL(pagina);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.connect();

            status = httpsConn.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK)
                is = httpsConn.getInputStream();
            else
                is = httpsConn.getErrorStream();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // Convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        return result;
    }
    //endregion

    //region Requests w/ Headers

    //region Post

    public static JSONObject getJSONObjectFromURLAndHeader(String pagina, String header)
    {
        String result = mountJSONWithHeader(pagina, header);
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = new JSONObject(result);
        }
        catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return jsonObject;
    }

    private static String mountJSONWithHeader(String pagina, String header){
        String result = "";
        int status;
        InputStream is = null;

        try {
            URL url = new URL(pagina);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("POST");
            httpsConn.setRequestProperty("gToken", header);
            httpsConn.connect();

            status = httpsConn.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK)
                is = httpsConn.getInputStream();
            else
                is = httpsConn.getErrorStream();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // Convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        return result;
    }

    //endregion

    //region Get
    public static JSONArray getJSONFromURLAndHeaders(String url, List<String> headers)
    {
        String result = mountJSONWithHeaders(url, headers);

        JSONArray jArray = new JSONArray();
        try
        {
            jArray = new JSONArray(result);
        }
        catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return jArray;
    }

    public static JSONObject getJSONObjectFromURLAndHeaders(String pagina, List<String> headers)
    {
        String result = mountJSONWithHeaders(pagina, headers);
        JSONObject jsonObject = new JSONObject();
        try
        {
            jsonObject = new JSONObject(result);
        }
        catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }
        return jsonObject;
    }

    private static String mountJSONWithHeaders(String _url, List<String> headers){
        String result = "";
        int status;
        InputStream is = null;

        try {
            URL url = new URL(_url);
            URLConnection urlConn = url.openConnection();

            HttpsURLConnection httpsConn = (HttpsURLConnection) urlConn;
            httpsConn.setAllowUserInteraction(false);
            httpsConn.setInstanceFollowRedirects(true);
            httpsConn.setRequestMethod("GET");
            httpsConn.setRequestProperty("gToken", headers.get(0));
            httpsConn.setRequestProperty("account", headers.get(1));
            httpsConn.connect();

            status = httpsConn.getResponseCode();

            if (status == HttpURLConnection.HTTP_OK)
                is = httpsConn.getInputStream();
            else
                is = httpsConn.getErrorStream();

        } catch (Exception e) {
            Log.e("log_tag", "Failed connection: " + e.toString());
        }

        // Convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        return result;
    }
    //endregion
    //endregion
}