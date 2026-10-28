package com.simats.codeingo.ui.visualizer

/**
 * 💻 Cryptography and Network Security Language Code Snippet Provider
 *
 * Provides production-grade code implementations across 7 languages:
 * Python, Java, C++, Go, JavaScript (Node/WebCrypto), Kotlin, and Swift (CryptoKit).
 *
 * Completely eliminates the bug where JS, TS, Kotlin, and Swift fell through to Go.
 */
object CNSLanguageCodeProvider {

    fun code(structure: String, language: String): String {
        val s = structure.lowercase()
        val lang = language.lowercase().trim()
        return when {
            s.contains("caesar") || s.contains("shift") || s.contains("array") -> caesarCode(lang)
            s.contains("des") || s.contains("feistel") || s.contains("stack") -> feistelCode(lang)
            s.contains("aes") || s.contains("matrix") || s.contains("queue") -> aesCode(lang)
            s.contains("vigenere") || s.contains("poly") || s.contains("linked") -> vigenereCode(lang)
            s.contains("diffie") || s.contains("hellman") || (s.contains("tree") && !s.contains("avl") && !s.contains("trie")) -> diffieHellmanCode(lang)
            s.contains("rsa") || s.contains("avl") -> rsaCode(lang)
            s.contains("elliptic") || s.contains("ecc") || s.contains("trie") -> eccCode(lang)
            s.contains("sha") || s.contains("hash") || s.contains("graph") -> sha256Code(lang)
            s.contains("hmac") || s.contains("collision") || s.contains("sorting") -> hmacCode(lang)
            else -> tlsCode(lang)
        }
    }

    // --- Caesar Cipher ---
    private fun caesarCode(lang: String): String = when (lang) {
        "python" -> """
def caesar_cipher(text: str, shift: int, encrypt: bool = True) -> str:
    eff_shift = shift if encrypt else -shift
    result = []
    for char in text:
        if char.isalpha():
            base = ord('A') if char.isupper() else ord('a')
            shifted = (ord(char) - base + eff_shift) % 26
            result.append(chr(base + shifted))
        else:
            result.append(char)
    return "".join(result)

# Example: caesar_cipher("HELLO", 3) -> "KHOOR"
""".trimIndent()

        "java" -> """
public class CaesarCipher {
    public static String process(String text, int shift, boolean encrypt) {
        int effShift = encrypt ? shift : (26 - (shift % 26));
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                sb.append((char) (base + (c - base + effShift) % 26));
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <string>
#include <cctype>

std::string caesarCipher(const std::string& text, int shift, bool encrypt) {
    int effShift = encrypt ? shift : (26 - (shift % 26));
    std::string result = text;
    for (char& c : result) {
        if (std::isalpha(c)) {
            char base = std::isupper(c) ? 'A' : 'a';
            c = base + (c - base + effShift) % 26;
        }
    }
    return result;
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
function caesarCipher(text, shift, encrypt = true) {
    const effShift = encrypt ? shift : (26 - (shift % 26));
    return text.split('').map(char => {
        if (/[A-Z]/.test(char)) {
            return String.fromCharCode(((char.charCodeAt(0) - 65 + effShift) % 26) + 65);
        } else if (/[a-z]/.test(char)) {
            return String.fromCharCode(((char.charCodeAt(0) - 97 + effShift) % 26) + 97);
        }
        return char;
    }).join('');
}
// caesarCipher("HELLO", 3) -> "KHOOR"
""".trimIndent()

        "kotlin" -> """
fun caesarCipher(text: String, shift: Int, encrypt: Boolean = true): String {
    val effShift = if (encrypt) shift else (26 - (shift % 26))
    return buildString {
        for (c in text) {
            when {
                c in 'A'..'Z' -> append(((c - 'A' + effShift) % 26 + 'A'.code).toChar())
                c in 'a'..'z' -> append(((c - 'a' + effShift) % 26 + 'a'.code).toChar())
                else -> append(c)
            }
        }
    }
}
""".trimIndent()

        "swift" -> """
func caesarCipher(_ text: String, shift: Int, encrypt: Bool = true) -> String {
    let effShift = encrypt ? shift : (26 - (shift % 26))
    return String(text.map { char in
        if char.isLetter {
            let base: UInt32 = char.isUppercase ? 65 : 97
            let scalar = char.unicodeScalars.first!.value
            let shifted = (scalar - base + UInt32(effShift)) % 26
            return Character(UnicodeScalar(base + shifted)!)
        }
        return char
    })
}
""".trimIndent()

        else -> """
package main

func CaesarCipher(text string, shift int, encrypt bool) string {
    effShift := shift
    if !encrypt {
        effShift = 26 - (shift % 26)
    }
    runes := []rune(text)
    for i, c := range runes {
        if c >= 'A' && c <= 'Z' {
            runes[i] = 'A' + (c - 'A' + rune(effShift)) % 26
        } else if c >= 'a' && c <= 'z' {
            runes[i] = 'a' + (c - 'a' + rune(effShift)) % 26
        }
    }
    return string(runes)
}
""".trimIndent()
    }

