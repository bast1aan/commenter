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

