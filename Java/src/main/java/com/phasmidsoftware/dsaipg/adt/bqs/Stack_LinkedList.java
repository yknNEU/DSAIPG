/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.bqs;

import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.Objects;

/**
 * Stack_LinkedList is a generic implementation of the Stack interface backed by a linked list.
 * It provides operations to push, pop, and peek elements, as well as checking if the stack is empty.
 * This class leverages LinkedList's functionality to handle stack operations.
 *
 * @param <Item> the type of elements stored in this stack
 */
public class Stack_LinkedList<Item> implements Stack<Item> {

    /**
     * push method, delegates to list as add.
     *
     * @param item the item to push.
     */
    public void push(Item item) {
        list.add(item);
    }

    /**
     * pop method, delegates to list as remove
     *
     * @return the item on the top of this stack
     * @throws BQSException if this stack is empty
     */
    public Item pop() throws BQSException {
        return list.remove();
    }

    /**
     * peek method, delegates to list as getHead
     *
     * @return the value at the top of the stack (no change is made to the stack). Result may be null
     */
    public Item peek() {
        return list.getHead();
    }

    /**
     * isEmpty method, delegates to list.
     *
     * @return true if this stack is empty
     */
    public boolean isEmpty() {
        return list.isEmpty();
    }

    /**
     * Returns an iterator over elements of type {@code T}.
     *
     * @return an Iterator.
     */
    @NotNull
    public Iterator<Item> iterator() {
        return list.iterator();
    }

    /**
     * Compares this Stack_LinkedList instance with the specified object for equality.
     * Two Stack_LinkedList instances are considered equal if their underlying lists are equal.
     *
     * @param o the object to compare this Stack_LinkedList instance to
     * @return true if the specified object is equal to this Stack_LinkedList instance, otherwise false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stack_LinkedList<?> that)) return false;
        return list.equals(that.list);
    }

    /**
     * Computes the hash code for this stack based on its underlying linked list.
     * The hash code is used to support efficient storage and retrieval in hash-based collections.
     *
     * @return the hash code value for this stack instance
     */
    @Override
    public int hashCode() {
        return Objects.hash(list);
    }

    /**
     * Generates a string representation of the Stack_LinkedList instance.
     * The string includes the representation of the underlying linked list
     * used to implement the stack.
     *
     * @return a string representation of this Stack_LinkedList instance
     */
    @Override
    public String toString() {
        return "Stack_LinkedList{" +
                "list=" + list +
                '}';
    }

    /**
     * Secondary, but sole public, constructor.
     */
    public Stack_LinkedList() {
        this(new LinkedList_Elements<>());
    }

    /**
     * Primary, but package-private, constructor.
     */
    Stack_LinkedList(LinkedList<Item> list) {
        this.list = list;
    }

    /**
     * Represents the underlying linked list used to implement the stack operations.
     * This field holds the elements of the stack in a linked list structure.
     * It supports efficient operations for adding, removing, and accessing elements at the head of the list.
     * The linked list ensures that the stack operations (push, pop, peek, isEmpty) can be performed efficiently.
     */
    private final LinkedList<Item> list;
}