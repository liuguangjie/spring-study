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

package org.springframework.beans.factory;

import org.springframework.beans.BeansException;

/**
 * The root interface for accessing a Spring bean container.
 * This is the basic client view of a bean container;
 * further interfaces such as {@link ListableBeanFactory} and
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}
 * are available for specific purposes.
 *
 * <p>This interface is implemented by objects that hold a number of bean definitions,
 * each uniquely identified by a String name. Depending on the bean definition,
 * the factory will return either an independent instance of a contained object
 * (the Prototype design pattern), or a single shared instance (a superior
 * alternative to the Singleton design pattern, in which the instance is a
 * singleton in the scope of the factory). Which type of instance will be returned
 * depends on the bean factory configuration: the API is the same. Since Spring
 * 2.0, further scopes are available depending on the concrete application
 * context (e.g. "request" and "session" scopes in a web environment).
 *
 * <p>The point of this approach is that the BeanFactory is a central registry
 * of application components, and centralizes configuration of application
 * components (no more do individual objects need to read properties files,
 * for example). See chapters 4 and 11 of "Expert One-on-One J2EE Design and
 * Development" for a discussion of the benefits of this approach.
 *
 * <p>Note that it is generally better to rely on Dependency Injection
 * ("push" configuration) to configure application objects through setters
 * or constructors, rather than use any form of "pull" configuration like a
 * BeanFactory lookup. Spring's Dependency Injection functionality is
 * implemented using this BeanFactory interface and its subinterfaces.
 *
 * <p>Normally a BeanFactory will load bean definitions stored in a configuration
 * source (such as an XML document), and use the <code>org.springframework.beans</code>
 * package to configure the beans. However, an implementation could simply return
 * Java objects it creates as necessary directly in Java code. There are no
 * constraints on how the definitions could be stored: LDAP, RDBMS, XML,
 * properties file, etc. Implementations are encouraged to support references
 * amongst beans (Dependency Injection).
 *
 * <p>In contrast to the methods in {@link ListableBeanFactory}, all of the
 * operations in this interface will also check parent factories if this is a
 * {@link HierarchicalBeanFactory}. If a bean is not found in this factory instance,
 * the immediate parent factory will be asked. Beans in this factory instance
 * are supposed to override beans of the same name in any parent factory.
 *
 * <p>Bean factory implementations should support the standard bean lifecycle interfaces
 * as far as possible. The full set of initialization methods and their standard order is:<br>
 * 1. BeanNameAware's <code>setBeanName</code><br>
 * 2. BeanClassLoaderAware's <code>setBeanClassLoader</code><br>
 * 3. BeanFactoryAware's <code>setBeanFactory</code><br>
 * 4. ResourceLoaderAware's <code>setResourceLoader</code>
 * (only applicable when running in an application context)<br>
 * 5. ApplicationEventPublisherAware's <code>setApplicationEventPublisher</code>
 * (only applicable when running in an application context)<br>
 * 6. MessageSourceAware's <code>setMessageSource</code>
 * (only applicable when running in an application context)<br>
 * 7. ApplicationContextAware's <code>setApplicationContext</code>
 * (only applicable when running in an application context)<br>
 * 8. ServletContextAware's <code>setServletContext</code>
 * (only applicable when running in a web application context)<br>
 * 9. <code>postProcessBeforeInitialization</code> methods of BeanPostProcessors<br>
 * 10. InitializingBean's <code>afterPropertiesSet</code><br>
 * 11. a custom init-method definition<br>
 * 12. <code>postProcessAfterInitialization</code> methods of BeanPostProcessors
 *
 * <p>On shutdown of a bean factory, the following lifecycle methods apply:<br>
 * 1. DisposableBean's <code>destroy</code><br>
 * 2. a custom destroy-method definition
 * *******************************************************************************************
 * ~$ 根接口来访问一个Spring bean容器。这是基本的客户端视图bean容器;进一步接口,
 *    如{@link ListableBeanFactory }和{@link org.springframework.beans.factory.config.ConfigurableBeanFactory }可用于特定目的.
 *
 * <p>实现此接口的对象持有的bean定义,每个惟一地标识一个字符串的名字.
 *    根据bean定义,工厂将返回一个独立实例包含对象(原型设计模式),
 *    或一个共享实例(单例设计模式的一个更好的选择,在一个单例实例在工厂)的范围.
 *    哪种类型的实例将取决于bean返回工厂配置:API是相同的.
 *    Spring 2.0以来,更多的范围可以根据具体的应用程序上下文(例如“请求”和“会话”范围在web环境中).
 *
 * <p>这种方法的关键是BeanFactory中央注册中心的应用程序组件,
 *    和集中配置的应用程序组件(单个对象不再需要读取属性文件,例如).
 *    参见第四章和11的“专家一对一的J2EE设计和开发”的讨论这种方法的好处.
 *
 * <p>注意,最好还是依靠依赖注入(“推”配置)来配置应用程序对象通过setter方法或构造函数,
 *    而不是使用任何形式的“拉动”配置像BeanFactory查找.
 *    Spring的依赖注入的功能是使用这个BeanFactory接口及其实现的个子.
 *
 * <p>通常BeanFactory加载bean定义存储在一个配置源(例如XML文档),并使用org.springframework.bean包配置bean.
 *    然而,一个实现可以直接返回Java对象创建必要的Java代码.
 *    没有限制如何存储的定义:LDAP、RDBMS,XML属性文件等.实现鼓励支持在bean引用(依赖注入).
 *
 * <p>与方法在{@link ListableBeanFactory },还该接口的所有操作将检查parent 工厂如果这是一个{@link HierarchicalBeanFactory }.
 *    如果没有找到在这个工厂bean实例,直接父工厂将被要求.
 *    在这个工厂bean实例应该重写bean名称相同的任何parent 工厂.
 *
 * <p>应该支持标准的Bean生命周期Bean工厂实现接口尽可能。完整的初始化方法及其标准的顺序是:
 * 	  查看上面的英文
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Chris Beams
 * @since 13 April 2001
 * @see BeanNameAware#setBeanName
 * @see BeanClassLoaderAware#setBeanClassLoader
 * @see BeanFactoryAware#setBeanFactory
 * @see org.springframework.context.ResourceLoaderAware#setResourceLoader
 * @see org.springframework.context.ApplicationEventPublisherAware#setApplicationEventPublisher
 * @see org.springframework.context.MessageSourceAware#setMessageSource
 * @see org.springframework.context.ApplicationContextAware#setApplicationContext
 * @see org.springframework.web.context.ServletContextAware#setServletContext
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessBeforeInitialization
 * @see InitializingBean#afterPropertiesSet
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getInitMethodName
 * @see org.springframework.beans.factory.config.BeanPostProcessor#postProcessAfterInitialization
 * @see DisposableBean#destroy
 * @see org.springframework.beans.factory.support.RootBeanDefinition#getDestroyMethodName
 */
