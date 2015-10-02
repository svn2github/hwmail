package com.hs.mail.container.config;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * This class provides an application-wide access to the Spring
 * ApplicationContext!
 * 
 * @author WDO
 */
public class ComponentManager implements ApplicationContextAware {

	private static ApplicationContext context;

	@Override
	public void setApplicationContext(ApplicationContext appContext)
			throws BeansException {
		context = appContext;
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
		return context.getBean(beanName);
	}
	
	public static <T> T getBeanOfType(Class<T> type) throws BeansException {
		return BeanFactoryUtils.beanOfType(context, type);
	}	

}
