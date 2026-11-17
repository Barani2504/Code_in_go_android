import json
import os

CURRICULUM_DIR = r"app/src/main/assets/curriculum"
os.makedirs(CURRICULUM_DIR, exist_ok=True)

def card(idx, title, analogy, core, eg):
    return {
        "cardIndex": idx,
        "title": title,
        "analogy": analogy,
        "coreConcept": core,
        "example": eg
    }

def q(qid, prompt, options, correct_idx, explanation, wrong_explanations, real_world):
    return {
        "id": qid,
        "type": "MULTIPLE_CHOICE",
        "prompt": prompt,
        "options": options,
        "correctIndex": correct_idx,
        "explanation": explanation,
        "wrongExplanations": wrong_explanations,
        "realWorldExample": real_world
    }

chapters = []

# ==========================================
# REALM 1: SECRETS 101 (Unit I)
# ==========================================

# Chapter 1
chapters.append({
    "id": 1, "number": 1, "realmId": 1, "realmTitle": "Secrets 101",
    "title": "Why do we need security?",
    "syllabusUnit": "Unit I: Security Concepts & Policies",
    "covers": "CIA triad, security trends, security policies, legal & ethical aspects",
    "visualizerId": "cia_triad", "visualizerTitle": "CIA Triad Tap-Game",
    "prerequisites": "None", "estimatedMinutes": 5,
    "learningGoals": [
        "You will be able to distinguish Confidentiality, Integrity, and Availability.",
        "You will be able to evaluate real-world trade-offs in security policies.",
        "You will be able to identify ethical and legal duties under cybersecurity laws."
    ],
    "lessonCards": [
        card(1, "The Golden Rule: The CIA Triad", "Imagine a bank vault: locks protect secrets, seals detect stolen gold, and doors permit access.", "Security rests on three pillars: Confidentiality hides secrets, Integrity guards data accuracy, and Availability guarantees access.", "Medical records require privacy (C), tamper-proof logs (I), and 24/7 doctor access (A)."),
        card(2, "Security Policies & Enforcement", "A fire drill rules manual tells employees where to walk before smoke appears.", "A security policy is a formal plan of rules governing data access, passcodes, and breach response.", "Mandating password rotation every 90 days or enforcing zero-trust badge swipes at every door."),
        card(3, "Ethics & Law in Cyberspace", "A locksmith possesses lockpicks to fix broken doors, never to burgle houses.", "Ethical hackers operate under strict authorization. Accessing systems without written permission violates global cybercrime acts.", "Penetration testers sign a formal Rules of Engagement document before probing corporate firewalls.")
    ],
    "questions": [
        q("c1_q1", "Which pillar of the CIA triad is broken when an attacker alters medical dosage records in a hospital database?", ["Confidentiality", "Integrity", "Availability", "Authentication"], 1, "Integrity guarantees that information remains accurate and unaltered by unauthorized parties.", ["Confidentiality is about hiding secrets.", "Correct! Modifying stored values breaks integrity.", "Availability is about uptime.", "Authentication verifies user identity."], "In 2016, attackers altered Bangladesh Bank transfer records."),
        q("c1_q2", "A ransomware group launches a massive DDoS attack against an emergency dispatch center. Which security pillar is directly targeted?", ["Availability", "Confidentiality", "Non-repudiation", "Integrity"], 0, "Availability ensures services and resources remain accessible to authorized users when needed.", ["Correct! Knocking dispatch systems offline directly destroys availability.", "Confidentiality was not breached.", "Non-repudiation proves authorship.", "Integrity relates to altered data."], "The 2016 Dyn DNS attack took major services offline across North America."),
        q("c1_q3", "What distinguishes an ethical white-hat security researcher from an unauthorized hacker?", ["The coding language used", "Written permission and authorization", "Internet speed", "Using Linux instead of Windows"], 1, "Explicit, legal written authorization from the system owner defines ethical security operations.", ["Coding languages are neutral tools.", "Correct! Without written permission, accessing computer systems is illegal.", "Network speed has zero bearing.", "Operating systems are tools, not legal definitions."], "The US Computer Fraud and Abuse Act (CFAA) prosecutes unauthorized access."),
        q("c1_q4", "Which security policy rule best prevents employees from leaking customer database backups on USB sticks?", ["Disabling removable media via endpoint management", "Increasing screen monitor brightness", "Changing Wi-Fi passwords yearly", "Using larger font sizes"], 0, "Endpoint controls disable unauthorized USB storage devices.", ["Correct! Disabling mass storage interfaces blocks hardware-based exfiltration.", "Brightness has no impact.", "Wi-Fi passwords do not prevent physical USB copying.", "Typography has no security impact."], "Stuxnet was transported into air-gapped facilities on infected USB sticks."),
        q("c1_q5", "An employee reads their coworker's confidential performance review without permission. Which principle is violated?", ["Confidentiality", "Availability", "Redundancy", "Fault Tolerance"], 0, "Confidentiality ensures that only authorized individuals can view sensitive information.", ["Correct! Unauthorized reading directly compromises confidentiality.", "The document remained online.", "Redundancy refers to duplicate hardware.", "Fault tolerance allows systems to survive hardware crashes."], "Insider unauthorized snooping causes over 30% of enterprise privacy breaches."),
        q("c1_q6", "What is the primary objective of the Principle of Least Privilege?", ["Grant users only minimal permissions required for their tasks", "Give all developers root administrator access", "Allow all employees to share passwords", "Keep all firewall ports open"], 0, "Least privilege limits user and service account rights to strictly what is essential.", ["Correct! Minimal permissions restrict blast radius if an account is compromised.", "Root access for all drastically increases security risk.", "Password sharing destroys accountability.", "Open firewall ports create attack surfaces."], "The 2013 Target breach began with compromised HVAC vendor credentials having excessive access.")
    ],
    "ashRecap": "Confidentiality hides secrets, Integrity guards truth, and Availability keeps the lights on!"
})

