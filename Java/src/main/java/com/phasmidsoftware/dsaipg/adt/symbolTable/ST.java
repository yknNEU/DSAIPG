/*
 * Copyright (c) 2017. Phasmid Software
 */

package com.phasmidsoftware.dsaipg.adt.symbolTable;

/**
 * Interface to model a symbol table.
 * <p>
 * This is similar but different to the java.util.Dictionary interface.
 *
 * @param <Key>   key type.
 * @param <Value> value type.
 */
public interface ST<Key, Value> extends ImmutableSymbolTable<Key, Value> {

    /**
     * Insert a key/value pair.
     * If the key already exists, then its value will simply be overwritten.
     *
     * @param key the key.
     * @param val the value.
     * @return the original value, if any, otherwise null.
     */
    Value put(Key key, Value val);

    /**
     * Delete a key.
     *
     * @param key the key.
     * @return the original value, if any, otherwise null.
     */
    Value delete(Key key);

}