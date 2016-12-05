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

package org.springframework.core.convert.converter;

/**
 * For registering converters with a type conversion system.
 * *********************************************************
 * ~$ 注册转换器类型转换系统.
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 3.0
 */
public interface ConverterRegistry {
	
	/**
	 * Add a plain converter to this registry.
	 * The convertible sourceType/targetType pair is derived from the Converter's parameterized types.
	 * ***********************************************************************************************
	 * ~$ 这个注册表添加一个简单的转换.可转换sourceType / targetType对来源于转换器的参数化类型.
	 * @throws IllegalArgumentException if the parameterized types could not be resolved
	 */
	void addConverter(Converter<?, ?> converter);

	/**
	 * Add a plain converter to this registry.
	 * The convertible sourceType/targetType pair is specified explicitly.
	 * Allows for a Converter to be reused for multiple distinct pairs without having to create a Converter class for each pair.
	 * *************************************************************************************************************************
	 * ~$ 这个注册表添加一个简单的转换.
	 *    可转换sourceType / targetType对显式地指定.
	 *    允许转换为多个不同的对不被重用为每一对创建一个转换类.
	 * @since 3.1
	 */
	void addConverter(Class<?> sourceType, Class<?> targetType, Converter<?, ?> converter);

	/**
	 * Add a generic converter to this registry.
	 * *****************************************
	 * ~$ 这个注册表添加一个通用的转换器.
	 */
	void addConverter(GenericConverter converter);
	
	/**
	 * Add a ranged converter factory to this registry.
	 * The convertible sourceType/rangeType pair is derived from the ConverterFactory's parameterized types.
	 * *****************************************************************************************************
	 * ~$ 这个注册表添加一个远程变频器的工厂.
	 *    可转换sourceType / rangeType对来源于ConverterFactory参数化的类型.
	 * @throws IllegalArgumentException if the parameterized types could not be resolved. 
	 */
	void addConverterFactory(ConverterFactory<?, ?> converterFactory);

	/**
	 * Remove any converters from sourceType to targetType.
	 * ****************************************************
	 * ~$ 删除任何转换器sourceType targetType.
	 * @param sourceType the source type
	 * @param targetType the target type
	 */
	void removeConvertible(Class<?> sourceType, Class<?> targetType);

}
