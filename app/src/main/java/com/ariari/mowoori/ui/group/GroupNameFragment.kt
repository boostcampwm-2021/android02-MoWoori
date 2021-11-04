package com.ariari.mowoori.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentGroupNameBinding

class GroupNameFragment : Fragment() {

    private var _binding: FragmentGroupNameBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setNavigateToHome()
    }

    private fun setNavigateToHome() {
        binding.btnGroupNameComplete.setOnClickListener {
            it.findNavController().navigate(R.id.action_groupNameFragment_to_homeFragment)
        }
    }
}