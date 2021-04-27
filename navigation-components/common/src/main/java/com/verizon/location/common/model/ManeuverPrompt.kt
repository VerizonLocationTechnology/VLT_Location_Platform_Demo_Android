package com.verizon.location.common.model

data class ManeuverPrompt(
    val type: ManeuverType,
    val text: String,
    val lengthInMeters: Double? = null
)