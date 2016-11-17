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

import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;

/**
 * 策略接口,用于确定是否一个特定的bean定义
 *  有资格作为一个自动装配的候选人为一个特定的依赖.
 *
 * @author Mark Fisher
 * @author Juergen Hoeller
 * @since 2.5
 */
public interface AutowireCandidateResolver {

	/**
	 * 确定是否有资格作为一个给定的bean定义
     * 自动装配的候选人的依赖.
	 * @param bdHolder bean定义包括bean名称和别名
	 * @param descriptor 目标方法参数或字段的描述符
	 * @return 是否该bean定义限定为自动装配候选人
	 */
	boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor);

	/**
	 * Determine whether a default value is suggested for the given dependency.
     * 确定一个默认值是否建议为给定的依赖。
	 * @param descriptor the descriptor for the target method parameter or field
     *                   目标方法参数或字段的描述符
	 * @return the value suggested (typically an expression String),
	 * or <code>null</code> if none found
     * 提出的价值(通常是一个表达式的字符串),
     *or< /code>null<code>如果没有发现
	 * @since 3.0
	 */
	Object getSuggestedValue(DependencyDescriptor descriptor);

}
