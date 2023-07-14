package com.hanium.rideornot.ui

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.hanium.rideornot.R
import com.hanium.rideornot.data.ArrivalResponse
import com.hanium.rideornot.data.ArrivalService
import com.hanium.rideornot.data.ArrivalView
import com.hanium.rideornot.databinding.FragmentStationDetailBinding
import com.hanium.rideornot.domain.Line
import com.hanium.rideornot.domain.LineDao
import com.hanium.rideornot.domain.StationDao
import com.hanium.rideornot.domain.StationDatabase.Companion.getInstance
import kotlinx.coroutines.*

class StationDetailFragment : Fragment(), ArrivalView {

    private lateinit var binding: FragmentStationDetailBinding
    private lateinit var stationDao: StationDao
    private lateinit var lineDao: LineDao

    private var lineRVAdapter = LineRVAdapter(ArrayList())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStationDetailBinding.inflate(inflater, container, false)
        binding.rvLine.adapter = lineRVAdapter

        // TDL: 추후 이전 화면에서 선택된 역 전달받는 코드 추가 필요
        val database = getInstance(requireContext())
        stationDao = database!!.stationDao()
        lineDao = database.lineDao()

        // 임시로 양재역 설정
        val station = "양재"
        CoroutineScope(Dispatchers.Main).launch {
            val lineId = withContext(Dispatchers.IO) { stationDao.findLineByName(station) }
            val lineList = withContext(Dispatchers.IO) { lineDao.getLinesByIds(lineId) as ArrayList<Line> }

            // 도착 정보 조회
            getArrivalInfo(station)

            // RecyclerView 어댑터 연결
            lineRVAdapter = LineRVAdapter(lineList)
            Log.d("linelist", lineList.toString())

            binding.rvLine.adapter = lineRVAdapter
            setLineCustom(station, lineList[0].lineName)

            lineRVAdapter.setMyItemClickListener(object : LineRVAdapter.MyItemClickListener {
                override fun onItemClick(line: Line) {
                    setLineCustom(station, line.lineName)
                }
            })
        }


        return binding.root
    }


    private fun initView(arrivalList: ArrayList<ArrivalResponse>) {
        val upDirectionList = ArrayList<ArrivalResponse>()
        val downDirectionList = ArrayList<ArrivalResponse>()

        // 상행과 하행 방향으로 데이터를 나누기
        // ** 추후 lineName별로 조회하도록 수정 필요!! 현재 임시로 3호선 설정
        for (arrival in arrivalList) {
            if (arrival.direction == "상행" && arrival.lineName == "1003") {
                upDirectionList.add(arrival)
            } else if (arrival.direction == "하행" && arrival.lineName == "1003") {
                downDirectionList.add(arrival)
            }
            arrival.destination = arrival.destination.substringBefore("행")
        }
        // 상행, 하행 방향 데이터를 arrivalTime 순으로 정렬, 상위 2개 데이터 추출
        val upTopTwoList = upDirectionList.sortedBy { it.arrivalTime }.take(2)
        val downTopTwoList = downDirectionList.sortedBy { it.arrivalTime }.take(2)


        // 상행, 하행에 따라 방면을 구분해서 시간 순으로 2개씩 표시
        binding.tvUpFirstArrivalStation.text = upTopTwoList[0].destination
        binding.tvUpFirstArrivalTime.text = formatArrivalTime(upTopTwoList[0].arrivalTime)

        binding.tvUpSecondArrivalStation.text = upTopTwoList[1].destination
        binding.tvUpSecondArrivalTime.text = formatArrivalTime(upTopTwoList[1].arrivalTime)


        binding.tvDownFirstArrivalStation.text = downTopTwoList[0].destination
        binding.tvDownFirstArrivalTime.text = formatArrivalTime(downTopTwoList[0].arrivalTime)

        binding.tvDownSecondArrivalStation.text = downTopTwoList[1].destination
        binding.tvDownSecondArrivalTime.text = formatArrivalTime(downTopTwoList[1].arrivalTime)

    }

    /**
     * 도착시간을 정제하여 형식화된 문자열로 반환
     * @param arrivalTime 도착시간 (밀리초)
     * @return 형식화된 도착시간 문자열 (예: "3분 24초")
     */
    private fun formatArrivalTime(arrivalTime: Int): String {
        val minutes = arrivalTime / 1000 / 60
        val seconds = arrivalTime / 1000 % 60
        return "${minutes}분 ${seconds}초"
    }


    /**
     * 해당 역의 열차 도착 정보를 조회
     * @param stationName 역 이름
     */
    private fun getArrivalInfo(stationName: String) {
        val arrivalService = ArrivalService()
        arrivalService.setArrivalView(this)

        // 열차 도착 정보 조회 API 호출
        arrivalService.getArrivalInfo(stationName)
    }

    override fun onArrivalSuccess(result: ArrayList<ArrivalResponse>) {
        initView(result)
    }

    override fun onArrivalFailure(code: Int, message: String) {
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