package com.example.restoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.duniasteak.restoapp.adapter.home.OrderListAdapter
import com.example.restoapp.databinding.ProductCardBinding
import com.example.restoapp.databinding.RecviewCategoryBinding
import com.example.restoapp.model.Category
import com.example.restoapp.model.Order
import com.example.restoapp.model.Product
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.util.loadImage
import com.example.restoapp.view.ProductManagementFragmentDirections
import com.example.restoapp.viewmodel.ProductViewModel

class ProductListAdapter(private val productList:ArrayList<Product>,private val listener: IProductListListener):RecyclerView.Adapter<ProductListAdapter.ProductListViewHolder>(){
    class ProductListViewHolder(var binding: ProductCardBinding): RecyclerView.ViewHolder(binding.root)
    interface IProductListListener{
        fun selectProduct(product: Product)
        fun deleteProduct(productId: Int)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductListViewHolder {
        val binding = ProductCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductListViewHolder(binding)
    }
    override fun getItemCount(): Int = productList.size

    override fun onBindViewHolder(holder: ProductListViewHolder, position: Int) {
        val product = productList[position]
        with(holder.binding){
            textProductName?.text = product.name
            imageView?.loadImage(product.image,progressBar!!)
            textDesc?.text = product.description
            textPrice?.text = convertToRupiah( product.price!!)
            textPoint?.text = "${product.poin!!} pts"
            buttonDelete?.setOnClickListener {
                listener.deleteProduct(product.id!!)
            }
            buttonEdit?.setOnClickListener {
                listener.selectProduct(product)
            }
        }
    }

}