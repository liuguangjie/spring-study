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

package org.springframework.beans.factory.support;

import java.lang.reflect.Method;

import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Represents an override of a method that looks up an object in the same IoC context.
 *
 * <p>Methods eligible for lookup override must not have arguments.
 * ***********************************************************************************
 * ~$ 代表了一个覆盖的方法,查找一个对象在同一IoC上下文.
 * <p>查询重写方法必须没有参数。
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 1.1
 */
public class LookupOverride extends MethodOverride {
	
	private final String beanName;


	/**
	 * Construct a new LookupOverride.
	 * *******************************
	 * ~$ 构造一个新的LookupOverride.
	 * @param methodName the name of the method to override.
	 * This method must have no arguments.
	 * 			~$ 覆盖的方法的名称.这个方法肯定没有参数.
	 * @param beanName name of the bean in the current BeanFactory
	 * that the overriden method should return
	 */
	public LookupOverride(String methodName, String beanName) {
		super(methodName);
		Assert.notNull(beanName, "Bean name must not be null");
		this.beanName = beanName;
	}

	/**
	 * Return the name of the bean that should be returned by this method.
	 * ******************************************************************
	 * ~$ 返回的bean的名称,用这种方法应该返回.
	 */
	public String getBeanName() {
		return this.beanName;
	}


	/**
	 * Match method of the given name, with no parameters.
	 * ***************************************************
	 * ~$ 给定名称的匹配方法,没有参数.
	 */
	@Override
	public boolean matches(Method method) {
		return (method.getName().equals(getMethodName()) && method.getParameterTypes().length == 0);
	}


	@Override
	public String toString() {
		return "LookupOverride for method '" + getMethodName() + "'; will return bean '" + this.beanName + "'";
	}

	@Override
	public boolean equals(Object other) {
		return (other instanceof LookupOverride && super.equals(other) &&
				ObjectUtils.nullSafeEquals(this.beanName, ((LookupOverride) other).beanName));
	}

	@Override
	public int hashCode() {
		return (29 * super.hashCode() + ObjectUtils.nullSafeHashCode(this.beanName));
	}

}
