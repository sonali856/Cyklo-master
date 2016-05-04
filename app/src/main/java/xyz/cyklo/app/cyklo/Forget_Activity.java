package xyz.cyklo.app.cyklo;



import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Forget_Activity extends AppCompatActivity {
    Button forget;
    EditText email;
    private static final String REGISTER_URL = "http://cyklo.xyz/9999/login/forget.php";
    private static String urlSuffix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);
        forget=(Button)findViewById(R.id.forgetPass);
        email=(EditText)findViewById(R.id.uuserName);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                forgetPassword();
            }
        });
    }

    void forgetPassword(){
        urlSuffix = "?&email_id="+email.getText().toString();
        class ForgetUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Forget_Activity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String myJSON) {
                super.onPostExecute(myJSON);
                int success = 0;
                String message = null;

                if (loading != null && loading.isShowing()) {
                    loading.dismiss();
                }

                try {
                    JSONObject jsonObject = new JSONObject(myJSON);
                    success = jsonObject.getInt("success");
                    //  Toast.makeText(ForgetActivity.this, myJSON, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (success == 1) {

                    Toast.makeText(Forget_Activity.this,"check mail",
                            Toast.LENGTH_LONG).show();
                } else {

                    Toast.makeText(Forget_Activity.this, "try again",
                            Toast.LENGTH_LONG).show();
                }

            }


            //   URL url = new URL(REGISTER_URL+s);
            //   HttpURLConnection con = (HttpURLConnection) url.openConnection();
            @Override
            protected String doInBackground(String... args) {

                DefaultHttpClient httpclient = new DefaultHttpClient(new BasicHttpParams());
                HttpPost httppost = new HttpPost((REGISTER_URL + urlSuffix));

                // Depends on your web service
                httppost.setHeader("Content-type", "application/json");

                InputStream inputStream = null;
                String result = null;
                try {
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();

                    inputStream = entity.getContent();
                    // json is UTF-8 by default
                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
                    StringBuilder sb = new StringBuilder();

                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    result = sb.toString();
                } catch (Exception e) {
                    // Oops
                } finally {
                    try {
                        if (inputStream != null) inputStream.close();
                    } catch (Exception squish) {
                    }
                }
                return result;
            }
        }

        ForgetUser ru = new ForgetUser();
        ru.execute();
    }
}
