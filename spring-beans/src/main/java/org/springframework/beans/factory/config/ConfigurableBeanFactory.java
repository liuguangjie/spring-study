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

package org.springframework.beans.factory.config;

import java.beans.PropertyEditor;
import java.security.AccessControlContext;

import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;

/**
 * Configuration interface to be implemented by most bean factories. Provides
 * facilities to configure a bean factory, in addition to the bean factory
 * client methods in the {@link BeanFactory}
 * interface.
 *
 * <p>This bean factory interface is not meant to be used in normal application
 * code: Stick to {@link BeanFactory} or
 * {@link org.springframework.beans.factory.ListableBeanFactory} for typical
 * needs. This extended interface is just meant to allow for framework-internal
 * plug'n'play and for special access to bean factory configuration methods.
 *
 * *****************************************************************************
 * ~$ 大多数bean配置接口实现的工厂.
 *    提供设施配置bean工厂,除了bean工厂端方法{@link BeanFactory }接口.
 *
 * <p>这个bean工厂接口并不意味着在正常的应用程序代码中使用:
 *    坚持{ @link BeanFactory }或{@link org.springframework.beans.factory.ListableBeanFactory }为典型的需求.
 *    这个扩展接口是为了允许framework-internal 娱乐和特殊访问bean工厂配置方法.
 *
 * @author Juergen Hoeller
 * @since 03.11.2003
 * @see BeanFactory
 * @see org.springframework.beans.factory.ListableBeanFactory
 * @see ConfigurableListableBeanFactory
 */
public interface ConfigurableBeanFactory extends HierarchicalBeanFactory, SingletonBeanRegistry {

	/**
	 * Scope identifier for the standard singleton scope: "singleton".
	 * Custom scopes can be added via <code>registerScope</code>.
	 * ***************************************************************
	 * ~$ 范围标识符标准单范围:"singleton".
	 *    通过registerScope可以添加自定义范围.
	 * @see #registerScope
	 */
	String SCOPE_SINGLETON = "singleton";

	/**
	 * Scope identifier for the standard prototype scope: "prototype".
	 * Custom scopes can be added via <code>registerScope</code>.
	 * @see #registerScope
	 */
	String SCOPE_PROTOTYPE = "prototype";


	/**
	 * Set the parent of this bean factory.
	 * <p>Note that the parent cannot be changed: It should only be set outside
	 * a constructor if it isn't available at the time of factory instantiation.
	 * ************************************************************************
	 * ~$设置这个bean工厂的根.
	 * <p>注意,根不能改变:外面只能设置一个构造函数如果不是可用的时候工厂实例化.
	 * @param parentBeanFactory the parent BeanFactory
	 * @throws IllegalStateException if this factory is already associated with
	 * a parent BeanFactory
	 * @see #getParentBeanFactory()
	 */
	void setParentBeanFactory(BeanFactory parentBeanFactory) throws IllegalStateException;

	/**
	 * Set the class loader to use for loading bean classes.
	 * Default is the thread context class loader.
	 * <p>Note that this class loader will only apply to bean definitions
	 * that do not carry a resolved bean class yet. This is the case as of
	 * Spring 2.0 by default: Bean definitions only carry bean class names,
	 * to be resolved once the factory processes the bean definition.
	 * ********************************************************************
	 * ~$ 设置使用的类加载器加载bean类.默认是线程上下文类加载器.
	 * <p>注意,这个类装入器只适用于bean定义,不带解决bean类.
	 *    默认是这样的Spring 2.0:Bean定义只带Bean类的名字,一旦解决工厂处理Bean定义.
	 *
	 * @param beanClassLoader the class loader to use,
	 * or <code>null</code> to suggest the default class loader
	 */
	void setBeanClassLoader(ClassLoader beanClassLoader);

	/**
	 * Return this factory's class loader for loading bean classes.
	 * ************************************************************
	 * ~$ 返回这个工厂的类加载器加载bean类.
	 */
	ClassLoader getBeanClassLoader();

