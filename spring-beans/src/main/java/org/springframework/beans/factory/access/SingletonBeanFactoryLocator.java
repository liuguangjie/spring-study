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

package org.springframework.beans.factory.access;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

/**
 * <p>Keyed-singleton implementation of {@link BeanFactoryLocator},
 * which accesses shared Spring {@link BeanFactory} instances.</p>
 * ****************************************************************
 * ~$ <p> Keyed-singleton 实现 {@link BeanFactoryLocator} 访问共享
 *        spring  {@link BeanFactory} 实列
 *
 * <p>Please see the warning in BeanFactoryLocator's javadoc about appropriate usage
 * of singleton style BeanFactoryLocator implementations. It is the opinion of the 
 * Spring team that the use of this class and similar classes is unnecessary except
 * (sometimes) for a small amount of glue code. Excessive usage will lead to code
 * that is more tightly coupled, and harder to modify or test.</p>
 * **********************************************************************************
 * ~$ <p> 请参见警告BeanFactoryLocator javadoc是适当地使用单例的风格BeanFactoryLocator实现.
 *    Spring团队的意见,使用这个类的和类似的类是不必要的(有时)除了少量的胶水代码.
 *    过度使用会导致更紧密耦合的代码,和难以修改或测试.
 *
 * <p>In this implementation, a BeanFactory is built up from one or more XML
 * definition file fragments, accessed as resources. The default resource name
 * searched for is 'classpath*:beanRefFactory.xml', with the Spring-standard
 * 'classpath*:' prefix ensuring that if the classpath contains multiple copies
 * of this file (perhaps one in each component jar) they will be combined. To
 * override the default resource name, instead of using the no-arg 
 * {@link #getInstance()} method, use the {@link #getInstance(String selector)}
 * variant, which will treat the 'selector' argument as the resource name to
 * search for.</p>
 * *****************************************************************************
 * ~$ 在这个实现中, BeanFactory是建立从一个或多个XML定义文件片段,作为资源访问.
 *    默认的资源名称搜索是“classpath *:beanRefFactory.xml”, Spring-standard的'classpath*:'
 *    前缀确保如果这个文件的类路径中包含多个copie(也许是一个在每个组件jar)他们将总和.
 *    覆盖默认的资源名称,而不是使用不带参数的{@link #getInstance()}的方法,
 *    使用 {@link #getInstance(String selector)} 变体,这将把'selector'的论点作为资源名称搜索
 *
 * <p>The purpose of this 'outer' BeanFactory is to create and hold a copy of one
 * or more 'inner' BeanFactory or ApplicationContext instances, and allow those
 * to be obtained either directly or via an alias. As such, this class provides
 * both singleton style access to one or more BeanFactories/ApplicationContexts,
 * and also a level of indirection, allowing multiple pieces of code, which are
 * not able to work in a Dependency Injection fashion, to refer to and use the
 * same target BeanFactory/ApplicationContext instance(s), by different names.<p>
 * ******************************************************************************
 * ~$ 这个'outer'BeanFactory的目的是创建并保持一个或多个副本'inner'
 *    BeanFactory或ApplicationContext实例, 让那些获得直接或通过一个别名.
 *	  因此,这个类提供了单例的形式访问一个或多个beanfactory/applicationcontext,
 *    还有一个间接层, 允许多个部分代码,不能够依赖注入的方式工作,
 *    引用和使用相同的目标BeanFactory / ApplicationContext实例(s),不同的名字.
 *
 * <p>Consider an example application scenario:
 *
 * <ul>
 * <li><code>com.mycompany.myapp.util.applicationContext.xml</code> -
 * ApplicationContext definition file which defines beans for 'util' layer.
 * <li><code>com.mycompany.myapp.dataaccess-applicationContext.xml</code> -
 * ApplicationContext definition file which defines beans for 'data access' layer.
 * Depends on the above.
 * <li><code>com.mycompany.myapp.services.applicationContext.xml</code> -
 * ApplicationContext definition file which defines beans for 'services' layer.
 * Depends on the above.
 * </ul>
 *
 * <p>In an ideal scenario, these would be combined to create one ApplicationContext,
 * or created as three hierarchical ApplicationContexts, by one piece of code
 * somewhere at application startup (perhaps a Servlet filter), from which all other
 * code in the application would flow, obtained as beans from the context(s). However
 * when third party code enters into the picture, things can get problematic. If the 
 * third party code needs to create user classes, which should normally be obtained
 * from a Spring BeanFactory/ApplicationContext, but can handle only newInstance()
 * style object creation, then some extra work is required to actually access and 
 * use object from a BeanFactory/ApplicationContext. One solutions is to make the
 * class created by the third party code be just a stub or proxy, which gets the
 * real object from a BeanFactory/ApplicationContext, and delegates to it. However,
 * it is is not normally workable for the stub to create the BeanFactory on each
 * use, as depending on what is inside it, that can be an expensive operation.
 * Additionally, there is a fairly tight coupling between the stub and the name of
 * the definition resource for the BeanFactory/ApplicationContext. This is where
 * SingletonBeanFactoryLocator comes in. The stub can obtain a
 * SingletonBeanFactoryLocator instance, which is effectively a singleton, and
 * ask it for an appropriate BeanFactory. A subsequent invocation (assuming the
 * same class loader is involved) by the stub or another piece of code, will obtain
 * the same instance. The simple aliasing mechanism allows the context to be asked
 * for by a name which is appropriate for (or describes) the user. The deployer can
 * match alias names to actual context names.
 * *******************************************************************************
 * ~$ 考虑一个例子应用场景:
 * <p>在一个理想的场景,这将创建一个ApplicationContext相结合,
 *    或者创建三个层次applicationcontext,
 *    一段代码在应用程序启动时(可能是一个Servlet过滤器),
 *    从应用程序中所有其他代码流,获得从上下文bean(s).
 *    然而,当第三方代码进入画面,事情可能会变得有问题的.
 *    如果第三方代码需要创建用户类,
 *    这通常应该从Spring BeanFactory / ApplicationContext获得,
 *    但只能处理newInstance()样式对象创建,
 *    然后需要一些额外的工作实际上从BeanFactory/ApplicationContext访问和使用对象.
 *    一个解决方案是使类第三方创建的代码只是一个存根或代理,
 *    得到真正的对象从BeanFactory/ApplicationContext,并代表.
 *    然而,是通常并不是可行的存根上创建BeanFactory每次使用,
 *    取决于在里面,这可以是一个昂贵的操作.
 *    此外,有一个相当的存根和名称之间的紧密耦合BeanFactory / ApplicationContext定义资源.
 *    这是SingletonBeanFactoryLocator进来的地方.
 *    存根可以获得一个SingletonBeanFactoryLocator实例,
 *    这实际上是一个单例,为一个适当的BeanFactory问.
 *    随后调用(假设涉及相同的类装入器)通过存根或另一段代码,将获得相同的实例.
 *    简单的别名机制允许上下文来要求一个名字这是适合用户(或描述).
 *    部署人员可以匹配实际的上下文名称别名.
 *
 * <p>Another use of SingletonBeanFactoryLocator, is to demand-load/use one or more
 * BeanFactories/ApplicationContexts. Because the definition can contain one of more
 * BeanFactories/ApplicationContexts, which can be independent or in a hierarchy, if 
 * they are set to lazy-initialize, they will only be created when actually requested
 * for use.
 *
 * <p>Given the above-mentioned three ApplicationContexts, consider the simplest
 * SingletonBeanFactoryLocator usage scenario, where there is only one single
 * ***********************************************************************************
 * ~$ <p>另一个使用SingletonBeanFactoryLocator是demand-load/使用一个或多个beanfactory/applicationcontext.
 * 	  因为更多的beanfactory/applicationcontext的定义可以包含一个,可独立或在一个层次结构,
 * 	  如果他们将lazy-initialize,他们只会被请求时创建的实际使用.
 *    <p>鉴于上述三个applicationcontext,考虑SingletonBeanFactoryLocator最简单的使用场景,其中只有一个单实列
 *
 * <code>beanRefFactory.xml</code> definition file:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 * 
 * &lt;beans>
 * 
 *   &lt;bean id="com.mycompany.myapp"
 *         class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>
 *         &lt;value>com/mycompany/myapp/util/applicationContext.xml&lt;/value>
 *         &lt;value>com/mycompany/myapp/dataaccess/applicationContext.xml&lt;/value>
 *         &lt;value>com/mycompany/myapp/dataaccess/services.xml&lt;/value>
 *       &lt;/list>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * 
 * &lt;/beans>
 * </pre>
 *
 * The client code is as simple as:
 *
 * <pre class="code">
 * BeanFactoryLocator bfl = SingletonBeanFactoryLocator.getInstance();
 * BeanFactoryReference bf = bfl.useBeanFactory("com.mycompany.myapp");
 * // now use some bean from factory 
 * MyClass zed = bf.getFactory().getBean("mybean");
 * </pre>
 *
 * Another relatively simple variation of the <code>beanRefFactory.xml</code> definition file could be:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 * 
 * &lt;beans>
 * 
 *   &lt;bean id="com.mycompany.myapp.util" lazy-init="true"
 *         class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;value>com/mycompany/myapp/util/applicationContext.xml&lt;/value>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * 
 *   &lt;!-- child of above -->
 *   &lt;bean id="com.mycompany.myapp.dataaccess" lazy-init="true"
 *         class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>&lt;value>com/mycompany/myapp/dataaccess/applicationContext.xml&lt;/value>&lt;/list>
 *     &lt;/constructor-arg>
 *     &lt;constructor-arg>
 *       &lt;ref bean="com.mycompany.myapp.util"/>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * 
 *   &lt;!-- child of above -->
 *   &lt;bean id="com.mycompany.myapp.services" lazy-init="true"
 *         class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>&lt;value>com/mycompany/myapp/dataaccess.services.xml&lt;/value>&lt;/value>
 *     &lt;/constructor-arg>
 *     &lt;constructor-arg>
 *       &lt;ref bean="com.mycompany.myapp.dataaccess"/>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * 
 *   &lt;!-- define an alias -->
 *   &lt;bean id="com.mycompany.myapp.mypackage"
 *         class="java.lang.String">
 *     &lt;constructor-arg>
 *       &lt;value>com.mycompany.myapp.services&lt;/value>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * 
 * &lt;/beans>
 * </pre>
 *
 * <p>In this example, there is a hierarchy of three contexts created. The (potential)
 * advantage is that if the lazy flag is set to true, a context will only be created
 * if it's actually used. If there is some code that is only needed some of the time,
 * this mechanism can save some resources. Additionally, an alias to the last context
 * has been created. Aliases allow usage of the idiom where client code asks for a
 * context with an id which represents the package or module the code is in, and the
 * actual definition file(s) for the SingletonBeanFactoryLocator maps that id to
 * a real context id.
 *
 * <p>A final example is more complex, with a <code>beanRefFactory.xml</code> for every module.
 * All the files are automatically combined to create the final definition.
 *
 * ********************************************************************************************
 * ~$ 在这个例子中,有三个层次创建上下文.(潜在的)优点是如果懒加载的标志被设置为true,
 *    才会创建一个上下文的实际使用. 如果有一些代码,只需要一些时间,
 *    这种机制可以节省一些资源.另外,最后一个上下文创建一个别名.
 *    别名允许使用成语的客户机代码要求一个上下文id代表包或模块的代码,
 *    和实际定义文件(s)SingletonBeanFactoryLocator id映射到一个真正的上下文id.
 *
 * <p> 最后一个例子是更复杂的, 每一个beanRefFactory.xml.  所有的文件会自动组合来创建最终的定义
 *
 * <p><code>beanRefFactory.xml</code> file inside jar for util module:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 * 
 * &lt;beans>
 *   &lt;bean id="com.mycompany.myapp.util" lazy-init="true"
 *        class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;value>com/mycompany/myapp/util/applicationContext.xml&lt;/value>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * &lt;/beans>
 * </pre>
 * 
 * <code>beanRefFactory.xml</code> file inside jar for data-access module:<br>
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 * 
 * &lt;beans>
 *   &lt;!-- child of util -->
 *   &lt;bean id="com.mycompany.myapp.dataaccess" lazy-init="true"
 *        class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>&lt;value>com/mycompany/myapp/dataaccess/applicationContext.xml&lt;/value>&lt;/list>
 *     &lt;/constructor-arg>
 *     &lt;constructor-arg>
 *       &lt;ref bean="com.mycompany.myapp.util"/>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * &lt;/beans>
 * </pre>
 * 
 * <code>beanRefFactory.xml</code> file inside jar for services module:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 * 
 * &lt;beans>
 *   &lt;!-- child of data-access -->
 *   &lt;bean id="com.mycompany.myapp.services" lazy-init="true"
 *        class="org.springframework.context.support.ClassPathXmlApplicationContext">
 *     &lt;constructor-arg>
 *       &lt;list>&lt;value>com/mycompany/myapp/dataaccess/services.xml&lt;/value>&lt;/list>
 *     &lt;/constructor-arg>
 *     &lt;constructor-arg>
 *       &lt;ref bean="com.mycompany.myapp.dataaccess"/>
 *     &lt;/constructor-arg>
 *   &lt;/bean>
 * &lt;/beans>
 * </pre>
 * 
 * <code>beanRefFactory.xml</code> file inside jar for mypackage module. This doesn't
 * create any of its own contexts, but allows the other ones to be referred to be
 * a name known to this module:
 *
 * <pre class="code">&lt;?xml version="1.0" encoding="UTF-8"?>
 * &lt;!DOCTYPE beans PUBLIC "-//SPRING//DTD BEAN 2.0//EN" "http://www.springframework.org/dtd/spring-beans-2.0.dtd">
 * 
 * &lt;beans>
 *   &lt;!-- define an alias for "com.mycompany.myapp.services" -->
 *   &lt;alias name="com.mycompany.myapp.services" alias="com.mycompany.myapp.mypackage"/&gt;
 * &lt;/beans>
 * </pre>
 *   
 * @author Colin Sampaleanu
 * @author Juergen Hoeller
 * @see org.springframework.context.access.ContextSingletonBeanFactoryLocator
 * @see org.springframework.context.access.DefaultLocatorFactory
 */
