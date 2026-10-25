package com.example.notifications

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class InAppBannerData(
    val id: Long = System.currentTimeMillis(),
    val title: String,
    val body: String,
    val actionRoute: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

object InAppBannerManager {

    private val _bannerState = MutableStateFlow<InAppBannerData?>(null)
    val bannerState: StateFlow<InAppBannerData?> = _bannerState.asStateFlow()

    fun showBanner(data: InAppBannerData) {
        _bannerState.value = data
    }

    fun dismissBanner() {
        _bannerState.value = null
    }
}
