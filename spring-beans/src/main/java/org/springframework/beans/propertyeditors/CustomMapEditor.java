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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Property editor for Maps, converting any source Map
 * to a given target Map type.
 * ***************************************************
 * ~$ 属性编辑Maps,将任何源映射到一个给定的目标Map类型。
 * @author Juergen Hoeller
 * @since 2.0.1
 * @see Map
 * @see SortedMap
 */
public class CustomMapEditor extends PropertyEditorSupport {

	private final Class mapType;

	private final boolean nullAsEmptyMap;


	/**
	 * Create a new CustomMapEditor for the given target type,
	 * keeping an incoming <code>null</code> as-is.
	 * *******************************************************
	 * ~$ 创建一个新的CustomMapEditor给定目标类型,按原样保持传入null.
	 * @param mapType the target type, which needs to be a
	 * sub-interface of Map or a concrete Map class
	 * @see Map
	 * @see java.util.HashMap
	 * @see TreeMap
	 * @see LinkedHashMap
	 */
	public CustomMapEditor(Class mapType) {
		this(mapType, false);
	}

	/**
	 * Create a new CustomMapEditor for the given target type.
	 * <p>If the incoming value is of the given type, it will be used as-is.
	 * If it is a different Map type or an array, it will be converted
	 * to a default implementation of the given Map type.
	 * If the value is anything else, a target Map with that single
	 * value will be created.
	 * <p>The default Map implementations are: TreeMap for SortedMap,
	 * and LinkedHashMap for Map.
	 * *********************************************************************
	 * ~$ 创建一个新的CustomMapEditor给定目标类型.
	 * <p>如果传入的值为给定的类型,它将按原样使用.如果它是一个不同的地图类型或数组,
	 *    它将会被转化为一个给定的地图类型的默认实现.如果该值是什么,目标将创建与单值映射.
	 * <p>默认的映射实现:TreeMap SortedMap,LinkedHashMap的地图.
	 * @param mapType the target type, which needs to be a
	 * sub-interface of Map or a concrete Map class
	 * @param nullAsEmptyMap ap whether to convert an incoming <code>null</code>
	 * value to an empty Map (of the appropriate type)
	 * @see Map
	 * @see TreeMap
	 * @see LinkedHashMap
	 */
	public CustomMapEditor(Class mapType, boolean nullAsEmptyMap) {
		if (mapType == null) {
			throw new IllegalArgumentException("Map type is required");
		}
		if (!Map.class.isAssignableFrom(mapType)) {
			throw new IllegalArgumentException(
					"Map type [" + mapType.getName() + "] does not implement [java.util.Map]");
		}
		this.mapType = mapType;
		this.nullAsEmptyMap = nullAsEmptyMap;
	}


	/**
	 * Convert the given text value to a Map with a single element.
	 * ************************************************************
	 * ~$ 将给定的文本值转换为单个元素的Map.
	 */
	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(text);
	}

	/**
	 * Convert the given value to a Map of the target type.
	 * ****************************************************
	 * ~$ 将给定的值转换成目标类型的Map.
	 */
	@Override
	public void setValue(Object value) {
		if (value == null && this.nullAsEmptyMap) {
			super.setValue(createMap(this.mapType, 0));
		}
		else if (value == null || (this.mapType.isInstance(value) && !alwaysCreateNewMap())) {
			// Use the source value as-is, as it matches the target type.
			/** 按原样使用源值,因为它匹配目标类型.*/
			super.setValue(value);
		}
		else if (value instanceof Map) {
			// Convert Map elements.
			/** 转换 Map  元素*/
			Map<?, ?> source = (Map) value;
			Map target = createMap(this.mapType, source.size());
			for (Map.Entry entry : source.entrySet()) {
				target.put(convertKey(entry.getKey()), convertValue(entry.getValue()));
			}
			super.setValue(target);
		}
		else {
			throw new IllegalArgumentException("Value cannot be converted to Map: " + value);
		}
	}

	/**
	 * Create a Map of the given type, with the given
	 * initial capacity (if supported by the Map type).
	 * ************************************************
	 * ~$ 创建一个指定类型的Map,用给定的初始容量(如果支持的Map类型).
	 * @param mapType a sub-interface of Map
	 * @param initialCapacity the initial capacity
	 * @return the new Map instance
	 */
	protected Map createMap(Class mapType, int initialCapacity) {
		if (!mapType.isInterface()) {
			try {
				return (Map) mapType.newInstance();
			}
			catch (Exception ex) {
				throw new IllegalArgumentException(
						"Could not instantiate map class [" + mapType.getName() + "]: " + ex.getMessage());
			}
		}
		else if (SortedMap.class.equals(mapType)) {
			return new TreeMap();
		}
		else {
			return new LinkedHashMap(initialCapacity);
		}
	}

	/**
	 * Return whether to always create a new Map,
	 * even if the type of the passed-in Map already matches.
	 * <p>Default is "false"; can be overridden to enforce creation of a
	 * new Map, for example to convert elements in any case.
	 * ******************************************************************
	 * ~$ 返回是否总是创建一个新的Map,即使已经传入Map的类型匹配.
	 * <p>Default is "false"; 可以覆盖执行创建一个新的映射,例如将元素在任何情况下.
	 * @see #convertKey
	 * @see #convertValue
	 */
	protected boolean alwaysCreateNewMap() {
		return false;
	}

	/**
	 * Hook to convert each encountered Map key.
	 * The default implementation simply returns the passed-in key as-is.
	 * <p>Can be overridden to perform conversion of certain keys,
	 * for example from String to Integer.
	 * <p>Only called if actually creating a new Map!
	 * This is by default not the case if the type of the passed-in Map
	 * already matches. Override {@link #alwaysCreateNewMap()} to
	 * enforce creating a new Map in every case.
	 * *******************************************************************
	 * ~$ 钩将每个遇到Map键.默认实现简单地按原样返回传入的关键.
	 * <p>可以覆盖执行某些键的转换,例如从字符串到整数.
	 * <p>只叫如果真正创建一个新的Map!这是默认情况下不会出现这种情况如果已经传入Map的类型匹配.
	 *    覆盖{@link #alwaysCreateNewMap()}执行创建一个新的Map在任何情况下.
	 * @param key the source key
	 * @return the key to be used in the target Map
	 * @see #alwaysCreateNewMap
	 */
	protected Object convertKey(Object key) {
		return key;
	}

	/**
	 * Hook to convert each encountered Map value.
	 * The default implementation simply returns the passed-in value as-is.
	 * <p>Can be overridden to perform conversion of certain values,
	 * for example from String to Integer.
	 * <p>Only called if actually creating a new Map!
	 * This is by default not the case if the type of the passed-in Map
	 * already matches. Override {@link #alwaysCreateNewMap()} to
	 * enforce creating a new Map in every case.
	 * @param value the source value
	 * @return the value to be used in the target Map
	 * @see #alwaysCreateNewMap
	 */
	protected Object convertValue(Object value) {
		return value;
	}


	/**
	 * This implementation returns <code>null</code> to indicate that
	 * there is no appropriate text representation.
	 * ***************************************************************
	 * ~$ 这个实现返回null,表明没有适当的文本表示.
	 */
	@Override
	public String getAsText() {
		return null;
	}

}
