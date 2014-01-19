/*
 * Copyright (c) 2014 by Malte Isberner (https://github.com/misberner).
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
package com.github.misberner.apcommons.util.annotations;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;

public abstract class AnnotationUtils {
	
	/**
	 * Checks if the direct parent (i.e., enclosing element) of this element is annotated
	 * with the given annotation type, and if so, returns the annotation object.
	 * <p>
	 * This method is safe in the sense that it won't result in a {@link NullPointerException}
	 * when the specified element does not have a parent, or a <tt>null</tt> element reference
	 * is specified.
	 * 
	 * @param elem the element
	 * @param annotationType the annotation type to check for
	 * @return the annotation object, if the specified element has a parent that is annotated
	 * with the given annotation type.
	 */
	public static <A extends Annotation>
	A getParentAnnotation(Element elem, Class<A> annotationType) {
		if(elem == null) {
			return null;
		}
		Element enclosing = elem.getEnclosingElement();
		if(enclosing == null) {
			return null;
		}
		return enclosing.getAnnotation(annotationType);
	}
	
	/**
	 * Checks if the element or one of its ancestors (i.e., reflexive-transitively enclosing elements)
	 * is annotated with the given annotation type, and if so, returns the annotation object.
	 * <p>
	 * This method is safe in the sense that it won't result in a {@link NullPointerException}
	 * when the specified element does not have a parent, or a <tt>null</tt> element reference
	 * is specified.
	 * 
	 * @param elem the element
	 * @param annotationType the annotation type to check for
	 * @return the annotation object, if the specified element or one of its ancestors is annotated
	 * with the given annotation type.
	 */
	public static <A extends Annotation>
	A getAncestorAnnotation(Element elem, Class<A> annotationType) {
		Element curr = elem;
		while(curr != null) {
			A ann = curr.getAnnotation(annotationType);
			if(ann != null) {
				return ann;
			}
			curr = curr.getEnclosingElement();
		}
		return null;
	}
	
	public static <A extends Annotation>
	A getEnclosingAnnotation(Element elem, Class<A> annotationType, boolean onlyDirect) {
		if(onlyDirect) {
			return getParentAnnotation(elem, annotationType);
		}
		return getAncestorAnnotation(elem, annotationType);
	}
	
	@SafeVarargs
	public static boolean isAnnotated(Element elem, Class<? extends Annotation> ...expectedAnnotations) {
		if(elem == null) {
			return false;
		}
		
		for(Class<? extends Annotation> annotationType : expectedAnnotations) {
			if(elem.getAnnotation(annotationType) != null) {
				return true;
			}
		}
		return false;
	}
	
	@SafeVarargs
	public static boolean isParentAnnotated(Element elem, Class<? extends Annotation> ...expectedAnnotations) {
		if(elem == null) {
			return false;
		}
		Element parent = elem.getEnclosingElement();
		return isAnnotated(parent, expectedAnnotations);
	}
	
	@SafeVarargs
	public static boolean isAncestorAnnotated(Element elem, Class<? extends Annotation> ...expectedAnnotations) {
		Element curr = elem;
		while(curr != null) {
			if(isAnnotated(curr, expectedAnnotations)) {
				return true;
			}
			curr = curr.getEnclosingElement();
		}
		return false;
	}
	
	@SafeVarargs
	public static
	boolean isEnclosingAnnotated(Element elem, boolean onlyDirect, Class<? extends Annotation> ...expectedAnnotations) {
		if(onlyDirect) {
			return isParentAnnotated(elem, expectedAnnotations);
		}
		return isAncestorAnnotated(elem, expectedAnnotations);
	}
	
	
	/**
	 * Find an annotation mirror of an element, given the annotation's fully qualified class name.
	 * 
	 * @param elem the element
	 * @param annClassName the fully qualified class name of the annotation
	 * @return the respective annotation mirror, or <tt>null</tt> if the element is not annotated
	 * with the given annotation.
	 */
	public static AnnotationMirror findAnnotationMirror(Element elem, CharSequence annClassName) {
		for(AnnotationMirror am : elem.getAnnotationMirrors()) {
			TypeElement te = (TypeElement)am.getAnnotationType().asElement();
			if(te.getQualifiedName().contentEquals(annClassName)) {
				return am;
			}
		}
		return null;
	}
	
	/**
	 * Find an annotation mirror of an element, given the annotation's {@link Class} object.
	 * 
	 * @param elem the element
	 * @param annClass the annotation's {@link Class} object
	 * @return the respective annotation mirror, or <tt>null</tt> if the element is not annotated
	 * with the given annotation.
	 */
	public static AnnotationMirror findAnnotationMirror(Element elem, Class<? extends Annotation> annClass) {
		return findAnnotationMirror(elem, annClass.getName());
	}
	
	/**
	 * Get an element value from an annotation mirror.
	 * <p>
	 * <i>Note:</i> Annotation mirrors usually do not contain default values, only those that were
	 * set explicitly.
	 * 
	 * @param am the annotation mirror
	 * @param valueName the name of the value
	 * @return the respective annotation value, or <tt>null</tt> if no such value exists or was explicitly set
	 */
	public static AnnotationValue findAnnotationValue(AnnotationMirror am, CharSequence valueName) {
		for(Map.Entry<? extends ExecutableElement,? extends AnnotationValue> e : am.getElementValues().entrySet()) {
			if(e.getKey().getSimpleName().contentEquals(valueName))
				return e.getValue();
		}
		return null;
	}
	
	public static Map<String,AnnotationValue> getAnnotationValues(AnnotationMirror am) {
		Map<String,AnnotationValue> result = new HashMap<>();
		for(Map.Entry<? extends ExecutableElement,? extends AnnotationValue> e : am.getElementValues().entrySet()) {
			result.put(e.getKey().getSimpleName().toString(), e.getValue());
		}
		return result;
	}
	
	public static Map<String,AnnotationValue> getAnnotationValues(Element element, Class<? extends Annotation> annotationClazz) {
		AnnotationMirror am = findAnnotationMirror(element, annotationClazz);
		if(am == null) {
			return null;
		}
		return getAnnotationValues(am);
	}
	
	
	
	private AnnotationUtils() {}
}
