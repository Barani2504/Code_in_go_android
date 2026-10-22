package com.simats.codeingo.data.repository

import com.simats.codeingo.data.model.DSAExerciseType

/**
 * 🏕️ BasecampQuestionsData — Chapter 0: Foundations Primer
 * Contains all 45 pedagogy-reviewed questions for Levels 101 to 105:
 *  - Level 101: Stage 1: Bits, Bytes & Hex (c0_l1) - 10 questions
 *  - Level 102: Stage 2: Clock Math (Modulo) (c0_l2) - 10 questions
 *  - Level 103: Stage 3: XOR Magic (c0_l3) - 10 questions
 *  - Level 104: Stage 4: Who Attacks What? CIA Triad (c0_l4) - 10 questions
 *  - Level 105: Stage 5: Boss: Basecamp Trial (c0_boss) - 5 questions
 */
object BasecampQuestionsData {

    fun getQuestionsForLevel(levelNumber: Int): List<QuizQuestion> = when (levelNumber) {
        101 -> level101Questions()
        102 -> level102Questions()
        103 -> level103Questions()
        104 -> level104Questions()
        105 -> level105BossQuestions()
        else -> level101Questions()
    }

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 101: BITS, BYTES & HEXADECIMAL (c0_l1)
    // ══════════════════════════════════════════════════════════════════
    private fun level101Questions(): List<QuizQuestion> = listOf(
        // Q1 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l1_q1",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Byte Foundations",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "How many individual bits make up exactly one standard byte in computer memory?",
            targetPrompt = "Select the correct bit width",
            options = listOf("8 bits", "4 bits", "16 bits", "32 bits"),
            correctOptionIndex = 0,
            conceptTag = "byte_definition",
            misconceptionTag = "confusing_nibble_or_word",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: 1 byte = 8 bits, which can store 2^8 = 256 distinct values!",
            hint1 = "Think of powers of 2. An octet is another common name for this unit.",
            hint2 = "Half of this unit is called a 'nibble' (4 bits).",
            hint3 = "2^8 = 256 possible patterns: 8 bits make one byte.",
            explanation = "One byte consists of exactly 8 bits. With 8 binary switches (0 or 1), a byte can represent 2^8 = 256 unique values (0 to 255).",
            wrongOptionExplanations = mapOf(
                1 to "4 bits is known as a 'nibble' (half a byte).",
                2 to "16 bits is a 'half-word' or 2 bytes.",
                3 to "32 bits is a standard 4-byte word."
            )
        ),

        // Q2 (FILL_CODE)
        QuizQuestion(
            id = "c0_l1_q2",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Hex Conversion",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Complete the Python snippet to convert decimal 15 into its single-digit hexadecimal representation:",
            targetPrompt = "Tap the hex token",
            fillCodeTemplate = "# Decimal 15 maps to the highest single hex digit\nhex_digit = [___](15)  # Expected output: '0xf'",
            fillCodeWordBank = listOf("hex", "bin", "oct", "chr"),
            fillCodeCorrectToken = "hex",
            conceptTag = "hex_representation",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Use Python's built-in hex() function to view base-16!",
            hint1 = "We want base-16 (hexadecimal).",
            hint2 = "Python has bin() for binary, oct() for octal, and hex() for hexadecimal.",
            hint3 = "The function name is hex.",
            explanation = "In hexadecimal (base-16), decimal 10 through 15 are represented by letters A through F. Decimal 15 is hex 0xF. hex(15) returns '0xf'."
        ),

        // Q3 (MATCH_PAIRS)
        QuizQuestion(
            id = "c0_l1_q3",
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Nibble Matcher",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Connect each decimal value to its exact 4-bit binary nibble representation:",
            targetPrompt = "Match value to 4-bit nibble",
            matchLeft = listOf("Decimal 0", "Decimal 5", "Decimal 10", "Decimal 15"),
            matchRight = listOf("0000", "0101", "1010", "1111"),
            matchSolution = mapOf(
                "Decimal 0" to "0000",
                "Decimal 5" to "0101",
                "Decimal 10" to "1010",
                "Decimal 15" to "1111"
            ),
            conceptTag = "binary_nibble_weights",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Nibble weights are 8, 4, 2, 1. For example, 10 = 8 + 2 = 1010!",
            hint1 = "5 = 4 + 1 -> 0101.",
            hint2 = "10 in binary has the 8s bit and 2s bit on: 1010.",
            hint3 = "15 turns all 4 bits on: 8 + 4 + 2 + 1 = 1111.",
            explanation = "A 4-bit nibble has place values 8, 4, 2, and 1. 0 is 0000; 5 is 4+1 (0101); 10 is 8+2 (1010); and 15 is 8+4+2+1 (1111)."
        ),

        // Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c0_l1_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Hex Digit Capacity",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Fact check this computer architecture statement:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "One single hexadecimal character (0-9, A-F) represents exactly 4 bits of binary data (one nibble).",
            trueFalseIsCorrectTrue = true,
            conceptTag = "hex_nibble_correspondence",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: 16 = 2^4. Every hex character maps directly to 4 binary bits!",
            hint1 = "Base 16 has 16 symbols. How many bits are needed to count from 0 to 15?",
            hint2 = "2^4 = 16. Four bits have 16 combinations (0000 to 1111).",
            hint3 = "This statement is true.",
            explanation = "Because 16 = 2^4, every hexadecimal digit corresponds to exactly 4 binary bits. This is why two hex characters cleanly represent one 8-bit byte."
        ),

        // Q5 (ORDER_STEPS)
        QuizQuestion(
            id = "c0_l1_q5",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Bit Weight Order",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Arrange the positional weights of an 8-bit byte from Most Significant Bit (MSB, Bit 7) down to Least Significant Bit (LSB, Bit 0):",
            targetPrompt = "Order bit weights high to low",
            orderStepsInitial = listOf(
                "Weight 1 (2^0, Bit 0 LSB)",
                "Weight 128 (2^7, Bit 7 MSB)",
                "Weight 16 (2^4, Bit 4)",
                "Weight 64 (2^6, Bit 6)"
            ),
            orderStepsSolution = listOf(
                "Weight 128 (2^7, Bit 7 MSB)",
                "Weight 64 (2^6, Bit 6)",
                "Weight 16 (2^4, Bit 4)",
                "Weight 1 (2^0, Bit 0 LSB)"
            ),
            conceptTag = "positional_binary_weights",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: MSB is the highest power of 2 (128) down to LSB (1)!",
            hint1 = "Start with the largest power of 2: 2^7 = 128.",
            hint2 = "Next is 2^6 = 64, followed by 2^4 = 16.",
            hint3 = "The lowest weight is 2^0 = 1.",
            explanation = "In big-endian notation, bit 7 is the Most Significant Bit (MSB) with weight 2^7 = 128. Bit weights decrease down to bit 0 with weight 2^0 = 1."
        ),

        // Q6 (COMPLEXITY_DIAL)
        QuizQuestion(
            id = "c0_l1_q6",
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Register State Count",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "What is the asymptotic state space size (number of unique combinations) of an n-bit cryptographic register?",
            targetPrompt = "Set the state growth dial",
            complexityDialCorrect = "O(2^n)",
            conceptTag = "exponential_keyspace",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Each added bit doubles the total possible combinations (2^n)!",
            hint1 = "1 bit has 2 states. 2 bits have 4 states. 3 bits have 8 states.",
            hint2 = "Every additional bit multiplies the possibilities by 2.",
            hint3 = "This exponential growth is represented as O(2^n).",
            explanation = "Each binary bit has 2 states (0 or 1). For n bits, the number of distinct states is 2 × 2 × ... = 2^n. This exponential growth O(2^n) is the foundation of cryptographic key strength."
        ),

        // Q7 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l1_q7",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "ASCII Character Mapping",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "In standard ASCII encoding, which character is represented by decimal 65 (hex 0x41, binary 01000001)?",
            targetPrompt = "Select the ASCII character",
            options = listOf("Letter 'A'", "Digit '0'", "Letter 'a'", "Space character ' '"),
            correctOptionIndex = 0,
            conceptTag = "ascii_encoding",
            misconceptionTag = "case_sensitivity_ascii",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Uppercase 'A' starts at 65 (0x41). Lowercase 'a' starts at 97 (0x61)!",
            hint1 = "It is an uppercase letter at the beginning of the Latin alphabet.",
            hint2 = "In hex, 0x41: 4*16 + 1 = 65.",
            hint3 = "Decimal 65 is capital letter 'A'.",
            explanation = "ASCII defines capital letter 'A' as decimal 65 (hex 0x41). Lowercase 'a' is 97 (0x61), digit '0' is 48 (0x30), and space is 32 (0x20).",
            wrongOptionExplanations = mapOf(
                1 to "Digit '0' is ASCII code 48 (0x30).",
                2 to "Lowercase 'a' is ASCII code 97 (0x61), 32 higher than uppercase 'A'.",
                3 to "Space ' ' is ASCII code 32 (0x20)."
            )
        ),

        // Q8 (FILL_CODE)
        QuizQuestion(
            id = "c0_l1_q8",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "ASCII Ordinal Lookup",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Fill in the Python function to inspect the integer byte code of uppercase character 'B':",
            targetPrompt = "Tap the ordinal function",
            fillCodeTemplate = "# Obtain numeric ASCII code for 'B' (expected: 66)\ncode = [___]('B')",
            fillCodeWordBank = listOf("ord", "chr", "int", "len"),
            fillCodeCorrectToken = "ord",
            conceptTag = "ascii_ord_conversion",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: ord() gets the numeric code of a character; chr() turns a number into a character!",
            hint1 = "The function returns the 'ordinal' value of a character.",
            hint2 = "ord('A') returns 65.",
            hint3 = "Use ord.",
            explanation = "In Python, ord(c) returns the integer ASCII/Unicode code point of a character. ord('B') returns 66."
        ),

        // Q9 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c0_l1_q9",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Key Length in Hex",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Verify this cryptographic key representation calculation:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "A standard 256-bit AES encryption key can be written as exactly 64 hexadecimal characters.",
            trueFalseIsCorrectTrue = true,
            conceptTag = "hex_key_formatting",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: 256 bits divided by 4 bits per hex character = 64 hex characters!",
            hint1 = "Each hex character stores 4 bits.",
            hint2 = "Calculate 256 / 4.",
            hint3 = "256 / 4 = 64. The statement is true.",
            explanation = "Since each hexadecimal character encodes 4 bits, a 256-bit key requires 256 / 4 = 64 hexadecimal digits (or 32 raw bytes)."
        ),

        // Q10 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l1_q10",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Maximum Byte Value",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "What is the maximum unsigned integer value that can be held in a single 8-bit byte (0xFF)?",
            targetPrompt = "Select maximum byte value",
            options = listOf("255", "256", "128", "65535"),
            correctOptionIndex = 0,
            conceptTag = "byte_range",
            misconceptionTag = "off_by_one_range",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: 8 bits hold 256 total values, from 0 up to 255 (2^8 - 1)!",
            hint1 = "Remember that counting starts at 0.",
            hint2 = "2^8 = 256 total numbers.",
            hint3 = "Range is 0 to 255. The maximum value is 255.",
            explanation = "An 8-bit byte has 2^8 = 256 total combinations. Since 0 is the minimum unsigned value, the maximum value is 256 - 1 = 255 (binary 11111111, hex 0xFF).",
            wrongOptionExplanations = mapOf(
                1 to "256 is the count of values, but because 0 is included, the max value is 255.",
                2 to "128 is 2^7, only the value of the MSB bit.",
                3 to "65535 is the max value of a 16-bit word (2 bytes)."
            )
        )
    )

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 102: CLOCK MATH & MODULO (c0_l2)
    // ══════════════════════════════════════════════════════════════════
    private fun level102Questions(): List<QuizQuestion> = listOf(
        // Q1 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l2_q1",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Clock Arithmetic",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "On a standard 12-hour clock, what time is represented by 15 o'clock (15 mod 12)?",
            targetPrompt = "Select the clock hour",
            options = listOf("3 o'clock", "15 o'clock", "5 o'clock", "12 o'clock"),
            correctOptionIndex = 0,
            conceptTag = "clock_math_intro",
            misconceptionTag = "direct_subtraction_vs_modulo",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: 15 / 12 = 1 full lap with 3 hours remainder!",
            hint1 = "Subtract 12 from 15 to find where the hand points after 1 full lap.",
            hint2 = "15 - 12 = 3.",
            hint3 = "15 mod 12 is 3.",
            explanation = "Clock math is modular arithmetic. 15 hours on a 12-hour clock means completing 1 full cycle of 12 hours plus 3 remaining hours: 15 mod 12 = 3.",
            wrongOptionExplanations = mapOf(
                1 to "A 12-hour clock wraps around and cannot show 15.",
                2 to "5 o'clock would be 17 mod 12.",
                3 to "12 o'clock is 0 mod 12 (or 12 mod 12 = 0)."
            )
        ),

        // Q2 (FILL_CODE)
        QuizQuestion(
            id = "c0_l2_q2",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Modulo Operator",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Use the modulo operator to find the final hour on a 24-hour clock after starting at hour 20 and adding 7 hours:",
            targetPrompt = "Tap the modulo symbol",
            fillCodeTemplate = "# Hour wrap on a 24-hour day\nfinal_hour = (20 + 7) [___] 24  # Evaluates to 3",
            fillCodeWordBank = listOf("%", "//", "/", "**"),
            fillCodeCorrectToken = "%",
            conceptTag = "modulo_operator_syntax",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: The '%' symbol is the modulo (remainder) operator in Python, C, Java, and Kotlin!",
            hint1 = "We want the remainder after division.",
            hint2 = "In programming languages, '%' computes the remainder.",
            hint3 = "Tap the % token.",
            explanation = "The % operator computes the remainder after division. 20 + 7 = 27. 27 % 24 = 3 (3:00 AM on the following day)."
        ),

        // Q3 (MATCH_PAIRS)
        QuizQuestion(
            id = "c0_l2_q3",
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Alphabet Modulo 26",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Connect each integer offset to its exact remainder modulo 26 (the Caesar cipher alphabet ring):",
            targetPrompt = "Match value to value mod 26",
            matchLeft = listOf("26 mod 26", "28 mod 26", "35 mod 26", "52 mod 26"),
            matchRight = listOf("0 (Letter 'A')", "2 (Letter 'C')", "9 (Letter 'J')", "0 (Letter 'A')"),
            matchSolution = mapOf(
                "26 mod 26" to "0 (Letter 'A')",
                "28 mod 26" to "2 (Letter 'C')",
                "35 mod 26" to "9 (Letter 'J')",
                "52 mod 26" to "0 (Letter 'A')"
            ),
            conceptTag = "alphabet_ring_mod26",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Any multiple of 26 (like 26 or 52) has remainder 0!",
            hint1 = "28 - 26 = 2.",
            hint2 = "35 - 26 = 9.",
            hint3 = "52 is 26 * 2, so its remainder is 0.",
            explanation = "In modulo 26 arithmetic, multiples of 26 wrap cleanly to 0. 28 % 26 = 2, and 35 % 26 = 9."
        ),

        // Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c0_l2_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Congruence Definition",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Evaluate this fundamental theorem of modular arithmetic:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "Two integers a and b are congruent modulo n (written a ≡ b mod n) if and only if their difference (a - b) is divisible by n.",
            trueFalseIsCorrectTrue = true,
            conceptTag = "modular_congruence_definition",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: If a and b leave the same remainder, their difference is a multiple of n!",
            hint1 = "For example, 15 ≡ 3 mod 12 because 15 - 3 = 12, which is divisible by 12.",
            hint2 = "28 ≡ 2 mod 26 because 28 - 2 = 26.",
            hint3 = "This statement is the exact definition of modular congruence.",
            explanation = "By mathematical definition, a ≡ b (mod n) means that n divides (a - b) evenly with remainder 0."
        ),

        // Q5 (ORDER_STEPS)
        QuizQuestion(
            id = "c0_l2_q5",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Division Algorithm",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order the steps of the Division Algorithm to calculate 29 mod 7:",
            targetPrompt = "Arrange division steps in order",
            orderStepsInitial = listOf(
                "Subtract: 29 - 28 = 1",
                "Identify dividend 29 and modulus 7",
                "Conclude remainder R = 1",
                "Find quotient Q = 4 (since 4 × 7 = 28)"
            ),
            orderStepsSolution = listOf(
                "Identify dividend 29 and modulus 7",
                "Find quotient Q = 4 (since 4 × 7 = 28)",
                "Subtract: 29 - 28 = 1",
                "Conclude remainder R = 1"
            ),
            conceptTag = "division_algorithm_steps",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Dividend = Quotient × Modulus + Remainder!",
            hint1 = "First identify inputs (29 and 7).",
            hint2 = "Next find how many times 7 fits into 29 (4 times, giving 28).",
            hint3 = "Subtract 28 from 29 to obtain remainder 1.",
            explanation = "By the Division Algorithm, 29 = 4 × 7 + 1. The remainder 1 is the result of 29 mod 7."
        ),

        // Q6 (COMPLEXITY_DIAL)
        QuizQuestion(
            id = "c0_l2_q6",
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Modulo Operation Time",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "What is the time complexity of computing a modulo operation (a % n) on fixed 64-bit hardware integers?",
            targetPrompt = "Set hardware execution time",
            complexityDialCorrect = "O(1)",
            conceptTag = "modulo_complexity",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Hardware CPUs execute division and modulo in constant time O(1) clock cycles!",
            hint1 = "Fixed-width word operations do not scale with the size of data.",
            hint2 = "A single CPU instruction computes both quotient and remainder.",
            hint3 = "Constant time is O(1).",
            explanation = "For fixed-size machine words (like 32-bit or 64-bit integers), division and modulo instructions execute in constant time O(1)."
        ),

        // Q7 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l2_q7",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Negative Modulo Reality",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is the mathematical value of (-3 mod 12) on a clock (such as 3 hours before 12 o'clock)?",
            targetPrompt = "Select the positive remainder",
            options = listOf("9 o'clock", "-3 o'clock", "3 o'clock", "0 o'clock"),
            correctOptionIndex = 0,
            conceptTag = "negative_modulo",
            misconceptionTag = "negative_sign_in_modulo",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: In cryptography, remainders must be positive! (-3 + 12) mod 12 = 9!",
            hint1 = "3 hours before 12 is 9.",
            hint2 = "Add the modulus 12 to the negative number: -3 + 12 = 9.",
            hint3 = "9 o'clock.",
            explanation = "In modular arithmetic, remainders are strictly within [0, n-1]. Moving backwards by 3 hours from 12 brings the clock to 9 o'clock. Mathematically: -3 ≡ 9 (mod 12).",
            wrongOptionExplanations = mapOf(
                1 to "Clocks do not have negative hours.",
                2 to "3 o'clock would be +3 hours, not 3 hours backwards.",
                3 to "0 o'clock is 12:00, not 3 hours before."
            )
        ),

        // Q8 (FILL_CODE)
        QuizQuestion(
            id = "c0_l2_q8",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Safe Negative Modulo",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "In Kotlin and Java, '(-3) % 12' produces '-3'. Complete the formula to guarantee a positive modular remainder:",
            targetPrompt = "Tap the addition variable",
            fillCodeTemplate = "// Canonical positive modulo formula\nfun safeMod(x: Int, n: Int): Int {\n    return ((x % n) + [___]) % n\n}",
            fillCodeWordBank = listOf("n", "x", "1", "0"),
            fillCodeCorrectToken = "n",
            conceptTag = "safe_modulo_implementation",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: Adding n before the second modulo shifts any negative remainder into positive range!",
            hint1 = "We need to offset the negative remainder by adding the divisor.",
            hint2 = "The divisor/modulus is n.",
            hint3 = "Add n: ((x % n) + n) % n.",
            explanation = "Because % in C-family languages computes remainder rather than Euclidean modulo, adding n ensures the numerator is positive before the final % n."
        ),

        // Q9 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c0_l2_q9",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Caesar Wrap Around",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Check this Caesar cipher encryption statement:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "In a Caesar cipher with shift key K=3, encrypting letter 'Z' (index 25) wraps around to letter 'C' (index 2).",
            trueFalseIsCorrectTrue = true,
            conceptTag = "caesar_wrap_around",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: (25 + 3) = 28. 28 mod 26 = 2, which is letter 'C' (0='A', 1='B', 2='C')!",
            hint1 = "'Z' is index 25.",
            hint2 = "25 + 3 = 28.",
            hint3 = "28 % 26 = 2. Index 2 is 'C'. The statement is true.",
            explanation = "With alphabet indexing 0='A' to 25='Z', shifting 'Z' by 3 produces (25 + 3) mod 26 = 28 mod 26 = 2, which corresponds to 'C'."
        ),

        // Q10 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l2_q10",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Multiplication Modulo",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "What is (7 × 5) mod 12?",
            targetPrompt = "Select the modular product",
            options = listOf("11", "35", "5", "1"),
            correctOptionIndex = 0,
            conceptTag = "modular_multiplication",
            misconceptionTag = "forgetting_final_modulo",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: 7 × 5 = 35. 35 / 12 = 2 laps with 11 hours left over!",
            hint1 = "Compute the product first: 7 * 5 = 35.",
            hint2 = "How many times does 12 fit into 35? 12 * 2 = 24.",
            hint3 = "35 - 24 = 11.",
            explanation = "7 × 5 = 35. Dividing 35 by 12 gives quotient 2 (2 × 12 = 24) and remainder 35 - 24 = 11.",
            wrongOptionExplanations = mapOf(
                1 to "35 is the raw product before taking modulo 12.",
                2 to "5 would mean a remainder of 5.",
                3 to "1 would be 25 mod 12."
            )
        )
    )

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 103: XOR MAGIC (c0_l3)
    // ══════════════════════════════════════════════════════════════════
    private fun level103Questions(): List<QuizQuestion> = listOf(
        // Q1 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l3_q1",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "XOR Identity",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "In Boolean logic and bitwise math, what is the result of 1 ⊕ 1 (1 XOR 1)?",
            targetPrompt = "Select the XOR output",
            options = listOf("0", "1", "2", "Undefined"),
            correctOptionIndex = 0,
            conceptTag = "xor_truth_table",
            misconceptionTag = "confusing_xor_with_or",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Exclusive OR outputs 1 ONLY if the two inputs differ. Two identical inputs yield 0!",
            hint1 = "XOR means 'exclusive OR'.",
            hint2 = "If both inputs are 1, they are identical, not exclusive.",
            hint3 = "1 XOR 1 = 0.",
            explanation = "Exclusive OR (XOR) outputs 1 when exactly one input is 1. If both inputs are identical (both 1 or both 0), the output is 0.",
            wrongOptionExplanations = mapOf(
                1 to "Inclusive OR outputs 1 for (1 OR 1), but XOR outputs 0.",
                2 to "Boolean logic deals with 0 and 1, not 2.",
                3 to "XOR is strictly defined for all boolean pairs."
            )
        ),

        // Q2 (FILL_CODE)
        QuizQuestion(
            id = "c0_l3_q2",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "XOR Operator Syntax",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Complete the bitwise XOR operation in Python between byte 'p' and key 'k':",
            targetPrompt = "Tap the XOR symbol",
            fillCodeTemplate = "# Bitwise XOR encryption in Python\ncipher_byte = p [___] k",
            fillCodeWordBank = listOf("^", "|", "&", "~"),
            fillCodeCorrectToken = "^",
            conceptTag = "bitwise_xor_operator",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: The caret '^' is the bitwise XOR operator in C, Java, Kotlin, and Python!",
            hint1 = "'|' is bitwise OR, '&' is bitwise AND.",
            hint2 = "The caret symbol represents XOR.",
            hint3 = "Tap ^.",
            explanation = "In Python, C, Java, and Kotlin, the caret symbol '^' denotes bitwise XOR."
        ),

        // Q3 (MATCH_PAIRS)
        QuizQuestion(
            id = "c0_l3_q3",
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "XOR Truth Table",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Connect each bitwise XOR input pair to its exact output bit:",
            targetPrompt = "Match input pair to XOR output",
            matchLeft = listOf("0 ⊕ 0", "0 ⊕ 1", "1 ⊕ 0", "1 ⊕ 1"),
            matchRight = listOf("0 (Same)", "1 (Different)", "1 (Different)", "0 (Same)"),
            matchSolution = mapOf(
                "0 ⊕ 0" to "0 (Same)",
                "0 ⊕ 1" to "1 (Different)",
                "1 ⊕ 0" to "1 (Different)",
                "1 ⊕ 1" to "0 (Same)"
            ),
            conceptTag = "xor_truth_table_complete",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Same inputs = 0. Different inputs = 1!",
            hint1 = "0 and 0 are the same -> 0.",
            hint2 = "0 and 1 differ -> 1.",
            hint3 = "1 and 1 are the same -> 0.",
            explanation = "The XOR truth table: 0⊕0=0, 0⊕1=1, 1⊕0=1, 1⊕1=0. It represents addition modulo 2 without carry."
        ),

        // Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c0_l3_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Self-Inverse Property",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Evaluate this fundamental algebraic property of XOR:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "XOR is self-inverting (an involution): for any plaintext P and key K, (P ⊕ K) ⊕ K = P.",
            trueFalseIsCorrectTrue = true,
            conceptTag = "xor_involution_property",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: K ⊕ K = 0. And P ⊕ 0 = P. XORing with the same key twice restores the original plaintext!",
            hint1 = "Since K ⊕ K cancels out to 0, (P ⊕ K) ⊕ K = P ⊕ (K ⊕ K) = P ⊕ 0 = P.",
            hint2 = "This means the exact same algorithm encrypts and decrypts.",
            hint3 = "The statement is true.",
            explanation = "Because K ⊕ K = 0 and P ⊕ 0 = P, applying XOR twice with the same key recovers the original plaintext. This involution property powers stream ciphers and One-Time Pads."
        ),

        // Q5 (ORDER_STEPS)
        QuizQuestion(
            id = "c0_l3_q5",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "XOR Round Sequence",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Trace the full encryption and decryption round of a 4-bit message using key 0110:",
            targetPrompt = "Order the XOR round steps",
            orderStepsInitial = listOf(
                "XOR C with Key K (0110) ➔ Recovers Plaintext P: 1010",
                "Start with Plaintext P: 1010",
                "Obtain Ciphertext C: 1100",
                "XOR P with Secret Key K (0110)"
            ),
            orderStepsSolution = listOf(
                "Start with Plaintext P: 1010",
                "XOR P with Secret Key K (0110)",
                "Obtain Ciphertext C: 1100",
                "XOR C with Key K (0110) ➔ Recovers Plaintext P: 1010"
            ),
            conceptTag = "xor_trace_execution",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Plaintext ➔ XOR with Key ➔ Ciphertext ➔ XOR with Key ➔ Original Plaintext!",
            hint1 = "Begin with the initial plaintext 1010.",
            hint2 = "Apply XOR with key 0110: 1^0=1, 0^1=1, 1^1=0, 0^0=0 -> 1100.",
            hint3 = "Apply XOR again with key 0110 to recover 1010.",
            explanation = "P(1010) ⊕ K(0110) = C(1100). Then C(1100) ⊕ K(0110) = P(1010). Perfect reversibility with zero data loss."
        ),

        // Q6 (COMPLEXITY_DIAL)
        QuizQuestion(
            id = "c0_l3_q6",
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Hardware XOR Latency",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "What is the computational complexity of computing bitwise XOR across an entire word on standard modern silicon processors?",
            targetPrompt = "Set processor latency dial",
            complexityDialCorrect = "O(1)",
            conceptTag = "hardware_xor_performance",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: XOR gates execute in a single CPU clock cycle O(1) in parallel hardware!",
            hint1 = "All bits in a word are processed simultaneously via parallel logic gates.",
            hint2 = "It does not require looping.",
            hint3 = "Single cycle execution is O(1).",
            explanation = "Silicon logic gates execute bitwise XOR across entire 64-bit or 256-bit SIMD registers simultaneously in a single clock cycle: O(1)."
        ),

        // Q7 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l3_q7",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Why Cryptography Loves XOR",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Why is XOR heavily preferred over AND or OR in cryptographic ciphers?",
            targetPrompt = "Select the cryptographic rationale",
            options = listOf(
                "When XORed with a random key bit, the output has a 50% chance of 0 and 50% chance of 1, leaking zero information",
                "XOR compresses data so that ciphertexts are half the original size",
                "XOR produces only 1s, making keys impossible to guess",
                "XOR requires special cryptographic licenses to use"
            ),
            correctOptionIndex = 0,
            conceptTag = "xor_perfect_secrecy",
            misconceptionTag = "compression_misconception",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: A random key makes the output distribution perfectly uniform (50/50)! AND/OR have 75/25 bias!",
            hint1 = "Notice that AND outputs 0 75% of the time, and OR outputs 1 75% of the time.",
            hint2 = "Only XOR preserves a perfect 50/50 balance with a random key.",
            hint3 = "Zero bias means zero information leakage.",
            explanation = "If the key bit is uniformly random (50% 0, 50% 1), the XOR output is also exactly 50% 0 and 50% 1 regardless of the plaintext bit. This prevents statistical analysis.",
            wrongOptionExplanations = mapOf(
                1 to "XOR preserves exact bit length; it does not compress data.",
                2 to "XOR outputs both 0s and 1s depending on inputs.",
                3 to "XOR is a standard mathematical gate used freely everywhere."
            )
        ),

        // Q8 (FILL_CODE)
        QuizQuestion(
            id = "c0_l3_q8",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Binary XOR Evaluation",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Calculate the bitwise XOR between binary 1011 and 1101 (1011 ⊕ 1101):",
            targetPrompt = "Tap the binary result token",
            fillCodeTemplate = "# Binary XOR calculation\n#   1 0 1 1\n# ⊕ 1 1 0 1\n# ---------\nresult = 0b[___]",
            fillCodeWordBank = listOf("0110", "1111", "0000", "1001"),
            fillCodeCorrectToken = "0110",
            conceptTag = "binary_xor_computation",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Check bit by bit: 1^1=0, 0^1=1, 1^0=1, 1^1=0 ➔ 0110!",
            hint1 = "Bit 3: 1 ⊕ 1 = 0.",
            hint2 = "Bit 2: 0 ⊕ 1 = 1.",
            hint3 = "Bit 1: 1 ⊕ 0 = 1; Bit 0: 1 ⊕ 1 = 0. Result: 0110.",
            explanation = "Aligning bits: 1^1=0, 0^1=1, 1^0=1, 1^1=0. The resulting binary value is 0110 (decimal 6)."
        ),

        // Q9 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c0_l3_q9",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Key Reuse Disaster",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Verify this historic cryptographic pitfall:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "If two messages P₁ and P₂ are encrypted with the same XOR key K, XORing the two ciphertexts together (C₁ ⊕ C₂) eliminates K and exposes P₁ ⊕ P₂.",
            trueFalseIsCorrectTrue = true,
            conceptTag = "two_time_pad_catastrophe",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: C₁ ⊕ C₂ = (P₁ ⊕ K) ⊕ (P₂ ⊕ K) = P₁ ⊕ P₂ ⊕ (K ⊕ K) = P₁ ⊕ P₂!",
            hint1 = "K ⊕ K equals 0.",
            hint2 = "The key cancels out of the equation completely.",
            hint3 = "This is the deadly Two-Time Pad vulnerability. True!",
            explanation = "Because K ⊕ K = 0, C₁ ⊕ C₂ = (P₁ ⊕ K) ⊕ (P₂ ⊕ K) = P₁ ⊕ P₂. The secret key is erased, and cryptanalysts can recover both plaintexts using natural language statistics."
        ),

        // Q10 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l3_q10",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "XOR Identity Property",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "For any binary bit or byte x, what is the result of x ⊕ 0?",
            targetPrompt = "Select the identity output",
            options = listOf("x (the original input unmodified)", "0", "1", "NOT x (the inverted input)"),
            correctOptionIndex = 0,
            conceptTag = "xor_identity_element",
            misconceptionTag = "confusing_with_multiplication_by_zero",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: 0 is the identity element of XOR. 0 ⊕ 0 = 0, and 1 ⊕ 0 = 1!",
            hint1 = "If x is 0: 0 ⊕ 0 = 0.",
            hint2 = "If x is 1: 1 ⊕ 0 = 1.",
            hint3 = "In both cases, the output is x itself.",
            explanation = "0 is the identity element of the XOR operation. XORing any value with 0 leaves it completely unchanged: x ⊕ 0 = x.",
            wrongOptionExplanations = mapOf(
                1 to "In multiplication x * 0 = 0, but in XOR x ⊕ 0 = x.",
                2 to "Only 1 ⊕ 0 produces 1; 0 ⊕ 0 produces 0.",
                3 to "Inversion (NOT x) is achieved by XORing with 1: x ⊕ 1 = NOT x."
            )
        )
    )

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 104: WHO ATTACKS WHAT? CIA TRIAD (c0_l4)
    // ══════════════════════════════════════════════════════════════════
    private fun level104Questions(): List<QuizQuestion> = listOf(
        // Q1 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l4_q1",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "CIA Triad Pillars",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What are the three fundamental pillars of information security known as the 'CIA Triad'?",
            targetPrompt = "Select the triad pillars",
            options = listOf(
                "Confidentiality, Integrity, Availability",
                "Control, Inspection, Authorization",
                "Cryptography, Identity, Authentication",
                "Certificates, IPsec, AES"
            ),
            correctOptionIndex = 0,
            conceptTag = "cia_triad_definition",
            misconceptionTag = "confusing_pillars_with_protocols",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Confidentiality (privacy), Integrity (no tampering), Availability (uptime)!",
            hint1 = "C = keeping secrets hidden.",
            hint2 = "I = preventing unauthorized alterations.",
            hint3 = "A = ensuring systems remain accessible to legitimate users.",
            explanation = "The CIA Triad is the foundational model of information security: Confidentiality (data privacy), Integrity (data accuracy/non-tampering), and Availability (reliable access to authorized users).",
            wrongOptionExplanations = mapOf(
                1 to "Control and inspection are operational processes, not the triad pillars.",
                2 to "Authentication and cryptography are tools used to achieve the pillars.",
                3 to "These are specific technical mechanisms and ciphers."
            )
        ),

        // Q2 (FILL_CODE)
        QuizQuestion(
            id = "c0_l4_q2",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Security Goal Mapping",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Complete the code variable designating the security pillar achieved by encrypting network packets against eavesdropping:",
            targetPrompt = "Tap the security pillar token",
            fillCodeTemplate = "# Preventing unauthorized parties from reading data\nachieved_pillar = '[___]'",
            fillCodeWordBank = listOf("Confidentiality", "Integrity", "Availability", "Redundancy"),
            fillCodeCorrectToken = "Confidentiality",
            conceptTag = "confidentiality_objective",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Eavesdropping violates Confidentiality!",
            hint1 = "The goal is keeping the contents secret.",
            hint2 = "Unauthorized readers are blocked from viewing plaintext.",
            hint3 = "Confidentiality.",
            explanation = "Confidentiality ensures that only authorized entities can view the underlying plaintext data. Encryption is the primary mechanism to enforce confidentiality."
        ),

        // Q3 (MATCH_PAIRS)
        QuizQuestion(
            id = "c0_l4_q3",
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Attack to Pillar Matcher",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Connect each cyber attack to the specific CIA Triad pillar it directly violates:",
            targetPrompt = "Match attack to violated pillar",
            matchLeft = listOf(
                "Passive Wi-Fi Sniffing",
                "Tampering with Bank Balance",
                "DDoS Distributed Flooding",
                "Ransomware Encrypting DB"
            ),
            matchRight = listOf(
                "Confidentiality",
                "Integrity",
                "Availability",
                "Availability"
            ),
            matchSolution = mapOf(
                "Passive Wi-Fi Sniffing" to "Confidentiality",
                "Tampering with Bank Balance" to "Integrity",
                "DDoS Distributed Flooding" to "Availability",
                "Ransomware Encrypting DB" to "Availability"
            ),
            conceptTag = "attack_pillar_mapping",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Sniffing = Confidentiality. Tampering = Integrity. Crashing = Availability!",
            hint1 = "Sniffing reads private messages -> Confidentiality.",
            hint2 = "Altering transaction balances modifies data -> Integrity.",
            hint3 = "DDoS floods take down service -> Availability.",
            explanation = "Eavesdropping violates Confidentiality; unauthorized modification violates Integrity; and resource exhaustion / destruction violates Availability."
        ),

        // Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c0_l4_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Attacker Classification",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Fact check this threat modeling classification:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "A passive adversary only observes communication without altering packets, while an active adversary modifies, injects, or drops traffic.",
            trueFalseIsCorrectTrue = true,
            conceptTag = "passive_vs_active_adversary",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Passive = wiretapping (listening). Active = Man-in-the-Middle (tampering)!",
            hint1 = "Passive attackers eavesdrop quietly.",
            hint2 = "Active attackers tamper with or generate fraudulent messages.",
            hint3 = "This classification is universally recognized in security models.",
            explanation = "Passive attacks threaten confidentiality via traffic analysis and interception. Active attacks threaten integrity and availability via message alteration, replay, and denial of service."
        ),

        // Q5 (ORDER_STEPS)
        QuizQuestion(
            id = "c0_l4_q5",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Defense in Depth",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Arrange the standard layered defense verification flow for processing an incoming secure request:",
            targetPrompt = "Order verification steps",
            orderStepsInitial = listOf(
                "Attach digital signature / HMAC for message Integrity",
                "Verify identity credentials (Authentication: Who are you?)",
                "Encrypt communication channel (Confidentiality: Wire privacy)",
                "Evaluate access permissions (Authorization: What can you do?)"
            ),
            orderStepsSolution = listOf(
                "Verify identity credentials (Authentication: Who are you?)",
                "Evaluate access permissions (Authorization: What can you do?)",
                "Encrypt communication channel (Confidentiality: Wire privacy)",
                "Attach digital signature / HMAC for message Integrity"
            ),
            conceptTag = "defense_in_depth_sequence",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: 1. Authentication ➔ 2. Authorization ➔ 3. Confidentiality ➔ 4. Integrity!",
            hint1 = "First determine WHO is speaking (Authentication).",
            hint2 = "Next verify if they are ALLOWED to perform the action (Authorization).",
            hint3 = "Then ensure transmission confidentiality and message integrity.",
            explanation = "Security starts with Authentication (proving identity), followed by Authorization (access control rules), and finally cryptographic transport confidentiality and message integrity."
        ),

        // Q6 (COMPLEXITY_DIAL)
        QuizQuestion(
            id = "c0_l1_q6_cia",
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Brute Force Keyspace",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "What is the worst-case time complexity to exhaustively brute-force guess an arbitrary k-bit symmetric encryption key?",
            targetPrompt = "Set keyspace search complexity",
            complexityDialCorrect = "O(2^n)",
            conceptTag = "brute_force_keyspace_growth",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: A k-bit key has 2^k possible combinations. Exhaustive search requires O(2^n) work!",
            hint1 = "Each bit doubles the number of keys to check.",
            hint2 = "For 128-bit AES, there are 2^128 keys.",
            hint3 = "Exhaustive key search is exponential: O(2^n).",
            explanation = "Because every bit in a k-bit key has two choices, the total search space is 2^k. In Big-O notation with key length n, this is O(2^n)."
        ),

        // Q7 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l4_q7",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Authentication vs Authorization",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is the critical distinction between Authentication and Authorization in access control?",
            targetPrompt = "Select the exact distinction",
            options = listOf(
                "Authentication verifies WHO you are; Authorization determines WHAT you are permitted to do",
                "Authentication encrypts files; Authorization decrypts files",
                "Authentication is performed by the client; Authorization is performed by the DNS server",
                "They are synonyms referring to the exact same security check"
            ),
            correctOptionIndex = 0,
            conceptTag = "authn_vs_authz",
            misconceptionTag = "confusing_authn_and_authz",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: AuthN = Identity (Passport). AuthZ = Permission (Boarding Pass / VIP access)!",
            hint1 = "Showing your ID card is Authentication.",
            hint2 = "Checking if you have permission to enter the server room is Authorization.",
            hint3 = "Who you are vs What you can do.",
            explanation = "Authentication (AuthN) proves your identity (e.g. password, biometric). Authorization (AuthZ) verifies what resources and operations you have permission to access (e.g. read/write admin rules).",
            wrongOptionExplanations = mapOf(
                1 to "Encryption and decryption are cryptographic operations, not identity verification.",
                2 to "DNS translates domain names to IPs, not authorization policies.",
                3 to "AuthN and AuthZ are distinct concepts: identity verification vs access control."
            )
        ),

        // Q8 (FILL_CODE)
        QuizQuestion(
            id = "c0_l4_q8",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Integrity Verification",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Complete the code variable indicating which security property is guaranteed by appending an HMAC tag or digital signature to a file:",
            targetPrompt = "Tap the security property",
            fillCodeTemplate = "# Detecting if an unauthorized party modified the message\nproperty_enforced = '[___]'",
            fillCodeWordBank = listOf("Integrity", "Availability", "Anonymity", "Redundancy"),
            fillCodeCorrectToken = "Integrity",
            conceptTag = "integrity_via_hmac",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: Hash digests and signatures detect data tampering, guaranteeing Integrity!",
            hint1 = "We want to know if the message was altered in transit.",
            hint2 = "Unmodified, authentic data satisfies Integrity.",
            hint3 = "Tap Integrity.",
            explanation = "Integrity guarantees that data has not been modified, deleted, or inserted by unauthorized actors. Cryptographic hashes, HMACs, and digital signatures provide integrity checks."
        ),

        // Q9 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c0_l4_q9",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Encryption vs Integrity",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Scrutinize this dangerous cryptographic misconception:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "Standard symmetric encryption (like AES in CBC mode) inherently guarantees both Confidentiality and Integrity without needing a MAC or signature.",
            trueFalseIsCorrectTrue = false,
            conceptTag = "encryption_does_not_equal_integrity",
            difficulty = 2,
            hint = "🎮 Phoenix Cheat Code: FALSE! Encryption hides data (Confidentiality), but attackers can still flip bits in transit unless you use Authenticated Encryption (GCM/HMAC)!",
            hint1 = "Can an attacker modify bits in encrypted ciphertext?",
            hint2 = "Yes! In CBC or stream ciphers, flipping ciphertext bits predictably flips plaintext bits.",
            hint3 = "Encryption alone does NOT guarantee integrity. The statement is False.",
            explanation = "Encryption alone provides only Confidentiality. Without an authentication tag (such as in AES-GCM or HMAC), attackers can tamper with ciphertext (bit-flipping attacks) without detection."
        ),

        // Q10 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_l4_q10",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Availability Threats",
            speakerName = "Ember",
            speakerImage = "Ember",
            promptSentence = "Which of the following cyber threats directly targets the Availability pillar of an online banking system?",
            targetPrompt = "Select the Availability attack",
            options = listOf(
                "A volumetric Distributed Denial of Service (DDoS) flood overwhelming web servers",
                "Eavesdropping on customer login passwords on an open Wi-Fi network",
                "Reverse engineering the Android APK to read hardcoded string resources",
                "Passive traffic analysis observing packet sizes and transmission frequency"
            ),
            correctOptionIndex = 0,
            conceptTag = "availability_ddos_threat",
            misconceptionTag = "confusing_ddos_with_eavesdropping",
            difficulty = 1,
            hint = "🎮 Phoenix Cheat Code: DDoS crashes servers and exhausts bandwidth, rendering services unavailable!",
            hint1 = "Which option makes the service completely unreachable for legitimate users?",
            hint2 = "DDoS floods exhaust server CPU, memory, and bandwidth.",
            hint3 = "DDoS attacks Availability.",
            explanation = "DDoS attacks flood network pipes and application servers with bogus requests, exhausting resources and preventing legitimate users from accessing services (violating Availability).",
            wrongOptionExplanations = mapOf(
                1 to "Eavesdropping on passwords violates Confidentiality.",
                2 to "Reverse engineering code inspects software logic (Confidentiality).",
                3 to "Passive traffic analysis is an eavesdropping reconnaissance attack on Confidentiality."
            )
        )
    )

    // ══════════════════════════════════════════════════════════════════
    // LEVEL 105: BOSS: BASECAMP TRIAL (c0_boss)
    // ══════════════════════════════════════════════════════════════════
    private fun level105BossQuestions(): List<QuizQuestion> = listOf(
        // Boss Q1 (FILL_CODE)
        QuizQuestion(
            id = "c0_boss_q1",
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Boss: Nibble Decoding",
            speakerName = "Warden",
            speakerImage = "Warden",
            promptSentence = "⚔️ BOSS TRIAL PHASE 1: Convert hexadecimal 0x2F into its exact decimal value:",
            targetPrompt = "Tap the decimal answer token",
            fillCodeTemplate = "# Hex 0x2F = (2 * 16) + 15\ndecimal_val = [___]  # 32 + 15",
            fillCodeWordBank = listOf("47", "31", "215", "42"),
            fillCodeCorrectToken = "47",
            conceptTag = "hex_to_decimal_calculation",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: High nibble 2 * 16 = 32. Low nibble 'F' = 15. 32 + 15 = 47!",
            hint1 = "High nibble is 2: 2 * 16 = 32.",
            hint2 = "Low nibble is 'F': decimal 15.",
            hint3 = "32 + 15 = 47.",
            explanation = "0x2F = (2 × 16^1) + (15 × 16^0) = 32 + 15 = 47."
        ),

        // Boss Q2 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_boss_q2",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss: Modular Synthesis",
            speakerName = "Warden",
            speakerImage = "Warden",
            promptSentence = "⚔️ BOSS TRIAL PHASE 2: Evaluate the modular expression: (17 × 5) mod 12.",
            targetPrompt = "Select the reduced remainder",
            options = listOf("1", "85", "5", "7"),
            correctOptionIndex = 0,
            conceptTag = "boss_modular_synthesis",
            misconceptionTag = "unreduced_product",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: 17 × 5 = 85. 85 / 12 = 7 full laps (84) with 1 hour remainder!",
            hint1 = "17 * 5 = 85.",
            hint2 = "85 = 7 * 12 + 1.",
            hint3 = "The remainder is 1.",
            explanation = "17 × 5 = 85. Dividing 85 by 12 yields quotient 7 (7 × 12 = 84) and remainder 85 - 84 = 1. Alternatively: (17 mod 12) × (5 mod 12) = 5 × 5 = 25 ≡ 1 mod 12.",
            wrongOptionExplanations = mapOf(
                1 to "85 is the raw product before taking modulo 12.",
                2 to "5 would be 17 mod 12 alone.",
                3 to "7 is the quotient, not the remainder."
            )
        ),

        // Boss Q3 (ORDER_STEPS)
        QuizQuestion(
            id = "c0_boss_q3",
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Boss: Stream Cipher Decryption",
            speakerName = "Warden",
            speakerImage = "Warden",
            promptSentence = "⚔️ BOSS TRIAL PHASE 3: Order the sequence to decrypt an intercepted XOR stream ciphertext block:",
            targetPrompt = "Order decryption steps",
            orderStepsInitial = listOf(
                "Obtain recovered Plaintext P: 0101",
                "Compute bitwise P = C ⊕ K",
                "Receive 4-bit Ciphertext C: 1111",
                "Retrieve Secret Key Stream K: 1010"
            ),
            orderStepsSolution = listOf(
                "Receive 4-bit Ciphertext C: 1111",
                "Retrieve Secret Key Stream K: 1010",
                "Compute bitwise P = C ⊕ K",
                "Obtain recovered Plaintext P: 0101"
            ),
            conceptTag = "boss_stream_decryption",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: Receive Ciphertext ➔ Retrieve Key ➔ Compute C ⊕ K ➔ Plaintext!",
            hint1 = "Start with the received ciphertext 1111.",
            hint2 = "Fetch the matching keystream block 1010.",
            hint3 = "XOR them: 1^1=0, 1^0=1, 1^1=0, 1^0=1 -> 0101.",
            explanation = "C(1111) ⊕ K(1010) = P(0101). Bit by bit: 1^1=0, 1^0=1, 1^1=0, 1^0=1."
        ),

        // Boss Q4 (TRUE_FALSE_SWIPE)
        QuizQuestion(
            id = "c0_boss_q4",
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Boss: Universal Primitive",
            speakerName = "Warden",
            speakerImage = "Warden",
            promptSentence = "⚔️ BOSS TRIAL PHASE 4: Validate this core architecture axiom:",
            targetPrompt = "Swipe True or False",
            trueFalseStatement = "Because XOR is self-inverting (x ⊕ y ⊕ y = x), stream ciphers like ChaCha20 use the exact same encryption function for decryption.",
            trueFalseIsCorrectTrue = true,
            conceptTag = "boss_xor_stream_axiom",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: TRUE! Decryption in stream ciphers is identical to encryption: simply XOR the keystream again!",
            hint1 = "Does stream cipher decryption need an inverse circuit?",
            hint2 = "No! XORing the keystream with ciphertext reverses encryption perfectly.",
            hint3 = "The statement is true.",
            explanation = "Modern stream ciphers (such as ChaCha20 and AES-CTR) generate a pseudorandom keystream and XOR it with data. Decryption simply XORs the ciphertext with the identical keystream."
        ),

        // Boss Q5 (MULTIPLE_CHOICE)
        QuizQuestion(
            id = "c0_boss_q5",
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss: Triad Breach Analysis",
            speakerName = "Warden",
            speakerImage = "Warden",
            promptSentence = "⚔️ BOSS TRIAL PHASE 5: An attacker flips bits inside an in-flight encrypted payment transaction to modify the recipient account number without breaking the encryption key. Which CIA Triad pillar did the attacker breach?",
            targetPrompt = "Identify the breached security pillar",
            options = listOf(
                "Integrity",
                "Confidentiality",
                "Availability",
                "Authentication only"
            ),
            correctOptionIndex = 0,
            conceptTag = "boss_integrity_breach_analysis",
            misconceptionTag = "confusing_malleability_with_confidentiality_loss",
            difficulty = 3,
            hint = "🎮 Phoenix Cheat Code: The attacker did not decrypt the transaction (confidentiality remained), but altered the data without detection! This is an Integrity breach!",
            hint1 = "The attacker altered the destination account number.",
            hint2 = "Data tampering directly violates Integrity.",
            hint3 = "The breached pillar is Integrity.",
            explanation = "Malleability attacks modify ciphertext to cause unauthorized changes in plaintext. Because data was altered without detection, Integrity was breached even if the attacker never learned the key.",
            wrongOptionExplanations = mapOf(
                1 to "Confidentiality was not breached if the attacker did not recover the plaintext contents.",
                2 to "Availability is not breached because the transaction still went through (it was just corrupted).",
                3 to "Authentication may be intact if the original sender signed in, but the message itself was modified."
            )
        )
    )
}
