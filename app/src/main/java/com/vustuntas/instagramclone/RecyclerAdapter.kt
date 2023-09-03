package com.vustuntas.instagramclone

import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import java.util.ArrayList

class RecyclerAdapter (var postArrayList : ArrayList<Post>): RecyclerView.Adapter<RecyclerAdapter.PostsVH>() {
    class PostsVH(itemView : View) : RecyclerView.ViewHolder(itemView){

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostsVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view,parent,false)
        return PostsVH(itemView)
    }

    override fun onBindViewHolder(holder: PostsVH, position: Int) {
        val postObject = postArrayList.get(position) as Post
        println(postObject.comment)
        val imageView = holder.itemView.findViewById<ImageView>(R.id.gorselRecycler)
        Picasso.get().load(postObject.URL).into(imageView)
        holder.itemView.findViewById<TextView>(R.id.userEmailRecycler).text = postObject.userMail
        holder.itemView.findViewById<TextView>(R.id.commentTextRecycler).text = postObject.comment
    }

    override fun getItemCount(): Int {
        return postArrayList.size
    }
}