public class SingletonBeanFactoryLocator implements BeanFactoryLocator {

	private static final String DEFAULT_RESOURCE_LOCATION = "classpath*:beanRefFactory.xml";

	protected static final Log logger = LogFactory.getLog(SingletonBeanFactoryLocator.class);

	/** The keyed BeanFactory instances */
	private static final Map<String, BeanFactoryLocator> instances = new HashMap<String, BeanFactoryLocator>();


	/**
	 * Returns an instance which uses the default "classpath*:beanRefFactory.xml",
	 * as the name of the definition file(s). All resources returned by calling the
	 * current thread context ClassLoader's <code>getResources</code> method with
	 * this name will be combined to create a BeanFactory definition set.
	 * ****************************************************************************
	 * ~$ 返回一个实例,使用默认的“classpath *:beanRefFactory.xml”的名称定义文件,
	 *    所有资源通过调用返回当前线程的上下文类加载器的getresource方法具有该名称将结合创建BeanFactory定义集.
	 * @return the corresponding BeanFactoryLocator instance
	 * @throws BeansException in case of factory loading failure
	 */
	public static BeanFactoryLocator getInstance() throws BeansException {
		return getInstance(null);
	}

	/**
	 * Returns an instance which uses the the specified selector, as the name of the
	 * definition file(s). In the case of a name with a Spring 'classpath*:' prefix,
	 * or with no prefix, which is treated the same, the current thread context
	 * ClassLoader's <code>getResources</code> method will be called with this value
	 * to get all resources having that name. These resources will then be combined to
	 * form a definition. In the case where the name uses a Spring 'classpath:' prefix,
	 * or a standard URL prefix, then only one resource file will be loaded as the
	 * definition.
	 * ********************************************************************************
	 * ~$ 使用指定的选择器返回一个实例,当定义文件的名称(s).
	 *    在spring的一个名字“classpath*:”前缀,或者没有前缀,这是相同的,
	 *    当前线程的上下文类加载器的 getresource 方法将调用这个值来获得所有资源的名称.
	 *    这些资源将被组合起来形成一个定义.
	 *    名称的情况下使用Spring的类路径:前缀,或者一个标准URL前缀,
	 *    那么只有一个将加载资源文件的定义.
	 * @param selector the name of the resource(s) which will be read and
	 * combined to form the definition for the BeanFactoryLocator instance.
	 * Any such files must form a valid BeanFactory definition.
	 * @return the corresponding BeanFactoryLocator instance
	 * @throws BeansException in case of factory loading failure
	 */
	public static BeanFactoryLocator getInstance(String selector) throws BeansException {
		String resourceLocation = selector;
		if (resourceLocation == null) {
			resourceLocation = DEFAULT_RESOURCE_LOCATION;
		}

		// For backwards compatibility, we prepend 'classpath*:' to the selector name if there
		/** 向后兼容性,我们预先考虑的classpath *:如果选择器名称 */
		// is no other prefix (i.e. classpath*:, classpath:, or some URL prefix.
		/** 没有其他前缀(即classpath*: 类路径:,或者一些URL前缀.*/
		if (!ResourcePatternUtils.isUrl(resourceLocation)) {
			resourceLocation = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resourceLocation;
		}

		synchronized (instances) {
			if (logger.isTraceEnabled()) {
				logger.trace("SingletonBeanFactoryLocator.getInstance(): instances.hashCode=" +
						instances.hashCode() + ", instances=" + instances);
			}
			BeanFactoryLocator bfl = instances.get(resourceLocation);
			if (bfl == null) {
				bfl = new SingletonBeanFactoryLocator(resourceLocation);
				instances.put(resourceLocation, bfl);
			}
			return bfl;
		}
	}


