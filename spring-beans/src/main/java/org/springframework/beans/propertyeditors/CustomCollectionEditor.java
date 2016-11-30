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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Property editor for Collections, converting any source Collection
 * to a given target Collection type.
 *
 * <p>By default registered for Set, SortedSet and List,
 * to automatically convert any given Collection to one of those
 * target types if the type does not match the target property.
 * *****************************************************************
 * ~$ 属性编辑器集合,将任何源集合转换为一个给定的目标集合类型.
 *
 * <p> 默认情况下注册,SortedSet和列表,自动给定集合转换为一个目标类型如果类型不匹配目标属性.
 * @author Juergen Hoeller
 * @since 1.1.3
 * @see Collection
 * @see java.util.Set
 * @see SortedSet
 * @see List
 */
public class CustomCollectionEditor extends PropertyEditorSupport {

	private final Class collectionType;

	private final boolean nullAsEmptyCollection;


	/**
	 * Create a new CustomCollectionEditor for the given target type,
	 * keeping an incoming <code>null</code> as-is.
	 * **************************************************************
	 * ~$ 创建一个新的CustomCollectionEditor给定目标类型,按原样保持传入null.
	 * @param collectionType the target type, which needs to be a
	 * sub-interface of Collection or a concrete Collection class
	 * @see Collection
	 * @see ArrayList
	 * @see TreeSet
	 * @see LinkedHashSet
	 */
	public CustomCollectionEditor(Class collectionType) {
		this(collectionType, false);
	}

	/**
	 * Create a new CustomCollectionEditor for the given target type.
	 * <p>If the incoming value is of the given type, it will be used as-is.
	 * If it is a different Collection type or an array, it will be converted
	 * to a default implementation of the given Collection type.
	 * If the value is anything else, a target Collection with that single
	 * value will be created.
	 * <p>The default Collection implementations are: ArrayList for List,
	 * TreeSet for SortedSet, and LinkedHashSet for Set.
	 * **********************************************************************
	 * ~$ 创建一个新的CustomCollectionEditor给定目标类型.
	 * <p>如果传入的值为给定的类型,它将按原样使用.如果它是一个不同的集合类型或数组,
	 *    它将被转换成一个默认实现给定集合的类型.如果值是什么,目标集合与单值将被创建.
	 * <p>列表默认集合实现:ArrayList,TreeSet SortedSet,LinkedHashSet 设置.
	 * @param collectionType the target type, which needs to be a
	 * sub-interface of Collection or a concrete Collection class
	 * @param nullAsEmptyCollection whether to convert an incoming <code>null</code>
	 * value to an empty Collection (of the appropriate type)
	 * @see Collection
	 * @see ArrayList
	 * @see TreeSet
	 * @see LinkedHashSet
	 */
	public CustomCollectionEditor(Class collectionType, boolean nullAsEmptyCollection) {
		if (collectionType == null) {
			throw new IllegalArgumentException("Collection type is required");
		}
		if (!Collection.class.isAssignableFrom(collectionType)) {
			throw new IllegalArgumentException(
					"Collection type [" + collectionType.getName() + "] does not implement [java.util.Collection]");
		}
		this.collectionType = collectionType;
		this.nullAsEmptyCollection = nullAsEmptyCollection;
	}


