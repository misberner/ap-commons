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

public interface Reporter {
	
	public void error(Object ...msgObjects);
	public void warning(Object ...msgObjects);
	public void mandatoryWarning(Object ...msgObjects);
	public void note(Object ...msgObjects);
	public void other(Object ...msgObjects);
	
	public void message(Kind diagnosticKind, Object ...msgObjects);

}
