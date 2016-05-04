package xyz.cyklo.app.cyklo;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Register_Activity extends Activity implements AdapterView.OnItemSelectedListener
{    Bitmap bm;
    int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    int result;
    Button btnSelect;
    ImageView ivImage;
    String collegeName,test1,error;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextEmail;
    private Spinner college;
    private EditText phoneNo;
    private TextView test,sa;
    private Button buttonRegister;
    ArrayAdapter<CharSequence> adapter;
    private static final String REGISTER_URL = "http://cyklo.xyz/9999/login/register.php";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        college = (Spinner) findViewById(R.id.spinCollege);
        editTextUsername = (EditText) findViewById(R.id.etUserName);
        editTextPassword = (EditText) findViewById(R.id.etPassword);
        editTextEmail = (EditText) findViewById(R.id.etEmail);
        phoneNo = (EditText) findViewById(R.id.etNumber);
        buttonRegister = (Button) findViewById(R.id.btnSave);
        test=(TextView)findViewById(R.id.test);
        sa=(TextView)findViewById(R.id.s);
        college.setOnItemSelectedListener(this);
        adapter = ArrayAdapter.createFromResource(this,
                R.array.college_names, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
        college.setAdapter(adapter);

        btnSelect = (Button) findViewById(R.id.btnSelectPhoto);
        btnSelect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) findViewById(R.id.ivImage);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v == buttonRegister) {
                    registerUser();
                }
            }
        });


    }



    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        collegeName = parent.getItemAtPosition(position).toString();

    }
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(Register_Activity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals("Choose from Library")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, "Select File"),
                            SELECT_FILE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        bm = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(bm);
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaStore.MediaColumns.DATA };
        Cursor cursor = managedQuery(selectedImageUri, projection, null, null,
                null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ivImage.setImageBitmap(bm);
    }


    private void registerUser() {

        String username = editTextUsername.getText().toString().trim().toLowerCase();
        String password = editTextPassword.getText().toString().trim().toLowerCase();
        String email = editTextEmail.getText().toString().trim().toLowerCase();
        String phone = phoneNo.getText().toString();
        // String image= getStringImage(bm);
        register(username, password, collegeName, email, phone);
    }
    /* public String getStringImage(Bitmap bmp){
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
         byte[] imageBytes = baos.toByteArray();
         String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);

         return Uri.encode(encodedImage);
     }*/
    private void register(String username, String password,String college,String email,String phone) {


        final String urlSuffix = "?&username="+username+"&password="+password+"&college="+college+"&email_id="+email+"&phone_id="+phone+"&image=78r76456";
        class RegisterUser extends AsyncTask<String, Void, String> {

            ProgressDialog loading;
            String TAG_SUCCESS="success";
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Register_Activity.this, "Please Wait",null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                test.setText(test1);
                loading.dismiss();

                try {

                    JSONObject jsonObject = new JSONObject(s);
                    // sa.setText(s);
                    result = jsonObject.getInt(TAG_SUCCESS);
                    error = jsonObject.getString("error");
                    if (error!=null) {
                        if (result == 1)
                            Toast.makeText(Register_Activity.this, "Check Email to Verify", Toast.LENGTH_LONG).show();
                        else
                            Toast.makeText(Register_Activity.this, "Sorry , Try Again ", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        Toast.makeText(Register_Activity.this,error,Toast.LENGTH_SHORT).show();
                    }
                }catch (Exception e) {

                }

            }
            @Override
            protected String doInBackground(String... params) {
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
        ru.execute(urlSuffix);
    }
}
