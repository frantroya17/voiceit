package com.voiceit.voiceit2sdk;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.voiceit.voiceit2.VoiceItAPI2;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


public class Empleado extends AppCompatActivity {
    private VoiceItAPI2 myVoiceIt;
    String idUser;
    String idJefe = "1";
    Spinner sp;
    private String phrase = "Con mi voz puedo ingresar donde quiera";
    private String contentLanguage = "es-ES";
    private Context mContext = this;
    Intent intent;
    String urlBase2="http://82.223.101.171/voiceitAndroid/php/";
    String urlBase="http://sgi.thesuperdriver.com/fltroya/voiceitAndroid/php/";
    String idEmpleado;
    EditText nombre;
    String[] nombresUnicos = {};
    Object item = "";
    String latitud;
    String longitud;
    private LocationManager locManager;
    private Location loc;
    private RadioButton entrada;
    AlertDialog.Builder alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empleado);
        //intent=getIntent();
        //Toast.makeText(mContext,intent.getStringExtra("idJefe"),Toast.LENGTH_SHORT).show();
        myVoiceIt = new VoiceItAPI2("key_1408470b4b69464aa95ff27db1d77326", "tok_13223856222844569a9a89ea78c4450b");
        rellenarArrayNombres();
        entrada=findViewById(R.id.rbEntrada);
        alertDialog=new AlertDialog.Builder(this);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,}, 1000);

    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public void aniadirUser(View view) {
        nombre = findViewById(R.id.etNombreAnadir);
        if (!nombre.getText().toString().trim().equals("")) {
            comprobarUserUnico(nombre.getText().toString().trim());
        } else {
            Toast.makeText(mContext, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show();
        }
    }

    public void crearUser() {
        myVoiceIt.createUser(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    idUser = response.getString("userId");
                    insertEmpleado(nombre.getText().toString(), idUser, idJefe);
                } catch (Exception e) {
                    Toast.makeText(mContext, "Error-->" + e.getCause(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                if (errorResponse != null) {
                    Toast.makeText(mContext, "Error al crear el usuario--> " + errorResponse.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void comprobarUserUnico(String nombreUnico) {
        String url = urlBase+"comprobarUserUnico.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("true")) {
                            crearUser();
                        } else {
                            Toast.makeText(mContext, "El usuario ya existe", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Otro error", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("nombreUnico", nombreUnico.trim());
                params.put("idJefe", idJefe);
                return params;
            }

        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    public void insertEmpleado(String nombreUnico, String numeroUser, String idJefe) {
        String url = urlBase+"insertEmpleado.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.trim().equals("true")) {
                            Toast.makeText(mContext, "La insercion fue correcta", Toast.LENGTH_SHORT).show();
                            nombre.setText("");
                            nombre.setHint(getString(R.string.nombreUnicoAnadir));
                            rellenarArrayNombres();

                        } else {
                            Toast.makeText(mContext, "La insercion falló", Toast.LENGTH_SHORT).show();
                            nombre.setText("");
                            nombre.setHint("Error, es posible que este nombre ya exista. Vuelva a escribir uno válido.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Otro error", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("nombreUnico", nombreUnico.trim());
                params.put("numeroUsuario", numeroUser.trim());
                params.put("idJefe", idJefe.trim());
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    public void checkResponse(JSONObject response) {
        try {
            if (response.getString("responseCode").equals("IFVD")
                    || response.getString("responseCode").equals("ACLR")
                    || response.getString("responseCode").equals("IFAD")
                    || response.getString("responseCode").equals("SRNR")
                    || response.getString("responseCode").equals("UNFD")
                    || response.getString("responseCode").equals("MISP")
                    || response.getString("responseCode").equals("DAID")
                    || response.getString("responseCode").equals("UNAC")
                    || response.getString("responseCode").equals("CLNE")
                    || response.getString("responseCode").equals("INCP")
                    || response.getString("responseCode").equals("NPFC")) {
                Toast.makeText(this, "responseCode: " + response.getString("responseCode")
                        + ", " + getString(com.voiceit.voiceit2.R.string.CHECK_CODE), Toast.LENGTH_LONG).show();
                Log.e("MainActivity", "responseCode: " + response.getString("responseCode")
                        + ", " + getString(com.voiceit.voiceit2.R.string.CHECK_CODE));
            }
        } catch (JSONException e) {
            Log.d("MainActivity", "JSON exception : " + e.toString());
        }
    }

    public void rellenarSpinner() {
        sp = findViewById(R.id.spNombres);
        sp.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, nombresUnicos));

        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                item = parent.getItemAtPosition(pos);
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public void rellenarArrayNombres() {
        String url = urlBase+"comprobarUsers.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        nombresUnicos = response.trim().split("&");
                        rellenarSpinner();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Otro error al rellenar el array", Toast.LENGTH_SHORT).show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("idJefe", idJefe);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    public void comprobarUserByName(View view) {
        String name = item.toString();
        String url = urlBase+"comprobarUserByNombre.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.trim().equals("")) {
                            alertDialog.setTitle("Importante");
                            alertDialog.setMessage("¿Aceptas registrar la voz de "+name+"? Si ya estaba registrada se borraran antiguos registros");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    registrarVoz(response.trim());
                                }
                            });
                            alertDialog.setNegativeButton("Cancelar",null);
                            AlertDialog al=alertDialog.create();
                            al.show();
                        } else {
                            Toast.makeText(mContext, "El nombre seleccionado no esta en la bd", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Otro error", Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("nombreUnico", name);
                params.put("idJefe", idJefe);

                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    public void registrarVoz(String name) {

        myVoiceIt.encapsulatedVoiceEnrollment(this, name, contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Toast.makeText(mContext, "Exito inscripcion", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                if (errorResponse != null) {
                    Toast.makeText(mContext, "Error inscripcion", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void compruebaVoz(String name) {
        myVoiceIt.encapsulatedVoiceVerification(this, name, contentLanguage, phrase, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                fichamos();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                checkResponse(errorResponse);
                if (errorResponse != null) {
                    System.out.println("encapsulatedVoiceVerification onFailure Result : " + errorResponse.toString());
                }
            }
        });
    }

    public void comprobarUserByName2(View view) {

        String url = urlBase+"comprobarUserByNombre.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.trim().equals("")) {
                            alertDialog.setTitle("Importante");
                            alertDialog.setMessage("¿Aceptas comprobar la voz de "+item.toString()+"?");
                            alertDialog.setCancelable(false);
                            alertDialog.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    compruebaVoz(response.trim());
                                }
                            });
                            alertDialog.setNegativeButton("Cancelar",null);
                            AlertDialog al=alertDialog.create();
                            al.show();
                        } else {
                            Toast.makeText(mContext, "El nombre seleccionado no esta en la bd", Toast.LENGTH_SHORT).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Otro error en comprobaruser2"+ error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("nombreUnico", item.toString());
                params.put("idJefe", idJefe);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    public void fichamos() {
        String url = urlBase+"comprobarIdEmpleadoByNombre.php";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (!response.trim().equals("")) {
                            idEmpleado = response.trim();
                            insertFicha();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext, "Otro error en fichamos "+error.getMessage(), Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // the POST parameters:
                params.put("nombreUnico", item.toString().trim());
                params.put("idJefe", idJefe);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }

    public void insertFicha() {
        String url = urlBase+"insertFicha.php";
        String pulsado;
        if(entrada.isChecked()){
            pulsado="on";
        }else{
            pulsado="";
        }

        locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            latitud="";
            longitud="";
        }else{
            loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            longitud=String.valueOf(loc.getLongitude());
            latitud=String.valueOf(loc.getLatitude());
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.trim().equals("true")){
                            Toast.makeText(mContext,"Has fichado con exito",Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(mContext,"Hubo algun error al fichar",Toast.LENGTH_SHORT).show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(mContext,"Otro error en insertFicha"+ error.getMessage(),Toast.LENGTH_SHORT).show();
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {

                Map<String, String>  params = new HashMap<>();
                // the POST parameters:
                params.put("idEmpleado", idEmpleado);
                params.put("idJefe", idJefe);
                params.put("latitud", latitud);
                params.put("longitud", longitud);
                params.put("entrada", pulsado);
                return params;
            }
        };
        Volley.newRequestQueue(this).add(postRequest);
    }
}
