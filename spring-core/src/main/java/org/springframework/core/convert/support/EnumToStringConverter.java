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

package org.springframework.core.convert.support;

import org.springframework.core.convert.converter.Converter;

/**
 * Simply calls {@link Enum#name()} to convert a source Enum to a String.
 * **********************************************************************
 * ~$ 简单地调用{@link Enum#name()}源枚举转换为一个字符串.
 * @author Keith Donald
 * @since 3.0
 */
final class EnumToStringConverter implements Converter<Enum<?>, String> {

	public String convert(Enum<?> source) {
		return source.name();
	}

}
