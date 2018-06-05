package com.example.lab3notebook;

public enum ResourcesModel {

    // создаем 2 перечисления с тайтлом и макетом
    // для удобной работы в адаптере
    FIRST_SCREEN(R.string.txt_screen_1, R.layout.activity_note_list),
    SECOND_SCREEN(R.string.txt_screen_2, R.layout.activity_tag_list);

    private int mTitleResourceId;
    private int mLayoutResourceId;

    ResourcesModel(int titleResId, int layoutResId) {
        mTitleResourceId = titleResId;
        mLayoutResourceId = layoutResId;
    }

    public int getTitleResourceId() {
        return mTitleResourceId;
    }

    public int getLayoutResourceId() {
        return mLayoutResourceId;
    }
}