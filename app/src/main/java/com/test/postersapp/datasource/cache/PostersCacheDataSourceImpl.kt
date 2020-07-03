package com.test.postersapp.datasource.cache

import com.test.postersapp.data.datasource.PostersCacheDataSource
import com.test.postersapp.domain.model.Poster
import io.objectbox.Box
import io.reactivex.Single

class PostersCacheDataSourceImpl constructor(private val box: Box<Poster>): PostersCacheDataSource {

    override suspend fun getPosters(): Single<List<Poster>> = Single.fromCallable { box.all }

    override fun putPosters(posters: List<Poster>) = box.put(posters)

    override suspend fun isCached(): Boolean = !box.isEmpty
}