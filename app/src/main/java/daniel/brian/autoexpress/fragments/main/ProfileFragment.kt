@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.fragments.main

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.shuhart.stepview.BuildConfig
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.activities.MainActivity
import daniel.brian.autoexpress.databinding.FragmentProfileBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.ProfileViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private val viewModel by viewModels<ProfileViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentProfileBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    // calling the logout click
        onLogOutClick()

    // navigating to user account
    binding.constraintProfile.setOnClickListener {
        findNavController().navigate(R.id.action_profileFragment_to_userAccountFragment)
    }



    // updating the version code
    "Version ${BuildConfig.VERSION_CODE}".also { binding.tvVersion.text = it }

    // collecting our state
    lifecycleScope.launchWhenStarted {
        viewModel.user.collectLatest { it ->
            when(it){
                is Resource.Error -> {
                    Snackbar.make(requireView(),it.message.toString(),Snackbar.LENGTH_SHORT).show()
                    binding.progressbarSettings.visibility = View.GONE
                }
                is Resource.Loading -> {
                    binding.progressbarSettings.visibility = View.VISIBLE
                }
                is Resource.Success -> {
                    binding.progressbarSettings.visibility = View.GONE
                    Glide.with(requireView()).load(it.data!!.imagePath).error(
                        ColorDrawable(Color.BLACK)
                    ).into(binding.imageUser)
                    it.data.username.also { binding.tvUserName.text = it }
                }
                else -> Unit
            }
        }
    }
}

    private fun onLogOutClick() {
        binding.linearLogOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(context,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
            requireActivity().finish()
        }
    }

}