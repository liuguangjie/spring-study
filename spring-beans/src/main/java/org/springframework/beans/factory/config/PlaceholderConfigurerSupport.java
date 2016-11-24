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

package org.springframework.beans.factory.config;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.util.StringValueResolver;

/**
 * Abstract base class for property resource configurers that resolve placeholders
 * in bean definition property values. Implementations <em>pull</em> values from a
 * properties file or other {@linkplain org.springframework.core.env.PropertySource
 * property source} into bean definitions.
 *
 * <p>The default placeholder syntax follows the Ant / Log4J / JSP EL style:
 * ********************************************************************************
 * ~$ 抽象基类财产资源configurers解决占位符在bean定义属性值.
 *   实现把值从一个属性文件或其他{@linkplain org.springframework.core.env.PropertySource property source}到bean定义.
 *
 * <p>默认占位符语法跟Ant /Log4J / JSP EL风格:
 *<pre class="code">${...}</pre>
 *
 * Example XML bean definition:
 *
 *<pre class="code">{@code
 *<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource"/>
 *    <property name="driverClassName" value="}${driver}{@code"/>
 *    <property name="url" value="jdbc:}${dbname}{@code"/>
 *</bean>
 *}</pre>
 *
 * Example properties file:
 *
 * <pre class="code"> driver=com.mysql.jdbc.Driver
 * dbname=mysql:mydb</pre>
 *
 * Annotated bean definitions may take advantage of property replacement using
 * the {@link org.springframework.beans.factory.annotation.Value @Value} annotation:
 * *********************************************************************************
 * ~$ 带注解的bean定义可能利用属性替换使用{@link org.springframework.beans.factory.annotation.Value @Value }注解:
 *<pre class="code">@Value("${person.age}")</pre>
 *
 * Implementations check simple property values, lists, maps, props, and bean names
 * in bean references. Furthermore, placeholder values can also cross-reference
 * other placeholders, like:
 *
 *<pre class="code">rootPath=myrootdir
 *subPath=${rootPath}/subdir</pre>
 *
 * -- In contrast to {@link PropertyOverrideConfigurer}, subclasses of this type allow
 * filling in of explicit placeholders in bean definitions.
 *
 * <p>If a configurer cannot resolve a placeholder, a {@link BeanDefinitionStoreException}
 * will be thrown. If you want to check against multiple properties files, specify multiple
 * resources via the {@link #setLocations locations} property. You can also define multiple
 * configurers, each with its <em>own</em> placeholder syntax. Use {@link
 * #ignoreUnresolvablePlaceholders} to intentionally suppress throwing an exception if a
 * placeholder cannot be resolved.
 *
 * <p>Default property values can be defined globally for each configurer instance
 * via the {@link #setProperties properties} property, or on a property-by-property basis
 * using the default value separator which is {@code ":"} by default and
 * customizable via {@link #setValueSeparator(String)}.
 *
 * <p>Example XML property with default value:
 * *****************************************************************************************
 * -- ~$ 与{@link PropertyOverrideConfigurer },这种类型的子类允许填写明确的占位符的bean定义.
 *
 * <p>如果一个配置无法解决一个占位符,{@link BeanDefinitionStoreException }将抛出.
 * 如果你想检查多个属性文件,指定多个资源通过{@link #setLocations locations}属性.
 * 您还可以定义多个configurers,每个有自己的占位符的语法.
 * 使用{@link #ignoreUnresolvablePlaceholders }故意压制抛出异常,如果不能解决一个占位符.
 *
 * <p>每个配置的默认属性值可以定义全局实例通过{@link #setProperties properties}属性,
 *    或基于property-by-property使用默认值分隔符{@code ":"}默认和可定制的通过{@link #setValueSeparator(String)}.
 *
 * <p>示例XML属性默认值:
 *<pre class="code">{@code
 *  <property name="url" value="jdbc:}${dbname:defaultdb}{@code"/>
 *}</pre>
 *
 * @author Chris Beams
 * @author Juergen Hoeller
 * @since 3.1
 * @see PropertyPlaceholderConfigurer
 * @see org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 */
