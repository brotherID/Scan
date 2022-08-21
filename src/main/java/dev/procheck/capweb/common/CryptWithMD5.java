package dev.procheck.capweb.common;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
/**
 * Object pour cryptage et hashage 25/05/2022
 * @author K.ABIDA
 *
 */
public class CryptWithMD5 {
	 private static MessageDigest md;

	 /**
	  * Permit de crpyter une chaine avec l'anlgorithme MD5
	  * @param pass la chaine en question
	  * @return la chaine cryptée
	  */
	   public static String cryptWithMD5(String pass){
	    try {
	        md = MessageDigest.getInstance("MD5");
	        byte[] passBytes = pass.getBytes();
	        md.reset();
	        byte[] digested = md.digest(passBytes);
	        StringBuffer sb = new StringBuffer();
	        for(int i=0;i<digested.length;i++){
	            sb.append(Integer.toHexString(0xff & digested[i]));
	        }
	        return sb.toString();
	    } catch (NoSuchAlgorithmException ex) {
	    }
	        return null;
	   }
	   
	  
	   
}