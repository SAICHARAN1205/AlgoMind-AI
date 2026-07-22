export const algorithmCodeMap = {
  'Bubble Sort': `void bubbleSort(int arr[]) {
    for (int i = 0; i < n - 1; i++) {
        for (int j = 0; j < n - i - 1; j++) {
            if (arr[j] > arr[j + 1]) swap(arr[j], arr[j + 1]);
        } // End of inner loop pass
        if (!swapped) break; // Early exit
    }
}`,
  'Binary Search': `int low = 0, high = n - 1;
while (low <= high) {
    int mid = low + (high - low) / 2;
    if (arr[mid] == target) {
        return mid; // Found!
    } else if (arr[mid] < target) {
        low = mid + 1;
    } else { high = mid - 1; }
}
return -1; // Not found`,
  'Merge Sort': `void mergeSort(int arr[], int l, int r) {
    if (l >= r) return;
    int m = l + (r - l) / 2;
    mergeSort(arr, l, m);
    mergeSort(arr, m + 1, r);
    merge(arr, l, m, r);
}`,
  'Breadth First Search (BFS)': `void bfs(Node start) {
    Queue<Node> queue = new LinkedList<>();
    Set<Node> visited = new HashSet<>();
    
    queue.add(start);
    visited.add(start);
    
    while (!queue.isEmpty()) {
        Node current = queue.poll();
        visit(current);
        
        for (Node neighbor : current.neighbors) {
            if (!visited.contains(neighbor)) {
                visited.add(neighbor);
                queue.add(neighbor);
            }
        }
    }
}`,
  'Depth First Search (DFS)': `void dfs(Node node, Set<Node> visited) {
    visited.add(node);
    visit(node);
    
    for (Node neighbor : node.neighbors) {
        if (!visited.contains(neighbor)) {
            dfs(neighbor, visited);
        }
    }
}`,
  'Dijkstra’s Algorithm': `void dijkstra(Node start) {
    PriorityQueue<Node> pq = new PriorityQueue<>();
    distance[start] = 0;
    pq.add(start);
    
    while (!pq.isEmpty()) {
        Node u = pq.poll();
        if (visited[u]) continue;
        visited[u] = true;
        
        for (Edge edge : u.edges) {
            Node v = edge.to;
            if (distance[u] + edge.weight < distance[v]) {
                distance[v] = distance[u] + edge.weight;
                pq.add(v);
            }
        }
    }
}`,
  'Stack Operations': `class Stack {
    void push(int data) {
        top = new Node(data, top);
    }
    
    int pop() {
        if (isEmpty()) throw EmptyStackException;
        int data = top.data;
        top = top.next;
        return data;
    }
    
    int peek() {
        if (isEmpty()) throw EmptyStackException;
        return top.data;
    }
}`,
  'Queue Operations': `class Queue {
    void enqueue(int data) {
        Node node = new Node(data);
        if (rear != null) rear.next = node;
        rear = node;
        if (front == null) front = rear;
    }
    
    int dequeue() {
        if (isEmpty()) throw EmptyQueueException;
        int data = front.data;
        front = front.next;
        if (front == null) rear = null;
        return data;
    }
}`,
  'Binary Search Tree (BST)': `class BST {
    Node insert(Node node, int val) {
        if (node == null) return new Node(val);
        if (val < node.val) node.left = insert(node.left, val);
        else if (val > node.val) node.right = insert(node.right, val);
        return node;
    }
    
    Node search(Node node, int val) {
        if (node == null || node.val == val) return node;
        if (val < node.val) return search(node.left, val);
        return search(node.right, val);
    }
}`,
  'Inorder Traversal': `void inorder(Node node) {
    if (node == null) return;
    inorder(node.left);
    visit(node);
    inorder(node.right);
}`,
  'Preorder Traversal': `void preorder(Node node) {
    if (node == null) return;
    visit(node);
    preorder(node.left);
    preorder(node.right);
}`,
  'Postorder Traversal': `void postorder(Node node) {
    if (node == null) return;
    postorder(node.left);
    postorder(node.right);
    visit(node);
}`
};

export const FIB_RECURSIVE_CODE = `int fib(int n) {
    if (n <= 1) return n;
    return fib(n - 1) + fib(n - 2);
}`;

export const FIB_MEMOIZED_CODE = `int fib(int n, int[] memo) {
    if (memo[n] != -1) return memo[n];
    if (n <= 1) return n;
    memo[n] = fib(n - 1, memo) + fib(n - 2, memo);
    return memo[n];
}`;

export const FIB_BOTTOM_UP_CODE = `int fib(int n) {
    int[] dp = new int[n + 1];
    dp[0] = 0;
    if (n > 0) dp[1] = 1;
    for (int i = 2; i <= n; i++) {
        dp[i] = dp[i - 1] + dp[i - 2];
    }
    return dp[n];
}`;
