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

package org.springframework.beans;

import java.beans.PropertyEditor;

/**
 * Encapsulates methods for registering JavaBeans {@link PropertyEditor PropertyEditors}.
 * This is the central interface that a {@link PropertyEditorRegistrar} operates on.
 *
 * <p>Extended by {@link BeanWrapper}; implemented by {@link BeanWrapperImpl}
 * and {@link org.springframework.validation.DataBinder}.
 * *************************************************************************************
 * ~$ 封装的方法注册javabean {@link PropertyEditor PropertyEditors }.
 *    这是中央接口,{@link PropertyEditorRegistrar }操作.
 *
 * <p>延长{@link BeanWrapper };实现{@link BeanWrapperImpl }和{@link org.springframework.validation.DataBinder }.
 *
 * @author Juergen Hoeller
 * @since 1.2.6
 * @see PropertyEditor
 * @see PropertyEditorRegistrar
 * @see BeanWrapper
 * @see org.springframework.validation.DataBinder
 */
public interface PropertyEditorRegistry {

	/**
	 * Register the given custom property editor for all properties of the given type.
	 * *******************************************************************************
	 * ~$ 注册给定的自定义属性编辑器给定类型的所有属性.
	 * @param requiredType the type of the property
	 * @param propertyEditor the editor to register
	 */
	void registerCustomEditor(Class<?> requiredType, PropertyEditor propertyEditor);

	/**
	 * Register the given custom property editor for the given type and
	 * property, or for all properties of the given type.
	 * <p>If the property path denotes an array or Collection property,
	 * the editor will get applied either to the array/Collection itself
	 * (the {@link PropertyEditor} has to create an array or Collection value) or
	 * to each element (the <code>PropertyEditor</code> has to create the element type),
	 * depending on the specified required type.
	 * <p>Note: Only one single registered custom editor per property path
	 * is supported. In the case of a Collection/array, do not register an editor
	 * for both the Collection/array and each element on the same property.
	 * <p>For example, if you wanted to register an editor for "items[n].quantity"
	 * (for all values n), you would use "items.quantity" as the value of the
	 * 'propertyPath' argument to this method.
	 * *********************************************************************************
	 * ~$ 注册了对于给定的类型和属性,自定义属性编辑器或给定类型的所有属性.
	 * <p>如果属性路径表示数组或集合属性,编辑器将会应用到数组/集合本身({@link PropertyEditor }
	 *    必须创建一个数组或集合值)或每个元素(PropertyEditor来创建元素类型),根据需要指定类型.
	 * <p>注意:每个属性只有一个单一的注册自定义编辑器支持路径.
	 *     对于一组/数组,不注册一个编辑收集/数组,每个元素在同一性质.
	 * <p>例如,如果您想注册一个编辑器"items[n].quantity"(n)所有的值,
	 *    您将使用"items.quantity"的价值“propertyPath”这个方法的参数.
	 *
	 * @param requiredType the type of the property. This may be <code>null</code>
	 * if a property is given but should be specified in any case, in particular in
	 * case of a Collection - making clear whether the editor is supposed to apply
	 * to the entire Collection itself or to each of its entries. So as a general rule:
	 * <b>Do not specify <code>null</code> here in case of a Collection/array!</b>
	 * @param propertyPath the path of the property (name or nested path), or
	 * <code>null</code> if registering an editor for all properties of the given type
	 * @param propertyEditor editor to register
	 */
	void registerCustomEditor(Class<?> requiredType, String propertyPath, PropertyEditor propertyEditor);

	/**
	 * Find a custom property editor for the given type and property.
	 * **************************************************************
	 * ~$ 找到一个自定义属性编辑器为给定的类型和属性.
	 * @param requiredType the type of the property (can be <code>null</code> if a property
	 * is given but should be specified in any case for consistency checking)
	 * @param propertyPath the path of the property (name or nested path), or
	 * <code>null</code> if looking for an editor for all properties of the given type
	 * @return the registered editor, or <code>null</code> if none
	 */
	PropertyEditor findCustomEditor(Class<?> requiredType, String propertyPath);

}
