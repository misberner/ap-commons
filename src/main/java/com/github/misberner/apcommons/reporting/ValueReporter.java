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
import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic.Kind;

import com.github.misberner.apcommons.util.annotations.AnnotationUtils;

public class ValueReporter extends AnnotationReporter {
	
	
	protected AnnotationValue value;

	public ValueReporter(ProcessingEnvironment processingEnv, Element element,
			AnnotationMirror annotation, AnnotationValue value) {
		super(processingEnv, element, annotation);
		this.value = value;
	}
	
	public ValueReporter(ProcessingEnvironment processingEnv, Element element,
			AnnotationMirror annotation, CharSequence valueName) {
		super(processingEnv, element, annotation);
		this.value = AnnotationUtils.findAnnotationValue(super.annotation, valueName);
	}
	
	public ValueReporter(ProcessingEnvironment processingEnv, Element element,
			CharSequence annotationName, CharSequence valueName) {
		super(processingEnv, element, annotationName);
		this.value = AnnotationUtils.findAnnotationValue(super.annotation, valueName);
	}
	
	public ValueReporter(ProcessingEnvironment processingEnv, Element element,
			Class<? extends Annotation> annotationClazz, CharSequence valueName) {
		super(processingEnv, element, annotationClazz);
		this.value = AnnotationUtils.findAnnotationValue(super.annotation, valueName);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.reporting.AnnotationReporter#printMessage(javax.tools.Diagnostic.Kind, java.lang.CharSequence)
	 */
	@Override
	protected void printMessage(Kind kind, CharSequence message) {
		processingEnv.getMessager().printMessage(kind, message, element, annotation, value);
	}

	
}
