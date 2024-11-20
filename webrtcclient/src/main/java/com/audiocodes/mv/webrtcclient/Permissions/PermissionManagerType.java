package com.audiocodes.mv.webrtcclient.Permissions;


public enum PermissionManagerType
{

    CONTACTS(PermissionWrapper.READ_CONTACTS),
    PHONE(PermissionWrapper.READ_PHONE_STATE),
    CAMERA(PermissionWrapper.CAMERA),
    MICROPHONE(PermissionWrapper.RECORD_AUDIO),
    READ_MEDIA_AUDIO(PermissionWrapper.READ_MEDIA_AUDIO),

    BLUETOOTH(PermissionWrapper.BLUETOOTH),

    NOTIFICATION(PermissionWrapper.NOTIFICATION);

    private String typeName;

    PermissionManagerType(String name)
    {
        this.typeName = name;
    }

    public String getTypeName()
    {
        return typeName;
    }
}
