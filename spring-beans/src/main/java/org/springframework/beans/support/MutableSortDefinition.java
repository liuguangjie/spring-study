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

package org.springframework.beans.support;

import java.io.Serializable;

import org.springframework.util.StringUtils;

/**
 * Mutable implementation of the {@link SortDefinition} interface.
 * Supports toggling the ascending value on setting the same property again.
 * *************************************************************************
 * ~$ 可变的实现{@link SortDefinition }接口.支持切换设置相同的属性提升价值.
 * @author Juergen Hoeller
 * @author Jean-Pierre Pawlak
 * @since 26.05.2003
 * @see #setToggleAscendingOnProperty
 */
public class MutableSortDefinition implements SortDefinition, Serializable {

	private String property = "";

	private boolean ignoreCase = true;

	private boolean ascending = true;

	private boolean toggleAscendingOnProperty = false;


	/**
	 * Create an empty MutableSortDefinition,
	 * to be populated via its bean properties.
	 * ****************************************
	 * ~$ 创建一个空MutableSortDefinition,通过其填充bean属性.
	 * @see #setProperty
	 * @see #setIgnoreCase
	 * @see #setAscending
	 */
	public MutableSortDefinition() {
	}

	/**
	 * Copy constructor: create a new MutableSortDefinition
	 * that mirrors the given sort definition.
	 * ****************************************************
	 * ~$ 拷贝构造函数:创建一个新的MutableSortDefinition镜子给定的类定义.
	 * @param source the original sort definition
	 */
	public MutableSortDefinition(SortDefinition source) {
		this.property = source.getProperty();
		this.ignoreCase = source.isIgnoreCase();
		this.ascending = source.isAscending();
	}

	/**
	 * Create a MutableSortDefinition for the given settings.
	 * *******************************************************
	 * ~$ 创建一个MutableSortDefinition给定设置.
	 * @param property the property to compare
	 * @param ignoreCase whether upper and lower case in String values should be ignored
	 * @param ascending whether to sort ascending (true) or descending (false)
	 */
	public MutableSortDefinition(String property, boolean ignoreCase, boolean ascending) {
		this.property = property;
		this.ignoreCase = ignoreCase;
		this.ascending = ascending;
	}

	/**
	 * Create a new MutableSortDefinition.
	 * @param toggleAscendingOnSameProperty whether to toggle the ascending flag
	 * if the same property gets set again (that is, <code>setProperty</code> gets
	 * called with already set property name again).
	 */
	public MutableSortDefinition(boolean toggleAscendingOnSameProperty) {
		this.toggleAscendingOnProperty = toggleAscendingOnSameProperty;
	}


	/**
	 * Set the property to compare.
	 * <p>If the property was the same as the current, the sort is reversed if
	 * "toggleAscendingOnProperty" is activated, else simply ignored.
	 * ***********************************************************************
	 * ~$ 将属性设置为比较.
	 * <p>如果属性是一样的,那种是逆转如果"toggleAscendingOnProperty"被激活,完全不理会.
	 * @see #setToggleAscendingOnProperty
	 */
	public void setProperty(String property) {
		if (!StringUtils.hasLength(property)) {
			this.property = "";
		}
		else {
			// Implicit toggling of ascending?
			/** 隐式切换的提升? */
			if (isToggleAscendingOnProperty()) {
				this.ascending = (!property.equals(this.property) || !this.ascending);
			}
			this.property = property;
		}
	}

	public String getProperty() {
		return this.property;
	}

	/**
	 * Set whether upper and lower case in String values should be ignored.
	 * ********************************************************************
	 * ~$ 设置大写和小写的字符串值是否应该被忽略.
	 */
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}

	public boolean isIgnoreCase() {
		return this.ignoreCase;
	}

	/**
	 * Set whether to sort ascending (true) or descending (false).
	 * **********************************************************
	 * ~$ 设置是否升序(真正的)或降序(假).
	 */
	public void setAscending(boolean ascending) {
		this.ascending = ascending;
	}

	public boolean isAscending() {
		return this.ascending;
	}

	/**
	 * Set whether to toggle the ascending flag if the same property gets set again
	 * (that is, {@link #setProperty} gets called with already set property name again).
	 * <p>This is particularly useful for parameter binding through a web request,
	 * where clicking on the field header again might be supposed to trigger a
	 * resort for the same field but opposite order.
	 * *********************************************************************************
	 * ~$ 设置是否切换升国旗,如果同样的属性被设置
	 *    (也就是说,{@link #setProperty }调用已经设置和属性名).
	 * <p>来说,这是特别有用的参数通过web请求绑定,
	 *    再次单击字段标题会在哪里应该触发一个度假村为同一领域,但相反的顺序.
	 */
	public void setToggleAscendingOnProperty(boolean toggleAscendingOnProperty) {
		this.toggleAscendingOnProperty = toggleAscendingOnProperty;
	}

	/**
	 * Return whether to toggle the ascending flag if the same property gets set again
	 * (that is, {@link #setProperty} gets called with already set property name again).
	 * *********************************************************************************
	 * ~$ 返回是否切换升国旗,如果同样的属性被设置(也就是说,{@link #setProperty }调用已经设置和属性名).
	 */
	public boolean isToggleAscendingOnProperty() {
		return this.toggleAscendingOnProperty;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SortDefinition)) {
			return false;
		}
		SortDefinition otherSd = (SortDefinition) other;
		return (getProperty().equals(otherSd.getProperty()) &&
		    isAscending() == otherSd.isAscending() && isIgnoreCase() == otherSd.isIgnoreCase());
	}

	@Override
	public int hashCode() {
		int hashCode = getProperty().hashCode();
		hashCode = 29 * hashCode + (isIgnoreCase() ? 1 : 0);
		hashCode = 29 * hashCode + (isAscending() ? 1 : 0);
		return hashCode;
	}

}
