package com.zhangsl.wallet.common.util;


import com.zhangsl.wallet.common.exception.WalletException;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by zhang.shaolong on 2018/3/15
 */
public class Md5Util {

	private static final String MD5 = "MD5";

	private static final String SIGN = "sign";

	public static String md5(String signContent) {
		return md5(signContent,"UTF-8");
	}

	public String createContent(Map<String, ? extends Object> parmMap) {
		StringBuffer bu = new StringBuffer();
		List<String> keys = new ArrayList<String>(parmMap.keySet());
		Collections.sort(keys);
		parmMap.forEach((k,v)->{
			if (!SIGN.equalsIgnoreCase(k)) {
				String val = parse2String(v);
				if (!(val == null || "".equals(val))) {
					addString(bu, k, val, "UTF-8");
				}
			}

		});
		return buildString(bu);

	}


	public static boolean signCore(String signContent, String sourceSign) {
		String encryptStr = md5(signContent);
		if (!encryptStr.equals(sourceSign)) {
			throw new WalletException("加密错误");
		}
		return true;
	}

	public static String md5(String signContent, String charsetName) {
		if(!(signContent == null || "".equals(signContent))){
			return null;
		}
		return sign(signContent,charsetName);
	}

	private static String sign(String signContent, String charsetName) {
		try {
			byte[] source = signContent.getBytes(charsetName);
			MessageDigest md = MessageDigest.getInstance(MD5);
			md.update(source);
			byte b[] = md.digest();
			StringBuffer buf = new StringBuffer();
			for (int i = 0; i < b.length; i++) {
				buf.append(Integer.toHexString((b[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return buf.toString();
		} catch (Exception e) {
			throw new WalletException("Md5Util sign error",e);
		} 
	}


	private static void addString(StringBuffer bu,String key,String value ,String charset) {
		if (bu == null) {
			return ;
		}
		bu.append(key);
		bu.append("=");
		try {
			bu.append(!(charset == null || "".equals(charset)) ? URLEncoder.encode(value, charset) : value);
		} catch (UnsupportedEncodingException e) {
			bu.append(value);
		}
		bu.append("&");
	}

	public static String parse2String(Object val) {
		if (val == null) {
			return null;
		}
		if (val instanceof Integer) {
			return String.valueOf(((Integer) val).intValue());
		} else {
			return String.valueOf(val);
		}
	}

	private static String buildString(StringBuffer bu) {
		if(bu == null || bu.length() == 0) {
			return "";
		}
		return bu.substring(0, bu.length() - 1);
	}
}
