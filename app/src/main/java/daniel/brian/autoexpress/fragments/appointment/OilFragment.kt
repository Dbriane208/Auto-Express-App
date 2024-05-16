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
import daniel.brian.autoexpress.databinding.FragmentOilBinding
import daniel.brian.autoexpress.utils.Resource
import daniel.brian.autoexpress.viewmodel.AppointmentsViewModel
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class OilFragment : Fragment() {
    private lateinit var binding: FragmentOilBinding
    private val viewModel by viewModels<AppointmentsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentOilBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //setting up the list adapter
        setUpListArrayAdapter()

        binding.apply {
            oilBook.setOnClickListener {
                val service = oilService.text.toString()
                val branch = oilBranch.text.toString()
                val time = timeOil.text.toString()
                val date = dateOil.text.toString()
                val name = firstNameO.text.toString()
                val phone = oilPhone.text.toString()
                val carReg = carRegOil.text.toString()
                val carModel = carModelOil.text.toString()

                val appointment = Appointment(service, branch, name, phone, time, date, carReg, carModel)
                viewModel.getOilAppointment(appointment)
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.oilAppointment.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Snackbar.make(requireView(), it.message.toString(), Snackbar.LENGTH_LONG).show()
                    }
                    is Resource.Loading -> {
                        binding.oilBook.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.oilBook.visibility = View.GONE
                        Snackbar.make(requireView(),"Oil Appointment Booked Successfully", Snackbar.LENGTH_LONG).show()
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
        val oil = resources.getStringArray(R.array.oilServices)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, oil)
        binding.oilService.setAdapter(adapter)

        val branches = resources.getStringArray(R.array.branches)
        val oilAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        binding.oilBranch.setAdapter(oilAdapter)

        val time = resources.getStringArray(R.array.time)
        val timeAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, time)
        binding.timeOil.setAdapter(timeAdapter)
    }

}