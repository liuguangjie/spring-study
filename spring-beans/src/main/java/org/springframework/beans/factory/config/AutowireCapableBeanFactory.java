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

import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;

/**
 * Extension of the {@link BeanFactory}
 * interface to be implemented by bean factories that are capable of
 * autowiring, provided that they want to expose this functionality for
 * existing bean instances.
 *
 * <p>This subinterface of BeanFactory is not meant to be used in normal
 * application code: stick to {@link BeanFactory}
 * or {@link org.springframework.beans.factory.ListableBeanFactory} for
 * typical use cases.
 *
 * <p>Integration code for other frameworks can leverage this interface to
 * wire and populate existing bean instances that Spring does not control
 * the lifecycle of. This is particularly useful for WebWork Actions and
 * Tapestry Page objects, for example.
 *
 * <p>Note that this interface is not implemented by
 * {@link org.springframework.context.ApplicationContext} facades,
 * as it is hardly ever used by application code. That said, it is available
 * from an application context too, accessible through ApplicationContext's
 * {@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}
 * method.
 *
 * <p>You may also implement the {@link org.springframework.beans.factory.BeanFactoryAware}
 * interface, which exposes the internal BeanFactory even when running in an
 * ApplicationContext, to get access to an AutowireCapableBeanFactory:
 * simply cast the passed-in BeanFactory to AutowireCapableBeanFactory.
 *
 * *****************************************************************************************
 * ~$ 扩展的{@link BeanFactory }接口来实现bean工厂能够自动装配的,
 *    只要他们想要揭露这个功能对于现有的bean实例.
 *
 * <p> 这个子接口的BeanFactory并不意味着在正常的应用程序代码中使用:坚持{@link BeanFactory }
 *     或{@link org.springframework.beans.factory.ListableBeanFactory }为典型的用例.
 *
 * <p>其他框架集成代码可以利用这个接口连接和填充现有的bean实例,spring不控制的生命周期.
 *    这是特别有用的网络系统行为和Tapestry页面对象,例如。
 *
 * <p>注意这个接口不是由{@link org.springframework.context.ApplicationContext }实现 门面模式,
 * 	  因为它是很少使用的应用程序代码.也就是说,它可以从一个应用程序上下文,
 * 	  可以通过ApplicationContext的{@link org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()}的方法.
 *
 * <p>你也可以实现{@link org.springframework.beans.factory.BeanFactoryAware }接口,
 *    它暴露了内部BeanFactory即使运行在ApplicationContext,
 *    获得一个AutowireCapableBeanFactory:简单地把传入BeanFactory AutowireCapableBeanFactory.
 *
 *
 * @author Juergen Hoeller
 * @since 04.12.2003
 * @see org.springframework.beans.factory.BeanFactoryAware
 * @see ConfigurableListableBeanFactory
 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
 */
public interface AutowireCapableBeanFactory extends BeanFactory {

	/**
	 * Constant that indicates no externally defined autowiring. Note that
	 * BeanFactoryAware etc and annotation-driven injection will still be applied.
	 * ***************************************************************************
	 * ~$ 常数,表明你没有外部定义的自动装配.注意,BeanFactoryAware等和注解驱动注入仍将被应用.
	 *
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_NO = 0;

	/**
	 * Constant that indicates autowiring bean properties by name
	 * (applying to all bean property setters).
	 * **********************************************************
	 * ~$ 常数表明自动装配bean属性的名字(适用于所有bean属性setter).
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_BY_NAME = 1;

	/**
	 * Constant that indicates autowiring bean properties by type
	 * (applying to all bean property setters).
	 * **********************************************************
	 * ~$ 常数表明自动装配bean属性的类型(适用于所有bean属性setter).
	 * @see #createBean
	 * @see #autowire
	 * @see #autowireBeanProperties
	 */
	int AUTOWIRE_BY_TYPE = 2;

	/**
	 * Constant that indicates autowiring the greediest constructor that
	 * can be satisfied (involves resolving the appropriate constructor).
	 * ******************************************************************
	 * ~$ 常数,表明能满足自动装配贪婪的构造函数(包括解决适当的构造函数).
	 * @see #createBean
	 * @see #autowire
	 */
	int AUTOWIRE_CONSTRUCTOR = 3;

	/**
	 * Constant that indicates determining an appropriate autowire strategy
	 * through introspection of the bean class.
	 * ********************************************************************
	 * ~$ 常数表明确定一个适当的自动装配策略通过内省的bean类.
	 * @see #createBean
	 * @see #autowire
	 * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
	 * prefer annotation-based autowiring for clearer demarcation of autowiring needs.
	 * *******************************************************************************
	 * ~$ 如果您使用的是混合自动装配策略,更喜欢基于注解的自动装配自动装配需要的清晰界定.
	 */
	@Deprecated
	int AUTOWIRE_AUTODETECT = 4;


