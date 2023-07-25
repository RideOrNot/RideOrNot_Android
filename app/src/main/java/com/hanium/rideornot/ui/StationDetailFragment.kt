package com.hanium.rideornot.ui

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.hanium.rideornot.R
import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.data.response.Arrival
import com.hanium.rideornot.databinding.FragmentStationDetailBinding
import com.hanium.rideornot.domain.Line
import com.hanium.rideornot.ui.common.ViewModelFactory
import kotlin.collections.ArrayList

class StationDetailFragment : Fragment() {

    private lateinit var binding: FragmentStationDetailBinding

    private var lineRVAdapter = LineRVAdapter(ArrayList())

    private val viewModel: StationDetailViewModel by viewModels { ViewModelFactory(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStationDetailBinding.inflate(inflater, container, false)

        // TDL: 추후 이전 화면에서 선택된 역 전달받는 코드 추가 필요

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvLine.adapter = lineRVAdapter

        // 임시로 양재역 설정
        val station = "양재"
        viewModel.loadArrivalInfo(station, 1003)
        viewModel.arrivalInfoList.observe(viewLifecycleOwner) { arrivalInfoList ->
            initView(arrivalInfoList)
        }

        viewModel.loadLineList(station)

        viewModel.lineList.observe(viewLifecycleOwner) { lineList ->
            // lineList 데이터를 사용하여 UI 업데이트 등 필요한 작업 수행
            // 예: RecyclerView 어댑터에 데이터 설정, UI에 출력 등
            Log.d("list", lineList.toString())

            lineRVAdapter.updateData(lineList as ArrayList<Line>)

            setLineCustom(station, lineList[0].lineName)
        }

        lineRVAdapter.setMyItemClickListener(object : LineRVAdapter.MyItemClickListener {
            override fun onItemClick(line: Line) {
                setLineCustom(station, line.lineName)
                viewModel.loadArrivalInfo(station, line.lineId)
            }
        })

        // 새로고침
        binding.btnRefresh.setOnClickListener {
            viewModel.loadArrivalInfo(station, lineRVAdapter.getItem(lineRVAdapter.selectedItemPosition).lineId)
        }

    }


    private fun initView(arrivalResult: ArrivalResponse) {
        // 시간
        binding.tvTime.text = formatRefreshTime(arrivalResult.currentTime)

        // 혼잡도
        binding.tvStationCongestionContent.text = arrivalResult.congestion.toString()

        // 도착정보
        // 상행과 하행 방향으로 데이터를 나누기
        val upDirectionList = arrivalResult.arrivalList.filter { it.direction == "상행" }
        val downDirectionList = arrivalResult.arrivalList.filter { it.direction == "하행" }

        // 상행 방면 데이터 표시
        showDirectionData(upDirectionList, binding.tvUpFirstArrivalStation, binding.tvUpFirstArrivalTime)
        showDirectionData(upDirectionList.drop(1), binding.tvUpSecondArrivalStation, binding.tvUpSecondArrivalTime)

        // 하행 방면 데이터 표시
        showDirectionData(downDirectionList, binding.tvDownFirstArrivalStation, binding.tvDownFirstArrivalTime)
        showDirectionData(downDirectionList.drop(1), binding.tvDownSecondArrivalStation, binding.tvDownSecondArrivalTime)

        // 상행, 하행 데이터가 비어있는 경우 메시지 표시
        binding.tvUpNoArrivalDataMessage.visibility = if (upDirectionList.isEmpty()) View.VISIBLE else View.INVISIBLE
        binding.tvDownNoArrivalDataMessage.visibility = if (downDirectionList.isEmpty()) View.VISIBLE else View.INVISIBLE
    }

    /**
     * 상행 또는 하행 방향의 도착 정보를 UI에 표시
     * @param directionList 상행 또는 하행 방향의 도착 정보 리스트
     * @param stationView 표시할 역(도착지) 정보를 표시하는 TextView
     * @param timeView 표시할 도착 시간 정보를 표시하는 TextView
     */
    private fun showDirectionData(directionList: List<Arrival>, stationView: TextView, timeView: TextView) {
        if (directionList.isNotEmpty()) {
            stationView.visibility = View.VISIBLE
            timeView.visibility = View.VISIBLE
            val firstArrival = directionList[0]
            stationView.text = firstArrival.destination.substringBefore("행")
            timeView.text = formatArrivalTime(firstArrival.arrivalTime)
        } else {
            stationView.visibility = View.INVISIBLE
            timeView.visibility = View.INVISIBLE
        }
    }


    /**
     * 도착시간을 정제하여 형식화된 문자열로 반환
     * @param arrivalTime 도착시간 (초)
     * @return 형식화된 도착시간 문자열 (예: "3분 24초")
     */
    private fun formatArrivalTime(arrivalTime: Int): String {
        val minutes = arrivalTime / 60
        val seconds = arrivalTime % 60
        return "${minutes}분 ${seconds}초"
    }


    /**
     * 새로고침 시간을 받아서 12시간제로 형식화된 문자열로 반환
     * @param refreshTime 새로고침 시간 (예: "2023-07-17 오후 16:14:14")
     * @return 12시간제 형식으로 형식화된 시간 (예: "오후 04:14")
     */
    private fun formatRefreshTime(refreshTime: String): String {
        // 문자열에서 시간 부분을 추출
        val timeParts = refreshTime.split(" ")[2].split(":")
        val hours = timeParts[0].toInt()
        val minutes = timeParts[1].toInt()

        // 12시간제로 변환
        val amPm = if (refreshTime.contains("오후")) "오후" else "오전"
        val convertedHours = if (hours % 12 == 0) 12 else hours % 12

        return "$amPm ${String.format("%02d", convertedHours)}:${String.format("%02d", minutes)}"
    }


    /**
     * 호선별 커스텀 설정
     * @param stationName 역 이름
     * @param lineName 호선 이름
     */
    private fun setLineCustom(stationName: String, lineName: String) {
        binding.btnLineNumber.text = stationName

        val lineColorResId = when (lineName) {
            "1호선" -> R.color.line_1
            "2호선" -> R.color.line_2
            "3호선" -> R.color.line_3
            "4호선" -> R.color.line_4
            "5호선" -> R.color.line_5
            "6호선" -> R.color.line_6
            "7호선" -> R.color.line_7
            "8호선" -> R.color.line_8
            "9호선" -> R.color.line_9
            "분당선" -> R.color.line_bundang
            "신분당선" -> R.color.line_sinbundang
            "우이신설선" -> R.color.line_ui_sinseol
            "경의중앙선" -> R.color.line_gyeongui_jungang
            "공항철도" -> R.color.line_airport_rail_link
            "경춘선" -> R.color.line_gyeongchun
            else -> R.color.gray_400
        }

        val color = ContextCompat.getColor(requireContext(), lineColorResId)
        binding.ivLineBg.backgroundTintList = ColorStateList.valueOf(color)

        val backgroundDrawable = binding.btnLineNumber.background as? GradientDrawable
        backgroundDrawable?.setStroke(4, color)
        binding.btnLineNumber.background = backgroundDrawable
    }

}