package com.test.postersapp.domain.di

import com.test.postersapp.BuildConfig
import com.test.postersapp.data.datasource.PostersCacheDataSource
import com.test.postersapp.data.datasource.PostersRemoteDataSource
import com.test.postersapp.data.repository.PostersRepositoryImpl
import com.test.postersapp.datasource.cache.PostersCacheDataSourceImpl
import com.test.postersapp.datasource.cache.createObjectBox
import com.test.postersapp.datasource.remote.PostersApi
import com.test.postersapp.datasource.remote.PostersRemoteDataSourceImpl
import com.test.postersapp.datasource.remote.createNetworkClient
import com.test.postersapp.domain.model.Poster
import com.test.postersapp.domain.repository.PostersRepository
import com.test.postersapp.domain.usecase.GetPostersUseCase
import com.test.postersapp.ui.viewmodel.PostersViewModel
import io.objectbox.Box
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit


val viewModelModule: Module = module {
    viewModel { PostersViewModel(getPostersUseCase = get()) }
}

val useCaseModule: Module = module {
    factory { GetPostersUseCase(posterRepository = get()) }
}

val repositoryModule: Module = module {
    single {
        PostersRepositoryImpl(
            postersRemoteDataSource = get(), postersCacheDataSource = get()
        ) as PostersRepository
    }
}

val dataSourceModule: Module = module {
    single { PostersRemoteDataSourceImpl(api = postersApi) as PostersRemoteDataSource }
    single { PostersCacheDataSourceImpl(box = get()) as PostersCacheDataSource }
}

val cacheModule: Module = module {
    single { createObjectBox(androidContext()) }
}

val networkModule: Module = module {
    single { postersApi }
}

//private lateinit var box: Box<Poster>

private const val BASE_URL = "https://raw.githubusercontent.com/ar2code/apitest/master/"

private val retrofit: Retrofit = createNetworkClient(
    BASE_URL,
    BuildConfig.DEBUG
)

private val postersApi: PostersApi = retrofit.create(PostersApi::class.java)

private const val PLANT_CACHE = "PLANT_CACHE"

