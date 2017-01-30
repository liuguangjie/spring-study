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

package org.springframework.beans.factory.annotation;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.MergedBeanDefinitionPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;

/**
 * {@link org.springframework.beans.factory.config.BeanPostProcessor} implementation
 * that autowires annotated fields, setter methods and arbitrary config methods.
 * Such members to be injected are detected through a Java 5 annotation: by default,
 * Spring's {@link Autowired @Autowired} and {@link Value @Value} annotations.
 *
 * <p>Also supports JSR-330's {@link javax.inject.Inject @Inject} annotation,
 * if available, as a direct alternative to Spring's own <code>@Autowired</code>.
 *
 * <p>Only one constructor (at max) of any given bean class may carry this
 * annotation with the 'required' parameter set to <code>true</code>, 
 * indicating <i>the</i> constructor to autowire when used as a Spring bean. 
 * If multiple <i>non-required</i> constructors carry the annotation, they 
 * will be considered as candidates for autowiring. The constructor with 
 * the greatest number of dependencies that can be satisfied by matching
 * beans in the Spring container will be chosen. If none of the candidates
 * can be satisfied, then a default constructor (if present) will be used.
 * An annotated constructor does not have to be public.
 *
 * <p>Fields are injected right after construction of a bean, before any
 * config methods are invoked. Such a config field does not have to be public.
 *
 * <p>Config methods may have an arbitrary name and any number of arguments; each of
 * those arguments will be autowired with a matching bean in the Spring container.
 * Bean property setter methods are effectively just a special case of such a
 * general config method. Config methods do not have to be public.
 *
 * <p>Note: A default AutowiredAnnotationBeanPostProcessor will be registered
 * by the "context:annotation-config" and "context:component-scan" XML tags.
 * Remove or turn off the default annotation configuration there if you intend
 * to specify a custom AutowiredAnnotationBeanPostProcessor bean definition.
 * <p><b>NOTE:</b> Annotation injection will be performed <i>before</i> XML injection;
 * thus the latter configuration will override the former for properties wired through
 * both approaches.
 *
 * ***********************************************************************************************
 * ~$ {@link org.springframework.beans.factory.config.BeanPostProcessor}实现自动装配带注释的字段,
 *    setter方法和任意的配置方法. 这些成员被注入检测通过Java 5注释:默认情况下,
 *    Spring的{@link Autowired @Autowired }和{@link Value @Value }注释.
 *
 * <p>还支持jsr-330's {@link javax.inject.Inject @Inject} 注解,如果可用,直接替代Spring的<code>@Autowired</code>.
 *
 * <p>只有一个构造函数(max)任何给定的bean类可能携带该注释'required'参数设置为true,表示构造函数作为一个Spring bean时自动装配.
 *    如果多个非必需的构造函数进行注释,他们将被视为适合自动装配.
 *    最大的构造函数依赖关系的数量可以满足匹配bean在Spring容器将被选中.
 *    如果没有一个候选人可以满足,那么将使用一个默认的构造函数(如果存在).
 *    一个带注释的构造函数不需要公开.
 *
 * <p>字段注入建设bean之后,任何配置方法之前被调用。这样的配置字段不需要公开.
 *
 * <p>配置方法可能有任意名字和任意数量的参数,这些参数将会与通过名字匹配Spring容器中的bean.
 *    Bean属性setter方法有效的只是这样一个通用配置方法的一个特例. 配置方法不需要公开.
 *
 * <p>注意:默认AutowiredAnnotationBeanPostProcessor将注册"context:annotation-config"和"context:component-scan"XML标记.
 *    删除或关闭默认注释配置如果你意愿指定一个自定义AutowiredAnnotationBeanPostProcessor bean定义.
 * <p>注意:注释注入将XML注入之前执行,因此后者配置将会覆盖前属性连接通过这两种方法.
 * @author Juergen Hoeller
 * @author Mark Fisher
 * @since 2.5
 * @see #setAutowiredAnnotationType
 * @see Autowired
 * @see Value
 */
