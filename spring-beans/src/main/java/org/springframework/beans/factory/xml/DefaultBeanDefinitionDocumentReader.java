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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;

/**
 * Default implementation of the {@link BeanDefinitionDocumentReader} interface.
 * Reads bean definitions according to the "spring-beans" DTD and XSD format
 * (Spring's default XML bean definition format).
 *
 * <p>The structure, elements and attribute names of the required XML document
 * are hard-coded in this class. (Of course a transform could be run if necessary
 * to produce this format). <code>&lt;beans&gt;</code> doesn't need to be the root
 * element of the XML document: This class will parse all bean definition elements
 * in the XML file, not regarding the actual root element.
 * *******************************************************************************
 * ~$ 接口的默认实现{@link BeanDefinitionDocumentReader }.
 *   根据“Spring bean”读取bean定义DTD和XSD格式(春天的默认XML bean定义格式).
 *
 * <p>的结构、元素和属性名称所需的XML文档都硬编码在这个类.
 *    (当然可以运行在必要时变换产生这种格式).豆子不需要XML文档的根元素:这个类将解析所有bean定义XML文件中的元素,而不是对实际的根元素.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Erik Wiersma
 * @since 18.12.2003
 */
public class DefaultBeanDefinitionDocumentReader implements BeanDefinitionDocumentReader {

	public static final String BEAN_ELEMENT = BeanDefinitionParserDelegate.BEAN_ELEMENT;

	public static final String NESTED_BEANS_ELEMENT = "beans";

	public static final String ALIAS_ELEMENT = "alias";

	public static final String NAME_ATTRIBUTE = "name";

	public static final String ALIAS_ATTRIBUTE = "alias";

	public static final String IMPORT_ELEMENT = "import";

	public static final String RESOURCE_ATTRIBUTE = "resource";

	/** @see org.springframework.context.annotation.Profile */
	public static final String PROFILE_ATTRIBUTE = "profile";


	protected final Log logger = LogFactory.getLog(getClass());

	private XmlReaderContext readerContext;

	private Environment environment;

	private BeanDefinitionParserDelegate delegate;


	/**
	 * {@inheritDoc}
	 * <p>Default value is {@code null}; property is required for parsing any
	 * {@code <beans/>} element with a {@code profile} attribute present.
	 * ***********************************************************************
	 * ~$ 默认值是 {@code null}; 属性需要解析任何{@code <bean/> }元素{@code profile}.
	 * @see #doRegisterBeanDefinitions
	 */
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	/**
	 * {@inheritDoc}
	 * <p>This implementation parses bean definitions according to the "spring-beans" XSD
	 * (or DTD, historically).
	 * <p>Opens a DOM Document; then initializes the default settings
	 * specified at the {@code <beans/>} level; then parses the contained bean definitions.
	 * ************************************************************************************
	 * ~$ 这个实现解析bean定义根据“spring bean”XSD(或DTD,历史上).
	 *    打开一个DOM文档,然后初始化指定的默认设置在{@code <bean/> },然后解析包含bean定义.
	 */
	public void registerBeanDefinitions(Document doc, XmlReaderContext readerContext) {
		this.readerContext = readerContext;

		logger.debug("Loading bean definitions");
		Element root = doc.getDocumentElement();

		doRegisterBeanDefinitions(root);
	}

	/**
	 * Register each bean definition within the given root {@code <beans/>} element.
	 * *****************************************************************************
	 * ~$ 注册每个bean定义在给定根{@code <bean/> }元素.
	 * @throws IllegalStateException if {@code <beans profile="..."} attribute is present
	 * and Environment property has not been set
	 * @see #setEnvironment
	 */
	protected void doRegisterBeanDefinitions(Element root) {
		String profileSpec = root.getAttribute(PROFILE_ATTRIBUTE);
		if (StringUtils.hasText(profileSpec)) {
			Assert.state(this.environment != null, "environment property must not be null");
			String[] specifiedProfiles = StringUtils.tokenizeToStringArray(profileSpec, BeanDefinitionParserDelegate.MULTI_VALUE_ATTRIBUTE_DELIMITERS);
			if (!this.environment.acceptsProfiles(specifiedProfiles)) {
				return;
			}
		}

		// any nested <beans> elements will cause recursion in this method. In
		/** 任何嵌套的< bean >元素将导致该方法递归.在 */
		// order to propagate and preserve <beans> default-* attributes correctly,
		/** 以传播和保存< bean >默认- *属性正确,*/
		// keep track of the current (parent) delegate, which may be null. Create
		/** 跟踪当前(parent)委托,这可能是null.创建*/
		// the new (child) delegate with a reference to the parent for fallback purposes,
		/** 新(child)代表一个参考到parent 回退的目的,*/
		// then ultimately reset this.delegate back to its original (parent) reference.
		/** 最终重置.委托回到原来的(parent)参考.*/
		// this behavior emulates a stack of delegates without actually necessitating one.
		/** 这种行为模拟一堆代表不需要.*/
		BeanDefinitionParserDelegate parent = this.delegate;
		this.delegate = createHelper(readerContext, root, parent);

		preProcessXml(root);
		parseBeanDefinitions(root, this.delegate);
		postProcessXml(root);

		this.delegate = parent;
	}

