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
package com.github.misberner.apcommons.processing;

import java.lang.annotation.Annotation;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;

import com.github.misberner.apcommons.processing.exceptions.ProcessingException;
import com.github.misberner.apcommons.util.APUtils;
import com.github.misberner.apcommons.util.annotations.AnnotationUtils;

/**
 * Checks whether the enclosing element of an annotated element is also
 * annotated with a certain annotation.
 * <p>
 * This is useful in cases where annotations supply only additional informations, but
 * the annotated elements should not be processed as root elements (e.g., method parameters
 * in annotated methods). Usually a warning or error should be issued if such annotations
 * are found without the enclosing element being annotated.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 * @param <A> annotation type
 */
public class CheckEnclosingAnnotatedProcessor<A extends Annotation> extends AbstractSingleAnnotationProcessor<A> {
	
	private final Class<? extends Annotation>[] expectedAnnotations;
	private final boolean onlyDirect;
	private final Kind diagnosticKind;

	/**
	 * Constructor.
	 * 
	 * @param annotationType the annotation type to check
	 * @param onlyDirect whether or not to consider only the directly enclosing element, or all ancestors
	 * @param diagnosticKind the diagnostic kind to use if the enclosing element is not annotated with one of
	 * the expected annotations.
	 * @param expectedAnnotations the expected annotations of the enclosing elements
	 */
	@SafeVarargs
	public CheckEnclosingAnnotatedProcessor(Class<A> annotationType, boolean onlyDirect, Kind diagnosticKind,
			Class<? extends Annotation> ...expectedAnnotations) {
		super(annotationType);
		this.onlyDirect = onlyDirect;
		this.diagnosticKind = diagnosticKind;
		this.expectedAnnotations = expectedAnnotations.clone();
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.processing.AbstractSingleAnnotationProcessor#process(javax.lang.model.element.Element, javax.lang.model.element.AnnotationMirror, java.lang.annotation.Annotation, com.github.misberner.apcommons.util.APUtils)
	 */
	@Override
	public void process(Element elem, AnnotationMirror annotation, A annotationObject, APUtils utils)
			throws Exception, ProcessingException {
		if(!AnnotationUtils.isEnclosingAnnotated(elem, onlyDirect, expectedAnnotations)) {
			String verb = (diagnosticKind == Kind.ERROR) ? "must" : "should";
			StringBuilder sb = new StringBuilder();
			TypeElement te = (TypeElement)annotation.getAnnotationType().asElement();
			sb.append("Element annotated with ").append(te.getSimpleName()).append(' ');
			sb.append(verb).append(" be ");
			if(onlyDirect) {
				sb.append("directly ");
			}
			sb.append("enclosed in an element annotated with one of the following annotations:\n");
			for(Class<? extends Annotation> ea : expectedAnnotations) {
				sb.append(" - ").append(ea.getName()).append('\n');
			}
			utils.getMessager().printMessage(diagnosticKind, sb, elem, annotation);
		}
	}
	
	

}
