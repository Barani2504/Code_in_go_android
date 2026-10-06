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
            promptSentence = "Complete the code to append 40 to the end of the array:",
            targetPrompt = "Tap the correct token",
            hint = "🎮 Phoenix Cheat Code: Append pushes straight to the tail array boundary in instant O(1) time!",
            fillCodeTemplate = "arr = [10, 20, 30]\narr.[___](40)\nprint(arr)",
            fillCodeWordBank = listOf("append", "push", "insert", "extend"),
            fillCodeCorrectToken = "append"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match each Array operation with its worst-case Time Complexity:",
            targetPrompt = "Tap left, then match on right",
            hint = "🎮 Phoenix Cheat Code: Direct index arithmetic is always instant O(1), but shifting items needs O(n) scans!",
            matchLeft = listOf("Access arr[i]", "Linear Search", "Insert at index 0"),
            matchRight = listOf("O(1) Instant Offset", "O(n) Sequential Scan", "O(n) Shift Elements"),
            matchSolution = mapOf(
                "Access arr[i]" to "O(1) Instant Offset",
                "Linear Search" to "O(n) Sequential Scan",
                "Insert at index 0" to "O(n) Shift Elements"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the steps to insert a new element at index 2 in an array:",
            targetPrompt = "Arrange into sequence",
            hint = "🎮 Phoenix Cheat Code: Always clear room by shifting right before writing your new value!",
            orderStepsInitial = listOf(
                "Write new value at index 2",
                "Shift elements right from index 2 onwards",
                "Increment array count"
            ),
            orderStepsSolution = listOf(
                "Shift elements right from index 2 onwards",
                "Write new value at index 2",
                "Increment array count"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Contiguous means back-to-back in RAM like consecutive lockers!",
            trueFalseStatement = "An Array stores elements in contiguous memory locations, enabling O(1) direct address arithmetic.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Analyze this array index access algorithm:",
            targetPrompt = "Dial the worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: Mathematical offset calculations take zero loops = constant O(1) speed!",
            complexityCodeSnippet = "def get_element(arr, i):\n    return arr[i] # address = base + i * size",
            complexityDialCorrect = "O(1)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "What is the index of the LAST element in an array of length N?",
            targetPrompt = "Select the correct index",
            options = listOf("N - 1", "N", "N + 1", "0"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: 0-indexed arrays start at 0, making the N-th slot sit at N - 1!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match primitive data types to their standard memory byte size:",
            targetPrompt = "Connect data types to byte footprints",
            hint = "🎮 Phoenix Cheat Code: Double the precision doubles the bits: 32-bit is 4 bytes, 64-bit is 8 bytes!",
            matchLeft = listOf("Boolean Flag", "Int32 Integer", "Float64 Float"),
            matchRight = listOf("1 Byte", "4 Bytes", "8 Bytes"),
            matchSolution = mapOf(
                "Boolean Flag" to "1 Byte",
                "Int32 Integer" to "4 Bytes",
                "Float64 Float" to "8 Bytes"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete counter increment inside an element counting loop:",
            targetPrompt = "Slot increment operator",
            hint = "🎮 Phoenix Cheat Code: count += 1 adds 1 to the tally on each match!",
            fillCodeTemplate = "count = 0\nfor x in arr:\n    if x > 0: count [___] 1\nreturn count",
            fillCodeWordBank = listOf("+=", "-=", "=", "*="),
            fillCodeCorrectToken = "+="
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Primitive strings in Python & Java are immutable—modifications allocate brand new strings!",
            trueFalseStatement = "In languages like Python and Java, strings are mutable and can be modified in-place without copying.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 3 steps of Dynamic Array Capacity Resizing:",
            targetPrompt = "Arrange resizing lifecycle",
            hint = "🎮 Phoenix Cheat Code: Double capacity, copy old items, then insert the new item!",
            orderStepsInitial = listOf(
                "Copy existing elements over to new array",
                "Allocate new array with 2x capacity",
                "Insert new element into double-sized array"
            ),
            orderStepsSolution = listOf(
                "Allocate new array with 2x capacity",
                "Copy existing elements over to new array",
                "Insert new element into double-sized array"
            )
        )
    )

    private fun dsaLevel2Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Fill in the method to insert 99 at index 0:",
            targetPrompt = "Slot the insertion method",
            hint = "🎮 Phoenix Cheat Code: insert(index, item) specifies the exact target slot!",
            fillCodeTemplate = "arr = [1, 2, 3]\narr.[___](0, 99)\nprint(arr)",
            fillCodeWordBank = listOf("insert", "push", "append", "extend"),
            fillCodeCorrectToken = "insert"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match array modifications with their operation complexity:",
            targetPrompt = "Connect operations",
            hint = "🎮 Phoenix Cheat Code: Popping from tail requires zero shifts, deleting at front shifts all N items!",
            matchLeft = listOf("Pop from end", "Delete at index 0", "Binary Search on sorted"),
            matchRight = listOf("O(1) No Shift", "O(n) Left Shift", "O(log n) Halving"),
            matchSolution = mapOf(
                "Pop from end" to "O(1) No Shift",
                "Delete at index 0" to "O(n) Left Shift",
                "Binary Search on sorted" to "O(log n) Halving"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the steps to delete element at index k from an array:",
            targetPrompt = "Arrange deletion sequence",
            hint = "🎮 Phoenix Cheat Code: Overwrite k by shifting subsequent elements left, then decrement count!",
            orderStepsInitial = listOf(
                "Decrement array length",
                "Shift elements left from k+1 to end",
                "Remove element reference at index k"
            ),
            orderStepsSolution = listOf(
                "Remove element reference at index k",
                "Shift elements left from k+1 to end",
                "Decrement array length"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: When middle items are removed, later items must slide left to close the hole!",
            trueFalseStatement = "Deleting an element from the middle of an array requires shifting all subsequent elements left.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Dial time complexity of popping the last element from a dynamic array:",
            targetPrompt = "Select complexity",
            hint = "🎮 Phoenix Cheat Code: Popping from the tail needs no element shifting—instant O(1)!",
            complexityCodeSnippet = "val = arr[len - 1]\narr.len -= 1\nreturn val",
            complexityDialCorrect = "O(1)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is the auxiliary space complexity of an in-place array reversal?",
            targetPrompt = "Choose space complexity",
            options = listOf("O(1)", "O(n)", "O(n²)", "O(log n)"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: In-place reversal only uses two pointer variables: strictly O(1) space!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Complete swapping two elements using a temporary variable:",
            targetPrompt = "Slot the stored temp value",
            hint = "🎮 Phoenix Cheat Code: temp preserves the initial value of arr[i] before overwrite!",
            fillCodeTemplate = "temp = arr[i]\narr[i] = arr[j]\narr[j] = [___]",
            fillCodeWordBank = listOf("temp", "arr[i]", "None", "0"),
            fillCodeCorrectToken = "temp"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Match Prefix Sum queries to their computational cost:",
            targetPrompt = "Pair query costs",
            hint = "🎮 Phoenix Cheat Code: Precomputing table takes O(n), but subsequent queries take O(1)!",
            matchLeft = listOf("Prefix Table Build", "Subarray Sum Query", "Naive Sum without Prefix"),
            matchRight = listOf("O(n) Single Pass", "O(1) Table Subtraction", "O(n) Iterative Loop"),
            matchSolution = mapOf(
                "Prefix Table Build" to "O(n) Single Pass",
                "Subarray Sum Query" to "O(1) Table Subtraction",
                "Naive Sum without Prefix" to "O(n) Iterative Loop"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Standard C/C++ 2D arrays are stored row-major in a single contiguous block!",
            trueFalseStatement = "In standard row-major 2D arrays, matrix elements in row 0 are followed immediately in memory by row 1.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 3 steps to rotate an array right by 1 position:",
            targetPrompt = "Arrange rotation steps",
            hint = "🎮 Phoenix Cheat Code: Stash the last item in temp, slide everyone right, put temp at index 0!",
            orderStepsInitial = listOf(
                "arr[0] = lastItem",
                "lastItem = arr[n - 1]",
                "Shift all elements right by 1 from n-2 down to 0"
            ),
            orderStepsSolution = listOf(
                "lastItem = arr[n - 1]",
                "Shift all elements right by 1 from n-2 down to 0",
                "arr[0] = lastItem"
            )
        )
    )

    private fun dsaLevel3Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Complete string reverse with two pointers marching towards center:",
            targetPrompt = "Slot the decrement token",
            hint = "🎮 Phoenix Cheat Code: Left pointer goes forward (+1) while right marches backward (-1)!",
            fillCodeTemplate = "while left < right:\n    s[left], s[right] = s[right], s[left]\n    left += 1\n    right [___] 1",
            fillCodeWordBank = listOf("-=", "+=", "=", "=="),
            fillCodeCorrectToken = "-="
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match String algorithms with their optimal time complexity:",
            targetPrompt = "Pair algorithms",
            hint = "🎮 Phoenix Cheat Code: Palindrome check inspects each char once; direct char access is O(1)!",
            matchLeft = listOf("Palindrome Two-Pointer", "Loop Concatenation s += ch", "Char Lookup by Index"),
            matchRight = listOf("O(n) Single Scan", "O(n²) Copying Cost", "O(1) Direct Lookup"),
            matchSolution = mapOf(
                "Palindrome Two-Pointer" to "O(n) Single Scan",
                "Loop Concatenation s += ch" to "O(n²) Copying Cost",
                "Char Lookup by Index" to "O(1) Direct Lookup"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the steps of checking if a string is a Palindrome:",
            targetPrompt = "Arrange palindrome verification",
            hint = "🎮 Phoenix Cheat Code: Set pointers at edges, compare, shrink inward!",
            orderStepsInitial = listOf(
                "If s[left] != s[right]: return False",
                "left = 0; right = len(s) - 1",
                "left += 1; right -= 1",
                "Return True if all match"
            ),
            orderStepsSolution = listOf(
                "left = 0; right = len(s) - 1",
                "If s[left] != s[right]: return False",
                "left += 1; right -= 1",
                "Return True if all match"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: 'racecar' backwards is 'racecar'—that's a palindrome!",
            trueFalseStatement = "A Palindrome is a string that reads identically forwards and backwards.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial the time complexity of checking anagrams using character frequency maps:",
            targetPrompt = "Select time complexity",
            hint = "🎮 Phoenix Cheat Code: Tallying character counts for length N takes linear O(n) time!",
            complexityCodeSnippet = "count = Counter(s1)\nfor ch in s2: count[ch] -= 1\nreturn all(v == 0 for v in count.values())",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is the time complexity of slicing a substring of length K in Python?",
            targetPrompt = "Choose slice complexity",
            options = listOf("O(k)", "O(1)", "O(n²)", "O(log k)"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Slicing copies K characters into a new string, taking O(k) time!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Filter alphanumeric characters before palindrome comparison:",
            targetPrompt = "Slot the alphanumeric check method",
            hint = "🎮 Phoenix Cheat Code: isalnum() filters out punctuation and whitespace!",
            fillCodeTemplate = "clean = []\nfor ch in s:\n    if ch.[___]():\n        clean.append(ch.lower())",
            fillCodeWordBank = listOf("isalnum", "isalpha", "isnumeric", "isdigit"),
            fillCodeCorrectToken = "isalnum"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match String terminology with their formal definition:",
            targetPrompt = "Connect definitions",
            hint = "🎮 Phoenix Cheat Code: Substrings must be contiguous; subsequences keep order but can skip!",
            matchLeft = listOf("Substring", "Subsequence", "Anagram"),
            matchRight = listOf("Contiguous Block", "Relative Order Preserved", "Permuted Letters"),
            matchSolution = mapOf(
                "Substring" to "Contiguous Block",
                "Subsequence" to "Relative Order Preserved",
                "Anagram" to "Permuted Letters"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Substrings are already contiguous subsequences!",
            trueFalseStatement = "Every substring is a valid subsequence of a string, but not every subsequence is a substring.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order steps to find the Longest Common Prefix across sorted strings:",
            targetPrompt = "Arrange algorithm flow",
            hint = "🎮 Phoenix Cheat Code: Sorting clusters similar prefixes—just compare the very first and last string!",
            orderStepsInitial = listOf(
                "Compare first and last strings char by char",
                "Sort string list alphabetically",
                "Return matched prefix substring"
            ),
            orderStepsSolution = listOf(
                "Sort string list alphabetically",
                "Compare first and last strings char by char",
                "Return matched prefix substring"
            )
        )
    )

    private fun dsaLevel4Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Order the steps to insert a new node at the HEAD of a Singly Linked List:",
            targetPrompt = "Reorder code operations",
            hint = "🎮 Phoenix Cheat Code: Point newNode's next wire to head first, then update head!",
            orderStepsInitial = listOf(
                "head = newNode",
                "newNode.next = head",
                "newNode = Node(value)"
            ),
            orderStepsSolution = listOf(
                "newNode = Node(value)",
                "newNode.next = head",
                "head = newNode"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match Linked List operations with their time complexities:",
            targetPrompt = "Pair operation costs",
            hint = "🎮 Phoenix Cheat Code: Head prepends are instant O(1); finding index k requires k node hops!",
            matchLeft = listOf("Prepend at Head", "Traverse to Tail", "Access Node at Index k"),
            matchRight = listOf("O(1) Instant Wire", "O(n) Node Hops", "O(k) Sequential Walk"),
            matchSolution = mapOf(
                "Prepend at Head" to "O(1) Instant Wire",
                "Traverse to Tail" to "O(n) Node Hops",
                "Access Node at Index k" to "O(k) Sequential Walk"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Define the next pointer attribute inside ListNode constructor:",
            targetPrompt = "Slot the pointer field",
            hint = "🎮 Phoenix Cheat Code: self.next links to the subsequent ListNode in the chain!",
            fillCodeTemplate = "class ListNode:\n    def __init__(self, val=0):\n        self.val = val\n        self.[___] = None",
            fillCodeWordBank = listOf("next", "prev", "head", "tail"),
            fillCodeCorrectToken = "next"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Singly linked lists only have forward pointers—backward walks require Doubly Linked Lists!",
            trueFalseStatement = "A Singly Linked List allows O(1) backward navigation from any node to its preceding node.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Dial the time complexity of searching a target value in an unsorted Singly Linked List:",
            targetPrompt = "Dial the worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: You must traverse node-by-node from head to tail in O(n) time!",
            complexityCodeSnippet = "curr = head\nwhile curr:\n    if curr.val == target: return True\n    curr = curr.next",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Complete Floyd's cycle detection where fast moves twice as fast:",
            targetPrompt = "Tap the fast pointer step",
            hint = "🎮 Phoenix Cheat Code: Fast leaps two steps at a time: fast = fast.next.next!",
            fillCodeTemplate = "slow = head\nfast = head\nwhile fast and fast.next:\n    slow = slow.next\n    fast = fast.[___].next\n    if slow == fast: return True",
            fillCodeWordBank = listOf("next", "prev", "head", "val"),
            fillCodeCorrectToken = "next"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Order operations to delete middle node 'curr' in Doubly Linked List:",
            targetPrompt = "Bridge neighbor pointers",
            hint = "🎮 Phoenix Cheat Code: Connect neighbor nodes across curr before severing curr's dangling pointers!",
            orderStepsInitial = listOf(
                "curr.next = None; curr.prev = None",
                "curr.prev.next = curr.next",
                "curr.next.prev = curr.prev"
            ),
            orderStepsSolution = listOf(
                "curr.prev.next = curr.next",
                "curr.next.prev = curr.prev",
                "curr.next = None; curr.prev = None"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Match Linked List structures with their defining topology:",
            targetPrompt = "Pair topologies",
            hint = "🎮 Phoenix Cheat Code: Singly is forward-only; Doubly has dual pointers; Circular wraps tail to head!",
            matchLeft = listOf("Singly Linked", "Doubly Linked", "Circular Linked"),
            matchRight = listOf("Forward Only Wire", "Bidirectional Pointers", "Tail Points to Head"),
            matchSolution = mapOf(
                "Singly Linked" to "Forward Only Wire",
                "Doubly Linked" to "Bidirectional Pointers",
                "Circular Linked" to "Tail Points to Head"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Using prev, curr, and nextNode pointers takes strictly O(1) auxiliary memory!",
            trueFalseStatement = "Reversing a Singly Linked List iteratively can be accomplished in O(1) auxiliary space.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order the 4 steps of reversing a Singly Linked List iteratively:",
            targetPrompt = "Arrange pointer reversal loop",
            hint = "🎮 Phoenix Cheat Code: Save next, flip current arrow backward, step prev and curr forward!",
            orderStepsInitial = listOf(
                "curr.next = prev",
                "nextNode = curr.next",
                "prev = curr; curr = nextNode",
                "prev = None; curr = head"
            ),
            orderStepsSolution = listOf(
                "prev = None; curr = head",
                "nextNode = curr.next",
                "curr.next = prev",
                "prev = curr; curr = nextNode"
            )
        )
    )

    private fun dsaLevel5Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Fill in the Stack pop method to remove and return top element:",
            targetPrompt = "Tap the missing token",
            hint = "🎮 Phoenix Cheat Code: Pop ejects the topmost item off the stack in O(1) speed!",
            fillCodeTemplate = "def pop(self):\n    if not self.is_empty():\n        return self.items.[___]()\n    raise IndexError('Empty')",
            fillCodeWordBank = listOf("pop", "remove", "shift", "poll"),
            fillCodeCorrectToken = "pop"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match each linear structure with its primary access order:",
            targetPrompt = "Tap pair items",
            hint = "🎮 Phoenix Cheat Code: Stack is LIFO (plates), Queue is FIFO (line at store)!",
            matchLeft = listOf("Stack", "Queue", "Deque"),
            matchRight = listOf("LIFO (Last-In First-Out)", "FIFO (First-In First-Out)", "Double-Ended Access"),
            matchSolution = mapOf(
                "Stack" to "LIFO (Last-In First-Out)",
                "Queue" to "FIFO (First-In First-Out)",
                "Deque" to "Double-Ended Access"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Order the steps of the Balanced Parentheses validation algorithm:",
            targetPrompt = "Arrange bracket check loop",
            hint = "🎮 Phoenix Cheat Code: Push opening brackets, pop on closing brackets, verify stack is empty!",
            orderStepsInitial = listOf(
                "If closing bracket: pop top and verify match",
                "If opening bracket: push onto stack",
                "Return stack.is_empty() at the end",
                "Initialize empty stack"
            ),
            orderStepsSolution = listOf(
                "Initialize empty stack",
                "If opening bracket: push onto stack",
                "If closing bracket: pop top and verify match",
                "Return stack.is_empty() at the end"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Queue operates on FIFO—earliest joined element is dequeued first!",
            trueFalseStatement = "A Queue operates on a LIFO (Last-In, First-Out) order where recent items are dequeued first.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Dial time complexity of Push and Pop on a Linked-List backed Stack:",
            targetPrompt = "Select worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: Prepend to head requires zero loop steps—instant O(1)!",
            complexityCodeSnippet = "def push(val): head = Node(val, next=head)\ndef pop(): val = head.val; head = head.next; return val",
            complexityDialCorrect = "O(1)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Which data structure manages function call frames and recursion execution?",
            targetPrompt = "Choose system structure",
            options = listOf("Call Stack", "FIFO Queue", "Binary Heap", "Hash Table"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: The OS call stack pushes frames on function entry and pops on return!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Dequeue an element from the front of Python collections.deque:",
            targetPrompt = "Slot the front removal method",
            hint = "🎮 Phoenix Cheat Code: popleft() dequeues from the head in O(1) time!",
            fillCodeTemplate = "from collections import deque\nq = deque([10, 20, 30])\nfront = q.[___]()\nprint(front)",
            fillCodeWordBank = listOf("popleft", "pop", "remove", "dequeue"),
            fillCodeCorrectToken = "popleft"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Match real-world systems with their core backing data structure:",
            targetPrompt = "Connect applications",
            hint = "🎮 Phoenix Cheat Code: Undo is LIFO (Stack), printing documents in turn is FIFO (Queue)!",
            matchLeft = listOf("Browser Back History", "Printer Document Queue", "Emergency Room Triage"),
            matchRight = listOf("Stack (LIFO)", "Queue (FIFO)", "Priority Queue"),
            matchSolution = mapOf(
                "Browser Back History" to "Stack (LIFO)",
                "Printer Document Queue" to "Queue (FIFO)",
                "Emergency Room Triage" to "Priority Queue"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Modulo index math wraps front and rear pointers without shifting!",
            trueFalseStatement = "A Circular Queue implemented with arrays avoids O(n) element shifting during dequeue.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order the steps of implementing a Queue using Two Stacks (dequeue operation):",
            targetPrompt = "Arrange two-stack dequeue",
            hint = "🎮 Phoenix Cheat Code: If stack2 is empty, pour all of stack1 into stack2, then pop stack2!",
            orderStepsInitial = listOf(
                "Pop and return the top element from stack2",
                "If stack2 is empty: pop all from stack1 and push into stack2",
                "Verify both stacks are not empty"
            ),
            orderStepsSolution = listOf(
                "Verify both stacks are not empty",
                "If stack2 is empty: pop all from stack1 and push into stack2",
                "Pop and return the top element from stack2"
            )
        )
    )

    private fun dsaLevel6Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Match data structures with their optimal insertion locations:",
            targetPrompt = "Connect data structures",
            hint = "🎮 Phoenix Cheat Code: Arrays insert fast at tail, Singly lists prepend fast at head!",
            matchLeft = listOf("Dynamic Array Tail", "Singly List Head", "Queue Back"),
            matchRight = listOf("O(1) Amortized Append", "O(1) Prepend", "O(1) Enqueue"),
            matchSolution = mapOf(
                "Dynamic Array Tail" to "O(1) Amortized Append",
                "Singly List Head" to "O(1) Prepend",
                "Queue Back" to "O(1) Enqueue"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order the steps of finding middle node in Linked List (Tortoise & Hare):",
            targetPrompt = "Arrange runner pointers",
            hint = "🎮 Phoenix Cheat Code: Fast moves 2x while slow moves 1x—when fast reaches end, slow is in the middle!",
            orderStepsInitial = listOf(
                "slow = slow.next; fast = fast.next.next",
                "slow = head; fast = head",
                "While fast and fast.next is not None:",
                "return slow (middle node)"
            ),
            orderStepsSolution = listOf(
                "slow = head; fast = head",
                "While fast and fast.next is not None:",
                "slow = slow.next; fast = fast.next.next",
                "return slow (middle node)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Dial time complexity of Min Stack getMin() query with an auxiliary min stack:",
            targetPrompt = "Select worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: The min stack keeps running minimums at its top—instant O(1) query!",
            complexityCodeSnippet = "def getMin():\n    return min_stack[-1]",
            complexityDialCorrect = "O(1)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Arrays require continuous memory; linked list nodes can scatter throughout RAM!",
            trueFalseStatement = "Linked Lists have better cache locality than Arrays due to pointer indirection.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Check if stack is empty before popping in safe pop implementation:",
            targetPrompt = "Slot the length check",
            hint = "🎮 Phoenix Cheat Code: if len(self.stack) == 0 protects against Underflow errors!",
            fillCodeTemplate = "def safe_pop(self):\n    if len(self.stack) == [___]:\n        return None\n    return self.stack.pop()",
            fillCodeWordBank = listOf("0", "1", "-1", "None"),
            fillCodeCorrectToken = "0"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Which operation on an array of size N always takes O(N) time in worst case?",
            targetPrompt = "Select O(N) operation",
            options = listOf("Insert at Index 0", "Access Index 0", "Pop from End", "Update Index 0"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Inserting at index 0 forces every single one of the N items to shift right!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match problems to their optimal linear solver technique:",
            targetPrompt = "Pair problems to techniques",
            hint = "🎮 Phoenix Cheat Code: Sliding window handles contiguous subarrays; two pointers handles sorted targets!",
            matchLeft = listOf("Maximum Subarray Sum", "Pair Sum in Sorted Array", "Next Greater Element"),
            matchRight = listOf("Kadane's Algorithm", "Two Pointers from Edges", "Monotonic Stack"),
            matchSolution = mapOf(
                "Maximum Subarray Sum" to "Kadane's Algorithm",
                "Pair Sum in Sorted Array" to "Two Pointers from Edges",
                "Next Greater Element" to "Monotonic Stack"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order the steps of Kadane's Algorithm for Maximum Subarray Sum:",
            targetPrompt = "Arrange Kadane loop",
            hint = "🎮 Phoenix Cheat Code: max_ending_here takes max of current element or current + running sum!",
            orderStepsInitial = listOf(
                "max_ending_here = max(x, max_ending_here + x)",
                "max_so_far = max(max_so_far, max_ending_here)",
                "max_so_far = arr[0]; max_ending_here = 0",
                "return max_so_far"
            ),
            orderStepsSolution = listOf(
                "max_so_far = arr[0]; max_ending_here = 0",
                "max_ending_here = max(x, max_ending_here + x)",
                "max_so_far = max(max_so_far, max_ending_here)",
                "return max_so_far"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Monotonic stacks discard smaller elements to maintain strict ordering!",
            trueFalseStatement = "A Monotonic Stack maintains its elements in either strictly ascending or descending order.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Dial the amortized time per element in Monotonic Stack Next Greater Element:",
            targetPrompt = "Select amortized complexity",
            hint = "🎮 Phoenix Cheat Code: Each element is pushed once and popped at most once = O(1) amortized!",
            complexityCodeSnippet = "Total operations across entire array of size N <= 2N pushes and pops.",
            complexityDialCorrect = "O(1)"
        )
    )

    private fun dsaLevel7Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Calculate bucket index using modulo arithmetic with table capacity:",
            targetPrompt = "Slot the modulo operator",
            hint = "🎮 Phoenix Cheat Code: hash(key) % capacity wraps large hashes into valid bucket slots!",
            fillCodeTemplate = "def get_bucket(key, capacity):\n    return hash(key) [___] capacity",
            fillCodeWordBank = listOf("%", "//", "/", "**"),
            fillCodeCorrectToken = "%"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match collision resolution strategies with their mechanism:",
            targetPrompt = "Connect collision mechanisms",
            hint = "🎮 Phoenix Cheat Code: Chaining stores a linked list in each bucket; probing searches nearby slots!",
            matchLeft = listOf("Separate Chaining", "Linear Probing", "Quadratic Probing"),
            matchRight = listOf("Bucket Linked Lists", "Step Forward (i + 1)", "Step by Squares (i + k²)"),
            matchSolution = mapOf(
                "Separate Chaining" to "Bucket Linked Lists",
                "Linear Probing" to "Step Forward (i + 1)",
                "Quadratic Probing" to "Step by Squares (i + k²)"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the steps of Hash Table lookup with Separate Chaining:",
            targetPrompt = "Arrange lookup steps",
            hint = "🎮 Phoenix Cheat Code: Compute bucket index, jump to head of chain, scan linked list for key!",
            orderStepsInitial = listOf(
                "Traverse linked list at bucket looking for key",
                "Compute bucket index: idx = hash(key) % capacity",
                "Return found value or None if end reached"
            ),
            orderStepsSolution = listOf(
                "Compute bucket index: idx = hash(key) % capacity",
                "Traverse linked list at bucket looking for key",
                "Return found value or None if end reached"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: If load factor exceeds threshold (typically 0.75), tables rehash to double capacity!",
            trueFalseStatement = "The Load Factor of a hash table is defined as the number of stored keys divided by total bucket capacity.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial worst-case lookup time in a Hash Table where all N keys collide into 1 bucket:",
            targetPrompt = "Select worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: If every key collides into one bucket, the bucket becomes a linear list of size N = O(n)!",
            complexityCodeSnippet = "All keys hash to bucket 0.\nLookup traverses a linked list of length N.",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is the AVERAGE time complexity of Hash Table key lookup with good hash distribution?",
            targetPrompt = "Select average complexity",
            options = listOf("O(1)", "O(n)", "O(log n)", "O(n²)"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: With uniform hash distribution, lookups take O(1) constant average time!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Complete Two Sum complement check using a hash map:",
            targetPrompt = "Slot complement subtraction",
            hint = "🎮 Phoenix Cheat Code: complement = target - num reveals what matching number we need!",
            fillCodeTemplate = "seen = {}\nfor i, num in enumerate(nums):\n    comp = target [___] num\n    if comp in seen: return [seen[comp], i]\n    seen[num] = i",
            fillCodeWordBank = listOf("-", "+", "*", "//"),
            fillCodeCorrectToken = "-"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Match Hash structures to their unique feature:",
            targetPrompt = "Pair hash structures",
            hint = "🎮 Phoenix Cheat Code: Set stores unique keys; Map stores key-value pairs; Bloom Filter gives fast probabilistic membership!",
            matchLeft = listOf("HashSet", "HashMap", "Bloom Filter"),
            matchRight = listOf("Unique Keys Only", "Key-Value Bindings", "Probabilistic Membership"),
            matchSolution = mapOf(
                "HashSet" to "Unique Keys Only",
                "HashMap" to "Key-Value Bindings",
                "Bloom Filter" to "Probabilistic Membership"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: Hash keys must be immutable so their hash code never changes during program execution!",
            trueFalseStatement = "In Python, mutable objects like lists can be used as keys in a standard dictionary.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 3 steps of Hash Table Rehashing (Resizing):",
            targetPrompt = "Arrange rehashing flow",
            hint = "🎮 Phoenix Cheat Code: Double capacity, recompute hash indices for every key, insert into new buckets!",
            orderStepsInitial = listOf(
                "Re-insert every key by computing new_hash % new_capacity",
                "Allocate new bucket array with 2x capacity",
                "Replace old bucket array with new resized array"
            ),
            orderStepsSolution = listOf(
                "Allocate new bucket array with 2x capacity",
                "Re-insert every key by computing new_hash % new_capacity",
                "Replace old bucket array with new resized array"
            )
        )
    )

    private fun dsaLevel8Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Order the 3 steps of In-Order Binary Tree Traversal:",
            targetPrompt = "Arrange traversal order",
            hint = "🎮 Phoenix Cheat Code: In-Order visits Left first, Root in middle, Right last!",
            orderStepsInitial = listOf("Visit Root Node", "Traverse Left Subtree", "Traverse Right Subtree"),
            orderStepsSolution = listOf("Traverse Left Subtree", "Visit Root Node", "Traverse Right Subtree")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Order the 3 steps of Pre-Order Binary Tree Traversal:",
            targetPrompt = "Arrange pre-order steps",
            hint = "🎮 Phoenix Cheat Code: Pre-Order visits Root FIRST, then Left, then Right!",
            orderStepsInitial = listOf("Traverse Left Subtree", "Visit Root Node", "Traverse Right Subtree"),
            orderStepsSolution = listOf("Visit Root Node", "Traverse Left Subtree", "Traverse Right Subtree")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Order the 3 steps of Post-Order Binary Tree Traversal:",
            targetPrompt = "Arrange post-order steps",
            hint = "🎮 Phoenix Cheat Code: Post-Order visits Left and Right first, Root LAST!",
            orderStepsInitial = listOf("Visit Root Node", "Traverse Right Subtree", "Traverse Left Subtree"),
            orderStepsSolution = listOf("Traverse Left Subtree", "Traverse Right Subtree", "Visit Root Node")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Match tree terms with their structural meaning:",
            targetPrompt = "Pair tree terms",
            hint = "🎮 Phoenix Cheat Code: Root has no parent; Leaves have no children; Depth counts edges from root!",
            matchLeft = listOf("Root Node", "Leaf Node", "Node Depth"),
            matchRight = listOf("Top Node (No Parent)", "Bottom Nodes (No Children)", "Distance from Root"),
            matchSolution = mapOf(
                "Root Node" to "Top Node (No Parent)",
                "Leaf Node" to "Bottom Nodes (No Children)",
                "Node Depth" to "Distance from Root"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: A tree with N vertices always contains exactly N - 1 edges!",
            trueFalseStatement = "A valid Tree of N nodes with no cycles always contains exactly N - 1 edges.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Dial the time complexity to visit all N nodes in a binary tree traversal:",
            targetPrompt = "Select time complexity",
            hint = "🎮 Phoenix Cheat Code: Every node is visited exactly once = O(n) linear time!",
            complexityCodeSnippet = "def traverse(node):\n    if not node: return\n    traverse(node.left)\n    print(node.val)\n    traverse(node.right)",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Calculate maximum depth of a binary tree recursively:",
            targetPrompt = "Slot the max function",
            hint = "🎮 Phoenix Cheat Code: 1 + max(left_depth, right_depth) calculates height!",
            fillCodeTemplate = "def maxDepth(root):\n    if not root: return 0\n    return 1 + [___](maxDepth(root.left), maxDepth(root.right))",
            fillCodeWordBank = listOf("max", "min", "sum", "len"),
            fillCodeCorrectToken = "max"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Which data structure is used to implement Level-Order (BFS) tree traversal?",
            targetPrompt = "Choose BFS structure",
            options = listOf("FIFO Queue", "LIFO Stack", "Priority Heap", "Hash Table"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Level-order traversal processes nodes row-by-row using a FIFO Queue!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: In a full binary tree of height H, the max leaf count is 2^H!",
            trueFalseStatement = "The maximum number of nodes at depth level D of a binary tree is 2^D.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the steps of Level-Order (BFS) Tree Traversal:",
            targetPrompt = "Arrange BFS queue loop",
            hint = "🎮 Phoenix Cheat Code: Enqueue root, dequeue to visit, enqueue children!",
            orderStepsInitial = listOf(
                "node = queue.popleft(); record node.val",
                "If node.left: queue.append(node.left)",
                "If node.right: queue.append(node.right)",
                "queue = deque([root])"
            ),
            orderStepsSolution = listOf(
                "queue = deque([root])",
                "node = queue.popleft(); record node.val",
                "If node.left: queue.append(node.left)",
                "If node.right: queue.append(node.right)"
            )
        )
    )

    private fun dsaLevel9Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match BST search conditions with the branch to take:",
            targetPrompt = "Connect search directions",
            hint = "🎮 Phoenix Cheat Code: If target < root go left; if target > root go right!",
            matchLeft = listOf("target < curr.val", "target > curr.val", "target == curr.val"),
            matchRight = listOf("Descend Left Subtree", "Descend Right Subtree", "Found Target Node"),
            matchSolution = mapOf(
                "target < curr.val" to "Descend Left Subtree",
                "target > curr.val" to "Descend Right Subtree",
                "target == curr.val" to "Found Target Node"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: In-Order traversal of a BST ALWAYS outputs keys in ascending sorted order!",
            trueFalseStatement = "Performing an In-Order traversal on any Binary Search Tree produces a strictly sorted array.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Dial worst-case search time in a skewed (unbalanced) BST resembling a linked list:",
            targetPrompt = "Select worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: If inserted in sorted order, a naive BST degrades into a linear chain of length N = O(n)!",
            complexityCodeSnippet = "Inserted: 1 -> 2 -> 3 -> ... -> N\nTree height h = N",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Complete BST search step when target is smaller than current node:",
            targetPrompt = "Slot the left child pointer",
            hint = "🎮 Phoenix Cheat Code: Walk left when target is smaller than the current key!",
            fillCodeTemplate = "def searchBST(root, val):\n    if not root or root.val == val: return root\n    if val < root.val: return searchBST(root.[___], val)\n    return searchBST(root.right, val)",
            fillCodeWordBank = listOf("left", "right", "parent", "val"),
            fillCodeCorrectToken = "left"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "What is the allowed Balance Factor (height(left) - height(right)) for every node in an AVL tree?",
            targetPrompt = "Select valid AVL balance factors",
            options = listOf("{-1, 0, +1}", "{0, 1, 2}", "{-2, 0, 2}", "Any integer"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: An AVL tree strictly enforces height differences of at most 1: {-1, 0, +1}!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Match AVL imbalance patterns with their restorative rotation:",
            targetPrompt = "Pair AVL rotations",
            hint = "🎮 Phoenix Cheat Code: Left-Left needs Right Rotation; Left-Right needs Left-then-Right Rotation!",
            matchLeft = listOf("Left-Left Heavy", "Right-Right Heavy", "Left-Right Heavy"),
            matchRight = listOf("Single Right Rotation", "Single Left Rotation", "Left then Right Rotation"),
            matchSolution = mapOf(
                "Left-Left Heavy" to "Single Right Rotation",
                "Right-Right Heavy" to "Single Left Rotation",
                "Left-Right Heavy" to "Left then Right Rotation"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Dial worst-case search complexity in a height-balanced AVL Tree of N items:",
            targetPrompt = "Select guaranteed complexity",
            hint = "🎮 Phoenix Cheat Code: Because AVL height is strictly capped at 1.44 * log2(N), search is guaranteed O(log n)!",
            complexityCodeSnippet = "AVL height h <= 1.44 * log2(N)\nEvery descent cuts search space in half.",
            complexityDialCorrect = "O(log n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: In a BST, the in-order predecessor is the maximum value in the left subtree!",
            trueFalseStatement = "When deleting a node with two children in a BST, you can replace it with its in-order successor.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete finding the minimum value node in a BST:",
            targetPrompt = "Slot the left traversal step",
            hint = "🎮 Phoenix Cheat Code: The minimum key is found by following left pointers as far as possible!",
            fillCodeTemplate = "def findMin(root):\n    curr = root\n    while curr and curr.[___]:\n        curr = curr.left\n    return curr",
            fillCodeWordBank = listOf("left", "right", "parent", "val"),
            fillCodeCorrectToken = "left"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Order the 3 steps of validating if a binary tree is a valid BST:",
            targetPrompt = "Arrange BST range check",
            hint = "🎮 Phoenix Cheat Code: Check if current key falls within min_val and max_val, then recurse left and right!",
            orderStepsInitial = listOf(
                "Validate left with max bound: validate(node.left, min_val, node.val)",
                "If not (min_val < node.val < max_val): return False",
                "Validate right with min bound: validate(node.right, node.val, max_val)"
            ),
            orderStepsSolution = listOf(
                "If not (min_val < node.val < max_val): return False",
                "Validate left with max bound: validate(node.left, min_val, node.val)",
                "Validate right with min bound: validate(node.right, node.val, max_val)"
            )
        )
    )

    private fun dsaLevel10Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match heap data structures to their defining invariant:",
            targetPrompt = "Pair heap invariants",
            hint = "🎮 Phoenix Cheat Code: Min-Heap root is minimum; Max-Heap root is maximum!",
            matchLeft = listOf("Min-Heap Root", "Max-Heap Root", "Trie Root"),
            matchRight = listOf("Smallest Element", "Largest Element", "Empty Prefix Node"),
            matchSolution = mapOf(
                "Min-Heap Root" to "Smallest Element",
                "Max-Heap Root" to "Largest Element",
                "Trie Root" to "Empty Prefix Node"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Calculate parent index in a 0-indexed binary heap array:",
            targetPrompt = "Slot the divisor",
            hint = "🎮 Phoenix Cheat Code: parent(i) = (i - 1) // 2 in 0-indexed binary heaps!",
            fillCodeTemplate = "def parent(i):\n    return (i - 1) // [___]",
            fillCodeWordBank = listOf("2", "1", "4", "i"),
            fillCodeCorrectToken = "2"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: In a complete binary heap, children of node i are at 2*i + 1 and 2*i + 2!",
            trueFalseStatement = "A Binary Heap is a complete binary tree that can be compactly represented in a flat array without pointers.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Dial time complexity to extract minimum from a Min-Heap with N elements:",
            targetPrompt = "Select extract-min complexity",
            hint = "🎮 Phoenix Cheat Code: Extracting min replaces root with last item and sifts down: O(log n)!",
            complexityCodeSnippet = "root = heap[0]\nheap[0] = heap.pop()\nsift_down(0) # traverses tree height",
            complexityDialCorrect = "O(log n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Parsons Puzzle",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Order the 3 steps of Min-Heap Insert (Bubble-Up):",
            targetPrompt = "Arrange insert sequence",
            hint = "🎮 Phoenix Cheat Code: Append to end of array, then swap with parent until heap property holds!",
            orderStepsInitial = listOf(
                "While new node < parent: swap with parent",
                "Append new element to end of heap array",
                "Update current index to parent position"
            ),
            orderStepsSolution = listOf(
                "Append new element to end of heap array",
                "While new node < parent: swap with parent",
                "Update current index to parent position"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is the time complexity of the Floyd's build_heap (heapify) algorithm for N elements?",
            targetPrompt = "Select build_heap complexity",
            options = listOf("O(n)", "O(n log n)", "O(n²)", "O(log n)"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Bottom-up heapify runs in linear O(n) time via converging series summation!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "DuoPencil",
            promptSentence = "Complete Trie character branch lookup:",
            targetPrompt = "Slot the children dictionary",
            hint = "🎮 Phoenix Cheat Code: Each Trie node indexes downstream characters inside its children map!",
            fillCodeTemplate = "curr = self.root\nfor ch in word:\n    if ch not in curr.[___]:\n        return False\n    curr = curr.children[ch]\nreturn curr.is_end_of_word",
            fillCodeWordBank = listOf("children", "nodes", "chars", "next"),
            fillCodeCorrectToken = "children"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match prefix structures to their best use-case:",
            targetPrompt = "Connect applications",
            hint = "🎮 Phoenix Cheat Code: Tries excel at autocomplete prefix matching; Heaps excel at Top-K elements!",
            matchLeft = listOf("Trie Prefix Tree", "Max-Heap", "Min-Heap of Size K"),
            matchRight = listOf("Search Autocomplete", "Order Priority Queue", "Top K Largest Elements"),
            matchSolution = mapOf(
                "Trie Prefix Tree" to "Search Autocomplete",
                "Max-Heap" to "Order Priority Queue",
                "Min-Heap of Size K" to "Top K Largest Elements"
            )
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Make your decision",
            hint = "🎮 Phoenix Cheat Code: In a Trie, search time depends strictly on word length L, independent of total words N!",
            trueFalseStatement = "Searching for a word of length L in a Trie takes O(L) time regardless of how many millions of words are stored.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial time complexity to inspect the minimum element in a Min-Heap without removing it:",
            targetPrompt = "Select peek complexity",
            hint = "🎮 Phoenix Cheat Code: The minimum element always sits at index 0—instant O(1) peek!",
            complexityCodeSnippet = "def peek():\n    return heap[0] # root element",
            complexityDialCorrect = "O(1)"
        )
    )

    private fun dsaLevel11Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "What fundamental access ordering rule does a Stack data structure follow?",
            targetPrompt = "Select the Stack principle",
            options = listOf("LIFO (Last In, First Out)", "FIFO (First In, First Out)", "Random Access", "Sorted Access"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Like a stack of plates—the last plate put on top is the first one taken off!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Complete the Stack peek function to inspect the top plate without removing it:",
            targetPrompt = "Access the last element",
            hint = "🎮 Phoenix Cheat Code: In a Python list acting as a stack, index -1 points directly to the top element!",
            fillCodeTemplate = "class Stack:\n    def __init__(self):\n        self.items = []\n    def peek(self):\n        return self.items[[___]]",
            fillCodeWordBank = listOf("-1", "0", "top", "1"),
            fillCodeCorrectToken = "-1"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match each Stack operation with its behavior:",
            targetPrompt = "Connect operations",
            hint = "🎮 Phoenix Cheat Code: Push adds to top, Pop removes from top, Peek reads top without removing!",
            matchLeft = listOf("push(val)", "pop()", "peek()"),
            matchRight = listOf("Adds element to the top", "Removes & returns top element", "Inspects top without removal"),
            matchSolution = mapOf("push(val)" to "Adds element to the top", "pop()" to "Removes & returns top element", "peek()" to "Inspects top without removal")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Determine validity",
            hint = "🎮 Phoenix Cheat Code: Push and Pop at the stack top take immediate O(1) time!",
            trueFalseStatement = "Push and Pop operations on a Stack take O(1) constant time because operations occur strictly at the top.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial the time complexity of searching for an element at the bottom of an unsorted Stack:",
            targetPrompt = "Select search complexity",
            hint = "🎮 Phoenix Cheat Code: You must pop through every single element above it—that's O(n) linear scans!",
            complexityCodeSnippet = "def search_stack(s, target):\n    # must pop elements until target found\n    while not s.is_empty():\n        if s.pop() == target: return True",
            complexityDialCorrect = "O(n)"
        )
    )

    private fun dsaLevel12Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Multiple Choice",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "If you push [10, 20, 30] sequentially onto an empty stack, which element is now at the top?",
            targetPrompt = "Identify top element",
            options = listOf("30", "10", "20", "None"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: 30 was pushed last, so it rests directly at the top!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Complete the push method to insert val onto the top of the internal storage:",
            targetPrompt = "Choose method",
            hint = "🎮 Phoenix Cheat Code: Appending to the end of a dynamic array models stack push in O(1) amortized time!",
            fillCodeTemplate = "def push(self, val):\n    self.storage.[___](val)",
            fillCodeWordBank = listOf("append", "insert", "shift", "add"),
            fillCodeCorrectToken = "append"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Order Sequence",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Arrange the stack state after executing: push(5), push(9), push(2) (from Top to Bottom):",
            targetPrompt = "Order from Top to Bottom",
            hint = "🎮 Phoenix Cheat Code: 2 was pushed last (Top), 9 in middle, 5 at the very bottom!",
            orderStepsInitial = listOf("9 (Middle)", "5 (Bottom)", "2 (Top)"),
            orderStepsSolution = listOf("2 (Top)", "9 (Middle)", "5 (Bottom)")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Evaluate condition",
            hint = "🎮 Phoenix Cheat Code: Attempting to push onto a full fixed-size stack triggers Stack Overflow!",
            trueFalseStatement = "Attempting to push an item into a fixed-capacity stack that is already full causes a 'Stack Overflow' error.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Where is the function call stack located during program execution?",
            targetPrompt = "Select memory area",
            options = listOf("Call Stack in RAM", "Disk Storage", "Browser Cache", "GPU Memory"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Local function frames are allocated and deallocated on the system Call Stack!"
        )
    )

    private fun dsaLevel13Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Multiple Choice",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Given stack [A, B, C] where C is on top, what does pop() return?",
            targetPrompt = "Identify popped value",
            options = listOf("C", "A", "B", "None"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Pop always removes and returns the topmost item, which is C!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Guard against popping from an empty stack:",
            targetPrompt = "Complete error check",
            hint = "🎮 Phoenix Cheat Code: When a stack is empty, popping causes Stack Underflow!",
            fillCodeTemplate = "def pop(self):\n    if self.[___]():\n        raise IndexError(\"Stack Underflow\")\n    return self.items.pop()",
            fillCodeWordBank = listOf("is_empty", "is_full", "has_items", "reset"),
            fillCodeCorrectToken = "is_empty"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match stack operations with their return values for stack [7, 14, 21] (top is 21):",
            targetPrompt = "Pair operations",
            hint = "🎮 Phoenix Cheat Code: pop() returns 21, then peek() returns 14!",
            matchLeft = listOf("First pop()", "Subsequent peek()", "Final size after 1 pop"),
            matchRight = listOf("Returns 21", "Returns 14", "Size is 2"),
            matchSolution = mapOf("First pop()" to "Returns 21", "Subsequent peek()" to "Returns 14", "Final size after 1 pop" to "Size is 2")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Verify fact",
            hint = "🎮 Phoenix Cheat Code: Popping an empty stack is an illegal operation called Underflow!",
            trueFalseStatement = "Calling pop() on an empty stack is known as Stack Underflow.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial the time complexity of popping the top element from a Linked-List based stack:",
            targetPrompt = "Select pop complexity",
            hint = "🎮 Phoenix Cheat Code: Updating head = head.next takes instant O(1) time!",
            complexityCodeSnippet = "def pop(self):\n    val = self.head.val\n    self.head = self.head.next\n    return val",
            complexityDialCorrect = "O(1)"
        )
    )

    private fun dsaLevel14Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Multiple Choice",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Which data structure is optimal for verifying balanced parentheses like '{[()]}'?",
            targetPrompt = "Select optimal structure",
            options = listOf("Stack", "Queue", "Binary Tree", "Heap"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Push opening brackets onto a stack; match and pop on closing brackets!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Complete bracket matching validation logic:",
            targetPrompt = "Fill stack operation",
            hint = "🎮 Phoenix Cheat Code: When encountering an opening bracket '(', push it onto the stack!",
            fillCodeTemplate = "stack = []\nfor ch in s:\n    if ch in '({[':\n        stack.[___](ch)\n    elif ch in ')}]':\n        if not stack: return False\n        stack.pop()",
            fillCodeWordBank = listOf("append", "remove", "pop", "sort"),
            fillCodeCorrectToken = "append"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Determine string validity",
            hint = "🎮 Phoenix Cheat Code: The brackets are crossed: '(' closes before '['!",
            trueFalseStatement = "The string '([)]' has properly balanced and nested parentheses.",
            trueFalseIsCorrectTrue = false
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match bracket expressions to their balance status:",
            targetPrompt = "Match expressions",
            hint = "🎮 Phoenix Cheat Code: Track matching pairs: () and {}!",
            matchLeft = listOf("{ [ ] }", "( ( )", ") ("),
            matchRight = listOf("Valid & Balanced", "Unbalanced (Missing Close)", "Unbalanced (Premature Close)"),
            matchSolution = mapOf("{ [ ] }" to "Valid & Balanced", "( ( )" to "Unbalanced (Missing Close)", ") (" to "Unbalanced (Premature Close)")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Why does the browser's 'Back' button use a Stack?",
            targetPrompt = "Explain navigation",
            options = listOf("The most recently visited URL must be popped first", "Pages load in FIFO order", "URLs are stored alphabetically", "Memory is smaller"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Back navigation pops the previous page in LIFO order!"
        )
    )

    private fun dsaLevel15Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss Challenge",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "⚔️ BOSS TRIAL 1: Given pushes [1, 2, 3] then two pops, what is currently left on the stack?",
            targetPrompt = "Calculate remaining element",
            options = listOf("1", "2", "3", "Empty"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Pushed 1, 2, 3. Popped 3, then popped 2. Only 1 remains!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Boss Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "⚔️ BOSS TRIAL 2: Implement undo in a text editor using a stack:",
            targetPrompt = "Choose pop",
            hint = "🎮 Phoenix Cheat Code: To undo the latest action, pop it from the history stack!",
            fillCodeTemplate = "def undo(self):\n    if not self.history:\n        return None\n    last_action = self.history.[___]()\n    return last_action",
            fillCodeWordBank = listOf("pop", "push", "clear", "shift"),
            fillCodeCorrectToken = "pop"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Boss Big-O",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "⚔️ BOSS TRIAL 3: Dial space complexity required to evaluate a valid parentheses string of length N with a stack:",
            targetPrompt = "Select space complexity",
            hint = "🎮 Phoenix Cheat Code: In the worst case (e.g. '((((('), all N characters are pushed: O(n) space!",
            complexityCodeSnippet = "def is_valid(s):\n    stack = [] # holds up to N opening brackets",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Boss Sequence",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "⚔️ BOSS TRIAL 4: Trace evaluation of postfix expression '3 4 + 2 *' using a stack:",
            targetPrompt = "Order evaluation steps",
            hint = "🎮 Phoenix Cheat Code: 3 + 4 = 7, then 7 * 2 = 14!",
            orderStepsInitial = listOf("Push 7 onto stack", "Multiply 7 * 2 = 14", "Add 3 + 4 = 7"),
            orderStepsSolution = listOf("Add 3 + 4 = 7", "Push 7 onto stack", "Multiply 7 * 2 = 14")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Boss Verdict",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "⚔️ BOSS TRIAL 5: Swipe to escape the Tower of Plates:",
            targetPrompt = "Final verification",
            hint = "🎮 Phoenix Cheat Code: In recursion, every active function call occupies a stack frame on the call stack!",
            trueFalseStatement = "Recursion inherently relies on the system Call Stack to remember return addresses and local variables.",
            trueFalseIsCorrectTrue = true
        )
    )

    private fun dsaLevel16Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "What is the primary governing principle of a Queue?",
            targetPrompt = "Select Queue rule",
            options = listOf("FIFO (First In, First Out)", "LIFO (Last In, First Out)", "Priority First", "Random"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Just like standing in a grocery line—the first customer to arrive is served first!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Import Python's high-performance double-ended queue for O(1) pops and appends:",
            targetPrompt = "Type class name",
            hint = "🎮 Phoenix Cheat Code: collections.deque provides O(1) operations on both ends!",
            fillCodeTemplate = "from collections import [___]\nq = deque()\nq.append(10)\nq.popleft()",
            fillCodeWordBank = listOf("deque", "queue", "stack", "list"),
            fillCodeCorrectToken = "deque"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match Queue terminology with its real-world counterpart:",
            targetPrompt = "Connect concepts",
            hint = "🎮 Phoenix Cheat Code: Front is the service window, Rear is the back of the line!",
            matchLeft = listOf("Enqueue", "Dequeue", "Front", "Rear"),
            matchRight = listOf("Joining the back of the line", "Being served and leaving", "First person in line", "Last person in line"),
            matchSolution = mapOf("Enqueue" to "Joining the back of the line", "Dequeue" to "Being served and leaving", "Front" to "First person in line", "Rear" to "Last person in line")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Evaluate Queue behavior",
            hint = "🎮 Phoenix Cheat Code: Elements enter at the rear and exit at the front!",
            trueFalseStatement = "In a Queue, new elements are added at the rear and removed from the front.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial time complexity of enqueueing to the rear of a Linked List queue with a tail pointer:",
            targetPrompt = "Select enqueue complexity",
            hint = "🎮 Phoenix Cheat Code: With a tail pointer, tail.next = new_node is instant O(1) time!",
            complexityCodeSnippet = "def enqueue(self, val):\n    self.tail.next = Node(val)\n    self.tail = self.tail.next",
            complexityDialCorrect = "O(1)"
        )
    )

    private fun dsaLevel17Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Multiple Choice",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "If an empty queue has [A, B, C] enqueued in that order, who is first in line?",
            targetPrompt = "Identify front person",
            options = listOf("A", "C", "B", "None"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: A entered first, so A is at the front of the queue!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Complete the enqueue operation on a deque:",
            targetPrompt = "Choose enqueue method",
            hint = "🎮 Phoenix Cheat Code: append adds directly to the right (rear) of the deque!",
            fillCodeTemplate = "def enqueue(self, item):\n    self.queue.[___](item)",
            fillCodeWordBank = listOf("append", "pop", "insert", "extend"),
            fillCodeCorrectToken = "append"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Order Sequence",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Order the queue contents after enqueuing 10, then 20, then 30 (from Front to Rear):",
            targetPrompt = "Order from Front to Rear",
            hint = "🎮 Phoenix Cheat Code: 10 was first (Front), 20 in middle, 30 arrived last (Rear)!",
            orderStepsInitial = listOf("30 (Rear)", "10 (Front)", "20 (Middle)"),
            orderStepsSolution = listOf("10 (Front)", "20 (Middle)", "30 (Rear)")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Analyze array shift cost",
            hint = "🎮 Phoenix Cheat Code: Using a plain Python list for queue dequeues requires shifting all N elements: O(n)!",
            trueFalseStatement = "Using list.pop(0) on a Python standard list takes O(n) time because all remaining elements must shift left by 1 index.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Which real-world hardware component uses a queue buffer?",
            targetPrompt = "Identify queue system",
            options = listOf("Printer Spooler", "Calculator Display", "ROM BIOS", "Power Supply"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Print jobs are printed in the exact order they arrive: FIFO printer spooler!"
        )
    )

    private fun dsaLevel18Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Multiple Choice",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Queue contains [Cat, Dog, Bird] with Cat at Front. After dequeue(), what is returned?",
            targetPrompt = "Identify dequeued value",
            options = listOf("Cat", "Bird", "Dog", "None"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Cat is at the front, so Cat is dequeued first!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Complete deque removal from the front in O(1) time:",
            targetPrompt = "Select popleft",
            hint = "🎮 Phoenix Cheat Code: deque.popleft() removes the front item in instant O(1) time!",
            fillCodeTemplate = "def dequeue(self):\n    if not self.queue:\n        return None\n    return self.queue.[___]()",
            fillCodeWordBank = listOf("popleft", "pop", "remove", "shift"),
            fillCodeCorrectToken = "popleft"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match queue state transitions starting with [1, 2, 3]:",
            targetPrompt = "Track operations",
            hint = "🎮 Phoenix Cheat Code: Dequeue 1 leaves [2, 3]; Enqueue 4 makes it [2, 3, 4]!",
            matchLeft = listOf("After 1 dequeue", "After then enqueuing 4", "New Front element"),
            matchRight = listOf("Queue is [2, 3]", "Queue is [2, 3, 4]", "Element 2"),
            matchSolution = mapOf("After 1 dequeue" to "Queue is [2, 3]", "After then enqueuing 4" to "Queue is [2, 3, 4]", "New Front element" to "Element 2")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Assess empty condition",
            hint = "🎮 Phoenix Cheat Code: If front > rear or count == 0, the queue is empty!",
            trueFalseStatement = "Calling dequeue on an empty queue is an underflow error.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial the time complexity of dequeuing from a circular queue with array indices:",
            targetPrompt = "Select circular dequeue complexity",
            hint = "🎮 Phoenix Cheat Code: front = (front + 1) % capacity takes instant O(1) arithmetic!",
            complexityCodeSnippet = "def dequeue():\n    val = arr[front]\n    front = (front + 1) % size\n    return val",
            complexityDialCorrect = "O(1)"
        )
    )

    private fun dsaLevel19Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Multiple Choice",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Why is a Circular Queue preferred over a standard linear array queue?",
            targetPrompt = "Explain circular benefit",
            options = listOf("Reuses vacated slots at the front", "Holds infinite elements", "Sorts elements automatically", "Has smaller pointers"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: A circular queue wraps around to index 0 using modulo math, reusing freed space!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Complete the index wrap-around formula in a Circular Queue:",
            targetPrompt = "Insert modulo operator",
            hint = "🎮 Phoenix Cheat Code: The % modulo operator wraps the pointer back to index 0 when reaching capacity!",
            fillCodeTemplate = "next_rear = (rear + 1) [___] capacity",
            fillCodeWordBank = listOf("%", "/", "//", "*"),
            fillCodeCorrectToken = "%"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Analyze ring buffer",
            hint = "🎮 Phoenix Cheat Code: In a ring buffer, front and rear advance cyclically without moving data!",
            trueFalseStatement = "In a circular buffer of size 5, advancing index 4 by 1 brings the pointer back to index 0.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Match buffer conditions with their pointer states:",
            targetPrompt = "Identify buffer states",
            hint = "🎮 Phoenix Cheat Code: count == 0 means empty; count == capacity means full!",
            matchLeft = listOf("Buffer Empty", "Buffer Full", "Next Slot Calculation"),
            matchRight = listOf("count == 0", "count == capacity", "(index + 1) % capacity"),
            matchSolution = mapOf("Buffer Empty" to "count == 0", "Buffer Full" to "count == capacity", "Next Slot Calculation" to "(index + 1) % capacity")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Which graph traversal algorithm fundamentally relies on a Queue?",
            targetPrompt = "Select traversal algorithm",
            options = listOf("BFS (Breadth-First Search)", "DFS (Depth-First Search)", "Binary Search", "Quicksort"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: BFS explores level-by-level using a FIFO Queue!"
        )
    )

    private fun dsaLevel20Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss Challenge",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "⚔️ BOSS TRIAL 1: Queue has elements [10, 20]. We execute: enqueue(30), dequeue(), dequeue(). Who remains?",
            targetPrompt = "Calculate remaining element",
            options = listOf("30", "10", "20", "Empty"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: [10, 20, 30]. Dequeue removed 10, then 20. 30 is still in the queue!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Boss Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "⚔️ BOSS TRIAL 2: Implement BFS node processing queue step:",
            targetPrompt = "Choose popleft",
            hint = "🎮 Phoenix Cheat Code: BFS extracts the front vertex using popleft()!",
            fillCodeTemplate = "def bfs(graph, start):\n    queue = deque([start])\n    while queue:\n        node = queue.[___]()\n        visit(node)",
            fillCodeWordBank = listOf("popleft", "pop", "peek", "clear"),
            fillCodeCorrectToken = "popleft"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Boss Big-O",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "⚔️ BOSS TRIAL 3: Dial time complexity to visit all V vertices and E edges using BFS with a queue:",
            targetPrompt = "Select BFS complexity",
            hint = "🎮 Phoenix Cheat Code: Each vertex and edge is enqueued and examined once: O(V + E)!",
            complexityCodeSnippet = "def bfs_complexity():\n    # enqueues V vertices, explores E edges",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Boss Match",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "⚔️ BOSS TRIAL 4: Contrast Stack and Queue characteristics:",
            targetPrompt = "Match characteristics",
            hint = "🎮 Phoenix Cheat Code: Stacks are LIFO for DFS & Undo; Queues are FIFO for BFS & Lines!",
            matchLeft = listOf("Stack", "Queue", "Priority Queue"),
            matchRight = listOf("LIFO (Last In First Out) • DFS", "FIFO (First In First Out) • BFS", "Highest priority served first"),
            matchSolution = mapOf("Stack" to "LIFO (Last In First Out) • DFS", "Queue" to "FIFO (First In First Out) • BFS", "Priority Queue" to "Highest priority served first")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Boss Verdict",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "⚔️ BOSS TRIAL 5: Swipe to master the Ticket Rush:",
            targetPrompt = "Final queue verification",
            hint = "🎮 Phoenix Cheat Code: Two stacks can simulate a queue where one stack handles enqueue and the other handles dequeue!",
            trueFalseStatement = "A FIFO Queue can be fully implemented using two LIFO Stacks.",
            trueFalseIsCorrectTrue = true
        )
    )

    private fun dsaLevel21Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "What is the topmost node in a tree data structure with no parent called?",
            targetPrompt = "Identify top node",
            options = listOf("Root", "Leaf", "Branch", "Edge"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: The Root is the sovereign crown node from which the entire tree originates!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Complete the TreeNode class definition with value and left/right children:",
            targetPrompt = "Initialize left child",
            hint = "🎮 Phoenix Cheat Code: In a binary tree, each node starts with left and right child pointers set to None!",
            fillCodeTemplate = "class TreeNode:\n    def __init__(self, val):\n        self.val = val\n        self.left = [___]\n        self.right = None",
            fillCodeWordBank = listOf("None", "0", "self", "root"),
            fillCodeCorrectToken = "None"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Match tree terms with their definitions:",
            targetPrompt = "Connect tree terms",
            hint = "🎮 Phoenix Cheat Code: Root has no parent, Leaves have no children, Edges connect nodes!",
            matchLeft = listOf("Root Node", "Leaf Node", "Edge"),
            matchRight = listOf("Top node with no parent", "Node with zero children", "Link connecting two nodes"),
            matchSolution = mapOf("Root Node" to "Top node with no parent", "Leaf Node" to "Node with zero children", "Edge" to "Link connecting two nodes")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Verify tree property",
            hint = "🎮 Phoenix Cheat Code: A valid tree with N nodes always contains exactly N - 1 edges and no cycles!",
            trueFalseStatement = "A connected tree with N nodes always has exactly N - 1 edges and zero cycles.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial time complexity to access the Root node of a tree from a stored reference:",
            targetPrompt = "Select root access complexity",
            hint = "🎮 Phoenix Cheat Code: Accessing tree.root is an instant O(1) pointer dereference!",
            complexityCodeSnippet = "def get_root(tree):\n    return tree.root",
            complexityDialCorrect = "O(1)"
        )
    )

    private fun dsaLevel22Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Multiple Choice",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "What do we call two nodes that share the exact same immediate parent node?",
            targetPrompt = "Name relationship",
            options = listOf("Siblings", "Cousins", "Ancestors", "Leaves"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Nodes sharing the same parent are called Siblings!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Calculate the total number of children a binary tree node has:",
            targetPrompt = "Count valid pointers",
            hint = "🎮 Phoenix Cheat Code: Sum 1 for left if present, plus 1 for right if present!",
            fillCodeTemplate = "def count_children(node):\n    count = 0\n    if node.[___] is not None: count += 1\n    if node.right is not None: count += 1\n    return count",
            fillCodeWordBank = listOf("left", "parent", "root", "val"),
            fillCodeCorrectToken = "left"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Examine binary tree rule",
            hint = "🎮 Phoenix Cheat Code: In a strictly binary tree, a node can have at most 2 children: left and right!",
            trueFalseStatement = "In a binary tree, any node can have at most two direct child nodes.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Match ancestral terms in a tree structure:",
            targetPrompt = "Connect relations",
            hint = "🎮 Phoenix Cheat Code: Ancestor is above on the path to root; Descendant is below!",
            matchLeft = listOf("Parent", "Descendant", "Degree of a Node"),
            matchRight = listOf("Immediate predecessor above", "Any node downstream on path", "Number of children of the node"),
            matchSolution = mapOf("Parent" to "Immediate predecessor above", "Descendant" to "Any node downstream on path", "Degree of a Node" to "Number of children of the node")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "In a tree of height H, what is the depth of the root node?",
            targetPrompt = "Identify root depth",
            options = listOf("0", "1", "H", "Infinite"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: By standard computer science convention, the root has depth 0!"
        )
    )

    private fun dsaLevel23Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Multiple Choice",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "How can you programmatically verify that a node is a leaf node in a binary tree?",
            targetPrompt = "Identify leaf condition",
            options = listOf("node.left is None and node.right is None", "node.val == 0", "node.parent is None", "node.left != node.right"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: A leaf has zero children—both left and right pointers are None!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Complete recursive leaf detection:",
            targetPrompt = "Check right child",
            hint = "🎮 Phoenix Cheat Code: Both left and right must be None for a node to be a leaf!",
            fillCodeTemplate = "def is_leaf(node):\n    if node is None: return False\n    return node.left is None and node.[___] is None",
            fillCodeWordBank = listOf("right", "val", "parent", "next"),
            fillCodeCorrectToken = "right"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match tree metrics with their definitions:",
            targetPrompt = "Match metrics",
            hint = "🎮 Phoenix Cheat Code: Height is longest path to a leaf; Depth is path length from root!",
            matchLeft = listOf("Height of Tree", "Depth of Node", "Full Binary Tree"),
            matchRight = listOf("Longest path from root to leaf", "Number of edges from root to node", "Every node has 0 or 2 children"),
            matchSolution = mapOf("Height of Tree" to "Longest path from root to leaf", "Depth of Node" to "Number of edges from root to node", "Full Binary Tree" to "Every node has 0 or 2 children")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Evaluate leaf node count",
            hint = "🎮 Phoenix Cheat Code: A single-node tree has the root acting as the only leaf node!",
            trueFalseStatement = "In a tree with only a single node, that node is simultaneously both the root and a leaf.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Big-O Dial",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Dial time complexity to count all leaf nodes in a tree with N total nodes:",
            targetPrompt = "Select counting complexity",
            hint = "🎮 Phoenix Cheat Code: You must inspect all N nodes in the tree: O(n) linear time!",
            complexityCodeSnippet = "def count_leaves(root):\n    if not root: return 0\n    if not root.left and not root.right: return 1\n    return count_leaves(root.left) + count_leaves(root.right)",
            complexityDialCorrect = "O(n)"
        )
    )

    private fun dsaLevel24Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Concept Check",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "Which traversal visits the root first, then left subtree, then right subtree?",
            targetPrompt = "Select traversal type",
            options = listOf("Preorder Traversal (Root, Left, Right)", "Inorder Traversal (Left, Root, Right)", "Postorder Traversal (Left, Right, Root)", "Level-order Traversal"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: 'Pre' means BEFORE: the Root is visited first!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Fill in Code",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "Complete Inorder Traversal (Left -> Root -> Right):",
            targetPrompt = "Insert visit step",
            hint = "🎮 Phoenix Cheat Code: In Inorder, append the current root's value between left and right recursions!",
            fillCodeTemplate = "def inorder(root, res):\n    if not root: return\n    inorder(root.left, res)\n    res.append(root.[___])\n    inorder(root.right, res)",
            fillCodeWordBank = listOf("val", "left", "right", "root"),
            fillCodeCorrectToken = "val"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Match Pairs",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "Match DFS tree traversal orders with their sequence pattern:",
            targetPrompt = "Connect traversal patterns",
            hint = "🎮 Phoenix Cheat Code: Pre = Root first; In = Root in middle; Post = Root last!",
            matchLeft = listOf("Preorder", "Inorder", "Postorder"),
            matchRight = listOf("Root → Left → Right", "Left → Root → Right", "Left → Right → Root"),
            matchSolution = mapOf("Preorder" to "Root → Left → Right", "Inorder" to "Left → Root → Right", "Postorder" to "Left → Right → Root")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Fact Swipe",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "Swipe left for False, right for True:",
            targetPrompt = "Verify BST property",
            hint = "🎮 Phoenix Cheat Code: An Inorder traversal of any Binary Search Tree produces strictly sorted ascending order!",
            trueFalseStatement = "An Inorder traversal of a Binary Search Tree (BST) always visits keys in sorted, ascending order.",
            trueFalseIsCorrectTrue = true
        ),
        QuizQuestion(
            gameType = DSAExerciseType.ORDER_STEPS,
            typeTitle = "Order Sequence",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "Order the nodes visited in Preorder for a root (A) with left child (B) and right child (C):",
            targetPrompt = "Order traversal",
            hint = "🎮 Phoenix Cheat Code: Preorder visits Root (A), then Left (B), then Right (C)!",
            orderStepsInitial = listOf("C (Right)", "A (Root)", "B (Left)"),
            orderStepsSolution = listOf("A (Root)", "B (Left)", "C (Right)")
        )
    )

    private fun dsaLevel25Questions(): List<QuizQuestion> = listOf(
        QuizQuestion(
            gameType = DSAExerciseType.MULTIPLE_CHOICE,
            typeTitle = "Boss Challenge",
            speakerName = "Duo",
            speakerImage = "Phoenix",
            promptSentence = "⚔️ BOSS TRIAL 1: In a balanced Binary Search Tree with N nodes, what is the search time complexity?",
            targetPrompt = "Select search complexity",
            options = listOf("O(log n)", "O(n)", "O(1)", "O(n²)"),
            correctOptionIndex = 0,
            hint = "🎮 Phoenix Cheat Code: Halving the search space at each branch yields lightning-fast O(log n) time!"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.FILL_CODE,
            typeTitle = "Boss Code",
            speakerName = "Lily",
            speakerImage = "Lily",
            promptSentence = "⚔️ BOSS TRIAL 2: Complete the recursive tree height calculation:",
            targetPrompt = "Choose max function",
            hint = "🎮 Phoenix Cheat Code: Height is 1 plus the maximum height between left and right subtrees!",
            fillCodeTemplate = "def height(root):\n    if not root: return 0\n    return 1 + [___](height(root.left), height(root.right))",
            fillCodeWordBank = listOf("max", "min", "sum", "len"),
            fillCodeCorrectToken = "max"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.COMPLEXITY_DIAL,
            typeTitle = "Boss Big-O",
            speakerName = "Oscar",
            speakerImage = "Oscar",
            promptSentence = "⚔️ BOSS TRIAL 3: Dial worst-case search complexity in a completely skewed (degenerate) BST resembling a linked list:",
            targetPrompt = "Select worst-case complexity",
            hint = "🎮 Phoenix Cheat Code: When every node has only 1 child, the tree degenerates into an O(n) line!",
            complexityCodeSnippet = "def search_skewed_bst(root, key):\n    # tree is a straight line of N nodes",
            complexityDialCorrect = "O(n)"
        ),
        QuizQuestion(
            gameType = DSAExerciseType.MATCH_PAIRS,
            typeTitle = "Boss Match",
            speakerName = "Vikram",
            speakerImage = "Vikram",
            promptSentence = "⚔️ BOSS TRIAL 4: Match the 5 Master Data Structures to their primary superpower:",
            targetPrompt = "Match data structures",
            hint = "🎮 Phoenix Cheat Code: Array O(1) index, Linked List O(1) insert, Stack LIFO, Queue FIFO, Tree Hierarchy!",
            matchLeft = listOf("Array", "Linked List", "Stack", "Queue"),
            matchRight = listOf("Instant O(1) index access", "Fast pointer insertion without shifting", "LIFO execution & undo stacks", "FIFO fairness & buffering"),
            matchSolution = mapOf("Array" to "Instant O(1) index access", "Linked List" to "Fast pointer insertion without shifting", "Stack" to "LIFO execution & undo stacks", "Queue" to "FIFO fairness & buffering")
        ),
        QuizQuestion(
            gameType = DSAExerciseType.TRUE_FALSE_SWIPE,
            typeTitle = "Grand Finale",
            speakerName = "Junior",
            speakerImage = "Junior",
            promptSentence = "⚔️ GRAND FINALE: Swipe to claim the title of Data Structures Master 🏆:",
            targetPrompt = "Final confirmation",
            hint = "🎮 Phoenix Cheat Code: Congratulations! You've mastered Arrays, Linked Lists, Stacks, Queues, and Trees!",
            trueFalseStatement = "Mastering contiguous arrays, dynamic pointer nodes, LIFO stacks, FIFO queues, and hierarchical trees provides the bedrock for all modern software engineering.",
            trueFalseIsCorrectTrue = true
        )
    )

}
