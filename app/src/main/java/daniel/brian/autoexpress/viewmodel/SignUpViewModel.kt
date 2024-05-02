@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import daniel.brian.autoexpress.data.User
import daniel.brian.autoexpress.utils.Constants
import daniel.brian.autoexpress.utils.Constants.RC_SIGN_IN
import daniel.brian.autoexpress.utils.Constants.USER_COLLECTION
import daniel.brian.autoexpress.utils.RegisterFieldState
import daniel.brian.autoexpress.utils.RegisterValidation
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.utils.validateEmail
import daniel.brian.autoexpress.utils.validatePassword
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@Suppress("DEPRECATION")
@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register = _register.asStateFlow()

    private val _validation = Channel<RegisterFieldState>()
    val validation = _validation.receiveAsFlow()

    private val _regWithGoogle = MutableStateFlow<Resource<FirebaseUser>>(Resource.Unspecified())
    val regWithGoogle = _regWithGoogle.asStateFlow()

    fun createAccountWithEmailAndPassword(user: User,password: String){
       if (checkValidation(user, password)){
           runBlocking {
               _register.emit(Resource.Loading())
           }
           firebaseAuth.createUserWithEmailAndPassword(user.email,password)
               .addOnSuccessListener {it ->
                   it.user?.let {
                       saveUserInfo(it.uid,user)
                   }
               }.addOnFailureListener {
                   _register.value = Resource.Error(it.message.toString())
               }
       }else{
           val registerFieldState = RegisterFieldState(
               validateEmail(user.email),
               validatePassword(password)
           )

           // We're using send by the fact that we're using Channel to push the registerFieldState to the pull-based Flow
           runBlocking {
               _validation.send(registerFieldState)
           }
       }
    }

    private fun checkValidation(user: User,password: String): Boolean{
        val emailValidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)

        return emailValidation is RegisterValidation.Success &&
                passwordValidation is RegisterValidation.Success
    }

    private fun saveUserInfo(userUid: String, user: User) {
        db.collection(USER_COLLECTION)
            .document(userUid) // unique id of each customer
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)
            }.addOnFailureListener {
                _register.value = Resource.Error(it.message.toString())
            }
    }

    fun signIn(fragment: Fragment){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(Constants.DEFAULT_WEB_CLIENT)
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(fragment.requireActivity(),gso)
        val intent = googleSignInClient.signInIntent
        fragment.startActivityForResult(intent,RC_SIGN_IN)
    }

    fun handleSignInResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            data?.let {
                val task = GoogleSignIn.getSignedInAccountFromIntent(it)
                try {
                    val account = task.getResult(ApiException::class.java)
                    account?.idToken?.let { idToken ->
                        firebaseAuthWithGoogle(idToken)
                    } ?: run {
                        _regWithGoogle.value = Resource.Error("Google Sign-In failed: idToken is null")
                    }
                } catch (e: ApiException) {
                    _regWithGoogle.value = Resource.Error("Google Sign-In failed: ${e.message}")
                }
            } ?: run {
                _regWithGoogle.value = Resource.Error("Google Sign-In failed: Intent data is null")
            }
        } else {
            _regWithGoogle.value = Resource.Error("Google Sign-In canceled or failed")
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String?) {
        viewModelScope.launch {
            _regWithGoogle.emit(Resource.Loading())
            val credential = GoogleAuthProvider.getCredential(idToken,null)
            firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener{
                    if (it.isSuccessful){
                        _regWithGoogle.value = Resource.Success(firebaseAuth.currentUser!!)
                        saveUserData(firebaseAuth.currentUser!!)
                    }else{
                        _regWithGoogle.value = Resource.Error(it.exception?.message.toString())
                    }
                }
        }
    }

    private fun saveUserData(currentUser: FirebaseUser?) {
        viewModelScope.launch {
            currentUser?.let {it ->
                val userData = hashMapOf(
                    "uid" to it.uid,
                    "name" to it.displayName,
                    "email" to it.email
                )

                db.collection(USER_COLLECTION)
                    .document(it.uid)
                    .set(userData)
                    .addOnSuccessListener {
                        _regWithGoogle.value = Resource.Success(currentUser)
                    }.addOnFailureListener {
                        _regWithGoogle.value = Resource.Error(it.message.toString())
                    }

            }
        }

    }


}