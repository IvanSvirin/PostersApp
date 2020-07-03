package com.test.postersapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.GridLayoutAnimationController
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.test.postersapp.R
import com.test.postersapp.domain.model.Poster
import com.test.postersapp.ui.adapter.PostersAdapter
import com.test.postersapp.ui.viewmodel.PostersViewModel
import com.test.postersapp.util.ResourceState
import kotlinx.android.synthetic.main.main_fragment.*
import org.koin.androidx.viewmodel.ext.android.viewModel


class PostersFragment : Fragment() {
    private var postersAdapter: PostersAdapter? = null
    private lateinit var animation: Animation
    private lateinit var controller: GridLayoutAnimationController


    companion object {
        fun newInstance() = PostersFragment()
    }

    private val viewModel: PostersViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getPosters()
        createAnimationController()
        observePosters()
        setListener()
    }

    private fun observePosters() {
        viewModel.mutableLiveData.observe(viewLifecycleOwner, Observer {
            when (it.state) {
                ResourceState.LOADING -> progressBar.visibility = View.VISIBLE
                ResourceState.SUCCESS -> showResult(it.data)
                ResourceState.ERROR -> showError(it.message)
            }
        })
    }

    private fun setListener() {
        switch1.setOnCheckedChangeListener { _, isChecked -> if (isChecked) viewModel.getFiltered() else viewModel.getPosters() }
    }

    private fun showError(message: String?) {
        progressBar.visibility = View.INVISIBLE
        showLoadErrorAlert(message)
    }

    private fun showResult(data: List<Poster>?) {
        progressBar.visibility = View.INVISIBLE
        postersAdapter = context?.let { data?.let { it1 -> PostersAdapter(it, it1) } }

        gridView.setLayoutAnimation(controller)
        gridView.adapter = postersAdapter
    }

    private fun showLoadErrorAlert(message: String?) {
        val builder = AlertDialog.Builder(context!!)
        with(builder)
        {
            setMessage(message)
            setPositiveButton(
                "OK"
            ) { dialog, id ->
                dialog.cancel()
            }
            show()
        }
    }

    private fun createAnimationController() {
        animation = AnimationUtils.loadAnimation(context, R.anim.grid_item_anim)
        controller = GridLayoutAnimationController(animation, .2f, .2f)
    }
}
