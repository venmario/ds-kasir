package com.example.restoapp.view

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.cloudinary.Cloudinary
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.cloudinary.utils.ObjectUtils
import com.example.restoapp.R
import com.example.restoapp.databinding.FragmentProductFormBinding
import com.example.restoapp.global.Secret
import com.example.restoapp.model.Category
import com.example.restoapp.model.Product
import com.example.restoapp.util.convertToRupiah
import com.example.restoapp.util.loadImage
import com.example.restoapp.viewmodel.ProductViewModel
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import java.net.URI


class ProductFormFragment : Fragment() {

    private lateinit var binding: FragmentProductFormBinding
    private val viewModel: ProductViewModel by activityViewModels()
    private lateinit var adapterCategory : ArrayAdapter<Category>
    private var items:ArrayList<Category> = arrayListOf()
    private lateinit var product: Product
    private lateinit var updatedProduct: Product
    private lateinit var navController: NavController
    val SELECT_IMAGE = 200
    val UPDATE_IMAGE = 201

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.categories.observe(viewLifecycleOwner){
            if (it.isNotEmpty()){
                items = it
                Log.d("categories",it.toString())
                adapterCategory = ArrayAdapter<Category>(requireContext(), R.layout.item_category, items)
                (binding.itemCategory as MaterialAutoCompleteTextView).apply {
                    setAdapter(adapterCategory)
                    setText(items[0].name, false)
                }
            }
        }
        viewModel.selectedProduct.observe(viewLifecycleOwner) {
            if (it != null) {//update product
                product = Product(it.id,it.name,it.description,it.image,it.available,it.categoryId,it.price,it.poin)
                updatedProduct = Product()
                updatedProduct.id = product.id
                with(binding){
                    textName?.setText(product.name)
                    textPrice?.setText(product.price!!.toString())
                    textDescription?.setText(product.description)
                    if (product.available == true){
                        radioGroup?.check(R.id.radioAvailable)
                    }else{
                        radioGroup?.check(R.id.radioNotAvailable)
                    }
                    imageView?.loadImage(product.image, progressBar!!)
                    for (item in items){
                        if(item.id == product.categoryId){
                            (itemCategory as MaterialAutoCompleteTextView).apply{
                                setText(item.name, false)
                            }
                            break
                        }
                    }

                    var categoryId = product.categoryId
                    itemCategory?.setOnItemClickListener{ _, _, position, _ ->
                        categoryId = items[position].id
                    }

                    buttonUploadImage?.setOnClickListener {
                        val i = Intent()
                        i.setType("image/*")
                        i.setAction(Intent.ACTION_GET_CONTENT)
                        Log.d("select image","update")

                        startActivityForResult(Intent.createChooser(i, "Select Image"), UPDATE_IMAGE)
                    }

                    buttonSave?.setOnClickListener {
                        if (textName?.text.toString() != product.name){
                            updatedProduct.name = textName?.text.toString()
                        }
                        if(textPrice?.text.toString().toInt() != product.price){
                            updatedProduct.price = textPrice?.text.toString().toInt()
                            updatedProduct.poin = updatedProduct.price!!/1000
                        }
                        if(textDescription?.text.toString() != product.description){
                            updatedProduct.description = textDescription?.text.toString()
                        }
                        val selectedId = radioGroup?.checkedRadioButtonId
                        val currentAvailable = radioAvailable?.id == selectedId
                        val prevAvailable = product.available
                        if(prevAvailable != currentAvailable){
                            updatedProduct.available = currentAvailable
                        }
                        if(categoryId != product.id){
                            updatedProduct.categoryId = categoryId
                        }
                        Log.d("updated produc", updatedProduct.toString())
                        Log.d("prev produc", product.toString())
                        viewModel.updateProduct(requireActivity(), updatedProduct)
                        navController.popBackStack()
                    }
                }
            }else{//create product
                product = Product()
                product.image = "https://plus.unsplash.com/premium_photo-1666978195894-b2e3a3f14d9b?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D"
                with(binding){

                    var categoryId = items[0].id
                    itemCategory?.setOnItemClickListener{ _, _, position, _ ->
                        categoryId = items[position].id
                    }

                    imageView?.loadImage(product.image, progressBar!!)

                    buttonUploadImage?.setOnClickListener {
                        val i = Intent()
                        i.setType("image/*")
                        i.setAction(Intent.ACTION_GET_CONTENT)
                        Log.d("select image","create")

                        startActivityForResult(Intent.createChooser(i, "Select Image"), SELECT_IMAGE)
                    }

                    buttonSave?.setOnClickListener {
                        product.name = textName?.text.toString()
                        product.price = textPrice?.text.toString().toInt()
                        product.poin = product.price!! / 1000
                        product.description = textDescription?.text.toString()
                        val selectedId = radioGroup?.checkedRadioButtonId
                        product.available = radioAvailable?.id == selectedId
                        product.categoryId = categoryId
                        Log.d("created produc", product.toString())
                        viewModel.createProduct(requireActivity(), product)
                        navController.popBackStack()
                    }
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_IMAGE) {
                val selectedImageUri = data?.data
                if (selectedImageUri != null) {
                    binding.imageView?.setImageURI(selectedImageUri)
                    uploadToCloudinary(selectedImageUri,"create")
                }
            }else if (requestCode == UPDATE_IMAGE){
                val selectedImageUri = data?.data
                if (selectedImageUri != null) {
                    binding.imageView?.setImageURI(selectedImageUri)
                    uploadToCloudinary(selectedImageUri,"update")
                }
            }
        }
    }

    private fun uploadToCloudinary(imageURI: Uri, type:String) {

        MediaManager.get().upload(imageURI).callback(object:UploadCallback{
            override fun onStart(requestId: String?) {
                binding.buttonUploadImage?.text = "Start upload"
            }

            override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                binding.buttonUploadImage?.text = "Uploading..."
            }

            override fun onSuccess(requestId: String?, resultData: MutableMap<Any?, Any?>?) {
                Log.d("url", resultData?.get("secure_url").toString())
                val url = resultData?.get("secure_url").toString()
                if (type == "create"){
                    product.image = url
                }else{
                    updatedProduct.image = url
                }
                binding.buttonUploadImage?.text = "Uploaded"
            }

            override fun onError(requestId: String?, error: ErrorInfo?) {
                Log.d("cloudinary", error?.description.toString())
                binding.buttonUploadImage?.text = error?.description.toString()
            }

            override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                Log.d("cloudinary", error?.description.toString())
                binding.buttonUploadImage?.text = error?.description.toString()
            }

        }).dispatch();
    }


}