package com.example.foodie.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import java.sql.Connection

class Forgot_Password : AppCompatActivity() {
    lateinit var custom_toolbar: Toolbar
    lateinit var Number:EditText
    lateinit var EmailAddress:EditText
    lateinit var OTPbtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        custom_toolbar= findViewById(R.id.appbar)
        custom_toolbar.title = "Foodie"
        setSupportActionBar(custom_toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        Number = findViewById(R.id.etMobileNum)
        EmailAddress = findViewById(R.id.etEmailAddress)
        OTPbtn = findViewById(R.id.btnGetOTP)

        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
        val params = JSONObject()
        params.put("mobile_number",Number.text)
        params.put("email",EmailAddress.text)

        OTPbtn.setOnClickListener(){
            if(Number.text.isBlank() || Number.text.length < 10){
                Number.error = "Enter a valid mobile number"
                Toast.makeText(this,"A field is blank",Toast.LENGTH_SHORT).show()
            }else if(EmailAddress.text.isBlank()){
                EmailAddress.error = "Enter a valid Email address"
                Toast.makeText(this,"A field is blank",Toast.LENGTH_SHORT).show()
            }
            else{
                if(ConnectionManager().checkConnectivity(this)){
                    val sharedPref = getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)
                    sharedPref.edit().putString("mobileNumber", Number.text.toString()).apply()
                    val jsonObject = object : JsonObjectRequest(Request.Method.POST,url,params,Response.Listener {
                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if(success){
                                val first_time = data.getBoolean("first_try")
                                if (first_time) {
                                    Toast.makeText(
                                        this,
                                        "OTP sent",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this,OTP::class.java)
                                    startActivity(intent)

                                } else {
                                    Toast.makeText(
                                        this,
                                        "OTP sent already",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this,OTP::class.java)
                                    startActivity(intent)
                                }

                            } else {
                                val responseMessageServer =
                                    data.getString("errorMessage")
                                Toast.makeText(
                                    this,
                                    responseMessageServer.toString(),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        catch (e:JSONException){
                            Toast.makeText(this,"some error occurred1",Toast.LENGTH_SHORT).show()
                        }

                    },Response.ErrorListener {
                        Toast.makeText(this,"Some error occurred2",Toast.LENGTH_SHORT).show()

                    }){
                        override fun getHeaders(): MutableMap<String, String> {
                            val header = HashMap<String,String>()
                            header ["content-type"] = "application/json"
                            header["token"] = "2cd8da1c3b65d3"
                            return header
                        }
                    }
                    queue.add(jsonObject)

                }else {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("No Internet")
                    dialog.setMessage("Internet connection not found!")
                    dialog.setPositiveButton("Open Settings") { text, listener ->
                        val settingIntent = Intent(Settings.ACTION_SETTINGS)
                        startActivity(settingIntent)
                        finish()

                    }
                    dialog.setNegativeButton("Exit") { text, listener ->
                        ActivityCompat.finishAffinity(this)
                    }
                    dialog.create()
                    dialog.show()
                }
            }
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> {
            val intent = Intent(this,Login::class.java)
            startActivity(intent)
            this.finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }
}