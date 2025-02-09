package com.nisovin.shopkeepers.util.data.property.validation.java;

import com.nisovin.shopkeepers.util.data.property.Property;
import com.nisovin.shopkeepers.util.data.property.validation.PropertyValidator;
import com.nisovin.shopkeepers.util.java.Validate;

/**
 * Default {@link PropertyValidator}s for {@link String} values.
 */
public final class StringValidators {

	/**
	 * A {@link PropertyValidator} that ensures that the validated String is not {@link String#isEmpty() empty}.
	 */
	public static final PropertyValidator<String> NON_EMPTY = new PropertyValidator<String>() {
		@Override
		public void validate(Property<? extends String> property, String value) {
			Validate.isTrue(!value.isEmpty(), "String is empty!");
		}
	};

	private StringValidators() {
	}
}
