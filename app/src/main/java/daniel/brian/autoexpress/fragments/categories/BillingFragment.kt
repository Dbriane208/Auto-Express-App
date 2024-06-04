package daniel.brian.autoexpress.fragments.categories

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import daniel.brian.autoexpress.R
import daniel.brian.autoexpress.databinding.FragmentBillingBinding

class BillingFragment : Fragment() {
    private lateinit var binding: FragmentBillingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentBillingBinding.inflate(layoutInflater)
        return binding.root
    }

}