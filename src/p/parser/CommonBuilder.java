package p.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

public class CommonBuilder {

    Object root;
    Stack<Object> structures = new Stack();

    public void createRootMap() {
        root = new HashMap<>();
        structures.push(root);
    }

    public void createRootList() {
        root = new ArrayList<>();
        structures.push(root);
    }

    public void pushArrayArrayElement() {
        List element = new ArrayList();
        ((ArrayList) structures.peek()).add(element);
        structures.push(element);
    }
    
    public void pushArrayMapElement() {
        HashMap element = new HashMap();
        structures.push(element);
        ((ArrayList) structures.peek()).add(element);
    }
    

    public void pushArrayElement(Object element) {
        ((ArrayList) structures.peek()).add(element);
    }

    public void popStructure() {
        structures.pop();
    }

    public Object getProduct() {
        return root;
    }
}
