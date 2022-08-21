package dev.procheck.capweb.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import dev.procheck.capweb.entities.User;


public class Methodes {
		
	public static int GenerateRandom() {
		int min = 0;
		int max = 1999999999;

		int random_int = (int) (Math.random() * (max - min + 1) + min);
		return random_int;
	}

	public static String getId() {

		SimpleDateFormat f = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		String s = f.format(new Date());
		return s;
	}

	public static long getDifferenceDays(Date d1, Date d2) {
		long diff = d2.getTime() - d1.getTime();
		return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
	}

	public static String calculRefRemise(User u) {
		//001{D;DDMMYY;6;;;DATESYS}-{D;S;3;L;0;SEQ} donne 001150222-001
		String formule =u.getParamRefRemise().getFormule();//{V;;3;;;PC}{D;ddMM;4;;;DATESYS}{S;;2;L;0;SEQ}
	    Pattern pattern = Pattern.compile("\\{(.*?)\\}");//Defines a pattern (to be used in a search)
	    Matcher matcher = pattern.matcher(formule);//Used to search for the pattern
	   while (matcher.find()) {//si on a trouvé le pattern
	    	String partFor=matcher.group(1);//  V;;3;;;PC  D;ddMM;4;;;DATESYS  S;;2;L;0;SEQ
	    	String partForF=formatPartOfFormule(partFor,u);
	    	formule=formule.replace(partFor, partForF);
	    }
	    formule=formule.replace("{", "");
	    formule=formule.replace("}", "");
		return formule;
	}

	private static String formatPartOfFormule(String partFor, User u) {
		// {type;format;longueur;L/R;padcaractre;data}
		String[] comp = partFor.split(";"); // D ddMM 4 DATESYS
		if (comp[0].equals("D")) {
			// {D;ddMM;4;;;DATESYS}--> 1802
			SimpleDateFormat dateFormat = new SimpleDateFormat(comp[1]); // ddMM=1802
			if (comp[5].equals("DATESYS"))
				partFor = dateFormat.format(new Date());// 18-02-2022
		} else if (comp[0].equals("S")) {
			// {S;;2;L;0;SEQ}--> 001
			int seqF = u.getSequence() == null || u.getSequence() == 0 ? 1 : u.getSequence();
			int longueur = Integer.parseInt(comp[2]);// 2
			String padcaractre = comp[4];// 0
			if (comp[3].equals("L"))//
				partFor = StringUtils.leftPad(seqF + "", longueur, padcaractre);// longueur de chaine =2 + 01
			else if (comp[3].equals("R"))
				partFor = StringUtils.rightPad(seqF + "", longueur, padcaractre);
			else {
				partFor = seqF + "";
			}
		} else if (comp[0].equals("V")) {
			// {V;;3;;;PC}--> 019
			int longueur = Integer.parseInt(comp[2]);// 3
			String padcaractre = comp[4];// ""
			String data = comp[5];// PC
			if (data.equals("PC")) {
				data = u.getCapturePoint().substring(2);// 00019 -> 019
			} else if (data.equals("PCC")) {
				data = u.getCapturePoint();
			}
			if (comp[3].equals("L")) {
				partFor = StringUtils.leftPad(data, longueur, padcaractre);
			} else if (comp[3].equals("R")) {
				partFor = StringUtils.rightPad(data, longueur, padcaractre);
			} else {
				partFor = data;
			}
		}
		return partFor;
	}
}
