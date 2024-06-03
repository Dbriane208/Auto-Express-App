package daniel.brian.autoexpress.fragments.login

import android.app.Activity.RESULT_CANCELED
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.activities.ShoppingActivity
import daniel.brian.autoexpress.data.User
import daniel.brian.autoexpress.databinding.FragmentSignUpBinding
import daniel.brian.autoexpress.utils.Constants.RC_SIGN_IN
import daniel.brian.autoexpress.utils.RegisterValidation
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.IntroductionViewModel
import daniel.brian.autoexpress.viewmodel.SignUpViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private val viewModel by viewModels<SignUpViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // navigate to the login fragment
        binding.toLogin.setOnClickListener{
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.btnSignIn.setOnClickListener {
            // introViewModel.startButtonClick()
            viewModel.signIn(this)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.regWithGoogle.collect{
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Check Internet Connection", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        Snackbar.make(requireView(), "Loading...", Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Success -> {
                        Snackbar.make(requireView(),"Registration Successful!",Snackbar.LENGTH_LONG).show()
                        Intent(requireActivity(),ShoppingActivity::class.java).also {
                            intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                    else -> Unit
                }
            }
        }

        binding.apply {
            btnRegister.setOnClickListener {
                //introViewModel.startButtonClick()
                val user = User(
                    username.text.toString().trim(),
                    email.text.toString().trim()
                )

                val password = password.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect{
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(), "Similar email exists.Try another email!", Snackbar.LENGTH_LONG).show()
                        binding.btnRegister.revertAnimation()
                    }
                    is Resource.Loading ->{
                        binding.btnRegister.startAnimation()
                    }
                    is Resource.Success ->{
                        binding.btnRegister.revertAnimation()
                        Snackbar.make(requireView(),"Registration Successful!",Snackbar.LENGTH_LONG).show()
                        Intent(requireActivity(), ShoppingActivity::class.java).also { intent ->
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }

                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.validation.collect{validation ->
                if (validation.email is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.email.apply {
                            requestFocus()
                            error = validation.email.message
                        }
                    }
                }

                if (validation.password is RegisterValidation.Failed){
                    withContext(Dispatchers.Main){
                        binding.password.apply {
                            requestFocus()
                            error = validation.password.message
                        }
                    }
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                viewModel.handleSignInResult(resultCode, data)
            } else if (resultCode == RESULT_CANCELED) {
                // Show a message to the user explaining that the sign-in process was canceled.
                Snackbar.make(requireView(), "Google Sign-In canceled.", Snackbar.LENGTH_LONG).show()
            }
        }
    }

}