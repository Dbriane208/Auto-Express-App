package daniel.brian.autoexpress.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import daniel.brian.autoexpress.data.Product
import daniel.brian.autoexpress.databinding.ProductRvBinding

class AccessoriesAdapter: RecyclerView.Adapter<AccessoriesAdapter.AccessoriesViewHolder>() {
    inner class AccessoriesViewHolder(private val binding: ProductRvBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                itemBrandName.text = product.brand
                Glide.with(itemView).load(product.images[0]).into(itemProductImage)
                productName.text = product.name
                itemDescription.text = product.description
                ("Ksh " + product.price.toString()).also { itemOldPrice.text = it }
                itemOldPrice.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                if (product.offerPercentage != null){
                    ("Ksh " + (product.price - (product.price * product.offerPercentage)).toString()).also { itemNewPrice.text = it }
                }else{
                    ("Ksh " + product.price.toString()).also { itemNewPrice.text = it }
                }
            }

        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AccessoriesViewHolder {
        return AccessoriesViewHolder(
            ProductRvBinding.inflate(
                LayoutInflater.from(parent.context),parent,false
            )
        )
    }

    private val diffUtil = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return  oldItem == newItem
        }

    }

    val differ = AsyncListDiffer(this,diffUtil)

    override fun onBindViewHolder(holder: AccessoriesViewHolder, position: Int) {
        val accessories = differ.currentList[position]
        holder.bind(accessories)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}