package scout.scoutmobile.utils;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ServerQuery {

    private HttpClient client;
    private HttpPost postRequest;

    public ServerQuery(String url) {
        client = new DefaultHttpClient();
        postRequest = new HttpPost(url);
    }

    public void setPostWithData(ArrayList<NameValuePair> data) {
        InputStream buffer = null;
        BufferedReader reader = new BufferedReader(new InputStreamReader(buffer));
        StringBuilder strBuilder = new StringBuilder();
        JSONObject userJson;
        try {
            // Add your data
//            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
//            nameValuePairs.add(new BasicNameValuePair("id", "12345"));
//            nameValuePairs.add(new BasicNameValuePair("stringdata", "AndDev is Cool!"));
//            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = client.execute(postRequest);
            buffer = response.getEntity().getContent();

            String line = null;
            while ((line = reader.readLine()) != null) {
                strBuilder.append(line + '\n');
            }

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
        } catch (IOException e) {
            // TODO Auto-generated catch block
        } finally {
            try {
                buffer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            userJson = new JSONObject(strBuilder.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


}