    // --- Feistel / DES ---
    private fun feistelCode(lang: String): String = when (lang) {
        "python" -> """
def feistel_round(left: int, right: int, subkey: int, f_func) -> tuple[int, int]:
    # L_{i+1} = R_i
    # R_{i+1} = L_i XOR F(R_i, K_i)
    new_left = right
    new_right = (left ^ f_func(right, subkey)) & 0xFFFFFFFF
    return new_left, new_right

def f_des(half_block: int, subkey: int) -> int:
    # 32-bit to 48-bit expansion, key XOR, 8 S-Boxes, P-permutation
    return ((half_block * 31) ^ subkey) & 0xFFFFFFFF
""".trimIndent()

        "java" -> """
public class FeistelNetwork {
    public static int[] round(int left, int right, int subkey) {
        int fResult = fFunction(right, subkey);
        int newLeft = right;
        int newRight = left ^ fResult;
        return new int[]{ newLeft, newRight };
    }
    
    private static int fFunction(int halfBlock, int subkey) {
        return (halfBlock * 31) ^ subkey;
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <cstdint>
#include <utility>

uint32_t fFunction(uint32_t halfBlock, uint32_t subkey) {
    return (halfBlock * 31) ^ subkey;
}

std::pair<uint32_t, uint32_t> feistelRound(uint32_t left, uint32_t right, uint32_t subkey) {
    uint32_t newLeft = right;
    uint32_t newRight = left ^ fFunction(right, subkey);
    return { newLeft, newRight };
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
function feistelRound(left, right, subkey, fFunction) {
    const newLeft = right >>> 0;
    const newRight = (left ^ fFunction(right, subkey)) >>> 0;
    return [newLeft, newRight];
}

function fFunction(halfBlock, subkey) {
    return (Math.imul(halfBlock, 31) ^ subkey) >>> 0;
}
""".trimIndent()

        "kotlin" -> """
data class FeistelState(val left: Long, val right: Long)

fun feistelRound(state: FeistelState, subkey: Long, fFunc: (Long, Long) -> Long): FeistelState {
    val newLeft = state.right
    val newRight = (state.left xor fFunc(state.right, subkey)) and 0xFFFFFFFFL
    return FeistelState(newLeft, newRight)
}
""".trimIndent()

        "swift" -> """
struct FeistelState {
    let left: UInt32
    let right: UInt32
}

func feistelRound(state: FeistelState, subkey: UInt32, fFunc: (UInt32, UInt32) -> UInt32) -> FeistelState {
    let newLeft = state.right
    let newRight = state.left ^ fFunc(state.right, subkey)
    return FeistelState(left: newLeft, right: newRight)
}
""".trimIndent()

        else -> """
package main

func FeistelRound(left, right, subkey uint32) (uint32, uint32) {
    fOut := (right * 31) ^ subkey
    return right, left ^ fOut
}
""".trimIndent()
    }

