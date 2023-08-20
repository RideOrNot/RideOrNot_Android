package com.hanium.rideornot.ui

import android.content.res.ColorStateList
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.hanium.rideornot.R
import com.hanium.rideornot.data.response.Arrival
import com.hanium.rideornot.data.response.ArrivalResponse
import com.hanium.rideornot.databinding.ActivityStationDetailBinding
import com.hanium.rideornot.domain.Line
import com.hanium.rideornot.ui.common.ViewModelFactory
import com.hanium.rideornot.utils.methods.getLineColorIdByLineName

class StationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStationDetailBinding
    private val args: StationDetailActivityArgs by navArgs()

    private var lineRVAdapter = LineRVAdapter(ArrayList())

    private val viewModel: StationDetailViewModel by viewModels { ViewModelFactory(this) }

    private var stationName = ""
    private val timerList = mutableListOf<CountDownTimer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvLine.adapter = lineRVAdapter

        // 이전 화면에서 선택된 역 전달받기
        stationName = args.stationName

        viewModel.loadLineList(stationName)
        viewModel.lineList.observe(this) { lineList ->
            // lineList 데이터를 사용하여 UI 업데이트 등 필요한 작업 수행
//            Log.d("[StationDetail] lineList", lineList.toString())

            lineRVAdapter.updateData(lineList as ArrayList<Line>)

            val lineId = lineList[0].lineId

            // 도착 정보 로드
            viewModel.loadArrivalList(stationName, lineId)

            // 이전/이후 역 로드
            viewModel.loadNeighboringStation(stationName, lineId)
            binding.tvBeforeStationName.isSelected = true
            binding.tvNextStationName.isSelected = true

            setLineCustom(stationName, lineList[0].lineName)
        }

        // 도착 정보
        viewModel.arrivalList.observe(this) { arrivalList ->
            initView(arrivalList)
        }

        // 이전/이후 역
        viewModel.stationItem.observe(this) { stationItem ->
            binding.tvBeforeStationName.text = when {
                stationItem.beforeStationId1 == 0 -> "종착"
                stationItem.beforeStationId2 == 0 -> stationItem.beforeStation1
                else -> "${stationItem.beforeStation1}/${stationItem.beforeStation2}"
            }

            binding.tvNextStationName.text = when {
                stationItem.nextStationId1 == 0 -> "종착"
                stationItem.nextStationId2 == 0 -> stationItem.nextStation1
                else -> "${stationItem.nextStation1}/${stationItem.nextStation2}"
            }
        }

        // 호선 RecyclerView 아이템 클릭 시
        lineRVAdapter.setMyItemClickListener(object : LineRVAdapter.MyItemClickListener {
            override fun onItemClick(line: Line) {
                setLineCustom(stationName, line.lineName)
                viewModel.loadNeighboringStation(stationName, line.lineId)
                viewModel.loadArrivalList(stationName, line.lineId)
            }
        })

        // 새로 고침
        binding.btnRefresh.setOnClickListener {
            val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate360)
            binding.btnRefresh.startAnimation(rotateAnimation)

            viewModel.loadArrivalList(
                stationName,
                lineRVAdapter.getItem(lineRVAdapter.selectedItemPosition).lineId
            )
        }

        // 뒤로 가기
        binding.btnMoveBack.setOnClickListener {
            finish()
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        // 모든 타이머 중지
        timerList.forEach { it.cancel() }
        timerList.clear()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.none, R.anim.fade_out)
    }


    private fun initView(arrivalResult: ArrivalResponse) {
        // 실행 중인 도착 시간 타이머 초기화
        timerList.forEach { it.cancel() }
        timerList.clear()

        // 호출 시간
        (formatRefreshTime(arrivalResult.currentTime) + " 기준").also { binding.tvTime.text = it }

        // 혼잡도
        binding.tvStationCongestionContent.text = arrivalResult.congestion

        // 도착 정보
        // 상행과 하행 / 외선과 내선 방향으로 데이터를 나누기
        val upDirectionList =
            arrivalResult.arrivalList.filter { it.direction == "상행" || it.direction == "외선" }
        val downDirectionList =
            arrivalResult.arrivalList.filter { it.direction == "하행" || it.direction == "내선" }

        // 상행 방면 데이터 표시
        showDirectionData(
            upDirectionList,
            binding.tvUpFirstArrivalStation,
            binding.tvUpFirstArrivalTime
        )
        showDirectionData(
            upDirectionList.drop(1),
            binding.tvUpSecondArrivalStation,
            binding.tvUpSecondArrivalTime
        )

        // 하행 방면 데이터 표시
        showDirectionData(
            downDirectionList,
            binding.tvDownFirstArrivalStation,
            binding.tvDownFirstArrivalTime
        )
        showDirectionData(
            downDirectionList.drop(1),
            binding.tvDownSecondArrivalStation,
            binding.tvDownSecondArrivalTime
        )

        // 상행, 하행 데이터가 비어있는 경우 메시지 표시
        binding.tvUpNoArrivalDataMessage.visibility =
            if (upDirectionList.isEmpty()) View.VISIBLE else View.INVISIBLE
        binding.tvDownNoArrivalDataMessage.visibility =
            if (downDirectionList.isEmpty()) View.VISIBLE else View.INVISIBLE
    }


    /**
     * 상행 또는 하행 방향의 도착 정보를 UI에 표시
     * @param directionList 상행 또는 하행 방향의 도착 정보 리스트
     * @param stationView 표시할 역(도착지) 정보를 표시하는 TextView
     * @param timeView 표시할 도착 시간 정보를 표시하는 TextView
     */
    private fun showDirectionData(
        directionList: List<Arrival>,
        stationView: TextView,
        timeView: TextView
    ) {
        if (directionList.isNotEmpty()) {
            stationView.visibility = View.VISIBLE
            timeView.visibility = View.VISIBLE
            val firstArrival = directionList[0]
            stationView.text = firstArrival.destination.substringBefore("행")

            if (firstArrival.arrivalTime != 0) {
                updateArrivalTimeWithTimer(firstArrival.arrivalTime, timeView)
            } else {
                timeView.text = formatArrivalTime(firstArrival.arrivalTime)
            }
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
     * 타이머를 활용하여 도착시간을 표시
     * @param arrivalTime 도착 시간 (초)
     * @param timeView 시간을 표시할 TextView
     */
    private fun updateArrivalTimeWithTimer(arrivalTime: Int, timeView: TextView) {
        // 타이머 설정
        val timer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // 1초마다 호출되는 콜백 메소드
                // 타이머가 갱신될 때마다 현재 도착 시간을 업데이트
                val currentTime = arrivalTime - ((10000 - millisUntilFinished) / 1000).toInt()
                val formattedTime = formatArrivalTime(currentTime)

                // 남은 시간이 0초 이하이면
                if (currentTime <= 0) {
                    onFinish()
                } else {
                    // UI에 도착 시간 표시
                    timeView.text = formattedTime
                }
            }

            override fun onFinish() {
                // 타이머가 완료되었을 때 호출되는 콜백 메소드
                // 도착 정보 API 호출
                val rotateAnimation =
                    AnimationUtils.loadAnimation(this@StationDetailActivity, R.anim.rotate360)
                binding.btnRefresh.startAnimation(rotateAnimation)

                viewModel.loadArrivalList(
                    stationName,
                    lineRVAdapter.getItem(lineRVAdapter.selectedItemPosition).lineId
                )
            }
        }
        timer.start()

        // 생성한 타이머를 리스트에 추가
        timerList.add(timer)
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
        (stationName + "역").also { binding.tvStationName.text = it }
        binding.btnLineNumber.text = stationName

        val lineColorResId = getLineColorIdByLineName(lineName)

        val color = ContextCompat.getColor(this, lineColorResId)
        binding.ivLineBg.backgroundTintList = ColorStateList.valueOf(color)

        val backgroundDrawable = binding.btnLineNumber.background as? GradientDrawable
        backgroundDrawable?.setStroke(4, color)
        binding.btnLineNumber.background = backgroundDrawable
    }

}