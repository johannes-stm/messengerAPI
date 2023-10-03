package org.johannesstm.controller;

import javax.ws.rs.FormParam;

public class ProfilePictureUploadForm {

    @FormParam("profilePicture")
    private byte[] profilePicture;



    public void setProfilePicture(byte[] profilePicture) {
        this.profilePicture = profilePicture;
    }

    public byte[] getProfilePicture() {
        return profilePicture;
    }
}