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

package org.springframework.beans.factory.xml;

import org.w3c.dom.Element;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.util.StringUtils;

/**
 * Abstract {@link BeanDefinitionParser} implementation providing
 * a number of convenience methods and a
 * {@link AbstractBeanDefinitionParser#parseInternal template method}
 * that subclasses must override to provide the actual parsing logic.
 *
 * <p>Use this {@link BeanDefinitionParser} implementation when you want
 * to parse some arbitrarily complex XML into one or more
 * {@link BeanDefinition BeanDefinitions}. If you just want to parse some
 * XML into a single <code>BeanDefinition</code>, you may wish to consider
 * the simpler convenience extensions of this class, namely
 * {@link AbstractSingleBeanDefinitionParser} and
 * {@link AbstractSimpleBeanDefinitionParser}.
 * ***********************************************************************
 * ~$文摘{@link BeanDefinitionParser }实现提供一个方便的方法和一个
 * {@link AbstractBeanDefinitionParser#parseInternal template method},子类必须覆盖提供实际的解析逻辑.
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @author Rick Evans
 * @author Dave Syer
 * @since 2.0
 */
public abstract class AbstractBeanDefinitionParser implements BeanDefinitionParser {

	/** Constant for the id attribute */
	public static final String ID_ATTRIBUTE = "id";

	/** Constant for the name attribute */
	public static final String NAME_ATTRIBUTE = "name";

