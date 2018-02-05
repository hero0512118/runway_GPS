package edu.nctu.runway.runway_curiosity

import android.Manifest
import android.content.Intent
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.location.LocationServices

import java.util.*

class MainActivity : AppCompatActivity() {

    private val requestCodeFineLocation = 1
    private val requestLocationUpdateKey = "requesting-location-updates"
    private val permissionGranted = PackageManager.PERMISSION_GRANTED

    var mLatitudeLabel :String? = resources.getString(R.string.latitude_label)
    var mLongitudeLabel  :String? = resources.getString(R.string.longitude_label)
    var mLatitudeText = findViewById<TextView>(R.id.latitude_text)
    var mLongitudeText = findViewById<TextView>(R.id.longitude_text)
    private var mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)!!
    var mLocationCallback = LocationCallback()
    var mLocationRequest = LocationRequest()
    private var mRequestingLocationUpdates : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (savedInstanceState!!.keySet().contains(requestLocationUpdateKey))
            mRequestingLocationUpdates = savedInstanceState!!.getBoolean(requestLocationUpdateKey)
        //update UI
    }

    @SuppressLint("NewApi")
    override fun onStart() {
        super.onStart()

        // check permission
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != permissionGranted) {
            //request permission
            requestPermissions()
            Log.i("test", "request")
        }
        else{
            getLastLocation()
            Log.i("test", "getLocation")
        }
        mLocationRequest.interval = 10000
        mLocationRequest.fastestInterval= 5000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (mRequestingLocationUpdates) mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                mLocationCallback, null)
    }

    override fun onPause(){
        super.onPause()
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putBoolean(requestLocationUpdateKey, mRequestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    @SuppressWarnings("MissingPermission")
    private fun getLastLocation(){
        mFusedLocationClient.lastLocation
                .addOnCompleteListener{task ->
                    if (task.isSuccessful){
                        if (task.result == null)
                        else{
                            mLatitudeText.text = String.format(Locale.ENGLISH, "%s : %f",
                                    mLatitudeLabel,
                                    task.result.latitude)
                            mLongitudeText.text = String.format(Locale.ENGLISH, "%s : %f",
                                    mLongitudeLabel,
                                    task.result.longitude)
                            Log.i("test", "updated")
                        }
                    }else{
                        //show warning
                        Log.w("LastLocation:exception", task.exception)
                        Snackbar.make(findViewById(R.id.status), R.string.no_location_detected,10).show()
                    }
                }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(grantResults.size){
            0 -> Log.i("UserPermission","User Interaction was cancelled")
            when(grantResults[0]) {
                permissionGranted -> {
                    Snackbar.make(findViewById(R.id.status), R.string.permission_denied_explanation, Snackbar.LENGTH_SHORT)
                }
                else -> {
                    Snackbar.make(findViewById(R.id.status), R.string.permission_denied_explanation, Snackbar.LENGTH_INDEFINITE)
                    var onClickListener = View.OnClickListener {
                        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                        intent.data = Uri.fromParts("package", "BuildConfig.APPLICATION_ID", "null")
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                    onClickListener
                }
            } -> return
        }
    }

    private fun requestPermissions(){
        ActivityCompat.requestPermissions(this,
        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), requestCodeFineLocation)
    }
}

