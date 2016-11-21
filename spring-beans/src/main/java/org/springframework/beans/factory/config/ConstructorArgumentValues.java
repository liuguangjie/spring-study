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

package org.springframework.beans.factory.config;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.Mergeable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

/**
 * Holder for constructor argument values, typically as part of a bean definition.
 *
 * <p>Supports values for a specific index in the constructor argument list
 * as well as for generic argument matches by type.
 *
 * ********************************************************************************
 * ~$ 保持者构造函数参数值,通常作为一个bean定义的一部分.
 *
 * <p>支持值为一个特定的索引在构造函数的参数列表 以及泛型类型参数匹配
 * @author Juergen Hoeller
 * @since 09.11.2003
 * @see BeanDefinition#getConstructorArgumentValues
 */
public class ConstructorArgumentValues {

	private final Map<Integer, ValueHolder> indexedArgumentValues = new LinkedHashMap<Integer, ValueHolder>(0);

	private final List<ValueHolder> genericArgumentValues = new LinkedList<ValueHolder>();


	/**
	 * Create a new empty ConstructorArgumentValues object.
	 * ***************************************************
	 * ~$ 创建一个新的实体 ConstructorArgumentValues 对象
	 */
	public ConstructorArgumentValues() {
	}

	/**
	 * Deep copy constructor.
	 * ~$ 深度copy 构造函数
	 * @param original the ConstructorArgumentValues to copy
	 */
	public ConstructorArgumentValues(ConstructorArgumentValues original) {
		addArgumentValues(original);
	}


	/**
	 * Copy all given argument values into this object, using separate holder
	 * instances to keep the values independent from the original object.
	 * <p>Note: Identical ValueHolder instances will only be registered once,
	 * to allow for merging and re-merging of argument value definitions. Distinct
	 * ValueHolder instances carrying the same content are of course allowed.
	 * ***************************************************************************
	 * ~$ 所有给定的参数值复制到该对象,使用单独的持有人实例保持独立于原始对象的值.
	 *
	 * <p>注:相同ValueHolder实例只能注册一次,以允许合并和重新合并参数值的定义.
	 * 	ValueHolder实例携带相同的内容当然是允许的
	 */
	public void addArgumentValues(ConstructorArgumentValues other) {
		if (other != null) {
			for (Map.Entry<Integer, ValueHolder> entry : other.indexedArgumentValues.entrySet()) {
				addOrMergeIndexedArgumentValue(entry.getKey(), entry.getValue().copy());
			}
			for (ValueHolder valueHolder : other.genericArgumentValues) {
				if (!this.genericArgumentValues.contains(valueHolder)) {
					addOrMergeGenericArgumentValue(valueHolder.copy());
				}
			}
		}
	}


	/**
	 * Add an argument value for the given index in the constructor argument list.
	 * ***************************************************************************
	 * ~$ 添加一个参数值给定索引在构造函数的参数列表
	 * @param index the index in the constructor argument list
	 *              构造函数参数列表中的索引
	 * @param value the argument value
	 */
	public void addIndexedArgumentValue(int index, Object value) {
		addIndexedArgumentValue(index, new ValueHolder(value));
	}

	/**
	 * Add an argument value for the given index in the constructor argument list.
	 * ***************************************************************************
	 * ~$ 添加一个参数值给定索引在构造函数的参数列表
	 * @param index the index in the constructor argument list
	 * @param value the argument value
	 * @param type the type of the constructor argument
	 */
	public void addIndexedArgumentValue(int index, Object value, String type) {
		addIndexedArgumentValue(index, new ValueHolder(value, type));
	}

	/**
	 * Add an argument value for the given index in the constructor argument list.
	 * ***************************************************************************
	 * ~$ 添加一个参数值给定索引在构造函数的参数列表
	 * @param index the index in the constructor argument list
	 * @param newValue the argument value in the form of a ValueHolder
	 */
	public void addIndexedArgumentValue(int index, ValueHolder newValue) {
		Assert.isTrue(index >= 0, "Index must not be negative");
		Assert.notNull(newValue, "ValueHolder must not be null");
		addOrMergeIndexedArgumentValue(index, newValue);
	}

