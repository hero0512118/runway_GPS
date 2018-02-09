package edu.nctu.runway.runway_curiosity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

    private lateinit var mStartButton :TextView
    private lateinit var mStopButton :TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mLatitudeLabel = resources.getString(R.string.latitude_label)
        mLongitudeLabel  = resources.getString(R.string.longitude_label)
        mLastUpdateTimeLabel = resources.getString(R.string.lastUpdateTime_label)
        mLastUpdateTime = ""
        mLatitudeText = findViewById(R.id.latitude_text)
        mLongitudeText = findViewById(R.id.longitude_text)
        mLastUpdateTimeText = findViewById(R.id.lastUpdateTime_text)
        mDataText = findViewById(R.id.data_text)

        mStartButton = findViewById<TextView>(R.id.start) as Button
        mStopButton = findViewById<TextView>(R.id.stop) as Button


        mDataText.text=""
    }
}

