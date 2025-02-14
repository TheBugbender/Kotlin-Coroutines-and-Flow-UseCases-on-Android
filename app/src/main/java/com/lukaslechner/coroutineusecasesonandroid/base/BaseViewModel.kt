package com.lukaslechner.coroutineusecasesonandroid.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import timber.log.Timber

open class BaseViewModel<T> : ViewModel() {

    protected val uiState: MutableLiveData<T> = MutableLiveData()

    fun uiState(): LiveData<T> = uiState

    suspend fun <T> retry(
        numOfRetries: Int = 0,
        timeout: Long = 0,
        delayBetweenRetry: Long = 0,
        block: suspend () -> T
    ): T {
        repeat(numOfRetries) {
            try {
                return if (timeout > 0) coroutineScope {
                    withTimeout(timeout) {
                        block()
                    }
                }
                else block()
            } catch (e: Exception) {
                Timber.e(e)
            }
            delay(delayBetweenRetry)
        }
        return block()
    }
}