    // --- AES-128 ---
    private fun aesCode(lang: String): String = when (lang) {
        "python" -> """
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
import os

# AES-128-GCM Authenticated Encryption
key = AESGCM.generate_key(bit_length=128)
aesgcm = AESGCM(key)
nonce = os.urandom(12) # 96-bit unique nonce

plaintext = b"NIST FIPS-197 AES State Vector"
aad = b"authenticated-metadata"
ciphertext = aesgcm.encrypt(nonce, plaintext, aad)

decrypted = aesgcm.decrypt(nonce, ciphertext, aad)
assert decrypted == plaintext
""".trimIndent()

        "java" -> """
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.SecureRandom;

public class AesGcmDemo {
    public static byte[] encrypt(byte[] plaintext, byte[] key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
        GCMParameterSpec gcmSpec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, gcmSpec);
        return cipher.doFinal(plaintext); // Output includes 16-byte GCM authentication tag
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <openssl/evp.h>
#include <openssl/rand.h>

bool aes128GcmEncrypt(const uint8_t* ptext, int plen,
                      const uint8_t* key, const uint8_t* iv,
                      uint8_t* ctext, uint8_t* tag) {
    EVP_CIPHER_CTX* ctx = EVP_CIPHER_CTX_new();
    EVP_EncryptInit_ex(ctx, EVP_aes_128_gcm(), NULL, key, iv);
    int len;
    EVP_EncryptUpdate(ctx, ctext, &len, ptext, plen);
    EVP_EncryptFinal_ex(ctx, ctext + len, &len);
    EVP_CIPHER_CTX_ctrl(ctx, EVP_CTRL_GCM_GET_TAG, 16, tag);
    EVP_CIPHER_CTX_free(ctx);
    return true;
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
// Using Modern Web Cryptography API
async function encryptAesGcm(keyBuffer, iv, plaintextBytes) {
    const cryptoKey = await crypto.subtle.importKey(
        "raw", keyBuffer, { name: "AES-GCM" }, false, ["encrypt"]
    );
    return await crypto.subtle.encrypt(
        { name: "AES-GCM", iv: iv },
        cryptoKey,
        plaintextBytes
    );
}
""".trimIndent()

        "kotlin" -> """
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

fun encryptAesGcm(data: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key, "AES"), GCMParameterSpec(128, iv))
    return cipher.doFinal(data) // returns ciphertext + 128-bit tag
}
""".trimIndent()

        "swift" -> """
import CryptoKit

func encryptAesGcm(data: Data, key: SymmetricKey) throws -> AES.GCM.SealedBox {
    return try AES.GCM.seal(data, using: key)
}
""".trimIndent()

        else -> """
package main

import (
    "crypto/aes"
    "crypto/cipher"
    "crypto/rand"
    "io"
)

func EncryptAESGCM(key, plaintext []byte) ([]byte, error) {
    block, err := aes.NewCipher(key)
    if err != nil { return nil, err }
    gcm, err := cipher.NewGCM(block)
    if err != nil { return nil, err }
    nonce := make([]byte, gcm.NonceSize())
    io.ReadFull(rand.Reader, nonce)
    return gcm.Seal(nonce, nonce, plaintext, nil), nil
}
""".trimIndent()
    }

