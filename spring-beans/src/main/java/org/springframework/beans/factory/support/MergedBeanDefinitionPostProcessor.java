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

import org.springframework.beans.factory.config.BeanPostProcessor;

/**
 * Post-processor callback interface for <i>merged</i> bean definitions at runtime.
 * {@link BeanPostProcessor} implementations may implement this sub-interface in
 * order to post-process the merged bean definition that the Spring BeanFactory
 * uses to create a specific bean instance.
 *
 * <p>The {@link #postProcessMergedBeanDefinition} method may for example introspect
 * the bean definition in order to prepare some cached metadata before post-processing
 * actual instances of a bean. It is also allowed to modify the bean definition
 * but <i>only</i> for bean definition properties which are actually intended
 * for concurrent modification. Basically, this only applies to operations
 * defined on the {@link RootBeanDefinition} itself but not to the properties
 * of its base classes.
 * ************************************************************************************
 * ~$ 后处理器在运行时为合并后的bean定义回调接口.{@link BeanPostProcessor }
 *    实现可能实现这个sub-interface为了后处理合并后的bean定义Spring BeanFactory用来创建一个特定的bean实例.
 *
 * <p>例如{@link #postProcessMergedBeanDefinition }方法可能反省bean定义为了准备一些缓存元数据后处理前实际bean的实例.
 *   它也允许修改bean定义的bean定义但只有属性,实际上是用于并发修改.
 *    基本上,这只适用于操作上定义{@link RootBeanDefinition }本身而不是其基类的属性.
 * @author Juergen Hoeller
 * @since 2.5
 */
public interface MergedBeanDefinitionPostProcessor extends BeanPostProcessor {

	/**
	 * Post-process the given merged bean definition for the specified bean.
	 * *********************************************************************
	 * ~$后处理给定合并为指定的bean定义bean.
	 * @param beanDefinition the merged bean definition for the bean
	 * @param beanType the actual type of the managed bean instance
	 * @param beanName the name of the bean
	 */
	void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName);

}
