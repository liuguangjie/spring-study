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

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.env.Environment;

import org.w3c.dom.Document;

/**
 * SPI for parsing an XML document that contains Spring bean definitions.
 * Used by XmlBeanDefinitionReader for actually parsing a DOM document.
 *
 * <p>Instantiated per document to parse: Implementations can hold
 * state in instance variables during the execution of the
 * <code>registerBeanDefinitions</code> method, for example global
 * settings that are defined for all bean definitions in the document.
 * **********************************************************************
 * ~$ SPI解析一个XML文档,其中包含Spring bean定义。实际上XmlBeanDefinitionReader用于解析DOM文档.
 *
 * <p>实例化每个文档解析:实现可以在实例变量中保持状态的执行期间registerBeanDefinitions方法,
 *    例如全局设置定义的所有bean定义文件.
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 18.12.2003
 * @see XmlBeanDefinitionReader#setDocumentReaderClass
 */
public interface BeanDefinitionDocumentReader {

	/**
	 * Read bean definitions from the given DOM document,
	 * and register them with the given bean factory.
	 * **************************************************
	 * ~$ 从给定的DOM文档读取bean定义,用给定的bean注册工厂.
	 * @param doc the DOM document
	 * @param readerContext the current context of the reader. Includes the resource being parsed
	 *                      ~$   当前上下文的读者.包括资源被解析
	 * @throws BeanDefinitionStoreException in case of parsing errors
	 */
	void registerBeanDefinitions(Document doc, XmlReaderContext readerContext)
			throws BeanDefinitionStoreException;

	/**
	 * Set the Environment to use when reading bean definitions. Used for evaluating
	 * profile information to determine whether a {@code <beans/>} document/element should
	 * be included or omitted.
	 * ***********************************************************************************
	 * ~$ 设置环境时使用阅读bean定义.用于评估概要信息,以确定一个{@code <bean/> }document/element应该包含或省略.
	 */
	void setEnvironment(Environment environment);

}
