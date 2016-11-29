/*
 * Copyright 2002-2008 the original author or authors.
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

package org.springframework.beans.factory.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.ResourceBundle;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessor;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.StringUtils;

/**
 * Bean definition reader for a simple properties format.
 *
 * <p>Provides bean definition registration methods for Map/Properties and
 * ResourceBundle. Typically applied to a DefaultListableBeanFactory.
 * ************************************************************************
 * ~$ Bean定义读者简单属性格式.
 * <p>提供bean定义Map 属性和ResourceBundle登记方法.通常应用于DefaultListableBeanFactory.
 * <p><b>Example:</b>
 *
 * <pre class="code">
 * employee.(class)=MyClass       // bean is of class MyClass
 * employee.(abstract)=true       // this bean can't be instantiated directly
 * employee.group=Insurance       // real property
 * employee.usesDialUp=false      // real property (potentially overridden)
 *
 * salesrep.(parent)=employee     // derives from "employee" bean definition
 * salesrep.(lazy-init)=true      // lazily initialize this singleton bean
 * salesrep.manager(ref)=tony     // reference to another bean
 * salesrep.department=Sales      // real property
 *
 * techie.(parent)=employee       // derives from "employee" bean definition
 * techie.(scope)=prototype       // bean is a prototype (not a shared instance)
 * techie.manager(ref)=jeff       // reference to another bean
 * techie.department=Engineering  // real property
 * techie.usesDialUp=true         // real property (overriding parent value)
 *
 * ceo.$0(ref)=secretary          // inject 'secretary' bean as 0th constructor arg
 * ceo.$1=1000000                 // inject value '1000000' at 1st constructor arg
 * </pre>
 * 
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 26.11.2003
 * @see DefaultListableBeanFactory
 */
public class PropertiesBeanDefinitionReader extends AbstractBeanDefinitionReader {

	/**
	 * Value of a T/F attribute that represents true.
	 * Anything else represents false. Case seNsItive.
	 * **********************************************
	 * ~$ T/F值属性,是真实的.
	 *    什么是错误的.大小写敏感的。
	 */
	public static final String TRUE_VALUE = "true";

	/**
	 * Separator between bean name and property name.
	 * We follow normal Java conventions.
	 * **********************************************
	 * ~$ 分离器之间的bean名称和属性名称.
	 *    我们遵循普通Java约定.
	 */
	public static final String SEPARATOR = ".";

	/**
	 * Special key to distinguish <code>owner.(class)=com.myapp.MyClass</code>-
	 * ***********************************************************************
	 * ~$ 特殊的key    区别
	 */
	public static final String CLASS_KEY = "(class)";

	/**
	 * Special key to distinguish <code>owner.(parent)=parentBeanName</code>.
	 */
	public static final String PARENT_KEY = "(parent)";

	/**
	 * Special key to distinguish <code>owner.(scope)=prototype</code>.
	 * Default is "true".
	 */
	public static final String SCOPE_KEY = "(scope)";

	/**
	 * Special key to distinguish <code>owner.(singleton)=false</code>.
	 * Default is "true".
	 */
	public static final String SINGLETON_KEY = "(singleton)";

	/**
	 * Special key to distinguish <code>owner.(abstract)=true</code>
	 * Default is "false".
	 */
	public static final String ABSTRACT_KEY = "(abstract)";

	/**
	 * Special key to distinguish <code>owner.(lazy-init)=true</code>
	 * Default is "false".
	 */
	public static final String LAZY_INIT_KEY = "(lazy-init)";

	/**
	 * Property suffix for references to other beans in the current
	 * BeanFactory: e.g. <code>owner.dog(ref)=fido</code>.
	 * Whether this is a reference to a singleton or a prototype
	 * will depend on the definition of the target bean.
	 * *************************************************************
	 * ~$ 属性后缀引用其他bean在当前BeanFactory:例如owner.dog(ref)=fido.
	 *   是否这是单例的引用或原型将取决于目标bean的定义.
	 */
	public static final String REF_SUFFIX = "(ref)";

	/**
	 * Prefix before values referencing other beans.
	 * *********************************************
	 * ~$ 前缀前值引用其他bean.
	 */
	public static final String REF_PREFIX = "*";

