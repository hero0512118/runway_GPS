package edu.nctu.runway.runway_curiosity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView

class ShowDataActivity : AppCompatActivity(){

    private lateinit var mLatitudeLabel : String
    private lateinit var mLongitudeLabel : String
    private lateinit var mLastUpdateTimeLabel : String
    private lateinit var mLastUpdateTime : String
    private lateinit var mLatitudeText :TextView
    private lateinit var mLongitudeText : TextView
    private lateinit var mLastUpdateTimeText :TextView
    private lateinit var mDataText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showdata)

        mLatitudeLabel = resources.getString(R.string.latitude_label)
        mLongitudeLabel  = resources.getString(R.string.longitude_label)
        mLastUpdateTimeLabel = resources.getString(R.string.lastUpdateTime_label)
        mLastUpdateTime = ""
        mLatitudeText = findViewById(R.id.latitude_text)
        mLongitudeText = findViewById(R.id.longitude_text)
        mLastUpdateTimeText = findViewById(R.id.lastUpdateTime_text)
        mDataText = findViewById(R.id.data_text)

        mDataText.text = intent.getStringExtra("raw data")
    }

    fun pathwayHandler(view: View) {
        val int = Intent(this, MainActivity::class.java)
        startActivity(int)
    }
}