	protected BeanDefinitionParserDelegate createHelper(XmlReaderContext readerContext, Element root, BeanDefinitionParserDelegate parentDelegate) {
		BeanDefinitionParserDelegate delegate = new BeanDefinitionParserDelegate(readerContext, environment);
		delegate.initDefaults(root, parentDelegate);
		return delegate;
	}

	/**
	 * Return the descriptor for the XML resource that this parser works on.
	 * *********************************************************************
	 * ~$ 返回的XML资源的描述符,这个解析器工作.
	 */
	protected final XmlReaderContext getReaderContext() {
		return this.readerContext;
	}

	/**
	 * Invoke the {@link org.springframework.beans.factory.parsing.SourceExtractor} to pull the
	 * source metadata from the supplied {@link Element}.
	 * ****************************************************************************************
	 * ~$ 调用{@link org.springframework.beans.factory.parsing.SourceExtractor }将源元数据从提供的 {@link Element}.
	 */
	protected Object extractSource(Element ele) {
		return this.readerContext.extractSource(ele);
	}


	/**
	 * Parse the elements at the root level in the document:
	 * "import", "alias", "bean".
	 * *****************************************************
	 * ~$ 解析文档中的元素在根级别:"import", "alias", "bean".
	 * @param root the DOM root element of the document
	 */
	protected void parseBeanDefinitions(Element root, BeanDefinitionParserDelegate delegate) {
		if (delegate.isDefaultNamespace(root)) {
			NodeList nl = root.getChildNodes();
			for (int i = 0; i < nl.getLength(); i++) {
				Node node = nl.item(i);
				if (node instanceof Element) {
					Element ele = (Element) node;
					if (delegate.isDefaultNamespace(ele)) {
						parseDefaultElement(ele, delegate);
					}
					else {
						delegate.parseCustomElement(ele);
					}
				}
			}
		}
		else {
			delegate.parseCustomElement(root);
		}
	}

	private void parseDefaultElement(Element ele, BeanDefinitionParserDelegate delegate) {
		if (delegate.nodeNameEquals(ele, IMPORT_ELEMENT)) {
			importBeanDefinitionResource(ele);
		}
		else if (delegate.nodeNameEquals(ele, ALIAS_ELEMENT)) {
			processAliasRegistration(ele);
		}
		else if (delegate.nodeNameEquals(ele, BEAN_ELEMENT)) {
			processBeanDefinition(ele, delegate);
		}
		else if (delegate.nodeNameEquals(ele, NESTED_BEANS_ELEMENT)) {
			// recurse
			doRegisterBeanDefinitions(ele);
		}
	}

