package org.xserver.test;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EMail {
	static public void main(String[] args) throws Exception {
		if (args.length != 1) {
			usage("Wrong number of arguments.");
		}

		int runLength = Integer.parseInt(args[0]);

		Logger logger = LoggerFactory.getLogger(EMail.class);

		for (int i = 1; i <= runLength; i++) {
			if ((i % 10) < 9) {
				logger.debug("This is a debug message. Message number: " + i);
			} else {
				logger.warn("This is a warning message. Message number: " + i);
			}
		}

		logger.error("At last an error.", new Exception("Just testing"));

	}

	static void usage(String msg) throws UnsupportedEncodingException {
		System.err.println(msg);
		System.err.println("Usage: java " + EMail.class.getName()
				+ " runLength configFile\n"
				+ "   runLength (integer) the number of logs to generate\n"
				+ "   configFile a logback configuration file in XML format."
				+ " XML files must have a '.xml' extension.");
		System.exit(1);
	}
}