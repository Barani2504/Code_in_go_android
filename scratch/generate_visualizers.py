import json

specs = [
    {
        "id": "cia_triad", "title": "CIA Triad Tap-Game", "realm": 1, "realmTitle": "Secrets 101",
        "promise": "Learn the three pillars of security through interactive scenarios.",
        "story": ("CIA Triad", "Security guards three things: secrets, truth, and access.", "A bank vault hides gold, stamps bars to detect counterfeits, and opens during business hours.", "Confidentiality, Integrity, Availability", "🏛️"),
        "watch": [
            ("Confidentiality hides secret messages from unauthorized eyes.", "Confidentiality flag activated.", "Encryption algorithms (AES, RSA) guarantee C."),
            ("Integrity ensures no attacker modifies the data in transit.", "Integrity hash seal applied.", "Cryptographic digests (SHA-256) verify I."),
            ("Availability guarantees authorized users can access resources anytime.", "Uptime indicator switched to 99.99%.", "DDoS mitigation & redundancy guarantee A.")
        ],
        "check": ("If an attacker secretly modifies the balance in your bank account, which pillar is broken?", ["Confidentiality", "Integrity", "Availability", "Authentication"], 1, "Integrity ensures data is accurate and untampered!"),
        "lost": ("Think of a sealed package", "Confidentiality is an opaque envelope. Integrity is a wax seal that breaks if opened. Availability is the mailman delivering on time.", "All three work together to keep data safe.")
    },
    {
        "id": "attack_defense_sorter", "title": "Attack or Defense? Sorter", "realm": 1, "realmTitle": "Secrets 101",
        "promise": "Distinguish active intrusions from silent surveillance.",
        "story": ("Threat Spectrum", "Attacks either listen silently or actively modify traffic.", "A wiretap listens without making noise; an imposter actively steals your identity.", "Active vs Passive Attacks", "⚔️"),
        "watch": [
            ("Passive attacks eavesdrop without altering message bits.", "Packet sniffer highlighted.", "Traffic analysis and wiretapping."),
            ("Active attacks inject fake packets or alter existing data.", "Payload modification pulse.", "Replay, masquerade, and message alteration.")
        ],
        "check": ("Which of these is a passive attack?", ["Eavesdropping on Wi-Fi", "Modifying bank packets", "Sending a SYN flood", "Impersonating a server"], 0, "Eavesdropping listens silently without altering network packets!"),
        "lost": ("Think of spies vs burglars", "A spy sits on a bench taking photos (passive). A burglar kicks down the door to steal (active).", "Passive attacks breach privacy; active attacks breach integrity.")
    },
    {
        "id": "substitution_lab", "title": "Caesar Wheel & Playfair Grid", "realm": 1, "realmTitle": "Secrets 101",
        "promise": "Turn a wheel to shift letters and see your secret emerge.",
        "story": ("Letter Swapping", "Shift every letter along the alphabet by a secret dial number.", "A rotating combination lock that advances every letter forward by three steps.", "Modular Shift Cipher", "🎡"),
        "watch": [
            ("Plaintext letters align with the standard A-Z alphabet.", "Plaintext wheel set at index 0.", "Alphabet mapped to indices 0..25."),
            ("Turn the dial by key K = 3 to shift every letter.", "Shift dial turns +3 positions.", "A becomes D, B becomes E."),
            ("Read the inner ring to reveal the encrypted ciphertext.", "Cipher text letters illuminate in accent.", "Formula: C = (P + 3) mod 26.")
        ],
        "check": ("Using a Caesar shift of 3, what does 'CAT' encrypt to?", ["FDW", "ECV", "DBU", "EAW"], 0, "C+3=F, A+3=D, T+3=W -> FDW!"),
        "lost": ("Think of a 26-hour clock", "If it's 1 o'clock and you add 3 hours, the hand points to 4. Letters wrap around the same way.", "Shifting letters preserves frequency patterns.")
    },
    {
        "id": "transposition_stego", "title": "Rail Fence & Hide-in-Image", "realm": 1, "realmTitle": "Secrets 101",
        "promise": "Shuffle letters into zig-zags and hide secret bits inside pixels.",
        "story": ("Shuffling & Hiding", "Transposition jumbles positions; steganography hides data inside pixels.", "Writing a secret message across zigzag fence slats, then taking the rails apart.", "Transposition and Steganography", "🧩"),
        "watch": [
            ("Write plaintext in a zigzag pattern across multiple rails.", "Letters zigzag down and up.", "Rail 1 gets indices 0, 4, 8..."),
            ("Read along horizontal rails to form the shuffled ciphertext.", "Letters reordered row-by-row.", "Letter frequencies remain identical."),
            ("Least Significant Bit (LSB) steganography swaps tiny pixel bits invisibly.", "Pixel blue component adjusted by 1 bit.", "Human eyes cannot detect 1/256th color change.")
        ],
        "check": ("How does transposition differ from substitution?", ["It replaces letters with symbols", "It rearranges letters without changing them", "It requires two keys", "It compresses text"], 1, "Transposition only changes positions; the original letters stay unchanged!"),
        "lost": ("Think of anagrams and secret compartments", "Transposition is scrambling letters like an anagram. Steganography is hiding a letter in a hollow book heel.", "Transposition shuffles; steganography conceals existence.")
    },
    {
        "id": "cryptanalysis_lab", "title": "Frequency Lab & OTP Simulator", "realm": 1, "realmTitle": "Secrets 101",
        "promise": "Crack ciphers with letter tallies or use One-Time Pads for perfect secrecy.",
        "story": ("Breaking vs Perfect Secrecy", "Frequency tallies break ciphers, but one-time pads are mathematically unbreakable.", "Looking for the tallest tree in a forest: English letter 'E' always towers above the rest.", "Frequency Analysis & Shannon Entropy", "📊"),
        "watch": [
            ("Count how often each letter appears in the ciphertext.", "Letter count bar chart animates.", "English letter E appears 12.7% of the time."),
            ("Map the tallest bar to 'E' to break simple substitutions.", "Top bar mapped to E.", "Frequency analysis exposes fixed shifts."),
            ("One-Time Pad uses truly random keys matching message length.", "Random key XORed with plaintext.", "Every possible message is equally likely.")
        ],
        "check": ("Why is the One-Time Pad theoretically unbreakable?", ["The key is 4096 bits long", "Every plaintext is equally probable given ciphertext", "It uses prime numbers", "It uses SHA-256"], 1, "Shannon proved OTP provides perfect secrecy because ciphertext gives zero information about plaintext!"),
        "lost": ("Think of rolling a die for every letter", "If every letter gets a random shift that is never reused, all output messages are equally likely.", "Never reuse an OTP key, or frequency patterns leak.")
    },
    {
        "id": "modular_clock", "title": "Clock Arithmetic & Euclid Steps", "realm": 2, "realmTitle": "Symmetric Forge",
        "promise": "See how numbers wrap on a clock and find greatest common divisors.",
        "story": ("Clock Math", "Numbers wrap around a circle, just like clock hours.", "If a clock shows 9 o'clock and you wait 5 hours, it shows 2 o'clock (14 mod 12 = 2).", "Modular Arithmetic & Euclidean GCD", "⏰"),
        "watch": [
            ("Numbers increment around a dial of modulus N.", "Clock hand sweeps forward.", "Modulo wraps numbers back to zero."),
            ("Euclid's algorithm finds GCD by repeatedly taking remainders.", "Remainder step highlights.", "gcd(a, b) = gcd(b, a mod b)."),
            ("Extended Euclid finds the modular multiplicative inverse.", "Inverse step calculates d.", "Used for RSA private keys.")
        ],
        "check": ("What is 17 mod 5?", ["2", "3", "4", "1"], 0, "17 divided by 5 is 3 with a remainder of 2!"),
        "lost": ("Think of days of the week", "If today is Tuesday (day 2), in 7 days it is Tuesday again because 9 mod 7 = 2.", "Modulo is simply the remainder after division.")
    },
    {
        "id": "feistel_builder", "title": "Feistel Builder & S-DES", "realm": 2, "realmTitle": "Symmetric Forge",
        "promise": "Split data in half, scramble one side, and swap them.",
        "story": ("The Feistel Machine", "Split input in two, scramble the right side, XOR the left, then swap.", "A revolving door where you swap left and right hands while changing coats.", "Feistel Network Architecture", "⚙️"),
        "watch": [
            ("Split 64-bit block into Left (L) and Right (R) halves.", "Block splits down the middle.", "L0 and R0 created."),
            ("Pass R through round function F with subkey K1.", "Right half scrambles through F.", "Substitution and permutation applied."),
            ("XOR output with L, then swap L and R.", "Left half updates and halves cross over.", "L1 = R0, R1 = L0 XOR F(R0, K1).")
        ],
        "check": ("Why is Feistel architecture popular in block ciphers?", ["It uses prime numbers", "Encryption and decryption use the identical circuit", "It prevents all attacks", "It compresses files"], 1, "Feistel decryption uses the exact same circuit; you just reverse the subkeys!"),
        "lost": ("Think of folding dough", "You fold left over right, knead spices in, and fold again. Doing it in reverse unfolds it.", "Feistel transforms any round function into an invertible cipher.")
    },
    {
        "id": "des_explorer", "title": "DES 16-Round Explorer", "realm": 2, "realmTitle": "Symmetric Forge",
        "promise": "Tour the historic cipher that protected global banking for 25 years.",
        "story": ("DES Anatomy", "Sixteen rounds of S-box substitution and bit permutation.", "Passing secret documents through 16 consecutive stamp-and-shuffle stations.", "Data Encryption Standard (DES)", "🏛️"),
        "watch": [
            ("Initial permutation shuffles input bits.", "64 bits rearranged by IP table.", "Bit ordering changes."),
            ("Eight S-boxes substitute 6 bits down to 4 non-linear bits.", "6-bit input maps to 4-bit output.", "S-boxes provide confusion."),
            ("Repeat for 16 rounds with distinct 48-bit subkeys.", "Rounds 1 through 16 complete.", "56-bit effective key length.")
        ],
        "check": ("Why is standard DES obsolete today?", ["Its 56-bit key can be brute-forced in hours", "S-boxes are linear", "It cannot encrypt text", "It was never standardized"], 0, "Modern computers can test all 2^56 keys in minutes!"),
        "lost": ("Think of a 56-letter combination lock", "It was unbreakable in 1977, but supercomputers today try every combination in seconds.", "Triple-DES extended it; AES replaced it.")
    },
    {
        "id": "aes_state_matrix", "title": "AES State Matrix & GF(2⁸)", "realm": 2, "realmTitle": "Symmetric Forge",
        "promise": "Watch a 4x4 grid of bytes substitute, shift, mix, and add keys.",
        "story": ("The Gold Standard", "A 4x4 matrix of bytes undergoes four transformations each round.", "A Rubik's cube where every face swaps stickers, slides rows, mixes columns, and locks.", "Advanced Encryption Standard (AES)", "🔲"),
        "watch": [
            ("SubBytes swaps each byte using an 8-bit S-Box.", "Matrix cells swap values.", "Non-linear confusion applied."),
            ("ShiftRows cyclically shifts rows 1, 2, and 3 left.", "Rows slide left by 1, 2, and 3 steps.", "Diffusion across rows."),
            ("MixColumns multiplies columns in Galois Field GF(2^8).", "Columns recalculate matrix values.", "Diffusion down columns."),
            ("AddRoundKey XORs the round subkey into the state.", "State XORs with 128-bit key.", "Key whitening complete.")
        ],
        "check": ("Which AES step is responsible for non-linear byte confusion?", ["ShiftRows", "SubBytes", "MixColumns", "AddRoundKey"], 1, "SubBytes uses an inversion in GF(2^8) followed by affine transform for non-linear confusion!"),
        "lost": ("Think of shuffling cards in two directions", "SubBytes changes the card faces; ShiftRows and MixColumns shuffle horizontally and vertically.", "After 10 rounds, one tiny bit flip changes half the ciphertext.")
    },
    {
        "id": "block_modes_rc4", "title": "Block Modes & RC4 Keystream", "realm": 2, "realmTitle": "Symmetric Forge",
        "promise": "See why ECB leaks penguin outlines and how CBC chains blocks.",
        "story": ("Chaining Blocks", "Encrypting blocks independently leaks patterns; chaining hides everything.", "Stamping cookies with the same mold makes identical shapes; mixing each with the last hides the pattern.", "ECB, CBC, and Stream Ciphers", "🐧"),
        "watch": [
            ("ECB mode encrypts identical plaintext blocks into identical cipher blocks.", "Identical blocks light up with same color.", "The infamous Tux penguin outline remains visible."),
            ("CBC mode XORs each plaintext block with the previous ciphertext.", "Chain lines connect blocks.", "Requires an Initialization Vector (IV)."),
            ("RC4 generates a continuous stream of pseudo-random bytes.", "Byte stream flows into XOR gate.", "Fast stream encryption.")
        ],
        "check": ("Why should ECB mode NEVER be used for images or structured data?", ["It is too slow", "Identical plaintext blocks produce identical ciphertext blocks", "It requires 3 keys", "It cannot decrypt"], 1, "ECB leaks structural patterns because identical plain blocks encrypt to identical cipher blocks!"),
        "lost": ("Think of wallpaper vs paint", "ECB is like wallpaper where the repeat pattern is obvious. CBC blends each roll with the last so no pattern repeats.", "Always use CBC or GCM with a unique IV.")
    },
    {
        "id": "prime_sieve", "title": "Prime Sieve & Miller-Rabin", "realm": 3, "realmTitle": "Public-Key Citadel",
        "promise": "Filter out composites and test massive numbers for primality.",
        "story": ("The Atoms of Numbers", "Primes divide only by 1 and themselves; multiplying is easy, factoring is hard.", "Dropping sand through smaller sieves until only pure golden nuggets remain.", "Prime Sieves & Probabilistic Primality", "✨"),
        "watch": [
            ("Eratosthenes sieve strikes out multiples of 2, 3, 5...", "Non-primes cross off the grid.", "Only primes remain."),
            ("Multiplying two large primes p and q is instantaneous.", "Multiplication arrow shoots to N = p * q.", "Easy direction."),
            ("Finding p and q given only N takes billions of years.", "Clock spins with locked padlock.", "Trapdoor one-way function.")
        ],
        "check": ("What makes RSA public-key encryption secure?", ["Adding large numbers is impossible", "Multiplying primes is fast, but factoring their product is hard", "Computers cannot store primes", "Primes cannot be divided"], 1, "Integer factorization is a classic one-way trapdoor function!"),
        "lost": ("Think of mixing yellow and blue paint", "It takes one second to mix green paint, but picking apart the molecules back into yellow and blue is practically impossible.", "Primes are the basic paint colors of mathematics.")
    },
    {
        "id": "crt_discrete_log", "title": "CRT Clock & Discrete Log", "realm": 3, "realmTitle": "Public-Key Citadel",
        "promise": "Solve modular puzzle clocks and understand discrete logarithms.",
        "story": ("Modular Riddles", "Chinese Remainder Theorem reconstructs numbers from small remainders.", "Two gears of sizes 3 and 5: knowing both tooth alignments tells you the exact gear turn.", "CRT & Discrete Logarithms", "⚙️"),
        "watch": [
            ("Align remainders mod 3, mod 5, and mod 7.", "Clocks synchronize hands.", "Unique solution mod (3*5*7 = 105)."),
            ("Modular exponentiation calculates g^x mod p efficiently.", "Square-and-multiply runs fast.", "Fast forward operation."),
            ("Given g and g^x mod p, finding x is the discrete log problem.", "Question mark hovers over exponent x.", "Diffie-Hellman hardness assumption.")
        ],
        "check": ("In the Diffie-Hellman protocol, what hard math problem protects the shared secret?", ["Factoring primes", "The Discrete Logarithm Problem", "Solving linear equations", "Computing matrix inverses"], 1, "Finding the secret exponent x from g^x mod p is computationally intractable for large primes!"),
        "lost": ("Think of calculating steps forward vs retracing footsteps", "It's easy to take 100 steps forward, but looking at the landing spot in a sandstorm and knowing the step count is hard.", "Discrete log is the trapdoor behind Diffie-Hellman.")
    },
    {
        "id": "rsa_stepper", "title": "RSA Step-by-Step with Tiny Primes", "realm": 3, "realmTitle": "Public-Key Citadel",
        "promise": "Generate public and private keys using tiny primes p=3 and q=11.",
        "story": ("Two Secret Keys", "One public lock that anyone can snap shut; one private key to open it.", "A mailbox where anyone drops letters through the slot, but only the owner has the door key.", "RSA Public-Key Cryptosystem", "🔑"),
        "watch": [
            ("Pick tiny primes p = 3 and q = 11. Compute n = 33.", "Primes multiply into modulus n = 33.", "Modulus shared publicly."),
            ("Calculate totient phi(n) = (3-1)*(11-1) = 20.", "Totient dial counts coprimes.", "Euler totient formula."),
            ("Choose public exponent e = 3. Find private key d = 7.", "Keys (3, 33) and (7, 33) generate.", "(3 * 7) mod 20 = 1."),
            ("Encrypt: C = M^3 mod 33. Decrypt: M = C^7 mod 33.", "Message transforms and decrypts.", "Math locks and unlocks perfectly.")
        ],
        "check": ("If public key is (e=3, n=33) and message M = 2, what is ciphertext C = M^e mod n?", ["6", "8", "1", "16"], 1, "2^3 = 8, and 8 mod 33 = 8!"),
        "lost": ("Think of a padlock and key", "Anyone can click a padlock shut (public key). Only the person with the physical key (private key) can open it.", "Never share your private key d.")
    },
    {
        "id": "dh_elgamal_lab", "title": "Diffie-Hellman Paint & ElGamal", "realm": 3, "realmTitle": "Public-Key Citadel",
        "promise": "Mix colors in public to agree on a secret shared key without eavesdroppers knowing.",
        "story": ("Secret Key in Plain Sight", "Alice and Bob mix secret colors with a public color to create a shared secret.", "Mixing yellow with your secret color, exchanging mixtures, and adding your secret again: both get the same brown!", "Diffie-Hellman Key Exchange", "🎨"),
        "watch": [
            ("Alice and Bob agree on public paint (Yellow).", "Yellow pot shows between Alice and Bob.", "Public generator and modulus."),
            ("Alice adds secret Red (gets Orange); Bob adds secret Blue (gets Green).", "Alice holds Orange, Bob holds Green.", "Private exponentiation."),
            ("They swap mixtures over the untrusted wire.", "Cans cross paths in mid-air.", "Eavesdropper Mallory only sees Orange and Green."),
            ("Each adds their own secret color: both arrive at the exact same Brown!", "Both paint pots turn identical Rich Brown.", "Shared secret established!")
        ],
        "check": ("Can an eavesdropper looking at the exchanged mixtures find the shared secret?", ["Yes, by mixing them together", "No, separating the original secret colors is mathematically intractable", "Yes, using brute force in 1 second", "Only if Bob reveals his name"], 1, "Separating mixed paint colors represents the discrete logarithm problem!"),
        "lost": ("Think of a locked briefcase with two padlocks", "Alice puts her lock on, Bob puts his lock on, Alice removes hers, Bob removes his.", "Diffie-Hellman lets two strangers agree on a secret over an open radio.")
    },
    {
        "id": "ecc_point_addition", "title": "Elliptic Curve Point Addition", "realm": 3, "realmTitle": "Public-Key Citadel",
        "promise": "Bounce points across an algebraic curve to achieve 3072-bit RSA security with 256 bits.",
        "story": ("Geometry of Secrets", "Draw a line through two points on a curve, find where it hits, and reflect across the axis.", "A frictionless billiard ball bouncing off curve bumpers in a predictable mathematical pattern.", "Elliptic Curve Cryptography (ECC)", "📈"),
        "watch": [
            ("Curve equation: y² = x³ + ax + b over a finite field.", "Symmetric curve draws smoothly.", "Symmetric about the x-axis."),
            ("Point addition: draw line through P and Q to hit -R, reflect to R.", "Tangent line draws and reflects.", "P + Q = R."),
            ("Point doubling: draw tangent at P to find 2P.", "Tangent bounces to 2P.", "Repeated doubling computes k * P fast.")
        ],
        "check": ("Why is a 256-bit ECC key considered as secure as a 3072-bit RSA key?", ["ECC keys are compressed", "Discrete log on elliptic curves has no sub-exponential attack algorithm", "RSA was designed poorly", "ECC was created by NASA"], 1, "The elliptic curve discrete log problem is vastly harder than prime factoring per bit!"),
        "lost": ("Think of billiard trick shots", "You bounce the ball 100 times. Anyone seeing the final ball position cannot guess how many bounces occurred.", "Smaller keys mean faster mobile performance.")
    },
    {
        "id": "sha256_avalanche", "title": "SHA-256 Digest & Avalanche Meter", "realm": 4, "realmTitle": "Trust & Proof",
        "promise": "Flip a single character and watch half the 256-bit hash explode.",
        "story": ("Digital Fingerprint", "Every piece of data gets a unique, fixed-size fingerprint.", "A blender that turns apples into fruit paste: identical inputs give identical paste, but you can't un-blend paste.", "Cryptographic Hash Functions", "🌪️"),
        "watch": [
            ("Hash of 'hello' generates 64 hexadecimal characters.", "Hash digest displays.", "Always 256 bits long."),
            ("Change 'hello' to 'Hello' (one bit flip).", "First letter capitalizes.", "Input difference: 1 bit."),
            ("Avalanche effect flips ~50% of all output bits completely.", "Avalanche meter swings to 51%.", "Complete output unpredictability.")
        ],
        "check": ("What is the primary property of the cryptographic Avalanche Effect?", ["Output size grows with input", "Flipping one input bit changes roughly half the output bits", "Hashes can be reversed", "Hashes compress files to 0 bytes"], 1, "A good hash ensures tiny input tweaks completely randomize the output digest!"),
        "lost": ("Think of a row of dominos with fireworks", "Knocking over one tiny domino triggers fireworks that randomize the entire room.", "You can never guess the input from the hash.")
    },
    {
        "id": "hmac_lab", "title": "HMAC Pad Builder & Collisions", "realm": 4, "realmTitle": "Trust & Proof",
        "promise": "Mix a secret key with inner and outer pads to prevent length-extension attacks.",
        "story": ("Sealed Authenticity", "A hash alone proves integrity; a hash with a secret key proves who sent it.", "Wax-stamping a letter with your personal signet ring so the recipient knows you wrote it.", "Hash-based Message Authentication Codes", "🛡️"),
        "watch": [
            ("Plain hash(K || M) is vulnerable to length-extension attacks.", "Vulnerability banner displays.", "Attackers can append extra data."),
            ("HMAC uses inner pad (ipad) and outer pad (opad).", "Two padding boxes surround key.", "K XOR ipad and K XOR opad."),
            ("Result: Hash(K_opad || Hash(K_ipad || M)).", "Nested hash completes.", "Mathematically immune to extension.")
        ],
        "check": ("What does an HMAC provide that a standard SHA-256 hash does NOT?", ["Data compression", "Authentication that the sender holds the secret key", "Faster encryption", "Reversibility"], 1, "HMAC verifies both data integrity and message authenticity using a shared secret key!"),
        "lost": ("Think of a signed wax seal", "Anyone can photocopy a document (hash), but only someone with the actual metal stamp (key) can press the wax seal.", "HMAC combines a hash with a secret key.")
    },
    {
        "id": "sign_verify_lab", "title": "Sign & Verify Interactive", "realm": 4, "realmTitle": "Trust & Proof",
        "promise": "Hash a contract, encrypt with your private key, and let the world verify with your public key.",
        "story": ("Digital Pen & Paper", "Sign with your private key; anyone in the world verifies with your public key.", "An autograph stamped in glowing ink that everyone can inspect under a magnifying glass, but only you can sign.", "Digital Signatures & Non-Repudiation", "✍️"),
        "watch": [
            ("Alice hashes her document to a 256-bit digest.", "Document condenses into hash.", "Hashing ensures fixed size."),
            ("Alice encrypts the digest using her private key to produce the signature.", "Signature attaches to document.", "Signature = Digest^d mod n."),
            ("Bob decrypts the signature using Alice's public key.", "Signature decrypts to digest.", "Digest = Signature^e mod n."),
            ("Bob compares both digests: match means genuine and untampered!", "Green checkmark illuminates.", "Integrity and non-repudiation verified.")
        ],
        "check": ("Which key is used to CREATE a digital signature?", ["The receiver's public key", "The sender's private key", "The shared symmetric key", "The CA root key"], 1, "Only the sender possesses their private key, proving authenticity!"),
        "lost": ("Think of a notary's stamp", "Only the notary has the stamp (private key). Everyone knows what the stamp looks like (public key) to verify it.", "Signing proves identity and prevents denying the message.")
    },
    {
        "id": "auth_challenge", "title": "Salted Hash & Challenge-Response", "realm": 4, "realmTitle": "Trust & Proof",
        "promise": "Add salt to stop rainbow tables and prove knowledge without sending passwords.",
        "story": ("Never Send Passwords", "Prove you know the secret without ever sending the secret over the wire.", "A guard asks for today's riddle answer: you solve a math problem based on it without saying the secret word.", "Authentication & Salted Hashes", "🛂"),
        "watch": [
            ("Storing plain hashes allows rainbow table lookup attacks.", "Pre-computed table matches hash.", "Vulnerable to lookup."),
            ("Add a random 128-bit salt to every password before hashing.", "Salt prepends to password.", "Defeats rainbow tables completely."),
            ("Challenge-Response: server sends nonce, client signs it.", "Nonce travels to client.", "Password never crosses the network.")
        ],
        "check": ("Why do we add a unique salt to passwords before hashing them?", ["To make hashes shorter", "To ensure identical passwords generate completely different hashes", "To encrypt the database", "To bypass login"], 1, "Salts ensure two users with the same password have distinct hashes, neutralizing rainbow tables!"),
        "lost": ("Think of adding unique spice to identical soups", "Even if two cooks use the same water and vegetable, unique spice makes each soup taste different.", "Never store plain passwords.")
    },
    {
        "id": "kerberos_pki", "title": "Kerberos Ticket Flow & X.509", "realm": 4, "realmTitle": "Trust & Proof",
        "promise": "Get a ticket from the ticket booth to ride any service in the amusement park.",
        "story": ("Tickets & Passports", "Log in once, receive a Ticket Granting Ticket, and present it to services.", "Buying a wristband at the carnival entrance so you don't pay money at every ride.", "Kerberos and Public Key Infrastructure", "🎫"),
        "watch": [
            ("Client authenticates with Authentication Server (AS).", "AS issues TGT.", "Encrypted with user's password hash."),
            ("Client presents TGT to Ticket Granting Server (TGS).", "TGS verifies TGT.", "Issues service ticket for file server."),
            ("Client presents service ticket to target server.", "Target server validates ticket.", "Access granted without password re-entry."),
            ("X.509 certificates bind a public key to an identity via Certificate Authority.", "CA signs certificate.", "Chain of trust established.")
        ],
        "check": ("What is the primary benefit of Kerberos single sign-on tickets?", ["Users never need passwords", "Passwords are only sent once during initial authentication", "Tickets last forever", "It replaces firewalls"], 1, "Kerberos passes encrypted short-lived tickets instead of transmitting passwords across the network!"),
        "lost": ("Think of a movie ticket", "You show ID at the box office once to buy a ticket. The usher at screen 4 just tears your ticket without asking for your ID again.", "Tickets expire automatically for safety.")
    },
    {
        "id": "ipsec_pgp", "title": "PGP Ring & IPsec Tunnel", "realm": 5, "realmTitle": "Real-World Defense",
        "promise": "Build an encrypted highway between networks and sign emails with PGP.",
        "story": ("Secure Highways", "Wrap entire IP packets inside encrypted envelopes for secure branch-to-branch tunnels.", "An armored truck driving through a public highway: no one outside can see what's inside.", "IPsec Tunneling and PGP Email", "🛣️"),
        "watch": [
            ("Transport mode encrypts only payload; headers remain visible.", "Payload turns dark.", "Useful for host-to-host."),
            ("Tunnel mode encrypts the entire original IP packet and adds a new header.", "Entire packet wraps in new IP shell.", "Gateway-to-gateway VPN."),
            ("PGP signs email and encrypts symmetric session keys with RSA.", "Email seals with key ring.", "Web of trust authentication.")
        ],
        "check": ("In IPsec Tunnel mode, what is encrypted?", ["Only the TCP payload", "The entire original IP packet including original headers", "Only the Wi-Fi password", "Nothing"], 1, "Tunnel mode encapsulates the complete original IP packet inside a brand-new IP header!"),
        "lost": ("Think of putting an envelope inside a bigger envelope", "Transport mode is blacking out the letter text. Tunnel mode is putting the whole stamped envelope inside an armored pouch.", "Tunnel mode connects remote branch offices securely.")
    },
    {
        "id": "tls_wifi_handshake", "title": "TLS 1.3 & Wi-Fi 4-Way Handshake", "realm": 5, "realmTitle": "Real-World Defense",
        "promise": "Establish a secure web session in a single round-trip.",
        "story": ("The Internet's Lock", "Client and server say hello, negotiate parameters, and start encrypting in 1 round trip.", "Two diplomats introducing themselves with official badges, exchanging secret ciphers, and closing the door.", "Transport Layer Security (TLS 1.3)", "🤝"),
        "watch": [
            ("ClientHello sends supported ciphers and key share.", "Client message flies to server.", "1-RTT handshake begins."),
            ("ServerHello responds with chosen cipher and server key share.", "Server message returns.", "Both compute session key via ECDHE."),
            ("EncryptedExtensions and Certificate follow immediately.", "Certificate chain validates.", "Server authenticated."),
            ("Application data flows under symmetric AES-GCM.", "Lock icon turns green.", "Full forward secrecy guaranteed.")
        ],
        "check": ("Why is TLS 1.3 faster and safer than TLS 1.2?", ["It removed weak ciphers and completes handshake in 1 round-trip (1-RTT)", "It disables encryption", "It doesn't use certificates", "It uses plain HTTP"], 0, "TLS 1.3 trimmed legacy ciphers and merged key exchange into the very first round-trip!"),
        "lost": ("Think of a telephone handshake", "Hello, here's my language and half our code; server replies with its half. Now both speak encrypted.", "Look for the padlock in your browser address bar.")
    },
    {
        "id": "firewall_builder", "title": "Firewall Rule Builder & IDS", "realm": 5, "realmTitle": "Real-World Defense",
        "promise": "Build packet filter rules to block malicious ports and pass legitimate traffic.",
        "story": ("The Castle Gate", "Inspect every packet's source, destination, and port before granting entry.", "A security guard checking passports and luggage tags at the border checkpoint.", "Firewalls, DMZ, and Intrusion Detection", "🧱"),
        "watch": [
            ("Packet arrives: Source IP, Destination IP, Protocol, Port.", "Packet header expands.", "Header fields inspected."),
            ("Rule matches: DENY TCP port 23 (Telnet).", "Red barrier drops.", "Insecure protocol blocked."),
            ("Rule matches: ALLOW TCP port 443 (HTTPS) established.", "Green gate opens.", "Legitimate web traffic passes."),
            ("Stateful inspection tracks TCP connection handshake states.", "State table updates SYN/ACK.", "Blocks unsolicited incoming packets.")
        ],
        "check": ("Which firewall rule should ALWAYS be placed at the very end of an enterprise ruleset?", ["ALLOW ALL", "DEFAULT DENY (DROP ALL)", "ALLOW PORT 80", "LOG OFF"], 1, "A default deny policy ensures anything not explicitly allowed is automatically rejected!"),
        "lost": ("Think of a bouncer with a guest list", "If your name is on the list, you enter. If your name isn't on the list, the default rule is you stay outside.", "Explicit allow, default deny.")
    },
    {
        "id": "sqli_dos_sandbox", "title": "SQLi Sandbox & DoS Flood Meter", "realm": 5, "realmTitle": "Real-World Defense",
        "promise": "Type `' OR '1'='1` to break a mock login and see how parameterized queries fix it.",
        "story": ("Input Tricks", "Unsanitized user inputs fool SQL databases into treating data as executable commands.", "Asking someone for their name and saying 'Robert'); DROP TABLE Students;--' so they execute the instruction.", "Web Application Injection & DDoS", "💉"),
        "watch": [
            ("Vulnerable query concatenates input: SELECT * FROM Users WHERE user = 'admin'.", "SQL string stitches together.", "Data mixes with code."),
            ("Attacker types ' OR '1'='1 --.", "Query evaluates to TRUE.", "Login bypassed without password!"),
            ("Defense: Parameterized queries treat input purely as literal data.", "Placeholders (?) bind parameters.", "Payload treated as raw string, attack fails.")
        ],
        "check": ("What is the primary defense against SQL Injection?", ["Using strong passwords", "Parameterized queries (Prepared Statements)", "Hiding the database name", "Disabling JavaScript"], 1, "Prepared statements keep user data completely separate from executable SQL instructions!"),
        "lost": ("Think of a fill-in-the-blank form", "If you write 'I am the King' in the name blank, you are still just a name on the form, not actual royalty.", "Never concatenate strings into SQL queries.")
    },
    {
        "id": "zkp_merkle_pqc", "title": "ZKP Cave & Merkle Tree Explorer", "realm": 5, "realmTitle": "Real-World Defense",
        "promise": "Prove you know the secret word in the cave without saying it out loud.",
        "story": ("The Frontier of Proof", "Zero-knowledge proofs convince someone you know a secret without revealing the secret.", "Proving you have the key to a circular cave door by entering one side and walking out the other.", "Zero-Knowledge & Post-Quantum", "🌌"),
        "watch": [
            ("Peggy enters circular cave from path A or B.", "Peggy walks into cave branch.", "Victor waits outside."),
            ("Victor calls out: 'Come out through path A!'.", "Victor makes random challenge.", "Victor cannot see inside."),
            ("Peggy uses secret key to open door and exits via path A.", "Peggy unlocks door and steps out.", "Probability of guessing: 50%."),
            ("Repeat 30 times: probability of cheating drops to 1 in 1,000,000,000!", "Success meter fills to 100%.", "Proof achieved with ZERO secret leaked.")
        ],
        "check": ("What does a Zero-Knowledge Proof achieve?", ["It proves a statement is true without revealing any secret information", "It cracks passwords with quantum computers", "It replaces all encryption", "It deletes blockchain blocks"], 0, "ZKP convinces a verifier with mathematical certainty while keeping the underlying secret 100% confidential!"),
        "lost": ("Think of color-blind test cards", "You hold two balls behind your back and swap them (or don't). Your friend guesses every time, proving they see the colors without telling you which is which.", "Proof without exposure.")
    }
]

