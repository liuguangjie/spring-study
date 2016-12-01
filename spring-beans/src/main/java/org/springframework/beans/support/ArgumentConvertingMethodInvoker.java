/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.beans.support;

import java.beans.PropertyEditor;
import java.lang.reflect.Method;

import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.util.MethodInvoker;
import org.springframework.util.ReflectionUtils;

/**
 * Subclass of {@link MethodInvoker} that tries to convert the given
 * arguments for the actual target method via a {@link TypeConverter}.
 *
 * <p>Supports flexible argument conversions, in particular for
 * invoking a specific overloaded method.
 * *******************************************************************
 * ~$ 的子类{@link MethodInvoker },试图将给定的参数用于实际目标方法通过{@link TypeConverter }.
 *
 * <p>支持灵活的参数转换,特别是用于调用一个特定的重载方法.
 * @author Juergen Hoeller
 * @since 1.1
 * @see org.springframework.beans.BeanWrapperImpl#convertIfNecessary
 */
public class ArgumentConvertingMethodInvoker extends MethodInvoker {

	private TypeConverter typeConverter;

	private boolean useDefaultConverter = true;


	/**
	 * Set a TypeConverter to use for argument type conversion.
	 * <p>Default is a {@link SimpleTypeConverter}.
	 * Can be overridden with any TypeConverter implementation, typically
	 * a pre-configured SimpleTypeConverter or a BeanWrapperImpl instance.
	 * *******************************************************************
	 * ~$ 设置一个TypeConverter用于参数类型转换.
	 * <p>默认是一个{@link SimpleTypeConverter }.可以与任何TypeConverter实现覆盖,
	 *    通常预先配置的SimpleTypeConverter或BeanWrapperImpl实例.
	 * @see SimpleTypeConverter
	 * @see org.springframework.beans.BeanWrapperImpl
	 */
	public void setTypeConverter(TypeConverter typeConverter) {
		this.typeConverter = typeConverter;
		this.useDefaultConverter = false;
	}

	/**
	 * Return the TypeConverter used for argument type conversion.
	 * <p>Can be cast to {@link PropertyEditorRegistry}
	 * if direct access to the underlying PropertyEditors is desired
	 * (provided that the present TypeConverter actually implements the
	 * PropertyEditorRegistry interface).
	 * ****************************************************************
	 * ~$ 返回TypeConverter用于参数类型转换.
	 * <p>可以把{@link PropertyEditorRegistry }如果需要
	 *    直接访问底层PropertyEditors(提供目前TypeConverter实际上实现了PropertyEditorRegistry接口).
	 */
	public TypeConverter getTypeConverter() {
		if (this.typeConverter == null && this.useDefaultConverter) {
			this.typeConverter = getDefaultTypeConverter();
		}
		return this.typeConverter;
	}

	/**
	 * Obtain the default TypeConverter for this method invoker.
	 * <p>Called if no explicit TypeConverter has been specified.
	 * The default implementation builds a
	 * {@link SimpleTypeConverter}.
	 * Can be overridden in subclasses.
	 * **********************************************************
	 * ~$ 获得该方法调用程序的默认TypeConverter.
	 * <p>如果没有显式TypeConverter指定.默认实现构建一个{@link SimpleTypeConverter }.
	 *    可以在子类中覆盖.
	 */
	protected TypeConverter getDefaultTypeConverter() {
		return new SimpleTypeConverter();
	}

	/**
	 * Register the given custom property editor for all properties of the given type.
	 * <p>Typically used in conjunction with the default
	 * {@link SimpleTypeConverter}; will work with any
	 * TypeConverter that implements the PropertyEditorRegistry interface as well.
	 * *******************************************************************************
	 * ~$ 注册给定的自定义属性编辑器给定类型的所有属性.
	 * <p>通常使用与默认{@link SimpleTypeConverter };
	 *    将与任何TypeConverter实现PropertyEditorRegistry接口.
	 *
	 * @param requiredType type of the property
	 * @param propertyEditor editor to register
	 * @see #setTypeConverter
	 * @see PropertyEditorRegistry#registerCustomEditor
	 */
	public void registerCustomEditor(Class requiredType, PropertyEditor propertyEditor) {
		TypeConverter converter = getTypeConverter();
		if (!(converter instanceof PropertyEditorRegistry)) {
			throw new IllegalStateException(
					"TypeConverter does not implement PropertyEditorRegistry interface: " + converter);
		}
		((PropertyEditorRegistry) converter).registerCustomEditor(requiredType, propertyEditor);
	}


	/**
	 * This implementation looks for a method with matching parameter types.
	 * *********************************************************************
	 * ~$ 这个实现查找与匹配方法参数类型.
	 * @see #doFindMatchingMethod
	 */
	@Override
	protected Method findMatchingMethod() {
		Method matchingMethod = super.findMatchingMethod();
		// Second pass: look for method where arguments can be converted to parameter types.
		/** 第二个通过:寻找方法,参数可以转换为参数类型.*/
		if (matchingMethod == null) {
			// Interpret argument array as individual method arguments.
			/** 解释参数数组作为单独的方法参数.*/
			matchingMethod = doFindMatchingMethod(getArguments());
		}
		if (matchingMethod == null) {
			// Interpret argument array as single method argument of array type.
			/** 解释参数数组类型的数组作为单个方法的参数.*/
			matchingMethod = doFindMatchingMethod(new Object[] {getArguments()});
		}
		return matchingMethod;
	}

	/**
	 * Actually find a method with matching parameter type, i.e. where each
	 * argument value is assignable to the corresponding parameter type.
	 * ********************************************************************
	 * ~$ 其实找到一个方法参数类型匹配,即每个参数值是分配到相应的参数类型.
	 * @param arguments the argument values to match against method parameters
	 * @return a matching method, or <code>null</code> if none
	 */
	protected Method doFindMatchingMethod(Object[] arguments) {
		TypeConverter converter = getTypeConverter();
		if (converter != null) {
			String targetMethod = getTargetMethod();
			Method matchingMethod = null;
			int argCount = arguments.length;
			Method[] candidates = ReflectionUtils.getAllDeclaredMethods(getTargetClass());
			int minTypeDiffWeight = Integer.MAX_VALUE;
			Object[] argumentsToUse = null;
			for (Method candidate : candidates) {
				if (candidate.getName().equals(targetMethod)) {
					// Check if the inspected method has the correct number of parameters.
					/** 检查检查方法有正确数量的参数.*/
					Class[] paramTypes = candidate.getParameterTypes();
					if (paramTypes.length == argCount) {
						Object[] convertedArguments = new Object[argCount];
						boolean match = true;
						for (int j = 0; j < argCount && match; j++) {
							// Verify that the supplied argument is assignable to the method parameter.
							/** 确认提供的参数是可转让的方法参数.*/
							try {
								convertedArguments[j] = converter.convertIfNecessary(arguments[j], paramTypes[j]);
							}
							catch (TypeMismatchException ex) {
								// Ignore -> simply doesn't match.
								/** 忽略 -> 完全不匹配.*/
								match = false;
							}
						}
						if (match) {
							int typeDiffWeight = getTypeDifferenceWeight(paramTypes, convertedArguments);
							if (typeDiffWeight < minTypeDiffWeight) {
								minTypeDiffWeight = typeDiffWeight;
								matchingMethod = candidate;
								argumentsToUse = convertedArguments;
							}
						}
					}
				}
			}
			if (matchingMethod != null) {
				setArguments(argumentsToUse);
				return matchingMethod;
			}
		}
		return null;
	}

}
