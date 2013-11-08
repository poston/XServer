package org.xserver.component.dynamic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;

public class XServerClassLoader extends ClassLoader {
	private String[] dirs;

	public XServerClassLoader(String path) {
		dirs = path.split(System.getProperty("path.separator"));
	}

	public XServerClassLoader(String path, ClassLoader parent) {
		super(parent);
		dirs = path.split(System.getProperty("path.separator"));
	}

	@Override
	public synchronized Class<?> findClass(String name)
			throws ClassNotFoundException {
		for (int i = 0; i < dirs.length; i++) {
			byte[] buf = getClassData(dirs[i], name);
			if (buf != null) {
				return defineClass(name, buf, 0, buf.length);
			}
		}

		throw new ClassNotFoundException();
	}

	protected byte[] getClassData(String directory, String name) {
		String classFile = directory + "/" + name.replace('.', '/') + ".class";
		int classSize = (new Long((new File(classFile)).length())).intValue();

		byte[] buf = new byte[classSize];

		try {
			FileInputStream fileIn = new FileInputStream(classFile);
			classSize = fileIn.read(buf);
			fileIn.close();
		} catch (FileNotFoundException e) {
			return null;
		} catch (IOException e) {
			return null;
		}

		return buf;
	}

	public static void main(String[] args) {
		XServerClassLoader xServerClassLoader = new XServerClassLoader(
				"XServer/src");

		try {
			Class<?> c = xServerClassLoader
					.loadClass("org.xserver.bootstrap.BootstrapManager");
			Method m = null;
			try {
				m = c.getMethod("invoke", null);
				m.invoke(null, null);
			} catch (Exception e) {
				System.out.println("No invoke function.");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
