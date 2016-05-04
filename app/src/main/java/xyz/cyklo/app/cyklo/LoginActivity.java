package xyz.cyklo.app.cyklo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

public class LoginActivity extends AppCompatActivity {
    String TAG_SUCCESS = "success";
    String TAG_MESSAGE = "message";
    Button register, login;
    String urlSuffix;
    EditText userName, password;
    TextView view,forgetPassword;
    private static final String REGISTER_URL = "http://cyklo.xyz/9999/login/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userName = (EditText) findViewById(R.id.etName);
        password = (EditText) findViewById(R.id.etPassword);
        register = (Button) findViewById(R.id.btnRegister);
        view=(TextView)findViewById(R.id.view);
        forgetPassword=(TextView)findViewById(R.id.forgotPassword);
        login = (Button) findViewById(R.id.btnLogin);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(LoginActivity.this, Register_Activity.class);
                startActivity(i);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forget=new Intent(LoginActivity.this,Forget_Activity.class);
                startActivity(forget);
            }
        });
    }

    public void login() {
        urlSuffix = "?&username="+userName.getText().toString()+"&password="+password.getText().toString();
        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String myJSON) {
                super.onPostExecute(myJSON);
                int success = 0;
                String message = "";

                if (loading != null && loading.isShowing()) {
                    loading.dismiss();
                }

                try {
                    JSONObject jsonObject = new JSONObject(myJSON);
                 //   Toast.makeText(LoginActivity.this, myJSON, Toast.LENGTH_SHORT).show();
                    success = jsonObject.getInt(TAG_SUCCESS);
                    message = jsonObject.getString(TAG_MESSAGE);
                    //view.setText(success + " " + message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (success == 1) {
                    Log.d("Success!", message);
                    Toast.makeText(LoginActivity.this, message,
                            Toast.LENGTH_LONG).show();
                    Intent i =new Intent(LoginActivity.this,MainActivity.class);
                  i.putExtra("email",userName.getText().toString());
                    startActivity(i);

                } else {
                    Log.d("Failure", message);
                    Toast.makeText(LoginActivity.this, message,
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

        RegisterUser ru = new RegisterUser();
        ru.execute();
    }

}





