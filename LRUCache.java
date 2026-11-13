import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class LRUCache {

    // Each node stores one cache entry.
    private static class Node {
        int key;
        String value;
        Node previous;
        Node next;

        Node(int key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache;

    // Dummy nodes simplify insertion and removal.
    // Most recently used entries stay near head.
    // Least recently used entries stay near tail.
    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException(
                    "Cache capacity must be greater than zero."
            );
        }

        this.capacity = capacity;
        this.cache = new HashMap<>();

        head = new Node(0, "");
        tail = new Node(0, "");

        head.next = tail;
        tail.previous = head;
    }

    public String get(int key) {
        Node node = cache.get(key);

        if (node == null) {
            return null;
        }

        // The entry was accessed, so it becomes most recently used.
        moveToFront(node);
        return node.value;
    }

    public void put(int key, String value) {
        Node existingNode = cache.get(key);

        if (existingNode != null) {
            // Update the existing entry.
            existingNode.value = value;
            moveToFront(existingNode);
            return;
        }

        Node newNode = new Node(key, value);

        cache.put(key, newNode);
        addToFront(newNode);

        // Remove the least recently used entry if capacity is exceeded.
        if (cache.size() > capacity) {
            Node leastRecentlyUsed = removeLast();
            cache.remove(leastRecentlyUsed.key);

            System.out.println(
                    "Evicted key: " + leastRecentlyUsed.key
            );
        }
    }

    public boolean remove(int key) {
        Node node = cache.get(key);

        if (node == null) {
            return false;
        }

        removeNode(node);
        cache.remove(key);
        return true;
    }

    public int size() {
        return cache.size();
    }

    public void display() {
        if (cache.isEmpty()) {
            System.out.println("Cache is empty.");
            return;
        }

        System.out.println("Most recently used -> Least recently used");

        Node current = head.next;

        while (current != tail) {
            System.out.println(current.key + " = " + current.value);
            current = current.next;
        }
    }

    private void addToFront(Node node) {
        node.previous = head;
        node.next = head.next;

        head.next.previous = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.previous.next = node.next;
        node.next.previous = node.previous;
    }

    private void moveToFront(Node node) {
        removeNode(node);
        addToFront(node);
    }

    private Node removeLast() {
        Node lastNode = tail.previous;
        removeNode(lastNode);
        return lastNode;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            System.out.print("Enter cache capacity: ");
            int capacity = Integer.parseInt(scanner.nextLine());

            LRUCache lruCache = new LRUCache(capacity);

            System.out.println("\nAvailable commands:");
            System.out.println("PUT <key> <value>");
            System.out.println("GET <key>");
            System.out.println("REMOVE <key>");
            System.out.println("DISPLAY");
            System.out.println("SIZE");
            System.out.println("EXIT");

            while (true) {
                System.out.print("\n> ");
                String input = scanner.nextLine().trim();

                if (input.isEmpty()) {
                    continue;
                }

                String[] parts = input.split("\\s+", 3);
                String command = parts[0].toUpperCase();

                try {
                    switch (command) {
                        case "PUT":
                            if (parts.length < 3) {
                                System.out.println(
                                        "Usage: PUT <key> <value>"
                                );
                                break;
                            }

                            int putKey = Integer.parseInt(parts[1]);
                            lruCache.put(putKey, parts[2]);
                            System.out.println("Entry stored.");
                            break;

                        case "GET":
                            if (parts.length < 2) {
                                System.out.println("Usage: GET <key>");
                                break;
                            }

                            int getKey = Integer.parseInt(parts[1]);
                            String value = lruCache.get(getKey);

                            if (value == null) {
                                System.out.println("Key not found.");
                            } else {
                                System.out.println("Value: " + value);
                            }
                            break;

                        case "REMOVE":
                            if (parts.length < 2) {
                                System.out.println("Usage: REMOVE <key>");
                                break;
                            }

                            int removeKey = Integer.parseInt(parts[1]);
                            boolean removed = lruCache.remove(removeKey);

                            System.out.println(
                                    removed ? "Entry removed." : "Key not found."
                            );
                            break;

                        case "DISPLAY":
                            lruCache.display();
                            break;

                        case "SIZE":
                            System.out.println(
                                    "Current size: " + lruCache.size()
                            );
                            break;

                        case "EXIT":
                            System.out.println("Exiting cache.");
                            return;

                        default:
                            System.out.println("Unknown command.");
                    }
                } catch (NumberFormatException exception) {
                    System.out.println("The key must be an integer.");
                }
            }
        } catch (NumberFormatException exception) {
            System.out.println("Capacity must be an integer.");
        } catch (IllegalArgumentException exception) {
            System.out.println(exception.getMessage());
        } finally {
            scanner.close();
        }
    }
}