	// We map BeanFactoryGroup objects by String keys, and by the definition object.
	/** 我们BeanFactoryGroup对象映射的字符串键,通过定义对象.*/
	private final Map<String, BeanFactoryGroup> bfgInstancesByKey = new HashMap<String, BeanFactoryGroup>();

	private final Map<BeanFactory, BeanFactoryGroup> bfgInstancesByObj = new HashMap<BeanFactory, BeanFactoryGroup>();

	private final String resourceLocation;


	/**
	 * Constructor which uses the the specified name as the resource name
	 * of the definition file(s).
	 * *******************************************************************
	 * ~$ 构造函数使用的指定的名称作为资源名称定义文件
	 * @param resourceLocation the Spring resource location to use
	 * (either a URL or a "classpath:" / "classpath*:" pseudo URL)
	 */
	protected SingletonBeanFactoryLocator(String resourceLocation) {
		this.resourceLocation = resourceLocation;
	}

	public BeanFactoryReference useBeanFactory(String factoryKey) throws BeansException {
		synchronized (this.bfgInstancesByKey) {
			BeanFactoryGroup bfg = this.bfgInstancesByKey.get(this.resourceLocation);

			if (bfg != null) {
				bfg.refCount++;
			}
			else {
				// This group definition doesn't exist, we need to try to load it.
				/** 这一组定义不存在,我们需要尝试加载它.*/
				if (logger.isTraceEnabled()) {
					logger.trace("Factory group with resource name [" + this.resourceLocation +
							"] requested. Creating new instance.");
				}
				
				// Create the BeanFactory but don't initialize it.
				/** 创建BeanFactory但不初始化它.*/
				BeanFactory groupContext = createDefinition(this.resourceLocation, factoryKey);

				// Record its existence now, before instantiating any singletons.
				/** 记录它的存在,在实例化之前任何单件.*/
				bfg = new BeanFactoryGroup();
				bfg.definition = groupContext;
				bfg.refCount = 1;
				this.bfgInstancesByKey.put(this.resourceLocation, bfg);
				this.bfgInstancesByObj.put(groupContext, bfg);

				// Now initialize the BeanFactory. This may cause a re-entrant invocation
				/** 现在BeanFactory进行初始化.这可能会导致一个可重入调用*/
				// of this method, but since we've already added the BeanFactory to our
				/** 这种方法的,但由于我们已经添加BeanFactory我们*/
				// mappings, the next time it will be found and simply have its
				/** 映射,下次就会发现,仅仅有它*/
				// reference count incremented.
				/** 引用计数增加。*/
				try {
					initializeDefinition(groupContext);
				}
				catch (BeansException ex) {
					this.bfgInstancesByKey.remove(this.resourceLocation);
					this.bfgInstancesByObj.remove(groupContext);
					throw new BootstrapException("Unable to initialize group definition. " +
							"Group resource name [" + this.resourceLocation + "], factory key [" + factoryKey + "]", ex);
				}
			}

			try {
				BeanFactory beanFactory;
				if (factoryKey != null) {
					beanFactory = bfg.definition.getBean(factoryKey, BeanFactory.class);
				}
				else {
					beanFactory = bfg.definition.getBean(BeanFactory.class);
				}
				return new CountingBeanFactoryReference(beanFactory, bfg.definition);
			}
			catch (BeansException ex) {
				throw new BootstrapException("Unable to return specified BeanFactory instance: factory key [" +
						factoryKey + "], from group with resource name [" + this.resourceLocation + "]", ex);
			}

		}
	}

