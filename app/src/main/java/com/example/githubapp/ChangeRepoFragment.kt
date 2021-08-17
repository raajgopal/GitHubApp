package com.example.githubapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.change_repo_layout.view.*

class ChangeRepoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.change_repo_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val actionbar = (activity as AppCompatActivity).supportActionBar
        actionbar?.title =
            resources.getString(R.string.change_repo_page)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        actionbar?.setDisplayHomeAsUpEnabled(true)
        setupUi(view)
    }

    private fun setupUi(viewGroup: View) {
        viewGroup.submitButton.setOnClickListener {
            val ownerText = viewGroup.ownerText.editText?.text
            val repoText = viewGroup.repoText.editText?.text
            if (!ownerText.isNullOrEmpty() && !repoText.isNullOrEmpty()) {
                loadFragment(GitHubListFragment(), ownerText.toString(), repoText.toString())
            } else {
                Toast.makeText(
                    context,
                    "Owner/Repo info is incomplete, Proceeding with default values",
                    Toast.LENGTH_LONG
                )
                    .show()
                loadFragment(GitHubListFragment(), null, null)
            }
        }
    }

    private fun loadFragment(fragment: Fragment, ownerText: String?, repoText: String?) {
        fragment.apply {
            arguments = Bundle().apply {
                putString("owner", ownerText)
                putString("repo", repoText)
            }
        }

        val newTransaction = activity?.supportFragmentManager?.beginTransaction()
        newTransaction?.replace(R.id.contentFragment, fragment)
        newTransaction?.commit()
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