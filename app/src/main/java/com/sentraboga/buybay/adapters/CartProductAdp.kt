package com.sentraboga.buybay.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sentraboga.buybay.data.CartProduct
import com.sentraboga.buybay.data.Product
import com.sentraboga.buybay.databinding.CartProductItemBinding
import com.sentraboga.buybay.databinding.SpecialRvItemBinding
import java.text.NumberFormat
import java.util.Locale

class CartProductAdp: RecyclerView.Adapter<CartProductAdp.CardProductViewHolder>() {

    inner class CardProductViewHolder(val binding: CartProductItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(cartProduct: CartProduct) {
            binding.apply {
                Glide.with(itemView)
                    .load(cartProduct.product.images[0])
                    .apply(
                        RequestOptions()
                        .override(400, 400)
                        .centerCrop()
                    )
                    .into(imgCartProd)

                tvProdName.text = cartProduct.product.name
                tvProdQuantity.text = cartProduct.quantity.toString()
                prodDuration.text = cartProduct.subsDuration

                val formattedPrice: String = if (cartProduct.product.offerPercentage != null) {
                    val discountedPrice = cartProduct.product.price * (1 - cartProduct.product.offerPercentage)
                    NumberFormat.getNumberInstance(Locale.US).format(discountedPrice)
                } else {
                    NumberFormat.getNumberInstance(Locale.US).format(cartProduct.product.price)
                }
                tvProdPrice.text = "Rp $formattedPrice"
            }
        }

    }

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>(){
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.product.id == newItem.product.id
        }

        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardProductViewHolder {
        return CardProductViewHolder(
           CartProductItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: CardProductViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)

        holder.itemView.setOnClickListener {
            onProdClick?.invoke(cartProduct)
        }
        holder.binding.increaseQ.setOnClickListener {
            onPlusClick?.invoke(cartProduct)
        }

        holder.binding.decreaseQ.setOnClickListener {
            onMinusClick?.invoke(cartProduct)
        }

    }

    var onProdClick: ((CartProduct) -> Unit)? = null
    var onPlusClick: ((CartProduct) -> Unit)? = null
    var onMinusClick: ((CartProduct) -> Unit)? = null

}