	/**
	 * Prefix used to denote a constructor argument definition.
	 * ********************************************************
	 * ~$ 前缀用来表示一个构造函数参数的定义.
	 */
	public static final String CONSTRUCTOR_ARG_PREFIX = "$";


	private String defaultParentBean;

	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();


	/**
	 * Create new PropertiesBeanDefinitionReader for the given bean factory.
	 * ********************************************************************
	 * ~$ 对于给定的bean创建新的PropertiesBeanDefinitionReader工厂.
	 * @param registry the BeanFactory to load bean definitions into,
	 * in the form of a BeanDefinitionRegistry
	 *                 ~$ BeanFactory加载bean定义,BeanDefinitionRegistry的形式
	 */
	public PropertiesBeanDefinitionReader(BeanDefinitionRegistry registry) {
		super(registry);
	}


	/**
	 * Set the default parent bean for this bean factory.
	 * If a child bean definition handled by this factory provides neither
	 * a parent nor a class attribute, this default value gets used.
	 * <p>Can be used e.g. for view definition files, to define a parent
	 * with a default view class and common attributes for all views.
	 * View definitions that define their own parent or carry their own
	 * class can still override this.
	 * <p>Strictly speaking, the rule that a default parent setting does
	 * not apply to a bean definition that carries a class is there for
	 * backwards compatiblity reasons. It still matches the typical use case.
	 * **********************************************************************
	 * ~$ 设置默认的父母为这个bean工厂bean.如果一个孩子bean定义由本厂提供无论是家长还是一个类属性,此默认值使用.
	 * <p>可以使用例如视图定义文件,定义一个父母和一个默认的视图类和公共属性视图.
	 *    视图定义,定义自己的父母或携带自己的类仍然可以覆盖这个.
	 * <p>严格地说,一个默认的规则父母设置不适用于一个bean的定义,一个类有原因向后相容性.它仍然匹配的典型用例.
	 */
	public void setDefaultParentBean(String defaultParentBean) {
		this.defaultParentBean = defaultParentBean;
	}

	/**
	 * Return the default parent bean for this bean factory.
	 * *****************************************************
	 * ~$返回默认的父bean为这个bean工厂.
	 */
	public String getDefaultParentBean() {
		return this.defaultParentBean;
	}

	/**
	 * Set the PropertiesPersister to use for parsing properties files.
	 * The default is DefaultPropertiesPersister.
	 * ****************************************************************
	 * ~$ 设置PropertiesPersister用于解析属性文件.缺省值是DefaultPropertiesPersister.
	 * @see DefaultPropertiesPersister
	 */
	public void setPropertiesPersister(PropertiesPersister propertiesPersister) {
		this.propertiesPersister =
				(propertiesPersister != null ? propertiesPersister : new DefaultPropertiesPersister());
	}

	/**
	 * Return the PropertiesPersister to use for parsing properties files.
	 * *******************************************************************
	 * ~$ 返回PropertiesPersister用于解析属性文件.
	 */
	public PropertiesPersister getPropertiesPersister() {
		return this.propertiesPersister;
	}


