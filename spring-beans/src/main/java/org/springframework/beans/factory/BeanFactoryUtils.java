/*
 * Copyright 2002-2010 the original author or authors.
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Convenience methods operating on bean factories, in particular
 * on the {@link ListableBeanFactory} interface.
 *
 * <p>Returns bean counts, bean names or bean instances,
 * taking into account the nesting hierarchy of a bean factory
 * (which the methods defined on the ListableBeanFactory interface don't,
 * in contrast to the methods defined on the BeanFactory interface).
 * **********************************************************************
 * ~$ 便利方法操作bean工厂,尤其是在{@link ListableBeanFactory }接口.
 *
 * <p>返回bean计数,bean名称或bean实例,考虑到bean工厂的嵌套层次结构
 *    (在ListableBeanFactory接口中定义的方法没有,相反在BeanFactory接口定义的方法).
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 04.07.2003
 */
public abstract class BeanFactoryUtils {

	/**
	 * Separator for generated bean names. If a class name or parent name is not
	 * unique, "#1", "#2" etc will be appended, until the name becomes unique.
	 * *************************************************************************
	 * ~$ 分离器为生成的bean的名称.如果一个类名或父母的名字不是独一无二的,
	 *    "#1","#2"等将附加,直到这个名字变得独特.
	 */
	public static final String GENERATED_BEAN_NAME_SEPARATOR = "#";


	/**
	 * Return whether the given name is a factory dereference
	 * (beginning with the factory dereference prefix).
	 * ******************************************************
	 * ~$ 返回给定名称是否工厂废弃 (从工厂废弃前缀).
	 * @param name the name of the bean
	 * @return whether the given name is a factory dereference
	 * @see BeanFactory#FACTORY_BEAN_PREFIX
	 */
	public static boolean isFactoryDereference(String name) {
		return (name != null && name.startsWith(BeanFactory.FACTORY_BEAN_PREFIX));
	}

	/**
	 * Return the actual bean name, stripping out the factory dereference
	 * prefix (if any, also stripping repeated factory prefixes if found).
	 * *******************************************************************
	 * ~$ 返回实际的bean名称,剔除工厂废弃前缀(如果有的话)也剥离如果发现重复工厂前缀).
	 * @param name the name of the bean
	 * @return the transformed name
	 * @see BeanFactory#FACTORY_BEAN_PREFIX
	 */
	public static String transformedBeanName(String name) {
		Assert.notNull(name, "'name' must not be null");
		String beanName = name;
		while (beanName.startsWith(BeanFactory.FACTORY_BEAN_PREFIX)) {
			beanName = beanName.substring(BeanFactory.FACTORY_BEAN_PREFIX.length());
		}
		return beanName;
	}

	/**
	 * Return whether the given name is a bean name which has been generated
	 * by the default naming strategy (containing a "#..." part).
	 * *********************************************************************
	 * ~$ 返回给定的名称是一个bean名称是否已生成的默认命名策略(包含"#..."部分).
	 * @param name the name of the bean
	 *             ~$ bean的名称
	 * @return whether the given name is a generated bean name
	 * ~$ 名字是否生成的bean的名称
	 * @see #GENERATED_BEAN_NAME_SEPARATOR
	 * @see org.springframework.beans.factory.support.BeanDefinitionReaderUtils#generateBeanName
	 * @see org.springframework.beans.factory.support.DefaultBeanNameGenerator
	 */
	public static boolean isGeneratedBeanName(String name) {
		return (name != null && name.contains(GENERATED_BEAN_NAME_SEPARATOR));
	}

	/**
	 * Extract the "raw" bean name from the given (potentially generated) bean name,
	 * excluding any "#..." suffixes which might have been added for uniqueness.
	 * ~$ 提取"raw"bean的名字从给定的(可能)生成bean名称,
	 *    不包括任何"#..."后缀可能添加了独特性。
	 * @param name the potentially generated bean name
	 * @return the raw bean name
	 * @see #GENERATED_BEAN_NAME_SEPARATOR
	 */
	public static String originalBeanName(String name) {
		Assert.notNull(name, "'name' must not be null");
		int separatorIndex = name.indexOf(GENERATED_BEAN_NAME_SEPARATOR);
		return (separatorIndex != -1 ? name.substring(0, separatorIndex) : name);
	}


