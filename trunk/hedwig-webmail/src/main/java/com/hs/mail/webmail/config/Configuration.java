package com.hs.mail.webmail.config;

import java.util.Locale;
import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.MessageSourceAccessor;

import com.hs.mail.webmail.dao.PreferencesDAO;

public class Configuration implements InitializingBean, ApplicationContextAware {

	private static Properties properties;

	private static MessageSourceAccessor messageSourceAccessor;
	
	private static ApplicationContext appContext;
	
	public static ApplicationContext getApplicationContext() {
		return appContext;
	}

	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		appContext = context;
	}

	/**
	 * This is about the same as context.getBean("beanName"), except it has its
	 * own static handle to the Spring context, so calling this method
	 * statically will give access to the beans by name in the Spring
	 * application context. As in the context.getBean("beanName") call, the
	 * caller must cast to the appropriate target class. If the bean does not
	 * exist, then a Runtime error will be thrown.
	 * 
	 * @param beanName
	 *            the name of the bean to get.
	 * @return an Object reference to the named bean.
	 */
	public static Object getBean(String beanName) throws BeansException {
		return appContext.getBean(beanName);
	}

	public static <T> T getBean(Class<T> type) throws BeansException {
		return appContext.getBean(type);
	}

	public void setMessageSourceAccessor(MessageSourceAccessor accessor) {
		messageSourceAccessor = accessor;
	}
	
	public static String getMessage(String code, Locale locale) {
		return messageSourceAccessor.getMessage(code, locale);
	}
	
	public void setProperties(Properties props) {
		properties = props;
	}

	public static String getProperty(String key) {
		return properties.getProperty(key);
	}
	
	public static PreferencesDAO getPreferencesDAO() {
		return (PreferencesDAO) getBean(PreferencesDAO.class);
	}

	public void afterPropertiesSet() throws Exception {
	}

}
