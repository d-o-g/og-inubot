/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.exceptions;

/**
 * A unique exception for the GrandExchange class that signifies that
 * a 'grand-exchange-open dependant' logic was attempted when the grand-exchange
 * was closed, an explicit default return could not be made.
 */
public class ExchangeClosedException extends IllegalStateException {

    public ExchangeClosedException() {
        super("The exchange interface is closed!");
    }

}
