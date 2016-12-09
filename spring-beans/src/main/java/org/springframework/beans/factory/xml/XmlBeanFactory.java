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

package org.springframework.beans.factory.xml;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;

/**
 * Convenience extension of {@link DefaultListableBeanFactory} that reads bean definitions
 * from an XML document. Delegates to {@link XmlBeanDefinitionReader} underneath; effectively
 * equivalent to using an XmlBeanDefinitionReader with a DefaultListableBeanFactory.
 ****************************************************************************************
 * 方便的扩展{@link DefaultListableBeanFactory },从XML文档读取bean定义。代表{@link XmlBeanDefinitionReader }下面;
 * 有效相当于使用XmlBeanDefinitionReader DefaultListableBeanFactory。
 *
 * <p>The structure, element and attribute names of the required XML document
 * are hard-coded in this class. (Of course a transform could be run if necessary
 * to produce this format). "beans" doesn't need to be the root element of the XML
 * document: This class will parse all bean definition elements in the XML file.
 * *******************************************************************************
 * 结构、元素和属性名称所需的XML文档都硬编码在这个类。(当然可以运行在必要时变换产生这种格式)。
 * “bean”不需要XML文档的根元素:这个类将解析所有bean定义XML文件中的元素。
 *
 * <p>This class registers each bean definition with the {@link DefaultListableBeanFactory}
 * superclass, and relies on the latter's implementation of the {@link BeanFactory} interface.
 * It supports singletons, prototypes, and references to either of these kinds of bean.
 * See {@code "spring-beans-3.x.xsd"} (or historically, {@code "spring-beans-2.0.dtd"}) for
 * details on options and configuration style.
 * ********************************************************************************
 * 这类注册每个bean定义{@link DefaultListableBeanFactory }超类,和依赖于后者的实现{@link BeanFactory }接口。它支持单件,原型,对这两种类型的bean的引用。
 * 看到{@code "spring-bean-3.x.xsd" }(或从历史上看,{@code spring-beans-2.0.dtd " })对细节的选择和配置方式。
 *
 * <p><b>For advanced needs, consider using a {@link DefaultListableBeanFactory} with
 * an {@link XmlBeanDefinitionReader}.</b> The latter allows for reading from multiple XML
 * resources and is highly configurable in its actual XML parsing behavior.
 * ********************************************************************************
 * 对于高级需求,考虑使用 {@link DefaultListableBeanFactory }与{@link XmlBeanDefinitionReader }
 * 后者允许读取来自多个XML资源和在其实际的XML解析行为是高度可配置的
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 15 April 2001
 * @see DefaultListableBeanFactory
 * @see XmlBeanDefinitionReader
 * @deprecated as of Spring 3.1 in favor of {@link DefaultListableBeanFactory} and
 * {@link XmlBeanDefinitionReader}
 */
@Deprecated
public class XmlBeanFactory extends DefaultListableBeanFactory {

	private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);


	/**
	 * Create a new XmlBeanFactory with the given resource,
	 * which must be parsable using DOM.
	 * ***************************************************
	 * 用给定的资源,创建一个新的XmlBeanFactory必须使用DOM解析
	 * @param resource XML resource to load bean definitions from
	 *                 XML资源加载bean定义
	 * @throws BeansException in case of loading or parsing errors
	 */
	public XmlBeanFactory(Resource resource) throws BeansException {
		this(resource, null);
	}

	/**
	 * Create a new XmlBeanFactory with the given input stream,
	 * which must be parsable using DOM.
	 * @param resource XML resource to load bean definitions from
	 * @param parentBeanFactory parent bean factory
	 * @throws BeansException in case of loading or parsing errors
	 */
	public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
		super(parentBeanFactory);
		/**  这个方法做的事情比较多 */
		this.reader.loadBeanDefinitions(resource);
	}

}
