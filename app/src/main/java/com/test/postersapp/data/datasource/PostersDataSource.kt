package com.test.postersapp.data.datasource

import com.test.postersapp.domain.model.Poster
import io.reactivex.Single

interface PostersRemoteDataSource {

    suspend fun getPosters(): Single<List<Poster>>
}

interface PostersCacheDataSource {

    suspend fun getPosters(): Single<List<Poster>>

    suspend fun isCached(): Boolean

    fun putPosters(posters: List<Poster>)
}