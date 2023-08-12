package com.hanium.rideornot.utils.methods
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
        "경의중앙선" -> R.color.line_gyeongui_jungang
        "공항철도" -> R.color.line_airport_rail_link
        "경춘선" -> R.color.line_gyeongchun
        "분당선" -> R.color.line_bundang
        "신분당선" -> R.color.line_sinbundang
        "우이신설선" -> R.color.line_ui_sinseol
        else -> R.color.gray_400
    }
    return lineColorResId
}

fun getLineColorIdByLineId(lineId: Int) : Int {
    val lineColorResId = when (lineId) {
        1001 -> R.color.line_1
        1002 -> R.color.line_2
        1003 -> R.color.line_3
        1004 -> R.color.line_4
        1005 -> R.color.line_5
        1006 -> R.color.line_6
        1007 -> R.color.line_7
        1008 -> R.color.line_8
        1009 -> R.color.line_9
        1063 -> R.color.line_gyeongui_jungang
        1065 -> R.color.line_airport_rail_link
        1067 -> R.color.line_gyeongchun
        1075 -> R.color.line_bundang
        1077 -> R.color.line_sinbundang
        1092 -> R.color.line_ui_sinseol
        else -> R.color.gray_400
    }
    return lineColorResId
}