package com.voiceit.voiceit2sdk;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    EditText etLogin,etPassword;
    Button btSesion,btRegistro;
    TextView tvTitulo,tvRegistro;
    Context c=this;
    String user="";
    String password="";
    Intent intent;
    String urlBase="http://82.223.101.171/voiceitAndroid/php/";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        etLogin=findViewById(R.id.etLogin);
        etPassword=findViewById(R.id.etPassword);
        btSesion=findViewById(R.id.btLogin);
        btRegistro=findViewById(R.id.btRegistro);
        tvTitulo=findViewById(R.id.tvTitulo);
        tvRegistro=findViewById(R.id.tvRegistro);
        intent=getIntent();
        if(intent.hasExtra("usuario")){
            etLogin.setText(intent.getStringExtra("usuario"));
        }
        if(intent.hasExtra("password")){
            etPassword.setText(intent.getStringExtra("password"));
        }
    }

    @Override
    public View onCreateView(String name, Context context, AttributeSet attrs) {


        return super.onCreateView(name, context, attrs);

    }


    public void loguearse(View view){
        if(!etLogin.getText().toString().isEmpty()&&!etPassword.getText().toString().isEmpty()){
            user=etLogin.getText().toString().trim();
            password=etPassword.getText().toString().trim();
            String url = urlBase+"comprobarLogin.php";
            StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if(!response.trim().equals("false")){
                                loginCorrecto(response.trim());
                            }else{
                                Toast.makeText(c,"No existe usuario con esas credenciales.",Toast.LENGTH_SHORT).show();
                                etPassword.setText("");
                                etPassword.setHint("Introduzca aquí su contraseña");
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

    public void loginCorrecto(String idJefe){
        Intent i=new Intent(c,Empleado.class);
        i.putExtra("idJefe",idJefe);
        startActivity(i);
    }

    public void registrarse(View view){
        Intent i=new Intent(c,Registro.class);
        startActivity(i);
    }
    public void cargarItems(){

    }
}
