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

package org.springframework.core.enums;

import org.springframework.util.Assert;

/**
 * Implementation of LabeledEnum which uses a letter as the code type.
 *
 * <p>Should almost always be subclassed, but for some simple situations it may be
 * used directly. Note that you will not be able to use unique type-based functionality
 * like <code>LabeledEnumResolver.getLabeledEnumSet(type)</code> in this case.
 * ************************************************************************************
 * ~$ 实现LabeledEnum它使用一个字母的代码类型.
 * <p> 几乎总是应该从它派生出子类,但是对于一些简单的情况下直接使用.
 *     请注意,您将无法使用独特的基于类型功能像LabeledEnumResolver.getLabeledEnumSet(type)在这种情况下.
 * @author Keith Donald
 * @since 1.2.2
 * @deprecated as of Spring 3.0, in favor of Java 5 enums.
 */
@Deprecated
public class LetterCodedLabeledEnum extends AbstractGenericLabeledEnum {

	/**
	 * The unique code of this enum.
	 * *****************************
	 * ~$ 这个枚举的惟一代码.
	 */
	private final Character code;


	/**
	 * Create a new LetterCodedLabeledEnum instance.
	 * *********************************************
	 * ~$ 创建一个新的LetterCodedLabeledEnum实例.
	 * @param code the letter code
	 * @param label the label (can be <code>null</code>)
	 */
	public LetterCodedLabeledEnum(char code, String label) {
		super(label);
		Assert.isTrue(Character.isLetter(code),
				"The code '" + code + "' is invalid: it must be a letter");
		this.code = new Character(code);
	}

	
	public Comparable getCode() {
		return code;
	}

	/**
	 * Return the letter code of this LabeledEnum instance.
	 * ****************************************************
	 * ~$ 返回这个LabeledEnum的字母代码实例.
	 */
	public char getLetterCode() {
		return ((Character) getCode()).charValue();
	}

}
