package com.mirae.mp3loud

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class ActivityMain : ActivityNoSystemBar() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }
}