# Chapter 2
chapters.append({
    "id": 2, "number": 2, "realmId": 1, "realmTitle": "Secrets 101",
    "title": "Attacks, services and defenses",
    "syllabusUnit": "Unit I: OSI Security Architecture",
    "covers": "Passive vs active attacks, security services & mechanisms, OSI security architecture, network security model",
    "visualizerId": "attack_defense_sorter", "visualizerTitle": "Attack or Defense? Sorter",
    "prerequisites": "Chapter 1", "estimatedMinutes": 5,
    "learningGoals": [
        "You will be able to classify attacks as passive eavesdropping or active tampering.",
        "You will be able to map security services to matching cryptographic mechanisms.",
        "You will be able to explain the OSI X.800 security architecture."
    ],
    "lessonCards": [
        card(1, "Passive vs Active Attacks", "A passive wiretapper listens to phone calls silently. An active attacker impersonates the caller.", "Passive attacks eavesdrop without altering data (hard to detect, easy to prevent). Active attacks modify, replay, or fabricate packets.", "Packet sniffing Wi-Fi traffic is passive; sending counterfeit DNS packets is active."),
        card(2, "X.800 Security Services", "A courier service offers locked boxes (encryption), stamped wax seals (integrity), and photo ID (authentication).", "X.800 defines five service classes: Authentication, Access Control, Data Confidentiality, Data Integrity, and Non-repudiation.", "Signing a PDF contract provides non-repudiation so the signer cannot deny sending it."),
        card(3, "Network Security Model", "Two castles send secret envoys across bandit-patrolled wilderness using trusted secret keys.", "A message is transformed by a security algorithm using a secret key, transmitted across an insecure channel, and verified at the destination.", "Your browser encrypts passwords with TLS before sending them across public coffee-shop Wi-Fi.")
    ],
    "questions": [
        q("c2_q1", "Which of the following is classified as a PASSIVE network attack?", ["Traffic analysis & packet sniffing", "Man-in-the-Middle packet alteration", "Replay of old authentication tokens", "Denial of Service flood"], 0, "Traffic analysis observes transmission patterns without altering message contents.", ["Correct! Eavesdropping without modifying data is passive.", "Altering packets actively manipulates data.", "Replaying captured packets introduces active network traffic.", "Flooding servers actively degrades availability."], "Eavesdroppers on unencrypted public Wi-Fi inspect packet traffic with Wireshark."),
        q("c2_q2", "Under the X.800 architecture, what does Non-Repudiation prevent?", ["A sender falsely denying they sent a message", "A hard drive from crashing", "An eavesdropper from stealing a password", "A web browser from rendering HTML"], 0, "Non-repudiation provides indisputable cryptographic proof that a specific party originated a message.", ["Correct! Digital signatures provide proof of origin that cannot be credibly denied.", "Hardware faults are handled by RAID redundancy.", "Confidentiality prevents eavesdropping.", "Browser rendering is presentation."], "Financial wire transfers require cryptographic digital signatures to legally prove origin."),
        q("c2_q3", "Why are passive attacks generally harder to detect than active attacks?", ["Because passive attacks do not alter data or create transmission errors", "Because passive attackers always use supercomputers", "Because passive attacks delete log files immediately", "Because routers do not process passive packets"], 0, "Since passive attacks merely observe data without modifying bits, they leave zero detectable file or network footprint.", ["Correct! No altered bits means no error logs or integrity alerts are triggered.", "Passive sniffing can be executed on cheap hardware taps.", "Passive eavesdroppers do not possess server write access.", "Routers route packets normally."], "Fiber-optic cable tapping copies light pulses without interrupting traffic."),
        q("c2_q4", "An attacker captures a user's login packet and resends it minutes later to authenticate. What attack is this?", ["Replay attack", "Denial of Service attack", "Traffic analysis attack", "Cryptanalysis attack"], 0, "A replay attack re-transmits valid intercepted authentication messages to gain unauthorized access.", ["Correct! Capturing and replaying valid credentials is a classic active replay attack.", "DoS takes systems down.", "Traffic analysis only examines timing frequencies.", "Cryptanalysis breaks mathematical ciphers."], "Car key fob relay attacks record unlock radio signals to unlock vehicle doors later."),
        q("c2_q5", "Which cryptographic mechanism is the primary defense against passive eavesdropping?", ["Symmetric or asymmetric encryption", "Checksum algorithms", "Firewall packet counters", "Port knocking"], 0, "Encryption scrambles plaintext into ciphertext, rendering intercepted data unreadable to passive listeners.", ["Correct! Strong encryption renders eavesdropped bytes meaningless to the attacker.", "Checksums verify integrity but do not hide information.", "Packet counters measure bandwidth.", "Port knocking hides ports."], "HTTPS TLS 1.3 prevents ISPs from reading credit card numbers submitted in web forms."),
        q("c2_q6", "In the standard network security model, what trusted component is responsible for distributing secret keys?", ["A Trusted Third Party (KDC / CA)", "The public internet router", "The DNS resolver", "The adversary"], 0, "A Trusted Third Party (such as a KDC or Certificate Authority) validates identities and distributes cryptographic keys.", ["Correct! A trusted authority securely issues or certifies public and private credentials.", "Public routers are untrusted intermediate hops.", "Standard DNS resolves names without guaranteeing key secrecy.", "The adversary is the hostile entity."], "Public Key Infrastructure (PKI) Certificate Authorities like Let's Encrypt issue web domain certificates.")
    ],
    "ashRecap": "Passive attacks eavesdrop quietly, while active attacks alter the conversation. Encryption shields you against both!"
})

# Chapter 3
chapters.append({
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
        card(1, "Caesar & Monoalphabetic Ciphers", "Rotating an alphabet decoder ring so A turns into D and B turns into E.", "Caesar shifts every letter by a fixed key k: C = (P + k) mod 26. Monoalphabetic replaces each character with a fixed substitute.", "With shift key 3, 'HELLO' shifts to 'KHOOR'."),
        card(2, "The Playfair 5x5 Grid", "Pairing letters like dance partners and swapping them using a square coordinate grid.", "Playfair encrypts pairs of letters (digraphs) using a 5x5 matrix filled with a keyword (merging I and J into one cell).", "Letters in the same row shift right; letters in the same column shift down; rectangles swap corners."),
        card(3, "Vigenère: Polyalphabetic Armor", "Using a secret password where each letter provides a different Caesar shift dial.", "Vigenère uses a repeating keyword. Each keyword letter selects a different Caesar shift, flattening single-letter frequency peaks.", "Key 'KEY' shifts letter 1 by 10, letter 2 by 4, and letter 3 by 24 repeatedly.")
    ],
    "questions": [
        q("c3_q1", "Using a Caesar cipher with key k = 3, what is the encryption of the word 'SECURITY'?", ["VHFXULWB", "TFDUJUZ", "WIGYVMX", "RDDFSJSX"], 0, "S->V, E->H, C->F, U->X, R->U, I->L, T->W, Y->B (wrapping around Z).", ["Correct! Every letter shifted forward by 3 modulo 26.", "This option applied key = 1.", "This option applied key = 4.", "This option shifted backwards by 1."], "Julius Caesar historically used key = 3 to protect military orders sent to his generals in Gaul."),
        q("c3_q2", "How does the Playfair cipher handle duplicate letters within the same digraph pair (e.g. 'LL' in 'BALLOON')?", ["It inserts an uncommon filler letter like 'X' between them", "It ignores the duplicate letter completely", "It deletes the entire word", "It converts both letters into digits 00"], 0, "Playfair rules require inserting a dummy filler letter (usually 'X' or 'Q') between identical letters in a pair.", ["Correct! 'LL' becomes 'LX' and 'LO' to ensure distinct matrix lookups.", "Ignoring duplicates destroys clean decryption.", "Playfair never deletes words.", "Classical Playfair operates on a 5x5 alphabet grid."], "The British military used Playfair in the Boer War and WWI because it required no mechanical machinery."),
        q("c3_q3", "Why is the Vigenère cipher called 'polyalphabetic'?", ["It uses multiple shift alphabets determined by the repeating keyword", "It requires 26 computers running in parallel", "It translates text into multiple spoken languages", "It only operates on polygons"], 0, "Because each character in the repeating key defines a different Caesar alphabet shift for successive characters.", ["Correct! Polyalphabetic means many alphabets are applied in rotation.", "Classical Vigenère was calculated by hand using a tableau.", "It operates on single-language character sets.", "Geometry has nothing to do with it."], "Vigenère was hailed as 'le chiffre indéchiffrable' for over two centuries."),
        q("c3_q4", "In a 5x5 Playfair grid, how many total letters can fit, and how is the 26-letter English alphabet accommodated?", ["25 letters fit; I and J share a single combined cell", "24 letters fit; vowels are removed", "30 letters fit; punctuation marks are added", "26 letters fit; the grid uses non-square cells"], 0, "5x5 = 25 cells. The letters I and J are merged into one cell to fit the 26-letter Latin alphabet.", ["Correct! Standard Playfair merges 'I' and 'J' in the same square.", "Vowels are essential to decryption.", "A 5x5 grid contains 25 positions.", "A grid with 26 cells cannot be a 5x5 square."], "John F. Kennedy's PT-109 rescue message was encoded with a Playfair cipher variant in WWII."),
        q("c3_q5", "Which classical cipher uses linear algebra and matrix multiplication (C = K * P mod 26) to encrypt blocks of letters?", ["Hill Cipher", "Caesar Cipher", "Rail Fence Cipher", "Atbash Cipher"], 0, "Lester Hill invented the Hill cipher in 1929, using matrix multiplication modulo 26 to encrypt polygraphic blocks.", ["Correct! Hill cipher encrypts m letters simultaneously using an m x m matrix key.", "Caesar cipher uses simple single-letter addition.", "Rail fence is a transposition cipher.", "Atbash is a Hebrew reversal cipher."], "Hill cipher was the first polygraphic cipher practical to operate on more than 3 symbols at once."),
        q("c3_q6", "What is the primary weakness of all simple monoalphabetic substitution ciphers?", ["They preserve the natural letter frequency distribution of the language", "They require keys longer than the internet", "They produce ciphertexts twice as long as the plaintext", "They can only be decrypted at night"], 0, "If 'E' is the most frequent letter in English, its substitute will also appear most frequently in the ciphertext.", ["Correct! Frequency analysis quickly breaks monoalphabetic ciphers by counting symbol occurrences.", "Monoalphabetic keys are only 26 characters long.", "Substitution replaces 1 letter with 1 letter.", "Time of day has no influence on cryptographic mathematics."], "Arab mathematician Al-Kindi discovered frequency cryptanalysis in the 9th century to break substitution ciphers.")
    ],
    "ashRecap": "Substitution swaps letters for other letters. Vigenère uses a keyword to make letter frequencies much harder to trace!"
})

