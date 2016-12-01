/*
 * Copyright 2002-2007 the original author or authors.
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
import java.util.regex.Pattern;

/**
 * Editor for <code>java.util.regex.Pattern</code>, to directly populate a Pattern property.
 * Expects the same syntax as Pattern's <code>compile</code> method.
 * *****************************************************************************************
 * ~$ 预计相同的语法语言环境的toString,即语言+可选国家+可选变量,
 *    用“_”分隔(例如“en”、“en_US”).还接受空格作为分隔符,选择下划线.
 * @author Juergen Hoeller
 * @since 2.0.1
 * @see Pattern
 * @see Pattern#compile(String)
 */
public class PatternEditor extends PropertyEditorSupport {

	private final int flags;


	/**
	 * Create a new PatternEditor with default settings.
	 * *************************************************
	 * ~$ 创建一个新的PatternEditor默认设置.
	 */
	public PatternEditor() {
		this.flags = 0;
	}

	/**
	 * Create a new PatternEditor with the given settings.
	 * @param flags the <code>java.util.regex.Pattern</code> flags to apply
	 * @see Pattern#compile(String, int)
	 * @see Pattern#CASE_INSENSITIVE
	 * @see Pattern#MULTILINE
	 * @see Pattern#DOTALL
	 * @see Pattern#UNICODE_CASE
	 * @see Pattern#CANON_EQ
	 */
	public PatternEditor(int flags) {
		this.flags = flags;
	}


	@Override
	public void setAsText(String text) {
		setValue(text != null ? Pattern.compile(text, this.flags) : null);
	}

	@Override
	public String getAsText() {
		Pattern value = (Pattern) getValue();
		return (value != null ? value.pattern() : "");
	}

}
