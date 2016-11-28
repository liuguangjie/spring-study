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

package org.springframework.beans.factory.parsing;

/**
 * Empty implementation of the {@link ReaderEventListener} interface,
 * providing no-op implementations of all callback methods.
 * ******************************************************************
 * ~$ 空实现{ @link ReaderEventListener }接口,提供操作来实现的回调方法.
 * @author Juergen Hoeller
 * @since 2.0
 */
public class EmptyReaderEventListener implements ReaderEventListener {

	public void defaultsRegistered(DefaultsDefinition defaultsDefinition) {
		// no-op
	}

	public void componentRegistered(ComponentDefinition componentDefinition) {
		// no-op
	}

	public void aliasRegistered(AliasDefinition aliasDefinition) {
		// no-op
	}

	public void importProcessed(ImportDefinition importDefinition) {
		// no-op
	}

}
