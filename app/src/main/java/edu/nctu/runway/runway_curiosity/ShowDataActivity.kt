package edu.nctu.runway.runway_curiosity

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TableLayout
import android.widget.TableRow

class ShowDataActivity : AppCompatActivity(){

    private lateinit var mDataText : TableLayout
    lateinit var  mRawData : Any

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_showdata)

        mDataText = findViewById(R.id.data_text)

        mRawData = intent.getStringExtra("raw data")
        var items = mRawData.toString().split("LocationSet") as MutableList

        row("TIME", "LATITUDE", "LONGITUDE")
        for (i in 0 until items.size-2){
            val timeText = items.last().split(",")[0].split("(time=")[1]
            val latitudeText = items.last().split("location=lat/lng: (")[1].split(",")[0]
            val longitudeText = items.last().split("location=lat/lng: (")[1].split(",")[1].split(")")[0]
            row(timeText, latitudeText, longitudeText)
            items = items.dropLast(1) as MutableList<String>
        }
    }

    private fun row(time_text : String, latitude_text : String, longitude_text : String){
        val table = findViewById<TableLayout>(R.id.data_text)
        val row = TableRow(this)
        val time = TextView(this)
        val latitude = TextView(this)
        val longitude = TextView(this)
        time.text = time_text + "         "
        latitude.text = latitude_text + "   "
        longitude.text = longitude_text
        row.addView(time)
        row.addView(latitude)
        row.addView(longitude)
        table.addView(row)
    }

    fun pathwayHandler(view: View) {
        val int = Intent(this, MainActivity::class.java)
        startActivity(int)
    }
}

