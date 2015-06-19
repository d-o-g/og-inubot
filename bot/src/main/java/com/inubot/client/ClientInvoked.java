/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.client;

/**
 * Anything that is only to be called by the client and not us should be marked with this annotation.
 * i.e callbacks
 */
public @interface ClientInvoked {
}