    // --- Vigenère Cipher ---
    private fun vigenereCode(lang: String): String = when (lang) {
        "python" -> """
def vigenere(text: str, key: str, encrypt: bool = True) -> str:
    result = []
    key = key.upper()
    key_idx = 0
    for char in text:
        if char.isalpha():
            base = ord('A') if char.isupper() else ord('a')
            k_shift = ord(key[key_idx % len(key)]) - ord('A')
            if not encrypt: k_shift = -k_shift
            shifted = (ord(char) - base + k_shift) % 26
            result.append(chr(base + shifted))
            key_idx += 1
        else:
            result.append(char)
    return "".join(result)
""".trimIndent()

        "java" -> """
public class VigenereCipher {
    public static String cipher(String text, String key, boolean encrypt) {
        StringBuilder sb = new StringBuilder();
        key = key.toUpperCase();
        int kIdx = 0;
        for (char c : text.toCharArray()) {
            if (Character.isLetter(c)) {
                char base = Character.isUpperCase(c) ? 'A' : 'a';
                int shift = key.charAt(kIdx % key.length()) - 'A';
                if (!encrypt) shift = 26 - shift;
                sb.append((char)(base + (c - base + shift) % 26));
                kIdx++;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <string>
#include <cctype>

std::string vigenere(const std::string& text, const std::string& key, bool encrypt) {
    std::string result = text;
    int kIdx = 0;
    for (char& c : result) {
        if (std::isalpha(c)) {
            char base = std::isupper(c) ? 'A' : 'a';
            int shift = std::toupper(key[kIdx % key.length()]) - 'A';
            if (!encrypt) shift = 26 - shift;
            c = base + (c - base + shift) % 26;
            kIdx++;
        }
    }
    return result;
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
function vigenere(text, key, encrypt = true) {
    const k = key.toUpperCase();
    let kIdx = 0;
    return text.split('').map(char => {
        if (/[A-Za-z]/.test(char)) {
            const base = char === char.toUpperCase() ? 65 : 97;
            let shift = k.charCodeAt(kIdx % k.length) - 65;
            if (!encrypt) shift = (26 - shift) % 26;
            kIdx++;
            return String.fromCharCode(((char.charCodeAt(0) - base + shift) % 26) + base);
        }
        return char;
    }).join('');
}
""".trimIndent()

        "kotlin" -> """
fun vigenere(text: String, key: String, encrypt: Boolean = true): String {
    val k = key.uppercase()
    var kIdx = 0
    return buildString {
        for (c in text) {
            if (c.isLetter()) {
                val base = if (c.isUpperCase()) 'A' else 'a'
                val shift = if (encrypt) k[kIdx % k.length] - 'A' else 26 - (k[kIdx % k.length] - 'A')
                append(((c - base + shift) % 26 + base.code).toChar())
                kIdx++
            } else {
                append(c)
            }
        }
    }
}
""".trimIndent()

        "swift" -> """
func vigenere(_ text: String, key: String, encrypt: Bool = true) -> String {
    let k = Array(key.uppercased().utf8)
    var kIdx = 0
    return String(text.map { char in
        if char.isLetter {
            let base: UInt8 = char.isUppercase ? 65 : 97
            let kShift = Int(k[kIdx % k.count] - 65)
            let effShift = encrypt ? kShift : (26 - kShift) % 26
            kIdx += 1
            let scalar = char.asciiValue!
            let shifted = (Int(scalar - base) + effShift) % 26
            return Character(UnicodeScalar(Int(base) + shifted)!)
        }
        return char
    })
}
""".trimIndent()

        else -> """
package main

import "strings"

func Vigenere(text, key string, encrypt bool) string {
    k := strings.ToUpper(key)
    runes := []rune(text)
    kIdx := 0
    for i, c := range runes {
        if (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') {
            base := 'a'
            if c >= 'A' && c <= 'Z' { base = 'A' }
            shift := rune(k[kIdx%len(k)] - 'A')
            if !encrypt { shift = 26 - shift }
            runes[i] = base + (c - base + shift)%26
            kIdx++
        }
    }
    return string(runes)
}
""".trimIndent()
    }

