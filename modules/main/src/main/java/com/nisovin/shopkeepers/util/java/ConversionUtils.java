package com.nisovin.shopkeepers.util.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class ConversionUtils {

	public static final Map<String, Boolean> BOOLEAN_VALUES;

	static {
		// Initialize boolean values:
		Map<String, Boolean> booleanValues = new HashMap<>();
		booleanValues.put("true", true);
		booleanValues.put("t", true);
		booleanValues.put("1", true);
		booleanValues.put("yes", true);
		booleanValues.put("y", true);
		booleanValues.put("on", true);
		booleanValues.put("enabled", true);

		booleanValues.put("false", false);
		booleanValues.put("f", false);
		booleanValues.put("0", false);
		booleanValues.put("no", false);
		booleanValues.put("n", false);
		booleanValues.put("off", false);
		booleanValues.put("disabled", false);

		BOOLEAN_VALUES = Collections.unmodifiableMap(booleanValues);
	}

	public static Integer parseInt(String string) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Long parseLong(String string) {
		try {
			return Long.parseLong(string);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Double parseDouble(String string) {
		if (string == null) return null;
		try {
			return Double.parseDouble(string);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Float parseFloat(String string) {
		if (string == null) return null;
		try {
			return Float.parseFloat(string);
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static Boolean parseBoolean(String string) {
		if (string == null) return null;
		return BOOLEAN_VALUES.get(string.toLowerCase(Locale.ROOT));
	}

	public static UUID parseUUID(String string) {
		if (string == null) return null;
		if (string.length() == 32) {
			// Possibly flat uuid, insert '-':
			string = string.substring(0, 8)
					+ "-"
					+ string.substring(8, 12)
					+ "-"
					+ string.substring(12, 16)
					+ "-"
					+ string.substring(16, 20)
					+ "-"
					+ string.substring(20, 32);
		}
		if (string.length() == 36) {
			try {
				return UUID.fromString(string);
			} catch (IllegalArgumentException e) {
				// Not a valid uuid.
			}
		}
		return null;
	}

	public static <E extends Enum<E>> E parseEnum(Class<E> enumType, String enumName) {
		Validate.notNull(enumType, "enumType is null");
		if (enumName == null) return null;

		// Try to parse the enum value without normalizing the enum name first (in case the enum does not adhere to the
		// expected normalized format):
		E enumValue = EnumUtils.valueOf(enumType, enumName);
		if (enumValue != null) return enumValue;

		// Try with enum name normalization:
		enumName = EnumUtils.normalizeEnumName(enumName);
		return EnumUtils.valueOf(enumType, enumName); // Returns null if parsing fails
	}

	// PARSE LISTS:

	public static List<Integer> parseIntList(Collection<String> strings) {
		List<Integer> result = new ArrayList<>(strings.size());
		if (strings != null) {
			for (String string : strings) {
				Integer value = parseInt(string);
				if (value != null) {
					result.add(value);
				}
			}
		}
		return result;
	}

	public static List<Long> parseLongList(Collection<String> strings) {
		List<Long> result = new ArrayList<>(strings.size());
		if (strings != null) {
			for (String string : strings) {
				Long value = parseLong(string);
				if (value != null) {
					result.add(value);
				}
			}
		}
		return result;
	}

	public static List<Double> parseDoubleList(Collection<String> strings) {
		List<Double> result = new ArrayList<>(strings.size());
		if (strings != null) {
			for (String string : strings) {
				Double value = parseDouble(string);
				if (value != null) {
					result.add(value);
				}
			}
		}
		return result;
	}

	public static List<Float> parseFloatList(Collection<String> strings) {
		List<Float> result = new ArrayList<>(strings.size());
		if (strings != null) {
			for (String string : strings) {
				Float value = parseFloat(string);
				if (value != null) {
					result.add(value);
				}
			}
		}
		return result;
	}

	public static <E extends Enum<E>> List<E> parseEnumList(Class<E> enumType, Collection<String> strings) {
		List<E> result = new ArrayList<>(strings.size());
		if (strings != null) {
			for (String string : strings) {
				E value = parseEnum(enumType, string);
				if (value != null) {
					result.add(value);
				}
			}
		}
		return result;
	}

	// CONVERT OBJECTS:

	public static String toString(Object object) {
		return (object != null) ? object.toString() : null;
	}

	public static Boolean toBoolean(Object object) {
		if (object instanceof Boolean) {
			return (Boolean) object;
		} else if (object instanceof Number) {
			int i = ((Number) object).intValue();
			if (i == 1) {
				return Boolean.TRUE;
			} else if (i == 0) {
				return Boolean.FALSE;
			}
		} else if (object instanceof String) {
			return parseBoolean((String) object);
		}
		return null;
	}

	public static Integer toInteger(Object object) {
		if (object instanceof Integer) {
			return (Integer) object;
		} else if (object instanceof Number) {
			return ((Number) object).intValue();
		} else if (object instanceof String) {
			return parseInt((String) object);
		}
		return null;
	}

	public static Long toLong(Object object) {
		if (object instanceof Long) {
			return (Long) object;
		} else if (object instanceof Number) {
			return ((Number) object).longValue();
		} else if (object instanceof String) {
			return parseLong((String) object);
		}
		return null;
	}

	public static Double toDouble(Object object) {
		if (object instanceof Double) {
			return (Double) object;
		} else if (object instanceof Number) {
			return ((Number) object).doubleValue();
		} else if (object instanceof String) {
			return parseDouble((String) object);
		}
		return null;
	}

	public static Float toFloat(Object object) {
		if (object instanceof Float) {
			return (Float) object;
		} else if (object instanceof Number) {
			return ((Number) object).floatValue();
		} else if (object instanceof String) {
			return parseFloat((String) object);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <E extends Enum<E>> E toEnum(Class<E> enumType, Object object) {
		if (enumType.isInstance(object)) return (E) object;
		// Note: We only expect objects of type String to be valid here. We do not convert the object to a String if it
		// is not already a String.
		if (object instanceof String) {
			String enumName = (String) object;
			return parseEnum(enumType, enumName);
		}
		return null;
	}

	// CONVERT LISTS OF OBJECTS:

	public static List<Integer> toIntegerList(List<?> list) {
		if (list == null) return null;
		List<Integer> result = new ArrayList<>(list.size());
		for (Object value : list) {
			Integer integerValue = toInteger(value);
			if (integerValue != null) {
				result.add(integerValue);
			}
		}
		return result;
	}

	public static List<Double> toDoubleList(List<?> list) {
		if (list == null) return null;
		List<Double> result = new ArrayList<>(list.size());
		for (Object value : list) {
			Double doubleValue = toDouble(value);
			if (doubleValue != null) {
				result.add(doubleValue);
			}
		}
		return result;
	}

	public static List<Float> toFloatList(List<?> list) {
		if (list == null) return null;
		List<Float> result = new ArrayList<>(list.size());
		for (Object value : list) {
			Float floatValue = toFloat(value);
			if (floatValue != null) {
				result.add(floatValue);
			}
		}
		return result;
	}

	public static List<Long> toLongList(List<?> list) {
		if (list == null) return null;
		List<Long> result = new ArrayList<>(list.size());
		for (Object value : list) {
			Long longValue = toLong(value);
			if (longValue != null) {
				result.add(longValue);
			}
		}
		return result;
	}

	public static List<Boolean> toBooleanList(List<?> list) {
		if (list == null) return null;
		List<Boolean> result = new ArrayList<>(list.size());
		for (Object value : list) {
			Boolean booleanValue = toBoolean(value);
			if (booleanValue != null) {
				result.add(booleanValue);
			}
		}
		return result;
	}

	public static List<String> toStringList(List<?> list) {
		if (list == null) return null;
		List<String> result = new ArrayList<>(list.size());
		for (Object value : list) {
			String stringValue = toString(value);
			if (stringValue != null) {
				result.add(stringValue);
			}
		}
		return result;
	}

	public static <E extends Enum<E>> List<E> toEnumList(Class<E> enumType, List<?> list) {
		if (list == null) return null;
		List<E> result = new ArrayList<>(list.size());
		for (Object value : list) {
			E enumValue = toEnum(enumType, value);
			if (enumValue != null) {
				result.add(enumValue);
			}
		}
		return result;
	}
}