	//-------------------------------------------------------------------------
	// Typical methods for creating and populating external bean instances
	//-------------------------------------------------------------------------
	/** 典型的方法来创建和填充外部bean实例*/
	/**
	 * Fully create a new bean instance of the given class.
	 * <p>Performs full initialization of the bean, including all applicable
	 * {@link BeanPostProcessor BeanPostProcessors}.
	 * <p>Note: This is intended for creating a fresh instance, populating annotated
	 * fields and methods as well as applying all standard bean initialiation callbacks.
	 * It does <i>not</> imply traditional by-name or by-type autowiring of properties;
	 * use {@link #createBean(Class, int, boolean)} for that purposes.
	 * *********************************************************************************
	 * ~$ 完全给定类的创建一个新的bean实例.
	 * <p>执行完整的bean的初始化,包括所有适用的{@link BeanPostProcessor BeanPostProcessors}.
	 * <p>注意:这是用于创建一个新的实例,填充带注释的字段和方法以及应用所有标准bean initialiation回调.
	 *    这并不意味着传统的名字或按类型属性的自动装配;使用{@link #createBean(Class,int,boolean)}的目的.
	 *
	 * @param beanClass the class of the bean to create
	 *                  创建bean的类
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 */
	<T> T createBean(Class<T> beanClass) throws BeansException;

	/**
	 * Populate the given bean instance through applying after-instantiation callbacks
	 * and bean property post-processing (e.g. for annotation-driven injection).
	 * <p>Note: This is essentially intended for (re-)populating annotated fields and
	 * methods, either for new instances or for deserialized instances. It does
	 * <i>not</i> imply traditional by-name or by-type autowiring of properties;
	 * use {@link #autowireBeanProperties} for that purposes.
	 * ********************************************************************************
	 * ~$ 填充给定的bean实例通过应用after-instantiation回调函数和bean属性后处理(例如注解驱动的注入).
	 * <p>注:这是用于(重新)填充带注释的字段和方法,对新实例或反序列化实例.
	 *    这并不意味着传统的名字或按类型属性的自动装配;使用{@link #autowireBeanProperties }的目的.
	 *
	 * @param existingBean the existing bean instance
	 * @throws BeansException if wiring failed
	 */
	void autowireBean(Object existingBean) throws BeansException;

	/**
	 * Configure the given raw bean: autowiring bean properties, applying
	 * bean property values, applying factory callbacks such as <code>setBeanName</code>
	 * and <code>setBeanFactory</code>, and also applying all bean post processors
	 * (including ones which might wrap the given raw bean).
	 * <p>This is effectively a superset of what {@link #initializeBean} provides,
	 * fully applying the configuration specified by the corresponding bean definition.
	 * <b>Note: This method requires a bean definition for the given name!</b>
	 *
	 * *********************************************************************************
	 * ~$ 给定原始配置bean:自动装配bean属性,应用bean属性值,应用工厂回调setBeanName和setBeanFactory等,
	 *    并应用所有bean后处理器(包括那些可能将给定的原始bean).
	 * <p>这是有效的超集{@link #initializeBean }提供什么,充分应用指定的配置相应的bean定义.
	 *    注意:这个方法需要一个bean定义为给定的名字!
	 *
	 * @param existingBean the existing bean instance  ~$ 现有的bean实例
	 * @param beanName the name of the bean, to be passed to it if necessary
	 * (a bean definition of that name has to be available)   ~$ bean的名称,如果需要传递给它(bean定义的名称必须是可用的)
	 * @return the bean instance to use, either the original or a wrapped one ~$ 使用bean实例,原始的或一个包裹
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
	 * if there is no bean definition with the given name
	 * @throws BeansException if the initialization failed
	 * @see #initializeBean
	 */
	Object configureBean(Object existingBean, String beanName) throws BeansException;

	/**
	 * Resolve the specified dependency against the beans defined in this factory.
	 * **************************************************************************
	 * ~$ 解决依赖对指定bean中定义这个工厂.
	 * @param descriptor the descriptor for the dependency ~$ 描述符的依赖
	 * @param beanName the name of the bean which declares the present dependency ~$ bean的名称声明目前的依赖
	 * @return the resolved object, or <code>null</code> if none found ~$ 如果没有发现解决对象,或null
	 * @throws BeansException in dependency resolution failed
	 */
	Object resolveDependency(DependencyDescriptor descriptor, String beanName) throws BeansException;


