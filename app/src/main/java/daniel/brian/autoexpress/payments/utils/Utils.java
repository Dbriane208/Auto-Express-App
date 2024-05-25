package daniel.brian.autoexpress.payments.utils;

import android.util.Base64;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utils {
    public static String getTimestamp(){
        return  new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
    }

    public static String validatePhoneNumber(String phone){
        if(phone.isEmpty()){
            return "";
        }

        if(phone.length() < 11 & phone.startsWith("0")){
            return phone.replaceFirst("^0","254");
        }

        if(phone.length() == 13 && phone.startsWith("+")){
            return phone.replaceFirst("^+","");
        }

        return  phone;
    }

    public static String validateCode(String businessCode){
        if(businessCode.length() <= 10){
           return  businessCode;
        }
        return businessCode;
    }

    public static String getPassword(String bussinessShortCode,String passkey,String timestamp){
        String str = bussinessShortCode + passkey + timestamp;
        return Base64.encodeToString(str.getBytes(),Base64.NO_WRAP);
    }
}
