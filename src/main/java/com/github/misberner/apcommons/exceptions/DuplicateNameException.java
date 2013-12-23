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
package com.github.misberner.apcommons.exceptions;

/**
 * Exception to indicate that a unique by-name mapping could not
 * be obtained, as there are duplicate names for different elements
 * in the provided collection of elements.
 * 
 * @author Malte Isberner <malte.isberner@gmail.com>
 *
 */
public class DuplicateNameException extends IllegalArgumentException {
	
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor.
	 * @param name the duplicate name that was encountered
	 */
	public DuplicateNameException(CharSequence name) {
		super("Duplicate name: " + name);
	}
	
	/**
	 * Default constructor.
	 */
	public DuplicateNameException() {}
	
	
}