	/**
	 * Add an argument value for the given index in the constructor argument list,
	 *
	 * merging the new value (typically a collection) with the current value
	 * if demanded: see {@link Mergeable}.
	 * **************************************************************************
	 * ~$ 添加一个参数值给定索引在构造函数的参数列表,
	 *
	 *    合并新值与当前值(通常是一个集合)
	 *    如果要求: see {@link Mergeable}.
	 * @param key the index in the constructor argument list
	 * @param newValue the argument value in the form of a ValueHolder
	 */
	private void addOrMergeIndexedArgumentValue(Integer key, ValueHolder newValue) {
		ValueHolder currentValue = this.indexedArgumentValues.get(key);
		if (currentValue != null && newValue.getValue() instanceof Mergeable) {
			Mergeable mergeable = (Mergeable) newValue.getValue();
			if (mergeable.isMergeEnabled()) {
				newValue.setValue(mergeable.merge(currentValue.getValue()));
			}
		}
		this.indexedArgumentValues.put(key, newValue);
	}

	/**
	 * Check whether an argument value has been registered for the given index.
	 * ************************************************************************
	 * ~$ 检查是否已经注册一个参数值为给定的索引
	 * @param index the index in the constructor argument list
	 *              构造函数参数列表中的索引
	 */
	public boolean hasIndexedArgumentValue(int index) {
		return this.indexedArgumentValues.containsKey(index);
	}

	/**
	 * Get argument value for the given index in the constructor argument list.
	 * ************************************************************************
	 * ~$ 在构造函数参数列表中获取给定索引的参数值
	 * @param index the index in the constructor argument list
	 *              ~$ 构造函数参数列表中的索引
	 * @param requiredType the type to match (can be <code>null</code> to match
	 * untyped values only)
	 *                     ~$ 可以是空  仅匹配类型
	 * @return the ValueHolder for the argument, or <code>null</code> if none set
	 * 					   ~$ ValueHolder 参数 , 如果没有设置为 null
	 */
	public ValueHolder getIndexedArgumentValue(int index, Class requiredType) {
		return getIndexedArgumentValue(index, requiredType, null);
	}

