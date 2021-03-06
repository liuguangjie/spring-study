/*
 * Copyright 2002-2006 the original author or authors.
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

package org.springframework.beans.factory;

/**
 * Counterpart of BeanNameAware. Returns the bean name of an object.
 *
 * <p>This interface can be introduced to avoid a brittle dependence
 * on bean name in objects used with Spring IoC and Spring AOP.
 * *****************************************************************
 * ~$ BeanNameAware同行.返回对象的bean的名称.
 *
 * <p>这个接口可以介绍避免脆性依赖在对象使用Spring IoC bean的名字和Spring AOP.
 * @author Rod Johnson
 * @since 2.0
 * @see BeanNameAware
 */
public interface NamedBean {

	/**
	 * Return the name of this bean in a Spring bean factory.
	 */
	String getBeanName();

}
