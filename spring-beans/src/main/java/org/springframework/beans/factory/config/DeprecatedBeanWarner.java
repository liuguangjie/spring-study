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

package org.springframework.beans.factory.config;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.util.StringUtils;

/**
 * Bean factory post processor that logs a warning for {@link Deprecated @Deprecated} beans.
 *
 * ****************************************************************************************
 * ~$ Bean工厂后置处理程序日志{@link Deprecated @Deprecated } Bean的警告。
 * @author Arjen Poutsma
 * @since 3.0.3
 */
public class DeprecatedBeanWarner implements BeanFactoryPostProcessor {

	/**
	 * Logger available to subclasses.
	 */
	protected transient Log logger = LogFactory.getLog(getClass());

	/**
	 * Set the name of the logger to use. The name will be passed to the underlying logger implementation through
	 * Commons Logging, getting interpreted as log category according to the logger's configuration.
	 * <p>This can be specified to not log into the category of this warner class but rather into a specific named category.
	 * *********************************************************************************************************************
	 * ~$ 设置日志记录器使用的名称。这个名字将被传递给底层通过通用日志记录器实现,被解释为日志类别根据日志的配置。
	 * <p>这个可以不指定登录这个华纳类的范畴,而是到一个特定的类别命名。
	 *
	 * @see LogFactory#getLog(String)
	 * @see org.apache.log4j.Logger#getLogger(String)
	 * @see java.util.logging.Logger#getLogger(String)
	 */
	public void setLoggerName(String loggerName) {
		this.logger = LogFactory.getLog(loggerName);
	}


	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		if (isLogEnabled()) {
			String[] beanNames = beanFactory.getBeanDefinitionNames();
			for (String beanName : beanNames) {
				Class<?> beanType = beanFactory.getType(beanName);
				if (beanType != null && beanType.isAnnotationPresent(Deprecated.class)) {
					BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
					logDeprecatedBean(beanName, beanDefinition);
				}
			}
		}
	}

	/**
	 * Logs a warning for a bean annotated with {@link Deprecated @Deprecated}.
	 * ************************************************************************
	 * ~$ 记录一个警告的bean注释{@link Deprecated @Deprecated }。
	 *
	 * @param beanName the name of the deprecated bean
	 * @param beanDefinition the definition of the deprecated bean
	 */
	protected void logDeprecatedBean(String beanName, BeanDefinition beanDefinition) {
		StringBuilder builder = new StringBuilder();
		builder.append(beanDefinition.getBeanClassName());
		builder.append(" ['");
		builder.append(beanName);
		builder.append('\'');
		String resourceDescription = beanDefinition.getResourceDescription();
		if (StringUtils.hasLength(resourceDescription)) {
			builder.append(" in ");
			builder.append(resourceDescription);
		}
		builder.append(" ] has been deprecated");
		logger.warn(builder.toString());
	}

	/**
	 * Determine whether the {@link #logger} field is enabled.
	 * <p>Default is {@code true} when the "warn" level is enabled. Subclasses can override this to change the level
	 * under which logging occurs.
	 * *********************************************************************************************
	 * ~$ 确定{@link #logger}字段是否启用。
	 * <p>默认是{@code true} 当启用了"warn"水平.子类可以重写该日志的级别发生变化.
	 */
	protected boolean isLogEnabled() {
		return logger.isWarnEnabled();
	}

}
