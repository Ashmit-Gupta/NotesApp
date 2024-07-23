package com.ashmit.notes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecyclerViewAdapter(private var arraylist : ArrayList<NotesModel>) : RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder>() {

    private lateinit var mListener: onItemClickListener
    private lateinit var mLongClickListener: onItemLongClickListener


    interface onItemClickListener{
        fun onItemClick(position: Int)

    }
    interface onItemLongClickListener{
        fun onItemLongClick(position: Int)
    }

    fun setOnItemLongClickListener(listener: onItemLongClickListener){
        mLongClickListener = listener
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    class MyViewHolder(itemView :View , listener :onItemClickListener , longListener: onItemLongClickListener):RecyclerView.ViewHolder(itemView){

        val title = itemView.findViewById<TextView>(R.id.title)
        val content = itemView.findViewById<TextView>(R.id.content)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition )
            }
            itemView.setOnLongClickListener{
                longListener.onItemLongClick(adapterPosition)
                true
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_notes , parent , false)
        return MyViewHolder(itemView , mListener , mLongClickListener)
    }

    override fun getItemCount(): Int {
        return arraylist.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentItem = arraylist[position]
        holder.title.text = currentItem.title
        holder.content.text = getPreview(currentItem.content!!)
    }
    private fun getPreview(content :String , maxLength:Int = 200) :String{
        return if(content.length > maxLength){
            content.substring(0 , maxLength) + " ..."
        }else{
            return content
        }
    }
}
