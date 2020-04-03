package com.example.cabme.riders;

public enum RecreateType
{
    REQUEST_SENT,
    REQUEST_CANCELLED,
    PROFILE_UPDATE,
    REQUEST_CONFIRMED;

    /**
     * This checks what kind of map is created and what to reload in MapViewActivity onCreate()
     * @param recreateTypeString
     * @return
     */
    public static RecreateType toRecreateType(String recreateTypeString){
        try {
            return valueOf(recreateTypeString);
        } catch (Exception e){
            return REQUEST_CANCELLED;
        }
    }
}