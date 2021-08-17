package com.example.githubapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.githubapp.api.ApiBuilder
import com.example.githubapp.models.ApiConstants
import com.example.githubapp.models.GitRepoInfo
import com.example.githubapp.models.Status
import com.example.githubapp.viewmodel.GitHubViewModel
import com.example.githubapp.viewmodel.ViewModelFactory
import kotlinx.android.synthetic.main.github_repo_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GitHubListFragment : Fragment(), GitHubInfoRecyclerViewAdapter.OnItemClickListener {

    private lateinit var viewModel: GitHubViewModel
    private lateinit var adapter: GitHubInfoRecyclerViewAdapter
    private var owner: String = ApiConstants.owner
    private var repo: String = ApiConstants.repo

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val viewGroup = inflater.inflate(R.layout.github_repo_fragment, container, false)
        if (savedInstanceState == null) {
            setupUI(viewGroup)
        }
        return viewGroup
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        processExtras()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
        CoroutineScope(Dispatchers.Default).launch {
            viewModel.fetchGitHubClosedPRInfo(owner, repo)
        }
    }

    private fun setupUI(viewGroup: View) {
        viewGroup.contentListView.layoutManager = LinearLayoutManager(context)
        adapter = GitHubInfoRecyclerViewAdapter(
            arrayListOf(),
            viewGroup.contentListView.context,
            this
        )
        viewGroup.contentListView.addItemDecoration(
            DividerItemDecoration(
                viewGroup.contentListView.context,
                (viewGroup.contentListView.layoutManager as LinearLayoutManager).orientation
            )
        )
        viewGroup.contentListView.adapter = adapter
        viewGroup.changeRepoButton.setOnClickListener {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.contentFragment, ChangeRepoFragment())
            transaction?.commit()
        }
    }

    private fun updateList(repoInfoList: List<GitRepoInfo>) {
        adapter.repoDetailList = repoInfoList
        adapter.notifyDataSetChanged()
    }

    private fun setupObserver(viewGroup: View) {
        viewModel.getGitHubRepoList().observe(viewLifecycleOwner, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    viewGroup.progressBar.visibility = View.GONE
                    it.data?.let { repoInfoList -> updateList(repoInfoList) }
                    viewGroup.contentListView.visibility = View.VISIBLE
                    viewGroup.textView.text = context?.resources?.getString(R.string.closed_prs)
                    viewGroup.textView.visibility = View.VISIBLE
                    viewGroup.changeRepoButton.visibility = View.VISIBLE
                }
                Status.ERROR -> {
                    //Handle Error
                    viewGroup.progressBar.visibility = View.GONE
                    viewGroup.changeRepoButton.visibility = View.VISIBLE
                    viewGroup.textView.text = it.message
                    viewGroup.textView.visibility = View.VISIBLE
                }
                Status.LOADING -> {
                    viewGroup.progressBar.visibility = View.VISIBLE
                    viewGroup.contentListView.visibility = View.GONE
                    viewGroup.textView.visibility = View.GONE
                    viewGroup.changeRepoButton.visibility = View.GONE
                }
            }
        })
    }

    private fun processExtras() {
        arguments?.getString("owner")?.let {
            owner = it
        }
        arguments?.getString("repo")?.let {
            repo = it
        }
    }


    private fun loadFragment(fragment: Fragment, position: Int) {
        fragment.apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
        val transaction = activity?.supportFragmentManager?.beginTransaction()
        transaction?.addToBackStack("GitHubDetailFragment")
        transaction?.replace(R.id.contentFragment, fragment)
        transaction?.commit()
    }

    override fun onItemClick(position: Int) {
        loadFragment(GitHubDetailFragment(), position)
    }
}