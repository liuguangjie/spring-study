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

package org.springframework.core.convert;

/**
 * A service interface for type conversion. This is the entry point into the convert system.
 * Call {@link #convert(Object, Class)} to perform a thread-safe type conversion using this system.
 * ******************************************************************************************
 * ~$ 类型转换的服务接口.这是入口点进入转换系统.
 * 叫{@link #convert(Object, Class)}执行线程安全的类型转换使用这个系统.
 * @author Keith Donald
 * @since 3.0
 */
public interface ConversionService {

	/**
	 * Returns true if objects of sourceType can be converted to targetType.
	 * If this method returns true, it means {@link #convert(Object, Class)} is capable of converting an instance of sourceType to targetType.
	 * Special note on collections, arrays, and maps types:
	 * For conversion between collection, array, and map types, this method will return 'true'
	 * even though a convert invocation may still generate a {@link ConversionException} if the underlying elements are not convertible.
	 * Callers are expected to handle this exceptional case when working with collections and maps.
	 * ******************************************************************************************
	 * ~$ 返回true,如果可以转换为targetType sourceType的对象.
	 *    如果这个方法返回true,这意味着{@link #convert(Object, Class)}能够转换的实例sourceType targetType.
	 *    特别注意收集、数组和地图类型:集合之间的转换,数组,和地图类型,
	 *    这个方法将返回“true”尽管转换调用可能仍然生成一个{@link ConversionException }如果底层元素不兑换.
	 *    调用者将处理这个特殊情况在处理 collections 和 maps.
	 * @param sourceType the source type to convert from (may be null if source is null)
	 *                   ~$ 转换的源类型(可能是null如果来源是null)
	 * @param targetType the target type to convert to (required)
	 *                   ~$ 目标类型转换(必需)
	 * @return true if a conversion can be performed, false if not
	 * @throws IllegalArgumentException if targetType is null
	 */
	boolean canConvert(Class<?> sourceType, Class<?> targetType);

	/**
	 * Returns true if objects of sourceType can be converted to the targetType.
	 * The TypeDescriptors provide additional context about the source and target locations where conversion would occur, often object fields or property locations.
	 * If this method returns true, it means {@link #convert(Object, TypeDescriptor, TypeDescriptor)} is capable of converting an instance of sourceType to targetType.
	 * Special note on collections, arrays, and maps types:
	 * For conversion between collection, array, and map types, this method will return 'true'
	 * even though a convert invocation may still generate a {@link ConversionException} if the underlying elements are not convertible.
	 * Callers are expected to handle this exceptional case when working with collections and maps.
	 * ********************************************************************************************
	 * ~$ 返回true,如果可以转化为targetType sourceType的对象.
	 *    typedescriptor提供额外的上下文的源和目标位置转换会发生,通常对象字段或属性的位置.
	 *    如果这个方法返回true,这意味着{@link #convert(Object, TypeDescriptor, TypeDescriptor)}能够转换的实例sourceType targetType.
	 *    特别注意收集、数组和地图类型:
	 *    集合之间的转换、数组和地图类型,这个方法将返回“true”
	 *    尽管转换调用可能仍然生成一个{@link ConversionException }如果底层元素不兑换.
	 *
	 * @param sourceType context about the source type to convert from (may be null if source is null)
	 *                   ~$ 上下文的源类型转换(可能是null如果来源是null)
	 * @param targetType context about the target type to convert to (required)
	 *                   ~$ 上下文的目标类型转换(必需)
	 * @return true if a conversion can be performed between the source and target types, false if not
	 * @throws IllegalArgumentException if targetType is null
	 */
	boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

	/**
	 * Convert the source to targetType.
	 * *********************************
	 * ~$ 源转换为targetType.
	 * @param source the source object to convert (may be null)
	 * @param targetType the target type to convert to (required)
	 * @return the converted object, an instance of targetType
	 * @throws ConversionException if a conversion exception occurred
	 * @throws IllegalArgumentException if targetType is null
	 */
	<T> T convert(Object source, Class<T> targetType);
	
	/**
	 * Convert the source to targetType.
	 * The TypeDescriptors provide additional context about the source and target locations where conversion will occur, often object fields or property locations.
	 * ****************************************************************************************************
	 * ~$ 源转换为targetType.typedescriptor提供额外的上下文的源和目标位置转换将发生,通常对象字段或属性的位置.
	 * @param source the source object to convert (may be null)
	 * @param sourceType context about the source type converting from (may be null if source is null)
	 * @param targetType context about the target type to convert to (required)
	 * @return the converted object, an instance of {@link TypeDescriptor#getObjectType() targetType}</code>
	 * @throws ConversionException if a conversion exception occurred
	 * @throws IllegalArgumentException if targetType is null
	 * @throws IllegalArgumentException if sourceType is null but source is not null
	 */
	Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);

}