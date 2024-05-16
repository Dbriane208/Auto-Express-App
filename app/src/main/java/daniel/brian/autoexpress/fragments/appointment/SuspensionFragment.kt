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
import daniel.brian.autoexpress.databinding.FragmentSuspensionBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.AppointmentsViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class SuspensionFragment : Fragment() {
    private lateinit var binding: FragmentSuspensionBinding
    private val viewModel by viewModels<AppointmentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentSuspensionBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting up the list adapter
        setUpListArrayAdapter()

        binding.apply {
            suspensionBook.setOnClickListener {
                val service = suspensionService.text.toString()
                val branch = suspensionBranch.text.toString()
                val name = firstNameS.text.toString()
                val date = dateSuspension.text.toString()
                val time = timeSuspension.text.toString()
                val phone = suspensionPhone.text.toString()
                val carReg = carRegSuspension.text.toString()
                val carModel = carModelSuspension.text.toString()

                val appointment = Appointment(service, branch, name, phone, time, date, carReg, carModel)
                viewModel.getSuspensionAppointment(appointment)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.suspensionAppointment.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.suspensionBook.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.suspensionBook.visibility = View.GONE
                        Snackbar.make(requireView(), "Suspension Appointment Booked Successfully", Snackbar.LENGTH_LONG).show()
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
        val suspension = resources.getStringArray(R.array.suspensionServices)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, suspension)
        binding.suspensionService.setAdapter(adapter)

        val branches = resources.getStringArray(R.array.branches)
        val branchesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        binding.suspensionBranch.setAdapter(branchesAdapter)

        val time = resources.getStringArray(R.array.time)
        val timeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, time)
        binding.timeSuspension.setAdapter(timeAdapter)
    }

}