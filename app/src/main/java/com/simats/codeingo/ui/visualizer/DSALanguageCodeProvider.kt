package com.simats.codeingo.ui.visualizer

object DSALanguageCodeProvider {

    fun code(structure: String, language: String): String {
        val s = structure.lowercase()
        val lang = language.lowercase()
        return when {
            s.contains("caesar") || s.contains("shift") || s.contains("array") -> caesarCode(lang)
            s.contains("des") || s.contains("feistel") || s.contains("stack") -> feistelCode(lang)
            s.contains("aes") || s.contains("matrix") || s.contains("queue") -> aesCode(lang)
            s.contains("vigenere") || s.contains("poly") || s.contains("linked") -> vigenereCode(lang)
            s.contains("diffie") || s.contains("hellman") || s.contains("tree") && !s.contains("avl") && !s.contains("trie") -> diffieHellmanCode(lang)
            s.contains("rsa") || s.contains("avl") -> rsaCode(lang)
            s.contains("elliptic") || s.contains("ecc") || s.contains("trie") -> eccCode(lang)
            s.contains("sha") || s.contains("hash") || s.contains("graph") -> sha256Code(lang)
            s.contains("hmac") || s.contains("collision") || s.contains("sorting") -> hmacCode(lang)
            else -> tlsCode(lang)
        }
    }

    private fun caesarCode(lang: String): String = when (lang) {
        "python" -> """
def caesar_cipher(text, shift, encrypt=True):
    shift = shift if encrypt else -shift
    result = []
    for char in text:
        if char.isalpha():
            base = ord('A') if char.isupper() else ord('a')
            shifted = (ord(char) - base + shift) % 26
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

    private fun feistelCode(lang: String): String = when (lang) {
        "python" -> """
def feistel_round(left, right, subkey, f_func):
    # Standard Feistel equation:
    # L_{i+1} = R_i
    # R_{i+1} = L_i XOR F(R_i, K_i)
    new_left = right
    new_right = left ^ f_func(right, subkey)
    return new_left, new_right

def f_round_func(half_block, subkey):
    # Non-linear expansion, subkey XOR, S-box substitution
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
        else -> """
package main

func FeistelRound(left, right, subkey uint32) (uint32, uint32) {
    fOut := (right * 31) ^ subkey
    return right, left ^ fOut
}
""".trimIndent()
    }

