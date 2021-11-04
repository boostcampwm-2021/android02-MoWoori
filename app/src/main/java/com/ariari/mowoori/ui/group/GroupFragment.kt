package com.ariari.mowoori.ui.group

import android.animation.Animator
import android.animation.AnimatorInflater
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.ariari.mowoori.R
import com.ariari.mowoori.databinding.FragmentGroupBinding
import com.ariari.mowoori.ui.group.entity.GroupMode

class GroupFragment : Fragment() {

    private var _binding: FragmentGroupBinding? = null
    private val binding get() = _binding ?: error(getString(R.string.binding_error))
    private val viewModel: GroupViewModel by viewModels()
    private val args: GroupFragmentArgs by navArgs()
    private val objectAnimator: Animator by lazy {
        AnimatorInflater.loadAnimator(requireContext(), R.animator.animator_invalid_vibrate)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentGroupBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setAnimationTarget()
        when (args.groupMode) {
            GroupMode.INVITE -> {
                setTitle(R.string.group_invite_title)
            }
            GroupMode.NEW -> {
                setTitle(R.string.group_name_title)
                setGroupName()
                setGroupNameValidation()
                setValidationObserver()
            }
        }
    }

    private fun setAnimationTarget() {
        objectAnimator.setTarget(binding.etGroup)
    }

    private fun setTitle(resId: Int) {
        binding.tvGroupTitle.setText(resId)
    }

    private fun setGroupName() {
        // TODO: 그룹 이름 생성 (형용사 + 명사)
    }

    private fun setGroupNameValidation() {
        binding.btnGroupComplete.setOnClickListener {
            viewModel.checkGroupNameValidation(binding.etGroup.text.toString())
        }
    }

    private fun setValidationObserver() {
        viewModel.isValid.observe(viewLifecycleOwner, { isValid ->
            if (isValid) {
                viewModel.addNewGroup()
                this.findNavController().navigate(R.id.action_groupNameFragment_to_homeFragment)
            } else {
                // TODO: 그룹 이름이 유효하지 않다고 명시
                objectAnimator.start()
            }
        })
    }
}
