package com.udacity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_DETAIL_STATUS = "status"
        const val EXTRA_DETAIL_FILENAME = "fileName"
    }

    private lateinit var fileName: TextView
    private lateinit var status: TextView
    private lateinit var okBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        fileName = findViewById(R.id.file_name_value)
        status = findViewById(R.id.status_value)
        okBtn = findViewById(R.id.ok_btn)
        okBtn.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        })
        if (intent?.extras != null) {
            fileName.text = intent.getStringExtra(EXTRA_DETAIL_FILENAME)
            status.text = intent.getStringExtra(EXTRA_DETAIL_STATUS)
        }
    }

}
