package me.kaeternn.pasteurcraft;

import javax.annotation.Nullable;

import org.bukkit.configuration.file.YamlConfiguration;

import me.kaeternn.pasteurcraft.entities.Disease;

public class LangUtils {
    private static YamlConfiguration lang;
    private static String errorMSG = "[PasteurCraft] There was an error with %msg% in your lang file : %error%";
    private static String version;
    private static String nbdisease;

    public LangUtils(YamlConfiguration lang, String version) { 
        LangUtils.lang = lang; 
        LangUtils.version = version;
        LangUtils.nbdisease = "0";
    }

    public static void setNbdisease(String nbdisease) { LangUtils.nbdisease = nbdisease; }

    // getMSG
    public static String getMSG(String msg, @Nullable Exception error, @Nullable ){
        try{
            return lang.getString(msg)
                .replace("%version%", version)
                .replace("%nbdisease%", nbdisease);
        } catch (Exception e) {
            return errorMSG.replace("",) + e.getMessage();
        }
    }

    public static String getMSG(String msg, Disease disease){
        try{
            return getMSG(lang.getString(msg))
                .replace("%disease%", disease.getName());
        } catch (Exception e) {
            return "[PasteurCraft] There was an error with " + msg + " in your lang file : " + e.getMessage();
        }
    }

    public static String getMSG(String msg, int diseaseid){
        try{
            return getMSG(lang.getString(msg))
                .replace("%diseaseid%", ((Integer) diseaseid).toString());
        } catch (Exception e) {
            return "[PasteurCraft] There was an error with " + msg + " in your lang file : " + e.getMessage();
        }
    }

    public static String getMSG(String msg, Disease disease, int diseaseid){
        return "";
    }
}
