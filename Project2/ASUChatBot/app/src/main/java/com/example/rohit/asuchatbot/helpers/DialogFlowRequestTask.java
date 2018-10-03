package com.example.rohit.asuchatbot.helpers;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.example.rohit.asuchatbot.interfaces.ResponseInterface;

import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.example.rohit.asuchatbot.constants.AppConstants.AUTHORIZATION;
import static com.example.rohit.asuchatbot.constants.AppConstants.BASE_URL;
import static com.example.rohit.asuchatbot.constants.AppConstants.CLIENT_ACCESS_TOKEN;
import static com.example.rohit.asuchatbot.constants.StringConstants.CONTENT_TYPE;
import static com.example.rohit.asuchatbot.constants.StringConstants.GET;
import static com.example.rohit.asuchatbot.constants.StringConstants.RESULT;
import static com.example.rohit.asuchatbot.constants.StringConstants.SPEECH;

/**
 * Created by Rohit on 03-05-2018.
 */

public class DialogFlowRequestTask extends AsyncTask<String, Void, String> {

    private ProgressDialog pDialog;
    private Context context;
    private ResponseInterface delegate = null;

    public DialogFlowRequestTask(Context context){
        this.context = context;
        delegate = (ResponseInterface) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        return getDialogFlowResponse(params[0], params[1]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        pDialog.dismiss();
        if(delegate!=null) {
            delegate.updateResponse(s);
        }
    }


    private String getDialogFlowResponse(String question, String sessionId) {
        String url = BASE_URL + question.replace(" ", "%20") + "&sessionId=" + sessionId;
        String answer = "";
        try {
            URL getIntentUrl = new URL(url);
            HttpURLConnection urlConnection = (HttpURLConnection) getIntentUrl.openConnection();
            urlConnection.setRequestMethod(GET);
            urlConnection.setRequestProperty(AUTHORIZATION, CLIENT_ACCESS_TOKEN);
            urlConnection.setRequestProperty(CONTENT_TYPE, "application/json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String inputLine = "";
            StringBuilder response = new StringBuilder();
            while ((inputLine = bufferedReader.readLine()) != null) {
                response.append(inputLine);
            }
            bufferedReader.close();
            JSONParser parser = new JSONParser();
            org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(response.toString());
            org.json.simple.JSONObject result = (org.json.simple.JSONObject) parser.parse(json.get(RESULT).toString());
            answer = result.get(SPEECH).toString();
        } catch (Exception e) {
            System.out.println("Exception while getting the Intent details: " + e.getMessage());
        }
        return answer;
    }

}
