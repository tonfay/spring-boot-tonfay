package com.tonfay.cache.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BeanUtils {
	private static final Logger logger = LoggerFactory.getLogger(BeanUtils.class);

	/**
	 * 利用序列化深度克隆
	 *
	 * @param src
	 * @return
	 * @throws Exception
	 */
	public static Object clone(Object src) throws Exception {
		logger.debug("clone begin..");
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(src);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		Object newObj = in.readObject();
		logger.debug("clone success.");
		return newObj;
	}
}
