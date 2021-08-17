package com.example.githubapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.githubapp.models.GitRepoInfo
import kotlinx.android.synthetic.main.github_repo_info_item.view.*

class GitHubInfoRecyclerViewAdapter(
    repoInfo: List<GitRepoInfo>,
    private val context: Context,
    private val onItemClickListener: OnItemClickListener?
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    var repoDetailList: List<GitRepoInfo> = repoInfo

    inner class GitHubInfoRecyclerViewHolder(
        itemView: View,
        private val onItemViewClick: OnItemClickListener?
    ) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        var avatarIcon: ImageView = itemView.iconView
        var pullRequestTitle: TextView = itemView.gitPrTitle
        var moreDetails: TextView = itemView.detailsText

        init {
            itemView.setOnClickListener(this)
            moreDetails.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            onItemViewClick?.onItemClick(bindingAdapterPosition)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return GitHubInfoRecyclerViewHolder(
            LayoutInflater
                .from(parent.context)
                .inflate(R.layout.github_repo_info_item, parent, false), onItemClickListener
        )
    }

    override fun getItemCount(): Int {
        return repoDetailList.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is GitHubInfoRecyclerViewHolder) {
            if (repoDetailList.isNotEmpty() && position < repoDetailList.size) {
                holder.pullRequestTitle.text = repoDetailList[position].title
                holder.moreDetails.visibility = View.VISIBLE
                val imageUrl: String = repoDetailList[position].head.user.avatar_url
                if (imageUrl.isNotEmpty()) {
                    Glide.with(context).load(imageUrl).into(holder.avatarIcon)
                }
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}