	/**
	 * Count all beans in any hierarchy in which this factory participates.
	 * Includes counts of ancestor bean factories.
	 * <p>Beans that are "overridden" (specified in a descendant factory
	 * with the same name) are only counted once.
	 * *******************************************************************
	 * ~$ 计算所有bean这个工厂参与的层次结构.包括计数的祖先bean工厂.
	 * <p>"overridden"的bean(具有相同名称的后代中指定工厂)是只统计一次.
	 * @param lbf the bean factory
	 * @return count of beans including those defined in ancestor factories
	 */
	public static int countBeansIncludingAncestors(ListableBeanFactory lbf) {
		return beanNamesIncludingAncestors(lbf).length;
	}
	
	/**
	 * Return all bean names in the factory, including ancestor factories.
	 * *******************************************************************
	 * ~$ 在工厂返回所有bean的名字,包括祖先工厂.
	 * @param lbf the bean factory
	 * @return the array of matching bean names, or an empty array if none
	 * @see #beanNamesForTypeIncludingAncestors
	 */
	public static String[] beanNamesIncludingAncestors(ListableBeanFactory lbf) {
		return beanNamesForTypeIncludingAncestors(lbf, Object.class);
	}


	/**
	 * Get all bean names for the given type, including those defined in ancestor
	 * factories. Will return unique names in case of overridden bean definitions.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>This version of <code>beanNamesForTypeIncludingAncestors</code> automatically
	 * includes prototypes and FactoryBeans.
	 * ********************************************************************************
	 * ~$ 得到所有bean名称对于给定的类型,包括那些在祖先中定义的工厂.将返回唯一名称的覆盖bean定义.
	 * <p>并考虑FactoryBeans创建的对象,这意味着FactoryBeans会初始化.
	 *    如果创建的对象FactoryBean不匹配,原始FactoryBean本身将匹配类型.
	 * <p>这个版本的beanNamesForTypeIncludingAncestors自动包括原型和FactoryBeans.
	 * @param lbf the bean factory
	 * @param type the type that beans must match
	 * @return the array of matching bean names, or an empty array if none
	 */
	public static String[] beanNamesForTypeIncludingAncestors(ListableBeanFactory lbf, Class type) {
		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		String[] result = lbf.getBeanNamesForType(type);
		if (lbf instanceof HierarchicalBeanFactory) {
			HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
			if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
				String[] parentResult = beanNamesForTypeIncludingAncestors(
						(ListableBeanFactory) hbf.getParentBeanFactory(), type);
				List<String> resultList = new ArrayList<String>();
				resultList.addAll(Arrays.asList(result));
				for (String beanName : parentResult) {
					if (!resultList.contains(beanName) && !hbf.containsLocalBean(beanName)) {
						resultList.add(beanName);
					}
				}
				result = StringUtils.toStringArray(resultList);
			}
		}
		return result;
	}

	/**
	 * Get all bean names for the given type, including those defined in ancestor
	 * factories. Will return unique names in case of overridden bean definitions.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit"
	 * flag is set, which means that FactoryBeans will get initialized. If the
	 * object created by the FactoryBean doesn't match, the raw FactoryBean itself
	 * will be matched against the type. If "allowEagerInit" is not set,
	 * only raw FactoryBeans will be checked (which doesn't require initialization
	 * of each FactoryBean).
	 * ****************************************************************************
	 * ~$ 得到所有bean名称对于给定的类型,包括那些在祖先中定义的工厂.将返回唯一名称的覆盖bean定义.
	 * <p>并考虑创建的对象FactoryBeans如果设置了“allowEagerInit”标志,这意味着FactoryBeans会初始化.
	 *    如果创建的对象FactoryBean不匹配,原始FactoryBean本身将匹配类型 .
	 *    如果没有设置“allowEagerInit”,只会检查原始FactoryBeans(不需要初始化每个FactoryBean).
	 *
	 * @param lbf the bean factory
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 *                             ~$ 是否包括原型或作用域的豆子也还是单身(也适用于FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 *  ~$ 是否初始化lazy-init单例对象和对象由FactoryBeans通过工厂方法(或“factory-bean”引用)的类型检查.
	 *    注意FactoryBeans需要急切地初始化以确定其类型:所以请注意,通过在“真正的”这个标志将初始化FactoryBeans和“factory-bean”引用.
	 * @param type the type that beans must match
	 * @return the array of matching bean names, or an empty array if none
	 */
	public static String[] beanNamesForTypeIncludingAncestors(
			ListableBeanFactory lbf, Class type, boolean includeNonSingletons, boolean allowEagerInit) {

		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		String[] result = lbf.getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
		if (lbf instanceof HierarchicalBeanFactory) {
			HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
			if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
				String[] parentResult = beanNamesForTypeIncludingAncestors(
						(ListableBeanFactory) hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
				List<String> resultList = new ArrayList<String>();
				resultList.addAll(Arrays.asList(result));
				for (String beanName : parentResult) {
					if (!resultList.contains(beanName) && !hbf.containsLocalBean(beanName)) {
						resultList.add(beanName);
					}
				}
				result = StringUtils.toStringArray(resultList);
			}
		}
		return result;
	}

	/**
	 * Return all beans of the given type or subtypes, also picking up beans defined in
	 * ancestor bean factories if the current bean factory is a HierarchicalBeanFactory.
	 * The returned Map will only contain beans of this type.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p><b>Note: Beans of the same name will take precedence at the 'lowest' factory level,
	 * i.e. such beans will be returned from the lowest factory that they are being found in,
	 * hiding corresponding beans in ancestor factories.</b> This feature allows for
	 * 'replacing' beans by explicitly choosing the same bean name in a child factory;
	 * the bean in the ancestor factory won't be visible then, not even for by-type lookups.
	 * ***************************************************************************************
	 * ~$ 返回给定类型的所有bean或亚型,还捡bean在祖先中定义bean工厂如果当前是HierarchicalBeanFactory bean工厂.
	 *    返回 Map 只会包含这种类型的bean.
	 * <p>并考虑FactoryBeans创建的对象,这意味着FactoryBeans会初始化.
	 *    如果创建的对象FactoryBean不匹配,原始FactoryBean本身将匹配类型.
	 * <p>注意:bean的名称相同的优先级将在工厂的最低级别,即这些bean将返回从最低的工厂,他们被发现,隐藏相应的祖先工厂bean.
	 *    这个特性允许“取代”bean通过显式地选择相同的bean名称子工厂,祖先的bean工厂不会是可见的,甚至按类型查询.
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @return the Map of matching bean instances, or an empty Map if none
	 * @throws BeansException if a bean could not be created
	 */
	public static <T> Map<String, T> beansOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type)
			throws BeansException {

		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String, T> result = new LinkedHashMap<String, T>(4);
		result.putAll(lbf.getBeansOfType(type));
		if (lbf instanceof HierarchicalBeanFactory) {
			HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
			if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
				Map<String, T> parentResult = beansOfTypeIncludingAncestors(
						(ListableBeanFactory) hbf.getParentBeanFactory(), type);
				for (Map.Entry<String, T> entry : parentResult.entrySet()) {
					String beanName = entry.getKey();
					if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
						result.put(beanName, entry.getValue());
					}
				}
			}
		}
		return result;
	}

	/**
	 * Return all beans of the given type or subtypes, also picking up beans defined in
	 * ancestor bean factories if the current bean factory is a HierarchicalBeanFactory.
	 * The returned Map will only contain beans of this type.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p><b>Note: Beans of the same name will take precedence at the 'lowest' factory level,
	 * i.e. such beans will be returned from the lowest factory that they are being found in,
	 * hiding corresponding beans in ancestor factories.</b> This feature allows for
	 * 'replacing' beans by explicitly choosing the same bean name in a child factory;
	 * the bean in the ancestor factory won't be visible then, not even for by-type lookups.
	 * **************************************************************************************
	 * ~$ 返回给定类型的所有bean或子类型,还捡bean在祖先中定义bean工厂如果当前是HierarchicalBeanFactory bean工厂.
	 *    返回Map只会包含这种类型的bean.
	 * <p>并考虑创建的对象FactoryBeans如果设置了“allowEagerInit”标志,这意味着FactoryBeans会初始化.
	 *    如果创建的对象FactoryBean不匹配,原始FactoryBean本身将匹配类型.
	 *    如果没有设置“allowEagerInit”,只会检查原始FactoryBeans(不需要初始化每个FactoryBean).
	 * <p>注意:bean的名称相同的优先级将在工厂的最低级别,即这些bean将返回从最低的工厂,他们被发现,隐藏相应的祖先工厂bean.
	 *    这个特性允许“取代”bean通过显式地选择相同的bean名称子工厂,祖先的bean工厂不会是可见的,甚至按类型查询.
	 *
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 *                             ~$ 是否包括原型或作用域的beans也还是 singletons (也适用于FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 *                       ~$  是否初始化lazy-init单例对象和对象由FactoryBeans通过工厂方法(或“factory-bean”引用)的类型检查.
	 *                          注意FactoryBeans需要急切地初始化以确定其类型:所以请注意,通过在“真正的”这个标志将初始化FactoryBeans和“factory-bean”引用.
	 * @return the Map of matching bean instances, or an empty Map if none
	 * @throws BeansException if a bean could not be created
	 */
	public static <T> Map<String, T> beansOfTypeIncludingAncestors(
			ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String, T> result = new LinkedHashMap<String, T>(4);
		result.putAll(lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit));
		if (lbf instanceof HierarchicalBeanFactory) {
			HierarchicalBeanFactory hbf = (HierarchicalBeanFactory) lbf;
			if (hbf.getParentBeanFactory() instanceof ListableBeanFactory) {
				Map<String, T> parentResult = beansOfTypeIncludingAncestors(
						(ListableBeanFactory) hbf.getParentBeanFactory(), type, includeNonSingletons, allowEagerInit);
				for (Map.Entry<String, T> entry : parentResult.entrySet()) {
					String beanName = entry.getKey();
					if (!result.containsKey(beanName) && !hbf.containsLocalBean(beanName)) {
						result.put(beanName, entry.getValue());
					}
				}
			}
		}
		return result;
	}


	/**
	 * Return a single bean of the given type or subtypes, also picking up beans
	 * defined in ancestor bean factories if the current bean factory is a
	 * HierarchicalBeanFactory. Useful convenience method when we expect a
	 * single bean and don't care about the bean name.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>This version of <code>beanOfTypeIncludingAncestors</code> automatically includes
	 * prototypes and FactoryBeans.
	 * <p><b>Note: Beans of the same name will take precedence at the 'lowest' factory level,
	 * i.e. such beans will be returned from the lowest factory that they are being found in,
	 * hiding corresponding beans in ancestor factories.</b> This feature allows for
	 * 'replacing' beans by explicitly choosing the same bean name in a child factory;
	 * the bean in the ancestor factory won't be visible then, not even for by-type lookups.
	 * ***************************************************************************************
	 * ~$ 返回一个指定类型的bean或亚型,还捡豆子在祖先中定义bean工厂如果当前是HierarchicalBeanFactory bean工厂.
	 *    有用的便利方法当我们期望一个bean和不关心bean的名称.
	 * <p>并考虑FactoryBeans创建的对象,这意味着FactoryBeans会初始化.
	 *    如果创建的对象FactoryBean不匹配,原始FactoryBean本身将匹配类型.
	 * <p>这个版本的beanOfTypeIncludingAncestors自动包括原型和FactoryBeans.
	 * <p>注意:bean的名称相同的优先级将在工厂的最低级别,即这些bean将返回从最低的工厂,他们被发现,隐藏相应的祖先工厂bean.
	 *    这个特性允许“取代”bean通过显式地选择相同的bean名称子工厂,祖先的bean工厂不会是可见的,甚至按类型查询.
	 *
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @return the matching bean instance
	 * @throws NoSuchBeanDefinitionException
	 * if 0 or more than 1 beans of the given type were found
	 * @throws BeansException if the bean could not be created
	 */
	public static <T> T beanOfTypeIncludingAncestors(ListableBeanFactory lbf, Class<T> type)
			throws BeansException {

		Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type);
		if (beansOfType.size() == 1) {
			return beansOfType.values().iterator().next();
		}
		else {
			throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
		}
	}

	/**
	 * Return a single bean of the given type or subtypes, also picking up beans
	 * defined in ancestor bean factories if the current bean factory is a
	 * HierarchicalBeanFactory. Useful convenience method when we expect a
	 * single bean and don't care about the bean name.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit" flag is set,
	 * which means that FactoryBeans will get initialized. If the object created by the
	 * FactoryBean doesn't match, the raw FactoryBean itself will be matched against the
	 * type. If "allowEagerInit" is not set, only raw FactoryBeans will be checked
	 * (which doesn't require initialization of each FactoryBean).
	 * <p><b>Note: Beans of the same name will take precedence at the 'lowest' factory level,
	 * i.e. such beans will be returned from the lowest factory that they are being found in,
	 * hiding corresponding beans in ancestor factories.</b> This feature allows for
	 * 'replacing' beans by explicitly choosing the same bean name in a child factory;
	 * the bean in the ancestor factory won't be visible then, not even for by-type lookups.
	 * **************************************************************************************
	 * ~$返回一个指定类型的bean或子类型,还捡bean在祖先中定义bean工厂如果当前是HierarchicalBeanFactory bean工厂.
	 *     有用的便利方法当我们期望一个bean和不关心bean的名称.
	 * <p>并考虑创建的对象FactoryBeans如果设置了“allowEagerInit”标志,这意味着FactoryBeans会初始化.
	 *    如果创建的对象FactoryBean不匹配,原始FactoryBean本身将匹配类型.
	 *    如果没有设置“allowEagerInit”,只会检查原始FactoryBeans(不需要初始化每个FactoryBean).
	 * <p>注意:bean的名称相同的优先级将在工厂的最低级别,即这些bean将返回从最低的工厂,他们被发现,隐藏相应的祖先工厂bean.
	 *    这个特性允许“取代”bean通过显式地选择相同的bean名称子工厂,祖先的bean工厂不会是可见的,甚至按类型查询.
	 *
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the matching bean instance
	 * @throws NoSuchBeanDefinitionException
	 * if 0 or more than 1 beans of the given type were found
	 * @throws BeansException if the bean could not be created
	 */
	public static <T> T beanOfTypeIncludingAncestors(
			ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		Map<String, T> beansOfType = beansOfTypeIncludingAncestors(lbf, type, includeNonSingletons, allowEagerInit);
		if (beansOfType.size() == 1) {
			return beansOfType.values().iterator().next();
		}
		else {
			throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
		}
	}

	/**
	 * Return a single bean of the given type or subtypes, not looking in ancestor
	 * factories. Useful convenience method when we expect a single bean and
	 * don't care about the bean name.
	 * <p>Does consider objects created by FactoryBeans, which means that FactoryBeans
	 * will get initialized. If the object created by the FactoryBean doesn't match,
	 * the raw FactoryBean itself will be matched against the type.
	 * <p>This version of <code>beanOfType</code> automatically includes
	 * prototypes and FactoryBeans.
	 * *******************************************************************************
	 * ~$ 返回一个指定类型的bean或亚型,不是在祖先的工厂.有用的便利方法当我们期望一个bean和不关心bean的名称.
	 * <p>并考虑FactoryBeans创建的对象,这意味着FactoryBeans会初始化.
	 *    如果创建的对象FactoryBean不匹配,原始FactoryBean本身将匹配类型.
	 * <p>这个版本的beanOfType自动包括原型和FactoryBeans.
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @return the matching bean instance
	 * @throws NoSuchBeanDefinitionException
	 * if 0 or more than 1 beans of the given type were found
	 * @throws BeansException if the bean could not be created
	 */
	public static <T> T beanOfType(ListableBeanFactory lbf, Class<T> type) throws BeansException {
		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String, T> beansOfType = lbf.getBeansOfType(type);
		if (beansOfType.size() == 1) {
			return beansOfType.values().iterator().next();
		}
		else {
			throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
		}
	}

	/**
	 * Return a single bean of the given type or subtypes, not looking in ancestor
	 * factories. Useful convenience method when we expect a single bean and
	 * don't care about the bean name.
	 * <p>Does consider objects created by FactoryBeans if the "allowEagerInit"
	 * flag is set, which means that FactoryBeans will get initialized. If the
	 * object created by the FactoryBean doesn't match, the raw FactoryBean itself
	 * will be matched against the type. If "allowEagerInit" is not set,
	 * only raw FactoryBeans will be checked (which doesn't require initialization
	 * of each FactoryBean).
	 * ***************************************************************************
	 * ~$ 返回一个指定类型的bean或亚型,不是在祖先的工厂.
	 *    有用的便利方法当我们期望一个bean和不关心bean的名称.
	 * <p>并考虑创建的对象FactoryBeans如果设置了“allowEagerInit”标志,这意味着FactoryBeans会初始化.
	 *    如果创建的对象FactoryBean不匹配,原始FactoryBean本身将匹配类型.
	 *    如果没有设置“allowEagerInit”,只会检查原始FactoryBeans(不需要初始化每个FactoryBean).
	 * @param lbf the bean factory
	 * @param type type of bean to match
	 * @param includeNonSingletons whether to include prototype or scoped beans too
	 * or just singletons (also applies to FactoryBeans)
	 * @param allowEagerInit whether to initialize <i>lazy-init singletons</i> and
	 * <i>objects created by FactoryBeans</i> (or by factory methods with a
	 * "factory-bean" reference) for the type check. Note that FactoryBeans need to be
	 * eagerly initialized to determine their type: So be aware that passing in "true"
	 * for this flag will initialize FactoryBeans and "factory-bean" references.
	 * @return the matching bean instance
	 * @throws NoSuchBeanDefinitionException
	 * if 0 or more than 1 beans of the given type were found
	 * @throws BeansException if the bean could not be created
	 */
	public static <T> T beanOfType(
			ListableBeanFactory lbf, Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		Assert.notNull(lbf, "ListableBeanFactory must not be null");
		Map<String, T> beansOfType = lbf.getBeansOfType(type, includeNonSingletons, allowEagerInit);
		if (beansOfType.size() == 1) {
			return beansOfType.values().iterator().next();
		}
		else {
			throw new NoSuchBeanDefinitionException(type, "expected single bean but found " + beansOfType.size());
		}
	}

}
