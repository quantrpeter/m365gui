package org.hkprog.m365gui;

import hk.quantr.javalib.CommonLib;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.logging.log4j.LogManager;
import org.junit.Test;

/**
 *
 * @author peter
 */
public class TestJava {
	private static final org.apache.logging.log4j.Logger logger = LogManager.getLogger(TestJava.class);
	
	@Test
	public void test(){
		try {
			String str=CommonLib.runCommand("/Users/peter/.nvm/versions/node/v24.2.0/bin/m365 tenant info get");
			logger.info("Command output: {}", str);
		} catch (Exception ex) {
			logger.error("Error executing command", ex);
			Logger.getLogger(TestJava.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