	/**
	 * Specify a temporary ClassLoader to use for type matching purposes.
	 * Default is none, simply using the standard bean ClassLoader.
	 * <p>A temporary ClassLoader is usually just specified if
	 * <i>load-time weaving</i> is involved, to make sure that actual bean
	 * classes are loaded as lazily as possible. The temporary loader is
	 * then removed once the BeanFactory completes its bootstrap phase.
	 * ********************************************************************
	 * ~$ 指定一个临时类加载器使用类型匹配的目的.默认是没有,只需使用标准的bean类加载器.
	 *    一个临时的类加载器通常只是如果指定
	 *    装入时编织,以确保实际加载bean类尽可能延迟.然后删除临时加载程序一旦BeanFactory完成了引导阶段.
	 * @since 2.5
	 */
	void setTempClassLoader(ClassLoader tempClassLoader);

	/**
	 * Return the temporary ClassLoader to use for type matching purposes,
	 * if any.
	 * *******************************************************************
	 * ~$ 返回临时类加载器使用类型匹配的目的,如果任何.
	 * @since 2.5
	 */
	ClassLoader getTempClassLoader();

	/**
	 * Set whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes. Default is on.
	 * <p>Turn this flag off to enable hot-refreshing of bean definition objects
	 * and in particular bean classes. If this flag is off, any creation of a bean
	 * instance will re-query the bean class loader for newly resolved classes.
	 * ***************************************************************************
	 * ~$ 设置是否缓存豆元数据,比如给bean定义(在合并的方式)和解决bean类。默认的是.
	 *	  关掉这个标志,使hot-refreshing bean定义的对象,特别是bean类.
	 *    如果这个标志,任何创建bean实例将重新查询bean类装入器新类来解决。
	 */
	void setCacheBeanMetadata(boolean cacheBeanMetadata);

	/**
	 * Return whether to cache bean metadata such as given bean definitions
	 * (in merged fashion) and resolved bean classes.
	 * ********************************************************************
	 * ~$ 返回是否缓存 bean 元数据等给bean定义(在合并的方式)和解决bean类.
	 */
	boolean isCacheBeanMetadata();

	/**
	 * Specify the resolution strategy for expressions in bean definition values.
	 * <p>There is no expression support active in a BeanFactory by default.
	 * An ApplicationContext will typically set a standard expression strategy
	 * here, supporting "#{...}" expressions in a Unified EL compatible style.
	 * **************************************************************************
	 * ~$ 指定表达式在bean定义值的解决策略.
	 * <p>没有默认BeanFactory表达式支持活动.
	 *   ApplicationContext通常会设定一个标准表达策略,支持“# {…}”表达式统一EL兼容的风格.
	 * @since 3.0
	 */
	void setBeanExpressionResolver(BeanExpressionResolver resolver);

	/**
	 * Return the resolution strategy for expressions in bean definition values.
	 * *************************************************************************
	 * ~$ 返回表达式在bean定义值的解决策略.
	 * @since 3.0
	 */
	BeanExpressionResolver getBeanExpressionResolver();

	/**
	 * Specify a Spring 3.0 ConversionService to use for converting
	 * property values, as an alternative to JavaBeans PropertyEditors.
	 * ****************************************************************
	 * ~$ 指定一个Spring 3.0 ConversionService用于转换属性值,作为一个javabean PropertyEditors的替代品.
	 * @since 3.0
	 */
	void setConversionService(ConversionService conversionService);

	/**
	 * Return the associated ConversionService, if any.
	 * ************************************************
	 * ~$ 返回相关ConversionService,如果任何.
	 * @since 3.0
	 */
	ConversionService getConversionService();

