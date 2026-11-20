package com.anabars.tripsplit.utils

object ShareAllocator {

    /**
     * Splits [total] minor units between the sharers in proportion to their weights.
     * Each share is rounded down, and the leftover goes to the payer if they share,
     * otherwise to the sharer with the lowest id (the earliest added). Shares always sum to [total].
     */
    fun allocate(total: Long, weights: Map<Long, Int>, payerId: Long): Map<Long, Long> {
        require(weights.isNotEmpty()) { "An expense needs at least one sharer" }
        require(weights.values.all { it > 0 }) { "Weights must be positive" }

        val weightSum = weights.values.sumOf { it.toLong() }
        val shares = weights.mapValues { (_, weight) -> total * weight / weightSum }.toMutableMap()
        val leftover = total - shares.values.sum()

        val recipient = if (payerId in weights) payerId else weights.keys.min()
        shares[recipient] = shares.getValue(recipient) + leftover
        return shares
    }
}
