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

package org.springframework.beans;

import java.beans.PropertyChangeEvent;

import org.springframework.core.ErrorCoded;

/**
 * Superclass for exceptions related to a property access,
 * such as type mismatch or invocation target exception.
 * *******************************************************
 * ~$ 超类与属性相关的异常访问,如类型不匹配或调用目标例外.
 * @author Rod Johnson
 * @author Juergen Hoeller
 */
public abstract class PropertyAccessException extends BeansException implements ErrorCoded {

	private transient PropertyChangeEvent propertyChangeEvent;
	

	/**
	 * Create a new PropertyAccessException.
	 * @param propertyChangeEvent the PropertyChangeEvent that resulted in the problem
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public PropertyAccessException(PropertyChangeEvent propertyChangeEvent, String msg, Throwable cause) {
		super(msg, cause);
		this.propertyChangeEvent = propertyChangeEvent;
	}

	/**
	 * Create a new PropertyAccessException without PropertyChangeEvent.
	 * *****************************************************************
	 * ~$ 创建一个新的没有PropertyChangeEvent PropertyAccessException.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public PropertyAccessException(String msg, Throwable cause) {
		super(msg, cause);
	}


	/**
	 * Return the PropertyChangeEvent that resulted in the problem.
	 * <p>May be <code>null</code>; only available if an actual bean property
	 * was affected.
	 * **********************************************************************
	 * ~$ 返回PropertyChangeEvent导致这个问题.可能是null;只有一个实际的bean属性是否受到影响.
	 */
	public PropertyChangeEvent getPropertyChangeEvent() {
		return this.propertyChangeEvent;
	}

	/**
	 * Return the name of the affected property, if available.
	 * *******************************************************
	 * ~$ 返回受影响的属性名,如果可用.
	 */
	public String getPropertyName() {
		return (this.propertyChangeEvent != null ? this.propertyChangeEvent.getPropertyName() : null);
	}

	/**
	 * Return the affected value that was about to be set, if any.
	 * ***********************************************************
	 * ~$ 返回值大约是设置的影响,如果有的话.
	 */
	public Object getValue() {
		return (this.propertyChangeEvent != null ? this.propertyChangeEvent.getNewValue() : null);
	}

}