	//-------------------------------------------------------------------------
	// Specialized methods for fine-grained control over the bean lifecycle
	//-------------------------------------------------------------------------
    /** 专业细粒度的控制bean生命周期的方法 */
	/**
	 * Fully create a new bean instance of the given class with the specified
	 * autowire strategy. All constants defined in this interface are supported here.
	 * <p>Performs full initialization of the bean, including all applicable
	 * {@link BeanPostProcessor BeanPostProcessors}. This is effectively a superset
	 * of what {@link #autowire} provides, adding {@link #initializeBean} behavior.
	 *
	 * ******************************************************************************
	 * ~$ 完全给定类的创建一个新的bean实例指定的自动装配策略.在此接口中定义的所有常量支持.
	 * <p>执行完整的bean的初始化,包括所有适用的{ @link BeanPostProcessor BeanPostProcessors }.
	 *    这是有效的超集{@link #autowire} 提供什么,添加{@link #initializeBean}的行为.
	 *
	 * @param beanClass the class of the bean to create ~$ 创建bean的类
	 * @param autowireMode by name or type, using the constants in this interface ~$ 通过名称或类型,使用这个接口的常量
	 * @param dependencyCheck whether to perform a dependency check for objects ~$是否进行依赖检查对象
	 * (not applicable to autowiring a constructor, thus ignored there)
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 */
	Object createBean(Class beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * Instantiate a new bean instance of the given class with the specified autowire
	 * strategy. All constants defined in this interface are supported here.
	 * Can also be invoked with <code>AUTOWIRE_NO</code> in order to just apply
	 * before-instantiation callbacks (e.g. for annotation-driven injection).
	 * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
	 * callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example
	 * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
	 * callbacks are applied, if applicable to the construction of the instance.
	 *
	 * *******************************************************************************
	 * ~$ 给定类的实例化一个新的bean实例指定的自动装配策略.在此接口中定义的所有常量支持.
	 *    也可以调用AUTOWIRE_NO为了只是应用before-instantiation回调(例如注解驱动的注入).
	 *
	 * <p>不适用标准{ @link BeanPostProcessor BeanPostProcessors }回调或执行任何进一步的bean的初始化.
	 *    这个接口提供了独特的,细粒度的操作的目的,例如{@link #initializeBean }.
	 *    然而,{@link InstantiationAwareBeanPostProcessor }应用回调,如果适用于建设的实例.
	 *
	 * @param beanClass the class of the bean to instantiate ~$ 要实例化bean的类
	 * @param autowireMode by name or type, using the constants in this interface ~$ 通过名称或类型,使用这个接口的常量
	 * @param dependencyCheck whether to perform a dependency check for object
	 * references in the bean instance (not applicable to autowiring a constructor,
	 * thus ignored there)  ~$ 是否进行依赖检查对象引用bean实例(不适用于自动装配一个构造函数,因此忽略了)
	 * @return the new bean instance
	 * @throws BeansException if instantiation or wiring failed
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_AUTODETECT
	 * @see #initializeBean
	 * @see #applyBeanPostProcessorsBeforeInitialization
	 * @see #applyBeanPostProcessorsAfterInitialization
	 */
	Object autowire(Class beanClass, int autowireMode, boolean dependencyCheck) throws BeansException;

	/**
	 * Autowire the bean properties of the given bean instance by name or type.
	 * Can also be invoked with <code>AUTOWIRE_NO</code> in order to just apply
	 * after-instantiation callbacks (e.g. for annotation-driven injection).
	 * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
	 * callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example
	 * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
	 * callbacks are applied, if applicable to the configuration of the instance.
	 *
	 * *******************************************************************************
	 * ~$ 自动装配给定的bean实例的bean属性名称或类型.
	 *    也可以调用AUTOWIRE_NO为了只是应用after-instantiation回调(例如注解驱动的注入).
	 * <p>不适用标准{@link BeanPostProcessor BeanPostProcessors }回调或执行任何进一步的bean的初始化.
	 *    这个接口提供了独特的,细粒度的操作的目的,例如{@link #initializeBean }.
	 *    然而,{@link InstantiationAwareBeanPostProcessor }应用回调,如果适用的配置实例.
	 *
	 * @param existingBean the existing bean instance ~$ 现有的bean实例
	 * @param autowireMode by name or type, using the constants in this interface ~$ 通过名称或类型,使用这个接口的常量
	 * @param dependencyCheck whether to perform a dependency check for object
	 * references in the bean instance  ~$ 是否进行依赖检查对象引用bean实例
	 * @throws BeansException if wiring failed
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_NO
	 */
	void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck)
			throws BeansException;

	/**
	 * Apply the property values of the bean definition with the given name to
	 * the given bean instance. The bean definition can either define a fully
	 * self-contained bean, reusing its property values, or just property values
	 * meant to be used for existing bean instances.
	 * <p>This method does <i>not</i> autowire bean properties; it just applies
	 * explicitly defined property values. Use the {@link #autowireBeanProperties}
	 * method to autowire an existing bean instance.
	 * <b>Note: This method requires a bean definition for the given name!</b>
	 * <p>Does <i>not</i> apply standard {@link BeanPostProcessor BeanPostProcessors}
	 * callbacks or perform any further initialization of the bean. This interface
	 * offers distinct, fine-grained operations for those purposes, for example
	 * {@link #initializeBean}. However, {@link InstantiationAwareBeanPostProcessor}
	 * callbacks are applied, if applicable to the configuration of the instance.
	 *
	 * ******************************************************************************
	 * ~$ 将bean定义的属性值与给定的bean实例的名字.bean定义可以定义一个完全独立的bean,
	 *    重用其属性值,或只是属性值应该用于现有的bean实例.
	 * <p>这个方法不会自动装配bean属性;它只是适用于显式定义的属性值.
	 *    使用{@link #autowireBeanProperties }方法自动装配一个现有的bean实例.
	 *   注意:这个方法需要一个bean定义为给定的名字!
	 * <p>不适用标准{ @link BeanPostProcessor BeanPostProcessors }回调或执行任何进一步的bean的初始化.
	 *    这个接口提供了独特的,细粒度的操作的目的,例如{@link #initializeBean }.
	 *    然而,{@link InstantiationAwareBeanPostProcessor }应用回调,如果适用的配置实例.
	 *
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean definition in the bean factory
	 * (a bean definition of that name has to be available)
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException
	 * if there is no bean definition with the given name
	 * @throws BeansException if applying the property values failed
	 * @see #autowireBeanProperties
	 */
	void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException;

	/**
	 * Initialize the given raw bean, applying factory callbacks
	 * such as <code>setBeanName</code> and <code>setBeanFactory</code>,
	 * also applying all bean post processors (including ones which
	 * might wrap the given raw bean).
	 * <p>Note that no bean definition of the given name has to exist
	 * in the bean factory. The passed-in bean name will simply be used
	 * for callbacks but not checked against the registered bean definitions.
	 *
	 * **********************************************************************
	 * ~$ 初始化给生豆,工厂应用回调等setBeanName setBeanFactory,
	 *    还应用所有bean后处理器(包括那些可能将给定的原始bean).
	 * <p>请注意,没有名字的bean定义bean工厂的存在.
	 *     传入bean的名字只会被用于回调而不是检查对注册的bean定义.
	 *
	 * @param existingBean the existing bean instance
	 * @param beanName the name of the bean, to be passed to it if necessary
	 * (only passed to {@link BeanPostProcessor BeanPostProcessors})
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if the initialization failed
	 */
	Object initializeBean(Object existingBean, String beanName) throws BeansException;

	/**
	 * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
	 * instance, invoking their <code>postProcessBeforeInitialization</code> methods.
	 * The returned bean instance may be a wrapper around the original.
	 * ******************************************************************************
	 * ~$ 应用{ @link BeanPostProcessor BeanPostProcessors }给定的现有的bean实例,
	 *    调用postProcessBeforeInitialization方法.返回的bean实例可能是原始的包装器.
	 *
	 * @param existingBean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if any post-processing failed
	 * @see BeanPostProcessor#postProcessBeforeInitialization
	 */
	Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 * Apply {@link BeanPostProcessor BeanPostProcessors} to the given existing bean
	 * instance, invoking their <code>postProcessAfterInitialization</code> methods.
	 * The returned bean instance may be a wrapper around the original.
	 * ******************************************************************************
	 * ~$ 应用{ @link BeanPostProcessor BeanPostProcessors }给定的现有的bean实例,
	 *    调用postProcessBeforeInitialization方法.返回的bean实例可能是原始的包装器.
	 * @param existingBean the new bean instance
	 * @param beanName the name of the bean
	 * @return the bean instance to use, either the original or a wrapped one
	 * @throws BeansException if any post-processing failed
	 * @see BeanPostProcessor#postProcessAfterInitialization
	 */
	Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
			throws BeansException;

	/**
	 * Resolve the specified dependency against the beans defined in this factory.
	 * ***************************************************************************
	 * ~$ 解决依赖对指定bean中定义这个工厂
	 * @param descriptor the descriptor for the dependency
	 * @param beanName the name of the bean which declares the present dependency
	 * @param autowiredBeanNames a Set that all names of autowired beans (used for
	 * resolving the present dependency) are supposed to be added to
	 * @param typeConverter the TypeConverter to use for populating arrays and
	 * collections
	 * @return the resolved object, or <code>null</code> if none found
	 * @throws BeansException in dependency resolution failed
	 */
	Object resolveDependency(DependencyDescriptor descriptor, String beanName,
                             Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException;

}
