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
        val effectiveLevel = if (levelNumber in 1..10) levelNumber else (((levelNumber - 1) % 10) + 1)
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
            else -> dsaLevel10Questions()
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
}
