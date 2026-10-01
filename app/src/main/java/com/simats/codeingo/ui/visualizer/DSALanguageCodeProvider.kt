package com.simats.codeingo.ui.visualizer

object DSALanguageCodeProvider {

    fun code(structure: String, language: String): String {
        val lang = language.lowercase()
        return when (structure.lowercase()) {
            "stack" -> stackCode(lang)
            "queue" -> queueCode(lang)
            "linked list", "linkedlist" -> linkedListCode(lang)
            "binary tree", "binarytree", "tree", "bst" -> binaryTreeCode(lang)
            "avl tree", "avl" -> avlTreeCode(lang)
            "trie" -> trieCode(lang)
            "graph" -> graphCode(lang)
            else -> arrayCode(lang)
        }
    }

    private fun stackCode(lang: String): String = when (lang) {
        "python" -> """
class Stack:
    def __init__(self):
        self.items = []
    
    def push(self, val):
        self.items.append(val)  # O(1) amortized
    
    def pop(self):
        if not self.is_empty():
            return self.items.pop()  # O(1) LIFO
        raise IndexError("Stack is empty")
    
    def peek(self):
        return self.items[-1] if self.items else None
    
    def is_empty(self):
        return len(self.items) == 0
""".trimIndent()
        "java" -> """
public class Stack<T> {
    private java.util.ArrayList<T> list = new java.util.ArrayList<>();
    
    public void push(T val) {
        list.add(val); // O(1)
    }
    
    public T pop() {
        if (isEmpty()) throw new java.util.EmptyStackException();
        return list.remove(list.size() - 1); // LIFO
    }
    
    public T peek() {
        if (isEmpty()) return null;
        return list.get(list.size() - 1);
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
""".trimIndent()
        "cpp", "c++" -> """
#include <vector>
#include <stdexcept>

template <typename T>
class Stack {
private:
    std::vector<T> items;
public:
    void push(const T& val) {
        items.push_back(val); // O(1)
    }
    
    T pop() {
        if (isEmpty()) throw std::out_of_range("Stack underflow");
        T top = items.back();
        items.pop_back();
        return top;
    }
    
    T peek() const {
        return items.back();
    }
    
    bool isEmpty() const {
        return items.empty();
    }
};
""".trimIndent()
        "go" -> """
package main

type Stack []int

func (s *Stack) Push(val int) {
    *s = append(*s, val) // O(1) amortized
}

func (s *Stack) Pop() int {
    if len(*s) == 0 {
        panic("stack underflow")
    }
    top := (*s)[len(*s)-1]
    *s = (*s)[:len(*s)-1]
    return top
}

func (s *Stack) Peek() int {
    return (*s)[len(*s)-1]
}
""".trimIndent()
        "javascript", "js" -> """
class Stack {
    constructor() {
        this.items = [];
    }
    push(val) {
        this.items.push(val); // O(1)
    }
    pop() {
        if (this.isEmpty()) return null;
        return this.items.pop(); // LIFO
    }
    peek() {
        return this.items[this.items.length - 1];
    }
    isEmpty() {
        return this.items.length === 0;
    }
}
""".trimIndent()
        "kotlin" -> """
class Stack<T> {
    private val elements = mutableListOf<T>()
    
    fun push(item: T) = elements.add(item)
    
    fun pop(): T? = if (elements.isNotEmpty()) elements.removeAt(elements.size - 1) else null
    
    fun peek(): T? = elements.lastOrNull()
    
    fun isEmpty(): Boolean = elements.isEmpty()
}
""".trimIndent()
        else -> """
struct Stack<T> {
    private var elements: [T] = []
    
    mutating func push(_ element: T) {
        elements.append(element) // O(1) amortized
    }
    
    mutating func pop() -> T? {
        return elements.popLast() // O(1) LIFO
    }
    
    func peek() -> T? {
        return elements.last
    }
    
    var isEmpty: Bool {
        elements.isEmpty
    }
}
""".trimIndent()
    }

    private fun queueCode(lang: String): String = when (lang) {
        "python" -> """
from collections import deque

class Queue:
    def __init__(self):
        self.items = deque()
    
    def enqueue(self, val):
        self.items.append(val)  # O(1)
    
    def dequeue(self):
        if not self.is_empty():
            return self.items.popleft()  # O(1) FIFO
        raise IndexError("Queue is empty")
    
    def peek(self):
        return self.items[0] if self.items else None
    
    def is_empty(self):
        return len(self.items) == 0
""".trimIndent()
        "java" -> """
import java.util.LinkedList;

public class Queue<T> {
    private LinkedList<T> list = new LinkedList<>();
    
    public void enqueue(T item) {
        list.addLast(item); // O(1)
    }
    
    public T dequeue() {
        if (isEmpty()) throw new RuntimeException("Queue empty");
        return list.removeFirst(); // O(1) FIFO
    }
    
    public T peek() {
        return list.peekFirst();
    }
    
    public boolean isEmpty() {
        return list.isEmpty();
    }
}
""".trimIndent()
        "kotlin" -> """
class Queue<T> {
    private val elements = java.util.ArrayDeque<T>()
    
    fun enqueue(item: T) = elements.addLast(item)
    
    fun dequeue(): T? = if (elements.isNotEmpty()) elements.removeFirst() else null
    
    fun peek(): T? = elements.peekFirst()
    
    fun isEmpty(): Boolean = elements.isEmpty()
}
""".trimIndent()
        else -> """
struct Queue<T> {
    private var elements: [T] = []
    
    mutating func enqueue(_ element: T) {
        elements.append(element)
    }
    
    mutating func dequeue() -> T? {
        return elements.isEmpty ? nil : elements.removeFirst()
    }
    
    func peek() -> T? {
        return elements.first
    }
    
    var isEmpty: Bool {
        elements.isEmpty
    }
}
""".trimIndent()
    }

