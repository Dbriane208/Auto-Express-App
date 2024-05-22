package daniel.brian.autoexpress.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.autoexpress.AutoExpressApplication
import daniel.brian.autoexpress.data.User
import daniel.brian.autoexpress.utils.RegisterValidation
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.utils.validateEmail
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import javax.inject.Inject
@HiltViewModel
class UserAccountViewModel @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val storage: StorageReference,
    app: Application //provides the content resolver
): AndroidViewModel(app) {

    /*
    - Getting the states and exposing them as read-only
    */
    private val _user = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val user = _user.asStateFlow()

    private val _updateInfo = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val updateInfo = _updateInfo.asStateFlow()

    init {
        getUser()
    }

    /*
    - The function will get the user from the firestore database
    - It also exposes the loading state and error state
    */
    private fun getUser() {
        viewModelScope.launch {
            _user.value = Resource.Loading()
        }

        firestore.collection("user").document(auth.uid!!).get()
            .addOnSuccessListener {
                val user = it.toObject(User::class.java)
                user?.let {
                    viewModelScope.launch {
                        _user.value = Resource.Success(it)
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _user.value = Resource.Error(it.message.toString())
                }
            }

    }

    /*
    - The function will update the user's information in the firestore database
    - It also exposes the loading state and error state
    - The function accepts two parameters: user and imageUri
    - The function validates the username and email and checks whether the user is authenticated
    - If the user is authenticated, the function will update the user's information in the firestore database
    */
    fun updateUser(user: User,imageUri: Uri?) {
        val areInputsValid = validateEmail(user.email) is RegisterValidation.Success
                && user.username.trim().isNotEmpty()

        if(!areInputsValid){
            viewModelScope.launch {
                _user.value = Resource.Error("Check your inputs..")
            }
            return
        }

        viewModelScope.launch {
            _updateInfo.value = Resource.Loading()
        }

        if(imageUri == null){
            saveUserInformation(user,true)
        }else{
            updateUserInformation(user,imageUri)
        }
    }

    /*
    - The function will save the user's information in the firestore database
    - It also exposes the loading state and error state
    - The function accepts two parameters: user and retrieveOldImage
    - We're using transaction to perform both read and write operations on documents
    - We change the imagePath of the user to the imageUri if null and retrieve the old image path
    */
    private fun saveUserInformation(user: User, retrieveOldImage: Boolean) {
        firestore.runTransaction { transaction ->
            val documentRef = firestore.collection("user").document(auth.uid!!)
            if (retrieveOldImage) {
                val currentUser = transaction.get(documentRef).toObject(User::class.java)
                val newUser = user.copy(imagePath = currentUser?.imagePath ?: "")
                transaction.set(documentRef, newUser)
            } else {
                transaction.set(documentRef, user)
            }
        }.addOnSuccessListener {
            viewModelScope.launch {
                _updateInfo.value = Resource.Success(user)
            }
        }.addOnFailureListener {
            viewModelScope.launch {
            }
        }
    }

    /*
    - We're uploading the image on firebase storage
    - We convert the image to byte array and upload it to firebase storage
    - We get the download url of the image and update the user's information in the firestore database
    - The function accepts two parameters: user and imageUri
    */
    private fun updateUserInformation(user: User, imageUri: Uri) {
        viewModelScope.launch {
            try{
                val imageBitmap = MediaStore.Images.Media.getBitmap(getApplication<AutoExpressApplication>().contentResolver,imageUri)

                val byteArrayOutputStream = ByteArrayOutputStream()
                imageBitmap.compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream)
                val imageByteArray = byteArrayOutputStream.toByteArray()

                val imageDirectory = storage.child("profileImages/${auth.uid!!}/${UUID.randomUUID()}")
                val result = imageDirectory.putBytes(imageByteArray).await()
                val imageUrl = result.storage.downloadUrl.await().toString()

                saveUserInformation(user.copy(imagePath = imageUrl),false)

            }catch(e: Exception){
                viewModelScope.launch {
                    _user.value = Resource.Error(e.message.toString())
                }

            }
        }
    }

}