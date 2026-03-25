package nl.han.ica.icss.parser;

import nl.han.ica.datastructures.IHANStack;
import java.util.ArrayList;

public class HANStack<T> implements IHANStack<T> {

    private ArrayList<T> list;

    public HANStack() {
        list = new ArrayList<>();
    }

    @Override
    public void push(Object value) {
        list.add((T) value);
    }

    @Override
    public T pop() {
        if (list.isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return list.remove(list.size() - 1);
    }

    @Override
    public T peek() {
        if (list.isEmpty()) {
            throw new RuntimeException("Stack is empty");
        }
        return list.get(list.size() - 1);
    }
}