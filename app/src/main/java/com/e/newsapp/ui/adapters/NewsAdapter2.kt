package com.e.newsapp.ui.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.e.newsapp.data.models.Article
import com.e.newsapp.databinding.ItemArticlePreviewBinding

class NewsAdapter2 : PagingDataAdapter<Article, NewsAdapter2.ArticleViewHolder>(DifferCallback) {

    class ArticleViewHolder( val itemBinding: ItemArticlePreviewBinding) : RecyclerView.ViewHolder(itemBinding.root)

    object  DifferCallback: DiffUtil.ItemCallback<Article>(){
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
           return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this , DifferCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
     val itemBinding = ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ArticleViewHolder(itemBinding)
    }

    override fun getItemCount(): Int {
        return  differ.currentList.size
    }
    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = differ.currentList[position]
        article?.let {
            holder.itemBinding.apply {
                Glide.with(holder.itemView).load(article.urlToImage).into(ivArticleImage)
                tvSource.text = article.source?.name
                tvDescription.text = article.description
                tvPublishedAt.text = article.publishedAt
                tvTitle.text = article.title
                setOnClickListener {
                    onItemClickListener?.let {
                        it(article)
                    }
                }
            }
        }
    }
//
    private var onItemClickListener : ((Article)-> Unit) ? = null

    private fun setOnClickListener(listener : ((Article)-> Unit)){
        onItemClickListener = listener
    }

//    val differ = AsyncListDiffer(this , differCallback)
}

//    class ArticleViewHolder( val itemBinding: ItemArticlePreviewBinding) : RecyclerView.ViewHolder(itemBinding.root)
//
//    private val differCallback = object  : DiffUtil.ItemCallback<Article>(){
//        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
//           return oldItem.url == newItem.url
//        }
//
//        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    val differ = AsyncListDiffer(this , differCallback)
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
//     val itemBinding = ItemArticlePreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return ArticleViewHolder(itemBinding)
//    }
//
//    override fun getItemCount(): Int {
//       return  differ.currentList.size
//    }
//
//    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
//        val article = differ.currentList[position]
//        holder.itemBinding.apply {
//            Glide.with(holder.itemView).load(article.urlToImage).into(ivArticleImage)
//            tvSource.text = article.source.name
//            tvDescription.text = article.description
//            tvPublishedAt.text = article.publishedAt
//            tvTitle.text  = article.title
//            setOnClickListener {
//                onItemClickListener?.let {
//                    it(article)
//                }
//            }
//        }
//    }
//
//    private var onItemClickListener : ((Article)-> Unit) ? = null
//
//    private fun setOnClickListener(listener : ((Article)-> Unit)){
//        onItemClickListener = listener
//    }