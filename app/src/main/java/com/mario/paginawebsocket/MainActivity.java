package com.mario.paginawebsocket;


import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    JSONObject object,cliente;


    private WebSocketClient mWebSocketClient;
    public static String nombreUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                sendMessage();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nick) {
             AlertDialog.Builder alert= new AlertDialog.Builder(this);
             final EditText user=new EditText(this);
                user.setSingleLine();
                user.setPadding(50,0,50,0);
                alert.setTitle("Nick");
                alert.setMessage("Introduzca el Nick");
                alert.setView(user);
                alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        nombreUsuario=user.getText().toString();
                    }
                });
                alert.setNegativeButton("Cancelar",null);
                alert.create();
                alert.show();


        } else if (id == R.id.conectar) {

                if(nombreUsuario==null){
                AlertDialog.Builder alertC=new AlertDialog.Builder(this);
                alertC.setTitle("Atención");
                alertC.setMessage("Introduzca el nick antes de la conexión");
                alertC.setPositiveButton("Aceptar",null);
                alertC.create();
                alertC.show();

                }else{
                connectWebSocket();

                }


        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://server-mariomoure.c9users.io:8081");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        Map<String, String> headers = new HashMap<>();
        mWebSocketClient = new WebSocketClient(uri,new Draft_17(),headers,0) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("{\"id\":\"" + nombreUsuario + "\"}");
                cliente=new JSONObject();
                /*try {
                    cliente.put("id",nombreUsuario);
                    String primerMensag=cliente.toString();
                    mWebSocketClient.send(primerMensag);
                } catch (JSONException e) {
                    mWebSocketClient.send("Error: "+ e.toString());
                }*/

            }


            @Override
            public void onMessage(String s) {
                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //************
                        //recibe los mensajes del servidor y comprueba si sn apra mi o no
                        TextView textView = (TextView)findViewById(R.id.messages);
                        String nombre;
                        String msg;
                        int priv;
                        String dst;


                        try{
                        object=new JSONObject(message);

                                nombre=object.getString("id");
                                msg=object.getString("msg");
                                priv=object.getInt("esPrivado");
                                dst=object.getString("dst");


                            //comprueba si el mensaje es privado y para quien es para que solo se lo muestre al destinatario
                            if(priv==1){
                                if(dst.equals(nombreUsuario)){
                                    textView.setText(textView.getText() + "\n" + nombre+ "\n" + msg);
                                }

                            }else
                            textView.setText(textView.getText() + "\n" + nombre+ "\n" + msg);


                        }
                        catch(JSONException e){

                            textView.setText(textView.getText() + "\n" + message);
                        }


                    }
                });
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public void sendMessage() {
        EditText editText = (EditText)findViewById(R.id.message);
        CheckBox check=(CheckBox) findViewById(R.id.CheckBox);
        EditText destinatario = (EditText)findViewById(R.id.destinatario);
        String mensaje=editText.getText().toString();

        int privado;
        if (check.isChecked()){
            privado=1;
        }else{
            privado=0;
        }
        String destino=destinatario.getText().toString();
       //Creacion de mensaje JSON con el objecto JSON
        //envia mensaje al servidor
        cliente=new JSONObject();
        try {
            cliente.put("id",nombreUsuario);
            cliente.put("msg",mensaje);
            cliente.put("esPrivado",privado);
            cliente.put("dst",destino);



        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*String enviar="{id:\""+nombreUsuario+"\",msg:\""+mensaje+"\",esPrivado:"+privado+",dst:\""+destino+"\"}";*/
        mWebSocketClient.send(cliente.toString());
        editText.setText("");
        destinatario.setText("");


    }


}

