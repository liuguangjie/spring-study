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

/**
 * Interface to be implemented by objects used within a {@link BeanFactory}
 * which are themselves factories. If a bean implements this interface,
 * it is used as a factory for an object to expose, not directly as a bean
 * instance that will be exposed itself.
 *
 * <p><b>NB: A bean that implements this interface cannot be used as a
 * normal bean.</b> A FactoryBean is defined in a bean style, but the
 * object exposed for bean references ({@link #getObject()} is always
 * the object that it creates.
 *
 * <p>FactoryBeans can support singletons and prototypes, and can
 * either create objects lazily on demand or eagerly on startup.
 * The {@link SmartFactoryBean} interface allows for exposing
 * more fine-grained behavioral metadata.
 *
 * <p>This interface is heavily used within the framework itself, for
 * example for the AOP {@link org.springframework.aop.framework.ProxyFactoryBean}
 * or the {@link org.springframework.jndi.JndiObjectFactoryBean}.
 * It can be used for application components as well; however,
 * this is not common outside of infrastructure code.
 *
 * <p><b>NOTE:</b> FactoryBean objects participate in the containing
 * BeanFactory's synchronization of bean creation. There is usually no
 * need for internal synchronization other than for purposes of lazy
 * initialization within the FactoryBean itself (or the like).
 * ********************************************************************************
 * ~$ 使用接口实现的对象在一个{ @link BeanFactory }这是自己的工厂.
 *    如果bean实现这个接口,使用它作为一个对象的工厂暴露,不能直接作为bean实例,将会暴露自己.
 *
 * <p>注:一个bean,它实现了这个接口不能作为正常使用bean.
 *    FactoryBean bean中定义的风格,但对象暴露bean引用({@link #getObject()}总是它创建的对象.
 *
 * <p>FactoryBeans可以支持单例和原型,可以懒洋洋地在需求或急切地在启动时创建对象.
 *    {@link SmartFactoryBean }接口允许暴露更多的细粒度行为的元数据.
 *
 * <p>这个接口是大量使用在框架本身,例如AOP {@link org.springframework.aop.framework.ProxyFactoryBean }或
 *    {@link org.springframework.jndi.JndiObjectFactoryBean }.它也可以用于应用程序组件;然而,这是不常见的基础设施以外的代码.
 *
 * <p>注意:FactoryBean对象参与包含BeanFactory bean创建的同步.
 *    通常不需要内部除了为了延迟初始化同步FactoryBean本身(或类似的).
 * @author Rod Johnson
 * @author Juergen Hoeller
 * @since 08.03.2003
 * @see BeanFactory
 * @see org.springframework.aop.framework.ProxyFactoryBean
 * @see org.springframework.jndi.JndiObjectFactoryBean
 */
public interface FactoryBean<T> {

	/**
	 * Return an instance (possibly shared or independent) of the object
	 * managed by this factory.
	 * <p>As with a {@link BeanFactory}, this allows support for both the
	 * Singleton and Prototype design pattern.
	 * <p>If this FactoryBean is not fully initialized yet at the time of
	 * the call (for example because it is involved in a circular reference),
	 * throw a corresponding {@link FactoryBeanNotInitializedException}.
	 * <p>As of Spring 2.0, FactoryBeans are allowed to return <code>null</code>
	 * objects. The factory will consider this as normal value to be used; it
	 * will not throw a FactoryBeanNotInitializedException in this case anymore.
	 * FactoryBean implementations are encouraged to throw
	 * FactoryBeanNotInitializedException themselves now, as appropriate.
	 * *************************************************************************
	 * ~$ 返回一个实例对象的(可能共享或独立)由这个工厂管理.
	 * <p>如同{@link BeanFactory },这使得对单例模式和原型设计模式的支持.
	 * <p>如果这个FactoryBean尚未完全初始化时调用(例如,因为它涉及一个循环引用),
	 *    把相应的{@link FactoryBeanNotInitializedException }.
	 * <p>Spring 2.0,FactoryBeans被允许返回null对象.工厂将考虑使用这是正常价值;
	 *   它不会抛出一个FactoryBeanNotInitializedException在这种情况下了.
	 *    FactoryBean实现鼓励把FactoryBeanNotInitializedException自己现在是适当的.
	 * @return an instance of the bean (can be <code>null</code>)
	 * @throws Exception in case of creation errors
	 * @see FactoryBeanNotInitializedException
	 */
	T getObject() throws Exception;

