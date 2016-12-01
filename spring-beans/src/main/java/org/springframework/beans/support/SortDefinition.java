/*
 * Copyright 2002-2005 the original author or authors.
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

package org.springframework.beans.support;

/**
 * Definition for sorting bean instances by a property.
 * ****************************************************
 * ~$ 定义排序bean实例的一个属性.
 * @author Juergen Hoeller
 * @since 26.05.2003
 */
public interface SortDefinition {

	/**
	 * Return the name of the bean property to compare.
	 * Can also be a nested bean property path.
	 * ************************************************
	 * ~$ 返回bean的名称属性进行比较.也可以是嵌套的bean属性路径.
	 */
	String getProperty();

	/**
	 * Return whether upper and lower case in String values should be ignored.
	 * ***********************************************************************
	 * ~$ 返回是否大写和小写字符串值应该被忽略.
	 */
	boolean isIgnoreCase();

	/**
	 * Return whether to sort ascending (true) or descending (false).
	 * **************************************************************
	 * ~$ 返回是否升序(真正的)或降序(假).
	 */
	boolean isAscending();

}
