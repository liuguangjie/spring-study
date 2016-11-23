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

package org.springframework.beans.factory.annotation;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Enumeration determining autowiring status: that is, whether a bean should
 * have its dependencies automatically injected by the Spring container using
 * setter injection. This is a core concept in Spring DI.
 *
 * <p>Available for use in annotation-based configurations, such as for the
 * AspectJ AnnotationBeanConfigurer aspect.
 * ***************************************************************************
 * ~$ 枚举确定自动装配状态:bean是否应该有其依赖关系由Spring容器自动注射使用setter注入.
 *    在spring里面 这是一个核心概念DI
 * <p>使用基于注解的配置,比如AspectJ AnnotationBeanConfigurer方面.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see Configurable
 * @see AutowireCapableBeanFactory
 */
public enum Autowire {

	/**
	 * Constant that indicates no autowiring at all.
	 * *********************************************
	 * ~$ 常数表明没有自动装配.
	 */
	NO(AutowireCapableBeanFactory.AUTOWIRE_NO),

	/**
	 * Constant that indicates autowiring bean properties by name.
	 * ***********************************************************
	 * ~$ 常数表明自动装配bean属性的名字.
	 */
	BY_NAME(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME),

	/**
	 * Constant that indicates autowiring bean properties by type.
	 * **********************************************************
	 * ~$ 常数表明自动装配bean属性的类型.
	 */
	BY_TYPE(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE);


	private final int value;


	Autowire(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	/**
	 * Return whether this represents an actual autowiring value.
	 * **********************************************************
	 * ~$ 返回这是否代表了一个实际的自动装配值.
	 * @return whether actual autowiring was specified
	 * (either BY_NAME or BY_TYPE)
	 */
	public boolean isAutowire() {
		return (this == BY_NAME || this == BY_TYPE);
	}

}
