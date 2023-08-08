package com.hanium.rideornot.utils
import com.hanium.rideornot.R

fun getLineColorIdByLineName(lineName: String) : Int {
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
    return lineColorResId
}

fun getLineColorIdByLineId(lineId: Int) : Int {
    val lineColorResId = when (lineId) {
        1 -> R.color.line_1
        2 -> R.color.line_2
        3 -> R.color.line_3
        4 -> R.color.line_4
        5 -> R.color.line_5
        6 -> R.color.line_6
        7 -> R.color.line_7
        8 -> R.color.line_8
        9 -> R.color.line_9
        15 -> R.color.line_bundang
        19 -> R.color.line_sinbundang
        113 -> R.color.line_ui_sinseol
        16 -> R.color.line_gyeongui_jungang
        40 -> R.color.line_airport_rail_link
        18 -> R.color.line_gyeongchun
        else -> R.color.gray_400
    }
    return lineColorResId
}