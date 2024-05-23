package daniel.brian.autoexpress.fragments.settings

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.data.User
import daniel.brian.autoexpress.databinding.FragmentUserAccountBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.ResetPasswordViewModel
import daniel.brian.autoexpress.viewmodel.UserAccountViewModel
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UserAccountFragment : Fragment() {
    private lateinit var binding: FragmentUserAccountBinding
    private val viewModel by viewModels<UserAccountViewModel>()
    private var imageUri: Uri? = null
    private lateinit var imageActivityResultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            imageUri = it.data?.data
            Glide.with(this).load(imageUri).into(binding.imageUser)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentUserAccountBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        hideUserLoading()
                    }
                    is Resource.Success -> {
                        showUserInformation(it.data!!)
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.updateInfo.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.buttonSave.revertAnimation()
                        Snackbar.make(requireView(),it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.buttonSave.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.buttonSave.revertAnimation()
                        findNavController().navigateUp()
                    }
                    else -> Unit
                }
            }
        }

        binding.buttonSave.setOnClickListener {
            binding.apply {
                val username = edUsername.text.toString()
                val email = edEmail.text.toString()
                val user = User(username, email)
                viewModel.updateUser(user,imageUri)
            }
        }

        binding.imageEdit.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            imageActivityResultLauncher.launch(intent)
        }

        binding.imageCloseUserAccount.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun showUserInformation(data: User) {
        binding.apply {
            edUsername.setText(data.username)
            edEmail.setText(data.email)
            Glide.with(this@UserAccountFragment).load(data.imagePath)
                .error(R.drawable.ic_profile).into(imageUser)
        }
    }

    private fun hideUserLoading() {
        binding.apply {
            progressbarAccount.visibility = View.GONE
            imageUser.visibility = View.VISIBLE
            imageEdit.visibility = View.VISIBLE
            edUsername.visibility = View.VISIBLE
            edEmail.visibility = View.VISIBLE
            buttonSave.visibility = View.VISIBLE
        }
    }

}