    private fun aesCode(lang: String): String = when (lang) {
        "python" -> """
from cryptography.hazmat.primitives.ciphers.aead import AESGCM
import os

# Generate a secure 256-bit AES key and 96-bit nonce
key = AESGCM.generate_key(bit_length=256)
aesgcm = AESGCM(key)
nonce = os.urandom(12)

# Authenticated Encryption with Associated Data (AEAD)
plaintext = b"Classified Payload"
aad = b"header-metadata"
ciphertext = aesgcm.encrypt(nonce, plaintext, aad)

# Decryption & Tag Verification
decrypted = aesgcm.decrypt(nonce, ciphertext, aad)
assert decrypted == plaintext
""".trimIndent()
        "java" -> """
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import java.security.SecureRandom;

public class AESGCMExample {
    public static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec spec = new GCMParameterSpec(128, iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, spec);
        return cipher.doFinal(plaintext); // Ciphertext + 16-byte Auth Tag
    }
}
""".trimIndent()
        "cpp", "c++" -> """
#include <openssl/evp.h>
#include <openssl/rand.h>

bool aes_gcm_encrypt(const unsigned char* ptext, int plen,
                     const unsigned char* key, const unsigned char* iv,
                     unsigned char* ctext, unsigned char* tag) {
    EVP_CIPHER_CTX* ctx = EVP_CIPHER_CTX_new();
    EVP_EncryptInit_ex(ctx, EVP_aes_256_gcm(), NULL, key, iv);
    int len;
    EVP_EncryptUpdate(ctx, ctext, &len, ptext, plen);
    EVP_EncryptFinal_ex(ctx, ctext + len, &len);
    EVP_CIPHER_CTX_ctrl(ctx, EVP_CTRL_GCM_GET_TAG, 16, tag);
    EVP_CIPHER_CTX_free(ctx);
    return true;
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

    private fun vigenereCode(lang: String): String = when (lang) {
        "python" -> """
def vigenere(text, key, encrypt=True):
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

std::string vigenere(const std::string& text, const std::string& key, bool enc) {
    std::string out = text;
    int kIdx = 0;
    for (char& c : out) {
        if (std::isalpha(c)) {
            char base = std::isupper(c) ? 'A' : 'a';
            int shift = std::toupper(key[kIdx % key.length()]) - 'A';
            if (!enc) shift = 26 - shift;
            c = base + (c - base + shift) % 26;
            kIdx++;
        }
    }
    return out;
}
""".trimIndent()
        else -> """
package main

func Vigenere(text, key string, encrypt bool) string {
    out := []rune(text)
    kRunes := []rune(key)
    kIdx := 0
    for i, c := range out {
        if (c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') {
            base := 'a'
            if c >= 'A' && c <= 'Z' { base = 'A' }
            shift := rune(kRunes[kIdx % len(kRunes)] - 'A')
            if !encrypt { shift = 26 - shift }
            out[i] = base + (c - base + shift) % 26
            kIdx++
        }
    }
    return string(out)
}
""".trimIndent()
    }

    private fun diffieHellmanCode(lang: String): String = when (lang) {
        "python" -> """
import random

# Public parameters agreed in the clear
P = 23  # Shared Prime Modulus
G = 5   # Shared Generator Base

# Alice picks secret a, computes A = g^a mod p
a = random.randint(1, P - 1)
A = pow(G, a, P)

# Bob picks secret b, computes B = g^b mod p
b = random.randint(1, P - 1)
B = pow(G, b, P)

# Alice computes shared secret: S_Alice = B^a mod p
s_alice = pow(B, a, P)

# Bob computes shared secret: S_Bob = A^b mod p
s_bob = pow(A, b, P)

assert s_alice == s_bob  # Identical shared key established!
""".trimIndent()
        "java" -> """
import java.math.BigInteger;

public class DiffieHellman {
    public static void main(String[] args) {
        BigInteger p = new BigInteger("23"); // Prime modulus
        BigInteger g = new BigInteger("5");  // Generator base
        
        BigInteger a = new BigInteger("6");  // Alice private
        BigInteger A = g.modPow(a, p);       // Alice public
        
        BigInteger b = new BigInteger("15"); // Bob private
        BigInteger B = g.modPow(b, p);       // Bob public
        
        BigInteger sharedAlice = B.modPow(a, p);
        BigInteger sharedBob = A.modPow(b, p);
        System.out.println("Shared Secret Match: " + sharedAlice.equals(sharedBob));
    }
}
""".trimIndent()
        "cpp", "c++" -> """
#include <iostream>

long long modExp(long long base, long long exp, long long mod) {
    long long res = 1;
    base %= mod;
    while (exp > 0) {
        if (exp % 2 == 1) res = (res * base) % mod;
        base = (base * base) % mod;
        exp /= 2;
    }
    return res;
}

// Diffie-Hellman Key Agreement:
// A = g^a % p, B = g^b % p, S = B^a % p == A^b % p
""".trimIndent()
        else -> """
package main

import "math/big"

func DiffieHellmanShared(p, g, privA, privB *big.Int) (*big.Int, *big.Int) {
    pubA := new(big.Int).Exp(g, privA, p)
    pubB := new(big.Int).Exp(g, privB, p)
    
    secretA := new(big.Int).Exp(pubB, privA, p)
    secretB := new(big.Int).Exp(pubA, privB, p)
    return secretA, secretB
}
""".trimIndent()
    }

    private fun rsaCode(lang: String): String = when (lang) {
        "python" -> """
# RSA Public Key Mathematics
p, q = 61, 53
n = p * q                      # Modulus: 3233
phi = (p - 1) * (q - 1)        # Euler's Totient: 3120
e = 17                         # Public Exponent (coprime to phi)
d = pow(e, -1, phi)            # Private Exponent (modular inverse): 2753

# Public Key: (e, n), Private Key: (d, n)
message = 65                   # ASCII 'A'
ciphertext = pow(message, e, n)   # Encrypt: c = m^e mod n
decrypted = pow(ciphertext, d, n) # Decrypt: m = c^d mod n
assert decrypted == message
""".trimIndent()
        "java" -> """
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import javax.crypto.Cipher;

public class RSAExample {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);
        KeyPair kp = kpg.generateKeyPair();
        
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, kp.getPublic());
        byte[] ct = cipher.doFinal("Confidential".getBytes());
        
