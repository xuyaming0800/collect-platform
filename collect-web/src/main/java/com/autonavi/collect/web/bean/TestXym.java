package com.autonavi.collect.web.bean;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.sf.json.JSONArray;

import org.apache.commons.lang.time.DateUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TestXym {
	private Logger logger = LogManager.getLogger(this.getClass());

	public static void main(String[] s) throws Exception {
		String data="2014-01-01 15:00:08";
		System.out.print(DateUtils.parseDate(data, new String[]{"yyyy-MM-dd HH:mm:ss"}));
	}

	private void write2Image(File item, String filePath) {
		try {

			ZipInputStream Zin = new ZipInputStream(new FileInputStream(item));// 输入源zip路径
			BufferedInputStream Bin = new BufferedInputStream(Zin);
			InputStreamReader inR = new InputStreamReader(Bin);
			BufferedReader buf = new BufferedReader(inR);
			String Parent = filePath;
			ZipEntry entry;
			File Fout = null;
			try {
				while ((entry = Zin.getNextEntry()) != null
						&& !entry.isDirectory()) {
					if (entry.getName().equals("ImageInfo.txt")) {

						String line;
						StringBuffer buffer = new StringBuffer();
						while ((line = buf.readLine()) != null) {
							buffer.append(line);
						}
						JSONArray json = JSONArray
								.fromObject(buffer.toString());
						System.out.println(json.size());

					} else {
						// Fout=new File(Parent,entry.getName());
						// FileOutputStream out=new FileOutputStream(Fout);
						// BufferedOutputStream Bout=new
						// BufferedOutputStream(out);
						// byte[] buf = new byte[1024];
						// int length=0;
						// while ((length = Bin.read(buf)) != -1) {
						// Bout.write(buf, 0, length);
						// }
						// Bout.close();
						// out.close();
						System.out.println(entry.getName() + "解压成功");
					}

				}
				buf.close();
				inR.close();
				Bin.close();
				Zin.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			logger.warn("生成上传图片异常", e);

		}
	}

}
