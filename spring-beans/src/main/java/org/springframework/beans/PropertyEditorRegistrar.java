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

/**
 * Interface for strategies that register custom
 * {@link java.beans.PropertyEditor property editors} with a
 * {@link PropertyEditorRegistry property editor registry}.
 *
 * <p>This is particularly useful when you need to use the same set of
 * property editors in several different situations: write a corresponding
 * registrar and reuse that in each case.
 * ***********************************************************************
 * ~$ 登记界面策略定制{@link java.beans.PropertyEditor property editors} 与{@link PropertyEditorRegistry property editor registry}.
 *
 * <p>这是特别有用,当你需要使用相同的属性集编辑器在几种不同情况下:写一个相应的注册和重用,在每种情况下.
 * @author Juergen Hoeller
 * @since 1.2.6
 * @see PropertyEditorRegistry
 * @see java.beans.PropertyEditor
 */
public interface PropertyEditorRegistrar {
	
	/**
	 * Register custom {@link java.beans.PropertyEditor PropertyEditors} with
	 * the given <code>PropertyEditorRegistry</code>.
	 * <p>The passed-in registry will usually be a {@link BeanWrapper} or a
	 * {@link org.springframework.validation.DataBinder DataBinder}.
	 * <p>It is expected that implementations will create brand new
	 * <code>PropertyEditors</code> instances for each invocation of this
	 * method (since <code>PropertyEditors</code> are not threadsafe).
	 * ***********************************************************************
	 * ~$ 注册自定义{@link java.beans.PropertyEditor PropertyEditors} 与给定PropertyEditorRegistry.
	 * <p>传入注册表通常是{@link BeanWrapper }或{@link org.springframework.validation.DataBinder DataBinder }.
	 * <p>预计实现将创建全新PropertyEditors实例为每个调用该方法(因为PropertyEditors不是线程安全的).
	 * @param registry the <code>PropertyEditorRegistry</code> to register the
	 * custom <code>PropertyEditors</code> with
	 */
	void registerCustomEditors(PropertyEditorRegistry registry);

}
