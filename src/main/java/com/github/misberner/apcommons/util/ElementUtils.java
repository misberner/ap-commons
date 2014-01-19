/*
 * Copyright (c) 2013-2014 by Malte Isberner (https://github.com/misberner).
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.misberner.apcommons.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.PackageElement;

import com.github.misberner.apcommons.exceptions.DuplicateNameException;

public class ElementUtils {

	
	
	

	/**
	 * Retrieves the {@link PackageElement} for the package which (indirectly) encloses the specified element.
	 * @param elem the element
	 * @return the package which encloses the element
	 */
	public static PackageElement getPackage(Element elem) {
		while(elem != null && elem.getKind() != ElementKind.PACKAGE) {
			elem = elem.getEnclosingElement();
		}
		return (PackageElement)elem;
	}
	
	/**
	 * Retrieves the name of the package which (indirectly) encloses the specified element.
	 * @param elem the element
	 * @return the package which encloses the element
	 */
	public static String getPackageName(Element elem) {
		PackageElement pkg = getPackage(elem);
		if(pkg == null) {
			return "";
		}
		return pkg.getQualifiedName().toString();
	}
	
	
	
	
	
	
	/**
	 * Retrieves a by-name mapping for all the elements contained in the argument.
	 * <p>
	 * <i>Note:</i> the values of the returned map are actually {@link List}s, since
	 * multiple elements might have the same simple name (e.g., in case of overloaded
	 * methods).
	 * 
	 * @param elements the elements
	 * @return a by-name multi-mapping for the specified elements 
	 */
	public static <E extends Element>
	Map<String,List<E>> elementsByName(Iterable<? extends E> elements) {
		
		Map<String,List<E>> result = new HashMap<>();
		
		for(E elem : elements) {
			String elemName = elem.getSimpleName().toString();
			List<E> list = result.get(elemName);
			if(list == null) {
				list = new ArrayList<>();
				result.put(elemName, list);
			}
			list.add(elem);
		}
		
		return result;
	}
	
	/**
	 * Retrieves a by-name mapping for all the elements contained in the argument.
	 * <p>
	 * <i>Note:</i> unlike the above {@link #elementsByName(Iterable)}, the mapping does
	 * not contain multiple elements per name. However, <tt>elements</tt> contains
	 * multiple elements with the same simple name, a {@link DuplicateNameException}
	 * is thrown.
	 * 
	 * @param elements the elements
	 * @return a by-name mapping for the specified elements
	 * @throws DuplicateNameException if at least two elements in <tt>elements</tt> have the same
	 * simple name.
	 */
	public static <E extends Element>
	Map<String,E> elementsByUniqueName(Iterable<? extends E> elements)
			throws DuplicateNameException {
		
		Map<String,E> result = new HashMap<>();
		
		for(E elem : elements) {
			String elemName = elem.getSimpleName().toString();
			if(result.put(elemName, elem) != null) {
				throw new DuplicateNameException(elemName);
			}
		}
		
		return result;
	}
	
	
	
	
	public static boolean checkElementKind(Element elem, ElementKind ...acceptedKinds) {
		ElementKind ek = elem.getKind();
		for(ElementKind ak : acceptedKinds) {
			if(ek == ak) {
				return true;
			}
		}
		return false;
	}
	
	
	public static <E extends Element>
	List<E> filterVisible(Iterable<? extends E> elemList, Visibility minimumVisibility) {
		List<E> result = new ArrayList<>();
		
		for(E elem : elemList) {
			if(Visibility.of(elem).compareTo(minimumVisibility) >= 0) {
				result.add(elem);
			}
		}
		
		return result;
	}
	
	
	public static <E extends Element>
	List<E> filterPublic(Iterable<? extends E> elemList) {
		return filterVisible(elemList, Visibility.PUBLIC);
	}
	
	public static <E extends Element>
	List<E> filterPackageVisible(Iterable<? extends E> elemList) {
		return filterVisible(elemList, Visibility.PACKAGE_PRIVATE);
	}
	
	
	public Visibility getEffectiveVisibility(Element element) {
		Visibility vis = Visibility.of(element);
		
		Element enclosing = element.getEnclosingElement();
		
		while(enclosing != null && enclosing.getKind() != ElementKind.PACKAGE) {
			vis = vis.meet(Visibility.of(enclosing));
			enclosing = enclosing.getEnclosingElement();
		}
		
		return vis;
	}
}
