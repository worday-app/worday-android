package com.wordayapp.worday.common.extension

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import com.wordayapp.worday.common.result.NetworkResult

fun <T> Flow<T>.asNetworkResult(): Flow<NetworkResult<T>> =
    this
        .map<T, NetworkResult<T>> { NetworkResult.Success(it) }
        .catch { emit(NetworkResult.Error(message = it.localizedMessage ?: "Bilinmeyen hata")) }