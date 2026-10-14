package com.simats.codeingo.data.repository

import com.simats.codeingo.data.model.DSAExerciseType
import java.util.UUID

data class QuizQuestion(
    val id: String = UUID.randomUUID().toString(),
    val gameType: DSAExerciseType = DSAExerciseType.MULTIPLE_CHOICE,
    val typeTitle: String,
    val speakerName: String,
    val speakerImage: String,
    val promptSentence: String,
    val targetPrompt: String,
    val options: List<String> = emptyList(),
    val correctOptionIndex: Int = 0,
    val hint: String? = null,
    val matchLeft: List<String> = emptyList(),
    val matchRight: List<String> = emptyList(),
    val matchSolution: Map<String, String> = emptyMap(),
    val orderStepsInitial: List<String> = emptyList(),
    val orderStepsSolution: List<String> = emptyList(),
    val fillCodeTemplate: String = "",
    val fillCodeWordBank: List<String> = emptyList(),
    val fillCodeCorrectToken: String = "",
    val complexityCodeSnippet: String = "",
    val complexityDialCorrect: String = "O(1)",
    val trueFalseStatement: String = "",
    val trueFalseIsCorrectTrue: Boolean = true
)

object QuizQuestionsData {

    fun getQuestionsForUnit(unitId: Int, levelNumber: Int): List<QuizQuestion> {
        val effectiveLevel = if (levelNumber in 1..25) levelNumber else (((levelNumber - 1) % 25) + 1)
        return when (effectiveLevel) {
            1 -> dsaLevel1Questions()
            2 -> dsaLevel2Questions()
            3 -> dsaLevel3Questions()
            4 -> dsaLevel4Questions()
            5 -> dsaLevel5Questions()
            6 -> dsaLevel6Questions()
            7 -> dsaLevel7Questions()
            8 -> dsaLevel8Questions()
            9 -> dsaLevel9Questions()
            10 -> dsaLevel10Questions()
            11 -> dsaLevel11Questions()
            12 -> dsaLevel12Questions()
            13 -> dsaLevel13Questions()
            14 -> dsaLevel14Questions()
            15 -> dsaLevel15Questions()
            16 -> dsaLevel16Questions()
            17 -> dsaLevel17Questions()
            18 -> dsaLevel18Questions()
            19 -> dsaLevel19Questions()
            20 -> dsaLevel20Questions()
            21 -> dsaLevel21Questions()
            22 -> dsaLevel22Questions()
            23 -> dsaLevel23Questions()
            24 -> dsaLevel24Questions()
            else -> dsaLevel25Questions()
        }
    }

