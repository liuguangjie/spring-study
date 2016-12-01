/*
 * Copyright 2002-2005 the original author or authors.
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
 * Editor for <code>java.util.Locale</code>, to directly populate a Locale property.
 *
 * <p>Expects the same syntax as Locale's <code>toString</code>, i.e. language +
 * optionally country + optionally variant, separated by "_" (e.g. "en", "en_US").
 * Also accepts spaces as separators, as alternative to underscores.
 * *********************************************************************************
 * ~$ 编辑java.util.Locale,直接填充一个Locale属性.
 *
 * <p>预计相同的语法语言环境的toString,即语言+可选国家+可选变量,
 *    用“_”分隔(例如“en”、“en_US”)。还接受空格作为分隔符,选择下划线.
 * @author Juergen Hoeller
 * @since 26.05.2003
 * @see java.util.Locale
 * @see StringUtils#parseLocaleString
 */
public class LocaleEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) {
		setValue(StringUtils.parseLocaleString(text));
	}

	@Override
	public String getAsText() {
		Object value = getValue();
		return (value != null ? value.toString() : "");
	}

}
