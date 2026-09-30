package com.simats.duolingo.data.repository

import com.simats.duolingo.domain.model.*

/**
 * Single source of truth for the complete Data Structures & Algorithms Course.
 * Implements the 11 Metaphor Worlds, 18 Phoenix evolution milestones,
 * and signature interactive activities across all units.
 */
object CourseRepository {

    val dsaCourse: Course by lazy {
        Course(
            id = "dsa_master",
            title = "Data Structures & Algorithms",
            units = listOf(
                buildUnit1(),
                buildUnit2(),
                buildUnit3(),
                buildUnit4(),
                buildUnit5(),
                buildUnit6(),
                buildUnit7(),
                buildUnit8(),
                buildUnit9(),
                buildUnit10(),
                buildUnit11()
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 1: Foundations — The Hatchery Warehouse                      */
    /* ------------------------------------------------------------------ */
    private fun buildUnit1(): DsaUnit {
        return DsaUnit(
            id = "unit_1_foundations",
            index = 1,
            title = "Foundations",
            subtitle = "The Hatchery Warehouse",
            difficulty = Difficulty.GREEN,
            worldTheme = "Warehouse of Primordial Eggs",
            themeColorHex = 0xFF58CC02,
            themeDarkColorHex = 0xFF46A302,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u1_mid", 2, "Shell cracks under initial knowledge!"),
                PhoenixTrigger("u1_boss", 3, "Baby Fledgling stirs upon defeating Chaos Egg!")
            ),
            boss = BossBattle(
                id = "boss_1_chaos_egg",
                title = "The Chaos Egg",
                subtitle = "Rapid Classification Trial",
                bossName = "Primordial Chaos",
                maxHp = 100,
                timeLimitSeconds = 45,
                rewardXp = 50,
                phases = 3
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u1_l1", unitId = "unit_1_foundations", index = 1,
                    title = "What is a Data Structure?",
                    subtitle = "Messy Room vs Organized Shelf",
                    type = LessonType.NORMAL,
                    xOffsetDp = 0f,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "Why do we organize data in specialized structures instead of one giant pile?",
                            options = listOf(
                                "To make operations like searching, inserting, and deleting fast and predictable",
                                "Computers can only hold one number without them",
                                "Because programming languages forbid raw data",
                                "To increase power consumption"
                            ),
                            correctIndex = 0,
                            hint = "Think about finding your keys on a messy table vs in a labeled box.",
                            explanation = "A data structure is an organized format for storing, managing, and retrieving data efficiently.",
                            conceptId = "ds_definition"
                        ),
                        TrueFalseSwipeExercise(
                            prompt = "True or False?",
                            statement = "There is a single 'best' data structure that out-performs all others in every scenario.",
                            isTrue = false,
                            hint = "Different problems demand different trade-offs (e.g. fast read vs fast write).",
                            explanation = "Every data structure has trade-offs! For example, arrays offer O(1) random access but O(n) middle insertion.",
                            conceptId = "trade_offs"
                        ),
                        MatchPairsExercise(
                            prompt = "Match the real-world metaphor to its data structure concept:",
                            pairs = listOf(
                                "Numbered lockers in a row" to "Array with indexed access",
                                "A stack of cafeteria plates" to "LIFO Stack",
                                "Line of customers at coffee shop" to "FIFO Queue",
                                "Family genealogy tree" to "Hierarchical Tree"
                            ),
                            explanation = "Data structures map directly to real-world organization patterns!",
                            conceptId = "metaphors"
                        )
                    )
                ),
                DsaLesson(
                    id = "u1_l2", unitId = "unit_1_foundations", index = 2,
                    title = "Types of Data Structures",
                    subtitle = "Sort the Zoo",
                    type = LessonType.NORMAL,
                    xOffsetDp = -45f,
                    exercises = listOf(
                        SortIntoBucketsExercise(
                            prompt = "Classify these structures into Linear or Non-Linear:",
                            buckets = listOf("Linear (Sequential)", "Non-Linear (Multi-directional)"),
                            itemsWithBucket = listOf(
                                "Array" to 0,
                                "Binary Tree" to 1,
                                "Linked List" to 0,
                                "Graph" to 1,
                                "Stack" to 0,
                                "Trie" to 1
                            ),
                            explanation = "Linear structures arrange items in sequence (one predecessor, one successor). Non-linear structures branch into multiple paths.",
                            conceptId = "linear_vs_nonlinear"
                        ),
                        MultipleChoiceExercise(
                            prompt = "Which of the following is considered a Primitive data type?",
                            options = listOf("Int / Float", "Array", "HashMap", "Binary Search Tree"),
                            correctIndex = 0,
                            explanation = "Primitive types (int, float, char, boolean) store simple values directly in memory.",
                            conceptId = "primitive_types"
                        )
                    )
                ),
                DsaLesson(
                    id = "u1_l3", unitId = "unit_1_foundations", index = 3,
                    title = "Linear vs Non-Linear",
                    subtitle = "Path or Web",
                    type = LessonType.NORMAL,
                    xOffsetDp = 40f,
                    exercises = listOf(
                        TrueFalseSwipeExercise(
                            prompt = "True or False?",
                            statement = "In a linear structure, every element (except first and last) has exactly one previous and one next element.",
                            isTrue = true,
                            explanation = "Correct! Linear structures have strict sequential ordering.",
                            conceptId = "linear_ordering"
                        ),
                        MultipleChoiceExercise(
                            prompt = "Why do social networks like Twitter or Facebook use Graphs rather than Linear Arrays?",
                            options = listOf(
                                "Relationships are multi-way webs of friendships and follows, not a single line",
                                "Arrays cannot store strings",
                                "Graphs consume 0 bytes of memory",
                                "Phones cannot draw linear paths"
                            ),
                            correctIndex = 0,
                            explanation = "Social connections form arbitrary webs of vertices (users) and edges (friendships).",
                            conceptId = "graph_motivation"
                        )
                    )
                ),
                DsaLesson(
                    id = "u1_l4", unitId = "unit_1_foundations", index = 4,
                    title = "Static vs Dynamic",
                    subtitle = "Elastic Bus & Memory Resizing",
                    type = LessonType.CHECKPOINT,
                    xOffsetDp = -20f,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "What happens when a static array of size 5 receives a 6th element?",
                            options = listOf(
                                "Index Out of Bounds / Overflow error",
                                "It magically doubles automatically without cost",
                                "The computer turns off",
                                "The first element is permanently deleted"
                            ),
                            correctIndex = 0,
                            explanation = "Static arrays have a fixed memory block allocated at creation. Exceeding capacity causes an overflow!",
                            conceptId = "static_overflow"
                        ),
                        OrderStepsExercise(
                            prompt = "Order the steps a Dynamic Array takes when capacity is exceeded:",
                            steps = listOf(
                                "Allocate a new array with double the capacity (2 × n)",
                                "Copy all n elements from old array to new array",
                                "Add the new element into the new array",
                                "Free old array memory and update reference pointer"
                            ),
                            correctOrder = listOf(0, 1, 2, 3),
                            explanation = "Dynamic resizing is an O(n) operation when doubling occurs, but gives O(1) amortized insertion!",
                            conceptId = "dynamic_resizing"
                        )
                    )
                ),
                DsaLesson(
                    id = "u1_l5", unitId = "unit_1_foundations", index = 5,
                    title = "Time & Space Complexity",
                    subtitle = "Big-O Racetrack",
                    type = LessonType.NORMAL,
                    xOffsetDp = 35f,
                    exercises = listOf(
                        ComplexityDialExercise(
                            prompt = "Reading an element at array index arr[3]: What is the Big-O Time Complexity?",
                            codeSnippet = "val item = arr[3]",
                            correctComplexity = "O(1)",
                            explanation = "Array indexing uses direct arithmetic (base_address + index * item_size), taking constant O(1) time!",
                            conceptId = "complexity_indexing"
                        ),
                        ComplexityDialExercise(
                            prompt = "Searching for an element in an unsorted list of size n using a linear loop:",
                            codeSnippet = "for (x in list) { if (x == target) return true }",
                            correctComplexity = "O(n)",
                            explanation = "In the worst case, you must inspect all n elements, leading to O(n) linear time.",
                            conceptId = "complexity_linear"
                        ),
                        ComplexityDialExercise(
                            prompt = "Nested loop comparing every pair in an array:",
                            codeSnippet = "for (i in 0 until n) {\n  for (j in 0 until n) { compare(arr[i], arr[j]) }\n}",
                            correctComplexity = "O(n²)",
                            explanation = "Running an n-length loop inside another n-length loop results in n × n = O(n²) operations.",
                            conceptId = "complexity_quadratic"
                        )
                    )
                ),
                DsaLesson(
                    id = "u1_l6", unitId = "unit_1_foundations", index = 6,
                    title = "Foundations Boss Battle",
                    subtitle = "The Chaos Egg",
                    type = LessonType.BOSS,
                    xOffsetDp = 0f,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Rapid-Fire Chaos Egg Gauntlet: Answer correctly to defeat the Chaos Egg!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Complexity of accessing arr[0]?", options = listOf("O(1)", "O(n)", "O(log n)"), correctIndex = 0, explanation = "Instant access O(1)"),
                                MultipleChoiceExercise(prompt = "Is a Binary Tree linear or non-linear?", options = listOf("Non-linear", "Linear"), correctIndex = 0, explanation = "Trees branch in multiple directions"),
                                MultipleChoiceExercise(prompt = "Best data structure for LIFO (Last-In, First-Out)?", options = listOf("Stack", "Queue", "Array"), correctIndex = 0, explanation = "Stack is LIFO")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 2: Arrays — Locker Row Avenue                                */
    /* ------------------------------------------------------------------ */
    private fun buildUnit2(): DsaUnit {
        return DsaUnit(
            id = "unit_2_arrays",
            index = 2,
            title = "Arrays",
            subtitle = "Locker Row Avenue",
            difficulty = Difficulty.GREEN,
            worldTheme = "Corridor of Numbered Lockers",
            themeColorHex = 0xFFFF9600,
            themeDarkColorHex = 0xFFDC7800,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u2_mid", 4, "First Feathers gleam under structured memory!"),
                PhoenixTrigger("u2_boss", 5, "Ember Chick emerges after defeating Locker Thief!")
            ),
            boss = BossBattle(
                id = "boss_2_locker_thief",
                title = "The Locker Thief",
                subtitle = "Shifting Lockers Trial",
                bossName = "The Locker Thief",
                maxHp = 120,
                timeLimitSeconds = 60,
                rewardXp = 50
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u2_l1", unitId = "unit_2_arrays", index = 1,
                    title = "Array Basics",
                    subtitle = "Locker Row & Memory Addresses",
                    type = LessonType.NORMAL,
                    xOffsetDp = 0f,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "If an array starts at memory address 1000 and each integer takes 4 bytes, where is index 3 stored?",
                            options = listOf("1012", "1003", "1016", "1007"),
                            correctIndex = 0,
                            hint = "Formula: base + (index × size)",
                            explanation = "Address = 1000 + (3 × 4) = 1012! This direct calculation is why array lookup is always O(1).",
                            conceptId = "address_calculation"
                        ),
                        TrueFalseSwipeExercise(
                            prompt = "Index Boundaries",
                            statement = "For an array with size 7, accessing index 7 is valid and safe.",
                            isTrue = false,
                            explanation = "Arrays are 0-indexed! For size 7, valid indices are 0 to 6. Index 7 triggers ArrayIndexOutOfBoundsException!",
                            conceptId = "zero_indexing"
                        )
                    )
                ),
                DsaLesson(
                    id = "u2_l2", unitId = "unit_2_arrays", index = 2,
                    title = "Array Traversal",
                    subtitle = "Conveyor Sweep",
                    type = LessonType.NORMAL,
                    xOffsetDp = -40f,
                    exercises = listOf(
                        FillCodeExercise(
                            prompt = "Fill the missing tokens to traverse an array forward:",
                            codeWithBlanks = "for (i in 0 ___ arr.___) {\n    print(arr[___])\n}",
                            tokens = listOf("until", "size", "i", "downTo", "lastIndex"),
                            correctTokens = listOf("until", "size", "i"),
                            explanation = "`until` creates an exclusive upper bound from 0 up to size - 1.",
                            conceptId = "array_traversal"
                        )
                    )
                ),
                DsaLesson(
                    id = "u2_l3", unitId = "unit_2_arrays", index = 3,
                    title = "Insertion & Shift Party",
                    subtitle = "Slide Elements to the Right",
                    type = LessonType.NORMAL,
                    xOffsetDp = 45f,
                    exercises = listOf(
                        OperateVisualizerExercise(
                            prompt = "Insert 99 at index 1 in [10, 20, 30]. How many elements must shift to the right?",
                            structure = "array",
                            initialItems = listOf("10", "20", "30"),
                            targetOperation = "insert(1, 99)",
                            expectedStepIndices = listOf(2, 1),
                            hint = "Items at indices 1 and 2 must slide right to open a slot.",
                            explanation = "Elements at index 1 and 2 shift right by 1, costing O(n - k) operations.",
                            conceptId = "array_insertion"
                        )
                    )
                ),
                DsaLesson(
                    id = "u2_l4", unitId = "unit_2_arrays", index = 4,
                    title = "Deletion & Gap Closing",
                    subtitle = "Slide Elements to the Left",
                    type = LessonType.CHECKPOINT,
                    xOffsetDp = -25f,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "When deleting the first element (index 0) of an array with 1,000 items, how many items must shift?",
                            options = listOf("999 elements", "0 elements", "1,000 elements", "1 element"),
                            correctIndex = 0,
                            explanation = "All 999 remaining items must shift left by 1 to fill the hole at index 0, taking O(n) time.",
                            conceptId = "array_deletion"
                        )
                    )
                ),
                DsaLesson(
                    id = "u2_l5", unitId = "unit_2_arrays", index = 5,
                    title = "Searching: Linear vs Binary",
                    subtitle = "Hi-Lo Master",
                    type = LessonType.NORMAL,
                    xOffsetDp = 30f,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "What is the crucial prerequisite before you can use Binary Search on an array?",
                            options = listOf(
                                "The array must be sorted in order",
                                "The array must contain only positive integers",
                                "The array must have an odd number of items",
                                "The array must be dynamic"
                            ),
                            correctIndex = 0,
                            explanation = "Binary search relies on halving the search space based on whether target > mid. If unsorted, halving discards valid targets!",
                            conceptId = "binary_search_precondition"
                        ),
                        ComplexityDialExercise(
                            prompt = "Binary Search worst-case time complexity:",
                            correctComplexity = "O(log n)",
                            explanation = "Dividing the remaining items in half each step yields logarithmic O(log n) time.",
                            conceptId = "binary_search_complexity"
                        )
                    )
                ),
                DsaLesson(
                    id = "u2_l6", unitId = "unit_2_arrays", index = 6,
                    title = "Array Boss Battle",
                    subtitle = "The Locker Thief",
                    type = LessonType.BOSS,
                    xOffsetDp = 0f,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Outsmart the Locker Thief before time runs out!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Time to access arr[4]?", options = listOf("O(1)", "O(n)"), correctIndex = 0, explanation = "O(1)"),
                                MultipleChoiceExercise(prompt = "Time to insert at beginning of array?", options = listOf("O(n)", "O(1)"), correctIndex = 0, explanation = "Requires shifting all n items"),
                                MultipleChoiceExercise(prompt = "Can binary search run on unsorted array?", options = listOf("No", "Yes"), correctIndex = 0, explanation = "Must be sorted")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 3: Strings — The Scroll Scriptorium                          */
    /* ------------------------------------------------------------------ */
    private fun buildUnit3(): DsaUnit {
        return DsaUnit(
            id = "unit_3_strings",
            index = 3,
            title = "Strings",
            subtitle = "The Scroll Scriptorium",
            difficulty = Difficulty.GREEN,
            worldTheme = "Ancient Library of Floating Letter Tiles",
            themeColorHex = 0xFF1CB0F6,
            themeDarkColorHex = 0xFF1485BA,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u3_boss", 6, "Fluffy Flameling learns the power of words!")
            ),
            boss = BossBattle(
                id = "boss_3_scrambled_scroll",
                title = "The Scrambled Scroll",
                subtitle = "Anagram & Palindrome Gauntlet",
                bossName = "The Scriptorium Ghost",
                maxHp = 100,
                timeLimitSeconds = 50,
                rewardXp = 50
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u3_l1", unitId = "unit_3_strings", index = 1,
                    title = "String Basics & Immutability",
                    subtitle = "Letter Tiles",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        TrueFalseSwipeExercise(
                            prompt = "String Immutability in Java/Kotlin",
                            statement = "Modifying a string with `str += 'a'` mutates the existing string in-place in memory.",
                            isTrue = false,
                            explanation = "Strings are immutable! Concatenation allocates a completely brand-new string and copies the contents.",
                            conceptId = "string_immutability"
                        )
                    )
                ),
                DsaLesson(
                    id = "u3_l2", unitId = "unit_3_strings", index = 2,
                    title = "Palindrome: Two-Pointer Technique",
                    subtitle = "Mirror Chamber",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "How does the optimal Palindrome check work?",
                            options = listOf(
                                "Place two pointers (left at start, right at end) and compare inward",
                                "Reverse the string and check if length is even",
                                "Check if the first letter is 'A'",
                                "Count spaces"
                            ),
                            correctIndex = 0,
                            explanation = "Two pointers moving toward each other verify palindromes in O(n) time with O(1) extra space!",
                            conceptId = "two_pointers"
                        )
                    )
                ),
                DsaLesson(
                    id = "u3_l3", unitId = "unit_3_strings", index = 3,
                    title = "Strings Boss Battle",
                    subtitle = "The Scrambled Scroll",
                    type = LessonType.BOSS,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Decode the ancient scroll before it turns to ash!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Is 'racecar' a palindrome?", options = listOf("Yes", "No"), correctIndex = 0, explanation = "Reads same backwards and forwards"),
                                MultipleChoiceExercise(prompt = "Time complexity of checking palindrome with 2 pointers?", options = listOf("O(n)", "O(n²)"), correctIndex = 0, explanation = "Inspects each char once"),
                                MultipleChoiceExercise(prompt = "Are 'silent' and 'listen' anagrams?", options = listOf("Yes", "No"), correctIndex = 0, explanation = "Exact same character frequencies")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 4: Linked Lists — Chain Canyon                               */
    /* ------------------------------------------------------------------ */
    private fun buildUnit4(): DsaUnit {
        return DsaUnit(
            id = "unit_4_linked_lists",
            index = 4,
            title = "Linked Lists",
            subtitle = "Chain Canyon",
            difficulty = Difficulty.YELLOW,
            worldTheme = "Suspended Bridges of Coupled Train Cars",
            themeColorHex = 0xFFFFC800,
            themeDarkColorHex = 0xFFD4A600,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u4_mid", 7, "Spark Fledgling masters the art of pointer manipulation!"),
                PhoenixTrigger("u4_boss", 8, "Ash Wing emerges victorious from the runaway train!")
            ),
            boss = BossBattle(
                id = "boss_4_runaway_train",
                title = "The Runaway Train",
                subtitle = "Pointer Surgery Trial",
                bossName = "The Iron Conductor",
                maxHp = 130,
                timeLimitSeconds = 60,
                rewardXp = 50
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u4_l1", unitId = "unit_4_linked_lists", index = 1,
                    title = "Singly Linked List & Node Anatomy",
                    subtitle = "Coupled Train Cars",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "What two essential pieces of information does each Singly Linked List Node hold?",
                            options = listOf(
                                "Value (data) and a pointer to the Next node",
                                "Value and array index",
                                "Previous node and Next node only",
                                "Memory address of head and tail"
                            ),
                            correctIndex = 0,
                            explanation = "Each singly linked node holds its payload data and a `next` reference to the following node.",
                            conceptId = "node_anatomy"
                        )
                    )
                ),
                DsaLesson(
                    id = "u4_l2", unitId = "unit_4_linked_lists", index = 2,
                    title = "Pointer Surgery & Reversal",
                    subtitle = "Three-Pointer Dance",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        OrderStepsExercise(
                            prompt = "Order the steps of in-place linked list reversal (`prev`, `curr`, `next`):",
                            steps = listOf(
                                "Save next node: next = curr.next",
                                "Reverse pointer: curr.next = prev",
                                "Advance prev: prev = curr",
                                "Advance curr: curr = next"
                            ),
                            correctOrder = listOf(0, 1, 2, 3),
                            explanation = "The classic three-pointer dance reverses the list in O(n) time and O(1) space without losing references!",
                            conceptId = "reverse_linked_list"
                        )
                    )
                ),
                DsaLesson(
                    id = "u4_l3", unitId = "unit_4_linked_lists", index = 3,
                    title = "Linked List Boss Battle",
                    subtitle = "The Runaway Train",
                    type = LessonType.BOSS,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Couple the carriages before the bridge collapses!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Time to insert at head of Linked List?", options = listOf("O(1)", "O(n)"), correctIndex = 0, explanation = "Just rewire head pointer"),
                                MultipleChoiceExercise(prompt = "Does a Singly Linked List node have a `prev` pointer?", options = listOf("No", "Yes"), correctIndex = 0, explanation = "Only Doubly Linked Lists have `prev`"),
                                MultipleChoiceExercise(prompt = "What detects cycles in a Linked List?", options = listOf("Floyd's Tortoise and Hare", "Binary Search"), correctIndex = 0, explanation = "Slow and fast pointers")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 5: Stack — Pancake Tower Diner                               */
    /* ------------------------------------------------------------------ */
    private fun buildUnit5(): DsaUnit {
        return DsaUnit(
            id = "unit_5_stack",
            index = 5,
            title = "Stack",
            subtitle = "Pancake Tower Diner",
            difficulty = Difficulty.YELLOW,
            worldTheme = "Diner of Steaming High-Rise Pancakes",
            themeColorHex = 0xFFFF7043,
            themeDarkColorHex = 0xFFE64A19,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u5_boss", 9, "Cinder Glider masters Last-In First-Out mechanics!")
            ),
            boss = BossBattle(
                id = "boss_5_diner_rush",
                title = "Diner Rush",
                subtitle = "Rapid Push / Pop Challenge",
                bossName = "Chef Flapjack",
                maxHp = 120,
                timeLimitSeconds = 50,
                rewardXp = 50
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u5_l1", unitId = "unit_5_stack", index = 1,
                    title = "Stack Basics & LIFO",
                    subtitle = "Plates & Pancakes",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "What does LIFO stand for in Stack data structures?",
                            options = listOf(
                                "Last-In, First-Out",
                                "Linear-Index, Fast-Output",
                                "Lowest-Integer, First-Order",
                                "Long-Input, Fixed-Operation"
                            ),
                            correctIndex = 0,
                            explanation = "The last item added to the stack is the first one removed!",
                            conceptId = "lifo_principle"
                        )
                    )
                ),
                DsaLesson(
                    id = "u5_l2", unitId = "unit_5_stack", index = 2,
                    title = "Parentheses Matching",
                    subtitle = "Bracket Bouncer",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "When validating balanced brackets '({[]})', what do you do when an opening bracket is seen?",
                            options = listOf("Push it onto the stack", "Pop from the stack", "Clear the array", "Return false immediately"),
                            correctIndex = 0,
                            explanation = "Push opening brackets. When a closing bracket arrives, pop and verify it matches!",
                            conceptId = "valid_parentheses"
                        )
                    )
                ),
                DsaLesson(
                    id = "u5_l3", unitId = "unit_5_stack", index = 3,
                    title = "Stack Boss Battle",
                    subtitle = "Diner Rush",
                    type = LessonType.BOSS,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Serve orders with stack precision!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Push & Pop complexity?", options = listOf("O(1)", "O(n)"), correctIndex = 0, explanation = "Both take constant time"),
                                MultipleChoiceExercise(prompt = "Pop on empty stack causes?", options = listOf("Underflow error", "Overflow error"), correctIndex = 0, explanation = "Cannot pop empty stack"),
                                MultipleChoiceExercise(prompt = "Browser back button uses?", options = listOf("Stack", "Queue"), correctIndex = 0, explanation = "LIFO stack of visited URLs")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 6: Queue — Ember Café Rush                                   */
    /* ------------------------------------------------------------------ */
    private fun buildUnit6(): DsaUnit {
        return DsaUnit(
            id = "unit_6_queue",
            index = 6,
            title = "Queue",
            subtitle = "Ember Café Rush",
            difficulty = Difficulty.YELLOW,
            worldTheme = "Bustling Café with Orders Queue",
            themeColorHex = 0xFF58CC02,
            themeDarkColorHex = 0xFF46A302,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u6_boss", 10, "Ember Flyer conquers first-come first-served flow!")
            ),
            boss = BossBattle(
                id = "boss_6_cafe_stampede",
                title = "The Café Stampede",
                subtitle = "Circular Queue & Priority Triage",
                bossName = "Barista Blitz",
                maxHp = 130,
                timeLimitSeconds = 55,
                rewardXp = 50
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u6_l1", unitId = "unit_6_queue", index = 1,
                    title = "Queue Basics: FIFO",
                    subtitle = "Customers in Line",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "What does FIFO mean in Queue data structures?",
                            options = listOf("First-In, First-Out", "Fast-Index, First-Operation", "Fixed-Input, Fast-Output"),
                            correctIndex = 0,
                            explanation = "First-In, First-Out: Whoever arrives first is served first.",
                            conceptId = "fifo_principle"
                        )
                    )
                ),
                DsaLesson(
                    id = "u6_l2", unitId = "unit_6_queue", index = 2,
                    title = "Circular Queue & Deque",
                    subtitle = "Ring Buffer & Double-Ended Tunnel",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "Why use a Circular Queue instead of a simple linear array?",
                            options = listOf(
                                "To reuse empty slots at the front after dequeues using modulo arithmetic `(rear + 1) % size`",
                                "Because circles use 0 bytes of RAM",
                                "To store non-integer types"
                            ),
                            correctIndex = 0,
                            explanation = "A linear array wastes space as the front pointer advances. Circular buffers loop back to the start!",
                            conceptId = "circular_queue"
                        )
                    )
                ),
                DsaLesson(
                    id = "u6_l3", unitId = "unit_6_queue", index = 3,
                    title = "Queue Boss Battle",
                    subtitle = "The Café Stampede",
                    type = LessonType.BOSS,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Clear the customer rush before the café overheats!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Enqueue happens at which end?", options = listOf("Rear", "Front"), correctIndex = 0, explanation = "Items enter at the rear"),
                                MultipleChoiceExercise(prompt = "Dequeue happens at which end?", options = listOf("Front", "Rear"), correctIndex = 0, explanation = "Items leave from the front"),
                                MultipleChoiceExercise(prompt = "A Deque allows insertion at?", options = listOf("Both Front and Rear", "Only Rear"), correctIndex = 0, explanation = "Double-ended queue")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 7: Hashing — Hash Harbor                                     */
    /* ------------------------------------------------------------------ */
    private fun buildUnit7(): DsaUnit {
        return DsaUnit(
            id = "unit_7_hashing",
            index = 7,
            title = "Hashing",
            subtitle = "Hash Harbor",
            difficulty = Difficulty.YELLOW,
            worldTheme = "Port City of Magic Storage Vaults",
            themeColorHex = 0xFFCE82FF,
            themeDarkColorHex = 0xFFAA5ADC,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u7_mid", 11, "Flame Sprite unlocks O(1) hash maps!"),
                PhoenixTrigger("u7_boss", 12, "Blaze Wing weathers the collision storm!")
            ),
            boss = BossBattle(
                id = "boss_7_locker_storm",
                title = "The Locker Storm",
                subtitle = "Collision Resolution Arena",
                bossName = "The Collision Kraken",
                maxHp = 140,
                timeLimitSeconds = 60,
                rewardXp = 50
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u7_l1", unitId = "unit_7_hashing", index = 1,
                    title = "Hash Function & Bucket Arrays",
                    subtitle = "Magic Locker Assigner",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "What is the primary role of a Hash Function?",
                            options = listOf(
                                "Convert any key into an integer array index within bounds",
                                "Sort all elements alphabetically",
                                "Encrypt data so it cannot be read",
                                "Double the size of RAM"
                            ),
                            correctIndex = 0,
                            explanation = "Hash functions compute an integer index from keys for instant O(1) average lookup.",
                            conceptId = "hash_function"
                        )
                    )
                ),
                DsaLesson(
                    id = "u7_l2", unitId = "unit_7_hashing", index = 2,
                    title = "Collisions: Chaining vs Probing",
                    subtitle = "Two Guests, One Locker",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "How does Separate Chaining resolve hash collisions?",
                            options = listOf(
                                "Stores collided items in a linked list or bucket at that same index",
                                "Deletes the older item",
                                "Throws an exception",
                                "Shuts down the server"
                            ),
                            correctIndex = 0,
                            explanation = "Separate chaining attaches a linked list to each bucket. Linear probing instead finds the next empty slot.",
                            conceptId = "collision_resolution"
                        )
                    )
                ),
                DsaLesson(
                    id = "u7_l3", unitId = "unit_7_hashing", index = 3,
                    title = "Hashing Boss Battle",
                    subtitle = "The Locker Storm",
                    type = LessonType.BOSS,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Resolve the hash storm before buckets overflow!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Average lookup time in HashMap?", options = listOf("O(1)", "O(n)"), correctIndex = 0, explanation = "O(1) average time"),
                                MultipleChoiceExercise(prompt = "Worst-case lookup when all keys collide?", options = listOf("O(n)", "O(1)"), correctIndex = 0, explanation = "Degrades to linear search"),
                                MultipleChoiceExercise(prompt = "Can a HashSet contain duplicate values?", options = listOf("No", "Yes"), correctIndex = 0, explanation = "Sets enforce unique elements")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 8: Trees — Ember Forest                                      */
    /* ------------------------------------------------------------------ */
    private fun buildUnit8(): DsaUnit {
        return DsaUnit(
            id = "unit_8_trees",
            index = 8,
            title = "Trees",
            subtitle = "Ember Forest",
            difficulty = Difficulty.BLUE,
            worldTheme = "Canopy of Glowing Branching Trees",
            themeColorHex = 0xFF00CD9C,
            themeDarkColorHex = 0xFF00A57D,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u8_mid", 13, "Inferno Youngling learns recursive tree navigation!"),
                PhoenixTrigger("u8_boss", 14, "Sunfire Hawk restores balance to the twisted forest!")
            ),
            boss = BossBattle(
                id = "boss_8_twisted_oak",
                title = "The Twisted Oak",
                subtitle = "Rebalancing & Traversal Gauntlet",
                bossName = "The Twisted Ancient",
                maxHp = 150,
                timeLimitSeconds = 65,
                rewardXp = 50
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u8_l1", unitId = "unit_8_trees", index = 1,
                    title = "Binary Search Tree (BST) Properties",
                    subtitle = "Plinko Left or Right",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "In a Binary Search Tree (BST), where are smaller elements placed relative to any node N?",
                            options = listOf("Left Subtree", "Right Subtree", "At the root", "In the parent node"),
                            correctIndex = 0,
                            explanation = "BST rule: all elements in the left subtree < node, and all elements in the right subtree > node.",
                            conceptId = "bst_property"
                        )
                    )
                ),
                DsaLesson(
                    id = "u8_l2", unitId = "unit_8_trees", index = 2,
                    title = "Tree Traversals: Inorder, Preorder, Postorder",
                    subtitle = "Forest Tour",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "Which traversal produces elements of a BST in ascending sorted order?",
                            options = listOf("Inorder Traversal (Left, Root, Right)", "Preorder Traversal", "Postorder Traversal"),
                            correctIndex = 0,
                            explanation = "Inorder (Left -> Root -> Right) always yields sorted output for any valid BST!",
                            conceptId = "inorder_sorted"
                        )
                    )
                ),
                DsaLesson(
                    id = "u8_l3", unitId = "unit_8_trees", index = 3,
                    title = "Binary Heap: Sift-Up & Sift-Down",
                    subtitle = "Bubble-Up Bubbles",
                    type = LessonType.CHECKPOINT,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "In a Max-Heap, which element is guaranteed to sit at the root?",
                            options = listOf("The maximum element", "The minimum element", "The median element"),
                            correctIndex = 0,
                            explanation = "In a Max-Heap, every parent node is greater than or equal to its children, so the max is at the root.",
                            conceptId = "heap_property"
                        )
                    )
                ),
                DsaLesson(
                    id = "u8_l4", unitId = "unit_8_trees", index = 4,
                    title = "Trees Boss Battle",
                    subtitle = "The Twisted Oak",
                    type = LessonType.BOSS,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Rebalance the ancient tree before darkness spreads!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Lookup time in balanced BST?", options = listOf("O(log n)", "O(n)"), correctIndex = 0, explanation = "O(log n) height"),
                                MultipleChoiceExercise(prompt = "Worst-case BST lookup when skewed into a line?", options = listOf("O(n)", "O(1)"), correctIndex = 0, explanation = "Degenerates into linked list"),
                                MultipleChoiceExercise(prompt = "Can a binary tree node have 3 children?", options = listOf("No", "Yes"), correctIndex = 0, explanation = "Binary means max 2 children")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 9: Trie — Crystal Cave of Words                              */
    /* ------------------------------------------------------------------ */
    private fun buildUnit9(): DsaUnit {
        return DsaUnit(
            id = "unit_9_trie",
            index = 9,
            title = "Trie",
            subtitle = "Crystal Cave of Words",
            difficulty = Difficulty.BLUE,
            worldTheme = "Subterranean Cavern of Glowing Letter Crystals",
            themeColorHex = 0xFF1CB0F6,
            themeDarkColorHex = 0xFF1485BA,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u9_boss", 15, "Radiant Firebird masters prefix autocomplete!")
            ),
            boss = BossBattle(
                id = "boss_9_whispering_wall",
                title = "The Whispering Wall",
                subtitle = "Prefix & Autocomplete Arena",
                bossName = "The Word Golem",
                maxHp = 140,
                timeLimitSeconds = 55,
                rewardXp = 50
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u9_l1", unitId = "unit_9_trie", index = 1,
                    title = "Trie Prefix Tree Anatomy",
                    subtitle = "Crystal Branches",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "What is the time complexity to search for a word of length L in a Trie?",
                            options = listOf("O(L)", "O(N)", "O(L × N)", "O(log N)"),
                            correctIndex = 0,
                            explanation = "Searching in a Trie takes O(L) time depending only on the word's length, independent of total words stored!",
                            conceptId = "trie_complexity"
                        )
                    )
                ),
                DsaLesson(
                    id = "u9_l2", unitId = "unit_9_trie", index = 2,
                    title = "Trie Boss Battle",
                    subtitle = "The Whispering Wall",
                    type = LessonType.BOSS,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Complete the words demanded by the crystal wall!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Do words with shared prefixes share nodes in a Trie?", options = listOf("Yes", "No"), correctIndex = 0, explanation = "Common prefixes reuse existing paths"),
                                MultipleChoiceExercise(prompt = "Tries are ideally used for?", options = listOf("Autocomplete & Spell Check", "Sorting integers"), correctIndex = 0, explanation = "Prefix tree operations")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 10: Graphs — Sky Islands                                     */
    /* ------------------------------------------------------------------ */
    private fun buildUnit10(): DsaUnit {
        return DsaUnit(
            id = "unit_10_graphs",
            index = 10,
            title = "Graphs",
            subtitle = "Sky Islands",
            difficulty = Difficulty.BLUE,
            worldTheme = "Archipelago of Floating Floating Islands & Bridges",
            themeColorHex = 0xFF5B5EA6,
            themeDarkColorHex = 0xFF43457D,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u10_mid", 16, "Solar Phoenix masters BFS & DFS traversals!"),
                PhoenixTrigger("u10_boss", 17, "Eternal Ember charts all connected components!")
            ),
            boss = BossBattle(
                id = "boss_10_storm_archipelago",
                title = "The Storm Archipelago",
                subtitle = "Shortest Path & Maze Trial",
                bossName = "The Sky Titan",
                maxHp = 160,
                timeLimitSeconds = 70,
                rewardXp = 50
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u10_l1", unitId = "unit_10_graphs", index = 1,
                    title = "Graph Fundamentals & Representations",
                    subtitle = "Islands & Bridges",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "Which representation uses less memory for sparse graphs (few edges)?",
                            options = listOf("Adjacency List (O(V + E))", "Adjacency Matrix (O(V²))"),
                            correctIndex = 0,
                            explanation = "Adjacency lists only store edges that actually exist, saving huge amounts of memory on sparse graphs.",
                            conceptId = "adj_list_vs_matrix"
                        )
                    )
                ),
                DsaLesson(
                    id = "u10_l2", unitId = "unit_10_graphs", index = 2,
                    title = "Breadth-First Search (BFS)",
                    subtitle = "Ripple Flood",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "Which data structure is fundamental to implementing BFS?",
                            options = listOf("Queue (FIFO)", "Stack (LIFO)", "Array only", "Heap"),
                            correctIndex = 0,
                            explanation = "BFS explores layer-by-layer (radiating outward) using a Queue to track neighboring nodes.",
                            conceptId = "bfs_queue"
                        )
                    )
                ),
                DsaLesson(
                    id = "u10_l3", unitId = "unit_10_graphs", index = 3,
                    title = "Depth-First Search (DFS)",
                    subtitle = "Deep Diver",
                    type = LessonType.CHECKPOINT,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "DFS explores deeply along a branch before backtracking. Which mechanism powers this?",
                            options = listOf("Recursion or Call Stack (LIFO)", "Queue (FIFO)", "Circular buffer"),
                            correctIndex = 0,
                            explanation = "DFS dives down a path using the call stack or an explicit Stack, backtracking when dead ends are reached.",
                            conceptId = "dfs_stack"
                        )
                    )
                ),
                DsaLesson(
                    id = "u10_l4", unitId = "unit_10_graphs", index = 4,
                    title = "Graphs Boss Battle",
                    subtitle = "The Storm Archipelago",
                    type = LessonType.BOSS,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "Bridge the islands before lightning strikes!",
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "BFS finds shortest path on unweighted graphs?", options = listOf("Yes", "No"), correctIndex = 0, explanation = "BFS guarantees minimum edge distance"),
                                MultipleChoiceExercise(prompt = "In-degree in a directed graph is?", options = listOf("Number of incoming edges", "Number of outgoing edges"), correctIndex = 0, explanation = "Incoming edges into a vertex")
                            )
                        )
                    )
                )
            )
        )
    }

    /* ------------------------------------------------------------------ */
    /*  UNIT 11: Advanced Structures — The Sun Citadel                    */
    /* ------------------------------------------------------------------ */
    private fun buildUnit11(): DsaUnit {
        return DsaUnit(
            id = "unit_11_advanced",
            index = 11,
            title = "Advanced Structures",
            subtitle = "The Sun Citadel",
            difficulty = Difficulty.RED,
            worldTheme = "Solar Core of Cosmic Flame Architecture",
            themeColorHex = 0xFFA855F7,
            themeDarkColorHex = 0xFF7E22CE,
            phoenixEvolutionsAwarded = listOf(
                PhoenixTrigger("u11_boss", 18, "PHOENIX ASCENDANT: Celestial Transcendent Deity of the Stars!")
            ),
            boss = BossBattle(
                id = "boss_11_eternal_flame",
                title = "The Eternal Flame Guardian",
                subtitle = "Cosmic Final Exam",
                bossName = "The Solar Sovereign",
                maxHp = 200,
                timeLimitSeconds = 90,
                rewardXp = 100,
                phases = 4
            ),
            lessons = listOf(
                DsaLesson(
                    id = "u11_l1", unitId = "unit_11_advanced", index = 1,
                    title = "Disjoint Set (Union-Find)",
                    subtitle = "Kingdom Merge",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "What two optimizations give Union-Find its near-constant α(n) inverse Ackermann time?",
                            options = listOf(
                                "Path Compression and Union by Rank",
                                "Binary Search and Sorting",
                                "Hashing and Modulo"
                            ),
                            correctIndex = 0,
                            explanation = "Path compression flattens tree depth during find(), and Union by Rank attaches smaller trees to larger trees.",
                            conceptId = "union_find"
                        )
                    )
                ),
                DsaLesson(
                    id = "u11_l2", unitId = "unit_11_advanced", index = 2,
                    title = "Segment Tree & Fenwick Tree",
                    subtitle = "Range Query Towers",
                    type = LessonType.NORMAL,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "What makes a Segment Tree ideal for range queries?",
                            options = listOf(
                                "Both range queries and point updates run in O(log n) time",
                                "Queries take O(1) time and updates take O(n²)",
                                "It requires 0 additional memory"
                            ),
                            correctIndex = 0,
                            explanation = "Segment trees answer range sum/min/max queries in O(log n) while supporting O(log n) updates!",
                            conceptId = "segment_tree"
                        )
                    )
                ),
                DsaLesson(
                    id = "u11_l3", unitId = "unit_11_advanced", index = 3,
                    title = "Red-Black Trees & B-Trees",
                    subtitle = "Color Laws & Database Storage",
                    type = LessonType.CHECKPOINT,
                    exercises = listOf(
                        MultipleChoiceExercise(
                            prompt = "Why do relational databases (PostgreSQL, MySQL) use B-Trees for disk indexing instead of Binary Search Trees?",
                            options = listOf(
                                "High branching factor minimizes slow disk I/O reads by grouping keys into disk-block pages",
                                "B-Trees are strictly non-linear and use no memory",
                                "Computers cannot store BSTs on hard drives"
                            ),
                            correctIndex = 0,
                            explanation = "Each B-tree node holds hundreds of keys fitting a disk page, reducing tree height to 3-4 levels for fast lookups!",
                            conceptId = "b_tree_databases"
                        )
                    )
                ),
                DsaLesson(
                    id = "u11_l4", unitId = "unit_11_advanced", index = 4,
                    title = "Final Boss: The Eternal Flame Guardian",
                    subtitle = "Cosmic Master Battle",
                    type = LessonType.BOSS,
                    exercises = listOf(
                        SpeedRoundExercise(
                            prompt = "The Solar Sovereign tests your complete mastery of all Data Structures!",
                            durationSeconds = 60,
                            rapidQuestions = listOf(
                                MultipleChoiceExercise(prompt = "Array lookup time?", options = listOf("O(1)", "O(n)"), correctIndex = 0, explanation = "Constant time"),
                                MultipleChoiceExercise(prompt = "Stack principle?", options = listOf("LIFO", "FIFO"), correctIndex = 0, explanation = "Last In First Out"),
                                MultipleChoiceExercise(prompt = "Queue principle?", options = listOf("FIFO", "LIFO"), correctIndex = 0, explanation = "First In First Out"),
                                MultipleChoiceExercise(prompt = "Tree traversal yielding sorted BST output?", options = listOf("Inorder", "Preorder"), correctIndex = 0, explanation = "Inorder"),
                                MultipleChoiceExercise(prompt = "Union-Find with path compression complexity?", options = listOf("O(α(n))", "O(n²)"), correctIndex = 0, explanation = "Nearly O(1) inverse Ackermann"),
                                MultipleChoiceExercise(prompt = "Tree structure used in database storage engines?", options = listOf("B-Tree", "Binary Heap"), correctIndex = 0, explanation = "B-Tree disk pages")
                            )
                        )
                    )
                )
            )
        )
    }
}
