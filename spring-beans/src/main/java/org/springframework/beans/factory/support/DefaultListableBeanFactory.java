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

package org.springframework.beans.factory.support;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Provider;

import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * 默认的实现
 * {@link org.springframework.beans.factory.ListableBeanFactory} and
 * {@link BeanDefinitionRegistry} interfaces: 一个成熟的bean工厂
 *	基于bean定义对象。
 *
 * <p>典型用法是注册所有bean定义第一(可能是阅读
 *从bean定义文件),之前访问bean。Bean定义查找
 *因此廉价的操作在一个本地bean定义表,
 *操作预构建的bean定义元数据对象。
 *
 * <p>可以作为一个独立的bean工厂使用,或作为超类定义
 * bean工厂。注意,读者对特定bean定义格式
 *通常分别实现而不是bean工厂子类:
 *看到例如{@link PropertiesBeanDefinitionReader }
 * {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader }.
 *
 * <p>另一个实现的
 * {@link org.springframework.beans.factory.ListableBeanFactory }接口,
 *看看{@link StaticListableBeanFactory },管理现有的
 * bean实例,而不是创建新的基于bean定义.
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Sam Brannen
 * @author Costin Leau
 * @since 16 April 2001
 * @see StaticListableBeanFactory
 * @see PropertiesBeanDefinitionReader
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 */
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory
		implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {

	private static Class javaxInjectProviderClass = null;

	static {
		ClassLoader cl = DefaultListableBeanFactory.class.getClassLoader();
		try {
			javaxInjectProviderClass = cl.loadClass("javax.inject.Provider");
		}
		catch (ClassNotFoundException ex) {
			// JSR-330 API not available - Provider interface simply not supported then.
		}
	}


	/** 从序列化id映射到工厂实例 */
	private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories =
			new ConcurrentHashMap<String, Reference<DefaultListableBeanFactory>>();

	/** 可选的id对于这个工厂,用于序列化 */
	private String serializationId;

	/** 是否允许续费的定义具有相同名称的不同 */
	private boolean allowBeanDefinitionOverriding = true;

	/** 是否允许渴望类加载即使lazy-init bean */
	private boolean allowEagerClassLoading = true;

	/** 解析器使用的检查如果bean定义是一个自动装配的候选人 */
	private AutowireCandidateResolver autowireCandidateResolver = new SimpleAutowireCandidateResolver();

	/** Map from dependency type to corresponding autowired value */
	/** 从依赖类型映射到相应的autowired 的值 */
	private final Map<Class, Object> resolvableDependencies = new HashMap<Class, Object>();

	/** Map of bean definition objects, keyed by bean name */
	/** map装的是所有容器的对象, key是bean的名称 */
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

	/** List of bean definition names, in registration order */
	/** 注册的bean定义名称,列表的顺序 */
	private final List<String> beanDefinitionNames = new ArrayList<String>();

	/** Whether bean definition metadata may be cached for all beans */
	/** 是否缓存所有bean的元数据 */
	private boolean configurationFrozen = false;

	/** Cached array of bean definition names in case of frozen configuration */
	/** 缓存所有固定的bean names*/
	private String[] frozenBeanDefinitionNames;


	/**
	 * Create a new DefaultListableBeanFactory.
	 */
	public DefaultListableBeanFactory() {
		super();
	}

	/**
	 * Create a new DefaultListableBeanFactory with the given parent.
	 * @param parentBeanFactory the parent BeanFactory
	 */
	public DefaultListableBeanFactory(BeanFactory parentBeanFactory) {
		super(parentBeanFactory);
	}


	/**
	 * Specify an id for serialization purposes, allowing this BeanFactory to be
	 * deserialized from this id back into the BeanFactory object, if needed.
	 * *********************************************************************
	 * 指定一个id为序列化的目的,允许BeanFactory从这个id回BeanFactory反序列化对象,如果需要的话。
	 */
	public void setSerializationId(String serializationId) {
		if (serializationId != null) {
			serializableFactories.put(serializationId, new WeakReference<DefaultListableBeanFactory>(this));
		}
		else if (this.serializationId != null) {
			serializableFactories.remove(this.serializationId);
		}
		this.serializationId = serializationId;
	}

	/**
	 * Set whether it should be allowed to override bean definitions by registering
	 * a different definition with the same name, automatically replacing the former.
	 * If not, an exception will be thrown. This also applies to overriding aliases.
	 * <p>Default is "true".
	 * *****************************************************************************
	 * 是否应该允许重写bean定义集通过注册 一个具有相同名称的不同的定义,自动替换前. 如果不是,就会抛出一个异常.这也适用于覆盖别名。
	 * @see #registerBeanDefinition
	 */
	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
	}

	/**
	 * Set whether the factory is allowed to eagerly load bean classes
	 * even for bean definitions that are marked as "lazy-init".
	 * <p>Default is "true". Turn this flag off to suppress class loading
	 * for lazy-init beans unless such a bean is explicitly requested.
	 * In particular, by-type lookups will then simply ignore bean definitions
	 * without resolved class name, instead of loading the bean classes on
	 * demand just to perform a type check.
	 * *************************************************************************
	 * 设置是否允许工厂急切地加载bean类 即使对bean定义标记为“lazy-init”。
	 *
	 * 	默认是“true”。关掉这个标志压制类加载 对于lazy-init bean,除非显式地请求这样一个bean。
	 *	特别是,按类型查找会简单地忽略bean定义 没有解决的类名,而不是加载bean类需求执行类型检查。
	 *
	 * @see AbstractBeanDefinition#setLazyInit
	 */
	public void setAllowEagerClassLoading(boolean allowEagerClassLoading) {
		this.allowEagerClassLoading = allowEagerClassLoading;
	}

	/**
	 * Set a custom autowire candidate resolver for this BeanFactory to use
	 * when deciding whether a bean definition should be considered as a
	 * candidate for autowiring.
	 * ******************************************************************
	 * 设置一个自定义自动装配候选人BeanFactory使用解析器 在决定是否应该被视为一个bean定义 自动装配候选人。
	 *
	 */
	public void setAutowireCandidateResolver(final AutowireCandidateResolver autowireCandidateResolver) {
		Assert.notNull(autowireCandidateResolver, "AutowireCandidateResolver must not be null");
		if (autowireCandidateResolver instanceof BeanFactoryAware) {
			if (System.getSecurityManager() != null) {
				final BeanFactory target = this;
				/** AccessController.doPrivileged 意思是这个是特别的,不用做权限检查.  */
				AccessController.doPrivileged(new PrivilegedAction<Object>() {
					public Object run() {
						((BeanFactoryAware) autowireCandidateResolver).setBeanFactory(target);
						return null;
					}
				}, getAccessControlContext());
			}
			else {
				((BeanFactoryAware) autowireCandidateResolver).setBeanFactory(this);
			}
		}
		this.autowireCandidateResolver = autowireCandidateResolver;
	}

	/**
	 * Return the autowire candidate resolver for this BeanFactory (never <code>null</code>).
	 * *************************************************************************************
	 *  返回这个BeanFactory的自动装配候选人解析器(never <code>null</code>)。
	 */
	public AutowireCandidateResolver getAutowireCandidateResolver() {
		return this.autowireCandidateResolver;
	}


	@Override
	public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
		super.copyConfigurationFrom(otherFactory);
		if (otherFactory instanceof DefaultListableBeanFactory) {
			DefaultListableBeanFactory otherListableFactory = (DefaultListableBeanFactory) otherFactory;
			this.allowBeanDefinitionOverriding = otherListableFactory.allowBeanDefinitionOverriding;
			this.allowEagerClassLoading = otherListableFactory.allowEagerClassLoading;
			this.autowireCandidateResolver = otherListableFactory.autowireCandidateResolver;
			this.resolvableDependencies.putAll(otherListableFactory.resolvableDependencies);
		}
	}


	//---------------------------------------------------------------------
	// Implementation of ListableBeanFactory interface
	//---------------------------------------------------------------------

	/** ListableBeanFactory 接口的实现*/

	public <T> T getBean(Class<T> requiredType) throws BeansException {
		Assert.notNull(requiredType, "Required type must not be null");
		String[] beanNames = getBeanNamesForType(requiredType);
		if (beanNames.length > 1) {
			ArrayList<String> autowireCandidates = new ArrayList<String>();
			for (String beanName : beanNames) {
				if (getBeanDefinition(beanName).isAutowireCandidate()) {
					autowireCandidates.add(beanName);
				}
			}
			if (autowireCandidates.size() > 0) {
				beanNames = autowireCandidates.toArray(new String[autowireCandidates.size()]);
			}
		}
		if (beanNames.length == 1) {
			return getBean(beanNames[0], requiredType);
		}
		else if (beanNames.length == 0 && getParentBeanFactory() != null) {
			return getParentBeanFactory().getBean(requiredType);
		}
		else {
			throw new NoSuchBeanDefinitionException(requiredType, "expected single bean but found " +
					beanNames.length + ": " + StringUtils.arrayToCommaDelimitedString(beanNames));
		}
	}

	@Override
	public boolean containsBeanDefinition(String beanName) {
		Assert.notNull(beanName, "Bean name must not be null");
		return this.beanDefinitionMap.containsKey(beanName);
	}

	public int getBeanDefinitionCount() {
		return this.beanDefinitionMap.size();
	}

	public String[] getBeanDefinitionNames() {
		synchronized (this.beanDefinitionMap) {
			if (this.frozenBeanDefinitionNames != null) {
				return this.frozenBeanDefinitionNames;
			}
			else {
				return StringUtils.toStringArray(this.beanDefinitionNames);
			}
		}
	}

	public String[] getBeanNamesForType(Class type) {
		return getBeanNamesForType(type, true, true);
	}

	public String[] getBeanNamesForType(Class type, boolean includeNonSingletons, boolean allowEagerInit) {
		List<String> result = new ArrayList<String>();

		// Check all bean definitions.
		/** 检查所有bean定义*/
		String[] beanDefinitionNames = getBeanDefinitionNames();
		for (String beanName : beanDefinitionNames) {
			// Only consider bean as eligible if the bean name
			/** 只考虑bean作为资格如果bean的名称 */
			// is not defined as alias for some other bean.
			/** 没有定义作为其他bean的别名 */
			if (!isAlias(beanName)) {
				try {
					RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
					// Only check bean definition if it is complete.
					/** 只检查bean定义完成 */
					if (!mbd.isAbstract() && (allowEagerInit ||
							((mbd.hasBeanClass() || !mbd.isLazyInit() || this.allowEagerClassLoading)) &&
									!requiresEagerInitForType(mbd.getFactoryBeanName()))) {
						// In case of FactoryBean, match object created by FactoryBean.
						/** FactoryBean,由FactoryBean匹配对象 */
						boolean isFactoryBean = isFactoryBean(beanName, mbd);
						boolean matchFound = (allowEagerInit || !isFactoryBean || containsSingleton(beanName)) &&
								(includeNonSingletons || isSingleton(beanName)) && isTypeMatch(beanName, type);
						if (!matchFound && isFactoryBean) {
							// In case of FactoryBean, try to match FactoryBean instance itself next.
							/** FactoryBean,尝试匹配FactoryBean实例本身 */
							beanName = FACTORY_BEAN_PREFIX + beanName;
							matchFound = (includeNonSingletons || mbd.isSingleton()) && isTypeMatch(beanName, type);
						}
						if (matchFound) {
							result.add(beanName);
						}
					}
				}
				catch (CannotLoadBeanClassException ex) {
					if (allowEagerInit) {
						throw ex;
					}
					// Probably contains a placeholder: let's ignore it for type matching purposes.
					/** 可能包含一个占位符:让我们忽略它类型匹配的目的*/
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Ignoring bean class loading failure for bean '" + beanName + "'", ex);
					}
					onSuppressedException(ex);
				}
				catch (BeanDefinitionStoreException ex) {
					if (allowEagerInit) {
						throw ex;
					}
					// Probably contains a placeholder: let's ignore it for type matching purposes.
					/** 可能包含一个占位符:让我们忽略它类型匹配的目的*/
					if (this.logger.isDebugEnabled()) {
						this.logger.debug("Ignoring unresolvable metadata in bean definition '" + beanName + "'", ex);
					}
					onSuppressedException(ex);
				}
			}
		}

		// Check singletons too, to catch manually registered singletons.
		/** 检查单件,手动注册单例*/
		String[] singletonNames = getSingletonNames();
		for (String beanName : singletonNames) {
			// Only check if manually registered.
			/** 只检查手动注册*/
			if (!containsBeanDefinition(beanName)) {
				// In case of FactoryBean, match object created by FactoryBean.
				/** FactoryBean,由FactoryBean匹配对象。*/
				if (isFactoryBean(beanName)) {
					if ((includeNonSingletons || isSingleton(beanName)) && isTypeMatch(beanName, type)) {
						result.add(beanName);
						// Match found for this bean: do not match FactoryBean itself anymore.
						/** 为这个bean匹配发现:不匹配FactoryBean本身了*/
						continue;
					}
					// In case of FactoryBean, try to match FactoryBean itself next.
					/** FactoryBean,尝试匹配FactoryBean本身*/
					beanName = FACTORY_BEAN_PREFIX + beanName;
				}
				// Match raw bean instance (might be raw FactoryBean).
				/** 匹配原始bean实例(可能是原始FactoryBean)*/
				if (isTypeMatch(beanName, type)) {
					result.add(beanName);
				}
			}
		}

		return StringUtils.toStringArray(result);
	}

	/**
	 * Check whether the specified bean would need to be eagerly initialized
	 * in order to determine its type.
	 * ********************************************************************
	 * 检查是否指定的bean需要急切地初始化以确定其类型
	 * @param factoryBeanName a factory-bean reference that the bean definition
	 * defines a factory method for
	 * ********************************************************************
	 *                        factory-bean引用bean定义定义了一个工厂方法
	 * @return whether eager initialization is necessary
	 * 			是否渴望初始化是必要的
	 */
	private boolean requiresEagerInitForType(String factoryBeanName) {
		return (factoryBeanName != null && isFactoryBean(factoryBeanName) && !containsSingleton(factoryBeanName));
	}

	public <T> Map<String, T> getBeansOfType(Class<T> type) throws BeansException {
		return getBeansOfType(type, true, true);
	}

	public <T> Map<String, T> getBeansOfType(Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)
			throws BeansException {

		String[] beanNames = getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
		Map<String, T> result = new LinkedHashMap<String, T>(beanNames.length);
		for (String beanName : beanNames) {
			try {
				result.put(beanName, getBean(beanName, type));
			}
			catch (BeanCreationException ex) {
				Throwable rootCause = ex.getMostSpecificCause();
				if (rootCause instanceof BeanCurrentlyInCreationException) {
					BeanCreationException bce = (BeanCreationException) rootCause;
					if (isCurrentlyInCreation(bce.getBeanName())) {
						if (this.logger.isDebugEnabled()) {
							this.logger.debug("Ignoring match to currently created bean '" + beanName + "': " +
									ex.getMessage());
						}
						onSuppressedException(ex);
						// Ignore: indicates a circular reference when autowiring constructors.
						/** 自动装配构造函数时忽略:表示一个循环引用 */
						// We want to find matches other than the currently created bean itself.
						/** 我们想要找到匹配其他比当前创建的bean本身 */
						continue;
					}
				}
				throw ex;
			}
		}
		return result;
	}

	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
		Set<String> beanNames = new LinkedHashSet<String>(getBeanDefinitionCount());
		beanNames.addAll(Arrays.asList(getBeanDefinitionNames()));
		beanNames.addAll(Arrays.asList(getSingletonNames()));
		Map<String, Object> results = new LinkedHashMap<String, Object>();
		for (String beanName : beanNames) {
			if (findAnnotationOnBean(beanName, annotationType) != null) {
				results.put(beanName, getBean(beanName));
			}
		}
		return results;
	}

	/**
	 * Find a {@link Annotation} of <code>annotationType</code> on the specified
	 * bean, traversing its interfaces and super classes if no annotation can be
	 * found on the given class itself, as well as checking its raw bean class
	 * if not found on the exposed bean reference (e.g. in case of a proxy).
	 * *************************************************************************
	 * 找到一个 指定的bean,遍历它的接口和超级类如果没有注释 在给定的类本身,
	 * 以及检查它的原始bean类 如果不是发现在接触bean引用(例如对于代理)。
	 */
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) {
		A ann = null;
		Class beanType = getType(beanName);
		if (beanType != null) {
			ann = AnnotationUtils.findAnnotation(beanType, annotationType);
		}
		if (ann == null && containsBeanDefinition(beanName)) {
			BeanDefinition bd = getMergedBeanDefinition(beanName);
			if (bd instanceof AbstractBeanDefinition) {
				AbstractBeanDefinition abd = (AbstractBeanDefinition) bd;
				if (abd.hasBeanClass()) {
					ann = AnnotationUtils.findAnnotation(abd.getBeanClass(), annotationType);
				}
			}
		}
		return ann;
	}


	//---------------------------------------------------------------------
	// Implementation of ConfigurableListableBeanFactory interface
	//---------------------------------------------------------------------
	/** ConfigurableListableBeanFactory接口的实现 */

	public void registerResolvableDependency(Class<?> dependencyType, Object autowiredValue) {
		Assert.notNull(dependencyType, "Type must not be null");
		if (autowiredValue != null) {
			Assert.isTrue((autowiredValue instanceof ObjectFactory || dependencyType.isInstance(autowiredValue)),
					"Value [" + autowiredValue + "] does not implement specified type [" + dependencyType.getName() + "]");
			this.resolvableDependencies.put(dependencyType, autowiredValue);
		}
	}

	public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor)
			throws NoSuchBeanDefinitionException {

		// Consider FactoryBeans as autowiring candidates.
		/** 考虑到 FactoryBeans 自动装配的最好选择 */
		boolean isFactoryBean = (descriptor != null && descriptor.getDependencyType() != null &&
				FactoryBean.class.isAssignableFrom(descriptor.getDependencyType()));
		if (isFactoryBean) {
			beanName = BeanFactoryUtils.transformedBeanName(beanName);
		}

		if (containsBeanDefinition(beanName)) {
			return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(beanName), descriptor);
		}
		else if (containsSingleton(beanName)) {
			return isAutowireCandidate(beanName, new RootBeanDefinition(getType(beanName)), descriptor);
		}
		else if (getParentBeanFactory() instanceof ConfigurableListableBeanFactory) {
			// No bean definition found in this factory -> delegate to parent.
			/** 在这个工厂没有发现bean定义 -> 委托给 根源 */
			return ((ConfigurableListableBeanFactory) getParentBeanFactory()).isAutowireCandidate(beanName, descriptor);
		}
		else {
			return true;
		}
	}

	/**
	 * Determine whether the specified bean definition qualifies as an autowire candidate,
	 * to be injected into other beans which declare a dependency of matching type.
	 * *******************************************************************************
	 * 确定指定的bean定义满足自动装配的候选人,注入其他bean声明一个依赖的匹配类型。
	 * @param beanName the name of the bean definition to check
	 *					bean的名称定义检查
	 * @param mbd the merged bean definition to check
	 *            合并后的bean定义来检查
	 * @param descriptor the descriptor of the dependency to resolve
	 *                   描述符的依赖关系来解决
	 * @return whether the bean should be considered as autowire candidate
	 * 			bean是否应该被视为自动装配的候选人
	 */
	protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd, DependencyDescriptor descriptor) {
		resolveBeanClass(mbd, beanName);
		if (mbd.isFactoryMethodUnique) {
			boolean resolve;
			synchronized (mbd.constructorArgumentLock) {
				resolve = (mbd.resolvedConstructorOrFactoryMethod == null);
			}
			if (resolve) {
				new ConstructorResolver(this).resolveFactoryMethodIfPossible(mbd);
			}
		}
		return getAutowireCandidateResolver().isAutowireCandidate(
				new BeanDefinitionHolder(mbd, beanName, getAliases(beanName)), descriptor);
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		BeanDefinition bd = this.beanDefinitionMap.get(beanName);
		if (bd == null) {
			if (this.logger.isTraceEnabled()) {
				this.logger.trace("No bean named '" + beanName + "' found in " + this);
			}
			throw new NoSuchBeanDefinitionException(beanName);
		}
		return bd;
	}

	public void freezeConfiguration() {
		this.configurationFrozen = true;
		synchronized (this.beanDefinitionMap) {
			this.frozenBeanDefinitionNames = StringUtils.toStringArray(this.beanDefinitionNames);
		}
	}

	public boolean isConfigurationFrozen() {
		return this.configurationFrozen;
	}

	/**
	 * Considers all beans as eligible for metadata caching
	 * if the factory's configuration has been marked as frozen.
	 * ********************************************************
	 * 考虑所有bean作为元数据缓存的资格,如果工厂的配置已经标记为冻结
	 * @see #freezeConfiguration()
	 */
	@Override
	protected boolean isBeanEligibleForMetadataCaching(String beanName) {
		return (this.configurationFrozen || super.isBeanEligibleForMetadataCaching(beanName));
	}

	public void preInstantiateSingletons() throws BeansException {
		if (this.logger.isInfoEnabled()) {
			this.logger.info("Pre-instantiating singletons in " + this);
		}
		synchronized (this.beanDefinitionMap) {
			// Iterate over a copy to allow for init methods which in turn register new bean definitions.
			/** 遍历一个副本,以便init方法反过来注册新的bean定义。*/
			// While this may not be part of the regular factory bootstrap, it does otherwise work fine.
			/** 虽然这可能不是正规工厂的一部分引导,否则正常工作 */
			List<String> beanNames = new ArrayList<String>(this.beanDefinitionNames);
			for (String beanName : beanNames) {
				RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
				if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
					if (isFactoryBean(beanName)) {
						final FactoryBean factory = (FactoryBean) getBean(FACTORY_BEAN_PREFIX + beanName);
						boolean isEagerInit;
						if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
							isEagerInit = AccessController.doPrivileged(new PrivilegedAction<Boolean>() {
								public Boolean run() {
									return ((SmartFactoryBean) factory).isEagerInit();
								}
							}, getAccessControlContext());
						}
						else {
							isEagerInit = (factory instanceof SmartFactoryBean &&
									((SmartFactoryBean) factory).isEagerInit());
						}
						if (isEagerInit) {
							getBean(beanName);
						}
					}
					else {
						getBean(beanName);
					}
				}
			}
		}
	}


	//---------------------------------------------------------------------
	// Implementation of BeanDefinitionRegistry interface
	//---------------------------------------------------------------------
	/** BeanDefinitionRegistry接口的实现 */
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition)
			throws BeanDefinitionStoreException {

		Assert.hasText(beanName, "Bean name must not be empty");
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");

		if (beanDefinition instanceof AbstractBeanDefinition) {
			try {
				((AbstractBeanDefinition) beanDefinition).validate();
			}
			catch (BeanDefinitionValidationException ex) {
				throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
						"Validation of bean definition failed", ex);
			}
		}

		synchronized (this.beanDefinitionMap) {
			Object oldBeanDefinition = this.beanDefinitionMap.get(beanName);
			if (oldBeanDefinition != null) {
				if (!this.allowBeanDefinitionOverriding) {
					throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,
							"Cannot register bean definition [" + beanDefinition + "] for bean '" + beanName +
							"': There is already [" + oldBeanDefinition + "] bound.");
				}
				else {
					if (this.logger.isInfoEnabled()) {
						this.logger.info("Overriding bean definition for bean '" + beanName +
								"': replacing [" + oldBeanDefinition + "] with [" + beanDefinition + "]");
					}
				}
			}
			else {
				this.beanDefinitionNames.add(beanName);
				this.frozenBeanDefinitionNames = null;
			}
			this.beanDefinitionMap.put(beanName, beanDefinition);

			resetBeanDefinition(beanName);
		}
	}

	public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		Assert.hasText(beanName, "'beanName' must not be empty");

		synchronized (this.beanDefinitionMap) {
			BeanDefinition bd = this.beanDefinitionMap.remove(beanName);
			if (bd == null) {
				if (this.logger.isTraceEnabled()) {
					this.logger.trace("No bean named '" + beanName + "' found in " + this);
				}
				throw new NoSuchBeanDefinitionException(beanName);
			}
			this.beanDefinitionNames.remove(beanName);
			this.frozenBeanDefinitionNames = null;

			resetBeanDefinition(beanName);
		}
	}

	/**
	 * Reset all bean definition caches for the given bean,
	 * including the caches of beans that are derived from it.
	 * *******************************************************
	 * 重置所有bean定义缓存特定bean,包括缓存的bean
	 * @param beanName the name of the bean to reset
	 *                 重置的bean的名称
	 */
	protected void resetBeanDefinition(String beanName) {
		// Remove the merged bean definition for the given bean, if already created.
		/** 删除合并为给定的bean定义bean,如果已经创建 */
		clearMergedBeanDefinition(beanName);

		// Remove corresponding bean from singleton cache, if any. Shouldn't usually
		/** 从单缓存删除相应的bean 通常不应该*/
		// be necessary, rather just meant for overriding a context's default beans
		/** 是必要的,而不是用来覆盖一个上下文的默认bean*/
		// (e.g. the default StaticMessageSource in a StaticApplicationContext).
		/** 默认StaticMessageSource StaticApplicationContext*/
		synchronized (getSingletonMutex()) {
			destroySingleton(beanName);
		}

		// Reset all bean definitions that have the given bean as parent (recursively).
		/** 重置所有bean定义的bean作为父(递归)*/
		for (String bdName : this.beanDefinitionNames) {
			if (!beanName.equals(bdName)) {
				BeanDefinition bd = this.beanDefinitionMap.get(bdName);
				if (beanName.equals(bd.getParentName())) {
					resetBeanDefinition(bdName);
				}
			}
		}
	}

	/**
	 * Only allows alias overriding if bean definition overriding is allowed.
	 */
	/** 只允许别名覆盖如果bean定义覆盖是被允许的 */
	@Override
	protected boolean allowAliasOverriding() {
		return this.allowBeanDefinitionOverriding;
	}


	//---------------------------------------------------------------------
	// Dependency resolution functionality
	//---------------------------------------------------------------------
	/** 依赖性解析功能*/
	public Object resolveDependency(DependencyDescriptor descriptor, String beanName,
			Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException  {

		descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
		if (descriptor.getDependencyType().equals(ObjectFactory.class)) {
			return new DependencyObjectFactory(descriptor, beanName);
		}
		else if (descriptor.getDependencyType().equals(javaxInjectProviderClass)) {
			return new DependencyProviderFactory().createDependencyProvider(descriptor, beanName);
		}
		else {
			return doResolveDependency(descriptor, descriptor.getDependencyType(), beanName, autowiredBeanNames, typeConverter);
		}
	}

	protected Object doResolveDependency(DependencyDescriptor descriptor, Class<?> type, String beanName,
			Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException  {

		Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);
		if (value != null) {
			if (value instanceof String) {
				String strVal = resolveEmbeddedValue((String) value);
				BeanDefinition bd = (beanName != null && containsBean(beanName) ? getMergedBeanDefinition(beanName) : null);
				value = evaluateBeanDefinitionString(strVal, bd);
			}
			TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
			return converter.convertIfNecessary(value, type);
		}

		if (type.isArray()) {
			Class componentType = type.getComponentType();
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, componentType, descriptor);
			if (matchingBeans.isEmpty()) {
				if (descriptor.isRequired()) {
					raiseNoSuchBeanDefinitionException(componentType, "array of " + componentType.getName(), descriptor);
				}
				return null;
			}
			if (autowiredBeanNames != null) {
				autowiredBeanNames.addAll(matchingBeans.keySet());
			}
			TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
			return converter.convertIfNecessary(matchingBeans.values(), type);
		}
		else if (Collection.class.isAssignableFrom(type) && type.isInterface()) {
			Class elementType = descriptor.getCollectionType();
			if (elementType == null) {
				if (descriptor.isRequired()) {
					throw new FatalBeanException("No element type declared for collection [" + type.getName() + "]");
				}
				return null;
			}
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, elementType, descriptor);
			if (matchingBeans.isEmpty()) {
				if (descriptor.isRequired()) {
					raiseNoSuchBeanDefinitionException(elementType, "collection of " + elementType.getName(), descriptor);
				}
				return null;
			}
			if (autowiredBeanNames != null) {
				autowiredBeanNames.addAll(matchingBeans.keySet());
			}
			TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
			return converter.convertIfNecessary(matchingBeans.values(), type);
		}
		else if (Map.class.isAssignableFrom(type) && type.isInterface()) {
			Class keyType = descriptor.getMapKeyType();
			if (keyType == null || !String.class.isAssignableFrom(keyType)) {
				if (descriptor.isRequired()) {
					throw new FatalBeanException("Key type [" + keyType + "] of map [" + type.getName() +
							"] must be assignable to [java.lang.String]");
				}
				return null;
			}
			Class valueType = descriptor.getMapValueType();
			if (valueType == null) {
				if (descriptor.isRequired()) {
					throw new FatalBeanException("No value type declared for map [" + type.getName() + "]");
				}
				return null;
			}
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, valueType, descriptor);
			if (matchingBeans.isEmpty()) {
				if (descriptor.isRequired()) {
					raiseNoSuchBeanDefinitionException(valueType, "map with value type " + valueType.getName(), descriptor);
				}
				return null;
			}
			if (autowiredBeanNames != null) {
				autowiredBeanNames.addAll(matchingBeans.keySet());
			}
			return matchingBeans;
		}
		else {
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
			if (matchingBeans.isEmpty()) {
				if (descriptor.isRequired()) {
					raiseNoSuchBeanDefinitionException(type, "", descriptor);
				}
				return null;
			}
			if (matchingBeans.size() > 1) {
				String primaryBeanName = determinePrimaryCandidate(matchingBeans, descriptor);
				if (primaryBeanName == null) {
					throw new NoSuchBeanDefinitionException(type, "expected single matching bean but found " +
							matchingBeans.size() + ": " + matchingBeans.keySet());
				}
				if (autowiredBeanNames != null) {
					autowiredBeanNames.add(primaryBeanName);
				}
				return matchingBeans.get(primaryBeanName);
			}
			// We have exactly one match.
			Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
			if (autowiredBeanNames != null) {
				autowiredBeanNames.add(entry.getKey());
			}
			return entry.getValue();
		}
	}

	/**
	 * Find bean instances that match the required type.
	 * 		找到相匹配的bean实例所需的类型
	 * Called during autowiring for the specified bean.
	 *		在自动装配呼吁指定的bean
	 * @param beanName the name of the bean that is about to be wired
	 *                 bean 的名字要被连接
	 * @param requiredType the actual type of bean to look for
	 * (may be an array component type or collection element type)
	 *                     实际的类型的bean来寻找
	 * (可能是一个组件类型数组或集合元素类型)
	 * @param descriptor the descriptor of the dependency to resolve
	 *                   描述符的依赖关系来解决
	 * @return a Map of candidate names and candidate instances that match
	 *
	 * the required type (never <code>null</code>)
	 * @throws BeansException in case of errors
	 * @see #autowireByType
	 * @see #autowireConstructor
	 */
	protected Map<String, Object> findAutowireCandidates(
			String beanName, Class requiredType, DependencyDescriptor descriptor) {

		String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(
				this, requiredType, true, descriptor.isEager());
		Map<String, Object> result = new LinkedHashMap<String, Object>(candidateNames.length);
		for (Class autowiringType : this.resolvableDependencies.keySet()) {
			if (autowiringType.isAssignableFrom(requiredType)) {
				Object autowiringValue = this.resolvableDependencies.get(autowiringType);
				autowiringValue = AutowireUtils.resolveAutowiringValue(autowiringValue, requiredType);
				if (requiredType.isInstance(autowiringValue)) {
					result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
					break;
				}
			}
		}
		for (String candidateName : candidateNames) {
			if (!candidateName.equals(beanName) && isAutowireCandidate(candidateName, descriptor)) {
				result.put(candidateName, getBean(candidateName));
			}
		}
		return result;
	}

	/**
	 * Determine the primary autowire candidate in the given set of beans.
	 * ******************************************************************
	 * 在给定的一组确定的主要自动装配候选人bean
	 * @param candidateBeans a Map of candidate names and candidate instances
	 * that match the required type, as returned by {@link #findAutowireCandidates}
	 *          候选人的名字和候选人的Map实例 匹配所需的类型
	 * @param descriptor the target dependency to match against
	 *                   匹配的目标依赖
	 * @return the name of the primary candidate, or <code>null</code> if none found
	 * 		主要候选人的名字
	 */
	protected String determinePrimaryCandidate(Map<String, Object> candidateBeans, DependencyDescriptor descriptor) {
		String primaryBeanName = null;
		String fallbackBeanName = null;
		for (Map.Entry<String, Object> entry : candidateBeans.entrySet()) {
			String candidateBeanName = entry.getKey();
			Object beanInstance = entry.getValue();
			if (isPrimary(candidateBeanName, beanInstance)) {
				if (primaryBeanName != null) {
					boolean candidateLocal = containsBeanDefinition(candidateBeanName);
					boolean primaryLocal = containsBeanDefinition(primaryBeanName);
					if (candidateLocal == primaryLocal) {
						throw new NoSuchBeanDefinitionException(descriptor.getDependencyType(),
								"more than one 'primary' bean found among candidates: " + candidateBeans.keySet());
					}
					else if (candidateLocal && !primaryLocal) {
						primaryBeanName = candidateBeanName;
					}
				}
				else {
					primaryBeanName = candidateBeanName;
				}
			}
			if (primaryBeanName == null &&
					(this.resolvableDependencies.values().contains(beanInstance) ||
							matchesBeanName(candidateBeanName, descriptor.getDependencyName()))) {
				fallbackBeanName = candidateBeanName;
			}
		}
		return (primaryBeanName != null ? primaryBeanName : fallbackBeanName);
	}

	/**
	 * Return whether the bean definition for the given bean name has been
	 * marked as a primary bean.
	 * 		返回的bean定义给定的bean名称是否已被标记为一个主要的bean
	 * @param beanName the name of the bean
	 * @param beanInstance the corresponding bean instance
	 *                     相应的bean实例
	 * @return whether the given bean qualifies as primary
	 * 		给定的bean是否合格为主
	 */
	protected boolean isPrimary(String beanName, Object beanInstance) {
		if (containsBeanDefinition(beanName)) {
			return getMergedLocalBeanDefinition(beanName).isPrimary();
		}
		BeanFactory parentFactory = getParentBeanFactory();
		return (parentFactory instanceof DefaultListableBeanFactory &&
				((DefaultListableBeanFactory) parentFactory).isPrimary(beanName, beanInstance));
	}

	/**
	 * Determine whether the given candidate name matches the bean name or the aliases
	 * stored in this bean definition.
	 * ******************************************************************************
	 * 确定给定的候选人的名字与bean名称或别名匹配存储在这个bean定义
	 */
	protected boolean matchesBeanName(String beanName, String candidateName) {
		return (candidateName != null &&
				(candidateName.equals(beanName) || ObjectUtils.containsElement(getAliases(beanName), candidateName)));
	}

	/**
	 * Raise a NoSuchBeanDefinitionException for an unresolvable dependency.
	 * ********************************************************************
	 * 提高NoSuchBeanDefinitionException不肯舍弃的依赖
	 */
	private void raiseNoSuchBeanDefinitionException(
			Class type, String dependencyDescription, DependencyDescriptor descriptor)
			throws NoSuchBeanDefinitionException {

		throw new NoSuchBeanDefinitionException(type, dependencyDescription,
				"expected at least 1 bean which qualifies as autowire candidate for this dependency. " +
				"Dependency annotations: " + ObjectUtils.nullSafeToString(descriptor.getAnnotations()));
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(ObjectUtils.identityToString(this));
		sb.append(": defining beans [");
		sb.append(StringUtils.arrayToCommaDelimitedString(getBeanDefinitionNames()));
		sb.append("]; ");
		BeanFactory parent = getParentBeanFactory();
		if (parent == null) {
			sb.append("root of factory hierarchy");
		}
		else {
			sb.append("parent: ").append(ObjectUtils.identityToString(parent));
		}
		return sb.toString();
	}


	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------

	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		throw new NotSerializableException("DefaultListableBeanFactory itself is not deserializable - " +
				"just a SerializedBeanFactoryReference is");
	}

	protected Object writeReplace() throws ObjectStreamException {
		if (this.serializationId != null) {
			return new SerializedBeanFactoryReference(this.serializationId);
		}
		else {
			throw new NotSerializableException("DefaultListableBeanFactory has no serialization id");
		}
	}


	/**
	 * Minimal id reference to the factory.
	 * Resolved to the actual factory instance on deserialization.
	 * **********************************************************
	 * 最小id引用到工厂.
	 * 解决实际的工厂实例反序列化.
	 */
	private static class SerializedBeanFactoryReference implements Serializable {

		private final String id;

		public SerializedBeanFactoryReference(String id) {
			this.id = id;
		}

		private Object readResolve() {
			Reference ref = serializableFactories.get(this.id);
			if (ref == null) {
				throw new IllegalStateException(
						"Cannot deserialize BeanFactory with id " + this.id + ": no factory registered for this id");
			}
			Object result = ref.get();
			if (result == null) {
				throw new IllegalStateException(
						"Cannot deserialize BeanFactory with id " + this.id + ": factory has been garbage-collected");
			}
			return result;
		}
	}


	/**
	 * Serializable ObjectFactory for lazy resolution of a dependency.
	 * **************************************************************
	 * Serializable ObjectFactory懒惰解决依赖关系
	 */
	private class DependencyObjectFactory implements ObjectFactory, Serializable {

		private final DependencyDescriptor descriptor;

		private final String beanName;

		private final Class type;

		public DependencyObjectFactory(DependencyDescriptor descriptor, String beanName) {
			this.descriptor = descriptor;
			this.beanName = beanName;
			this.type = determineObjectFactoryType();
		}

		private Class determineObjectFactoryType() {
			Type type = this.descriptor.getGenericDependencyType();
			if (type instanceof ParameterizedType) {
				Type arg = ((ParameterizedType) type).getActualTypeArguments()[0];
				if (arg instanceof Class) {
					return (Class) arg;
				}
			}
			return Object.class;
		}

		public Object getObject() throws BeansException {
			return doResolveDependency(this.descriptor, this.type, this.beanName, null, null);
		}
	}


	/**
	 * Serializable ObjectFactory for lazy resolution of a dependency.
	 * ***************************************************************
	 * Serializable ObjectFactory懒惰解决依赖关系
	 */
	private class DependencyProvider extends DependencyObjectFactory implements Provider {

		public DependencyProvider(DependencyDescriptor descriptor, String beanName) {
			super(descriptor, beanName);
		}

		public Object get() throws BeansException {
			return getObject();
		}
	}


	/**
	 * Separate inner class for avoiding a hard dependency on the <code>javax.inject</code> API.
	 * ****************************************************************************************
	 * 单独的内部类来避免硬依赖
	 */
	private class DependencyProviderFactory {

		public Object createDependencyProvider(DependencyDescriptor descriptor, String beanName) {
			return new DependencyProvider(descriptor, beanName);
		}
	}

}
