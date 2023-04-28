package org.collection.fm.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;


public class BinaryRunner {

	
	public static final String TIMEOUT_REACHED = "TIMEOUT!!";
	public static final  String MONITORING_PREFIX = "/usr/bin/time -v ";
	public static final String SCRIPT_PREFIX = "/bin/sh";
	public static final String MEMORY_ULIMIT_PREFIX = "ulimit -v ";

	public enum Status {
		SOLVED, MEMORY_LIMIT_REACHED, TIMEOUT, UNEXPECTED
	}

	public class BinaryResult {
		public String stdout;
		public Status status;
		
		public BinaryResult(String stdout, Status status) {
			this.stdout = stdout;
			this.status = status;
		}
	}


	public static BinaryResult runBinaryStatic(String command, long timeout) {
		final Runtime rt = Runtime.getRuntime();
		try {
			final Process ps = rt.exec(command);
			long pid = getPidOfProcess(ps);
				if(!ps.waitFor(timeout, TimeUnit.SECONDS)) {
					ps.destroy();
					final Process killPs = rt.exec(getKillCommand(pid));
					killPs.waitFor(10, TimeUnit.SECONDS);

				    return new BinaryRunner().new BinaryResult("", Status.TIMEOUT);
				}

			String val = "";
		    String line;
		    BufferedReader in = new BufferedReader(new InputStreamReader(ps.getInputStream()));
		    while ((line = in.readLine()) != null) {
		        val +=line + "\n";
		    }
		    BufferedReader err = new BufferedReader(new InputStreamReader(ps.getErrorStream()));
		    while ((line = err.readLine()) != null) {
		        val +=line + "\n";
		    }
			in.close();
			return new BinaryRunner().new BinaryResult(val, Status.SOLVED);
		} catch (final Exception e) {
			return new BinaryRunner().new BinaryResult("", Status.UNEXPECTED);
		}
	}
	
	
	public static long getPidOfProcess(Process ps) {
		 long pid = -1;

		    try {
		      if (ps.getClass().getName().equals("java.lang.UNIXProcess")) {
		        Field f = ps.getClass().getDeclaredField("pid");
		        f.setAccessible(true);
		        pid = f.getLong(ps);
		        f.setAccessible(false);
		      }
		    } catch (Exception e) {
		      pid = -1;
		    }
		    return pid;
	}
	
	private static String getKillCommand(long pid) {
		return "rkill " + pid;
	}
}
