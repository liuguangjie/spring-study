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

import org.springframework.core.convert.ConversionService;

/**
 * Interface that encapsulates configuration methods for a PropertyAccessor.
 * Also extends the PropertyEditorRegistry interface, which defines methods
 * for PropertyEditor management.
 *
 * <p>Serves as base interface for {@link BeanWrapper}.
 * *************************************************************************
 * ~$ PropertyAccessor接口,封装配置方法.也扩展了PropertyEditorRegistry接口,定义了PropertyEditor管理方法.
 * @author Juergen Hoeller
 * @since 2.0
 * @see BeanWrapper
 */
public interface ConfigurablePropertyAccessor extends PropertyAccessor, PropertyEditorRegistry, TypeConverter {

	/**
	 * Specify a Spring 3.0 ConversionService to use for converting
	 * property values, as an alternative to JavaBeans PropertyEditors.
	 * ***************************************************************
	 * ~$ 指定一个Spring 3.0 ConversionService用于转换属性值,作为一个javabean PropertyEditors的替代品.
	 */
	void setConversionService(ConversionService conversionService);

	/**
	 * Return the associated ConversionService, if any.
	 * ************************************************
	 * ~$ 返回相关ConversionService,如果有.
	 */
	ConversionService getConversionService();

	/**
	 * Set whether to extract the old property value when applying a
	 * property editor to a new value for a property.
	 * *************************************************************
	 * ~$ 设置是否提取旧属性值在应用属性编辑器,一个属性的新值.
	 */
	void setExtractOldValueForEditor(boolean extractOldValueForEditor);

	/**
	 * Return whether to extract the old property value when applying a
	 * property editor to a new value for a property.
	 * ****************************************************************
	 * ~$ 返回是否提取旧属性值在应用属性编辑器,一个属性的新值.
	 */
	boolean isExtractOldValueForEditor();

}