public abstract class PlaceholderConfigurerSupport extends PropertyResourceConfigurer
		implements BeanNameAware, BeanFactoryAware {

	/** Default placeholder prefix: {@value} */
	/** 默认占位符前缀: {@value} */
	public static final String DEFAULT_PLACEHOLDER_PREFIX = "${";

	/** Default placeholder suffix: {@value} */
	/** 默认占位符前缀后缀: {@value}*/
	public static final String DEFAULT_PLACEHOLDER_SUFFIX = "}";

	/** Default value separator: {@value} */
	/** 默认值分隔符: {@value}*/
	public static final String DEFAULT_VALUE_SEPARATOR = ":";


	/** Defaults to {@value #DEFAULT_PLACEHOLDER_PREFIX} */
	protected String placeholderPrefix = DEFAULT_PLACEHOLDER_PREFIX;

	/** Defaults to {@value #DEFAULT_PLACEHOLDER_SUFFIX} */
	protected String placeholderSuffix = DEFAULT_PLACEHOLDER_SUFFIX;

	/** Defaults to {@value #DEFAULT_VALUE_SEPARATOR} */
	protected String valueSeparator = DEFAULT_VALUE_SEPARATOR;

	protected boolean ignoreUnresolvablePlaceholders = false;

	protected String nullValue;

	private BeanFactory beanFactory;

	private String beanName;


	/**
	 * Set the prefix that a placeholder string starts with.
	 * The default is {@value #DEFAULT_PLACEHOLDER_PREFIX}.
	 * *****************************************************
	 * ~$ 设置一个占位符字符串的前缀开始.
	 */
	public void setPlaceholderPrefix(String placeholderPrefix) {
		this.placeholderPrefix = placeholderPrefix;
	}

	/**
	 * Set the suffix that a placeholder string ends with.
	 * The default is {@value #DEFAULT_PLACEHOLDER_SUFFIX}.
	 * ***************************************************
	 * ~$ 设置一个占位符字符串的后缀结尾.
	 */
	public void setPlaceholderSuffix(String placeholderSuffix) {
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * Specify the separating character between the placeholder variable
	 * and the associated default value, or {@code null} if no such
	 * special character should be processed as a value separator.
	 * The default is {@value #DEFAULT_VALUE_SEPARATOR}.
	 * *****************************************************************
	 * ~$ 指定占位符变量之间的分隔符和相关的默认值,或{@code null}如果没有这些特殊字符应该作为值分隔符处理.
	 */
	public void setValueSeparator(String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	/**
	 * Set a value that should be treated as {@code null} when
	 * resolved as a placeholder value: e.g. "" (empty String) or "null".
	 * <p>Note that this will only apply to full property values,
	 * not to parts of concatenated values.
	 * <p>By default, no such null value is defined. This means that
	 * there is no way to express {@code null} as a property
	 * value unless you explicitly map a corresponding value here.
	 * ******************************************************************
	 * ~$  设置一个值时,应视为{@code null} 解析为一个占位符值:如"" (empty String)或"null".
	 * 请注意,这只适用于完整的属性值,不连接的部分的值.
	 * 默认情况下,没有这样的null值的定义.这就意味着没有办法表达{@code null}作为一个属性值,除非您显式映射相应的值.
	 */
	public void setNullValue(String nullValue) {
		this.nullValue = nullValue;
	}

	/**
	 * Set whether to ignore unresolvable placeholders.
	 * <p>Default is "false": An exception will be thrown if a placeholder fails
	 * to resolve. Switch this flag to "true" in order to preserve the placeholder
	 * String as-is in such a case, leaving it up to other placeholder configurers
	 * to resolve it.
	 * ***************************************************************************
	 * ~$ 设置是否忽略不肯舍弃占位符.
	 * <p>默认是"false":会抛出一个异常,如果一个占位符未能解决.这个标志切换到"true"为了保持占位符字符串初始在这种情况下,
	 *    让其他占位符configurers解决它.
	 */
	public void setIgnoreUnresolvablePlaceholders(boolean ignoreUnresolvablePlaceholders) {
		this.ignoreUnresolvablePlaceholders = ignoreUnresolvablePlaceholders;
	}

	/**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * ***************************************************************************
	 * ~$ 只需要检查我们没有解析自己的bean定义,避免失败不肯舍弃占位符在属性文件的位置.
	 *    后一种情况可能发生在资源系统属性占位符的位置.
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/**
	 * Only necessary to check that we're not parsing our own bean definition,
	 * to avoid failing on unresolvable placeholders in properties file locations.
	 * The latter case can happen with placeholders for system properties in
	 * resource locations.
	 * ***************************************************************************
	 * ~$ 只需要检查我们没有解析自己的bean定义,避免失败不肯舍弃占位符在属性文件的位置.
	 *    后一种情况可能发生在资源系统属性占位符的位置.
	 * @see #setLocations
	 * @see org.springframework.core.io.ResourceEditor
	 */
	public void setBeanFactory(BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}


	protected void doProcessProperties(ConfigurableListableBeanFactory beanFactoryToProcess,
			StringValueResolver valueResolver) {

		BeanDefinitionVisitor visitor = new BeanDefinitionVisitor(valueResolver);

		String[] beanNames = beanFactoryToProcess.getBeanDefinitionNames();
		for (String curName : beanNames) {
			// Check that we're not parsing our own bean definition,
			/** 检查我们没有解析自己的bean定义,*/
			// to avoid failing on unresolvable placeholders in properties file locations.
			/** 为了避免失败不肯舍弃占位符在属性文件的位置.*/
			if (!(curName.equals(this.beanName) && beanFactoryToProcess.equals(this.beanFactory))) {
				BeanDefinition bd = beanFactoryToProcess.getBeanDefinition(curName);
				try {
					visitor.visitBeanDefinition(bd);
				}
				catch (Exception ex) {
					throw new BeanDefinitionStoreException(bd.getResourceDescription(), curName, ex.getMessage());
				}
			}
		}

		// New in Spring 2.5: resolve placeholders in alias target names and aliases as well.
		/** 新在Spring 2.5中:解决占位符别名目标名和别名.*/
		beanFactoryToProcess.resolveAliases(valueResolver);

		// New in Spring 3.0: resolve placeholders in embedded values such as annotation attributes.
		/** 新在Spring 3.0中:解决嵌入式值如注释属性中的占位符.*/
		beanFactoryToProcess.addEmbeddedValueResolver(valueResolver);
	}

}
