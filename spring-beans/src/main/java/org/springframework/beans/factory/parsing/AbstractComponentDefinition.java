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

package org.springframework.beans.factory.parsing;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanReference;

/**
 * Base implementation of {@link ComponentDefinition} that provides a basic implementation of
 * {@link #getDescription} which delegates to {@link #getName}. Also provides a base implementation
 * of {@link #toString} which delegates to {@link #getDescription} in keeping with the recommended
 * implementation strategy. Also provides default implementations of {@link #getInnerBeanDefinitions}
 * and {@link #getBeanReferences} that return an empty array.
 *
 * ***************************************************************************************************
 * ~$ 基本实现的{@link ComponentDefinition },它提供了一个基本实现代表的{@link #getDescription } {@link #getName }.
 *   还提供了一个基本实现的{@link #toString } {@link #getDescription }代表符合推荐的实现策略.
 *    还提供了默认的实现{@link #getInnerBeanDefinitions }和{@link #getBeanReferences },返回一个空数组.
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @since 2.0
 */
public abstract class AbstractComponentDefinition implements ComponentDefinition {

	/**
	 * Delegates to {@link #getName}.
	 */
	public String getDescription() {
		return getName();
	}

	/**
	 * Returns an empty array.
	 */
	public BeanDefinition[] getBeanDefinitions() {
		return new BeanDefinition[0];
	}

	/**
	 * Returns an empty array.
	 */
	public BeanDefinition[] getInnerBeanDefinitions() {
		return new BeanDefinition[0];
	}

	/**
	 * Returns an empty array.
	 */
	public BeanReference[] getBeanReferences() {
		return new BeanReference[0];
	}

	/**
	 * Delegates to {@link #getDescription}.
	 */
	@Override
	public String toString() {
		return getDescription();
	}

}
