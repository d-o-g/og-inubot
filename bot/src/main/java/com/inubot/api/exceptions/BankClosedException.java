/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.exceptions;

/**
 * @author unsigned
 * @since 01-05-2015
 */
/**
 * A unique exception for the bank class that signifies that
 * a 'bank-open dependant' logic was attempted when the bank
 * was closed, an explicit default return could not be made.
 */
public class BankClosedException extends IllegalStateException {

    public BankClosedException() {
        super("The bank is closed!");
    }

}