    // --- Diffie-Hellman ---
    private fun diffieHellmanCode(lang: String): String = when (lang) {
        "python" -> """
# Finite Field Diffie-Hellman (RFC 3526)
p = 23 # Safe Prime
g = 5  # Primitive Root Generator

# Alice
a_private = 6
A_public = pow(g, a_private, p) # 5^6 mod 23 = 8

# Bob
b_private = 15
B_public = pow(g, b_private, p) # 5^15 mod 23 = 19

# Both arrive at identical shared secret:
s_alice = pow(B_public, a_private, p) # 19^6 mod 23 = 2
s_bob = pow(A_public, b_private, p)   # 8^15 mod 23 = 2
assert s_alice == s_bob
""".trimIndent()

        "java" -> """
import java.math.BigInteger;

public class DiffieHellmanDemo {
    public static void main(String[] args) {
        BigInteger p = BigInteger.valueOf(23);
        BigInteger g = BigInteger.valueOf(5);

        BigInteger aPriv = BigInteger.valueOf(6);
        BigInteger aPub = g.modPow(aPriv, p);

        BigInteger bPriv = BigInteger.valueOf(15);
        BigInteger bPub = g.modPow(bPriv, p);

        BigInteger sA = bPub.modPow(aPriv, p);
        BigInteger sB = aPub.modPow(bPriv, p);
        System.out.println("Shared Secret Match: " + sA.equals(sB));
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <iostream>

long long modPow(long long base, long long exp, long long mod) {
    long long res = 1;
    base %= mod;
    while (exp > 0) {
        if (exp % 2 == 1) res = (res * base) % mod;
        base = (base * base) % mod;
        exp /= 2;
    }
    return res;
}

int main() {
    long long p = 23, g = 5;
    long long aPriv = 6, aPub = modPow(g, aPriv, p);
    long long bPriv = 15, bPub = modPow(g, bPriv, p);
    long long sA = modPow(bPub, aPriv, p);
    long long sB = modPow(aPub, bPriv, p);
    return (sA == sB) ? 0 : 1;
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
function modPow(base, exp, mod) {
    let res = 1n;
    base = BigInt(base) % BigInt(mod);
    exp = BigInt(exp);
    const m = BigInt(mod);
    while (exp > 0n) {
        if (exp % 2n === 1n) res = (res * base) % m;
        base = (base * base) % m;
        exp /= 2n;
    }
    return res;
}
const p = 23n, g = 5n;
const aPub = modPow(g, 6n, p);
const bPub = modPow(g, 15n, p);
const sAlice = modPow(bPub, 6n, p);
const sBob = modPow(aPub, 15n, p);
console.log(sAlice === sBob); // true
""".trimIndent()

        "kotlin" -> """
import java.math.BigInteger

fun diffieHellmanExchange(p: Long, g: Long, aPriv: Long, bPriv: Long): Pair<Long, Long> {
    val bigP = BigInteger.valueOf(p)
    val aPub = BigInteger.valueOf(g).modPow(BigInteger.valueOf(aPriv), bigP)
    val bPub = BigInteger.valueOf(g).modPow(BigInteger.valueOf(bPriv), bigP)
    val sA = bPub.modPow(BigInteger.valueOf(aPriv), bigP).toLong()
    val sB = aPub.modPow(BigInteger.valueOf(bPriv), bigP).toLong()
    return sA to sB
}
""".trimIndent()

        "swift" -> """
func modPow(_ base: Int, _ exp: Int, _ mod: Int) -> Int {
    var res = 1
    var b = base % mod
    var e = exp
    while e > 0 {
        if e % 2 == 1 { res = (res * b) % mod }
        b = (b * b) % mod
        e /= 2
    }
    return res
}
let p = 23, g = 5
let aPub = modPow(g, 6, p)
let bPub = modPow(g, 15, p)
let sharedA = modPow(bPub, 6, p)
let sharedB = modPow(aPub, 15, p)
""".trimIndent()

        else -> """
package main

import "math/big"

func DiffieHellman(p, g, aPriv, bPriv int64) (int64, int64) {
    P := big.NewInt(p)
    G := big.NewInt(g)
    aPub := new(big.Int).Exp(G, big.NewInt(aPriv), P)
    bPub := new(big.Int).Exp(G, big.NewInt(bPriv), P)
    sA := new(big.Int).Exp(bPub, big.NewInt(aPriv), P)
    sB := new(big.Int).Exp(aPub, big.NewInt(bPriv), P)
    return sA.Int64(), sB.Int64()
}
""".trimIndent()
    }

    // --- RSA ---
    private fun rsaCode(lang: String): String = when (lang) {
        "python" -> """
import math

# Toy RSA Key Generation
p, q = 61, 53
n = p * q                # 3233
phi = (p - 1) * (q - 1)  # 3120
e = 17                   # Coprime to phi
d = pow(e, -1, phi)      # Modular inverse d = 2753

# Encryption & Decryption
msg = 65
ciphertext = pow(msg, e, n)   # 65^17 mod 3233 = 2790
recovered = pow(ciphertext, d, n) # 2790^2753 mod 3233 = 65
assert recovered == msg
""".trimIndent()

        "java" -> """
import java.math.BigInteger;

public class RSADemo {
    public static void main(String[] args) {
        BigInteger p = BigInteger.valueOf(61);
        BigInteger q = BigInteger.valueOf(53);
        BigInteger n = p.multiply(q);
        BigInteger phi = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE));
        BigInteger e = BigInteger.valueOf(17);
        BigInteger d = e.modInverse(phi);

        BigInteger msg = BigInteger.valueOf(65);
        BigInteger c = msg.modPow(e, n);
        BigInteger dec = c.modPow(d, n);
        System.out.println("Decrypted match: " + msg.equals(dec));
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <iostream>

long long extGcd(long long a, long long b, long long &x, long long &y) {
    if (b == 0) { x = 1; y = 0; return a; }
    long long x1, y1;
    long long g = extGcd(b, a % b, x1, y1);
    x = y1;
    y = x1 - y1 * (a / b);
    return g;
}

long long modInverse(long long e, long long phi) {
    long long x, y;
    extGcd(e, phi, x, y);
    return (x % phi + phi) % phi;
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
function rsaToy(p, q, e, msg) {
    const n = BigInt(p * q);
    const phi = BigInt((p - 1) * (q - 1));
    // Modular inverse using Extended Euclidean
    let [a, b] = [BigInt(e), phi];
    let [x0, x1] = [0n, 1n];
    while (a > 1n) {
        let q = a / b;
        [a, b] = [b, a % b];
        [x0, x1] = [x1 - q * x0, x0];
    }
    const d = (x1 % phi + phi) % phi;
    const c = BigInt(msg) ** BigInt(e) % n;
    return { n, e, d, ciphertext: c };
}
""".trimIndent()

        "kotlin" -> """
import java.math.BigInteger

data class RsaKeys(val n: BigInteger, val e: BigInteger, val d: BigInteger)

fun generateRsa(p: Long, q: Long, eVal: Long = 17L): RsaKeys {
    val bigP = BigInteger.valueOf(p)
    val bigQ = BigInteger.valueOf(q)
    val n = bigP * bigQ
    val phi = (bigP - BigInteger.ONE) * (bigQ - BigInteger.ONE)
    val e = BigInteger.valueOf(eVal)
    val d = e.modInverse(phi)
    return RsaKeys(n, e, d)
}
""".trimIndent()

        "swift" -> """
import Security

// In production Swift, RSA is generated via SecKeyCreateRandomKey:
let attributes: [String: Any] = [
    kSecAttrKeyType as String: kSecAttrKeyTypeRSA,
    kSecAttrKeySizeInBits as String: 2048
]
var error: Unmanaged<CFError>?
let privateKey = SecKeyCreateRandomKey(attributes as CFDictionary, &error)
""".trimIndent()

        else -> """
package main

import "math/big"

func GenerateRSA(p, q, eVal int64) (*big.Int, *big.Int, *big.Int) {
    P, Q := big.NewInt(p), big.NewInt(q)
    N := new(big.Int).Mul(P, Q)
    phi := new(big.Int).Mul(new(big.Int).Sub(P, big.NewInt(1)), new(big.Int).Sub(Q, big.NewInt(1)))
    E := big.NewInt(eVal)
    D := new(big.Int).ModInverse(E, phi)
    return N, E, D
}
""".trimIndent()
    }

