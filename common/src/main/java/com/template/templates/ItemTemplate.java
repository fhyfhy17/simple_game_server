package com.template.templates;

import com.annotation.Template;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@Template(path = "Item.xlsx_item.xml")
public class ItemTemplate extends AbstractTemplate {

    private String name; //
    private String describe; //
    private int bigType; //
    private int type; //
    private int useType; //
    private int unlockRewardNum; //
    private int singlePlusMax; //
    private int totalPlusMax; //
    private int itemLevel; //
    private int limitLevel; //
    private List<Integer> limitVocations = new ArrayList<>(); //
    private int usePropLimit; //
    private boolean bindByGet; //
    private boolean bindByUse; //
    private boolean canTrade; //
    private int sellPrice; //
    private boolean needRedPoint; //
    private int sortIndex; //
    private String icon; //
    private String iconBig; //
    private int dropItemEffectID; //
    private String explain; //
    private String functionExplain; //
    private String enableTime; //
    private List<List<Integer>> baseAttributes = new ArrayList<>(); //
    private List<List<Integer>> equipActions = new ArrayList<>(); //

}