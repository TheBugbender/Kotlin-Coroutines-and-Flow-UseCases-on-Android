package com.lukaslechner.coroutineusecasesonandroid.usecases.coroutines.usecase7

import androidx.lifecycle.viewModelScope
import com.lukaslechner.coroutineusecasesonandroid.base.BaseViewModel
import com.lukaslechner.coroutineusecasesonandroid.mock.MockApi
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch

class TimeoutAndRetryViewModel(
    private val api: MockApi = mockApi()
) : BaseViewModel<UiState>() {

    fun performNetworkRequest() {
        uiState.value = UiState.Loading
        val numberOfRetries = 2
        val timeout = 1000L

        viewModelScope.launch {
            try {
                val versionFeatures = awaitAll(
                    async {
                        retry(numOfRetries = numberOfRetries, timeout = timeout) {
                            api.getAndroidVersionFeatures(27)
                        }
                    },
                    async {
                        retry(numOfRetries = numberOfRetries, timeout = timeout) {
                            api.getAndroidVersionFeatures(28)
                        }
                    })
                uiState.value = UiState.Success(versionFeatures = versionFeatures)
            }catch (e: Exception) {
                uiState.value = UiState.Error(message = "Network error")
            }
        }
    }
}