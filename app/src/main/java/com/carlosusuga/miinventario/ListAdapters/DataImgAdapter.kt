package com.carlosusuga.miinventario.ListAdapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.carlosusuga.miinventario.Activity.MainActivity
import com.carlosusuga.miinventario.Entities.DataImgEntity
import com.carlosusuga.miinventario.R
import com.carlosusuga.miinventario.ListAdapters.DataImgAdapter.PhotoHolder

class DataImgAdapter : ListAdapter<DataImgEntity, PhotoHolder> (DIFF_CALLBACK) {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoHolder(itemView)
    }

    override fun onBindViewHolder(holder: PhotoHolder, position: Int) {
        val currentPhoto = getItem(position)
        holder.tvNombreFoto.text = currentPhoto!!.namePhoto
        holder.tvDetalleFoto.text = currentPhoto.detailsPhoto
        holder.tvFechaFoto.text = currentPhoto.datePhoto
        holder.tvUriFoto.text = currentPhoto.imagePath

        Glide.with(holder.imgFotoMini.context)
            .load(currentPhoto.imagePath)
            .into(holder.imgFotoMini)
    }

    fun getPhotoAt(position: Int) : DataImgEntity? {
        return getItem(position)
    }

    inner class PhotoHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val tvNombreFoto: TextView
        val tvDetalleFoto: TextView
        val tvFechaFoto: TextView
        val tvUriFoto: TextView
        val imgFotoMini: ImageView

        init {
            tvNombreFoto = itemView.findViewById(R.id.tvNamePhoto)
            tvDetalleFoto = itemView.findViewById(R.id.tvDetailsPhoto)
            tvFechaFoto = itemView.findViewById(R.id.tvDatePhoto)
            tvUriFoto = itemView.findViewById(R.id.tvUriPhoto)
            imgFotoMini = itemView.findViewById(R.id.imgMiniCard)
            itemView.setOnClickListener {
                val position = adapterPosition
                if (listener != null && position != RecyclerView.NO_POSITION){
                    listener!!.onItemClick(getItem(position))
                }
            }
        }
    }

    interface OnItemClickListener{
        fun onItemClick(dataImgEntity: DataImgEntity)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        this.listener = listener
    }

    companion object{
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<DataImgEntity> =
            object : DiffUtil.ItemCallback<DataImgEntity>(){
                override fun areItemsTheSame(
                    oldItem: DataImgEntity,
                    newItem: DataImgEntity
                ): Boolean {
                    return oldItem.id == newItem.id
                }

                @SuppressLint("DiffUtilEquals")
                override fun areContentsTheSame(
                    oldItem: DataImgEntity,
                    newItem: DataImgEntity
                ): Boolean {
                    return oldItem.namePhoto == newItem.namePhoto &&
                            oldItem.detailsPhoto == newItem.detailsPhoto &&
                            oldItem.datePhoto == newItem.datePhoto &&
                            oldItem.imagePath == newItem.imagePath
                }

            }
    }
}