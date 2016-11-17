/*
 * Copyright 2002-2011 the original author or authors.
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

/**
 * Marker superinterface indicating that a bean is eligible to be
 * notified by the Spring container of a particular framework object
 * through a callback-style method.  Actual method signature is
 * determined by individual subinterfaces, but should typically
 * consist of just one void-returning method that accepts a single
 * argument.
 *
 * 标记超接口表示bean是合格的
 *通知Spring容器的一个特定的框架对象
 *通过回叫形式的方法。实际的方法签名
 *由个人个子,但应该一般
 *由void-returning方法接受一个
 *参数。
 *
 * <p>Note that merely implementing {@link Aware} provides no default
 * functionality. Rather, processing must be done explicitly, for example
 * in a {@link org.springframework.beans.factory.config.BeanPostProcessor BeanPostProcessor}.
 * Refer to {@link org.springframework.context.support.ApplicationContextAwareProcessor}
 * and {@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory}
 * for examples of processing {@code *Aware} interface callbacks.
 *
 * 注意,仅仅实现{@link Aware}没有提供默认值
 *功能。相反,必须显式地处理
 *  在{@link org.springframework.beans.factory.config.BeanPostProcessor BeanPostProcessor }.
 *  指{@link org.springframework.context.support.ApplicationContextAwareProcessor }
 *  和{@link org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory }
 *  的例子处理{ @code *Aware} 接口回调。
 *
 * @author Chris Beams
 * @since 3.1
 */
public interface Aware {

}
