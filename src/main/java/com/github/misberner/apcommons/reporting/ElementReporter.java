/*
 * Copyright (c) 2013 by Malte Isberner (https://github.com/misberner).
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
package com.github.misberner.apcommons.reporting;


import java.lang.annotation.Annotation;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

public class ElementReporter extends RootReporter {

	protected final Element element;
	
	public ElementReporter(ProcessingEnvironment processingEnv, Element element) {
		super(processingEnv);
		this.element = element;
	}

	@Override
	protected void printMessage(Kind kind, CharSequence message) {
		processingEnv.getMessager().printMessage(kind, message, element);
	}
	
	public AnnotationReporter forAnnotation(AnnotationMirror annotation) {
		return new AnnotationReporter(processingEnv, element, annotation);
	}
	
	public AnnotationReporter forAnnotation(CharSequence annotationName) {
		return new AnnotationReporter(processingEnv, element, annotationName);
	}
	
	public AnnotationReporter forAnnotation(Class<? extends Annotation> annotationClazz) {
		return new AnnotationReporter(processingEnv, element, annotationClazz);
	}

	
}