	/**
	 * Get argument value for the given index in the constructor argument list.
	 * ************************************************************************
	 * ~$ 在构造函数参数列表中获取给定索引的参数值
	 * @param index the index in the constructor argument list
	 *              ~$ 构造函数参数列表中的索引
	 * @param requiredType the type to match (can be <code>null</code> to match
	 * untyped values only)
	 *                     ~$ 可以是空  仅匹配类型
	 * @param requiredName the type to match (can be <code>null</code> to match
	 * unnamed values only)
	 *                     ~$ 可以是空  仅匹配名称
	 * @return the ValueHolder for the argument, or <code>null</code> if none set
	 * 			~$ ValueHolder 参数 , 如果没有设置为 null
	 */
	public ValueHolder getIndexedArgumentValue(int index, Class requiredType, String requiredName) {
		Assert.isTrue(index >= 0, "Index must not be negative");
		ValueHolder valueHolder = this.indexedArgumentValues.get(index);
		if (valueHolder != null &&
				(valueHolder.getType() == null ||
						(requiredType != null && ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) &&
				(valueHolder.getName() == null ||
						(requiredName != null && requiredName.equals(valueHolder.getName())))) {
			return valueHolder;
		}
		return null;
	}

	/**
	 * Return the map of indexed argument values.
	 * ******************************************
	 * ~$ 返回索引参数值的映射
	 * @return unmodifiable Map with Integer index as key and ValueHolder as value
	 * @see ValueHolder
	 */
	public Map<Integer, ValueHolder> getIndexedArgumentValues() {
		return Collections.unmodifiableMap(this.indexedArgumentValues);
	}


	/**
	 * Add a generic argument value to be matched by type.
	 * <p>Note: A single generic argument value will just be used once,
	 * rather than matched multiple times.
	 * *****************************************************************
	 * ~$ 添加一个通用的参数值匹配的类型.
	 * <p>注意:一个通用的参数值将是使用一次,而不是多次匹配
	 * @param value the argument value
	 */
	public void addGenericArgumentValue(Object value) {
		this.genericArgumentValues.add(new ValueHolder(value));
	}

	/**
	 * Add a generic argument value to be matched by type.
	 * <p>Note: A single generic argument value will just be used once,
	 * rather than matched multiple times.
	 * *****************************************************************
	 * ~$ 添加一个通用的参数值匹配的类型.
	 * <p>注意:一个通用的参数值将是使用一次,而不是多次匹配
	 *
	 * @param value the argument value
	 * @param type the type of the constructor argument
	 */
	public void addGenericArgumentValue(Object value, String type) {
		this.genericArgumentValues.add(new ValueHolder(value, type));
	}

	/**
	 * Add a generic argument value to be matched by type or name (if available).
	 * <p>Note: A single generic argument value will just be used once,
	 * rather than matched multiple times.
	 * *****************************************************************
	 * ~$ 添加一个通用的参数值匹配的类型或名称(如果可用).
	 * <p>注意:一个通用的参数值将是使用一次,而不是多次匹配
	 * @param newValue the argument value in the form of a ValueHolder
	 * <p>Note: Identical ValueHolder instances will only be registered once,
	 * to allow for merging and re-merging of argument value definitions. Distinct
	 * ValueHolder instances carrying the same content are of course allowed.
	 * <p>注:相同ValueHolder实例只能注册一次,以允许合并和重新合并参数值的定义.
	 *    不同ValueHolder实例携带相同的内容当然是允许的
	 */
	public void addGenericArgumentValue(ValueHolder newValue) {
		Assert.notNull(newValue, "ValueHolder must not be null");
		if (!this.genericArgumentValues.contains(newValue)) {
			addOrMergeGenericArgumentValue(newValue);
		}
	}

	/**
	 * Add a generic argument value, merging the new value (typically a collection)
	 * with the current value if demanded: see {@link Mergeable}.
	 * ****************************************************************************
	 * ~$ 添加一个通用的参数值,融合新值(通常是一个集合)
	 *    当前值如果要求:see {@link Mergeable}
	 * @param newValue the argument value in the form of a ValueHolder
	 */
	private void addOrMergeGenericArgumentValue(ValueHolder newValue) {
		if (newValue.getName() != null) {
			for (Iterator<ValueHolder> it = this.genericArgumentValues.iterator(); it.hasNext();) {
				ValueHolder currentValue = it.next();
				if (newValue.getName().equals(currentValue.getName())) {
					if (newValue.getValue() instanceof Mergeable) {
						Mergeable mergeable = (Mergeable) newValue.getValue();
						if (mergeable.isMergeEnabled()) {
							newValue.setValue(mergeable.merge(currentValue.getValue()));
						}
					}
					it.remove();
				}
			}
		}
		this.genericArgumentValues.add(newValue);
	}

	/**
	 * Look for a generic argument value that matches the given type.
	 * **************************************************************
	 * ~$ 找一个通用的参数值相匹配的特定类型
	 * @param requiredType the type to match
	 * @return the ValueHolder for the argument, or <code>null</code> if none set
	 */
	public ValueHolder getGenericArgumentValue(Class requiredType) {
		return getGenericArgumentValue(requiredType, null, null);
	}

	/**
	 * Look for a generic argument value that matches the given type.
	 * **************************************************************
	 * ~$ 找一个通用的参数值相匹配的特定类型
	 * @param requiredType the type to match
	 * @param requiredName the name to match
	 * @return the ValueHolder for the argument, or <code>null</code> if none set
	 */
	public ValueHolder getGenericArgumentValue(Class requiredType, String requiredName) {
		return getGenericArgumentValue(requiredType, requiredName, null);
	}

	/**
	 * Look for the next generic argument value that matches the given type,
	 * ignoring argument values that have already been used in the current
	 * resolution process.
	 * ********************************************************************
	 * ~$ 寻找下一个通用的参数值相匹配的特定类型,忽略参数值已经被用于当前的解决过程
	 * @param requiredType the type to match (can be <code>null</code> to find
	 * an arbitrary next generic argument value)
	 * @param requiredName the name to match (can be <code>null</code> to not
	 * match argument values by name)
	 * @param usedValueHolders a Set of ValueHolder objects that have already been used
	 * in the current resolution process and should therefore not be returned again
	 *                         ~$ 一组ValueHolder对象已经被使用 在当前解决过程,因此不应再返回
	 * @return the ValueHolder for the argument, or <code>null</code> if none found
	 */
	public ValueHolder getGenericArgumentValue(Class requiredType, String requiredName, Set<ValueHolder> usedValueHolders) {
		for (ValueHolder valueHolder : this.genericArgumentValues) {
			if (usedValueHolders != null && usedValueHolders.contains(valueHolder)) {
				continue;
			}
			if (valueHolder.getName() != null &&
					(requiredName == null || !valueHolder.getName().equals(requiredName))) {
				continue;
			}
			if (valueHolder.getType() != null &&
					(requiredType == null || !ClassUtils.matchesTypeName(requiredType, valueHolder.getType()))) {
				continue;
			}
			if (requiredType != null && valueHolder.getType() == null && valueHolder.getName() == null &&
					!ClassUtils.isAssignableValue(requiredType, valueHolder.getValue())) {
				continue;
			}
			return valueHolder;
		}
		return null;
	}

	/**
	 * Return the list of generic argument values.
	 * ~$ 返回通用的参数值的列表
	 * @return unmodifiable List of ValueHolders
	 * @see ValueHolder
	 */
	public List<ValueHolder> getGenericArgumentValues() {
		return Collections.unmodifiableList(this.genericArgumentValues);
	}


	/**
	 * Look for an argument value that either corresponds to the given index
	 * in the constructor argument list or generically matches by type.
	 * *********************************************************************
	 * ~$ 寻找一个参数值,对应于给定的索引 在构造函数中参数列表或一般匹配类型
	 * @param index the index in the constructor argument list
	 *              ~$ 构造函数参数列表中的索引
	 * @param requiredType the type to match
	 * @return the ValueHolder for the argument, or <code>null</code> if none set
	 */
	public ValueHolder getArgumentValue(int index, Class requiredType) {
		return getArgumentValue(index, requiredType, null, null);
	}

	/**
	 * Look for an argument value that either corresponds to the given index
	 * in the constructor argument list or generically matches by type.
	 * @param index the index in the constructor argument list
	 * @param requiredType the type to match
	 * @param requiredName the name to match
	 * @return the ValueHolder for the argument, or <code>null</code> if none set
	 */
	public ValueHolder getArgumentValue(int index, Class requiredType, String requiredName) {
		return getArgumentValue(index, requiredType, requiredName, null);
	}

	/**
	 * Look for an argument value that either corresponds to the given index
	 * in the constructor argument list or generically matches by type.
	 * @param index the index in the constructor argument list
	 * @param requiredType the type to match (can be <code>null</code> to find
	 * an untyped argument value)
	 * @param usedValueHolders a Set of ValueHolder objects that have already
	 * been used in the current resolution process and should therefore not
	 * be returned again (allowing to return the next generic argument match
	 * in case of multiple generic argument values of the same type)
	 * @return the ValueHolder for the argument, or <code>null</code> if none set
	 */
	public ValueHolder getArgumentValue(int index, Class requiredType, String requiredName, Set<ValueHolder> usedValueHolders) {
		Assert.isTrue(index >= 0, "Index must not be negative");
		ValueHolder valueHolder = getIndexedArgumentValue(index, requiredType, requiredName);
		if (valueHolder == null) {
			valueHolder = getGenericArgumentValue(requiredType, requiredName, usedValueHolders);
		}
		return valueHolder;
	}

	/**
	 * Return the number of argument values held in this instance,
	 * counting both indexed and generic argument values.
	 * **********************************************************
	 * ~$ 返回参数值的数量在这个实例中,计算索引和通用的参数值
	 */
	public int getArgumentCount() {
		return (this.indexedArgumentValues.size() + this.genericArgumentValues.size());
	}

	/**
	 * Return if this holder does not contain any argument values,
	 * neither indexed ones nor generic ones.
	 * **********************************************************
	 * ~$ 返回如果持有人不包含任何参数值,无论是索引的还是通用的
	 */
	public boolean isEmpty() {
		return (this.indexedArgumentValues.isEmpty() && this.genericArgumentValues.isEmpty());
	}

	/**
	 * Clear this holder, removing all argument values.
	 * ************************************************
	 * ~$ 清除这固定器移除所有参数值
	 */
	public void clear() {
		this.indexedArgumentValues.clear();
		this.genericArgumentValues.clear();
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ConstructorArgumentValues)) {
			return false;
		}
		ConstructorArgumentValues that = (ConstructorArgumentValues) other;
		if (this.genericArgumentValues.size() != that.genericArgumentValues.size() ||
				this.indexedArgumentValues.size() != that.indexedArgumentValues.size()) {
			return false;
		}
		Iterator<ValueHolder> it1 = this.genericArgumentValues.iterator();
		Iterator<ValueHolder> it2 = that.genericArgumentValues.iterator();
		while (it1.hasNext() && it2.hasNext()) {
			ValueHolder vh1 = it1.next();
			ValueHolder vh2 = it2.next();
			if (!vh1.contentEquals(vh2)) {
				return false;
			}
		}
		for (Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
			ValueHolder vh1 = entry.getValue();
			ValueHolder vh2 = that.indexedArgumentValues.get(entry.getKey());
			if (!vh1.contentEquals(vh2)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hashCode = 7;
		for (ValueHolder valueHolder : this.genericArgumentValues) {
			hashCode = 31 * hashCode + valueHolder.contentHashCode();
		}
		hashCode = 29 * hashCode;
		for (Map.Entry<Integer, ValueHolder> entry : this.indexedArgumentValues.entrySet()) {
			hashCode = 31 * hashCode + (entry.getValue().contentHashCode() ^ entry.getKey().hashCode());
		}
		return hashCode;
	}


	/**
	 * Holder for a constructor argument value, with an optional type
	 * attribute indicating the target type of the actual constructor argument.
	 * ***********************************************************************
	 * ~$ 保持者一个构造函数参数值,和一个可选的类型 属性表明实际的目标类型构造函数参数
	 */
	public static class ValueHolder implements BeanMetadataElement {

		private Object value;

		private String type;

		private String name;

		private Object source;

		private boolean converted = false;

		private Object convertedValue;

		/**
		 * Create a new ValueHolder for the given value.
		 * *********************************************
		 * ~$ 创建一个新的ValueHolder为给定的值
		 * @param value the argument value
		 */
		public ValueHolder(Object value) {
			this.value = value;
		}

		/**
		 * Create a new ValueHolder for the given value and type.
		 * *****************************************************
		 * ~$ 创建一个新的ValueHolder为给定的值和类型
		 * @param value the argument value
		 * @param type the type of the constructor argument
		 */
		public ValueHolder(Object value, String type) {
			this.value = value;
			this.type = type;
		}

		/**
		 * Create a new ValueHolder for the given value, type and name.
		 * ************************************************************
		 * ~$ 创建一个新的ValueHolder给定值的类型和名称
		 * @param value the argument value
		 * @param type the type of the constructor argument
		 * @param name the name of the constructor argument
		 */
		public ValueHolder(Object value, String type, String name) {
			this.value = value;
			this.type = type;
			this.name = name;
		}

		/**
		 * Set the value for the constructor argument.
		 * @see PropertyPlaceholderConfigurer
		 */
		public void setValue(Object value) {
			this.value = value;
		}

		/**
		 * Return the value for the constructor argument.
		 */
		public Object getValue() {
			return this.value;
		}

		/**
		 * Set the type of the constructor argument.
		 */
		public void setType(String type) {
			this.type = type;
		}

		/**
		 * Return the type of the constructor argument.
		 */
		public String getType() {
			return this.type;
		}

		/**
		 * Set the name of the constructor argument.
		 */
		public void setName(String name) {
			this.name = name;
		}

		/**
		 * Return the name of the constructor argument.
		 */
		public String getName() {
			return this.name;
		}

		/**
		 * Set the configuration source <code>Object</code> for this metadata element.
		 * <p>The exact type of the object will depend on the configuration mechanism used.
		 * ********************************************************************************
		 * ~$ 设置配置源<code>Object</code>的元数据元素.
		 * <p> 对象的确切类型将取决于所使用的配置机制
		 */
		public void setSource(Object source) {
			this.source = source;
		}

		public Object getSource() {
			return this.source;
		}

		/**
		 * Return whether this holder contains a converted value already (<code>true</code>),
		 * or whether the value still needs to be converted (<code>false</code>).
		 * **********************************************************************************
		 * ~$ 返回这是否持有人已经包含一个转换值 (<code>true</code>),
		 *    是否仍然需要转换价值 (<code>false</code>).
		 */
		public synchronized boolean isConverted() {
			return this.converted;
		}

		/**
		 * Set the converted value of the constructor argument,
		 * after processed type conversion.
		 * ****************************************************
		 * ~$ 构造函数参数的设置转换后的值,后加工类型转换
		 */
		public synchronized void setConvertedValue(Object value) {
			this.converted = true;
			this.convertedValue = value;
		}

		/**
		 * Return the converted value of the constructor argument,
		 * after processed type conversion.
		 * *******************************************************
		 * ~$ 返回转换后的值的构造函数参数 后加工类型转换
		 */
		public synchronized Object getConvertedValue() {
			return this.convertedValue;
		}

		/**
		 * Determine whether the content of this ValueHolder is equal
		 * to the content of the given other ValueHolder.
		 * <p>Note that ValueHolder does not implement <code>equals</code>
		 * directly, to allow for multiple ValueHolder instances with the
		 * same content to reside in the same Set.
		 * ***************************************************************
		 * ~$ 确定该ValueHolder的内容是否相等 其他ValueHolder给定的内容.
		 * <p>注意ValueHolder没有实现 <code>equals</code>
		 *    直接允许多个ValueHolder实例的 同样的内容驻留在同一组
		 */
		private boolean contentEquals(ValueHolder other) {
			return (this == other ||
					(ObjectUtils.nullSafeEquals(this.value, other.value) && ObjectUtils.nullSafeEquals(this.type, other.type)));
		}

		/**
		 * Determine whether the hash code of the content of this ValueHolder.
		 * <p>Note that ValueHolder does not implement <code>hashCode</code>
		 * directly, to allow for multiple ValueHolder instances with the
		 * same content to reside in the same Set.
		 * ******************************************************************
		 * ~$ 确定这个ValueHolder内容的散列码.
		 * <p>注意ValueHolder没有实现 <code>hashCode</code>
		 *   直接允许多个ValueHolder实例的
		 *   同样的内容驻留在同一组
		 */
		private int contentHashCode() {
			return ObjectUtils.nullSafeHashCode(this.value) * 29 + ObjectUtils.nullSafeHashCode(this.type);
		}

		/**
		 * Create a copy of this ValueHolder: that is, an independent
		 * ValueHolder instance with the same contents.
		 * **********************************************************
		 * ~$ 创建一个副本,这个ValueHolder:一个独立的ValueHolder实例相同的内容
		 */
		public ValueHolder copy() {
			ValueHolder copy = new ValueHolder(this.value, this.type, this.name);
			copy.setSource(this.source);
			return copy;
		}
	}

}
