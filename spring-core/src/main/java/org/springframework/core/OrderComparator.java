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

package org.springframework.core;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * {@link Comparator} implementation for {@link Ordered} objects,
 * sorting by order value ascending (resp. by priority descending).
 *
 * <p>Non-<code>Ordered</code> objects are treated as greatest order
 * values, thus ending up at the end of the list, in arbitrary order
 * (just like same order values of <code>Ordered</code> objects).
 *
 * @author Juergen Hoeller
 * @since 07.04.2003
 * @see Ordered
 * @see Collections#sort(List, Comparator)
 * @see Arrays#sort(Object[], Comparator)
 */
public class OrderComparator implements Comparator<Object> {

	/**
	 * Shared default instance of OrderComparator.
	 */
	public static OrderComparator INSTANCE = new OrderComparator();


	public int compare(Object o1, Object o2) {
		boolean p1 = (o1 instanceof PriorityOrdered);
		boolean p2 = (o2 instanceof PriorityOrdered);
		if (p1 && !p2) {
			return -1;
		}
		else if (p2 && !p1) {
			return 1;
		}

		// Direct evaluation instead of Integer.compareTo to avoid unnecessary object creation.
		int i1 = getOrder(o1);
		int i2 = getOrder(o2);
		return (i1 < i2) ? -1 : (i1 > i2) ? 1 : 0;
	}

	/**
	 * Determine the order value for the given object.
	 * <p>The default implementation checks against the {@link Ordered}
	 * interface. Can be overridden in subclasses.
	 * @param obj the object to check
	 * @return the order value, or <code>Ordered.LOWEST_PRECEDENCE</code> as fallback
	 */
	protected int getOrder(Object obj) {
		return (obj instanceof Ordered ? ((Ordered) obj).getOrder() : Ordered.LOWEST_PRECEDENCE);
	}


	/**
	 * Sort the given List with a default OrderComparator.
	 * <p>Optimized to skip sorting for lists with size 0 or 1,
	 * in order to avoid unnecessary array extraction.
	 * @param list the List to sort
	 * @see Collections#sort(List, Comparator)
	 */
	public static void sort(List<?> list) {
		if (list.size() > 1) {
			Collections.sort(list, INSTANCE);
		}
	}

	/**
	 * Sort the given array with a default OrderComparator.
	 * <p>Optimized to skip sorting for lists with size 0 or 1,
	 * in order to avoid unnecessary array extraction.
	 * @param array the array to sort
	 * @see Arrays#sort(Object[], Comparator)
	 */
	public static void sort(Object[] array) {
		if (array.length > 1) {
			Arrays.sort(array, INSTANCE);
		}
	}

}
