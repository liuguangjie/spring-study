/*
 * Copyright 2002-2010 the original author or authors.
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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link PropertyValues} interface.
 * Allows simple manipulation of properties, and provides constructors
 * to support deep copy and construction from a Map.
 * *******************************************************************
 * ~$ 默认实现 {@link PropertyValues} 接口.
 *    允许简单的操纵性能, 并提供构造函数 支持重Map中 深度copy和创建
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 13 May 2001
 */
public class MutablePropertyValues implements PropertyValues, Serializable {

	private final List<PropertyValue> propertyValueList;

	private Set<String> processedProperties;

	private volatile boolean converted = false;


	/**
	 * Creates a new empty MutablePropertyValues object.
	 * <p>Property values can be added with the <code>add</code> method.
	 * *****************************************************************
	 * ~$ 创建一个新的空MutablePropertyValues对象
	 * <p> 可以添加属性值与 <code>add</code> 方法
	 * @see #add(String, Object)
	 */
	public MutablePropertyValues() {
		this.propertyValueList = new ArrayList<PropertyValue>(0);
	}

	/**
	 * Deep copy constructor. Guarantees PropertyValue references
	 * are independent, although it can't deep copy objects currently
	 * referenced by individual PropertyValue objects.
	 * **************************************************************
	 * ~$ 深度复制 构造函数. 保证PropertyValue引用是独立的,
	 *    虽然它不能深复制对象目前个人PropertyValue引用的对象
	 * @param original the PropertyValues to copy
	 * @see #addPropertyValues(PropertyValues)
	 */
	public MutablePropertyValues(PropertyValues original) {
		// We can optimize this because it's all new:
		// There is no replacement of existing property values.
		/** 我们可以优化这个,因为它是所有 新: */
		/** 没有替换现有的属性值 */
		if (original != null) {
			PropertyValue[] pvs = original.getPropertyValues();
			this.propertyValueList = new ArrayList<PropertyValue>(pvs.length);
			for (PropertyValue pv : pvs) {
				this.propertyValueList.add(new PropertyValue(pv));
			}
		}
		else {
			this.propertyValueList = new ArrayList<PropertyValue>(0);
		}
	}

	/**
	 * Construct a new MutablePropertyValues object from a Map.
	 * @param original Map with property values keyed by property name Strings
	 * @see #addPropertyValues(Map)
	 */
	public MutablePropertyValues(Map<?, ?> original) {
		// We can optimize this because it's all new:
		// There is no replacement of existing property values.
		if (original != null) {
			this.propertyValueList = new ArrayList<PropertyValue>(original.size());
			for (Map.Entry entry : original.entrySet()) {
				this.propertyValueList.add(new PropertyValue(entry.getKey().toString(), entry.getValue()));
			}
		}
		else {
			this.propertyValueList = new ArrayList<PropertyValue>(0);
		}
	}

	/**
	 * Construct a new MutablePropertyValues object using the given List of
	 * PropertyValue objects as-is.
	 * <p>This is a constructor for advanced usage scenarios.
	 * It is not intended for typical programmatic use.
	 * ********************************************************************
	 * ~$ 构造一个新的MutablePropertyValues对象使用给定初始的PropertyValue对象列表.
	 * <p> 这是一个先进的使用场景的构造函数.它不是用于典型的编程使用
	 * @param propertyValueList List of PropertyValue objects
	 */
	public MutablePropertyValues(List<PropertyValue> propertyValueList) {
		this.propertyValueList =
				(propertyValueList != null ? propertyValueList : new ArrayList<PropertyValue>());
	}


	/**
	 * Return the underlying List of PropertyValue objects in its raw form.
	 * The returned List can be modified directly, although this is not recommended.
	 * <p>This is an accessor for optimized access to all PropertyValue objects.
	 * It is not intended for typical programmatic use.
	 * ****************************************************************************
	 * ~$ 返回底层PropertyValue列表对象在它的原始状态
	 *    返回的列表可以直接修改,虽然并不推荐这样做
	 * <p> 这是一个优化访问所有PropertyValue对象的访问器.
	 *     它不是用于典型的编程使用
	 */
	public List<PropertyValue> getPropertyValueList() {
		return this.propertyValueList;
	}

	/**
	 * Return the number of PropertyValue entries in the list.
	 * *******************************************************
	 * ~$ 返回PropertyValue列表中的条目的数量
	 */
	public int size() {
		return this.propertyValueList.size();
	}