# Chapter 4
chapters.append({
    "id": 4, "number": 4, "realmId": 1, "realmTitle": "Secrets 101",
    "title": "Shuffling & hiding",
    "syllabusUnit": "Unit I: Transposition & Steganography",
    "covers": "Rail fence, columnar transposition, steganography, product ciphers",
    "visualizerId": "rail_fence", "visualizerTitle": "Rail Fence & Stego Lab",
    "prerequisites": "Chapter 3", "estimatedMinutes": 5,
    "learningGoals": [
        "You will be able to write and read messages along a Rail Fence zig-zag.",
        "You will be able to perform Columnar Transposition using a numeric key.",
        "You will be able to explain how Steganography hides data inside image pixels."
    ],
    "lessonCards": [
        card(1, "The Rail Fence Zig-Zag", "Writing words down and up along wooden fence rails, then reading off row by row.", "Transposition rearranges letter positions without changing identity. Rail fence writes characters diagonally and reads horizontally.", "'DEFEND' on 2 rails becomes row 1 'DFN' + row 2 'EED' = 'DFNEED'."),
        card(2, "Columnar Transposition", "Writing text into columns under a numbered header, then reading out columns by key order.", "Characters fill a matrix row by row. Columns are then extracted according to the alphabetical rank of the secret key word.", "Key 'CAT' orders columns: 2 (A), 1 (C), 3 (T)."),
        card(3, "Steganography: Hiding in Plain Sight", "Writing with invisible lemon juice between the lines of a normal shopping list.", "Steganography hides the very existence of a secret message by replacing least significant bits (LSBs) of media files.", "Altering the last bit of RGB pixel values creates color changes invisible to the human eye.")
    ],
    "questions": [
        q("c4_q1", "What is the key difference between Cryptography and Steganography?", ["Cryptography scrambles messages; Steganography hides the message's existence", "Cryptography uses software; Steganography only uses paper", "Steganography is mathematical; Cryptography is not", "Cryptography is illegal in modern times"], 0, "Cryptography makes data unreadable; steganography conceals the fact that communication is occurring at all.", ["Correct! Cryptography hides content; steganography hides existence.", "Both techniques are implemented in modern software.", "Both leverage computer science.", "Cryptography is standard legal technology."], "Spies have embedded secret text into digital image EXIF tags and low-order audio bits."),
        q("c4_q2", "In a 2-rail Rail Fence cipher, how is the plaintext 'ATTACK' written across the rails?", ["Rail 1: A T A, Rail 2: T A K", "Rail 1: A T T, Rail 2: A C K", "Rail 1: A C, Rail 2: T T A K", "Rail 1: K C A, Rail 2: T T A"], 0, "Positions alternate: 0(A)->R1, 1(T)->R2, 2(T)->R1, 3(A)->R2, 4(C)->R1, 5(K)->R2. R1='ATC', R2='TAK'. Result: 'ATCTAK'.", ["Correct! Letters alternate across top and bottom rails in a zig-zag.", "This divides the word in half linearly without zig-zagging.", "This uneven division violates standard rail fence progression.", "This reversed the characters."], "American Civil War scouts used rail fence ciphers to quickly encode battlefield telegrams."),
        q("c4_q3", "In digital image steganography, what does 'LSB insertion' stand for?", ["Least Significant Bit insertion", "Longest Secret Byte insertion", "Level Security Block insertion", "Linear Shift Buffer insertion"], 0, "LSB replaces the least significant bit of each color byte (0-255), changing pixel color values by at most 1 unit.", ["Correct! Modifying the least significant bit makes visual color shifts imperceptible to human eyes.", "The acronym specifically references bit significance in binary numbers.", "Security block is unrelated.", "Buffer shifts refer to memory queues."], "A 24-bit 1920x1080 wallpaper image can hide over 700 KB of secret files in its LSB layer."),
        q("c4_q4", "What happens to the letter frequency distribution in a pure transposition cipher?", ["Letter frequencies remain 100% identical to the plaintext", "Letter frequencies become completely flat like white noise", "Vowels vanish from the ciphertext", "Every character becomes a random number"], 0, "Transposition only rearranges letter positions. The total count of every 'E', 'T', and 'A' remains completely unchanged.", ["Correct! Shuffling characters preserves exact letter frequencies.", "Flattened frequencies are the goal of polyalphabetic substitution.", "Letters are never removed.", "Characters remain letters."], "Cryptanalysts immediately suspect transposition when letter frequencies match ordinary English text."),
        q("c4_q5", "What is a 'Product Cipher'?", ["A cipher that combines two or more ciphers in sequence (like substitution + transposition)", "A commercial cipher sold in retail stores", "A cipher that multiplies numbers without modular division", "A cipher designed only for grocery barcodes"], 0, "Claude Shannon defined product ciphers as the alternating combination of substitution (confusion) and transposition (diffusion).", ["Correct! Chaining substitution and transposition builds modern block ciphers like DES and AES.", "Commercial licensing has nothing to do with it.", "Product refers to functional composition.", "Barcodes are visual encoding schemes."], "Feistel networks alternate substitution S-boxes with permutation P-boxes to implement product ciphers."),
        q("c4_q6", "How can an investigator detect that an image file contains hidden LSB steganography?", ["Statistical chi-square analysis of pixel value frequencies", "Zooming in with a magnifying glass", "Renaming the file extension to .txt", "Checking the file creation date in Windows Explorer"], 0, "Natural images have smooth color transitions. Steganography disrupts natural bit pairing, creating detectable statistical anomalies.", ["Correct! Steganalysis tools use statistical frequency tests to detect modified bit distributions.", "Human vision cannot detect a 1/255 color shade change.", "Renaming file extensions does not analyze internal binary entropy.", "File timestamps reflect OS actions."], "StegExpose and Stegdetect are popular forensic tools used by law enforcement to detect hidden payloads.")
    ],
    "ashRecap": "Transposition shuffles letters like a deck of cards. Steganography hides the secret in plain view so nobody even suspects it's there!"
})

