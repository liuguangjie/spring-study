/*
 * Copyright 2002-2010 the original author or authors.
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

import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

/**
 * Base class for those {@link BeanDefinitionParser} implementations that
 * need to parse and define just a <i>single</i> <code>BeanDefinition</code>.
 *
 * <p>Extend this parser class when you want to create a single bean definition
 * from an arbitrarily complex XML element. You may wish to consider extending
 * the {@link AbstractSimpleBeanDefinitionParser} when you want to create a
 * single bean definition from a relatively simple custom XML element.
 *
 * <p>The resulting <code>BeanDefinition</code> will be automatically registered
 * with the {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}.
 * Your job simply is to {@link #doParse parse} the custom XML {@link Element}
 * into a single <code>BeanDefinition</code>.
 * **********************************************************************************
 * ~$ 基类的{@link BeanDefinitionParser }的实现只需要解析和定义一个BeanDefinition.
 * <p>扩展这个解析器类当您想要创建一个bean定义任意复杂的XML元素.您可能希望考虑扩展{@link AbstractSimpleBeanDefinitionParser }
 *    当你想从一个相对简单的创建一个bean定义定制的XML元素.
 *
 * <p>结果BeanDefinition将自动注册的{@link org.springframework.beans.factory.support.BeanDefinitionRegistry }
 * 你的工作仅仅是{@link #doParse parse}定制XML{@link Element}到单个BeanDefinition.
 *
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @since 2.0
 * @see #getBeanClass
 * @see #getBeanClassName
 * @see #doParse
 */
public abstract class AbstractSingleBeanDefinitionParser extends AbstractBeanDefinitionParser {

	/**
	 * Creates a {@link BeanDefinitionBuilder} instance for the
	 * {@link #getBeanClass bean Class} and passes it to the
	 * {@link #doParse} strategy method.
	 * ********************************************************
	 * ~$ 创建一个{@link BeanDefinitionBuilder } {@link #getBeanClass bean Class}
	 *   的实例并将其传递到{@link #doParse} 的策略方法.
	 * @param element the element that is to be parsed into a single BeanDefinition
	 *                ~$ 元素也被解析成一个BeanDefinition
	 * @param parserContext the object encapsulating the current state of the parsing process
	 *                      ~$ 对象封装解析过程的当前状态
	 * @return the BeanDefinition resulting from the parsing of the supplied {@link Element}
	 * ~$ 产生的BeanDefinition解析提供{@link Element}
	 * @throws IllegalStateException if the bean {@link Class} returned from
	 * {@link #getBeanClass(Element)} is <code>null</code>
	 * @see #doParse
	 */
	@Override
	protected final AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
		String parentName = getParentName(element);
		if (parentName != null) {
			builder.getRawBeanDefinition().setParentName(parentName);
		}
		Class<?> beanClass = getBeanClass(element);
		if (beanClass != null) {
			builder.getRawBeanDefinition().setBeanClass(beanClass);
		}
		else {
			String beanClassName = getBeanClassName(element);
			if (beanClassName != null) {
				builder.getRawBeanDefinition().setBeanClassName(beanClassName);
			}
		}
		builder.getRawBeanDefinition().setSource(parserContext.extractSource(element));
		if (parserContext.isNested()) {
			// Inner bean definition must receive same scope as containing bean.
			builder.setScope(parserContext.getContainingBeanDefinition().getScope());
		}
		if (parserContext.isDefaultLazyInit()) {
			// Default-lazy-init applies to custom bean definitions as well.
			builder.setLazyInit(true);
		}
		doParse(element, parserContext, builder);
		return builder.getBeanDefinition();
	}

	/**
	 * Determine the name for the parent of the currently parsed bean,
	 * in case of the current bean being defined as a child bean.
	 * <p>The default implementation returns <code>null</code>,
	 * indicating a root bean definition.
	 * **************************************************************
	 * ~$ 确定parent 当前解析的bean的名称,当前的bean定义为一个childbean.
	 * <p>默认实现返回null,表明根bean定义.
	 * @param element the <code>Element</code> that is being parsed
	 * @return the name of the parent bean for the currently parsed bean,
	 * or <code>null</code> if none
	 */
	protected String getParentName(Element element) {
		return null;
	}

	/**
	 * Determine the bean class corresponding to the supplied {@link Element}.
	 * <p>Note that, for application classes, it is generally preferable to
	 * override {@link #getBeanClassName} instead, in order to avoid a direct
	 * dependence on the bean implementation class. The BeanDefinitionParser
	 * and its NamespaceHandler can be used within an IDE plugin then, even
	 * if the application classes are not available on the plugin's classpath.
	 * ***********************************************************************
	 * ~$ 确定相对应的bean类提供{@link Element}.
	 * <p>注意,应用程序类,通常比覆盖{@link #getBeanClassName }相反,为了避免直接对bean实现类的依赖.
	 *    BeanDefinitionParser及其NamespaceHandler内可以使用IDE插件之后,即使应用程序类是不可以在插件的类路径中.
	 *
	 * @param element the <code>Element</code> that is being parsed
	 *                ~$ 被解析的元素
	 * @return the {@link Class} of the bean that is being defined via parsing
	 * the supplied <code>Element</code>, or <code>null</code> if none
	 * @see #getBeanClassName
	 */
	protected Class<?> getBeanClass(Element element) {
		return null;
	}

	/**
	 * Determine the bean class name corresponding to the supplied {@link Element}.
	 * ****************************************************************************
	 * ~$ 确定相对应的bean类提供{@link Element}.
	 * @param element the <code>Element</code> that is being parsed
	 * @return the class name of the bean that is being defined via parsing
	 * the supplied <code>Element</code>, or <code>null</code> if none
	 * @see #getBeanClass
	 */
	protected String getBeanClassName(Element element) {
		return null;
	}

	/**
	 * Parse the supplied {@link Element} and populate the supplied
	 * {@link BeanDefinitionBuilder} as required.
	 * <p>The default implementation delegates to the <code>doParse</code>
	 * version without ParserContext argument.
	 * ********************************************************************
	 * ~$ 解析提供{@link Element}和填充提供{@link BeanDefinitionBuilder }.
	 * <p>默认实现代表doParse版本没有ParserContext论点.
	 * @param element the XML element being parsed
	 * @param parserContext the object encapsulating the current state of the parsing process
	 * @param builder used to define the <code>BeanDefinition</code>
	 * @see #doParse(Element, BeanDefinitionBuilder)
	 */
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		doParse(element, builder);
	}

	/**
	 * Parse the supplied {@link Element} and populate the supplied
	 * {@link BeanDefinitionBuilder} as required.
	 * <p>The default implementation does nothing.
	 * *************************************************************
	 * ~$ 解析提供{@link Element}和填充提供{@link BeanDefinitionBuilder}.
	 * <p>没有默认的实现.
	 * @param element the XML element being parsed
	 * @param builder used to define the <code>BeanDefinition</code>
	 */
	protected void doParse(Element element, BeanDefinitionBuilder builder) {
	}

}
