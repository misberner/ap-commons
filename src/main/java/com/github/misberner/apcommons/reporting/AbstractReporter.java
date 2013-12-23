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

import javax.tools.Diagnostic.Kind;

public abstract class AbstractReporter implements Reporter {

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.reporting.Reporter#error(java.lang.Object[])
	 */
	@Override
	public void error(Object... msgObjects) {
		message(Kind.ERROR, msgObjects);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.reporting.Reporter#warning(java.lang.Object[])
	 */
	@Override
	public void warning(Object... msgObjects) {
		message(Kind.WARNING, msgObjects);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.reporting.Reporter#mandatoryWarning(java.lang.Object[])
	 */
	@Override
	public void mandatoryWarning(Object... msgObjects) {
		message(Kind.MANDATORY_WARNING, msgObjects);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.reporting.Reporter#note(java.lang.Object[])
	 */
	@Override
	public void note(Object... msgObjects) {
		message(Kind.NOTE, msgObjects);
	}

	/*
	 * (non-Javadoc)
	 * @see com.github.misberner.apcommons.reporting.Reporter#other(java.lang.Object[])
	 */
	@Override
	public void other(Object... msgObjects) {
		message(Kind.OTHER, msgObjects);
	}
	
	@Override
	public void message(Kind diagnosticKind, Object... msgObjects) {
		CharSequence msg = assembleMessage(msgObjects);
		printMessage(diagnosticKind, msg);
	}
	
	/**
	 * Concatenates the string representations of the given objects.
	 * 
	 * @param msgObjects the objects to display
	 * @return the concatenation of the object's string representations.
	 */
	protected CharSequence assembleMessage(Object ...msgObjects) {
		StringBuilder sb = new StringBuilder();
		for(Object obj : msgObjects) {
			sb.append(obj);
		}
		return sb;
	}
	
	/**
	 * Actually print a message of the given diagnostic kind.
	 * 
	 * @param kind the diagnostic kind
	 * @param message the message to print
	 */
	protected abstract void printMessage(Kind kind, CharSequence message);
}
