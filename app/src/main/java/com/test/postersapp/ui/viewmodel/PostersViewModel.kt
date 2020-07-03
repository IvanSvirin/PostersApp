package com.test.postersapp.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.postersapp.domain.model.Poster
import com.test.postersapp.domain.usecase.GetPostersUseCase
import com.test.postersapp.util.Resource
import com.test.postersapp.util.ResourceState
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class PostersViewModel constructor(private val getPostersUseCase: GetPostersUseCase) : ViewModel() {
    val mutableLiveData = MutableLiveData<Resource<List<Poster>>>()
    private var posters = ArrayList<Poster>()

    fun getPosters() {
        if (posters.isNullOrEmpty()) {
            mutableLiveData.postValue(Resource(ResourceState.LOADING))
            GlobalScope.launch {
                getPostersUseCase.getPosters().let {
                    it.subscribe({
                        if (!it.isNullOrEmpty()) {
                            posters = ArrayList(it)
                            mutableLiveData.postValue(Resource(ResourceState.SUCCESS, posters))
                        } else {
                            mutableLiveData.postValue(
                                Resource(
                                    ResourceState.ERROR,
                                    null,
                                    "No data found"
                                )
                            )
                        }
                    }, {
                        mutableLiveData.postValue(Resource(ResourceState.ERROR, null, it.message))
                    })
                }
            }
        } else {
            mutableLiveData.postValue(Resource(ResourceState.SUCCESS, posters))
        }
    }

    fun getFiltered() {
        mutableLiveData.postValue(Resource(ResourceState.SUCCESS, posters.filter { it.year == 2020 }))
    }
}
