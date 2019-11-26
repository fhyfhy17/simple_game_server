package com.template.templates;

import java.util.HashMap;

public class MailTemplateCache {

   public static HashMap<Integer,MailTemplate> cache=new HashMap<>();

   public static HashMap<Integer,MailTemplate> getMap(){
       return cache;
   }

    public static void setMap(HashMap<Integer, MailTemplate> map) {
        MailTemplateCache.cache = map;
    }

   public static MailTemplate get(int id){
       return cache.get(id);
   }

   public static void after(){

   }

}