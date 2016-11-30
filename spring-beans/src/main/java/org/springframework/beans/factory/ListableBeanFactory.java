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

package org.springframework.beans.factory;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;

/**
 * Extension of the {@link BeanFactory} interface to be implemented by bean factories
 * that can enumerate all their bean instances, rather than attempting bean lookup
 * by name one by one as requested by clients. BeanFactory implementations that
 * preload all their bean definitions (such as XML-based factories) may implement
 * this interface.
 *
 * <p>If this is a {@link HierarchicalBeanFactory}, the return values will <i>not</i>
 * take any BeanFactory hierarchy into account, but will relate only to the beans
 * defined in the current factory. Use the {@link BeanFactoryUtils} helper class
 * to consider beans in ancestor factories too.
 *
 * <p>The methods in this interface will just respect bean definitions of this factory.
 * They will ignore any singleton beans that have been registered by other means like
 * {@link org.springframework.beans.factory.config.ConfigurableBeanFactory}'s
 * <code>registerSingleton</code> method, with the exception of
 * <code>getBeanNamesOfType</code> and <code>getBeansOfType</code> which will check
 * such manually registered singletons too. Of course, BeanFactory's <code>getBean</code>
 * does allow transparent access to such special beans as well. However, in typical
 * scenarios, all beans will be defined by external bean definitions anyway, so most
 * applications don't need to worry about this differentation.
 *
 * <p><b>NOTE:</b> With the exception of <code>getBeanDefinitionCount</code>
 * and <code>containsBeanDefinition</code>, the methods in this interface
 * are not designed for frequent invocation. Implementations may be slow.
 * ************************************************************************************
 * ~$扩展的{ @link BeanFactory }接口来实现bean工厂,可以列举所有bean实例,而不是试图bean查找一个接一个的名字所要求的客户.
 * BeanFactory实现预加载所有bean定义(比如基于xml的工厂)可能实现这个接口.
 *
 * <p>如果这是一个{@link HierarchicalBeanFactory },返回值不会考虑任何BeanFactory层次结构,但只与当前工厂bean中定义.
 *    使用{@link BeanFactoryUtils }助手类考虑在祖先工厂bean.
 *
 * <p>这个接口中的方法只会尊重这个工厂的bean定义.他们会忽略任何单例bean注册的其他手段如
 *    {@link org.springframework.beans.factory.config.ConfigurableBeanFactory }’s registerSingleton方法,
 *    除了getBeanNamesOfType和getBeansOfType检查此类手动注册单例。当然,BeanFactory getBean确实允许对此类特殊的透明访问bean.
 *    然而,在一般情况下,所有bean将由外部定义的bean定义,所以大多数应用程序不需要担心这个differentation.
 *
 * <p>注意:除了getBeanDefinitionCount containsBeanDefinition,这个接口中的方法不为频繁的调用而设计的.实现可能是缓慢的.
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 16 April 2001
 * @see HierarchicalBeanFactory
 * @see BeanFactoryUtils
 */
public interface ListableBeanFactory extends BeanFactory {

	/**
	 * Check if this bean factory contains a bean definition with the given name.
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * **************************************************************************
	 * ~$ 检查是否这个bean工厂包含一个给定名称的bean定义.
	 * <p>不考虑任何层次本厂可能参与,而忽略任何已登记的单例bean通过其他方式比bean定义.
	 * @param beanName the name of the bean to look for
	 * @return if this bean factory contains a bean definition with the given name
	 * @see #containsBean
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 * Return the number of beans defined in the factory.
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * *******************************************************************
	 * ~$ 返回工厂中定义bean的数量.
	 * <p>不考虑任何层次本厂可能参与,而忽略任何已登记的单例bean通过其他方式比bean定义.
	 * @return the number of beans defined in the factory
	 */
	int getBeanDefinitionCount();