    private fun dsaLevel1Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Complete the Caesar cipher encryption formula in Python using modular wrap-around:",
            targetPrompt = "Tap the modulo token",
            hint = "🎮 Phoenix Cheat Code: Modular arithmetic % 26 wraps 'Z' back around to 'A'!",
            fillCodeTemplate = "def caesar_encrypt(char, key):\n    # Shift character code within alphabet ring 0..25\n    return chr((ord(char) - ord('A') + key) [___] 26 + ord('A'))",
            fillCodeWordBank = listOf("%", "/", "//", "**"),
            fillCodeCorrectToken = "%"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match Caesar shift parameters with their cryptographic properties:",
            targetPrompt = "Tap left, then match on right",
            hint = "🎮 Phoenix Cheat Code: ROT13 shifts 13 letters so encrypting twice returns right back to plaintext!",
            matchLeft = listOf("Key k = 3", "Key k = 13", "Brute Force Space"),
            matchRight = listOf("Original Caesar Shift", "ROT13 Self-Inverse", "25 Exhaustive Shifts"),
            matchSolution = mapOf(
                "Key k = 3" to "Original Caesar Shift",
                "Key k = 13" to "ROT13 Self-Inverse",
                "Brute Force Space" to "25 Exhaustive Shifts"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the algorithmic steps to decrypt a Caesar ciphertext letter:",
            targetPrompt = "Arrange into sequence",
            hint = "🎮 Phoenix Cheat Code: Convert char to 0-25 index, subtract shift under modulo 26, then restore char!",
            orderStepsInitial = listOf(
                "Convert index back to char: chr(p + ord('A'))",
                "Convert letter to 0-based index: x = ord(c) - ord('A')",
                "Reverse shift with modulo: p = (x - key + 26) % 26"
            ),
            orderStepsSolution = listOf(
                "Convert letter to 0-based index: x = ord(c) - ord('A')",
                "Reverse shift with modulo: p = (x - key + 26) % 26",
                "Convert index back to char: chr(p + ord('A'))"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: With 26 letters in the alphabet and k=0 being identity, only 25 non-trivial keys exist!",
            trueFalseStatement = "A Caesar cipher can be completely broken by exhaustively testing just 25 possible shift keys.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Analyze the time complexity to brute-force crack a Caesar cipher of text length N:",
            targetPrompt = "Dial the worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: Testing 25 constant shifts across length N is strictly 25 × N = linear O(n)!",
            complexityCodeSnippet = "def brute_force_caesar(ciphertext):\n    for k in range(1, 26): # 25 possible shifts\n        yield decrypt(ciphertext, k) # O(N) scan",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "In modular arithmetic, what is (-3) mod 26 equivalent to in the standard residue range [0, 25]?",
            targetPrompt = "Select the correct residue",
            options = listOf("23", "3", "-3", "20"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Add the modulus 26 until non-negative: -3 + 26 = 23 (letter X)!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match fundamental cryptosystem components to their formal definitions:",
            targetPrompt = "Connect definitions",
            hint = "🎮 Phoenix Cheat Code: M is plaintext, C is ciphertext, and D inverts E under key k!",
            matchLeft = listOf("Plaintext Space M", "Ciphertext Space C", "Decryption Rule D"),
            matchRight = listOf("Original Unencrypted Message", "Encrypted Output Symbols", "D(k, C) = M Inversion"),
            matchSolution = mapOf(
                "Plaintext Space M" to "Original Unencrypted Message",
                "Ciphertext Space C" to "Encrypted Output Symbols",
                "Decryption Rule D" to "D(k, C) = M Inversion"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Slot the operator to reverse the encryption shift in decryption:",
            targetPrompt = "Slot decryption operator",
            hint = "🎮 Phoenix Cheat Code: Encryption adds key (+), so decryption inverts by subtracting (-)!",
            fillCodeTemplate = "def caesar_decrypt(c_idx, key):\n    # Decrypt by subtracting key under modulo 26\n    return (c_idx [___] key + 26) % 26",
            fillCodeWordBank = listOf("-", "+", "*", "^"),
            fillCodeCorrectToken = "-"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: ROT13 uses a fixed shift of 13 with no secret key—anyone can invert it!",
            trueFalseStatement = "ROT13 is considered cryptographically secure against modern computational attacks.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 3 steps of Automated Caesar Cipher Cryptanalysis:",
            targetPrompt = "Arrange cracking pipeline",
            hint = "🎮 Phoenix Cheat Code: Generate all 25 candidate plaintexts, score against English letter frequencies, pick the best!",
            orderStepsInitial = listOf(
                "Pick candidate with highest frequency score",
                "Try all shifts k from 1 to 25",
                "Compute English frequency match score for each candidate"
            ),
            orderStepsSolution = listOf(
                "Try all shifts k from 1 to 25",
                "Compute English frequency match score for each candidate",
                "Pick candidate with highest frequency score"
            )
        )
    )

    private fun dsaLevel2Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Complete the monoalphabetic substitution mapping lookup:",
            targetPrompt = "Slot the lookup dictionary",
            hint = "🎮 Phoenix Cheat Code: lookup dictionary translates plaintext chars directly to substituted cipher letters!",
            fillCodeTemplate = "def substitute(text, cipher_alphabet):\n    lookup = {chr(65 + i): cipher_alphabet[i] for i in range(26)}\n    return \"\".join([___].get(ch, ch) for ch in text)",
            fillCodeWordBank = listOf("lookup", "cipher_alphabet", "text", "chr"),
            fillCodeCorrectToken = "lookup"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match classical substitution ciphers to their total key space capacity:",
            targetPrompt = "Connect ciphers to key spaces",
            hint = "🎮 Phoenix Cheat Code: Caesar has 25 shifts, Affine has 12 coprime multipliers × 26, General substitution has 26! permutations!",
            matchLeft = listOf("Caesar Shift", "Monoalphabetic Substitution", "Affine Cipher (a, b)"),
            matchRight = listOf("25 Shifts", "26! ≈ 4.03 × 10²⁶ Keys", "12 × 26 = 312 Keys"),
            matchSolution = mapOf(
                "Caesar Shift" to "25 Shifts",
                "Monoalphabetic Substitution" to "26! ≈ 4.03 × 10²⁶ Keys",
                "Affine Cipher (a, b)" to "12 × 26 = 312 Keys"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the steps to break a Monoalphabetic Substitution cipher:",
            targetPrompt = "Arrange cryptanalysis sequence",
            hint = "🎮 Phoenix Cheat Code: Count frequencies, match highest to ETAOIN, and verify using common digraphs like TH!",
            orderStepsInitial = listOf(
                "Map highest frequency cipher symbols to E, T, A, O, I, N",
                "Inspect common digraphs (TH, HE, IN) to confirm substitutions",
                "Tally occurrence counts for each letter in ciphertext"
            ),
            orderStepsSolution = listOf(
                "Tally occurrence counts for each letter in ciphertext",
                "Map highest frequency cipher symbols to E, T, A, O, I, N",
                "Inspect common digraphs (TH, HE, IN) to confirm substitutions"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: A massive 26! key space is useless because 1:1 letter frequency statistics remain intact!",
            trueFalseStatement = "Because monoalphabetic substitution has 26! keys, it cannot be broken using frequency analysis.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Dial brute-force complexity to test all 26! monoalphabetic permutations:",
            targetPrompt = "Select complexity tier",
            hint = "🎮 Phoenix Cheat Code: Permuting N distinct symbols scales factorially/exponentially as O(2ⁿ)!",
            complexityCodeSnippet = "# Exhaustive search over 26! permutations:\n# 403,291,461,126,605,635,584,000,000 keys\ndef exhaustive_key_search():\n    for perm in itertools.permutations(alphabet):\n        test_decrypt(perm)",
            complexityDialCorrect = "O(2ⁿ)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Which six letters account for over 45% of all letters in standard English text?",
            targetPrompt = "Choose letter frequency group",
            options = listOf("E, T, A, O, I, N", "B, C, D, F, G, H", "Q, X, Z, J, K, V", "U, V, W, X, Y, Z"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Remember the legendary printer's acronym ETAOIN SHRDLU!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match English n-grams with their cryptographic significance:",
            targetPrompt = "Pair n-grams",
            hint = "🎮 Phoenix Cheat Code: E is #1 single letter (~12.7%), TH is top digraph, THE is top trigraph!",
            matchLeft = listOf("Letter E (~12.7%)", "Digraphs TH and HE", "Trigraphs THE and AND"),
            matchRight = listOf("Highest Frequency Monogram", "Most Frequent Two-Letter Pairs", "Dominant Three-Letter Words"),
            matchSolution = mapOf(
                "Letter E (~12.7%)" to "Highest Frequency Monogram",
                "Digraphs TH and HE" to "Most Frequent Two-Letter Pairs",
                "Trigraphs THE and AND" to "Dominant Three-Letter Words"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the coprimality check ensuring Affine key 'a' has a modular inverse modulo 26:",
            targetPrompt = "Slot greatest common divisor function",
            hint = "🎮 Phoenix Cheat Code: gcd(a, 26) == 1 ensures 'a' shares no common divisors with 26!",
            fillCodeTemplate = "import math\ndef is_valid_affine_key(a, m=26):\n    # Key 'a' must be coprime to 26 for modular inverse to exist\n    return math.[___](a, m) == 1",
            fillCodeWordBank = listOf("gcd", "lcm", "pow", "sqrt"),
            fillCodeCorrectToken = "gcd"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: In E(x) = (ax + b) mod 26, 'a' must be coprime to 26; even numbers and 13 fail!",
            trueFalseStatement = "In the Affine cipher E(x) = (a·x + b) mod 26, key 'a' can safely be any even integer.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 3 steps to decrypt an Affine cipher: D(c) = a⁻¹(c - b) mod 26:",
            targetPrompt = "Arrange algebraic steps",
            hint = "🎮 Phoenix Cheat Code: Compute modular inverse of a, subtract b, then multiply under modulo 26!",
            orderStepsInitial = listOf(
                "Multiply difference by modular inverse: p = (a_inv * diff) % 26",
                "Find modular multiplicative inverse of a: a_inv",
                "Subtract intercept: diff = (c - b + 26) % 26"
            ),
            orderStepsSolution = listOf(
                "Find modular multiplicative inverse of a: a_inv",
                "Subtract intercept: diff = (c - b + 26) % 26",
                "Multiply difference by modular inverse: p = (a_inv * diff) % 26"
            )
        )
    )

    private fun dsaLevel3Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Slot the unary operator to reverse rail bounce direction upon hitting fence boundaries:",
            targetPrompt = "Slot sign inversion operator",
            hint = "🎮 Phoenix Cheat Code: Negating the direction flips +1 down to -1 up and vice-versa!",
            fillCodeTemplate = "def rail_fence_step(rail, num_rails, direction):\n    if rail == 0 or rail == num_rails - 1:\n        direction = [___]direction\n    return rail + direction, direction",
            fillCodeWordBank = listOf("-", "+", "not", "~"),
            fillCodeCorrectToken = "-"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match Transposition Cipher types with their spatial mechanics:",
            targetPrompt = "Pair cipher mechanics",
            hint = "🎮 Phoenix Cheat Code: Rail Fence bounces in zigzags; Columnar reads matrices by alphabetical key order!",
            matchLeft = listOf("Rail Fence Cipher", "Columnar Transposition", "Route Cipher"),
            matchRight = listOf("Zigzag wave across rails", "Matrix grid sorted by key word", "Spiral / grid pattern traversal"),
            matchSolution = mapOf(
                "Rail Fence Cipher" to "Zigzag wave across rails",
                "Columnar Transposition" to "Matrix grid sorted by key word",
                "Route Cipher" to "Spiral / grid pattern traversal"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the 3 steps of Columnar Transposition encryption:",
            targetPrompt = "Arrange transposition flow",
            hint = "🎮 Phoenix Cheat Code: Label columns with key letters, fill rows horizontally, read columns alphabetically!",
            orderStepsInitial = listOf(
                "Read off matrix column-by-column in alphabetical key order",
                "Write plaintext horizontally row-by-row into grid",
                "Number columns according to alphabetical order of key letters"
            ),
            orderStepsSolution = listOf(
                "Number columns according to alphabetical order of key letters",
                "Write plaintext horizontally row-by-row into grid",
                "Read off matrix column-by-column in alphabetical key order"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Transposition only permutes character locations; letter counts remain identical to English!",
            trueFalseStatement = "Transposition ciphers alter individual letter frequencies by substituting characters with offset symbols.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial time complexity of Columnar Transposition for message length N and key length K:",
            targetPrompt = "Select time complexity",
            hint = "🎮 Phoenix Cheat Code: Writing N characters into a grid and reading them once takes linear O(n) time!",
            complexityCodeSnippet = "# Writing N chars into grid and reading out K columns:\ndef columnar_cipher(plaintext, key):\n    grid = write_grid(plaintext, len(key))\n    return read_columns(grid, sorted(key))",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What fundamental characteristic distinguishes Transposition from Substitution ciphers?",
            targetPrompt = "Choose fundamental distinction",
            options = listOf(
                "Transposition changes symbol positions; Substitution changes symbol identities",
                "Transposition requires asymmetric keys; Substitution uses symmetric keys",
                "Transposition only encrypts numbers; Substitution encrypts letters",
                "Transposition compresses message size; Substitution expands it"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Transposition = permutation of indices; Substitution = mapping of symbols!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match cryptanalysis frequency signatures to cipher categories:",
            targetPrompt = "Connect frequency profiles",
            hint = "🎮 Phoenix Cheat Code: Transposition preserves English frequencies; polyalphabetic flattens them!",
            matchLeft = listOf("Identical to Standard English", "Shifted English Peaks", "Flattened Uniform Distribution"),
            matchRight = listOf("Pure Transposition Cipher", "Monoalphabetic Substitution", "Polyalphabetic / Modern Cipher"),
            matchSolution = mapOf(
                "Identical to Standard English" to "Pure Transposition Cipher",
                "Shifted English Peaks" to "Monoalphabetic Substitution",
                "Flattened Uniform Distribution" to "Polyalphabetic / Modern Cipher"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Slot the math function ensuring sufficient grid rows for all message characters:",
            targetPrompt = "Slot ceiling function",
            hint = "🎮 Phoenix Cheat Code: ceil(length / columns) rounds up so no characters get truncated!",
            fillCodeTemplate = "import math\ndef compute_rows(message_len, num_columns):\n    # Compute required rows using ceiling division\n    return math.[___](message_len / num_columns)",
            fillCodeWordBank = listOf("ceil", "floor", "trunc", "round"),
            fillCodeCorrectToken = "ceil"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Double Transposition scrambles rectangular structures, multiplying cryptanalytic difficulty!",
            trueFalseStatement = "Applying two transposition ciphers in sequence (Double Transposition) offers significantly higher security than a single transposition.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order steps to decrypt Rail Fence cipher with 2 rails:",
            targetPrompt = "Arrange decryption sequence",
            hint = "🎮 Phoenix Cheat Code: Split ciphertext in half, then interleave rail 1 and rail 2!",
            orderStepsInitial = listOf(
                "Interleave alternating characters from rail 1 and rail 2",
                "Divide ciphertext into two equal halves (rail 1, rail 2)",
                "Assemble and return reconstructed plaintext"
            ),
            orderStepsSolution = listOf(
                "Divide ciphertext into two equal halves (rail 1, rail 2)",
                "Interleave alternating characters from rail 1 and rail 2",
                "Assemble and return reconstructed plaintext"
            )
        )
    )

    private fun dsaLevel4Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Slot the denominator term in Friedman's Index of Coincidence formula Σ f_i(f_i-1) / [n(n-1)]:",
            targetPrompt = "Slot formula denominator term",
            hint = "🎮 Phoenix Cheat Code: For n total letters, pairs chosen without replacement is n * (n - 1)!",
            fillCodeTemplate = "def index_of_coincidence(freqs, n):\n    # Probability that two randomly selected letters match\n    numerator = sum(f * (f - 1) for f in freqs)\n    return numerator / (n * [___])",
            fillCodeWordBank = listOf("(n - 1)", "(n + 1)", "n", "26"),
            fillCodeCorrectToken = "(n - 1)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match Index of Coincidence (IoC) values to language and cipher profiles:",
            targetPrompt = "Pair IoC values",
            hint = "🎮 Phoenix Cheat Code: English has IoC ≈ 0.067, while random letters / polyalphabetic noise has IoC ≈ 0.038!",
            matchLeft = listOf("Natural English Text", "Random Polyalphabetic Ciphertext", "Monoalphabetic Ciphertext"),
            matchRight = listOf("IoC ≈ 0.067", "IoC ≈ 0.038", "IoC ≈ 0.067 (Preserved)"),
            matchSolution = mapOf(
                "Natural English Text" to "IoC ≈ 0.067",
                "Random Polyalphabetic Ciphertext" to "IoC ≈ 0.038",
                "Monoalphabetic Ciphertext" to "IoC ≈ 0.067 (Preserved)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the 3 steps of Kasiski Examination to find polyalphabetic key length m:",
            targetPrompt = "Arrange Kasiski procedure",
            hint = "🎮 Phoenix Cheat Code: Find repeated ciphertext segments, measure their distance, and find common factors!",
            orderStepsInitial = listOf(
                "Find greatest common divisor (GCD) factors of distances",
                "Identify repeated character sequences of length 3+ in ciphertext",
                "Measure character distance (interval) between each repetition"
            ),
            orderStepsSolution = listOf(
                "Identify repeated character sequences of length 3+ in ciphertext",
                "Measure character distance (interval) between each repetition",
                "Find greatest common divisor (GCD) factors of distances"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Diffusion spreads redundancy; Confusion obscures the relationship between key and ciphertext!",
            trueFalseStatement = "Shannon's concept of 'Diffusion' ensures that changing one plaintext bit alters many ciphertext bits.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Dial time complexity to compute letter frequencies of text length N over 26 characters:",
            targetPrompt = "Select worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: Incrementing letter array counters during a single pass is linear O(n)!",
            complexityCodeSnippet = "def letter_frequencies(text):\n    counts = [0] * 26\n    for char in text: # single pass\n        counts[ord(char) - 65] += 1\n    return counts",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What two fundamental principles did Claude Shannon establish as the foundation of secure cryptosystems?",
            targetPrompt = "Choose Shannon's principles",
            options = listOf("Confusion and Diffusion", "Public and Private Keys", "Compression and Hashing", "Zero-Knowledge and Multi-Party"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Confusion makes key-to-ciphertext complex; Diffusion spreads statistical properties!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Match Shannon's cryptographic principles with their modern implementations:",
            targetPrompt = "Connect principles to primitives",
            hint = "🎮 Phoenix Cheat Code: S-boxes provide Confusion, Permutations (P-boxes) provide Diffusion, One-Time Pad gives Perfect Secrecy!",
            matchLeft = listOf("Confusion", "Diffusion", "Perfect Secrecy"),
            matchRight = listOf("S-Boxes (Substitution Non-linearity)", "P-Boxes (Permutation Bit Scattering)", "One-Time Pad (P(M|C) = P(M))"),
            matchSolution = mapOf(
                "Confusion" to "S-Boxes (Substitution Non-linearity)",
                "Diffusion" to "P-Boxes (Permutation Bit Scattering)",
                "Perfect Secrecy" to "One-Time Pad (P(M|C) = P(M))"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Complete Pearson's chi-squared formula matching observed cipher counts against expected English frequencies:",
            targetPrompt = "Slot division operator",
            hint = "🎮 Phoenix Cheat Code: Chi-squared formula is sum of (Observed - Expected)² / Expected!",
            fillCodeTemplate = "def chi_squared_stat(observed, expected):\n    # Sum of (O - E)^2 / E across alphabet\n    return sum((o - e) ** 2 [___] e for o, e in zip(observed, expected))",
            fillCodeWordBank = listOf("/", "*", "+", "-"),
            fillCodeCorrectToken = "/"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Monoalphabetic substitution retains the 0.067 IoC of English because letter pair collisions are unchanged!",
            trueFalseStatement = "In a monoalphabetic substitution, the Index of Coincidence drops to 0.038, identical to random noise.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order steps to break Vigenère cipher using Kasiski examination and frequency analysis:",
            targetPrompt = "Arrange Vigenère cracking",
            hint = "🎮 Phoenix Cheat Code: Find key length m, slice ciphertext into m periodic streams, solve each stream as Caesar!",
            orderStepsInitial = listOf(
                "Solve each of the m streams independently as a Caesar cipher",
                "Slice ciphertext into m periodic streams",
                "Determine key length m using Kasiski repeated trigraph spacings",
                "Assemble individual stream shift keys to form master keyword"
            ),
            orderStepsSolution = listOf(
                "Determine key length m using Kasiski repeated trigraph spacings",
                "Slice ciphertext into m periodic streams",
                "Solve each of the m streams independently as a Caesar cipher",
                "Assemble individual stream shift keys to form master keyword"
            )
        )
    )

    private fun dsaLevel5Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the polyalphabetic Vigenère encryption addition modulo 26:",
            targetPrompt = "Slot the addition operator",
            hint = "🎮 Phoenix Cheat Code: Vigenère shifts plaintext index by key index: (p + k) % 26!",
            fillCodeTemplate = "def vigenere_encrypt_char(p_char, k_char):\n    p = ord(p_char) - ord('A')\n    k = ord(k_char) - ord('A')\n    return chr((p [___] k) % 26 + ord('A'))",
            fillCodeWordBank = listOf("+", "-", "*", "^"),
            fillCodeCorrectToken = "+"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match classical & polyalphabetic ciphers with their primary vulnerability:",
            targetPrompt = "Connect ciphers to exploits",
            hint = "🎮 Phoenix Cheat Code: Caesar has tiny key space; Monoalphabetic has unchanged letter stats; Vigenère has periodic repetition!",
            matchLeft = listOf("Caesar Cipher", "Monoalphabetic Substitution", "Vigenère Cipher"),
            matchRight = listOf("25-Key Exhaustive Brute Force", "Single-Letter Frequency Analysis", "Kasiski Examination & Periodic IoC Slicing"),
            matchSolution = mapOf(
                "Caesar Cipher" to "25-Key Exhaustive Brute Force",
                "Monoalphabetic Substitution" to "Single-Letter Frequency Analysis",
                "Vigenère Cipher" to "Kasiski Examination & Periodic IoC Slicing"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Order the algorithmic steps to decrypt a Vigenère ciphertext message:",
            targetPrompt = "Arrange Vigenère decryption",
            hint = "🎮 Phoenix Cheat Code: Repeat keyword, calculate (c - k) mod 26 for each char, convert back to letters!",
            orderStepsInitial = listOf(
                "Look up ciphertext letter c_i and key letter k_i",
                "Repeat key word cyclically to match message length",
                "Convert decrypted numerical index back to character",
                "Compute plaintext index: p_i = (c_i - k_i + 26) % 26"
            ),
            orderStepsSolution = listOf(
                "Repeat key word cyclically to match message length",
                "Look up ciphertext letter c_i and key letter k_i",
                "Compute plaintext index: p_i = (c_i - k_i + 26) % 26",
                "Convert decrypted numerical index back to character"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Claude Shannon proved OTP has perfect secrecy because P(M|C) = P(M)!",
            trueFalseStatement = "The One-Time Pad (Vernam cipher) is mathematically unbreakable even with infinite computing power if the key is truly random, never reused, and as long as the message.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Dial time complexity of Vigenère encryption/decryption for message length N and key length K:",
            targetPrompt = "Select worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: Encrypting each character takes O(1) modulo math: overall strictly linear O(n)!",
            complexityCodeSnippet = "def vigenere(message, key):\n    return \"\".join(\n        shift(char, key[i % len(key)])\n        for i, char in enumerate(message)\n    )",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What critical cryptographic flaw allowed British cryptanalysts to break the German Lorenz SZ40 cipher in WW2?",
            targetPrompt = "Choose the fatal mistake",
            options = listOf(
                "Two different messages were transmitted using the identical keystream ('Two-Time Pad')",
                "The key length was restricted to only 8 bits",
                "The encryption hardware was connected to the open civilian telephone network",
                "The cipher used modular addition instead of bitwise XOR"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Reusing a stream cipher keystream allows cryptanalysts to XOR ciphertexts and eliminate the key!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Match Vernam One-Time Pad theoretical requirements with their mathematical rules:",
            targetPrompt = "Connect requirements",
            hint = "🎮 Phoenix Cheat Code: Uniform distribution, length >= message, single use only!",
            matchLeft = listOf("Key Generation", "Key Length", "Key Lifecycle"),
            matchRight = listOf("Truly Random (Uniform Distribution)", "At least as long as plaintext message", "Used exactly once then destroyed"),
            matchSolution = mapOf(
                "Key Generation" to "Truly Random (Uniform Distribution)",
                "Key Length" to "At least as long as plaintext message",
                "Key Lifecycle" to "Used exactly once then destroyed"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Slot the bitwise XOR operator providing information-theoretic security in a One-Time Pad:",
            targetPrompt = "Slot XOR operator",
            hint = "🎮 Phoenix Cheat Code: In Python, ^ performs bitwise XOR: (P ^ K) ^ K = P!",
            fillCodeTemplate = "def vernam_otp_encrypt(plaintext_byte, key_byte):\n    # Perfect secrecy via bitwise XOR operation\n    return plaintext_byte [___] key_byte",
            fillCodeWordBank = listOf("^", "&", "|", "<<"),
            fillCodeCorrectToken = "^"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: K ⊕ K = 0! The key cancels out completely: C1 ⊕ C2 = P1 ⊕ P2!",
            trueFalseStatement = "If a One-Time Pad key is reused twice (C1 = P1 ⊕ K, C2 = P2 ⊕ K), an attacker can compute C1 ⊕ C2 = P1 ⊕ P2 and completely eliminate the key.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order the 3 steps of Two-Time Pad Cryptanalysis:",
            targetPrompt = "Arrange Two-Time Pad attack",
            hint = "🎮 Phoenix Cheat Code: Intercept two ciphertexts with same key, XOR them to cancel the key, then crib-drag English words!",
            orderStepsInitial = listOf(
                "Apply language statistics and crib-dragging to separate P1 and P2",
                "Intercept two ciphertexts C1 and C2 encrypted with the identical key K",
                "Compute XOR sum: C1 ⊕ C2 = (P1 ⊕ K) ⊕ (P2 ⊕ K) = P1 ⊕ P2"
            ),
            orderStepsSolution = listOf(
                "Intercept two ciphertexts C1 and C2 encrypted with the identical key K",
                "Compute XOR sum: C1 ⊕ C2 = (P1 ⊕ K) ⊕ (P2 ⊕ K) = P1 ⊕ P2",
                "Apply language statistics and crib-dragging to separate P1 and P2"
            )
        )
    )

    private fun dsaLevel6Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "In a classic Feistel cipher network round, how is the new right half R_i computed from L_{i-1} and round function F?",
            targetPrompt = "Select the Feistel formula",
            options = listOf(
                "R_i = L_{i-1} ⊕ F(R_{i-1}, K_i)",
                "R_i = L_{i-1} + R_{i-1}",
                "R_i = F(L_{i-1}, K_i)",
                "R_i = L_{i-1} * K_i"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Feistel XORs the prior left half with the round function output: R_i = L_{i-1} ⊕ F(R_{i-1}, K_i)!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the Feistel network swap and XOR computation for round i:",
            targetPrompt = "Slot the XOR operator",
            hint = "🎮 Phoenix Cheat Code: In Python, ^ performs bitwise XOR: new_right = left ^ F(right, key)!",
            fillCodeTemplate = "def feistel_round(left, right, subkey):\n    # Left becomes right; Right becomes Left XOR F(Right, Subkey)\n    new_left = right\n    new_right = left [___] round_function(right, subkey)\n    return new_left, new_right",
            fillCodeWordBank = listOf("^", "&", "|", "+"),
            fillCodeCorrectToken = "^"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match DES specifications with their architectural dimensions:",
            targetPrompt = "Connect DES dimensions",
            hint = "🎮 Phoenix Cheat Code: DES takes 64-bit blocks, uses 16 Feistel rounds, and has 56 effective key bits!",
            matchLeft = listOf("DES Effective Key", "DES Block Size", "DES Feistel Rounds"),
            matchRight = listOf("56 bits (8 parity bits discarded)", "64 bits (8 bytes)", "16 symmetric rounds"),
            matchSolution = mapOf(
                "DES Effective Key" to "56 bits (8 parity bits discarded)",
                "DES Block Size" to "64 bits (8 bytes)",
                "DES Feistel Rounds" to "16 symmetric rounds"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Decryption runs subkeys in reverse order—the round function F does NOT need to be invertible!",
            trueFalseStatement = "In a Feistel cipher network, the round function F must be mathematically invertible for decryption to work.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 4 steps of a single Feistel encryption round:",
            targetPrompt = "Arrange Feistel sequence",
            hint = "🎮 Phoenix Cheat Code: Split block, pass Right through F with key, XOR with Left, swap halves!",
            orderStepsInitial = listOf(
                "XOR output of function F with Left half to form new Right half",
                "Split 64-bit input into Left (L) and Right (R) 32-bit halves",
                "Assign previous Right half directly to new Left half",
                "Pass Right half and round subkey into round function F"
            ),
            orderStepsSolution = listOf(
                "Split 64-bit input into Left (L) and Right (R) 32-bit halves",
                "Pass Right half and round subkey into round function F",
                "XOR output of function F with Left half to form new Right half",
                "Assign previous Right half directly to new Left half"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Dial the brute-force time complexity to exhaustively search DES's 56-bit keyspace:",
            targetPrompt = "Select keyspace complexity",
            hint = "🎮 Phoenix Cheat Code: Trying every key across an n-bit keyspace takes exponential O(2^n) time!",
            complexityCodeSnippet = "# DES effective key length = 56 bits\ntotal_possible_keys = 2 ** 56  # ≈ 7.2 * 10^16 checks",
            complexityDialCorrect = "O(2^n)"
        )
    )

    private fun dsaLevel7Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Which AES transformation provides non-linear Confusion by substituting each byte using Rijndael S-Boxes?",
            targetPrompt = "Select confusion operation",
            options = listOf("SubBytes", "ShiftRows", "MixColumns", "AddRoundKey"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: SubBytes substitutes bytes via non-linear Galois Field inversion S-Boxes!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Slot the dimensions of the AES 128-bit state matrix (4 rows by [___] columns of bytes):",
            targetPrompt = "Slot column count",
            hint = "🎮 Phoenix Cheat Code: 4 rows × 4 columns = 16 bytes = 128 bits!",
            fillCodeTemplate = "# AES state matrix: 16 bytes organized as 4x4 matrix\nstate_matrix = [[0] * [___] for _ in range(4)]",
            fillCodeWordBank = listOf("4", "8", "16", "32"),
            fillCodeCorrectToken = "4"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match AES round operations with their cryptographic purposes:",
            targetPrompt = "Connect AES transformations",
            hint = "🎮 Phoenix Cheat Code: SubBytes = Confusion, ShiftRows/MixColumns = Diffusion, AddRoundKey = Key Mixing!",
            matchLeft = listOf("SubBytes", "ShiftRows & MixColumns", "AddRoundKey"),
            matchRight = listOf("Non-linear Confusion (S-Box)", "Linear Bit Diffusion", "Secret Key Injection (XOR)"),
            matchSolution = mapOf(
                "SubBytes" to "Non-linear Confusion (S-Box)",
                "ShiftRows & MixColumns" to "Linear Bit Diffusion",
                "AddRoundKey" to "Secret Key Injection (XOR)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: AES ALWAYS operates on 128-bit blocks, whether the key is 128, 192, or 256 bits!",
            trueFalseStatement = "When configuring AES-256, the cipher block size expands from 128 bits to 256 bits.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Order the 4 transformations executed in a standard AES encryption round:",
            targetPrompt = "Arrange AES round sequence",
            hint = "🎮 Phoenix Cheat Code: Substitute, Shift, Mix, Add key: SubBytes -> ShiftRows -> MixColumns -> AddRoundKey!",
            orderStepsInitial = listOf(
                "AddRoundKey (bitwise XOR with round subkey)",
                "ShiftRows (cyclical permutation of state matrix rows)",
                "SubBytes (non-linear byte substitution via S-Box)",
                "MixColumns (matrix multiplication in GF(2^8))"
            ),
            orderStepsSolution = listOf(
                "SubBytes (non-linear byte substitution via S-Box)",
                "ShiftRows (cyclical permutation of state matrix rows)",
                "MixColumns (matrix multiplication in GF(2^8))",
                "AddRoundKey (bitwise XOR with round subkey)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Dial the time complexity of AES encrypting a single 128-bit block across 10 fixed rounds:",
            targetPrompt = "Select block encryption complexity",
            hint = "🎮 Phoenix Cheat Code: 10 fixed rounds of table lookups and shifts execute in deterministic O(1) constant time!",
            complexityCodeSnippet = "def aes_block_encrypt(block, round_keys):\n    # 10 fixed iterations over 16 bytes\n    for r in range(10): ...\n    return block",
            complexityDialCorrect = "O(1)"
        )
    )

    private fun dsaLevel8Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Why is Electronic Codebook (ECB) mode dangerously insecure for encrypting structured messages or images?",
            targetPrompt = "Select the ECB vulnerability",
            options = listOf(
                "Identical plaintext blocks encrypt to identical ciphertext blocks, preserving visual patterns",
                "It requires public-key certificates",
                "It requires twice as much memory as other modes",
                "It leaks the secret key directly in the ciphertext"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: The famous ECB Penguin shows that identical plaintext blocks produce identical ciphertext blocks!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Complete the Cipher Block Chaining (CBC) encryption formula XORing plaintext with previous ciphertext:",
            targetPrompt = "Slot the XOR operator",
            hint = "🎮 Phoenix Cheat Code: CBC XORs plaintext block with previous ciphertext before encryption: P_i ^ C_{i-1}!",
            fillCodeTemplate = "def cbc_encrypt_block(prev_ciphertext, plaintext_block, cipher):\n    # XOR plaintext with previous ciphertext (or IV) before block cipher\n    chained = bytes(p [___] c for p, c in zip(plaintext_block, prev_ciphertext))\n    return cipher.encrypt_block(chained)",
            fillCodeWordBank = listOf("^", "&", "|", "+"),
            fillCodeCorrectToken = "^"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match block cipher modes of operation with their defining attributes:",
            targetPrompt = "Connect block cipher modes",
            hint = "🎮 Phoenix Cheat Code: ECB leaks patterns, CBC chains sequential blocks, CTR enables parallel stream encryption!",
            matchLeft = listOf("Electronic Codebook (ECB)", "Cipher Block Chaining (CBC)", "Counter Mode (CTR)"),
            matchRight = listOf("Insecure pattern leakage", "Chains blocks with random IV", "Stream mode with parallel execution"),
            matchSolution = mapOf(
                "Electronic Codebook (ECB)" to "Insecure pattern leakage",
                "Cipher Block Chaining (CBC)" to "Chains blocks with random IV",
                "Counter Mode (CTR)" to "Stream mode with parallel execution"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: In CTR mode, encrypting Nonce || Counter can be done across multiple CPU cores simultaneously!",
            trueFalseStatement = "Counter (CTR) mode allows parallel hardware encryption and random-access decryption of arbitrary blocks.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 4 steps of CBC mode encryption for block i:",
            targetPrompt = "Arrange CBC encryption flow",
            hint = "🎮 Phoenix Cheat Code: Get previous ciphertext (or IV), XOR with plaintext, encrypt with key, emit and store!",
            orderStepsInitial = listOf(
                "Encrypt XOR result with key: C_i = E(P_i ⊕ C_{i-1}, K)",
                "Emit C_i and store as feedback vector for block i+1",
                "Obtain previous ciphertext C_{i-1} (or random IV for block 0)",
                "XOR plaintext block P_i with C_{i-1}"
            ),
            orderStepsSolution = listOf(
                "Obtain previous ciphertext C_{i-1} (or random IV for block 0)",
                "XOR plaintext block P_i with C_{i-1}",
                "Encrypt XOR result with key: C_i = E(P_i ⊕ C_{i-1}, K)",
                "Emit C_i and store as feedback vector for block i+1"
            )
        )
    )

    private fun dsaLevel9Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "What catastrophic cryptographic failure occurs if an IV (nonce) is reused with the same key in AES-GCM?",
            targetPrompt = "Select the GCM nonce reuse vulnerability",
            options = listOf(
                "Attacker can recover the GHASH authentication key and forge arbitrary valid tags",
                "The ciphertext automatically decrypts itself",
                "The key expands to 512 bits",
                "The server denies all incoming TCP connections"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Reusing a nonce in GCM leaks the authentication subkey H, completely destroying integrity!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the AEAD tag verification check in constant time:",
            targetPrompt = "Slot the exception",
            hint = "🎮 Phoenix Cheat Code: If the authentication tag does not verify, never return plaintext—raise a SecurityException!",
            fillCodeTemplate = "def aead_decrypt_and_verify(cipher, ciphertext, tag):\n    if not cipher.verify_tag(tag):\n        raise [___](\"Ciphertext was tampered in transit!\")\n    return cipher.decrypt(ciphertext)",
            fillCodeWordBank = listOf("SecurityException", "IndexError", "TimeoutError", "KeyError"),
            fillCodeCorrectToken = "SecurityException"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match Authenticated Encryption paradigms with their properties:",
            targetPrompt = "Connect encryption paradigms",
            hint = "🎮 Phoenix Cheat Code: AES-GCM is hardware-accelerated AEAD; Plain CTR provides confidentiality only!",
            matchLeft = listOf("AES-GCM", "Plain AES-CTR", "Associated Data (AAD)"),
            matchRight = listOf("Hardware AEAD (Confidentiality + Integrity)", "Confidentiality only (Vulnerable to bit flips)", "Authenticated in cleartext (Headers/Addresses)"),
            matchSolution = mapOf(
                "AES-GCM" to "Hardware AEAD (Confidentiality + Integrity)",
                "Plain AES-CTR" to "Confidentiality only (Vulnerable to bit flips)",
                "Associated Data (AAD)" to "Authenticated in cleartext (Headers/Addresses)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: AAD (Associated Data) like packet IP headers is authenticated but left in cleartext for routers!",
            trueFalseStatement = "In AEAD schemes, Associated Data (AAD) is verified against tampering but transmitted unencrypted.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order the 3 stages of AES-GCM authenticated encryption:",
            targetPrompt = "Arrange GCM flow",
            hint = "🎮 Phoenix Cheat Code: CTR encrypts payload, GHASH computes polynomial tag, mask key seals tag!",
            orderStepsInitial = listOf(
                "Multiply ciphertext and AAD blocks into GHASH polynomial authenticator",
                "Encrypt GHASH tag with mask key to produce final Authentication Tag",
                "Encrypt plaintext blocks using CTR mode with unique Nonce"
            ),
            orderStepsSolution = listOf(
                "Encrypt plaintext blocks using CTR mode with unique Nonce",
                "Multiply ciphertext and AAD blocks into GHASH polynomial authenticator",
                "Encrypt GHASH tag with mask key to produce final Authentication Tag"
            )
        )
    )

    private fun dsaLevel10Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "What is the effective key length and block size of 3DES (Triple DES with 3 independent keys)?",
            targetPrompt = "Select 3DES specifications",
            options = listOf(
                "168-bit key length, 64-bit block size",
                "128-bit key length, 128-bit block size",
                "256-bit key length, 64-bit block size",
                "56-bit key length, 128-bit block size"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: 3 keys × 56 effective bits = 168 bits; block size remains classic DES 64 bits!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Complete the PKCS#7 padding length calculation for 16-byte block boundary:",
            targetPrompt = "Slot modulo operator",
            hint = "🎮 Phoenix Cheat Code: Padding bytes = block_size - (length % block_size)!",
            fillCodeTemplate = "def pkcs7_pad_len(message_len, block_size=16):\n    # Calculate padding bytes needed (1 to 16 bytes)\n    return block_size - (message_len [___] block_size)",
            fillCodeWordBank = listOf("%", "//", "*", "+"),
            fillCodeCorrectToken = "%"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match cryptanalytic attacks with their symmetric cipher targets:",
            targetPrompt = "Connect attacks to targets",
            hint = "🎮 Phoenix Cheat Code: Padding Oracle attacks CBC padding; Meet-in-the-Middle attacks 2DES/3DES!",
            matchLeft = listOf("Padding Oracle Attack", "Meet-in-the-Middle Attack", "Galois Field GF(2^8)"),
            matchRight = listOf("Exploits CBC decryption error messages", "Reduces 2-key 3DES security to 2^112", "Algebraic field for AES MixColumns"),
            matchSolution = mapOf(
                "Padding Oracle Attack" to "Exploits CBC decryption error messages",
                "Meet-in-the-Middle Attack" to "Reduces 2-key 3DES security to 2^112",
                "Galois Field GF(2^8)" to "Algebraic field for AES MixColumns"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: AES-128's 2^128 keyspace requires more energy than boiling the oceans to brute-force!",
            trueFalseStatement = "AES-128 is considered mathematically broken because differential cryptanalysis can crack it in O(n) linear time.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Dial the brute-force time complexity to break an AES-128 key across its 2^128 keyspace:",
            targetPrompt = "Select keyspace complexity",
            hint = "🎮 Phoenix Cheat Code: Exhaustive key search is exponential in key length n: O(2^n)!",
            complexityCodeSnippet = "# AES-128 keyspace has 2^128 possible keys\nkeyspace_size = 2 ** 128  # ≈ 3.4 * 10^38 combinations",
            complexityDialCorrect = "O(2^n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order the defense layers against Block Overlord's chosen-ciphertext attacks:",
            targetPrompt = "Arrange defense strategy",
            hint = "🎮 Phoenix Cheat Code: Use AES-256 GCM, unique nonce, compute GHASH tag, verify in constant time!",
            orderStepsInitial = listOf(
                "Compute 128-bit GHASH authentication tag over ciphertext and AAD",
                "Generate cryptographically secure 96-bit unique Nonce",
                "Verify tag in constant time before decrypting any block",
                "Select AES-256 with Galois/Counter Mode (GCM)"
            ),
            orderStepsSolution = listOf(
                "Select AES-256 with Galois/Counter Mode (GCM)",
                "Generate cryptographically secure 96-bit unique Nonce",
                "Compute 128-bit GHASH authentication tag over ciphertext and AAD",
                "Verify tag in constant time before decrypting any block"
            )
        )
    )

    private fun dsaLevel11Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "If p = 3 and q = 11 are two distinct primes, what is Euler's totient φ(n) for their modulus n = p * q = 33?",
            targetPrompt = "Calculate Euler's totient",
            options = listOf("φ(33) = (3-1)(11-1) = 20", "φ(33) = 33", "φ(33) = 14", "φ(33) = 30"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: For the product of two primes, φ(pq) = (p - 1) * (q - 1)!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the modular exponentiation helper in Python using built-in 3-argument pow():",
            targetPrompt = "Slot the modulus argument",
            hint = "🎮 Phoenix Cheat Code: pow(base, exp, modulus) calculates (base ** exp) % modulus in O(log exp) time!",
            fillCodeTemplate = "def mod_exp(base, exp, modulus):\n    # Efficient modular exponentiation: (base ** exp) % modulus\n    return pow(base, exp, [___])",
            fillCodeWordBank = listOf("modulus", "base", "exp", "2"),
            fillCodeCorrectToken = "modulus"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match number theory theorems with their algebraic formulation:",
            targetPrompt = "Connect mathematical theorems",
            hint = "🎮 Phoenix Cheat Code: Fermat says a^(p-1) = 1 mod p; Totient of primes is (p-1)(q-1)!",
            matchLeft = listOf("Euler's Totient φ(pq)", "Fermat's Little Theorem", "Extended Euclidean Algorithm"),
            matchRight = listOf("(p - 1) * (q - 1)", "a^(p-1) ≡ 1 (mod p)", "Computes modular inverse d = e^(-1) mod φ(n)"),
            matchSolution = mapOf(
                "Euler's Totient φ(pq)" to "(p - 1) * (q - 1)",
                "Fermat's Little Theorem" to "a^(p-1) ≡ 1 (mod p)",
                "Extended Euclidean Algorithm" to "Computes modular inverse d = e^(-1) mod φ(n)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Coprime means they share no common factors other than 1: gcd(a, b) = 1!",
            trueFalseStatement = "Two positive integers a and b are called coprime if and only if their Greatest Common Divisor gcd(a, b) = 1.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial time complexity of the Euclidean algorithm to compute gcd(a, b) for b <= a:",
            targetPrompt = "Select Euclidean complexity",
            hint = "🎮 Phoenix Cheat Code: Lamé's theorem proves Euclidean algorithm steps are logarithmic in b: O(log n)!",
            complexityCodeSnippet = "def gcd(a, b):\n    # Each step cuts remainder by at least half every 2 steps\n    while b: a, b = b, a % b\n    return a",
            complexityDialCorrect = "O(log n)"
        )
    )

    private fun dsaLevel12Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What shared secret key K do Alice and Bob arrive at over an insecure channel in Diffie-Hellman key exchange?",
            targetPrompt = "Select the shared secret formula",
            options = listOf("K = g^(ab) mod p", "K = g^(a+b) mod p", "K = (A + B) mod p", "K = g^p mod ab"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Alice computes B^a = g^(ab) mod p; Bob computes A^b = g^(ab) mod p!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Slot the exponent Alice uses to compute her public share A = g^a mod p:",
            targetPrompt = "Slot the private exponent",
            hint = "🎮 Phoenix Cheat Code: Alice raises generator g to her secret private exponent a modulo p!",
            fillCodeTemplate = "def compute_alice_public(g, alice_private_a, p):\n    # Compute Alice's public share A = g^a mod p\n    return pow(g, [___], p)",
            fillCodeWordBank = listOf("alice_private_a", "g", "p", "2"),
            fillCodeCorrectToken = "alice_private_a"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Match Diffie-Hellman roles with their calculated parameters:",
            targetPrompt = "Connect DH calculations",
            hint = "🎮 Phoenix Cheat Code: Alice computes B^a, Bob computes A^b, Eve sees only g, p, A, B!",
            matchLeft = listOf("Alice Computes", "Bob Computes", "Eavesdropper Intercepts"),
            matchRight = listOf("K = B^a mod p = g^(ab)", "K = A^b mod p = g^(ab)", "g, p, A, B (Hard discrete log)"),
            matchSolution = mapOf(
                "Alice Computes" to "K = B^a mod p = g^(ab)",
                "Bob Computes" to "K = A^b mod p = g^(ab)",
                "Eavesdropper Intercepts" to "g, p, A, B (Hard discrete log)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Unauthenticated DH is vulnerable to active Man-in-the-Middle (MITM) attacks!",
            trueFalseStatement = "Standard anonymous Diffie-Hellman key exchange is intrinsically secure against active Man-in-the-Middle (MITM) attacks without digital signatures.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the 4 steps of the Diffie-Hellman Key Exchange protocol:",
            targetPrompt = "Arrange DH protocol flow",
            hint = "🎮 Phoenix Cheat Code: Agree publicly on g and p, compute public shares A and B, exchange shares, compute g^(ab)!",
            orderStepsInitial = listOf(
                "Both independently compute shared secret K = g^(ab) mod p",
                "Agree publicly on large prime modulus p and generator base g",
                "Alice chooses secret a and transmits public share A = g^a mod p",
                "Bob chooses secret b and transmits public share B = g^b mod p"
            ),
            orderStepsSolution = listOf(
                "Agree publicly on large prime modulus p and generator base g",
                "Alice chooses secret a and transmits public share A = g^a mod p",
                "Bob chooses secret b and transmits public share B = g^b mod p",
                "Both independently compute shared secret K = g^(ab) mod p"
            )
        )
    )

    private fun dsaLevel13Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "In RSA encryption, which mathematical relationship connects public exponent e and private exponent d?",
            targetPrompt = "Select the RSA key relationship",
            options = listOf("e · d ≡ 1 (mod φ(n))", "e + d = n", "e · d = p · q", "e^d ≡ n (mod 2)"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: d is the modular multiplicative inverse of e modulo Euler's totient φ(n)!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the RSA ciphertext decryption formula M = C^d mod n:",
            targetPrompt = "Slot the private exponent",
            hint = "🎮 Phoenix Cheat Code: Ciphertext C is raised to private exponent d modulo n to recover plaintext M!",
            fillCodeTemplate = "def rsa_decrypt(ciphertext_c, private_key_d, modulus_n):\n    # Decrypt ciphertext using private key exponent d: (C ** d) % n\n    return pow(ciphertext_c, [___], modulus_n)",
            fillCodeWordBank = listOf("private_key_d", "modulus_n", "ciphertext_c", "65537"),
            fillCodeCorrectToken = "private_key_d"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match RSA algebraic components with their mathematical identities:",
            targetPrompt = "Connect RSA components",
            hint = "🎮 Phoenix Cheat Code: Modulus n = p*q, Encryption C = M^e mod n, Decryption M = C^d mod n!",
            matchLeft = listOf("RSA Encryption", "RSA Decryption", "RSA Modulus"),
            matchRight = listOf("C = M^e mod n", "M = C^d mod n", "n = p * q"),
            matchSolution = mapOf(
                "RSA Encryption" to "C = M^e mod n",
                "RSA Decryption" to "M = C^d mod n",
                "RSA Modulus" to "n = p * q"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Textbook RSA is deterministic and homomorphic—OAEP padding is essential!",
            trueFalseStatement = "Textbook unpadded RSA (C = M^e mod n) is secure for direct deployment because integer factorization is hard.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 4 algebraic steps of RSA key generation:",
            targetPrompt = "Arrange RSA key generation flow",
            hint = "🎮 Phoenix Cheat Code: Choose primes p & q, compute n and φ(n), select public e, compute private d!",
            orderStepsInitial = listOf(
                "Select public exponent e (commonly 65537) coprime to φ(n)",
                "Compute modulus n = p*q and totient φ(n) = (p-1)(q-1)",
                "Compute private key exponent d = e^(-1) mod φ(n)",
                "Choose two large distinct prime numbers p and q"
            ),
            orderStepsSolution = listOf(
                "Choose two large distinct prime numbers p and q",
                "Compute modulus n = p*q and totient φ(n) = (p-1)(q-1)",
                "Select public exponent e (commonly 65537) coprime to φ(n)",
                "Compute private key exponent d = e^(-1) mod φ(n)"
            )
        )
    )

    private fun dsaLevel14Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Why does modern mobile security (e.g. Apple Secure Enclave, Android Keystore, TLS 1.3) favor 256-bit ECC over 3072-bit RSA?",
            targetPrompt = "Select the ECC advantage",
            options = listOf(
                "Equivalent 128-bit security level with vastly smaller keys, lower battery drain, and faster operations",
                "ECC eliminates the need for private keys",
                "RSA has been banned by international treaty",
                "ECC requires no mathematical calculations"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: 256-bit ECC matches 3072-bit RSA security at a fraction of the compute and memory overhead!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Slot coefficient 'a' in the short Weierstrass elliptic curve equation y² ≡ x³ + ax + b (mod p):",
            targetPrompt = "Slot coefficient variable",
            hint = "🎮 Phoenix Cheat Code: The Weierstrass formula is y² = x³ + ax + b mod p!",
            fillCodeTemplate = "def is_on_curve(x, y, a, b, p):\n    # Weierstrass equation: y^2 = x^3 + a*x + b (mod p)\n    return (y ** 2) % p == (x ** 3 + [___] * x + b) % p",
            fillCodeWordBank = listOf("a", "b", "p", "y"),
            fillCodeCorrectToken = "a"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Match Elliptic Curve point operations with their geometric definitions:",
            targetPrompt = "Connect geometric definitions",
            hint = "🎮 Phoenix Cheat Code: Addition uses secant line; Doubling uses tangent line; Scalar multiplication repeats doublings!",
            matchLeft = listOf("Point Addition P + Q", "Point Doubling 2P", "Scalar Multiplication d · G"),
            matchRight = listOf("Secant line through P & Q, reflected across x-axis", "Tangent line at P, reflected across x-axis", "Repeated doubling and addition to produce public key Q"),
            matchSolution = mapOf(
                "Point Addition P + Q" to "Secant line through P & Q, reflected across x-axis",
                "Point Doubling 2P" to "Tangent line at P, reflected across x-axis",
                "Scalar Multiplication d · G" to "Repeated doubling and addition to produce public key Q"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Finding scalar d given Q = d*G is the ECDLP—exponentially difficult for classical computers!",
            trueFalseStatement = "Given generator point G and public point Q = d · G on an elliptic curve, finding private scalar d is computationally easy.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Dial time complexity of computing public point Q = d · G via double-and-add for a 256-bit scalar d:",
            targetPrompt = "Select scalar multiplication complexity",
            hint = "🎮 Phoenix Cheat Code: Double-and-add executes at most 256 doublings: logarithmic in scalar value d = O(log n)!",
            complexityCodeSnippet = "# Double-and-add takes at most 2*k point additions for k-bit scalar d\noperations <= 2 * 256",
            complexityDialCorrect = "O(log n)"
        )
    )

    private fun dsaLevel15Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss Challenge",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "⚔️ BOSS TRIAL 1: Which quantum algorithm threatens RSA and ECC by solving discrete logarithms and factoring in polynomial time?",
            targetPrompt = "Identify quantum threat",
            options = listOf("Shor's Algorithm", "Grover's Algorithm", "Dijkstra's Algorithm", "Bellman-Ford Algorithm"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Peter Shor discovered the polynomial-time quantum period-finding algorithm in 1994!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Boss Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "⚔️ BOSS TRIAL 2: Complete the quantum speedup property: Grover's search across N items runs in O([___](N)):",
            targetPrompt = "Slot complexity function",
            hint = "🎮 Phoenix Cheat Code: Grover provides a quadratic speedup: O(sqrt(N))!",
            fillCodeTemplate = "# Grover's quantum search provides quadratic speedup\nquantum_search_complexity = \"[___](N)\"",
            fillCodeWordBank = listOf("sqrt", "log", "exp", "fact"),
            fillCodeCorrectToken = "sqrt"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Boss Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "⚔️ BOSS TRIAL 3: Match quantum concepts with their cryptographic impacts:",
            targetPrompt = "Connect quantum impacts",
            hint = "🎮 Phoenix Cheat Code: Shor breaks RSA/ECC; Grover halves symmetric key bits; ML-KEM uses lattices!",
            matchLeft = listOf("Shor's Algorithm", "Grover's Algorithm", "Post-Quantum Cryptography"),
            matchRight = listOf("Breaks RSA & ECC in polynomial time", "Halves symmetric bit security (AES-256 needed)", "Lattice-based algorithms (ML-KEM / Kyber)"),
            matchSolution = mapOf(
                "Shor's Algorithm" to "Breaks RSA & ECC in polynomial time",
                "Grover's Algorithm" to "Halves symmetric bit security (AES-256 needed)",
                "Post-Quantum Cryptography" to "Lattice-based algorithms (ML-KEM / Kyber)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Boss Verdict",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "⚔️ BOSS TRIAL 4: Swipe left for False, right for True:",
            targetPrompt = "Evaluate quantum impact",
            hint = "🎮 Phoenix Cheat Code: Shor breaks period-finding (asymmetric); AES-256 retains 128-bit quantum security against Grover!",
            trueFalseStatement = "AES-256 is broken in polynomial time by Shor's algorithm and must be retired.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Boss Sequence",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "⚔️ BOSS TRIAL 5: Order the migration steps to defend against the Quantum Cryptanalyst:",
            targetPrompt = "Arrange defense roadmap",
            hint = "🎮 Phoenix Cheat Code: Upgrade to AES-256, deploy hybrid ML-KEM key exchange, deprecate RSA, enforce PQC signatures!",
            orderStepsInitial = listOf(
                "Adopt hybrid key exchange pairing X25519 with Post-Quantum ML-KEM",
                "Increase symmetric key lengths to AES-256 to withstand Grover's speedup",
                "Enforce post-quantum digital signature standards (ML-DSA / Dilithium)",
                "Deprecate legacy RSA and DH certificates across the network"
            ),
            orderStepsSolution = listOf(
                "Increase symmetric key lengths to AES-256 to withstand Grover's speedup",
                "Adopt hybrid key exchange pairing X25519 with Post-Quantum ML-KEM",
                "Deprecate legacy RSA and DH certificates across the network",
                "Enforce post-quantum digital signature standards (ML-DSA / Dilithium)"
            )
        )
    )

    private fun dsaLevel16Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Which cryptographic property ensures an attacker cannot take an existing hash output H(M) and reverse-engineer the original plaintext input M?",
            targetPrompt = "Select the hash property",
            options = listOf("Pre-image Resistance (One-Way Property)", "Collision Resistance", "Reversibility", "Compression Ratio"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Pre-image resistance makes hashes strictly a one-way street: finding M given H(M) is infeasible!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the SHA-256 hex digest computation in Python using hashlib:",
            targetPrompt = "Slot hexdigest method",
            hint = "🎮 Phoenix Cheat Code: .hexdigest() returns the 256-bit hash as a 64-character hexadecimal string!",
            fillCodeTemplate = "import hashlib\ndef compute_sha256(data_bytes):\n    # Return 64-char hexadecimal digest string\n    return hashlib.sha256(data_bytes).[___]()",
            fillCodeWordBank = listOf("hexdigest", "digest", "encode", "hex"),
            fillCodeCorrectToken = "hexdigest"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match hash resistance levels with their mathematical definitions:",
            targetPrompt = "Connect resistance definitions",
            hint = "🎮 Phoenix Cheat Code: Pre-image = invert hash; Second pre-image = find clone of given M; Collision = find ANY match!",
            matchLeft = listOf("Pre-image Resistance", "Second Pre-image Resistance", "Collision Resistance"),
            matchRight = listOf("Given h, infeasible to find M such that H(M) = h", "Given M1, infeasible to find M2 with H(M1) = H(M2)", "Infeasible to find ANY two distinct inputs with H(M1) = H(M2)"),
            matchSolution = mapOf(
                "Pre-image Resistance" to "Given h, infeasible to find M such that H(M) = h",
                "Second Pre-image Resistance" to "Given M1, infeasible to find M2 with H(M1) = H(M2)",
                "Collision Resistance" to "Infeasible to find ANY two distinct inputs with H(M1) = H(M2)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: The Avalanche Effect dictates that a 1-bit input flip alters ~50% of output bits!",
            trueFalseStatement = "In a secure cryptographic hash function, changing a single bit in the input message flips approximately 50% of the output bits.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial time complexity of computing a SHA-256 hash digest for a message of N bytes:",
            targetPrompt = "Select hashing complexity",
            hint = "🎮 Phoenix Cheat Code: The compression function processes message blocks sequentially: linear O(n)!",
            complexityCodeSnippet = "def hash_message(msg):\n    # Iterates over all N bytes in 64-byte blocks\n    for block in chunk_64_bytes(msg): ...",
            complexityDialCorrect = "O(n)"
        )
    )

    private fun dsaLevel17Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Which architectural paradigm powers SHA-3 (Keccak) to eliminate Merkle-Damgård length-extension vulnerabilities?",
            targetPrompt = "Select SHA-3 architecture",
            options = listOf(
                "Sponge Construction (Absorbing and Squeezing phases)",
                "Merkle-Damgård Compression",
                "Feistel Network",
                "Galois Field Matrix"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: The Sponge construction absorbs input message blocks into internal state and squeezes output blocks!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Slot the internal state bit size of standard SHA-3 Keccak-f permutation (5×5 array of 64-bit lanes):",
            targetPrompt = "Slot internal bit size",
            hint = "🎮 Phoenix Cheat Code: Keccak-f operates on a 5×5×64 = 1600-bit internal state matrix!",
            fillCodeTemplate = "# SHA-3 Keccak permutation internal state bit size\nkeccak_state_bits = [___]  # 5x5 lanes of 64 bits",
            fillCodeWordBank = listOf("1600", "512", "256", "128"),
            fillCodeCorrectToken = "1600"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Match hash constructions with their architectural characteristics:",
            targetPrompt = "Connect hash architectures",
            hint = "🎮 Phoenix Cheat Code: SHA-2 uses Merkle-Damgård; SHA-3 uses Sponge; Length extension plagues Merkle-Damgård!",
            matchLeft = listOf("SHA-256 Architecture", "SHA-3 Architecture", "Length Extension Attack"),
            matchRight = listOf("Merkle-Damgård Construction", "Sponge Construction (Absorb/Squeeze)", "Affects Merkle-Damgård, eliminated in SHA-3"),
            matchSolution = mapOf(
                "SHA-256 Architecture" to "Merkle-Damgård Construction",
                "SHA-3 Architecture" to "Sponge Construction (Absorb/Squeeze)",
                "Length Extension Attack" to "Affects Merkle-Damgård, eliminated in SHA-3"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Merkle-Damgård exposes its internal compression state as the output digest, enabling extension attacks!",
            trueFalseStatement = "In a Length Extension Attack on SHA-256, an attacker knowing Hash(Key || M) can compute Hash(Key || M || Suffix) without knowing the Key.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 4 phases of Keccak Sponge construction:",
            targetPrompt = "Arrange Sponge lifecycle",
            hint = "🎮 Phoenix Cheat Code: Pad message, absorb into rate portion, apply permutation rounds, squeeze output bytes!",
            orderStepsInitial = listOf(
                "Apply 24 rounds of Keccak-f[1600] state permutation",
                "Pad input message to block rate boundary",
                "Squeeze: Extract digest bytes from rate portion of state",
                "Absorb: XOR message blocks into rate portion of 1600-bit state"
            ),
            orderStepsSolution = listOf(
                "Pad input message to block rate boundary",
                "Absorb: XOR message blocks into rate portion of 1600-bit state",
                "Apply 24 rounds of Keccak-f[1600] state permutation",
                "Squeeze: Extract digest bytes from rate portion of state"
            )
        )
    )

    private fun dsaLevel18Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Why is naive prefix key hashing Hash(Key || Message) insecure, necessitating HMAC?",
            targetPrompt = "Identify prefix hashing flaw",
            options = listOf(
                "Merkle-Damgård hash functions are vulnerable to Length Extension Attacks when using naive key prefixing",
                "The hash function automatically prints the key to stdout",
                "Keys must always be encrypted as public RSA keys",
                "Hashes only support numbers, not text messages"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Length Extension allows an attacker to append data to Hash(Key || M) without knowing the key!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete HMAC computation in Python using standard hmac module with SHA-256:",
            targetPrompt = "Slot hash algorithm",
            hint = "🎮 Phoenix Cheat Code: Pass hashlib.sha256 as the digestmod to create an HMAC-SHA256 authenticator!",
            fillCodeTemplate = "import hmac, hashlib\ndef compute_hmac(key, message):\n    # Produce SHA-256 HMAC hexadecimal digest\n    return hmac.new(key, message, hashlib.[___]).hexdigest()",
            fillCodeWordBank = listOf("sha256", "md5", "aes", "rsa"),
            fillCodeCorrectToken = "sha256"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match HMAC parameters with their cryptographic definitions:",
            targetPrompt = "Connect HMAC constants",
            hint = "🎮 Phoenix Cheat Code: ipad uses byte 0x36, opad uses byte 0x5c, nested inside two hash layers!",
            matchLeft = listOf("Inner Pad (ipad)", "Outer Pad (opad)", "HMAC Definition"),
            matchRight = listOf("Repeated byte 0x36", "Repeated byte 0x5c", "H((K ⊕ opad) || H((K ⊕ ipad) || M))"),
            matchSolution = mapOf(
                "Inner Pad (ipad)" to "Repeated byte 0x36",
                "Outer Pad (opad)" to "Repeated byte 0x5c",
                "HMAC Definition" to "H((K ⊕ opad) || H((K ⊕ ipad) || M))"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: String '==' exits on the first mismatched byte, leaking byte-by-byte timing data!",
            trueFalseStatement = "Comparing HMAC authentication tags using Python's standard string equality '==' is safe from timing attacks.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 3 stages of calculating an HMAC:",
            targetPrompt = "Arrange HMAC steps",
            hint = "🎮 Phoenix Cheat Code: XOR key with ipad, hash with message, XOR key with opad, hash outer result!",
            orderStepsInitial = listOf(
                "Compute outer hash: Final_HMAC = Hash((K ⊕ opad) || H_inner)",
                "Compute inner hash: H_inner = Hash((K ⊕ ipad) || Message)",
                "XOR key with inner pad (0x36) and outer pad (0x5c)"
            ),
            orderStepsSolution = listOf(
                "XOR key with inner pad (0x36) and outer pad (0x5c)",
                "Compute inner hash: H_inner = Hash((K ⊕ ipad) || Message)",
                "Compute outer hash: Final_HMAC = Hash((K ⊕ opad) || H_inner)"
            )
        )
    )

    private fun dsaLevel19Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "In a public-key digital signature system, which key must the signer use to create the signature?",
            targetPrompt = "Identify signing key",
            options = listOf("Signer's Private Key", "Signer's Public Key", "Recipient's Public Key", "Certificate Authority Root Key"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Only the signer has access to their private key—this guarantees non-repudiation!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete digital signature verification using the signer's public key:",
            targetPrompt = "Slot verify method",
            hint = "🎮 Phoenix Cheat Code: .verify() checks that the signature matches the message digest under the public key!",
            fillCodeTemplate = "def verify_signature(signer_public_key, signature, digest):\n    # Return boolean verification result\n    return signer_public_key.[___](signature, digest)",
            fillCodeWordBank = listOf("verify", "sign", "decrypt", "encrypt"),
            fillCodeCorrectToken = "verify"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match Public Key Infrastructure (PKI) components with their functions:",
            targetPrompt = "Connect PKI roles",
            hint = "🎮 Phoenix Cheat Code: Signatures provide non-repudiation, X.509 binds identities, OCSP checks revocation!",
            matchLeft = listOf("Digital Signature", "X.509 Certificate", "OCSP Protocol"),
            matchRight = listOf("Integrity & Non-repudiation", "Binds public key to domain/identity", "Real-time certificate revocation check"),
            matchSolution = mapOf(
                "Digital Signature" to "Integrity & Non-repudiation",
                "X.509 Certificate" to "Binds public key to domain/identity",
                "OCSP Protocol" to "Real-time certificate revocation check"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Digital signatures guarantee BOTH message integrity and legal non-repudiation!",
            trueFalseStatement = "A cryptographic digital signature proves message integrity and guarantees non-repudiation.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 4 steps of X.509 PKI certificate chain validation:",
            targetPrompt = "Arrange PKI trust validation",
            hint = "🎮 Phoenix Cheat Code: Extract leaf, verify with intermediate CA, verify with trusted root, check OCSP revocation!",
            orderStepsInitial = listOf(
                "Check validity date ranges and query OCSP for revocation status",
                "Verify leaf certificate signature using Intermediate CA public key",
                "Extract server's leaf public key certificate",
                "Verify Intermediate CA signature against local trusted Root CA anchor"
            ),
            orderStepsSolution = listOf(
                "Extract server's leaf public key certificate",
                "Verify leaf certificate signature using Intermediate CA public key",
                "Verify Intermediate CA signature against local trusted Root CA anchor",
                "Check validity date ranges and query OCSP for revocation status"
            )
        )
    )

    private fun dsaLevel20Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss Challenge",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "⚔️ BOSS TRIAL 1: According to the Birthday Paradox, approximately how many random hash trials are required to find a collision in an n-bit hash?",
            targetPrompt = "Calculate collision complexity",
            options = listOf("2^(n/2)", "2^n", "n²", "n / 2"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: The Birthday Paradox dramatically reduces collision resistance from 2^n to approximately 2^(n/2) operations!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Boss Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "⚔️ BOSS TRIAL 2: Complete the Birthday Bound exponent calculation for an n-bit hash:",
            targetPrompt = "Slot division operator",
            hint = "🎮 Phoenix Cheat Code: Birthday attack effort scales as 2^(n/2) operations!",
            fillCodeTemplate = "def birthday_attack_operations(hash_bits):\n    # Collision effort is 2^(hash_bits // 2)\n    return 2 ** (hash_bits [___] 2)",
            fillCodeWordBank = listOf("//", "*", "+", "%"),
            fillCodeCorrectToken = "//"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Boss Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "⚔️ BOSS TRIAL 3: Match hash algorithms with their collision security status:",
            targetPrompt = "Connect security status",
            hint = "🎮 Phoenix Cheat Code: MD5 broken in seconds, SHA-1 shattered in 2017, SHA-256 stands unyielding at 2^128!",
            matchLeft = listOf("MD5 Collision Security", "SHA-1 Collision Security", "SHA-256 Collision Security"),
            matchRight = listOf("Broken: Collisions found in seconds", "Broken: First PDF collision demonstrated in 2017", "Secure: Requires 2^128 operations (Infeasible)"),
            matchSolution = mapOf(
                "MD5 Collision Security" to "Broken: Collisions found in seconds",
                "SHA-1 Collision Security" to "Broken: First PDF collision demonstrated in 2017",
                "SHA-256 Collision Security" to "Secure: Requires 2^128 operations (Infeasible)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Boss Verdict",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "⚔️ BOSS TRIAL 4: Swipe left for False, right for True:",
            targetPrompt = "Evaluate hash security",
            hint = "🎮 Phoenix Cheat Code: MD5's broken collision resistance allows rogue CA certificate generation!",
            trueFalseStatement = "Because finding collisions in MD5 takes only seconds on commodity laptops, MD5 is completely prohibited in digital certificates.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Boss Sequence",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "⚔️ BOSS TRIAL 5: Order the defense protocol to slay the Collision Beast and enforce cryptographic integrity:",
            targetPrompt = "Arrange integrity protocol",
            hint = "🎮 Phoenix Cheat Code: Deprecate broken MD5/SHA-1, migrate to SHA-256/SHA-3, enforce constant-time tag comparisons!",
            orderStepsInitial = listOf(
                "Migrate digital certificates and PKI roots to SHA-256 and SHA-3",
                "Enforce constant-time comparison on all HMAC and signature verification tags",
                "Deprecate broken collision-vulnerable hashes (MD5 and SHA-1)",
                "Implement certificate transparency logging to detect rogue CAs"
            ),
            orderStepsSolution = listOf(
                "Deprecate broken collision-vulnerable hashes (MD5 and SHA-1)",
                "Migrate digital certificates and PKI roots to SHA-256 and SHA-3",
                "Enforce constant-time comparison on all HMAC and signature verification tags",
                "Implement certificate transparency logging to detect rogue CAs"
            )
        )
    )

    private fun dsaLevel21Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "How does TLS 1.3 achieve a 1-RTT (Round Trip Time) handshake compared to TLS 1.2's 2-RTT handshake?",
            targetPrompt = "Identify 1-RTT mechanism",
            options = listOf(
                "Client sends speculative Diffie-Hellman key share in ClientHello",
                "Server pre-encrypts data before connection is established",
                "Client and server use hardcoded pre-shared master keys",
                "TLS 1.3 skips digital certificate verification entirely"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: In TLS 1.3, the client speculatively guesses the key agreement group and sends its public key share right in the ClientHello!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the TLS 1.3 cipher suite prefix validation in Python:",
            targetPrompt = "Select prefix check method",
            hint = "🎮 Phoenix Cheat Code: TLS 1.3 AEAD cipher suites universally start with TLS_AES_ (e.g. TLS_AES_256_GCM_SHA384)!",
            fillCodeTemplate = "def is_tls13_cipher(suite_name):\n    # Check for modern TLS 1.3 AEAD cipher suite prefix\n    return suite_name.[___](\"TLS_AES_\")",
            fillCodeWordBank = listOf("startswith", "endswith", "split", "find"),
            fillCodeCorrectToken = "startswith"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match TLS 1.3 modernizations with their cryptographic justifications:",
            targetPrompt = "Connect TLS 1.3 features",
            hint = "🎮 Phoenix Cheat Code: Ephemeral DH provides forward secrecy; AEAD replaces flawed CBC padding; encrypted SNI stops eavesdroppers!",
            matchLeft = listOf("Removal of RSA Key Transport", "Removal of CBC Mode & RC4", "Encrypted Extensions"),
            matchRight = listOf("Guarantees Forward Secrecy via Ephemeral DH", "Eliminates Padding Oracle & Stream Cipher attacks", "Protects Server Certificate & SNI from Eavesdroppers"),
            matchSolution = mapOf(
                "Removal of RSA Key Transport" to "Guarantees Forward Secrecy via Ephemeral DH",
                "Removal of CBC Mode & RC4" to "Eliminates Padding Oracle & Stream Cipher attacks",
                "Encrypted Extensions" to "Protects Server Certificate & SNI from Eavesdroppers"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Assess TLS 1.3 forward secrecy",
            hint = "🎮 Phoenix Cheat Code: TLS 1.3 completely deprecated static RSA key exchange to guarantee forward secrecy for all sessions!",
            trueFalseStatement = "In TLS 1.3, perfect forward secrecy (PFS) is mandatory because static RSA key exchange has been completely deprecated.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 4 steps of a standard TLS 1.3 1-RTT cryptographic handshake:",
            targetPrompt = "Arrange TLS 1.3 handshake sequence",
            hint = "🎮 Phoenix Cheat Code: 1. ClientHello with key share -> 2. ServerHello & Certificate -> 3. Client Finished -> 4. Encrypted Application Data!",
            orderStepsInitial = listOf(
                "Server sends ServerHello + EncryptedExtensions + Certificate + Finished",
                "Client sends ClientHello with speculative Key Share (e.g. X25519)",
                "Client sends Finished MAC confirmation to finalize handshake",
                "Bidirectional encrypted Application Data transmission begins"
            ),
            orderStepsSolution = listOf(
                "Client sends ClientHello with speculative Key Share (e.g. X25519)",
                "Server sends ServerHello + EncryptedExtensions + Certificate + Finished",
                "Client sends Finished MAC confirmation to finalize handshake",
                "Bidirectional encrypted Application Data transmission begins"
            )
        )
    )


    private fun dsaLevel22Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is the key functional difference between IPsec AH (Authentication Header) and IPsec ESP (Encapsulating Security Payload)?",
            targetPrompt = "Differentiate AH vs ESP",
            options = listOf(
                "ESP provides encryption and authentication, while AH provides integrity without encryption",
                "AH encrypts the payload, while ESP only signs packet headers",
                "ESP operates at Layer 7 Application, while AH operates at Layer 2 Data Link",
                "AH requires pre-shared keys, while ESP requires RSA certificates only"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: ESP provides Confidentiality + Integrity + Authentication. AH provides Integrity + Authentication only (no encryption)!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the IPsec tunnel mode packet construction in Python by prepending the outer IP header:",
            targetPrompt = "Concatenate packet layers",
            hint = "🎮 Phoenix Cheat Code: In Tunnel mode, the new outer IP header is prepended (+) to the ESP header and encrypted original IP packet!",
            fillCodeTemplate = "def construct_ipsec_tunnel_packet(new_outer_ip, esp_header, original_ip_packet):\n    # Tunnel mode encapsulates entire original packet inside new outer IP wrapper\n    return new_outer_ip [___] esp_header + original_ip_packet",
            fillCodeWordBank = listOf("+", "*", "&", "^"),
            fillCodeCorrectToken = "+"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match IPsec modes and protocols with their architectural behaviors:",
            targetPrompt = "Connect IPsec concepts",
            hint = "🎮 Phoenix Cheat Code: Transport = host-to-host (payload only); Tunnel = gateway-to-gateway (entire packet); IKE = automated key negotiation!",
            matchLeft = listOf("IPsec Transport Mode", "IPsec Tunnel Mode", "IKE Protocol (IKEv2)"),
            matchRight = listOf(
                "Encrypts payload only; preserves original IP header (Host-to-Host)",
                "Encapsulates entire original IP packet in new IP header (Gateway-to-Gateway)",
                "Negotiates security associations (SAs) and establishes shared keys"
            ),
            matchSolution = mapOf(
                "IPsec Transport Mode" to "Encrypts payload only; preserves original IP header (Host-to-Host)",
                "IPsec Tunnel Mode" to "Encapsulates entire original IP packet in new IP header (Gateway-to-Gateway)",
                "IKE Protocol (IKEv2)" to "Negotiates security associations (SAs) and establishes shared keys"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Assess tunnel mode encapsulation",
            hint = "🎮 Phoenix Cheat Code: Tunnel mode wraps the whole original IP packet inside ESP, hiding internal network topologies from intermediate routers!",
            trueFalseStatement = "In IPsec Tunnel mode, the original source and destination IP addresses are completely hidden and encrypted inside the ESP payload.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 4 steps of IKEv2 Security Association (SA) establishment:",
            targetPrompt = "Arrange IKEv2 negotiation phases",
            hint = "🎮 Phoenix Cheat Code: 1. IKE_SA_INIT (DH exchange) -> 2. IKE_AUTH (authenticate peers) -> 3. CREATE_CHILD_SA (ESP parameters) -> 4. Secure data transit!",
            orderStepsInitial = listOf(
                "IKE_AUTH: Authenticate peer identities via digital certificates or PSK",
                "IKE_SA_INIT: Negotiate crypto algorithms and perform Diffie-Hellman key exchange",
                "CREATE_CHILD_SA: Negotiate IPsec ESP security associations for user data",
                "Encrypted IPsec ESP tunnel data traffic commences between gateways"
            ),
            orderStepsSolution = listOf(
                "IKE_SA_INIT: Negotiate crypto algorithms and perform Diffie-Hellman key exchange",
                "IKE_AUTH: Authenticate peer identities via digital certificates or PSK",
                "CREATE_CHILD_SA: Negotiate IPsec ESP security associations for user data",
                "Encrypted IPsec ESP tunnel data traffic commences between gateways"
            )
        )
    )

    private fun dsaLevel23Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Which packet attributes make up the classical '5-tuple' evaluated by stateful network firewalls?",
            targetPrompt = "Identify firewall 5-tuple",
            options = listOf(
                "Source IP, Destination IP, Source Port, Destination Port, Transport Protocol",
                "MAC Address, IP Address, Subnet Mask, Gateway, DNS Server",
                "Payload Hash, Sequence Number, Window Size, TTL, Checksum",
                "Public Key, Private Key, Session ID, Cipher Suite, Nonce"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: The 5-tuple consists of Source/Destination IP, Source/Destination Port, and Protocol (TCP/UDP)!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the stateful firewall rule in Python to permit returning packets for active connections:",
            targetPrompt = "Slot connection state token",
            hint = "🎮 Phoenix Cheat Code: Stateful firewalls track TCP handshakes; ESTABLISHED session traffic is permitted through!",
            fillCodeTemplate = "def filter_packet(packet, connection_table):\n    # Permit inbound packet if TCP connection is active\n    if packet.connection_state == [___]:\n        return \"ALLOW\"\n    return \"DROP\"",
            fillCodeWordBank = listOf("\"ESTABLISHED\"", "\"SYN_SENT\"", "\"CLOSED\"", "\"UNKNOWN\""),
            fillCodeCorrectToken = "\"ESTABLISHED\""
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match network firewall architectures with their inspection capabilities:",
            targetPrompt = "Connect firewall types",
            hint = "🎮 Phoenix Cheat Code: Stateless = isolated packets; Stateful = tracks TCP sessions; NGFW = Layer 7 application inspection!",
            matchLeft = listOf("Stateless Packet Filter", "Stateful Inspection Firewall", "Next-Gen Firewall (NGFW)"),
            matchRight = listOf(
                "Evaluates individual packets in isolation without tracking session state",
                "Tracks TCP handshake states (SYN, ESTABLISHED) across full bidirectional flows",
                "Deep packet inspection (DPI) analyzing Layer 7 applications and user identities"
            ),
            matchSolution = mapOf(
                "Stateless Packet Filter" to "Evaluates individual packets in isolation without tracking session state",
                "Stateful Inspection Firewall" to "Tracks TCP handshake states (SYN, ESTABLISHED) across full bidirectional flows",
                "Next-Gen Firewall (NGFW)" to "Deep packet inspection (DPI) analyzing Layer 7 applications and user identities"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Assess Zero Trust philosophy",
            hint = "🎮 Phoenix Cheat Code: Zero Trust treats every network segment—internal or external—as hostile and unverified!",
            trueFalseStatement = "The core tenet of Zero Trust Architecture is 'Never Trust, Always Verify', requiring continuous authentication and microsegmentation even inside internal network perimeters.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 4 stages of Zero Trust access evaluation for an enterprise resource request:",
            targetPrompt = "Arrange Zero Trust pipeline",
            hint = "🎮 Phoenix Cheat Code: 1. Identity & device verification -> 2. Least privilege token -> 3. Microsegmentation transit -> 4. Continuous behavioral monitoring!",
            orderStepsInitial = listOf(
                "Issue short-lived cryptographically signed token with least-privilege permissions",
                "Device posture health check and multi-factor identity authentication (MFA)",
                "Route traffic through microsegmented gateway with mutual TLS encryption",
                "Continuously assess telemetry signals and revoke access on anomalous behavior"
            ),
            orderStepsSolution = listOf(
                "Device posture health check and multi-factor identity authentication (MFA)",
                "Issue short-lived cryptographically signed token with least-privilege permissions",
                "Route traffic through microsegmented gateway with mutual TLS encryption",
                "Continuously assess telemetry signals and revoke access on anomalous behavior"
            )
        )
    )

    private fun dsaLevel24Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "How do cryptographic SYN Cookies mitigate TCP SYN Flood denial-of-service attacks?",
            targetPrompt = "Identify SYN Cookie defense",
            options = listOf(
                "Encodes connection state into Initial Sequence Number (ISN) without allocating memory",
                "Blocks all incoming SYN packets that do not originate from whitelisted IP addresses",
                "Requires the client to solve an RSA public key challenge before sending SYN-ACK",
                "Limits total TCP connections across the network to 1024 concurrent sessions"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: SYN Cookies encode client IP, port, and a secret key into the 32-bit TCP sequence number, allocating ZERO server state until the final ACK!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete the SYN Cookie sequence number verification formula in Python:",
            targetPrompt = "Slot increment operator",
            hint = "🎮 Phoenix Cheat Code: In the TCP 3-way handshake, the client's final ACK sequence number is server ISN + 1!",
            fillCodeTemplate = "def verify_syn_cookie(ack_seq, expected_cookie):\n    # Valid ACK sequence number matches cookie incremented by 1\n    return ack_seq == expected_cookie [___] 1",
            fillCodeWordBank = listOf("+", "-", "*", "^"),
            fillCodeCorrectToken = "+"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match network layer attacks with their primary mitigation mechanisms:",
            targetPrompt = "Connect attacks to defenses",
            hint = "🎮 Phoenix Cheat Code: DAI stops fake ARP responses, DNSSEC signs DNS records, and SYN cookies prevent memory exhaustion!",
            matchLeft = listOf("ARP Spoofing / Poisoning", "DNS Cache Poisoning", "TCP SYN Flood DoS"),
            matchRight = listOf(
                "Dynamic ARP Inspection (DAI) with DHCP Snooping binding table",
                "DNSSEC with RRSIG and DS cryptographic validation chains",
                "Cryptographic SYN Cookies without server backlog allocation"
            ),
            matchSolution = mapOf(
                "ARP Spoofing / Poisoning" to "Dynamic ARP Inspection (DAI) with DHCP Snooping binding table",
                "DNS Cache Poisoning" to "DNSSEC with RRSIG and DS cryptographic validation chains",
                "TCP SYN Flood DoS" to "Cryptographic SYN Cookies without server backlog allocation"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Assess DNSSEC trust chain",
            hint = "🎮 Phoenix Cheat Code: DNSSEC establishes a cryptographic hierarchy of RRSIG signatures tied to the root key!",
            trueFalseStatement = "In DNSSEC, recursive resolvers verify DNS record authenticity by traversing a cryptographic chain of trust from the Root Zone (.) trust anchor down to the authoritative domain.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 4 steps of a cryptographic SYN Cookie exchange under a SYN flood attack:",
            targetPrompt = "Arrange SYN Cookie flow",
            hint = "🎮 Phoenix Cheat Code: 1. SYN flood arrives -> 2. Stateless SYN cookie sent -> 3. Client returns ACK -> 4. Server verifies and allocates socket!",
            orderStepsInitial = listOf(
                "Legitimate client replies with ACK packet containing (Cookie + 1) in acknowledgement field",
                "Attacker floods server with half-open SYN packets from spoofed IPs",
                "Server verifies cookie math and instantiates TCP connection socket only upon valid ACK",
                "Server computes 32-bit cryptographic hash (SYN Cookie) and responds with SYN-ACK without saving state"
            ),
            orderStepsSolution = listOf(
                "Attacker floods server with half-open SYN packets from spoofed IPs",
                "Server computes 32-bit cryptographic hash (SYN Cookie) and responds with SYN-ACK without saving state",
                "Legitimate client replies with ACK packet containing (Cookie + 1) in acknowledgement field",
                "Server verifies cookie math and instantiates TCP connection socket only upon valid ACK"
            )
        )
    )

    private fun dsaLevel25Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss Challenge",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "⚔️ BOSS TRIAL 1: Why does TLS 1.3 mandate Authenticated Encryption with Associated Data (AEAD) algorithms and forbid Mac-then-Encrypt CBC mode?",
            targetPrompt = "Analyze AEAD requirement",
            options = listOf(
                "To eliminate cryptographic padding oracle attacks (such as POODLE and Lucky13)",
                "Because CBC mode is too computationally fast and exhausts server CPUs",
                "To permit plaintext transmission of sensitive user authentication tokens",
                "AEAD reduces symmetric cipher key sizes from 256 bits down to 32 bits"
            ),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Mac-then-Encrypt CBC mode suffered from timing side-channels and padding oracles; AEAD combines encryption and integrity securely!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Boss Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "⚔️ BOSS TRIAL 2: Complete the IPsec security association protocol assignment in Python:",
            targetPrompt = "Slot protocol token",
            hint = "🎮 Phoenix Cheat Code: ESP (Encapsulating Security Payload) is required for confidentiality and encryption in IPsec VPN tunnels!",
            fillCodeTemplate = "def configure_vpn_tunnel():\n    # Enforce Encapsulating Security Payload for full tunnel encryption\n    protocol = [___]\n    return {\"mode\": \"TUNNEL\", \"protocol\": protocol}",
            fillCodeWordBank = listOf("\"ESP\"", "\"AH\"", "\"ICMP\"", "\"ARP\""),
            fillCodeCorrectToken = "\"ESP\""
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Boss Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "⚔️ BOSS TRIAL 3: Match Network Security defensive layers with their protection scope:",
            targetPrompt = "Connect defense layers",
            hint = "🎮 Phoenix Cheat Code: TLS 1.3 secures application sessions, IPsec encrypts network packets, DNSSEC secures name resolution!",
            matchLeft = listOf("TLS 1.3 (Transport Layer)", "IPsec ESP (Network Layer)", "DNSSEC (Application Layer)"),
            matchRight = listOf(
                "End-to-end encrypted session between client and application",
                "Secure site-to-site VPN encrypting all IP packet traffic",
                "Cryptographic authenticity preventing DNS spoofing"
            ),
            matchSolution = mapOf(
                "TLS 1.3 (Transport Layer)" to "End-to-end encrypted session between client and application",
                "IPsec ESP (Network Layer)" to "Secure site-to-site VPN encrypting all IP packet traffic",
                "DNSSEC (Application Layer)" to "Cryptographic authenticity preventing DNS spoofing"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Boss Verdict",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "⚔️ BOSS TRIAL 4: Swipe left for False, right for True:",
            targetPrompt = "Evaluate microsegmentation defense",
            hint = "🎮 Phoenix Cheat Code: Microsegmentation isolates lateral movement—each internal workload requires strict authentication!",
            trueFalseStatement = "In a Zero Trust microsegmentation model, compromise of a single internal host cannot automatically grant access to neighboring database servers.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Grand Finale",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "⚔️ GRAND FINALE: Order the defense protocol to vanquish the Cyber Titan and secure the global network 🏆:",
            targetPrompt = "Arrange defense blueprint",
            hint = "🎮 Phoenix Cheat Code: 1. TLS & DNSSEC -> 2. Stateful Firewalls & SYN Cookies -> 3. IPsec ESP Tunnels -> 4. Zero Trust Microsegmentation!",
            orderStepsInitial = listOf(
                "Establish encrypted IPsec ESP tunnels across distributed data centers",
                "Enforce TLS 1.3 with ephemeral key exchange and DNSSEC validation",
                "Implement Zero Trust continuous identity verification and microsegmentation",
                "Deploy stateful firewalls with cryptographically generated SYN Cookies"
            ),
            orderStepsSolution = listOf(
                "Enforce TLS 1.3 with ephemeral key exchange and DNSSEC validation",
                "Deploy stateful firewalls with cryptographically generated SYN Cookies",
                "Establish encrypted IPsec ESP tunnels across distributed data centers",
                "Implement Zero Trust continuous identity verification and microsegmentation"
            )
        )
    )

}
