package com.example.githubapp.models

data class GitRepoInfo(val created_at: String, val closed_at: String, val title: String, val state: String, val head: HeadModel)