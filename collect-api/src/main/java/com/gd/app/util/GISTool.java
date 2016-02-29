package com.gd.app.util;

public class GISTool {
    /**
     * 求另一点的经纬度
     * 
     * @param s
     *            距离
     * @param x
     *            当前点纬度
     * @param y
     *            当前点经度
     */
    public static LanLngs getOtherPoint(double s, double x, double y) throws Exception {
        /**
         * 地球半径
         */
        double R = 6378137;
        /**
         * 同纬度
         */
        double x1 = x - 2 * Math.asin(Math.sin(s / (2 * R)) / Math.cos(y * Math.PI / 180)) * 180
                    / Math.PI;
        double x2 = x + 2 * Math.asin(Math.sin(s / (2 * R)) / Math.cos(y * Math.PI / 180)) * 180
                    / Math.PI;
        /**
         * 同经度
         */
        double y1 = y - (2 * Math.asin(Math.sin(s / (2 * R))) * 180) / Math.PI;
        double y2 = y + (2 * Math.asin(Math.sin(s / (2 * R))) * 180) / Math.PI;
        LanLngs lanLngs = new LanLngs();
        lanLngs.setX1(x1);
        lanLngs.setX2(x2);
        lanLngs.setY1(y1);
        lanLngs.setY2(y2);
        return lanLngs;
    }

    /**
     * 求两点间距离
     * 
     * @param lanLngs
     * @return
     */
    public static double getDistance(LanLngs lanLngs) throws Exception {
        double latRadians1 = lanLngs.getY1() * (Math.PI / 180);
        double latRadians2 = lanLngs.getY2() * (Math.PI / 180);
        double latRadians = latRadians1 - latRadians2;
        double lngRadians = lanLngs.getX1() * (Math.PI / 180) - lanLngs.getX2() * (Math.PI / 180);
        double f = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(latRadians / 2), 2)
                                           + Math.cos(latRadians1) * Math.cos(latRadians2)
                                           * Math.pow(Math.sin(lngRadians / 2), 2)));
        return f * 6378137;
    }

    public static long getAngle(double px1, double py1, double px2, double py2) {
        //两点的x、y值
        double x = px2 - px1;
        double y = py2 - py1;
        double hypotenuse = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //斜边长度
        double cos = x / hypotenuse;
        double radian = Math.acos(cos);
        //求出弧度
        double angle = 180 / (Math.PI / radian);
        //用弧度算出角度
        if (y < 0) {
            //angle = -angle;
            angle += 90;
        } else if ((y == 0) && (x < 0)) {
            angle = 270;
        } else {
            if (angle <= 90) {
                angle = 90 - angle;
            } else {
                angle = 450 - angle;
            }
        }
        return Math.round(angle);
    }

//    public static void main(String[] args) {
//        String test1="116.299034&Y=39.980797 116.30513227777777 39.98207333333333";
//        String test = "x1=116.303673&y1=39.946858&x2=116.38813&y2=39.916846";
//        String offset=CoordinateConvert.GPS2Deflexion("116.299034,39.980797");
//        double x1=Double.parseDouble(offset.split(",")[0]);
//        double y1=Double.parseDouble(offset.split(",")[1]);
//        
//        LanLngs lans = new LanLngs();
//        lans.setX1(x1);
//        lans.setY1(y1);
//        lans.setX2(116.30513227777777);
//        lans.setY2(39.98207333333333);
//        try {
//            System.out.println(getDistance(lans));
//        } catch (Exception e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }
}
