package edu.nctu.runway.runway_curiosity

import android.Manifest
import android.content.Intent
import android.annotation.SuppressLint
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

import kotlinx.android.synthetic.main.activity_main.*
import java.text.DateFormat


import java.util.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val requestCodeFineLocation = 1
    private val requestCheckSetting = 0x1
    private val keyRequestLocationUpdate = "requesting-location-updates"
    private val keyLocation = "location"
    private val keyLastUpdateTime = "last-update-time"
    private val permissionGranted = PackageManager.PERMISSION_GRANTED

    private lateinit var mLatitudeLabel : String
    private lateinit var mLongitudeLabel : String
    private lateinit var mLastUpdateTimeLabel : String
    private lateinit var mLastUpdateTime : String
    private lateinit var mLatitudeText :TextView
    private lateinit var mLongitudeText : TextView
    private lateinit var mLastUpdateTimeText :TextView

    private lateinit var mStartButton :TextView
    private lateinit var mStopButton :TextView

    private lateinit var mFusedLocationClient : FusedLocationProviderClient
    private var mLocationCallback = LocationCallback()
    private var mLocationRequest = LocationRequest()
    private var mRequestingLocationUpdates = false

    private lateinit var mLocationSettingsRequest: LocationSettingsRequest
    private lateinit var mSettingsClient: SettingsClient

    private lateinit var mMap: GoogleMap
    private var mCurrentLocation : Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLatitudeLabel = resources.getString(R.string.latitude_label)
        mLongitudeLabel  = resources.getString(R.string.longitude_label)
        mLastUpdateTimeLabel = resources.getString(R.string.lastUpdateTime_label)
        mLatitudeText = findViewById(R.id.latitude_text)
        mLongitudeText = findViewById(R.id.longitude_text)
        mLastUpdateTimeText = findViewById(R.id.lastUpdateTime_text)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mSettingsClient = LocationServices.getSettingsClient(this)
        lastUpdateTime_text.text = ""
        mLastUpdateTime = ""
        mStartButton = findViewById<TextView>(R.id.start) as Button
        mStopButton = findViewById<TextView>(R.id.stop) as Button


        updateValuesFromBundle(savedInstanceState)
        //update UI

        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.mapView) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setLocationRequest()
        createLocationCallback()
        buildLocationSettingsRequest()
    }

    @SuppressLint("MissingPermission")
    override fun onResume() {
        super.onResume()
        if (mRequestingLocationUpdates && checkPermissions()) {
            startLocationUpdates()
        } else if (!checkPermissions()) {
            requestPermissions()
        }

        updateUI()
    }

    override fun onPause(){
        super.onPause()
        stopLocationUpdates()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putBoolean(keyRequestLocationUpdate, mRequestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }

    private fun setLocationRequest(){
        mLocationRequest.interval = 50000
        mLocationRequest.fastestInterval= 1000
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun createLocationCallback() {
        mLocationCallback = object : LocationCallback() {
            @SuppressLint("MissingPermission")
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)

                mCurrentLocation = locationResult.lastLocation
                mLastUpdateTime = DateFormat.getTimeInstance().format(Date())
                updateUI()
            }
        }
    }

    private fun buildLocationSettingsRequest() {
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        mLocationSettingsRequest = builder.build()
    }

