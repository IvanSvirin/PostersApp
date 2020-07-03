package com.test.postersapp.domain.usecase

import com.test.postersapp.domain.model.Poster
import com.test.postersapp.domain.repository.PostersRepository
import io.reactivex.Single

class GetPostersUseCase constructor(private val posterRepository: PostersRepository) {
    suspend fun getPosters(): Single<List<Poster>> = posterRepository.getPosters()
}