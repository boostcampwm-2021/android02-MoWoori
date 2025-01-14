package com.ariari.mowoori.ui.stamp

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver.OnPreDrawListener
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.ariari.mowoori.R
import com.ariari.mowoori.base.BaseFragment
import com.ariari.mowoori.databinding.FragmentStampsBinding
import com.ariari.mowoori.ui.missions.entity.Mission
import com.ariari.mowoori.ui.stamp.adapter.StampsAdapter
import com.ariari.mowoori.ui.stamp.entity.DetailInfo
import com.ariari.mowoori.ui.stamp.entity.DetailMode
import com.ariari.mowoori.ui.stamp.entity.Stamp
import com.ariari.mowoori.util.EventObserver
import com.ariari.mowoori.util.isNetWorkAvailable
import com.ariari.mowoori.widget.NetworkDialogFragment
import com.ariari.mowoori.widget.ProgressDialogManager
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class StampsFragment : BaseFragment<FragmentStampsBinding>(R.layout.fragment_stamps) {
    private val safeArgs: StampsFragmentArgs by navArgs()
    private lateinit var mission: Mission
    private lateinit var adapter: StampsAdapter
    private val viewModel: StampsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        postponeEnterTransition()
        setStartEnterTransition()
        setCompleteBtnVisible()
        if (!hasInitialized) {
            setSpanCount()
            loadMissionInfo()
            hasInitialized = true
        }
        setAdapter()
        setCompleteClick()
        setObserver()
    }

    private fun setStartEnterTransition() {
        // 리사이클러 뷰가 측정이 완료될 때까지 트랜지션 지연
        binding.rvStamps.viewTreeObserver.addOnPreDrawListener(object: OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                binding.rvStamps.viewTreeObserver.removeOnPreDrawListener(this)
                startPostponedEnterTransition()
                return true
            }
        })
    }

    private fun setCompleteBtnVisible() {
        viewModel.setIsMyMission(safeArgs.userId)
    }

    private fun loadMissionInfo() {
        if (requireContext().isNetWorkAvailable()) {
            viewModel.setLoadingEvent(true)
            viewModel.loadMissionInfo(safeArgs.missionId)
        } else {
            showNetworkDialog()
        }
    }

    private fun setAdapter() {
        Timber.d("setAdapter")
        adapter = StampsAdapter(object : StampsAdapter.OnItemClickListener {
            override fun itemClick(position: Int, imageView: ImageView) {
                val stamp = adapter.currentList[position]
                val extras = FragmentNavigatorExtras(
                    imageView to stamp.stampId
                )
                this@StampsFragment.findNavController()
                    .navigate(
                        StampsFragmentDirections.actionStampsFragmentToStampDetailFragment(
                            detailInfo = DetailInfo(
                                safeArgs.userId,
                                safeArgs.userNickname,
                                mission.missionId,
                                mission.missionInfo.missionName,
                                DetailMode.INQUIRY,
                                stamp
                            )
                        ), extras
                    )
            }
        })
        binding.rvStamps.adapter = adapter
    }

    private fun setSpanCount() {
        // 뷰 사이즈 측정 시점을 관찰하는 옵저버
        binding.rvStamps.viewTreeObserver.addOnPreDrawListener(object : OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                // 뷰의 측정이 자주 일어날 경우 중첩된 리스너 등록을 방지하기 위해 콜백 함수가 호출되면 해당 리스너를 제거한다.

                binding.rvStamps.viewTreeObserver.removeOnPreDrawListener(this)
                val resources = requireActivity().resources
                val recyclerViewWidth = binding.rvStamps.width
                val itemWidth =
                    resources.getDimension(R.dimen.stamp_width) + resources.getDimension(R.dimen.stamp_padding)
                viewModel.setSpanCount(recyclerViewWidth / itemWidth)
                return true
            }
        })
    }

    private fun setCompleteClick() {
        binding.btnStampsComplete.setOnClickListener {
            it.findNavController()
                .navigate(
                    StampsFragmentDirections.actionStampsFragmentToStampDetailFragment(
                        detailInfo = DetailInfo(
                            safeArgs.userId,
                            safeArgs.userNickname,
                            mission.missionId,
                            mission.missionInfo.missionName,
                            DetailMode.CERTIFY,
                            Stamp()
                        )
                    )
                )
        }
    }

    private fun setObserver() {
        setLoadingObserver()
        setMissionObserver()
        setBackBtnObserver()
        setSpanCountObserver()
        setStampListObserver()
        setNetworkDialogObserver()
    }

    private fun setLoadingObserver() {
        viewModel.loadingEvent.observe(viewLifecycleOwner, EventObserver {
            if (it) ProgressDialogManager.instance.show(requireContext())
            else ProgressDialogManager.instance.clear()
        })
    }

    private fun setMissionObserver() {
        viewModel.mission.observe(viewLifecycleOwner, {
            mission = it
        })
    }

    private fun setBackBtnObserver() {
        viewModel.backBtnClick.observe(viewLifecycleOwner, EventObserver {
            this.findNavController().popBackStack()
        })
    }

    private fun setSpanCountObserver() {
        viewModel.spanCount.observe(viewLifecycleOwner, { spanCount ->
            val gridLayoutManager = GridLayoutManager(requireContext(), spanCount)
            binding.rvStamps.layoutManager = gridLayoutManager
        })
    }

    private fun setStampListObserver() {
        viewModel.stampList.observe(viewLifecycleOwner, { stampList ->
            adapter.submitList(stampList)
        })
    }

    private fun setNetworkDialogObserver() {
        viewModel.isNetworkDialogShowed.observe(viewLifecycleOwner, EventObserver {
            if (it) showNetworkDialog()
        })
    }

    private fun showNetworkDialog() {
        NetworkDialogFragment(object : NetworkDialogFragment.NetworkDialogListener {
            override fun onCancelClick(dialog: DialogFragment) {
                dialog.dismiss()
                findNavController().navigate(R.id.action_stampsFragment_to_homeFragment)
            }

            override fun onRetryClick(dialog: DialogFragment) {
                dialog.dismiss()
                loadMissionInfo()
                viewModel.resetNetworkDialog()
            }
        }).show(requireActivity().supportFragmentManager, "NetworkDialogFragment")
    }
}
