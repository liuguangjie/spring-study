/*
 * Copyright 2002-2007 the original author or authors.
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

import java.util.Properties;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.InitializingBean;

/**
 * Subclass of PropertyPlaceholderConfigurer that supports JDK 1.4's
 * Preferences API (<code>java.util.prefs</code>).
 *
 * <p>Tries to resolve placeholders as keys first in the user preferences,
 * then in the system preferences, then in this configurer's properties.
 * Thus, behaves like PropertyPlaceholderConfigurer if no corresponding
 * preferences defined.
 *
 * <p>Supports custom paths for the system and user preferences trees. Also
 * supports custom paths specified in placeholders ("myPath/myPlaceholderKey").
 * Uses the respective root node if not specified.
 * ****************************************************************************
 * 子类PropertyPlaceholderConfigurer支持JDK 1.4 API的偏好(<code>java.util.prefs</code>).
 *
 * <p>试图解决占位符作为键首先在用户首选项,然后在系统偏好,然后在这个配置的属性.
 *    因此,像PropertyPlaceholderConfigurer如果没有相应的参数定义.
 *
 * <p>系统支持自定义路径和用户首选项树.还支持自定义路径中指定占位符(“myPath / myPlaceholderKey”).
 *    如果没有指定使用各自的根节点.
 * @author Juergen Hoeller
 * @since 16.02.2004
 * @see #setSystemTreePath
 * @see #setUserTreePath
 * @see Preferences
 */
public class PreferencesPlaceholderConfigurer extends PropertyPlaceholderConfigurer implements InitializingBean {

	private String systemTreePath;

	private String userTreePath;

	private Preferences systemPrefs;

	private Preferences userPrefs;


	/**
	 * Set the path in the system preferences tree to use for resolving
	 * placeholders. Default is the root node.
	 * ****************************************************************
	 * ~$ 设置系统首选项树中的路径用于解决占位符.默认是根节点.
	 */
	public void setSystemTreePath(String systemTreePath) {
		this.systemTreePath = systemTreePath;
	}

	/**
	 * Set the path in the system preferences tree to use for resolving
	 * placeholders. Default is the root node.
	 * ****************************************************************
	 * ~$ 设置系统首选项树中的路径用于解决占位符.默认是根节点。
	 */
	public void setUserTreePath(String userTreePath) {
		this.userTreePath = userTreePath;
	}


	/**
	 * This implementation eagerly fetches the Preferences instances
	 * for the required system and user tree nodes.
	 * *************************************************************
	 * ~$ 这个实现急切地获取所需的系统和用户的偏好实例树节点.
	 */
	public void afterPropertiesSet() {
		this.systemPrefs = (this.systemTreePath != null) ?
		    Preferences.systemRoot().node(this.systemTreePath) : Preferences.systemRoot();
		this.userPrefs = (this.userTreePath != null) ?
		    Preferences.userRoot().node(this.userTreePath) : Preferences.userRoot();
	}

	/**
	 * This implementation tries to resolve placeholders as keys first
	 * in the user preferences, then in the system preferences, then in
	 * the passed-in properties.
	 * ****************************************************************
	 * ~$ 这个实现试图解决占位符作为键首先在用户首选项,然后在系统设置,然后在传入属性.
	 */
	@Override
	protected String resolvePlaceholder(String placeholder, Properties props) {
		String path = null;
		String key = placeholder;
		int endOfPath = placeholder.lastIndexOf('/');
		if (endOfPath != -1) {
			path = placeholder.substring(0, endOfPath);
			key = placeholder.substring(endOfPath + 1);
		}
		String value = resolvePlaceholder(path, key, this.userPrefs);
		if (value == null) {
			value = resolvePlaceholder(path, key, this.systemPrefs);
			if (value == null) {
				value = props.getProperty(placeholder);
			}
		}
		return value;
	}

	/**
	 * Resolve the given path and key against the given Preferences.
	 * *************************************************************
	 * ~$ 解决给定的路径和关键对给定的偏好.
	 * @param path the preferences path (placeholder part before '/')
	 * @param key the preferences key (placeholder part after '/')
	 * @param preferences the Preferences to resolve against
	 * @return the value for the placeholder, or <code>null</code> if none found
	 */
	protected String resolvePlaceholder(String path, String key, Preferences preferences) {
		if (path != null) {
			 // Do not create the node if it does not exist...
			/** 不创建节点,如果它不存在...*/
			try {
				if (preferences.nodeExists(path)) {
					return preferences.node(path).get(key, null);
				}
				else {
					return null;
				}
			}
			catch (BackingStoreException ex) {
				throw new BeanDefinitionStoreException("Cannot access specified node path [" + path + "]", ex);
			}
		}
		else {
			return preferences.get(key, null);
		}
	}

}