        cipher.init(Cipher.DECRYPT_MODE, kp.getPrivate());
        byte[] pt = cipher.doFinal(ct);
    }
}
""".trimIndent()
        "cpp", "c++" -> """
#include <openssl/rsa.h>
#include <openssl/pem.h>

RSA* generateRSAKeyPair() {
    BIGNUM* e = BN_new();
    BN_set_word(e, RSA_F4); // e = 65537
    RSA* rsa = RSA_new();
    RSA_generate_key_ex(rsa, 2048, e, NULL);
    BN_free(e);
    return rsa;
}
""".trimIndent()
        else -> """
package main

import (
    "crypto/rand"
    "crypto/rsa"
    "crypto/sha256"
)

func GenerateAndEncrypt(msg []byte) ([]byte, error) {
    privKey, _ := rsa.GenerateKey(rand.Reader, 2048)
    return rsa.EncryptOAEP(sha256.New(), rand.Reader, &privKey.PublicKey, msg, nil)
}
""".trimIndent()
    }

    private fun eccCode(lang: String): String = when (lang) {
        "python" -> """
from cryptography.hazmat.primitives.asymmetric import ec
from cryptography.hazmat.primitives import hashes

# Elliptic Curve (secp256k1) Digital Signatures (ECDSA)
private_key = ec.generate_private_key(ec.SECP256K1())
public_key = private_key.public_key()

message = b"Blockchain Transaction 0x4f3a..."
signature = private_key.sign(message, ec.ECDSA(hashes.SHA256()))

# Public Key Verification
public_key.verify(signature, message, ec.ECDSA(hashes.SHA256()))
""".trimIndent()
        "java" -> """
import java.security.KeyPairGenerator;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;

public class ECCExample {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(new ECGenParameterSpec("secp256r1"));
        var kp = kpg.generateKeyPair();
        
        Signature signer = Signature.getInstance("SHA256withECDSA");
        signer.initSign(kp.getPrivate());
        signer.update("Signed Message".getBytes());
        byte[] sig = signer.sign();
    }
}
""".trimIndent()
        "cpp", "c++" -> """
#include <openssl/ec.h>
#include <openssl/obj_mac.h>

EC_KEY* generateECC() {
    EC_KEY* key = EC_KEY_new_by_curve_name(NID_secp256k1);
    EC_KEY_generate_key(key);
    return key;
}
""".trimIndent()
        else -> """
package main

import (
    "crypto/ecdsa"
    "crypto/elliptic"
    "crypto/rand"
)

func GenerateECCKey() (*ecdsa.PrivateKey, error) {
    return ecdsa.GenerateKey(elliptic.P256(), rand.Reader)
}
""".trimIndent()
    }

    private fun sha256Code(lang: String): String = when (lang) {
        "python" -> """
import hashlib

data = b"AshCrypt Cryptography & Network Security"
hasher = hashlib.sha256()
hasher.update(data)
digest = hasher.hexdigest()

print("SHA-256 Digest (256 bits / 64 hex chars):")
print(digest)
""".trimIndent()
        "java" -> """
import java.security.MessageDigest;

public class SHA256Example {
    public static String hash(String input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }
}
""".trimIndent()
        "cpp", "c++" -> """
#include <openssl/sha.h>
#include <string>
#include <sstream>
#include <iomanip>

