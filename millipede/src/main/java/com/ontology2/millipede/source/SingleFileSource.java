package com.ontology2.millipede.source;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import com.ontology2.millipede.Codec;
import com.ontology2.millipede.FileOpener;
import com.ontology2.millipede.IdentityCodec;

public class SingleFileSource<T> implements Source<T> {
	
	private final Codec<T> codec;
	private final File file;
	InputStream stream;
	BufferedReader reader;
	String nextLine;
	long count;
	long chars;
	
	public SingleFileSource(Codec<T> codec,String filename) throws Exception {
		this.codec=codec;
		this.file=new File(filename).getAbsoluteFile();
		
		FileOpener opener=new FileOpener();
		stream=opener.createInputStream(filename);
		
	    reader=new BufferedReader(new InputStreamReader(stream,"UTF-8"));
	    nextLine=reader.readLine();
	}

	@Override
	public boolean hasMoreElements() {
		return (nextLine!=null);
	}

	@Override
	public T nextElement() throws Exception {
		String output=nextLine;
		nextLine=reader.readLine();

		if (nextLine!=null) {
			count++;
			chars += nextLine.length();
		}

		return codec.decode(output);
	}
	
	public static SingleFileSource<String> createRaw(String filename) throws Exception {
		return new SingleFileSource(new IdentityCodec(),filename);
	}
	
	public long getCount() {
		return count;
	}
	
	public long getChars() {
		return chars;
	}
	
	public File getFile() {
		return file;
	}
}