	/**
	 * Convert the given text value to a Collection with a single element.
	 * *******************************************************************
	 * ~$ 将给定的文本值转换为一个元素的集合.
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(text);
	}

	/**
	 * Convert the given value to a Collection of the target type.
	 * **********************************************************
	 * ~$ 将给定的值转换成目标类型的集合.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		if (value == null && this.nullAsEmptyCollection) {
			super.setValue(createCollection(this.collectionType, 0));
		}
		else if (value == null || (this.collectionType.isInstance(value) && !alwaysCreateNewCollection())) {
			// Use the source value as-is, as it matches the target type.
			/** 按原样使用源值,因为它匹配目标类型.*/
			super.setValue(value);
		}
		else if (value instanceof Collection) {
			// Convert Collection elements.
			/** 把元素集合.*/
			Collection source = (Collection) value;
			Collection target = createCollection(this.collectionType, source.size());
			for (Object elem : source) {
				target.add(convertElement(elem));
			}
			super.setValue(target);
		}
		else if (value.getClass().isArray()) {
			// Convert array elements to Collection elements.
			/** 把数组元素集合的元素. */
			int length = Array.getLength(value);
			Collection target = createCollection(this.collectionType, length);
			for (int i = 0; i < length; i++) {
				target.add(convertElement(Array.get(value, i)));
			}
			super.setValue(target);
		}
		else {
			// A plain value: convert it to a Collection with a single element.
			/** 普通的价值:它转换为单个元素的集合.*/
			Collection target = createCollection(this.collectionType, 1);
			target.add(convertElement(value));
			super.setValue(target);
		}
	}

	/**
	 * Create a Collection of the given type, with the given
	 * initial capacity (if supported by the Collection type).
	 * *******************************************************
	 * ~$ 创建一个指定类型的集合,用给定的初始容量(如果支持的集合类型).
	 * @param collectionType a sub-interface of Collection
	 * @param initialCapacity the initial capacity
	 * @return the new Collection instance
	 */
	protected Collection createCollection(Class collectionType, int initialCapacity) {
		if (!collectionType.isInterface()) {
			try {
				return (Collection) collectionType.newInstance();
			}
			catch (Exception ex) {
				throw new IllegalArgumentException(
						"Could not instantiate collection class [" + collectionType.getName() + "]: " + ex.getMessage());
			}
		}
		else if (List.class.equals(collectionType)) {
			return new ArrayList(initialCapacity);
		}
		else if (SortedSet.class.equals(collectionType)) {
			return new TreeSet();
		}
		else {
			return new LinkedHashSet(initialCapacity);
		}
	}

	/**
	 * Return whether to always create a new Collection,
	 * even if the type of the passed-in Collection already matches.
	 * <p>Default is "false"; can be overridden to enforce creation of a
	 * new Collection, for example to convert elements in any case.
	 * ******************************************************************
	 * ~$ 返回是否总是创建一个新的集合,即使已经传入集合的类型匹配.
	 * <p>Default is "false";可以覆盖执行创建一个新的集合,例如将元素在任何情况下.
	 * @see #convertElement
	 */
	protected boolean alwaysCreateNewCollection() {
		return false;
	}

	/**
	 * Hook to convert each encountered Collection/array element.
	 * The default implementation simply returns the passed-in element as-is.
	 * <p>Can be overridden to perform conversion of certain elements,
	 * for example String to Integer if a String array comes in and
	 * should be converted to a Set of Integer objects.
	 * <p>Only called if actually creating a new Collection!
	 * This is by default not the case if the type of the passed-in Collection
	 * already matches. Override {@link #alwaysCreateNewCollection()} to
	 * enforce creating a new Collection in every case.
	 * ************************************************************************
	 * ~$ 钩将每次遇到收集/数组元素.默认实现简单地按原样返回传入的元素.
	 * <p> 可以覆盖执行某些元素的转换,例如字符串,整数如果一个字符串数组,应该转换为一组整数对象.
	 * <p>只叫如果真正创建一个新的集合!这是默认情况下不会出现这种情况如果传入的类型已经匹配集合.
	 *    覆盖{@link #alwaysCreateNewCollection()}执行在任何情况下创建一个新的集合.
	 * @param element the source element
	 * @return the element to be used in the target Collection
	 * @see #alwaysCreateNewCollection()
	 */
	protected Object convertElement(Object element) {
		return element;
	}


	/**
	 * This implementation returns <code>null</code> to indicate that
	 * there is no appropriate text representation.
	 * **************************************************************
	 * ~$ 这个实现返回null,表明没有适当的文本表示.
	 */
	@Override
	public String getAsText() {
		return null;
	}

}
