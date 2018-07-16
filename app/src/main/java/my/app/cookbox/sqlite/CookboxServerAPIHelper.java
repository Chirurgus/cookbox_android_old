package my.app.cookbox.sqlite;

import android.util.Log;
import android.util.Pair;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import my.app.cookbox.recipe.Recipe;
import my.app.cookbox.recipe.RecipeTag;

import static android.content.ContentValues.TAG;

public class CookboxServerAPIHelper {
    public CookboxServerAPIHelper(String url) {
        this.mURL = url;
    }

    public JSONObject get_recipe(long id) {
        final String uri = "/recipe/" + id;
        return getJson(uri);
    }

    public void put_recipe(JSONObject recipe) {
        final String uri = "/recipe";
        putJson(uri, recipe);
    }

    public JSONObject get_tag(long id) {
        final String uri = "/recipe/tag/" + id;
        return getJson(uri);
    }

    public void put_tag(RecipeTag tag) {
        final String uri = "/recipe/tag";
        putJson(uri, new JSONObject());
    }

    public JSONObject get_schema(long db_version) {
        final String uri = "/recipe/schema/schema/" + db_version;
        return getJson(uri);
    }

    public JSONObject get_migration(long db_version) {
        final String uri = "/recipe/schema/migration/" + db_version;
        return getJson(uri);
    }

    public  JSONObject sync(String sync_token) {
        String uri = "/recipe/sync";
        if (sync_token != null) {
            uri = uri + "/" + sync_token;
        }
        return getJson(uri);
    }

    String mURL;
    HttpURLConnection mConnection = null;

    private void deleteJSON(String uri) {
         try {
            URL url = new URL(mURL + uri);

            mConnection = (HttpURLConnection) url.openConnection();

            mConnection.setDoOutput(false);
            mConnection.setDoInput(false);
            mConnection.setRequestMethod("DELETE");

            int response_code = mConnection.getResponseCode();
            if (response_code != 200) {
                throw new Exception("Recieved a non sucess code:" + response_code);
            }
        }
        catch (Exception err) {
            Log.e(TAG, "delete: " + err.getMessage());
        }
    }

    private JSONObject  postJSON(String uri, JSONObject json) {
         try {
            URL url = new URL(mURL + uri);

            mConnection = (HttpURLConnection) url.openConnection();

            mConnection.setDoOutput(true);
            mConnection.setRequestMethod("POST");

            OutputStreamWriter os = new OutputStreamWriter(
                   mConnection.getOutputStream()
            );
            os.write(json.toString());
            os.close();

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

    private JSONObject putJson(String uri, JSONObject json) {
         try {
            URL url = new URL(mURL + uri);

            mConnection = (HttpURLConnection) url.openConnection();

            mConnection.setDoOutput(true);
            mConnection.setRequestMethod("PUT");

            OutputStreamWriter os = new OutputStreamWriter(
                   mConnection.getOutputStream()
            );
            os.write(json.toString());
            os.close();

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

    private JSONObject getJson(String uri) {
        try {
            URL url = new URL(mURL + uri);

            mConnection = (HttpURLConnection) url.openConnection();

            mConnection.setDoInput(true);
            mConnection.setRequestMethod("GET");
            mConnection.setRequestProperty("Accept", "application/json");

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