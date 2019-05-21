package team5.class004.android.interfaces

interface AppConstants {
    companion object {
//        const val API_URL = "http://10.0.2.2:8080"
//        const val API_URL = "http://192.168.0.11:8080"
        const val API_URL = "http://ec2-13-124-136-43.ap-northeast-2.compute.amazonaws.com:8080"
        const val S3_URL = "https://s3.amazonaws.com/creatinghabits-userfiles-mobilehub-665767729/public";
        const val CONNECT_TIMEOUT = 15
        const val WRITE_TIMEOUT = 15
        const val READ_TIMEOUT = 15

    }
}
