/*
 * Copyright 2002-2006 the original author or authors.
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

package org.springframework.transaction.config;

import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.AbstractBeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.transaction.interceptor.TransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.w3c.dom.Element;

/**
 * @author Rob Harrop
 * @since 2.0
 */
class AnnotationDrivenBeanDefinitionParser extends AbstractBeanDefinitionParser {

	private static final String TRANSACTION_ATTRIBUTE_SOURCE_ADVISOR_NAME = ".transactionAttributeSourceAdvisor";

	public static final String TRANSACTION_INTERCEPTOR = "transactionInterceptor";

	protected AbstractBeanDefinition parseInternal(Element element, ParserContext parserContext) {

		// Register the APC if needed.
		AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(parserContext,null);

		String transactionManagerName = element.getAttribute(TxNamespaceUtils.TRANSACTION_MANAGER_ATTRIBUTE);
		Class sourceClass = TxNamespaceUtils.getAnnotationTransactionAttributeSourceClass();

		// Create the TransactionInterceptor definition
		RootBeanDefinition interceptorDefinition = new RootBeanDefinition(TransactionInterceptor.class);
		interceptorDefinition.getPropertyValues().addPropertyValue(
						TxNamespaceUtils.TRANSACTION_MANAGER_PROPERTY, new RuntimeBeanReference(transactionManagerName));
		interceptorDefinition.getPropertyValues().addPropertyValue(
						TxNamespaceUtils.TRANSACTION_ATTRIBUTE_SOURCE, new RootBeanDefinition(sourceClass));

		// Create the TransactionAttributeSourceAdvisor definition.
		RootBeanDefinition advisorDefinition = new RootBeanDefinition(TransactionAttributeSourceAdvisor.class);
		advisorDefinition.getPropertyValues().addPropertyValue(TRANSACTION_INTERCEPTOR, interceptorDefinition);
		return advisorDefinition;
	}

	protected boolean autogenerateId() {
		return true;
	}
}
