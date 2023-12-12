package com.sentraboga.buybay.adapters

import android.graphics.Paint
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sentraboga.buybay.data.Product
import com.sentraboga.buybay.databinding.ProductRvItemBinding
import java.text.NumberFormat
import java.util.Locale

class BestProductAdp: RecyclerView.Adapter<BestProductAdp.BestProductViewHolder>() {

    inner class  BestProductViewHolder(private val binding: ProductRvItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView)
                    .load(product.images[0])
                    .apply(
                        RequestOptions()
                            .override(400, 400)
                            .centerCrop()
                    )
                    .into(imgProduct)

                if (product.offerPercentage != null) {
                    val remainingPricePercentage = 1f - product.offerPercentage
                    val priceAfterOffer = remainingPricePercentage * product.price
                    val formattedPriceAfterOff = NumberFormat.getNumberInstance(Locale.US).format(priceAfterOffer)
                    tvNewPrice.text = "Rp $formattedPriceAfterOff"

                    val formattedOldPrice = NumberFormat.getNumberInstance(Locale.US).format(product.price)
                    tvPrice.text = "Rp $formattedOldPrice"
                    tvPrice.paintFlags = tvPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    val formattedOldPrice = NumberFormat.getNumberInstance(Locale.US).format(product.price)
                    tvPrice.text = "Rp $formattedOldPrice"
                    tvPrice.paintFlags = 0
                    tvNewPrice.visibility = View.INVISIBLE
                }

                tvName.text = product.name
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
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BestProductViewHolder {
        return BestProductViewHolder(
            ProductRvItemBinding.inflate(
                LayoutInflater.from(parent.context)
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: BestProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)

        holder.itemView.setOnClickListener {
            onClick?.invoke(product)
        }
    }

    var onClick: ((Product) -> Unit)? = null

}