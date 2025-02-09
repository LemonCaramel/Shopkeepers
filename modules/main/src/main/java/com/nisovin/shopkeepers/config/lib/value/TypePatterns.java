package com.nisovin.shopkeepers.config.lib.value;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import com.nisovin.shopkeepers.util.java.Validate;

public class TypePatterns {

	public static TypePattern forClass(Class<?> clazz) {
		return new ClassTypePattern(clazz);
	}

	private static class ClassTypePattern implements TypePattern {

		private final Class<?> clazz;

		public ClassTypePattern(Class<?> clazz) {
			Validate.notNull(clazz, "clazz is null");
			this.clazz = clazz;
		}

		@Override
		public boolean matches(Type type) {
			if (this.clazz == type) return true;
			if (type instanceof ParameterizedType) {
				if (this.clazz == ((ParameterizedType) type).getRawType()) {
					return true;
				}
			}
			return false;
		}
	}

	public static TypePattern forBaseType(Class<?> baseType) {
		return new BaseTypePattern(baseType);
	}

	private static class BaseTypePattern implements TypePattern {

		private final Class<?> baseType;

		public BaseTypePattern(Class<?> baseType) {
			Validate.notNull(baseType, "baseType is null");
			this.baseType = baseType;
		}

		@Override
		public boolean matches(Type type) {
			Type actualType = type;
			if (type instanceof ParameterizedType) {
				actualType = ((ParameterizedType) type).getRawType();
			}
			if (!(actualType instanceof Class)) {
				return false;
			}
			Class<?> actualClass = (Class<?>) actualType;
			return this.baseType.isAssignableFrom(actualClass);
		}
	}

	public static TypePattern parameterized(Class<?> clazz, TypePattern... typeParameters) {
		return new ParameterizedTypePattern(clazz, typeParameters);
	}

	public static TypePattern parameterized(Class<?> clazz, Class<?>... typeParameters) {
		TypePattern[] typePatterns = null;
		if (typeParameters != null) {
			typePatterns = new TypePattern[typeParameters.length];
			for (int i = 0; i < typeParameters.length; ++i) {
				Class<?> typeParameter = typeParameters[i];
				Validate.notNull(typeParameter, "typeParameters contains null");
				typePatterns[i] = TypePatterns.forClass(typeParameter);
			}
		}
		// This results in an IllegalArgumentException if typeParameters is null:
		return parameterized(clazz, typePatterns);
	}

	private static class ParameterizedTypePattern extends ClassTypePattern {

		private final TypePattern[] typeParameters; // Not null

		public ParameterizedTypePattern(Class<?> clazz, TypePattern... typeParameters) {
			super(clazz);
			Validate.notNull(typeParameters, "typeParameters is null");
			Validate.isTrue(typeParameters.length > 0, "typeParameters is empty");
			this.typeParameters = typeParameters.clone();
		}

		@Override
		public boolean matches(Type type) {
			if (!super.matches(type)) return false;
			if (!(type instanceof ParameterizedType)) {
				return false;
			}
			Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
			if (typeArguments == null || typeArguments.length != this.typeParameters.length) {
				return false;
			}
			for (int i = 0; i < typeParameters.length; ++i) {
				if (!this.typeParameters[i].matches(typeArguments[i])) {
					return false;
				}
			}
			return true;
		}
	}

	public static TypePattern any() {
		return AnyTypePattern.INSTANCE;
	}

	/**
	 * Matches any type.
	 */
	private static class AnyTypePattern implements TypePattern {

		public static final AnyTypePattern INSTANCE = new AnyTypePattern();

		public AnyTypePattern() {
		}

		@Override
		public boolean matches(Type type) {
			return true;
		}
	}

	private TypePatterns() {
	}
}
