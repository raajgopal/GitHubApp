package com.example.githubapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.githubapp.GitHubRepository
import com.example.githubapp.api.ApiService

class ViewModelFactory(
    private val apiService: ApiService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(GitHubViewModel::class.java)) {
            return GitHubViewModel(GitHubRepository(apiService)) as T
        }
        throw IllegalArgumentException("Unknown class name")
    }
}