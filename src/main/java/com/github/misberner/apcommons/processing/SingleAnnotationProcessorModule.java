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
package com.github.misberner.apcommons.processing;

import java.lang.annotation.Annotation;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;

import com.github.misberner.apcommons.processing.exceptions.ProcessingException;
import com.github.misberner.apcommons.util.APUtils;

/**
 * A processor module for isolated, per-element processing of a single annotation
 * type.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public interface SingleAnnotationProcessorModule<A extends Annotation> {
	
	/**
	 * Retrieves the annotation type associated with this module.
	 * @return the annotation type
	 */
	public Class<A> getAnnotationType();
	
	/**
	 * Called before the first element is processed. If this method throws
	 * an exception, no element processing will happen.
	 * 
	 * @param utils the utility object
	 * @throws Exception if an error occurs
	 * @throws ProcessingException if a processing-related error occurs
	 */
	void pre(APUtils utils) throws Exception, ProcessingException;
	
	/**
	 * Process a single annotated element.
	 * 
	 * @param elem the element to process
	 * @param annotationMirror the annotation mirror corresponding to the
	 * annotation to process.
	 * @param annotation the annotation object corresponding to the annotation
	 * to process.
	 * @param utils the utility object
	 * 
	 * @throws Exception if an error occurs
	 * @throws ProcessingException if a processing-related error occurs
	 */
	void process(Element elem,
			AnnotationMirror annotationMirror,
			A annotation,
			APUtils utils) throws Exception, ProcessingException;
	
	/**
	 * Called after the successful processing of all elements.
	 * 
	 * @param utils the utility object
	 * 
	 * @throws Exception if an error occurs 
	 * @throws ProcessingException if a processing-related error occurs
	 */
	void post(APUtils utils) throws Exception, ProcessingException;
	
	/**
	 * Called after unsuccessful processing of all elements, or if
	 * processing was aborted due to a fatal processing error.
	 * 
	 * @param utils the utility object
	 * 
	 * @throws Exception if an error occurs
	 * @throws ProcessingException if a processing-related error occurs
	 */
	void postFailure(APUtils utils) throws Exception, ProcessingException;
}
