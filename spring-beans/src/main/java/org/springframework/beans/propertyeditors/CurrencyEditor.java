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

package org.springframework.beans.propertyeditors;

import java.beans.PropertyEditorSupport;
import java.util.Currency;

/**
 * Editor for <code>java.util.Currency</code>, translating currency codes into Currency
 * objects. Exposes the currency code as text representation of a Currency object.
 * ************************************************************************************
 * ~$ 编辑java.util.Currency,货币代码翻译成货币对象.
 *    公开一种货币的货币代码为文本表示对象.
 * @author Juergen Hoeller
 * @since 3.0
 * @see Currency
 */
public class CurrencyEditor extends PropertyEditorSupport {

	@Override
	public void setAsText(String text) throws IllegalArgumentException {
		setValue(Currency.getInstance(text));
	}

	@Override
	public String getAsText() {
		Currency value = (Currency) getValue();
		return (value != null ? value.getCurrencyCode() : "");
	}

}
