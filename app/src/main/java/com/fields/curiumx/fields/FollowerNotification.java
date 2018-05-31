package com.fields.curiumx.fields;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties

public class FollowerNotification {


        public String followerID;
        public String followedID;

        public FollowerNotification() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public FollowerNotification(String followerID, String followedID) {
            this.followedID = followedID;
            this.followerID = followerID;

        }


}
