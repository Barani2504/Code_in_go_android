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
            title = "Unit 1: Classical Ciphers & Foundations",
            difficulty = DSADifficulty.GREEN,
            worldTheme = "The Cipher Sanctum",
            worldEmoji = "🔐",
            worldMetaphor = "The Phoenix decodes scrambled cryptograms using shift keys and transposition matrices",
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
                name = "The Cipher Breaker",
                title = "Cryptanalysis Chaos Golem",
                worldTheme = "The Cipher Sanctum",
                avatarEmoji = "🔐👹",
                bossHp = 100,
                timeLimitSeconds = 60,
                quote = "All your classical ciphers will shatter under my frequency analysis!",
                defeatQuote = "Impossible! Your layered substitution and transposition defied all cryptanalysis!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 3
            ),
            phoenixEvolutionsAwarded = listOf(2, 3),
            isUnlocked = true
        ),

        // UNIT 2: Symmetric Encryption
        DSACourseUnit(
            id = "unit_02_arrays",
            index = 2,
            title = "Unit 2: Symmetric Encryption & Block Ciphers",
            difficulty = DSADifficulty.GREEN,
            worldTheme = "The Block Fortress",
            worldEmoji = "🏰",
            worldMetaphor = "The Phoenix aligns 128-bit state matrices and passes blocks through 16-round Feistel gates",
            lessons = listOf(
                DSACourseLesson(
                    id = "u2_l1_array_basics",
                    title = "DES & Feistel Networks",
                    signatureActivity = "Feistel Split & XOR",
                    signatureDescription = "Split 64-bit blocks into L and R halves; apply round function F and key mixing across 16 rounds.",
                    concepts = listOf("feistel_network", "des_16_rounds", "confusion_diffusion"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u2_l2_traversal",
                    title = "AES Architecture & State Matrix",
                    signatureActivity = "4x4 State Matrix Transformation",
                    signatureDescription = "Execute SubBytes S-Box lookup, ShiftRows cycloid shift, and MixColumns Galois field matrix multiplication.",
                    concepts = listOf("rijndael_sbox", "shift_rows", "mix_columns", "galois_fields"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u2_l3_insertion",
                    title = "Block Cipher Modes: ECB vs CBC",
                    signatureActivity = "Ciphertext Chaining Track",
                    signatureDescription = "Chain blocks using random Initialization Vectors (IV) to destroy plaintext repetition patterns.",
                    concepts = listOf("ecb_pattern_leak", "cbc_chaining", "initialization_vectors"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u2_l4_deletion",
                    title = "Counter Mode & Stream Encryption",
                    signatureActivity = "CTR Stream Generator",
                    signatureDescription = "Encrypt continuous incrementing nonce counters to enable random-access parallel decryption.",
                    concepts = listOf("ctr_mode", "parallel_encryption", "stream_cipher"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u2_l5_binary_search",
                    title = "Authenticated Encryption (AES-GCM)",
                    signatureActivity = "Galois Field Authenticator",
                    signatureDescription = "Compute GHASH authentication tags alongside CTR encryption to defeat chosen-ciphertext tampering.",
                    concepts = listOf("aead", "gcm_ghash", "integrity_tag"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u2_l6_2d_arrays",
                    title = "Key Scheduling & Exhaustive Search",
                    signatureActivity = "Key Expansion Matrix",
                    signatureDescription = "Derive round subkeys with Rijndael key expansion and evaluate brute force security over 128/256 bits.",
                    concepts = listOf("key_schedule", "rcon_constants", "brute_force_keyspace"),
                    xpReward = 25
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_02",
                name = "The Block Overlord",
                title = "Master of Feistel & S-Boxes",
                worldTheme = "The Block Fortress",
                avatarEmoji = "🏰🛡️",
                bossHp = 120,
                timeLimitSeconds = 60,
                quote = "My 16-round Feistel network and Galois field S-Boxes will scatter your plaintext into unrecognizable noise!",
                defeatQuote = "Your round-key scheduling and differential cryptanalysis broke through my cipher shields! The Phoenix ascends!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 5
            ),
            phoenixEvolutionsAwarded = listOf(4, 5),
            isUnlocked = true
        ),

        // UNIT 3: Asymmetric Cryptography
        DSACourseUnit(
            id = "unit_03_strings",
            index = 3,
            title = "Unit 3: Asymmetric Cryptography & Trapdoors",
            difficulty = DSADifficulty.GREEN,
            worldTheme = "The Asymmetric Spire",
            worldEmoji = "🔑",
            worldMetaphor = "The Phoenix traverses one-way modular trapdoors and climbs elliptic curve generator points",
            lessons = listOf(
                DSACourseLesson(
                    id = "u3_l1_basics",
                    title = "Modular Math & Euler's Totient",
                    signatureActivity = "Modular Clock Ring",
                    signatureDescription = "Wrap large exponents around modular clock rings using Euler's Totient φ(n) = (p-1)(q-1).",
                    concepts = listOf("modular_arithmetic", "eulers_totient", "fermats_little_theorem"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u3_l2_traversal",
                    title = "Diffie-Hellman Key Exchange",
                    signatureActivity = "Color Mixing & Shared Secret",
                    signatureDescription = "Exchange public components g^a and g^b to compute shared secret g^(ab) mod p over hostile channels.",
                    concepts = listOf("diffie_hellman", "discrete_logarithm", "shared_secret"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u3_l3_word_forge",
                    title = "RSA Public Key Encryption",
                    signatureActivity = "Prime Factor Trapdoor Forge",
                    signatureDescription = "Multiply huge primes p and q to forge public exponent e and private decryption trapdoor d.",
                    concepts = listOf("rsa_trapdoor", "prime_factorization", "extended_euclidean"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u3_l4_palindrome",
                    title = "Elliptic Curve Cryptography",
                    signatureActivity = "Point Doubling & Addition Graph",
                    signatureDescription = "Trace tangent lines on curve y² = x³ + ax + b to execute fast scalar point multiplication Q = d * G.",
                    concepts = listOf("elliptic_curves", "secp256k1", "ecdlp"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u3_l5_anagrams",
                    title = "Post-Quantum Cryptography",
                    signatureActivity = "Lattice Vector Sieve",
                    signatureDescription = "Traverse high-dimensional lattices (ML-KEM / Kyber) immune to Shor's quantum factorization.",
                    concepts = listOf("post_quantum", "shors_algorithm", "lattice_cryptography"),
                    xpReward = 25
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_03",
                name = "The Quantum Cryptanalyst",
                title = "Shor's Algorithm Spectre",
                worldTheme = "The Asymmetric Spire",
                avatarEmoji = "🔑⚡",
                bossHp = 130,
                timeLimitSeconds = 60,
                quote = "I wield Shor's quantum factorization! All your prime trapdoors and discrete logs will collapse!",
                defeatQuote = "Impossible! Your post-quantum lattice defenses and elliptic curve point multipliers held strong! The Phoenix takes flight!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 6
            ),
            phoenixEvolutionsAwarded = listOf(6),
            isUnlocked = true
        ),

        // UNIT 4: Hashes & Signatures
        DSACourseUnit(
            id = "unit_04_linked_lists",
            index = 4,
            title = "Unit 4: Cryptographic Hashes & Signatures",
            difficulty = DSADifficulty.YELLOW,
            worldTheme = "The Signature Bastion",
            worldEmoji = "🛡️",
            worldMetaphor = "The Phoenix seals message digests into Merkle trees and verifies X.509 certificate chains",
            lessons = listOf(
                DSACourseLesson(
                    id = "u4_l1_singly",
                    title = "Hash Functions & Pre-image Resistance",
                    signatureActivity = "Digest Avalanche Sieve",
                    signatureDescription = "Flip a single plaintext bit and observe a 50% cascade avalanche across the 256-bit digest.",
                    concepts = listOf("avalanche_effect", "preimage_resistance", "collision_resistance"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u4_l2_pointer_surgery",
                    title = "SHA-2 vs SHA-3 Sponge Functions",
                    signatureActivity = "Sponge Absorb & Squeeze",
                    signatureDescription = "Absorb message blocks into 1600-bit Keccak state and squeeze output bytes to thwart length-extension attacks.",
                    concepts = listOf("merkle_damgard", "sponge_construction", "keccak_permutation"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u4_l3_deletion",
                    title = "Message Authentication (HMAC)",
                    signatureActivity = "Inner & Outer Pad Mixer",
                    signatureDescription = "Combine shared keys with ipad (0x36) and opad (0x5c) to forge tamper-proof message tags.",
                    concepts = listOf("hmac", "inner_outer_pads", "message_integrity"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u4_l4_reversal",
                    title = "Digital Signatures & Non-Repudiation",
                    signatureActivity = "ECDSA Signing & Verification",
                    signatureDescription = "Sign hashed digests with private keys and verify with public keys to guarantee non-repudiation.",
                    concepts = listOf("digital_signatures", "ecdsa", "non_repudiation"),
                    xpReward = 30
                ),
                DSACourseLesson(
                    id = "u4_l5_doubly_circular",
                    title = "PKI & X.509 Trust Chains",
                    signatureActivity = "Certificate Authority Chain",
                    signatureDescription = "Validate leaf certificates through intermediate CAs up to trusted root trust anchors with OCSP verification.",
                    concepts = listOf("x509_certificates", "pki_trust_chain", "ocsp_revocation"),
                    xpReward = 30
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_04",
                name = "The Collision Beast",
                title = "Twin Pre-image Chimera",
                worldTheme = "The Signature Bastion",
                avatarEmoji = "🛡️👹",
                bossHp = 150,
                timeLimitSeconds = 70,
                quote = "The Birthday Paradox will birth a twin hash! I shall forge your certificates and break your HMACs!",
                defeatQuote = "Unbreakable collision resistance! Your sponge constructions and PKI roots stand immaculate!",
                phaseCount = 3,
                targetPhoenixStageAwarded = 8
            ),
            phoenixEvolutionsAwarded = listOf(7, 8),
            isUnlocked = true
        ),

        // UNIT 5: Network Security
        DSACourseUnit(
            id = "unit_05_stack",
            index = 5,
            title = "Unit 5: Network Security & Firewalls",
            difficulty = DSADifficulty.YELLOW,
            worldTheme = "The Firewall Citadel",
            worldEmoji = "🌐",
            worldMetaphor = "The Phoenix navigates the 1-RTT TLS handshake and erects stateful packet inspection perimeters",
            lessons = listOf(
                DSACourseLesson(
                    id = "u5_l1_basics",
                    title = "TLS 1.3 Transport Security",
                    signatureActivity = "1-RTT Handshake Sequence",
                    signatureDescription = "Send speculative Diffie-Hellman key shares in ClientHello to establish encrypted sessions in 1 round-trip.",
                    concepts = listOf("tls_1_3", "one_rtt_handshake", "forward_secrecy"),
                    xpReward = 15
                ),
                DSACourseLesson(
                    id = "u5_l2_implementations",
                    title = "IPsec & Secure VPN Tunnels",
                    signatureActivity = "Tunnel Mode Encapsulation",
                    signatureDescription = "Encapsulate entire IP packets inside Encapsulating Security Payload (ESP) headers for site-to-site VPNs.",
                    concepts = listOf("ipsec", "esp_tunnel_mode", "security_associations"),
                    xpReward = 20
                ),
                DSACourseLesson(
                    id = "u5_l3_bracket_bouncer",
                    title = "Stateful Firewalls & Zero Trust",
                    signatureActivity = "Connection State Table Filter",
                    signatureDescription = "Track TCP SYN, ESTABLISHED, and FIN states; apply microsegmentation and continuous least-privilege checks.",
                    concepts = listOf("stateful_inspection", "zero_trust", "packet_filtering"),
                    xpReward = 25
                ),
                DSACourseLesson(
                    id = "u5_l4_expression_eval",
                    title = "SYN Floods, DNSSEC & Defenses",
                    signatureActivity = "SYN Cookie & DNSSEC Verifier",
                    signatureDescription = "Mitigate denial of service via cryptographic SYN cookies and validate RRSIG DNS trust chains.",
                    concepts = listOf("syn_cookies", "dnssec_chains", "mitm_mitigation"),
                    xpReward = 30
                )
            ),
            boss = DSABossSpec(
                id = "boss_unit_05",
                name = "The Cyber Titan",
                title = "Zero-Day Infiltration Matrix",
                worldTheme = "The Firewall Citadel",
                avatarEmoji = "🌐⚡",
                bossHp = 160,
                timeLimitSeconds = 60,
                quote = "A massive SYN flood and rogue DNS spoofing will overwhelm your gateway buffers!",
                defeatQuote = "SYN cookies deployed, IPsec tunnels secured! The global network is defended and secure!",
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
