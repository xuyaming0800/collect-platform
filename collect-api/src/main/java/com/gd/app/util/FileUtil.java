package com.gd.app.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import autonavi.online.framework.property.PropertiesConfigUtil;

import com.gd.app.exception.AppException;
import com.gd.app.exception.AppExceptionEnum;

public class FileUtil {
    private static Logger log =LogManager.getLogger(FileUtil.class);
    
    static final String pattern = "yyyyMM"+File.separator+"dd"; 
    
    private static String IMAGE_SUFFIX = ".jpg";
    
    /**
     * 移动文件至新目录 如果新目录中已存在此文件名，刚将已存在的文件后缀改为.old 新目录中同时会产生新的文件
     * 
     * @param file
     * @param targetDir
     */
    public static boolean moveFile(File file, String targetDir) {
        boolean result = false;
        try {
            log.info("Move:" + file.getName() + " to:" + targetDir);
            byte[] buffer = new byte[1024];
            int length = 0;
            DataInputStream dis = null;
            DataOutputStream dao = null;

            File targetFile = new File(targetDir + "/" + file.getName());
            if (targetFile.exists()) {
                log.warn("File already exist:" + targetFile.getAbsolutePath());
                renameFileInSameDir(targetFile, ".old");
                targetFile.delete();
            }

            dao = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(targetDir + "/" + file.getName())));
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            while (dis.available() > 0) {
                length = dis.read(buffer);
                dao.write(buffer, 0, length);
            }
            dao.close();
            dis.close();
            result = file.delete();
        } catch (Exception e) {
            log.error("移文件:" + file.getName() + "失败");
        }
        return result;
    }

    public static File renameFileInSameDir(File file, String subFix) {
        File targetFile = null;
        byte[] buffer = new byte[1024];
        int length = 0;
        DataInputStream dis = null;
        DataOutputStream dao = null;
        try {
            if (file.getName().indexOf(".") <= 0) {
                log.warn("File name error:" + file.getName());
                return null;
            }
            String targetFileName = file.getAbsolutePath().substring(0,
                file.getAbsolutePath().lastIndexOf("."))
                                    + subFix;

            log.info("Rename:" + file.getName() + " to:" + targetFileName);

            targetFile = new File(targetFileName);
            if (targetFile.exists()) {
                log.warn("File already exist:" + targetFile.getAbsolutePath());
            }

            dao = new DataOutputStream(new BufferedOutputStream(
                new FileOutputStream(targetFileName)));
            dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file)));
            while (dis.available() > 0) {
                length = dis.read(buffer);
                dao.write(buffer, 0, length);
            }
            dao.close();
            dis.close();
            file.delete();
            log.info("删除原文件: " + file.getAbsolutePath());
        } catch (Exception e) {
            log.warn("备份 同名文件 Error:" + e, e);
        }

        return targetFile;
    }

    public static void write2Image(InputStream in, String filePath, String fileName) {
        try {
            int totalLen = in.available();
            byte[] imageByte = new byte[totalLen];
            byte[] bytebegin = new byte[4];
            byte[] byteend = new byte[2];
            in.read(imageByte);
            for (int i = 0; i < bytebegin.length; i++) {
                bytebegin[i] = imageByte[i];
            }
            byteend[0] = imageByte[totalLen - 2];
            byteend[1] = imageByte[totalLen - 1];

            //判断图片是否为jpg格式
            if (bytebegin[0] == (byte) 0xff && bytebegin[1] == (byte) 0xd8
                && bytebegin[2] == (byte) 0xff && bytebegin[3] == (byte) 0xe0
                && byteend[0] == (byte) 0xff && byteend[1] == (byte) 0xd9) {

                log.info("获取上传文件的大小：" + totalLen);
                BufferedOutputStream outStream = new BufferedOutputStream(new FileOutputStream(
                    new File(filePath)));
                outStream.write(imageByte, 0, totalLen);
                outStream.flush();
                outStream.close();
            } else {

                log.info("上传文件不是png格式文件，文件名为【" + fileName + "】");
                throw new AppException(AppExceptionEnum.IMAGE_TYPE_ERROR);
            }
            imageByte = null; //清空数组，让数组资源回收

        } catch (AppException e) {
            throw e;

        } catch (Exception e) {
            log.warn("生成上传图片异常", e);
            throw new AppException(AppExceptionEnum.PRASE_IMAGE_ERROR);
        }
    }

    public static boolean validateImage(String imagePath) {
        try {
            File file = new File(imagePath);
            if (file.exists()) {
                RandomAccessFile is;
                is = new RandomAccessFile(file, "r");
                if (is != null) {
                    long length = is.length();
                    byte[] bytebegin = new byte[4];
                    byte[] byteend = new byte[2];
                    is.read(bytebegin);
                    is.seek(length - 2);
                    is.read(byteend);
                    is.close();
                    //jpg
                    if (bytebegin[0] == (byte) 0xff && bytebegin[1] == (byte) 0xd8
                        && bytebegin[2] == (byte) 0xff && bytebegin[3] == (byte) 0xe0
                        && byteend[0] == (byte) 0xff && byteend[1] == (byte) 0xd9) {
                        return true;
                    }
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkImageFile(String filePath, String code) {
        File file = new File(filePath);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                fis = new FileInputStream(file);
                
                byte[] buf = new byte[1024];
                int length = -1;
                while ((length = fis.read(buf)) > -1) {
                    baos.write(buf, 0, length);
                }

                md.update(baos.toByteArray());
                byte[] result = md.digest();

                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < result.length; i++) {
                    byte now = result[i];
                    String hex = Integer.toHexString(now & 0xff);
                    if (hex.length() < 2) {
                        stringBuilder.append("0");
                    }
                    stringBuilder.append(hex);
                }
                String md5ValidteStr = stringBuilder.toString();
                return code.equalsIgnoreCase(md5ValidteStr);
            } catch (Exception e) {
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {

                    }
                }
                
                if (null != baos) {
                	try {
						baos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
                }
            }
        }else{
        	log.error("图片不存在, path = "+ filePath);
        }
        return false;
    }
    
	/**
	 * 根据当前日期返回当天的图片存储目录
	 * @return 
	 * 201310/22/1/random
	 * 201310/22/3/random
	 */
	public static String getImagePath(){
		String date = DateUtil.parseString(new Date(), pattern);
		long timeStamp = System.currentTimeMillis();
		long rand = (long) ((99999 - 10000 + 1) * Math.random() + 10000);
		long i = timeStamp % 200;
		StringBuilder path = new StringBuilder(date);
		path.append(File.separator).append(i)
		.append(File.separator).append(getRandomString(5))
		.append(timeStamp).append(rand);
		return path.toString();
	}
	
	public static String getRandomString(int length) {  
	    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";  
	    Random random = new Random();  
	    StringBuffer buf = new StringBuffer();  
	    for (int i = 0; i < length; i++) {  
	        int num = random.nextInt(52);
	        buf.append(str.charAt(num == 52?51:num));  
	    }  
	    return buf.toString();  
	}  
 
	/**
	 * 判断当前目录是否存
	 * @return
	 */
	public static boolean isPathExist(String path){
		File file = new File(path);
		if(file.exists()){
			return true;
		}
		return false;
	}
	
	/**
	 * 判断当前目录是否为空 
	 * 如果为空 即创建该目录
	 * @param path
	 * @return
	 */
	public static void createPathIfNotExist(String path){
		File file = new File(path);
		if(file.exists()){
			return ;
		}
		synchronized (path) {
			 if(!file.exists()){
				 file.mkdirs();
			 }
		}
	}
	/**
	 * @param imageId
	 *            201305230751279086 201310_24_91_79091
	 * @return 201305230751279086 http://caiji.mapabc.com/gdAppServer/upload/201305230748231366.jpg, 201310_24_91_79091 http://caiji.mapabc.com/gdAppServer/img/201310/24/91/201310_24_91_79091.jpg
	 */
	public static  String getImageUrl(String imageId) {
		if (StringUtils.isEmpty(imageId)) {
			log.error("imageId参数为空");
			return StringUtils.EMPTY;
		}
		StringBuilder imgPath = new StringBuilder();
		String oldImgPath="";
		try {
			oldImgPath = PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_IMAGE_URL).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}
		if (imageId.indexOf("_") < 0) {// 201305230748231366
			log.info("该图片id来源于老图片规则,imageId = " + imageId);
			imgPath.append(oldImgPath).append(imageId).append(IMAGE_SUFFIX);
			// http://caiji.mapabc.com/gdAppServer/upload/201305230748231366.jpg
			return imgPath.toString();
		}
		if (imageId.split("_").length != 4) {
			log.error(MessageFormat.format("图片id格式错误 imageId={0}", new Object[] { imageId}));
			return StringUtils.EMPTY;
		}
		// imageId = 201310_24_91_79091
		String imgName = StringUtils.replace(imageId, "_", "/");
		String resultPath = imgName.substring(0, imgName.lastIndexOf("/"));// 201310/24/91

		String newImgPath="";
		try {
			newImgPath = PropertiesConfigUtil
					.getPropertiesConfigInstance()
					.getProperty(SysProps.PROP_IMAGE_URL_NEW).toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new AppException(AppExceptionEnum.PAGE_QUERY_ERROR);
		}

		imgPath.append(newImgPath).append(resultPath).append("/").append(imageId);
		if (!imageId.endsWith(IMAGE_SUFFIX)) {
			imgPath.append(IMAGE_SUFFIX);
		}
		// http://caiji.mapabc.com/gdAppServer/img/201310/24/91/201310_24_91_79091.jpg
		return imgPath.toString();
	}
 
}
