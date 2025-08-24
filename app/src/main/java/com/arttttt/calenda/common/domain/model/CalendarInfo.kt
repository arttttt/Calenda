package com.arttttt.calenda.common.domain.model

data class CalendarInfo(
    val id: Long,
    val displayName: String,
    val accountName: String,
    val ownerName: String,
    val color: Int
)