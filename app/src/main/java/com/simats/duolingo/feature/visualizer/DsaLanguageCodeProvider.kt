package com.simats.duolingo.feature.visualizer

/**
 * Provides idiomatic data structure code implementations across multiple languages:
 * Python, Java, C++, Swift, Go, JavaScript, Kotlin.
 * Replicated directly from iOS DSALanguageCodeProvider.swift.
 */
object DsaLanguageCodeProvider {

    fun getCode(structure: String, language: String): String {
        val lang = language.lowercase().trim()
        val s = structure.lowercase().trim()
        return when {
            s.contains("stack") -> stackCode(lang)
            s.contains("queue") -> queueCode(lang)
            s.contains("linked") -> linkedListCode(lang)
            s.contains("avl") -> avlTreeCode(lang)
            s.contains("tree") || s.contains("bst") -> binaryTreeCode(lang)
            s.contains("trie") -> trieCode(lang)
            s.contains("graph") -> graphCode(lang)
            s.contains("sort") -> sortingCode(lang)
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
        "c++", "cpp" -> """
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
        if (isEmpty()) throw std::out_of_range("Stack empty");
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
        "swift" -> """
struct Stack<T> {
    private var elements: [T] = []
    
    mutating func push(_ element: T) {
        elements.append(element) // O(1)
    }
    
    mutating func pop() -> T? {
        elements.popLast() // O(1) LIFO
    }
    
    func peek() -> T? {
        elements.last
    }
    
    var isEmpty: Bool {
        elements.isEmpty
    }
}
""".trimIndent()
        "go" -> """
package main

type Stack []int

func (s *Stack) Push(val int) {
    *s = append(*s, val) // O(1)
}

func (s *Stack) Pop() int {
    if len(*s) == 0 { return -1 }
    idx := len(*s) - 1
    val := (*s)[idx]
    *s = (*s)[:idx]
    return val
}

func (s *Stack) Peek() int {
    if len(*s) == 0 { return -1 }
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
        else -> """
class Stack<T> {
    private val elements = mutableListOf<T>()
    
    fun push(item: T) = elements.add(item) // O(1)
    
    fun pop(): T? = if (elements.isNotEmpty()) elements.removeAt(elements.size - 1) else null
    
    fun peek(): T? = elements.lastOrNull()
    
    val isEmpty: Boolean get() = elements.isEmpty()
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
        self.items.append(val) # O(1)
    
    def dequeue(self):
        return self.items.popleft() # O(1) FIFO
    
    def peek(self):
        return self.items[0] if self.items else None
""".trimIndent()
        "java" -> """
import java.util.LinkedList;

public class Queue<T> {
    private LinkedList<T> list = new LinkedList<>();
    
    public void enqueue(T item) {
        list.addLast(item); // O(1)
    }
    
    public T dequeue() {
        return list.removeFirst(); // FIFO
    }
    
    public T peek() {
        return list.peekFirst();
    }
}
""".trimIndent()
        else -> """
class Queue<T> {
    private val elements = java.util.ArrayDeque<T>()
    
    fun enqueue(item: T) = elements.addLast(item) // O(1)
    
    fun dequeue(): T? = if (elements.isNotEmpty()) elements.removeFirst() else null
    
    fun peek(): T? = elements.peekFirst()
}
""".trimIndent()
    }

    private fun linkedListCode(lang: String): String = when (lang) {
        "python" -> """
class Node:
    def __init__(self, val):
        self.val = val
        self.next = None

class LinkedList:
    def __init__(self):
        self.head = None
    
    def insert_head(self, val):
        node = Node(val)
        node.next = self.head
        self.head = node # O(1)
    
    def reverse(self):
        prev, curr = None, self.head
        while curr:
            nxt = curr.next
            curr.next = prev
            prev = curr
            curr = nxt
        self.head = prev
""".trimIndent()
        else -> """
class Node<T>(var data: T, var next: Node<T>? = null)

class LinkedList<T> {
    var head: Node<T>? = null
    
    fun insertHead(data: T) {
        val newNode = Node(data, head)
        head = newNode // O(1)
    }
    
    fun reverse() {
        var prev: Node<T>? = null
        var curr = head
        while (curr != null) {
            val next = curr.next
            curr.next = prev
            prev = curr
            curr = next
        }
        head = prev
    }
}
""".trimIndent()
    }

    private fun binaryTreeCode(lang: String): String = when (lang) {
        "python" -> """
class TreeNode:
    def __init__(self, val):
        self.val = val
        self.left = None
        self.right = None

def insert_bst(root, val):
    if not root:
        return TreeNode(val)
    if val < root.val:
        root.left = insert_bst(root.left, val)
    else:
        root.right = insert_bst(root.right, val)
    return root
""".trimIndent()
        else -> """
class TreeNode(var value: Int) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

fun insertBst(root: TreeNode?, value: Int): TreeNode {
    if (root == null) return TreeNode(value)
    if (value < root.value) {
        root.left = insertBst(root.left, value)
    } else {
        root.right = insertBst(root.right, value)
    }
    return root
}
""".trimIndent()
    }

    private fun avlTreeCode(lang: String): String = """
// AVL Self-Balancing Tree with Rotations
class AVLNode(var value: Int) {
    var height: Int = 1
    var left: AVLNode? = null
    var right: AVLNode? = null
}

fun rightRotate(y: AVLNode): AVLNode {
    val x = y.left!!
    val t2 = x.right
    x.right = y
    y.left = t2
    y.height = maxOf(y.left?.height ?: 0, y.right?.height ?: 0) + 1
    x.height = maxOf(x.left?.height ?: 0, x.right?.height ?: 0) + 1
    return x
}
""".trimIndent()

    private fun trieCode(lang: String): String = """
class TrieNode {
    val children = mutableMapOf<Char, TrieNode>()
    var isEndOfWord = false
}

class Trie {
    val root = TrieNode()
    
    fun insert(word: String) {
        var node = root
        for (char in word) {
            node = node.children.getOrPut(char) { TrieNode() }
        }
        node.isEndOfWord = true
    }
    
    fun search(word: String): Boolean {
        var node = root
        for (char in word) {
            node = node.children[char] ?: return false
        }
        return node.isEndOfWord
    }
}
""".trimIndent()

    private fun graphCode(lang: String): String = """
class Graph {
    private val adjList = mutableMapOf<Int, MutableList<Int>>()
    
    fun addEdge(u: Int, v: Int) {
        adjList.getOrPut(u) { mutableListOf() }.add(v)
        adjList.getOrPut(v) { mutableListOf() }.add(u)
    }
    
    fun bfs(start: Int): List<Int> {
        val visited = mutableSetOf<Int>()
        val queue = ArrayDeque<Int>()
        val result = mutableListOf<Int>()
        
        visited.add(start)
        queue.add(start)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            result.add(node)
            for (neighbor in adjList[node] ?: emptyList()) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    queue.add(neighbor)
                }
            }
        }
        return result
    }
}
""".trimIndent()

    private fun sortingCode(lang: String): String = """
fun bubbleSort(arr: IntArray) {
    val n = arr.size
    for (i in 0 until n - 1) {
        for (j in 0 until n - i - 1) {
            if (arr[j] > arr[j + 1]) {
                val temp = arr[j]
                arr[j] = arr[j + 1]
                arr[j + 1] = temp
            }
        }
    }
}
""".trimIndent()

    private fun arrayCode(lang: String): String = when (lang) {
        "python" -> """
# Contiguous Memory Buffer & Random Access
arr = [10, 20, 30, 40]
val = arr[2] # O(1) Address Math: base + 2 * size
arr.insert(2, 25) # O(n) Shifts elements right
arr.pop(2) # O(n) Shifts elements left
""".trimIndent()
        else -> """
// Array Contiguous Memory & Fast Access
val array = intArrayOf(10, 20, 30, 40)
val element = array[2] // O(1) direct address calculation
""".trimIndent()
    }
}
