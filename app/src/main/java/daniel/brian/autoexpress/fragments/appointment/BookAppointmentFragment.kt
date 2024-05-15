package daniel.brian.autoexpress.fragments.appointment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.databinding.FragmentBookAppointmentBinding


class BookAppointmentFragment : Fragment() {
   private lateinit var binding: FragmentBookAppointmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookAppointmentBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // moving to the appointment fragments
        binding.tyresAdd.setOnClickListener {
            findNavController().navigate(R.id.action_bookAppointmentFragment_to_tyresFragment)
        }

        binding.oilAdd.setOnClickListener {
            findNavController().navigate(R.id.action_bookAppointmentFragment_to_oilFragment)
        }

        binding.brakesAdd.setOnClickListener {
            findNavController().navigate(R.id.action_bookAppointmentFragment_to_brakesFragment)
        }

        binding.batteryAdd.setOnClickListener {
            findNavController().navigate(R.id.action_bookAppointmentFragment_to_batteryFragment)
        }

        binding.suspensionAdd.setOnClickListener {
            findNavController().navigate(R.id.action_bookAppointmentFragment_to_suspensionFragment)
        }

        binding.othersAdd.setOnClickListener {
            findNavController().navigate(R.id.action_bookAppointmentFragment_to_othersFragment)
        }
    }
}