	/**
	 * Return the names of all beans defined in this factory.
	 * <p>Does not consider any hierarchy this factory may participate in,
	 * and ignores any singleton beans that have been registered by
	 * other means than bean definitions.
	 * *******************************************************************
	 * ~$ 返回所有bean的名称定义在这个工厂.
	 * <p>不考虑任何层次本厂可能参与,而忽略任何已登记的单例bean通过其他方式比bean定义.
	 * @return the names of all beans defined in this factory,
	 * or an empty array if none defined
	 */
	String[] getBeanDefinitionNames();
	
	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of <code>getObjectType</code>
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' <code>beanNamesForTypeIncludingAncestors</code>
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>This version of <code>getBeanNamesForType</code> matches all kinds of beans,
	 * be it singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for <code>getBeanNamesOfType(type, true, true)</code>.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * **********************************************************************************
	 * ~$返回bean的名称匹配给定的类型(包括子类),从bean定义或getObjectType在FactoryBeans而言的价值.
	 * <p>注意:这个方法缺省顶级 beans.它不检查嵌套bean可能匹配的指定类型.
	 * <p>并考虑FactoryBeans创建的对象,这意味着FactoryBeans会初始化.
	 *    如果创建的对象FactoryBean不匹配,原始FactoryBean本身将匹配类型.
	 * <p>不考虑任何层次这家工厂可能参与.使用“beanNamesForTypeIncludingAncestors BeanFactoryUtils包括祖先工厂bean.
	 * <p>注意:不能忽略单例bean注册的其他方式比bean定义.
	 * <p>这个版本的getBeanNamesForType匹配各种豆类,无论是单件、原型或FactoryBeans.
	 *    在大多数的实现,结果将是一样getBeanNamesOfType(类型,真的,真的).
	 * <p>通过此方法返回的Bean的名称应该返回Bean的名字的顺序定义在后台配置,尽可能.
	 * @param type the class or interface to match, or <code>null</code> for all bean names
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	String[] getBeanNamesForType(Class<?> type);

	/**
	 * Return the names of beans matching the given type (including subclasses),
	 * judging from either bean definitions or the value of <code>getObjectType</code>
	 * in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' <code>beanNamesForTypeIncludingAncestors</code>
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>Bean names returned by this method should always return bean names <i>in the
	 * order of definition</i> in the backend configuration, as far as possible.
	 * *************************************************************************************
	 * ~$ 返回bean的名称匹配给定的类型(包括子类),从bean定义或getObjectType在FactoryBeans而言的价值.
	 * <p>注意:这个方法缺省顶级 beans.它不检查嵌套bean可能匹配的指定类型.
	 * @param type the class or interface to match, or <code>null</code> for all bean names
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the names of beans (or objects created by FactoryBeans) matching
	 * the given object type (including subclasses), or an empty array if none
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beanNamesForTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	String[] getBeanNamesForType(Class<?> type, boolean includeNonSingletons, boolean allowEagerInit);

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * <code>getObjectType</code> in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' <code>beansOfTypeIncludingAncestors</code>
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>This version of getBeansOfType matches all kinds of beans, be it
	 * singletons, prototypes, or FactoryBeans. In most implementations, the
	 * result will be the same as for <code>getBeansOfType(type, true, true)</code>.
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * @param type the class or interface to match, or <code>null</code> for all concrete beans
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @since 1.1.2
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class)
	 */
	<T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException;

	/**
	 * Return the bean instances that match the given object type (including
	 * subclasses), judging from either bean definitions or the value of
	 * <code>getObjectType</code> in the case of FactoryBeans.
	 * <p><b>NOTE: This method introspects top-level beans only.</b> It does <i>not</i>
	 * check nested beans which might match the specified type as well.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p>Does not consider any hierarchy this factory may participate in.
	 * Use BeanFactoryUtils' <code>beansOfTypeIncludingAncestors</code>
	 * to include beans in ancestor factories too.
	 * <p>Note: Does <i>not</i> ignore singleton beans that have been registered
	 * by other means than bean definitions.
	 * <p>The Map returned by this method should always return bean names and
	 * corresponding bean instances <i>in the order of definition</i> in the
	 * backend configuration, as far as possible.
	 * @param type the class or interface to match, or <code>null</code> for all concrete beans
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 * @see FactoryBean#getObjectType
	 * @see BeanFactoryUtils#beansOfTypeIncludingAncestors(ListableBeanFactory, Class, boolean, boolean)
	 */
	<T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException;

	/**
	 * Find all beans whose <code>Class</code> has the supplied {@link Annotation} type.
	 * @param annotationType the type of annotation to look for
	 * @return a Map with the matching beans, containing the bean names as
	 * keys and the corresponding bean instances as values
	 * @throws BeansException if a bean could not be created
	 */
	Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType)
			throws BeansException;

	/**
	 * Find a {@link Annotation} of <code>annotationType</code> on the specified
	 * bean, traversing its interfaces and super classes if no annotation can be
	 * found on the given class itself.
	 * @param beanName the name of the bean to look for annotations on
	 * @param annotationType the annotation class to look for
	 * @return the annotation of the given type found, or <code>null</code>
	 */
	<A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType);

}
