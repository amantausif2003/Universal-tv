package com.remote.control.allsmarttv.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.remote.control.allsmarttv.databinding.DeviceSearchingItemBinding
import com.remote.control.allsmarttv.utils.DevicesInfoUtil


class SearchingTvAdapter(var callBackItem: SearchingTvAdapterCallBack) :
    RecyclerView.Adapter<SearchingTvAdapter.ViewHolder>() {

    private var mContext: Context? = null
    private var hubAdapter: ArrayList<DevicesInfoUtil>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = DeviceSearchingItemBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        hubAdapter?.let {
            holder.tvName.text = it[position].name
        }

        //holder.tvName.setBackgroundResource(Constants.colorArray[position])

    }

    fun updateDeviceList(deviceList: ArrayList<DevicesInfoUtil>) {
        hubAdapter = deviceList
    }

    override fun getItemCount(): Int {
        return hubAdapter!!.size
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