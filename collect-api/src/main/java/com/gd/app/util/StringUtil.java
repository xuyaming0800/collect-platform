package com.gd.app.util;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {
    /**
     * 除去空格 方法描述：
     * 
     * 作者： yingzi 完成时间： May 24, 2013 1:16:30 PM 维护人员： yingzi 维护时间： May 24, 2013
     * 1:16:30 PM 维护原因: 当前版本： v3.0
     * 
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    /**
     * 全角字符转成半角字符 方法描述：
     * 
     * 作者： yingzi 完成时间： May 24, 2013 1:16:49 PM 维护人员： yingzi 维护时间： May 24, 2013
     * 1:16:49 PM 维护原因: 当前版本： v3.0
     * 
     * @param input
     * @return
     */
    public static String replaceDBC2SBC(String input) {
        if (input == null)
            return "";
        Pattern pattern = Pattern.compile("[\u3000\uff01-\uff5f]{1}");
        Matcher m = pattern.matcher(input);
        StringBuffer s = new StringBuffer();
        while (m.find()) {
            char c = m.group(0).charAt(0);
            char replacedChar = c == '　' ? ' ' : (char) (c - 0xfee0);
            m.appendReplacement(s, String.valueOf(replacedChar));
        }
        m.appendTail(s);
        return s.toString();
    }

    /**
     * 对于特殊标点的处理('',‘’)
     * 方法描述：
     *
     * 作者： yingzi
     * 完成时间： Jun 6, 2013 2:02:15 PM
     * @param input
     * @return
     */
    public static String replacePoint2Blank(String input) {
        String dest = "";
        if (input != null) {
            Pattern p = Pattern.compile("[']");
            Matcher m = p.matcher(input);
            dest = m.replaceAll("");
            dest = dest.replace("‘", "").replace("’", "");
        }
        return dest;
    }

    /**
     * 
     * 方法描述：格式化字符串
     *
     * 作者： yingzi
     * 完成时间： Jun 6, 2013 2:09:16 PM
     * @param input
     * @return
     */
    public static String formatStr(String input) {
        String dest = "";
        if (input != null) {
            //全角半角转换
            dest = replaceDBC2SBC(input);
            //特殊标点处理
            dest = replacePoint2Blank(dest);
            //空格,换行符处理
            dest = replaceBlank(dest);
        }
        return dest;
    }

    public static final String full2HalfChange(String QJstr) throws UnsupportedEncodingException {
        StringBuffer outStrBuf = new StringBuffer("");
        String Tstr = "";
        byte[] b = null;
        for (int i = 0; i < QJstr.length(); i++) {
            Tstr = QJstr.substring(i, i + 1);
            // 全角空格转换成半角空格  
            if (Tstr.equals("　")) {
                outStrBuf.append(" ");
                continue;
            }
            b = Tstr.getBytes("unicode");
            // 得到 unicode 字节数据  
            if (b[2] == -1) {
                b[3] = (byte) (b[3] + 32);
                b[2] = 0;
                outStrBuf.append(new String(b, "unicode"));
            } else {
                outStrBuf.append(Tstr);
            }
        }
        return outStrBuf.toString();
    }
    private static String getRegDataName(String dataName) {
        String result = dataName;
        String filter = "甲,乙,丙,丁,戊,己,庚,辛,壬,癸,子,丑,寅,卯,辰,巳,午,未,申,酉,戌,亥";
        String[] filters = filter.split(",");
        for (String val : filters) {
            if (dataName.endsWith(val)) {
                return dataName;
            }
        }
        String reg = "(.*)(\\w+)-{0,1}(\\w*)([\u4E00-\u9FA5]+)";
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(dataName);
        if (m.matches()) {
            String subStr = dataName.replaceAll("(.*)(\\w+)-{0,1}(\\w*)", "");
            result = dataName.substring(0, dataName.lastIndexOf(subStr));
        }
        return result;
    }
    
    /**
     * 
     * 方法描述：将数据库中的门址名称按规则返回
     * @param dataName
     * @return
     */
    public static String formatTaskName(String dataName) {
        //除去空格/换行
        String frmStr = formatStr(dataName);
        //规则匹配后再转成小写
        return getRegDataName(frmStr).toLowerCase();
    }

	public static boolean isEmpty(String str){
		
		if(str==null||str.equals("")){
			return true;
		}
		return false; 
	}
    
    public static String getPListFileStr(String ipaPath,String picPathA,String picPathB,String identifier,String appName,String version ){
    	StringBuffer sb = new StringBuffer();
    	sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
    	sb.append("<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">");
    	sb.append("<plist version=\"1.0\">");
    	   sb.append("<dict>");
    	       sb.append("<key>items</key>");
    	           sb.append("<array>");
    	           sb.append("<dict>");
    	              sb.append("<key>assets</key>");
    	                 sb.append("<array>");
    	
    	                    sb.append("<dict>");
    	                       sb.append("<key>kind</key>");
    	                       sb.append("<string>software-package</string>");
    	                       sb.append("<key>url</key>");
    	                       sb.append("<string>"+ipaPath+"</string>");
    	                    sb.append("</dict>");
    
    	                    sb.append("<dict>");
						    	sb.append("<key>kind</key>");
						    	sb.append("<string>display-image</string>");
						    	sb.append("<key>needs-shine</key>");
						    	sb.append("<true/>");
						    	sb.append("<key>url</key>");
						    	sb.append("<string>"+picPathA+"</string>");
    	                    sb.append("</dict>");
    	
    	                    sb.append("<dict>");
						    	sb.append("<key>kind</key>");
						    	sb.append("<string>full-size-image</string>");
						    	sb.append("<key>needs-shine</key>");
						    	sb.append("<true/>");
						    	sb.append("<key>url</key>");
						    	sb.append("<string>"+picPathB+"</string>");
					    	sb.append("</dict>");
    	
					    sb.append("</array>");
    	
					    sb.append("<key>metadata</key>");
    	                    sb.append("<dict>");
						       sb.append("<key>bundle-identifier</key>");
						       sb.append("<string>"+identifier+"</string>");
						       sb.append("<key>bundle-version</key>");
						       sb.append("<key>"+version+"</key>");
						       sb.append("<key>kind</key>");
						       sb.append("<string>software</string>");
						       sb.append("<key>subtitle</key>");
						       sb.append("<string>"+appName+"</string>");
						       sb.append("<key>title</key>");
						       sb.append("<string>"+appName+"</string>");
    	                    sb.append("</dict>");
    	            
    	          sb.append("</dict>");
    	      sb.append("</array>");
    	   sb.append("</dict>");
    	sb.append("</plist>");
    
    	return sb.toString();
    }
//    public static void main(String[] args) {
//        String test = "中国人民英雄纪念碑　－－－－－－－‘’。。。。。００００＝＝－－－－－－＝＿＋";
//        //中国人民英雄纪念碑-------。。。。。0000==------=_+
//        //除去全角 /半角的(''号)符号。全角转半脚
//        System.out.println(formatStr(test));
//    }
}