	/**
	 * Return the type of object that this FactoryBean creates,
	 * or <code>null</code> if not known in advance.
	 * <p>This allows one to check for specific types of beans without
	 * instantiating objects, for example on autowiring.
	 * <p>In the case of implementations that are creating a singleton object,
	 * this method should try to avoid singleton creation as far as possible;
	 * it should rather estimate the type in advance.
	 * For prototypes, returning a meaningful type here is advisable too.
	 * <p>This method can be called <i>before</i> this FactoryBean has
	 * been fully initialized. It must not rely on state created during
	 * initialization; of course, it can still use such state if available.
	 * <p><b>NOTE:</b> Autowiring will simply ignore FactoryBeans that return
	 * <code>null</code> here. Therefore it is highly recommended to implement
	 * this method properly, using the current state of the FactoryBean.
	 * *************************************************************************
	 * ~$ 返回此FactoryBean创建的对象类型,或null如果事先不知道.
	 * <p>这允许一个检查特定类型的bean没有实例化对象,例如在自动装配.
	 * <p>在创建一个单例对象的实现,这种方法应该尽量避免单例创建尽可能;它应该事先估计的类型.
	 *    为原型,返回有意义的类型也是明智的.
	 * <p>这种方法可以调用在此之前FactoryBean已经完全初始化.它不能依靠国家中创建初始化;当然,这仍然可以使用如果可用状态.
	 * <p> 注意:自动装配会简单地忽略FactoryBeans返回null.因此,强烈建议实现这个方法得当,使用FactoryBean的当前状态.
	 * @return the type of object that this FactoryBean creates,
	 * or <code>null</code> if not known at the time of the call
	 * @see ListableBeanFactory#getBeansOfType
	 */
	Class<?> getObjectType();

	/**
	 * Is the object managed by this factory a singleton? That is,
	 * will {@link #getObject()} always return the same object
	 * (a reference that can be cached)?
	 * <p><b>NOTE:</b> If a FactoryBean indicates to hold a singleton object,
	 * the object returned from <code>getObject()</code> might get cached
	 * by the owning BeanFactory. Hence, do not return <code>true</code>
	 * unless the FactoryBean always exposes the same reference.
	 * <p>The singleton status of the FactoryBean itself will generally
	 * be provided by the owning BeanFactory; usually, it has to be
	 * defined as singleton there.
	 * <p><b>NOTE:</b> This method returning <code>false</code> does not
	 * necessarily indicate that returned objects are independent instances.
	 * An implementation of the extended {@link SmartFactoryBean} interface
	 * may explicitly indicate independent instances through its
	 * {@link SmartFactoryBean#isPrototype()} method. Plain {@link FactoryBean}
	 * implementations which do not implement this extended interface are
	 * simply assumed to always return independent instances if the
	 * <code>isSingleton()</code> implementation returns <code>false</code>.
	 * **************************************************************************
	 * ~$ 由这个工厂管理的对象是一个单例?也就是说,{@link #getObject()}总是返回相同的对象(可以缓存的引用)?
	 * <p> 注意:如果FactoryBean表明持有一个单例对象,返回的对象的getObject()可能会由拥有BeanFactory缓存.
	 *     因此,不要返回true,除非FactoryBean总是暴露相同的参考.
	 * <p>独立地位FactoryBean本身通常会由拥有BeanFactory;通常,它必须被定义为单例.
	 * <p>注意:这个方法返回false并不一定表明,返回的对象是独立的实例.
	 *    扩展的实现{@link SmartFactoryBean }接口可能会明确表明独立实例通过其{@link SmartFactoryBean # isPrototype()}的方法.
	 *    平原{@link FactoryBean }不实现这个扩展接口的实现仅仅是认为如果isSingleton总是返回独立实例返回false()实现.
	 * @return whether the exposed object is a singleton
	 * @see #getObject()
	 * @see SmartFactoryBean#isPrototype()
	 */
	boolean isSingleton();

}
