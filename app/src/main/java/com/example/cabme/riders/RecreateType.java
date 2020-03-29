package com.example.cabme.riders;

/**
 * To check what kind of map will be created and what to reload in MapViewActivity onCreate()
 */
public enum RecreateType
{
    REQUEST_SENT,
    REQUEST_CANCELLED,
    PROFILE_UPDATE,
    REQUEST_CONFIRMED;

    public static RecreateType toRecreateType(String recreateTypeString){
        try {
            return valueOf(recreateTypeString);
        } catch (Exception e){
            return REQUEST_CANCELLED;
        }
    }
}