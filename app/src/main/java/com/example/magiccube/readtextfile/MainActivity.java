package com.example.magiccube.readtextfile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> al=new ArrayList<>();
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;
    String ip;
    String scripturlstring;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView=(ListView)findViewById(R.id.listv);
        String[] items={};
        arrayList = new ArrayList<>(Arrays.asList(items));
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.txtitem, arrayList);
        listView.setAdapter(adapter);
        ip = getURL();
        scripturlstring = "http://"+ip+"/edit_script.php";
        Button Button_setURL = (Button) findViewById(R.id.button_setURL);
        Button_setURL.setText(getURL());

        button();
        readTxt();







        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Show input box
                showInputBox(arrayList.get(position),position);
            }
        });



    }
    public void add(String newItem){
        ListView listView=(ListView)findViewById(R.id.listv);
        listView.setAdapter(adapter);
        arrayList.add(newItem);
        adapter.notifyDataSetChanged();
    }
    public void clearList(){

        adapter.notifyDataSetChanged();
    }

    public void clearTxt(){

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://"+ip+"/clear_script.php");


                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

                    in.close();


                }catch (MalformedURLException e) {
                } catch (IOException e) {
                }
            }
        }).start();


    }



    private void button(){

        Button Button_update = (Button) findViewById(R.id.bu_update);
        Button_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                readListView();



            }
        });

        Button Button_setURL = (Button) findViewById(R.id.button_setURL);
        Button_setURL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                showURLDialog();



            }
        });

    }
    public void readTxt(){

        new Thread() {
            @Override
            public void run() {

                String path ="http://"+ip+"/test.txt";
                URL u = null;
                try {
                    u = new URL(path);
                    HttpURLConnection c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("GET");
                    c.connect();
                    InputStream in = c.getInputStream();
                    final ByteArrayOutputStream bo = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    InputStreamReader isr = new InputStreamReader(in, "UTF-8");
                    BufferedReader br = new BufferedReader(isr);
                    String line;

                        while ((line = br.readLine()) != null) {
                            al.add(line);
                        }
                    in.read(buffer);
                    bo.write(buffer);




                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            for(int i=0; i<al.size(); i++){
                                add(al.get(i).toString());
                            }





                            try {
                                bo.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }
    public void editText(final String text){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String textparam = "text1=" + URLEncoder.encode(text, "UTF-8");

                    URL scripturl = new URL(scripturlstring);
                    HttpURLConnection connection = (HttpURLConnection) scripturl.openConnection();
                    connection.setDoOutput(true);
                    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                    connection.setFixedLengthStreamingMode(textparam.getBytes().length);

                    OutputStreamWriter contentWriter = new OutputStreamWriter(connection.getOutputStream());
                    contentWriter.write(textparam);
                    contentWriter.flush();
                    contentWriter.close();



                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                        }
                    });

                    connection.disconnect();


                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public void showInputBox(String oldItem, final int index){
        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.setTitle("Input Box");
        dialog.setContentView(R.layout.input_box);
        TextView txtMessage=(TextView)dialog.findViewById(R.id.txtmessage);
        txtMessage.setText("Wert ändern!");
        txtMessage.setTextColor(Color.parseColor("#ff2222"));
        final EditText editText=(EditText)dialog.findViewById(R.id.txtinput);
        editText.setText(oldItem);
        Button bt=(Button)dialog.findViewById(R.id.btdone);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arrayList.set(index,editText.getText().toString());
                adapter.notifyDataSetChanged();
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    public void readListView(){
        int delay = 150;
        Button Button_update = (Button) findViewById(R.id.bu_update);
        Button_update.setEnabled(false);
        clearTxt();
        Context context1 = getApplicationContext();
        CharSequence text1 = "Config wird geupdated...";
        int duration1 = Toast.LENGTH_SHORT;

        Toast toast1 = Toast.makeText(context1, text1, duration1);
        toast1.show();


        for(int i=0; i < arrayList.size(); i++){

            delay = delay + 150;
            final int i2 = i;
            android.os.Handler h = new android.os.Handler();

            h.postDelayed(new Runnable() {
                @Override
                public void run() {
                    editText(arrayList.get(i2).toString());
                }
            }, delay);


        }
        //sortTxt();
        android.os.Handler h2 = new android.os.Handler();

        h2.postDelayed(new Runnable() {
            @Override
            public void run() {
                Button Button_update2 = (Button) findViewById(R.id.bu_update);
                Context context = getApplicationContext();
                CharSequence text = "Fertig!";
                int duration = Toast.LENGTH_SHORT;

                Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                Button_update2.setEnabled(true);
            }
        }, delay+150);


    }

    private void saveURL(String URL){

        SharedPreferences prefs = this.getSharedPreferences("AppPrefs", MODE_PRIVATE);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("URL", URL);
        editor.apply();

    }

    private String getURL(){

        SharedPreferences prefs = this.getSharedPreferences("AppPrefs", MODE_PRIVATE);
        return prefs.getString("URL","localhost");

    }

    private void showURLDialog(){
        final Dialog dialog=new Dialog(MainActivity.this);
        dialog.setTitle("Input Box");
        dialog.setContentView(R.layout.input_box);
        TextView txtMessage=(TextView)dialog.findViewById(R.id.txtmessage);
        txtMessage.setText("URL ändern!");
        txtMessage.setTextColor(Color.parseColor("#2EC509"));
        final EditText editText=(EditText)dialog.findViewById(R.id.txtinput);
        editText.setText(getURL());
        Button bt=(Button)dialog.findViewById(R.id.btdone);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveURL(editText.getText().toString());
                ip = editText.getText().toString();
                dialog.dismiss();

            }
        });
        dialog.show();
    }

}