public interface BeanFactory {

	/**
	 * Used to dereference a {@link FactoryBean} instance and distinguish it from
	 * beans <i>created</i> by the FactoryBean. For example, if the bean named
	 * <code>myJndiObject</code> is a FactoryBean, getting <code>&myJndiObject</code>
	 * will return the factory, not the instance returned by the factory.
	 * ******************************************************************************
	 * ~$ 用于废弃{@link FactoryBean }实例和区别于bean FactoryBean创建的.
	 *    例如,如果bean名为myJndiObject FactoryBean,myJndiObject将返回工厂,而不是实例返回的工厂.
	 */
	String FACTORY_BEAN_PREFIX = "&";

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>This method allows a Spring BeanFactory to be used as a replacement for the
	 * Singleton or Prototype design pattern. Callers may retain references to
	 * returned objects in the case of Singleton beans.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * *********************************************************************************
	 * ~$ 返回一个实例,这可能是共享或独立,指定的bean.
	 * <p>这种方法允许Spring BeanFactory用作替代单或原型设计模式.调用者可能会保留对返回对象的引用的单例bean.
	 * <p>翻译别名回相应的规范的bean的名称.会问父母工厂如果bean无法找到在这个工厂实例.
	 * @param name the name of the bean to retrieve
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there is no bean definition
	 * with the specified name
	 * @throws BeansException if the bean could not be obtained
	 */
	Object getBean(String name) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Behaves the same as {@link #getBean(String)}, but provides a measure of type
	 * safety by throwing a BeanNotOfRequiredTypeException if the bean is not of the
	 * required type. This means that ClassCastException can't be thrown on casting
	 * the result correctly, as can happen with {@link #getBean(String)}.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * *********************************************************************************
	 * ~$ 返回一个实例,这可能是共享或独立,指定的bean.
	 * <p>行为一样{@link #getBean(String)},但提供了一个衡量的类型安全扔BeanNotOfRequiredTypeException如果bean没有所需的类型.
	 *    这意味着ClassCastException不能被铸造结果正确,可能发生与{@link #getBean(String)}.
	 * <p>翻译别名回相应的规范的bean的名称。会问父母工厂如果bean无法找到在这个工厂实例.
	 * @param name the name of the bean to retrieve
	 *             ~$ 要检索bean的名称
	 * @param requiredType type the bean must match. Can be an interface or superclass
	 * of the actual class, or <code>null</code> for any match. For example, if the value
	 * is <code>Object.class</code>, this method will succeed whatever the class of the
	 * returned instance.
	 *                     ~$输入bean必须匹配.可以一个接口或实际类的超类,为任何匹配或null.
	 *                       例如,如果值是对象.类,这个方法会成功返回的类实例.
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there's no such bean definition
	 * @throws BeanNotOfRequiredTypeException if the bean is not of the required type
	 * @throws BeansException if the bean could not be created
	 */
	<T> T getBean(String name, Class<T> requiredType) throws BeansException;

	/**
	 * Return the bean instance that uniquely matches the given object type, if any.
	 * *****************************************************************************
	 * ~$ 返回的bean实例匹配给定的对象类型,如果有.
	 * @param requiredType type the bean must match; can be an interface or superclass.
	 * {@code null} is disallowed.
	 *                     ~$ bean类型必须匹配,可以一个接口或超类. {@code null} 无效.
	 * <p>This method goes into {@link ListableBeanFactory} by-type lookup territory
	 * but may also be translated into a conventional by-name lookup based on the name
	 * of the given type. For more extensive retrieval operations across sets of beans,
	 * use {@link ListableBeanFactory} and/or {@link BeanFactoryUtils}.
	 * ********************************************************************************
	 * ~$ 这种方法进入{@link ListableBeanFactory }按类型查找领土但也可能转化为一个传统的名字查找基于给定类型的名称.
	 *    为更广泛的跨组bean检索操作,使用{@link ListableBeanFactory }和/或{@link BeanFactoryUtils }.
	 * @return an instance of the single bean matching the required type
	 * @throws NoSuchBeanDefinitionException if there is not exactly one matching bean found
	 * @since 3.0
	 * @see ListableBeanFactory
	 */
	<T> T getBean(Class<T> requiredType) throws BeansException;

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * <p>Allows for specifying explicit constructor arguments / factory method arguments,
	 * overriding the specified default arguments (if any) in the bean definition.
	 * ***********************************************************************************
	 * ~$返回一个实例,这可能是共享或独立,指定的bean.
	 * <p>允许指定显式构造函数参数/工厂方法参数,覆盖指定的默认参数(如果有的话)的bean定义.
	 *
	 * @param name the name of the bean to retrieve
	 *             ~$ 要检索bean的名称
	 * @param args arguments to use if creating a prototype using explicit arguments to a
	 * static factory method. It is invalid to use a non-null args value in any other case.
	 *              ~$如果使用显式创建一个原型参数使用静态工厂方法的参数.
	 *                它是无效的在其他任何情况下使用一个非空参数值.
	 * @return an instance of the bean
	 * @throws NoSuchBeanDefinitionException if there's no such bean definition
	 * @throws BeanDefinitionStoreException if arguments have been given but
	 * the affected bean isn't a prototype
	 * @throws BeansException if the bean could not be created
	 * @since 2.5
	 */
	Object getBean(String name, Object... args) throws BeansException;

	/**
	 * Does this bean factory contain a bean definition or externally registered singleton
	 * instance with the given name?
	 * <p>If the given name is an alias, it will be translated back to the corresponding
	 * canonical bean name.
	 * <p>If this factory is hierarchical, will ask any parent factory if the bean cannot
	 * be found in this factory instance.
	 * <p>If a bean definition or singleton instance matching the given name is found,
	 * this method will return {@code true} whether the named bean definition is concrete
	 * or abstract, lazy or eager, in scope or not. Therefore, note that a {@code true}
	 * return value from this method does not necessarily indicate that {@link #getBean}
	 * will be able to obtain an instance for the same name.
	 * ************************************************************************************
	 * ~$ 这个bean工厂包含bean定义或外部注册单例实例与给定的名字吗?
	 * <p>如果给定的名称是一个别名,它将被转换回相应的规范的bean的名称.
	 * <p>如果这个工厂是分层的,将要求任何父母工厂如果bean无法找到在这个工厂实例.
	 * <p>如果一个bean定义或单例实例找到匹配给定的名称,这个方法将返回{@code true}指定的bean定义是否具体或抽象,懒惰或渴望,在范围或不是.
	 * 因此,请注意,{@code true}从这个方法返回值并不一定表明{@link #getBean }将能够获得相同名称的一个实例.
	 *
	 * @param name the name of the bean to query
	 * @return whether a bean with the given name is present
	 */
	boolean containsBean(String name);

	/**
	 * Is this bean a shared singleton? That is, will {@link #getBean} always
	 * return the same instance?
	 * <p>Note: This method returning <code>false</code> does not clearly indicate
	 * independent instances. It indicates non-singleton instances, which may correspond
	 * to a scoped bean as well. Use the {@link #isPrototype} operation to explicitly
	 * check for independent instances.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * *********************************************************************************
	 * ~$这个bean是一个共享的单吗?也就是说,将{@link #getBean }总是返回相同的实例吗?
	 * <p>注意:这个方法返回false并不标明独立实例.它表明单体实例,对应于一个作用域的bean.
	 *    使用{@link #isPrototype }操作来显式地检查独立实例.
	 * <p>翻译别名回相应的规范的bean的名称.会问parent工厂如果bean无法找到在这个工厂实例.
	 * @param name the name of the bean to query
	 * @return whether this bean corresponds to a singleton instance
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @see #getBean
	 * @see #isPrototype
	 */
	boolean isSingleton(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Is this bean a prototype? That is, will {@link #getBean} always return
	 * independent instances?
	 * <p>Note: This method returning <code>false</code> does not clearly indicate
	 * a singleton object. It indicates non-independent instances, which may correspond
	 * to a scoped bean as well. Use the {@link #isSingleton} operation to explicitly
	 * check for a shared singleton instance.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * *********************************************************************************
	 * ~$ 这个bean是一个原型吗?,{@link #getBean }将总是返回独立实例?
	 * <p>注意:这个方法返回false并不标明一个单例对象.它指出诱致性实例,对应于一个作用域的bean.
	 *    使用{@link #isSingleton }操作来显式地检查共享单例实例.
	 * <p>翻译别名回相应的规范的bean的名称.会问parent工厂如果bean无法找到在这个工厂实例.
	 * @param name the name of the bean to query
	 * @return whether this bean will always deliver independent instances
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.3
	 * @see #getBean
	 * @see #isSingleton
	 */
	boolean isPrototype(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Check whether the bean with the given name matches the specified type.
	 * More specifically, check whether a {@link #getBean} call for the given name
	 * would return an object that is assignable to the specified target type.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * *********************************************************************************
	 * ~$ 检查是否指定的bean的名字匹配类型.更具体地说,检查是否一个{@link #getBean }
	 *    调用的名字会返回一个对象分配到指定的目标类型.
	 * <p>翻译别名回相应的规范的bean的名称.会问parent工厂如果bean无法找到在这个工厂实例。
	 * @param name the name of the bean to query
	 * @param targetType the type to match against
	 * @return <code>true</code> if the bean type matches,
	 * <code>false</code> if it doesn't match or cannot be determined yet
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 2.0.1
	 * @see #getBean
	 * @see #getType
	 */
	boolean isTypeMatch(String name, Class<?> targetType) throws NoSuchBeanDefinitionException;

	/**
	 * Determine the type of the bean with the given name. More specifically,
	 * determine the type of object that {@link #getBean} would return for the given name.
	 * <p>For a {@link FactoryBean}, return the type of object that the FactoryBean creates,
	 * as exposed by {@link FactoryBean#getObjectType()}.
	 * <p>Translates aliases back to the corresponding canonical bean name.
	 * Will ask the parent factory if the bean cannot be found in this factory instance.
	 * *************************************************************************************
	 * ~$ 确定bean与给定的类型名称.更具体地说,确定对象的类型{@link #getBean }将返回给定的名字.
	 * <p>对于一个{@link FactoryBean },还FactoryBean创建的类型的对象,
	 *    所暴露的{@link FactoryBean # getObjectType()}.
	 * <p>翻译别名回相应的规范的bean的名称.会问parent工厂如果bean无法找到在这个工厂实例.
	 * @param name the name of the bean to query
	 * @return the type of the bean, or <code>null</code> if not determinable
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @since 1.1.2
	 * @see #getBean
	 * @see #isTypeMatch
	 */
	Class<?> getType(String name) throws NoSuchBeanDefinitionException;

	/**
	 * Return the aliases for the given bean name, if any.
	 * All of those aliases point to the same bean when used in a {@link #getBean} call.
	 * <p>If the given name is an alias, the corresponding original bean name
	 * and other aliases (if any) will be returned, with the original bean name
	 * being the first element in the array.
	 * <p>Will ask the parent factory if the bean cannot be found in this factory instance.
	 * ************************************************************************************
	 * ~$ 返回给定bean名称、别名.所有这些别名指向相同的bean中使用{@link #getBean }调用.
	 * <p>如果名字是一个别名,相应的原始bean名称和其他别名(如果有的话)将返回,
	 *    与原来的bean名称数组中的第一个元素.
	 * <p>会问 parent 工厂如果bean无法找到在这个工厂实例。
	 * @param name the bean name to check for aliases
	 * @return the aliases, or an empty array if none
	 * @see #getBean
	 */
	String[] getAliases(String name);

}
