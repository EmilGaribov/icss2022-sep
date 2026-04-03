package nl.han.ica.datastructures;

public class LinkedListNode<T> {
    private LinkedListNode<T> next;
    private final T value;

    public LinkedListNode(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setNext(LinkedListNode<T> next) {
        this.next = next;
    }

    public LinkedListNode<T> getNext() {
        return next;
    }

}