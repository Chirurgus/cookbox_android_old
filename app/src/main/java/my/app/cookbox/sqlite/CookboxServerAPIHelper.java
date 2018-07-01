package my.app.cookbox.sqlite;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.recipe.RecipeTag;

import static android.content.ContentValues.TAG;

public class CookboxServerAPIHelper {
    public CookboxServerAPIHelper(String url) {
        this.mURL = url;
    }

    public JSONObject get(long id) {
        return null;
    }

    public void put(Recipe recipe) {

    }

    public JSONObject get_tag(long id) {
        return null;
    }

    public JSONObject  put_tag(RecipeTag tag) {
        return null;
    }

    public  JSONObject sync(String sync_token) {
        final String uri = "/recipe/sync";

        try {
            URL url = new URL(mURL + uri);

            mConnection = (HttpURLConnection) url.openConnection();

            mConnection.setRequestMethod("GET");
            mConnection.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
            mConnection.setRequestProperty("Accept", "application/json");
            //mConnection.setDoOutput(true);
            mConnection.setDoInput(true);


            if (sync_token != null) {
                JSONObject token = new JSONObject();
                token.put("token", sync_token);

                DataOutputStream os = new DataOutputStream(mConnection.getOutputStream());

                os.writeBytes(token.toString());

                os.flush();
                os.close();
            }

            int response_code = mConnection.getResponseCode();
            if (response_code != 200) {
                throw new Exception("Recieved a non sucess code:" + response_code);
            }
            InputStream is = mConnection.getInputStream();
            return makeJson(is);
        }
        catch (Exception err) {
            Log.e(TAG, "sync: " + err.getMessage());
        }
        return null;
    }

    String mURL;
    HttpURLConnection mConnection = null;

    private JSONObject makeJson(InputStream is) throws JSONException, IOException {
        StringBuilder sb = new StringBuilder();

        String line;
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        return new JSONObject(sb.toString());
    }
}

/*
public void sendPost() {
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            try {
                URL url = new URL(urlAdress);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("timestamp", 1488873360);
                jsonParam.put("uname", message.getUser());
                jsonParam.put("message", message.getMessage());
                jsonParam.put("latitude", 0D);
                jsonParam.put("longitude", 0D);

                Log.i("JSON", jsonParam.toString());
                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                //os.writeBytes(URLEncoder.encode(jsonParam.toString(), "UTF-8"));
                os.writeBytes(jsonParam.toString());

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    });

    thread.start();
}
 */