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

/**
 * Reporter that by default prints messages associated with a certain annotation
 * of a certain element.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class AnnotationReporter extends ElementReporter {
	

	protected AnnotationMirror annotation;
	
	/**
	 * Constructor.
	 * @param processingEnv the processing environment
	 * @param element the element to which messages refer
	 * @param annotation the annotation to which messages refer
	 */
	public AnnotationReporter(ProcessingEnvironment processingEnv,
			Element element, AnnotationMirror annotation) {
		super(processingEnv, element);
		this.annotation = annotation;
	}
	
	public AnnotationReporter(ProcessingEnvironment processingEnv,
			Element element, CharSequence annotationName) {
		super(processingEnv, element);
		this.annotation = AnnotationUtils.findAnnotationMirror(element, annotationName);
	}
	
	public AnnotationReporter(ProcessingEnvironment processingEnv,
			Element element, Class<? extends Annotation> annotationClazz) {
		super(processingEnv, element);
		this.annotation = AnnotationUtils.findAnnotationMirror(element, annotationClazz);
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.reporting.AbstractReporter#printMessage(javax.tools.Diagnostic.Kind, java.lang.CharSequence)
	 */
	@Override
	protected void printMessage(Kind kind, CharSequence message) {
		processingEnv.getMessager().printMessage(kind, message, element, annotation);
	}
	
	/**
	 * Retrieves a reporter which prints messages corresponding to a certain value
	 * of the annotation.
	 * 
	 * @param valueName the name of the annotation value
	 * @return the corresponding reporter
	 */
	public ValueReporter forValue(CharSequence valueName) {
		return new ValueReporter(processingEnv, element, annotation, valueName);
	}
	
	/**
	 * Retrieves a reporter which prints messages corresponding to a certain value
	 * of the annotation.
	 * 
	 * @param value the annotation value
	 * @return the corresponding reporter
	 */
	public ValueReporter forValue(AnnotationValue value) {
		return new ValueReporter(processingEnv, element, annotation, value);
	}
	
}
