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

import java.lang.reflect.Field;

import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.FactoryBeanNotInitializedException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * {@link FactoryBean} which retrieves a static or non-static field value.
 * 
 * <p>Typically used for retrieving public static final constants. Usage example:
 * ******************************************************************************
 * ~$ {@link FactoryBean }获取静态和非静态字段值。
 * <p>通常用于检索公共静态常量。使用的例子:
 * <pre class="code">// standard definition for exposing a static field, specifying the "staticField" property
 *                   // 公开的标准定义一个静态字段,指定“staticField”属性
 * &lt;bean id="myField" class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean"&gt;
 *   &lt;property name="staticField" value="java.sql.Connection.TRANSACTION_SERIALIZABLE"/&gt;
 * &lt;/bean&gt;
 *
 * // convenience version that specifies a static field pattern as bean name
 * // 方便版本指定一个静态字段模式bean的名称
 * &lt;bean id="java.sql.Connection.TRANSACTION_SERIALIZABLE"
 *       class="org.springframework.beans.factory.config.FieldRetrievingFactoryBean"/&gt;</pre>
 * </pre>
 * 
 * <p>If you are using Spring 2.0, you can also use the following style of configuration for
 * public static fields.
 * ******************************************************************************************
 * ~$<p>如果您正在使用Spring 2.0中,您还可以使用以下配置公共静态字段的样式。
 * <pre class="code">&lt;util:constant static-field="java.sql.Connection.TRANSACTION_SERIALIZABLE"/&gt;</pre>
 *
 * @author Juergen Hoeller
 * @since 1.1
 * @see #setStaticField
 */
public class FieldRetrievingFactoryBean
		implements FactoryBean<Object>, BeanNameAware, BeanClassLoaderAware, InitializingBean {

	private Class targetClass;

	private Object targetObject;

	private String targetField;

	private String staticField;

	private String beanName;

	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	// the field we will retrieve
	/** 我们将检索的字段*/
	private Field fieldObject;


	/**
	 * Set the target class on which the field is defined.
	 * Only necessary when the target field is static; else,
	 * a target object needs to be specified anyway.
	 * *****************************************************
	 * ~$ 设置目标类的字段定义。
	 *   只有必要时目标字段是静态的;否则,需要指定一个目标对象。
	 * @see #setTargetObject
	 * @see #setTargetField
	 */
	public void setTargetClass(Class targetClass) {
		this.targetClass = targetClass;
	}

	/**
	 * Return the target class on which the field is defined.
	 * ******************************************************
	 * ~$ 返回的目标类字段定义。
	 */
	public Class getTargetClass() {
		return targetClass;
	}

	/**
	 * Set the target object on which the field is defined.
	 * Only necessary when the target field is not static;
	 * else, a target class is sufficient.
	 * ****************************************************
	 * ~$ 设置的目标对象字段定义.只有必要时目标字段不是静态的,别的,目标类就足够了。
	 * @see #setTargetClass
	 * @see #setTargetField
	 */
	public void setTargetObject(Object targetObject) {
		this.targetObject = targetObject;
	}

	/**
	 * Return the target object on which the field is defined.
	 * *******************************************************
	 * ~$ 返回的目标对象字段定义.
	 */
	public Object getTargetObject() {
		return this.targetObject;
	}

	/**
	 * Set the name of the field to be retrieved.
	 * Refers to either a static field or a non-static field,
	 * depending on a target object being set.
	 * ******************************************************
	 * ~$ 组检索字段的名称.是指静态字段或非静态字段,根据设定的目标对象。
	 * @see #setTargetClass
	 * @see #setTargetObject
	 */
	public void setTargetField(String targetField) {
		this.targetField = StringUtils.trimAllWhitespace(targetField);
	}

	/**
	 * Return the name of the field to be retrieved.
	 * ********************************************
	 * ~$ 返回检索字段的名称。
	 */
	public String getTargetField() {
		return this.targetField;
	}

	/**
	 * Set a fully qualified static field name to retrieve,
	 * e.g. "example.MyExampleClass.MY_EXAMPLE_FIELD".
	 * Convenient alternative to specifying targetClass and targetField.
	 * ****************************************************************
	 * ~$ 设置一个完全限定的静态字段名称检索,如。“example.MyExampleClass.MY_EXAMPLE_FIELD”。指定targetClass和targetField方便的替代品。
	 * @see #setTargetClass
	 * @see #setTargetField
	 */
	public void setStaticField(String staticField) {
		this.staticField = StringUtils.trimAllWhitespace(staticField);
	}

	/**
	 * The bean name of this FieldRetrievingFactoryBean will be interpreted
	 * as "staticField" pattern, if neither "targetClass" nor "targetObject"
	 * nor "targetField" have been specified.
	 * This allows for concise bean definitions with just an id/name.
	 * *********************************************************************
	 * ~$ 本FieldRetrievingFactoryBean的bean的名称将被解释为“staticField”模式,
	 * 如果既不是“targetClass”、“targetObject”、“targetField”已经指定。这允许只有一个id /名称简洁的bean定义。
	 */
	public void setBeanName(String beanName) {
		this.beanName = StringUtils.trimAllWhitespace(BeanFactoryUtils.originalBeanName(beanName));
	}

	public void setBeanClassLoader(ClassLoader classLoader) {
		this.beanClassLoader = classLoader;
	}


	public void afterPropertiesSet() throws ClassNotFoundException, NoSuchFieldException {
		if (this.targetClass != null && this.targetObject != null) {
			throw new IllegalArgumentException("Specify either targetClass or targetObject, not both");
		}

		if (this.targetClass == null && this.targetObject == null) {
			if (this.targetField != null) {
				throw new IllegalArgumentException(
				    "Specify targetClass or targetObject in combination with targetField");
			}

			// If no other property specified, consider bean name as static field expression.
			/** 如果没有指定其他财产,考虑bean名称作为静态字段表达式。*/
			if (this.staticField == null) {
				this.staticField = this.beanName;
			}

			// Try to parse static field into class and field.
			/** 静态字段解析成类,字段.*/
			int lastDotIndex = this.staticField.lastIndexOf('.');
			if (lastDotIndex == -1 || lastDotIndex == this.staticField.length()) {
				throw new IllegalArgumentException(
						"staticField must be a fully qualified class plus method name: " +
						"e.g. 'example.MyExampleClass.MY_EXAMPLE_FIELD'");
			}
			String className = this.staticField.substring(0, lastDotIndex);
			String fieldName = this.staticField.substring(lastDotIndex + 1);
			this.targetClass = ClassUtils.forName(className, this.beanClassLoader);
			this.targetField = fieldName;
		}

		else if (this.targetField == null) {
			// Either targetClass or targetObject specified.
			/** targetClass或targetObject指定。*/
			throw new IllegalArgumentException("targetField is required");
		}

		// Try to get the exact method first.
		/** 试着先确切的方法。*/
		Class targetClass = (this.targetObject != null) ? this.targetObject.getClass() : this.targetClass;
		this.fieldObject = targetClass.getField(this.targetField);
	}


	public Object getObject() throws IllegalAccessException {
		if (this.fieldObject == null) {
			throw new FactoryBeanNotInitializedException();
		}
		ReflectionUtils.makeAccessible(this.fieldObject);
		if (this.targetObject != null) {
			// instance field
			return this.fieldObject.get(this.targetObject);
		}
		else{
			// class field
			return this.fieldObject.get(null);
		}
	}

	public Class<?> getObjectType() {
		return (this.fieldObject != null ? this.fieldObject.getType() : null);
	}

	public boolean isSingleton() {
		return false;
	}

}
