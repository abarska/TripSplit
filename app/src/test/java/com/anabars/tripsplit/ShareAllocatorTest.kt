package com.anabars.tripsplit

import com.anabars.tripsplit.utils.ShareAllocator
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import kotlin.random.Random

class ShareAllocatorTest {

    @Test
    fun `weighted split rounds down and gives the leftover to the payer`() {
        // A=1, B=3, C=1 on 91.37; floors 1827 / 5482 / 1827 leave 1
        val shares = ShareAllocator.allocate(9137, mapOf(1L to 1, 2L to 3, 3L to 1), payerId = 1L)

        assertThat(shares).containsExactly(1L, 1828L, 2L, 5482L, 3L, 1827L)
        assertThat(shares.values.sum()).isEqualTo(9137L)
    }

    @Test
    fun `payer who shares receives the leftover`() {
        val shares = ShareAllocator.allocate(1000, mapOf(1L to 1, 2L to 1, 3L to 1), payerId = 2L)

        assertThat(shares).containsExactly(1L, 333L, 2L, 334L, 3L, 333L)
    }

    @Test
    fun `payer who does not share leaves the leftover to the earliest added sharer`() {
        val shares = ShareAllocator.allocate(1000, mapOf(4L to 1, 2L to 1, 3L to 1), payerId = 1L)

        assertThat(shares).containsExactly(2L, 334L, 3L, 333L, 4L, 333L)
    }

    @Test
    fun `later multiplicator change does not affect shares already allocated`() {
        val saved = ShareAllocator.allocate(9137, mapOf(1L to 1, 2L to 3, 3L to 1), payerId = 1L)
        val afterChange = ShareAllocator.allocate(9137, mapOf(1L to 1, 2L to 2, 3L to 1), payerId = 1L)

        assertThat(saved[2L]).isEqualTo(5482L)
        assertThat(afterChange[2L]).isNotEqualTo(saved[2L])
    }

    @Test
    fun `single sharer gets the whole total`() {
        assertThat(ShareAllocator.allocate(777, mapOf(5L to 2), payerId = 9L))
            .containsExactly(5L, 777L)
    }

    @Test
    fun `exact division leaves no leftover`() {
        assertThat(ShareAllocator.allocate(900, mapOf(1L to 1, 2L to 2), payerId = 1L))
            .containsExactly(1L, 300L, 2L, 600L)
    }

    @Test
    fun `shares always sum to the total for random totals and weights`() {
        val random = Random(42)
        repeat(2000) {
            val total = random.nextLong(1, 10_000_000_000L)
            val sharerCount = random.nextInt(1, 12)
            val weights = (1..sharerCount).associate { it.toLong() to random.nextInt(1, 20) }
            val payerId = random.nextLong(0, sharerCount + 2L)

            val shares = ShareAllocator.allocate(total, weights, payerId)

            assertThat(shares.keys).isEqualTo(weights.keys)
            assertThat(shares.values.sum()).isEqualTo(total)
            assertThat(shares.values.all { it >= 0 }).isTrue()
        }
    }
}
