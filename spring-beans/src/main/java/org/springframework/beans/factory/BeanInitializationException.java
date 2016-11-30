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

import org.springframework.beans.FatalBeanException;

/**
 * Exception that a bean implementation is suggested to throw if its own
 * factory-aware initialization code fails. BeansExceptions thrown by
 * bean factory methods themselves should simply be propagated as-is.
 *
 * <p>Note that non-factory-aware initialization methods like afterPropertiesSet()
 * or a custom "init-method" can throw any exception.
 * *******************************************************************************
 * ~$ 例外一个bean实现建议,如果自己factory-aware初始化代码失败.
 *    BeansExceptions bean工厂方法抛出的自己应该只是原有传播.
 * <p>注意,non-factory-aware初始化方法如afterPropertiesSet()或一个定制的“init方法”可以抛出任何异常.
 * @author Juergen Hoeller
 * @since 13.11.2003
 * @see BeanFactoryAware#setBeanFactory
 * @see InitializingBean#afterPropertiesSet
 */
public class BeanInitializationException extends FatalBeanException {

	/**
	 * Create a new BeanInitializationException with the specified message.
	 * @param msg the detail message
	 */
	public BeanInitializationException(String msg) {
		super(msg);
	}

	/**
	 * Create a new BeanInitializationException with the specified message
	 * and root cause.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public BeanInitializationException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
