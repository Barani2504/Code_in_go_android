package com.simats.codeingo.ui.visualizer.engine

/**
 * ⚡ FIPS 180-4 Secure Hash Standard (SHA-256) Math Engine
 *
 * Exposes:
 * 1. Standard initial state H0..H7 and 64 round constants K[0..63]
 * 2. Complete message padding and 512-bit chunk generation
 * 3. 64-word message schedule W[0..63] computation
 * 4. Step-by-step intermediate working registers A, B, C, D, E, F, G, H for all 64 steps
 * 5. Bit-level Hamming distance calculation for avalanche effect analysis (never fake nibble slicing)
 *
 * Verified against NIST test vectors:
 * "abc" -> ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad
 * ""    -> e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855
 */
object Sha256Engine {

    // Initial Hash Values (first 32 bits of fractional parts of square roots of first 8 primes 2..19)
    val H_INIT = intArrayOf(
        0x6a09e667.toInt(),
        0xbb67ae85.toInt(),
        0x3c6ef372.toInt(),
        0xa54ff53a.toInt(),
        0x510e527f.toInt(),
        0x9b05688c.toInt(),
        0x1f83d9ab.toInt(),
        0x5be0cd19.toInt()
    )

    // 64 Round Constants (first 32 bits of fractional parts of cube roots of first 64 primes 2..311)
    val K = intArrayOf(
        0x428a2f98.toInt(), 0x71374491.toInt(), 0xb5c0fbcf.toInt(), 0xe9b5dba5.toInt(),
        0x3956c25b.toInt(), 0x59f111f1.toInt(), 0x923f82a4.toInt(), 0xab1c5ed5.toInt(),
        0xd807aa98.toInt(), 0x12835b01.toInt(), 0x243185be.toInt(), 0x550c7dc3.toInt(),
        0x72be5d74.toInt(), 0x80deb1fe.toInt(), 0x9bdc06a7.toInt(), 0xc19bf174.toInt(),
        0xe49b69c1.toInt(), 0xefbe4786.toInt(), 0x0fc19dc6.toInt(), 0x240ca1cc.toInt(),
        0x2de92c6f.toInt(), 0x4a7484aa.toInt(), 0x5cb0a9dc.toInt(), 0x76f988da.toInt(),
        0x983e5152.toInt(), 0xa831c66d.toInt(), 0xb00327c8.toInt(), 0xbf597fc7.toInt(),
        0xc6e00bf3.toInt(), 0xd5a79147.toInt(), 0x06ca6351.toInt(), 0x14292967.toInt(),
        0x27b70a85.toInt(), 0x2e1b2138.toInt(), 0x4d2c6dfc.toInt(), 0x53380d13.toInt(),
        0x650a7354.toInt(), 0x766a0abb.toInt(), 0x81c2c92e.toInt(), 0x92722c85.toInt(),
        0xa2bfe8a1.toInt(), 0xa81a664b.toInt(), 0xc24b8b70.toInt(), 0xc76c51a3.toInt(),
        0xd192e819.toInt(), 0xd6990624.toInt(), 0xf40e3585.toInt(), 0x106aa070.toInt(),
        0x19a4c116.toInt(), 0x1e376c08.toInt(), 0x2748774c.toInt(), 0x34b0bcb5.toInt(),
        0x391c0cb3.toInt(), 0x4ed8aa4a.toInt(), 0x5b9cca4f.toInt(), 0x682e6ff3.toInt(),
        0x748f82ee.toInt(), 0x78a5636f.toInt(), 0x84c87814.toInt(), 0x8cc70208.toInt(),
        0x90befffa.toInt(), 0xa4506ceb.toInt(), 0xbef9a3f7.toInt(), 0xc67178f2.toInt()
    )

