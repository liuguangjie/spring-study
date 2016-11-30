/*
 * Copyright 2002-2011 the original author or authors.
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
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

import org.springframework.util.StringUtils;

/**
 * Property editor for <code>java.util.Date</code>,
 * supporting a custom <code>java.text.DateFormat</code>.
 *
 * <p>This is not meant to be used as system PropertyEditor but rather
 * as locale-specific date editor within custom controller code,
 * parsing user-entered number strings into Date properties of beans
 * and rendering them in the UI form.
 *
 * <p>In web MVC code, this editor will typically be registered with
 * <code>binder.registerCustomEditor</code> calls in a custom
 * <code>initBinder</code> method.
 * *******************************************************************
 * ~$ java.util.Date 属性编辑器.目前为止,支持自定义java.text.DateFormat.
 *
 * <p>这并不是意味着作为系统PropertyEditor而是特定日期内编辑自定义控制器代码,
 *    解析用户输入的字符串的日期属性数量在UI表单bean和呈现它们.
 *
 * <p>在web MVC代码,这个编辑器通常是注册绑定.registerCustomEditor调用自定义initBinder方法.
 * @author Juergen Hoeller
 * @since 28.04.2003
 * @see Date
 * @see DateFormat
 * @see org.springframework.validation.DataBinder#registerCustomEditor
 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder
 */
public class CustomDateEditor extends PropertyEditorSupport {

	private final DateFormat dateFormat;

	private final boolean allowEmpty;

	private final int exactDateLength;


	/**
	 * Create a new CustomDateEditor instance, using the given DateFormat
	 * for parsing and rendering.
	 * <p>The "allowEmpty" parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as null value.
	 * Otherwise, an IllegalArgumentException gets thrown in that case.
	 * ******************************************************************
	 * ~$ 创建一个新的CustomDateEditor实例,使用给定的DateFormat解析和渲染.
	 * <p> “allowEmpty”参数状态如果应该允许空字符串解析,即得到解释为空值.
	 *      否则,却是IllegalArgumentException被扔在这种情况下.
	 * @param dateFormat DateFormat to use for parsing and rendering
	 * @param allowEmpty if empty strings should be allowed
	 */
	public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = -1;
	}

	/**
	 * Create a new CustomDateEditor instance, using the given DateFormat
	 * for parsing and rendering.
	 * <p>The "allowEmpty" parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as null value.
	 * Otherwise, an IllegalArgumentException gets thrown in that case.
	 * <p>The "exactDateLength" parameter states that IllegalArgumentException gets
	 * thrown if the String does not exactly match the length specified. This is useful
	 * because SimpleDateFormat does not enforce strict parsing of the year part,
	 * not even with <code>setLenient(false)</code>. Without an "exactDateLength"
	 * specified, the "01/01/05" would get parsed to "01/01/0005". However, even
	 * with an "exactDateLength" specified, prepended zeros in the day or month
	 * part may still allow for a shorter year part, so consider this as just
	 * one more assertion that gets you closer to the intended date format.
	 * ********************************************************************************
	 * ~$ 创建一个新的CustomDateEditor实例,使用给定的DateFormat解析和渲染.
	 * <p>“allowEmpty”参数状态如果应该允许空字符串解析,即得到解释为空值.
	 *     否则,却是IllegalArgumentException被扔在这种情况下.
	 * <p>“exactDateLength”参数指出IllegalArgumentException被抛出如果字符串不精确匹配指定的长度.
	 *    这是有用的因为SimpleDateFormat不执行严格的解析部分,甚至与setLenient(假).
	 *    如果没有指定一个“exactDateLength”,“01/01/05”会解析“01/01/0005”.
	 *    然而,即使在一个“exactDateLength”指定,前缀0天或月部分可能仍然允许短一年部分,
	 *    所以认为这只是作为一个断言让你接近预期的日期格式.
	 * @param dateFormat DateFormat to use for parsing and rendering
	 * @param allowEmpty if empty strings should be allowed
	 * @param exactDateLength the exact expected length of the date String
	 */
	public CustomDateEditor(DateFormat dateFormat, boolean allowEmpty, int exactDateLength) {
		this.dateFormat = dateFormat;
		this.allowEmpty = allowEmpty;
		this.exactDateLength = exactDateLength;
	}


	/**
	 * Parse the Date from the given text, using the specified DateFormat.
	 * *******************************************************************
	 * ~$ 从给定的文本解析日期,使用指定的DateFormat.
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasText(text)) {
			// Treat empty String as null value.
			/** 把空字符串作为null值.*/
			setValue(null);
		}
		else if (text != null && this.exactDateLength >= 0 && text.length() != this.exactDateLength) {
			throw new IllegalArgumentException(
					"Could not parse date: it is not exactly" + this.exactDateLength + "characters long");
		}
		else {
			try {
				setValue(this.dateFormat.parse(text));
			}
			catch (ParseException ex) {
				throw new IllegalArgumentException("Could not parse date: " + ex.getMessage(), ex);
			}
		}
	}

	/**
	 * Format the Date as String, using the specified DateFormat.
	 * **********************************************************
	 * ~$ 格式的日期字符串,使用指定的DateFormat.
	 */
	@Override
	public String getAsText() {
		Date value = (Date) getValue();
		return (value != null ? this.dateFormat.format(value) : "");
	}

}
