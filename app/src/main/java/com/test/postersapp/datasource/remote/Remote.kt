package com.test.postersapp.datasource.remote

import com.test.postersapp.datasource.model.PosterEntity
import io.reactivex.Single
import retrofit2.http.GET

interface PostersApi {

    @GET("movies.json")
    fun getPosters(): Single<List<PosterEntity>>

}