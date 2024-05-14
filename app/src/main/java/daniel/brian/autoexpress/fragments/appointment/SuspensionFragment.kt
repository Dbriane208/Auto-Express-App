package daniel.brian.autoexpress.fragments.appointment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.databinding.FragmentSuspensionBinding

class SuspensionFragment : Fragment() {
    private lateinit var binding: FragmentSuspensionBinding

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

        val suspension = resources.getStringArray(R.array.suspensionServices)
        val adapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, suspension)
        binding.suspensionService.setAdapter(adapter)

        val branches = resources.getStringArray(R.array.branches)
        val branchesAdapter = ArrayAdapter(requireContext(), R.layout.dropdown_item, branches)
        binding.suspensionBranch.setAdapter(branchesAdapter)
    }

}