	/**
	 * Actually creates definition in the form of a BeanFactory, given a resource name
	 * which supports standard Spring resource prefixes ('classpath:', 'classpath*:', etc.)
	 * This is split out as a separate method so that subclasses can override the actual
	 * type used (to be an ApplicationContext, for example).
	 * <p>The default implementation simply builds a
	 * {@link DefaultListableBeanFactory}
	 * and populates it using an
	 * {@link XmlBeanDefinitionReader}.
	 * <p>This method should not instantiate any singletons. That function is performed
	 * by {@link #initializeDefinition initializeDefinition()}, which should also be
	 * overridden if this method is.
	 * ****************************************************************************************
	 * ~$ 实际上创建了定义的形式BeanFactory,给定一个资源名支持标准的Spring资源前缀(类路径:,“classpath *:”,等等).
	 *    这是分割出来作为一个单独的方法,子类可以重写使用实际的类型(例如ApplicationContext,).
	 *
	 * <p>默认实现简单地构建一个{@link DefaultListableBeanFactory }
	 *    和填充它使用一个{@link XmlBeanDefinitionReader }.
	 * <p> 这个方法不应该任何单例实例化.这个函数是由{@link #initializeDefinition initializeDefinition()},
	 * 这也应该重写此方法。
	 * @param resourceLocation the resource location for this factory group
	 * @param factoryKey the bean name of the factory to obtain
	 * @return the corresponding BeanFactory reference
	 */
	protected BeanFactory createDefinition(String resourceLocation, String factoryKey) {
		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(factory);
		ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

		try {
			Resource[] configResources = resourcePatternResolver.getResources(resourceLocation);
			if (configResources.length == 0) {
				throw new FatalBeanException("Unable to find resource for specified definition. " +
						"Group resource name [" + this.resourceLocation + "], factory key [" + factoryKey + "]");
			}
			reader.loadBeanDefinitions(configResources);
		}
		catch (IOException ex) {
			throw new BeanDefinitionStoreException(
					"Error accessing bean definition resource [" + this.resourceLocation + "]", ex);
		}
		catch (BeanDefinitionStoreException ex) {
			throw new FatalBeanException("Unable to load group definition: " +
					"group resource name [" + this.resourceLocation + "], factory key [" + factoryKey + "]", ex);
		}

		return factory;
	}
	
