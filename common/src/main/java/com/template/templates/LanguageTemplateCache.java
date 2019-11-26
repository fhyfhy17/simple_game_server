package com.template.templates;

import java.util.HashMap;

public class LanguageTemplateCache {

   public static HashMap<Integer,LanguageTemplate> cache=new HashMap<>();

   public static HashMap<Integer,LanguageTemplate> getMap(){
       return cache;
   }

    public static void setMap(HashMap<Integer, LanguageTemplate> map) {
        LanguageTemplateCache.cache = map;
    }

   public static LanguageTemplate get(int id){
       return cache.get(id);
   }

   public static void after(){

   }

}