    private fun rotr(x: Int, n: Int): Int = (x ushr n) or (x shl (32 - n))
    private fun ch(x: Int, y: Int, z: Int): Int = (x and y) xor (x.inv() and z)
    private fun maj(x: Int, y: Int, z: Int): Int = (x and y) xor (x and z) xor (y and z)
    private fun sigma0(x: Int): Int = rotr(x, 2) xor rotr(x, 13) xor rotr(x, 22)
    private fun sigma1(x: Int): Int = rotr(x, 6) xor rotr(x, 11) xor rotr(x, 25)
    private fun gamma0(x: Int): Int = rotr(x, 7) xor rotr(x, 18) xor (x ushr 3)
    private fun gamma1(x: Int): Int = rotr(x, 17) xor rotr(x, 19) xor (x ushr 10)

    data class ShaRoundState(
        val step: Int,
        val w: Int,
        val k: Int,
        val t1: Int,
        val t2: Int,
        val a: Int,
        val b: Int,
        val c: Int,
        val d: Int,
        val e: Int,
        val f: Int,
        val g: Int,
        val h: Int
    )

    data class ShaBlockTrace(
        val blockIndex: Int,
        val paddedBytes: ByteArray,
        val wSchedule: IntArray,
        val initialRegisters: IntArray,
        val steps: List<ShaRoundState>,
        val outputRegisters: IntArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is ShaBlockTrace) return false
            return blockIndex == other.blockIndex &&
                    paddedBytes.contentEquals(other.paddedBytes) &&
                    wSchedule.contentEquals(other.wSchedule) &&
                    initialRegisters.contentEquals(other.initialRegisters) &&
                    steps == other.steps &&
                    outputRegisters.contentEquals(other.outputRegisters)
        }
        override fun hashCode(): Int = blockIndex.hashCode()
    }

    data class Sha256Trace(
        val input: ByteArray,
        val totalBits: Long,
        val paddedData: ByteArray,
        val blocks: List<ShaBlockTrace>,
        val finalHash: ByteArray,
        val finalHex: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is Sha256Trace) return false
            return input.contentEquals(other.input) && finalHex == other.finalHex
        }
        override fun hashCode(): Int = finalHex.hashCode()
    }

    /** Pads input according to FIPS 180-4 section 5.1.1 */
    fun padMessage(input: ByteArray): ByteArray {
        val originalLen = input.size.toLong()
        val bitLen = originalLen * 8

        // Pad with 1 bit (0x80) + k zero bytes + 8 bytes length
        // (len + 1 + k + 8) % 64 == 0  =>  (len + 9 + k) % 64 == 0
        var k = (56 - ((originalLen + 1) % 64)).toInt()
        if (k < 0) k += 64

        val totalLen = (originalLen + 1 + k + 8).toInt()
        val padded = ByteArray(totalLen)
        System.arraycopy(input, 0, padded, 0, input.size)
        padded[originalLen.toInt()] = 0x80.toByte()

        // Append 64-bit big endian length
        for (i in 0..7) {
            padded[totalLen - 1 - i] = ((bitLen ushr (i * 8)) and 0xFF).toByte()
        }
        return padded
    }

    /** Runs complete SHA-256 compression generating trace */
    fun hashWithTrace(input: ByteArray): Sha256Trace {
        val padded = padMessage(input)
        val numBlocks = padded.size / 64
        val currentH = H_INIT.clone()
        val blockTraces = mutableListOf<ShaBlockTrace>()

        for (b in 0 until numBlocks) {
            val blockBytes = padded.copyOfRange(b * 64, (b + 1) * 64)
            val w = IntArray(64)

            // Step 1: Prepare message schedule W[0..15]
            for (t in 0..15) {
                val idx = t * 4
                w[t] = ((blockBytes[idx].toInt() and 0xFF) shl 24) or
                        ((blockBytes[idx + 1].toInt() and 0xFF) shl 16) or
                        ((blockBytes[idx + 2].toInt() and 0xFF) shl 8) or
                        (blockBytes[idx + 3].toInt() and 0xFF)
            }

            // Step 2: W[16..63]
            for (t in 16..63) {
                val s0 = gamma0(w[t - 15])
                val s1 = gamma1(w[t - 2])
                w[t] = s1 + w[t - 7] + s0 + w[t - 16]
            }

            // Working registers
            var a = currentH[0]
            var b = currentH[1]
            var c = currentH[2]
            var d = currentH[3]
            var e = currentH[4]
            var f = currentH[5]
            var g = currentH[6]
            var h = currentH[7]

            val initialRegs = intArrayOf(a, b, c, d, e, f, g, h)
            val roundStates = mutableListOf<ShaRoundState>()

            for (t in 0..63) {
                val t1 = h + sigma1(e) + ch(e, f, g) + K[t] + w[t]
                val t2 = sigma0(a) + maj(a, b, c)

                h = g
                g = f
                f = e
                e = d + t1
                d = c
                c = b
                b = a
                a = t1 + t2

                roundStates.add(
                    ShaRoundState(
                        step = t,
                        w = w[t],
                        k = K[t],
                        t1 = t1,
                        t2 = t2,
                        a = a,
                        b = b,
                        c = c,
                        d = d,
                        e = e,
                        f = f,
                        g = g,
                        h = h
                    )
                )
            }

            // Intermediate hash value computation
            currentH[0] += a
            currentH[1] += b
            currentH[2] += c
            currentH[3] += d
            currentH[4] += e
            currentH[5] += f
            currentH[6] += g
            currentH[7] += h

            blockTraces.add(
                ShaBlockTrace(
                    blockIndex = b,
                    paddedBytes = blockBytes,
                    wSchedule = w,
                    initialRegisters = initialRegs,
                    steps = roundStates,
                    outputRegisters = currentH.clone()
                )
            )
        }

        // Produce 32-byte hash
        val finalHash = ByteArray(32)
        for (i in 0..7) {
            val reg = currentH[i]
            finalHash[i * 4] = ((reg ushr 24) and 0xFF).toByte()
            finalHash[i * 4 + 1] = ((reg ushr 16) and 0xFF).toByte()
            finalHash[i * 4 + 2] = ((reg ushr 8) and 0xFF).toByte()
            finalHash[i * 4 + 3] = (reg and 0xFF).toByte()
        }

        val hex = finalHash.joinToString("") { "%02x".format(it) }

        return Sha256Trace(
            input = input,
            totalBits = input.size * 8L,
            paddedData = padded,
            blocks = blockTraces,
            finalHash = finalHash,
            finalHex = hex
        )
    }

    /** Bit-level Hamming distance calculation for avalanche demonstration */
    data class AvalancheResult(
        val totalBits: Int = 256,
        val flippedBitsCount: Int,
        val percentage: Float,
        val bitDiffMap: BooleanArray // true if bit differed
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other !is AvalancheResult) return false
            return flippedBitsCount == other.flippedBitsCount && bitDiffMap.contentEquals(other.bitDiffMap)
        }
        override fun hashCode(): Int = flippedBitsCount
    }

    fun computeAvalanche(hashA: ByteArray, hashB: ByteArray): AvalancheResult {
        require(hashA.size == 32 && hashB.size == 32) { "Both hashes must be 32 bytes (SHA-256)" }
        var diffCount = 0
        val diffMap = BooleanArray(256)

        for (byteIdx in 0..31) {
            val byteA = hashA[byteIdx].toInt() and 0xFF
            val byteB = hashB[byteIdx].toInt() and 0xFF
            val xorByte = byteA xor byteB

            for (bit in 0..7) {
                val isDiff = ((xorByte ushr (7 - bit)) and 1) == 1
                val globalBitIdx = byteIdx * 8 + bit
                diffMap[globalBitIdx] = isDiff
                if (isDiff) diffCount++
            }
        }

        val pct = (diffCount.toFloat() / 256.0f) * 100.0f
        return AvalancheResult(
            totalBits = 256,
            flippedBitsCount = diffCount,
            percentage = pct,
            bitDiffMap = diffMap
        )
    }
}