# Chapter 5
chapters.append({
    "id": 5, "number": 5, "realmId": 1, "realmTitle": "Secrets 101",
    "title": "Perfect secrecy & breaking codes",
    "syllabusUnit": "Unit I: Information Entropy & Cryptanalysis",
    "covers": "One-time pad, entropy, frequency analysis, cryptanalysis",
    "visualizerId": "frequency_analysis", "visualizerTitle": "Frequency Lab & OTP",
    "prerequisites": "Chapter 4", "estimatedMinutes": 5,
    "learningGoals": [
        "You will be able to explain why the One-Time Pad achieves mathematically perfect secrecy.",
        "You will be able to perform frequency analysis to crack simple ciphers.",
        "You will be able to differentiate ciphertext-only, known-plaintext, and chosen-plaintext attacks."
    ],
    "lessonCards": [
        card(1, "The One-Time Pad (OTP)", "A secret key as long as the book itself, used once on a single page, then burned in fire.", "A truly random key of equal length to the plaintext, used exactly once, provides unbreakable perfect secrecy (proven by Claude Shannon).", "A 5-letter word XORed with 5 truly random bytes produces ciphertext that could equally decrypt to any 5-letter word."),
        card(2, "Why Reusing OTP is Catastrophic", "Writing two letters on the same carbon copy paper leaves visible overlapping pencil grooves.", "If key K is reused: C1 XOR C2 = (P1 XOR K) XOR (P2 XOR K) = P1 XOR P2. The secret key cancels out entirely!", "Soviet code clerks reused OTP pages in the 1940s, allowing the Venona project to decrypt sensitive diplomatic cables."),
        card(3, "Cryptanalysis Attack Models", "A detective with only a locked letter vs a detective who also found the sender's rough draft.", "Ciphertext-only is the hardest attack. Known-plaintext pairs sample input and output. Chosen-plaintext lets the attacker inject custom plaintexts.", "Chosen-plaintext is standard for evaluating modern ciphers: if you can choose the input, can you recover the key?")
    ],
    "questions": [
        q("c5_q1", "According to Claude Shannon, why is a properly implemented One-Time Pad mathematically UNBREAKABLE?", ["Every possible plaintext of that length is equally probable to an eavesdropper", "The key is encrypted by RSA first", "Computers crash when calculating XOR", "The ciphertext is always 0"], 0, "Given ciphertext C, brute-forcing all possible keys produces every valid English sentence of that length with zero information about which was real.", ["Correct! With a truly random key used once, ciphertext gives zero information about plaintext.", "OTP requires no public-key encryption.", "XOR is the fastest CPU instruction available.", "Ciphertext consists of uniformly distributed random bytes."], "The Washington-Moscow hotline used One-Time Pad teleprinters during the Cold War."),
        q("c5_q2", "What fatal flaw happens if an operator reuses the same One-Time Pad key K on two different messages P1 and P2?", ["XORing the two ciphertexts together cancels the key: C1 XOR C2 = P1 XOR P2", "The computer processor overheats", "The messages turn into blank whitespace", "The encryption speed drops by 50%"], 0, "C1 XOR C2 = (P1 XOR K) XOR (P2 XOR K) = P1 XOR P2. The key disappears, allowing frequency analysis of P1 and P2.", ["Correct! Reusing a one-time pad leaks the XOR of the plaintexts directly.", "Processing XOR has no abnormal thermal impact on hardware.", "Data remains binary values.", "XOR throughput is unchanged."], "The US Venona project decrypted thousands of Soviet spy cables because codebooks were duplicated."),
        q("c5_q3", "In cryptanalysis, what is a 'Chosen-Plaintext Attack' (CPA)?", ["The attacker chooses arbitrary plaintext messages and obtains matching ciphertexts", "The attacker has only intercept access to ciphertext bytes", "The attacker already knows the secret master key", "The attacker bribes the system administrator"], 0, "In a chosen-plaintext attack, the cryptanalyst feeds crafted inputs into the black-box cipher to observe how outputs change.", ["Correct! CPA allows defenders to test whether differential patterns leak secret key bits.", "Having only ciphertext is a Ciphertext-Only Attack.", "If the key is already known, no cryptanalysis attack is needed.", "Bribing administrators is social engineering."], "Modern block cipher standards like AES must be mathematically proven secure against Chosen-Plaintext Attacks."),
        q("c5_q4", "What is the most frequent single letter in standard English prose used in frequency analysis?", ["E (approx. 12.7%)", "Z (approx. 15%)", "X (approx. 20%)", "Q (approx. 18%)"], 0, "Letter 'E' occurs most frequently in English text (approx. 12.7%), followed by 'T', 'A', and 'O'.", ["Correct! 'E' is the highest frequency letter in standard English.", "'Z' is one of the rarest letters (< 0.1%).", "'X' is extremely rare (< 0.2%).", "'Q' is also extremely rare (< 0.1%)."], "Arthur Conan Doyle's Sherlock Holmes story 'The Dancing Men' used letter 'E' frequency to crack the code."),
        q("c5_q5", "What practical obstacle prevents the One-Time Pad from being used for all internet web traffic?", ["Key distribution: securely sharing keys as long as every message is impractical", "XOR arithmetic is too slow for mobile phones", "Fiber-optic cables cannot carry pad bits", "Browsers do not support addition"], 0, "If you already have a secure channel to transmit gigabytes of random OTP keys, you could just send the message itself on that channel!", ["Correct! Securely distributing huge quantities of pre-shared keys at global scale is logistically impossible.", "XOR is the fastest mathematical operation possible.", "Fiber optics carry arbitrary binary data.", "Browsers execute complete cryptographic suites."], "Modern SSL/TLS solves key distribution by using asymmetric cryptography instead of bulk pre-shared pads."),
        q("c5_q6", "What is 'Kerckhoffs's Principle', the foundation of modern cryptography?", ["A cryptosystem must be secure even if everything about it is public, except the key", "Security algorithms must always be kept secret commercial property", "Keys should never exceed 8 bits", "Only government military officials may read cipher code"], 0, "Auguste Kerckhoffs stated in 1883 that security through obscurity fails; security must depend solely on key secrecy.", ["Correct! Public algorithms peer-reviewed by global mathematicians prove security without hidden secrets.", "Security through obscurity is widely condemned.", "8-bit keys can be cracked in less than a microsecond.", "Open public standards protect civil liberties and online banking."], "AES and RSA are completely open public standards; knowing their code provides zero help in guessing the key.")
    ],
    "ashRecap": "The One-Time Pad is the only truly unbreakable cipher, but only if the key is completely random, as long as the message, and never reused!"
})

# ==========================================
# REALM 2: SYMMETRIC FORGE (Unit II)
# ==========================================

