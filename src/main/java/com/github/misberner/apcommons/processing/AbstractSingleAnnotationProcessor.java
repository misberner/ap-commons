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
 * Abstract base class for {@link SingleAnnotationProcessorModule} implementations.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 * @param <A> annotation type
 */
public abstract class AbstractSingleAnnotationProcessor<A extends Annotation> implements
		SingleAnnotationProcessorModule<A> {
	
	private final Class<A> annotationType;
	
	/**
	 * Constructor.
	 * 
	 * @param annotationType the annotation type processed by this module
	 */
	public AbstractSingleAnnotationProcessor(Class<A> annotationType) {
		this.annotationType = annotationType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.processing.SingleAnnotationProcessorModule#getAnnotationType()
	 */
	@Override
	public Class<A> getAnnotationType() {
		return annotationType;
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.processing.SingleAnnotationProcessorModule#pre(com.github.misberner.apcommons.util.APUtils)
	 */
	@Override
	public void pre(APUtils utils) throws Exception, ProcessingException {
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.processing.SingleAnnotationProcessorModule#process(javax.lang.model.element.Element, javax.lang.model.element.AnnotationMirror, java.lang.annotation.Annotation, com.github.misberner.apcommons.util.APUtils)
	 */
	@Override
	public void process(Element elem, AnnotationMirror annotationMirror,
			A annotation, APUtils utils) throws Exception, ProcessingException {
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.processing.SingleAnnotationProcessorModule#post(com.github.misberner.apcommons.util.APUtils)
	 */
	@Override
	public void post(APUtils utils) throws Exception, ProcessingException {
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.processing.SingleAnnotationProcessorModule#postFailure(com.github.misberner.apcommons.util.APUtils)
	 */
	@Override
	public void postFailure(APUtils utils) throws Exception,
			ProcessingException {
	}
}
