package com.e.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.e.newsapp.data.models.Article
import com.e.newsapp.databinding.ItemArticlePreviewBinding
import com.e.newsapp.utils.loadImage

class NewsAdapter() : RecyclerView.Adapter<NewsAdapter.ArticleViewHolder>() {
    class ArticleViewHolder(val itemBinding: ItemArticlePreviewBinding) : RecyclerView.ViewHolder(itemBinding.root)

    private val differCallBack = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallBack)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemBinding = ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        with(holder){
            itemBinding.tvDescription.text = article.title
        }
        holder.itemBinding.apply {
            tvTitle.text = article.title
            tvPublishedAt.text = article.publishedAt
            tvDescription.text = article.description
            tvSource.text = article.source?.name
            ivArticleImage.loadImage(article.urlToImage)
//            Glide.with(root).load(article.urlToImage).into(ivArticleImage)
            root.setOnClickListener {
                onItemClickListener?.let {
                    it(article)
                }
            }
        }
    }

    private var onItemClickListener: ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Article) -> Unit)) {
        onItemClickListener = listener
    }
}