# Chapter 6
chapters.append({
    "id": 6, "number": 6, "realmId": 2, "realmTitle": "Symmetric Forge",
    "title": "Clock math (math toolkit I)",
    "syllabusUnit": "Unit II: Finite Fields & Number Theory Basics",
    "covers": "Modular arithmetic, Euclid's GCD, groups/rings/fields in plain words",
    "visualizerId": "clock_arithmetic", "visualizerTitle": "Clock Arithmetic & Euclid Steps",
    "prerequisites": "Chapter 5", "estimatedMinutes": 5,
    "learningGoals": [
        "You will be able to calculate modular addition and multiplication on a clock circle.",
        "You will be able to compute Greatest Common Divisor (GCD) using the Euclidean algorithm.",
        "You will be able to explain what a mathematical Field is in plain words."
    ],
    "lessonCards": [
        card(1, "Modular Arithmetic: The Clock Circle", "If it is 10:00 AM now, 5 hours later it is 3:00 PM, not 15:00.", "Modular arithmetic wraps numbers around a fixed modulus n. 15 mod 12 = 3. In computing, mod 2^8 wraps byte values from 0 to 255.", "25 mod 7 = 4, because 25 = (3 * 7) + 4."),
        card(2, "Euclid's GCD & Extended Euclid", "Finding the largest square tile that perfectly paves a rectangular floor without cutting.", "Euclid's algorithm finds GCD(a, b) by repeated remainder division: GCD(a, b) = GCD(b, a mod b). The extended version finds multiplicative modular inverses.", "GCD(54, 24) -> GCD(24, 6) -> GCD(6, 0) = 6."),
        card(3, "Groups, Rings & Fields in Plain English", "A playground with rules: Groups let you add and subtract; Rings add multiplication; Fields let you divide cleanly.", "A Galois Field GF(2^8) allows computers to perform addition, subtraction, multiplication, and division on 8-bit bytes without ever exceeding 255.", "AES operates inside GF(2^8) to ensure byte substitutions never produce fractional or overflow numbers.")
    ],
    "questions": [
        q("c6_q1", "What is the value of 38 mod 7?", ["3", "5", "1", "7"], 0, "38 divided by 7 equals 5 with a remainder of 3. (5 * 7 = 35; 38 - 35 = 3).", ["Correct! 38 = (5 * 7) + 3.", "5 is the quotient, not the remainder.", "1 is incorrect.", "The remainder modulo 7 can never equal or exceed 7."], "Modulo 26 is used to wrap letters in alphabet ciphers like Caesar."),
        q("c6_q2", "Using the Euclidean algorithm, what is GCD(48, 18)?", ["6", "12", "3", "2"], 0, "48 mod 18 = 12 -> 18 mod 12 = 6 -> 12 mod 6 = 0. The last non-zero remainder is 6.", ["Correct! 6 is the greatest common divisor.", "12 does not divide 18 evenly.", "3 is a divisor, but not the greatest common divisor.", "2 is a divisor, but 6 is greater."], "Euclid's algorithm runs in logarithmic time O(log(min(a,b)))."),
        q("c6_q3", "Two numbers a and b are called 'coprime' or 'relatively prime' when:", ["Their GCD is 1: GCD(a, b) = 1", "Both numbers are even", "Both numbers end in 5", "Their sum is 100"], 0, "Relatively prime integers share no common positive factors other than 1.", ["Correct! GCD(a, b) = 1 means they are coprime.", "Even numbers share factor 2.", "Numbers ending in 5 share factor 5.", "Sum is irrelevant to common factors."], "In RSA, public exponent e must be coprime to Euler's totient phi(n)."),
        q("c6_q4", "What is the modular multiplicative inverse of 3 modulo 7 (find x such that (3 * x) mod 7 = 1)?", ["5", "2", "4", "6"], 0, "(3 * 5) = 15. 15 mod 7 = 1. Therefore, 5 is the modular inverse of 3 mod 7.", ["Correct! 3 * 5 = 15 = (2 * 7) + 1.", "3 * 2 = 6 mod 7 = 6.", "3 * 4 = 12 mod 7 = 5.", "3 * 6 = 18 mod 7 = 4."], "Modular inverse calculation is required to decrypt RSA and ElGamal."),
        q("c6_q5", "Why does cryptography use finite fields like GF(2^8) instead of ordinary real numbers?", ["Finite fields prevent floating-point rounding errors and arithmetic overflow", "Finite fields make numbers negative", "Real numbers are illegal in software", "Finite fields have only 2 numbers"], 0, "In a finite field of 256 elements, every operation (+, -, *, /) produces an exact 8-bit integer with zero rounding loss.", ["Correct! Cryptography requires 100% deterministic, reversible arithmetic.", "Finite fields do not produce negative floating values.", "Real numbers are common in engineering, but too imprecise for exact crypto.", "GF(2^8) contains 256 distinct elements."], "AES Rijndael polynomial arithmetic takes place in Galois Field GF(2^8)."),
        q("c6_q6", "What is the result of 14 mod 5?", ["4", "2", "3", "1"], 0, "14 = (2 * 5) + 4. The remainder is 4.", ["Correct! 14 divided by 5 leaves remainder 4.", "2 is the quotient.", "3 is incorrect.", "1 is incorrect."], "Clock arithmetic underpins all cyclic group cryptography.")
    ],
    "ashRecap": "Clock math wraps numbers in a neat loop. Finite fields let computers multiply and divide bytes without ever losing a single bit!"
})

# Chapter 7
chapters.append({
    "id": 7, "number": 7, "realmId": 2, "realmTitle": "Symmetric Forge",
    "title": "How block ciphers think",
    "syllabusUnit": "Unit II: Block Cipher Principles & Feistel Structure",
    "covers": "Block vs stream, confusion & diffusion, Feistel, S-DES, design principles",
    "visualizerId": "feistel_builder", "visualizerTitle": "Feistel Builder Lab",
    "prerequisites": "Chapter 6", "estimatedMinutes": 5,
    "learningGoals": [
        "You will be able to distinguish block ciphers from stream ciphers.",
        "You will be able to define Shannon's concepts of Confusion and Diffusion.",
        "You will be able to trace a round through a Feistel network."
    ],
    "lessonCards": [
        card(1, "Block vs Stream Ciphers", "A shipping container loaded in 64-bit crates vs a garden hose spraying bits continuously.", "Block ciphers process fixed-size chunks of data (e.g. 128 bits) at a time. Stream ciphers encrypt one bit or byte at a time on the fly.", "AES processes 128-bit blocks; ChaCha20 generates a continuous keystream."),
        card(2, "Confusion and Diffusion", "Confusion is turning letters into a foggy puzzle; Diffusion is dropping food dye that spreads across the entire pool.", "Confusion obscures the relationship between the key and the ciphertext (via S-boxes). Diffusion spreads single plaintext bit changes across all ciphertext bits (via P-boxes).", "Changing 1 bit in the input scrambles ~64 bits in the output."),
        card(3, "The Feistel Network", "Splitting a deck into left and right halves, transforming one half, and swapping them round by round.", "Horst Feistel's brilliant design: splits data into L and R. R_new = L XOR F(R, RoundKey); L_new = R. Decryption runs the exact same code with keys reversed!", "DES, Blowfish, and Twofish all use Feistel networks.")
    ],
    "questions": [
        q("c7_q1", "What makes a Feistel cipher network uniquely practical for hardware implementation?", ["Decryption uses the identical circuit architecture as encryption, just reversing subkeys", "It does not require electricity", "It has zero mathematical operations", "It only works on 4-bit numbers"], 0, "Because L and R swap each round with XOR, the round function F does NOT need to be invertible; running keys in reverse order decrypts automatically.", ["Correct! The exact same hardware handles encryption and decryption.", "All silicon circuits require electric power.", "Feistel rounds apply XOR, substitution, and permutation.", "Feistel structures scale to 64, 128, and 256 bits."], "DES and Triple-DES use 16-round Feistel structures."),
        q("c7_q2", "In cryptographic design, what component is primarily responsible for introducing CONFUSION?", ["Non-linear Substitution boxes (S-boxes)", "Linear wiring permutations (P-boxes)", "XOR operations with 0", "Adding 1 to a counter"], 0, "S-boxes provide non-linear substitution, making the mathematical relationship between key and ciphertext complicated and non-linear.", ["Correct! S-boxes provide confusion.", "Permutation P-boxes provide diffusion.", "XOR with 0 leaves bits unchanged.", "Counters are used in CTR mode."], "S-box design determines a block cipher's resistance to differential cryptanalysis."),
        q("c7_q3", "What component is primarily responsible for introducing DIFFUSION in a block cipher?", ["Permutation P-boxes and bit transposition", "S-boxes only", "Clock frequency oscillators", "Random number seeds"], 0, "Permutations and linear transformations spread the influence of single input bits across multiple output bits.", ["Correct! Permutations distribute influence across the entire block.", "S-boxes specialize in local non-linear confusion.", "Clock oscillators control CPU timing.", "Random seeds initialize PRNGs."], "AES uses ShiftRows and MixColumns to achieve complete block diffusion in two rounds."),
        q("c7_q4", "In a standard Feistel round with input halves (L0, R0), what is the formula for the right output half R1?", ["R1 = L0 XOR F(R0, K1)", "R1 = R0 + L0", "R1 = L0 * K1", "R1 = R0 XOR K1"], 0, "R1 = L0 XOR F(R0, K1), while L1 = R0.", ["Correct! The round function output is XORed with the left half.", "Simple addition overflows without Feistel properties.", "Multiplication is not standard Feistel logic.", "L0 must be combined with the function output."], "This exact formula was designed by Horst Feistel at IBM in the 1970s."),
        q("c7_q5", "How many bits are in a single standard block processed by DES and Simplified DES (S-DES)?", ["DES is 64 bits; S-DES is 8 bits", "DES is 1024 bits; S-DES is 512 bits", "DES is 1 bit; S-DES is 2 bits", "Both are 256 bits"], 0, "Standard DES processes 64-bit blocks. Educational S-DES processes 8-bit blocks with 10-bit keys.", ["Correct! DES = 64-bit block; S-DES = 8-bit block.", "1024 bits is common for RSA keys, not DES blocks.", "1 bit is a stream cipher unit.", "256 bits is common for AES-256."], "S-DES was created by Edward Schaefer to teach students Feistel rounds without massive 64-bit tables."),
        q("c7_q6", "Why does a pure stream cipher require that a keystream NEVER repeat?", ["Reusing keystream allows attackers to XOR ciphertexts and recover plaintexts", "A repeating keystream deletes hard drives", "Stream ciphers only run on Saturdays", "Keystreams can only have 10 bits"], 0, "Like the One-Time Pad, reusing keystream K on messages A and B allows calculating (A XOR K) XOR (B XOR K) = A XOR B.", ["Correct! Keystream reuse destroys confidentiality.", "Software does not erase hardware.", "Days of the week are irrelevant.", "Keystreams can generate millions of bytes."], "The WEP Wi-Fi protocol was broken because IVs were too short, causing keystream reuse.")
    ],
    "ashRecap": "Block ciphers mix Confusion (S-boxes) and Diffusion (P-boxes). Feistel networks make decrypting as easy as running the round keys backward!"
})

