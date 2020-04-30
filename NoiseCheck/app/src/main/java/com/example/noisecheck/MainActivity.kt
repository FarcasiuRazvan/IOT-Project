package com.example.noisecheck

import android.content.Context
import android.graphics.Color
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.Executors

import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    lateinit var mainHandler: Handler
    val idDBNoise="5e8e394f18674e0478669276"
    var isCheckingActive=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startBtn = findViewById(R.id.checkBtn) as Button
        val stopBtn = findViewById(R.id.stopCheckBtn) as Button
        startBtn.setOnClickListener {
            isCheckingActive=true
        }
        stopBtn.setOnClickListener {
            isCheckingActive=false

        }
        mainHandler=Handler(Looper.getMainLooper())
    }

    override fun onPause(){
        super.onPause()
        mainHandler.removeCallbacks(updateTextTask)
    }

    override fun onResume(){
        super.onResume()
        mainHandler.post(updateTextTask)
    }

    private val updateTextTask = object : Runnable{
        override fun run(){
            nextAmplitude()
            mainHandler.postDelayed(this,5000)
        }
    }

    private fun nextAmplitude(){
        if (isCheckingActive==true)
        {
            if(isConnectedToNetwork()==true)
            {
                Executors.newSingleThreadExecutor().execute{
                    GET("http://192.168.1.7:3000/noises/"+idDBNoise);
                }
            }
        }
        else{
            blueSquare.setBackgroundColor(Color.GRAY)
            greenSquare.setBackgroundColor(Color.GRAY)
            redSquare.setBackgroundColor(Color.GRAY)
        }
    }

    private fun isConnectedToNetwork(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting()?:false
    }

    fun GET(url:String){
        val client = OkHttpClient()
        val request = Request.Builder().url(url).get().build()
        val response = client.newCall(request).execute()
        val jsonDataString=response.body()?.string()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                val json = JSONArray(jsonDataString)
                var errors=json.join(",")
                throw Exception(errors)
            }

            override fun onResponse(call: Call, response: Response) {
                this@MainActivity.runOnUiThread(object : Runnable{
                    override fun run() {
                        val json = JSONObject(jsonDataString)
                        if(json["number"].toString().toDouble()>0.0){
                            blueSquare.setBackgroundColor(Color.CYAN)
                        }
                        if(json["number"].toString().toDouble()>1000.0){
                            greenSquare.setBackgroundColor(Color.GREEN)
                        }
                        if(json["number"].toString().toDouble()>7000.0){
                            redSquare.setBackgroundColor(Color.RED)
                        }

                    }
                })
            }
        })
    }
}

