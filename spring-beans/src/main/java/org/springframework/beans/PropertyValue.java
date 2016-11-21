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

package org.springframework.beans;

import java.beans.PropertyDescriptor;
import java.io.Serializable;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Object to hold information and value for an individual bean property.
 * Using an object here, rather than just storing all properties in
 * a map keyed by property name, allows for more flexibility, and the
 * ability to handle indexed properties etc in an optimized way.
 *
 * <p>Note that the value doesn't need to be the final required type:
 * A {@link BeanWrapper} implementation should handle any necessary conversion,
 * as this object doesn't know anything about the objects it will be applied to.
 *
 * *****************************************************************************
 * ~$ 对象来保存信息和价值对于单个bean属性.
 *    使用一个对象,而不是存储在所有属性
 *    地图的属性名,允许更大的灵活性,
 *    能力等以一种优化的方式来处理索引属性
 *
 * <p>注意所需的价值不需要最后的类型:
 *    一个{@link BeanWrapper }实现应该处理任何必要的转换,
 *    这个对象不了解它将被应用到对象
 *
 * @author Rod Johnson
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 13 May 2001
 * @see PropertyValues
 * @see BeanWrapper
 */
public class PropertyValue extends BeanMetadataAttributeAccessor implements Serializable {

	private final String name;

	private final Object value;

	private Object source;

	private boolean optional = false;

	private boolean converted = false;

	private Object convertedValue;

	/** Package-visible field that indicates whether conversion is necessary */
	/** Package-visible字段表明是否转换是必要的 */
	volatile Boolean conversionNecessary;

	/** Package-visible field for caching the resolved property path tokens */
	/** Package-visible字段缓存解决产权路径标记*/
	volatile Object resolvedTokens;

	/** Package-visible field for caching the resolved PropertyDescriptor */
	/** Package-visible字段缓存PropertyDescriptor解决*/
	volatile PropertyDescriptor resolvedDescriptor;


	/**
	 * Create a new PropertyValue instance.
	 * @param name the name of the property (never <code>null</code>)
	 * @param value the value of the property (possibly before type conversion)
	 */
	public PropertyValue(String name, Object value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Copy constructor.
	 * @param original the PropertyValue to copy (never <code>null</code>)
	 */
	public PropertyValue(PropertyValue original) {
		Assert.notNull(original, "Original must not be null");
		this.name = original.getName();
		this.value = original.getValue();
		this.source = original.getSource();
		this.optional = original.isOptional();
		this.converted = original.converted;
		this.convertedValue = original.convertedValue;
		this.conversionNecessary = original.conversionNecessary;
		this.resolvedTokens = original.resolvedTokens;
		this.resolvedDescriptor = original.resolvedDescriptor;
		copyAttributesFrom(original);
	}

	/**
	 * Constructor that exposes a new value for an original value holder.
	 * The original holder will be exposed as source of the new holder.
	 * ******************************************************************
	 * ~$ 构造函数,使一个新值原始值持有人.
	 *    原持有人将公开为新持有人的来源
	 * @param original the PropertyValue to link to (never <code>null</code>)
	 * @param newValue the new value to apply
	 */
	public PropertyValue(PropertyValue original, Object newValue) {
		Assert.notNull(original, "Original must not be null");
		this.name = original.getName();
		this.value = newValue;
		this.source = original;
		this.optional = original.isOptional();
		this.conversionNecessary = original.conversionNecessary;
		this.resolvedTokens = original.resolvedTokens;
		this.resolvedDescriptor = original.resolvedDescriptor;
		copyAttributesFrom(original);
	}


	/**
	 * Return the name of the property.
	 * ~$ 返回属性的名称
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * Return the value of the property.
	 * <p>Note that type conversion will <i>not</i> have occurred here.
	 * It is the responsibility of the BeanWrapper implementation to
	 * perform type conversion.
	 * ***************************************************************
	 * ~$ 返回属性的值
	 * <p>请注意,类型转换将 <i>not</i>  这里发生过
	 *    BeanWrapper实现的责任 执行类型转换
	 */
	public Object getValue() {
		return this.value;
	}

	/**
	 * Return the original PropertyValue instance for this value holder.
	 * *****************************************************************
	 * ~$ 这个值持有人交回原PropertyValue实例
	 * @return the original PropertyValue (either a source of this
	 * value holder or this value holder itself).
	 */
	public PropertyValue getOriginalPropertyValue() {
		PropertyValue original = this;
		while (original.source instanceof PropertyValue && original.source != original) {
			original = (PropertyValue) original.source;
		}
		return original;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public boolean isOptional() {
		return this.optional;
	}

	/**
	 * Return whether this holder contains a converted value already (<code>true</code>),
	 * or whether the value still needs to be converted (<code>false</code>).
	 * *********************************************************************************
	 * ~$ 返回这是否持有人已经包含一个转换值(<code>true</code>),
	 *    是否仍然需要转换价值 (<code>false</code>).
	 */
	public synchronized boolean isConverted() {
		return this.converted;
	}

	/**
	 * Set the converted value of the constructor argument,
	 * after processed type conversion.
	 * ****************************************************
	 * ~$ 构造函数参数的设置转换后的值  后加工类型转换
	 */
	public synchronized void setConvertedValue(Object value) {
		this.converted = true;
		this.convertedValue = value;
	}

	/**
	 * Return the converted value of the constructor argument,
	 * after processed type conversion.
	 * ******************************************************
	 * ~$ 返回转换后的值的构造函数参数, 后加工类型转换
	 */
	public synchronized Object getConvertedValue() {
		return this.convertedValue;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof PropertyValue)) {
			return false;
		}
		PropertyValue otherPv = (PropertyValue) other;
		return (this.name.equals(otherPv.name) &&
				ObjectUtils.nullSafeEquals(this.value, otherPv.value) &&
				ObjectUtils.nullSafeEquals(this.source, otherPv.source));
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() * 29 + ObjectUtils.nullSafeHashCode(this.value);
	}

	@Override
	public String toString() {
		return "bean property '" + this.name + "'";
	}

}
