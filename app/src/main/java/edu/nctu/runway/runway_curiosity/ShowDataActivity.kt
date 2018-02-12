package edu.nctu.runway.runway_curiosity

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.TableLayout
import java.io.FileOutputStream


class ShowDataActivity : AppCompatActivity(){

    private lateinit var mLatitudeLabel : String
    private lateinit var mLongitudeLabel : String
    private lateinit var mLastUpdateTimeLabel : String
    private lateinit var mLastUpdateTime : String
    private lateinit var mLatitudeText :TextView
    private lateinit var mLongitudeText : TextView
    private lateinit var mLastUpdateTimeText :TextView
    private lateinit var mDataText : TableLayout
    lateinit var  mRawData : Any

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

        mRawData = intent.getStringExtra("raw data")
        Log.i("DATA", "$mRawData")



    }

    fun pathwayHandler(view: View) {
        val int = Intent(this, MainActivity::class.java)
        startActivity(int)

        /*val debtList = datasource.debtList
        val feeList = datasource.feeList
        val table = findViewById(R.id.myTableLayout) as TableLayout
        for (i in 0 until debtList.size()) {
            val row = TableRow(this)
            val debt = debtList.get(i)
            val fee = feeList.get(i)
            val tvDebt = TextView(this)
            tvDebt.text = "" + debt
            val tvFee = TextView(this)
            tvFee.text = "" + fee
            row.addView(tvDebt)
            row.addView(tvFee)
            table.addView(row)*/

    }
}

