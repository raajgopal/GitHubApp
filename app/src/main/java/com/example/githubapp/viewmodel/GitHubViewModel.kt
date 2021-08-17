package com.example.githubapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubapp.repository.GitHubRepository
import com.example.githubapp.models.GitRepoInfo
import com.example.githubapp.models.Resource

class GitHubViewModel(private val repository: GitHubRepository) : ViewModel() {
    private val gitHubRepoList = MutableLiveData<Resource<List<GitRepoInfo>>>()

    suspend fun fetchGitHubClosedPRInfo(owner: String, repo: String) {
        gitHubRepoList.postValue(Resource.loading(null))
        repository.fetchGitRepoInfoFromApi(gitHubRepoList, owner, repo)
    }

    fun getGitHubRepoList(): LiveData<Resource<List<GitRepoInfo>>> {
        return gitHubRepoList
    }
}