package com.nisovin.shopkeepers.util.data.property;

/**
 * Converts objects to {@link String} representations.
 *
 * @param <T>
 *            the type of object being converted
 */
@FunctionalInterface
public interface StringConverter<T> {

	/**
	 * A {@link StringConverter} that uses {@link String#valueOf(Object)} to convert the given objects to Strings.
	 */
	public static final StringConverter<Object> DEFAULT = String::valueOf;

	/**
	 * Converts the given object to a String.
	 * 
	 * @param object
	 *            the object, can be <code>null</code>
	 * @return the String representation, not <code>null</code>
	 */
	public String toString(T object);
}
