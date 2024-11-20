package com.audiocodes.mv.webrtcclient.Permissions;


public interface PermissionRequest
{

    void granted();

    void revoked();

    void allResults(boolean allGranted);
}
