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

import org.springframework.beans.BeanMetadataElement;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.core.AttributeAccessor;

/**
 * A BeanDefinition describes a bean instance, which has property values,
 * constructor argument values, and further information supplied by
 * concrete implementations.
 *
 * <p>This is just a minimal interface: The main intention is to allow a
 * {@link BeanFactoryPostProcessor} such as {@link PropertyPlaceholderConfigurer}
 * to introspect and modify property values and other bean metadata.
 * *****************************************************************************
 * ~$ BeanDefinition描述一个bean实例,属性值,构造函数参数值,进一步的信息提供的具体实现
 *
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @since 19.03.2004
 * @see ConfigurableListableBeanFactory#getBeanDefinition
 * @see org.springframework.beans.factory.support.RootBeanDefinition
 * @see org.springframework.beans.factory.support.ChildBeanDefinition
 */
public interface BeanDefinition extends AttributeAccessor, BeanMetadataElement {

	/**
	 * Scope identifier for the standard singleton scope: "singleton".
	 * <p>Note that extended bean factories might support further scopes.
	 * ******************************************************************
	 * ~$ 范围标识符标准单范围:"singleton"
	 *  <p>注意进一步扩展bean工厂可能支持范围
	 * @see #setScope
	 */
	String SCOPE_SINGLETON = ConfigurableBeanFactory.SCOPE_SINGLETON;

	/**
	 * Scope identifier for the standard prototype scope: "prototype".
	 * <p>Note that extended bean factories might support further scopes.
	 * ******************************************************************
	 * ~$ 范围标识符标准单范围:"prototype"
	 *  <p>注意进一步扩展bean工厂可能支持范围
	 * @see #setScope
	 */
	String SCOPE_PROTOTYPE = ConfigurableBeanFactory.SCOPE_PROTOTYPE;


	/**
	 * Role hint indicating that a <code>BeanDefinition</code> is a major part
	 * of the application. Typically corresponds to a user-defined bean.
	 * ***********************************************************************
	 * ~$ 角色暗示表明<code>BeanDefinition></code>是应用程序的一个主要部分.
	 *    通常对应于一个用户定义的bean
	 */
	int ROLE_APPLICATION = 0;

	/**
	 * Role hint indicating that a <code>BeanDefinition</code> is a supporting
	 * part of some larger configuration, typically an outer
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 * <code>SUPPORT</code> beans are considered important enough to be aware
	 * of when looking more closely at a particular
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition},
	 * but not when looking at the overall configuration of an application.
	 * **********************************************************************
	 *
	 * ~$ 角色暗示表明<code>BeanDefinition</code>是一个支持一些更大的配置的一部分,
	 * 通常外部{@link org.springframework.beans.factory.parsing.ComponentDefinition }.
	 * <code>SUPPORT</code>支持bean时认为如此重要以至于足以被意识到更加关注一个特定的
	 * {@link org.springframework.beans.factory.parsing.ComponentDefinition },但当看着整个应用程序的配置
	 */
	int ROLE_SUPPORT = 1;

	/**
	 * Role hint indicating that a <code>BeanDefinition</code> is providing an
	 * entirely background role and has no relevance to the end-user. This hint is
	 * used when registering beans that are completely part of the internal workings
	 * of a {@link org.springframework.beans.factory.parsing.ComponentDefinition}.
	 * *****************************************************************************
	 * ~$角色暗示表明<code>BeanDefinition</code>提供一个完全的背景角色,也没有与最终用户之间的关系。
	 * 这提示注册时使用bean是完全的内部工作的一部分{@link org.springframework.beans.factory.parsing.ComponentDefinition }
	 */
	int ROLE_INFRASTRUCTURE = 2;


	/**
	 * Return the name of the parent definition of this bean definition, if any.
	 * *************************************************************************
	 * ~$ 返回根对象 的名称定义的bean定义,如果任何
	 */
	String getParentName();

	/**
	 * Set the name of the parent definition of this bean definition, if any.
	 * *********************************************************************
	 * ~$ 设置父的定义这个bean定义的名称,如果有的话
	 */
	void setParentName(String parentName);

