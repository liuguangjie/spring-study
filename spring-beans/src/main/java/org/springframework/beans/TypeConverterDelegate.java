/*
 * Copyright 2002-2011 the original author or authors.
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

import java.beans.PropertyEditor;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.CollectionFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * Internal helper class for converting property values to target types.
 *
 * <p>Works on a given {@link PropertyEditorRegistrySupport} instance.
 * Used as a delegate by {@link BeanWrapperImpl} and {@link SimpleTypeConverter}.
 * *******************************************************************************
 * ~$ 内部的助手类属性值转换成目标类型.
 * <p>适用于一个给定的{@link PropertyEditorRegistrySupport }实例.
 *    作为一个委托由{@link BeanWrapperImpl }和{@link SimpleTypeConverter }.
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 2.0
 * @see BeanWrapperImpl
 * @see SimpleTypeConverter
 */
class TypeConverterDelegate {

	private static final Log logger = LogFactory.getLog(TypeConverterDelegate.class);

	private final PropertyEditorRegistrySupport propertyEditorRegistry;

	private final Object targetObject;


	/**
	 * Create a new TypeConverterDelegate for the given editor registry.
	 * *****************************************************************
	 * ~$ 创建一个新的TypeConverterDelegate给定编辑注册表.
	 * @param propertyEditorRegistry the editor registry to use
	 */
	public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry) {
		this(propertyEditorRegistry, null);
	}

	/**
	 * Create a new TypeConverterDelegate for the given editor registry and bean instance.
	 * ***********************************************************************************
	 * ~$ 创建一个新的TypeConverterDelegate给定编辑注册表和bean实例.
	 * @param propertyEditorRegistry the editor registry to use
	 *                               ~$   编辑注册表使用
	 * @param targetObject the target object to work on (as context that can be passed to editors)
	 *                     ~$ 目标对象的工作(如上下文,可以通过编辑)
	 */
	public TypeConverterDelegate(PropertyEditorRegistrySupport propertyEditorRegistry, Object targetObject) {
		this.propertyEditorRegistry = propertyEditorRegistry;
		this.targetObject = targetObject;
	}


	/**
	 * Convert the value to the specified required type.
	 * *************************************************
	 * ~$  将值转换为所需的指定类型.
	 * @param newValue the proposed new value
	 *                 ~$ 拟议的新值
	 * @param requiredType the type we must convert to
	 *                     ~$ 我们必须转换类型
	 * (or <code>null</code> if not known, for example in case of a collection element)
	 * @param methodParam the method parameter that is the target of the conversion
	 *                    ~$ 的方法参数转换的目标
	 * (may be <code>null</code>)
	 * @return the new value, possibly the result of type conversion
	 * @throws IllegalArgumentException if type conversion failed
	 */
	public <T> T convertIfNecessary(Object newValue, Class<T> requiredType, MethodParameter methodParam)
			throws IllegalArgumentException {

		return convertIfNecessary(null, null, newValue, requiredType,
				(methodParam != null ? new TypeDescriptor(methodParam) : TypeDescriptor.valueOf(requiredType)));
	}

	/**
	 * Convert the value to the required type for the specified property.
	 * *******************************************************************
	 * ~$  将值转换为所需的类型为指定的属性.
	 * @param propertyName name of the property
	 * @param oldValue the previous value, if available (may be <code>null</code>)
	 * @param newValue the proposed new value
	 * @param requiredType the type we must convert to
	 * (or <code>null</code> if not known, for example in case of a collection element)
	 * @return the new value, possibly the result of type conversion
	 * @throws IllegalArgumentException if type conversion failed
	 */
	public <T> T convertIfNecessary(
			String propertyName, Object oldValue, Object newValue, Class<T> requiredType)
			throws IllegalArgumentException {

		return convertIfNecessary(propertyName, oldValue, newValue, requiredType, TypeDescriptor.valueOf(requiredType));
	}

	/**
	 * Convert the value to the required type (if necessary from a String),
	 * for the specified property.
	 * ********************************************************************
	 * ~$  将值转换为所需的类型(如果有必要从一个字符串),指定属性.
	 * @param propertyName name of the property
	 * @param oldValue the previous value, if available (may be <code>null</code>)
	 * @param newValue the proposed new value
	 * @param requiredType the type we must convert to
	 * (or <code>null</code> if not known, for example in case of a collection element)
	 * @param typeDescriptor the descriptor for the target property or field
	 * @return the new value, possibly the result of type conversion
	 * @throws IllegalArgumentException if type conversion failed
	 */
	@SuppressWarnings("unchecked")
	public <T> T convertIfNecessary(String propertyName, Object oldValue, Object newValue,
			Class<T> requiredType, TypeDescriptor typeDescriptor) throws IllegalArgumentException {

		Object convertedValue = newValue;

		// Custom editor for this type?
		/** 自定义编辑这个类型吗?*/
		PropertyEditor editor = this.propertyEditorRegistry.findCustomEditor(requiredType, propertyName);

		ConversionFailedException firstAttemptEx = null;

		// No custom editor but custom ConversionService specified?
		/** 没有自定义编辑器定制ConversionService指定吗? */
		ConversionService conversionService = this.propertyEditorRegistry.getConversionService();
		if (editor == null && conversionService != null && convertedValue != null && typeDescriptor != null) {
			TypeDescriptor sourceTypeDesc = TypeDescriptor.forObject(newValue);
			TypeDescriptor targetTypeDesc = typeDescriptor;
			if (conversionService.canConvert(sourceTypeDesc, targetTypeDesc)) {
				try {
					return (T) conversionService.convert(convertedValue, sourceTypeDesc, targetTypeDesc);
				}
				catch (ConversionFailedException ex) {
					// fallback to default conversion logic below
					/** 回退到下面的默认转换逻辑 */
					firstAttemptEx = ex;
				}
			}
		}

		// Value not of required type?
		/** 价值不是必需的类型? */
		if (editor != null || (requiredType != null && !ClassUtils.isAssignableValue(requiredType, convertedValue))) {
			if (requiredType != null && Collection.class.isAssignableFrom(requiredType) && convertedValue instanceof String) {
				TypeDescriptor elementType = typeDescriptor.getElementTypeDescriptor();
				if (elementType != null && Enum.class.isAssignableFrom(elementType.getType())) {
					convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
				}
			}
			if (editor == null) {
				editor = findDefaultEditor(requiredType, typeDescriptor);
			}
			convertedValue = doConvertValue(oldValue, convertedValue, requiredType, editor);
		}

		if (requiredType != null) {
			// Try to apply some standard type conversion rules if appropriate.
			/** 尝试应用一些标准的类型转换规则是否合适。*/
			if (convertedValue != null) {
				if (requiredType.isArray()) {
					// Array required -> apply appropriate conversion of elements.
					/** 元素的数组需要- >应用适当的转换.*/
					if (convertedValue instanceof String && Enum.class.isAssignableFrom(requiredType.getComponentType())) {
						convertedValue = StringUtils.commaDelimitedListToStringArray((String) convertedValue);
					}
					return (T) convertToTypedArray(convertedValue, propertyName, requiredType.getComponentType());
				}
				else if (convertedValue instanceof Collection) {
					// Convert elements to target type, if determined.
					/** 将元素转换成目标类型,如果确定.*/
					convertedValue = convertToTypedCollection(
							(Collection) convertedValue, propertyName, requiredType, typeDescriptor);
				}
				else if (convertedValue instanceof Map) {
					// Convert keys and values to respective target type, if determined.
					/** 键和值转换为各自的目标类型,如果确定.*/
					convertedValue = convertToTypedMap(
							(Map) convertedValue, propertyName, requiredType, typeDescriptor);
				}
				if (convertedValue.getClass().isArray() && Array.getLength(convertedValue) == 1) {
					convertedValue = Array.get(convertedValue, 0);
				}
				if (String.class.equals(requiredType) && ClassUtils.isPrimitiveOrWrapper(convertedValue.getClass())) {
					// We can stringify any primitive value...
					/** 函数我们可以把任何原始值...*/
					return (T) convertedValue.toString();
				}
				else if (convertedValue instanceof String && !requiredType.isInstance(convertedValue)) {
					if (!requiredType.isInterface() && !requiredType.isEnum()) {
						try {
							Constructor strCtor = requiredType.getConstructor(String.class);
							return (T) BeanUtils.instantiateClass(strCtor, convertedValue);
						}
						catch (NoSuchMethodException ex) {
							// proceed with field lookup
							/** 进行现场查找 */
							if (logger.isTraceEnabled()) {
								logger.trace("No String constructor found on type [" + requiredType.getName() + "]", ex);
							}
						}
						catch (Exception ex) {
							if (logger.isDebugEnabled()) {
								logger.debug("Construction via String failed for type [" + requiredType.getName() + "]", ex);
							}
						}
					}
					String trimmedValue = ((String) convertedValue).trim();
					if (requiredType.isEnum() && "".equals(trimmedValue)) {
						// It's an empty enum identifier: reset the enum value to null.
						/** 一个空枚举标识符:重置枚举值为null.*/
						return null;
					}
					
					convertedValue = attemptToConvertStringToEnum(requiredType, trimmedValue, convertedValue);
				}
			}

			if (!ClassUtils.isAssignableValue(requiredType, convertedValue)) {
				if (firstAttemptEx != null) {
					throw firstAttemptEx;
				}
				// Definitely doesn't match: throw IllegalArgumentException/IllegalStateException
				/** 绝对不匹配:把IllegalArgumentException / IllegalStateException */
				StringBuilder msg = new StringBuilder();
				msg.append("Cannot convert value of type [").append(ClassUtils.getDescriptiveType(newValue));
				msg.append("] to required type [").append(ClassUtils.getQualifiedName(requiredType)).append("]");
				if (propertyName != null) {
					msg.append(" for property '").append(propertyName).append("'");
				}
				if (editor != null) {
					msg.append(": PropertyEditor [").append(editor.getClass().getName()).append(
							"] returned inappropriate value of type [").append(
							ClassUtils.getDescriptiveType(convertedValue)).append("]");
					throw new IllegalArgumentException(msg.toString());
				}
				else {
					msg.append(": no matching editors or conversion strategy found");
					throw new IllegalStateException(msg.toString());
				}
			}
		}

		if (firstAttemptEx != null) {
			logger.debug("Original ConversionService attempt failed - ignored since " +
					"PropertyEditor based conversion eventually succeeded", firstAttemptEx);
		}

		return (T) convertedValue;
	}

	private Object attemptToConvertStringToEnum(Class<?> requiredType, String trimmedValue, Object currentConvertedValue) {
		Object convertedValue = currentConvertedValue;

		if (Enum.class.equals(requiredType)) {
			// target type is declared as raw enum, treat the trimmed value as <enum.fqn>.FIELD_NAME
			/** 目标类型被声明为原始枚举,将削减值< enum.fqn > .FIELD_NAME */
			int index = trimmedValue.lastIndexOf(".");
			if (index > - 1) {
				String enumType = trimmedValue.substring(0, index);
				String fieldName = trimmedValue.substring(index + 1);
				ClassLoader loader = this.targetObject.getClass().getClassLoader();
				try {
					Class<?> enumValueType = loader.loadClass(enumType);
					Field enumField = enumValueType.getField(fieldName);
					convertedValue = enumField.get(null);
				}
				catch (ClassNotFoundException ex) {
					if(logger.isTraceEnabled()) {
						logger.trace("Enum class [" + enumType + "] cannot be loaded from [" + loader + "]", ex);
					}
				}
				catch (Throwable ex) {
					if(logger.isTraceEnabled()) {
						logger.trace("Field [" + fieldName + "] isn't an enum value for type [" + enumType + "]", ex);
					}
				}
			}
		}

		if (convertedValue == currentConvertedValue) {
			// Try field lookup as fallback: for JDK 1.5 enum or custom enum
			/** 尝试字段查找作为候选:JDK 1.5枚举或自定义的枚举*/
			// with values defined as static fields. Resulting value still needs
			/** 值定义为静态字段.得到的值仍然需要*/
			// to be checked, hence we don't return it right away.
			/** 检查,因此我们不返回它.*/
			try {
				Field enumField = requiredType.getField(trimmedValue);
				convertedValue = enumField.get(null);
			}
			catch (Throwable ex) {
				if (logger.isTraceEnabled()) {
					logger.trace("Field [" + convertedValue + "] isn't an enum value", ex);

				}
			}
		}

		return convertedValue;
	}
	/**
	 * Find a default editor for the given type.
	 * ******************************************
	 * ~$ 找到一个给定类型的默认编辑器.
	 * @param requiredType the type to find an editor for
	 *                     ~$ 找一个编辑器类型
	 * @param descriptor the JavaBeans descriptor for the property
	 *                   ~$   javabean描述符属性
	 * @return the corresponding editor, or <code>null</code> if none
	 */
	protected PropertyEditor findDefaultEditor(Class requiredType, TypeDescriptor typeDescriptor) {
		PropertyEditor editor = null;
		//if (typeDescriptor instanceof PropertyTypeDescriptor) {
			//PropertyDescriptor pd = ((PropertyTypeDescriptor) typeDescriptor).getPropertyDescriptor();
			//editor = pd.createPropertyEditor(this.targetObject);
		//}
		if (editor == null && requiredType != null) {
			// No custom editor -> check BeanWrapperImpl's default editors.
			/** 没有自定义编辑器- >检查BeanWrapperImpl的默认编辑器.*/
			editor = this.propertyEditorRegistry.getDefaultEditor(requiredType);
			if (editor == null && !String.class.equals(requiredType)) {
				// No BeanWrapper default editor -> check standard JavaBean editor.
				/** 没有BeanWrapper默认编辑器- >检查标准JavaBean编辑器.*/
				editor = BeanUtils.findEditorByConvention(requiredType);
			}
		}
		return editor;
	}

	/**
	 * Convert the value to the required type (if necessary from a String),
	 * using the given property editor.
	 * ********************************************************************
	 * ~$ 将值转换为所需的类型(如果有必要从一个字符串),使用给定的属性编辑器.
	 * @param oldValue the previous value, if available (may be <code>null</code>)
	 * @param newValue the proposed new value
	 * @param requiredType the type we must convert to
	 * (or <code>null</code> if not known, for example in case of a collection element)
	 * @param editor the PropertyEditor to use
	 * @return the new value, possibly the result of type conversion
	 * @throws IllegalArgumentException if type conversion failed
	 */
	protected Object doConvertValue(Object oldValue, Object newValue, Class<?> requiredType, PropertyEditor editor) {
		Object convertedValue = newValue;
		boolean sharedEditor = false;

		if (editor != null) {
			sharedEditor = this.propertyEditorRegistry.isSharedEditor(editor);
		}

		if (editor != null && !(convertedValue instanceof String)) {
			// Not a String -> use PropertyEditor's setValue.
			/** 使用PropertyEditor setValue不是一个字符串.*/
			// With standard PropertyEditors, this will return the very same object;
			/** 与标准PropertyEditors,这将返回完全相同的对象; */
			// we just want to allow special PropertyEditors to override setValue
			/** 我们只是想让特殊PropertyEditors覆盖setValue*/
			// for type conversion from non-String values to the required type.
			/** 从non-String值类型转换所需的类型.*/
			try {
				Object newConvertedValue;
				if (sharedEditor) {
					// Synchronized access to shared editor instance.
					/** 同步访问共享编辑器实例. */
					synchronized (editor) {
						editor.setValue(convertedValue);
						newConvertedValue = editor.getValue();
					}
				}
				else {
					// Unsynchronized access to non-shared editor instance.
					/** 同步访问非共享编辑器实例.*/
					editor.setValue(convertedValue);
					newConvertedValue = editor.getValue();
				}
				if (newConvertedValue != convertedValue) {
					convertedValue = newConvertedValue;
					// Reset PropertyEditor: It already did a proper conversion.
					/** 重置PropertyEditor:它已经做了适当的转换。*/
					// Don't use it again for a setAsText call.
					/** 不要再使用它setAsText调用.*/
					editor = null;
				}
			}
			catch (Exception ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
				}
				// Swallow and proceed.
			}
		}

		Object returnValue = convertedValue;

		if (requiredType != null && !requiredType.isArray() && convertedValue instanceof String[]) {
			// Convert String array to a comma-separated String.
			/** 字符串数组转换为一个以逗号分隔的字符串.*/
			// Only applies if no PropertyEditor converted the String array before.
			/** 仅适用于如果没有PropertyEditor转换字符串数组.*/
			// The CSV String will be passed into a PropertyEditor's setAsText method, if any.
			/** CSV字符串将被传递到PropertyEditor的setAsText方法,如果有的话.*/
			if (logger.isTraceEnabled()) {
				logger.trace("Converting String array to comma-delimited String [" + convertedValue + "]");
			}
			convertedValue = StringUtils.arrayToCommaDelimitedString((String[]) convertedValue);
		}

		if (convertedValue instanceof String) {
			if (editor != null) {
				// Use PropertyEditor's setAsText in case of a String value.
				/** 使用PropertyEditor setAsText字符串值.*/
				if (logger.isTraceEnabled()) {
					logger.trace("Converting String to [" + requiredType + "] using property editor [" + editor + "]");
				}
				String newTextValue = (String) convertedValue;
				if (sharedEditor) {
					// Synchronized access to shared editor instance.
					synchronized (editor) {
						return doConvertTextValue(oldValue, newTextValue, editor);
					}
				}
				else {
					// Unsynchronized access to non-shared editor instance.
					return doConvertTextValue(oldValue, newTextValue, editor);
				}
			}
			else if (String.class.equals(requiredType)) {
				returnValue = convertedValue;
			}
		}

		return returnValue;
	}

	/**
	 * Convert the given text value using the given property editor.
	 * **************************************************************
	 * ~$ 将给定的文本值使用给定的属性编辑器.
	 * @param oldValue the previous value, if available (may be <code>null</code>)
	 * @param newTextValue the proposed text value
	 * @param editor the PropertyEditor to use
	 * @return the converted value
	 */
	protected Object doConvertTextValue(Object oldValue, String newTextValue, PropertyEditor editor) {
		try {
			editor.setValue(oldValue);
		}
		catch (Exception ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("PropertyEditor [" + editor.getClass().getName() + "] does not support setValue call", ex);
			}
			// Swallow and proceed.
		}
		editor.setAsText(newTextValue);
		return editor.getValue();
	}

	protected Object convertToTypedArray(Object input, String propertyName, Class<?> componentType) {
		if (input instanceof Collection) {
			// Convert Collection elements to array elements.
			/** 集合的元素转换为数组元素.*/
			Collection coll = (Collection) input;
			Object result = Array.newInstance(componentType, coll.size());
			int i = 0;
			for (Iterator it = coll.iterator(); it.hasNext(); i++) {
				Object value = convertIfNecessary(
						buildIndexedPropertyName(propertyName, i), null, it.next(), componentType);
				Array.set(result, i, value);
			}
			return result;
		}
		else if (input.getClass().isArray()) {
			// Convert array elements, if necessary.
			/** 把数组元素,如果必要的.*/
			if (componentType.equals(input.getClass().getComponentType()) &&
					!this.propertyEditorRegistry.hasCustomEditorForElement(componentType, propertyName)) {
				return input;
			}
			int arrayLength = Array.getLength(input);
			Object result = Array.newInstance(componentType, arrayLength);
			for (int i = 0; i < arrayLength; i++) {
				Object value = convertIfNecessary(
						buildIndexedPropertyName(propertyName, i), null, Array.get(input, i), componentType);
				Array.set(result, i, value);
			}
			return result;
		}
		else {
			// A plain value: convert it to an array with a single component.
			/** 一个普通的价值:与一个单独的组件将其转换为一个数组.*/
			Object result = Array.newInstance(componentType, 1);
			Object value = convertIfNecessary(
					buildIndexedPropertyName(propertyName, 0), null, input, componentType);
			Array.set(result, 0, value);
			return result;
		}
	}

	@SuppressWarnings("unchecked")
	protected Collection convertToTypedCollection(
			Collection original, String propertyName, Class requiredType, TypeDescriptor typeDescriptor) {

		if (!Collection.class.isAssignableFrom(requiredType)) {
			return original;
		}

		boolean approximable = CollectionFactory.isApproximableCollectionType(requiredType);
		if (!approximable && !canCreateCopy(requiredType)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Custom Collection type [" + original.getClass().getName() +
						"] does not allow for creating a copy - injecting original Collection as-is");
			}
			return original;
		}

		boolean originalAllowed = requiredType.isInstance(original);
		typeDescriptor = typeDescriptor.narrow(original);
		TypeDescriptor elementType = typeDescriptor.getElementTypeDescriptor();
		if (elementType == null && originalAllowed &&
				!this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
			return original;
		}

		Iterator it;
		try {
			it = original.iterator();
			if (it == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Collection of type [" + original.getClass().getName() +
							"] returned null Iterator - injecting original Collection as-is");
				}
				return original;
			}
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot access Collection of type [" + original.getClass().getName() +
						"] - injecting original Collection as-is: " + ex);
			}
			return original;
		}

		Collection convertedCopy;
		try {
			if (approximable) {
				convertedCopy = CollectionFactory.createApproximateCollection(original, original.size());
			}
			else {
				convertedCopy = (Collection) requiredType.newInstance();
			}
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot create copy of Collection type [" + original.getClass().getName() +
						"] - injecting original Collection as-is: " + ex);
			}
			return original;
		}

		int i = 0;
		for (; it.hasNext(); i++) {
			Object element = it.next();
			String indexedPropertyName = buildIndexedPropertyName(propertyName, i);
			Object convertedElement = convertIfNecessary(indexedPropertyName, null, element,
					(elementType != null ? elementType.getType() : null) , elementType);
			try {
				convertedCopy.add(convertedElement);
			}
			catch (Throwable ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Collection type [" + original.getClass().getName() +
							"] seems to be read-only - injecting original Collection as-is: " + ex);
				}
				return original;
			}
			originalAllowed = originalAllowed && (element == convertedElement);
		}
		return (originalAllowed ? original : convertedCopy);
	}

	@SuppressWarnings("unchecked")
	protected Map convertToTypedMap(
			Map original, String propertyName, Class requiredType, TypeDescriptor typeDescriptor) {

		if (!Map.class.isAssignableFrom(requiredType)) {
			return original;
		}

		boolean approximable = CollectionFactory.isApproximableMapType(requiredType);
		if (!approximable && !canCreateCopy(requiredType)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Custom Map type [" + original.getClass().getName() +
						"] does not allow for creating a copy - injecting original Map as-is");
			}
			return original;
		}

		boolean originalAllowed = requiredType.isInstance(original);
		typeDescriptor = typeDescriptor.narrow(original);
		TypeDescriptor keyType = typeDescriptor.getMapKeyTypeDescriptor();
		TypeDescriptor valueType = typeDescriptor.getMapValueTypeDescriptor();
		if (keyType == null && valueType == null && originalAllowed &&
				!this.propertyEditorRegistry.hasCustomEditorForElement(null, propertyName)) {
			return original;
		}

		Iterator it;
		try {
			it = original.entrySet().iterator();
			if (it == null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Map of type [" + original.getClass().getName() +
							"] returned null Iterator - injecting original Map as-is");
				}
				return original;
			}
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot access Map of type [" + original.getClass().getName() +
						"] - injecting original Map as-is: " + ex);
			}
			return original;
		}

		Map convertedCopy;
		try {
			if (approximable) {
				convertedCopy = CollectionFactory.createApproximateMap(original, original.size());
			}
			else {
				convertedCopy = (Map) requiredType.newInstance();
			}
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Cannot create copy of Map type [" + original.getClass().getName() +
						"] - injecting original Map as-is: " + ex);
			}
			return original;
		}

		while (it.hasNext()) {
			Map.Entry entry = (Map.Entry) it.next();
			Object key = entry.getKey();
			Object value = entry.getValue();
			String keyedPropertyName = buildKeyedPropertyName(propertyName, key);
			Object convertedKey = convertIfNecessary(keyedPropertyName, null, key,
					(keyType != null ? keyType.getType() : null), keyType);
			Object convertedValue = convertIfNecessary(keyedPropertyName, null, value,
					(valueType!= null ? valueType.getType() : null), valueType);
			try {
				convertedCopy.put(convertedKey, convertedValue);
			}
			catch (Throwable ex) {
				if (logger.isDebugEnabled()) {
					logger.debug("Map type [" + original.getClass().getName() +
							"] seems to be read-only - injecting original Map as-is: " + ex);
				}
				return original;
			}
			originalAllowed = originalAllowed && (key == convertedKey) && (value == convertedValue);
		}
		return (originalAllowed ? original : convertedCopy);
	}

	private String buildIndexedPropertyName(String propertyName, int index) {
		return (propertyName != null ?
				propertyName + PropertyAccessor.PROPERTY_KEY_PREFIX + index + PropertyAccessor.PROPERTY_KEY_SUFFIX :
				null);
	}

	private String buildKeyedPropertyName(String propertyName, Object key) {
		return (propertyName != null ?
				propertyName + PropertyAccessor.PROPERTY_KEY_PREFIX + key + PropertyAccessor.PROPERTY_KEY_SUFFIX :
				null);
	}

	private boolean canCreateCopy(Class requiredType) {
		return (!requiredType.isInterface() && !Modifier.isAbstract(requiredType.getModifiers()) &&
				Modifier.isPublic(requiredType.getModifiers()) && ClassUtils.hasConstructor(requiredType));
	}

}
