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

package org.springframework.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.Ordered;

/**
 * Annotation that defines ordering. The value is optional, and represents order value
 * as defined in the {@link Ordered} interface. Lower values have higher priority.
 * The default value is <code>Ordered.LOWEST_PRECEDENCE</code>, indicating
 * lowest priority (losing to any other specified order value).
 *
 * <p><b>NOTE:</b> Annotation-based ordering is supported for specific kinds of
 * components only, e.g. for annotation-based AspectJ aspects. Spring container
 * strategies, on the other hand, are typically based on the {@link Ordered}
 * interface in order to allow for configurable ordering of each <i>instance</i>.
 * ***********************************************************************************
 * ~$ 注释定义排序.值是可选的,和代表秩序价值{ @link下令}中定义的接口.
 *    低价值更高的优先级。默认值是有序的.
 *    LOWEST_PRECEDENCE,表明最低优先级(输给其他任何指定的订单值).
 *
 * <p>注意:基于注解的排序是支持特定类型的组件,例如基于注解的AspectJ方面.
 *    Spring容器策略,另一方面,通常是基于{ @link下令}接口以允许每个实例的配置命令.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 2.0
 * @see org.springframework.core.Ordered
 * @see AnnotationAwareOrderComparator
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
public @interface Order {

	/**
	 * The order value. Default is {@link Ordered#LOWEST_PRECEDENCE}.
	 * @see Ordered#getOrder()
	 */
	int value() default Ordered.LOWEST_PRECEDENCE;

}
