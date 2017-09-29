package com.example.jiamiaohe.gamehelper.picture.recognition.vimerzhao;


import java.util.ArrayList;
import java.util.HashMap;

public class RatioData {
    static final String xText = "x of Text";
    static final String yText = "y of Text";
    static final String wText = "width of Text";
    static final String xImg= "x of Image";
    static final String yImg= "y of Image";
    static final String OTHERS= "xText2, xImg2, ImgSize, TextHeight";
    static final String xSkill= "x of skill image";
    static final String ySkill= "y of skill image";
    static final String sizeSkill = "width of skill, height of skill";
    static final String levelTag = "level of every hero";

    static ArrayList<Integer> dataArr;
    // 按照以下格式添加数据
    static HashMap<String, ArrayList<Integer>> set1280_768 = new HashMap<>();
    static {
        dataArr = new ArrayList<>();
        dataArr.add(0, 99);
        dataArr.add(1, 202);
        dataArr.add(2, 361);
        dataArr.add(3, 415);
        dataArr.add(4, 471);
        dataArr.add(5, 519);
        set1280_768.put(xText, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 210);
        dataArr.add(1, 305);
        dataArr.add(2, 398);
        dataArr.add(3, 491);
        dataArr.add(4, 584);
        set1280_768.put(yText, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 102);
        dataArr.add(1, 159);
        dataArr.add(2, 24);
        dataArr.add(3, 24);
        dataArr.add(4, 24);
        dataArr.add(5, 100);
        set1280_768.put(wText, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 177);
        dataArr.add(1, 241);
        dataArr.add(2, 304);
        dataArr.add(3, 368);
        dataArr.add(4, 432);
        dataArr.add(5, 495);
        set1280_768.put(xImg, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 246);
        dataArr.add(1, 340);
        dataArr.add(2, 433);
        dataArr.add(3, 527);
        dataArr.add(4, 621);
        set1280_768.put(yImg, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 729);
        dataArr.add(1, 808);
        dataArr.add(2, 46);
        dataArr.add(3, 24);
        set1280_768.put(OTHERS, dataArr);
    }

    static HashMap<String, ArrayList<Integer>> set1920_1080 = new HashMap<>();
    static {
        dataArr = new ArrayList<>();
        dataArr.add(0,140);
        dataArr.add(1, 296);
        dataArr.add(2, 540);
        dataArr.add(3, 622);
        dataArr.add(4, 706);
        dataArr.add(5, 776);
        set1920_1080.put(xText, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 296);
        dataArr.add(1, 427);
        dataArr.add(2, 558);
        dataArr.add(3, 691);
        dataArr.add(4, 823);
        set1920_1080.put(yText, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 156);
        dataArr.add(1, 235);
        dataArr.add(2, 28);
        dataArr.add(3, 28);
        dataArr.add(4, 28);
        dataArr.add(5, 140);
        set1920_1080.put(wText, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 276);
        dataArr.add(1, 366);
        dataArr.add(2, 455);
        dataArr.add(3, 544);
        dataArr.add(4, 634);
        dataArr.add(5, 723);
        set1920_1080.put(xImg, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 346);
        dataArr.add(1, 478);
        dataArr.add(2, 609);
        dataArr.add(3, 741);
        dataArr.add(4, 874);
        set1920_1080.put(yImg, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 1088);
        dataArr.add(1, 1222);
        dataArr.add(2, 64);
        dataArr.add(3, 34);
        set1920_1080.put(OTHERS, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 32);//x1
        dataArr.add(1, 980);//x1
        dataArr.add(2, 378);//y1
        dataArr.add(3, 510);
        dataArr.add(4, 640);
        dataArr.add(5, 772);
        dataArr.add(6, 904);//y5
        dataArr.add(7, 34);//w
        dataArr.add(8, 34);//h,要和文字一样高
        set1920_1080.put(levelTag, dataArr);

        //技能x坐标
        dataArr = new ArrayList<>();
        dataArr.add(0, 140);
        dataArr.add(1, 1087);
        set1920_1080.put(xSkill, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 140);
        dataArr.add(1, 1087);
        set1920_1080.put(xSkill, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 351);
        dataArr.add(1, 482);
        dataArr.add(1, 614);
        dataArr.add(1, 745);
        dataArr.add(1, 877);
        set1920_1080.put(ySkill, dataArr);

        dataArr = new ArrayList<>();
        dataArr.add(0, 58);  //width or height
        set1920_1080.put(sizeSkill, dataArr);

        //等级

    }
}