print(f"Total visualizer specs: {len(specs)}")

# Format into Kotlin file CnsVisualizerRegistry.kt
kotlin_code = '''package com.simats.codeingo.ui.visualizer.scaffold

/**
 * 🎨 CnsVisualizerRegistry — Central Catalog of all 25 Visualizers
 * Matching Anna University CS8792 Units I-V & Stallings CNS 8th Edition.
 */
object CnsVisualizerRegistry {

    val allSpecs: List<VisualizerSpec> = listOf(
'''

for s in specs:
    kotlin_code += f'''        VisualizerSpec(
            id = "{s['id']}",
            title = "{s['title']}",
            realmNumber = {s['realm']},
            realmTitle = "{s['realmTitle']}",
            oneSentencePromise = "{s['promise']}",
            story = VisualizerStory(
                title = "{s['story'][0]}",
                oneSentenceHook = "{s['story'][1]}",
                realLifeAnalogy = "{s['story'][2]}",
                conceptName = "{s['story'][3]}",
                iconEmoji = "{s['story'][4]}"
            ),
            watchSteps = listOf(
'''
    for idx, w in enumerate(s['watch']):
        kotlin_code += f'''                WatchStep(
                    stepIndex = {idx},
                    narration = "{w[0]}",
                    whatChanged = "{w[1]}",
                    detailNotes = "{w[2]}"
                ),
'''
    kotlin_code += f'''            ),
            checkQuestion = CheckQuestion(
                prompt = "{s['check'][0]}",
                options = listOf({", ".join(f'"{opt}"' for opt in s['check'][1])}),
                correctIndex = {s['check'][2]},
                explanation = "{s['check'][3]}"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "{s['lost'][0]}",
                simpleAnalogy = "{s['lost'][1]}",
                takeawayBullet = "{s['lost'][2]}"
            )
        ),
'''

kotlin_code += '''    )

    private val specsMap: Map<String, VisualizerSpec> = allSpecs.associateBy { it.id }

    fun getSpecById(id: String): VisualizerSpec? = specsMap[id]

    fun getSpecsForRealm(realmNumber: Int): List<VisualizerSpec> = allSpecs.filter { it.realmNumber == realmNumber }

    fun getSpecForChapter(chapterNumber: Int): VisualizerSpec? {
        val index = chapterNumber - 1
        return allSpecs.getOrNull(index)
    }
}
'''

with open('app/src/main/java/com/simats/codeingo/ui/visualizer/scaffold/CnsVisualizerRegistry.kt', 'w', encoding='utf-8') as f:
    f.write(kotlin_code)

print("Wrote CnsVisualizerRegistry.kt successfully!")