//    @SuppressWarnings("MissingPermission")
/*    private fun getLastLocation(){
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
    }*/

    private fun checkPermissions(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == permissionGranted)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(grantResults.size){
            0 -> Log.i("Permission","User Interaction was cancelled")
            else -> when(grantResults[0]) {
                        permissionGranted -> {
                            if (mRequestingLocationUpdates) {
                                Log.i("Permission", "Permission granted, updates requested, starting location updates")
                                startLocationUpdates()
                            }else{}
                        }
                        else -> {
                            showSnackbar(R.string.permission_denied_explanation,
                                    R.string.settings, View.OnClickListener {
                                val intent = Intent()
                                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                                intent.data = Uri.fromParts("package",
                                        BuildConfig.APPLICATION_ID, null)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                startActivity(intent)
                            })}
                    }
        }
    }

    private fun requestPermissions(){
        val shouldProvideRationale = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
        if (shouldProvideRationale) {
            Log.i("Permission", "Displaying permission rationale to provide additional context.")
            showSnackbar(R.string.permission_rationale,
                    android.R.string.ok, View.OnClickListener {
                // Request permission
                ActivityCompat.requestPermissions(this@MainActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        requestCodeFineLocation)
            })
        } else {
            Log.i("Permission", "Requesting permission")
            ActivityCompat.requestPermissions(this@MainActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    requestCodeFineLocation)
        }
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(keyRequestLocationUpdate)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        keyRequestLocationUpdate)
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(keyLocation)) {
                // Since KEY_LOCATION was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(keyLocation)
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(keyLastUpdateTime)) {
                mLastUpdateTimeLabel = savedInstanceState.getString(keyLastUpdateTime)
            }
            updateUI()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        // Begin by checking if the device has the necessary location settings.
        mSettingsClient.checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(this) {
                    Log.i("Setting check", "All location settings are satisfied.")
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                            mLocationCallback, Looper.myLooper())
                    updateUI()
                }
                .addOnFailureListener(this) { e ->
                    val statusCode = (e as ApiException).statusCode
                    when (statusCode) {
                        LocationSettingsStatusCodes.RESOLUTION_REQUIRED -> {
                            Log.i("Setting Check", "Location settings are not satisfied. Attempting to upgrade " + "location settings ")
                            try {
                                // Show the dialog by calling startResolutionForResult(), and check the
                                // result in onActivityResult().
                                val rae = e as ResolvableApiException
                                rae.startResolutionForResult(this@MainActivity, requestCheckSetting)
                            } catch (sie: IntentSender.SendIntentException) {
                                Log.i("Setting Check", "PendingIntent unable to execute request.")
                            }

                        }
                        LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE -> {
                            val errorMessage = "Location settings are inadequate, and cannot be " + "fixed here. Fix in Settings."
                            Log.e("Setting Check", errorMessage)
                            Toast.makeText(this@MainActivity, errorMessage, Toast.LENGTH_LONG).show()
                            mRequestingLocationUpdates = false
                        }
                    }
                    updateUI()
                }
    }

    private fun stopLocationUpdates() {
        if ((!mRequestingLocationUpdates)) Log.d("Location Update", "stopLocationUpdates: updates never requested, no-op.")
        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this) {
                    mRequestingLocationUpdates = false
                    setButtonsEnabledState()
                }
    }

    private fun setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartButton.isEnabled = false
            mStopButton.isEnabled = true
        } else {
            mStartButton.isEnabled = true
            mStopButton.isEnabled = false
        }
    }

    fun startUpdatesButtonHandler(view: View) {
        if ((!mRequestingLocationUpdates)) {
            mRequestingLocationUpdates = true
            setButtonsEnabledState()
            startLocationUpdates()
        }
    }

    fun stopUpdatesButtonHandler(view: View) {
        stopLocationUpdates()
    }

    private fun updateUI(){
        setButtonsEnabledState()
        mLatitudeText.text = String.format(Locale.TAIWAN, "%s: %f", mLatitudeLabel,
                mCurrentLocation?.latitude)
        mLongitudeText.text = String.format(Locale.TAIWAN, "%s: %f", mLongitudeLabel,
                mCurrentLocation?.longitude)
        mLastUpdateTimeText.text = String.format(Locale.TAIWAN, "%s: %s",
                mLastUpdateTimeLabel, mLastUpdateTime)
        if (mCurrentLocation == null) return
        var mLatlng = LatLng(mCurrentLocation!!.latitude, mCurrentLocation!!.longitude)
        mMap.addMarker(MarkerOptions().position(mLatlng).title("Current Position"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mLatlng))
        mMap.setMinZoomPreference(15F)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
    }

    private fun showSnackbar(mainTextStringId: Int, actionStringId: Int,
                             listener: View.OnClickListener) {
        Snackbar.make(
                findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show()
    }
}