# Chapter 8
chapters.append({
    "id": 8, "number": 8, "realmId": 2, "realmTitle": "Symmetric Forge",
    "title": "DES and why it aged",
    "syllabusUnit": "Unit II: Data Encryption Standard & Cryptanalysis",
    "covers": "DES, strength of DES, 3DES, differential & linear cryptanalysis (intuition)",
    "visualizerId": "des", "visualizerTitle": "DES 16-Round Visualizer",
    "prerequisites": "Chapter 7", "estimatedMinutes": 5,
    "learningGoals": [
        "You will be able to outline the 16-round architecture of DES.",
        "You will be able to explain why a 56-bit key is vulnerable to modern brute-force.",
        "You will be able to describe how Triple-DES (3DES) encrypts data."
    ],
    "lessonCards": [
        card(1, "The Data Encryption Standard (DES)", "A sturdy 1970s steel bank vault with 16 internal mechanical gear shifts.", "Adopted in 1977, DES takes a 64-bit block, applies an Initial Permutation (IP), runs 16 Feistel rounds using eight 6-to-4 bit S-boxes, and ends with IP^-1.", "DES was standard for global ATM banking networks for over two decades."),
        card(2, "The 56-bit Key Trap", "A briefcase lock with only 3 dials: secure against thieves on foot, but opened in seconds by a robot.", "Although the DES key is 64 bits, 8 bits are parity checks, leaving an effective key of only 56 bits (2^56 ≈ 7.2 × 10^16 keys). In 1998, EFF's Deep Crack machine broke DES in 56 hours.", "Today, a modern graphics card can exhaust 2^56 keys in under a day."),
        card(3, "Triple-DES (3DES) & EDE Mode", "Putting three padlocks on a treasure chest: Encrypt with Key 1, Decrypt with Key 2, Encrypt with Key 3.", "To extend DES life without rewriting software, 3DES applies Encrypt-Decrypt-Encrypt (EDE): C = E(K3, D(K2, E(K1, P))). Using 3 distinct keys provides 168-bit key length.", "If K1 = K2 = K3, 3DES falls back to standard single DES for backwards compatibility.")
    ],
    "questions": [
        q("c8_q1", "What is the effective secret key length of the standard Data Encryption Standard (DES)?", ["56 bits", "64 bits", "128 bits", "256 bits"], 0, "Although DES input keys are 64 bits, 8 bits are parity check bits, leaving exactly 56 bits of true cryptographic entropy.", ["Correct! 56 effective key bits.", "64 includes 8 parity check bits.", "128 bits is AES-128.", "256 bits is AES-256."], "In 1999, distributed.net and EFF cracked a DES key in 22 hours and 15 minutes."),
        q("c8_q2", "How many Feistel rounds are performed in standard DES encryption?", ["16 rounds", "8 rounds", "10 rounds", "32 rounds"], 0, "DES executes exactly 16 Feistel rounds with sixteen 48-bit subkeys generated from the key schedule.", ["Correct! 16 rounds.", "S-DES has 2 rounds; DES has 16.", "10 rounds is standard AES-128.", "32 rounds is used in some specialized ciphers."], "Each round uses a 48-bit subkey generated by PC-1, circular left shifts, and PC-2."),
        q("c8_q3", "Why does Triple-DES use an Encrypt-Decrypt-Encrypt (EDE) sequence instead of Encrypt-Encrypt-Encrypt (EEE)?", ["Setting K1 = K2 = K3 makes it backwards-compatible with single DES: E(K, D(K, E(K, P))) = E(K, P)", "Decryption runs twice as fast as encryption", "It makes the ciphertext half the size", "Because decryption deletes viruses"], 0, "In EDE, when K1=K2, the middle decryption cancels the first encryption, leaving a standard single-DES ciphertext.", ["Correct! EDE provides seamless backwards compatibility with legacy DES hardware.", "Decryption uses identical Feistel execution time.", "Block cipher output size always equals block size (64 bits).", "Decryption has no anti-virus capability."], "Financial payments systems ran 3DES EDE for years before migrating to AES."),
        q("c8_q4", "What cryptanalytic technique introduced by Biham and Shamir analyzes how input differences propagate to output differences?", ["Differential Cryptanalysis", "Linear Cryptanalysis", "Frequency Analysis", "Social Engineering"], 0, "Differential cryptanalysis studies how specific differences between plaintext pairs lead to predictable differences in intermediate states.", ["Correct! Differential cryptanalysis exploits S-box difference distributions.", "Linear cryptanalysis approximates round functions with linear boolean equations.", "Frequency analysis targets monoalphabetic substitution.", "Social engineering manipulates human operators."], "IBM knew about differential cryptanalysis in 1974 and hardened DES S-boxes against it 15 years before it was publicly published."),
        q("c8_q5", "Why is standard single DES completely prohibited in modern cybersecurity protocols?", ["Its 56-bit key space can be brute-forced in hours by modern compute hardware", "It only encrypts capital letters", "It produces unreadable binary", "It requires internet access to run"], 0, "A 56-bit key space (2^56 keys) is far too small; cloud GPU clusters can test all possible keys in hours.", ["Correct! 56-bit keys are obsolete and insecure against brute force.", "DES processes raw binary bytes, regardless of character set.", "All cipher output is binary.", "DES is a purely local mathematical algorithm."], "NIST officially withdrew DES as a federal standard in 2005, replacing it with AES."),
        q("c8_q6", "Why doesn't Double-DES (running DES twice with two keys: K1, K2) provide 112 bits of security?", ["The Meet-in-the-Middle attack reduces effective security to only 2^57 operations", "Keys cancel each other out to 0", "Computers crash on double operations", "Double-DES produces 128-bit blocks"], 0, "The Meet-in-the-Middle attack encrypts from one side and decrypts from the other, matching intermediate states in a hash table in 2^57 time.", ["Correct! Meet-in-the-Middle proves Double-DES is only slightly stronger than single DES.", "Different keys do not cancel to zero.", "Computers handle double encryption easily.", "Block size remains 64 bits."], "This is why 3DES requires three operations (EDE) rather than two.")
    ],
    "ashRecap": "DES was an engineering marvel with 16 Feistel rounds, but its 56-bit key was too small for modern computing. Always use AES!"
})

