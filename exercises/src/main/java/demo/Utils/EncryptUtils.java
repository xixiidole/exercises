package demo.Utils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 常用加密算法工具�?
 * 
 * @author cq
 */
public class EncryptUtils {

	/**
	 * 用MD5算法进行加密 [以后版本中将不在使用]<br>
	 * 使用新的方式，参考：http://wiki.51good.cn:8090/pages/viewpage.action?pageId=4719777
	 * 
	 * @param str
	 *            �?��加密的字符串
	 * @return MD5加密后的结果
	 */
	public static String encodeMD5String(String str) {
		return encode(str, "MD5");
	}

	/**
	 * 用SHA算法进行加密
	 * 
	 * @param str
	 *            �?��加密的字符串
	 * @return SHA加密后的结果
	 */
	public static String encodeSHAString(String str) {
		return encode(str, "SHA");
	}

	private static String encode(String str, String method) {
		MessageDigest md = null;
		String dstr = null;
		try {
			md = MessageDigest.getInstance(method);
			md.update(str.getBytes());
			dstr = new BigInteger(1, md.digest()).toString(16);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return dstr;
	} // 密钥�?6位长度的byte[]进行Base64转换后得到的字符�?

	public static String key = "HNCSLQTLmMGStGtOpF4xNyvYt54EQ==";

	/**
	 * <li>
	 * 方法名称:encrypt</li> <li>
	 * 功能描述:
	 * 
	 * <pre>
	 * 解密方法
	 * </pre>
	 * 
	 * </li>
	 * 
	 * @param xmlStr
	 *            �?��解密的消息字符串
	 * @return 解密后的字符�?
	 * @throws Exception
	 */
	public static String decrypt(String xmlStr) throws Exception {
		// base64解码
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] encBuf = null;
		try {
			encBuf = decoder.decodeBuffer(xmlStr);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 取密钥和偏转向量
		byte[] key = new byte[8];
		byte[] iv = new byte[8];
		getKeyIV(EncryptUtils.key, key, iv);

		SecretKeySpec deskey = new SecretKeySpec(key, "DES");
		IvParameterSpec ivParam = new IvParameterSpec(iv);

		// 使用DES算法解密
		byte[] temp = null;
		try {
			temp = EncryptUtils.DES_CBC_Decrypt(encBuf, deskey, ivParam);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 进行解密后的md5Hash校验
		byte[] md5Hash = null;
		try {
			md5Hash = EncryptUtils.MD5Hash(temp, 16, temp.length - 16);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 进行解密校检
		for (int i = 0; i < md5Hash.length; i++) {
			if (md5Hash[i] != temp[i]) {
				// System.out.println(md5Hash[i] + "MD5校验错误�? + temp[i]);
				throw new Exception("MD5校验错误");
			}
		}

		// 返回解密后的数组，其中前16位MD5Hash码要除去�?
		return new String(temp, 16, temp.length - 16, "utf-8");
	}

	/**
	 * <li>
	 * 方法名称:TripleDES_CBC_Encrypt</li> <li>
	 * 功能描述:
	 * 
	 * <pre>
	 * 经过封装的三重DES/CBC加密算法，如果包含中文，请注意编码�?
	 * </pre>
	 * 
	 * </li>
	 * 
	 * @param sourceBuf
	 *            �?��加密内容的字节数组�?
	 * @param deskey
	 *            KEY �?4位字节数组�?过SecretKeySpec类转换�?成�?
	 * @param ivParam
	 *            IV偏转向量，由8位字节数组�?过IvParameterSpec类转换�?成�?
	 * @return 加密后的字节数组
	 * @throws Exception
	 */
	public static byte[] TripleDES_CBC_Encrypt(byte[] sourceBuf,
			SecretKeySpec deskey, IvParameterSpec ivParam) throws Exception {
		byte[] cipherByte;
		// 使用DES对称加密算法的CBC模式加密
		Cipher encrypt = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");

		encrypt.init(Cipher.ENCRYPT_MODE, deskey, ivParam);

		cipherByte = encrypt.doFinal(sourceBuf, 0, sourceBuf.length);
		// 返回加密后的字节数组
		return cipherByte;
	}

	/**
	 * <li>
	 * 方法名称:TripleDES_CBC_Decrypt</li> <li>
	 * 功能描述:
	 * 
	 * <pre>
	 * 经过封装的三重DES / CBC解密算法
	 * </pre>
	 * 
	 * </li>
	 * 
	 * @param sourceBuf
	 *            �?��解密内容的字节数�?
	 * @param deskey
	 *            KEY �?4位字节数组�?过SecretKeySpec类转换�?成�?
	 * @param ivParam
	 *            IV偏转向量，由6位字节数组�?过IvParameterSpec类转换�?成�?
	 * @return 解密后的字节数组
	 * @throws Exception
	 */
	public static byte[] TripleDES_CBC_Decrypt(byte[] sourceBuf,
			SecretKeySpec deskey, IvParameterSpec ivParam) throws Exception {

		byte[] cipherByte;
		// 获得Cipher实例，使用CBC模式�?
		Cipher decrypt = Cipher.getInstance("TripleDES/CBC/PKCS5Padding");
		// 初始化加密实例，定义为解密功能，并传入密钥，偏转向量
		decrypt.init(Cipher.DECRYPT_MODE, deskey, ivParam);

		cipherByte = decrypt.doFinal(sourceBuf, 0, sourceBuf.length);
		// 返回解密后的字节数组
		return cipherByte;
	}

	/**
	 * <li>
	 * 方法名称:DES_CBC_Encrypt</li> <li>
	 * 功能描述:
	 * 
	 * <pre>
	 * 经过封装的DES/CBC加密算法，如果包含中文，请注意编码�?
	 * </pre>
	 * 
	 * </li>
	 * 
	 * @param sourceBuf
	 *            �?��加密内容的字节数组�?
	 * @param deskey
	 *            KEY �?位字节数组�?过SecretKeySpec类转换�?成�?
	 * @param ivParam
	 *            IV偏转向量，由8位字节数组�?过IvParameterSpec类转换�?成�?
	 * @return 加密后的字节数组
	 * @throws Exception
	 */
	public static byte[] DES_CBC_Encrypt(byte[] sourceBuf,
			SecretKeySpec deskey, IvParameterSpec ivParam) throws Exception {
		byte[] cipherByte;
		// 使用DES对称加密算法的CBC模式加密
		Cipher encrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");

		encrypt.init(Cipher.ENCRYPT_MODE, deskey, ivParam);

		cipherByte = encrypt.doFinal(sourceBuf, 0, sourceBuf.length);
		// 返回加密后的字节数组
		return cipherByte;
	}

	/**
	 * <li>
	 * 方法名称:DES_CBC_Decrypt</li> <li>
	 * 功能描述:
	 * 
	 * <pre>
	 * 经过封装的DES/CBC解密算法�?
	 * </pre>
	 * 
	 * </li>
	 * 
	 * @param sourceBuf
	 *            �?��解密内容的字节数�?
	 * @param deskey
	 *            KEY �?位字节数组�?过SecretKeySpec类转换�?成�?
	 * @param ivParam
	 *            IV偏转向量，由6位字节数组�?过IvParameterSpec类转换�?成�?
	 * @return 解密后的字节数组
	 * @throws Exception
	 */
	public static byte[] DES_CBC_Decrypt(byte[] sourceBuf,
			SecretKeySpec deskey, IvParameterSpec ivParam) throws Exception {

		byte[] cipherByte;
		// 获得Cipher实例，使用CBC模式�?
		Cipher decrypt = Cipher.getInstance("DES/CBC/PKCS5Padding");
		// 初始化加密实例，定义为解密功能，并传入密钥，偏转向量
		decrypt.init(Cipher.DECRYPT_MODE, deskey, ivParam);

		cipherByte = decrypt.doFinal(sourceBuf, 0, sourceBuf.length);
		// 返回解密后的字节数组
		return cipherByte;
	}

	/**
	 * <li>
	 * 方法名称:MD5Hash</li> <li>
	 * 功能描述:
	 * 
	 * <pre>
	 * MD5，进行了�?��的封装，以�?用于加，解密字符串的校验�?
	 * </pre>
	 * 
	 * </li>
	 * 
	 * @param buf
	 *            �?��MD5加密字节数组�?
	 * @param offset
	 *            加密数据起始位置�?
	 * @param length
	 *            �?��加密的数组长度�?
	 * @return
	 * @throws Exception
	 */
	public static byte[] MD5Hash(byte[] buf, int offset, int length)
			throws Exception {
		MessageDigest md = MessageDigest.getInstance("MD5");
		md.update(buf, offset, length);
		return md.digest();
	}

	/**
	 * <li>
	 * 方法名称:byte2hex</li> <li>
	 * 功能描述:
	 * 
	 * <pre>
	 * 字节数组转换为二行制表示
	 * </pre>
	 * 
	 * </li>
	 * 
	 * @param inStr
	 *            �?��转换字节数组�?
	 * @return 字节数组的二进制表示�?
	 */
	public static String byte2hex(byte[] inStr) {
		String stmp;
		StringBuffer out = new StringBuffer(inStr.length * 2);

		for (int n = 0; n < inStr.length; n++) {
			// 字节�?�?运算，去除高位置字节 11111111
			stmp = Integer.toHexString(inStr[n] & 0xFF);
			if (stmp.length() == 1) {
				// 如果�?至F的单位字符串，则添加0
				out.append("0" + stmp);
			} else {
				out.append(stmp);
			}
		}
		return out.toString();
	}

	/**
	 * <li>
	 * 方法名称:addMD5</li> <li>
	 * 功能描述:
	 * 
	 * <pre>
	 * MD校验�?组合方法，前16位放MD5Hash码�? 把MD5验证码byte[]，加密内容byte[]组合的方法�?
	 * </pre>
	 * 
	 * </li>
	 * 
	 * @param md5Byte
	 *            加密内容的MD5Hash字节数组�?
	 * @param bodyByte
	 *            加密内容字节数组
	 * @return 组合后的字节数组，比加密内容�?6个字节�?
	 */
	public static byte[] addMD5(byte[] md5Byte, byte[] bodyByte) {
		int length = bodyByte.length + md5Byte.length;
		byte[] resutlByte = new byte[length];

		// �?6位放MD5Hash�?
		for (int i = 0; i < length; i++) {
			if (i < md5Byte.length) {
				resutlByte[i] = md5Byte[i];
			} else {
				resutlByte[i] = bodyByte[i - md5Byte.length];
			}
		}

		return resutlByte;
	}

	/**
	 * <li>
	 * 方法名称:encrypt</li> <li>
	 * 加密方法
	 * 
	 * @param xmlStr
	 *            �?��加密的消息字符串
	 * @return 加密后的字符�?
	 */
	public static String encrypt(String xmlStr) {
		byte[] encrypt = null;

		try {
			// 取需要加密内容的utf-8编码�?
			encrypt = xmlStr.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// 取MD5Hash码，并组合加密数�?
		byte[] md5Hasn = null;
		try {
			md5Hasn = EncryptUtils.MD5Hash(encrypt, 0, encrypt.length);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 组合消息�?
		byte[] totalByte = EncryptUtils.addMD5(md5Hasn, encrypt);

		// 取密钥和偏转向量
		byte[] key = new byte[8];
		byte[] iv = new byte[8];
		getKeyIV(EncryptUtils.key, key, iv);
		SecretKeySpec deskey = new SecretKeySpec(key, "DES");
		IvParameterSpec ivParam = new IvParameterSpec(iv);

		// 使用DES算法使用加密消息�?
		byte[] temp = null;
		try {
			temp = EncryptUtils.DES_CBC_Encrypt(totalByte, deskey, ivParam);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// 使用Base64加密后返�?
		return new BASE64Encoder().encode(temp);
	}
	
	/**
	 * <li>
	 * 方法名称:getKeyIV</li> <li>
	 * 功能描述:
	 * 
	 * <pre>
	 * 
	 * </pre>
	 * </li>
	 * 
	 * @param encryptKey
	 * @param key
	 * @param iv
	 */
	public static void getKeyIV(String encryptKey, byte[] key, byte[] iv) {
		// 密钥Base64解密
		BASE64Decoder decoder = new BASE64Decoder();
		byte[] buf = null;
		try {
			buf = decoder.decodeBuffer(encryptKey);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// �?位为key
		int i;
		for (i = 0; i < key.length; i++) {
			key[i] = buf[i];
		}
		// �?位为iv向量
		for (i = 0; i < iv.length; i++) {
			iv[i] = buf[i + 8];
		}
	}

	public static void main(String[] args) throws IOException {
		try {
			String licenseNo = decrypt("0TNpuna7uhgaKO38Qk5tkCJQqSiEl1YH+WReevoM3MLVcV0okvjpcx1yuFvhwpLMHeHwT+UPCjyENRDAaaLmHt+tgh3rVHsc/wtsEEoDYLDPwPc7oJP/vg==");
			//System.out.println("2017-12-30".compareTo(decrypt("tyMoS/A1OL6OVB/H3/LLRe6Rhp7LNnbQOCWPsYqhnOuKU/9hsGgp0C4YI7Rq/lVKCXcbEVSKQ4A=").split("@@")[1]));
			//String licenseNo =encrypt("蓝蜻蜓演示医院@@2019-08-24@@180189161903@@nis7.0,cdc7.0@@crbbk,sybk,syjcbk,zlbk@@O4RTSO85");
			//String licenseNos =  licenseNo.replaceAll("\r\n", "");
			//System.out.println(licenseNos);
			System.out.println(licenseNo);
			System.out.println(licenseNo.trim());
			System.out.println(licenseNo.split("@@")[5]);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String user = "qq123456789";
		// System.out.println("原始字符�?" + user);
		// System.out.println("MD5加密 " + encodeMD5String(user));
		//System.out.println("dess加密" +encodeDessString("JY000244"));
		// System.out.println("1dess解密 " + decodeDessString("1B1AAEE20E34C53A"));
		// System.out.println("SHA加密 " + encodeSHAString(user));
		//String base64Str = encodeBase64String("hncslqt99");
		//System.out.println("Base64加密 " + base64Str);
		//System.out.println("Base64解密 " + decodeBase64String("SE5jc2xxdDk5bmlzbGljZW5zZQ=="));
		//File file = new File("D:\\zlRichEditor_new.txt");
		//Base64.decode(file);
		// System.out.println("Base64加密 " + encodeMD5String("admin"));
		//System.out.println(System.currentTimeMillis());
	}
	
	/**
	 * <h2>获取CDC直报的签名数�?/h2><br/>
	 * 以timestamp(时间�?、nonce(随机�?、token(授权�?的顺序组合成数组(String[3]),
	 * 并进行Arrays.sort排序后按顺序组合成字符串后，
	 * 进行SHA1加密�?
	 * 并把加密后byte[]转换�?4位BigInteger�?
	 * 
	 * @param timestamp (时间�?
	 * @param nonce (随机�?UUID)
	 * @param token (授权�?
	 * @return
	 */
	public static String getSignature4CdcZb(String timestamp, String nonce, String token){
		String[] infos = {timestamp,nonce,token};
		Arrays.sort(infos);
		StringBuffer sbf = new StringBuffer();
		for(String info : infos){
			sbf.append(info);
		}
		return getSha1(sbf.toString());
	}
	
	public static String getSha1(String str) {

        char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            MessageDigest mdTemp = MessageDigest.getInstance("SHA1");
            mdTemp.update(str.getBytes("UTF-8"));
            byte[] md = mdTemp.digest();
            int j = md.length;
            char buf[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                buf[k++] = hexDigits[byte0 >>> 4 & 0xf];
                buf[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(buf);
        } catch (Exception e) {
            return null;
        }
    }
}