package daniel.brian.autoexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.autoexpress.data.CartProduct
import daniel.brian.autoexpress.firebase.FirebaseCommon
import daniel.brian.autoexpress.utils.Resource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailsViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val firebaseCommon: FirebaseCommon
) : ViewModel() {

    private val _addToCart = MutableStateFlow<Resource<CartProduct>>(Resource.Unspecified())
    val addToCart = _addToCart.asStateFlow()

    fun addUpdateInCart(cartProduct: CartProduct){
        firestore.collection("user").document(auth.uid!!).collection("cart")
            .whereEqualTo("product.id", cartProduct.product.id).get()
            .addOnSuccessListener {
                it.documents.let {
                    if (it.isEmpty()){
                        addNewProduct(cartProduct)
                    }else{
                        val product = it.first().toObject(CartProduct::class.java)
                        if(product == cartProduct){
                            val documentId = it.first().id
                            increaseQuantity(documentId,cartProduct)
                        }else{
                            addNewProduct(cartProduct)
                        }
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _addToCart.value = Resource.Error(it.message.toString())
                }
            }

    }

    private fun increaseQuantity(documentId: String, cartProduct: CartProduct) {
        firebaseCommon.increaseQuantity(documentId){_,e ->
            viewModelScope.launch {
                if (e != null){
                    _addToCart.value = Resource.Error(e.message.toString())
                }else{
                    _addToCart.value = Resource.Success(cartProduct)
                }
            }
        }

    }

    private fun addNewProduct(cartProduct: CartProduct) {
        firebaseCommon.addProductToCart(cartProduct = cartProduct){addProducts,e ->
            viewModelScope.launch {
                if (e != null){
                    _addToCart.value = Resource.Error(e.message.toString())
                }else{
                    _addToCart.value = Resource.Success(addProducts!!)
                }
            }
        }
    }
}