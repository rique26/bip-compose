package com.ebody.bip.features.wellbeing.data.model

data class MoodRemoteEntity(
    val id: Long = 0L,
    val level: Int = 0,
    val notes: String = "",
    val dateTime: Any = ""
) {
    constructor() : this(0L, 0, "", "")
}
