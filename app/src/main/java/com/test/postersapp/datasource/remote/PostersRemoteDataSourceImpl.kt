package com.test.postersapp.datasource.remote

import com.test.postersapp.data.datasource.PostersRemoteDataSource
import com.test.postersapp.datasource.model.mapToDomain
import com.test.postersapp.domain.model.Poster
import io.reactivex.Single

class PostersRemoteDataSourceImpl constructor(
    private val api: PostersApi
) : PostersRemoteDataSource {

    override suspend fun getPosters(): Single<List<Poster>> = api.getPosters().map { it.mapToDomain() }
}