	/**
	 * Instantiate singletons and do any other normal initialization of the factory.
	 * Subclasses that override {@link #createDefinition createDefinition()} should
	 * also override this method.
	 * *****************************************************************************
	 * ~$ 实例化单例,其他工厂的正常初始化.
	 *    子类覆盖{@link #createDefinition createDefinition()}也应该重写此方法.
	 * @param groupDef the factory returned by {@link #createDefinition createDefinition()}
	 */
	protected void initializeDefinition(BeanFactory groupDef) {
		if (groupDef instanceof ConfigurableListableBeanFactory) {
			((ConfigurableListableBeanFactory) groupDef).preInstantiateSingletons();
		}
	}

	/**
	 * Destroy definition in separate method so subclass may work with other definition types.
	 * ***************************************************************************************
	 * ~$ 破坏定义在不同的方法所以子类可能也适用于其他类型定义.
	 * @param groupDef the factory returned by {@link #createDefinition createDefinition()}
	 * @param selector the resource location for this factory group
	 */
	protected void destroyDefinition(BeanFactory groupDef, String selector) {
		if (groupDef instanceof ConfigurableBeanFactory) {
			if (logger.isTraceEnabled()) {
				logger.trace("Factory group with selector '" + selector +
						"' being released, as there are no more references to it");
			}
			((ConfigurableBeanFactory) groupDef).destroySingletons();
		}
	}