	/**
	 * Add a PropertyEditorRegistrar to be applied to all bean creation processes.
	 * <p>Such a registrar creates new PropertyEditor instances and registers them
	 * on the given registry, fresh for each bean creation attempt. This avoids
	 * the need for synchronization on custom editors; hence, it is generally
	 * preferable to use this method instead of {@link #registerCustomEditor}.
	 * ***************************************************************************
	 * ~$ 添加一个PropertyEditorRegistrar适用于所有bean创建过程.
	 * <p>这样一个注册商创造了新的PropertyEditor实例和寄存器在给定的注册表,每个bean创建新的尝试.
	 *    这避免了同步的需要定制编辑器;因此,它通常比使用这种方法而不是{ @link # registerCustomEditor }.
	 * @param registrar the PropertyEditorRegistrar to register
	 */
	void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar);

	/**
	 * Register the given custom property editor for all properties of the
	 * given type. To be invoked during factory configuration.
	 * <p>Note that this method will register a shared custom editor instance;
	 * access to that instance will be synchronized for thread-safety. It is
	 * generally preferable to use {@link #addPropertyEditorRegistrar} instead
	 * of this method, to avoid for the need for synchronization on custom editors.
	 * ****************************************************************************
	 * ~$ 注册给定的自定义属性编辑器给定类型的所有属性.工厂配置期间被调用.
	 * <p>注意,这个方法将注册一个共享自定义编辑器实例;访问该实例将同步线程安全.
	 *    通常比使用{@link #addPropertyEditorRegistrar }
	 *    而不是这种方法,以避免需要同步的自定义编辑器.
	 * @param requiredType type of the property
	 * @param propertyEditorClass the {@link PropertyEditor} class to register
	 */
	void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass);

	/**
	 * Initialize the given PropertyEditorRegistry with the custom editors
	 * that have been registered with this BeanFactory.
	 * ********************************************************************
	 * ~$ 用自定义编辑器初始化给定PropertyEditorRegistry BeanFactory已经注册.
	 * @param registry the PropertyEditorRegistry to initialize
	 */
	void copyRegisteredEditorsTo(PropertyEditorRegistry registry);

	/**
	 * Set a custom type converter that this BeanFactory should use for converting
	 * bean property values, constructor argument values, etc.
	 * <p>This will override the default PropertyEditor mechanism and hence make
	 * any custom editors or custom editor registrars irrelevant.
	 * ***************************************************************************
	 * ~$ 设置一个自定义类型转换器BeanFactory应该用于转换bean属性值,构造函数参数值,等等.
	 * <p>这将覆盖默认PropertyEditor机制,因此做任何自定义编辑器或自定义编辑器注册无关紧要.
	 * @see #addPropertyEditorRegistrar
	 * @see #registerCustomEditor
	 * @since 2.5
	 */
	void setTypeConverter(TypeConverter typeConverter);

	/**
	 * Obtain a type converter as used by this BeanFactory. This may be a fresh
	 * instance for each call, since TypeConverters are usually <i>not</i> thread-safe.
	 * <p>If the default PropertyEditor mechanism is active, the returned
	 * TypeConverter will be aware of all custom editors that have been registered.
	 * ********************************************************************************
	 * ~$ 获得这一BeanFactory所使用的类型转换器.这可能是一个新的实例为每个调用,因为TypeConverters通常不是线程安全的.
	 * <p>如果默认PropertyEditor机制被激活时,返回的TypeConverter将意识到所有已登记的自定义编辑器.
	 * @since 2.5
	 */
	TypeConverter getTypeConverter();

	/**
	 * Add a String resolver for embedded values such as annotation attributes.
	 * ***********************************************************************
	 * ~$ 添加一个字符串解析器等嵌入值注释属性.
	 * @param valueResolver the String resolver to apply to embedded values
	 * @since 3.0
	 */
	void addEmbeddedValueResolver(StringValueResolver valueResolver);

	/**
	 * Resolve the given embedded value, e.g. an annotation attribute.
	 * ***************************************************************
	 * ~$ 解决给定的内含价值,例如一个注释属性.
	 * @param value the value to resolve
	 * @return the resolved value (may be the original value as-is)
	 * @since 3.0
	 */
	String resolveEmbeddedValue(String value);

	/**
	 * Add a new BeanPostProcessor that will get applied to beans created
	 * by this factory. To be invoked during factory configuration.
	 * <p>Note: Post-processors submitted here will be applied in the order of
	 * registration; any ordering semantics expressed through implementing the
	 * {@link org.springframework.core.Ordered} interface will be ignored. Note
	 * that autodetected post-processors (e.g. as beans in an ApplicationContext)
	 * will always be applied after programmatically registered ones.
	 * ***************************************************************************
	 * ~$ 添加一个新的BeanPostProcessor,将应用于这个工厂创建的bean.工厂配置期间被调用.
	 * <p> 注意:提交后处理器将被应用在注册秩序的;任何顺序语义表达通过实施{@link org.springframework.core.Ordered }接口将被忽略.
	 *     注意个后处理器(如作为ApplicationContext bean)总是会应用编程后注册的.
	 *
	 * @param beanPostProcessor the post-processor to register
	 */
	void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

	/**
	 * Return the current number of registered BeanPostProcessors, if any.
	 * *******************************************************************
	 * ~$ 返回当前注册BeanPostProcessors,如果任何.
	 */
	int getBeanPostProcessorCount();

	/**
	 * Register the given scope, backed by the given Scope implementation.
	 * *******************************************************************
	 * ~$ 注册给定的范围,由给定的范围内实现.
	 *
	 * @param scopeName the scope identifier
	 * @param scope the backing Scope implementation
	 */
	void registerScope(String scopeName, Scope scope);

	/**
	 * Return the names of all currently registered scopes.
	 * <p>This will only return the names of explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * *********************************************************************
	 * ~$ 返回所有目前注册范围的名称.
	 * <p>这将只返回的名称注册范围明确. 内置的范围,如"singleton"和"prototype"不会暴露.
	 * @return the array of scope names, or an empty array if none
	 * @see #registerScope
	 */
	String[] getRegisteredScopeNames();

	/**
	 * Return the Scope implementation for the given scope name, if any.
	 * <p>This will only return explicitly registered scopes.
	 * Built-in scopes such as "singleton" and "prototype" won't be exposed.
	 * *********************************************************************
	 * ~$ 返回范围实现给定范围名称,如果有的话.
	 * <p>这将只返回的名称注册范围明确. 内置的范围,如"singleton"和"prototype"不会暴露.
	 * @param scopeName the name of the scope
	 * @return the registered Scope implementation, or <code>null</code> if none
	 * @see #registerScope
	 */
	Scope getRegisteredScope(String scopeName);

	/**
	 * Provides a security access control context relevant to this factory.
	 * ********************************************************************
	 * ~$ 提供了一个安全访问控制上下文相关的工厂.
	 * @return the applicable AccessControlContext (never <code>null</code>)
	 * @since 3.0
	 */
	AccessControlContext getAccessControlContext();

	/**
	 * Copy all relevant configuration from the given other factory.
	 * <p>Should include all standard configuration settings as well as
	 * BeanPostProcessors, Scopes, and factory-specific internal settings.
	 * Should not include any metadata of actual bean definitions,
	 * such as BeanDefinition objects and bean name aliases.
	 * ********************************************************************
	 * ~$ 复制所有相关配置给其他工厂.
	 * <p>应包括所有标准配置设置以及BeanPostProcessors范围,factory-specific内部设置.
	 *    不应包括任何实际bean定义的元数据,如BeanDefinition对象和bean名称的别名.
	 * @param otherFactory the other BeanFactory to copy from
	 */
	void copyConfigurationFrom(ConfigurableBeanFactory otherFactory);

	/**
	 * Given a bean name, create an alias. We typically use this method to
	 * support names that are illegal within XML ids (used for bean names).
	 * <p>Typically invoked during factory configuration, but can also be
	 * used for runtime registration of aliases. Therefore, a factory
	 * implementation should synchronize alias access.
	 * ********************************************************************
	 * ~$ 给定一个bean名称,创建一个别名.我们通常使用这种方法来支持名字是非法的在XML id(用于bean名称).
	 * <p>在工厂配置通常调用,但也可以用于运行时注册别名.因此,工厂的实现应该同步别名访问.
	 * @param beanName the canonical name of the target bean
	 * @param alias the alias to be registered for the bean
	 * @throws BeanDefinitionStoreException if the alias is already in use
	 */
	void registerAlias(String beanName, String alias) throws BeanDefinitionStoreException;

	/**
	 * Resolve all alias target names and aliases registered in this
	 * factory, applying the given StringValueResolver to them.
	 * <p>The value resolver may for example resolve placeholders
	 * in target bean names and even in alias names.
	 * *************************************************************
	 * ~$ 解决所有别名目标名和别名注册在这个工厂,应用StringValueResolver给他们.
	 * <p>解析器可能价值例如解决占位符在目标bean的名字甚至在别名.
	 * @param valueResolver the StringValueResolver to apply
	 * @since 2.5
	 */
	void resolveAliases(StringValueResolver valueResolver);

	/**
	 * Return a merged BeanDefinition for the given bean name,
	 * merging a child bean definition with its parent if necessary.
	 * Considers bean definitions in ancestor factories as well.
	 * *************************************************************
	 * ~$ 返回一个给定bean名称合并BeanDefinition,合并一个孩子bean定义与母公司如果必要的.
	 * @param beanName the name of the bean to retrieve the merged definition for
	 * @return a (potentially merged) BeanDefinition for the given bean
	 * @throws NoSuchBeanDefinitionException if there is no bean definition with the given name
	 * @since 2.5
	 */
	BeanDefinition getMergedBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Determine whether the bean with the given name is a FactoryBean.
	 * ****************************************************************
	 * ~$ 确定bean的名字是FactoryBean.
	 * @param name the name of the bean to check
	 * @return whether the bean is a FactoryBean
	 * (<code>false</code> means the bean exists but is not a FactoryBean)
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.5
	 */
	boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Explicitly control in-creation status of the specified bean. For
	 * container internal use only.
	 * ****************************************************************
	 * ~$ 显式地控制创作状态的指定的bean.仅对容器内部使用.
	 * @param beanName the name of the bean
	 * @param inCreation whether the bean is currently in creation
	 * @since 3.1
	 */
	void setCurrentlyInCreation(String beanName, boolean inCreation);

	/**
	 * Determine whether the specified bean is currently in creation.
	 * **************************************************************
	 * ~$ 确定指定的bean现在正在创造。
	 * @param beanName the name of the bean
	 * @return whether the bean is currently in creation
	 * @since 2.5
	 */
	boolean isCurrentlyInCreation(String beanName);

	/**
	 * Register a dependent bean for the given bean,
	 * to be destroyed before the given bean is destroyed.
	 * ***************************************************
	 * ~$ 注册一个bean为给定的bean的依赖,给定的bean被摧毁之前被摧毁.
	 * @param beanName the name of the bean
	 * @param dependentBeanName the name of the dependent bean
	 * @since 2.5
	 */
	void registerDependentBean(String beanName, String dependentBeanName);

	/**
	 * Return the names of all beans which depend on the specified bean, if any.
	 * *************************************************************************
	 * ~$ 返回所有bean的名称这取决于指定的bean,如果任何.
	 * @param beanName the name of the bean
	 * @return the array of dependent bean names, or an empty array if none
	 * @since 2.5
	 */
	String[] getDependentBeans(String beanName);

	/**
	 * Return the names of all beans that the specified bean depends on, if any.
	 * *************************************************************************
	 * ~$ 返回所有bean指定的bean的名称取决于,如果任何.
	 * @param beanName the name of the bean
	 * @return the array of names of beans which the bean depends on,
	 * or an empty array if none
	 * @since 2.5
	 */
	String[] getDependenciesForBean(String beanName);

	/**
	 * Destroy the given bean instance (usually a prototype instance
	 * obtained from this factory) according to its bean definition.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * ****************************************************************
	 * ~$ 摧毁给定的bean实例(通常是一个原型实例从这个工厂)根据其bean定义.
	 * <p>期间出现的任何异常应该被捕获并记录而不是破坏传播到该方法的调用者.
	 * @param beanName the name of the bean definition
	 * @param beanInstance the bean instance to destroy
	 */
	void destroyBean(String beanName, Object beanInstance);

	/**
	 * Destroy the specified scoped bean in the current target scope, if any.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * **********************************************************************
	 * ~$ 破坏范围指定bean在当前目标范围,如果任何.
	 * <p>期间出现的任何异常应该被捕获并记录而不是破坏传播到该方法的调用者.
	 * @param beanName the name of the scoped bean
	 */
	void destroyScopedBean(String beanName);

	/**
	 * Destroy all singleton beans in this factory, including inner beans that have
	 * been registered as disposable. To be called on shutdown of a factory.
	 * <p>Any exception that arises during destruction should be caught
	 * and logged instead of propagated to the caller of this method.
	 * ******************************************************************************
	 * ~$ 摧毁所有单例bean在这个工厂,包括内在bean已经注册为一次性。呼吁关闭工厂.
	 * <p>期间出现的任何异常应该被捕获并记录而不是破坏传播到该方法的调用者.
	 */
	void destroySingletons();

}
