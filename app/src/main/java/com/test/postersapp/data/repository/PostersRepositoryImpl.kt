package com.test.postersapp.data.repository

import com.test.postersapp.data.datasource.PostersCacheDataSource
import com.test.postersapp.data.datasource.PostersRemoteDataSource
import com.test.postersapp.domain.model.Poster
import com.test.postersapp.domain.repository.PostersRepository
import io.reactivex.Single


class PostersRepositoryImpl constructor(
    private val postersRemoteDataSource: PostersRemoteDataSource,
    private val postersCacheDataSource: PostersCacheDataSource
) : PostersRepository {

    override suspend fun getPosters(): Single<List<Poster>> =
        when (postersCacheDataSource.isCached()) {
            true -> postersCacheDataSource.getPosters()
            false -> postersRemoteDataSource.getPosters().doOnSuccess { postersCacheDataSource.putPosters(it) }
        }
}