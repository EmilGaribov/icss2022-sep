package nl.han.ica.datastructures;

import java.util.Iterator;

public class HANLinkedList<T> implements IHANLinkedList<T>{
    private LinkedListNode<T> first = null;

    @Override
    public void addFirst(T value) {
        if (first == null) {
            first = new LinkedListNode<>(value);
        } else {
            LinkedListNode<T> toAdd = new LinkedListNode<>(value);
            toAdd.setNext(first);
            first = toAdd;
        }
    }

    @Override
    public void clear() {
        first = null;
    }

    @Override
    public void insert(int index, T value) {
        LinkedListNode<T> temp = new LinkedListNode<>(value);
        LinkedListNode<T> current = first;
        if (first == null) {
            first = temp;
            return;
        }
        if (index == 0) {
            temp.setNext(current);
            first = temp;
            return;
        }
        if (index > getSize()) {
            return;
        }
        int count = 0;
        while (count != index - 1) {
            current = current.getNext();
            count++;
        }
        temp.setNext(current.getNext());
        current.setNext(temp);
    }

    @Override
    public void delete(int pos) {
        if (pos == 0) {
            removeFirst();
            return;
        }
        if (pos > getSize()) {
            return;
        }
        LinkedListNode<T> current = first;
        int count = 0;
        while (count != pos - 1) {
            current = current.getNext();
            count++;
        }
        LinkedListNode<T> toRemove = current.getNext();
        current.setNext(toRemove.getNext());
    }

    @Override
    public T get(int pos) {
        Iterator<T> iterator = iterator();
        for (int i = 0; i < pos; i++) {
            iterator.next();
        }
        return iterator.next();
    }

    @Override
    public void removeFirst() {
        first = first.getNext();
    }

    @Override
    public T getFirst() {
        return first.getValue();
    }

    @Override
    public int getSize() {
        int size = 0;
        Iterator<T> iterator = iterator();

        while(iterator.hasNext()) {
            size++;
            iterator.next();
        }
        return size;
    }

    @Override
    public Iterator<T> iterator() {
        return new LinkedListIterator<>(first);
    }
}
