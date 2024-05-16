@file:Suppress("DEPRECATION")

package daniel.brian.autoexpress.fragments.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.data.Appointment
import daniel.brian.autoexpress.databinding.FragmentBatteryBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.AppointmentsViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class BatteryFragment : Fragment() {
    private lateinit var binding: FragmentBatteryBinding
    private val viewModel by viewModels<AppointmentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBatteryBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting up the list adapter
        setUpListArrayAdapter()

       binding.apply {
           batteryBook.setOnClickListener {
               val service = batteryService.text.toString()
               val branch = batteryBranch.text.toString()
               val name = firstNameB.text.toString()
               val time = timeBattery.text.toString()
               val date = dateBattery.text.toString()
               val phone = batteryPhone.text.toString()
               val carReg = carRegBattery.text.toString()
               val carModel = carModelBattery.text.toString()

               val appointment = Appointment(service, branch, name, phone, time, date, carReg, carModel)
               viewModel.getBatteryAppointment(appointment)
           }
       }

        lifecycleScope.launchWhenStarted {
            viewModel.batteryAppointment.collect {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.batteryBook.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.batteryBook.visibility = View.GONE
                        Snackbar.make(requireView(), "Battery Appointment Booked Successfully", Snackbar.LENGTH_LONG).show()
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
        val batteries = resources.getStringArray(R.array.batteryServices)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, batteries)
        binding.batteryService.setAdapter(adapter)

        val branches = resources.getStringArray(R.array.branches)
        val branchesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        binding.batteryBranch.setAdapter(branchesAdapter)

        val time = resources.getStringArray(R.array.time)
        val timeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, time)
        binding.timeBattery.setAdapter(timeAdapter)
    }

}