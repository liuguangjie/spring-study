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

import java.security.AccessControlContext;

/**
 * Provider of the security context of the code running inside the bean factory.
 * ****************************************************************************
 * ~$ 提供者中运行的代码的安全上下文bean工厂.
 * @author Costin Leau
 * @since 3.0
 */
public interface SecurityContextProvider {

	/**
	 * Provides a security access control context relevant to a bean factory.
	 * **********************************************************************
	 * ~$ 提供了一个安全访问控制上下文bean相关工厂.
	 * @return bean factory security control context
	 */
	AccessControlContext getAccessControlContext();

}
