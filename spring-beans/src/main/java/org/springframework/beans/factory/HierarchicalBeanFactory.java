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
 * Sub-interface implemented by bean factories that can be part
 * of a hierarchy.
 *
 * <p>The corresponding <code>setParentBeanFactory</code> method for bean
 * factories that allow setting the parent in a configurable
 * fashion can be found in the ConfigurableBeanFactory interface.
 * **********************************************************************
 * ~$ Sub-interface由bean工厂,可以实现一个层次结构的一部分.
 *
 * <p>相应的setParentBeanFactory bean工厂方法可配置的方式,允许设置parent ConfigurableBeanFactory接口中可以找到.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 07.07.2003
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
 */
public interface HierarchicalBeanFactory extends BeanFactory {
	
	/**
	 * Return the parent bean factory, or <code>null</code> if there is none.
	 * **********************************************************************
	 * ~$ 返回parent bean工厂,如果没有或null.
	 */
	BeanFactory getParentBeanFactory();

	/**
	 * Return whether the local bean factory contains a bean of the given name,
	 * ignoring beans defined in ancestor contexts.
	 * <p>This is an alternative to <code>containsBean</code>, ignoring a bean
	 * of the given name from an ancestor bean factory.
	 * ************************************************************************
	 * ~$ 返回本地bean工厂是否包含bean的名字,忽视祖先中定义bean上下文.
	 * <p>这是一个替代containsBean,忽略bean的名字从一个祖先bean工厂.
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is defined in the local factory
	 * @see BeanFactory#containsBean
	 */
	boolean containsLocalBean(String name);

}
