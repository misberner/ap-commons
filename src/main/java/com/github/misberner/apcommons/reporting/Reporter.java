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

import javax.tools.Diagnostic;
import javax.tools.Diagnostic.Kind;

/**
 * An interface for easier reporting of diagnostic messages.
 * <p>
 * <a name="message-building">All reporting methods have an {@link Object} varargs parameter. The concrete message is built by concatenating
 * the string representation of all elements of the respective {@link Object} array. Note that spaces are not inserted automatically
 * to separate elements. For obtaining the string representation, {@link String#valueOf(Object)} is used, hence {@code null} values
 * are allowed and have a String representation of {@code "null"}.</a>
 *  
 * @author Malte Isberner <malte.isberner@gmail.com>
 */
public interface Reporter {
	
	/**
	 * Prints an error message.
	 * @param msgObjects the components of the message, <a href="#message-building">see above</a>
	 * @see Diagnostic.Kind#ERROR
	 */
	public void error(Object ...msgObjects);
	
	/**
	 * Prints a warning message.
	 * @param msgObjects the components of the message, <a href="#message-building">see above</a>
	 * @see Diagnostic.Kind#WARNING
	 */
	public void warning(Object ...msgObjects);
	
	/**
	 * Prints a warning message that is mandated by a specification.
	 * @param msgObjects the components of the message, <a href="#message-building">see above</a>
	 * @see Diagnostic.Kind#MANDATORY_WARNING
	 */
	public void mandatoryWarning(Object ...msgObjects);
	
	/**
	 * Prints an informational message.
	 * @param msgObjects the components of the message, <a href="#message-building">see above</a>
	 * @see Diagnostic.Kind#NOTE
	 */
	public void note(Object ...msgObjects);
	
	/**
	 * Prints some other type of message.
	 * @param msgObjects the components of the message, <a href="#message-building">see above</a>
	 * @see Diagnostic.Kind#OTHER
	 */
	public void other(Object ...msgObjects);
	
	/**
	 * Prints a message of the specified diagnostic kind.
	 * @param diagnosticKind the diagnostic kind of the message
	 * @param msgObjects the components of the message, <a href="#message-building">see above</a>
	 */
	public void message(Kind diagnosticKind, Object ...msgObjects);

}
