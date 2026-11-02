package com.simats.codeingo.data.network.model

import kotlinx.serialization.Serializable

@Serializable
data class EmotionEventRequest(
    val emotion: String,
    val reason: String,
    val context: String? = null
)

@Serializable
data class MascotStageDTO(
    val id: Int,
    val name: String,
    val subtitle: String? = null,
    val act: Int? = 1,
    val element: String? = "Fire",
    val auraColor: String? = null,
    val quote: String? = null,
    val xpRequired: Int? = 0,
    val imageUrl: String? = null
)

@Serializable
data class MascotConfigResponse(
    val mascotName: String? = null,
    val characterRole: String? = null,
    val assets: Map<String, String>? = null,
    val stages: List<MascotStageDTO>? = null
)
