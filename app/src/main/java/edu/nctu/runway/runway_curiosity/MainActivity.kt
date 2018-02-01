package edu.nctu.runway.runway_curiosity

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.TextView

import com.google.android.gms.location.LocationServices
import java.util.*

class MainActivity : AppCompatActivity() {

    var mLastLocation : Location? =null
    var mLatitudeLabel :String? = resources.getString(R.string.latitude_label)
    var mLongitudeLabel  :String? = resources.getString(R.string.longitude_label)
    var mLatitudeText = findViewById<TextView>(R.id.latitude_text)
    var mLongitudeText = findViewById<TextView>(R.id.longitude_text)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    @SuppressLint("NewApi")
    override fun onStart() {
        super.onStart()
        // request_codes
        val requestCodeFineLocation = 1
        val requestCodeCoarseLocation = 2
        // check permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        //request permission
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCodeFineLocation)
        else getLastLocation()

        /*if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        //request permission
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), requestCodeCoarseLocation)
        */

    }

    @SuppressWarnings("MissingPermission")
    private fun getLastLocation(){
        var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.lastLocation
                .addOnCompleteListener{task ->
                    if (task.isSuccessful){
                            mLatitudeText.text = String.Companion.format(Locale.ENGLISH,"%s : %f",
                                    mLatitudeLabel,
                                    task.result.latitude)
                            mLongitudeText.text = String.Companion.format(Locale.ENGLISH, "%s : %f",
                                    mLongitudeLabel,
                                    task.result.longitude)
                        }else{
                            //show warning
                        }
                }
    }
}

