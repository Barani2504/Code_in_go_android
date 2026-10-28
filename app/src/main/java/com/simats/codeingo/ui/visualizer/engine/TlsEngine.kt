package com.simats.codeingo.ui.visualizer.engine

import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * 🌐 TLS 1.3 Handshake & Protocol Math Engine
 *
 * Exposes:
 * 1. Full TLS 1.3 Handshake flight sequences (ClientHello -> ServerHello -> EncryptedExtensions -> Cert -> CertVerify -> Finished)
 * 2. Interactive Packet Inspector with plaintext headers and ciphertext records
 * 3. Real AES-128-GCM record protection and tamper verification (tag mismatch)
 * 4. X.509 Certificate Chain validation simulator (Root -> Intermediate -> Leaf)
 * 5. HKDF key derivation ladder visualization
 */
object TlsEngine {

    data class TlsPacket(
        val flightNumber: Int,
        val sender: String, // "Client" or "Server"
        val recordType: String, // "Handshake (0x16)" or "Application Data (0x17)"
        val messageType: String,
        val isEncrypted: Boolean,
        val description: String,
        val fields: Map<String, String>
    )

    data class X509Cert(
        val subject: String,
        val issuer: String,
        val isRoot: Boolean,
        val validFromYear: Int,
        val validToYear: Int,
        val signatureValid: Boolean,
        val keyType: String = "ECDSA secp256r1"
    )

    data class CertChainValidation(
        val chain: List<X509Cert>,
        val targetDomain: String,
        val currentYear: Int,
        val isValid: Boolean,
        val failureReason: String?
    )

    data class ApplicationRecordTamperResult(
        val originalRecordHex: String,
        val tamperedRecordHex: String,
        val tagVerified: Boolean,
        val plaintextRecovered: String?,
        val errorDetail: String?
    )

    /** Standard TLS 1.3 Handshake Flight Messages */
    fun getHandshakePackets(): List<TlsPacket> = listOf(
        TlsPacket(
            flightNumber = 1,
            sender = "Client",
            recordType = "Handshake (0x16)",
            messageType = "ClientHello",
            isEncrypted = false,
            description = "Initiates TLS session. Offers TLS 1.3, sends client ECDHE public share g^x, and SNI extension.",
            fields = mapOf(
                "Version" to "TLS 1.3 (0x0304)",
                "Cipher Suites" to "TLS_AES_128_GCM_SHA256, TLS_CHACHA20_POLY1305_SHA256",
                "Key Share (g^x)" to "X25519: 9d71a4f0... (32 bytes public ephemeral)",
                "SNI Extension" to "codeingo.simats.com",
                "Supported Groups" to "x25519, secp256r1"
            )
        ),
        TlsPacket(
            flightNumber = 2,
            sender = "Server",
            recordType = "Handshake (0x16)",
            messageType = "ServerHello",
            isEncrypted = false,
            description = "Confirms TLS 1.3, selects cipher suite, and responds with server ECDHE public share g^y.",
            fields = mapOf(
                "Version" to "TLS 1.3 (0x0304)",
                "Selected Cipher Suite" to "TLS_AES_128_GCM_SHA256",
                "Key Share (g^y)" to "X25519: 4c18f3a2... (32 bytes public ephemeral)",
                "Server Random" to "28b49e10... (32 bytes)"
            )
        ),
        TlsPacket(
            flightNumber = 2,
            sender = "Server",
            recordType = "Application Data (0x17)",
            messageType = "EncryptedExtensions",
            isEncrypted = true,
            description = "First message encrypted under Handshake Secret. Configures negotiated server parameters.",
            fields = mapOf(
                "ALPN" to "h2 (HTTP/2)",
                "Server Early Data" to "Not accepted",
                "Encryption" to "AES-128-GCM (Handshake Key)"
            )
        ),
        TlsPacket(
            flightNumber = 2,
            sender = "Server",
            recordType = "Application Data (0x17)",
            messageType = "Certificate (X.509)",
            isEncrypted = true,
            description = "Sends the certificate chain establishing server identity to prevent MITM attacks.",
            fields = mapOf(
                "Leaf Cert" to "CN=codeingo.simats.com",
                "Intermediate CA" to "CN=Simats Secure CA G2",
                "Root Anchor" to "CN=GlobalTrust Root CA"
            )
        ),
        TlsPacket(
            flightNumber = 2,
            sender = "Server",
            recordType = "Application Data (0x17)",
            messageType = "CertificateVerify",
            isEncrypted = true,
            description = "Digital signature over handshake transcript using server's private key, proving private key possession.",
            fields = mapOf(
                "Signature Scheme" to "ecdsa_secp256r1_sha256",
                "Verified" to "Valid signature over transcript hash"
            )
        ),
        TlsPacket(
            flightNumber = 2,
            sender = "Server",
            recordType = "Application Data (0x17)",
            messageType = "Finished",
            isEncrypted = true,
            description = "HMAC authentication tag over the entire handshake transcript up to this point.",
            fields = mapOf(
                "Verify Data" to "HMAC-SHA256(ServerFinishedKey, TranscriptHash)"
            )
        ),
        TlsPacket(
            flightNumber = 3,
            sender = "Client",
            recordType = "Application Data (0x17)",
            messageType = "Finished",
            isEncrypted = true,
            description = "Client HMAC over handshake transcript. Handshake is now 1-RTT complete! Application traffic keys derived.",
            fields = mapOf(
                "Verify Data" to "HMAC-SHA256(ClientFinishedKey, TranscriptHash)",
                "Status" to "1-RTT Complete -> Switching to App Traffic Keys"
            )
        ),
        TlsPacket(
            flightNumber = 4,
            sender = "Client",
            recordType = "Application Data (0x17)",
            messageType = "Application Data (GET /api/crypto)",
            isEncrypted = true,
            description = "Encrypted HTTP/2 payload transmitted using AES-128-GCM application traffic keys.",
            fields = mapOf(
                "Payload" to "GET /api/crypto HTTP/2 (Encrypted)",
                "Integrity Tag" to "16-byte Poly1305 / GCM Tag appended"
            )
        )
    )