	/**
	 * We track BeanFactory instances with this class.
	 * 我们跟踪BeanFactory与这个类的实例.
	 */
	private static class BeanFactoryGroup {

		private BeanFactory definition;

		private int refCount = 0;
	}


	/**
	 * BeanFactoryReference implementation for this locator.
	 * BeanFactoryReference实现定位.
	 */
	private class CountingBeanFactoryReference implements BeanFactoryReference {

		private BeanFactory beanFactory;

		private BeanFactory groupContextRef;

		public CountingBeanFactoryReference(BeanFactory beanFactory, BeanFactory groupContext) {
			this.beanFactory = beanFactory;
			this.groupContextRef = groupContext;
		}

		public BeanFactory getFactory() {
			return this.beanFactory;
		}

		// Note that it's legal to call release more than once!
		/** 请注意,这是合法的叫释放不止一次! */
		public void release() throws FatalBeanException {
			synchronized (bfgInstancesByKey) {
				BeanFactory savedRef = this.groupContextRef;
				if (savedRef != null) {
					this.groupContextRef = null;
					BeanFactoryGroup bfg = bfgInstancesByObj.get(savedRef);
					if (bfg != null) {
						bfg.refCount--;
						if (bfg.refCount == 0) {
							destroyDefinition(savedRef, resourceLocation);
							bfgInstancesByKey.remove(resourceLocation);
							bfgInstancesByObj.remove(savedRef);
						}
					}
					else {
						// This should be impossible.
						/** 这应该是不可能的. */
						logger.warn("Tried to release a SingletonBeanFactoryLocator group definition " +
								"more times than it has actually been used. Resource name [" + resourceLocation + "]");
					}
				}
			}
		}
	}

}
