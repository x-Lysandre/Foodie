package com.example.foodie.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import java.util.jar.Attributes

class Login : AppCompatActivity() {
    lateinit var custom_toolbar: Toolbar
    lateinit var EnterMobileNum: EditText
    lateinit var EnterPassword: EditText
    lateinit var LogIn: Button
    lateinit var forgotPass : TextView
    lateinit var SignUp :TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref  = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        if(sharedPref.getBoolean("isLoggedIn",true)) {
            val intent = Intent(this,MainActivity::class.java)
            intent.putExtra("name",sharedPref.getString("UName","Name"))
            startActivity(intent)
            finish()
        }
        setContentView(R.layout.activity_login)

        LogIn = findViewById(R.id.btnLogIn)
        EnterMobileNum = findViewById(R.id.etEnterMobileNum)
        EnterPassword = findViewById(R.id.etEnterPassword)
        forgotPass = findViewById(R.id.txtForgotPassword)
        SignUp = findViewById(R.id.txtSignUp)

        custom_toolbar = findViewById(R.id.appbar)
        custom_toolbar.title = "Foodie"
        setSupportActionBar(custom_toolbar)

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/login/fetch_result"
        val params = JSONObject()
        params.put("mobile_number", EnterMobileNum.text)
        params.put("password", EnterPassword.text)

        LogIn.setOnClickListener {
            if(EnterMobileNum.text.isBlank()||EnterMobileNum.text.length<10){
                EnterMobileNum.error = "Enter valid Mobile Number"
                Toast.makeText(this,"Field cannot be leave blank",Toast.LENGTH_SHORT).show()
            }
            else {
                if(EnterPassword.text.isBlank()) {
                    EnterPassword.error = "Enter a password"
                    Toast.makeText(this,"Password field is Blank",Toast.LENGTH_SHORT).show()
                }else if(EnterPassword.text.toString().length < 3){
                    EnterPassword.error = "Password should be greater than 3 character"
                    Toast.makeText(this,"Invalid Password",Toast.LENGTH_SHORT).show()
                }else{
                    if(ConnectionManager().checkConnectivity(this)){
                        val jsonObject = object: JsonObjectRequest(Request.Method.POST,url,params, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if(success){
                                    val nestedData = data.getJSONObject("data")

                                    sharedPref.edit().putBoolean("isLoggedIn",true).apply()
                                    sharedPref.edit().putString("user_id",nestedData.getString("user_id")).apply()
                                    sharedPref.edit().putString("UName",nestedData.getString("name")).apply()
                                    sharedPref.edit().putString("EAddress",nestedData.getString("email")).apply()
                                    sharedPref.edit().putString("address",nestedData.getString("address")).apply()
                                    sharedPref.edit().putString("Num",nestedData.getString("mobile_number")).apply()
                                    Toast.makeText(this,"Welcome ${nestedData.getString("name")} ",Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this,MainActivity::class.java)
                                    intent.putExtra("name",nestedData.getString("name"))
                                    intent.putExtra("email",nestedData.getString("email"))
                                    intent.putExtra("address",nestedData.getString("address"))
                                    intent.putExtra("mobile_number",nestedData.getString("mobile_number"))
                                    startActivity(intent)
                                    Log.i("this is login","the login id is ${nestedData.getString("user_id")}")
                                    finish()

                                }
                                else{
                                    Toast.makeText(this,"Invalid password or MobileNumber",Toast.LENGTH_SHORT).show()
                                }
                            }
                            catch (e: JSONException){
                                Toast.makeText(this,"some error occurred1",Toast.LENGTH_SHORT).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(this,"some error occurred2",Toast.LENGTH_SHORT).show()

                        }){
                            override fun getHeaders(): MutableMap<String, String> {
                                val header = HashMap<String, String>()
                                header["content"]="application/json"
                                header["token"] = "2cd8da1c3b65d3"
                                return header
                            }

                        }
                        queue.add(jsonObject)


                    }else{
                        val dialog = AlertDialog.Builder(this)
                        dialog.setTitle("No Internet")
                        dialog.setMessage("Internet connection not found!")
                        dialog.setPositiveButton("Open Settings"){text,listener->
                            val settingIntent  = Intent(Settings.ACTION_SETTINGS)
                            startActivity(settingIntent)
                            finish()
                        }
                        dialog.setNegativeButton("Exit"){text,listener->
                            ActivityCompat.finishAffinity(this)
                        }
                        dialog.create()
                        dialog.show()
                    }
                }
            }
        }
        SignUp.setOnClickListener {
            val intent = Intent(this, Registration::class.java)
            startActivity(intent)
        }
        forgotPass.setOnClickListener {
            val intent = Intent(this, Forgot_Password::class.java)
            startActivity(intent)
        }

    }
}