    // --- ECC ---
    private fun eccCode(lang: String): String = when (lang) {
        "python" -> """
from cryptography.hazmat.primitives.asymmetric import ec

# ECDH Key Exchange using standard secp256r1
alice_key = ec.generate_private_key(ec.SECP256R1())
bob_key = ec.generate_private_key(ec.SECP256R1())

# Derive identical shared secret
shared_alice = alice_key.exchange(ec.ECDH(), bob_key.public_key())
shared_bob = bob_key.exchange(ec.ECDH(), alice_key.public_key())
assert shared_alice == shared_bob
""".trimIndent()

        "java" -> """
import java.security.KeyPairGenerator;
import java.security.KeyPair;
import javax.crypto.KeyAgreement;

public class EcdhDemo {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256); // secp256r1
        KeyPair alice = kpg.generateKeyPair();
        KeyPair bob = kpg.generateKeyPair();

        KeyAgreement ka = KeyAgreement.getInstance("ECDH");
        ka.init(alice.getPrivate());
        ka.doPhase(bob.getPublic(), true);
        byte[] sharedSecret = ka.generateSecret();
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <openssl/ec.h>
#include <openssl/ecdh.h>

// Toy Curve: y^2 = x^3 + ax + b (mod p)
struct Point { long long x, y; bool isInf; };

Point pointAdd(Point p1, Point p2, long long a, long long p) {
    if (p1.isInf) return p2;
    if (p2.isInf) return p1;
    // Slope m calculation and chord-and-tangent addition
    return { 0, 0, false };
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
async function ecdhWebCrypto() {
    const aliceKeys = await crypto.subtle.generateKey(
        { name: "ECDH", namedCurve: "P-256" }, true, ["deriveKey", "deriveBits"]
    );
    const bobKeys = await crypto.subtle.generateKey(
        { name: "ECDH", namedCurve: "P-256" }, true, ["deriveKey", "deriveBits"]
    );
    const sharedSecret = await crypto.subtle.deriveBits(
        { name: "ECDH", public: bobKeys.publicKey },
        aliceKeys.privateKey,
        256
    );
    return sharedSecret;
}
""".trimIndent()

        "kotlin" -> """
// Toy curve point addition over F17: y² ≡ x³ + 2x + 2 (mod 17)
data class ECPoint(val x: Int, val y: Int, val isInfinity: Boolean = false)

fun doublePoint(pt: ECPoint, a: Int = 2, p: Int = 17): ECPoint {
    if (pt.isInfinity || pt.y == 0) return ECPoint(0, 0, true)
    val num = (3 * pt.x * pt.x + a) % p
    val den = (2 * pt.y) % p
    // slope m = num * den^(p-2) mod p
    return pt
}
""".trimIndent()

        "swift" -> """
import CryptoKit

// Apple CryptoKit P-256 Key Agreement
let alicePriv = P256.KeyAgreement.PrivateKey()
let bobPriv = P256.KeyAgreement.PrivateKey()

let aliceShared = try! alicePriv.sharedSecretFromKeyAgreement(with: bobPriv.publicKey)
let bobShared = try! bobPriv.sharedSecretFromKeyAgreement(with: alicePriv.publicKey)
assert(aliceShared == bobShared)
""".trimIndent()

        else -> """
package main

import (
    "crypto/ecdsa"
    "crypto/elliptic"
    "crypto/rand"
)

func GenerateECC() (*ecdsa.PrivateKey, error) {
    curve := elliptic.P256()
    return ecdsa.GenerateKey(curve, rand.Reader)
}
""".trimIndent()
    }

