import json
import os

CURRICULUM_DIR = r"c:\Users\baran\AndroidStudioProjects\Codeingo\app\src\main\assets\curriculum"
os.makedirs(CURRICULUM_DIR, exist_ok=True)

CHAPTERS_DATA = [
    # ── REALM 1: SECRETS 101 (Unit I) ──
    {
        "id": 1, "number": 1, "realmId": 1, "realmTitle": "Secrets 101",
        "title": "Why do we need security?",
        "syllabusUnit": "Unit I: Security Trends, Policies & Legal Aspects",
        "covers": "CIA triad, security trends, security policies, legal & ethical aspects",
        "visualizerId": "cia_triad", "visualizerTitle": "CIA Triad Tap-Game",
        "prerequisites": "None", "estimatedMinutes": 5,
        "learningGoals": [
            "You will be able to distinguish Confidentiality, Integrity, and Availability.",
            "You will be able to evaluate real-world trade-offs in security policies.",
            "You will be able to identify ethical and legal duties under cybersecurity laws."
        ],
        "lessonCards": [
            {
                "cardIndex": 1, "title": "The Golden Rule: The CIA Triad",
                "analogy": "Imagine a bank vault: locks protect secrets, seals detect stolen gold, and open doors permit access.",
                "coreConcept": "Security rests on three pillars: Confidentiality hides secrets, Integrity guards data accuracy, and Availability guarantees access.",
                "example": "Medical records require privacy (C), tamper-proof logs (I), and 24/7 doctor access (A)."
            },
            {
                "cardIndex": 2, "title": "Security Policies & Enforcement",
                "analogy": "A fire drill rules manual tells employees where to walk before smoke appears.",
                "coreConcept": "A security policy is a formal plan of rules governing data access, passcodes, and breach response.",
                "example": "Mandating password rotation every 90 days or enforcing zero-trust badge swipes at every door."
            },
            {
                "cardIndex": 3, "title": "Ethics & Law in Cyberspace",
                "analogy": "A locksmith possesses lockpicks to fix broken doors, never to burgle houses.",
                "coreConcept": "Ethical hackers operate under strict authorization. Accessing systems without written permission violates global cybercrime acts.",
                "example": "Penetration testers sign a formal Rules of Engagement document before probing corporate firewalls."
            }
        ],
        "questions": [
            {
                "id": "c1_q1", "type": "MULTIPLE_CHOICE",
                "prompt": "Which pillar of the CIA triad is broken when an attacker alters medical dosage records in a hospital database?",
                "options": ["Confidentiality", "Integrity", "Availability", "Authentication"],
                "correctIndex": 1,
                "explanation": "Integrity guarantees that information remains accurate and unaltered by unauthorized parties.",
                "wrongExplanations": [
                    "Confidentiality is about hiding secrets from eavesdroppers, not tampering with data.",
                    "Correct! Modifying stored values breaks integrity.",
                    "Availability is about keeping systems online and reachable.",
                    "Authentication verifies identity, but altering data directly violates integrity."
                ],
                "realWorldExample": "In 2016, attackers modified SWIFT transaction amounts at the central bank of Bangladesh."
            },
            {
                "id": "c1_q2", "type": "MULTIPLE_CHOICE",
                "prompt": "A ransomware group launches a massive DDoS attack against an emergency 911 dispatch center. Which security pillar is directly targeted?",
                "options": ["Availability", "Confidentiality", "Non-repudiation", "Integrity"],
                "correctIndex": 0,
                "explanation": "Availability ensures services and resources remain accessible to authorized users when needed.",
                "wrongExplanations": [
                    "Correct! Taking dispatch services offline directly destroys availability.",
                    "Confidentiality was not breached because no secret data was exfiltrated.",
                    "Non-repudiation proves who performed an action, which is not the primary impact here.",
                    "Integrity relates to altered data, whereas this attack knocked the service offline."
                ],
                "realWorldExample": "Dyn DNS DDoS attack in 2016 knocked major services offline across North America."
            },
            {
                "id": "c1_q3", "type": "MULTIPLE_CHOICE",
                "prompt": "What distinguishes an ethical white-hat security researcher from an unauthorized hacker?",
                "options": ["The coding language they use", "Written permission and authorization", "The speed of their internet", "Using Linux instead of Windows"],
                "correctIndex": 1,
                "explanation": "Explicit, legal written authorization from the system owner defines ethical security operations.",
                "wrongExplanations": [
                    "Coding languages are neutral tools used by both defenders and attackers.",
                    "Correct! Without written permission, accessing computer systems is illegal.",
                    "Network speed has zero bearing on legal and ethical boundaries.",
                    "Operating systems are personal tools, not legal definitions."
                ],
                "realWorldExample": "The US Computer Fraud and Abuse Act (CFAA) prosecutes access exceeding authorization."
            },
            {
                "id": "c1_q4", "type": "MULTIPLE_CHOICE",
                "prompt": "Which security policy rule best prevents employees from leaking customer database backups on USB sticks?",
                "options": ["Disabling removable media via endpoint management", "Increasing screen monitor brightness", "Changing Wi-Fi passwords yearly", "Using larger font sizes"],
                "correctIndex": 0,
                "explanation": "Data Loss Prevention (DLP) and endpoint controls disable unauthorized USB storage devices.",
                "wrongExplanations": [
                    "Correct! Disabling mass storage interfaces blocks hardware-based exfiltration.",
                    "Monitor settings provide zero protection against file copies.",
                    "Wi-Fi passwords do not prevent physical USB copying.",
                    "Typography has no security impact on storage bus access."
                ],
                "realWorldExample": "Stuxnet was initially transported into air-gapped nuclear facilities on infected USB thumb drives."
            },
            {
                "id": "c1_q5", "type": "MULTIPLE_CHOICE",
                "prompt": "An employee reads their coworker's confidential performance review without permission. Which principle is violated?",
                "options": ["Confidentiality", "Availability", "Redundancy", "Fault Tolerance"],
                "correctIndex": 0,
                "explanation": "Confidentiality ensures that only authorized individuals can view sensitive information.",
                "wrongExplanations": [
                    "Correct! Unauthorized reading directly compromises confidentiality.",
                    "The document remained online and accessible, so availability was unharmed.",
                    "Redundancy refers to backup hardware and duplicate systems.",
                    "Fault tolerance allows systems to survive hardware crashes."
                ],
                "realWorldExample": "Insider threats account for over 30% of enterprise customer data exposure incidents."
            },
            {
                "id": "c1_q6", "type": "MULTIPLE_CHOICE",
                "prompt": "What is the primary objective of a security policy's Principle of Least Privilege?",
                "options": ["Grant users only the minimal permissions required for their tasks", "Give every developer root administrator access", "Allow all employees to share passwords", "Keep all firewall ports open by default"],
                "correctIndex": 0,
                "explanation": "Least privilege limits user and service account rights to strictly what is essential, minimizing blast radius.",
                "wrongExplanations": [
                    "Correct! Minimal permissions restrict what an attacker can do if an account is compromised.",
                    "Root access for all drastically increases security risk.",
                    "Password sharing destroys accountability and non-repudiation.",
                    "Open firewall ports create unnecessary attack surfaces."
                ],
                "realWorldExample": "The 2013 Target breach began with compromised HVAC vendor credentials having excessive network access."
            }
        ],
        "ashRecap": "Remember: Confidentiality keeps secrets hidden, Integrity guards truth, and Availability keeps the lights on!"
    },

    # Chapter 2: Attacks, services and defenses
    {
        "id": 2, "number": 2, "realmId": 1, "realmTitle": "Secrets 101",
        "title": "Attacks, services and defenses",
        "syllabusUnit": "Unit I: OSI Security Architecture & Network Security Model",
        "covers": "Passive vs active attacks, security services & mechanisms, OSI security architecture, network security model",
        "visualizerId": "attack_defense_sorter", "visualizerTitle": "Attack or Defense? Sorter",
        "prerequisites": "Chapter 1", "estimatedMinutes": 5,
        "learningGoals": [
            "You will be able to classify attacks as passive eavesdropping or active tampering.",
            "You will be able to map security services to matching cryptographic mechanisms.",
            "You will be able to explain the OSI X.800 security architecture."
        ],
        "lessonCards": [
            {
                "cardIndex": 1, "title": "Passive vs Active Attacks",
                "analogy": "A passive wiretapper listens to phone calls silently. An active attacker impersonates the caller.",
                "coreConcept": "Passive attacks eavesdrop without altering data (hard to detect, easy to prevent). Active attacks modify, replay, or fabricate packets.",
                "example": "Packet sniffing Wi-Fi traffic is passive; sending counterfeit DNS packets is active."
            },
            {
                "cardIndex": 2, "title": "X.800 Security Services",
                "analogy": "A courier service offers locked boxes (encryption), stamped wax seals (integrity), and photo ID (authentication).",
                "coreConcept": "X.800 defines five service classes: Authentication, Access Control, Data Confidentiality, Data Integrity, and Non-repudiation.",
                "example": "Signing a PDF contract provides non-repudiation so the signer cannot deny sending it."
            },
            {
                "cardIndex": 3, "title": "Network Security Model",
                "analogy": "Two castles send secret envoys across bandit-patrolled wilderness using trusted secret keys.",
                "coreConcept": "A message is transformed by a security algorithm using a secret key, transmitted across an insecure channel, and verified at the destination.",
                "example": "Your browser encrypts passwords with TLS before sending them across public coffee-shop Wi-Fi."
            }
        ],
        "questions": [
            {
                "id": "c2_q1", "type": "MULTIPLE_CHOICE",
                "prompt": "Which of the following is classified as a PASSIVE network attack?",
                "options": ["Traffic analysis & packet sniffing", "Man-in-the-Middle packet alteration", "Replay of old authentication tokens", "Denial of Service flood"],
                "correctIndex": 0,
                "explanation": "Traffic analysis observes transmission patterns without altering message contents, making it passive.",
                "wrongExplanations": [
                    "Correct! Eavesdropping on packet traffic without modifying it is passive.",
                    "Altering packets in transit actively manipulates data.",
                    "Replaying captured packets introduces active network traffic.",
                    "Flooding servers with SYN packets actively degrades availability."
                ],
                "realWorldExample": "Eavesdroppers on unencrypted airport Wi-Fi use Wireshark to inspect unencrypted HTTP cookie tokens."
            },
            {
                "id": "c2_q2", "type": "MULTIPLE_CHOICE",
                "prompt": "Under the X.800 architecture, what does Non-Repudiation prevent?",
                "options": ["A sender falsely denying they sent a message", "A hard drive from crashing", "An eavesdropper from stealing a password", "A web browser from rendering HTML"],
                "correctIndex": 0,
                "explanation": "Non-repudiation provides indisputable cryptographic proof that a specific party originated a message.",
                "wrongExplanations": [
                    "Correct! Digital signatures provide proof of origin that cannot be credibly denied.",
                    "Hardware faults are managed by RAID redundancy, not non-repudiation.",
                    "Confidentiality prevents eavesdropping.",
                    "Browser rendering is an application layer presentation task."
                ],
                "realWorldExample": "Financial wire transfers require cryptographic digital signatures to legally prove origin."
            },
            {
                "id": "c2_q3", "type": "MULTIPLE_CHOICE",
                "prompt": "Why are passive attacks generally harder to detect than active attacks?",
                "options": ["Because passive attacks do not alter data or create transmission errors", "Because passive attackers always use supercomputers", "Because passive attacks delete log files immediately", "Because routers do not process passive packets"],
                "correctIndex": 0,
                "explanation": "Since passive attacks merely observe data in transit without modifying bits, they leave zero detectable file or network footprint.",
                "wrongExplanations": [
                    "Correct! No altered bits means no error logs or integrity alerts are triggered.",
                    "Passive sniffing can be executed on simple laptops or cheap hardware taps.",
                    "Passive eavesdroppers do not possess write access to delete server log files.",
                    "Routers route packets normally while sniffers copy packets off the wire."
                ],
                "realWorldExample": "Fiber-optic cable tapping allows surveillance agencies to copy light pulses without interrupting traffic."
            },
            {
                "id": "c2_q4", "type": "MULTIPLE_CHOICE",
                "prompt": "An attacker captures a user's login packet and resends it minutes later to authenticate without knowing the password. What attack is this?",
                "options": ["Replay attack", "Denial of Service attack", "Traffic analysis attack", "Cryptanalysis attack"],
                "correctIndex": 0,
                "explanation": "A replay attack re-transmits valid intercepted authentication messages to gain unauthorized access.",
                "wrongExplanations": [
                    "Correct! Capturing and replaying valid credentials is a classic active replay attack.",
                    "DoS attacks take systems down rather than authenticating into them.",
                    "Traffic analysis only examines packet size and timing frequencies.",
                    "Cryptanalysis attempts to break the mathematical cipher itself."
                ],
                "realWorldExample": "Car key fob relay attacks record unlock radio signals to unlock vehicle doors later."
            },
            {
                "id": "c2_q5", "type": "MULTIPLE_CHOICE",
                "prompt": "Which cryptographic mechanism is the primary defense against passive eavesdropping?",
                "options": ["Symmetric or asymmetric encryption", "Checksum algorithms", "Firewall packet counters", "Port knocking"],
                "correctIndex": 0,
                "explanation": "Encryption scrambles plaintext into ciphertext, rendering intercepted data unreadable to passive listeners.",
                "wrongExplanations": [
                    "Correct! Strong encryption renders eavesdropped bytes meaningless to the attacker.",
                    "Checksums verify data integrity but do not hide information.",
                    "Packet counters measure bandwidth, not data confidentiality.",
                    "Port knocking hides listening ports but does not encrypt transferred traffic."
                ],
                "realWorldExample": "HTTPS TLS 1.3 prevents ISPs from reading credit card numbers submitted in web forms."
            },
            {
                "id": "c2_q6", "type": "MULTIPLE_CHOICE",
                "prompt": "In the standard network security model, what trusted component is responsible for distributing secret keys to communicating parties?",
                "options": ["A Trusted Third Party (Key Distribution Center / CA)", "The public internet router", "The DNS resolver", "The adversary"],
                "correctIndex": 0,
                "explanation": "A Trusted Third Party (such as a KDC or Certificate Authority) validates identities and distributes cryptographic keys.",
                "wrongExplanations": [
                    "Correct! A trusted authority securely issues or certifies public and private credentials.",
                    "Public routers are untrusted intermediate hops.",
                    "Standard DNS resolvers map domain names to IPs without guaranteeing key secrecy.",
                    "The adversary is the hostile entity attempting to break the channel."
                ],
                "realWorldExample": "Public Key Infrastructure (PKI) Certificate Authorities like Let's Encrypt issue web domain certificates."
            }
        ],
        "ashRecap": "Spot the difference: Passive attacks listen in secret, while Active attacks alter the message. Strong encryption stops both!"
    },

    # Chapter 3: Swapping letters
    {
        "id": 3, "number": 3, "realmId": 1, "realmTitle": "Secrets 101",
        "title": "Swapping letters",
        "syllabusUnit": "Unit I: Classical Ciphers & Substitution Techniques",
        "covers": "Caesar, Monoalphabetic, Playfair, Hill, Vigenère",
        "visualizerId": "caesar_wheel", "visualizerTitle": "Caesar Wheel & Playfair Grid",
        "prerequisites": "Chapter 2", "estimatedMinutes": 5,
        "learningGoals": [
            "You will be able to encrypt and decrypt messages with the Caesar shift.",
            "You will be able to construct a 5x5 Playfair cipher matrix.",
            "You will be able to use a keyword table in the Vigenère polyalphabetic cipher."
        ],
        "lessonCards": [
            {
                "cardIndex": 1, "title": "Caesar & Monoalphabetic Ciphers",
                "analogy": "Rotating an alphabet decoder ring so A turns into D and B turns into E.",
                "coreConcept": "Caesar shifts every letter by a fixed key k: C = (P + k) mod 26. Monoalphabetic replaces each character with a fixed substitute.",
                "example": "With shift key 3, 'HELLO' shifts to 'KHOOR'."
            },
            {
                "cardIndex": 2, "title": "The Playfair 5x5 Grid",
                "analogy": "Pairing letters like dance partners and swapping them using a square coordinate grid.",
                "coreConcept": "Playfair encrypts pairs of letters (digraphs) using a 5x5 matrix filled with a keyword (merging I and J into one cell).",
                "example": "Letters in the same row shift right; letters in the same column shift down; rectangles swap corners."
            },
            {
                "cardIndex": 3, "title": "Vigenère: Polyalphabetic Armor",
                "analogy": "Using a secret password where each letter provides a different Caesar shift dial.",
                "coreConcept": "Vigenère uses a repeating keyword. Each keyword letter selects a different Caesar shift, flattening single-letter frequency peaks.",
                "example": "Key 'KEY' shifts letter 1 by 10, letter 2 by 4, and letter 3 by 24 repeatedly."
            }
        ],
        "questions": [
            {
                "id": "c3_q1", "type": "MULTIPLE_CHOICE",
                "prompt": "Using a Caesar cipher with key k = 3, what is the encryption of the word 'SECURITY'?",
                "options": ["VHFXULWB", "TFDUJUZ", "WIGYVMX", "RDDFSJSX"],
                "correctIndex": 0,
                "explanation": "S->V, E->H, C->F, U->X, R->U, I->L, T->W, Y->B (wrapping around Z).",
                "wrongExplanations": [
                    "Correct! Every letter shifted forward by 3 modulo 26.",
                    "This option applied key = 1 (Caesar shift +1).",
                    "This option applied key = 4.",
                    "This option shifted backwards by 1."
                ],
                "realWorldExample": "Julius Caesar historically used key = 3 to protect military orders sent to his generals in Gaul."
            },
            {
                "id": "c3_q2", "type": "MULTIPLE_CHOICE",
                "prompt": "How does the Playfair cipher handle duplicate letters within the same digraph pair (e.g. 'LL' in 'BALLOON')?",
                "options": ["It inserts an uncommon filler letter like 'X' between them", "It ignores the duplicate letter completely", "It deletes the entire word", "It converts both letters into digits 00"],
                "correctIndex": 0,
                "explanation": "Playfair rules require inserting a dummy filler letter (usually 'X' or 'Q') between identical letters in a pair.",
                "wrongExplanations": [
                    "Correct! 'LL' becomes 'LX' and 'LO' to ensure distinct matrix lookups.",
                    "Ignoring duplicates would destroy the ability to cleanly decrypt the message.",
                    "Playfair never deletes words from the plaintext message.",
                    "Classical Playfair operates strictly on a 5x5 alphabet grid, not numerals."
                ],
                "realWorldExample": "The British military used Playfair in the Boer War and WWI because it required no mechanical machinery."
            },
            {
                "id": "c3_q3", "type": "MULTIPLE_CHOICE",
                "prompt": "Why is the Vigenère cipher called 'polyalphabetic'?",
                "options": ["It uses multiple shift alphabets determined by the repeating keyword", "It requires 26 computers running in parallel", "It translates text into multiple spoken languages", "It only operates on polygons"],
                "correctIndex": 0,
                "explanation": "Because each character in the repeating key defines a different Caesar alphabet shift for successive characters.",
                "wrongExplanations": [
                    "Correct! Polyalphabetic means many alphabets are applied in rotation.",
                    "Classical Vigenère was calculated entirely by hand using a Vigenère tableau.",
                    "It operates on single-language character sets.",
                    "Geometry has nothing to do with polyalphabetic substitution."
                ],
                "realWorldExample": "Vigenère was hailed as 'le chiffre indéchiffrable' (the unbreakable cipher) for over two centuries."
            },
            {
                "id": "c3_q4", "type": "MULTIPLE_CHOICE",
                "prompt": "In a 5x5 Playfair grid, how many total letters can fit, and how is the 26-letter English alphabet accommodated?",
                "options": ["25 letters fit; I and J share a single combined cell", "24 letters fit; vowels are removed", "30 letters fit; punctuation marks are added", "26 letters fit; the grid uses non-square cells"],
                "correctIndex": 0,
                "explanation": "5x5 = 25 cells. The letters I and J are merged into one cell to fit the 26-letter Latin alphabet.",
                "wrongExplanations": [
                    "Correct! Standard Playfair merges 'I' and 'J' in the same square.",
                    "Vowels are essential to decryption and cannot be discarded.",
                    "A 5x5 grid mathematically contains exactly 25 positions.",
                    "A grid with 26 cells cannot be a 5x5 square matrix."
                ],
                "realWorldExample": "John F. Kennedy's PT-109 rescue message was encoded with a Playfair cipher variant in WWII."
            },
            {
                "id": "c3_q5", "type": "MULTIPLE_CHOICE",
                "prompt": "Which classical cipher uses linear algebra and matrix multiplication (C = K * P mod 26) to encrypt blocks of letters?",
                "options": ["Hill Cipher", "Caesar Cipher", "Rail Fence Cipher", "Atbash Cipher"],
                "correctIndex": 0,
                "explanation": "Lester Hill invented the Hill cipher in 1929, using matrix multiplication modulo 26 to encrypt polygraphic blocks.",
                "wrongExplanations": [
                    "Correct! Hill cipher encrypts m letters simultaneously using an m x m matrix key.",
                    "Caesar cipher uses simple single-letter addition, not matrix dot products.",
                    "Rail fence is a transposition cipher that zig-zags letter positions.",
                    "Atbash is a simple Hebrew reversal cipher (A<->Z, B<->Y)."
                ],
                "realWorldExample": "Hill cipher was the first polygraphic cipher practical to operate on more than 3 symbols at once."
            },
            {
                "id": "c3_q6", "type": "MULTIPLE_CHOICE",
                "prompt": "What is the primary weakness of all simple monoalphabetic substitution ciphers?",
                "options": ["They preserve the natural letter frequency distribution of the language", "They require keys longer than the internet", "They produce ciphertexts twice as long as the plaintext", "They can only be decrypted at night"],
                "correctIndex": 0,
                "explanation": "If 'E' is the most frequent letter in English, its substitute will also appear most frequently in the ciphertext.",
                "wrongExplanations": [
                    "Correct! Frequency analysis quickly breaks monoalphabetic ciphers by counting symbol occurrences.",
                    "Monoalphabetic keys are only 26 characters long.",
                    "Substitution replaces 1 letter with 1 letter, so message length is identical.",
                    "Time of day has no influence on cryptographic mathematics."
                ],
                "realWorldExample": "Arab mathematician Al-Kindi discovered frequency cryptanalysis in the 9th century to break substitution ciphers."
            }
        ],
        "ashRecap": "Substitution swaps letters for other letters. Vigenère uses a keyword to make letter frequencies much harder to trace!"
    },

    # Chapter 4: Shuffling & hiding
    {
        "id": 4, "number": 4, "realmId": 1, "realmTitle": "Secrets 101",
        "title": "Shuffling & hiding",
        "syllabusUnit": "Unit I: Transposition Techniques & Steganography",
        "covers": "Rail fence, columnar transposition, steganography, product ciphers",
        "visualizerId": "rail_fence", "visualizerTitle": "Rail Fence & Stego Lab",
        "prerequisites": "Chapter 3", "estimatedMinutes": 5,
        "learningGoals": [
            "You will be able to write and read messages along a Rail Fence zig-zag.",
            "You will be able to perform Columnar Transposition using a numeric key.",
            "You will be able to explain how Steganography hides data inside image pixels."
        ],
        "lessonCards": [
            {
                "cardIndex": 1, "title": "The Rail Fence Zig-Zag",
                "analogy": "Writing words down and up along wooden fence rails, then reading off row by row.",
                "coreConcept": "Transposition rearranges letter positions without changing identity. Rail fence writes characters diagonally and reads horizontally.",
                "example": "'DEFEND' on 2 rails becomes row 1 'DFN' + row 2 'EED' = 'DFNEED'."
            },
            {
                "cardIndex": 2, "title": "Columnar Transposition",
                "analogy": "Writing text into columns under a numbered header, then reading out columns by key order.",
                "coreConcept": "Characters fill a matrix row by row. Columns are then extracted according to the alphabetical rank of the secret key word.",
                "example": "Key 'CAT' orders columns: 2 (A), 1 (C), 3 (T)."
            },
            {
                "cardIndex": 3, "title": "Steganography: Hiding in Plain Sight",
                "analogy": "Writing with invisible lemon juice between the lines of a normal shopping list.",
                "coreConcept": "Steganography hides the very existence of a secret message by replacing least significant bits (LSBs) of media files.",
                "example": "Altering the last bit of RGB pixel values creates color changes invisible to the human eye."
            }
        ],
        "questions": [
            {
                "id": "c4_q1", "type": "MULTIPLE_CHOICE",
                "prompt": "What is the key difference between Cryptography and Steganography?",
                "options": ["Cryptography scrambles messages; Steganography hides the message's existence", "Cryptography uses software; Steganography only uses paper", "Steganography is mathematical; Cryptography is not", "Cryptography is illegal in modern times"],
                "correctIndex": 0,
                "explanation": "Cryptography makes data unreadable; steganography conceals the fact that communication is occurring at all.",
                "wrongExplanations": [
                    "Correct! Cryptography hides content; steganography hides existence.",
                    "Both techniques are implemented in modern software and media files.",
                    "Both fields leverage computer science and mathematical algorithms.",
                    "Cryptography is standard legal technology safeguarding the internet."
                ],
                "realWorldExample": "Spies have embedded secret text into digital image EXIF tags and low-order audio sample bits."
            },
            {
                "id": "c4_q2", "type": "MULTIPLE_CHOICE",
                "prompt": "In a 2-rail Rail Fence cipher, how is the plaintext 'ATTACK' written across the rails?",
                "options": ["Rail 1: A T A, Rail 2: T A K", "Rail 1: A T T, Rail 2: A C K", "Rail 1: A C, Rail 2: T T A K", "Rail 1: K C A, Rail 2: T T A"],
                "correctIndex": 0,
                "explanation": "Positions alternate: pos 0(A)->R1, 1(T)->R2, 2(T)->R1, 3(A)->R2, 4(C)->R1, 5(K)->R2. R1='ATC', R2='TAK'. Result: 'ATCTAK'.",
                "wrongExplanations": [
                    "Correct! Letters alternate across top and bottom rails in a zig-zag.",
                    "This divides the word in half linearly without zig-zagging.",
                    "This uneven division violates standard rail fence progression.",
                    "This reversed the characters."
                ],
                "realWorldExample": "American Civil War scouts used rail fence ciphers to quickly encode battlefield telegrams."
            },
            {
                "id": "c4_q3", "type": "MULTIPLE_CHOICE",
                "prompt": "In digital image steganography, what does 'LSB insertion' stand for?",
                "options": ["Least Significant Bit insertion", "Longest Secret Byte insertion", "Level Security Block insertion", "Linear Shift Buffer insertion"],
                "correctIndex": 0,
                "explanation": "LSB replaces the least significant bit of each color byte (0-255), changing pixel color values by at most 1 unit.",
                "wrongExplanations": [
                    "Correct! Modifying the least significant bit makes visual color shifts imperceptible to human eyes.",
                    "The acronym specifically references bit significance in binary numbers.",
                    "Security block is unrelated to pixel color channels.",
                    "Buffer shifts refer to memory queues, not steganography."
                ],
                "realWorldExample": "A 24-bit 1920x1080 wallpaper image can hide over 700 KB of secret files in its LSB layer without visible changes."
            },
            {
                "id": "c4_q4", "type": "MULTIPLE_CHOICE",
                "prompt": "What happens to the letter frequency distribution in a pure transposition cipher?",
                "options": ["Letter frequencies remain 100% identical to the plaintext", "Letter frequencies become completely flat like white noise", "Vowels vanish from the ciphertext", "Every character becomes a random number"],
                "correctIndex": 0,
                "explanation": "Transposition only rearranges letter positions. The total count of every 'E', 'T', and 'A' remains completely unchanged.",
                "wrongExplanations": [
                    "Correct! Shuffling characters preserves exact letter frequencies.",
                    "Flattened frequencies are the goal of polyalphabetic substitution, not transposition.",
                    "Letters are never removed during transposition.",
                    "Characters remain letters, simply moved to different indices."
                ],
                "realWorldExample": "Cryptanalysts immediately suspect transposition when letter frequencies match ordinary English text."
            },
            {
                "id": "c4_q5", "type": "MULTIPLE_CHOICE",
                "prompt": "What is a 'Product Cipher'?",
                "options": ["A cipher that combines two or more ciphers in sequence (like substitution + transposition)", "A commercial cipher sold in retail stores", "A cipher that multiplies numbers without modular division", "A cipher designed only for grocery barcodes"],
                "correctIndex": 0,
                "explanation": "Claude Shannon defined product ciphers as the alternating combination of substitution (confusion) and transposition (diffusion).",
                "wrongExplanations": [
                    "Correct! Chaining substitution and transposition builds modern block ciphers like DES and AES.",
                    "Commercial licensing has nothing to do with cryptographic construction.",
                    "Product in cryptography refers to functional composition, not arithmetic multiplication.",
                    "Barcodes are visual encoding schemes, not cryptographic ciphers."
                ],
                "realWorldExample": "Feistel networks alternate substitution S-boxes with permutation P-boxes to implement product ciphers."
            },
            {
                "id": "c4_q6", "type": "MULTIPLE_CHOICE",
                "prompt": "How can an investigator detect that an image file contains hidden LSB steganography?",
                "options": ["Statistical chi-square analysis of pixel value frequencies", "Zooming in with a magnifying glass", "Renaming the file extension to .txt", "Checking the file creation date in Windows Explorer"],
                "correctIndex": 0,
                "explanation": "Natural images have smooth color transitions. Steganography disrupts natural bit pairing, creating detectable statistical anomalies.",
                "wrongExplanations": [
                    "Correct! Steganalysis tools use statistical frequency tests to detect modified bit distributions.",
                    "Human vision cannot detect a 1/255 color shade change.",
                    "Renaming file extensions does not analyze internal binary entropy.",
                    "File timestamps reflect OS actions, not image payload contents."
                ],
                "realWorldExample": "StegExpose and Stegdetect are popular forensic tools used by law enforcement to detect hidden payloads."
            }
        ],
        "ashRecap": "Transposition shuffles letters like a deck of cards. Steganography hides the secret in plain view so nobody even suspects it's there!"
    },

    # Chapter 5: Perfect secrecy & breaking codes
    {
        "id": 5, "number": 5, "realmId": 1, "realmTitle": "Secrets 101",
        "title": "Perfect secrecy & breaking codes",
        "syllabusUnit": "Unit I: One-Time Pad, Information Entropy & Cryptanalysis",
        "covers": "One-time pad, entropy, frequency analysis, cryptanalysis",
        "visualizerId": "frequency_analysis", "visualizerTitle": "Frequency Lab & OTP",
        "prerequisites": "Chapter 4", "estimatedMinutes": 5,
        "learningGoals": [
            "You will be able to explain why the One-Time Pad achieves mathematically perfect secrecy.",
            "You will be able to perform frequency analysis to crack simple ciphers.",
            "You will be able to differentiate ciphertext-only, known-plaintext, and chosen-plaintext attacks."
        ],
        "lessonCards": [
            {
                "cardIndex": 1, "title": "The One-Time Pad (OTP)",
                "analogy": "A secret key as long as the book itself, used once on a single page, then burned in fire.",
                "coreConcept": "A truly random key of equal length to the plaintext, used exactly once, provides unbreakable perfect secrecy (proven by Claude Shannon).",
                "example": "A 5-letter word XORed with 5 truly random bytes produces ciphertext that could equally decrypt to any 5-letter word."
            },
            {
                "cardIndex": 2, "title": "Why Reusing OTP is Catastrophic",
                "analogy": "Writing two letters on the same carbon copy paper leaves visible overlapping pencil grooves.",
                "coreConcept": "If key K is reused: C1 XOR C2 = (P1 XOR K) XOR (P2 XOR K) = P1 XOR P2. The secret key cancels out entirely!",
                "example": "Soviet code clerks reused OTP pages in the 1940s, allowing the Venona project to decrypt sensitive diplomatic cables."
            },
            {
                "cardIndex": 3, "title": "Cryptanalysis Attack Models",
                "analogy": "A detective with only a locked letter vs a detective who also found the sender's rough draft.",
                "coreConcept": "Ciphertext-only is the hardest attack. Known-plaintext pairs sample input and output. Chosen-plaintext lets the attacker inject custom plaintexts.",
                "example": "Chosen-plaintext is standard for evaluating modern ciphers: if you can choose the input, can you recover the key?"
            }
        ],
        "questions": [
            {
                "id": "c5_q1", "type": "MULTIPLE_CHOICE",
                "prompt": "According to Claude Shannon, why is a properly implemented One-Time Pad mathematically UNBREAKABLE?",
                "options": ["Every possible plaintext of that length is equally probable to an eavesdropper", "The key is encrypted by RSA first", "Computers crash when calculating XOR", "The ciphertext is always 0"],
                "correctIndex": 0,
                "explanation": "Given ciphertext C, brute-forcing all possible keys produces every valid English sentence of that length with zero information about which was real.",
                "wrongExplanations": [
                    "Correct! With a truly random key used once, ciphertext gives zero information about plaintext (P(M|C) = P(M)).",
                    "OTP requires no public-key encryption; it relies purely on random key bits.",
                    "XOR is the fastest, simplest CPU instruction available.",
                    "Ciphertext consists of uniformly distributed random bytes, not zeros."
                ],
                "realWorldExample": "The Washington-Moscow hotline used One-Time Pad teleprinters during the Cold War."
            },
            {
                "id": "c5_q2", "type": "MULTIPLE_CHOICE",
                "prompt": "What fatal flaw happens if an operator reuses the same One-Time Pad key K on two different messages P1 and P2?",
                "options": ["XORing the two ciphertexts together cancels the key: C1 XOR C2 = P1 XOR P2", "The computer processor overheats", "The messages turn into blank whitespace", "The encryption speed drops by 50%"],
                "correctIndex": 0,
                "explanation": "C1 XOR C2 = (P1 XOR K) XOR (P2 XOR K) = P1 XOR P2. The key disappears, allowing frequency analysis of P1 and P2.",
                "wrongExplanations": [
                    "Correct! Reusing a one-time pad leaks the XOR of the plaintexts directly.",
                    "Processing XOR has no abnormal thermal impact on hardware.",
                    "Data remains binary values, not whitespace.",
                    "XOR throughput is unchanged regardless of key reuse."
                ],
                "realWorldExample": "The US Venona project decrypted thousands of Soviet spy cables because codebooks were duplicated."
            },
            {
                "id": "c5_q3", "type": "MULTIPLE_CHOICE",
                "prompt": "In cryptanalysis, what is a 'Chosen-Plaintext Attack' (CPA)?",
                "options": ["The attacker chooses arbitrary plaintext messages and obtains matching ciphertexts", "The attacker has only intercept access to ciphertext bytes", "The attacker already knows the secret master key", "The attacker bribes the system administrator"],
                "correctIndex": 0,
                "explanation": "In a chosen-plaintext attack, the cryptanalyst feeds crafted inputs into the black-box cipher to observe how outputs change.",
                "wrongExplanations": [
                    "Correct! CPA allows defenders to test whether differential patterns leak secret key bits.",
                    "Having only ciphertext is a Ciphertext-Only Attack.",
                    "If the key is already known, no cryptanalysis attack is needed.",
                    "Bribing administrators is social engineering/bribery, not mathematical CPA."
                ],
                "realWorldExample": "Modern block cipher standards like AES must be mathematically proven secure against Chosen-Plaintext Attacks."
            },
            {
                "id": "c5_q4", "type": "MULTIPLE_CHOICE",
                "prompt": "What is the most frequent single letter in standard English prose used in frequency analysis?",
                "options": ["E (approx. 12.7%)", "Z (approx. 15%)", "X (approx. 20%)", "Q (approx. 18%)"],
                "correctIndex": 0,
                "explanation": "Letter 'E' occurs most frequently in English text (approx. 12.7%), followed by 'T', 'A', and 'O'.",
                "wrongExplanations": [
                    "Correct! 'E' is the highest frequency letter in standard English.",
                    "'Z' is one of the rarest letters (< 0.1%).",
                    "'X' is extremely rare (< 0.2%).",
                    "'Q' is also extremely rare (< 0.1%)."
                ],
                "realWorldExample": "Arthur Conan Doyle's Sherlock Holmes story 'The Adventure of the Dancing Men' used letter 'E' frequency to crack the code."
            },
            {
                "id": "c5_q5", "type": "MULTIPLE_CHOICE",
                "prompt": "What practical obstacle prevents the One-Time Pad from being used for all internet web traffic?",
                "options": ["Key distribution: securely sharing keys as long as every message is impractical", "XOR arithmetic is too slow for mobile phones", "Fiber-optic cables cannot carry pad bits", "Browsers do not support addition"],
                "correctIndex": 0,
                "explanation": "If you already have a secure channel to transmit gigabytes of random OTP keys, you could just send the message itself on that channel!",
                "wrongExplanations": [
                    "Correct! Securely distributing huge quantities of pre-shared keys at global scale is logistically impossible.",
                    "XOR is the fastest mathematical operation possible in silicon.",
                    "Fiber optics carry arbitrary binary data at the speed of light.",
                    "Browsers execute complete cryptographic suites in WebAssembly and JS."
                ],
                "realWorldExample": "Modern SSL/TLS solves key distribution by using asymmetric cryptography (Diffie-Hellman) instead of bulk pre-shared pads."
            },
            {
                "id": "c5_q6", "type": "MULTIPLE_CHOICE",
                "prompt": "What is 'Kerckhoffs's Principle', the foundation of modern cryptography?",
                "options": ["A cryptosystem must be secure even if everything about it is public, except the key", "Security algorithms must always be kept secret commercial property", "Keys should never exceed 8 bits", "Only government military officials may read cipher code"],
                "correctIndex": 0,
                "explanation": "Auguste Kerckhoffs stated in 1883 that security through obscurity fails; security must depend solely on key secrecy.",
                "wrongExplanations": [
                    "Correct! Public algorithms peer-reviewed by global mathematicians prove security without hidden secrets.",
                    "Security through obscurity is widely condemned by modern cybersecurity standards.",
                    "8-bit keys can be cracked in less than a microsecond.",
                    "Open public standards protect civil liberties, online banking, and everyday privacy."
                ],
                "realWorldExample": "AES and RSA are completely open public standards; knowing their code provides zero help in guessing the key."
            }
        ],
        "ashRecap": "The One-Time Pad is the only truly unbreakable cipher, but only if the key is completely random, as long as the message, and never reused!"
    }
]

print(f"Generating curriculum chapters...")
for ch in CHAPTERS_DATA:
    filename = f"chapter_{ch['number']:02d}.json"
    filepath = os.path.join(CURRICULUM_DIR, filename)
    with open(filepath, "w", encoding="utf-8") as f:
        json.dump(ch, f, indent=2, ensure_ascii=False)
    print(f"Saved: {filename}")

print("Phase 1 done.")