	/**
	 * Copy all given PropertyValues into this object. Guarantees PropertyValue
	 * references are independent, although it can't deep copy objects currently
	 * referenced by individual PropertyValue objects.
	 * *************************************************************************
	 * ~$ 所有给定propertyvalue复制到这个对象.
	 *    担保PropertyValue引用是独立的,
	 *    尽管它不能深复制对象目前个人PropertyValue引用的对象
	 * @param other the PropertyValues to copy
	 * @return this in order to allow for adding multiple property values in a chain
	 */
	public MutablePropertyValues addPropertyValues(PropertyValues other) {
		if (other != null) {
			PropertyValue[] pvs = other.getPropertyValues();
			for (PropertyValue pv : pvs) {
				addPropertyValue(new PropertyValue(pv));
			}
		}
		return this;
	}

	/**
	 * Add all property values from the given Map.
	 * *******************************************
	 * ~$ 添加所有属性值从给定的Map
	 * @param other Map with property values keyed by property name,
	 * which must be a String
	 * @return this in order to allow for adding multiple property values in a chain
	 */
	public MutablePropertyValues addPropertyValues(Map<?, ?> other) {
		if (other != null) {
			for (Map.Entry<?, ?> entry : other.entrySet()) {
				addPropertyValue(new PropertyValue(entry.getKey().toString(), entry.getValue()));
			}
		}
		return this;
	}

	/**
	 * Add a PropertyValue object, replacing any existing one for the
	 * corresponding property or getting merged with it (if applicable).
	 * *****************************************************************
	 * ~$ 添加一个PropertyValue对象,替换任何现有的一个相应的产权或合并(如果适用)
	 * @param pv PropertyValue object to add
	 * @return this in order to allow for adding multiple property values in a chain
	 */
	public MutablePropertyValues addPropertyValue(PropertyValue pv) {
		for (int i = 0; i < this.propertyValueList.size(); i++) {
			PropertyValue currentPv = this.propertyValueList.get(i);
			if (currentPv.getName().equals(pv.getName())) {
				pv = mergeIfRequired(pv, currentPv);
				setPropertyValueAt(pv, i);
				return this;
			}
		}
		this.propertyValueList.add(pv);
		return this;
	}

	/**
	 * Overloaded version of <code>addPropertyValue</code> that takes
	 * a property name and a property value.
	 * <p>Note: As of Spring 3.0, we recommend using the more concise
	 * and chaining-capable variant {@link #add}.
	 * **************************************************************
	 * ~$ 重载版本的 <code>addPropertyValue</code> 属性名和属性值.
	 * <p>注意:在Spring 3.0中,我们建议使用更简洁和{@link #add} chaining-capable变体。
	 * @param propertyName name of the property
	 * @param propertyValue value of the property
	 * @see #addPropertyValue(PropertyValue)
	 */
	public void addPropertyValue(String propertyName, Object propertyValue) {
		addPropertyValue(new PropertyValue(propertyName, propertyValue));
	}

	/**
	 * Add a PropertyValue object, replacing any existing one for the
	 * corresponding property or getting merged with it (if applicable).
	 * *****************************************************************
	 * ~$ 添加一个PropertyValue对象,替换任何现有的一个相应的产权或合并(如果适用)
	 * @param propertyName name of the property
	 * @param propertyValue value of the property
	 * @return this in order to allow for adding multiple property values in a chain
	 */
	public MutablePropertyValues add(String propertyName, Object propertyValue) {
		addPropertyValue(new PropertyValue(propertyName, propertyValue));
		return this;
	}

	/**
	 * Modify a PropertyValue object held in this object.
	 * Indexed from 0.
	 * *************************************************
	 * ~$ 修改PropertyValue对象在这个对象举行.
	 *    索引从0
	 */
	public void setPropertyValueAt(PropertyValue pv, int i) {
		this.propertyValueList.set(i, pv);
	}

	/**
	 * Merges the value of the supplied 'new' {@link PropertyValue} with that of
	 * the current {@link PropertyValue} if merging is supported and enabled.
	 * *************************************************************************
	 * ~$ 合并的价值提供的 'new' {@link PropertyValue}
	 *    与当前{@link PropertyValue } 如果合并是支持和启用
	 * @see Mergeable
	 */
	private PropertyValue mergeIfRequired(PropertyValue newPv, PropertyValue currentPv) {
		Object value = newPv.getValue();
		if (value instanceof Mergeable) {
			Mergeable mergeable = (Mergeable) value;
			if (mergeable.isMergeEnabled()) {
				Object merged = mergeable.merge(currentPv.getValue());
				return new PropertyValue(newPv.getName(), merged);
			}
		}
		return newPv;
	}

