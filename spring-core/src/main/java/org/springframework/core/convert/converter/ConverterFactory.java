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
 * A factory for "ranged" converters that can convert objects from S to subtypes of R.
 * **********************************************************************************
 * ~$ 工厂"ranged" 转换器,可以把对象从S R的子类型.
 * @author Keith Donald
 * @since 3.0 
 * @param <S> The source type converters created by this factory can convert from
 *           ~$ 源类型转换器由这个工厂可以转换
 * @param <R> The target range (or base) type converters created by this factory can convert to;
 *           ~$ 目标范围(或基础)类型转换器由这个工厂可以转换为 ;
 * for example {@link Number} for a set of number subtypes.
 */
public interface ConverterFactory<S, R> {

	/**
	 * Get the converter to convert from S to target type T, where T is also an instance of R.
	 * ***************************************************************************************
	 * ~$ 让转换器转换S到目标类型T,其中T是R的一个实例.
	 * @param <T> the target type
	 * @param targetType the target type to convert to
	 * @return A converter from S to T
	 */
	<T extends R> Converter<S, T> getConverter(Class<T> targetType);

}
