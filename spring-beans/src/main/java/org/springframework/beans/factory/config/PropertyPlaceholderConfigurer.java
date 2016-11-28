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

import java.util.Properties;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.core.Constants;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringValueResolver;

/**
 * {@link PlaceholderConfigurerSupport} subclass that resolves ${...} placeholders
 * against {@link #setLocation local} {@link #setProperties properties} and/or system properties
 * and environment variables.
 *
 * <p>As of Spring 3.1, {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 * PropertySourcesPlaceholderConfigurer} should be used preferentially over this implementation; it is
 * more flexible through taking advantage of the {@link org.springframework.core.env.Environment Environment} and
 * {@link org.springframework.core.env.PropertySource PropertySource} mechanisms also made available in Spring 3.1.
 *
 * <p>{@link PropertyPlaceholderConfigurer} is still appropriate for use when:
 * <ul>
 * <li>the {@link org.springframework.context spring-context} module is not available (i.e., one is using
 * Spring's {@code BeanFactory} API as opposed to {@code ApplicationContext}).
 * <li>existing configuration makes use of the {@link #setSystemPropertiesMode(int) "systemPropertiesMode"} and/or
 * {@link #setSystemPropertiesModeName(String) "systemPropertiesModeName"} properties. Users are encouraged to move
 * away from using these settings, and rather configure property source search order through the container's
 * {@code Environment}; however, exact preservation of functionality may be maintained by continuing to
 * use {@code PropertyPlaceholderConfigurer}.
 * </ul>
 *
 * <p>Prior to Spring 3.1, the {@code <context:property-placeholder/>} namespace element
 * registered an instance of {@code PropertyPlaceholderConfigurer}. It will still do so if
 * using the {@code spring-context-3.0.xsd} definition of the namespace. That is, you can preserve
 * registration of {@code PropertyPlaceholderConfigurer} through the namespace, even if using Spring 3.1;
 * simply do not update your {@code xsi:schemaLocation} and continue using the 3.0 XSD.
 *
 * ********************************************************************************************************************
 * ~$ {@link PlaceholderConfigurerSupport }子类解决$ {…}占位符反对{@link #setLocation}
 *    {@link #setProperties properties}和/或系统属性和环境变量.
 *
 * <p>Spring 3.1,{@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer PropertySourcesPlaceholderConfigurer }
 *    应该优先使用此实现;通过利用更加灵活的{@link org.springframework.core.env.Environment }和
 *    {@link org.springframework.core.env.PropertySource PropertySource }机制也在Spring 3.1中可用.
 *
 * <p>{ @link PropertyPlaceholderConfigurer }仍然是适合使用时:
 * {@link org.springframework.context spring-context } 模块不可用(即.一个是使用Spring的{@code BeanFactory } API而不是{@code ApplicationContext }).
 * 现有的配置利用{@link #setSystemPropertiesMode(int)“systemPropertiesMode”}和/或{@link #setSystemPropertiesModeName(String) "systemPropertiesModeName"}属性.
 * 鼓励用户使用这些设置离开,而配置属性源搜索顺序通过容器{@code Environment};然而,确切的保护功能可能会被继续保持用{@code PropertyPlaceholderConfigurer }.
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 02.10.2003
 * @see #setSystemPropertiesModeName
 * @see PlaceholderConfigurerSupport
 * @see PropertyOverrideConfigurer
 * @see org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 */
public class PropertyPlaceholderConfigurer extends PlaceholderConfigurerSupport {

	/** Never check system properties. */
	/** 从来没有检查系统属性.*/
	public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;

	/**
	 * Check system properties if not resolvable in the specified properties.
	 * This is the default.
	 * **********************************************************************
	 * ~$ 检查系统属性如果无法在指定的属性.这是默认的.
	 */
	public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;

	/**
	 * Check system properties first, before trying the specified properties.
	 * This allows system properties to override any other property source.
	 * *********************************************************************
	 * ~$ 首先检查系统属性,之前指定的属性.这允许系统属性覆盖任何其他属性来源.
	 */
	public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;


	private static final Constants constants = new Constants(PropertyPlaceholderConfigurer.class);

	private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

	private boolean searchSystemEnvironment = true;


