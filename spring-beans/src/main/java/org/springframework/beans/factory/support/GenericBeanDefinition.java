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

package org.springframework.beans.factory.support;

import org.springframework.beans.factory.config.BeanDefinition;

/**
 * GenericBeanDefinition is a one-stop shop for standard bean definition purposes.
 * Like any bean definition, it allows for specifying a class plus optionally
 * constructor argument values and property values. Additionally, deriving from a
 * parent bean definition can be flexibly configured through the "parentName" property.
 *
 * <p>In general, use this <code>GenericBeanDefinition</code> class for the purpose of
 * registering user-visible bean definitions (which a post-processor might operate on,
 * potentially even reconfiguring the parent name). Use <code>RootBeanDefinition</code> /
 * <code>ChildBeanDefinition</code> where parent/child relationships happen to be pre-determined.
 * **********************************************************************************************
 * ~$ GenericBeanDefinition提供一站式标准bean定义的目的.
 *    像任何bean定义,它允许指定一个类+可选地构造函数参数值和属性值.此外,
 *    源于父母可以灵活配置bean定义“parentName”属性
 *
 * <p> 一般来说,使用这个 <code>GenericBeanDefinition</code> 类注册用户可见的bean定义的目的
 * 						(后处理 可能操作,重新配置 根 bean的名字)
 * 	  使用 	<code>RootBeanDefinition</code> / <code>ChildBeanDefinition</code>
 * 	  父/子关系恰好是预先确定的
 *
 * @author Juergen Hoeller
 * @since 2.5
 * @see #setParentName
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 */
public class GenericBeanDefinition extends AbstractBeanDefinition {

	private String parentName;


	/**
	 * Create a new GenericBeanDefinition, to be configured through its bean
	 * properties and configuration methods.
	 * *********************************************************************
	 * ~$ 创建一个新的GenericBeanDefinition,通过其配置bean属性和配置方法
	 * @see #setBeanClass
	 * @see #setBeanClassName
	 * @see #setScope
	 * @see #setAutowireMode
	 * @see #setDependencyCheck
	 * @see #setConstructorArgumentValues
	 * @see #setPropertyValues
	 */
	public GenericBeanDefinition() {
		super();
	}

	/**
	 * Create a new GenericBeanDefinition as deep copy of the given
	 * bean definition.
	 * ************************************************************
	 * ~$ 创建一个新的GenericBeanDefinition深拷贝的bean定义
	 * @param original the original bean definition to copy from
	 */
	public GenericBeanDefinition(BeanDefinition original) {
		super(original);
	}


	public void setParentName(String parentName) {
		this.parentName = parentName;
	}

	public String getParentName() {
		return this.parentName;
	}


	@Override
	public AbstractBeanDefinition cloneBeanDefinition() {
		return new GenericBeanDefinition(this);
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof GenericBeanDefinition && super.equals(other)));
	}

	@Override
	public String toString() {
		return "Generic bean: " + super.toString();
	}

}
