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
import java.text.NumberFormat;

import org.springframework.util.NumberUtils;
import org.springframework.util.StringUtils;

/**
 * Property editor for any Number subclass such as Short, Integer, Long,
 * BigInteger, Float, Double, BigDecimal. Can use a given NumberFormat for
 * (locale-specific) parsing and rendering, or alternatively the default
 * <code>decode</code> / <code>valueOf</code> / <code>toString</code> methods.
 *
 * <p>This is not meant to be used as system PropertyEditor but rather
 * as locale-specific number editor within custom controller code,
 * parsing user-entered number strings into Number properties of beans
 * and rendering them in the UI form.
 *
 * <p>In web MVC code, this editor will typically be registered with
 * <code>binder.registerCustomEditor</code> calls in a custom
 * <code>initBinder</code> method.
 * ****************************************************************************
 * ~$ 等任何子类的属性编辑器短,整数,长,BigInteger,浮动,翻倍,BigDecimal.
 *    可以使用给定的NumberFormat(特定)解析和渲染,或者默认解码的toString方法.
 *
 * <p>这不是意味着作为系统PropertyEditor而是特定数量在自定义控制器代码编辑器,
 *    用户输入的数字字符串解析为bean的属性数量和渲染UI表单.
 *
 * <p>在web MVC代码,这个编辑器通常是注册绑定.registerCustomEditor调用自定义initBinder方法.
 * @author Juergen Hoeller
 * @since 06.06.2003
 * @see Number
 * @see NumberFormat
 * @see org.springframework.validation.DataBinder#registerCustomEditor
 * @see org.springframework.web.servlet.mvc.BaseCommandController#initBinder
 */
public class CustomNumberEditor extends PropertyEditorSupport {

	private final Class<? extends Number> numberClass;

	private final NumberFormat numberFormat;

	private final boolean allowEmpty;


	/**
	 * Create a new CustomNumberEditor instance, using the default
	 * <code>valueOf</code> methods for parsing and <code>toString</code>
	 * methods for rendering.
	 * <p>The "allowEmpty" parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as <code>null</code> value.
	 * Else, an IllegalArgumentException gets thrown in that case.
	 * ************************************************************************
	 * ~$ 创建一个新的CustomNumberEditor实例,使用默认的方法解析和toString方法呈现.
	 * <p>“allowEmpty”参数状态如果应该允许空字符串解析,即得到解释为空值.
	 *    别的,却是IllegalArgumentException被扔在这种情况下.
	 * @param numberClass Number subclass to generate
	 * @param allowEmpty if empty strings should be allowed
	 * @throws IllegalArgumentException if an invalid numberClass has been specified
	 * @see NumberUtils#parseNumber(String, Class)
	 * @see Integer#valueOf
	 * @see Integer#toString
	 */
	public CustomNumberEditor(Class<? extends Number> numberClass, boolean allowEmpty) throws IllegalArgumentException {
		this(numberClass, null, allowEmpty);
	}

	/**
	 * Create a new CustomNumberEditor instance, using the given NumberFormat
	 * for parsing and rendering.
	 * <p>The allowEmpty parameter states if an empty String should
	 * be allowed for parsing, i.e. get interpreted as <code>null</code> value.
	 * Else, an IllegalArgumentException gets thrown in that case.
	 * ************************************************************************
	 * ~$ 创建一个新的CustomNumberEditor实例,使用NumberFormat的解析和渲染.
	 * <p>allowEmpty参数状态如果应该允许空字符串解析,即得到解释为空值.
	 *    别的,却是IllegalArgumentException被扔在这种情况下.
	 * @param numberClass Number subclass to generate
	 *                    ~$ 数量生成子类
	 * @param numberFormat NumberFormat to use for parsing and rendering
	 *                     ~$ NumberFormat用于解析和渲染
	 * @param allowEmpty if empty strings should be allowed
	 * @throws IllegalArgumentException if an invalid numberClass has been specified
	 * @see NumberUtils#parseNumber(String, Class, NumberFormat)
	 * @see NumberFormat#parse
	 * @see NumberFormat#format
	 */
	public CustomNumberEditor(Class<? extends Number> numberClass, NumberFormat numberFormat, boolean allowEmpty)
	    throws IllegalArgumentException {

		if (numberClass == null || !Number.class.isAssignableFrom(numberClass)) {
			throw new IllegalArgumentException("Property class must be a subclass of Number");
		}
		this.numberClass = numberClass;
		this.numberFormat = numberFormat;
		this.allowEmpty = allowEmpty;
	}


	/**
	 * Parse the Number from the given text, using the specified NumberFormat.
	 * ***********************************************************************
	 * ~$ 从给定的文本解析数量,使用NumberFormat指定.
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		if (this.allowEmpty && !StringUtils.hasText(text)) {
			// Treat empty String as null value.
			/** 把空字符串作为null值. */
			setValue(null);
		}
		else if (this.numberFormat != null) {
			// Use given NumberFormat for parsing text.
			/** 使用NumberFormat解析文本.*/
			setValue(NumberUtils.parseNumber(text, this.numberClass, this.numberFormat));
		}
		else {
			// Use default valueOf methods for parsing text.
			/** 使用默认的方法解析文本.*/
			setValue(NumberUtils.parseNumber(text, this.numberClass));
		}
	}

	/**
	 * Coerce a Number value into the required target class, if necessary.
	 * *******************************************************************
	 * ~$ 强迫一个数量值所需的目标类,如果必要的.
	 */
	@Override
	public void setValue(Object value) {
		if (value instanceof Number) {
			super.setValue(NumberUtils.convertNumberToTargetClass((Number) value, this.numberClass));
		}
		else {
			super.setValue(value);
		}
	}

	/**
	 * Format the Number as String, using the specified NumberFormat.
	 * **************************************************************
	 * ~$ 格式字符串的数量,使用NumberFormat指定.
	 */
	@Override
	public String getAsText() {
		Object value = getValue();
		if (value == null) {
			return "";
		}
		if (this.numberFormat != null) {
			// Use NumberFormat for rendering value.
			/** 使用NumberFormat呈现价值. */
			return this.numberFormat.format(value);
		}
		else {
			// Use toString method for rendering value.
			/** 使用toString方法呈现的价值. */
			return value.toString();
		}
	}

}
