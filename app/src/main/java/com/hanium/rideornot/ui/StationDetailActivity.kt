package com.hanium.rideornot.ui

import android.app.AlertDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
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
    private val viewModel: StationDetailViewModel by viewModels { ViewModelFactory(this) }

    private var lineRVAdapter = LineRVAdapter(ArrayList())

    private var stationName = ""
    private var lineId = -1
    private var selectedLinePosition = -1
    private val timerList = mutableListOf<CountDownTimer>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        binding.rvLine.adapter = lineRVAdapter

        // 이전 화면에서 선택된 역 전달받기
        stationName = args.stationName

        viewModel.loadLineList(stationName)
        viewModel.lineList.observe(this) { lineList ->

            if (lineList.isNullOrEmpty()) {
                Snackbar.make(binding.root, "존재하지 않는 역입니다.", Snackbar.LENGTH_SHORT).show()
                return@observe
            }

            lineRVAdapter.updateData(lineList as ArrayList<Line>)

            if (selectedLinePosition == -1)
                lineId = lineList[0].lineId

            // 도착 정보 로드
            viewModel.loadArrivalList(stationName, lineId)

            // 역 정보 로드
            viewModel.loadStation(stationName, lineId)
            binding.tvPrevStationName.isSelected = true
            binding.tvNextStationName.isSelected = true

            if (selectedLinePosition == -1) {
                setLineCustom(lineList[0].lineName)
            } else {
                val targetLine = lineList.find { it.lineId == lineId }
                val targetPosition = lineRVAdapter.lineList.indexOf(targetLine)
                lineRVAdapter.selectedItemPosition = targetPosition

                setLineCustom(lineList[targetPosition].lineName)
            }
        }

        // 도착 정보
        viewModel.arrivalList.observe(this) { arrivalList ->
            initView(arrivalList)
        }

        // 이전/이후 역
        viewModel.stationItem.observe(this) { stationItem ->
            viewModel.updateNeighboringStationView(stationItem)
        }

        // 호선 RecyclerView 아이템 클릭 시
        lineRVAdapter.setMyItemClickListener(object : LineRVAdapter.MyItemClickListener {
            override fun onItemClick(line: Line) {
                setLineCustom(line.lineName)
                viewModel.loadStation(stationName, line.lineId)
                viewModel.loadArrivalList(stationName, line.lineId)
            }
        })

        // 이전/이후 역 클릭 시
        binding.ivLineBgPrev.setOnClickListener {
            val prevStationName = binding.tvPrevStationName.text.toString()
            if (binding.tvPrevStationName.text.contains("/")) {
                // 역 선택 다이얼로그를 표시하고, 사용자가 역을 선택하면 해당 역으로 이동
                val stationNames = prevStationName.split("/")
                showStationSelectionDialog(stationNames)
            } else if (prevStationName != "종착") {
                // 이전 역 이름이 "종착"이 아닌 경우에만 역 이동
                moveToStation(prevStationName)
            }
        }
        binding.ivLineBgNext.setOnClickListener {
            val nextStationName = binding.tvNextStationName.text.toString()
            if (binding.tvNextStationName.text.contains("/")) {
                // 역 선택 다이얼로그를 표시하고, 사용자가 역을 선택하면 해당 역으로 이동
                val stationNames = nextStationName.split("/")
                showStationSelectionDialog(stationNames)
            } else if (nextStationName != "종착") {
                // 이후 역 이름이 "종착"이 아닌 경우에만 역 이동
                moveToStation(nextStationName)
            }
        }

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

    override fun onPause() {
        super.onPause()

        if (stationName.isNotEmpty()) {
            viewModel.insertLastStationHistory(stationName)
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
        val colorResId = when {
            "혼잡" in arrivalResult.congestion && "혼잡도" !in arrivalResult.congestion -> R.color.red
            "혼잡도" in arrivalResult.congestion -> R.color.black
            else -> R.color.green
        }
        val color = ContextCompat.getColor(binding.root.context, colorResId)
        binding.ivStationCongestion.setColorFilter(color)

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

        // 방면
        if (upDirectionList.isNotEmpty())
            binding.tvUpDirection.text =
                upDirectionList[0].destination.split(" - ")[1].split(" ")[0]
        if (downDirectionList.isNotEmpty())
            binding.tvDownDirection.text =
                downDirectionList[0].destination.split(" - ")[1].split(" ")[0]


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

            // 목적지
            val destination = firstArrival.destination
            stationView.text = if ("(급행)" in destination) {
                val modifiedDestination = "(급)${destination.substringBefore("행")}"
                val spannable = SpannableString(modifiedDestination)
                val startIndex = modifiedDestination.indexOf("(급)")
                spannable.setSpan(
                    ForegroundColorSpan(Color.RED),
                    startIndex,
                    startIndex + 3,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
                spannable
            } else {
                destination.substringBefore("행")
            }

            // 도착 시간
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
        // 문자열에서 시간 부분과 오전/오후 부분을 추출
        val timeParts = refreshTime.split(" ")[2].split(":")
        val hours = timeParts[0].toInt()
        val minutes = timeParts[1].toInt()
        val amPmIndicator = refreshTime.split(" ")[1]  // 오전 또는 오후

        // 12시간제로 변환
        val convertedHours = if (hours % 12 == 0) 12 else hours % 12

        return "$amPmIndicator ${String.format("%02d", convertedHours)}:${
            String.format(
                "%02d",
                minutes
            )
        }"
    }


    /**
     * 호선별 커스텀 설정
     * @param lineName 호선 이름
     */
    private fun setLineCustom(lineName: String) {
        val lineColorResId = getLineColorIdByLineName(lineName)

        val color = ContextCompat.getColor(this, lineColorResId)
        binding.ivLineBgPrev.backgroundTintList = ColorStateList.valueOf(color)
        binding.ivLineBgNext.backgroundTintList = ColorStateList.valueOf(color)
        binding.ivLineBg.backgroundTintList = ColorStateList.valueOf(color)

        val backgroundDrawable = binding.btnLineNumber.background as? GradientDrawable
        backgroundDrawable?.setStroke(4, color)
        binding.btnLineNumber.background = backgroundDrawable
    }


    /**
     * 이동할 역을 선택하는 다이얼로그 표시
     * @param stationNames 선택할 역 이름 목록
     */
    private fun showStationSelectionDialog(stationNames: List<String>) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("이동할 역 선택")
            .setCancelable(true)
            .setItems(stationNames.toTypedArray()) { dialog, which ->
                // 선택한 역 이름으로 이동
                moveToStation(stationNames[which])
                dialog.dismiss()
            }
            .create()
        dialog.show()
    }

    /**
     * 선택한 역으로 이동 작업을 수행
     * @param stationName 이동할 역의 이름
     */
    private fun moveToStation(stationName: String) {
        lineId = lineRVAdapter.getItem(lineRVAdapter.selectedItemPosition).lineId
        selectedLinePosition = lineRVAdapter.selectedItemPosition
        this.stationName = stationName

        viewModel.loadLineList(stationName)
    }

}