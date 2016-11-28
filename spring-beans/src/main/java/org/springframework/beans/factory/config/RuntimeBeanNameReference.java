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

package org.springframework.beans.factory.config;

import org.springframework.util.Assert;

/** 
 * Immutable placeholder class used for a property value object when it's a
 * reference to another bean name in the factory, to be resolved at runtime.
 *
 * *************************************************************************
 * ~$ 不可变的占位符类用于属性值对象的时候对另一个bean的引用名称在工厂,在运行时得到解决.
 * @author Juergen Hoeller
 * @since 2.0
 * @see RuntimeBeanReference
 * @see BeanDefinition#getPropertyValues()
 * @see org.springframework.beans.factory.BeanFactory#getBean
 */
public class RuntimeBeanNameReference implements BeanReference {
	
	private final String beanName;

	private Object source;


	/**
	 * Create a new RuntimeBeanNameReference to the given bean name.
	 * *************************************************************
	 * ~$ 创建一个新的RuntimeBeanNameReference给定的bean的名称.
	 * @param beanName name of the target bean
	 */
	public RuntimeBeanNameReference(String beanName) {
		Assert.hasText(beanName, "'beanName' must not be empty");
		this.beanName = beanName;
	}

	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Set the configuration source <code>Object</code> for this metadata element.
	 * <p>The exact type of the object will depend on the configuration mechanism used.
	 * ********************************************************************************
	 * ~$ 这个元数据元素的配置源对象.
	 * <p>对象的确切类型将取决于所使用的配置机制.
	 */
	public void setSource(Object source) {
		this.source = source;
	}

	public Object getSource() {
		return this.source;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RuntimeBeanNameReference)) {
			return false;
		}
		RuntimeBeanNameReference that = (RuntimeBeanNameReference) other;
		return this.beanName.equals(that.beanName);
	}

	@Override
	public int hashCode() {
		return this.beanName.hashCode();
	}

	@Override
	public String toString() {
	   return '<' + getBeanName() + '>';
	}

}
