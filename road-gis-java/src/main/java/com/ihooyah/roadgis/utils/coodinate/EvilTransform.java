package com.ihooyah.roadgis.utils.coodinate;



public class EvilTransform {

    public static final double SEMI_MAJOR_AXIS = 6378245.0;
    public static final double FLATTENING = 0.00335233;
    public static final double SEMI_MINOR_AXIS = SEMI_MAJOR_AXIS * (1.0 - FLATTENING);
    private static final double a = SEMI_MAJOR_AXIS;
    private static final double b = SEMI_MINOR_AXIS;
    public static final double EE = (a * a - b * b) / (a * b);

    // wgs84 转火星坐标
    public static SimpleCoodinates wgs84ToGcj02(double wgsLat, double wgsLon) {
        if (isOutOfChina(wgsLat, wgsLon)) {
            return new SimpleCoodinates(wgsLat, wgsLon);
        }
        double dLat = transformLat(wgsLon - 105.0, wgsLat - 35.0);
        double dLon = transformLon(wgsLon - 105.0, wgsLat - 35.0);
        double radLat = wgsLat / 180.0 * Math.PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((SEMI_MAJOR_AXIS * (1 - EE)) / (magic * sqrtMagic) * Math.PI);
        dLon = (dLon * 180.0) / (SEMI_MAJOR_AXIS / sqrtMagic * Math.cos(radLat) * Math.PI);
        double gcjLat = wgsLat + dLat;
        double gcjLon = wgsLon + dLon;
        return new SimpleCoodinates(gcjLat, gcjLon);
    }

    public static SimpleCoodinates gcj02ToWgs4490(double gcjLat, double gcjLon) {
        SimpleCoodinates g0 = new SimpleCoodinates(gcjLat, gcjLon);
        SimpleCoodinates w0 = new SimpleCoodinates(g0);
        SimpleCoodinates g1 = wgs84ToGcj02(w0.getLat(), w0.getLon());
        SimpleCoodinates w1 = w0.substract(g1.substract(g0));
        while (maxAbsDiff(w1, w0) >= 1e-6) {
            w0 = w1;
            g1 = wgs84ToGcj02(w0.getLat(), w0.getLon());
            SimpleCoodinates gpsDiff = g1.substract(g0);
            w1 = w0.substract(gpsDiff);
        }

        return wgs84To4490(w1.getLat(), w1.getLon());
    }

    /**
     * gcj2wgs <- function(gcjLat, gcjLon){ g0 <- c(gcjLat, gcjLon) w0 <- g0 g1
     * <- wgs2gcj(w0[1], w0[2]) w1 <- w0 - (g1 - g0) while(max(abs(w1 - w0)) >=
     * 1e-6){ w0 <- w1 g1 <- wgs2gcj(w0[1], w0[2]) w1 <- w0 - (g1 - g0) }
     * return(data.frame(lat = w1[1], lng = w1[2]))
     *
     * @param gcjLat
     * @param gcjLon
     * @return
     */
    // 火星坐标转wgs84坐标
    public static SimpleCoodinates gcj02ToWgs84(double gcjLat, double gcjLon) {
        SimpleCoodinates g0 = new SimpleCoodinates(gcjLat, gcjLon);
        SimpleCoodinates w0 = new SimpleCoodinates(g0);
        SimpleCoodinates g1 = wgs84ToGcj02(w0.getLat(), w0.getLon());
        SimpleCoodinates w1 = w0.substract(g1.substract(g0));
        while (maxAbsDiff(w1, w0) >= 1e-6) {
            w0 = w1;
            g1 = wgs84ToGcj02(w0.getLat(), w0.getLon());
            SimpleCoodinates gpsDiff = g1.substract(g0);
            w1 = w0.substract(gpsDiff);
        }

        return w1;
    }


    public static SimpleCoodinates gcj02To4490(double gcjLat, double gcjLon) {
        SimpleCoodinates coodinates84 = gcj02ToWgs84(gcjLat, gcjLon);

        return wgs84To4490(coodinates84.getLat(), coodinates84.getLon());
    }
//    public static SimpleCoodinates gcj02ToWgs4490(double gcjLat, double gcjLon) {
//        SimpleCoodinates wgs84 = gcj02ToWgs84(gcjLat, gcjLon);
//        return wgs84To4490(wgs84.getLat(), wgs84.getLon());
//    }

    // 火星坐标转百度坐标
    public static SimpleCoodinates gcj02ToBd09(double gcjLat, double gcjLon) {

        double x = gcjLon, y = gcjLat;
        double z = Math.sqrt(x * x + y * y) + 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) + 0.000003 * Math.cos(x * Math.PI);
        double longitude = z * Math.cos(theta) + 0.0065;
        double latitude = z * Math.sin(theta) + 0.006;
        return new SimpleCoodinates(latitude, longitude);

    }

    // 百度坐标转火星坐标
    public static SimpleCoodinates bd09ToGcj02(double bdLat, double bdLon) {
        double x = bdLon - 0.0065, y = bdLat - 0.006;
        double z = Math.sqrt(x * x + y * y) - 0.00002 * Math.sin(y * Math.PI);
        double theta = Math.atan2(y, x) - 0.000003 * Math.cos(x * Math.PI);
        double longitude = z * Math.cos(theta);
        double latitude = z * Math.sin(theta);
        return new SimpleCoodinates(latitude, longitude);
    }

    // wgs84 转百度坐标
    public static SimpleCoodinates wgs84ToBd09(double wgsLat, double wgsLon) {
        SimpleCoodinates gcj02 = EvilTransform.wgs84ToGcj02(wgsLat, wgsLon);
        SimpleCoodinates bd = EvilTransform.gcj02ToBd09(gcj02.getLat(), gcj02.getLon());
        return bd;
    }

    // 百度坐标 转 wgs84
    public static SimpleCoodinates bd09ToWgs84(double bdLat, double bdLon) {
        SimpleCoodinates gcj02 = EvilTransform.bd09ToGcj02(bdLat, bdLon);
        SimpleCoodinates wgs84 = EvilTransform.gcj02ToWgs84(gcj02.getLat(), gcj02.getLon());
        return wgs84;
    }

    // 天地图4490 转百度坐标
