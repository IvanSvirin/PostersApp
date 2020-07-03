package com.test.postersapp.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.test.postersapp.R
import com.test.postersapp.ui.fragment.PostersFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, PostersFragment.newInstance())
                    .commitNow()
        }
    }
}