	/**
	 * Set the system property mode by the name of the corresponding constant,
	 * e.g. "SYSTEM_PROPERTIES_MODE_OVERRIDE".
	 * ***********************************************************************
	 * ~$ 设置系统属性模式的相应的常数,比如 "SYSTEM_PROPERTIES_MODE_OVERRIDE".
	 * @param constantName name of the constant
	 * @throws IllegalArgumentException if an invalid constant was specified
	 * @see #setSystemPropertiesMode
	 */
	public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
		this.systemPropertiesMode = constants.asNumber(constantName).intValue();
	}

	/**
	 * Set how to check system properties: as fallback, as override, or never.
	 * For example, will resolve ${user.dir} to the "user.dir" system property.
	 * <p>The default is "fallback": If not being able to resolve a placeholder
	 * with the specified properties, a system property will be tried.
	 * "override" will check for a system property first, before trying the
	 * specified properties. "never" will not check system properties at all.
	 * ************************************************************************
	 * ~$ 如何检查设置系统属性:作为后备,覆盖,或没有.
	 * 例如,将解决${user.dir}."user.dir"系统属性.
	 * @see #SYSTEM_PROPERTIES_MODE_NEVER
	 * @see #SYSTEM_PROPERTIES_MODE_FALLBACK
	 * @see #SYSTEM_PROPERTIES_MODE_OVERRIDE
	 * @see #setSystemPropertiesModeName
	 */
	public void setSystemPropertiesMode(int systemPropertiesMode) {
		this.systemPropertiesMode = systemPropertiesMode;
	}

	/**
	 * Set whether to search for a matching system environment variable
	 * if no matching system property has been found. Only applied when
	 * "systemPropertyMode" is active (i.e. "fallback" or "override"), right
	 * after checking JVM system properties.
	 * <p>Default is "true". Switch this setting off to never resolve placeholders
	 * against system environment variables. Note that it is generally recommended
	 * to pass external values in as JVM system properties: This can easily be
	 * achieved in a startup script, even for existing environment variables.
	 * <p><b>NOTE:</b> Access to environment variables does not work on the
	 * Sun VM 1.4, where the corresponding {@link System#getenv} support was
	 * disabled - before it eventually got re-enabled for the Sun VM 1.5.
	 * Please upgrade to 1.5 (or higher) if you intend to rely on the
	 * environment variable support.
	 * ***************************************************************************
	 * ~$ 设置是否寻找一个匹配的系统环境变量如果没有匹配的系统属性被发现.
	 *    只适用于"systemPropertyMode"(i.e. "fallback" or "override"),后检查JVM系统属性.
	 * <p>默认为"true".关掉这个设置不会解决占位符对系统环境变量.
	 *    请注意,一般建议通过外部值在JVM系统属性:这可以很容易地实现一个启动脚本,甚至对现有环境变量.
	 * <p>注:访问环境变量不工作在Sun VM 1.4,相应的{@link System#getenv} 支持有缺陷的——终于重新启用之前1.5 Sun VM.
	 *    请升级到1.5(或更高版本)如果您打算依赖环境变量的支持.
	 * @see #setSystemPropertiesMode
	 * @see System#getProperty(String)
	 * @see System#getenv(String)
	 */
	public void setSearchSystemEnvironment(boolean searchSystemEnvironment) {
		this.searchSystemEnvironment = searchSystemEnvironment;
	}

	/**
	 * Resolve the given placeholder using the given properties, performing
	 * a system properties check according to the given mode.
	 * <p>The default implementation delegates to <code>resolvePlaceholder
	 * (placeholder, props)</code> before/after the system properties check.
	 * <p>Subclasses can override this for custom resolution strategies,
	 * including customized points for the system properties check.
	 * *********************************************************************
	 * ~$ 使用给定的属性解决给定的占位符,执行一个系统属性检查根据给定的模式.
	 * <p>默认实现代表resolvePlaceholder(占位符、道具)系统属性检查之前/之后.
	 * <p>子类可以重写这个定制的解决策略,包括系统属性定制点检查.
	 * @param placeholder the placeholder to resolve
	 * @param props the merged properties of this configurer
	 * @param systemPropertiesMode the system properties mode,
	 * according to the constants in this class
	 * @return the resolved value, of null if none
	 * @see #setSystemPropertiesMode
	 * @see System#getProperty
	 * @see #resolvePlaceholder(String, Properties)
	 */
	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		String propVal = null;
		if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_OVERRIDE) {
			propVal = resolveSystemProperty(placeholder);
		}
		if (propVal == null) {
			propVal = resolvePlaceholder(placeholder, props);
		}
		if (propVal == null && systemPropertiesMode == SYSTEM_PROPERTIES_MODE_FALLBACK) {
			propVal = resolveSystemProperty(placeholder);
		}
		return propVal;
	}

	/**
	 * Resolve the given placeholder using the given properties.
	 * The default implementation simply checks for a corresponding property key.
	 * <p>Subclasses can override this for customized placeholder-to-key mappings
	 * or custom resolution strategies, possibly just using the given properties
	 * as fallback.
	 * <p>Note that system properties will still be checked before respectively
	 * after this method is invoked, according to the system properties mode.
	 * **************************************************************************
	 * ~$ 解决给定的占位符使用给定的属性.默认实现只检查一个相应的属性键.
	 * <p>子类可以重写这个定制placeholder-to-key映射或自定义解决策略,可能只是使用给定的属性作为候选.
	 * <p>注意,系统属性仍将之前检查分别调用该方法后,根据系统属性模式.
	 * @param placeholder the placeholder to resolve
	 * @param props the merged properties of this configurer
	 * @return the resolved value, of <code>null</code> if none
	 * @see #setSystemPropertiesMode
	 */
	protected String resolvePlaceholder(String placeholder, Properties props) {
		return props.getProperty(placeholder);
	}

	/**
	 * Resolve the given key as JVM system property, and optionally also as
	 * system environment variable if no matching system property has been found.
	 * **************************************************************************
	 * ~$ 解决给定的关键是JVM系统属性,选择也是系统环境变量如果没有找到匹配的系统属性.
	 * @param key the placeholder to resolve as system property key
	 * @return the system property value, or <code>null</code> if not found
	 * @see #setSearchSystemEnvironment
	 * @see System#getProperty(String)
	 * @see System#getenv(String)
	 */
	protected String resolveSystemProperty(String key) {
		try {
			String value = System.getProperty(key);
			if (value == null && this.searchSystemEnvironment) {
				value = System.getenv(key);
			}
			return value;
		}
		catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Could not access system property '" + key + "': " + ex);
			}
			return null;
		}
	}


	/**
	 * Visit each bean definition in the given bean factory and attempt to replace ${...} property
	 * placeholders with values from the given properties.
	 * ********************************************************************************************
	 * ~$ 在给定的访问每个bean定义bean工厂和试图取代$ {…}属性占位符从给定的属性值.
	 */
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props)
			throws BeansException {

		StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);

		this.doProcessProperties(beanFactoryToProcess, valueResolver);
	}

	/**
	 * Parse the given String value for placeholder resolution.
	 * ********************************************************
	 * ~$ 解析给定的字符串值占位符的决议.
	 * @param strVal the String value to parse   ~$ 解析的字符串值
	 * @param props the Properties to resolve placeholders against ~$ 属性来解决占位符
	 * @param visitedPlaceholders the placeholders that have already been visited
	 * during the current resolution attempt (ignored in this version of the code)
	 *                            ~$ 已经访问过的占位符 在当前 试图解决
	 * @deprecated as of Spring 3.0, in favor of using {@link #resolvePlaceholder}
	 * with {@link PropertyPlaceholderHelper}.
	 * Only retained for compatibility with Spring 2.5 extensions.
	 *  ~$ 在spring3.0 中 使用  {@link #resolvePlaceholder} 在 {@link PropertyPlaceholderHelper}.
	 */
	@Deprecated
	protected String parseStringValue(String strVal, Properties props, Set<?> visitedPlaceholders) {
		PropertyPlaceholderHelper helper = new PropertyPlaceholderHelper(
				placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
		PlaceholderResolver resolver = new PropertyPlaceholderConfigurerResolver(props);
		return helper.replacePlaceholders(strVal, resolver);
	}


	private class PlaceholderResolvingStringValueResolver implements StringValueResolver {

		private final PropertyPlaceholderHelper helper;

		private final PlaceholderResolver resolver;

		public PlaceholderResolvingStringValueResolver(Properties props) {
			this.helper = new PropertyPlaceholderHelper(
					placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
			this.resolver = new PropertyPlaceholderConfigurerResolver(props);
		}

		public String resolveStringValue(String strVal) throws BeansException {
			String value = this.helper.replacePlaceholders(strVal, this.resolver);
			return (value.equals(nullValue) ? null : value);
		}
	}


	private class PropertyPlaceholderConfigurerResolver implements PlaceholderResolver {

		private final Properties props;

		private PropertyPlaceholderConfigurerResolver(Properties props) {
			this.props = props;
		}

		public String resolvePlaceholder(String placeholderName) {
			return PropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName, props, systemPropertiesMode);
		}
	}

}
