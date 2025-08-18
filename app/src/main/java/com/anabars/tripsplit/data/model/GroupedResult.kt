package com.anabars.tripsplit.data.model

sealed class GroupedResult<K, V> {
    class Loading<K, V> : GroupedResult<K, V>()
    class Empty<K, V> : GroupedResult<K, V>()
    data class Success<K, V>(val data: Map<K, List<V>>) : GroupedResult<K, V>()
}