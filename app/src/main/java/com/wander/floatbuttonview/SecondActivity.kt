package com.wander.floatbuttonview

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_second.*

class SecondActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        jump.setOnClickListener { startActivity(Intent(this, SecondActivity::class.java)) }
        Log.d("life","activity")
    }
}
