/**
 * Enumeration for the permissions users can have for of items, and bank accounts (or anything
 * else that can be accessed by a user)
 */
package edu.unh.cs.cs619.bulletzone.datalayer;

public enum Permission {
    Invalid, Owner, Add, Remove, Use, Transfer;
}
