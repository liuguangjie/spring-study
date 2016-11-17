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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeanMetadataElement;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Holder for a BeanDefinition with name and aliases.
 * Can be registered as a placeholder for an inner bean.
 *
 * <p>Can also be used for programmatic registration of inner bean
 * definitions. If you don't care about BeanNameAware and the like,
 * registering RootBeanDefinition or ChildBeanDefinition is good enough.
 * *********************************************************************
 * 持有人的BeanDefinition名和别名。
 *	可以注册为一种内在的bean的占位符。
 *
 * 也可以用于编程登记内心的bean
 *	定义。如果你不关心BeanNameAware之类的,
 *	注册RootBeanDefinition或ChildBeanDefinition不够好。
 *
 * @author Juergen Hoeller
 * @since 1.0.2
 * @see org.springframework.beans.factory.BeanNameAware
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 */
public class BeanDefinitionHolder implements BeanMetadataElement {

	private final BeanDefinition beanDefinition;

	private final String beanName;

	private final String[] aliases;


	/**
	 * Create a new BeanDefinitionHolder.
	 * 创建一个新的BeanDefinitionHolder
	 * @param beanDefinition the BeanDefinition to wrap
	 *                       包装   BeanDefinition
	 * @param beanName the name of the bean, as specified for the bean definition
	 *                 bean的名称,指定的bean定义
	 */
	public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName) {
		this(beanDefinition, beanName, null);
	}

	/**
	 * Create a new BeanDefinitionHolder.
	 * 创建一个新的BeanDefinitionHolder
	 * @param beanDefinition the BeanDefinition to wrap
	 *                       包装   BeanDefinition
	 * @param beanName the name of the bean, as specified for the bean definition
	 *                 bean的名称,指定的bean定义
	 * @param aliases alias names for the bean, or <code>null</code> if none
	 *					别名bean,或者null 如果没有
	 */
	public BeanDefinitionHolder(BeanDefinition beanDefinition, String beanName, String[] aliases) {
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");
		Assert.notNull(beanName, "Bean name must not be null");
		this.beanDefinition = beanDefinition;
		this.beanName = beanName;
		this.aliases = aliases;
	}

	/**
	 * Copy constructor: Create a new BeanDefinitionHolder with the
	 * same contents as the given BeanDefinitionHolder instance.
	 * ************************************************************
	 * 拷贝构造函数:创建一个新的BeanDefinitionHolder 相同的内容给BeanDefinitionHolder实例。
	 *
	 * <p>Note: The wrapped BeanDefinition reference is taken as-is;
	 * it is <code>not</code> deeply copied.
	 *注意:包装BeanDefinition引用被原样;它不是复制。
	 * @param beanDefinitionHolder the BeanDefinitionHolder to copy
	 */
	public BeanDefinitionHolder(BeanDefinitionHolder beanDefinitionHolder) {
		Assert.notNull(beanDefinitionHolder, "BeanDefinitionHolder must not be null");
		this.beanDefinition = beanDefinitionHolder.getBeanDefinition();
		this.beanName = beanDefinitionHolder.getBeanName();
		this.aliases = beanDefinitionHolder.getAliases();
	}


	/**
	 * Return the wrapped BeanDefinition.
	 * 返回包装BeanDefinition。
	 */
	public BeanDefinition getBeanDefinition() {
		return this.beanDefinition;
	}

	/**
	 * Return the primary name of the bean, as specified for the bean definition.
	 * **************************************************************************
	 * 返回主bean的名称,作为指定的bean定义。
	 */
	public String getBeanName() {
		return this.beanName;
	}

	/**
	 * Return the alias names for the bean, as specified directly for the bean definition.
	 * @return the array of alias names, or <code>null</code> if none
	 * **************************************************************
	 *	返回bean的别名,直接指定的bean定义。
	 *	返回 别名的数组, or null
	 */
	public String[] getAliases() {
		return this.aliases;
	}

	/**
	 * Expose the bean definition's source object.
	 * 暴露的bean定义的源对象。
	 * @see BeanDefinition#getSource()
	 */
	public Object getSource() {
		return this.beanDefinition.getSource();
	}

	/**
	 * Determine whether the given candidate name matches the bean name
	 * or the aliases stored in this bean definition.
	 * ***************************************************************
	 * 确定给定的候选人名字匹配的bean的名称或存储在bean定义别名。
	 */
	public boolean matchesName(String candidateName) {
		return (candidateName != null &&
				(candidateName.equals(this.beanName) || ObjectUtils.containsElement(this.aliases, candidateName)));
	}


	/**
	 * Return a friendly, short description for the bean, stating name and aliases.
	 * ***************************************************************************
	 * 返回一个友好,bean的简短描述,称名和别名。
	 * @see #getBeanName()
	 * @see #getAliases()
	 */
	public String getShortDescription() {
		StringBuilder sb = new StringBuilder();
		sb.append("Bean definition with name '").append(this.beanName).append("'");
		if (this.aliases != null) {
			sb.append(" and aliases [").append(StringUtils.arrayToCommaDelimitedString(this.aliases)).append("]");
		}
		return sb.toString();
	}

	/**
	 * Return a long description for the bean, including name and aliases
	 * as well as a description of the contained {@link BeanDefinition}.
	 * ***********************************************************************
	 * 为bean返回一个长描述,包括名称和别名以及包含{@link BeanDefinition }的描述。
	 * @see #getShortDescription()
	 * @see #getBeanDefinition()
	 */
	public String getLongDescription() {
		StringBuilder sb = new StringBuilder(getShortDescription());
		sb.append(": ").append(this.beanDefinition);
		return sb.toString();
	}

	/**
	 * This implementation returns the long description. Can be overridden
	 * to return the short description or any kind of custom description instead.
	 * *************************************************************************
	 * 这个实现返回长描述。可以覆盖返回简短描述或任何类型的自定义描述。
	 * @see #getLongDescription()
	 * @see #getShortDescription()
	 */
	@Override
	public String toString() {
		return getLongDescription();
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof BeanDefinitionHolder)) {
			return false;
		}
		BeanDefinitionHolder otherHolder = (BeanDefinitionHolder) other;
		return this.beanDefinition.equals(otherHolder.beanDefinition) &&
				this.beanName.equals(otherHolder.beanName) &&
				ObjectUtils.nullSafeEquals(this.aliases, otherHolder.aliases);
	}

	@Override
	public int hashCode() {
		int hashCode = this.beanDefinition.hashCode();
		hashCode = 29 * hashCode + this.beanName.hashCode();
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.aliases);
		return hashCode;
	}

}
