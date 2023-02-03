package com.remote.control.allsmarttv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.remote.control.allsmarttv.databinding.DeviceSearchingItemBinding


class SearchingTvAdapter(var callBackItem: SearchingTvAdapterCallBack) :
    RecyclerView.Adapter<SearchingTvAdapter.ViewHolder>() {

    private var mContext: Context? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DeviceSearchingItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

//        holder.tvName.setBackgroundResource(Constants.colorArray[position])

    }

    override fun getItemCount(): Int {
        return 0
    }

    inner class ViewHolder(itemView: DeviceSearchingItemBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        var tvName: TextView = itemView.reItemName

        init {

            tvName.setOnClickListener {
                callBackItem.selectTv(adapterPosition)
            }

        }
    }

    interface SearchingTvAdapterCallBack {
        fun selectTv(itemPosition: Int)
    }
}