std::string sha256(const std::string& input) {
    unsigned char hash[SHA256_DIGEST_LENGTH];
    SHA256((const unsigned char*)input.c_str(), input.size(), hash);
    std::stringstream ss;
    for (int i = 0; i < SHA256_DIGEST_LENGTH; i++)
        ss << std::hex << std::setw(2) << std::setfill('0') << (int)hash[i];
    return ss.str();
}
""".trimIndent()
        else -> """
package main

import (
    "crypto/sha256"
    "encoding/hex"
)

func SHA256Hex(msg string) string {
    h := sha256.Sum256([]byte(msg))
    return hex.EncodeToString(h[:])
}
""".trimIndent()
    }

    private fun hmacCode(lang: String): String = when (lang) {
        "python" -> """
import hmac
import hashlib

key = b"secret-authentication-key-256"
message = b"Action: Transfer 500 Credits"

# HMAC-SHA256: H((K ^ opad) || H((K ^ ipad) || M))
tag = hmac.new(key, message, hashlib.sha256).hexdigest()

# Constant-time comparison prevents timing attacks
assert hmac.compare_digest(tag, hmac.new(key, message, hashlib.sha256).hexdigest())
""".trimIndent()
        "java" -> """
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;

public class HMACExample {
    public static byte[] compute(byte[] key, byte[] data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key, "HmacSHA256"));
        return mac.doFinal(data);
    }
}
""".trimIndent()
        "cpp", "c++" -> """
#include <openssl/hmac.h>

unsigned char* computeHMAC(const void* key, int keyLen,
                           const unsigned char* data, int dataLen,
                           unsigned char* result) {
    unsigned int len = 32;
    return HMAC(EVP_sha256(), key, keyLen, data, dataLen, result, &len);
}
""".trimIndent()
        else -> """
package main

import (
    "crypto/hmac"
    "crypto/sha256"
)

func ComputeHMAC(key, data []byte) []byte {
    mac := hmac.New(sha256.New, key)
    mac.Write(data)
    return mac.Sum(nil)
}
""".trimIndent()
    }

    private fun tlsCode(lang: String): String = when (lang) {
        "python" -> """
import ssl
import socket

hostname = 'ashcrypt.edu'
context = ssl.create_default_context()
# Enforce TLS 1.3 minimum protocol version
context.minimum_version = ssl.TLSVersion.TLSv1_3

with socket.create_connection((hostname, 443)) as sock:
    with context.wrap_socket(sock, server_hostname=hostname) as ssock:
        print("Connected with TLS version:", ssock.version())
        print("Negotiated Cipher Suite:", ssock.cipher())
""".trimIndent()
        "java" -> """
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class TLS13Client {
    public static void connect() throws Exception {
        SSLContext ctx = SSLContext.getInstance("TLSv1.3");
        ctx.init(null, null, null);
        SSLSocketFactory factory = ctx.getSocketFactory();
        try (SSLSocket socket = (SSLSocket) factory.createSocket("ashcrypt.edu", 443)) {
            socket.setEnabledProtocols(new String[]{"TLSv1.3"});
            socket.startHandshake();
        }
    }
}
""".trimIndent()
        "cpp", "c++" -> """
#include <openssl/ssl.h>

SSL_CTX* createTLS13Context() {
    SSL_CTX* ctx = SSL_CTX_new(TLS_client_method());
    SSL_CTX_set_min_proto_version(ctx, TLS1_3_VERSION);
    SSL_CTX_set_ciphersuites(ctx, "TLS_AES_256_GCM_SHA384:TLS_CHACHA20_POLY1305_SHA256");
    return ctx;
}
""".trimIndent()
        else -> """
package main

import (
    "crypto/tls"
    "net/http"
)

func SecureTLS13Client() *http.Client {
    tlsConfig := &tls.Config{
        MinVersion: tls.VersionTLS13,
    }
    transport := &http.Transport{TLSClientConfig: tlsConfig}
    return &http.Client{Transport: transport}
}
""".trimIndent()
    }
}
