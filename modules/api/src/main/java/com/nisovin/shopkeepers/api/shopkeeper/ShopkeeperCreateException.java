package com.nisovin.shopkeepers.api.shopkeeper;

/**
 * This exception is thrown when a shopkeeper cannot be created, or recreated from its save data.
 */
public class ShopkeeperCreateException extends Exception {

	private static final long serialVersionUID = -2026963951805397944L;

	/**
	 * Creates a new {@link ShopkeeperCreateException}.
	 * 
	 * @param message
	 *            the detail message
	 */
	public ShopkeeperCreateException(String message) {
		super(message);
	}

	/**
	 * Creates a new {@link ShopkeeperCreateException}.
	 * 
	 * @param message
	 *            the detail message
	 * @param cause
	 *            the cause
	 */
	public ShopkeeperCreateException(String message, Throwable cause) {
		super(message, cause);
	}
}
