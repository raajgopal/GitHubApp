package com.example.githubapp.api

import com.example.githubapp.models.GitRepoInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("/repos/{owner}/{repo}/pulls")
    fun fetchGitPullRequestInfo(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Query("state") state: String
    ): Call<List<GitRepoInfo>>
}