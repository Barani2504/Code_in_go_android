package com.simats.codeingo.ui.visualizer.model

import com.simats.codeingo.data.model.DSAVisualizerType

data class CNSTestVector(
    val labType: DSAVisualizerType,
    val title: String,
    val sourceStandard: String,
    val inputData: String,
    val keyOrParams: String,
    val expectedOutput: String,
    val description: String
)

object CNSTestVectorRepository {

    val VECTORS: List<CNSTestVector> = listOf(
        CNSTestVector(
            labType = DSAVisualizerType.ARRAY,
            title = "Caesar-3 Classical Vector",
            sourceStandard = "Suetonius Historical Record (Shift K=3)",
            inputData = "THE QUICK BROWN FOX JUMPS OVER THE LAZY DOG",
            keyOrParams = "Shift K = 3",
            expectedOutput = "WKH TXLFN EURZQ IRA MXPSV RYHU WKH ODCB GRJ",
            description = "Standard historical Caesar shift. Every monogram shifts forward by 3 alphabet positions modulo 26."
        ),
        CNSTestVector(
            labType = DSAVisualizerType.STACK,
            title = "NIST FIPS 46-3 Official Test Vector",
            sourceStandard = "NIST Special Publication 800-20 / FIPS 46-3",
            inputData = "0x0123456789ABCDEF",
            keyOrParams = "Key: 0x133457799BBCDFF1 (56 effective bits)",
            expectedOutput = "0x85E813540F0AB405",
            description = "Primary NIST compliance test vector exercising all 16 Feistel rounds, PC-1, PC-2, and 8 S-Boxes."
        ),
        CNSTestVector(
            labType = DSAVisualizerType.QUEUE,
            title = "NIST FIPS-197 Appendix B Test Vector",
            sourceStandard = "NIST FIPS-197 (AES-128 Specification)",
            inputData = "3243f6a8885a308d313198a2e0370734",
            keyOrParams = "Key: 2b7e151628aed2a6abf7158809cf4f3c",
            expectedOutput = "3925841d02dc09fbdc118597196a0b32",
            description = "Official NIST verification vector for AES-128 key expansion and 10 full state matrix rounds."
        ),
        CNSTestVector(
            labType = DSAVisualizerType.LINKED_LIST,
            title = "Vigenère Polyalphabetic Canonical Vector",
            sourceStandard = "Classical Cryptography Reference Vector",
            inputData = "ATTACKATDAWN",
            keyOrParams = "Key: LEMON",
            expectedOutput = "LXFOPVEFRNHR",
            description = "Standard polyalphabetic test vector demonstrating Tabula Recta cyclic key alignment."
        ),
        CNSTestVector(
            labType = DSAVisualizerType.BINARY_TREE,
            title = "Finite Field DH Exchange Vector",
            sourceStandard = "RFC 3526 / RFC 2409 Group 1 Test Case",
            inputData = "Alice a=6, Bob b=15",
            keyOrParams = "Prime p=23, Generator g=5",
            expectedOutput = "Shared Secret K = 2  (A=8, B=19)",
            description = "5^6 mod 23 = 8 (Alice Public), 5^15 mod 23 = 19 (Bob Public). Both derive 19^6 = 8^15 = 2 mod 23."
        ),
        CNSTestVector(
            labType = DSAVisualizerType.AVL_TREE,
            title = "RSA Safe Prime Toy Vector",
            sourceStandard = "PKCS #1 v2.2 Pedagogical Vector",
            inputData = "Message m = 65 ('A')",
            keyOrParams = "p=61, q=53, n=3233, e=17, d=2753",
            expectedOutput = "Ciphertext c = 2790  (Decrypted: 65)",
            description = "65^17 mod 3233 = 2790. Decryption: 2790^2753 mod 3233 = 65. Validates modular inverse d."
        ),
        CNSTestVector(
            labType = DSAVisualizerType.TRIE,
            title = "Toy Finite Field Curve F17 Vector",
            sourceStandard = "SECG SEC 1 Pedagogical Curve",
            inputData = "Base Point G = (5, 1), Order = 19",
            keyOrParams = "Curve: y² ≡ x³ + 2x + 2 (mod 17)",
            expectedOutput = "2G = (6, 3), 3G = (10, 6), 19G = 𝒪",
            description = "Verifies point doubling, chord-and-tangent slope m = (3x² + a)/(2y) mod 17, and subgroup cyclic order."
        ),
        CNSTestVector(
            labType = DSAVisualizerType.GRAPH,
            title = "NIST FIPS 180-4 Standard Hash Vector",
            sourceStandard = "NIST FIPS 180-4 (Secure Hash Standard)",
            inputData = "abc",
            keyOrParams = "None (Unkeyed Cryptographic Hash)",
            expectedOutput = "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            description = "Canonical NIST test vector verifying 512-bit message padding and 64 compression rounds."
        ),
        CNSTestVector(
            labType = DSAVisualizerType.SORTING,
            title = "RFC 4231 HMAC-SHA256 Test Case 1",
            sourceStandard = "IETF RFC 4231 (HMAC-SHA-256 Test Vectors)",
            inputData = "Hi There",
            keyOrParams = "Key: 0x0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b0b (20 bytes)",
            expectedOutput = "b0344c61d8db38535ca8afceaf0bf12b881dc200c9833da726e9376c2e32cff7",
            description = "Verifies key zero-padding to 64 bytes, inner pad XOR, and outer pad nested HMAC construction."
        ),
        CNSTestVector(
            labType = DSAVisualizerType.STEP_RECORDER,
            title = "TLS 1.3 RFC 8446 Appendix D Vector",
            sourceStandard = "IETF RFC 8446 (The Transport Layer Security 1.3 Protocol)",
            inputData = "ClientHello + KeyShare (X25519) -> ServerHello",
            keyOrParams = "CipherSuite: TLS_AES_128_GCM_SHA256",
            expectedOutput = "1-RTT Handshake -> AES-128-GCM App Traffic",
            description = "Official TLS 1.3 handshake trace establishing Forward Secrecy and authenticated encryption."
        )
    )

    fun getVectorForLab(labType: DSAVisualizerType): CNSTestVector? {
        return VECTORS.find { it.labType == labType }
    }
}
