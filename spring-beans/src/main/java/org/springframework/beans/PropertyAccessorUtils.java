/*
 * Copyright 2002-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.beans;

/**
 * Utility methods for classes that perform bean property access
 * according to the {@link PropertyAccessor} interface.
 * *************************************************************
 * ~$ 实用程序类的方法执行bean属性访问根据{@link PropertyAccessor }接口.
 * @author Juergen Hoeller
 * @since 1.2.6
 */
public abstract class PropertyAccessorUtils {

	/**
	 * Return the actual property name for the given property path.
	 * ************************************************************
	 * ~$ 返回给定属性的实际属性名的路径.
	 * @param propertyPath the property path to determine the property name
	 * for (can include property keys, for example for specifying a map entry)
	 * @return the actual property name, without any key elements
	 */
	public static String getPropertyName(String propertyPath) {
		int separatorIndex = (propertyPath.endsWith(PropertyAccessor.PROPERTY_KEY_SUFFIX) ?
				propertyPath.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR) : -1);
		return (separatorIndex != -1 ? propertyPath.substring(0, separatorIndex) : propertyPath);
	}

	/**
	 * Check whether the given property path indicates an indexed or nested property.
	 * ******************************************************************************
	 * ~$ 检查给定属性的路径是否表明索引或嵌套的属性.
	 * @param propertyPath the property path to check
	 * @return whether the path indicates an indexed or nested property
	 */
	public static boolean isNestedOrIndexedProperty(String propertyPath) {
		if (propertyPath == null) {
			return false;
		}
		for (int i = 0; i < propertyPath.length(); i++) {
			char ch = propertyPath.charAt(i);
			if (ch == PropertyAccessor.NESTED_PROPERTY_SEPARATOR_CHAR ||
					ch == PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Determine the first nested property separator in the
	 * given property path, ignoring dots in keys (like "map[my.key]").
	 * ****************************************************************
	 * ~$ 确定第一个嵌套的属性在给定属性路径分隔符,忽略点钥匙(like "map[my.key]").
	 * @param propertyPath the property path to check
	 * @return the index of the nested property separator, or -1 if none
	 */
	public static int getFirstNestedPropertySeparatorIndex(String propertyPath) {
		return getNestedPropertySeparatorIndex(propertyPath, false);
	}

	/**
	 * Determine the first nested property separator in the
	 * given property path, ignoring dots in keys (like "map[my.key]").
	 * ****************************************************************
	 * ~$ 确定第一个嵌套的属性在给定属性路径分隔符,忽略点钥匙(like "map[my.key]").
	 * @param propertyPath the property path to check
	 * @return the index of the nested property separator, or -1 if none
	 */
	public static int getLastNestedPropertySeparatorIndex(String propertyPath) {
		return getNestedPropertySeparatorIndex(propertyPath, true);
	}

	/**
	 * Determine the first (or last) nested property separator in the
	 * given property path, ignoring dots in keys (like "map[my.key]").
	 * @param propertyPath the property path to check
	 * @param last whether to return the last separator rather than the first
	 * @return the index of the nested property separator, or -1 if none
	 */
	private static int getNestedPropertySeparatorIndex(String propertyPath, boolean last) {
		boolean inKey = false;
		int length = propertyPath.length();
		int i = (last ? length - 1 : 0);
		while (last ? i >= 0 : i < length) {
			switch (propertyPath.charAt(i)) {
				case PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR:
				case PropertyAccessor.PROPERTY_KEY_SUFFIX_CHAR:
					inKey = !inKey;
					break;
				case PropertyAccessor.NESTED_PROPERTY_SEPARATOR_CHAR:
					if (!inKey) {
						return i;
					}
			}
			if (last) {
				i--;
			}
			else {
				i++;
			}
		}
		return -1;
	}

	/**
	 * Determine whether the given registered path matches the given property path,
	 * either indicating the property itself or an indexed element of the property.
	 * ****************************************************************************
	 * ~$ 确定给定的路径匹配给定属性路径注册,表明property 本身或索引元素的属性.
	 * @param propertyPath the property path (typically without index)
	 * @param registeredPath the registered path (potentially with index)
	 * @return whether the paths match
	 */
	public static boolean matchesProperty(String registeredPath, String propertyPath) {
		if (!registeredPath.startsWith(propertyPath)) {
			return false;
		}
		if (registeredPath.length() == propertyPath.length()) {
			return true;
		}
		if (registeredPath.charAt(propertyPath.length()) != PropertyAccessor.PROPERTY_KEY_PREFIX_CHAR) {
			return false;
		}
		return (registeredPath.indexOf(PropertyAccessor.PROPERTY_KEY_SUFFIX_CHAR, propertyPath.length() + 1) ==
				registeredPath.length() - 1);
	}

	/**
	 * Determine the canonical name for the given property path.
	 * Removes surrounding quotes from map keys:<br>
	 * <code>map['key']</code> -> <code>map[key]</code><br>
	 * <code>map["key"]</code> -> <code>map[key]</code>
	 * *********************************************************
	 * ~$ 确定给定属性路径的规范名称.
	 *    删除周围引用映射键:
	 * @param propertyName the bean property path
	 * @return the canonical representation of the property path
	 */
	public static String canonicalPropertyName(String propertyName) {
		if (propertyName == null) {
			return "";
		}

		StringBuilder sb = new StringBuilder(propertyName);
		int searchIndex = 0;
		while (searchIndex != -1) {
			int keyStart = sb.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX, searchIndex);
			searchIndex = -1;
			if (keyStart != -1) {
				int keyEnd = sb.indexOf(
						PropertyAccessor.PROPERTY_KEY_SUFFIX, keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length());
				if (keyEnd != -1) {
					String key = sb.substring(keyStart + PropertyAccessor.PROPERTY_KEY_PREFIX.length(), keyEnd);
					if ((key.startsWith("'") && key.endsWith("'")) || (key.startsWith("\"") && key.endsWith("\""))) {
						sb.delete(keyStart + 1, keyStart + 2);
						sb.delete(keyEnd - 2, keyEnd - 1);
						keyEnd = keyEnd - 2;
					}
					searchIndex = keyEnd + PropertyAccessor.PROPERTY_KEY_SUFFIX.length();
				}
			}
		}
		return sb.toString();
	}

	/**
	 * Determine the canonical names for the given property paths.
	 * @param propertyNames the bean property paths (as array)
	 * @return the canonical representation of the property paths
	 * (as array of the same size)
	 * @see #canonicalPropertyName(String)
	 */
	public static String[] canonicalPropertyNames(String[] propertyNames) {
		if (propertyNames == null) {
			return null;
		}
		String[] result = new String[propertyNames.length];
		for (int i = 0; i < propertyNames.length; i++) {
			result[i] = canonicalPropertyName(propertyNames[i]);
		}
		return result;
	}

}
