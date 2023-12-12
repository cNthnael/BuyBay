package com.sentraboga.buybay.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import java.text.NumberFormat
import java.util.*
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.RequestOptions
import com.sentraboga.buybay.data.Product
import com.sentraboga.buybay.databinding.SpecialRvItemBinding

class SpecialProductsAdp: RecyclerView.Adapter<SpecialProductsAdp.SpecialProductsViewHolder>() {

    inner class SpecialProductsViewHolder(private val binding: SpecialRvItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView)
                    .load(product.images[0])
                    .apply(RequestOptions()
                        .override(400, 400)
                        .centerCrop()
                    )
                    .into(imageSpecialRvItem)

                tvSpecialProductName.text = product.name

                val formattedPrice: String = if (product.offerPercentage != null) {
                    val discountedPrice = product.price * (1 - product.offerPercentage)
                    NumberFormat.getNumberInstance(Locale.US).format(discountedPrice)
                } else {
                    NumberFormat.getNumberInstance(Locale.US).format(product.price)
                }

                tvSpecialProductPrice.text = "Rp $formattedPrice"
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SpecialProductsViewHolder {
        return SpecialProductsViewHolder(
            SpecialRvItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: SpecialProductsViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick: ((Product) -> Unit)? = null

}