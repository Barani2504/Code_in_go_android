package com.simats.codeingo.data.repository

import com.simats.codeingo.data.model.DSAExerciseType

/**
 * 🛂 Chapter6QuestionsData — Chapter 6: Identity Vault
 * Authentication & Key Management (Android Expansion Track)
 * Contains all 45 pedagogy-reviewed questions for Levels 26 to 30:
 *  - Level 26: Stage 1: Prove It's You (Factors & Passwords) - 10 questions
 *  - Level 27: Stage 2: One-Time Codes (HOTP & TOTP) - 10 questions
 *  - Level 28: Stage 3: Ticket Booth (Kerberos & TGT) - 10 questions
 *  - Level 29: Stage 4: Key Lifecycle & KMS - 10 questions
 *  - Level 30: Stage 5: Boss: The Gatekeeper (OAuth & OIDC) - 5 questions
 */
object Chapter6QuestionsData {

    fun getQuestionsForLevel(levelNumber: Int): List<QuizQuestion> = when (levelNumber) {
        26 -> level26Questions()
        27 -> level27Questions()
        28 -> level28Questions()
        29 -> level29Questions()
        30 -> level30BossQuestions()
        else -> level26Questions()
    }

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 26: PROVE IT'S YOU (FACTORS & PASSWORDS) (c6_l1)
    // ══════════════════════════════════════════════════════════════════
    private fun level26Questions(): List<QuizQuestion> = listOf(
        // Q1 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l1_q1",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Three Authentication Factors",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "What are the three canonical categories of authentication factors in security architecture?",
            targetPrompt = "Select the three distinct factor categories",
            options = listOf(
                "Something you know, Something you have, Something you are",
                "Username, Password, PIN number",
                "Software token, Hardware token, Security card",
                "Encryption, Hashing, Digital Signature"
            ),
            correctOptionIndex = 0,
            conceptTag = "three_authentication_factors",
            misconceptionTag = "confusing_credentials_with_categories",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Knowledge (password), Possession (phone/token), Inherence (biometrics)!",
            hint1 = "Think about categories: memory, physical items, and physical traits.",
            hint2 = "Password is knowledge; a phone is possession; a fingerprint is inherence.",
            hint3 = "Something you know, have, and are.",
            explanation = "Authentication factors are grouped into three distinct categories: Knowledge (something you know: passwords, PINs), Possession (something you have: hardware tokens, smartphones), and Inherence (something you are: fingerprints, facial geometry).",
            wrongOptionExplanations = mapOf(
                1 to "Username, password, and PIN all belong to the single category 'Something you know'.",
                2 to "Tokens and security cards both represent 'Something you have'.",
                3 to "Encryption, hashing, and signatures are cryptographic primitives, not authentication factors."
            )
        ),

        // Q2 (FILL_CODE)
        QuizQuestion(
            id = "c6_l1_q2",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Password Salting",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Complete the PBKDF2 password derivation call in Python by supplying the unique random salt parameter:",
            targetPrompt = "Tap the salt variable token",
            fillCodeTemplate = "# Deriving secure password hash with unique random salt\nsalt = os.urandom(16)\nhash_val = hashlib.pbkdf2_hmac('sha256', password, [___], 100_000)",
            fillCodeWordBank = listOf("salt", "password", "username", "key"),
            fillCodeCorrectToken = "salt",
            conceptTag = "password_salting_pbkdf2",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: PBKDF2 takes (hash_name, password, salt, iterations)!",
            hint1 = "The salt prevents rainbow table lookups.",
            hint2 = "The third parameter is the random salt.",
            hint3 = "Tap salt.",
            explanation = "A cryptographic salt is a random byte sequence generated uniquely per user. Passing 'salt' into PBKDF2 ensures that two users with identical passwords produce completely different hash digests."
        ),

        // Q3 (MATCH_PAIRS)
        QuizQuestion(
            id = "c6_l1_q3",
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Factor Matching",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Connect each authentication credential to its exact factor category:",
            targetPrompt = "Match credential to factor category",
            matchLeft = listOf(
                "Master Password / PIN",
                "FIDO2 Hardware Key",
                "Retina / Iris Scan",
                "Push Alert on Smartphone"
            ),
            matchRight = listOf(
                "Something You Know",
                "Something You Have",
                "Something You Are",
                "Something You Have"
            ),
            matchSolution = mapOf(
                "Master Password / PIN" to "Something You Know",
                "FIDO2 Hardware Key" to "Something You Have",
                "Retina / Iris Scan" to "Something You Are",
                "Push Alert on Smartphone" to "Something You Have"
            ),
            conceptTag = "credential_factor_classification",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Brain memory = Know. Physical device = Have. Biology = Are!",
            hint1 = "PIN is memorized -> Something You Know.",
            hint2 = "FIDO2 key is a USB device -> Something You Have.",
            hint3 = "Iris scan is a biological feature -> Something You Are.",
            explanation = "Passwords and PINs are knowledge factors; physical tokens, smartcards, and phones are possession factors; biometrics (fingerprint, iris, voice) are inherence factors."
        ),

        // Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c6_l1_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "MFA Category Rule",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Evaluate this security policy statement:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "Requiring a user to enter both their account password AND an answer to a secret security question (e.g. 'Mother's maiden name') constitutes valid Multi-Factor Authentication (MFA).",
            trueFalseIsCorrectTrue = false,
            conceptTag = "mfa_distinct_categories_rule",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: FALSE! Both are 'Something you know'! True MFA requires credentials from TWO DIFFERENT factor categories!",
            hint1 = "A password is a knowledge factor.",
            hint2 = "A security question answer is also a knowledge factor.",
            hint3 = "Two items from the same category is NOT multi-factor. False!",
            explanation = "True Multi-Factor Authentication requires credentials from at least two DIFFERENT categories (e.g. Knowledge + Possession). Using a password plus a secret question is merely two knowledge factors ('Two-Step', not 'Two-Factor')."
        ),

        // Q5 (ORDER_STEPS)
        QuizQuestion(
            id = "c6_l1_q5",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Password Storage Pipeline",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Arrange the secure backend sequence for registering and saving a user password:",
            targetPrompt = "Order password registration steps",
            orderStepsInitial = listOf(
                "Save username, salt, and resulting hash in database (discard plaintext)",
                "Generate cryptographically secure 16-byte random salt (CSPRNG)",
                "Receive plaintext password over TLS encrypted connection",
                "Compute memory-hard adaptive hash (e.g. Argon2id / bcrypt)"
            ),
            orderStepsSolution = listOf(
                "Receive plaintext password over TLS encrypted connection",
                "Generate cryptographically secure 16-byte random salt (CSPRNG)",
                "Compute memory-hard adaptive hash (e.g. Argon2id / bcrypt)",
                "Save username, salt, and resulting hash in database (discard plaintext)"
            ),
            conceptTag = "password_storage_lifecycle",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Receive over TLS ➔ Generate Salt ➔ Compute Argon2id Hash ➔ Store Salt + Hash!",
            hint1 = "First receive input over secure channel.",
            hint2 = "Generate a fresh random salt.",
            hint3 = "Compute the slow hash with the salt, then persist.",
            explanation = "Secure password registration requires: receiving over TLS, generating a fresh CSPRNG salt, passing password + salt through a slow KDF (Argon2id), and storing only the salt and hash."
        ),

        // Q6 (COMPLEXITY_DIAL)
        QuizQuestion(
            id = "c6_l1_q6",
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Rainbow Table Lookup Time",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is the lookup time complexity for an attacker cracking unsalted, fast password hashes using a precomputed Rainbow Table?",
            targetPrompt = "Set table lookup complexity",
            complexityDialCorrect = "O(1)",
            conceptTag = "rainbow_table_o1_lookup",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Without a salt, rainbow tables reduce password cracking to an instant hash-table lookup O(1)!",
            hint1 = "Precomputed tables map hash -> password.",
            hint2 = "Hash table lookups take constant time.",
            hint3 = "Constant lookup time is O(1).",
            explanation = "Rainbow tables precalculate billions of password-to-hash mappings offline. When hashes are unsalted, matching an intercepted hash against the table takes constant time O(1)."
        ),

        // Q7 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l1_q7",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "How Salt Defeats Rainbow Tables",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Why does adding a unique cryptographic salt to each user password completely neutralize precomputed Rainbow Tables?",
            targetPrompt = "Select the defensive mechanism",
            options = listOf(
                "Because the salt is unique per user, an attacker cannot use one precomputed table; a new table would need to be computed for every single user",
                "Because salts encrypt the database using AES-256",
                "Because salts truncate passwords to 8 characters",
                "Because salts require the user to enter a 6-digit SMS code"
            ),
            correctOptionIndex = 0,
            conceptTag = "salting_defenses_explained",
            misconceptionTag = "confusing_salt_with_encryption",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: A 128-bit salt means 2^128 possible tables. Precomputing all tables is impossible!",
            hint1 = "Rainbow tables work by sharing one precalculated lookup table across millions of accounts.",
            hint2 = "If every user has a different random prefix, that shared table becomes useless.",
            hint3 = "An attacker must build a new table for each individual user.",
            explanation = "Rainbow tables rely on precomputing hash chains for common passwords. With a unique 16-byte salt per user, the attacker must recompute the entire table for every individual user, which is computationally infeasible."
        ),

        // Q8 (FILL_CODE)
        QuizQuestion(
            id = "c6_l1_q8",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Memory-Hard KDF Standard",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Supply the name of the modern Password Hashing Competition (PHC) winner designed to be memory-hard against GPU and ASIC cracking:",
            targetPrompt = "Tap the modern KDF token",
            fillCodeTemplate = "# Recommended state-of-the-art password hash\nkdf_algorithm = '[___]'",
            fillCodeWordBank = listOf("Argon2id", "MD5", "SHA256", "DES"),
            fillCodeCorrectToken = "Argon2id",
            conceptTag = "argon2id_memory_hard_kdf",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Argon2id is the winner of the Password Hashing Competition (RFC 9106)!",
            hint1 = "MD5 and SHA-256 are fast general-purpose hashes, not password KDFs.",
            hint2 = "The PHC winner is Argon2id.",
            hint3 = "Tap Argon2id.",
            explanation = "Argon2id won the Password Hashing Competition. It is memory-hard and time-costly, preventing adversaries from running millions of parallel guesses on graphics cards (GPUs) or custom chips (ASICs)."
        ),

        // Q9 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c6_l1_q9",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fast Hashes for Passwords",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Fact check this backend architecture practice:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "Fast cryptographic hash functions like SHA-256 and MD5 are recommended for password storage because they minimize server CPU load during login.",
            trueFalseIsCorrectTrue = false,
            conceptTag = "fast_hashes_bad_for_passwords",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: FALSE! Fast hashes allow GPU cracking rigs to compute billions of guesses per second! Passwords need SLOW adaptive KDFs!",
            hint1 = "If a hash takes 1 microsecond on a CPU, how fast can a GPU crack it?",
            hint2 = "Modern GPUs can calculate billions of SHA-256 hashes every second.",
            hint3 = "Fast hashes make offline brute-force trivial. The statement is False.",
            explanation = "Fast hash functions are catastrophic for password storage. A modern GPU cluster can compute over 100 billion SHA-256 hashes per second. Password storage requires intentionally slow, tunable KDFs (Argon2id, bcrypt, scrypt)."
        ),

        // Q10 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l1_q10",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Credential Stuffing Attack",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is a 'Credential Stuffing' attack?",
            targetPrompt = "Select the credential stuffing definition",
            options = listOf(
                "Automated bots testing leaked username/password combos from past breaches across thousands of other websites",
                "Injecting SQL syntax into login fields to bypass authentication",
                "Flooding web servers with UDP packets to knock them offline",
                "Eavesdropping on unencrypted public Wi-Fi to capture passwords in transit"
            ),
            correctOptionIndex = 0,
            conceptTag = "credential_stuffing_definition",
            misconceptionTag = "confusing_stuffing_with_sqli",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Users reuse passwords! Bots take credentials leaked from Site A and try them on Site B, C, and D!",
            hint1 = "It exploits the human habit of password reuse across different websites.",
            hint2 = "Attackers automate millions of login attempts using breach dumps.",
            hint3 = "Testing leaked pairs across other sites.",
            explanation = "Credential stuffing is the automated injection of breached username/password pairs into website login forms, relying on the fact that over 60% of users reuse passwords across multiple services."
        )
    )

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 27: ONE-TIME CODES (HOTP & TOTP) (c6_l2)
    // ══════════════════════════════════════════════════════════════════
    private fun level27Questions(): List<QuizQuestion> = listOf(
        // Q1 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l2_q1",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "HOTP vs TOTP Core Difference",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "What is the fundamental architectural difference between HOTP (RFC 4226) and TOTP (RFC 6238)?",
            targetPrompt = "Select the difference between HOTP and TOTP",
            options = listOf(
                "HOTP derives codes from an incrementing event counter; TOTP derives codes from the current Unix timestamp",
                "HOTP uses RSA public keys; TOTP uses symmetric AES keys",
                "HOTP produces 8-digit codes; TOTP produces 32-character hex codes",
                "HOTP requires an active cellular connection; TOTP works over Bluetooth"
            ),
            correctOptionIndex = 0,
            conceptTag = "hotp_vs_totp_foundation",
            misconceptionTag = "confusing_counter_with_asymmetric",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: HOTP = HMAC(K, Counter). TOTP = HMAC(K, floor(UnixTime / 30))!",
            hint1 = "H in HOTP stands for HMAC with an event counter.",
            hint2 = "T in TOTP stands for Time-based.",
            hint3 = "Counter vs current time.",
            explanation = "HOTP increments an event counter C on each button press. TOTP is an extension of HOTP where the counter is dynamically computed as C = floor(current_unix_time / time_step), typically using a 30-second window.",
            wrongOptionExplanations = mapOf(
                1 to "Both HOTP and TOTP use symmetric shared secrets with HMAC, not RSA.",
                2 to "Both HOTP and TOTP typically produce 6-digit or 8-digit decimal codes.",
                3 to "Neither algorithm requires cellular or Bluetooth; they compute codes offline."
            )
        ),

        // Q2 (FILL_CODE)
        QuizQuestion(
            id = "c6_l2_q2",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "TOTP Time Step Calculation",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Complete the Python calculation deriving the 30-second TOTP counter from the Unix epoch timestamp:",
            targetPrompt = "Tap the timestamp variable",
            fillCodeTemplate = "# TOTP time-step counter derivation (RFC 6238)\ntime_step = 30\ncounter = int([___] // time_step)",
            fillCodeWordBank = listOf("current_unix_time", "user_pin", "salt_bytes", "session_id"),
            fillCodeCorrectToken = "current_unix_time",
            conceptTag = "totp_time_counter_formula",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: TOTP counter = floor(Unix Time / 30)!",
            hint1 = "Unix time is the number of seconds elapsed since Jan 1, 1970.",
            hint2 = "Dividing Unix time by 30 gives the current 30-second bucket.",
            hint3 = "current_unix_time.",
            explanation = "TOTP calculates the counter C = floor(current_unix_time / 30). Because both the phone and server share accurate UTC clocks, they compute the identical counter value without communicating."
        ),

        // Q3 (MATCH_PAIRS)
        QuizQuestion(
            id = "c6_l2_q3",
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "OTP Specification Matcher",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Connect each one-time password standard to its technical specification:",
            targetPrompt = "Match RFC standard to property",
            matchLeft = listOf(
                "RFC 4226 Standard",
                "RFC 6238 Standard",
                "Standard 30 Seconds",
                "Base32 Shared Secret"
            ),
            matchRight = listOf(
                "HOTP (Counter-based)",
                "TOTP (Time-based)",
                "Default Time Step Window (X)",
                "QR Code Seed stored on Phone"
            ),
            matchSolution = mapOf(
                "RFC 4226 Standard" to "HOTP (Counter-based)",
                "RFC 6238 Standard" to "TOTP (Time-based)",
                "Standard 30 Seconds" to "Default Time Step Window (X)",
                "Base32 Shared Secret" to "QR Code Seed stored on Phone"
            ),
            conceptTag = "otp_rfc_specifications",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: RFC 4226 = HOTP. RFC 6238 = TOTP. QR Code contains the Base32 seed!",
            hint1 = "RFC 4226 defined the original counter-based HOTP.",
            hint2 = "RFC 6238 adapted it to timestamps.",
            hint3 = "The QR code displays the Base32 pre-shared secret key.",
            explanation = "RFC 4226 defines HOTP using HMAC-SHA1. RFC 6238 extends it to TOTP using 30-second time steps. The authenticator app setup QR code conveys the pre-shared secret formatted in Base32."
        ),

        // Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c6_l2_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "TOTP Offline Generation",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Examine this smartphone authenticator capability:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "Authenticator apps (like Google Authenticator or 1Password) require an active internet connection to generate valid TOTP codes.",
            trueFalseIsCorrectTrue = false,
            conceptTag = "totp_offline_generation_fact",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: FALSE! TOTP is completely offline! The app only reads its internal clock and computes HMAC locally!",
            hint1 = "Does your phone need internet to know the current time?",
            hint2 = "No. The phone and server calculate the hash independently using their local clocks.",
            hint3 = "TOTP works in airplane mode. The statement is False.",
            explanation = "TOTP generation is completely offline. The authenticator app simply reads the local device time, calculates C = floor(time / 30), and runs HMAC-SHA1 with the saved secret key."
        ),

        // Q5 (ORDER_STEPS)
        QuizQuestion(
            id = "c6_l2_q5",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Dynamic Truncation Pipeline",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Arrange the RFC 4226 Dynamic Truncation steps that convert a 20-byte HMAC digest into a 6-digit code:",
            targetPrompt = "Order the truncation steps",
            orderStepsInitial = listOf(
                "Compute modulo 1,000,000 to produce final 6-digit decimal code",
                "Extract 4-bit offset from the low nibble of the 20th byte",
                "Generate 20-byte HMAC-SHA1(K, Counter) digest",
                "Read 4 bytes at that offset into a 31-bit positive integer"
            ),
            orderStepsSolution = listOf(
                "Generate 20-byte HMAC-SHA1(K, Counter) digest",
                "Extract 4-bit offset from the low nibble of the 20th byte",
                "Read 4 bytes at that offset into a 31-bit positive integer",
                "Compute modulo 1,000,000 to produce final 6-digit decimal code"
            ),
            conceptTag = "rfc4226_dynamic_truncation",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: 1. HMAC digest ➔ 2. 4-bit offset ➔ 3. 31-bit integer ➔ 4. Modulo 1,000,000!",
            hint1 = "First compute the raw 20-byte HMAC digest.",
            hint2 = "Use the last byte's lowest 4 bits as an index offset (0 to 15).",
            hint3 = "Extract 4 bytes starting at that offset, zero out the MSB (31 bits), and take modulo 10^6.",
            explanation = "Dynamic Truncation: 1) Compute HMAC-SHA1(K, C). 2) Extract offset = digest[19] & 0x0F. 3) Extract 4 bytes from digest[offset] & 0x7FFFFFFF into a 31-bit integer. 4) Result = integer % 1,000,000."
        ),

        // Q6 (COMPLEXITY_DIAL)
        QuizQuestion(
            id = "c6_l2_q6",
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "6-Digit Code Space",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "A 6-digit decimal code has exactly 1,000,000 possible values (000000 to 999999). What is the time complexity of a brute-force search across this fixed keyspace?",
            targetPrompt = "Set keyspace search complexity",
            complexityDialCorrect = "O(1)",
            conceptTag = "fixed_code_keyspace_o1",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: A fixed 6-digit space (10^6) is a constant upper bound O(1), which is why rate-limiting is essential!",
            hint1 = "The keyspace does not grow with input length; it is fixed at 1,000,000.",
            hint2 = "A fixed constant upper bound is represented in Big-O notation.",
            hint3 = "Constant time is O(1).",
            explanation = "Because a 6-digit code has a fixed domain of 10^6 combinations (independent of message length n), exhaustive testing is mathematically bounded by a constant O(1). This makes account rate-limiting and lockout mandatory."
        ),

        // Q7 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l2_q7",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Handling Clock Drift",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "How do production TOTP authentication servers accommodate minor clock skew between a user's smartphone and server time?",
            targetPrompt = "Select the clock drift tolerance strategy",
            options = listOf(
                "The server checks the current time step T, as well as adjacent steps T-1 and T+1 (±30s window)",
                "The server forces the user's phone to reboot its GPS chip",
                "The server disables 2FA for any user with more than 1 second of drift",
                "The server accepts any code generated within the last 24 hours"
            ),
            correctOptionIndex = 0,
            conceptTag = "totp_clock_drift_window",
            misconceptionTag = "zero_tolerance_drift",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: RFC 6238 recommends checking T-1, T, and T+1 to handle minor time lag!",
            hint1 = "Device clocks can be a few seconds fast or slow.",
            hint2 = "Checking adjacent time steps prevents rejecting users who just clicked submit.",
            hint3 = "Check T-1 and T+1.",
            explanation = "RFC 6238 section 5.2 recommends a transmission tolerance window checking the previous time step (T-1) and next time step (T+1), providing a 90-second total window to absorb network lag and clock drift."
        ),

        // Q8 (FILL_CODE)
        QuizQuestion(
            id = "c6_l2_q8",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "6-Digit Modulo Reduction",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Fill in the modulo divisor that reduces a 31-bit integer into a 6-digit decimal code (000000 to 999999):",
            targetPrompt = "Tap the 10^6 modulo divisor",
            fillCodeTemplate = "# Extracting exactly 6 decimal digits\nsix_digit_code = truncated_int % [___]",
            fillCodeWordBank = listOf("1_000_000", "100_000", "10_000_000", "256"),
            fillCodeCorrectToken = "1_000_000",
            conceptTag = "hotp_modulo_10_6",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: 10^6 = 1,000,000 produces remainders from 0 to 999,999 (6 digits)!",
            hint1 = "10 to the 6th power.",
            hint2 = "1 with 6 zeros: 1,000,000.",
            hint3 = "1_000_000.",
            explanation = "Taking modulo 10^6 (1,000,000) restricts the integer to the range [0, 999999]. If the resulting number has fewer than 6 digits, it is left-padded with zeros (e.g. 42 becomes '000042')."
        ),

        // Q9 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c6_l2_q9",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "SMS 2FA Vulnerability",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Fact check this telecom security warning:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "SMS-based one-time authentication codes are significantly more vulnerable to interception than app-based TOTP codes due to SIM-swapping and SS7 network flaws.",
            trueFalseIsCorrectTrue = true,
            conceptTag = "sms_2fa_threats",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: TRUE! Attackers bribe or trick telco staff to transfer your phone number to their SIM card (SIM swapping)!",
            hint1 = "Can telecom carrier employees redirect SMS messages?",
            hint2 = "Yes, via SIM swapping or SS7 signaling exploits.",
            hint3 = "NIST deprecated SMS as a secure authenticator factor. True!",
            explanation = "NIST SP 800-63B warns against SMS 2FA. SMS messages travel unencrypted across carrier networks, are vulnerable to SS7 signaling interception, and can be hijacked via SIM-swap social engineering attacks against mobile carriers."
        ),

        // Q10 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l2_q10",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "MFA Fatigue / Push Bombing",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is an 'MFA Fatigue' (or Push Bombing) attack?",
            targetPrompt = "Select the MFA Fatigue description",
            options = listOf(
                "An attacker triggers dozens of mobile push approval prompts in rapid succession until the victim approves one out of annoyance",
                "A virus that drains the smartphone battery so MFA codes cannot be generated",
                "A mathematical attack that cracks the TOTP secret key using quantum computers",
                "A bug where authenticator apps delete the user's secret keys after 30 days"
            ),
            correctOptionIndex = 0,
            conceptTag = "mfa_fatigue_attack",
            misconceptionTag = "confusing_fatigue_with_cryptanalysis",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: High-profile breaches (like Uber 2022) happened when attackers repeatedly spammed push alerts until the user clicked 'Approve'!",
            hint1 = "It relies on human psychological wear-down.",
            hint2 = "The attacker has the password and spams notification prompts.",
            hint3 = "Flooding push prompts until approval.",
            explanation = "In MFA Fatigue attacks, an adversary who already has the user's password repeatedly triggers push authorization requests at late hours until the exhausted or confused victim taps 'Approve' to stop the buzzes."
        )
    )

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 28: TICKET BOOTH (KERBEROS & TGT) (c6_l3)
    // ══════════════════════════════════════════════════════════════════
    private fun level28Questions(): List<QuizQuestion> = listOf(
        // Q1 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l3_q1",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Kerberos Protocol Mission",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "What is the primary objective of the Kerberos network authentication protocol?",
            targetPrompt = "Select the core mission of Kerberos",
            options = listOf(
                "To enable secure Single Sign-On (SSO) authentication across an untrusted network using symmetric tickets, without sending passwords over the wire",
                "To encrypt hard drives using full-disk encryption",
                "To provide public Wi-Fi hotspot captive portals",
                "To convert SQL queries into NoSQL documents"
            ),
            correctOptionIndex = 0,
            conceptTag = "kerberos_core_mission",
            misconceptionTag = "confusing_kerberos_with_disk_enc",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Kerberos lets you log in ONCE and get tickets to access mail, file shares, and intranet servers without re-entering passwords!",
            hint1 = "Named after the three-headed dog guarding the gates.",
            hint2 = "It is an enterprise SSO authentication protocol.",
            hint3 = "SSO authentication using symmetric tickets without sending passwords.",
            explanation = "Kerberos (RFC 4120) provides mutual authentication and Single Sign-On (SSO). Clients authenticate once to a trusted Key Distribution Center (KDC) and receive cryptographic tickets to access enterprise services without sending passwords."
        ),

        // Q2 (FILL_CODE)
        QuizQuestion(
            id = "c6_l3_q2",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "KDC Dual Components",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "A Kerberos Key Distribution Center (KDC) comprises the Authentication Server (AS) and the:",
            targetPrompt = "Tap the KDC service token",
            fillCodeTemplate = "# The two sub-services of a Kerberos KDC:\n# 1. Authentication Server (AS)\n# 2. [___] (TGS)\nkdc_second_half = 'TGS'",
            fillCodeWordBank = listOf("Ticket_Granting_Service", "Domain_Name_Service", "Certificate_Authority", "Proxy_Server"),
            fillCodeCorrectToken = "Ticket_Granting_Service",
            conceptTag = "kdc_dual_services",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: KDC = AS (Authentication Server) + TGS (Ticket Granting Service)!",
            hint1 = "TGS stands for Ticket Granting...",
            hint2 = "It issues tickets for specific application services.",
            hint3 = "Ticket_Granting_Service.",
            explanation = "The KDC consists of two components: 1) The Authentication Server (AS), which validates user identity and issues the Ticket Granting Ticket (TGT); and 2) The Ticket Granting Service (TGS), which issues service tickets."
        ),

        // Q3 (MATCH_PAIRS)
        QuizQuestion(
            id = "c6_l3_q3",
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Kerberos Vocabulary",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Connect each Kerberos term to its exact operational definition:",
            targetPrompt = "Match Kerberos entity to definition",
            matchLeft = listOf(
                "KDC",
                "TGT",
                "TGS",
                "Principal"
            ),
            matchRight = listOf(
                "Key Distribution Center (Central Authority)",
                "Ticket Granting Ticket (Admission pass to TGS)",
                "Ticket Granting Service (Issues service tickets)",
                "Unique identity (user or service e.g. user@REALM)"
            ),
            matchSolution = mapOf(
                "KDC" to "Key Distribution Center (Central Authority)",
                "TGT" to "Ticket Granting Ticket (Admission pass to TGS)",
                "TGS" to "Ticket Granting Service (Issues service tickets)",
                "Principal" to "Unique identity (user or service e.g. user@REALM)"
            ),
            conceptTag = "kerberos_entity_roles",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Principal = Identity. TGT = Admission pass. TGS = Booth. KDC = Castle!",
            hint1 = "KDC is the trusted third party authority.",
            hint2 = "TGT is the ticket you show to the ticket granting service.",
            hint3 = "Principal is the unique name of a user or service.",
            explanation = "In Kerberos architecture: A Principal is a unique identity; the KDC houses authentication databases; the TGT proves initial login; and the TGS issues service tickets for specific network shares."
        ),

        // Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c6_l3_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Password Exposure Over Wire",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Validate this network transmission security claim:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "In the Kerberos protocol, a user's plaintext password is transmitted across the local network to the KDC during initial authentication.",
            trueFalseIsCorrectTrue = false,
            conceptTag = "kerberos_zero_password_transmission",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: FALSE! The user's password NEVER travels across the wire! The client proves possession of the password locally by decrypting a response!",
            hint1 = "Kerberos was designed specifically for insecure networks.",
            hint2 = "The client derives a symmetric key from the password to decrypt the AS reply.",
            hint3 = "Passwords are never sent over the wire. False!",
            explanation = "Kerberos never sends passwords over the network. The user's password is used locally on their machine to derive a symmetric key (via string2key) to decrypt the Authentication Server's response."
        ),

        // Q5 (ORDER_STEPS)
        QuizQuestion(
            id = "c6_l3_q5",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Kerberos 6-Message Dance",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Arrange the full 6-step message sequence for Kerberos authentication and service access:",
            targetPrompt = "Order the Kerberos exchange steps",
            orderStepsInitial = listOf(
                "Client presents Service Ticket to Application Server (AP-REQ / AP-REP)",
                "Client sends AS-REQ to KDC Authentication Server",
                "Client sends TGS-REQ with TGT to Ticket Granting Service",
                "KDC returns AS-REP containing TGT and Session Key"
            ),
            orderStepsSolution = listOf(
                "Client sends AS-REQ to KDC Authentication Server",
                "KDC returns AS-REP containing TGT and Session Key",
                "Client sends TGS-REQ with TGT to Ticket Granting Service",
                "Client presents Service Ticket to Application Server (AP-REQ / AP-REP)"
            ),
            conceptTag = "kerberos_message_exchange_sequence",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: 1. AS-REQ ➔ 2. AS-REP (TGT) ➔ 3. TGS-REQ (Service Request) ➔ 4. AP-REQ (App Server)!",
            hint1 = "Start with AS-REQ requesting initial login.",
            hint2 = "AS returns TGT.",
            hint3 = "Client takes TGT to TGS, gets Service Ticket, and presents it to App Server.",
            explanation = "Kerberos flow: 1) AS-REQ (login request), 2) AS-REP (returns TGT encrypted with KRBTGT key), 3) TGS-REQ (presents TGT to request service), 4) TGS-REP (returns Service Ticket), 5-6) AP-REQ / AP-REP (mutual authentication with application server)."
        ),

        // Q6 (COMPLEXITY_DIAL)
        QuizQuestion(
            id = "c6_l3_q6",
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "SSO Ticket Scaling",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "After initial login and obtaining a TGT, how many full password verifications are required to access N different enterprise services throughout the workday?",
            targetPrompt = "Set password prompt count dial",
            complexityDialCorrect = "O(1)",
            conceptTag = "sso_ticket_scaling_constant",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: ZERO additional password prompts! The user enters their password exactly once O(1) in Single Sign-On!",
            hint1 = "Single Sign-On means logging in once.",
            hint2 = "Accessing 10 or 100 services reuses the TGT without asking for passwords again.",
            hint3 = "Constant O(1) password entries.",
            explanation = "The core value of Single Sign-On (SSO) in Kerberos is that full password entry is performed exactly once O(1) at the start of the day. Subsequent service access requests are transparently fulfilled using the TGT."
        ),

        // Q7 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l3_q7",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Kerberos Timestamp Requirement",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Why does Kerberos strictly require synchronized clocks (typically within 5 minutes via NTP) across all network machines?",
            targetPrompt = "Select the clock synchronization rationale",
            options = listOf(
                "Tickets and authenticators contain timestamps to detect and prevent replay attacks; expired timestamps are rejected",
                "Because symmetric encryption cannot function if clocks differ",
                "To ensure backup log files are printed in chronological order",
                "To calculate billing charges for employee internet usage"
            ),
            correctOptionIndex = 0,
            conceptTag = "kerberos_ntp_replay_defense",
            misconceptionTag = "confusing_replay_with_crypto_failure",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: If an eavesdropper captures your ticket on the wire, they cannot replay it later because the timestamp will have expired!",
            hint1 = "Replay attacks resend captured valid packets.",
            hint2 = "Short-lived timestamps make captured tickets useless after 5 minutes.",
            hint3 = "Detect and prevent replay attacks.",
            explanation = "Kerberos authenticators include a current timestamp encrypted with the session key. The server compares this against its local clock. If the skew exceeds 5 minutes, the ticket is rejected, thwarting replay attacks."
        ),

        // Q8 (FILL_CODE)
        QuizQuestion(
            id = "c6_l3_q8",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Golden Ticket Vulnerability",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "In Active Directory / Kerberos, which master account key must an attacker compromise to forge arbitrary Golden Tickets?",
            targetPrompt = "Tap the Kerberos master account token",
            fillCodeTemplate = "# The master service account key that encrypts all TGTs\nvulnerable_account = '[___]'",
            fillCodeWordBank = listOf("KRBTGT", "GUEST", "LOCAL_ADMIN", "SYSTEM"),
            fillCodeCorrectToken = "KRBTGT",
            conceptTag = "golden_ticket_krbtgt_compromise",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: The 'KRBTGT' account password hash encrypts every Ticket Granting Ticket in the domain!",
            hint1 = "It is the internal Kerberos Ticket Granting service account.",
            hint2 = "Abbreviation of Kerberos Ticket Granting.",
            hint3 = "KRBTGT.",
            explanation = "The KRBTGT account's NTLM/AES key encrypts all TGTs issued by the domain controller. If an attacker extracts this key (a 'Golden Ticket' attack), they can forge their own valid TGTs with arbitrary group memberships and infinite validity."
        ),

        // Q9 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c6_l3_q9",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "TGT Confidentiality",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Examine how Ticket Granting Tickets protect their internal contents:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "A Ticket Granting Ticket (TGT) is encrypted with the client user's password key so that the client can inspect and edit the ticket contents.",
            trueFalseIsCorrectTrue = false,
            conceptTag = "tgt_encrypted_with_kdc_key",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: FALSE! The TGT is encrypted with the KDC'S OWN SECRET KEY (KRBTGT)! The client is just a courier and cannot read or edit it!",
            hint1 = "Can a client tamper with their own ticket permissions?",
            hint2 = "No! The ticket is an opaque blob encrypted with the KDC's secret key.",
            hint3 = "The client cannot decrypt the TGT. The statement is False.",
            explanation = "The TGT is encrypted with the KDC's private key (the KRBTGT secret). The client machine holds the TGT as an opaque binary blob and cannot read or alter its privileges without invalidating the encryption."
        ),

        // Q10 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l3_q10",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Kerberoasting Attack",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "How does the 'Kerberoasting' attack work against enterprise networks?",
            targetPrompt = "Select the Kerberoasting attack mechanism",
            options = listOf(
                "Any authenticated user requests service tickets for accounts with Service Principal Names (SPNs), then cracks the service password hashes offline",
                "An attacker floods the domain controller with SYN packets to crash the KDC",
                "An attacker physically unplugs network switches in the server room",
                "An attacker modifies the local hosts file to point google.com to a rogue server"
            ),
            correctOptionIndex = 0,
            conceptTag = "kerberoasting_mechanism",
            misconceptionTag = "confusing_kerberoasting_with_dos",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: Any domain user can legally request service tickets! Because service tickets are encrypted with the service account's password, attackers crack them offline with Hashcat!",
            hint1 = "It requires valid domain user credentials.",
            hint2 = "Service tickets are encrypted using the password hash of the service account.",
            hint3 = "Offline cracking of service account hashes.",
            explanation = "Kerberoasting allows any valid domain user to request a service ticket (TGS-REP) for any registered Service Principal Name (SPN). Because the ticket is encrypted with the service account's NTLM hash, the attacker extracts the ticket and cracks it offline with tools like Hashcat without generating network alerts."
        )
    )

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 29: KEY LIFECYCLE & KMS (c6_l4)
    // ══════════════════════════════════════════════════════════════════
    private fun level29Questions(): List<QuizQuestion> = listOf(
        // Q1 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l4_q1",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Envelope Encryption Concept",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "What is the core architecture of 'Envelope Encryption' used by cloud KMS (AWS KMS, Google Cloud KMS, HashiCorp Vault)?",
            targetPrompt = "Select the envelope encryption workflow",
            options = listOf(
                "Plaintext data is encrypted locally with a Data Encryption Key (DEK); the DEK is encrypted with a master Key Encryption Key (KEK) managed in an HSM",
                "Encrypting data twice with the same AES key",
                "Placing encrypted hard drives inside a physical metal envelope",
                "Sending all raw multi-gigabyte database files directly over the internet to KMS for encryption"
            ),
            correctOptionIndex = 0,
            conceptTag = "envelope_encryption_architecture",
            misconceptionTag = "sending_payload_to_kms",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Data is encrypted with a local DEK. The DEK is encrypted with a master KEK. Two layers like an envelope!",
            hint1 = "DEK encrypts data. KEK encrypts DEK.",
            hint2 = "The massive file never leaves your server; only the small 32-byte key is sent to KMS.",
            hint3 = "DEK encrypts data; KEK encrypts the DEK.",
            explanation = "In envelope encryption, heavy data is encrypted locally using a unique Data Encryption Key (DEK). The DEK is then encrypted (wrapped) by a master Key Encryption Key (KEK) protected inside the KMS HSM. This avoids sending gigabytes of payload data across network boundaries to KMS."
        ),

        // Q2 (FILL_CODE)
        QuizQuestion(
            id = "c6_l4_q2",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "DEK Unwrapping Call",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "To decrypt a file protected by envelope encryption, what parameter is sent to KMS to recover the plaintext data key?",
            targetPrompt = "Tap the encrypted DEK variable",
            fillCodeTemplate = "# Decrypting the wrapped data key with KMS\nplaintext_dek = kms_client.decrypt(ciphertext_key=[___])",
            fillCodeWordBank = listOf("encrypted_dek", "raw_file_payload", "public_cert", "user_password"),
            fillCodeCorrectToken = "encrypted_dek",
            conceptTag = "dek_unwrapping_decryption",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Send only the encrypted DEK to KMS. KMS decrypts it and returns the plaintext DEK!",
            hint1 = "You do NOT send the raw file payload to KMS.",
            hint2 = "You send the wrapped key that was stored alongside the file.",
            hint3 = "encrypted_dek.",
            explanation = "To read data, the application sends only the encrypted DEK to KMS. KMS validates access permissions, decrypts the DEK using the master KEK inside the HSM, and returns the plaintext DEK in memory for local file decryption."
        ),

        // Q3 (MATCH_PAIRS)
        QuizQuestion(
            id = "c6_l4_q3",
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "KMS Terminology",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Connect each key management primitive to its precise security role:",
            targetPrompt = "Match KMS term to function",
            matchLeft = listOf(
                "HSM",
                "DEK",
                "KEK",
                "Key Rotation"
            ),
            matchRight = listOf(
                "Tamper-resistant physical crypto hardware",
                "Symmetric key that encrypts actual data payload",
                "Master key that encrypts Data Encryption Keys",
                "Retiring active key and generating new version"
            ),
            matchSolution = mapOf(
                "HSM" to "Tamper-resistant physical crypto hardware",
                "DEK" to "Symmetric key that encrypts actual data payload",
                "KEK" to "Master key that encrypts Data Encryption Keys",
                "Key Rotation" to "Retiring active key and generating new version"
            ),
            conceptTag = "kms_component_roles",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: HSM = Vault. DEK = File key. KEK = Key that locks the DEK. Rotation = Replacing keys!",
            hint1 = "HSM is a dedicated physical hardware appliance.",
            hint2 = "DEK is the Data Encryption Key.",
            hint3 = "KEK is the Key Encryption Key.",
            explanation = "HSM provides physical tamper resistance and key non-exportability. DEK encrypts data. KEK wraps the DEK. Key rotation limits cryptoperiod exposure by creating new key versions on a regular schedule."
        ),

        // Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c6_l4_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "KEK HSM Isolation",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Validate this fundamental security assurance of cloud KMS:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "In cloud KMS architectures, the root Key Encryption Key (KEK) is never exported outside the cryptographic boundary of the Hardware Security Module (HSM).",
            trueFalseIsCorrectTrue = true,
            conceptTag = "kek_hsm_boundary_isolation",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: TRUE! The master key CANNOT leave the HSM. All cryptographic operations happen inside the physical hardware!",
            hint1 = "HSMs are designed with zero-export firmware.",
            hint2 = "Even cloud provider administrators cannot view or dump the master KEK.",
            hint3 = "The KEK never leaves the HSM boundary. True!",
            explanation = "Hardware Security Modules (HSMs) enforce non-exportability: the plaintext master KEK resides strictly in volatile HSM registers and cannot be read, exported, or extracted by software APIs or cloud administrators."
        ),

        // Q5 (ORDER_STEPS)
        QuizQuestion(
            id = "c6_l4_q5",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Envelope Encryption Write Pipeline",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Arrange the 5-step sequence for encrypting and persisting a large database backup using envelope encryption:",
            targetPrompt = "Order envelope encryption steps",
            orderStepsInitial = listOf(
                "Securely zeroize (wipe) the Plaintext DEK from application RAM",
                "Encrypt the database backup locally with the Plaintext DEK",
                "Persist the encrypted database alongside the Encrypted DEK on storage",
                "Call KMS GenerateDataKey to receive Plaintext DEK and Encrypted DEK"
            ),
            orderStepsSolution = listOf(
                "Call KMS GenerateDataKey to receive Plaintext DEK and Encrypted DEK",
                "Encrypt the database backup locally with the Plaintext DEK",
                "Securely zeroize (wipe) the Plaintext DEK from application RAM",
                "Persist the encrypted database alongside the Encrypted DEK on storage"
            ),
            conceptTag = "envelope_encryption_write_flow",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: 1. Generate DEK ➔ 2. Encrypt Data locally ➔ 3. Wipe Plaintext DEK from RAM ➔ 4. Save Encrypted Data + Encrypted DEK!",
            hint1 = "First ask KMS for a data key pair (plaintext + encrypted).",
            hint2 = "Encrypt the large file locally.",
            hint3 = "Zeroize the plaintext key from memory immediately, then store the ciphertext file and encrypted key.",
            explanation = "Envelope encryption pipeline: 1) KMS generates and returns a plaintext DEK and its encrypted version. 2) Local data is encrypted with the plaintext DEK. 3) The plaintext DEK is overwritten in RAM. 4) The encrypted DEK is stored alongside the ciphertext."
        ),

        // Q6 (COMPLEXITY_DIAL)
        QuizQuestion(
            id = "c6_l4_q6",
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "KMS Network Call Scaling",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "When encrypting a 50 GB database file, how many network round-trips to the cloud KMS service are required using envelope encryption?",
            targetPrompt = "Set KMS network call complexity",
            complexityDialCorrect = "O(1)",
            conceptTag = "envelope_encryption_o1_network",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Exactly ONE KMS API call! The 50 GB file is encrypted locally without streaming across the internet!",
            hint1 = "You only call KMS once to obtain the 32-byte DEK.",
            hint2 = "The 50 GB data is processed entirely in local memory/disk.",
            hint3 = "A single API call is O(1).",
            explanation = "Envelope encryption requires exactly one KMS network call O(1) to generate the DEK. The entire 50 GB payload is encrypted locally at memory bandwidth speeds, eliminating multi-gigabyte network transfer bottlenecks."
        ),

        // Q7 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l4_q7",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Shamir's Secret Sharing",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "What is Shamir's Secret Sharing (k-of-n threshold scheme) used for in master key ceremonies?",
            targetPrompt = "Select the Shamir secret sharing definition",
            options = listOf(
                "A mathematical algorithm splitting a master key into n shares such that any k shares reconstruct the key, while k-1 shares reveal zero information",
                "Encrypting a password with k different AES keys in a loop",
                "Storing k backup copies of an unencrypted private key on USB drives",
                "A protocol requiring users to change passwords every k days"
            ),
            correctOptionIndex = 0,
            conceptTag = "shamir_secret_sharing_threshold",
            misconceptionTag = "confusing_secret_sharing_with_backup_copies",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: Like polynomial interpolation! Any k points define a polynomial of degree k-1, but k-1 points reveal nothing!",
            hint1 = "Named after cryptographer Adi Shamir (the 'S' in RSA).",
            hint2 = "Split knowledge prevents any single executive or admin from having unilateral key access.",
            hint3 = "Any k of n shares reconstruct the secret.",
            explanation = "Shamir's Secret Sharing uses polynomial interpolation over finite fields. A secret is encoded as the y-intercept of a random polynomial of degree k-1. Any k points (shares) uniquely recover the polynomial, whereas k-1 points yield zero information."
        ),

        // Q8 (FILL_CODE)
        QuizQuestion(
            id = "c6_l4_q8",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Cryptoperiod and Key Rotation",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "The defined time span during which a cryptographic key is authorized for active encryption before mandatory retirement is its:",
            targetPrompt = "Tap the key policy token",
            fillCodeTemplate = "# NIST SP 800-57 key lifespan policy\nkey_lifecycle_span = '[___]'",
            fillCodeWordBank = listOf("Cryptoperiod", "Bandwidth", "Clock_Drift", "Epoch"),
            fillCodeCorrectToken = "Cryptoperiod",
            conceptTag = "cryptoperiod_definition",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: NIST SP 800-57 defines a 'Cryptoperiod' as the time span a key is authorized for use!",
            hint1 = "Crypto + Period.",
            hint2 = "The period during which the key is valid.",
            hint3 = "Cryptoperiod.",
            explanation = "A cryptoperiod is the time span during which a specific key is authorized for use. Limiting the cryptoperiod limits the volume of data exposed if a key is compromised and reduces the time an attacker has for cryptanalysis."
        ),

        // Q9 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c6_l4_q9",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Automatic Key Rotation Behavior",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "Examine this cloud KMS key rotation behavior:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "When a KMS master key is rotated to version 2, all files previously encrypted under version 1 immediately become unreadable until manually re-encrypted.",
            trueFalseIsCorrectTrue = false,
            conceptTag = "kms_multi_version_decryption",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: FALSE! KMS retains older key versions specifically to decrypt historical data! Only NEW encryptions use Version 2!",
            hint1 = "Does rotating a key break existing backups?",
            hint2 = "No! The KMS preserves the historical key version for read/decrypt access.",
            hint3 = "Old data remains readable. The statement is False.",
            explanation = "In cloud KMS, automatic key rotation creates a new key version for subsequent encryption operations while retaining previous key versions indefinitely to seamlessly decrypt historical data."
        ),

        // Q10 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_l4_q10",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Hardware Security Module (HSM) Guarantees",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What unique physical and cryptographic guarantees does a certified HSM (FIPS 140-2/3 Level 3/4) provide over storing keys on a server hard drive?",
            targetPrompt = "Select the HSM hardware guarantees",
            options = listOf(
                "Physical tamper resistance, zero plaintext export, zeroization of keys upon physical chassis intrusion, and hardware-accelerated CSPRNG",
                "Unlimited free cloud storage for backup images",
                "Faster Wi-Fi and Bluetooth download speeds",
                "Automatic password generation for all office employees"
            ),
            correctOptionIndex = 0,
            conceptTag = "hsm_fips140_guarantees",
            misconceptionTag = "confusing_hsm_with_cloud_storage",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: An HSM physically destroys its internal keys (zeroization) if someone tries to drill into its casing or probe circuit voltages!",
            hint1 = "FIPS 140-2 Level 3/4 mandates physical tamper response.",
            hint2 = "Keys can never leave the hardware chip in plaintext.",
            hint3 = "Physical tamper resistance and zeroization.",
            explanation = "FIPS 140-2/3 Level 3 and 4 certified HSMs feature epoxy resin coating, voltage/temperature intrusion sensors, and active tamper response that zeroes out (destroys) private keys if an attacker physically opens or probes the casing."
        )
    )

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 30: BOSS: THE GATEKEEPER (OAUTH & OIDC) (c6_boss)
    // ══════════════════════════════════════════════════════════════════
    private fun level30BossQuestions(): List<QuizQuestion> = listOf(
        // Boss Q1 (FILL_CODE)
        QuizQuestion(
            id = "c6_boss_q1",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Boss: PKCE Code Challenge",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "⚔️ BOSS TRIAL PHASE 1: In the OAuth 2.0 PKCE flow for mobile apps, the client creates a high-entropy secret and derives the challenge using SHA-256:",
            targetPrompt = "Tap the PKCE verifier variable",
            fillCodeTemplate = "# PKCE Code Challenge derivation (RFC 7636)\ncode_verifier = generate_random_bytes(32)\ncode_challenge = base64_url(sha256([___]))",
            fillCodeWordBank = listOf("code_verifier", "access_token", "client_secret", "client_id"),
            fillCodeCorrectToken = "code_verifier",
            conceptTag = "boss_pkce_challenge_derivation",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: The challenge is the SHA-256 hash of the secret code_verifier! The server checks this when the app exchanges the authorization code!",
            hint1 = "The client keeps the verifier secret until the token exchange step.",
            hint2 = "The challenge is derived directly from the verifier.",
            hint3 = "sha256(code_verifier).",
            explanation = "RFC 7636 (PKCE): The mobile app creates a cryptographically random 'code_verifier'. It computes 'code_challenge = Base64URL(SHA256(code_verifier))'. When exchanging the authorization code, sending the code_verifier proves the app is the legitimate initiator."
        ),

        // Boss Q2 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_boss_q2",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss: OAuth vs OpenID Connect",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "⚔️ BOSS TRIAL PHASE 2: What is the fundamental difference between OAuth 2.0 and OpenID Connect (OIDC)?",
            targetPrompt = "Select the fundamental difference",
            options = listOf(
                "OAuth 2.0 is an AUTHORIZATION framework for scoped API access; OIDC is an IDENTITY layer on top of OAuth 2.0 that provides user authentication via signed ID tokens",
                "OAuth 2.0 is for mobile apps; OIDC is only for mainframe computers",
                "OAuth 2.0 uses passwords; OIDC only uses biometrics",
                "They are conflicting standards that cannot be used together"
            ),
            correctOptionIndex = 0,
            conceptTag = "boss_oauth_vs_oidc_distinction",
            misconceptionTag = "confusing_oauth_authz_with_authn",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: OAuth 2.0 gives an API Access Token (Authorization / Keys). OIDC gives an ID Token JWT (Authentication / Passport)!",
            hint1 = "OAuth = delegated authorization (letting an app access your Google Drive files).",
            hint2 = "OIDC = identity authentication (verifying who logged in).",
            hint3 = "Authorization vs Identity layer.",
            explanation = "OAuth 2.0 is an authorization framework designed to grant scoped API access tokens. OpenID Connect (OIDC) extends OAuth 2.0 by introducing the 'id_token' (a signed JSON Web Token), standardizing user identity authentication."
        ),

        // Boss Q3 (ORDER_STEPS)
        QuizQuestion(
            id = "c6_boss_q3",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Boss: Full PKCE Flow",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "⚔️ BOSS TRIAL PHASE 3: Order the complete authorization code exchange flow with PKCE on a mobile client:",
            targetPrompt = "Order the PKCE handshake steps",
            orderStepsInitial = listOf(
                "IdP validates Code Verifier matches Code Challenge, then issues Access Token & ID Token",
                "User authenticates at Identity Provider and consents to requested scopes",
                "App sends Authorization Code + raw Code Verifier to Token Endpoint",
                "App opens browser with Auth Request including Code Challenge",
                "IdP redirects back to App with temporary single-use Authorization Code"
            ),
            orderStepsSolution = listOf(
                "App opens browser with Auth Request including Code Challenge",
                "User authenticates at Identity Provider and consents to requested scopes",
                "IdP redirects back to App with temporary single-use Authorization Code",
                "App sends Authorization Code + raw Code Verifier to Token Endpoint",
                "IdP validates Code Verifier matches Code Challenge, then issues Access Token & ID Token"
            ),
            conceptTag = "boss_pkce_complete_flow",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: 1. Auth Request + Challenge ➔ 2. User Consent ➔ 3. Code redirect ➔ 4. Send Code + Verifier ➔ 5. Token Issued!",
            hint1 = "Initiate with Code Challenge in browser.",
            hint2 = "User signs in; IdP redirects with short-lived Authorization Code.",
            hint3 = "App posts Authorization Code and raw Code Verifier to the token endpoint to receive tokens.",
            explanation = "PKCE flow: 1) App opens browser with Code Challenge. 2) User logs in at IdP. 3) IdP redirects with Authorization Code. 4) App POSTs the Authorization Code + raw Code Verifier. 5) IdP verifies SHA256(verifier) == challenge and issues tokens."
        ),

        // Boss Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c6_boss_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Boss: JWT Encryption Reality",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "⚔️ BOSS TRIAL PHASE 4: Scrutinize this common developer misconception:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "A standard signed JSON Web Token (JWS / JWT) is encrypted by default, meaning third parties cannot read sensitive claims like user email or role by inspecting the token string.",
            trueFalseIsCorrectTrue = false,
            conceptTag = "boss_jwt_signed_vs_encrypted",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: FALSE! Standard JWTs are SIGNED (JWS), NOT ENCRYPTED! Anyone can paste the token into jwt.io and read all claims in plain JSON!",
            hint1 = "Is base64url encoding equivalent to encryption?",
            hint2 = "No! Base64 is easily decoded by anyone.",
            hint3 = "Standard JWTs provide integrity, not confidentiality. The statement is False.",
            explanation = "Standard JSON Web Signatures (JWS) are merely Base64URL-encoded JSON payloads with a cryptographic signature. Anyone can decode and inspect the claims (e.g. at jwt.io). Confidentiality requires JSON Web Encryption (JWE)."
        ),

        // Boss Q5 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c6_boss_q5",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss: Stateless JWT Validation",
            speakerName = "The Gatekeeper",
            speakerImage = "Gatekeeper",
            promptSentence = "⚔️ BOSS TRIAL PHASE 5: How does a microservice API gateway validate incoming stateless JWT access tokens at 100,000 req/sec without querying the central database?",
            targetPrompt = "Select the stateless validation mechanism",
            options = listOf(
                "The gateway verifies the cryptographic signature (e.g. RS256) using the IdP's cached public key (JWKS) and verifies the exp (expiry) and iss (issuer) claims locally",
                "The gateway sends an HTTP request to the central SQL database for every single incoming packet",
                "The gateway compares the token against a giant spreadsheet sent daily over email",
                "The gateway decrypts the token with the user's personal PIN number"
            ),
            correctOptionIndex = 0,
            conceptTag = "boss_stateless_jwt_jwks_validation",
            misconceptionTag = "stateful_database_dependency",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: Asymmetric cryptography! The IdP signs with its private key; any gateway validates with the cached public key from the JWKS endpoint!",
            hint1 = "The IdP signs tokens with an asymmetric private key.",
            hint2 = "API gateways fetch and cache the public keys from /.well-known/jwks.json.",
            hint3 = "Local asymmetric signature verification.",
            explanation = "Stateless JWT validation works via asymmetric cryptography. The Identity Provider signs tokens with its private key. Microservices download and cache the IdP's public keys (JWKS) and verify signatures, expiration (`exp`), and issuer (`iss`) locally in microseconds without database queries."
        )
    )
}
