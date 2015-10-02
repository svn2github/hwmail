package com.hs.mail.webmail.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.MissingServletRequestParameterException;

public class RequestUtils {
	
	private RequestUtils() {
	}
	
	public static String getParameter(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			try {
				String encoding = request.getCharacterEncoding();
				// If the request.getCharacterEncoding() is null, the default
				// parsing value of the String is ISO-8859-1.
				return new String(value.getBytes((null == encoding) 
							? "ISO-8859-1" : encoding), "UTF-8");
			} catch (Exception ex) {
			}
		}
		return value;
	}

	public static String getParameter(HttpServletRequest request, String name,
			String defaultValue) {
		String value = getParameter(request, name);
		return (value != null) ? value : defaultValue;
	}

	public static String getRequiredParameter(HttpServletRequest request,
			String name) throws MissingServletRequestParameterException {
		String value = getParameter(request, name);
		if (value == null) {
			throw new MissingServletRequestParameterException(name,
					String.class.getName());
		}
		return value;
	}
	
	public static String[] getParameterValues(HttpServletRequest request,
			String name) {
		String[] values = request.getParameterValues(name);
		if (values != null) {
			try {
				String encoding = request.getCharacterEncoding();
				for (int i = 0; i < values.length; i++) {
					values[i] = (null == values[i]) 
						? null 
						: new String(values[i].getBytes((null == encoding) 
								? "ISO-8859-1" : encoding), "UTF-8");
				}
			} catch (Exception ex) {
			}
		}
		return values;
	}

	public static boolean getParameterBool(HttpServletRequest request,
			String name) {
		String value = getParameter(request, name);
		return Boolean.valueOf(value).booleanValue();
	}
	
	public static boolean getParameterBool(HttpServletRequest request,
			String name, boolean defaultValue) {
		String value = getParameter(request, name, Boolean.toString(defaultValue));
		return Boolean.valueOf(value).booleanValue();
	}
	
	public static int getParameterInt(HttpServletRequest request, String name)
			throws MissingServletRequestParameterException {
		String value = getParameter(request, name);
		if (value != null) {
			return toInt(value.trim());
		} else {
			throw new MissingServletRequestParameterException(name,
					Integer.class.getName());
		}
	}
	
	public static int getParameterInt(HttpServletRequest request, String name,
			int defaultValue) {
		try {
			return getParameterInt(request, name);
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public static int[] getParameterInts(HttpServletRequest request, String name) {
		String[] values = request.getParameterValues(name);
		return (values != null) ? toInts(values) : null;
	}

	public static int[] getParameterInts(HttpServletRequest request,
			String name, char separator) {
		String value = request.getParameter(name);
		return (value != null) ? toInts(StringUtils.split(value, separator))
				: null;
	}

	public static Date parseDate(String value, String format) {
		if (value != null) {
			try {
				return new SimpleDateFormat(format).parse(StringUtils.replace(
						value, "-", ".", 2));
			} catch (ParseException e) {
				throw new TypeMismatchException(value, Date.class);
			}
		}
		return null;
	}

	public static Date getParameterDate(HttpServletRequest request, String name) {
		String value = request.getParameter(name);
		if (value != null) {
			if (value.length() == 19)
				return parseDate(value, "yyyy.MM.dd HH:mm:ss");
			else if (value.length() == 16)
				return parseDate(value, "yyyy.MM.dd HH:mm");
			else
				return parseDate(value, "yyyy.MM.dd");
		}
		return null;
	}
	
	public static Date getParameterDate(HttpServletRequest request,
			String name, boolean ceil) {
        String value = request.getParameter(name);
        if (value != null) {
            if (value.length() == 10) {
                return (ceil) ? parseDate(value + " 23:59:59",
                        "yyyy.MM.dd HH:mm:ss") : parseDate(value, "yyyy.MM.dd");
            } else {
                return parseDate(value, "yyyy.MM.dd HH:mm:ss");
            }
        }
        return null;
    }

	public static int toInt(String number) {
		try {
			return Integer.parseInt(number);
		} catch (Exception ex) {
			throw new TypeMismatchException(number, Integer.class);
		}
	}

	public static int[] toInts(String[] values) {
		int[] numbers = new int[values.length];
		for (int i = 0; i < values.length; i++) {
			numbers[i] = toInt(values[i].trim());
		}
		return numbers;
	}

}