//    public static SimpleCoodinates wgs4490ToBd09(double wgs4490Lat, double wgs4490Lon) {
//
//        SimpleCoodinates wgs84 = new SimpleCoodinates(wgs4490Lat + 0.0002676, wgs4490Lon + 0.000447);
//        return EvilTransform.wgs84ToBd09(wgs84.getLat(), wgs84.getLon());
//
//    }

    // 百度坐标 转 天地图4490
    public static SimpleCoodinates bd09To4490(double bdLat, double bdLon) {
        SimpleCoodinates gcj02 = EvilTransform.bd09ToGcj02(bdLat, bdLon);
        SimpleCoodinates wgs84 = EvilTransform.gcj02ToWgs84(gcj02.getLat(), gcj02.getLon());
        return new SimpleCoodinates(wgs84.getLat() - 0.0002676, wgs84.getLon() - 0.000447);
//        wgs84To4490Request(acitiy, wgs84.getLat(), wgs84.getLon(), listener);
    }

    // wgs 转 天地图4490
    public static SimpleCoodinates wgs84To4490(double wgs84Lat, double wgs84Lon) {
        SimpleCoodinates wgs4490 = new SimpleCoodinates(wgs84Lat - 0.0002676, wgs84Lon - 0.000447);
        return wgs4490;
    }
    // 4490 转 天地图 gcj02
    public static SimpleCoodinates w4490Togcj02(double w4490Lat, double w4490Lon) {
        SimpleCoodinates simpleCoodinates = wgs4490Towgs84(w4490Lat, w4490Lon);
        SimpleCoodinates simpleCoodinates1 = wgs84ToGcj02(simpleCoodinates.latitude, simpleCoodinates.longitude);
        return simpleCoodinates1;
    }

    // 天地图4490 wgs84
    public static SimpleCoodinates wgs4490Towgs84(double wgs4490Lat, double wgs4490Lon) {

        SimpleCoodinates wgs84 = new SimpleCoodinates(wgs4490Lat + 0.0002676, wgs4490Lon + 0.000447);
        return wgs84;

    }


    private static double maxAbsDiff(SimpleCoodinates w1, SimpleCoodinates w0) {
        SimpleCoodinates diff = w1.substract(w0);
        double absLatDiff = Math.abs(diff.getLat());
        double absLonDiff = Math.abs(diff.getLon());

        return (absLatDiff > absLonDiff ? absLatDiff : absLonDiff);
    }


    private static double EARTH_RADIUS = 6378.137;

    private static double rad(double d) {
        return d * Math.PI / 180.0;
    }

    public static double getDistance(double lat1, double lng1, double lat2,
                                     double lng2) {
        double radLat1 = rad(lat1);
        double radLat2 = rad(lat2);
        double a = radLat1 - radLat2;
        double b = rad(lng1) - rad(lng2);
        double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
                + Math.cos(radLat1) * Math.cos(radLat2)
                * Math.pow(Math.sin(b / 2), 2)));
        s = s * EARTH_RADIUS;
        s = Math.round(s * 10000) / 10;
        return s;
    }


    private static double transformLon(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
        ret = ret + (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret = ret + (20.0 * Math.sin(x * Math.PI) + 40.0 * Math.sin(x / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret = ret + (150.0 * Math.sin(x / 12.0 * Math.PI) + 300.0 * Math.sin(x * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
        ret = ret + (20.0 * Math.sin(6.0 * x * Math.PI) + 20.0 * Math.sin(2.0 * x * Math.PI)) * 2.0 / 3.0;
        ret = ret + (20.0 * Math.sin(y * Math.PI) + 40.0 * Math.sin(y / 3.0 * Math.PI)) * 2.0 / 3.0;
        ret = ret + (160.0 * Math.sin(y / 12.0 * Math.PI) + 320.0 * Math.sin(y * Math.PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static boolean isOutOfChina(double wgsLat, double wgsLon) {
        if (wgsLat < 0.8293 || wgsLat > 55.8271) {
            return true;
        }
        if (wgsLon < 72.004 || wgsLon > 137.8347) {
            return true;
        }
        return false;
    }

    public static boolean isInNanjing(double lat, double lon) {
        if (lon > 118.125 && lon < 119.53125 && lat > 31.11328125 && lat < 32.6953125) {
            return true;
        }
        return false;
    }
    public static void main(String[] args) {
        //118.678454384295,31.9451261290794
        SimpleCoodinates simpleCoodinates=wgs84To4490(32.0825467547779,118.89079179042);
        SimpleCoodinates simpleCoodinates1=wgs4490Towgs84(32.0822032351443,118.890437668687 );
        System.out.println(simpleCoodinates.getLat()+","+simpleCoodinates.getLon());
        System.out.println(simpleCoodinates1.getLat()+","+simpleCoodinates1.getLon());
    }

}
