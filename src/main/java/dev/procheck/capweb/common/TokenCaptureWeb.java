package dev.procheck.capweb.common;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;

import com.google.common.hash.Hashing;
/*
 * Othor 	: Khalil ABIDA
 * Companie : procheck
 * Date 	: 19/01/2021
 */

public class TokenCaptureWeb {
	public SecretKey DES_KEY;
	Cipher c;
	
	// public KeyGenerator keygenerator;

	public TokenCaptureWeb(String keyFromConfig) throws Exception {
		byte[] key = fromBase64(keyFromConfig);
		DES_KEY = new SecretKeySpec(key, 0, key.length, "DESede") ;

		// To generate a key 
		/*
		 *   keygenerator = KeyGenerator.getInstance("DESede");
  			DES_KEY = keygenerator.generateKey();
		 */
		// Create the cipher
		c = Cipher.getInstance("DESede/ECB/PKCS5Padding");
	}

	public byte[] getToken(String data) throws Exception {
		c.init(Cipher.ENCRYPT_MODE, DES_KEY);
		byte[] text = Hashing.sha256().hashString(data, StandardCharsets.UTF_8).asBytes();
		byte[] textEncrypted = c.doFinal(text);
		return (textEncrypted);

	}

	/*public static void main(String[] args) {
		try {
			TokenCaptureWeb d3esecb = new TokenCaptureWeb("ekq5E27OWIn45Wv7cEwpvxWefzv7f7+w");
			//String data ="codeBanque;cdf;sysDateTime;dateRemise;user;ribRemettant;referenceRemise;montantRemise";
			String data ="011;12345;20210310091110;10/03/2021;B0021545;011123000012200001234999;000000123;8000.65";
			System.out.println("TOKEN : "+toBase64(
					d3esecb.getToken(data)));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	public static String toBase64(byte[] data) {
		return new String(Base64.getEncoder().encode(data));
	}
	public static byte[] fromBase64(String databas64) {
		return Base64.getDecoder().decode(databas64);
	}
}