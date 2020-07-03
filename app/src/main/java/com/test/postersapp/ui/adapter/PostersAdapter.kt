package com.test.postersapp.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.test.postersapp.R
import com.test.postersapp.domain.model.Poster


class PostersAdapter constructor(private val context: Context, private val posters: List<Poster>) :
    BaseAdapter() {

    override fun getView(p0: Int, convertView: View?, p2: ViewGroup?): View {
        val iv: ImageView
        if (convertView == null) {
            iv = ImageView(context)
        } else {
            iv = convertView as ImageView
        }
        Glide.with(context)
            .load(posters[p0].poster)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(iv)
        return iv
    }

    override fun getItem(p0: Int): Any {
        return posters[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getCount(): Int {
        return posters.size
    }
}