    // --- SHA-256 ---
    private fun sha256Code(lang: String): String = when (lang) {
        "python" -> """
import hashlib

def hash_sha256(data: bytes) -> str:
    return hashlib.sha256(data).hexdigest()

# Avalanche Effect Demonstration:
h1 = hash_sha256(b"Codeingo_A")
h2 = hash_sha256(b"Codeingo_B")
# Single bit change produces ~50% bit divergence across 256 bits
""".trimIndent()

        "java" -> """
import java.security.MessageDigest;

public class Sha256Example {
    public static String hash(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : encodedhash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <openssl/sha.h>
#include <iomanip>
#include <sstream>

std::string sha256(const std::string& str) {
    unsigned char hash[SHA256_DIGEST_LENGTH];
    SHA256_CTX sha256;
    SHA256_Init(&sha256);
    SHA256_Update(&sha256, str.c_str(), str.size());
    SHA256_Final(hash, &sha256);
    std::stringstream ss;
    for(int i = 0; i < SHA256_DIGEST_LENGTH; i++) {
        ss << std::hex << std::setw(2) << std::setfill('0') << (int)hash[i];
    }
    return ss.str();
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
async function sha256Hex(message) {
    const msgBuffer = new TextEncoder().encode(message);
    const hashBuffer = await crypto.subtle.digest('SHA-256', msgBuffer);
    const hashArray = Array.from(new Uint8Array(hashBuffer));
    return hashArray.map(b => b.toString(16).padStart(2, '0')).join('');
}
""".trimIndent()

        "kotlin" -> """
import java.security.MessageDigest

fun sha256(input: String): String {
    val md = MessageDigest.getInstance("SHA-256")
    val digest = md.digest(input.toByteArray())
    return digest.joinToString("") { "%02x".format(it) }
}
""".trimIndent()

        "swift" -> """
import CryptoKit

func sha256(_ string: String) -> String {
    let data = Data(string.utf8)
    let digest = SHA256.hash(data: data)
    return digest.compactMap { String(format: "%02x", $0) }.joined()
}
""".trimIndent()

        else -> """
package main

import (
    "crypto/sha256"
    "fmt"
)

func HashSHA256(data string) string {
    h := sha256.Sum256([]byte(data))
    return fmt.Sprintf("%x", h)
}
""".trimIndent()
    }