# Chapter 9
chapters.append({
    "id": 9, "number": 9, "realmId": 2, "realmTitle": "Symmetric Forge",
    "title": "AES, the modern standard",
    "syllabusUnit": "Unit II: Advanced Encryption Standard & Rijndael",
    "covers": "AES structure, 4 round steps, key expansion, finite field GF(2^8) in plain words",
    "visualizerId": "aes_matrix", "visualizerTitle": "AES State Matrix Visualizer",
    "prerequisites": "Chapter 8", "estimatedMinutes": 5,
    "learningGoals": [
        "You will be able to describe the 4x4 State Matrix of AES.",
        "You will be able to explain the 4 transformations: SubBytes, ShiftRows, MixColumns, AddRoundKey.",
        "You will be able to state the key sizes and round counts for AES-128, AES-192, and AES-256."
    ],
    "lessonCards": [
        card(1, "The 4x4 State Matrix", "A 16-seat auditorium arranged in a 4-by-4 grid where 128 bits sit as 16 individual bytes.", "AES (Rijndael) processes 128-bit blocks organized as a 4x4 matrix of bytes: 16 bytes = 128 bits. Unlike DES, AES is a substitution-permutation network, not a Feistel cipher.", "Every round transforms all 16 bytes simultaneously across rows and columns."),
        card(2, "The 4 AES Transformations", "1. Swap cards (SubBytes), 2. Slide rows (ShiftRows), 3. Mix blenders (MixColumns), 4. Stamp secret key (AddRoundKey).", "SubBytes provides non-linear confusion via an S-box in GF(2^8). ShiftRows slides rows cyclically. MixColumns multiplies columns by a fixed matrix. AddRoundKey XORs round subkeys.", "The final round omits MixColumns to allow symmetric reverse decryption."),
        card(3, "Key Sizes & Round Counts", "Longer keys require more rounds of mixing: 10 rounds for 128-bit, 12 rounds for 192-bit, 14 rounds for 256-bit.", "AES-128 uses 10 rounds; AES-192 uses 12 rounds; AES-256 uses 14 rounds. Even with the fastest supercomputers, brute-forcing AES-128 would take billions of years.", "AES-256 is certified by NSA for top-secret classified government communication.")
    ],
    "questions": [
        q("c9_q1", "What is the block size processed by the Advanced Encryption Standard (AES)?", ["128 bits (16 bytes)", "64 bits (8 bytes)", "256 bits (32 bytes)", "512 bits (64 bytes)"], 0, "AES strictly operates on 128-bit blocks (arranged as a 4x4 byte state matrix), regardless of whether key length is 128, 192, or 256 bits.", ["Correct! AES block size is always 128 bits.", "64 bits was the legacy DES block size.", "256 bits is a possible AES key length, but block size remains 128.", "512 bits is used in SHA-512 hashes."], "The Rijndael cipher was created by Belgian cryptographers Joan Daemen and Vincent Rijmen."),
        q("c9_q2", "How many rounds are performed in AES-128, AES-192, and AES-256 respectively?", ["10, 12, and 14 rounds", "16, 32, and 64 rounds", "8, 16, and 24 rounds", "5, 10, and 15 rounds"], 0, "AES uses 10 rounds for 128-bit keys, 12 rounds for 192-bit keys, and 14 rounds for 256-bit keys.", ["Correct! 10 (128-bit), 12 (192-bit), 14 (256-bit).", "16 rounds was used by DES.", "These numbers do not match NIST FIPS 197 standards.", "5 rounds would be vulnerable to cryptanalysis."], "NIST selected Rijndael after a 3-year international competition evaluating security, efficiency, and hardware performance."),
        q("c9_q3", "Which transformation in an AES round provides NON-LINEAR CONFUSION?", ["SubBytes", "ShiftRows", "MixColumns", "AddRoundKey"], 0, "SubBytes substitutes each byte with an S-box derived from multiplicative inverse in GF(2^8) combined with an affine transformation.", ["Correct! SubBytes provides non-linear substitution.", "ShiftRows provides diffusion by rotating rows.", "MixColumns provides diffusion by mixing column matrix vectors.", "AddRoundKey is linear bitwise XOR."], "AES hardware acceleration instructions (AES-NI) execute SubBytes and MixColumns in a single CPU clock cycle."),
        q("c9_q4", "In the ShiftRows step of AES, how many bytes are cyclically shifted to the left in row 0, row 1, row 2, and row 3?", ["Row 0 shifts 0; Row 1 shifts 1; Row 2 shifts 2; Row 3 shifts 3", "All rows shift by 2 bytes", "Row 0 shifts 1; Row 1 shifts 2; Row 2 shifts 3; Row 3 shifts 4", "Rows do not shift"], 0, "Row 0 is not shifted. Row 1 rotates left by 1 byte. Row 2 rotates left by 2 bytes. Row 3 rotates left by 3 bytes.", ["Correct! Row 0: 0, Row 1: 1, Row 2: 2, Row 3: 3 bytes.", "Shifting uniformly would not disperse byte positions across columns.", "Row 0 must stay aligned for diffusion across multiple rounds.", "Rows shift on every round."], "Combined with MixColumns, ShiftRows ensures that after 4 rounds, every ciphertext bit depends on every plaintext bit."),
        q("c9_q5", "Why is the MixColumns step omitted in the final round of AES?", ["To make the decryption process architecturally symmetrical and faster", "Because the computer runs out of memory", "MixColumns produces errors on the 10th round", "Because the key is exhausted"], 0, "Omitting MixColumns in the final round allows inverse AES decryption to mirror encryption without requiring extra round overhead.", ["Correct! It aligns encryption and decryption structures.", "Memory usage in AES is tiny (under 1 KB for S-box tables).", "MixColumns is mathematically sound across all rounds.", "The key schedule produces round keys for all rounds."], "Decryption reverses operations: InvShiftRows, InvSubBytes, InvAddRoundKey, InvMixColumns."),
        q("c9_q6", "What mathematical operation is executed during the AddRoundKey step?", ["Bitwise XOR between the 128-bit state matrix and the 128-bit round subkey", "Modular exponentiation", "Floating point division", "Alphabet substitution"], 0, "AddRoundKey performs a simple, fast bitwise XOR between each byte of the state matrix and the corresponding byte of the expanded round key.", ["Correct! AddRoundKey is an XOR operation.", "Exponentiation is used in RSA, not AES.", "Floating point math is never used in block ciphers.", "Substitution occurs in SubBytes."], "AES Key Expansion generates (Nr + 1) round keys using RotWord, SubWord, and Rcon constants.")
    ],
    "ashRecap": "AES turns 16 bytes into an unbreakable dance: SubBytes swaps, ShiftRows slides, MixColumns blends, and AddRoundKey locks it in with the key!"
})

