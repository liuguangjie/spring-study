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

package org.springframework.core.enums;

import java.util.Map;
import java.util.Set;

/**
 * Interface for looking up <code>LabeledEnum</code> instances.
 * ************************************************************
 * ~$ 界面查找LabeledEnum实例.
 * @author Keith Donald
 * @author Juergen Hoeller
 * @since 1.2.2
 * @deprecated as of Spring 3.0, in favor of Java 5 enums.
 */
@Deprecated
public interface LabeledEnumResolver {

	/**
	 * Return a set of enumerations of a particular type. Each element in the
	 * set should be an instance of LabeledEnum.
	 * **********************************************************************
	 * ~$ 返回一组特定类型的枚举.集合中的每个元素应该LabeledEnum的实例.
	 * @param type the enum type
	 * @return a set of localized enumeration instances for the provided type
	 * @throws IllegalArgumentException if the type is not supported
	 */
	public Set getLabeledEnumSet(Class type) throws IllegalArgumentException;

	/**
	 * Return a map of enumerations of a particular type. Each element in the
	 * map should be a key/value pair, where the key is the enum code, and the
	 * value is the <code>LabeledEnum</code> instance.
	 * **********************************************************************
	 * ~$ 返回一个特定类型的枚举的map.每个元素的map应该是一个键/值对,
	 *    关键是枚举代码,和值是LabeledEnum实例.
	 * @param type the enum type
	 * @return a Map of localized enumeration instances,
	 * with enum code as key and <code>LabeledEnum</code> instance as value
	 * @throws IllegalArgumentException if the type is not supported
	 */
	public Map getLabeledEnumMap(Class type) throws IllegalArgumentException;

	/**
	 * Resolve a single <code>LabeledEnum</code> by its identifying code.
	 * ******************************************************************
	 * ~$ 解决单个LabeledEnum标识码.
	 * @param type the enum type
	 * @param code the enum code
	 * @return the enum
	 * @throws IllegalArgumentException if the code did not map to a valid instance
	 */
	public LabeledEnum getLabeledEnumByCode(Class type, Comparable code) throws IllegalArgumentException;

	/**
	 * Resolve a single <code>LabeledEnum</code> by its identifying code.
	 * ******************************************************************
	 * ~$ 解决单个LabeledEnum标识码.
	 * @param type the enum type
	 * @param label the enum label
	 * @return the enum
	 * @throws IllegalArgumentException if the label did not map to a valid instance
	 */
	public LabeledEnum getLabeledEnumByLabel(Class type, String label) throws IllegalArgumentException;

}
