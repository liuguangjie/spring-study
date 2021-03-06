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

package org.springframework.beans.factory.support;

import org.springframework.util.Assert;

/**
 * Tag collection class used to hold managed array elements, which may
 * include runtime bean references (to be resolved into bean objects).
 * *******************************************************************
 * ~$ 标签集合类用于保存托管数组元素,其中可能包括运行时bean引用(解决到bean对象).
 * @author Juergen Hoeller
 * @since 3.0
 */
public class ManagedArray extends ManagedList<Object> {

	/** Resolved element type for runtime creation of the target array */
	/** 解决为运行时创建目标数组的元素类型 */
	volatile Class resolvedElementType;


	/**
	 * Create a new managed array placeholder.
	 * ***************************************
	 * ~$ 创建一个新的托管数组占位符.
	 * @param elementTypeName the target element type as a class name
	 * @param size the size of the array
	 */
	public ManagedArray(String elementTypeName, int size) {
		super(size);
		Assert.notNull(elementTypeName, "elementTypeName must not be null");
		setElementTypeName(elementTypeName);
	}

}
