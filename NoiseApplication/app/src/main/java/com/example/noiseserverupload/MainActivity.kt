package com.example.noiseserverupload

import android.Manifest.permission.RECORD_AUDIO
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

import okhttp3.*
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    val metter=SoundMetter()

    private val REQUEST_PERMISSION_CODE = 1000
    val idDBNoise="5e8e394f18674e0478669276"
    var number=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startBtn = findViewById(R.id.startBtn) as Button
        val stopBtn = findViewById(R.id.stopBtn) as Button
        if(checkPermissionFromDevice()==false) requestPermission()
        startBtn.setOnClickListener {
            if(checkPermissionFromDevice()==true)
            {
                Toast.makeText(this, "The device is listening !", Toast.LENGTH_LONG).show()
                metter.start()
                metter.getAmplitude()
                Executors.newSingleThreadExecutor().execute {postingAmplitude()}
            }
        }
        stopBtn.setOnClickListener {
            if(checkPermissionFromDevice()==true)
            {
                metter.stop()
                Toast.makeText(this, "The device has stop listening !", Toast.LENGTH_LONG).show()
                //findViewById<TextView>(R.id.NoiseLevel).text =number.toString()
            }
        }
    }

    private fun postingAmplitude() {
        Thread.sleep(5_000)
        number = metter.getAmplitude()
        Log.d("postingAmplitude", number.toString())
        while (number != 0.0) {
            Log.d("postingAmplitude", number.toString())
            val jsonEvent = """{
                    "title": "Noise0",
                    "number": "$number"
                    }""".trimIndent()
            var requestBody = RequestBody.create(
                MediaType.parse("application/json; charset=utf-8"),
                jsonEvent
            )

            Log.d("postingAmplitude",isConnectedToNetwork().toString())
            if (isConnectedToNetwork()) {
                PUT("http://192.168.1.7:3000/noises/" + idDBNoise, requestBody)
            }
            Thread.sleep(5_000)
            number = metter.getAmplitude()
        }
    }

    private fun isConnectedToNetwork(): Boolean {
        val connectivityManager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager?
        return connectivityManager?.activeNetworkInfo?.isConnectedOrConnecting()?:false
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(RECORD_AUDIO),
            REQUEST_PERMISSION_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionFromDevice(): Boolean {
        val record_audio_result = ContextCompat.checkSelfPermission(this, RECORD_AUDIO)
        return record_audio_result == PackageManager.PERMISSION_GRANTED
    }

    fun PUT(url: String, requestBody: RequestBody?) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .method("PUT", requestBody)
            .url(url)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                print("Oh no! It failed\n")
            }

            override fun onResponse(call: Call, response: Response) {
                this@MainActivity.runOnUiThread(object : Runnable {
                    override fun run() {
                        val jsonDataString = response.body()?.string()
                        Log.d("postingAmplitude",jsonDataString)
                        val json = JSONObject(jsonDataString)
                    }
                }
                )
            }
        })
    }
}
