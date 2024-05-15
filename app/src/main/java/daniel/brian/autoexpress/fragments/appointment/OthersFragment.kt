@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.fragments.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.data.Appointment
import daniel.brian.autoexpress.databinding.FragmentOthersBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.AppointmentsViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OthersFragment : Fragment() {
    private lateinit var binding: FragmentOthersBinding
    private val viewModel by viewModels<AppointmentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOthersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting up the list adapter
        setUpListArrayAdapter()

        binding.apply {
            othersBook.setOnClickListener {
                val service = othersService.text.toString()
                val branch = othersBranch.text.toString()
                val time = timeOthers.text.toString()
                val date = dateOthers.text.toString()
                val name = firstNameP.text.toString()
                val phone = othersPhone.text.toString()
                val carReg = carRegOthers.text.toString()
                val carModel = carModelOthers.text.toString()

                val appointment = Appointment(service, branch, name, phone, time, date, carReg, carModel)
                viewModel.getOthersAppointment(appointment)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.othersAppointment.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.othersBook.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.othersBook.visibility = View.GONE
                        Snackbar.make(requireView(), "Other Services Appointment Booked Successfully", Snackbar.LENGTH_LONG).show()
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.error.collectLatest {
                Snackbar.make(requireView(), it, Snackbar.LENGTH_LONG).show()
            }
        }

    }

    private fun setUpListArrayAdapter() {
        val products = resources.getStringArray(R.array.productServices)
        val adapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,products)
        binding.othersService.setAdapter(adapter)

        val branches = resources.getStringArray(R.array.branches)
        val branchesAdapter = ArrayAdapter(requireContext(),R.layout.dropdown_item,branches)
        binding.othersBranch.setAdapter(branchesAdapter)

        val time = resources.getStringArray(R.array.time)
        val timeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, time)
        binding.timeOthers.setAdapter(timeAdapter)
    }

}