package com.example.restoapp.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restoapp.R
import com.example.restoapp.adapter.CategoryListAdapter
import com.example.restoapp.adapter.ProductListAdapter
import com.example.restoapp.databinding.FragmentProductManagementBinding
import com.example.restoapp.model.Category
import com.example.restoapp.model.Product
import com.example.restoapp.viewmodel.ProductViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar


class ProductManagementFragment : Fragment(), ProductListAdapter.IProductListListener {

    private lateinit var binding: FragmentProductManagementBinding
    private val viewModel: ProductViewModel by activityViewModels()
    private lateinit var navController: NavController
    private val categories:ArrayList<Category> = arrayListOf()
    private val categoryListAdapter = CategoryListAdapter(arrayListOf(),this)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        disableButton()
        with(binding){
            recViewCategory?.layoutManager = LinearLayoutManager(context)
            recViewCategory?.adapter = categoryListAdapter

            buttonAddProduct?.setOnClickListener {
                viewModel.selectProduct(null)
                val action = ProductManagementFragmentDirections.actionProductForm()
                navController.navigate(action)
            }

            refreshLayout?.setOnRefreshListener {
                viewModel.getAllProducts(requireActivity())
                categoryListAdapter.updateCategoryList(arrayListOf())
                progressBar?.visibility = View.VISIBLE
                disableButton()
            }
        }

        viewModel.getAllProducts(requireActivity())

        observeViewModel(view)
    }

    private fun observeViewModel(view:View) {
        viewModel.categoriesWithProduct.observe(viewLifecycleOwner){
            if (it.isSuccess){
                categoryListAdapter.updateCategoryList(it.data!!)
                with(binding){
                    refreshLayout?.isRefreshing = false
                    progressBar?.visibility = View.GONE
                }
                enableButton()
                categories.clear()
                for (category in it.data!!){
                    categories.add(Category(category.id,category.name,null))
                }
                viewModel.populateCategories(categories)
            }
        }

        viewModel.createdProduct.observe(viewLifecycleOwner){
            if (it.isSuccess){
                Snackbar.make(view, "Product successfully created", Snackbar.LENGTH_SHORT)
                    .show()
                viewModel.setFalseCreateProduct()
            }
        }
        viewModel.updatedProduct.observe(viewLifecycleOwner){
            if (it.isSuccess){
                Snackbar.make(view, "Product successfully updated", Snackbar.LENGTH_SHORT)
                    .show()
                viewModel.setFalseUpdateProduct()
            }
        }
        viewModel.deletedProduct.observe(viewLifecycleOwner){
            if (it.isSuccess){
                Snackbar.make(view, "Product successfully deleted", Snackbar.LENGTH_SHORT)
                    .show()
                viewModel.getAllProducts(requireActivity())
                categoryListAdapter.updateCategoryList(arrayListOf())
                binding.progressBar?.visibility = View.VISIBLE
                disableButton()
                viewModel.setFalseDeleteProduct()
            }
        }
    }
    override fun selectProduct(product: Product) {
        viewModel.selectProduct(product)
        val action = ProductManagementFragmentDirections.actionProductForm()
        navController.navigate(action)
    }

    override fun deleteProduct(productId: Int) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Pick Up Order")
            .setMessage("Are you sure want to delete this product?")
            .setNegativeButton("No"){ dialog,_->
                dialog.dismiss()
            }
            .setPositiveButton("Yes"){dialog,_->
                viewModel.deleteProdcut(requireActivity(), productId)
                dialog.dismiss()
            }
        .show()
    }

    private fun disableButton(){
        with(binding){
            buttonAddProduct?.backgroundTintList = resources.getColorStateList(com.example.restoapp.R.color.md_theme_primary_disable)
            buttonAddProduct?.setTextColor(resources.getColorStateList(com.example.restoapp.R.color.md_theme_secondary_disable))
            buttonAddProduct?.isEnabled = false
        }
    }

    private fun enableButton(){
        with(binding){
            buttonAddProduct?.backgroundTintList = resources.getColorStateList(com.example.restoapp.R.color.md_theme_primary)
            buttonAddProduct?.setTextColor(resources.getColorStateList(com.example.restoapp.R.color.md_theme_secondary))
            buttonAddProduct?.isEnabled = true
        }
    }
}