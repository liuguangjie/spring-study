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

import java.util.Map;

import org.springframework.core.convert.TypeDescriptor;

/**
 * Common interface for classes that can access named properties
 * (such as bean properties of an object or fields in an object)
 * Serves as base interface for {@link BeanWrapper}.
 * *************************************************************
 * ~$ 常见的接口的类可以访问命名属性(如对象的bean属性或字段对象)作为基本接口{@link BeanWrapper }.
 * @author Juergen Hoeller
 * @since 1.1
 * @see BeanWrapper
 * @see PropertyAccessorFactory#forBeanPropertyAccess
 * @see PropertyAccessorFactory#forDirectFieldAccess
 */
public interface PropertyAccessor {

	/**
	 * Path separator for nested properties.
	 * Follows normal Java conventions: getFoo().getBar() would be "foo.bar".
	 * **********************************************************************
	 * ~$ 路径分隔符的嵌套属性.遵循普通Java惯例:getFoo().getBar()将“foo.bar”.
	 */
	String NESTED_PROPERTY_SEPARATOR = ".";
	char NESTED_PROPERTY_SEPARATOR_CHAR = '.';

	/**
	 * Marker that indicates the start of a property key for an
	 * indexed or mapped property like "person.addresses[0]".
	 * ********************************************************
	 * ~$ 开始标记表明属性索引或映射的关键属性像"person.addresses[0]".
	 */
	String PROPERTY_KEY_PREFIX = "[";
	char PROPERTY_KEY_PREFIX_CHAR = '[';

	/**
	 * Marker that indicates the end of a property key for an
	 * indexed or mapped property like "person.addresses[0]".
	 * *******************************************************
	 * ~$结束标记表明这样的属性索引或映射的关键属性"person.addresses[0]".
	 */
	String PROPERTY_KEY_SUFFIX = "]";
	char PROPERTY_KEY_SUFFIX_CHAR = ']';


	/**
	 * Determine whether the specified property is readable.
	 * <p>Returns <code>false</code> if the property doesn't exist.
	 * ************************************************************
	 * ~$确定指定的属性是可读的.
	 * <p>如果属性不存在返回false.
	 * @param propertyName the property to check
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return whether the property is readable
	 */
	boolean isReadableProperty(String propertyName);

	/**
	 * Determine whether the specified property is writable.
	 * <p>Returns <code>false</code> if the property doesn't exist.
	 * ************************************************************
	 * ~$ 确定指定的属性是可写的.
	 * <p> 如果属性不存在返回false.
	 * @param propertyName the property to check
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return whether the property is writable
	 */
	boolean isWritableProperty(String propertyName);

	/**
	 * Determine the property type for the specified property,
	 * either checking the property descriptor or checking the value
	 * in case of an indexed or mapped element.
	 * *************************************************************
	 * ~$ 确定指定的属性类型属性,属性描述符检查或检查值的索引或映射元素.
	 * @param propertyName the property to check
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return the property type for the particular property,
	 * or <code>null</code> if not determinable
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't readable
	 * @throws PropertyAccessException if the property was valid but the
	 * accessor method failed
	 */
	Class getPropertyType(String propertyName) throws BeansException;

	/**
	 * Return a type descriptor for the specified property:
	 * preferably from the read method, falling back to the write method.
	 * ******************************************************************
	 * ~$ 返回类型描述符指定属性:最好的阅读方法,落回写方法.
	 * @param propertyName the property to check
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return the property type for the particular property,
	 * or <code>null</code> if not determinable
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't readable
	 */
	TypeDescriptor getPropertyTypeDescriptor(String propertyName) throws BeansException;

	/**
	 * Get the current value of the specified property.
	 * ************************************************
	 * ~$ 得到指定属性的当前值.
	 * @param propertyName the name of the property to get the value of
	 * (may be a nested path and/or an indexed/mapped property)
	 * @return the value of the property
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't readable
	 * @throws PropertyAccessException if the property was valid but the
	 * accessor method failed
	 */
	Object getPropertyValue(String propertyName) throws BeansException;

	/**
	 * Set the specified value as current property value.
	 * **************************************************
	 * ~$ 指定的值设置为当前的属性值.
	 * @param propertyName the name of the property to set the value of
	 * (may be a nested path and/or an indexed/mapped property)
	 * @param value the new value
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyAccessException if the property was valid but the
	 * accessor method failed or a type mismatch occured
	 */
	void setPropertyValue(String propertyName, Object value) throws BeansException;

	/**
	 * Set the specified value as current property value.
	 * **************************************************
	 * ~$  指定的值设置为当前的属性值.
	 * @param pv an object containing the new property value
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyAccessException if the property was valid but the
	 * accessor method failed or a type mismatch occured
	 */
	void setPropertyValue(PropertyValue pv) throws BeansException;

