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

package org.springframework.beans.factory.xml;

import org.w3c.dom.Node;

import org.springframework.beans.factory.config.BeanDefinitionHolder;

/**
 * Interface used by the {@link DefaultBeanDefinitionDocumentReader}
 * to handle custom, nested (directly under a <code>&lt;bean&gt;</code>) tags.
 *
 * <p>Decoration may also occur based on custom attributes applied to the
 * <code>&lt;bean&gt;</code> tag. Implementations are free to turn the metadata in the
 * custom tag into as many
 * {@link org.springframework.beans.factory.config.BeanDefinition BeanDefinitions} as
 * required and to transform the
 * {@link org.springframework.beans.factory.config.BeanDefinition} of the enclosing
 * <code>&lt;bean&gt;</code> tag, potentially even returning a completely different
 * {@link org.springframework.beans.factory.config.BeanDefinition} to replace the
 * original.
 *
 * <p>{@link BeanDefinitionDecorator BeanDefinitionDecorators} should be aware that
 * they may be part of a chain. In particular, a {@link BeanDefinitionDecorator} should
 * be aware that a previous {@link BeanDefinitionDecorator} may have replaced the
 * original {@link org.springframework.beans.factory.config.BeanDefinition} with a
 * {@link org.springframework.aop.framework.ProxyFactoryBean} definition allowing for
 * custom {@link org.aopalliance.intercept.MethodInterceptor interceptors} to be added.
 *
 * <p>{@link BeanDefinitionDecorator BeanDefinitionDecorators} that wish to add an
 * interceptor to the enclosing bean should extend
 * {@link org.springframework.aop.config.AbstractInterceptorDrivenBeanDefinitionDecorator}
 * which handles the chaining ensuring that only one proxy is created and that it
 * contains all interceptors from the chain.
 *
 * <p>The parser locates a {@link BeanDefinitionDecorator} from the
 * {@link NamespaceHandler} for the namespace in which the custom tag resides.
 * *****************************************************************************************
 * ~$ 接口使用{@link DefaultBeanDefinitionDocumentReader }来处理自定义,嵌套(直属bean)标签.
 *
 * <p>装饰也可能出现基于自定义属性应用到bean标记.实现可以自由定制标记的元数据变成许多
 *   {@link org.springframework.beans.factory.config.BeanDefinition BeanDefinition }根据需要,
 *   变换{ @link org.springframework.beans.factory.config.BeanDefinition }包含bean的标签,
 *   甚至可能返回一个完全不同的{@link org.springframework.beans.factory.config.BeanDefinition }替换原来的.
 *
 * <p>{ @link BeanDefinitionDecorator BeanDefinitionDecorators }应该意识到他们可能是链的一部分.
 *   特别是,{ @link BeanDefinitionDecorator }应该意识到前一个{ @link BeanDefinitionDecorator }
 *    可能取代原{ @link org.springframework.beans.factory.config。BeanDefinition }与
 *    {@link org.springframework.aop.framework.ProxyFactoryBean }定义允许自定义
 *    {@link org.springframework.beans.factory.config.BeanDefinition }.
 *
 *
 * <p>{@link BeanDefinitionDecorator BeanDefinitionDecorators }希望添加一个拦截器的封闭bean应该扩展
 *    {@link org.springframework.aop.config.AbstractInterceptorDrivenBeanDefinitionDecorator }
 *    负责创建链接确保只有一个代理,它包含所有的拦截器链.
 *
 * <p>解析器定位一个{@link BeanDefinitionDecorator } {@link NamespaceHandler }的名称空间定义标记所在.
 *
 * @author Rob Harrop
 * @since 2.0
 * @see NamespaceHandler
 * @see BeanDefinitionParser
 */
public interface BeanDefinitionDecorator {

	/**
	 * Parse the specified {@link Node} (either an element or an attribute) and decorate
	 * the supplied {@link org.springframework.beans.factory.config.BeanDefinition},
	 * returning the decorated definition.
	 * <p>Implementations may choose to return a completely new definition, which will
	 * replace the original definition in the resulting
	 * {@link org.springframework.beans.factory.BeanFactory}.
	 * <p>The supplied {@link ParserContext} can be used to register any additional
	 * beans needed to support the main definition.
	 * *********************************************************************************
	 * ~$ 解析指定的{@link Node} (元素或属性)和装饰提供
	 *   {@link org.springframework.beans.factory.config.BeanDefinition },把装饰的定义.
	 *
	 * <p>实现可以选择返回一个全新的定义,它将替换原来的定义在结果{@link org.springframework.beans.factory.BeanFactory }.
	 * <p>提供的{@link ParserContext }可以用来注册任何额外的bean需要支持的主要定义.
	 */
	BeanDefinitionHolder decorate(Node node, BeanDefinitionHolder definition, ParserContext parserContext);

}