	public final BeanDefinition parse(Element element, ParserContext parserContext) {
		AbstractBeanDefinition definition = parseInternal(element, parserContext);
		if (definition != null && !parserContext.isNested()) {
			try {
				String id = resolveId(element, definition, parserContext);
				if (!StringUtils.hasText(id)) {
					parserContext.getReaderContext().error(
							"Id is required for element '" + parserContext.getDelegate().getLocalName(element)
									+ "' when used as a top-level tag", element);
				}
				String[] aliases = new String[0];
				String name = element.getAttribute(NAME_ATTRIBUTE);
				if (StringUtils.hasLength(name)) {
					aliases = StringUtils.trimArrayElements(StringUtils.commaDelimitedListToStringArray(name));
				}
				BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, id, aliases);
				registerBeanDefinition(holder, parserContext.getRegistry());
				if (shouldFireEvents()) {
					BeanComponentDefinition componentDefinition = new BeanComponentDefinition(holder);
					postProcessComponentDefinition(componentDefinition);
					parserContext.registerComponent(componentDefinition);
				}
			}
			catch (BeanDefinitionStoreException ex) {
				parserContext.getReaderContext().error(ex.getMessage(), element);
				return null;
			}
		}
		return definition;
	}

	/**
	 * Resolve the ID for the supplied {@link BeanDefinition}.
	 * <p>When using {@link #shouldGenerateId generation}, a name is generated automatically.
	 * Otherwise, the ID is extracted from the "id" attribute, potentially with a
	 * {@link #shouldGenerateIdAsFallback() fallback} to a generated id.
	 * **************************************************************************************
	 * ~$ 解决的ID提供{ @link BeanDefinition }.
	 * <p>当使用{ @link # shouldGenerateId代},一个名字是自动生成的.
	 *    否则,ID是提取“ID”属性,可能与{@link #shouldGenerateIdAsFallback() fallback }生成的ID.
	 * @param element the element that the bean definition has been built from
	 *                ~$ bean定义的元素已经建成
	 * @param definition the bean definition to be registered  ~$ 注册的bean定义
	 * @param parserContext the object encapsulating the current state of the parsing process;
	 * provides access to a {@link BeanDefinitionRegistry}
	 *                      ~$ 对象封装的当前状态解析过程,提供了访问{@link BeanDefinitionRegistry }
	 * @return the resolved id
	 * @throws BeanDefinitionStoreException if no unique name could be generated
	 * for the given bean definition
	 */
	protected String resolveId(Element element, AbstractBeanDefinition definition, ParserContext parserContext)
			throws BeanDefinitionStoreException {

		if (shouldGenerateId()) {
			return parserContext.getReaderContext().generateBeanName(definition);
		}
		else {
			String id = element.getAttribute(ID_ATTRIBUTE);
			if (!StringUtils.hasText(id) && shouldGenerateIdAsFallback()) {
				id = parserContext.getReaderContext().generateBeanName(definition);
			}
			return id;
		}
	}

	/**
	 * Register the supplied {@link BeanDefinitionHolder bean} with the supplied
	 * {@link BeanDefinitionRegistry registry}.
	 * <p>Subclasses can override this method to control whether or not the supplied
	 * {@link BeanDefinitionHolder bean} is actually even registered, or to
	 * register even more beans.
	 * <p>The default implementation registers the supplied {@link BeanDefinitionHolder bean}
	 * with the supplied {@link BeanDefinitionRegistry registry} only if the <code>isNested</code>
	 * parameter is <code>false</code>, because one typically does not want inner beans
	 * to be registered as top level beans.
	 * *******************************************************************************************
	 * ~$注册提供{@link BeanDefinitionHolder bean}与提供的{@link BeanDefinitionRegistry registry}.
	 * <p>子类可以重写这个方法来控制是否提供{@link BeanDefinitionHolder bean}是即使注册,或注册更多的bean.
	 * @param definition the bean definition to be registered  ~$ 注册的bean定义
	 * @param registry the registry that the bean is to be registered with 
	 * @see BeanDefinitionReaderUtils#registerBeanDefinition(BeanDefinitionHolder, BeanDefinitionRegistry)
	 */
	protected void registerBeanDefinition(BeanDefinitionHolder definition, BeanDefinitionRegistry registry) {
		BeanDefinitionReaderUtils.registerBeanDefinition(definition, registry);
	}


	/**
	 * Central template method to actually parse the supplied {@link Element}
	 * into one or more {@link BeanDefinition BeanDefinitions}.
	 * **********************************************************************
	 * ~$中央模板方法实际上提供的{@link Element}解析成一个或多个{@link BeanDefinition BeanDefinition }.
	 * @param element	the element that is to be parsed into one or more {@link BeanDefinition BeanDefinitions}
	 *                  ~$ 元素被解析成一个或多个{@link BeanDefinition BeanDefinition}
	 * @param parserContext the object encapsulating the current state of the parsing process;
	 * provides access to a {@link BeanDefinitionRegistry}
	 *                      ~$    对象封装的当前状态解析过程,提供了访问{@link BeanDefinitionRegistry}
	 * @return the primary {@link BeanDefinition} resulting from the parsing of the supplied {@link Element}
	 * @see #parse(Element, ParserContext)
	 * @see #postProcessComponentDefinition(BeanComponentDefinition)
	 */
	protected abstract AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext);

	/**
	 * Should an ID be generated instead of read from the passed in {@link Element}?
	 * <p>Disabled by default; subclasses can override this to enable ID generation.
	 * Note that this flag is about <i>always</i> generating an ID; the parser
	 * won't even check for an "id" attribute in this case.
	 * ******************************************************************************
	 * ~$应该一个ID生成而不是从通过读取{@link Element}?
	 * <p>默认情况下禁用,子类可以重写这个使ID的一代.
	 *    注意,这个标志是总是生成一个ID;解析器甚至不会检查一个“ID”属性.
	 * @return whether the parser should always generate an id
	 */
	protected boolean shouldGenerateId() {
		return false;
	}

	/**
	 * Should an ID be generated instead if the passed in {@link Element} does not
	 * specify an "id" attribute explicitly?
	 * <p>Disabled by default; subclasses can override this to enable ID generation
	 * as fallback: The parser will first check for an "id" attribute in this case,
	 * only falling back to a generated ID if no value was specified.
	 * ****************************************************************************
	 * ~$应该一个ID生成如果在{@link Element}通过没有显式地指定一个“ID”属性?
	 * <p>默认情况下禁用,子类可以重写这个使ID生成回退:解析器将首先检查“ID”属性在这种情况下,只有回落如果没有指定值生成的ID.
	 * @return whether the parser should generate an id if no id was specified
	 */
	protected boolean shouldGenerateIdAsFallback() {
		return false;
	}

	/**
	 * Controls whether this parser is supposed to fire a
	 * {@link BeanComponentDefinition}
	 * event after parsing the bean definition.
	 * <p>This implementation returns <code>true</code> by default; that is,
	 * an event will be fired when a bean definition has been completely parsed.
	 * Override this to return <code>false</code> in order to suppress the event.
	 * **************************************************************************
	 * ~$控制这个解析器是否应该火{@link BeanComponentDefinition }事件后解析bean定义.
	 * <p>这个实现默认返回true;也就是说,将触发一个事件,当一个bean的定义已经完全解析.
	 *    覆盖这个返回false,以镇压事件.
	 * @return <code>true</code> in order to fire a component registration event
	 * after parsing the bean definition; <code>false</code> to suppress the event
	 * @see #postProcessComponentDefinition
	 * @see org.springframework.beans.factory.parsing.ReaderContext#fireComponentRegistered
	 */
	protected boolean shouldFireEvents() {
		return true;
	}

	/**
	 * Hook method called after the primary parsing of a
	 * {@link BeanComponentDefinition} but before the
	 * {@link BeanComponentDefinition} has been registered with a
	 * {@link BeanDefinitionRegistry}.
	 * <p>Derived classes can override this method to supply any custom logic that
	 * is to be executed after all the parsing is finished.
	 * <p>The default implementation is a no-op.
	 * ***************************************************************************
	 * ~$ 钩方法命名的主要解析{@link BeanComponentDefinition }但是在{@link BeanComponentDefinition }
	 *   已注册{@link BeanDefinitionRegistry }
	 * @param componentDefinition the {@link BeanComponentDefinition} that is to be processed
	 */
	protected void postProcessComponentDefinition(BeanComponentDefinition componentDefinition) {
	}

}
