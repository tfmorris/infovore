package com.ontology2.millipede;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Serializable;
import java.util.List;
import java.util.zip.GZIPInputStream;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.io.Files;

//
// Automatically handles file compression and charsets
//

public class FileOpener {
	
	public BufferedReader createBufferedReader(String filename) throws Exception {
		return new BufferedReader(createReader(filename));
	}
	
	public Reader createReader(String filename) throws Exception {
		return new InputStreamReader(createInputStream(filename),"UTF-8");
	}
	
	public PrintWriter createWriter(String filename) throws Exception {
		return new PrintWriter(new OutputStreamWriter(createOutputStream(filename),"UTF-8"));
	}
	
	public OutputStream createOutputStream(String filename) throws IOException {
		OutputStream stream;
		Files.createParentDirs(new File(filename));
		return new FileOutputStream(filename);
	}

	public InputStream createInputStream(String filename) throws Exception {
		InputStream stream;
		if (filename.endsWith(".gz")) {
			InputStream innerStream=new FileInputStream(filename);
			stream=new GZIPInputStream(innerStream);
		} else if (filename.endsWith(".bz2")) {
			stream=decompressWithExternalBzip(filename);
		} else {
			stream=new FileInputStream(filename);
		}
		
		return stream;
	}
	
	public InputStream decompressWithExternalBzip(String filename) throws Exception {

		List<String> bzcatPaths=Lists.newArrayList();
		bzcatPaths.add("c:/cygwin/bin/bzcat.exe");
		
		String path=System.getenv("PATH");
		Iterable<String> parts=Splitter.on(System.getProperty("path.separator")).split(path);
		for(String dir:parts) {
			bzcatPaths.add(dir+"/bzcat");
			bzcatPaths.add(dir+"/bzcat.exe");
		}
		
		for(String bzcatPath:bzcatPaths) {
			if (new File(bzcatPath).canExecute()) {
				Process p=Runtime.getRuntime().exec(new String[] {
						bzcatPath,
						filename
				});
				
				return p.getInputStream();
			}
		}
		
		throw new Exception("Could not find a working copy of bzip");
	}

	public ObjectOutputStream createObjectOutputStream(String filename) throws IOException {
		return new ObjectOutputStream(createOutputStream(filename));
	}

	public void writeObject(String file, Serializable that) throws Exception {
		ObjectOutputStream oos=new ObjectOutputStream(createOutputStream(file));
		oos.writeObject(that);
		oos.close();
		
	}

	public <T extends Serializable> T readObject(String file) throws Exception {
		ObjectInputStream ois=new ObjectInputStream(createInputStream(file));
		try {
			return (T) ois.readObject();
		} finally {
			ois.close();
		}
	}
}
