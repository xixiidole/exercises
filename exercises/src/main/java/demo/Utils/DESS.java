package demo.Utils;

import java.security.Key;
import java.security.SecureRandom;
import java.security.Security;
import java.util.Date;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

public class DESS {

	private final static String DES = "DES";

	public static Throwable throwmsg(Throwable throwable, Throwable throwable1) {
		try {
			throwable.getClass().getMethod("initCause", new Class[] {

			java.lang.Throwable.class })
					.invoke(throwable, new Object[] { throwable1 });
		} catch (Exception exception) {
		}
		return throwable;
	}

	/**
	 * 解密 ，输入String密文，返回String明文，方法中实际�?将密文和key转换成byte[]作为参数调用本类中的字节数组加密方法
	 * 
	 * @param data
	 * @return 142680csm
	 * @throws Exception
	 */

	private String getStr() {
		try {

			int i = 0, b = 0, c = 0, d = 0;
			b = (-(i + ~i) << 2) - (i + ~i);
			c = -(i + ~i) << 2;
			d = ~i;
			i -= (i += (~i + ~i) >> 1);
			i += "Hello World".charAt((-(i + ~i) << 2) - (i + ~i)) >> (-(i + ~i) << 2)
					- (i + ~i);
			String strKey = "" + i;
			return strKey;
		} catch (Exception exception) {
			this.throwmsg(exception.fillInStackTrace(),
					exception.fillInStackTrace());
			return this.toString();
		}
	}

	private String ùgetStr() {
		return "";
	}

	private String ìgetStr() {
		return "";
	}

	public String decrypt(String data) {
		// 先定义一个明文字符串，赋值为�?
		String decStr = "";
		try {
			// 解密，实际上是调用的字节串解密的方法。先要将字符串型的密文和key转换成字节数�?
			decStr = new String(decrypt(hex2byte(data.getBytes("UTF-8")), this
					.ùgetKey().getBytes()));
		} catch (Exception e) {
			decStr = "csm analysis error";
		}
		return decStr;
	}

	/**
	 * 
	 * 加密，输入明文信息，返回密文 。方法中实际是将明文和key转换成byte[] 作为参数调用类中的字节数组解密方法�?
	 * 
	 * @param massage
	 * @return
	 * @throws Exception
	 */
	public String encrypt(String massage) {
		// 先定义一个密文字符串，赋值为�?
		String encStr = "";
		try {
			// 加密，调用字节串加密方法。先将key和密文字符串转换成字节数�?
			encStr = encrypt(massage.getBytes("UTF-8"), this.ùgetKey()
					.getBytes());
		} catch (Exception e) {
			encStr = "encrypt false";
		}
		return encStr;
	}

	/**
	 * 字节数组加密，输入字节数组型的数据源和密钥，返回String型的密文�?
	 * 
	 * @param src
	 *            数据�?
	 * @param key
	 *            密钥，长度必须是8的�?�?
	 * @return 返回加密后的数据
	 * @throws Exception
	 */
	public String encrypt(byte[] src, byte[] key) throws Exception {
		byte[] dencryptedData = null;
		String result = null;
		// DES算法要求有一个可信任的随机数�?
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建�?��密匙工厂，然后用它把DESKeySpec转换�?
		// �?��SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成加密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.ENCRYPT_MODE, securekey, sr);
		// 现在，获取数据并加密
		// 正式执行加密操作
		dencryptedData = cipher.doFinal(src);
		result = byte2hex(dencryptedData);
		return result;
	}

	/**
	 * 解密 ，输入字节数组型的密文和密钥，返回String型的明文
	 * 
	 * @param src
	 *            数据�?
	 * @param key
	 *            密钥，长度必须是8的�?�?
	 * @return 返回解密后的原始数据
	 * @throws Exception
	 */
	public String decrypt(byte[] src, byte[] key) throws Exception {
		byte[] dencryptedData = null;
		String result = null;
		// DES算法要求有一个可信任的随机数�?
		SecureRandom sr = new SecureRandom();
		// 从原始密匙数据创建一个DESKeySpec对象
		DESKeySpec dks = new DESKeySpec(key);
		// 创建�?��密匙工厂，然后用它把DESKeySpec对象转换�?
		// �?��SecretKey对象
		SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(DES);
		SecretKey securekey = keyFactory.generateSecret(dks);
		// Cipher对象实际完成解密操作
		Cipher cipher = Cipher.getInstance(DES);
		// 用密匙初始化Cipher对象
		cipher.init(Cipher.DECRYPT_MODE, securekey, sr);
		// 现在，获取数据并解密
		// 正式执行解密操作
		dencryptedData = cipher.doFinal(src);
		result = new String(dencryptedData, "UTF-8");
		return result;
	}

