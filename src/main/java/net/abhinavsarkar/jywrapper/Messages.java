package net.abhinavsarkar.jywrapper;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

final class Messages {
	private static final String BUNDLE_NAME = "net.abhinavsarkar.jywrapper.messages"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE =
		ResourceBundle.getBundle(BUNDLE_NAME);

	private Messages() {
	}

	public static String getString(final String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (final MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	public static String _(final String messageKey, final Object... arguments) {
		return new MessageFormat(getString(messageKey))
			.format(arguments, new StringBuffer(), null).toString();
	}
}
