package com.example.traveljournalapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.traveljournalapp.R
import com.example.traveljournalapp.databinding.TravelLayoutBinding
import com.example.traveljournalapp.data.model.Travel

class TravelAdapter(var travels : List<Travel>, val callBack : ItemListener) : RecyclerView.Adapter<TravelAdapter.TravelViewHolder>(){

    interface ItemListener {
        fun onItemClicked(index : Int)
        fun onDeleteClicked(travel: Travel)
    }

    inner class TravelViewHolder(private val binding: TravelLayoutBinding)
        :RecyclerView.ViewHolder(binding.root) , View.OnClickListener {

        init {
            binding.root.setOnClickListener(this)
            binding.deleteTravelBtn.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    callBack.onDeleteClicked(travels[position])
                }
            }

        }

        override fun onClick(v: View?) {
            callBack.onItemClicked(adapterPosition)
        }

        fun bind(travel: Travel) {
            binding.countryTitle.text = travel.countryName
            binding.destinationTitle.text = travel.destinationName
            binding.travelDate.text = travel.date
            Glide.with(binding.root).load(travel.photo).circleCrop().into(binding.travelImage)
        }
    }

    fun filterList(filteredList: List<Travel>) {
        travels = filteredList
        notifyDataSetChanged()
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        TravelViewHolder(TravelLayoutBinding.inflate(LayoutInflater.from(parent.context) , parent , false))


    override fun onBindViewHolder(holder: TravelViewHolder, position: Int) {
        var animation = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.anim_one)
        holder.itemView.startAnimation(animation)
        holder.bind(travels[position])
    }

    override fun getItemCount() =
        travels.size
}