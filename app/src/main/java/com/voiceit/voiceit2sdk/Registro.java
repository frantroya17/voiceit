package com.voiceit.voiceit2sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Registro extends AppCompatActivity {
    Context c=this;
    EditText etLogin,etPassword;
    String user="",password="";
    String urlBase="http://82.223.101.171/voiceitAndroid/php/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    public void cancelar(View view){
        Intent i=new Intent(c,Login.class);
        startActivity(i);
    }

    public void registro(View view){
        etLogin=findViewById(R.id.etRegistroUsuario);
        etPassword=findViewById(R.id.etRegistroPassword);
        if(!etLogin.getText().toString().isEmpty()&&!etPassword.getText().toString().isEmpty()){
            user=etLogin.getText().toString().trim();
            password=etPassword.getText().toString().trim();
            String url = urlBase+"insertJefe.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(response.trim().equals("true")){
                                Toast.makeText(c,"Se ha registrado con exito.",Toast.LENGTH_SHORT).show();
                                Intent i=new Intent(c,Login.class);
                                i.putExtra("usuario",user);
                                i.putExtra("password",password);
                                startActivity(i);
                            }else{
                                Toast.makeText(c,"Este correo ya esta registrado.",Toast.LENGTH_SHORT).show();
                                etPassword.setText("");
                                etLogin.setText("");
                                etPassword.setHint("Introduzca aquí la contraseña.");
                                etPassword.setHint("Introduzca aquí el correo.");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(c,"Otro error",Toast.LENGTH_SHORT).show();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams()
                {
                    Map<String, String>  params = new HashMap<>();
                    // the POST parameters:
                    params.put("usuario", user);
                    params.put("password",password);
                    return params;
                }

            };
            Volley.newRequestQueue(this).add(postRequest);
        }else{
            Toast.makeText(c,"Error. No puede haber ningun campo vacío.",Toast.LENGTH_SHORT).show();
        }
    }
}