	/**
	 * Return the current bean class name of this bean definition.
	 * <p>Note that this does not have to be the actual class name used at runtime, in
	 * case of a child definition overriding/inheriting the class name from its parent.
	 * Hence, do <i>not</i> consider this to be the definitive bean type at runtime but
	 * rather only use it for parsing purposes at the individual bean definition level.
	 * ********************************************************************************
	 * ~$ 返回当前这个bean定义的bean类的名字.
	 *  <p>请注意,这并不需要在运行时.
	 *  使用实际的类名称,以防孩子定义覆盖/继承父类名.
	 *  因此 <i>not</i>  认为这是明确的bean类型,而是只会用它来在运行时解析的目的在个体bean定义的级别。
	 */
	String getBeanClassName();

	/**
	 * Override the bean class name of this bean definition.
	 * <p>The class name can be modified during bean factory post-processing,
	 * typically replacing the original class name with a parsed variant of it.
	 * ************************************************************************
	 * ~$ 覆盖这个bean定义的bean类名称
	 * <P>在bean类名称可以修改工厂后处理,通常替换原有的类名称解析它的变种
	 */
	void setBeanClassName(String beanClassName);

	/**
	 * Return the factory bean name, if any.
	 * 返回工厂的名称,如果有
	 */
	String getFactoryBeanName();

	/**
	 * Specify the factory bean to use, if any.
	 * 指定要使用工厂bean,如果有
	 */
	void setFactoryBeanName(String factoryBeanName);

	/**
	 * Return a factory method, if any.
	 * 返回工厂方法 如果有
	 */
	String getFactoryMethodName();

	/**
	 * Specify a factory method, if any. This method will be invoked with
	 * constructor arguments, or with no arguments if none are specified.
	 * The method will be invoked on the specified factory bean, if any,
	 * or otherwise as a static method on the local bean class.
	 * ******************************************************************
	 * ~$ 指定一个工厂方法,如果有. 该方法将调用构造函数参数,如果没有指定或没有参数
	 *	  方法将调用指定的工厂bean,如果有,
	 *	  或者作为一个静态方法在本地bean类
	 * @param factoryMethodName static factory method name,
	 * or <code>null</code> if normal constructor creation should be used
	 *
	 * @see #getBeanClassName()
	 */
	void setFactoryMethodName(String factoryMethodName);

	/**
	 * Return the name of the current target scope for this bean,
	 * or <code>null</code> if not known yet.
	 * *********************************************************
	 * ~$ 返回当前目标范围这个bean的名称,如果还不知道 返回 <code> null </code>
	 */
	String getScope();

	/**
	 * Override the target scope of this bean, specifying a new scope name.
	 * *******************************************************************
	 * ~$ 覆盖这个bean的目标范围,指定一个新名称范围
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	void setScope(String scope);

	/**
	 * Return whether this bean should be lazily initialized, i.e. not
	 * eagerly instantiated on startup. Only applicable to a singleton bean.
	 * ********************************************************************
	 * ~$ 返回这个bean是否应该延迟初始化,
	 *	  即在启动时不急切地实例化.只适用于一个单例bean
	 */
	boolean isLazyInit();

	/**
	 * Set whether this bean should be lazily initialized.
	 * <p>If <code>false</code>, the bean will get instantiated on startup by bean
	 * factories that perform eager initialization of singletons.
	 * ***************************************************************************
	 * ~$ 设置这个bean是否应该延迟初始化  <p>If <code>false</code>,
	 *    实例化bean会在启动bean工厂执行急切的单例对象的初始化
	 */
	void setLazyInit(boolean lazyInit);

	/**
	 * Return the bean names that this bean depends on.
	 * ************************************************
	 * ~$ 返回bean的名称取决于这个bean
	 */
	String[] getDependsOn();

	/**
	 * Set the names of the beans that this bean depends on being initialized.
	 * The bean factory will guarantee that these beans get initialized first.
	 * ***********************************************************************
	 * ~$ 设置的bean的名称,这个bean取决于被初始化.
	 *	  bean工厂将保证这些bean先初始化.
	 */
	void setDependsOn(String[] dependsOn);

