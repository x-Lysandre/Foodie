package com.example.foodie.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodie.R
import com.example.foodie.util.ConnectionManager
import org.json.JSONException
import org.json.JSONObject
import kotlin.Number as Number

class OTP : AppCompatActivity() {
    lateinit var custom_toolbar:Toolbar
    lateinit var Enterotp: EditText
    lateinit var Password: EditText
    lateinit var Confirm: EditText
    lateinit var Submit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_otp)

        val sharedPref = getSharedPreferences(getString(R.string.preference_file_name),Context.MODE_PRIVATE)

        Enterotp = findViewById(R.id.etEnterOTP)
        Password = findViewById(R.id.etEnterPassword)
        Confirm = findViewById(R.id.etConfirmPassword)
        Submit = findViewById(R.id.btnSubmit)

        val mobilenumber = sharedPref.getString("mobileNumber","4")

        custom_toolbar = findViewById((R.id.appbar))
        setSupportActionBar(custom_toolbar)
        supportActionBar?.title = "OTP"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        Submit.setOnClickListener {
            if (Enterotp.text.isBlank()) {
                Enterotp.error = "OTP missing"
            } else {
                if (Password.text.isBlank() || Confirm.text.length <= 4) {
                    Password.error = "Invalid Password"
                } else {
                    if (Confirm.text.isBlank()) {
                        Confirm.error = "Confirm Password Missing"
                    } else {
                        if ((Password.text.toString().toInt() == Confirm.text.toString().toInt())
                        ) {
                            if (ConnectionManager().checkConnectivity(this)) {

                                try {
                                    //send mobile_number and password to get OTP
                                    val loginUser = JSONObject()
                                    if (mobilenumber != null) {
                                        loginUser.put("mobile_number", mobilenumber.toInt())
                                    }
                                    loginUser.put("password", Password.text.toString())
                                    loginUser.put("otp", Enterotp.text.toString())

                                    val queue = Volley.newRequestQueue(this)
                                    val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                                    val jsonObjectRequest = object : JsonObjectRequest(
                                        Method.POST,
                                        url,
                                        loginUser,
                                        Response.Listener {

                                            val response = it.getJSONObject("data")
                                            val success = response.getBoolean("success")

                                            if (success) {
                                                val serverMessage = response.getString("successMessage")

                                                Toast.makeText(
                                                    this,
                                                    serverMessage,
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                val intent = Intent(this,Login::class.java)
                                                startActivity(intent)

                                            } else {
                                                val responseMessageServer =
                                                    response.getString("errorMessage")
                                                Toast.makeText(
                                                    this,
                                                    responseMessageServer.toString(),
                                                    Toast.LENGTH_SHORT
                                                ).show()

                                            }
                                        },
                                        Response.ErrorListener {

                                            Toast.makeText(
                                                this,
                                                "Some Error occurred!!!",
                                                Toast.LENGTH_SHORT
                                            ).show()

                                        }) {
                                        override fun getHeaders(): MutableMap<String, String> {
                                            val headers = HashMap<String, String>()
                                            headers["Content-type"] = "application/json"
                                            headers["token"] = "2cd8da1c3b65d3"
                                            return headers
                                        }
                                    }

                                    queue.add(jsonObjectRequest)

                                } catch (e: JSONException) {
                                    Toast.makeText(
                                        this,
                                        "Some unexpected error occurred!!!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                                val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
                                alterDialog.setTitle("No Internet")
                                alterDialog.setMessage("Check Internet Connection!")
                                alterDialog.setPositiveButton("Open Settings") { _, _ ->
                                    val settingsIntent = Intent(Settings.ACTION_SETTINGS)
                                    startActivity(settingsIntent)
                                }
                                alterDialog.setNegativeButton("Exit") { _, _ ->
                                    ActivityCompat.finishAffinity(this)
                                }
                                alterDialog.create()
                                alterDialog.show()
                            }

                        } else {

                            Confirm.error = "Passwords don't match"
                        }
                    }
               }
           }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home ->{
                val intent = Intent(this,Forgot_Password::class.java)
                startActivity(intent)
                this.finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}