	/**
	 * Load bean definitions from the specified properties file,
	 * using all property keys (i.e. not filtering by prefix).
	 * *********************************************************
	 * ~$ 从指定的属性文件,加载bean定义使用所有property keys (即没有前缀的过滤).
	 * @param resource the resource descriptor for the properties file
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #loadBeanDefinitions(Resource, String)
	 */
	public int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource), null);
	}

	/**
	 * Load bean definitions from the specified properties file.
	 * *********************************************************
	 * ~$ 从指定的属性文件加载bean定义.
	 * @param resource the resource descriptor for the properties file
	 *                 ~$ 资源属性文件描述符
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or <code>null</code>)  ~$ 过滤器内的键映射:例如:'beans.'
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(Resource resource, String prefix) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(new EncodedResource(resource), prefix);
	}

	/**
	 * Load bean definitions from the specified properties file.
	 * @param encodedResource the resource descriptor for the properties file,
	 * allowing to specify an encoding to use for parsing the file
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(EncodedResource encodedResource) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(encodedResource, null);
	}

	/**
	 * Load bean definitions from the specified properties file.
	 * @param encodedResource the resource descriptor for the properties file,
	 * allowing to specify an encoding to use for parsing the file
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or <code>null</code>)
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int loadBeanDefinitions(EncodedResource encodedResource, String prefix)
			throws BeanDefinitionStoreException {

		Properties props = new Properties();
		try {
			InputStream is = encodedResource.getResource().getInputStream();
			try {
				if (encodedResource.getEncoding() != null) {
					getPropertiesPersister().load(props, new InputStreamReader(is, encodedResource.getEncoding()));
				}
				else {
					getPropertiesPersister().load(props, is);
				}
			}
			finally {
				is.close();
			}
			return registerBeanDefinitions(props, prefix, encodedResource.getResource().getDescription());
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException("Could not parse properties from " + encodedResource.getResource(), ex);
		}
	}

	/**
	 * Register bean definitions contained in a resource bundle,
	 * using all property keys (i.e. not filtering by prefix).
	 * ********************************************************
	 * ~$ 注册的bean定义中包含一个资源包,使用所有属性键(即没有前缀的过滤).
	 * @param rb the ResourceBundle to load from ~$ ResourceBundle加载
	 * @return the number of bean definitions found ~$ bean定义发现的数量
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors  ~$ 在加载或解析错误
	 * @see #registerBeanDefinitions(ResourceBundle, String)
	 */
	public int registerBeanDefinitions(ResourceBundle rb) throws BeanDefinitionStoreException {
		return registerBeanDefinitions(rb, null);
	}

	/**
	 * Register bean definitions contained in a ResourceBundle.
	 * <p>Similar syntax as for a Map. This method is useful to enable
	 * standard Java internationalization support.
	 * ***************************************************************
	 * ~$ 注册ResourceBundle中包含bean定义.
	 * <p>类似语法的映射.这个方法很有用,使标准的Java国际化支持.
	 * @param rb the ResourceBundle to load from
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or <code>null</code>)
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	public int registerBeanDefinitions(ResourceBundle rb, String prefix) throws BeanDefinitionStoreException {
		// Simply create a map and call overloaded method.
		/** 简单地创建一个map和调用重载方法.*/
		Map<String, Object> map = new HashMap<String, Object>();
		Enumeration keys = rb.getKeys();
		while (keys.hasMoreElements()) {
			String key = (String) keys.nextElement();
			map.put(key, rb.getObject(key));
		}
		return registerBeanDefinitions(map, prefix);
	}


	/**
	 * Register bean definitions contained in a Map,
	 * using all property keys (i.e. not filtering by prefix).
	 * *******************************************************
	 * ~$ 注册的bean定义中包含Map,使用所有property keys(即没有前缀的过滤).
	 * @param map Map: name -> property (String or Object). Property values
	 * will be strings if coming from a Properties file etc. Property names
	 * (keys) <b>must</b> be Strings. Class keys must be Strings.
	 *            ~$ Map:- >属性名(字符串或对象).属性值将字符串如果来自一个属性文件等.
	 *               属性名(键)必须是字符串.类键必须是字符串.
	 * @return the number of bean definitions found
	 * @throws BeansException in case of loading or parsing errors
	 * @see #registerBeanDefinitions(Map, String, String)
	 */
	public int registerBeanDefinitions(Map map) throws BeansException {
		return registerBeanDefinitions(map, null);
	}

	/**
	 * Register bean definitions contained in a Map.
	 * Ignore ineligible properties.
	 * *********************************************
	 * ~$注册的bean定义中包含Map.忽略不合格的properties.
	 * @param map Map name -> property (String or Object). Property values
	 * will be strings if coming from a Properties file etc. Property names
	 * (keys) <b>must</b> be Strings. Class keys must be Strings.
	 *            ~$ Map ->属性名(字符串或对象).属性值将字符串如果来自一个属性文件等.
	 *              属性名(键)必须是字符串.类键必须是字符串.
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or <code>null</code>)
	 * @return the number of bean definitions found
	 * @throws BeansException in case of loading or parsing errors
	 */
	public int registerBeanDefinitions(Map map, String prefix) throws BeansException {
		return registerBeanDefinitions(map, prefix, "Map " + map);
	}

	/**
	 * Register bean definitions contained in a Map.
	 * Ignore ineligible properties.
	 * *********************************************
	 * ~$注册的bean定义中包含Map.忽略不合格的properties.
	 * @param map Map name -> property (String or Object). Property values
	 * will be strings if coming from a Properties file etc. Property names
	 * (keys) <b>must</b> be strings. Class keys must be Strings.
	 * @param prefix a filter within the keys in the map: e.g. 'beans.'
	 * (can be empty or <code>null</code>)
	 * @param resourceDescription description of the resource that the
	 * Map came from (for logging purposes)
	 * @return the number of bean definitions found
	 * @throws BeansException in case of loading or parsing errors
	 * @see #registerBeanDefinitions(Map, String)
	 */
	public int registerBeanDefinitions(Map map, String prefix, String resourceDescription)
			throws BeansException {

		if (prefix == null) {
			prefix = "";
		}
		int beanCount = 0;

		for (Object key : map.keySet()) {
			if (!(key instanceof String)) {
				throw new IllegalArgumentException("Illegal key [" + key + "]: only Strings allowed");
			}
			String keyString = (String) key;
			if (keyString.startsWith(prefix)) {
				// Key is of form: prefix<name>.property
				String nameAndProperty = keyString.substring(prefix.length());
				// Find dot before property name, ignoring dots in property keys.
				/** 之前找到点属性名,忽略 property keys.*/
				int sepIdx = -1;
				int propKeyIdx = nameAndProperty.indexOf(PropertyAccessor.PROPERTY_KEY_PREFIX);
				if (propKeyIdx != -1) {
					sepIdx = nameAndProperty.lastIndexOf(SEPARATOR, propKeyIdx);
				}
				else {
					sepIdx = nameAndProperty.lastIndexOf(SEPARATOR);
				}
				if (sepIdx != -1) {
					String beanName = nameAndProperty.substring(0, sepIdx);
					if (logger.isDebugEnabled()) {
						logger.debug("Found bean name '" + beanName + "'");
					}
					if (!getRegistry().containsBeanDefinition(beanName)) {
						// If we haven't already registered it...
						/** 如果我们还没有注册...*/
						registerBeanDefinition(beanName, map, prefix + beanName, resourceDescription);
						++beanCount;
					}
				}
				else {
					// Ignore it: It wasn't a valid bean name and property,
					/** 忽略它:这不是一个有效的bean的名称和属性,*/
					// although it did start with the required prefix.
					/** 虽然在开始时所需的前缀.*/
					if (logger.isDebugEnabled()) {
						logger.debug("Invalid bean name and property [" + nameAndProperty + "]");
					}
				}
			}
		}

		return beanCount;
	}

	/**
	 * Get all property values, given a prefix (which will be stripped)
	 * and add the bean they define to the factory with the given name
	 * ****************************************************************
	 * ~$ 得到所有属性值,给定一个前缀(剥离)和bean定义添加到工厂的名字
	 * @param beanName name of the bean to define
	 * @param map Map containing string pairs
	 * @param prefix prefix of each entry, which will be stripped
	 * @param resourceDescription description of the resource that the
	 * Map came from (for logging purposes)
	 * @throws BeansException if the bean definition could not be parsed or registered
	 */
	protected void registerBeanDefinition(String beanName, Map<?, ?> map, String prefix, String resourceDescription)
			throws BeansException {

		String className = null;
		String parent = null;
		String scope = GenericBeanDefinition.SCOPE_SINGLETON;
		boolean isAbstract = false;
		boolean lazyInit = false;

		ConstructorArgumentValues cas = new ConstructorArgumentValues();
		MutablePropertyValues pvs = new MutablePropertyValues();

		for (Map.Entry entry : map.entrySet()) {
			String key = StringUtils.trimWhitespace((String) entry.getKey());
			if (key.startsWith(prefix + SEPARATOR)) {
				String property = key.substring(prefix.length() + SEPARATOR.length());
				if (CLASS_KEY.equals(property)) {
					className = StringUtils.trimWhitespace((String) entry.getValue());
				}
				else if (PARENT_KEY.equals(property)) {
					parent = StringUtils.trimWhitespace((String) entry.getValue());
				}
				else if (ABSTRACT_KEY.equals(property)) {
					String val = StringUtils.trimWhitespace((String) entry.getValue());
					isAbstract = TRUE_VALUE.equals(val);
				}
				else if (SCOPE_KEY.equals(property)) {
					// Spring 2.0 style
					scope = StringUtils.trimWhitespace((String) entry.getValue());
				}
				else if (SINGLETON_KEY.equals(property)) {
					// Spring 1.2 style
					String val = StringUtils.trimWhitespace((String) entry.getValue());
					scope = ((val == null || TRUE_VALUE.equals(val) ? GenericBeanDefinition.SCOPE_SINGLETON :
							GenericBeanDefinition.SCOPE_PROTOTYPE));
				}
				else if (LAZY_INIT_KEY.equals(property)) {
					String val = StringUtils.trimWhitespace((String) entry.getValue());
					lazyInit = TRUE_VALUE.equals(val);
				}
				else if (property.startsWith(CONSTRUCTOR_ARG_PREFIX)) {
					if (property.endsWith(REF_SUFFIX)) {
						int index = Integer.parseInt(property.substring(1, property.length() - REF_SUFFIX.length()));
						cas.addIndexedArgumentValue(index, new RuntimeBeanReference(entry.getValue().toString()));
					}
					else {
						int index = Integer.parseInt(property.substring(1));
						cas.addIndexedArgumentValue(index, readValue(entry));
					}
				}
				else if (property.endsWith(REF_SUFFIX)) {
					// This isn't a real property, but a reference to another prototype
					/** 这不是一个真正的property,但对另一个原型的引用*/
					// Extract property name: property is of form dog(ref)
					/** 提取属性名:property形式的dog(ref) */
					property = property.substring(0, property.length() - REF_SUFFIX.length());
					String ref = StringUtils.trimWhitespace((String) entry.getValue());

					// It doesn't matter if the referenced bean hasn't yet been registered:
					/** 没关系如果引用bean尚未注册:*/
					// this will ensure that the reference is resolved at runtime.
					/** 这将确保在运行时解析的引用.*/
					Object val = new RuntimeBeanReference(ref);
					pvs.add(property, val);
				}
				else {
					// It's a normal bean property.
					/** 这是一个普通的bean属性.*/
					pvs.add(property, readValue(entry));
				}
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("Registering bean definition for bean name '" + beanName + "' with " + pvs);
		}

		// Just use default parent if we're not dealing with the parent itself,
		/** 只使用默认的parent如果我们不应对parent本身,*/
		// and if there's no class name specified. The latter has to happen for
		/** 如果没有指定的类名.后者已经发生*/
		// backwards compatibility reasons.
		/** 向后兼容的原因.*/
		if (parent == null && className == null && !beanName.equals(this.defaultParentBean)) {
			parent = this.defaultParentBean;
		}

		try {
			AbstractBeanDefinition bd = BeanDefinitionReaderUtils.createBeanDefinition(
					parent, className, getBeanClassLoader());
			bd.setScope(scope);
			bd.setAbstract(isAbstract);
			bd.setLazyInit(lazyInit);
			bd.setConstructorArgumentValues(cas);
			bd.setPropertyValues(pvs);
			getRegistry().registerBeanDefinition(beanName, bd);
		}
		catch (ClassNotFoundException ex) {
			throw new CannotLoadBeanClassException(resourceDescription, beanName, className, ex);
		}
		catch (LinkageError err) {
			throw new CannotLoadBeanClassException(resourceDescription, beanName, className, err);
		}
	}

	/**
	 * Reads the value of the entry. Correctly interprets bean references for
	 * values that are prefixed with an asterisk.
	 * **********************************************************************
	 * ~$ 读取的值条目.正确解读bean引用值以星号.
	 */
	private Object readValue(Map.Entry entry) {
		Object val = entry.getValue();
		if (val instanceof String) {
			String strVal = (String) val;
			// If it starts with a reference prefix...
			/** 如果它开始于一个参考前缀...*/
			if (strVal.startsWith(REF_PREFIX)) {
				// Expand the reference.
				/** 扩大参考.*/
				String targetName = strVal.substring(1);
				if (targetName.startsWith(REF_PREFIX)) {
					// Escaped prefix -> use plain value.
					/** 逃脱前缀 -> 使用普通的值.*/
					val = targetName;
				}
				else {
					val = new RuntimeBeanReference(targetName);
				}
			}
		}
		return val;
	}

}
