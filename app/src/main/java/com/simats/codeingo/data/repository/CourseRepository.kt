package com.simats.codeingo.data.repository

import com.simats.codeingo.data.model.DSABossSpec
import com.simats.codeingo.data.model.DSACourseLesson
import com.simats.codeingo.data.model.DSACourseUnit
import com.simats.codeingo.data.model.DSADifficulty
import com.simats.codeingo.data.model.DSAExerciseItem
import com.simats.codeingo.data.model.DSAExerciseType
import com.simats.codeingo.data.model.DSALessonType

object CourseRepository {

    val allUnits: List<DSACourseUnit> = listOf(
        // UNIT 1: Foundations
        DSACourseUnit(
            id = "unit_01_foundations",
            index = 1,
            title = "Unit 1: Foundations",
            difficulty = DSADifficulty.GREEN,
            worldTheme = "The Hatchery Warehouse",
            worldEmoji = "🥚",
            worldMetaphor = "The Phoenix organizes a chaotic warehouse of celestial eggs",
            lessons = listOf(
                DSACourseLesson(
                    id = "u1_l1_what_is_ds",
                    title = "What is a Data Structure?",
                    type = DSALessonType.NORMAL,
                    signatureActivity = "Messy Room Rescue",
                    signatureDescription = "Drag eggs from a chaotic pile onto organized labeled shelves to see retrieval time plummet from O(n) to O(1).",
                    concepts = listOf("data_organization", "retrieval_efficiency", "memory_layout"),
                    xpReward = 15,
                    exercises = listOf(
                        DSAExerciseItem(
                            id = "u1_l1_q1",
                            type = DSAExerciseType.TRUE_FALSE_SWIPE,
                            prompt = "Data structures are only about storing data, not about how fast you can access it.",
                            explanation = "False! Data structures dictate both memory organization AND algorithmic efficiency of operations.",
                            conceptId = "data_organization",
                            correctAnswers = listOf("False")
                        ),
                        DSAExerciseItem(
                            id = "u1_l1_q2",
                            type = DSAExerciseType.MULTIPLE_CHOICE,
                            prompt = "Why do we organize data on labeled shelves instead of in a random jumbled pile?",
                            explanation = "Organized data allows deterministic, predictable retrieval with minimum comparisons.",
                            conceptId = "retrieval_efficiency",
                            options = listOf("To use more memory", "To search in constant or logarithmic time", "Computers only read alphabetical order", "It looks prettier"),
                            correctIndex = 1
                        )
                    )
                ),
                DSACourseLesson(
                    id = "u1_l2_types_of_ds",
                    title = "Types of Data Structures",
                    type = DSALessonType.NORMAL,
                    signatureActivity = "Sort the Zoo",
                    signatureDescription = "Drag structure cards into a taxonomy tree (Primitive vs Non-Primitive, Linear vs Non-Linear).",
                    concepts = listOf("taxonomy", "primitive_vs_non_primitive", "linear_vs_non_linear"),
                    xpReward = 15,
                    exercises = listOf(
                        DSAExerciseItem(
                            id = "u1_l2_q1",
                            type = DSAExerciseType.SORT_INTO_BUCKETS,
                            prompt = "Classify into Linear vs Non-Linear data structures",
                            explanation = "Arrays and Stacks have sequential predecessor/successor relationships. Trees and Graphs have hierarchical or arbitrary networks.",
                            conceptId = "linear_vs_non_linear",
                            targetBucketA = listOf("Array", "Stack", "Queue", "Linked List"),
                            targetBucketB = listOf("Tree", "Graph", "Trie"),
                            bucketALabel = "Linear",
                            bucketBLabel = "Non-Linear"
                        )
                    )
                ),
                DSACourseLesson(
                    id = "u1_l3_linear_vs_nonlinear",
                    title = "Linear vs Non-Linear",
                    type = DSALessonType.NORMAL,
                    signatureActivity = "Path or Web",
                    signatureDescription = "Walk single-lane tracks versus branching multi-way webs to feel traversal constraints.",
                    concepts = listOf("single_path", "branching", "traversal_complexity"),
                    xpReward = 15,
                    exercises = listOf(
                        DSAExerciseItem(
                            id = "u1_l3_q1",
                            type = DSAExerciseType.MULTIPLE_CHOICE,
                            prompt = "In a linear structure, how many immediate successors does an interior element have?",
                            explanation = "Linear elements have exactly one immediate predecessor and one immediate successor.",
                            conceptId = "single_path",
                            options = listOf("Exactly 1", "0 or more", "Log n", "Up to 2"),
                            correctIndex = 0
                        )
                    )
                ),
                DSACourseLesson(
                    id = "u1_l4_static_vs_dynamic",
                    title = "Static vs Dynamic",
                    type = DSALessonType.NORMAL,
                    signatureActivity = "Memory Sizing",
                    signatureDescription = "Witness fixed array bounds overflow versus dynamic resizing linked allocations.",
                    concepts = listOf("static_allocation", "dynamic_resizing", "contiguous_memory"),
                    xpReward = 20
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_01",
                name = "The Great Jumble",
                title = "Chaos Golem of the Warehouse",
                worldTheme = "The Hatchery Warehouse",
                avatarEmoji = "🥚👹",
                bossHp = 100,
                timeLimitSeconds = 60,
                quote = "All your eggs will crack in my bottomless O(n) mire!",
                defeatQuote = "No! Your tidy O(1) structures have brought order to my chaos!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 3
            ),
            phoenixEvolutionsAwarded = listOf(2, 3),
            isUnlocked = true
        ),

        // UNIT 2: Arrays
        DSACourseUnit(
            id = "unit_02_arrays",
            index = 2,
            title = "Unit 2: Arrays",
            difficulty = DSADifficulty.GREEN,
            worldTheme = "Locker Row Avenue",
            worldEmoji = "🗄️",
            worldMetaphor = "Consecutive lockers along an infinite high-school corridor",
            lessons = listOf(
                DSACourseLesson(
                    id = "u2_l1_array_basics",
                    title = "Array Basics & Address Math",
                    signatureActivity = "Locker Row & Address Calculator",
                    signatureDescription = "Calculate base + index * size with interactive sliders; trigger the out-of-bounds buzzer!",
                    concepts = listOf("contiguous_memory", "zero_indexed", "address_formula"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u2_l2_traversal",
                    title = "Conveyor Sweep Traversal",
                    signatureActivity = "Conveyor Sweep",
                    signatureDescription = "Drag the i pointer forward, backward, and with step skips.",
                    concepts = listOf("iteration", "boundary_conditions", "step_size"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u2_l3_insertion",
                    title = "Shift Party (Insertion)",
                    signatureActivity = "Shift Party",
                    signatureDescription = "Slide elements right one by one; live counter shows total element shifts.",
                    concepts = listOf("array_insertion", "element_shifting", "worst_case_o_n"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u2_l4_deletion",
                    title = "Close the Gap (Deletion)",
                    signatureActivity = "Close the Gap",
                    signatureDescription = "Remove an element and shift remaining items left to prevent ghost-slot memory bugs.",
                    concepts = listOf("array_deletion", "left_shift", "size_decrement"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u2_l5_binary_search",
                    title = "Hi-Lo Master (Binary Search)",
                    signatureActivity = "Hi-Lo Master",
                    signatureDescription = "Race linear search vs binary search with low/mid/high pins.",
                    concepts = listOf("binary_search", "divide_and_conquer", "sorted_prerequisite"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u2_l6_2d_arrays",
                    title = "2D Arrays & Battleship Grid",
                    signatureActivity = "Battleship Grid",
                    signatureDescription = "Tap [row][col]; toggle row-major vs column-major order; solve spiral paths.",
                    concepts = listOf("matrix_indexing", "row_major", "spiral_traversal"),
                    xpReward = 25
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_02",
                name = "The Locker Thief",
                title = "Phantom of the Corridor",
                worldTheme = "Locker Row Avenue",
                avatarEmoji = "🦹‍♂️🗝️",
                bossHp = 120,
                timeLimitSeconds = 60,
                quote = "I've locked and shifted all the lockers! You'll never compute the right address in time!",
                defeatQuote = "Curse your O(1) direct address calculations! The chick breaks free!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 5
            ),
            phoenixEvolutionsAwarded = listOf(4, 5),
            isUnlocked = true
        ),

        // UNIT 3: Strings
        DSACourseUnit(
            id = "unit_03_strings",
            index = 3,
            title = "Unit 3: Strings",
            difficulty = DSADifficulty.GREEN,
            worldTheme = "The Scroll Scriptorium",
            worldEmoji = "📜",
            worldMetaphor = "Inscribing runes on enchanted parchment scrolls",
            lessons = listOf(
                DSACourseLesson(
                    id = "u3_l1_basics",
                    title = "String Basics & Immutability",
                    signatureActivity = "Immutability Demo",
                    signatureDescription = "Try to edit an inscribed letter tile; watch the spell force a whole new copy.",
                    concepts = listOf("char_array", "immutability", "unicode_ascii"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u3_l2_traversal",
                    title = "Highlighter Sweep",
                    signatureActivity = "Highlighter",
                    signatureDescription = "Sweep pointers across runes to tally vowels, consonants, and tokens.",
                    concepts = listOf("string_iteration", "frequency_counting"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u3_l3_word_forge",
                    title = "Word Forge Manipulation",
                    signatureActivity = "Word Forge",
                    signatureDescription = "Drag slice, concat, reverse, replace, split, and join runestones.",
                    concepts = listOf("substring", "concatenation", "string_builder"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u3_l4_palindrome",
                    title = "Mirror Chamber (Palindrome)",
                    signatureActivity = "Mirror Chamber",
                    signatureDescription = "Two pointer mirrors converge from left and right boundaries; spot mismatches.",
                    concepts = listOf("two_pointers", "palindrome_verification", "case_insensitivity"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u3_l5_anagrams",
                    title = "Letter Sieve (Anagrams)",
                    signatureActivity = "Letter Sieve",
                    signatureDescription = "Drop letters into 26 frequency bins; compare bar charts vs sorting strings.",
                    concepts = listOf("frequency_array", "anagram_detection", "hash_bucketing"),
                    xpReward = 25
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_03",
                name = "The Scrambled Scroll",
                title = "Sphinx of the Scriptorium",
                worldTheme = "The Scroll Scriptorium",
                avatarEmoji = "📜🌀",
                bossHp = 130,
                timeLimitSeconds = 60,
                quote = "My palindromes are inverted, my anagrams entangled! Restore my scrolls or be sealed in ink!",
                defeatQuote = "Your two-pointer precision has unscrambled the ancient runes! The Phoenix takes flight!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 6
            ),
            phoenixEvolutionsAwarded = listOf(6),
            isUnlocked = true
        ),

        // UNIT 4: Linked Lists
        DSACourseUnit(
            id = "unit_04_linked_lists",
            index = 4,
            title = "Unit 4: Linked Lists",
            difficulty = DSADifficulty.YELLOW,
            worldTheme = "Chain Canyon",
            worldEmoji = "⛓️",
            worldMetaphor = "Enchanted railway train cars linked by magnetic couplers across deep chasms",
            lessons = listOf(
                DSACourseLesson(
                    id = "u4_l1_singly",
                    title = "Train Builder (Singly Linked)",
                    signatureActivity = "Train Builder",
                    signatureDescription = "Couple train cars together; each car holds cargo (data) and a pointer to the next.",
                    concepts = listOf("node_structure", "head_pointer", "null_terminator"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u4_l2_pointer_surgery",
                    title = "Pointer Surgery & Rewire Order",
                    signatureActivity = "Rewire Order Puzzle",
                    signatureDescription = "Rewire head, middle, and tail pointers in exact order without losing reference.",
                    concepts = listOf("lost_reference_hazard", "pointer_assignment_order", "insertion"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u4_l3_deletion",
                    title = "Node Uncoupling & Garbage Collection",
                    signatureActivity = "Unhook the Car",
                    signatureDescription = "Bypass target node; watch the unhooked orphan car dissolve into dust.",
                    concepts = listOf("deletion_by_value", "memory_freeing", "head_deletion_edge_case"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u4_l4_reversal",
                    title = "Three-Pointer Dance (Reversal)",
                    signatureActivity = "Three-Pointer Dance",
                    signatureDescription = "Step prev, curr, and next through the chain; flip arrow directions.",
                    concepts = listOf("in_place_reversal", "three_pointers", "o_1_space"),
                    xpReward = 30
                ),
                DSACourseLesson(
                    id = "u4_l5_doubly_circular",
                    title = "Doubly & Circular Lists (Carousel)",
                    signatureActivity = "Carousel & Tortoise-Hare",
                    signatureDescription = "Link tail back to head; race slow and fast pointers to detect infinite loops.",
                    concepts = listOf("doubly_linked", "circular_reference", "floyd_cycle_finding"),
                    xpReward = 30
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_04",
                name = "The Runaway Train",
                title = "Derailer of the Canyon",
                worldTheme = "Chain Canyon",
                avatarEmoji = "🚂💥",
                bossHp = 150,
                timeLimitSeconds = 70,
                quote = "The bridge is severed! Rewire the couplers before the runaway express plunges into the abyss!",
                defeatQuote = "Magnificent pointer surgery! The runaway train is secured and glides across the valley!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 8
            ),
            phoenixEvolutionsAwarded = listOf(7, 8),
            isUnlocked = true
        ),

        // UNIT 5: Stack
        DSACourseUnit(
            id = "unit_05_stack",
            index = 5,
            title = "Unit 5: Stack",
            difficulty = DSADifficulty.YELLOW,
            worldTheme = "Pancake Tower Diner",
            worldEmoji = "🥞",
            worldMetaphor = "Pancakes stacking up high on a griddle where only the top pancake is accessible",
            lessons = listOf(
                DSACourseLesson(
                    id = "u5_l1_basics",
                    title = "Stack Basics & LIFO",
                    signatureActivity = "Pancake Stacker",
                    signatureDescription = "Tap PUSH to drop pancakes and POP to serve the top one.",
                    concepts = listOf("lifo", "push_pop_peek", "top_pointer"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u5_l2_implementations",
                    title = "Array vs Linked List Stack",
                    signatureActivity = "Memory Model Compare",
                    signatureDescription = "Inspect top index pointer in array vs prepending to a linked list.",
                    concepts = listOf("array_backed_stack", "linked_list_stack", "capacity_overflow"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u5_l3_bracket_bouncer",
                    title = "Parentheses Matching",
                    signatureActivity = "Bracket Bouncer",
                    signatureDescription = "A stream of (), {}, and [] arrives; push openers and pop closers.",
                    concepts = listOf("balanced_brackets", "mismatch_detection", "empty_stack_check"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u5_l4_expression_eval",
                    title = "Railway Yard & Postfix Calc",
                    signatureActivity = "Railway Yard (Shunting Yard)",
                    signatureDescription = "Infix to postfix with switching tracks; compute postfix results on a griddle calculator.",
                    concepts = listOf("shunting_yard", "operator_precedence", "postfix_evaluation"),
                    xpReward = 30
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_05",
                name = "Diner Rush",
                title = "Chef Chaos of the Diner",
                worldTheme = "Pancake Tower Diner",
                avatarEmoji = "👨‍🍳🥞",
                bossHp = 160,
                timeLimitSeconds = 60,
                quote = "Orders are piling up high! One wrong POP and the whole stack topples over the counter!",
                defeatQuote = "Every order served in flawless LIFO order! The Phoenix endures the storm trial!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 9
            ),
            phoenixEvolutionsAwarded = listOf(9),
            isUnlocked = true
        ),

        // UNIT 6: Queue
        DSACourseUnit(
            id = "unit_06_queue",
            index = 6,
            title = "Unit 6: Queue",
            difficulty = DSADifficulty.YELLOW,
            worldTheme = "Ember Café Rush",
            worldEmoji = "☕️",
            worldMetaphor = "Customers ordering celestial ember brew in a strict First-Come, First-Served line",
            lessons = listOf(
                DSACourseLesson(
                    id = "u6_l1_basics",
                    title = "Queue Basics & FIFO",
                    signatureActivity = "Serve the Line",
                    signatureDescription = "Enqueue new patrons at rear and dequeue orders at front.",
                    concepts = listOf("fifo", "enqueue_dequeue", "front_rear_pointers"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u6_l2_circular_queue",
                    title = "Circular Queue Ring",
                    signatureActivity = "Ring Buffer Visualizer",
                    signatureDescription = "Wrap rear around via (rear + 1) % size; solve wasted front space.",
                    concepts = listOf("modulo_arithmetic", "ring_buffer", "full_vs_empty_condition"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u6_l3_deque",
                    title = "Deque (Double-Ended Queue)",
                    signatureActivity = "Double-Ended Tunnel",
                    signatureDescription = "Add and remove patrons from both ends; conquer sliding window max.",
                    concepts = listOf("deque", "sliding_window", "both_ends_o_1"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u6_l4_priority_queue",
                    title = "Priority Queue (ER Triage)",
                    signatureActivity = "ER Triage Simulation",
                    signatureDescription = "Higher priority emergencies jump to the head of treatment queue.",
                    concepts = listOf("priority_ordering", "comparator", "heap_backed_queue"),
                    xpReward = 30
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_06",
                name = "The Café Stampede",
                title = "Barista of the Tempest",
                worldTheme = "Ember Café Rush",
                avatarEmoji = "☕️⚡️",
                bossHp = 160,
                timeLimitSeconds = 60,
                quote = "A horde of impatient frost elementals wants coffee NOW! Keep the queue flowing or freeze!",
                defeatQuote = "Order maintained without a single collision! The Phoenix strikes through tempest storms!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 10
            ),
            phoenixEvolutionsAwarded = listOf(10),
            isUnlocked = true
        ),

        // UNIT 7: Hashing
        DSACourseUnit(
            id = "unit_07_hashing",
            index = 7,
            title = "Unit 7: Hashing",
            difficulty = DSADifficulty.YELLOW,
            worldTheme = "Hash Harbor",
            worldEmoji = "⚓️",
            worldMetaphor = "A magical automated maritime harbor routing cargo into numbered storage bays",
            lessons = listOf(
                DSACourseLesson(
                    id = "u7_l1_basics",
                    title = "Hashing Basics & Magic Lockers",
                    signatureActivity = "Magic Locker Assigner",
                    signatureDescription = "Input cargo key; compute instant bucket index without searching.",
                    concepts = listOf("hash_table", "key_value_mapping", "average_o_1"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u7_l2_hash_functions",
                    title = "Craft Your Hash Function",
                    signatureActivity = "Craft Your Hash",
                    signatureDescription = "Combine character codes % tableSize; test distribution histograms.",
                    concepts = listOf("uniform_distribution", "modulo_compression", "avalanche_effect"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u7_l3_hashmap_set",
                    title = "HashMap & HashSet (Club Bouncer)",
                    signatureActivity = "Club Bouncer",
                    signatureDescription = "Bouncer rejects duplicate keys instantly; map customer names to VIP passes.",
                    concepts = listOf("hash_set", "hash_map", "uniqueness_enforcement"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u7_l4_collisions",
                    title = "Collision Resolution: Chaining vs Probing",
                    signatureActivity = "Locker Storm & Rehashing",
                    signatureDescription = "Two ships assigned to bay 4! Compare chaining vs linear probing vs table resize.",
                    concepts = listOf("chaining", "open_addressing", "load_factor", "rehashing"),
                    xpReward = 30
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_07",
                name = "The Locker Storm",
                title = "Harbor Master of the Whirlpool",
                worldTheme = "Hash Harbor",
                avatarEmoji = "⚓️🌪️",
                bossHp = 180,
                timeLimitSeconds = 65,
                quote = "A rogue gale has blown identical hash codes into bay 7! Resolve my collisions before the docks flood!",
                defeatQuote = "Every collision chained and probed with perfection! The Phoenix rises anew from sacred ash!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 12
            ),
            phoenixEvolutionsAwarded = listOf(11, 12),
            isUnlocked = true
        ),

        // UNIT 8: Trees
        DSACourseUnit(
            id = "unit_08_trees",
            index = 8,
            title = "Unit 8: Trees",
            difficulty = DSADifficulty.BLUE,
            worldTheme = "Ember Forest",
            worldEmoji = "🌲",
            worldMetaphor = "Bioluminescent ancient forest canopy branching upward toward the skies",
            lessons = listOf(
                DSACourseLesson(
                    id = "u8_l1_basics",
                    title = "Tree Anatomy & Terminology",
                    signatureActivity = "Glowing Tree Labeler",
                    signatureDescription = "Tap to identify root, parent, child, leaf, height, depth, and subtrees.",
                    concepts = listOf("hierarchical_structure", "root_and_leaves", "tree_height"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u8_l2_traversals_dfs",
                    title = "Forest Tour: Pre/In/Post Order",
                    signatureActivity = "Forest Tour (Phoenix Flight)",
                    signatureDescription = "The Phoenix flies through branches; predict visiting order with interactive call-stack.",
                    concepts = listOf("preorder", "inorder_sorted", "postorder_eval"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u8_l3_traversals_bfs",
                    title = "Level-Order Traversal (BFS Wave)",
                    signatureActivity = "Wave-by-Wave Glow",
                    signatureDescription = "Send glowing pulse waves tier by tier across the canopy using a queue panel.",
                    concepts = listOf("breadth_first", "queue_traversal", "level_grouping"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u8_l4_bst",
                    title = "Binary Search Tree (Plinko Insert)",
                    signatureActivity = "Plinko Insert & Node Removal",
                    signatureDescription = "Drop numbers from root: smaller goes left, larger right; solve deletion puzzles.",
                    concepts = listOf("bst_property", "inorder_successor", "search_efficiency_o_h"),
                    xpReward = 30
                ),
                DSACourseLesson(
                    id = "u8_l5_avl_rotations",
                    title = "Balance Doctor (AVL Rotations)",
                    signatureActivity = "Balance Doctor",
                    signatureDescription = "Calculate balance factors (-2, +2); perform LL, RR, LR, and RL rotations.",
                    concepts = listOf("avl_tree", "balance_factor", "single_double_rotations"),
                    xpReward = 35
                ),
                DSACourseLesson(
                    id = "u8_l6_heap",
                    title = "Binary Heap (Bubble-up Bubbles)",
                    signatureActivity = "Bubble-up Bubbles",
                    signatureDescription = "Dual tree and array view; sift-up on insert, sift-down on extractMax.",
                    concepts = listOf("complete_binary_tree", "array_representation", "sift_up_down"),
                    xpReward = 35
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_08",
                name = "The Twisted Oak",
                title = "Overgrown Warden of the Forest",
                worldTheme = "Ember Forest",
                avatarEmoji = "🌲⚡️",
                bossHp = 200,
                timeLimitSeconds = 75,
                quote = "My branches are skewed into a degenerate O(n) linked list! Rebalance me if your rotations dare!",
                defeatQuote = "Perfect AVL balance restored! A golden coronal crest bursts upon the Phoenix!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 14
            ),
            phoenixEvolutionsAwarded = listOf(13, 14),
            isUnlocked = true
        ),

        // UNIT 9: Trie
        DSACourseUnit(
            id = "unit_09_trie",
            index = 9,
            title = "Unit 9: Trie",
            difficulty = DSADifficulty.BLUE,
            worldTheme = "Crystal Cave of Words",
            worldEmoji = "💎",
            worldMetaphor = "Subterranean cavern where words crystallize along shared prefix branches",
            lessons = listOf(
                DSACourseLesson(
                    id = "u9_l1_basics",
                    title = "Trie Anatomy & Prefix Sharing",
                    signatureActivity = "Crystal Growth",
                    signatureDescription = "Spell words by tapping gems; see shared prefixes reuse existing crystals.",
                    concepts = listOf("trie_node", "is_end_of_word", "shared_prefixes"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u9_l2_insert_search",
                    title = "Insert & Search Crystals",
                    signatureActivity = "Trace the Cave",
                    signatureDescription = "Trace words down cavern; distinguish prefix existence from complete word.",
                    concepts = listOf("exact_search", "prefix_search", "o_l_complexity"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u9_l3_autocomplete",
                    title = "Live Autocomplete Keyboard",
                    signatureActivity = "Mini Keyboard Strip",
                    signatureDescription = "Type prefix letters; watch candidate words light up sorted by frequency.",
                    concepts = listOf("dfs_prefix_collection", "autocomplete_ranking"),
                    xpReward = 30
                ),
                DSACourseLesson(
                    id = "u9_l4_delete",
                    title = "Safe Node Pruning",
                    signatureActivity = "Crystal Pruning",
                    signatureDescription = "Unmark end-of-word markers and safely prune leaf nodes with no children.",
                    concepts = listOf("trie_deletion", "backtracking_clean_up"),
                    xpReward = 30
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_09",
                name = "The Whispering Wall",
                title = "Oracle of the Crystal Chasm",
                worldTheme = "Crystal Cave of Words",
                avatarEmoji = "💎👁️",
                bossHp = 190,
                timeLimitSeconds = 65,
                quote = "Only words inscribed with pure prefix harmony can unlock my crystal gates!",
                defeatQuote = "Every rune authenticated! The Phoenix commands all 4 primal elements!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 15
            ),
            phoenixEvolutionsAwarded = listOf(15),
            isUnlocked = true
        ),

        // UNIT 10: Graphs
        DSACourseUnit(
            id = "unit_10_graphs",
            index = 10,
            title = "Unit 10: Graphs",
            difficulty = DSADifficulty.BLUE,
            worldTheme = "Sky Islands",
            worldEmoji = "🏝️",
            worldMetaphor = "Floating floating archipelagos connected by rope bridges, portals, and energy beams",
            lessons = listOf(
                DSACourseLesson(
                    id = "u10_l1_basics",
                    title = "Graph Basics: Vertices & Edges",
                    signatureActivity = "Bridge Builder",
                    signatureDescription = "Drag bridges between floating islands; tally vertex degrees.",
                    concepts = listOf("vertices_and_edges", "degree", "connectivity"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u10_l2_directed_weighted",
                    title = "Directed & Weighted Bridges",
                    signatureActivity = "Toll Bridge Navigator",
                    signatureDescription = "Traverse one-way bridges and calculate optimal toll costs.",
                    concepts = listOf("directed_acyclic", "edge_weights", "in_out_degree"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u10_l3_matrix_vs_list",
                    title = "Adjacency Matrix vs List",
                    signatureActivity = "Memory Blueprint",
                    signatureDescription = "Fill V x V grid from graph diagram; compare memory footprint with sparse lists.",
                    concepts = listOf("adjacency_matrix_o_v2", "adjacency_list_o_v_e", "space_tradeoff"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u10_l4_bfs",
                    title = "BFS: Ripple Flood Traversal",
                    signatureActivity = "Ripple Flood",
                    signatureDescription = "Expand radial ripple waves from start island using a queue to find shortest paths.",
                    concepts = listOf("bfs_queue", "shortest_unweighted_path", "visited_set"),
                    xpReward = 30
                ),
                DSACourseLesson(
                    id = "u10_l5_dfs",
                    title = "DFS: Deep Diver Traversal",
                    signatureActivity = "Deep Diver Maze",
                    signatureDescription = "Explorer dives down a chain with an unraveling rope; backtrack upon dead-ends.",
                    concepts = listOf("dfs_recursion", "backtracking", "cycle_detection"),
                    xpReward = 35
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_10",
                name = "The Storm Archipelago",
                title = "Lord of the Shattered Isles",
                worldTheme = "Sky Islands",
                avatarEmoji = "🏝️⚡️",
                bossHp = 220,
                timeLimitSeconds = 75,
                quote = "Bridges are collapsing in the hurricane! Find the route before all islands are cut off forever!",
                defeatQuote = "Unshakable graph mastery! The Phoenix dives into the solar core for Solar Ascent!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 17
            ),
            phoenixEvolutionsAwarded = listOf(16, 17),
            isUnlocked = true
        ),

        // UNIT 11: Advanced Structures
        DSACourseUnit(
            id = "unit_11_advanced",
            index = 11,
            title = "Unit 11: Advanced Structures",
            difficulty = DSADifficulty.RED,
            worldTheme = "The Sun Citadel",
            worldEmoji = "☀️",
            worldMetaphor = "The celestial solar sanctuary housing ultimate algorithmic masteries",
            lessons = listOf(
                DSACourseLesson(
                    id = "u11_l1_union_find",
                    title = "Union-Find: Kingdom Merge",
                    signatureActivity = "Kingdom Merge",
                    signatureDescription = "Merge kingdoms with union by rank; flatten branches with path compression.",
                    concepts = listOf("disjoint_set_union", "path_compression", "union_by_rank", "inverse_ackermann"),
                    xpReward = 30
                ),
                DSACourseLesson(
                    id = "u11_l2_segment_tree",
                    title = "Segment Tree: Range Query Tower",
                    signatureActivity = "Range Query Tower",
                    signatureDescription = "Select range [L, R]; tree highlights combined interval nodes.",
                    concepts = listOf("range_sum_min_max", "segment_tree_o_log_n", "point_update"),
                    xpReward = 35
                ),
                DSACourseLesson(
                    id = "u11_l3_fenwick_tree",
                    title = "Fenwick Tree (Lowbit Ladder)",
                    signatureActivity = "Lowbit Ladder",
                    signatureDescription = "Climb binary index steps with i & (-i) jumps; compute prefix sums.",
                    concepts = listOf("binary_indexed_tree", "lowbit_mask", "prefix_sums"),
                    xpReward = 35
                ),
                DSACourseLesson(
                    id = "u11_l4_red_black_tree",
                    title = "Red-Black Tree: Color Court",
                    signatureActivity = "Color Court",
                    signatureDescription = "Uphold sacred laws: root is black, no consecutive red nodes, equal black-height.",
                    concepts = listOf("red_black_properties", "color_flip", "rotation_fixup"),
                    xpReward = 40
                ),
                DSACourseLesson(
                    id = "u11_l5_b_tree",
                    title = "B-Tree: Library Shelving",
                    signatureActivity = "Library Disk Pages",
                    signatureDescription = "Multi-key node pages split upon saturation; see why databases rely on B-Trees.",
                    concepts = listOf("disk_io_optimization", "multiway_search", "node_splitting"),
                    xpReward = 40
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_11",
                name = "The Eternal Flame Guardian",
                title = "Primal Celestial Sovereign",
                worldTheme = "The Sun Citadel",
                avatarEmoji = "☀️🔥👑",
                bossHp = 300,
                timeLimitSeconds = 90,
                quote = "You have traversed all 11 worlds of data structures. Face my ultimate gauntlet to claim the Ascendant Flame!",
                defeatQuote = "LEGENDARY! You have conquered the complete spectrum of Data Structures! The Phoenix reaches PHOENIX ASCENDANT!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 18
            ),
            phoenixEvolutionsAwarded = listOf(18),
            isUnlocked = true
        )
    )

    fun getUnitById(id: String): DSACourseUnit? = allUnits.firstOrNull { it.id == id }

    fun getLessonById(id: String): DSACourseLesson? {
        for (unit in allUnits) {
            val lesson = unit.lessons.firstOrNull { it.id == id }
            if (lesson != null) return lesson
        }
        return null
    }

    fun getBossById(id: String): DSABossSpec? {
        return allUnits.firstOrNull { it.boss.id == id }?.boss
    }
}
