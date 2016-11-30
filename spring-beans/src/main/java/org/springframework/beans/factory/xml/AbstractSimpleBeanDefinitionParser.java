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

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.core.Conventions;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Convenient base class for when there exists a one-to-one mapping
 * between attribute names on the element that is to be parsed and
 * the property names on the {@link Class} being configured.
 *
 * <p>Extend this parser class when you want to create a single
 * bean definition from a relatively simple custom XML element. The
 * resulting <code>BeanDefinition</code> will be automatically
 * registered with the relevant
 * {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}.
 *
 * <p>An example will hopefully make the use of this particular parser
 * class immediately clear. Consider the following class definition:
 * *************************************************************************
 * ~$方便的时基类之间存在着一对一的映射属性名称解析的元素和属性名称{@link Class}配置.
 *
 * <p>扩展这个解析器类当你想从一个相对简单的创建一个bean定义定制的XML元素.
 *    结果BeanDefinition将自动注册有关{@link org.springframework.beans.factory.support.BeanDefinitionRegistry }.
 *
 * <p>一个例子将希望使用这个特定的解析器类清楚.考虑下面的类定义:
 * <pre class="code">public class SimpleCache implements Cache {
 * 
 *     public void setName(String name) {...}
 *     public void setTimeout(int timeout) {...}
 *     public void setEvictionPolicy(EvictionPolicy policy) {...}
 * 
 *     // remaining class definition elided for clarity...
 * }</pre>
 *
 * <p>Then let us assume the following XML tag has been defined to
 * permit the easy configuration of instances of the above class;
 * ***************************************************************
 * ~$ 然后让我们假设以下XML标记定义允许上述类的实例的简单配置;
 * <pre class="code">&lt;caching:cache name="..." timeout="..." eviction-policy="..."/&gt;</pre>
 *
 * <p>All that is required of the Java developer tasked with writing
 * the parser to parse the above XML tag into an actual
 *
 * <p> 所有所需的Java开发人员负责编写解析器来解析上述XML标记成一个实际
 *
 * <code>SimpleCache</code> bean definition is the following:
 *
 * <pre class="code">public class SimpleCacheBeanDefinitionParser extends AbstractSimpleBeanDefinitionParser {
 *
 *     protected Class getBeanClass(Element element) {
 *         return SimpleCache.class;
 *     }
 * }</pre> 
 *
 * <p>Please note that the <code>AbstractSimpleBeanDefinitionParser</code>
 * is limited to populating the created bean definition with property values.
 * if you want to parse constructor arguments and nested elements from the
 * supplied XML element, then you will have to implement the
 * {@link #postProcess(BeanDefinitionBuilder, Element)}
 * method and do such parsing yourself, or (more likely) subclass the
 * {@link AbstractSingleBeanDefinitionParser} or {@link AbstractBeanDefinitionParser}
 * classes directly.
 *
 * <p>The process of actually registering the
 * <code>SimpleCacheBeanDefinitionParser</code> with the Spring XML parsing
 * infrastructure is described in the Spring Framework reference documentation
 * (in one of the appendices).
 *
 * <p>For an example of this parser in action (so to speak), do look at
 * the source code for the
 * {@link UtilNamespaceHandler.PropertiesBeanDefinitionParser};
 * the observant (and even not so observant) reader will immediately notice that
 * there is next to no code in the implementation. The
 * <code>PropertiesBeanDefinitionParser</code> populates a
 * {@link org.springframework.beans.factory.config.PropertiesFactoryBean}
 * from an XML element that looks like this:
 * <pre class="code">&lt;util:properties location="jdbc.properties"/&gt;</pre>
 *
 * <p>The observant reader will notice that the sole attribute on the
 * <code>&lt;util:properties/&gt;</code> element matches the
 * {@link org.springframework.beans.factory.config.PropertiesFactoryBean#setLocation(org.springframework.core.io.Resource)}
 * method name on the <code>PropertiesFactoryBean</code> (the general
 * usage thus illustrated holds true for any number of attributes).
 * All that the <code>PropertiesBeanDefinitionParser</code> needs
 * actually do is supply an implementation of the
 * {@link #getBeanClass(Element)} method to return the
 * <code>PropertiesFactoryBean</code> type.
 * ************************************************************************************************
 * <p>请注意,AbstractSimpleBeanDefinitionParser有限与属性值填充创建的bean定义.
 * 如果你想解析构造函数参数,从提供的XML元素嵌套的元素,那么你将不得不实现
 * {@link #postProcess(BeanDefinitionBuilder, Element)}解析方法和做这样的自己,或者(更有可能)子类
 *  {@link AbstractSingleBeanDefinitionParser }或{@link AbstractBeanDefinitionParser }直接类.
 *
 * <p>注册的过程实际上的SimpleCacheBeanDefinitionParser Spring XML解析基础设施是Spring框架参考文档中描述(在一个附录).
 *
 * <p>这个解析器的一个示例的行动(这么说),做看的源代码{@link UtilNamespaceHandler.PropertiesBeanDefinitionParser },
 *  细心的读者(甚至不太细心的)会立即注意到旁边没有代码实现.
 * PropertiesBeanDefinitionParser填充{@link org.springframework.beans.factory.config.PropertiesFactoryBean }从XML元素看起来像这样:
 * 								util:properties location="jdbc.properties"
 *
 * <p>细心的读者会注意到,跑龙套的唯一属性:属性元素匹配的
 *    {@link org.springframework.beans.factory.config.PropertiesFactoryBean setLocation #(org.springframework.core.io.Resource)}
 *    在PropertiesFactoryBean方法名称(一般使用说明适用于任意数量的属性).
 *    PropertiesBeanDefinitionParser需要做的是提供一个实现的{@link #getBeanClass(Element)}方法返回PropertiesFactoryBean类型.
 *
 * @author Rob Harrop
 * @author Rick Evans
 * @author Juergen Hoeller
 * @since 2.0
 * @see Conventions#attributeNameToPropertyName(String)
 */
public abstract class AbstractSimpleBeanDefinitionParser extends AbstractSingleBeanDefinitionParser {

	/**
	 * Parse the supplied {@link Element} and populate the supplied
	 * {@link BeanDefinitionBuilder} as required.
	 * <p>This implementation maps any attributes present on the
	 * supplied element to {@link org.springframework.beans.PropertyValue}
	 * instances, and
	 * {@link BeanDefinitionBuilder#addPropertyValue(String, Object) adds them}
	 * to the
	 * {@link org.springframework.beans.factory.config.BeanDefinition builder}.
	 * <p>The {@link #extractPropertyName(String)} method is used to
	 * reconcile the name of an attribute with the name of a JavaBean
	 * property.
	 * ************************************************************************
	 * ~$ 解析提供{@link Element}和填充提供{@link BeanDefinitionBuilder }.
	 * <p>这个实现地图上所提供的任何属性元素{@link org.springframework.beans.PropertyValue }实例和
	 * {@link BeanDefinitionBuilder#addPropertyValue(String, Object) adds them},
	 * {@link org.springframework.beans.factory.config.BeanDefinition builder }.
	 * @param element the XML element being parsed
	 * @param builder used to define the <code>BeanDefinition</code>
	 * @see #extractPropertyName(String) 
	 */
	@Override
	protected void doParse(Element element, ParserContext parserContext, BeanDefinitionBuilder builder) {
		NamedNodeMap attributes = element.getAttributes();
		for (int x = 0; x < attributes.getLength(); x++) {
			Attr attribute = (Attr) attributes.item(x);
			if (isEligibleAttribute(attribute, parserContext)) {
				String propertyName = extractPropertyName(attribute.getLocalName());
				Assert.state(StringUtils.hasText(propertyName),
						"Illegal property name returned from 'extractPropertyName(String)': cannot be null or empty.");
				builder.addPropertyValue(propertyName, attribute.getValue());
			}
		}
		postProcess(builder, element);
	}

	/**
	 * Determine whether the given attribute is eligible for being
	 * turned into a corresponding bean property value.
	 * <p>The default implementation considers any attribute as eligible,
	 * except for the "id" attribute and namespace declaration attributes.
	 * *******************************************************************
	 * ~$ 确定给定的属性是否符合变成相应的bean属性值.
	 * <p>默认实现认为任何属性是合格的,除了"id"属性和名称空间声明属性.
	 * @param attribute the XML attribute to check
	 * @param parserContext the <code>ParserContext</code>
	 * @see #isEligibleAttribute(String)
	 */
	protected boolean isEligibleAttribute(Attr attribute, ParserContext parserContext) {
		boolean eligible = isEligibleAttribute(attribute);
		if(!eligible) {
			String fullName = attribute.getName();
			eligible = (!fullName.equals("xmlns") && !fullName.startsWith("xmlns:") &&
					isEligibleAttribute(parserContext.getDelegate().getLocalName(attribute)));
		}
		return eligible;
	}

	/**
	 * Determine whether the given attribute is eligible for being
	 * turned into a corresponding bean property value.
	 * <p>The default implementation considers any attribute as eligible,
	 * except for the "id" attribute and namespace declaration attributes.
	 * ******************************************************************
	 * ~$ 确定给定的属性是否符合变成相应的bean属性值.
	 * <p>默认实现认为任何属性是合格的,除了"id"属性和名称空间声明属性.
	 * @param attribute the XML attribute to check
	 * @see #isEligibleAttribute(String)
	 * @deprecated in favour of {@link #isEligibleAttribute(Attr, ParserContext)}
	 */
	@Deprecated
	protected boolean isEligibleAttribute(Attr attribute) {
		return false;
	}

	/**
	 * Determine whether the given attribute is eligible for being
	 * turned into a corresponding bean property value.
	 * <p>The default implementation considers any attribute as eligible,
	 * except for the "id" attribute.
	 * ******************************************************************
	 * ~$ 确定给定的属性是否符合变成相应的bean属性值.
	 * <p>默认实现认为任何属性是合格的,除了"id"属性.
	 * @param attributeName the attribute name taken straight from the
	 * XML element being parsed (never <code>null</code>)
	 */
	protected boolean isEligibleAttribute(String attributeName) {
		return !ID_ATTRIBUTE.equals(attributeName);
	}

	/**
	 * Extract a JavaBean property name from the supplied attribute name.
	 * <p>The default implementation uses the
	 * {@link Conventions#attributeNameToPropertyName(String)}
	 * method to perform the extraction.
	 * <p>The name returned must obey the standard JavaBean property name
	 * conventions. For example for a class with a setter method
	 * '<code>setBingoHallFavourite(String)</code>', the name returned had
	 * better be '<code>bingoHallFavourite</code>' (with that exact casing).
	 * *********************************************************************
	 * ~$ 从提供的属性名称中提取一个JavaBean属性名称.
	 * <p>默认实现使用{@link Conventions#attributeNameToPropertyName(String)}执行提取方法.
	 * <p>必须遵守标准的JavaBean属性返回的名字命名约定.例如一个类和一个setter方法的setBingoHallFavourite(String),
	 *    返回的名字最好是“bingoHallFavourite”(以同样的外壳).
	 * @param attributeName the attribute name taken straight from the
	 * XML element being parsed (never <code>null</code>)
	 * @return the extracted JavaBean property name (must never be <code>null</code>)
	 */
	protected String extractPropertyName(String attributeName) {
		return Conventions.attributeNameToPropertyName(attributeName);
	}

	/**
	 * Hook method that derived classes can implement to inspect/change a
	 * bean definition after parsing is complete.
	 * <p>The default implementation does nothing.
	 * ******************************************************************
	 * ~$ 钩方法,派生类可以实现检查/改变一个bean定义解析完成后.
	 * <p>没有默认的实现.
	 * @param beanDefinition the parsed (and probably totally defined) bean definition being built
	 *                       ~$ 解析(也许完全定义)bean定义
	 * @param element the XML element that was the source of the bean definition's metadata
	 */
	protected void postProcess(BeanDefinitionBuilder beanDefinition, Element element) {
	}

}
