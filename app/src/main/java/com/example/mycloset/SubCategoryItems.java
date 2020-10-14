package com.example.mycloset;

public class SubCategoryItems {
    private String subCategoryIcon;
    private String subCategoryName;
    private String subCategoryInfo;
    private String subCategory;
    private int itemUsageCount;
    private String subCategoryColor;
//    private boolean isChecked;

    //아이템은 그림, 이름, 정보를 가지고 있음. + 카테고리라는 속성은 대분류에서 중분류로 이동할 때, 클릭한 대분류의 속성을
    //가진 아이템만을 출력되게 하려고 이렇게 표현했다.
    public String getSubCategoryIcon() {
        return subCategoryIcon;
    }

    public String getSubCategoryName() {
        return subCategoryName;
    }

    public String getSubCategoryInfo() {
        return subCategoryInfo;
    }

    public String getCategory() {
        return subCategory;
    }

    public String getSubCategoryColor() {
        return subCategoryColor;
    }

    public int getItemUsageCount() {
        return itemUsageCount;
    }
//    public boolean getIsChecked(){
//        return isChecked;
//    }

    public SubCategoryItems(String subCategoryIcon, String subCategoryName, String subCategoryInfo, String subCategory, String subCategoryColor, int itemUsageCount) {
        this.subCategoryIcon = subCategoryIcon;
        this.subCategoryName = subCategoryName;
        this.subCategoryInfo = subCategoryInfo;
        this.subCategory = subCategory;
        this.subCategoryColor = subCategoryColor;
        this.itemUsageCount = itemUsageCount;
    }

    public void setSubCategoryIcon(String subCategoryIcon) {
        this.subCategoryIcon = subCategoryIcon;
    }

    public void setSubCategoryName(String subCategoryName) {
        this.subCategoryName = subCategoryName;
    }

    public void setSubCategoryInfo(String subCategoryInfo) {
        this.subCategoryInfo = subCategoryInfo;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public void setSubCategoryColor(String subCategoryColor) {
        this.subCategoryColor = subCategoryColor;
    }

    public void setItemUsageCount(int itemUsageCount) {
        this.itemUsageCount = itemUsageCount;
    }
}
