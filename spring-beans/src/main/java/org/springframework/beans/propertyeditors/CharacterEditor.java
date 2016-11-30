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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;

import org.springframework.util.StringUtils;

/**
 * Editor for a {@link Character}, to populate a property
 * of type <code>Character</code> or <code>char</code> from a String value.
 *
 * <p>Note that the JDK does not contain a default
 * {@link java.beans.PropertyEditor property editor} for <code>char</code>!
 * {@link org.springframework.beans.BeanWrapperImpl} will register this
 * editor by default.
 * ************************************************************************
 * ~$ 编辑{ @link字符},填充属性类型的字符或字符的字符串值.
 *
 * <p>注意,JDK不包含一个默认的{@link java.beans.PropertyEditor property editor}为char  !{@link org.springframework.beans.BeanWrapperImpl }将注册这个编辑器.
 * <p>Also supports conversion from a Unicode character sequence; e.g.
 * 		~$ 还支持从一个Unicode字符序列转换;例如
 * <code>u0041</code> ('A').
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Rick Evans
 * @since 1.2
 * @see Character
 * @see org.springframework.beans.BeanWrapperImpl
 */
public class CharacterEditor extends PropertyEditorSupport {

	/**
	 * The prefix that identifies a string as being a Unicode character sequence.
	 * **************************************************************************
	 * ~$ 标识一个字符串的前缀是一个Unicode字符序列.
	 */
	private static final String UNICODE_PREFIX = "\\u";

	/**
	 * The length of a Unicode character sequence.
	 * *******************************************
	 * ~$ Unicode字符序列的长度.
	 */
	private static final int UNICODE_LENGTH = 6;


	private final boolean allowEmpty;


	/**
	 * Create a new CharacterEditor instance.
	 * <p>The "allowEmpty" parameter controls whether an empty String is
	 * to be allowed in parsing, i.e. be interpreted as the <code>null</code>
	 * value when {@link #setAsText(String) text is being converted}. If
	 * <code>false</code>, an {@link IllegalArgumentException} will be thrown
	 * at that time.
	 * **********************************************************************
	 * ~$创建一个新的CharacterEditor实例.
	 * <p>“allowEmpty”参数控制是否被允许在一个空字符串解析,
	 *    即被视为null值的时候{ @link # setAsText文本(字符串)被转化成}.
	 *    如果错误,就会抛出一个{ @link的IllegalArgumentException }.
	 * @param allowEmpty if empty strings are to be allowed
	 */
	public CharacterEditor(boolean allowEmpty) {
		this.allowEmpty = allowEmpty;
	}


	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasLength(text)) {
			// Treat empty String as null value.
			/** 把空字符串作为null值.*/
			setValue(null);
		}
		else if (text == null) {
			throw new IllegalArgumentException("null String cannot be converted to char type");
		}
		else if (isUnicodeCharacterSequence(text)) {
			setAsUnicode(text);
		}
		else if (text.length() != 1) {
			throw new IllegalArgumentException("String [" + text + "] with length " +
					text.length() + " cannot be converted to char type");
		}
		else {
			setValue(new Character(text.charAt(0)));
		}
	}

	@Override
	public String getAsText() {
		Object value = getValue();
		return (value != null ? value.toString() : "");
	}


	private boolean isUnicodeCharacterSequence(String sequence) {
		return (sequence.startsWith(UNICODE_PREFIX) && sequence.length() == UNICODE_LENGTH);
	}

	private void setAsUnicode(String text) {
		int code = Integer.parseInt(text.substring(UNICODE_PREFIX.length()), 16);
		setValue(new Character((char) code));
	}

}
