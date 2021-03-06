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

package org.springframework.beans.factory;

import org.springframework.beans.FatalBeanException;

/**
 * Exception that indicates an expression evaluation attempt having failed.
 * ***********************************************************************
 * ~$ 异常,表明一个表达式求值尝试已经失败了.
 * @author Juergen Hoeller
 * @since 3.0
 */
public class BeanExpressionException extends FatalBeanException {

	/**
	 * Create a new BeanExpressionException with the specified message.
	 * @param msg the detail message
	 */
	public BeanExpressionException(String msg) {
		super(msg);
	}

	/**
	 * Create a new BeanExpressionException with the specified message
	 * and root cause.
	 * ***************************************************************
	 * ~$ 创建一个新的BeanExpressionException指定的消息和根源.
	 * @param msg the detail message
	 * @param cause the root cause
	 */
	public BeanExpressionException(String msg, Throwable cause) {
		super(msg, cause);
	}

}