public class AutowiredAnnotationBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
		implements MergedBeanDefinitionPostProcessor, PriorityOrdered, BeanFactoryAware {

	protected final Log logger = LogFactory.getLog(getClass());

	private final Set<Class<? extends Annotation>> autowiredAnnotationTypes =
			new LinkedHashSet<Class<? extends Annotation>>();
	
	private String requiredParameterName = "required";
	
	private boolean requiredParameterValue = true;

	private int order = Ordered.LOWEST_PRECEDENCE - 2;

	private ConfigurableListableBeanFactory beanFactory;

	private final Map<Class<?>, Constructor<?>[]> candidateConstructorsCache =
			new ConcurrentHashMap<Class<?>, Constructor<?>[]>();

	private final Map<Class<?>, InjectionMetadata> injectionMetadataCache =
			new ConcurrentHashMap<Class<?>, InjectionMetadata>();


	/**
	 * Create a new AutowiredAnnotationBeanPostProcessor
	 * for Spring's standard {@link Autowired} annotation.
	 * <p>Also supports JSR-330's {@link javax.inject.Inject} annotation, if available.
	 * ********************************************************************************
	 * ~$ 创建一个新的AutowiredAnnotationBeanPostProcessor Spring的标准{@link Autowired}注解
	 * <p>还支持 JSR-330's {@link javax.inject.Inject} annotation,如果可用.
	 */
	@SuppressWarnings("unchecked")
	public AutowiredAnnotationBeanPostProcessor() {
		this.autowiredAnnotationTypes.add(Autowired.class);
		this.autowiredAnnotationTypes.add(Value.class);
		ClassLoader cl = AutowiredAnnotationBeanPostProcessor.class.getClassLoader();
		try {
			this.autowiredAnnotationTypes.add((Class<? extends Annotation>) cl.loadClass("javax.inject.Inject"));
			logger.info("JSR-330 'javax.inject.Inject' annotation found and supported for autowiring");
		}
		catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
	}


	/**
	 * Set the 'autowired' annotation type, to be used on constructors, fields,
	 * setter methods and arbitrary config methods.
	 * <p>The default autowired annotation type is the Spring-provided
	 * {@link Autowired} annotation, as well as {@link Value}.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation type to indicate that a member is
	 * supposed to be autowired.
	 * **************************************************************************
	 * ~$ 设置autowired的注释类型,使用构造函数,字段,setter方法和任意的配置方法.
	 * <p>默认autowired的注释类型是spring{@link Autowired },以及 {@link Value}.
	 * <p>setter属性存在,这样开发者就可以提供自己的(non-Spring-specific)注释类型表明应该是autowired的一员.
	 */
	public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationType) {
		Assert.notNull(autowiredAnnotationType, "'autowiredAnnotationType' must not be null");
		this.autowiredAnnotationTypes.clear();
		this.autowiredAnnotationTypes.add(autowiredAnnotationType);
	}

	/**
	 * Set the 'autowired' annotation types, to be used on constructors, fields,
	 * setter methods and arbitrary config methods.
	 * <p>The default autowired annotation type is the Spring-provided
	 * {@link Autowired} annotation, as well as {@link Value}.
	 * <p>This setter property exists so that developers can provide their own
	 * (non-Spring-specific) annotation types to indicate that a member is
	 * supposed to be autowired.
	 * **************************************************************************
	 * ~$ 设置autowired的注释类型,使用构造函数,字段,setter方法和任意的配置方法.
	 * <p>默认autowired的注释类型是spring{@link Autowired },以及 {@link Value}.
	 * <p>setter属性存在,这样开发者就可以提供自己的(non-Spring-specific)注释类型表明应该是autowired的一员.
	 */
	public void setAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
		Assert.notEmpty(autowiredAnnotationTypes, "'autowiredAnnotationTypes' must not be empty");
		this.autowiredAnnotationTypes.clear();
		this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
	}

	/**
	 * Set the name of a parameter of the annotation that specifies
	 * whether it is required.
	 * ************************************************************
	 * ~$ 设置参数的注释指定的名称是否需要它.
	 * @see #setRequiredParameterValue(boolean)
	 */
	public void setRequiredParameterName(String requiredParameterName) {
		this.requiredParameterName = requiredParameterName;
	}

	/**
	 * Set the boolean value that marks a dependency as required 
	 * <p>For example if using 'required=true' (the default), 
	 * this value should be <code>true</code>; but if using 
	 * 'optional=false', this value should be <code>false</code>.
	 * **********************************************************
	 * ~$ 根据需要设置布尔值,标志着依赖例如如果使用'required=true'(默认),
	 *    这个值应该是真的,但如果使用'optional=false',这个值应该是假的.
	 * @see #setRequiredParameterName(String)
	 */
	public void setRequiredParameterValue(boolean requiredParameterValue) {
		this.requiredParameterValue = requiredParameterValue;
	}

	public void setOrder(int order) {
	  this.order = order;
	}

	public int getOrder() {
	  return this.order;
	}

	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		if (!(beanFactory instanceof ConfigurableListableBeanFactory)) {
			throw new IllegalArgumentException(
					"AutowiredAnnotationBeanPostProcessor requires a ConfigurableListableBeanFactory");
		}
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}


	public void postProcessMergedBeanDefinition(RootBeanDefinition beanDefinition, Class<?> beanType, String beanName) {
		if (beanType != null) {
			InjectionMetadata metadata = findAutowiringMetadata(beanType);
			metadata.checkConfigMembers(beanDefinition);
		}
	}

	@Override
	public Constructor<?>[] determineCandidateConstructors(Class<?> beanClass, String beanName) throws BeansException {
		// Quick check on the concurrent map first, with minimal locking.
		/** 快速检查并发映射,以最小的锁定.*/
		Constructor<?>[] candidateConstructors = this.candidateConstructorsCache.get(beanClass);
		if (candidateConstructors == null) {
			synchronized (this.candidateConstructorsCache) {
				candidateConstructors = this.candidateConstructorsCache.get(beanClass);
				if (candidateConstructors == null) {
					Constructor<?>[] rawCandidates = beanClass.getDeclaredConstructors();
					List<Constructor<?>> candidates = new ArrayList<Constructor<?>>(rawCandidates.length);
					Constructor<?> requiredConstructor = null;
					Constructor<?> defaultConstructor = null;
					for (Constructor<?> candidate : rawCandidates) {
						Annotation annotation = findAutowiredAnnotation(candidate);
						if (annotation != null) {
							if (requiredConstructor != null) {
								throw new BeanCreationException("Invalid autowire-marked constructor: " + candidate +
										". Found another constructor with 'required' Autowired annotation: " +
										requiredConstructor);
							}
							if (candidate.getParameterTypes().length == 0) {
								throw new IllegalStateException(
										"Autowired annotation requires at least one argument: " + candidate);
							}
							boolean required = determineRequiredStatus(annotation);
							if (required) {
								if (!candidates.isEmpty()) {
									throw new BeanCreationException(
											"Invalid autowire-marked constructors: " + candidates +
											". Found another constructor with 'required' Autowired annotation: " +
											requiredConstructor);
								}
								requiredConstructor = candidate;
							}
							candidates.add(candidate);
						}
						else if (candidate.getParameterTypes().length == 0) {
							defaultConstructor = candidate;
						}
					}
					if (!candidates.isEmpty()) {
						// Add default constructor to list of optional constructors, as fallback.
						/** 添加默认构造函数的可选列表构造函数,作为候选.*/
						if (requiredConstructor == null && defaultConstructor != null) {
							candidates.add(defaultConstructor);
						}
						candidateConstructors = candidates.toArray(new Constructor[candidates.size()]);
					}
					else {
						candidateConstructors = new Constructor[0];
					}
					this.candidateConstructorsCache.put(beanClass, candidateConstructors);
				}
			}
		}
		return (candidateConstructors.length > 0 ? candidateConstructors : null);
	}

	@Override
	public PropertyValues postProcessPropertyValues(
			PropertyValues pvs, PropertyDescriptor[] pds, Object bean, String beanName) throws BeansException {

		InjectionMetadata metadata = findAutowiringMetadata(bean.getClass());
		try {
			metadata.inject(bean, beanName, pvs);
		}
		catch (Throwable ex) {
			throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
		}
		return pvs;
	}

	/**
	 * 'Native' processing method for direct calls with an arbitrary target instance,
	 * resolving all of its fields and methods which are annotated with <code>@Autowired</code>.
	 * ****************************************************************************************
	 * ~$ 'Native'处理方法直接调用任意目标实例,解决所有的字段和方法与@autowired注解
	 * @param bean the target instance to process
	 * @throws BeansException if autowiring failed
	 */
	public void processInjection(Object bean) throws BeansException {
		Class<?> clazz = bean.getClass();
		InjectionMetadata metadata = findAutowiringMetadata(clazz);
		try {
			metadata.inject(bean, null, null);
		}
		catch (Throwable ex) {
			throw new BeanCreationException("Injection of autowired dependencies failed for class [" + clazz + "]", ex);
		}
	}


	private InjectionMetadata findAutowiringMetadata(Class<?> clazz) {
		// Quick check on the concurrent map first, with minimal locking.
		/** 快速检查并发映射,以最小的锁定*/
		InjectionMetadata metadata = this.injectionMetadataCache.get(clazz);
		if (metadata == null) {
			synchronized (this.injectionMetadataCache) {
				metadata = this.injectionMetadataCache.get(clazz);
				if (metadata == null) {
					metadata = buildAutowiringMetadata(clazz);
					this.injectionMetadataCache.put(clazz, metadata);
				}
			}
		}
		return metadata;
	}

	private InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
		LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
		Class<?> targetClass = clazz;

		do {
			LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();
			for (Field field : targetClass.getDeclaredFields()) {
				Annotation annotation = findAutowiredAnnotation(field);
				if (annotation != null) {
					if (Modifier.isStatic(field.getModifiers())) {
						if (logger.isWarnEnabled()) {
							logger.warn("Autowired annotation is not supported on static fields: " + field);
						}
						continue;
					}
					boolean required = determineRequiredStatus(annotation);
					currElements.add(new AutowiredFieldElement(field, required));
				}
			}
			for (Method method : targetClass.getDeclaredMethods()) {
				Method bridgedMethod = BridgeMethodResolver.findBridgedMethod(method);
				Annotation annotation = BridgeMethodResolver.isVisibilityBridgeMethodPair(method, bridgedMethod) ?
						findAutowiredAnnotation(bridgedMethod) : findAutowiredAnnotation(method);
				if (annotation != null && method.equals(ClassUtils.getMostSpecificMethod(method, clazz))) {
					if (Modifier.isStatic(method.getModifiers())) {
						if (logger.isWarnEnabled()) {
							logger.warn("Autowired annotation is not supported on static methods: " + method);
						}
						continue;
					}
					if (method.getParameterTypes().length == 0) {
						if (logger.isWarnEnabled()) {
							logger.warn("Autowired annotation should be used on methods with actual parameters: " + method);
						}
					}
					boolean required = determineRequiredStatus(annotation);
					PropertyDescriptor pd = BeanUtils.findPropertyForMethod(method);
					currElements.add(new AutowiredMethodElement(method, required, pd));
				}
			}
			elements.addAll(0, currElements);
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);

		return new InjectionMetadata(clazz, elements);
	}

	private Annotation findAutowiredAnnotation(AccessibleObject ao) {
		for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
			Annotation annotation = ao.getAnnotation(type);
			if (annotation != null) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * Obtain all beans of the given type as autowire candidates.
	 * **********************************************************
	 * ~$ 获得所有bean的特定类型自动装配的候选人.
	 * @param type the type of the bean
	 *             bean的类型
	 * @return the target beans, or an empty Collection if no bean of this type is found
	 *         目标bean,或一个空集合如果没有找到这种类型的bean
	 * @throws BeansException if bean retrieval failed
	 */
	protected <T> Map<String, T> findAutowireCandidates(Class<T> type) throws BeansException {
		if (this.beanFactory == null) {
			throw new IllegalStateException("No BeanFactory configured - " +
					"override the getBeanOfType method or specify the 'beanFactory' property");
		}
		return BeanFactoryUtils.beansOfTypeIncludingAncestors(this.beanFactory, type);
	}

	/**
	 * Determine if the annotated field or method requires its dependency.
	 * <p>A 'required' dependency means that autowiring should fail when no beans
	 * are found. Otherwise, the autowiring process will simply bypass the field
	 * or method when no beans are found.
	 * **************************************************************************
	 * ~$ 确定带注释的字段或方法需要依赖.
	 * <p>'required'依赖意味着自动装配应该没有发现bean时失败.
	 *    否则,自动装配过程只会绕过当没有找到bean字段或方法.
	 * @param annotation the Autowired annotation
	 * @return whether the annotation indicates that a dependency is required
	 */
	protected boolean determineRequiredStatus(Annotation annotation) {
		try {
			Method method = ReflectionUtils.findMethod(annotation.annotationType(), this.requiredParameterName);
			return (this.requiredParameterValue == (Boolean) ReflectionUtils.invokeMethod(method, annotation));
		}
		catch (Exception ex) {
			// required by default
			return true;
		}
	}

	/**
	 * Register the specified bean as dependent on the autowired beans.
	 * ****************************************************************
	 * ~$ 指定的bean注册为依赖于autowired的bean.
	 */
	private void registerDependentBeans(String beanName, Set<String> autowiredBeanNames) {
		if (beanName != null) {
			for (String autowiredBeanName : autowiredBeanNames) {
				beanFactory.registerDependentBean(autowiredBeanName, beanName);
				if (logger.isDebugEnabled()) {
					logger.debug(
							"Autowiring by type from bean name '" + beanName + "' to bean named '" + autowiredBeanName +
									"'");
				}
			}
		}
	}

	/**
	 * Resolve the specified cached method argument or field value.
	 * ***********************************************************
	 * ~$ 解决缓存指定方法参数或字段值.
	 */
	private Object resolvedCachedArgument(String beanName, Object cachedArgument) {
		if (cachedArgument instanceof DependencyDescriptor) {
			DependencyDescriptor descriptor = (DependencyDescriptor) cachedArgument;
			TypeConverter typeConverter = beanFactory.getTypeConverter();
			return beanFactory.resolveDependency(descriptor, beanName, null, typeConverter);
		}
		else if (cachedArgument instanceof RuntimeBeanReference) {
			return beanFactory.getBean(((RuntimeBeanReference) cachedArgument).getBeanName());
		}
		else {
			return cachedArgument;
		}
	}


	/**
	 * Class representing injection information about an annotated field.
	 * ******************************************************************
	 * ~$ 类代表注入一个带注释的字段信息.
	 */
	private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {

		private final boolean required;

		private volatile boolean cached = false;

		private volatile Object cachedFieldValue;

		public AutowiredFieldElement(Field field, boolean required) {
			super(field, null);
			this.required = required;
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			Field field = (Field) this.member;
			try {
				Object value;
				if (this.cached) {
					value = resolvedCachedArgument(beanName, this.cachedFieldValue);
				}
				else {
					DependencyDescriptor descriptor = new DependencyDescriptor(field, this.required);
					Set<String> autowiredBeanNames = new LinkedHashSet<String>(1);
					TypeConverter typeConverter = beanFactory.getTypeConverter();
					value = beanFactory.resolveDependency(descriptor, beanName, autowiredBeanNames, typeConverter);
					synchronized (this) {
						if (!this.cached) {
							if (value != null || this.required) {
								this.cachedFieldValue = descriptor;
								registerDependentBeans(beanName, autowiredBeanNames);
								if (autowiredBeanNames.size() == 1) {
									String autowiredBeanName = autowiredBeanNames.iterator().next();
									if (beanFactory.containsBean(autowiredBeanName)) {
										if (beanFactory.isTypeMatch(autowiredBeanName, field.getType())) {
											this.cachedFieldValue = new RuntimeBeanReference(autowiredBeanName);
										}
									}
								}
							}
							else {
								this.cachedFieldValue = null;
							}
							this.cached = true;
						}
					}
				}
				if (value != null) {
					ReflectionUtils.makeAccessible(field);
					field.set(bean, value); //完成 @Autowire 完成赋值
				}
			}
			catch (Throwable ex) {
				throw new BeanCreationException("Could not autowire field: " + field, ex);
			}
		}
	}


	/**
	 * Class representing injection information about an annotated method.
	 * *******************************************************************
	 * ~$ 类代表注入一个带注释的方法的信息.
	 */
	private class AutowiredMethodElement extends InjectionMetadata.InjectedElement {

		private final boolean required;

		private volatile boolean cached = false;

		private volatile Object[] cachedMethodArguments;

		public AutowiredMethodElement(Method method, boolean required, PropertyDescriptor pd) {
			super(method, pd);
			this.required = required;
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			if (checkPropertySkipping(pvs)) {
				return;
			}
			Method method = (Method) this.member;
			try {
				Object[] arguments;
				if (this.cached) {
					// Shortcut for avoiding synchronization...
					/** 快捷方式避免同步...*/
					arguments = resolveCachedArguments(beanName);
				}
				else {
					Class<?>[] paramTypes = method.getParameterTypes();
					arguments = new Object[paramTypes.length];
					DependencyDescriptor[] descriptors = new DependencyDescriptor[paramTypes.length];
					Set<String> autowiredBeanNames = new LinkedHashSet<String>(paramTypes.length);
					TypeConverter typeConverter = beanFactory.getTypeConverter();
					for (int i = 0; i < arguments.length; i++) {
						MethodParameter methodParam = new MethodParameter(method, i);
						GenericTypeResolver.resolveParameterType(methodParam, bean.getClass());
						descriptors[i] = new DependencyDescriptor(methodParam, this.required);
						arguments[i] = beanFactory.resolveDependency(
								descriptors[i], beanName, autowiredBeanNames, typeConverter);
						if (arguments[i] == null && !this.required) {
							arguments = null;
							break;
						}
					}
					synchronized (this) {
						if (!this.cached) {
							if (arguments != null) {
								this.cachedMethodArguments = new Object[arguments.length];
								for (int i = 0; i < arguments.length; i++) {
									this.cachedMethodArguments[i] = descriptors[i];
								}
								registerDependentBeans(beanName, autowiredBeanNames);
								if (autowiredBeanNames.size() == paramTypes.length) {
									Iterator<String> it = autowiredBeanNames.iterator();
									for (int i = 0; i < paramTypes.length; i++) {
										String autowiredBeanName = it.next();
										if (beanFactory.containsBean(autowiredBeanName)) {
											if (beanFactory.isTypeMatch(autowiredBeanName, paramTypes[i])) {
												this.cachedMethodArguments[i] = new RuntimeBeanReference(autowiredBeanName);
											}
										}
									}
								}
							}
							else {
								this.cachedMethodArguments = null;
							}
							this.cached = true;
						}
					}
				}
				if (arguments != null) {
					ReflectionUtils.makeAccessible(method);
					method.invoke(bean, arguments);
				}
			}
			catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
			catch (Throwable ex) {
				throw new BeanCreationException("Could not autowire method: " + method, ex);
			}
		}

		private Object[] resolveCachedArguments(String beanName) {
			if (this.cachedMethodArguments == null) {
				return null;
			}
			Object[] arguments = new Object[this.cachedMethodArguments.length];
			for (int i = 0; i < arguments.length; i++) {
				arguments[i] = resolvedCachedArgument(beanName, this.cachedMethodArguments[i]);
			}
			return arguments;
		}
	}

}
