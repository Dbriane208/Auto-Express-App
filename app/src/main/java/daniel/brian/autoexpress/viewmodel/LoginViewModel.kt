@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.BuildConfig
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.autoexpress.fragments.LoginFragment
import daniel.brian.autoexpress.utils.Constants.USER_COLLECTION
import daniel.brian.autoexpress.utils.Resource
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {

    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _googleLogin = MutableStateFlow<Resource<FirebaseUser>>(Resource.Loading())
    val googleLogin = _googleLogin.asStateFlow()

    fun login(email: String, password: String) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(Resource.Success(it))
                    }
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun loginWithGoogle(fragment: LoginFragment) {
        // get the id token from the last signed in account
        val idToken = GoogleSignIn.getLastSignedInAccount(fragment.requireContext())?.idToken
        // create a credential with the id token
        val credential = GoogleAuthProvider.getCredential(idToken, null)

        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // get the user from the firebase auth object
                    val user = task.result?.user
                    // check if the user exists in the database
                    val docRef = db.collection(USER_COLLECTION).document(user?.uid!!)
                    docRef.get().addOnSuccessListener { document ->
                        if (document.exists()) {
                            viewModelScope.launch {
                                _googleLogin.emit(Resource.Success(user))}
                        }else{
                            viewModelScope.launch {
                                _googleLogin.emit(Resource.Error("User does not exist"))
                            }
                        }
                }.addOnFailureListener {
                    viewModelScope.launch {
                        _googleLogin.emit(Resource.Error(it.message.toString()))
                    }
                }
            }
        }
    }
}