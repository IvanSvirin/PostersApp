package com.test.postersapp.domain.repository

import com.test.postersapp.domain.model.Poster
import io.reactivex.Single

interface PostersRepository {
    suspend fun getPosters(): Single<List<Poster>>
}