    private fun linkedListCode(lang: String): String = when (lang) {
        "python" -> """
class Node:
    def __init__(self, val, next=None):
        self.val = val
        self.next = next

class LinkedList:
    def __init__(self):
        self.head = None
    
    def insert_head(self, val):
        self.head = Node(val, self.head)  # O(1)
    
    def delete_head(self):
        if self.head:
            self.head = self.head.next  # O(1)
""".trimIndent()
        "kotlin" -> """
data class Node<T>(var value: T, var next: Node<T>? = null)

class LinkedList<T> {
    var head: Node<T>? = null
        private set

    fun insertHead(value: T) {
        head = Node(value, head) // O(1)
    }

    fun deleteHead(): T? {
        val oldHead = head ?: return null
        head = oldHead.next
        return oldHead.value
    }
}
""".trimIndent()
        else -> """
class Node<T> {
    var value: T
    var next: Node?
    init(value: T, next: Node? = nil) {
        self.value = value
        self.next = next
    }
}

class LinkedList<T> {
    var head: Node<T>?
    func insertHead(_ value: T) {
        head = Node(value: value, next: head) // O(1)
    }
}
""".trimIndent()
    }

    private fun binaryTreeCode(lang: String): String = when (lang) {
        "python" -> """
class TreeNode:
    def __init__(self, val, left=None, right=None):
        self.val = val
        self.left = left
        self.right = right

def insert_bst(root, val):
    if not root:
        return TreeNode(val)
    if val < root.val:
        root.left = insert_bst(root.left, val)
    else:
        root.right = insert_bst(root.right, val)
    return root
""".trimIndent()
        "kotlin" -> """
class TreeNode<T : Comparable<T>>(
    var value: T,
    var left: TreeNode<T>? = null,
    var right: TreeNode<T>? = null
)

fun <T : Comparable<T>> insertBst(root: TreeNode<T>?, value: T): TreeNode<T> {
    if (root == null) return TreeNode(value)
    if (value < root.value) {
        root.left = insertBst(root.left, value)
    } else {
        root.right = insertBst(root.right, value)
    }
    return root
}
""".trimIndent()
        else -> """
class TreeNode<T: Comparable> {
    var value: T
    var left: TreeNode?
    var right: TreeNode?
    init(_ value: T) { self.value = value }
}
""".trimIndent()
    }

    private fun avlTreeCode(lang: String): String = """
class AVLNode:
    def __init__(self, key):
        self.key = key
        self.left = None
        self.right = None
        self.height = 1

def get_height(node):
    return node.height if node else 0

def get_balance(node):
    return get_height(node.left) - get_height(node.right) if node else 0
""".trimIndent()

    private fun trieCode(lang: String): String = """
class TrieNode:
    def __init__(self):
        self.children = {}
        self.is_end_of_word = False

class Trie:
    def __init__(self):
        self.root = TrieNode()
    
    def insert(self, word: str):
        curr = self.root
        for ch in word:
            if ch not in curr.children:
                curr.children[ch] = TrieNode()
            curr = curr.children[ch]
        curr.is_end_of_word = True
""".trimIndent()

    private fun graphCode(lang: String): String = """
from collections import defaultdict, deque

class Graph:
    def __init__(self):
        self.adj = defaultdict(list)
    
    def add_edge(self, u, v, bidirectional=True):
        self.adj[u].append(v)
        if bidirectional:
            self.adj[v].append(u)
    
    def bfs(self, start):
        visited = {start}
        queue = deque([start])
        while queue:
            node = queue.popleft()
            for neighbor in self.adj[node]:
                if neighbor not in visited:
                    visited.add(neighbor)
                    queue.append(neighbor)
""".trimIndent()

    private fun arrayCode(lang: String): String = """
# Dynamic Array (List) Operations
arr = [10, 20, 30]

# Insertion at end: O(1) amortized
arr.append(40)

# Random access: O(1) direct address
val = arr[2]

# Deletion at end: O(1)
arr.pop()

# Linear Search: O(n)
found = 20 in arr
""".trimIndent()
}