	/**
	 * Remove the given PropertyValue, if contained.
	 * *********************************************
	 * ~$ 删除给定PropertyValue,如果包含
	 * @param pv the PropertyValue to remove
	 */
	public void removePropertyValue(PropertyValue pv) {
		this.propertyValueList.remove(pv);
	}

	/**
	 * Overloaded version of <code>removePropertyValue</code> that takes a property name.
	 * **********************************************************************************
	 * ~$ 重载版本的 <code>removePropertyValue</code>  一个属性的名字
	 * @param propertyName name of the property
	 * @see #removePropertyValue(PropertyValue)
	 */
	public void removePropertyValue(String propertyName) {
		this.propertyValueList.remove(getPropertyValue(propertyName));
	}


	public PropertyValue[] getPropertyValues() {
		return this.propertyValueList.toArray(new PropertyValue[this.propertyValueList.size()]);
	}

	public PropertyValue getPropertyValue(String propertyName) {
		for (PropertyValue pv : this.propertyValueList) {
			if (pv.getName().equals(propertyName)) {
				return pv;
			}
		}
		return null;
	}

	public PropertyValues changesSince(PropertyValues old) {
		MutablePropertyValues changes = new MutablePropertyValues();
		if (old == this) {
			return changes;
		}

		// for each property value in the new set
		/** 对于新的集合中的每个属性值 */
		for (PropertyValue newPv : this.propertyValueList) {
			// if there wasn't an old one, add it
			/** 如果没有一个旧的，加上它*/
			PropertyValue pvOld = old.getPropertyValue(newPv.getName());
			if (pvOld == null) {
				changes.addPropertyValue(newPv);
			}
			else if (!pvOld.equals(newPv)) {
				// it's changed  它改变了
				changes.addPropertyValue(newPv);
			}
		}
		return changes;
	}

	public boolean contains(String propertyName) {
		return (getPropertyValue(propertyName) != null ||
				(this.processedProperties != null && this.processedProperties.contains(propertyName)));
	}

	public boolean isEmpty() {
		return this.propertyValueList.isEmpty();
	}


	/**
	 * Register the specified property as "processed" in the sense
	 * of some processor calling the corresponding setter method
	 * outside of the PropertyValue(s) mechanism.
	 * <p>This will lead to <code>true</code> being returned from
	 * a {@link #contains} call for the specified property.
	 * **********************************************************
	 * ~$ 注册指定的属性为"processed"的一些处理 调用相应的setter方法以外的PropertyValue(s)机制
	 * @param propertyName the name of the property.
	 */
	public void registerProcessedProperty(String propertyName) {
		if (this.processedProperties == null) {
			this.processedProperties = new HashSet<String>();
		}
		this.processedProperties.add(propertyName);
	}

	/**
	 * Mark this holder as containing converted values only
	 * (i.e. no runtime resolution needed anymore).
	 *
	 * ****************************************************
	 * ~$ 标记持有者仅转换值 (即不需要运行时解决了)
	 */
	public void setConverted() {
		this.converted = true;
	}

	/**
	 * Return whether this holder contains converted values only (<code>true</code>),
	 * or whether the values still need to be converted (<code>false</code>).
	 * ******************************************************************************
	 * ~$ 只返回这个持有人是否包含转换值 (<code>true</code>),
	 *   或者是否要转换的值仍然需要 (<code>false</code>).
	 */
	public boolean isConverted() {
		return this.converted;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof MutablePropertyValues)) {
			return false;
		}
		MutablePropertyValues that = (MutablePropertyValues) other;
		return this.propertyValueList.equals(that.propertyValueList);
	}

	@Override
	public int hashCode() {
		return this.propertyValueList.hashCode();
	}

	@Override
	public String toString() {
		PropertyValue[] pvs = getPropertyValues();
		StringBuilder sb = new StringBuilder("PropertyValues: length=").append(pvs.length);
		if (pvs.length > 0) {
			sb.append("; ").append(StringUtils.arrayToDelimitedString(pvs, "; "));
		}
		return sb.toString();
	}

}
