package com.template.templates;

import java.util.HashMap;

public class ItemTemplateCache {

   public static HashMap<Integer,ItemTemplate> cache=new HashMap<>();

   public static HashMap<Integer,ItemTemplate> getMap(){
       return cache;
   }

   public static ItemTemplate get(int id){
       return cache.get(id);
   }

   public static void after(){

   }

}