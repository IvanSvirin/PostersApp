package com.test.postersapp.datasource.cache

import android.content.Context
import com.test.postersapp.domain.model.MyObjectBox
import com.test.postersapp.domain.model.Poster
import io.objectbox.Box
import io.objectbox.BoxStore

fun createObjectBox(context: Context): Box<Poster> {
    val boxStore: BoxStore = MyObjectBox.builder().androidContext(context).build()
    return boxStore.boxFor(Poster::class.java)
}