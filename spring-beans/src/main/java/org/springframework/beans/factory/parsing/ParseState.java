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

package org.springframework.beans.factory.parsing;

import java.util.Stack;

/**
 * Simple {@link Stack}-based structure for tracking the logical position during
 * a parsing process. {@link Entry entries} are added to the stack at
 * each point during the parse phase in a reader-specific manner.
 *
 * <p>Calling {@link #toString()} will render a tree-style view of the current logical
 * position in the parse phase. This representation is intended for use in
 * error messages.
 * ************************************************************************************
 * ~$ 简单的{@link Stack}的结构解析过程中跟踪逻辑位置.
 *    {@link Entry entries}被添加到堆栈每一点在解析阶段reader-specific的方式.
 *
 * <p> 调用{@link #toString()}将呈现一个样式视图解析当前逻辑位置的阶段.这表示是用于错误消息.
 * @author Rob Harrop
 * @since 2.0
 */
public final class ParseState {

	/**
	 * Tab character used when rendering the tree-style representation.
	 * ****************************************************************
	 * ~$ 制表符时使用呈现样式表示.
	 */
	private static final char TAB = '\t';

	/**
	 * Internal {@link Stack} storage.
	 * *******************************
	 * ~$ 内部{@link Stack }存储.
	 */
	private final Stack state;


	/**
	 * Create a new <code>ParseState</code> with an empty {@link Stack}.
	 */
	public ParseState() {
		this.state = new Stack();
	}

	/**
	 * Create a new <code>ParseState</code> whose {@link Stack} is a {@link Object#clone clone}
	 * of that of the passed in <code>ParseState</code>.
	 */
	private ParseState(ParseState other) {
		this.state = (Stack) other.state.clone();
	}


	/**
	 * Add a new {@link Entry} to the {@link Stack}.
	 */
	public void push(Entry entry) {
		this.state.push(entry);
	}

	/**
	 * Remove an {@link Entry} from the {@link Stack}.
	 */
	public void pop() {
		this.state.pop();
	}

	/**
	 * Return the {@link Entry} currently at the top of the {@link Stack} or
	 * <code>null</code> if the {@link Stack} is empty.
	 * *********************************************************************
	 * ~$ 返回顶部的{@link Entry}目前{@link Stack}或null如果{@link Stack}是空的.
	 */
	public Entry peek() {
		return (Entry) (this.state.empty() ? null : this.state.peek());
	}

	/**
	 * Create a new instance of {@link ParseState} which is an independent snapshot
	 * of this instance.
	 * ****************************************************************************
	 * ~$ 创建一个新的实例,{@link ParseState }这是一个独立的快照实例
	 */
	public ParseState snapshot() {
		return new ParseState(this);
	}


	/**
	 * Returns a tree-style representation of the current <code>ParseState</code>.
	 * ***************************************
	 * ~$ 返回当前ParseState样式表示.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int x = 0; x < this.state.size(); x++) {
			if (x > 0) {
				sb.append('\n');
				for (int y = 0; y < x; y++) {
					sb.append(TAB);
				}
				sb.append("-> ");
			}
			sb.append(this.state.get(x));
		}
		return sb.toString();
	}


	/**
	 * Marker interface for entries into the {@link ParseState}.
	 * *********************************************************
	 * ~$ 标记接口条目到{@link ParseState }.
	 */
	public interface Entry {

	}

}