# Chapter 10
chapters.append({
    "id": 10, "number": 10, "realmId": 2, "realmTitle": "Symmetric Forge",
    "title": "Modes & stream ciphers",
    "syllabusUnit": "Unit II: Block Cipher Modes & RC4 Stream Cipher",
    "covers": "ECB/CBC/CTR/GCM, RC4, pseudo-random numbers, key distribution",
    "visualizerId": "block_modes", "visualizerTitle": "Block Modes & RC4 Keystream",
    "prerequisites": "Chapter 9", "estimatedMinutes": 5,
    "learningGoals": [
        "You will be able to explain why Electronic Codebook (ECB) mode leaks data patterns.",
        "You will be able to diagram Cipher Block Chaining (CBC) with an Initialization Vector (IV).",
        "You will be able to contrast CTR mode and GCM authenticated encryption."
    ],
    "lessonCards": [
        card(1, "The ECB Penguin Problem", "Coloring every white pixel red and every black pixel blue leaves the penguin silhouette perfectly visible.", "Electronic Codebook (ECB) encrypts each 128-bit block independently. Identical plaintext blocks produce identical ciphertext blocks, leaking visual patterns.", "Encrypting a bitmap photo in ECB preserves the clear outline of the image."),
        card(2, "Cipher Block Chaining (CBC) & IV", "Locking each box with a chain link hooked to the previous box's locked padlock.", "Cipher Block Chaining XORs each plaintext block with the previous ciphertext block before encryption. The first block uses a random Initialization Vector (IV).", "Even if all plaintext blocks are identical, CBC outputs totally random-looking ciphertext."),
        card(3, "CTR Mode & GCM Authenticated Encryption", "Turning a block cipher into a super-fast stream cipher by encrypting a ticking counter.", "Counter (CTR) mode encrypts Nonce + Counter to create a keystream, enabling parallel processing. Galois/Counter Mode (GCM) adds an authentication tag (AEAD).", "TLS 1.3 requires AEAD modes like AES-GCM to prevent padding oracle tampering.")
    ],
    "questions": [
        q("c10_q1", "Why is Electronic Codebook (ECB) mode considered unsafe for encrypting files larger than one block?", ["Identical plaintext blocks produce identical ciphertext blocks, exposing data patterns", "ECB keys expire after 5 seconds", "ECB only runs on 8-bit computers", "ECB deletes the file header"], 0, "Because identical input blocks always map to identical output blocks, images and repeating records remain clearly discernible.", ["Correct! The famous ECB Linux Penguin demo shows visual patterns remain visible.", "Keys do not expire based on block mode.", "ECB works on any block cipher width.", "Headers are encrypted, not deleted."], "In 2013, Adobe suffered a breach where password hints and repeated passwords were exposed due to ECB mode."),
        q("c10_q2", "In Cipher Block Chaining (CBC) mode, what is the role of the Initialization Vector (IV)?", ["To randomize the first block so identical messages produce different ciphertexts", "To store the user's password", "To double the encryption speed", "To compress the plaintext file"], 0, "An unpredictable, unique IV ensures that two identical plaintexts encrypted with the same key produce completely different ciphertexts.", ["Correct! The IV randomizes the start of the CBC chain.", "Passwords must never be stored in an unencrypted IV.", "IV calculation adds a slight overhead, not speed.", "Block ciphers do not compress data."], "In CBC mode, the IV is transmitted in the clear alongside the ciphertext; it needs to be unique, not secret."),
        q("c10_q3", "What major performance advantage does Counter (CTR) mode have over CBC mode?", ["Blocks can be encrypted and decrypted in parallel across multiple CPU cores", "CTR mode requires no secret key", "CTR mode compresses data by 80%", "CTR mode does not use electricity"], 0, "Since the input to each block is simply Nonce + Counter, all blocks can be calculated simultaneously on independent processor threads.", ["Correct! CTR mode is fully parallelizable for massive multi-gigabit throughput.", "CTR mode strictly requires a symmetric secret key.", "CTR does not compress data.", "All computing requires power."], "AES-CTR is the foundation of high-speed NVMe disk encryption."),
        q("c10_q4", "What critical security feature does Galois/Counter Mode (GCM) provide that CBC and CTR alone do NOT?", ["Built-in cryptographic authentication and integrity (AEAD)", "Smaller file sizes", "Resistance to quantum computers", "Self-healing file recovery"], 0, "GCM is an Authenticated Encryption with Associated Data (AEAD) mode; it generates a GMAC tag detecting any bit tampering.", ["Correct! GCM proves confidentiality AND message authenticity simultaneously.", "Output size is identical plus a 16-byte authentication tag.", "AES-GCM is symmetric, vulnerable to Grover's algorithm if keys are small.", "GCM detects tampering; it does not repair damaged files."], "TLS 1.3 completely banned plain CBC mode, mandating AEAD modes like AES-GCM and ChaCha20-Poly1305."),
        q("c10_q5", "What is RC4?", ["A famous byte-oriented stream cipher designed by Ron Rivest in 1987", "A block cipher with 128 rounds", "A hashing algorithm used in Bitcoin", "A public-key digital signature standard"], 0, "RC4 is a variable key-size stream cipher with byte-oriented operations based on a 256-byte state permutation array S.", ["Correct! RC4 was widely used in SSL and WEP before statistical keystream biases retired it.", "RC4 is a stream cipher, not a block cipher.", "Bitcoin uses SHA-256 for hashing.", "RC4 is symmetric, not a digital signature."], "RC4 was officially prohibited in all versions of TLS by RFC 7465 due to keystream biases in initial bytes."),
        q("c10_q6", "What is a 'Padding Oracle' attack?", ["An exploit where server error messages reveal whether decrypted CBC padding is valid", "A machine that predicts future lottery numbers", "A hardware Trojan inserted into CPUs", "A technique to bypass passwords by entering spaces"], 0, "When servers return different errors for invalid PKCS#7 padding vs invalid decryption, attackers decrypt ciphertext byte by byte without the key.", ["Correct! The POODLE attack exploited SSL 3.0 CBC padding oracle flaws.", "Oracle in cryptography refers to an entity that answers queries.", "Padding oracle is an algorithmic flaw, not hardware.", "Padding is mathematical byte alignment."], "The POODLE and Lucky Thirteen attacks demonstrated why AEAD must be used instead of unauthenticated CBC.")
    ],
    "ashRecap": "Never use ECB mode! Use CBC with a random IV, or modern AES-GCM so you get ironclad encryption AND tamper-proof authentication in one pass!"
})

print(f"Loaded Realms 1 and 2 ({len(chapters)} chapters). Writing next realms...")

# Write current chapters
for ch in chapters:
    fn = f"chapter_{ch['number']:02d}.json"
    with open(os.path.join(CURRICULUM_DIR, fn), "w", encoding="utf-8") as f:
        json.dump(ch, f, indent=2, ensure_ascii=False)
    print(f"Generated: {fn}")