	/**
	 * Parse an "import" element and load the bean definitions
	 * from the given resource into the bean factory.
	 * *******************************************************
	 * ~$ 解析一个"import"元素和bean定义从给定的资源加载到bean工厂.
	 */
	protected void importBeanDefinitionResource(Element ele) {
		String location = ele.getAttribute(RESOURCE_ATTRIBUTE);
		if (!StringUtils.hasText(location)) {
			getReaderContext().error("Resource location must not be empty", ele);
			return;
		}

		// Resolve system properties: e.g. "${user.dir}"
		/** 解决系统属性:如. "${user.dir}" */
		location = environment.resolveRequiredPlaceholders(location);

		Set<Resource> actualResources = new LinkedHashSet<Resource>(4);

		// Discover whether the location is an absolute or relative URI
		/** 发现位置是否绝对或相对URI */
		boolean absoluteLocation = false;
		try {
			absoluteLocation = ResourcePatternUtils.isUrl(location) || ResourceUtils.toURI(location).isAbsolute();
		}
		catch (URISyntaxException ex) {
			// cannot convert to an URI, considering the location relative
			/** 不能转换为一个URI,考虑到相对的位置 */
			// unless it is the well-known Spring prefix "classpath*:"
			/** 除非是著名的Spring前缀"classpath *:" */
		}

		// Absolute or relative?
		/** 绝对的还是相对的? */
		if (absoluteLocation) {
			try {
				int importCount = getReaderContext().getReader().loadBeanDefinitions(location, actualResources);
				if (logger.isDebugEnabled()) {
					logger.debug("Imported " + importCount + " bean definitions from URL location [" + location + "]");
				}
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error(
						"Failed to import bean definitions from URL location [" + location + "]", ele, ex);
			}
		}
		else {
			// No URL -> considering resource location as relative to the current file.
			/** 没有URL->考虑资源位置相对于当前文件.*/
			try {
				int importCount;
				Resource relativeResource = getReaderContext().getResource().createRelative(location);
				if (relativeResource.exists()) {
					importCount = getReaderContext().getReader().loadBeanDefinitions(relativeResource);
					actualResources.add(relativeResource);
				}
				else {
					String baseLocation = getReaderContext().getResource().getURL().toString();
					importCount = getReaderContext().getReader().loadBeanDefinitions(
							StringUtils.applyRelativePath(baseLocation, location), actualResources);
				}
				if (logger.isDebugEnabled()) {
					logger.debug("Imported " + importCount + " bean definitions from relative location [" + location + "]");
				}
			}
			catch (IOException ex) {
				getReaderContext().error("Failed to resolve current resource location", ele, ex);
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to import bean definitions from relative location [" + location + "]",
						ele, ex);
			}
		}
		Resource[] actResArray = actualResources.toArray(new Resource[actualResources.size()]);
		getReaderContext().fireImportProcessed(location, actResArray, extractSource(ele));
	}

	/**
	 * Process the given alias element, registering the alias with the registry.
	 * *************************************************************************
	 * ~$ 注册过程元素给定的别名,别名与注册表.
	 */
	protected void processAliasRegistration(Element ele) {
		String name = ele.getAttribute(NAME_ATTRIBUTE);
		String alias = ele.getAttribute(ALIAS_ATTRIBUTE);
		boolean valid = true;
		if (!StringUtils.hasText(name)) {
			getReaderContext().error("Name must not be empty", ele);
			valid = false;
		}
		if (!StringUtils.hasText(alias)) {
			getReaderContext().error("Alias must not be empty", ele);
			valid = false;
		}
		if (valid) {
			try {
				getReaderContext().getRegistry().registerAlias(name, alias);
			}
			catch (Exception ex) {
				getReaderContext().error("Failed to register alias '" + alias +
						"' for bean with name '" + name + "'", ele, ex);
			}
			getReaderContext().fireAliasRegistered(name, alias, extractSource(ele));
		}
	}

	/**
	 * Process the given bean element, parsing the bean definition
	 * and registering it with the registry.
	 * ***********************************************************
	 * ~$ 过程给bean元素,解析bean定义并与注册中心注册.
	 */
	protected void processBeanDefinition(Element ele, BeanDefinitionParserDelegate delegate) {
		BeanDefinitionHolder bdHolder = delegate.parseBeanDefinitionElement(ele);
		if (bdHolder != null) {
			bdHolder = delegate.decorateBeanDefinitionIfRequired(ele, bdHolder);
			try {
				// Register the final decorated instance.
				/** 注册最后装饰实例.*/
				BeanDefinitionReaderUtils.registerBeanDefinition(bdHolder, getReaderContext().getRegistry());
			}
			catch (BeanDefinitionStoreException ex) {
				getReaderContext().error("Failed to register bean definition with name '" +
						bdHolder.getBeanName() + "'", ele, ex);
			}
			// Send registration event.
			/** 发送注册事件.*/
			getReaderContext().fireComponentRegistered(new BeanComponentDefinition(bdHolder));
		}
	}


	/**
	 * Allow the XML to be extensible by processing any custom element types first,
	 * before we start to process the bean definitions. This method is a natural
	 * extension point for any other custom pre-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 * ******************************************************************************
	 * ~$ 允许XML是可扩展的,通过处理任何自定义元素类型,在我们开始之前处理bean定义.
	 *    这种方法是一种自然的扩展点其他自定义XML的预处理.
	 * <p>默认实现是空的.子类可以重写这个方法来定制元素转换成标准的Spring bean定义,例如.
	 *    实现者可以访问解析器的bean定义读者和底层XML资源,通过相应的访问器.
	 * @see #getReaderContext()
	 */
	protected void preProcessXml(Element root) {
	}

	/**
	 * Allow the XML to be extensible by processing any custom element types last,
	 * after we finished processing the bean definitions. This method is a natural
	 * extension point for any other custom post-processing of the XML.
	 * <p>The default implementation is empty. Subclasses can override this method to
	 * convert custom elements into standard Spring bean definitions, for example.
	 * Implementors have access to the parser's bean definition reader and the
	 * underlying XML resource, through the corresponding accessors.
	 * *******************************************************************************
	 * ~$ 允许XML是可扩展的,通过处理任何自定义元素类型,我们处理完后bean定义.
	 *    这种方法是一种自然的扩展点其他自定义XML的后处理.
	 * <p>默认实现是空的.子类可以重写这个方法来定制元素转换成标准的Spring bean定义,例如.
	 *    实现者可以访问解析器的bean定义读者和底层XML资源,通过相应的访问器.
	 * @see #getReaderContext()
	 */
	protected void postProcessXml(Element root) {
	}

}
