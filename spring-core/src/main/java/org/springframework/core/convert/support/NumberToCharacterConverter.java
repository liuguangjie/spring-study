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
 * Converts from any JDK-standard Number implementation to a Character.
 * ********************************************************************
 * ~$ 从任何JDK-standard数字实现字符转换.
 * @author Keith Donald
 * @since 3.0
 * @see Character
 * @see Short
 * @see Integer
 * @see Long
 * @see java.math.BigInteger
 * @see Float
 * @see Double
 * @see java.math.BigDecimal
 */
final class NumberToCharacterConverter implements Converter<Number, Character> {

	public Character convert(Number source) {
		return (char) source.shortValue();
	}

}
