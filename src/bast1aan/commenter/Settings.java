/*
 * Commenter
 * Copyright (C) 2014 Bastiaan Welmers, bastiaan@welmers.net
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License version 2 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * version 2 along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package bast1aan.commenter;

import com.opensymphony.xwork2.util.ClassLoaderUtil;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class Settings {
	
	public final static String DRIVER = "driver";
	public final static String DSN = "dsn";
	public final static String USERNAME = "username";
	public final static String PASSWORD = "password";
	public final static String REMOTE_ADDR_HEADER = "remote_addr_header";
	
	private static Settings instance;
	
	public static Settings getInstance() {
		if (instance == null) {
			instance = new Settings();
		}
		return instance;
	}
	
	private Map<String, String> settings = new HashMap<String, String>();
	
	public Settings() {
		URL settingsUrl = ClassLoaderUtil.getResource("settings.properties", getClass());
		
		if (settingsUrl == null) {
			throw new CommenterException("settings.properties not found");
		}
		InputStream stream = null;
		
		Properties prop = new Properties();
		try {
			stream = settingsUrl.openStream();
			prop.load(stream);
			// load properties that are declared
			Field[] fields = Settings.class.getFields();
			for (Field field: fields) {
				if (Modifier.isStatic(field.getModifiers()) && field.getType() == String.class) {
					String key;
					try {
						key = (String) field.get(null);
					} catch (IllegalAccessException e) {
						throw new CommenterException("Error reading field from Settings class", e);
					}
					settings.put(key, prop.getProperty(key));
				}
			}
		} catch (IOException e) {
			throw new CommenterException("error reading settings.properties", e);
		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					throw new CommenterException("error closing settings.properties", e);
				}
			}
		}

	}

	public String get(String key) {
		return settings.get(key);
	}
}

