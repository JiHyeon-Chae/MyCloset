package com.example.mycloset;

import java.util.ArrayList;

public class CalendarItems {
    private String calendarOutfit;
    private ArrayList<SubCategoryItems> calendarClothes;
    private String subCategoryInfo;
    private String subCategory;
    private int calendarOutfitDateDay;
    private int calendarOutfitDateMonth;
    private int calendarOutfitDateYear;
    private String subCategoryColor;

    //아이템은 그림, 이름, 정보를 가지고 있음. + 카테고리라는 속성은 대분류에서 중분류로 이동할 때, 클릭한 대분류의 속성을
    //가진 아이템만을 출력되게 하려고 이렇게 표현했다.
    public String getCalendarOutfit() {
        return calendarOutfit;
    }

    public int getCalendarOutfitDateDay() {
        return calendarOutfitDateDay;
    }

    public int getCalendarOutfitDateMonth() {
        return calendarOutfitDateMonth;
    }

    public int getCalendarOutfitDateYear() {
        return calendarOutfitDateYear;
    }

    public ArrayList<SubCategoryItems> getCalendarClothes() {
        return calendarClothes;
    }


    public CalendarItems(int calendarOutfitDateYear, int calendarOutfitDateMonth, int calendarOutfitDateDay, String calendarOutfit, ArrayList<SubCategoryItems> calendarClothes) {
        this.calendarOutfitDateYear = calendarOutfitDateYear;
        this.calendarOutfitDateMonth = calendarOutfitDateMonth;
        this.calendarOutfitDateDay = calendarOutfitDateDay;
        this.calendarOutfit = calendarOutfit;
        this.calendarClothes = calendarClothes;
    }

    public void setCalendarOutfit(String calendarOutfit) {
        this.calendarOutfit = calendarOutfit;
    }

    public void setCalendarOutfitDateDay(int calendarOutfitDateDay) {
        this.calendarOutfitDateDay = calendarOutfitDateDay;
    }

    public void setCalendarOutfitDateYear(int calendarOutfitDateYear) {
        this.calendarOutfitDateYear = calendarOutfitDateYear;
    }

    public void setCalendarOutfitDateMonth(int calendarOutfitDateMonth) {
        this.calendarOutfitDateMonth = calendarOutfitDateMonth;
    }

    public void setCalendarClothes(ArrayList<SubCategoryItems> calendarClothes) {
        this.calendarClothes = calendarClothes;
    }
}
