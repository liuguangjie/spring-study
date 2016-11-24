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

package org.springframework.beans.factory.config;

import org.springframework.beans.BeansException;

/**
 * Subinterface of {@link BeanPostProcessor} that adds a before-destruction callback.
 *
 * <p>The typical usage will be to invoke custom destruction callbacks on
 * specific bean types, matching corresponding initialization callbacks.
 * **********************************************************************************
 * ~$ 子接口的{@link BeanPostProcessor },添加一个在销毁的回调函数。
 * <p>典型的用法是调用自定义回调在特定bean销毁类型,匹配相应的初始化回调。
 * @author Juergen Hoeller
 * @since 1.0.1
 */
public interface DestructionAwareBeanPostProcessor extends BeanPostProcessor {

	/**
	 * Apply this BeanPostProcessor to the given bean instance before
	 * its destruction. Can invoke custom destruction callbacks.
	 * <p>Like DisposableBean's <code>destroy</code> and a custom destroy method,
	 * this callback just applies to singleton beans in the factory (including
	 * inner beans).
	 * *************************************************************************
	 * ~$ 应用这个BeanPostProcessor毁灭前的特定bean实例。可以调用自定义回调。
	 * <p> 像DisposableBean的破坏和一个定制的销毁方法,这个回调仅适用于单例bean在工厂(包括内部bean)。
	 * @param bean the bean instance to be destroyed
	 * @param beanName the name of the bean
	 * @throws BeansException in case of errors
	 * @see org.springframework.beans.factory.DisposableBean
	 * @see org.springframework.beans.factory.support.AbstractBeanDefinition#setDestroyMethodName
	 */
	void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException;

}
