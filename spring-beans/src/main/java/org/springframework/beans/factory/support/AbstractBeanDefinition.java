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

package org.springframework.beans.factory.support;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Base class for concrete, full-fledged
 * {@link BeanDefinition} classes,
 * factoring out common properties of {@link RootBeanDefinition} and
 * {@link ChildBeanDefinition}.
 * <p>The autowire constants match the ones defined in the
 * {@link AutowireCapableBeanFactory}
 * interface.
 * *****************************************************************
 * 基本类 , 成熟的 {@link BeanDefinition} 类, 分解出公共属性
 * 	{@link RootBeanDefinition} 和  {@link ChildBeanDefinition}.
 * <p>自动装配常数匹配在 {@link AutowireCapableBeanFactory} 接口中定义的
 *
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @author Rob Harrop
 * @author Mark Fisher
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 */
public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor
		implements BeanDefinition, Cloneable {

	/**
	 * Constant for the default scope name: "", equivalent to singleton status
	 * but to be overridden from a parent bean definition (if applicable).
	 * ***********************************************************************
	 * ~$ 常数为默认范围名称:“”相当于单状态,但从父母的bean定义覆盖(如适用)
	 */
	public static final String SCOPE_DEFAULT = "";


	/**
	 * Constant that indicates no autowiring at all.
	 * ~$ 常数表明没有自动装配
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_NO = AutowireCapableBeanFactory.AUTOWIRE_NO;

	/**
	 * Constant that indicates autowiring bean properties by name.
	 * **********************************************************
	 * ~$ 常数表明自动装配bean属性的名字
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

	/**
	 * Constant that indicates autowiring bean properties by type.
	 * **********************************************************
	 * ~$ 常数表明自动装配bean属性的类型
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

	/**
	 * Constant that indicates autowiring a constructor.
	 * *************************************************
	 * ~$ 常数表明自动装配一个构造函数
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

	/**
	 * Constant that indicates determining an appropriate autowire strategy
	 * through introspection of the bean class.
	 * ********************************************************************
	 * ~$ 常数表明确定一个适当的自动装配策略通过内省的bean类
	 * @see #setAutowireMode
	 * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
	 * use annotation-based autowiring for clearer demarcation of autowiring needs.
	 * ***************************************************************************
	 * ~$ 如果您使用的是混合自动装配策略,使用基于注解的自动装配自动装配需求的清晰界定
	 */
	@Deprecated
	public static final int AUTOWIRE_AUTODETECT = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;


	/**
	 * Constant that indicates no dependency check at all.
	 * ***************************************************
	 * ~$ 常数表明任何依赖项检查
	 * @see #setDependencyCheck
	 */
	public static final int DEPENDENCY_CHECK_NONE = 0;

	/**
	 * Constant that indicates dependency checking for object references.
	 * *****************************************************************
	 * ~$ 常数表明依赖检查对象引用
	 * @see #setDependencyCheck
	 */
	public static final int DEPENDENCY_CHECK_OBJECTS = 1;

	/**
	 * Constant that indicates dependency checking for "simple" properties.
	 * *******************************************************************
	 * ~$ 常数表明依赖检查 "simple" 的特性
	 * @see #setDependencyCheck
	 * @see org.springframework.beans.BeanUtils#isSimpleProperty
	 */
	public static final int DEPENDENCY_CHECK_SIMPLE = 2;

	/**
	 * Constant that indicates dependency checking for all properties
	 * (object references as well as "simple" properties).
	 * **************************************************************
	 * ~$ 常数表明依赖检查所有属性 (对象引用包含 "simple" 属性)
	 * @see #setDependencyCheck
	 */
	public static final int DEPENDENCY_CHECK_ALL = 3;

	/**
	 * Constant that indicates the container should attempt to infer the {@link
	 * #setDestroyMethodName destroy method name} for a bean as opposed to explicit
	 * specification of a method name. The value {@value} is specifically designed to
	 * include characters otherwise illegal in a method name, ensuring no possibility of
	 * collisions with a legitimately named methods having the same name.
	 * ********************************************************************************
	 * ~$ 常数表明容器应该试图推断出 {@link #setDestroyMethodName 销毁方法名称}的bean,而不是明确的规范方法名称.
	 * {@value }的值是专门设计用于包括字符否则非法方法名称,确保不可能碰撞的合法拥有相同名称的命名方法
	 */
	public static final String INFER_METHOD = "(inferred)";


	private volatile Object beanClass;

	private String scope = SCOPE_DEFAULT;

	private boolean singleton = true;

	private boolean prototype = false;

	private boolean abstractFlag = false;

	private boolean lazyInit = false;

	private int autowireMode = AUTOWIRE_NO;

	private int dependencyCheck = DEPENDENCY_CHECK_NONE;

	private String[] dependsOn;

	private boolean autowireCandidate = true;

	private boolean primary = false;

	private final Map<String, AutowireCandidateQualifier> qualifiers =
			new LinkedHashMap<String, AutowireCandidateQualifier>(0);

	private boolean nonPublicAccessAllowed = true;

	private boolean lenientConstructorResolution = true;

	private ConstructorArgumentValues constructorArgumentValues;

	private MutablePropertyValues propertyValues;

	private MethodOverrides methodOverrides = new MethodOverrides();

	private String factoryBeanName;

	private String factoryMethodName;

	private String initMethodName;

	private String destroyMethodName;

	private boolean enforceInitMethod = true;

	private boolean enforceDestroyMethod = true;

	private boolean synthetic = false;

	private int role = BeanDefinition.ROLE_APPLICATION;

	private String description;

	private Resource resource;


	/**
	 * Create a new AbstractBeanDefinition with default settings.
	 * **********************************************************
	 * ~$ 创建一个新的AbstractBeanDefinition默认设置
	 */
	protected AbstractBeanDefinition() {
		this(null, null);
	}

	/**
	 * Create a new AbstractBeanDefinition with the given
	 * constructor argument values and property values.
	 * **************************************************
	 * ~$ 创建一个新的AbstractBeanDefinition给定
	 * 	  构造函数参数值和属性值
	 */
	protected AbstractBeanDefinition(ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		setConstructorArgumentValues(cargs);
		setPropertyValues(pvs);
	}

	/**
	 * Create a new AbstractBeanDefinition as deep copy of the given
	 * bean definition.
	 * *************************************************************
	 * ~$ 创建一个新的AbstractBeanDefinition深拷贝的bean定义
	 * @param original the original bean definition to copy from
	 *                 从最初的bean定义复制
	 * @deprecated since Spring 2.5, in favor of {@link #AbstractBeanDefinition(BeanDefinition)}
	 */
	@Deprecated
	protected AbstractBeanDefinition(AbstractBeanDefinition original) {
		this((BeanDefinition) original);
	}

	/**
	 * Create a new AbstractBeanDefinition as deep copy of the given
	 * bean definition.
	 * *************************************************************
	 * ~$ 创建一个新的AbstractBeanDefinition深拷贝的bean定义
	 * @param original the original bean definition to copy from
	 */
	protected AbstractBeanDefinition(BeanDefinition original) {
		setParentName(original.getParentName());
		setBeanClassName(original.getBeanClassName());
		setFactoryBeanName(original.getFactoryBeanName());
		setFactoryMethodName(original.getFactoryMethodName());
		setScope(original.getScope());
		setAbstract(original.isAbstract());
		setLazyInit(original.isLazyInit());
		setRole(original.getRole());
		setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
		setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
		setSource(original.getSource());
		copyAttributesFrom(original);

		if (original instanceof AbstractBeanDefinition) {
			AbstractBeanDefinition originalAbd = (AbstractBeanDefinition) original;
			if (originalAbd.hasBeanClass()) {
				setBeanClass(originalAbd.getBeanClass());
			}
			setAutowireMode(originalAbd.getAutowireMode());
			setDependencyCheck(originalAbd.getDependencyCheck());
			setDependsOn(originalAbd.getDependsOn());
			setAutowireCandidate(originalAbd.isAutowireCandidate());
			copyQualifiersFrom(originalAbd);
			setPrimary(originalAbd.isPrimary());
			setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
			setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
			setInitMethodName(originalAbd.getInitMethodName());
			setEnforceInitMethod(originalAbd.isEnforceInitMethod());
			setDestroyMethodName(originalAbd.getDestroyMethodName());
			setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
			setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
			setSynthetic(originalAbd.isSynthetic());
			setResource(originalAbd.getResource());
		}
		else {
			setResourceDescription(original.getResourceDescription());
		}
	}


	/**
	 * Override settings in this bean definition (assumably a copied parent
	 * from a parent-child inheritance relationship) from the given bean
	 * definition (assumably the child).
	 * ********************************************************************
	 * ~$  覆盖设置在这个bean定义 (多半复制父母的亲子继承关系)
	 *     从给定的bean定义(?)
	 * @deprecated since Spring 2.5, in favor of {@link #overrideFrom(BeanDefinition)}
	 */
	@Deprecated
	public void overrideFrom(AbstractBeanDefinition other) {
		overrideFrom((BeanDefinition) other);
	}

	/**
	 * Override settings in this bean definition (assumably a copied parent
	 * from a parent-child inheritance relationship) from the given bean
	 * definition (assumably the child).
	 * <ul>
	 * <li>Will override beanClass if specified in the given bean definition.
	 * <li>Will always take <code>abstract</code>, <code>scope</code>,
	 * <code>lazyInit</code>, <code>autowireMode</code>, <code>dependencyCheck</code>,
	 * and <code>dependsOn</code> from the given bean definition.
	 * <li>Will add <code>constructorArgumentValues</code>, <code>propertyValues</code>,
	 * <code>methodOverrides</code> from the given bean definition to existing ones.
	 * <li>Will override <code>factoryBeanName</code>, <code>factoryMethodName</code>,
	 * <code>initMethodName</code>, and <code>destroyMethodName</code> if specified
	 * in the given bean definition.
	 * </ul>
	 * **********************************************************************************
	 * ~$ 覆盖设置在这个bean定义(多半复制父母的亲子继承关系)从给定的bean定义(多半孩子)。
	 *    将会覆盖beanClass如果指定的bean定义。
	 *	 总是将抽象、范围lazyInit、autowireMode dependencyCheck,和取决于给定的bean定义.
	 *	 将添加constructorArgumentValues propertyvalue,methodOverrides给现有的bean定义.
	 *	 将会覆盖factoryBeanName factoryMethodName,initMethodName,destroyMethodName如果指定 在给定的bean定义.
	 */
	public void overrideFrom(BeanDefinition other) {
		if (StringUtils.hasLength(other.getBeanClassName())) {
			setBeanClassName(other.getBeanClassName());
		}
		if (StringUtils.hasLength(other.getFactoryBeanName())) {
			setFactoryBeanName(other.getFactoryBeanName());
		}
		if (StringUtils.hasLength(other.getFactoryMethodName())) {
			setFactoryMethodName(other.getFactoryMethodName());
		}
		if (StringUtils.hasLength(other.getScope())) {
			setScope(other.getScope());
		}
		setAbstract(other.isAbstract());
		setLazyInit(other.isLazyInit());
		setRole(other.getRole());
		getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
		getPropertyValues().addPropertyValues(other.getPropertyValues());
		setSource(other.getSource());
		copyAttributesFrom(other);

		if (other instanceof AbstractBeanDefinition) {
			AbstractBeanDefinition otherAbd = (AbstractBeanDefinition) other;
			if (otherAbd.hasBeanClass()) {
				setBeanClass(otherAbd.getBeanClass());
			}
			setAutowireCandidate(otherAbd.isAutowireCandidate());
			setAutowireMode(otherAbd.getAutowireMode());
			copyQualifiersFrom(otherAbd);
			setPrimary(otherAbd.isPrimary());
			setDependencyCheck(otherAbd.getDependencyCheck());
			setDependsOn(otherAbd.getDependsOn());
			setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
			setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
			if (StringUtils.hasLength(otherAbd.getInitMethodName())) {
				setInitMethodName(otherAbd.getInitMethodName());
				setEnforceInitMethod(otherAbd.isEnforceInitMethod());
			}
			if (StringUtils.hasLength(otherAbd.getDestroyMethodName())) {
				setDestroyMethodName(otherAbd.getDestroyMethodName());
				setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
			}
			getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
			setSynthetic(otherAbd.isSynthetic());
			setResource(otherAbd.getResource());
		}
		else {
			setResourceDescription(other.getResourceDescription());
		}
	}

	/**
	 * Apply the provided default values to this bean.
	 * ***********************************************
	 * ~$ 提供的默认值适用于这个bean.
	 * @param defaults the defaults to apply
	 */
	public void applyDefaults(BeanDefinitionDefaults defaults) {
		setLazyInit(defaults.isLazyInit());
		setAutowireMode(defaults.getAutowireMode());
		setDependencyCheck(defaults.getDependencyCheck());
		setInitMethodName(defaults.getInitMethodName());
		setEnforceInitMethod(false);
		setDestroyMethodName(defaults.getDestroyMethodName());
		setEnforceDestroyMethod(false);
	}


	/**
	 * Return whether this definition specifies a bean class.
	 * ******************************************************
	 * ~$ 返回是否该定义指定bean类.
	 */
	public boolean hasBeanClass() {
		return (this.beanClass instanceof Class);
	}

	/**
	 * Specify the class for this bean.
	 * ********************************
	 * ~$ 为这个bean指定的类.
	 */
	public void setBeanClass(Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Return the class of the wrapped bean, if already resolved.
	 * **********************************************************
	 * ~$ 返回包裹bean的类,如果已经解决.
	 * @return the bean class, or <code>null</code> if none defined
	 * @throws IllegalStateException if the bean definition does not define a bean class,
	 * or a specified bean class name has not been resolved into an actual Class
	 */
	public Class<?> getBeanClass() throws IllegalStateException {
		Object beanClassObject = this.beanClass;
		if (beanClassObject == null) {
			throw new IllegalStateException("No bean class specified on bean definition");
		}
		if (!(beanClassObject instanceof Class)) {
			throw new IllegalStateException(
					"Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
		}
		return (Class) beanClassObject;
	}

	public void setBeanClassName(String beanClassName) {
		this.beanClass = beanClassName;
	}

	public String getBeanClassName() {
		Object beanClassObject = this.beanClass;
		if (beanClassObject instanceof Class) {
			return ((Class) beanClassObject).getName();
		}
		else {
			return (String) beanClassObject;
		}
	}

	/**
	 * Determine the class of the wrapped bean, resolving it from a
	 * specified class name if necessary. Will also reload a specified
	 * Class from its name when called with the bean class already resolved.
	 * ***********************************************************************
	 * ~$ 确定包装bean的类,在必要时解决从指定的类名.
	 *   还将重新加载指定的类从它的名字叫bean类已经解决.
	 * @param classLoader the ClassLoader to use for resolving a (potential) class name
	 * @return the resolved bean class
	 * @throws ClassNotFoundException if the class name could be resolved
	 */
	public Class resolveBeanClass(ClassLoader classLoader) throws ClassNotFoundException {
		String className = getBeanClassName();
		if (className == null) {
			return null;
		}
		Class resolvedClass = ClassUtils.forName(className, classLoader);
		this.beanClass = resolvedClass;
		return resolvedClass;
	}


	/**
	 * Set the name of the target scope for the bean.
	 * <p>Default is singleton status, although this is only applied once
	 * a bean definition becomes active in the containing factory. A bean
	 * definition may eventually inherit its scope from a parent bean definitionFor this
	 * reason, the default scope name is empty (empty String), with
	 * singleton status being assumed until a resolved scope will be set.
	 * **********************************************************************************
	 * ~$ 设置的目标范围bean的名称.
	 * <p> 默认是单身状态,虽然这只是一次应用在包含工厂bean定义变得活跃.
	 *     bean定义可能最终继承它的范围从父母豆definitionFor这个原因,
	 *     默认名称是空的(空字符串),范围与独立地位被认为直到解决范围将被设置.
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	public void setScope(String scope) {
		this.scope = scope;
		this.singleton = SCOPE_SINGLETON.equals(scope) || SCOPE_DEFAULT.equals(scope);
		this.prototype = SCOPE_PROTOTYPE.equals(scope);
	}

	/**
	 * Return the name of the target scope for the bean.
	 * ************************************************
	 * ~$  返回的目标范围bean的名称.
	 */
	public String getScope() {
		return this.scope;
	}

	/**
	 * Set if this a <b>Singleton</b>, with a single, shared instance returned
	 * on all calls. In case of "false", the BeanFactory will apply the <b>Prototype</b>
	 * design pattern, with each caller requesting an instance getting an independent
	 * instance. How this is exactly defined will depend on the BeanFactory.
	 * <p>"Singletons" are the commoner type, so the default is "true".
	 * Note that as of Spring 2.0, this flag is just an alternative way to
	 * specify scope="singleton" or scope="prototype".
	 * *********************************************************************
	 * ~$ 如果这一个单例,单身,共享实例返回所有调用.“false”,BeanFactory将原型设计模式,
	 *    每个实例调用请求得到一个独立实例.这就是如何定义将取决于BeanFactory.
	 * <p> "Singletons"平民类型,默认是"true".注意,Spring 2.0,
	 *      这个标志是另一种方式来指定 scope="singleton" 或 scope="prototype".
	 * @deprecated since Spring 2.5, in favor of {@link #setScope}
	 * @see #setScope
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	@Deprecated
	public void setSingleton(boolean singleton) {
		this.scope = (singleton ? SCOPE_SINGLETON : SCOPE_PROTOTYPE);
		this.singleton = singleton;
		this.prototype = !singleton;
	}

	/**
	 * Return whether this a <b>Singleton</b>, with a single shared instance
	 * returned from all calls.
	 * **********************************************************************
	 * ~$ 返回这是否一个单例,从所有调用返回一个共享实例.
	 * @see #SCOPE_SINGLETON
	 */
	public boolean isSingleton() {
		return this.singleton;
	}

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance
	 * returned for each call.
	 * ********************************************************************
	 * ~$ 与一个独立实例返回这是否一个原型,为每个调用返回.
	 * @see #SCOPE_PROTOTYPE
	 */
	public boolean isPrototype() {
		return this.prototype;
	}

	/**
	 * Set if this bean is "abstract", i.e. not meant to be instantiated itself but
	 * rather just serving as parent for concrete child bean definitions.
	 * <p>Default is "false". Specify true to tell the bean factory to not try to
	 * instantiate that particular bean in any case.
	 * ****************************************************************************
	 * ~$ 如果这个bean是"abstract",即不仅仅要实例化本身,而是作为父母混凝土孩子bean定义.
	 */
	public void setAbstract(boolean abstractFlag) {
		this.abstractFlag = abstractFlag;
	}

	/**
	 * Return whether this bean is "abstract", i.e. not meant to be instantiated
	 * itself but rather just serving as parent for concrete child bean definitions.
	 * *****************************************************************************
	 * ~$ 返回这个bean是否“抽象”,即不是为了被实例化本身,而是作为父母混凝土孩子bean定义。
	 */
	public boolean isAbstract() {
		return this.abstractFlag;
	}

	/**
	 * Set whether this bean should be lazily initialized.
	 * <p>If <code>false</code>, the bean will get instantiated on startup by bean
	 * factories that perform eager initialization of singletons.
	 * ****************************************************************************
	 * ~$ 设置这个bean是否应该延迟初始化.如果错误,实例化bean会在启动时由bean工厂执行急切的单例对象的初始化.
	 */
	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	/**
	 * Return whether this bean should be lazily initialized, i.e. not
	 * eagerly instantiated on startup. Only applicable to a singleton bean.
	 * *********************************************************************
	 * ~$ 返回这个bean是否应该延迟初始化,即在启动时不急切地实例化.只适用于一个单例bean.
	 */
	public boolean isLazyInit() {
		return this.lazyInit;
	}


	/**
	 * Set the autowire mode. This determines whether any automagical detection
	 * and setting of bean references will happen. Default is AUTOWIRE_NO,
	 * which means there's no autowire.
	 * *************************************************************************
	 * ~$ 设置自动装配模式。这决定是否automagical检测和设置bean的引用将会发生.
	 *    默认是AUTOWIRE_NO,这意味着没有自动装配.
	 * @param autowireMode the autowire mode to set.
	 * Must be one of the constants defined in this class.
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_AUTODETECT
	 */
	public void setAutowireMode(int autowireMode) {
		this.autowireMode = autowireMode;
	}

	/**
	 * Return the autowire mode as specified in the bean definition.
	 * *************************************************************
	 * ~$ 返回自动装配模式中指定的bean定义。
	 */
	public int getAutowireMode() {
		return this.autowireMode;
	}

	/**
	 * Return the resolved autowire code,
	 * (resolving AUTOWIRE_AUTODETECT to AUTOWIRE_CONSTRUCTOR or AUTOWIRE_BY_TYPE).
	 * *********************************************
	 * ~$ 返回解析后的自动装配代码,
	 * (resolving AUTOWIRE_AUTODETECT to AUTOWIRE_CONSTRUCTOR or AUTOWIRE_BY_TYPE).
	 *
	 * @see #AUTOWIRE_AUTODETECT
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_BY_TYPE
	 */
	public int getResolvedAutowireMode() {
		if (this.autowireMode == AUTOWIRE_AUTODETECT) {
			// Work out whether to apply setter autowiring or constructor autowiring.
			/** 工作是否应用setter自动装配或构造函数自动装配。*/
			// If it has a no-arg constructor it's deemed to be setter autowiring,
			/** 如果它有一个不带参数的构造函数被认为是setter自动装配,*/
			// otherwise we'll try constructor autowiring.
			/** 否则我们将构造函数自动装配.*/
			Constructor[] constructors = getBeanClass().getConstructors();
			for (Constructor constructor : constructors) {
				if (constructor.getParameterTypes().length == 0) {
					return AUTOWIRE_BY_TYPE;
				}
			}
			return AUTOWIRE_CONSTRUCTOR;
		}
		else {
			return this.autowireMode;
		}
	}

	/**
	 * Set the dependency check code.
	 * @param dependencyCheck the code to set.
	 * Must be one of the four constants defined in this class.
	 * @see #DEPENDENCY_CHECK_NONE
	 * @see #DEPENDENCY_CHECK_OBJECTS
	 * @see #DEPENDENCY_CHECK_SIMPLE
	 * @see #DEPENDENCY_CHECK_ALL
	 */
	public void setDependencyCheck(int dependencyCheck) {
		this.dependencyCheck = dependencyCheck;
	}

	/**
	 * Return the dependency check code.
	 */
	public int getDependencyCheck() {
		return this.dependencyCheck;
	}

	/**
	 * Set the names of the beans that this bean depends on being initialized.
	 * The bean factory will guarantee that these beans get initialized first.
	 * <p>Note that dependencies are normally expressed through bean properties or
	 * constructor arguments. This property should just be necessary for other kinds
	 * of dependencies like statics (*ugh*) or database preparation on startup.
	 * *****************************************************************************
	 * ~$ 设置的bean的名称,这个bean取决于被初始化。bean工厂将保证这些bean先初始化.
	 *    注意,依赖关系通常是通过bean属性或构造函数参数表示的.
	 *    这个属性应该是必要的静力学等其他类型的依赖关系(*ugh*)或数据库在启动时做准备.
	 */
	public void setDependsOn(String[] dependsOn) {
		this.dependsOn = dependsOn;
	}

	/**
	 * Return the bean names that this bean depends on.
	 * ***********************************************
	 * ~$ 返回bean的名称取决于这个bean.
	 */
	public String[] getDependsOn() {
		return this.dependsOn;
	}

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * ********************************************************************************
	 * ~$ 设置这个bean是否适合让autowired进入其他bean.
	 */
	public void setAutowireCandidate(boolean autowireCandidate) {
		this.autowireCandidate = autowireCandidate;
	}

	/**
	 * Return whether this bean is a candidate for getting autowired into some other bean.
	 * ************************************************************************************
	 * ~$ 返回这个bean是否适合让autowired进入其他bean.
	 */
	public boolean isAutowireCandidate() {
		return this.autowireCandidate;
	}

	/**
	 * Set whether this bean is a primary autowire candidate.
	 * If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * **********************************************************
	 * ~$ 设置是否这个bean是一个主要的自动装配的候选人.
	 * 如果这个值是适用于一个bean在多个匹配的候选人,它将作为一个平局决胜.
	 */
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	/**
	 * Return whether this bean is a primary autowire candidate.
	 * If this value is true for exactly one bean among multiple
	 * matching candidates, it will serve as a tie-breaker.
	 * *********************************************************
	 * ~$ 返回这个bean是否主要自动装配的候选人.
	 *   如果这个值是适用于一个bean在多个匹配的候选人,它将作为一个平局决胜.
	 */
	public boolean isPrimary() {
		return this.primary;
	}

	/**
	 * Register a qualifier to be used for autowire candidate resolution,
	 * keyed by the qualifier's type name.
	 * ******************************************************************
	 * ~$ 注册一个限定符用于自动装配候选人决议,键控限定符的类型名称.
	 * @see AutowireCandidateQualifier#getTypeName()
	 */
	public void addQualifier(AutowireCandidateQualifier qualifier) {
		this.qualifiers.put(qualifier.getTypeName(), qualifier);
	}

	/**
	 * Return whether this bean has the specified qualifier.
	 * *****************************************************
	 * ~$ 返回这个bean是否指定限定符.
	 */
	public boolean hasQualifier(String typeName) {
		return this.qualifiers.keySet().contains(typeName);
	}

	/**
	 * Return the qualifier mapped to the provided type name.
	 * ******************************************************
	 * ~$ 返回限定符映射到所提供的类型名称.
	 */
	public AutowireCandidateQualifier getQualifier(String typeName) {
		return this.qualifiers.get(typeName);
	}

	/**
	 * Return all registered qualifiers.
	 * *********************************
	 * ~$ 返回所有注册的限定符.
	 * @return the Set of {@link AutowireCandidateQualifier} objects.
	 */
	public Set<AutowireCandidateQualifier> getQualifiers() {
		return new LinkedHashSet<AutowireCandidateQualifier>(this.qualifiers.values());
	}

	/**
	 * Copy the qualifiers from the supplied AbstractBeanDefinition to this bean definition.
	 * *************************************************************************************
	 * ~$ 复制的限定符提供AbstractBeanDefinition这个bean定义.
	 * @param source the AbstractBeanDefinition to copy from
	 */
	public void copyQualifiersFrom(AbstractBeanDefinition source) {
		Assert.notNull(source, "Source must not be null");
		this.qualifiers.putAll(source.qualifiers);
	}


	/**
	 * Specify whether to allow access to non-public constructors and methods,
	 * for the case of externalized metadata pointing to those.
	 * <p>This applies to constructor resolution, factory method resolution,
	 * and also init/destroy methods. Bean property accessors have to be public
	 * in any case and are not affected by this setting.
	 * <p>Note that annotation-driven configuration will still access non-public
	 * members as far as they have been annotated. This setting applies to
	 * externalized metadata in this bean definition only.
	 * **************************************************************************
	 * ~$ 指定是否允许访问非构造函数和方法,指向那些外部化的元数据.
	 * <p>这适用于构造函数分辨率,工厂方法的分辨率,init/destroy 方法.
	 *   Bean属性访问器必须在任何情况下,不受该设置的影响.
	 * <p>注意,注解驱动的配置仍然访问非公有制成员被注释.
	 *   这个设置只适用于这个bean定义中外部化的元数据.
	 */
	public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
		this.nonPublicAccessAllowed = nonPublicAccessAllowed;
	}

	/**
	 * Return whether to allow access to non-public constructors and methods.
	 * **********************************************************************
	 * ~$ 返回是否允许访问非构造函数和方法.
	 */
	public boolean isNonPublicAccessAllowed() {
		return this.nonPublicAccessAllowed;
	}

	/**
	 * Specify whether to resolve constructors in lenient mode (<code>true</code>,
	 * which is the default) or to switch to strict resolution (throwing an exception
	 * in case of ambigious constructors that all match when converting the arguments,
	 * whereas lenient mode would use the one with the 'closest' type matches).
	 * ********************************************************************************
	 * ~$ 指定是否在宽松模式解决构造函数(真的,这是默认的)或切换到严格的决议
	 *  (抛出异常的ambigious所有匹配的构造函数,当转换参数,而宽松模式将使用'closest'的类型匹配)。
	 */
	public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
		this.lenientConstructorResolution = lenientConstructorResolution;
	}

	/**
	 * Return whether to resolve constructors in lenient mode or in strict mode.
	 * *************************************************************************
	 * ~$ 返回是否在宽容解决构造函数模式或在严格模式.
	 */
	public boolean isLenientConstructorResolution() {
		return this.lenientConstructorResolution;
	}

	/**
	 * Specify constructor argument values for this bean.
	 * **************************************************
	 * ~$ 为这个bean指定构造函数参数值.
	 */
	public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
		this.constructorArgumentValues =
				(constructorArgumentValues != null ? constructorArgumentValues : new ConstructorArgumentValues());
	}

	/**
	 * Return constructor argument values for this bean (never <code>null</code>).
	 * ***************************************************************************
	 * ~$ 为这个bean构造函数参数返回值(没有null).
	 */
	public ConstructorArgumentValues getConstructorArgumentValues() {
		return this.constructorArgumentValues;
	}

	/**
	 * Return if there are constructor argument values defined for this bean.
	 * **********************************************************************
	 * ~$ 如果有构造函数参数返回值为这个bean定义。
	 */
	public boolean hasConstructorArgumentValues() {
		return !this.constructorArgumentValues.isEmpty();
	}

	/**
	 * Specify property values for this bean, if any.
	 * **********************************************
	 * ~$ 为这个bean指定属性值,如果可以.
	 */
	public void setPropertyValues(MutablePropertyValues propertyValues) {
		this.propertyValues = (propertyValues != null ? propertyValues : new MutablePropertyValues());
	}

	/**
	 * Return property values for this bean (never <code>null</code>).
	 * ***************************************************************
	 * ~$ 为这个bean返回属性值(没有null).
	 */
	public MutablePropertyValues getPropertyValues() {
		return this.propertyValues;
	}

	/**
	 * Specify method overrides for the bean, if any.
	 * ***********************************************
	 * ~$  指定bean方法覆盖,如果有.
	 */
	public void setMethodOverrides(MethodOverrides methodOverrides) {
		this.methodOverrides = (methodOverrides != null ? methodOverrides : new MethodOverrides());
	}

	/**
	 * Return information about methods to be overridden by the IoC
	 * container. This will be empty if there are no method overrides.
	 * Never returns null.
	 * ****************************************************************
	 * ~$ 返回的信息覆盖的IoC容器的方法.这将是空如果没有方法覆盖.永远不会返回null.
	 */
	public MethodOverrides getMethodOverrides() {
		return this.methodOverrides;
	}


	public void setFactoryBeanName(String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}

	public String getFactoryBeanName() {
		return this.factoryBeanName;
	}

	public void setFactoryMethodName(String factoryMethodName) {
		this.factoryMethodName = factoryMethodName;
	}

	public String getFactoryMethodName() {
		return this.factoryMethodName;
	}

	/**
	 * Set the name of the initializer method. The default is <code>null</code>
	 * in which case there is no initializer method.
	 * ************************************************************************
	 * ~$ 设置初始化方法的名称.默认是空的在这种情况下没有初始化方法.
	 */
	public void setInitMethodName(String initMethodName) {
		this.initMethodName = initMethodName;
	}

	/**
	 * Return the name of the initializer method.
	 * ******************************************
	 * ~$ 返回初始化方法的名称.
	 */
	public String getInitMethodName() {
		return this.initMethodName;
	}

	/**
	 * Specify whether or not the configured init method is the default.
	 * Default value is <code>false</code>.
	 * ****************************************************************
	 * ~$ 指定是否默认配置的init方法.默认值是错误的.
	 * @see #setInitMethodName
	 */
	public void setEnforceInitMethod(boolean enforceInitMethod) {
		this.enforceInitMethod = enforceInitMethod;
	}

	/**
	 * Indicate whether the configured init method is the default.
	 * ***********************************************************
	 * ~$ 指示是否默认配置的init方法.
	 * @see #getInitMethodName()
	 */
	public boolean isEnforceInitMethod() {
		return this.enforceInitMethod;
	}

	/**
	 * Set the name of the destroy method. The default is <code>null</code>
	 * in which case there is no destroy method.
	 * *********************************************************************
	 * ~$ 销毁方法的名称.默认是空的在这种情况下没有销毁方法.
	 */
	public void setDestroyMethodName(String destroyMethodName) {
		this.destroyMethodName = destroyMethodName;
	}

	/**
	 * Return the name of the destroy method.
	 * **************************************
	 * ~$  返回的销毁方法的名称。
	 */
	public String getDestroyMethodName() {
		return this.destroyMethodName;
	}

	/**
	 * Specify whether or not the configured destroy method is the default.
	 * Default value is <code>false</code>.
	 * ********************************************************************
	 * ~$ 指定是否销毁方法是默认配置.默认值是错误的.
	 * @see #setDestroyMethodName
	 */
	public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
		this.enforceDestroyMethod = enforceDestroyMethod;
	}

	/**
	 * Indicate whether the configured destroy method is the default.
	 * **************************************************************
	 * ~$ 指示是否默认配置的销毁方法。
	 * @see #getDestroyMethodName
	 */
	public boolean isEnforceDestroyMethod() {
		return this.enforceDestroyMethod;
	}


	/**
	 * Set whether this bean definition is 'synthetic', that is, not defined
	 * by the application itself (for example, an infrastructure bean such
	 * as a helper for auto-proxying, created through <code>&ltaop:config&gt;</code>).
	 * *******************************************************************************
	 * ~$ 设置这个bean定义是否'synthetic',也就是说,不是由应用程序本身(例如,一个基础设施bean如auto-proxying助手,通过aop:config).
	 */
	public void setSynthetic(boolean synthetic) {
		this.synthetic = synthetic;
	}

	/**
	 * Return whether this bean definition is 'synthetic', that is,
	 * not defined by the application itself.
	 * ************************************************************
	 * ~$ 返回这个bean定义是否'synthetic',也就是说,没有定义的应用程序本身.
	 */
	public boolean isSynthetic() {
		return this.synthetic;
	}

	/**
	 * Set the role hint for this <code>BeanDefinition</code>.
	 * *******************************************************
	 * ~$ 设置这个BeanDefinition提示作用.
	 */
	public void setRole(int role) {
		this.role = role;
	}

	/**
	 * Return the role hint for this <code>BeanDefinition</code>.
	 * **********************************************************
	 * ~$ 这个BeanDefinition返回提示作用
	 */
	public int getRole() {
		return this.role;
	}


	/**
	 * Set a human-readable description of this bean definition.
	 * *********************************************************
	 * ~$ 设置一个人类可读的描述,这个bean定义。
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	public String getDescription() {
		return this.description;
	}

	/**
	 * Set the resource that this bean definition came from
	 * (for the purpose of showing context in case of errors).
	 * *******************************************************
	 * ~$ 设置这个bean定义的资源来自(为了显示上下文的错误).
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
	}

	/**
	 * Return the resource that this bean definition came from.
	 * ********************************************************
	 * ~$ 返回的资源来自这个bean定义.
	 */
	public Resource getResource() {
		return this.resource;
	}

	/**
	 * Set a description of the resource that this bean definition
	 * came from (for the purpose of showing context in case of errors).
	 * *****************************************************************
	 * ~$ 设置资源的描述,这个bean定义来自(为了显示上下文的错误).
	 */
	public void setResourceDescription(String resourceDescription) {
		this.resource = new DescriptiveResource(resourceDescription);
	}

	public String getResourceDescription() {
		return (this.resource != null ? this.resource.getDescription() : null);
	}

	/**
	 * Set the originating (e.g. decorated) BeanDefinition, if any.
	 * *************************************************************
	 * ~$ 设置原始(如装饰)BeanDefinition,如果有.
	 */
	public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
		this.resource = new BeanDefinitionResource(originatingBd);
	}

	public BeanDefinition getOriginatingBeanDefinition() {
		return (this.resource instanceof BeanDefinitionResource ?
				((BeanDefinitionResource) this.resource).getBeanDefinition() : null);
	}

	/**
	 * Validate this bean definition.
	 * @throws BeanDefinitionValidationException in case of validation failure
	 * ***********************************************************************
	 * ~$ 验证这个bean定义.@throws BeanDefinitionValidationException验证失败
	 */
	public void validate() throws BeanDefinitionValidationException {
		if (!getMethodOverrides().isEmpty() && getFactoryMethodName() != null) {
			throw new BeanDefinitionValidationException(
					"Cannot combine static factory method with method overrides: " +
					"the static factory method must create the instance");
		}

		if (hasBeanClass()) {
			prepareMethodOverrides();
		}
	}

	/**
	 * Validate and prepare the method overrides defined for this bean.
	 * Checks for existence of a method with the specified name.
	 * @throws BeanDefinitionValidationException in case of validation failure
	 * *************************************************************************
	 * ~$ 验证和准备为这个bean定义覆盖的方法.检查是否存在与指定名称的方法
	 *   @throws BeanDefinitionValidationException 验证失败
	 */
	public void prepareMethodOverrides() throws BeanDefinitionValidationException {
		// Check that lookup methods exists.
		/** 检查,查找方法存在.*/
		MethodOverrides methodOverrides = getMethodOverrides();
		if (!methodOverrides.isEmpty()) {
			for (MethodOverride mo : methodOverrides.getOverrides()) {
				prepareMethodOverride(mo);
			}
		}
	}

	/**
	 * Validate and prepare the given method override.
	 * Checks for existence of a method with the specified name,
	 * marking it as not overloaded if none found.
	 * *************************************************************
	 * ~$ 验证和准备覆盖给定的方法.检查存在与指定名称的方法,将它标记为不超载如果没有发现.
	 * @param mo the MethodOverride object to validate
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
		int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
		if (count == 0) {
			throw new BeanDefinitionValidationException(
					"Invalid method override: no method with name '" + mo.getMethodName() +
					"' on class [" + getBeanClassName() + "]");
		}
		else if (count == 1) {
			// Mark override as not overloaded, to avoid the overhead of arg type checking.
			/** 覆盖标记为不超载,避免参数类型检查的开销.*/
			mo.setOverloaded(false);
		}
	}


	/**
	 * Public declaration of Object's <code>clone()</code> method.
	 * Delegates to {@link #cloneBeanDefinition()}.
	 * ***********************************************************
	 * ~$ 公开声明对象的克隆()方法。代表{ @link # cloneBeanDefinition()}.
	 * @see Object#clone()
	 */
	@Override
	public Object clone() {
		return cloneBeanDefinition();
	}

	/**
	 * Clone this bean definition.
	 * To be implemented by concrete subclasses.
	 * ******************************************
	 * ~$ 克隆这个bean定义.实现由具体的子类.
	 * @return the cloned bean definition object
	 */
	public abstract AbstractBeanDefinition cloneBeanDefinition();


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractBeanDefinition)) {
			return false;
		}

		AbstractBeanDefinition that = (AbstractBeanDefinition) other;

		if (!ObjectUtils.nullSafeEquals(getBeanClassName(), that.getBeanClassName())) return false;
		if (!ObjectUtils.nullSafeEquals(this.scope, that.scope)) return false;
		if (this.abstractFlag != that.abstractFlag) return false;
		if (this.lazyInit != that.lazyInit) return false;

		if (this.autowireMode != that.autowireMode) return false;
		if (this.dependencyCheck != that.dependencyCheck) return false;
		if (!Arrays.equals(this.dependsOn, that.dependsOn)) return false;
		if (this.autowireCandidate != that.autowireCandidate) return false;
		if (!ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers)) return false;
		if (this.primary != that.primary) return false;

		if (this.nonPublicAccessAllowed != that.nonPublicAccessAllowed) return false;
		if (this.lenientConstructorResolution != that.lenientConstructorResolution) return false;
		if (!ObjectUtils.nullSafeEquals(this.constructorArgumentValues, that.constructorArgumentValues)) return false;
		if (!ObjectUtils.nullSafeEquals(this.propertyValues, that.propertyValues)) return false;
		if (!ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides)) return false;

		if (!ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName)) return false;
		if (!ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName)) return false;
		if (!ObjectUtils.nullSafeEquals(this.initMethodName, that.initMethodName)) return false;
		if (this.enforceInitMethod != that.enforceInitMethod) return false;
		if (!ObjectUtils.nullSafeEquals(this.destroyMethodName, that.destroyMethodName)) return false;
		if (this.enforceDestroyMethod != that.enforceDestroyMethod) return false;

		if (this.synthetic != that.synthetic) return false;
		if (this.role != that.role) return false;

		return super.equals(other);
	}

	@Override
	public int hashCode() {
		int hashCode = ObjectUtils.nullSafeHashCode(getBeanClassName());
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.scope);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.propertyValues);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryBeanName);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryMethodName);
		hashCode = 29 * hashCode + super.hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("class [");
		sb.append(getBeanClassName()).append("]");
		sb.append("; scope=").append(this.scope);
		sb.append("; abstract=").append(this.abstractFlag);
		sb.append("; lazyInit=").append(this.lazyInit);
		sb.append("; autowireMode=").append(this.autowireMode);
		sb.append("; dependencyCheck=").append(this.dependencyCheck);
		sb.append("; autowireCandidate=").append(this.autowireCandidate);
		sb.append("; primary=").append(this.primary);
		sb.append("; factoryBeanName=").append(this.factoryBeanName);
		sb.append("; factoryMethodName=").append(this.factoryMethodName);
		sb.append("; initMethodName=").append(this.initMethodName);
		sb.append("; destroyMethodName=").append(this.destroyMethodName);
		if (this.resource != null) {
			sb.append("; defined in ").append(this.resource.getDescription());
		}
		return sb.toString();
	}

}
