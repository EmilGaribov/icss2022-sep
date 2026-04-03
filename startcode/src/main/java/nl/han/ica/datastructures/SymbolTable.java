package nl.han.ica.datastructures;

import nl.han.ica.icss.ast.types.ExpressionType;
import java.util.HashMap;
import java.util.Iterator;

public class SymbolTable {
    private final IHANLinkedList<HashMap<String, ExpressionType>> scopes;

    public SymbolTable() {
        this.scopes = new HANLinkedList<>();
    }

    public void pushScope() {
        scopes.addFirst(new HashMap<>());
    }

    public void popScope() {
        if (scopes.getSize() > 0) {
            scopes.removeFirst();
        }
    }

    public void add(String name, ExpressionType type) {
        if (scopes.getSize() > 0) {
            scopes.getFirst().put(name, type);
        }
    }

    public ExpressionType get(String name) {
        Iterator<HashMap<String, ExpressionType>> it = scopes.iterator();
        while (it.hasNext()) {
            HashMap<String, ExpressionType> currentScope = it.next();
            if (currentScope.containsKey(name)) {
                return currentScope.get(name);
            }
        }
        return null; // Variabele niet gevonden in de actieve scopes
    }
}