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

package org.springframework.beans.factory.xml;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;

/**
 * Base interface used by the {@link DefaultBeanDefinitionDocumentReader}
 * for handling custom namespaces in a Spring XML configuration file.
 *
 * <p>Implementations are expected to return implementations of the
 * {@link BeanDefinitionParser} interface for custom top-level tags and
 * implementations of the {@link BeanDefinitionDecorator} interface for
 * custom nested tags.
 *
 * <p>The parser will call {@link #parse} when it encounters a custom tag
 * directly under the <code>&lt;beans&gt;</code> tags and {@link #decorate} when
 * it encounters a custom tag directly under a <code>&lt;bean&gt;</code> tag.
 *
 * <p>Developers writing their own custom element extensions typically will
 * not implement this interface drectly, but rather make use of the provided
 * {@link NamespaceHandlerSupport} class.
 * *******************************************************************************
 * ~$ 基地所使用的接口{@link DefaultBeanDefinitionDocumentReader }来处理自定义名称空间在Spring XML配置文件.
 *
 * <p>实现预期回报的实现{@link BeanDefinitionParser }界面定制顶级的标签和实现{@link BeanDefinitionDecorator }界面自定义嵌套标签.
 *
 * <p>解析器将调用{@link #parse}当遇到一个自定义标签直属bean标签和{@link #decorate}当遇到一个自定义标签直属bean标记.
 *
 * <p>开发人员编写自己的定制元素扩展通常不会实现这个接口drectly,而是利用所提供的{@link NamespaceHandlerSupport }类.
 * @author Rob Harrop
 * @author Erik Wiersma
 * @since 2.0
 * @see DefaultBeanDefinitionDocumentReader
 * @see NamespaceHandlerResolver
 */
public interface NamespaceHandler {

	/**
	 * Invoked by the {@link DefaultBeanDefinitionDocumentReader} after
	 * construction but before any custom elements are parsed.
	 * ******************************************************************
	 * ~$ 调用的{@link DefaultBeanDefinitionDocumentReader }施工后但在任何自定义元素解析.
	 * @see NamespaceHandlerSupport#registerBeanDefinitionParser(String, BeanDefinitionParser) 
	 */
	void init();

	/**
	 * Parse the specified {@link Element} and register any resulting
	 * {@link BeanDefinition BeanDefinitions} with the
	 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
	 * that is embedded in the supplied {@link ParserContext}.
	 * <p>Implementations should return the primary <code>BeanDefinition</code>
	 * that results from the parse phase if they wish to be used nested
	 * inside (for example) a <code>&lt;property&gt;</code> tag.
	 * <p>Implementations may return <code>null</code> if they will
	 * <strong>not</strong> be used in a nested scenario.
	 * **************************************************************************
	 * ~$ 解析指定的{@link Element}和注册任何结果{@link BeanDefinition BeanDefinition }
	 *  与{@link org.springframework.beans.factory.support.BeanDefinitionRegistry }
	 *  这是嵌入在提供的{@link ParserContext }.
	 * <p>实现应该返回主BeanDefinition解析阶段的结果,如果他们希望使用嵌套在(比方说)一个属性标签.
	 * <p>实现可能会返回null如果他们不会在一个嵌套的场景中使用.
	 * @param element the element that is to be parsed into one or more <code>BeanDefinitions</code>
	 *                ~$元素被解析成一个或多个beandefinition
	 * @param parserContext the object encapsulating the current state of the parsing process
	 *                      ~$ 对象封装解析过程的当前状态
	 * @return the primary <code>BeanDefinition</code> (can be <code>null</code> as explained above) 
	 */
	BeanDefinition parse(Element element, ParserContext parserContext);

	/**
	 * Parse the specified {@link Node} and decorate the supplied
	 * {@link BeanDefinitionHolder}, returning the decorated definition.
	 * <p>The {@link Node} may be either an {@link org.w3c.dom.Attr} or an
	 * {@link Element}, depending on whether a custom attribute or element
	 * is being parsed.
	 * <p>Implementations may choose to return a completely new definition,
	 * which will replace the original definition in the resulting
	 * {@link org.springframework.beans.factory.BeanFactory}.
	 * <p>The supplied {@link ParserContext} can be used to register any
	 * additional beans needed to support the main definition.
	 * **********************************************************************
	 * ~$ 解析指定的{@link Node} 和装饰提供{@link BeanDefinitionHolder },返回装饰的定义.
	 * <p>{@link Node}节点可以是一个{@link org.w3c.dom.Attr }或{@link Element},
	 *     取决于一个自定义属性或元素被解析.
	 * <p>实现可以选择返回一个全新的定义,它将替换原来的定义在结果{@link org.springframework.beans.factory.BeanFactory }.
	 * <p>提供的{@link ParserContext }可以用来注册任何额外的bean需要支持的主要定义.
	 * @param source the source element or attribute that is to be parsed
	 *               ~$源要解析的元素或属性
	 * @param definition the current bean definition
	 *                   ~$the current bean definition
	 * @param parserContext the object encapsulating the current state of the parsing process
	 *                      ~$对象封装解析过程的当前状态
	 * @return the decorated definition (to be registered in the BeanFactory),
	 * or simply the original bean definition if no decoration is required.
	 * A <code>null</code> value is strictly speaking invalid, but will be leniently
	 * treated like the case where the original bean definition gets returned.
	 */
	BeanDefinitionHolder decorate(Node source, BeanDefinitionHolder definition, ParserContext parserContext);

}
