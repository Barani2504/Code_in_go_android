package com.simats.codeingo.ui.visualizer.scaffold

/**
 * 🎨 CnsVisualizerRegistry — Central Catalog of all 25 Visualizers
 * Matching Anna University CS8792 Units I-V & Stallings CNS 8th Edition.
 */
object CnsVisualizerRegistry {

    val allSpecs: List<VisualizerSpec> = listOf(
        VisualizerSpec(
            id = "cia_triad",
            title = "CIA Triad Tap-Game",
            realmNumber = 1,
            realmTitle = "Secrets 101",
            oneSentencePromise = "Learn the three pillars of security through interactive scenarios.",
            story = VisualizerStory(
                title = "CIA Triad",
                oneSentenceHook = "Security guards three things: secrets, truth, and access.",
                realLifeAnalogy = "A bank vault hides gold, stamps bars to detect counterfeits, and opens during business hours.",
                conceptName = "Confidentiality, Integrity, Availability",
                iconEmoji = "🏛️"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Confidentiality hides secret messages from unauthorized eyes.",
                    whatChanged = "Confidentiality flag activated.",
                    detailNotes = "Encryption algorithms (AES, RSA) guarantee C."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Integrity ensures no attacker modifies the data in transit.",
                    whatChanged = "Integrity hash seal applied.",
                    detailNotes = "Cryptographic digests (SHA-256) verify I."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Availability guarantees authorized users can access resources anytime.",
                    whatChanged = "Uptime indicator switched to 99.99%.",
                    detailNotes = "DDoS mitigation & redundancy guarantee A."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "If an attacker secretly modifies the balance in your bank account, which pillar is broken?",
                options = listOf("Confidentiality", "Integrity", "Availability", "Authentication"),
                correctIndex = 1,
                explanation = "Integrity ensures data is accurate and untampered!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a sealed package",
                simpleAnalogy = "Confidentiality is an opaque envelope. Integrity is a wax seal that breaks if opened. Availability is the mailman delivering on time.",
                takeawayBullet = "All three work together to keep data safe."
            )
        ),
        VisualizerSpec(
            id = "attack_defense_sorter",
            title = "Attack or Defense? Sorter",
            realmNumber = 1,
            realmTitle = "Secrets 101",
            oneSentencePromise = "Distinguish active intrusions from silent surveillance.",
            story = VisualizerStory(
                title = "Threat Spectrum",
                oneSentenceHook = "Attacks either listen silently or actively modify traffic.",
                realLifeAnalogy = "A wiretap listens without making noise; an imposter actively steals your identity.",
                conceptName = "Active vs Passive Attacks",
                iconEmoji = "⚔️"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Passive attacks eavesdrop without altering message bits.",
                    whatChanged = "Packet sniffer highlighted.",
                    detailNotes = "Traffic analysis and wiretapping."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Active attacks inject fake packets or alter existing data.",
                    whatChanged = "Payload modification pulse.",
                    detailNotes = "Replay, masquerade, and message alteration."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Which of these is a passive attack?",
                options = listOf("Eavesdropping on Wi-Fi", "Modifying bank packets", "Sending a SYN flood", "Impersonating a server"),
                correctIndex = 0,
                explanation = "Eavesdropping listens silently without altering network packets!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of spies vs burglars",
                simpleAnalogy = "A spy sits on a bench taking photos (passive). A burglar kicks down the door to steal (active).",
                takeawayBullet = "Passive attacks breach privacy; active attacks breach integrity."
            )
        ),
        VisualizerSpec(
            id = "substitution_lab",
            title = "Caesar Wheel & Playfair Grid",
            realmNumber = 1,
            realmTitle = "Secrets 101",
            oneSentencePromise = "Turn a wheel to shift letters and see your secret emerge.",
            story = VisualizerStory(
                title = "Letter Swapping",
                oneSentenceHook = "Shift every letter along the alphabet by a secret dial number.",
                realLifeAnalogy = "A rotating combination lock that advances every letter forward by three steps.",
                conceptName = "Modular Shift Cipher",
                iconEmoji = "🎡"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Plaintext letters align with the standard A-Z alphabet.",
                    whatChanged = "Plaintext wheel set at index 0.",
                    detailNotes = "Alphabet mapped to indices 0..25."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Turn the dial by key K = 3 to shift every letter.",
                    whatChanged = "Shift dial turns +3 positions.",
                    detailNotes = "A becomes D, B becomes E."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Read the inner ring to reveal the encrypted ciphertext.",
                    whatChanged = "Cipher text letters illuminate in accent.",
                    detailNotes = "Formula: C = (P + 3) mod 26."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Using a Caesar shift of 3, what does 'CAT' encrypt to?",
                options = listOf("FDW", "ECV", "DBU", "EAW"),
                correctIndex = 0,
                explanation = "C+3=F, A+3=D, T+3=W -> FDW!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a 26-hour clock",
                simpleAnalogy = "If it's 1 o'clock and you add 3 hours, the hand points to 4. Letters wrap around the same way.",
                takeawayBullet = "Shifting letters preserves frequency patterns."
            )
        ),
        VisualizerSpec(
            id = "transposition_stego",
            title = "Rail Fence & Hide-in-Image",
            realmNumber = 1,
            realmTitle = "Secrets 101",
            oneSentencePromise = "Shuffle letters into zig-zags and hide secret bits inside pixels.",
            story = VisualizerStory(
                title = "Shuffling & Hiding",
                oneSentenceHook = "Transposition jumbles positions; steganography hides data inside pixels.",
                realLifeAnalogy = "Writing a secret message across zigzag fence slats, then taking the rails apart.",
                conceptName = "Transposition and Steganography",
                iconEmoji = "🧩"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Write plaintext in a zigzag pattern across multiple rails.",
                    whatChanged = "Letters zigzag down and up.",
                    detailNotes = "Rail 1 gets indices 0, 4, 8..."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Read along horizontal rails to form the shuffled ciphertext.",
                    whatChanged = "Letters reordered row-by-row.",
                    detailNotes = "Letter frequencies remain identical."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Least Significant Bit (LSB) steganography swaps tiny pixel bits invisibly.",
                    whatChanged = "Pixel blue component adjusted by 1 bit.",
                    detailNotes = "Human eyes cannot detect 1/256th color change."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "How does transposition differ from substitution?",
                options = listOf("It replaces letters with symbols", "It rearranges letters without changing them", "It requires two keys", "It compresses text"),
                correctIndex = 1,
                explanation = "Transposition only changes positions; the original letters stay unchanged!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of anagrams and secret compartments",
                simpleAnalogy = "Transposition is scrambling letters like an anagram. Steganography is hiding a letter in a hollow book heel.",
                takeawayBullet = "Transposition shuffles; steganography conceals existence."
            )
        ),
        VisualizerSpec(
            id = "cryptanalysis_lab",
            title = "Frequency Lab & OTP Simulator",
            realmNumber = 1,
            realmTitle = "Secrets 101",
            oneSentencePromise = "Crack ciphers with letter tallies or use One-Time Pads for perfect secrecy.",
            story = VisualizerStory(
                title = "Breaking vs Perfect Secrecy",
                oneSentenceHook = "Frequency tallies break ciphers, but one-time pads are mathematically unbreakable.",
                realLifeAnalogy = "Looking for the tallest tree in a forest: English letter 'E' always towers above the rest.",
                conceptName = "Frequency Analysis & Shannon Entropy",
                iconEmoji = "📊"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Count how often each letter appears in the ciphertext.",
                    whatChanged = "Letter count bar chart animates.",
                    detailNotes = "English letter E appears 12.7% of the time."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Map the tallest bar to 'E' to break simple substitutions.",
                    whatChanged = "Top bar mapped to E.",
                    detailNotes = "Frequency analysis exposes fixed shifts."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "One-Time Pad uses truly random keys matching message length.",
                    whatChanged = "Random key XORed with plaintext.",
                    detailNotes = "Every possible message is equally likely."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Why is the One-Time Pad theoretically unbreakable?",
                options = listOf("The key is 4096 bits long", "Every plaintext is equally probable given ciphertext", "It uses prime numbers", "It uses SHA-256"),
                correctIndex = 1,
                explanation = "Shannon proved OTP provides perfect secrecy because ciphertext gives zero information about plaintext!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of rolling a die for every letter",
                simpleAnalogy = "If every letter gets a random shift that is never reused, all output messages are equally likely.",
                takeawayBullet = "Never reuse an OTP key, or frequency patterns leak."
            )
        ),
        VisualizerSpec(
            id = "modular_clock",
            title = "Clock Arithmetic & Euclid Steps",
            realmNumber = 2,
            realmTitle = "Symmetric Forge",
            oneSentencePromise = "See how numbers wrap on a clock and find greatest common divisors.",
            story = VisualizerStory(
                title = "Clock Math",
                oneSentenceHook = "Numbers wrap around a circle, just like clock hours.",
                realLifeAnalogy = "If a clock shows 9 o'clock and you wait 5 hours, it shows 2 o'clock (14 mod 12 = 2).",
                conceptName = "Modular Arithmetic & Euclidean GCD",
                iconEmoji = "⏰"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Numbers increment around a dial of modulus N.",
                    whatChanged = "Clock hand sweeps forward.",
                    detailNotes = "Modulo wraps numbers back to zero."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Euclid's algorithm finds GCD by repeatedly taking remainders.",
                    whatChanged = "Remainder step highlights.",
                    detailNotes = "gcd(a, b) = gcd(b, a mod b)."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Extended Euclid finds the modular multiplicative inverse.",
                    whatChanged = "Inverse step calculates d.",
                    detailNotes = "Used for RSA private keys."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "What is 17 mod 5?",
                options = listOf("2", "3", "4", "1"),
                correctIndex = 0,
                explanation = "17 divided by 5 is 3 with a remainder of 2!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of days of the week",
                simpleAnalogy = "If today is Tuesday (day 2), in 7 days it is Tuesday again because 9 mod 7 = 2.",
                takeawayBullet = "Modulo is simply the remainder after division."
            )
        ),
        VisualizerSpec(
            id = "feistel_builder",
            title = "Feistel Builder & S-DES",
            realmNumber = 2,
            realmTitle = "Symmetric Forge",
            oneSentencePromise = "Split data in half, scramble one side, and swap them.",
            story = VisualizerStory(
                title = "The Feistel Machine",
                oneSentenceHook = "Split input in two, scramble the right side, XOR the left, then swap.",
                realLifeAnalogy = "A revolving door where you swap left and right hands while changing coats.",
                conceptName = "Feistel Network Architecture",
                iconEmoji = "⚙️"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Split 64-bit block into Left (L) and Right (R) halves.",
                    whatChanged = "Block splits down the middle.",
                    detailNotes = "L0 and R0 created."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Pass R through round function F with subkey K1.",
                    whatChanged = "Right half scrambles through F.",
                    detailNotes = "Substitution and permutation applied."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "XOR output with L, then swap L and R.",
                    whatChanged = "Left half updates and halves cross over.",
                    detailNotes = "L1 = R0, R1 = L0 XOR F(R0, K1)."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Why is Feistel architecture popular in block ciphers?",
                options = listOf("It uses prime numbers", "Encryption and decryption use the identical circuit", "It prevents all attacks", "It compresses files"),
                correctIndex = 1,
                explanation = "Feistel decryption uses the exact same circuit; you just reverse the subkeys!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of folding dough",
                simpleAnalogy = "You fold left over right, knead spices in, and fold again. Doing it in reverse unfolds it.",
                takeawayBullet = "Feistel transforms any round function into an invertible cipher."
            )
        ),
        VisualizerSpec(
            id = "des_explorer",
            title = "DES 16-Round Explorer",
            realmNumber = 2,
            realmTitle = "Symmetric Forge",
            oneSentencePromise = "Tour the historic cipher that protected global banking for 25 years.",
            story = VisualizerStory(
                title = "DES Anatomy",
                oneSentenceHook = "Sixteen rounds of S-box substitution and bit permutation.",
                realLifeAnalogy = "Passing secret documents through 16 consecutive stamp-and-shuffle stations.",
                conceptName = "Data Encryption Standard (DES)",
                iconEmoji = "🏛️"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Initial permutation shuffles input bits.",
                    whatChanged = "64 bits rearranged by IP table.",
                    detailNotes = "Bit ordering changes."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Eight S-boxes substitute 6 bits down to 4 non-linear bits.",
                    whatChanged = "6-bit input maps to 4-bit output.",
                    detailNotes = "S-boxes provide confusion."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Repeat for 16 rounds with distinct 48-bit subkeys.",
                    whatChanged = "Rounds 1 through 16 complete.",
                    detailNotes = "56-bit effective key length."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Why is standard DES obsolete today?",
                options = listOf("Its 56-bit key can be brute-forced in hours", "S-boxes are linear", "It cannot encrypt text", "It was never standardized"),
                correctIndex = 0,
                explanation = "Modern computers can test all 2^56 keys in minutes!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a 56-letter combination lock",
                simpleAnalogy = "It was unbreakable in 1977, but supercomputers today try every combination in seconds.",
                takeawayBullet = "Triple-DES extended it; AES replaced it."
            )
        ),
        VisualizerSpec(
            id = "aes_state_matrix",
            title = "AES State Matrix & GF(2⁸)",
            realmNumber = 2,
            realmTitle = "Symmetric Forge",
            oneSentencePromise = "Watch a 4x4 grid of bytes substitute, shift, mix, and add keys.",
            story = VisualizerStory(
                title = "The Gold Standard",
                oneSentenceHook = "A 4x4 matrix of bytes undergoes four transformations each round.",
                realLifeAnalogy = "A Rubik's cube where every face swaps stickers, slides rows, mixes columns, and locks.",
                conceptName = "Advanced Encryption Standard (AES)",
                iconEmoji = "🔲"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "SubBytes swaps each byte using an 8-bit S-Box.",
                    whatChanged = "Matrix cells swap values.",
                    detailNotes = "Non-linear confusion applied."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "ShiftRows cyclically shifts rows 1, 2, and 3 left.",
                    whatChanged = "Rows slide left by 1, 2, and 3 steps.",
                    detailNotes = "Diffusion across rows."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "MixColumns multiplies columns in Galois Field GF(2^8).",
                    whatChanged = "Columns recalculate matrix values.",
                    detailNotes = "Diffusion down columns."
                ),
                WatchStep(
                    stepIndex = 3,
                    narration = "AddRoundKey XORs the round subkey into the state.",
                    whatChanged = "State XORs with 128-bit key.",
                    detailNotes = "Key whitening complete."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Which AES step is responsible for non-linear byte confusion?",
                options = listOf("ShiftRows", "SubBytes", "MixColumns", "AddRoundKey"),
                correctIndex = 1,
                explanation = "SubBytes uses an inversion in GF(2^8) followed by affine transform for non-linear confusion!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of shuffling cards in two directions",
                simpleAnalogy = "SubBytes changes the card faces; ShiftRows and MixColumns shuffle horizontally and vertically.",
                takeawayBullet = "After 10 rounds, one tiny bit flip changes half the ciphertext."
            )
        ),
        VisualizerSpec(
            id = "block_modes_rc4",
            title = "Block Modes & RC4 Keystream",
            realmNumber = 2,
            realmTitle = "Symmetric Forge",
            oneSentencePromise = "See why ECB leaks penguin outlines and how CBC chains blocks.",
            story = VisualizerStory(
                title = "Chaining Blocks",
                oneSentenceHook = "Encrypting blocks independently leaks patterns; chaining hides everything.",
                realLifeAnalogy = "Stamping cookies with the same mold makes identical shapes; mixing each with the last hides the pattern.",
                conceptName = "ECB, CBC, and Stream Ciphers",
                iconEmoji = "🐧"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "ECB mode encrypts identical plaintext blocks into identical cipher blocks.",
                    whatChanged = "Identical blocks light up with same color.",
                    detailNotes = "The infamous Tux penguin outline remains visible."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "CBC mode XORs each plaintext block with the previous ciphertext.",
                    whatChanged = "Chain lines connect blocks.",
                    detailNotes = "Requires an Initialization Vector (IV)."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "RC4 generates a continuous stream of pseudo-random bytes.",
                    whatChanged = "Byte stream flows into XOR gate.",
                    detailNotes = "Fast stream encryption."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Why should ECB mode NEVER be used for images or structured data?",
                options = listOf("It is too slow", "Identical plaintext blocks produce identical ciphertext blocks", "It requires 3 keys", "It cannot decrypt"),
                correctIndex = 1,
                explanation = "ECB leaks structural patterns because identical plain blocks encrypt to identical cipher blocks!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of wallpaper vs paint",
                simpleAnalogy = "ECB is like wallpaper where the repeat pattern is obvious. CBC blends each roll with the last so no pattern repeats.",
                takeawayBullet = "Always use CBC or GCM with a unique IV."
            )
        ),
        VisualizerSpec(
            id = "prime_sieve",
            title = "Prime Sieve & Miller-Rabin",
            realmNumber = 3,
            realmTitle = "Public-Key Citadel",
            oneSentencePromise = "Filter out composites and test massive numbers for primality.",
            story = VisualizerStory(
                title = "The Atoms of Numbers",
                oneSentenceHook = "Primes divide only by 1 and themselves; multiplying is easy, factoring is hard.",
                realLifeAnalogy = "Dropping sand through smaller sieves until only pure golden nuggets remain.",
                conceptName = "Prime Sieves & Probabilistic Primality",
                iconEmoji = "✨"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Eratosthenes sieve strikes out multiples of 2, 3, 5...",
                    whatChanged = "Non-primes cross off the grid.",
                    detailNotes = "Only primes remain."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Multiplying two large primes p and q is instantaneous.",
                    whatChanged = "Multiplication arrow shoots to N = p * q.",
                    detailNotes = "Easy direction."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Finding p and q given only N takes billions of years.",
                    whatChanged = "Clock spins with locked padlock.",
                    detailNotes = "Trapdoor one-way function."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "What makes RSA public-key encryption secure?",
                options = listOf("Adding large numbers is impossible", "Multiplying primes is fast, but factoring their product is hard", "Computers cannot store primes", "Primes cannot be divided"),
                correctIndex = 1,
                explanation = "Integer factorization is a classic one-way trapdoor function!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of mixing yellow and blue paint",
                simpleAnalogy = "It takes one second to mix green paint, but picking apart the molecules back into yellow and blue is practically impossible.",
                takeawayBullet = "Primes are the basic paint colors of mathematics."
            )
        ),
        VisualizerSpec(
            id = "crt_discrete_log",
            title = "CRT Clock & Discrete Log",
            realmNumber = 3,
            realmTitle = "Public-Key Citadel",
            oneSentencePromise = "Solve modular puzzle clocks and understand discrete logarithms.",
            story = VisualizerStory(
                title = "Modular Riddles",
                oneSentenceHook = "Chinese Remainder Theorem reconstructs numbers from small remainders.",
                realLifeAnalogy = "Two gears of sizes 3 and 5: knowing both tooth alignments tells you the exact gear turn.",
                conceptName = "CRT & Discrete Logarithms",
                iconEmoji = "⚙️"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Align remainders mod 3, mod 5, and mod 7.",
                    whatChanged = "Clocks synchronize hands.",
                    detailNotes = "Unique solution mod (3*5*7 = 105)."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Modular exponentiation calculates g^x mod p efficiently.",
                    whatChanged = "Square-and-multiply runs fast.",
                    detailNotes = "Fast forward operation."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Given g and g^x mod p, finding x is the discrete log problem.",
                    whatChanged = "Question mark hovers over exponent x.",
                    detailNotes = "Diffie-Hellman hardness assumption."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "In the Diffie-Hellman protocol, what hard math problem protects the shared secret?",
                options = listOf("Factoring primes", "The Discrete Logarithm Problem", "Solving linear equations", "Computing matrix inverses"),
                correctIndex = 1,
                explanation = "Finding the secret exponent x from g^x mod p is computationally intractable for large primes!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of calculating steps forward vs retracing footsteps",
                simpleAnalogy = "It's easy to take 100 steps forward, but looking at the landing spot in a sandstorm and knowing the step count is hard.",
                takeawayBullet = "Discrete log is the trapdoor behind Diffie-Hellman."
            )
        ),
        VisualizerSpec(
            id = "rsa_stepper",
            title = "RSA Step-by-Step with Tiny Primes",
            realmNumber = 3,
            realmTitle = "Public-Key Citadel",
            oneSentencePromise = "Generate public and private keys using tiny primes p=3 and q=11.",
            story = VisualizerStory(
                title = "Two Secret Keys",
                oneSentenceHook = "One public lock that anyone can snap shut; one private key to open it.",
                realLifeAnalogy = "A mailbox where anyone drops letters through the slot, but only the owner has the door key.",
                conceptName = "RSA Public-Key Cryptosystem",
                iconEmoji = "🔑"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Pick tiny primes p = 3 and q = 11. Compute n = 33.",
                    whatChanged = "Primes multiply into modulus n = 33.",
                    detailNotes = "Modulus shared publicly."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Calculate totient phi(n) = (3-1)*(11-1) = 20.",
                    whatChanged = "Totient dial counts coprimes.",
                    detailNotes = "Euler totient formula."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Choose public exponent e = 3. Find private key d = 7.",
                    whatChanged = "Keys (3, 33) and (7, 33) generate.",
                    detailNotes = "(3 * 7) mod 20 = 1."
                ),
                WatchStep(
                    stepIndex = 3,
                    narration = "Encrypt: C = M^3 mod 33. Decrypt: M = C^7 mod 33.",
                    whatChanged = "Message transforms and decrypts.",
                    detailNotes = "Math locks and unlocks perfectly."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "If public key is (e=3, n=33) and message M = 2, what is ciphertext C = M^e mod n?",
                options = listOf("6", "8", "1", "16"),
                correctIndex = 1,
                explanation = "2^3 = 8, and 8 mod 33 = 8!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a padlock and key",
                simpleAnalogy = "Anyone can click a padlock shut (public key). Only the person with the physical key (private key) can open it.",
                takeawayBullet = "Never share your private key d."
            )
        ),
        VisualizerSpec(
            id = "dh_elgamal_lab",
            title = "Diffie-Hellman Paint & ElGamal",
            realmNumber = 3,
            realmTitle = "Public-Key Citadel",
            oneSentencePromise = "Mix colors in public to agree on a secret shared key without eavesdroppers knowing.",
            story = VisualizerStory(
                title = "Secret Key in Plain Sight",
                oneSentenceHook = "Alice and Bob mix secret colors with a public color to create a shared secret.",
                realLifeAnalogy = "Mixing yellow with your secret color, exchanging mixtures, and adding your secret again: both get the same brown!",
                conceptName = "Diffie-Hellman Key Exchange",
                iconEmoji = "🎨"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Alice and Bob agree on public paint (Yellow).",
                    whatChanged = "Yellow pot shows between Alice and Bob.",
                    detailNotes = "Public generator and modulus."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Alice adds secret Red (gets Orange); Bob adds secret Blue (gets Green).",
                    whatChanged = "Alice holds Orange, Bob holds Green.",
                    detailNotes = "Private exponentiation."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "They swap mixtures over the untrusted wire.",
                    whatChanged = "Cans cross paths in mid-air.",
                    detailNotes = "Eavesdropper Mallory only sees Orange and Green."
                ),
                WatchStep(
                    stepIndex = 3,
                    narration = "Each adds their own secret color: both arrive at the exact same Brown!",
                    whatChanged = "Both paint pots turn identical Rich Brown.",
                    detailNotes = "Shared secret established!"
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Can an eavesdropper looking at the exchanged mixtures find the shared secret?",
                options = listOf("Yes, by mixing them together", "No, separating the original secret colors is mathematically intractable", "Yes, using brute force in 1 second", "Only if Bob reveals his name"),
                correctIndex = 1,
                explanation = "Separating mixed paint colors represents the discrete logarithm problem!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a locked briefcase with two padlocks",
                simpleAnalogy = "Alice puts her lock on, Bob puts his lock on, Alice removes hers, Bob removes his.",
                takeawayBullet = "Diffie-Hellman lets two strangers agree on a secret over an open radio."
            )
        ),
        VisualizerSpec(
            id = "ecc_point_addition",
            title = "Elliptic Curve Point Addition",
            realmNumber = 3,
            realmTitle = "Public-Key Citadel",
            oneSentencePromise = "Bounce points across an algebraic curve to achieve 3072-bit RSA security with 256 bits.",
            story = VisualizerStory(
                title = "Geometry of Secrets",
                oneSentenceHook = "Draw a line through two points on a curve, find where it hits, and reflect across the axis.",
                realLifeAnalogy = "A frictionless billiard ball bouncing off curve bumpers in a predictable mathematical pattern.",
                conceptName = "Elliptic Curve Cryptography (ECC)",
                iconEmoji = "📈"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Curve equation: y² = x³ + ax + b over a finite field.",
                    whatChanged = "Symmetric curve draws smoothly.",
                    detailNotes = "Symmetric about the x-axis."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Point addition: draw line through P and Q to hit -R, reflect to R.",
                    whatChanged = "Tangent line draws and reflects.",
                    detailNotes = "P + Q = R."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Point doubling: draw tangent at P to find 2P.",
                    whatChanged = "Tangent bounces to 2P.",
                    detailNotes = "Repeated doubling computes k * P fast."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Why is a 256-bit ECC key considered as secure as a 3072-bit RSA key?",
                options = listOf("ECC keys are compressed", "Discrete log on elliptic curves has no sub-exponential attack algorithm", "RSA was designed poorly", "ECC was created by NASA"),
                correctIndex = 1,
                explanation = "The elliptic curve discrete log problem is vastly harder than prime factoring per bit!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of billiard trick shots",
                simpleAnalogy = "You bounce the ball 100 times. Anyone seeing the final ball position cannot guess how many bounces occurred.",
                takeawayBullet = "Smaller keys mean faster mobile performance."
            )
        ),
        VisualizerSpec(
            id = "sha256_avalanche",
            title = "SHA-256 Digest & Avalanche Meter",
            realmNumber = 4,
            realmTitle = "Trust & Proof",
            oneSentencePromise = "Flip a single character and watch half the 256-bit hash explode.",
            story = VisualizerStory(
                title = "Digital Fingerprint",
                oneSentenceHook = "Every piece of data gets a unique, fixed-size fingerprint.",
                realLifeAnalogy = "A blender that turns apples into fruit paste: identical inputs give identical paste, but you can't un-blend paste.",
                conceptName = "Cryptographic Hash Functions",
                iconEmoji = "🌪️"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Hash of 'hello' generates 64 hexadecimal characters.",
                    whatChanged = "Hash digest displays.",
                    detailNotes = "Always 256 bits long."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Change 'hello' to 'Hello' (one bit flip).",
                    whatChanged = "First letter capitalizes.",
                    detailNotes = "Input difference: 1 bit."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Avalanche effect flips ~50% of all output bits completely.",
                    whatChanged = "Avalanche meter swings to 51%.",
                    detailNotes = "Complete output unpredictability."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "What is the primary property of the cryptographic Avalanche Effect?",
                options = listOf("Output size grows with input", "Flipping one input bit changes roughly half the output bits", "Hashes can be reversed", "Hashes compress files to 0 bytes"),
                correctIndex = 1,
                explanation = "A good hash ensures tiny input tweaks completely randomize the output digest!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a row of dominos with fireworks",
                simpleAnalogy = "Knocking over one tiny domino triggers fireworks that randomize the entire room.",
                takeawayBullet = "You can never guess the input from the hash."
            )
        ),
        VisualizerSpec(
            id = "hmac_lab",
            title = "HMAC Pad Builder & Collisions",
            realmNumber = 4,
            realmTitle = "Trust & Proof",
            oneSentencePromise = "Mix a secret key with inner and outer pads to prevent length-extension attacks.",
            story = VisualizerStory(
                title = "Sealed Authenticity",
                oneSentenceHook = "A hash alone proves integrity; a hash with a secret key proves who sent it.",
                realLifeAnalogy = "Wax-stamping a letter with your personal signet ring so the recipient knows you wrote it.",
                conceptName = "Hash-based Message Authentication Codes",
                iconEmoji = "🛡️"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Plain hash(K || M) is vulnerable to length-extension attacks.",
                    whatChanged = "Vulnerability banner displays.",
                    detailNotes = "Attackers can append extra data."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "HMAC uses inner pad (ipad) and outer pad (opad).",
                    whatChanged = "Two padding boxes surround key.",
                    detailNotes = "K XOR ipad and K XOR opad."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Result: Hash(K_opad || Hash(K_ipad || M)).",
                    whatChanged = "Nested hash completes.",
                    detailNotes = "Mathematically immune to extension."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "What does an HMAC provide that a standard SHA-256 hash does NOT?",
                options = listOf("Data compression", "Authentication that the sender holds the secret key", "Faster encryption", "Reversibility"),
                correctIndex = 1,
                explanation = "HMAC verifies both data integrity and message authenticity using a shared secret key!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a signed wax seal",
                simpleAnalogy = "Anyone can photocopy a document (hash), but only someone with the actual metal stamp (key) can press the wax seal.",
                takeawayBullet = "HMAC combines a hash with a secret key."
            )
        ),
        VisualizerSpec(
            id = "sign_verify_lab",
            title = "Sign & Verify Interactive",
            realmNumber = 4,
            realmTitle = "Trust & Proof",
            oneSentencePromise = "Hash a contract, encrypt with your private key, and let the world verify with your public key.",
            story = VisualizerStory(
                title = "Digital Pen & Paper",
                oneSentenceHook = "Sign with your private key; anyone in the world verifies with your public key.",
                realLifeAnalogy = "An autograph stamped in glowing ink that everyone can inspect under a magnifying glass, but only you can sign.",
                conceptName = "Digital Signatures & Non-Repudiation",
                iconEmoji = "✍️"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Alice hashes her document to a 256-bit digest.",
                    whatChanged = "Document condenses into hash.",
                    detailNotes = "Hashing ensures fixed size."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Alice encrypts the digest using her private key to produce the signature.",
                    whatChanged = "Signature attaches to document.",
                    detailNotes = "Signature = Digest^d mod n."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Bob decrypts the signature using Alice's public key.",
                    whatChanged = "Signature decrypts to digest.",
                    detailNotes = "Digest = Signature^e mod n."
                ),
                WatchStep(
                    stepIndex = 3,
                    narration = "Bob compares both digests: match means genuine and untampered!",
                    whatChanged = "Green checkmark illuminates.",
                    detailNotes = "Integrity and non-repudiation verified."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Which key is used to CREATE a digital signature?",
                options = listOf("The receiver's public key", "The sender's private key", "The shared symmetric key", "The CA root key"),
                correctIndex = 1,
                explanation = "Only the sender possesses their private key, proving authenticity!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a notary's stamp",
                simpleAnalogy = "Only the notary has the stamp (private key). Everyone knows what the stamp looks like (public key) to verify it.",
                takeawayBullet = "Signing proves identity and prevents denying the message."
            )
        ),
        VisualizerSpec(
            id = "auth_challenge",
            title = "Salted Hash & Challenge-Response",
            realmNumber = 4,
            realmTitle = "Trust & Proof",
            oneSentencePromise = "Add salt to stop rainbow tables and prove knowledge without sending passwords.",
            story = VisualizerStory(
                title = "Never Send Passwords",
                oneSentenceHook = "Prove you know the secret without ever sending the secret over the wire.",
                realLifeAnalogy = "A guard asks for today's riddle answer: you solve a math problem based on it without saying the secret word.",
                conceptName = "Authentication & Salted Hashes",
                iconEmoji = "🛂"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Storing plain hashes allows rainbow table lookup attacks.",
                    whatChanged = "Pre-computed table matches hash.",
                    detailNotes = "Vulnerable to lookup."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Add a random 128-bit salt to every password before hashing.",
                    whatChanged = "Salt prepends to password.",
                    detailNotes = "Defeats rainbow tables completely."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Challenge-Response: server sends nonce, client signs it.",
                    whatChanged = "Nonce travels to client.",
                    detailNotes = "Password never crosses the network."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Why do we add a unique salt to passwords before hashing them?",
                options = listOf("To make hashes shorter", "To ensure identical passwords generate completely different hashes", "To encrypt the database", "To bypass login"),
                correctIndex = 1,
                explanation = "Salts ensure two users with the same password have distinct hashes, neutralizing rainbow tables!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of adding unique spice to identical soups",
                simpleAnalogy = "Even if two cooks use the same water and vegetable, unique spice makes each soup taste different.",
                takeawayBullet = "Never store plain passwords."
            )
        ),
        VisualizerSpec(
            id = "kerberos_pki",
            title = "Kerberos Ticket Flow & X.509",
            realmNumber = 4,
            realmTitle = "Trust & Proof",
            oneSentencePromise = "Get a ticket from the ticket booth to ride any service in the amusement park.",
            story = VisualizerStory(
                title = "Tickets & Passports",
                oneSentenceHook = "Log in once, receive a Ticket Granting Ticket, and present it to services.",
                realLifeAnalogy = "Buying a wristband at the carnival entrance so you don't pay money at every ride.",
                conceptName = "Kerberos and Public Key Infrastructure",
                iconEmoji = "🎫"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Client authenticates with Authentication Server (AS).",
                    whatChanged = "AS issues TGT.",
                    detailNotes = "Encrypted with user's password hash."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Client presents TGT to Ticket Granting Server (TGS).",
                    whatChanged = "TGS verifies TGT.",
                    detailNotes = "Issues service ticket for file server."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Client presents service ticket to target server.",
                    whatChanged = "Target server validates ticket.",
                    detailNotes = "Access granted without password re-entry."
                ),
                WatchStep(
                    stepIndex = 3,
                    narration = "X.509 certificates bind a public key to an identity via Certificate Authority.",
                    whatChanged = "CA signs certificate.",
                    detailNotes = "Chain of trust established."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "What is the primary benefit of Kerberos single sign-on tickets?",
                options = listOf("Users never need passwords", "Passwords are only sent once during initial authentication", "Tickets last forever", "It replaces firewalls"),
                correctIndex = 1,
                explanation = "Kerberos passes encrypted short-lived tickets instead of transmitting passwords across the network!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a movie ticket",
                simpleAnalogy = "You show ID at the box office once to buy a ticket. The usher at screen 4 just tears your ticket without asking for your ID again.",
                takeawayBullet = "Tickets expire automatically for safety."
            )
        ),
        VisualizerSpec(
            id = "ipsec_pgp",
            title = "PGP Ring & IPsec Tunnel",
            realmNumber = 5,
            realmTitle = "Real-World Defense",
            oneSentencePromise = "Build an encrypted highway between networks and sign emails with PGP.",
            story = VisualizerStory(
                title = "Secure Highways",
                oneSentenceHook = "Wrap entire IP packets inside encrypted envelopes for secure branch-to-branch tunnels.",
                realLifeAnalogy = "An armored truck driving through a public highway: no one outside can see what's inside.",
                conceptName = "IPsec Tunneling and PGP Email",
                iconEmoji = "🛣️"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Transport mode encrypts only payload; headers remain visible.",
                    whatChanged = "Payload turns dark.",
                    detailNotes = "Useful for host-to-host."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Tunnel mode encrypts the entire original IP packet and adds a new header.",
                    whatChanged = "Entire packet wraps in new IP shell.",
                    detailNotes = "Gateway-to-gateway VPN."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "PGP signs email and encrypts symmetric session keys with RSA.",
                    whatChanged = "Email seals with key ring.",
                    detailNotes = "Web of trust authentication."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "In IPsec Tunnel mode, what is encrypted?",
                options = listOf("Only the TCP payload", "The entire original IP packet including original headers", "Only the Wi-Fi password", "Nothing"),
                correctIndex = 1,
                explanation = "Tunnel mode encapsulates the complete original IP packet inside a brand-new IP header!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of putting an envelope inside a bigger envelope",
                simpleAnalogy = "Transport mode is blacking out the letter text. Tunnel mode is putting the whole stamped envelope inside an armored pouch.",
                takeawayBullet = "Tunnel mode connects remote branch offices securely."
            )
        ),
        VisualizerSpec(
            id = "tls_wifi_handshake",
            title = "TLS 1.3 & Wi-Fi 4-Way Handshake",
            realmNumber = 5,
            realmTitle = "Real-World Defense",
            oneSentencePromise = "Establish a secure web session in a single round-trip.",
            story = VisualizerStory(
                title = "The Internet's Lock",
                oneSentenceHook = "Client and server say hello, negotiate parameters, and start encrypting in 1 round trip.",
                realLifeAnalogy = "Two diplomats introducing themselves with official badges, exchanging secret ciphers, and closing the door.",
                conceptName = "Transport Layer Security (TLS 1.3)",
                iconEmoji = "🤝"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "ClientHello sends supported ciphers and key share.",
                    whatChanged = "Client message flies to server.",
                    detailNotes = "1-RTT handshake begins."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "ServerHello responds with chosen cipher and server key share.",
                    whatChanged = "Server message returns.",
                    detailNotes = "Both compute session key via ECDHE."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "EncryptedExtensions and Certificate follow immediately.",
                    whatChanged = "Certificate chain validates.",
                    detailNotes = "Server authenticated."
                ),
                WatchStep(
                    stepIndex = 3,
                    narration = "Application data flows under symmetric AES-GCM.",
                    whatChanged = "Lock icon turns green.",
                    detailNotes = "Full forward secrecy guaranteed."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Why is TLS 1.3 faster and safer than TLS 1.2?",
                options = listOf("It removed weak ciphers and completes handshake in 1 round-trip (1-RTT)", "It disables encryption", "It doesn't use certificates", "It uses plain HTTP"),
                correctIndex = 0,
                explanation = "TLS 1.3 trimmed legacy ciphers and merged key exchange into the very first round-trip!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a telephone handshake",
                simpleAnalogy = "Hello, here's my language and half our code; server replies with its half. Now both speak encrypted.",
                takeawayBullet = "Look for the padlock in your browser address bar."
            )
        ),
        VisualizerSpec(
            id = "firewall_builder",
            title = "Firewall Rule Builder & IDS",
            realmNumber = 5,
            realmTitle = "Real-World Defense",
            oneSentencePromise = "Build packet filter rules to block malicious ports and pass legitimate traffic.",
            story = VisualizerStory(
                title = "The Castle Gate",
                oneSentenceHook = "Inspect every packet's source, destination, and port before granting entry.",
                realLifeAnalogy = "A security guard checking passports and luggage tags at the border checkpoint.",
                conceptName = "Firewalls, DMZ, and Intrusion Detection",
                iconEmoji = "🧱"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Packet arrives: Source IP, Destination IP, Protocol, Port.",
                    whatChanged = "Packet header expands.",
                    detailNotes = "Header fields inspected."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Rule matches: DENY TCP port 23 (Telnet).",
                    whatChanged = "Red barrier drops.",
                    detailNotes = "Insecure protocol blocked."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Rule matches: ALLOW TCP port 443 (HTTPS) established.",
                    whatChanged = "Green gate opens.",
                    detailNotes = "Legitimate web traffic passes."
                ),
                WatchStep(
                    stepIndex = 3,
                    narration = "Stateful inspection tracks TCP connection handshake states.",
                    whatChanged = "State table updates SYN/ACK.",
                    detailNotes = "Blocks unsolicited incoming packets."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "Which firewall rule should ALWAYS be placed at the very end of an enterprise ruleset?",
                options = listOf("ALLOW ALL", "DEFAULT DENY (DROP ALL)", "ALLOW PORT 80", "LOG OFF"),
                correctIndex = 1,
                explanation = "A default deny policy ensures anything not explicitly allowed is automatically rejected!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a bouncer with a guest list",
                simpleAnalogy = "If your name is on the list, you enter. If your name isn't on the list, the default rule is you stay outside.",
                takeawayBullet = "Explicit allow, default deny."
            )
        ),
        VisualizerSpec(
            id = "sqli_dos_sandbox",
            title = "SQLi Sandbox & DoS Flood Meter",
            realmNumber = 5,
            realmTitle = "Real-World Defense",
            oneSentencePromise = "Type `' OR '1'='1` to break a mock login and see how parameterized queries fix it.",
            story = VisualizerStory(
                title = "Input Tricks",
                oneSentenceHook = "Unsanitized user inputs fool SQL databases into treating data as executable commands.",
                realLifeAnalogy = "Asking someone for their name and saying 'Robert'); DROP TABLE Students;--' so they execute the instruction.",
                conceptName = "Web Application Injection & DDoS",
                iconEmoji = "💉"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Vulnerable query concatenates input: SELECT * FROM Users WHERE user = 'admin'.",
                    whatChanged = "SQL string stitches together.",
                    detailNotes = "Data mixes with code."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Attacker types ' OR '1'='1 --.",
                    whatChanged = "Query evaluates to TRUE.",
                    detailNotes = "Login bypassed without password!"
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Defense: Parameterized queries treat input purely as literal data.",
                    whatChanged = "Placeholders (?) bind parameters.",
                    detailNotes = "Payload treated as raw string, attack fails."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "What is the primary defense against SQL Injection?",
                options = listOf("Using strong passwords", "Parameterized queries (Prepared Statements)", "Hiding the database name", "Disabling JavaScript"),
                correctIndex = 1,
                explanation = "Prepared statements keep user data completely separate from executable SQL instructions!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of a fill-in-the-blank form",
                simpleAnalogy = "If you write 'I am the King' in the name blank, you are still just a name on the form, not actual royalty.",
                takeawayBullet = "Never concatenate strings into SQL queries."
            )
        ),
        VisualizerSpec(
            id = "zkp_merkle_pqc",
            title = "ZKP Cave & Merkle Tree Explorer",
            realmNumber = 5,
            realmTitle = "Real-World Defense",
            oneSentencePromise = "Prove you know the secret word in the cave without saying it out loud.",
            story = VisualizerStory(
                title = "The Frontier of Proof",
                oneSentenceHook = "Zero-knowledge proofs convince someone you know a secret without revealing the secret.",
                realLifeAnalogy = "Proving you have the key to a circular cave door by entering one side and walking out the other.",
                conceptName = "Zero-Knowledge & Post-Quantum",
                iconEmoji = "🌌"
            ),
            watchSteps = listOf(
                WatchStep(
                    stepIndex = 0,
                    narration = "Peggy enters circular cave from path A or B.",
                    whatChanged = "Peggy walks into cave branch.",
                    detailNotes = "Victor waits outside."
                ),
                WatchStep(
                    stepIndex = 1,
                    narration = "Victor calls out: 'Come out through path A!'.",
                    whatChanged = "Victor makes random challenge.",
                    detailNotes = "Victor cannot see inside."
                ),
                WatchStep(
                    stepIndex = 2,
                    narration = "Peggy uses secret key to open door and exits via path A.",
                    whatChanged = "Peggy unlocks door and steps out.",
                    detailNotes = "Probability of guessing: 50%."
                ),
                WatchStep(
                    stepIndex = 3,
                    narration = "Repeat 30 times: probability of cheating drops to 1 in 1,000,000,000!",
                    whatChanged = "Success meter fills to 100%.",
                    detailNotes = "Proof achieved with ZERO secret leaked."
                ),
            ),
            checkQuestion = CheckQuestion(
                prompt = "What does a Zero-Knowledge Proof achieve?",
                options = listOf("It proves a statement is true without revealing any secret information", "It cracks passwords with quantum computers", "It replaces all encryption", "It deletes blockchain blocks"),
                correctIndex = 0,
                explanation = "ZKP convinces a verifier with mathematical certainty while keeping the underlying secret 100% confidential!"
            ),
            lostHelp = LostHelp(
                simpleHeadline = "Think of color-blind test cards",
                simpleAnalogy = "You hold two balls behind your back and swap them (or don't). Your friend guesses every time, proving they see the colors without telling you which is which.",
                takeawayBullet = "Proof without exposure."
            )
        ),
    )

    private val specsMap: Map<String, VisualizerSpec> = allSpecs.associateBy { it.id }

    fun getSpecById(id: String): VisualizerSpec? = specsMap[id]

    fun getSpecsForRealm(realmNumber: Int): List<VisualizerSpec> = allSpecs.filter { it.realmNumber == realmNumber }

    fun getSpecForChapter(chapterNumber: Int): VisualizerSpec? {
        val index = chapterNumber - 1
        return allSpecs.getOrNull(index)
    }
}