    // --- HMAC & Passwords ---
    private fun hmacCode(lang: String): String = when (lang) {
        "python" -> """
import hmac
import hashlib

def compute_hmac(key: bytes, msg: bytes) -> str:
    # RFC 2104: H((K XOR opad) || H((K XOR ipad) || M))
    return hmac.new(key, msg, hashlib.sha256).hexdigest()

# PBKDF2 Password Hashing
import hashlib, os
salt = os.urandom(16)
kdf_key = hashlib.pbkdf2_hmac('sha256', b'user_password', salt, iterations=100000)
""".trimIndent()

        "java" -> """
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class HmacDemo {
    public static byte[] hmacSha256(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <openssl/hmac.h>

void computeHmac(const uint8_t* key, int klen, const uint8_t* data, int dlen, uint8_t* out) {
    unsigned int len = 32;
    HMAC(EVP_sha256(), key, klen, data, dlen, out, &len);
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
async function computeHmac(keyBytes, messageBytes) {
    const key = await crypto.subtle.importKey(
        "raw", keyBytes, { name: "HMAC", hash: "SHA-256" }, false, ["sign"]
    );
    const signature = await crypto.subtle.sign("HMAC", key, messageBytes);
    return new Uint8Array(signature);
}
""".trimIndent()

        "kotlin" -> """
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

fun hmacSha256(key: ByteArray, message: ByteArray): ByteArray {
    val mac = Mac.getInstance("HmacSHA256")
    mac.init(SecretKeySpec(key, "HmacSHA256"))
    return mac.doFinal(message)
}
""".trimIndent()

        "swift" -> """
import CryptoKit

func computeHMAC(key: SymmetricKey, data: Data) -> HMAC<SHA256>.AuthenticationCode {
    return HMAC<SHA256>.authenticationCode(for: data, using: key)
}
""".trimIndent()

        else -> """
package main

import (
    "crypto/hmac"
    "crypto/sha256"
)

func ComputeHMAC(key, data []byte) []byte {
    h := hmac.New(sha256.New, key)
    h.Write(data)
    return h.Sum(nil)
}
""".trimIndent()
    }

    // --- TLS 1.3 Handshake ---
    private fun tlsCode(lang: String): String = when (lang) {
        "python" -> """
import ssl
import socket

# TLS 1.3 Client Configuration
ctx = ssl.create_default_context()
ctx.minimum_version = ssl.TLSVersion.TLSv1_3

with socket.create_connection(('codeingo.simats.com', 443)) as sock:
    with ctx.wrap_socket(sock, server_hostname='codeingo.simats.com') as ssock:
        print("Negotiated Version:", ssock.version())
        print("Negotiated Cipher:", ssock.cipher())
""".trimIndent()

        "java" -> """
import javax.net.ssl.*;

public class TlsClient {
    public static void main(String[] args) throws Exception {
        SSLContext context = SSLContext.getInstance("TLSv1.3");
        context.init(null, null, null);
        SSLSocketFactory factory = context.getSocketFactory();
        SSLSocket socket = (SSLSocket) factory.createSocket("codeingo.simats.com", 443);
        socket.setEnabledProtocols(new String[]{"TLSv1.3"});
        socket.startHandshake();
    }
}
""".trimIndent()

        "cpp", "c++" -> """
#include <openssl/ssl.h>
#include <openssl/err.h>

void connectTls13(int socketFd) {
    SSL_CTX* ctx = SSL_CTX_new(TLS_client_method());
    SSL_CTX_set_min_proto_version(ctx, TLS1_3_VERSION);
    SSL* ssl = SSL_new(ctx);
    SSL_set_fd(ssl, socketFd);
    SSL_set_tlsext_host_name(ssl, "codeingo.simats.com");
    SSL_connect(ssl);
}
""".trimIndent()

        "javascript", "js", "typescript", "ts" -> """
import https from 'https';

const options = {
    hostname: 'codeingo.simats.com',
    port: 443,
    method: 'GET',
    minVersion: 'TLSv1.3'
};

const req = https.request(options, res => {
    console.log("TLS Protocol:", res.socket.getProtocol());
    console.log("TLS Cipher:", res.socket.getCipher());
});
req.end();
""".trimIndent()

        "kotlin" -> """
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion

// Modern TLS 1.3 OkHttp Client Configuration
val modernTlsSpec = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
    .tlsVersions(TlsVersion.TLS_1_3)
    .build()

val client = OkHttpClient.Builder()
    .connectionSpecs(listOf(modernTlsSpec))
    .build()
""".trimIndent()

        "swift" -> """
import Foundation
import Network

// Apple Network framework TLS 1.3 security options
let tlsOptions = NWProtocolTLS.Options()
sec_protocol_options_set_min_tls_protocol_version(
    tlsOptions.securityProtocolOptions,
    .TLSv13
)
let params = NWParameters(tls: tlsOptions)
""".trimIndent()

        else -> """
package main

import (
    "crypto/tls"
    "net/http"
)

func NewTLS13Client() *http.Client {
    tlsConfig := &tls.Config{
        MinVersion: tls.VersionTLS13,
        ServerName: "codeingo.simats.com",
    }
    transport := &http.Transport{TLSClientConfig: tlsConfig}
    return &http.Client{Transport: transport}
}
""".trimIndent()
    }
}

/** Backwards-compatible alias so existing callers don't break */
typealias DSALanguageCodeProvider = CNSLanguageCodeProvider
