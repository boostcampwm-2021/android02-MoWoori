package com.ariari.mowoori.ui.group

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentInviteCodeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class InviteCodeFragment : Fragment() {

    private var _binding: FragmentInviteCodeBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentInviteCodeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
