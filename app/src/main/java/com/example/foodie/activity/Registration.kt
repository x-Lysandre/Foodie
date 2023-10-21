package com.example.foodie.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

class Registration : AppCompatActivity() {
    lateinit var custom_toolbar :Toolbar
    lateinit var UserName : EditText
    lateinit var Pass: EditText
    lateinit var ConfPass: EditText
    lateinit var EmailAddress : EditText
    lateinit var MobileNum : EditText
    lateinit var DilAddress : EditText
    lateinit var Register: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        custom_toolbar= findViewById(R.id.appbar)
        setSupportActionBar(custom_toolbar)
        supportActionBar?.title ="Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)


        UserName = findViewById(R.id.etUsername)
        Pass = findViewById(R.id.etPassword)
        ConfPass = findViewById(R.id.etConfirmPassword)
        EmailAddress = findViewById(R.id.etEmailAddress)
        MobileNum = findViewById(R.id.etMobileNum)
        DilAddress = findViewById(R.id.etAddress)
        Register = findViewById(R.id.btnRegister)

        val sharedPref = getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

        Register.setOnClickListener(){

            val queue = Volley.newRequestQueue(this)
            val url ="http://13.235.250.119/v2/register/fetch_result"
            val params = JSONObject()
            params.put("name",UserName.text)
            params.put("mobile_number",MobileNum.text)
            params.put("password",Pass.text)
            params.put("address",DilAddress.text)
            params.put("email", EmailAddress.text)


            if(UserName.text.length<=3 ){
                UserName.error  = "Username should be greater than 3 character"
                Toast.makeText(this,"Username should be greater than 3 characters",Toast.LENGTH_SHORT).show()
            }else if(UserName.text.isBlank()||Pass.text.isBlank()||MobileNum.text.isBlank()||EmailAddress.text.isBlank()||DilAddress.text.isBlank()||ConfPass.text.isBlank()){
                Toast.makeText(this,"A field is blank",Toast.LENGTH_SHORT).show()
            }
            else{
                if(Pass.text.toString().toInt() == ConfPass.text.toString().toInt()){
                    if(ConnectionManager().checkConnectivity(this)){
                        val jsonObject = object: JsonObjectRequest(Request.Method.POST,url,params, Response.Listener {
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if(success){
                                    val nestedData = data.getJSONObject("data")

                                    sharedPref.edit().putString("username",nestedData.getString("name")).apply()
                                    sharedPref.edit().putString("emailAddress",nestedData.getString("email")).apply()
                                    sharedPref.edit().putString("address",nestedData.getString("address")).apply()
                                    sharedPref.edit().putString("Number",nestedData.getString("mobile_number")).apply()
                                    Toast.makeText(this,"You are successfully Registered. ",Toast.LENGTH_SHORT).show()

                                    val intent = Intent(this,Login::class.java)
                                    startActivity(intent)
                                    this.finish()

                                }
                                else{
                                    val responseMessageServer =
                                        data.getString("errorMessage")
                                    Toast.makeText(this,responseMessageServer.toString(),Toast.LENGTH_SHORT).show()
                                }
                            }
                            catch (e: JSONException){
                                Toast.makeText(this,"some error occurred",Toast.LENGTH_SHORT).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(this,"some error occurred",Toast.LENGTH_SHORT).show()

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
                else{
                    Toast.makeText(this,"Password doesn't match",Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                val intent = Intent(this,Login::class.java)
                startActivity(intent)
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}