	/**
	 * Return whether this bean is a candidate for getting autowired into some other bean.
	 * **********************************************************************************
	 * ~$ 返回这个bean是否适合让autowired进入其他bean
	 */
	boolean isAutowireCandidate();

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * ********************************************************************************
	 * ~$ 设置这个bean是否适合让autowired进入其他bean
	 */
	void setAutowireCandidate(boolean autowireCandidate);

	/**
	 * Return whether this bean is a primary autowire candidate.
	 * If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * *********************************************************
	 * ~$ 返回这个bean是否主要自动装配的候选人.
	 * 	  如果这个值适用于多个bean之一
	 * 	  匹配的候选人,它将作为一个平局决胜
	 */
	boolean isPrimary();

	/**
	 * Set whether this bean is a primary autowire candidate.
	 * <p>If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * **************************************************************
	 * ~$ 设置是否这个bean是一个主要的自动装配的候选人
	 * 	  如果这个值适用于一个bean在多个匹配的候选人,
	 * 	  它将作为一个平局决胜.
	 */
	void setPrimary(boolean primary);


	/**
	 * Return the constructor argument values for this bean.
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * *****************************************************************************
	 * ~$ 为这个bean构造函数参数返回值.
	 * <p>返回的实例可以修改在bean工厂后处理
	 * @return the ConstructorArgumentValues object (never <code>null</code>)
	 */
	ConstructorArgumentValues getConstructorArgumentValues();

	/**
	 * Return the property values to be applied to a new instance of the bean.
	 * <p>The returned instance can be modified during bean factory post-processing.
	 * *****************************************************************************
	 * ~$ 返回属性值适用于bean的一个新实例
	 * <p>返回的实例可以修改在bean工厂后处理
	 * @return the MutablePropertyValues object (never <code>null</code>)
	 */
	MutablePropertyValues getPropertyValues();


	/**
	 * Return whether this a <b>Singleton</b>, with a single, shared instance
	 * returned on all calls.
	 * **********************************************************************
	 * ~$ 返回这是否一个 <b>Singleton</b>,与一个单一的、共享实例返回的所有调用
	 * @see #SCOPE_SINGLETON
	 */
	boolean isSingleton();

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance
	 * returned for each call.
	 * ********************************************************************
	 * ~$ 返回这是否一个 <b>Prototype</b>, 与一个独立实例为每个调用返回
	 * @see #SCOPE_PROTOTYPE
	 */
	boolean isPrototype();

	/**
	 * Return whether this bean is "abstract", that is, not meant to be instantiated.
	 * ******************************************************************************
	 * ~$ 返回这个bean是否 "abstract",也就是说,无意被实例化
	 */
	boolean isAbstract();

	/**
	 * Get the role hint for this <code>BeanDefinition</code>. The role hint
	 * provides tools with an indication of the importance of a particular
	 * <code>BeanDefinition</code>.
	 * *********************************************************************
	 * ~$ 得到的作用提示 <code>BeanDefinition</code>.
	 * 	  提示为工具提供了一个指示作用的一个特定的重要性
	 * @see #ROLE_APPLICATION
	 * @see #ROLE_INFRASTRUCTURE
	 * @see #ROLE_SUPPORT
	 */
	int getRole();

	/**
	 * Return a human-readable description of this bean definition.
	 * ************************************************************
	 * ~$ 返回一个可读的描述这个bean定义
	 */
	String getDescription();

	/**
	 * Return a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 * *****************************************************************
	 * ~$ 返回一个资源的描述,这个bean定义来自(为了显示上下文的错误)
	 */
	String getResourceDescription();

	/**
	 * Return the originating BeanDefinition, or <code>null</code> if none.
	 * Allows for retrieving the decorated bean definition, if any.
	 * <p>Note that this method returns the immediate originator. Iterate through the
	 * originator chain to find the original BeanDefinition as defined by the user.
	 * ******************************************************************************
	 * ~$ 返回原始BeanDefinition, 如果没有返回 null .
	 *     允许检索bean定义,如果有
	 *
	 * <p>注意,这个方法返回的直接发起者.
	 * 遍历发起人链找到原始BeanDefinition由用户定义
	 */
	BeanDefinition getOriginatingBeanDefinition();

}