	/**
	 * 二行制转字符�?
	 * 
	 * @param b
	 * @return
	 */
	private String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));
			if (stmp.length() == 1)
				hs = hs + "0" + stmp;
			else
				hs = hs + stmp;
		}
		return hs.toUpperCase();
	}

	/**
	 * 字符串转二进�?
	 * 
	 * @param b
	 * @return
	 */
	private byte[] hex2byte(byte[] b) {
		if ((b.length % 2) != 0) {
			throw new IllegalArgumentException("长度不是偶数");
		}
		byte[] b2 = new byte[b.length / 2];
		for (int n = 0; n < b.length; n += 2) {
			String item = new String(b, n, 2);
			b2[n / 2] = (byte) Integer.parseInt(item, 16);
		}
		return b2;
	}

	private String ùgetKey() {
		Random rand = new Random();
		int i = rand.nextInt(10000), t = 0;
		try {
			String tmp = "";
			// 100/2-(-1)
			t = ("abdade".charAt(-(i + ~i) << 1 + 1) >> (-(i + ~i) << 0))
					+ (i + ~i);// 3
			tmp += "" + (char) t;
			// System.out.println(tmp);
			// 106/2-(-1)
			t = ("asja2e".charAt(-(i + ~i) << 1) >> (-(i + ~i) << 0))
					+ (i + ~i);
			tmp += "" + (char) t;
			// System.out.println(tmp);
			// 100/2
			t = ("adj34e".charAt(-(i + ~i) << 0) >> (-(i + ~i) << 0));
			tmp += "" + (char) t;
			// System.out.println(tmp);
			// 100/2+4
			t = ("rdy3we".charAt(-(i + ~i) << 0) >> (-(i + ~i) << 0))
					+ (-(i + ~i) << 2);
			tmp += "" + (char) t;
			// System.out.println(tmp);

			tmp += "sd8erw4".charAt(-(i + ~i) << 1);
			// System.out.println(tmp);

			tmp += "0csm";
			// System.out.println(tmp);
			return tmp;
		} catch (Exception exception) {
			return this.toString();
		}
	}

	public String getKey() {
		String strKey = "nykj201099";
		return strKey;
	}
	
	
	
	/**
     * 字符串默认键�?
     */
    private static String strDefaultKey = "mysecret";

    /**
     * 加密工具
     */
    private Cipher encryptCipher = null;

    /**
     * 解密工具
     */
    private Cipher decryptCipher = null;

    /**
     * 将byte数组转换为表�?6进制值的字符串， 如：byte[]{8,18}转换为：0813�?和public static byte[] hexStr2ByteArr(String strIn) 互为可�?的转换过�?
     *
     * @param arrB �?��转换的byte数组
     * @return 转换后的字符�?
     * @throws Exception 本方法不处理任何异常，所有异常全部抛�?
     */
    public static String byteArr2HexStr(byte[] arrB) throws Exception {
        int iLen = arrB.length;
        // 每个byte用两个字符才能表示，�?��字符串的长度是数组长度的两�?
        StringBuffer sb = new StringBuffer(iLen * 2);
        for (int i = 0; i < iLen; i++) {
            int intTmp = arrB[i];
            // 把负数转换为正数
            while (intTmp < 0) {
                intTmp = intTmp + 256;
            }
            // 小于0F的数�?��在前面补0
            if (intTmp < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(intTmp, 16));
        }
        return sb.toString();
    }

    /**
     * 将表�?6进制值的字符串转换为byte数组�?和public static String byteArr2HexStr(byte[] arrB) 互为可�?的转换过�?
     *
     * @param strIn �?��转换的字符串
     * @return 转换后的byte数组
     * @throws Exception 本方法不处理任何异常，所有异常全部抛�?
     */
    public static byte[] hexStr2ByteArr(String strIn) throws Exception {
        byte[] arrB = strIn.getBytes();
        int iLen = arrB.length;

        // 两个字符表示�?��字节，所以字节数组长度是字符串长度除�?
        byte[] arrOut = new byte[iLen / 2];
        for (int i = 0; i < iLen; i = i + 2) {
            String strTmp = new String(arrB, i, 2);
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);
        }
        return arrOut;
    }

    /**
     * 默认构�?方法，使用默认密�?
     *
     * @throws Exception
     */
    public DESS() throws Exception {
    }

    /**
     * 指定密钥构�?方法
     *
     * @param strKey 指定的密�?
     * @throws Exception
     */
    public DESS(String strKey) throws Exception {
        Security.addProvider(new com.sun.crypto.provider.SunJCE());
        Key key = getKey(strKey.getBytes());

        encryptCipher = Cipher.getInstance("DES");
        encryptCipher.init(Cipher.ENCRYPT_MODE, key);

        decryptCipher = Cipher.getInstance("DES");
        decryptCipher.init(Cipher.DECRYPT_MODE, key);
    }

    /**
     * 加密字节数组
     *
     * @param arrB �?��密的字节数组
     * @return 加密后的字节数组
     * @throws Exception
     */
    public byte[] encrypt(byte[] arrB) throws Exception {
        return encryptCipher.doFinal(arrB);
    }

    /**
     * 加密字符�?
     *
     * @param strIn �?��密的字符�?
     * @return 加密后的字符�?
     * @throws Exception
     */
    public String newEncrypt(String strIn) throws Exception {
        return byteArr2HexStr(encrypt(strIn.getBytes()));
    }

    /**
     * 解密字节数组
     *
     * @param arrB �?��密的字节数组
     * @return 解密后的字节数组
     * @throws Exception
     */
    public byte[] decrypt(byte[] arrB) throws Exception {
        return decryptCipher.doFinal(arrB);
    }

    /**
     * 解密字符�?
     *
     * @param strIn �?��密的字符�?
     * @return 解密后的字符�?
     * @throws Exception
     */
    public String newDecrypt(String strIn) throws Exception {
        return new String(decrypt(hexStr2ByteArr(strIn)));
    }
    
    public String newDecrypt(byte[] strIn) throws Exception {
        return new String(decrypt(strIn));
    }

    /**
     * 从指定字符串生成密钥，密钥所�?��字节数组长度�?�?不足8位时后面�?，超�?位只取前8�?
     *
     * @param arrBTmp 构成该字符串的字节数�?
     * @return 生成的密�?
     * @throws java.lang.Exception
     */
    private Key getKey(byte[] arrBTmp) throws Exception {
        // 创建�?��空的8位字节数组（默认值为0�?
        byte[] arrB = new byte[8];

        // 将原始字节数组转换为8�?
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }

        // 生成密钥
        Key key = new javax.crypto.spec.SecretKeySpec(arrB, "DES");

        return key;
    }

    public static byte[] getKeyNew(byte[] arrBTmp) throws Exception {
        // 创建�?��空的8位字节数组（默认值为0�?
        byte[] arrB = new byte[8];
        // 将原始字节数组转换为8�?
        for (int i = 0; i < arrBTmp.length && i < arrB.length; i++) {
            arrB[i] = arrBTmp[i];
        }
        return arrB;
    }

    public static void main(String[] args) {
        try {
            String test = "12345678954353453453454354353454543534534534534543543543534";
            DESS des = new DESS("justtest");//
            
            System.out.println("-----------");
            
            String token = "515ecb29-023d-45e7-83e0-31cc195625e7";
            String timestamp=(new Date().getTime() / 1000)+"";
            
            String test2 = "12345678954353453453454354353454543534534534534543543543534";
            DESS des2 = new DESS(token+timestamp);//
            System.err.println(des2.newEncrypt(test2));
            System.err.println(des2.newDecrypt(des2.newEncrypt(test2)));
    		
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
}
