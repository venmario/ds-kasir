package com.example.restoapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.duniasteak.restoapp.adapter.home.OrderListAdapter
import com.example.restoapp.databinding.RecviewCategoryBinding
import com.example.restoapp.model.Category

class CategoryListAdapter(private val categoryList:ArrayList<Category>,private val listener: ProductListAdapter.IProductListListener):RecyclerView.Adapter<CategoryListAdapter.CategoryListViewHolder>() {
    class CategoryListViewHolder(var binding: RecviewCategoryBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryListViewHolder {
        val binding = RecviewCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CategoryListViewHolder(binding)
    }

    override fun getItemCount(): Int = categoryList.size

    override fun onBindViewHolder(holder: CategoryListViewHolder, position: Int) {
        val category = categoryList[position]
        with(holder.binding){
            textCategoryName?.text = category.name
            val productListAdapter = ProductListAdapter(category.product!!, listener)
            childRecView?.layoutManager= GridLayoutManager(this.root.context, 3)
            childRecView?.isNestedScrollingEnabled = false
            childRecView?.adapter = productListAdapter
        }
    }

    fun updateCategoryList(newestCategoryList: ArrayList<Category>){
        categoryList.clear()
        categoryList.addAll(newestCategoryList)
        notifyDataSetChanged()
    }
}