	/**
	 * Perform a batch update from a Map.
	 * <p>Bulk updates from PropertyValues are more powerful: This method is
	 * provided for convenience. Behavior will be identical to that of
	 * the {@link #setPropertyValues(PropertyValues)} method.
	 * **********************************************************************
	 * ~$ 执行批量更新的 Map.
	 * <p>批量更新propertyvalue更强大:这种方法提供了方便.
	 *    行为将相同的{@link #setPropertyValues(PropertyValues)}的方法.
	 * @param map Map to take properties from. Contains property value objects,
	 * keyed by property name
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyBatchUpdateException if one or more PropertyAccessExceptions
	 * occured for specific properties during the batch update. This exception bundles
	 * all individual PropertyAccessExceptions. All other properties will have been
	 * successfully updated.
	 */
	void setPropertyValues(Map<?, ?> map) throws BeansException;

	/**
	 * The preferred way to perform a batch update.
	 * <p>Note that performing a batch update differs from performing a single update,
	 * in that an implementation of this class will continue to update properties
	 * if a <b>recoverable</b> error (such as a type mismatch, but <b>not</b> an
	 * invalid field name or the like) is encountered, throwing a
	 * {@link PropertyBatchUpdateException} containing all the individual errors.
	 * This exception can be examined later to see all binding errors.
	 * Properties that were successfully updated remain changed.
	 * <p>Does not allow unknown fields or invalid fields.
	 * *******************************************************************************
	 * ~$ 执行批量更新的首选方法.
	 * <p>注意,执行批量更新与执行一个更新,在这个类的实现将继续更新属性如果可恢复错误(如类型不匹配,而不是一个无效的字段名或类似的)遇到,
	 *    扔一个{@link PropertyBatchUpdateException }包含所有个人的错误.这个异常检查后可以看到所有绑定错误.属性成功更新保持持平.
	 * <p>不允许或无效的字段的未知领域.
	 * @param pvs PropertyValues to set on the target object
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyBatchUpdateException if one or more PropertyAccessExceptions
	 * occured for specific properties during the batch update. This exception bundles
	 * all individual PropertyAccessExceptions. All other properties will have been
	 * successfully updated.
	 * @see #setPropertyValues(PropertyValues, boolean, boolean)
	 */
	void setPropertyValues(PropertyValues pvs) throws BeansException;

	/**
	 * Perform a batch update with more control over behavior.
	 * <p>Note that performing a batch update differs from performing a single update,
	 * in that an implementation of this class will continue to update properties
	 * if a <b>recoverable</b> error (such as a type mismatch, but <b>not</b> an
	 * invalid field name or the like) is encountered, throwing a
	 * {@link PropertyBatchUpdateException} containing all the individual errors.
	 * This exception can be examined later to see all binding errors.
	 * Properties that were successfully updated remain changed.
	 * *******************************************************************************
	 * ~$ 执行批量更新和更多的控制行为.
	 * <p>注意,执行批量更新与执行一个更新,在这个类的实现将继续更新属性如果可恢复错误(如类型不匹配,而不是一个无效的字段名或类似的)遇到,
	 *   扔一个{@link PropertyBatchUpdateException }包含所有个人的错误.这个异常检查后可以看到所有绑定错误.属性成功更新保持持平.
	 * @param pvs PropertyValues to set on the target object
	 * @param ignoreUnknown should we ignore unknown properties (not found in the bean)
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyBatchUpdateException if one or more PropertyAccessExceptions
	 * occured for specific properties during the batch update. This exception bundles
	 * all individual PropertyAccessExceptions. All other properties will have been
	 * successfully updated.
	 * @see #setPropertyValues(PropertyValues, boolean, boolean)
	 */
	void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown)
	    throws BeansException;

	/**
	 * Perform a batch update with full control over behavior.
	 * <p>Note that performing a batch update differs from performing a single update,
	 * in that an implementation of this class will continue to update properties
	 * if a <b>recoverable</b> error (such as a type mismatch, but <b>not</b> an
	 * invalid field name or the like) is encountered, throwing a
	 * {@link PropertyBatchUpdateException} containing all the individual errors.
	 * This exception can be examined later to see all binding errors.
	 * Properties that were successfully updated remain changed.
	 * @param pvs PropertyValues to set on the target object
	 * @param ignoreUnknown should we ignore unknown properties (not found in the bean)
	 * @param ignoreInvalid should we ignore invalid properties (found but not accessible)
	 * @throws InvalidPropertyException if there is no such property or
	 * if the property isn't writable
	 * @throws PropertyBatchUpdateException if one or more PropertyAccessExceptions
	 * occured for specific properties during the batch update. This exception bundles
	 * all individual PropertyAccessExceptions. All other properties will have been
	 * successfully updated.
	 */
	void setPropertyValues(PropertyValues pvs, boolean ignoreUnknown, boolean ignoreInvalid)
	    throws BeansException;

}
