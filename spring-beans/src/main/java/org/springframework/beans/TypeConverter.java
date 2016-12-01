/*
 * Copyright 2002-2009 the original author or authors.
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

import org.springframework.core.MethodParameter;

/**
 * Interface that defines type conversion methods. Typically (but not necessarily)
 * implemented in conjunction with the PropertyEditorRegistry interface.
 * *******************************************************************************
 * ~$ 接口定义了类型转换的方法.通常(但不一定)会同PropertyEditorRegistry接口实现.
 * @author Juergen Hoeller
 * @since 2.0
 * @see PropertyEditorRegistry
 * @see SimpleTypeConverter
 * @see BeanWrapperImpl
 */
public interface TypeConverter {

	/**
	 * Convert the value to the required type (if necessary from a String).
	 * <p>Conversions from String to any type will typically use the <code>setAsText</code>
	 * method of the PropertyEditor class. Note that a PropertyEditor must be registered
	 * for the given class for this to work; this is a standard JavaBeans API.
	 * A number of PropertyEditors are automatically registered.
	 * *************************************************************************************
	 * ~$ 将值转换为所需的类型(如果有必要从一个字符串).
	 * <p>从字符串转换到任何类型通常会使用setAsText PropertyEditor类的方法.
	 *     请注意,PropertyEditor必须注册为给定类工作;这是一个标准JavaBeans API.许多PropertyEditors自动注册.
	 * @param value the value to convert
	 * @param requiredType the type we must convert to
	 * (or <code>null</code> if not known, for example in case of a collection element)
	 * @return the new value, possibly the result of type conversion
	 * @throws TypeMismatchException if type conversion failed
	 * @see java.beans.PropertyEditor#setAsText(String)
	 * @see java.beans.PropertyEditor#getValue()
	 */
	<T> T convertIfNecessary(Object value, Class<T> requiredType) throws TypeMismatchException;

	/**
	 * Convert the value to the required type (if necessary from a String).
	 * <p>Conversions from String to any type will typically use the <code>setAsText</code>
	 * method of the PropertyEditor class. Note that a PropertyEditor must be registered
	 * for the given class for this to work; this is a standard JavaBeans API.
	 * A number of PropertyEditors are automatically registered.
	 * *************************************************************************************
	 * ~$ 将值转换为所需的类型(如果有必要从一个字符串).
	 * <p>从字符串转换到任何类型通常会使用setAsText PropertyEditor类的方法.
	 *    请注意,PropertyEditor必须注册为给定类工作;这是一个标准JavaBeans API.许多PropertyEditors自动注册.
	 * @param value the value to convert
	 *              ~$ 值转换
	 * @param requiredType the type we must convert to
	 *                     ~$我们必须转换类型
	 * (or <code>null</code> if not known, for example in case of a collection element)
	 * @param methodParam the method parameter that is the target of the conversion
	 * (for analysis of generic types; may be <code>null</code>)
	 *                    ~$ 的方法参数转换的目标
	 * @return the new value, possibly the result of type conversion
	 * 						~$新值,可能类型转换的结果
	 * @throws TypeMismatchException if type conversion failed
	 * @see java.beans.PropertyEditor#setAsText(String)
	 * @see java.beans.PropertyEditor#getValue()
	 */
	<T> T convertIfNecessary(Object value, Class<T> requiredType, MethodParameter methodParam)
			throws TypeMismatchException;

}
