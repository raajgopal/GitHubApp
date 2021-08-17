package com.example.githubapp.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.githubapp.R
import com.example.githubapp.api.ApiBuilder
import com.example.githubapp.models.GitRepoInfo
import com.example.githubapp.models.Status
import com.example.githubapp.viewmodel.GitHubViewModel
import com.example.githubapp.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.github_repo_detail_info.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GitHubDetailFragment : Fragment() {
    private lateinit var viewModel: GitHubViewModel
    private var positionArgs: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.github_repo_detail_info, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        processExtras()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actionbar = (activity as AppCompatActivity).supportActionBar
        actionbar?.title =
            resources.getString(R.string.git_detail_info)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        setupViewModel()
        setupObserver(view)
    }

    //view model
    private fun setupViewModel() {
        viewModel = ViewModelProvider(
            requireActivity(),
            ViewModelFactory(
                apiService = ApiBuilder.apiService
            )
        ).get(GitHubViewModel::class.java)
    }

    private fun bindUiComponents(repoList: List<GitRepoInfo>, viewGroup: View) {
        if (positionArgs < repoList.size) {
            val repoInfo = repoList[positionArgs]
            viewGroup.userName.text =
                context?.resources?.getString(R.string.user_name, repoInfo.head.user.login)
            viewGroup.userName.visibility = View.VISIBLE
            viewGroup.prTitle.text =
                context?.resources?.getString(R.string.pr_title, repoInfo.title)
            viewGroup.prTitle.visibility = View.VISIBLE
            val date = repoInfo.created_at
            val closedDate = repoInfo.closed_at
            try {
                val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH)
                inputFormat.timeZone = TimeZone.getTimeZone("UTC")
                val outputFormat = SimpleDateFormat("yyyy-MM-dd hh:mm:ss a", Locale.ENGLISH)
                val parsedDate: Date? = inputFormat.parse(date)
                val parsedClosedDate: Date? = inputFormat.parse(closedDate)
                parsedDate?.let {
                    viewGroup.creationDate.text = context?.resources?.getString(
                        R.string.creation_at,
                        outputFormat.format(parsedDate)
                    )
                    viewGroup.creationDate.visibility = View.VISIBLE
                }
                parsedClosedDate?.let {
                    viewGroup.closedDate.text = context?.resources?.getString(
                        R.string.closed_at,
                        outputFormat.format(parsedClosedDate)
                    )
                    viewGroup.closedDate.visibility = View.VISIBLE
                }
            } catch (e: ParseException) {
                Log.e("Date Parsing failed", e.message.toString())
            }

            viewGroup.iconImage.visibility = View.VISIBLE
            val imageUrl = repoInfo.head.user.avatar_url
            context?.let { Glide.with(it).load(imageUrl).into(viewGroup.iconImage) }
        }
    }

    private fun setupObserver(viewGroup: View) {
        viewModel.getGitHubRepoList().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewGroup.progressBar.visibility = View.GONE
                    it.data?.let { list -> bindUiComponents(list, viewGroup) }
                }
                Status.LOADING -> {
                    viewGroup.progressBar.visibility = View.VISIBLE
                    viewGroup.userName.visibility = View.GONE
                    viewGroup.prTitle.visibility = View.GONE
                    viewGroup.creationDate.visibility = View.GONE
                    viewGroup.closedDate.visibility = View.GONE
                    viewGroup.iconImage.visibility = View.GONE

                }
                Status.ERROR -> {
                    //Handle Error
                    viewGroup.progressBar.visibility = View.GONE
                    viewGroup.userName.visibility = View.GONE
                    viewGroup.prTitle.visibility = View.GONE
                    viewGroup.creationDate.visibility = View.GONE
                    viewGroup.closedDate.visibility = View.GONE
                    Toast.makeText(viewGroup.progressBar.context, it.message, Toast.LENGTH_LONG)
                        .show()
                }
            }
        })
    }


    private fun processExtras() {
        arguments?.getInt("position")?.let {
            positionArgs = it
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        val actionbar = (activity as AppCompatActivity).supportActionBar
        actionbar?.title =
            resources.getString(R.string.app_name)
        actionbar?.setDisplayHomeAsUpEnabled(false)
        actionbar?.setDisplayHomeAsUpEnabled(false)
    }
}