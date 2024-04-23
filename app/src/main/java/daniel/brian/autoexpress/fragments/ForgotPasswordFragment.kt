package daniel.brian.autoexpress.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.databinding.FragmentForgotPasswordBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.ResetPasswordViewModel

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private val viewModel by viewModels<ResetPasswordViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // navigating to the login fragment
        binding.toLoginFromResetPass.setOnClickListener {
            findNavController().navigate(R.id.action_forgotPasswordFragment_to_loginFragment)
        }

        binding.apply {
           btnResetPassword.setOnClickListener {
               val email = resetEmail.text.toString().trim()
               viewModel.resetPassword(email)
           }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.resetPassword.collect{
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),"Error : ${it.message}",
                            Snackbar.LENGTH_LONG).show()
                    }

                    is Resource.Loading -> {
                        binding.btnResetPassword.startAnimation()
                    }

                    is Resource.Success -> {
                        Snackbar.make(requireView(),"Reset link to your email",
                            Snackbar.LENGTH_LONG).show()
                    }

                    else -> Unit
                }
            }
        }
    }

}