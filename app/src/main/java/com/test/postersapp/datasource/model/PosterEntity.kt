package com.test.postersapp.datasource.model

import com.squareup.moshi.Json
import com.test.postersapp.domain.model.Poster

data class PosterEntity(
    @field:Json(name = "id") val id: String,
    @field:Json(name = "poster") val poster: String?,
    @field:Json(name = "year") val year: Int?
)

fun PosterEntity.mapToDomain(): Poster =
    Poster(id, poster?:"", year?:0)

fun List<PosterEntity>.mapToDomain(): List<Poster> = map { it.mapToDomain() }