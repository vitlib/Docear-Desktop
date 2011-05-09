/*
 *  Freeplane - mind map editor
 *  Copyright (C) 2011 Volker Boerchers
 *
 *  This file author is Volker Boerchers
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.freeplane.features.common.format;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.freeplane.n3.nanoxml.XMLElement;

/** Scanner.scan(string) uses a number of Parsers to convert string into a Number, a Date or whatever. */
public class Scanner {
	private final ArrayList<String> locales;
	private final boolean isDefault;
	private String firstChars;
	private boolean checkFirstChars;
	private ArrayList<Parser> parsers = new ArrayList<Parser>();

	public Scanner(String[] locales, boolean isDefault) {
		this.locales = new ArrayList<String>(Arrays.asList(locales));
		this.isDefault = isDefault;
		validate();
	}

	private void validate() {
		if (locales.isEmpty())
			throw new IllegalArgumentException("illegal ");
	}

	public ArrayList<String> getLocales() {
		return locales;
	}

	public boolean isDefault() {
		return isDefault;
	}

	public String getFirstChars() {
		return firstChars;
	}

	public void setFirstChars(String firstChars) {
		this.firstChars = firstChars;
		checkFirstChars = !StringUtils.isEmpty(firstChars);
	}

	public void addParser(Parser parser) {
		parsers.add(parser);
	}

	public XMLElement toXml() {
		final XMLElement xmlElement = new XMLElement("scanner");
		xmlElement.setAttribute("locale", StringUtils.join(locales.iterator(), ","));
		xmlElement.addChild(firstCharsToXml());
		for (Parser parser : parsers) {
			xmlElement.addChild(parser.toXml());
		}
		return xmlElement;
	}

	private XMLElement firstCharsToXml() {
		final XMLElement xmlElement = new XMLElement("checkfirstchar");
		if (checkFirstChars)
			xmlElement.setAttribute("chars", firstChars);
		else
			xmlElement.setAttribute("isDisabled", "true");
		return xmlElement;
	}

	public boolean localeMatchesExactly(String locale) {
		return locales.contains(locale);
	}

	public boolean countryMatches(String locale) {
		return locales.contains(locale.replaceFirst("_.*", ""));
	}

	public Object parse(String string) {
		if (StringUtils.isEmpty(string) || (checkFirstChars && firstChars.indexOf(string.charAt(0)) == -1))
			return string;
		for (Parser parser : parsers) {
			final Object object = parser.parse(string);
			if (object != null)
				return object;
		}
		return string;
	}
}