    /** Validates an X.509 Certificate Chain */
    fun validateCertificateChain(
        chain: List<X509Cert>,
        targetDomain: String,
        currentYear: Int = 2026
    ): CertChainValidation {
        if (chain.isEmpty()) {
            return CertChainValidation(chain, targetDomain, currentYear, false, "Empty certificate chain")
        }

        // Verify root anchor
        val root = chain.last()
        if (!root.isRoot || root.subject != root.issuer) {
            return CertChainValidation(chain, targetDomain, currentYear, false, "Root certificate is not a valid self-signed anchor")
        }

        // Check dates and signatures
        for (i in 0 until chain.size) {
            val cert = chain[i]
            if (currentYear < cert.validFromYear || currentYear > cert.validToYear) {
                return CertChainValidation(chain, targetDomain, currentYear, false, "Certificate ${cert.subject} is expired (Valid: ${cert.validFromYear}-${cert.validToYear})")
            }
            if (!cert.signatureValid) {
                return CertChainValidation(chain, targetDomain, currentYear, false, "Cryptographic signature validation failed on ${cert.subject}")
            }
            if (i < chain.size - 1) {
                val issuerCert = chain[i + 1]
                if (cert.issuer != issuerCert.subject) {
                    return CertChainValidation(chain, targetDomain, currentYear, false, "Issuer mismatch: ${cert.issuer} != ${issuerCert.subject}")
                }
            }
        }

        // Check domain name on leaf
        val leaf = chain.first()
        val expectedSubj = "CN=$targetDomain"
        if (!leaf.subject.contains(targetDomain)) {
            return CertChainValidation(chain, targetDomain, currentYear, false, "Domain name mismatch: Certificate is for ${leaf.subject}, not $targetDomain")
        }

        return CertChainValidation(chain, targetDomain, currentYear, true, null)
    }

    /** Simulates TLS 1.3 Record encryption & tampering */
    fun testRecordTamper(
        plaintextPayload: String = "GET /user/keys HTTP/2\r\nHost: codeingo.simats.com\r\n\r\n",
        tamperByteIndex: Int = 5
    ): ApplicationRecordTamperResult {
        val appKey = ByteArray(16) { 0x55.toByte() }
        val iv = ByteArray(12) { 0x22.toByte() }
        val secretKey = SecretKeySpec(appKey, "AES")

        val encCipher = Cipher.getInstance("AES/GCM/NoPadding")
        encCipher.init(Cipher.ENCRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
        val ciphertext = encCipher.doFinal(plaintextPayload.toByteArray(Charsets.UTF_8))

        // TLS 1.3 Record header: Type 0x17, Version 0x0303, Length (2 bytes)
        val len = ciphertext.size
        val record = ByteArray(5 + len)
        record[0] = 0x17.toByte()
        record[1] = 0x03.toByte()
        record[2] = 0x03.toByte()
        record[3] = ((len ushr 8) and 0xFF).toByte()
        record[4] = (len and 0xFF).toByte()
        System.arraycopy(ciphertext, 0, record, 5, len)

        val originalHex = record.joinToString("") { "%02x".format(it) }

        // Tamper 1 byte in ciphertext body
        val tamperedRecord = record.clone()
        val targetIdx = (5 + tamperByteIndex).coerceIn(5, tamperedRecord.size - 1)
        tamperedRecord[targetIdx] = (tamperedRecord[targetIdx].toInt() xor 0x01).toByte()
        val tamperedHex = tamperedRecord.joinToString("") { "%02x".format(it) }

        // Try decrypting tampered
        val tamperedCiphertext = tamperedRecord.copyOfRange(5, tamperedRecord.size)
        var verified = false
        var recovered: String? = null
        var err: String? = null

        try {
            val decCipher = Cipher.getInstance("AES/GCM/NoPadding")
            decCipher.init(Cipher.DECRYPT_MODE, secretKey, GCMParameterSpec(128, iv))
            val decBytes = decCipher.doFinal(tamperedCiphertext)
            recovered = String(decBytes, Charsets.UTF_8)
            verified = true
        } catch (e: Exception) {
            verified = false
            err = "AEADBadTagException: Decryption tag mismatch! The TLS record was tampered with in transit."
        }

        return ApplicationRecordTamperResult(
            originalRecordHex = originalHex,
            tamperedRecordHex = tamperedHex,
            tagVerified = verified,
            plaintextRecovered = recovered,
            errorDetail = err
        )
    }
}
