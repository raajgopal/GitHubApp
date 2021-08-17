package com.example.githubapp.repository

import androidx.lifecycle.MutableLiveData
import com.example.githubapp.api.ApiService
import com.example.githubapp.models.ApiConstants
import com.example.githubapp.models.GitRepoInfo
import com.example.githubapp.models.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class GitHubRepository(private val apiService: ApiService) {

    suspend fun fetchGitRepoInfoFromApi(
        repoInfoLiveData: MutableLiveData<Resource<List<GitRepoInfo>>>,
        owner: String,
        repo: String
    ) = withContext(Dispatchers.Default) {
        val repoInfoApiCall = apiService.fetchGitPullRequestInfo(
            owner,
            repo,
            ApiConstants.state
        )
        repoInfoApiCall.enqueue(object : Callback<List<GitRepoInfo>> {
            override fun onFailure(call: Call<List<GitRepoInfo>>?, t: Throwable?) {
                repoInfoLiveData.postValue(
                    Resource.error(
                        "Problem calling API {${t?.message}}",
                        emptyList()
                    )
                )
            }

            override fun onResponse(
                call: Call<List<GitRepoInfo>>?,
                response: Response<List<GitRepoInfo>>?
            ) {
                response?.let {
                    if (it.isSuccessful && it.body() is List<GitRepoInfo>) {
                        repoInfoLiveData.postValue(Resource.success(it.body() as List<GitRepoInfo>))
                    } else {
                        repoInfoLiveData.postValue(
                            Resource.error(
                                "Data is Unavailable with given repo details",
                                emptyList()
                            )
                        )
                    }
                }
            }
        })
    }
}